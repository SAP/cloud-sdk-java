/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;

import io.vavr.control.Option;

final class EntitySetProcessor
{
    private static final Logger logger = MessageCollector.getLogger(EntitySetProcessor.class);

    private final Map<String, JDefinedClass> generatedEntities = new HashMap<>();
    private final Map<String, JDefinedClass> generatedComplexTypes = new HashMap<>();

    private final Service service;
    private final String odataEndpointPath;

    private final NamespaceClassGenerator namespaceClassGenerator;
    private final ServiceClassGenerator serviceClassGenerator;
    private final FluentHelperClassGenerator fluentGenerator;

    private final boolean serviceMethodsPerEntitySet;

    EntitySetProcessor(
        final Service service,
        final String odataEndpointPath,
        final NamespaceClassGenerator namespaceClassGenerator,
        final ServiceClassGenerator serviceClassGenerator,
        final FluentHelperClassGenerator fluentGenerator,
        final boolean serviceMethodsPerEntitySet )
    {
        this.service = service;
        this.odataEndpointPath = odataEndpointPath;
        this.namespaceClassGenerator = namespaceClassGenerator;
        this.serviceClassGenerator = serviceClassGenerator;
        this.fluentGenerator = fluentGenerator;
        this.serviceMethodsPerEntitySet = serviceMethodsPerEntitySet;
    }

    List<PreparedEntityBluePrint>
        processEntitySets( final Collection<String> includeEntitySets, final NamingContext entityClassNamingContext )
    {
        final List<PreparedEntityBluePrint> entityResults = new ArrayList<>();

        final Collection<Service.EntitySet> entitySetsToProcess = new ArrayList<>();
        if( includeEntitySets != null && !includeEntitySets.isEmpty() ) {
            for( final String entitySetName : includeEntitySets ) {
                final Service.EntitySet entitySet = service.getEntitySet(entitySetName);
                if( entitySet != null ) {
                    entitySetsToProcess.add(entitySet);
                } else {
                    logger
                        .warn(
                            String
                                .format(
                                    "Entity set '%s' was not found in the EDMX for service '%s', skipping.",
                                    entitySetName,
                                    service.getName()));
                }
            }
        } else {
            entitySetsToProcess.addAll(service.getAllEntitySets());
        }

        for( final Service.EntitySet entitySet : entitySetsToProcess ) {
            try {
                final Option<PreparedEntityBluePrint> entityStuffAfterFirstPass =
                    namespaceClassGenerator
                        .processEntitySet(
                            serviceClassGenerator,
                            fluentGenerator,
                            service,
                            odataEndpointPath,
                            generatedEntities,
                            generatedComplexTypes,
                            entitySet,
                            entityClassNamingContext,
                            serviceMethodsPerEntitySet);

                entityStuffAfterFirstPass.peek(entityResults::add);
            }
            catch( final JClassAlreadyExistsException e ) {
                throw new ODataGeneratorWriteException(e);
            }
        }
        return entityResults;
    }

    void processNavigationPropertyModels( final Iterable<PreparedEntityBluePrint> entityBluePrints )
    {
        for( final PreparedEntityBluePrint entityBluePrint : entityBluePrints ) {
            if( !entityBluePrint.getNavigationProperties().isEmpty() ) {
                namespaceClassGenerator.addNavigationPropertyCode(entityBluePrint, generatedEntities);
            }
        }
    }

    void processFunctionImports(
        final Collection<String> includeFunctionImports,
        final NamingContext entityClassNamingContext )
    {
        final NamingContext functionImportFetchMethodNamingContext = new NamingContext();

        final Collection<Service.FunctionImport> functionImportsToProcess = new ArrayList<>();
        if( includeFunctionImports != null && !includeFunctionImports.isEmpty() ) {
            for( final String functionImportName : includeFunctionImports ) {
                final Service.FunctionImport functionImport = service.getFunctionImport(functionImportName);

                if( functionImport != null ) {
                    functionImportsToProcess.add(functionImport);
                } else {
                    logger
                        .warn(
                            String
                                .format(
                                    "Function import '%s' was not found in the EDMX for service '%s', skipping.",
                                    functionImportName,
                                    service.getName()));
                }
            }
        } else {
            functionImportsToProcess.addAll(service.getAllFunctionImports());
        }

        for( final Service.FunctionImport functionImport : functionImportsToProcess ) {
            validateFunctionImport(functionImport);
            try {
                namespaceClassGenerator
                    .processFunctionImport(
                        serviceClassGenerator,
                        fluentGenerator,
                        service,
                        generatedEntities,
                        generatedComplexTypes,
                        functionImport,
                        entityClassNamingContext,
                        functionImportFetchMethodNamingContext);
            }
            catch( final JClassAlreadyExistsException e ) {
                throw new ODataGeneratorWriteException(e);
            }
        }
    }

    private void validateFunctionImport( final Service.FunctionImport functionImport )
    {
        if( functionImport.getHttpMethod() == null ) {
            throw new ODataGeneratorReadException(
                "There was not HTTP method given for function import "
                    + functionImport.getName()
                    + ". You need to provide one via the 'm:HttpMethod' property.");
        }
    }
}
