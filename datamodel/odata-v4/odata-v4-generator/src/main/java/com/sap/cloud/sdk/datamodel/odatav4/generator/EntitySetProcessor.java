package com.sap.cloud.sdk.datamodel.odatav4.generator;

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
    private final Map<String, JDefinedClass> generatedEnumTypes = new HashMap<>();

    private final Service service;
    private final String odataEndpointPath;

    private final NamespaceClassGenerator namespaceClassGenerator;
    private final ServiceClassGenerator serviceClassGenerator;

    EntitySetProcessor(
        final Service service,
        final String odataEndpointPath,
        final NamespaceClassGenerator namespaceClassGenerator,
        final ServiceClassGenerator serviceClassGenerator )
    {
        this.service = service;
        this.odataEndpointPath = odataEndpointPath;
        this.namespaceClassGenerator = namespaceClassGenerator;
        this.serviceClassGenerator = serviceClassGenerator;
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
                            service,
                            generatedEntities,
                            generatedComplexTypes,
                            generatedEnumTypes,
                            entitySet,
                            entityClassNamingContext);

                entityStuffAfterFirstPass.peek(entityResults::add);
            }
            catch( final JClassAlreadyExistsException e ) {
                throw new ODataGeneratorWriteException(e);
            }
        }
        return entityResults;
    }

    void processNavigationPropertyModels(
        final Iterable<PreparedEntityBluePrint> entityBluePrints,
        final NamingContext entityClassNamingContext )
    {
        for( final PreparedEntityBluePrint entityBluePrint : entityBluePrints ) {
            if( !entityBluePrint.getNavigationProperties().isEmpty() ) {
                try {
                    namespaceClassGenerator
                        .addNavigationPropertyCode(
                            entityBluePrint,
                            generatedEntities,
                            generatedComplexTypes,
                            generatedEnumTypes,
                            entityClassNamingContext);
                }
                catch( final JClassAlreadyExistsException e ) {
                    throw new ODataGeneratorWriteException(e);
                }
            }
        }
    }

    void processFunctionImports(
        final Collection<String> includeFunctionImports,
        final NamingContext entityClassNamingContext )
    {
        final NamingContext functionImportFetchMethodNamingContext = new NamingContext();

        final Collection<Service.ServiceFunction> functionImportsToProcess = new ArrayList<>();
        if( includeFunctionImports != null && !includeFunctionImports.isEmpty() ) {
            for( final String functionImportName : includeFunctionImports ) {
                final Collection<Service.ServiceFunction> functionImports =
                    service.getServiceFunction(functionImportName);

                if( functionImports != null && !functionImports.isEmpty() ) {
                    functionImportsToProcess.addAll(functionImports);
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
            functionImportsToProcess.addAll(service.getAllServiceFunctions());
        }

        for( final Service.ServiceFunction functionImport : functionImportsToProcess ) {
            validateFunctionOrActionImport(functionImport);
            try {
                namespaceClassGenerator
                    .processUnboundOperation(
                        serviceClassGenerator,
                        service,
                        generatedEntities,
                        generatedComplexTypes,
                        generatedEnumTypes,
                        functionImport,
                        entityClassNamingContext,
                        functionImportFetchMethodNamingContext,
                        true);
            }
            catch( final JClassAlreadyExistsException e ) {
                throw new ODataGeneratorWriteException(e);
            }
        }
    }

    void processActionImports(
        final Collection<String> includeActionImports,
        final NamingContext entityClassNamingContext )
    {
        final NamingContext actionImportFetchMethodNamingContext = new NamingContext();

        final Collection<Service.ServiceAction> actionImportsToProcess = new ArrayList<>();
        if( includeActionImports != null && !includeActionImports.isEmpty() ) {
            for( final String actionImportName : includeActionImports ) {
                final Collection<Service.ServiceAction> actionImports = service.getServiceAction(actionImportName);

                if( actionImports != null && !actionImports.isEmpty() ) {
                    actionImportsToProcess.addAll(actionImports);
                } else {
                    logger
                        .warn(
                            String
                                .format(
                                    "Action import '%s' was not found in the EDMX for service '%s', skipping.",
                                    actionImportName,
                                    service.getName()));
                }
            }
        } else {
            actionImportsToProcess.addAll(service.getAllServiceActions());
        }
        for( final Service.ServiceAction actionImport : actionImportsToProcess ) {
            validateFunctionOrActionImport(actionImport);
            try {
                namespaceClassGenerator
                    .processUnboundOperation(
                        serviceClassGenerator,
                        service,
                        generatedEntities,
                        generatedComplexTypes,
                        generatedEnumTypes,
                        actionImport,
                        entityClassNamingContext,
                        actionImportFetchMethodNamingContext,
                        false);
            }
            catch( final JClassAlreadyExistsException e ) {
                throw new ODataGeneratorWriteException(e);
            }
        }
    }

    private void validateFunctionOrActionImport( final Service.ServiceOperation unboundOperation )
    {
        if( unboundOperation.getHttpMethod() == null ) {
            throw new ODataGeneratorReadException(
                "There was not HTTP method given for function/action import "
                    + unboundOperation.getName()
                    + ". You need to provide one via the 'm:HttpMethod' property.");
        }
    }

    void processBoundFunctions( final NamingContext entityClassNamingContext )
    {
        final Collection<Service.ServiceBoundFunction> allServiceBoundFunctions = service.getAllServiceBoundFunctions();
        allServiceBoundFunctions.forEach(f -> {
            try {
                namespaceClassGenerator
                    .processBoundOperation(
                        service,
                        generatedEntities,
                        generatedComplexTypes,
                        generatedEnumTypes,
                        f,
                        entityClassNamingContext);
            }
            catch( final JClassAlreadyExistsException e ) {
                throw new ODataGeneratorWriteException(e);
            }
        });
    }

    void processBoundActions( final NamingContext entityClassNamingContext )
    {
        final Collection<Service.ServiceBoundAction> allServiceBoundActions = service.getAllServiceBoundActions();
        allServiceBoundActions.forEach(action -> {
            try {
                namespaceClassGenerator
                    .processBoundOperation(
                        service,
                        generatedEntities,
                        generatedComplexTypes,
                        generatedEnumTypes,
                        action,
                        entityClassNamingContext);
            }
            catch( final JClassAlreadyExistsException e ) {
                throw new ODataGeneratorWriteException(e);
            }
        });
    }
}
