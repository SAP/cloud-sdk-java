package com.sap.cloud.sdk.datamodel.odata.generator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.sap.cloud.sdk.datamodel.odata.utility.LegacyClassScanner;
import com.sap.cloud.sdk.datamodel.odata.utility.NamingStrategy;
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
    private final FluentHelperClassGenerator fluentGenerator;

    private final JCodeModel codeModel;
    private final DataModelGeneratorConfig config;

    CodeModelClassGenerator(
        @Nonnull final DataModelGeneratorConfig config,
        @Nonnull final String namespaceParentPackageName,
        @Nonnull final String servicePackageName )
    {
        this.config = config;

        codeModel = new JCodeModel();

        final NamingStrategy namingStrategy = config.getNamingStrategy();
        final JPackage namespaceParentPackage = codeModel._package(namespaceParentPackageName);

        namespaceClassGenerator =
            new NamespaceClassGenerator(
                codeModel,
                namespaceParentPackage,
                namingStrategy,
                config.getAnnotationStrategy(),
                config.isGeneratePojosOnly());

        if( config.isGeneratePojosOnly() ) {
            serviceClassGenerator = null;
            fluentGenerator = null;
        } else {
            final JPackage servicePackage = codeModel._package(servicePackageName);

            final ServiceBatchGenerator serviceBatchGenerator = new ServiceBatchGenerator(codeModel);

            final ServiceBatchChangeSetGenerator serviceBatchChangeSetGenerator =
                new ServiceBatchChangeSetGenerator(codeModel);

            final DefaultServiceBatchGenerator defaultServiceBatchGenerator =
                new DefaultServiceBatchGenerator(codeModel);

            final DefaultServiceBatchChangeSetGenerator defaultServiceBatchChangeSetGenerator =
                new DefaultServiceBatchChangeSetGenerator(codeModel);

            final LegacyClassScanner classScanner =
                config.isKeepExistingSignatures()
                    ? new LegacyClassScanner(config.getOutputDirectory())
                    : LegacyClassScanner.DISABLED;

            serviceClassGenerator =
                new ServiceClassGenerator(
                    codeModel,
                    servicePackage,
                    namespaceParentPackage,
                    namingStrategy,
                    serviceBatchChangeSetGenerator,
                    serviceBatchGenerator,
                    defaultServiceBatchGenerator,
                    defaultServiceBatchChangeSetGenerator,
                    config.isServiceMethodsPerEntitySet(),
                    classScanner);

            fluentGenerator = new FluentHelperClassGenerator(codeModel, namingStrategy, classScanner);
        }
    }

    void processService(
        final Service service,
        @Nullable final Collection<String> includedEntitySets,
        @Nullable final Collection<String> includedFunctionImports )
    {
        final String serviceTitle = service.getTitle();
        final String odataEndpointPath = service.getServiceUrl();

        logger.info("Processing OData service '" + serviceTitle + "' at " + odataEndpointPath);

        final EntitySetProcessor entitySetProcessor =
            new EntitySetProcessor(
                service,
                odataEndpointPath,
                namespaceClassGenerator,
                serviceClassGenerator,
                fluentGenerator,
                config.isServiceMethodsPerEntitySet());

        final NamingContext entityClassNamingContext =
            new NamingContext(NamingContext.NameEqualityStrategy.CASE_INSENSITIVE);

        if( config.getDeprecationNotice() != null ) {
            ((EdmService) service).setGenerateExplicitDeprecationNotices(true);
            serviceClassGenerator.setCustomDeprecationNoticeForService(config.getDeprecationNotice());
        }
        final List<PreparedEntityBluePrint> preparedEntityBluePrints =
            entitySetProcessor.processEntitySets(includedEntitySets, entityClassNamingContext);
        entitySetProcessor.processNavigationPropertyModels(preparedEntityBluePrints);
        if( !config.isGeneratePojosOnly() ) {
            entitySetProcessor.processFunctionImports(includedFunctionImports, entityClassNamingContext);
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
