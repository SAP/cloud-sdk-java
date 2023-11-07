/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.generator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import org.slf4j.Logger;

import com.sap.cloud.sdk.datamodel.odata.utility.LegacyClassScanner;
import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.writer.FileCodeWriter;

import lombok.AccessLevel;
import lombok.Getter;

class CodeModelClassGenerator
{
    private static final Logger logger = MessageCollector.getLogger(ODataToVdmGenerator.class);

    @Getter( AccessLevel.PACKAGE )
    private final NamespaceClassGenerator namespaceClassGenerator;
    @Getter( AccessLevel.PACKAGE )
    private final ServiceClassGenerator serviceClassGenerator;

    private final JCodeModel codeModel;
    private final DataModelGeneratorConfig config;
    private LegacyClassScanner classScanner;

    CodeModelClassGenerator(
        @Nonnull final DataModelGeneratorConfig config,
        @Nonnull final String namespaceParentPackageName,
        @Nonnull final String servicePackageName )
    {
        this.config = config;
        codeModel = new JCodeModel();

        final JPackage namespaceParentPackage = codeModel._package(namespaceParentPackageName);

        this.classScanner =
            config.isKeepExistingSignatures()
                ? new LegacyClassScanner(config.getOutputDirectory())
                : LegacyClassScanner.DISABLED;

        namespaceClassGenerator =
            new NamespaceClassGenerator(
                codeModel,
                namespaceParentPackage,
                config.getNamingStrategy(),
                config.getAnnotationStrategy(),
                config.isGeneratePojosOnly(),
                config.isServiceMethodsPerEntitySet(),
                classScanner);

        if( config.isGeneratePojosOnly() ) {
            serviceClassGenerator = null;
        } else {
            final JPackage servicePackage = codeModel._package(servicePackageName);
            /*final ServiceBatchGenerator serviceBatchGenerator = new ServiceBatchGenerator(codeModel);
            final ServiceBatchChangeSetGenerator serviceBatchChangeSetGenerator =
                new ServiceBatchChangeSetGenerator(codeModel);
            final DefaultServiceBatchGenerator defaultServiceBatchGenerator =
                new DefaultServiceBatchGenerator(codeModel);
            final DefaultServiceBatchChangeSetGenerator defaultServiceBatchChangeSetGenerator =
                new DefaultServiceBatchChangeSetGenerator(codeModel);*/

            serviceClassGenerator =
                new ServiceClassGenerator(
                    codeModel,
                    servicePackage,
                    namespaceParentPackage,
                    config.getNamingStrategy(),
                    config.isServiceMethodsPerEntitySet(),
                    classScanner);
        }
    }

    void processService(
        final Service service,
        final Collection<String> includedEntitySets,
        final Collection<String> includedFunctionImports,
        final Collection<String> includedActionImports )
    {
        final String serviceTitle = service.getTitle();
        final String odataEndpointPath = service.getServiceUrl();

        logger.info("Processing OData service '" + serviceTitle + "' at " + odataEndpointPath);

        final EntitySetProcessor entitySetProcessor =
            new EntitySetProcessor(service, namespaceClassGenerator, serviceClassGenerator);
        final NamingContext entityClassNamingContext =
            new NamingContext(NamingContext.NameEqualityStrategy.CASE_INSENSITIVE);

        if( config.getDeprecationNotice() != null ) {
            ((EdmService) service).setGenerateExplicitDeprecationNotices(true);
            serviceClassGenerator.setCustomDeprecationNoticeForService(config.getDeprecationNotice());
        }
        final List<PreparedEntityBluePrint> preparedEntityBluePrints =
            entitySetProcessor.processEntitySets(includedEntitySets, entityClassNamingContext);

        entitySetProcessor.processNavigationPropertyModels(preparedEntityBluePrints, entityClassNamingContext);

        if( !config.isGeneratePojosOnly() ) {
            entitySetProcessor.processFunctionImports(includedFunctionImports, entityClassNamingContext);
            entitySetProcessor.processActionImports(includedActionImports, entityClassNamingContext);

            // Generate bound operations last so that the types they are bound to have already been generated
            // since we can't generate them on the fly
            entitySetProcessor.processBoundFunctions(entityClassNamingContext);
            entitySetProcessor.processBoundActions(entityClassNamingContext);
        }
    }

    void writeClasses( @Nonnull final DataModelGeneratorConfig config, @Nonnull final Charset encoding )
    {
        ensureDirectoryExists(config.getOutputDirectory());

        final CodeWriter codeWriter = getCodeWriter(config, encoding);

        writeCodeModel(codeWriter);
    }

    private void writeCodeModel( final CodeWriter codeWriter )
    {
        try {
            codeModel.build(codeWriter);
        }
        catch( final IOException e ) {
            throw new ODataGeneratorWriteException(e);
        }
    }

    private void ensureDirectoryExists( final File targetDir )
    {
        if( !targetDir.exists() ) {
            final boolean success = targetDir.mkdirs();
            if( !success ) {
                throw new ODataGeneratorWriteException(
                    "Could not create directory at '" + targetDir.getAbsolutePath() + "'");
            }
        }
    }

    private CodeWriter getCodeWriter( @Nonnull final DataModelGeneratorConfig config, @Nonnull final Charset encoding )
    {
        CodeWriter codeWriter;

        if( config.isForceFileOverride() ) {
            try {
                codeWriter = new FileCodeWriter(config.getOutputDirectory(), encoding.toString());
            }
            catch( final IOException e ) {
                throw new ODataGeneratorWriteException(e);
            }
        } else {
            codeWriter = new SafeCodeWriter(config.getOutputDirectory(), encoding.toString());
        }

        // add copyright header
        if( !config.getCopyrightHeader().isEmpty() ) {
            codeWriter = new CopyrightHeaderCodeWriter(codeWriter, config.getCopyrightHeader(), encoding.toString());
        }

        // add generator reference
        if( config.isGenerateVersionReference() ) {
            codeWriter = new GeneratorReferenceCodeWriter(codeWriter, encoding.toString());
        }

        return codeWriter;
    }

    boolean wasServiceGenerated( final String serviceName )
    {
        return serviceClassGenerator != null && serviceClassGenerator.wasServiceGenerated(serviceName);
    }
}
