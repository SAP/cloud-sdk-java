/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.generator;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.escape.Escaper;
import com.google.common.html.HtmlEscapers;
import com.sap.cloud.sdk.datamodel.odata.utility.LegacyClassScanner;
import com.sap.cloud.sdk.datamodel.odata.utility.NamingStrategy;
import com.sap.cloud.sdk.datamodel.odata.utility.NamingUtils;
import com.sap.cloud.sdk.datamodel.odatav4.core.BatchRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CollectionValueActionRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CollectionValueFunctionRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.ServiceWithNavigableEntities;
import com.sap.cloud.sdk.datamodel.odatav4.core.SingleValueActionRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.SingleValueFunctionRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JCommentPart;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
class ServiceClassGenerator
{
    private static final String SERVICE_INTERFACE_NAMING = "%sService";
    private static final String SERVICE_IMPLEMENTATION_NAMING = "Default%sService";
    static final String DEFAULT_SERVICE_PATH_FIELD_NAMING = "DEFAULT_SERVICE_PATH";
    private static final String SERVICE_PATH_FIELD_NAMING = "servicePath";

    private static final String BUSINESS_HUB_LINK_TEMPLATE =
        "<p>Reference: <a href='https://api.sap.com/shell/discover/contentpackage/SAPS4HANACloud/api/%s?section=OVERVIEW'>"
            + "SAP Business Accelerator Hub"
            + "</a></p>";

    private final Map<String, JDefinedClass> generatedServiceInterfaceClasses = new HashMap<>();
    private final Map<String, JDefinedClass> generatedServiceImplementationClasses = new HashMap<>();
    //private final Map<String, BatchRelevantStubs> generatedBatchRelevantStubs = new HashMap<>();

    private final Escaper htmlEscaper = HtmlEscapers.htmlEscaper();

    private final JCodeModel codeModel;
    private final JPackage servicePackage;
    private final JPackage namespaceParentPackage;
    private final NamingStrategy codeNamingStrategy;
    private final boolean serviceMethodsPerEntitySet;
    private final LegacyClassScanner classScanner;
    @Setter( AccessLevel.PACKAGE )
    private String customDeprecationNoticeForService = null;

    /*private final ServiceBatchChangeSetGenerator serviceBatchChangeSetGenerator;
    private final ServiceBatchGenerator serviceBatchGenerator;
    private final DefaultServiceBatchGenerator defaultServiceBatchGenerator;
    private final DefaultServiceBatchChangeSetGenerator defaultServiceBatchChangeSetGenerator;
    */
    private void addClassLevelJavadoc( final JDocComment javadoc, final Service service )
    {
        if( !Strings.isNullOrEmpty(service.getInfoDescription()) ) {
            String description = htmlEscaper.escape(service.getInfoDescription());
            if( !description.matches(".*[.?!]\\s*$") ) {
                // we need to have a 'sentence ending character' at the end of the base description so that the package
                // summary gets it as a short description for the class/interface
                description += ".";
            }
            javadoc.add(String.format("<p>%s</p>", description));
        }

        if( !Strings.isNullOrEmpty(service.getExternalUrl()) ) {
            javadoc
                .add(
                    String
                        .format(
                            "<p><a href='%s'>%s</a></p>",
                            service.getExternalUrl(),
                            Strings.isNullOrEmpty(service.getExternalDescription())
                                ? service.getExternalUrl()
                                : service.getExternalDescription()));
        }

        if( service.hasLinkToApiBusinessHub() ) {
            javadoc.add(String.format(BUSINESS_HUB_LINK_TEMPLATE, service.getName()));
        }

        javadoc.add("<h3>Details:</h3><table summary='Details'>");
        javadoc.add(String.format("<tr><td align='right'>OData Service:</td><td>%s</td></tr>", service.getName()));

        if( !Strings.isNullOrEmpty(service.getInfoVersion()) ) {
            javadoc
                .add(
                    String.format("<tr><td align='right'>API Version:</td><td>%s</td></tr>", service.getInfoVersion()));
        }
        if( !Strings.isNullOrEmpty(service.getMinErpVersion()) ) {
            javadoc
                .add(
                    String
                        .format(
                            "<tr><td align='right'>Minimum ERP Version:</td><td>%s</td></tr>",
                            service.getMinErpVersion()));
        }
        final Collection<Service.ExternalOverview> additionalDetails = service.getExternalOverview();
        if( additionalDetails != null ) {
            for( final Service.ExternalOverview entry : additionalDetails ) {
                if( !Strings.isNullOrEmpty(entry.getName()) && entry.getValues() != null ) {
                    javadoc
                        .add(
                            String
                                .format(
                                    "<tr><td align='right'>%s:</td><td>%s</td></tr>",
                                    entry.getName(),
                                    Joiner.on(", ").join(entry.getValues())));
                }
            }
        }
        javadoc.add("</table>");
    }

    private JDefinedClass generateServiceInterface( final Service service, final String formattedInterfaceName )
        throws JClassAlreadyExistsException
    {
        final JDefinedClass interfaceClass = servicePackage._interface(formattedInterfaceName);
        addClassLevelJavadoc(interfaceClass.javadoc(), service);
        final JFieldVar defaultServicePathField =
            interfaceClass
                .field(JMod.NONE, String.class, DEFAULT_SERVICE_PATH_FIELD_NAMING, JExpr.lit(service.getServiceUrl()));
        defaultServicePathField
            .javadoc()
            .add(
                "If no other path was provided via the {@link #withServicePath(String)} method, this is the default service path used to access the endpoint.");

        DeprecationUtils.addStatusInformation(interfaceClass, service, customDeprecationNoticeForService);

        // withServicePath interface method
        final JMethod withServicePathMethod = interfaceClass.method(JMod.NONE, interfaceClass, "withServicePath");
        withServicePathMethod.annotate(Nonnull.class);
        final JVar servicePathParam = withServicePathMethod.param(JMod.FINAL, String.class, SERVICE_PATH_FIELD_NAMING);
        servicePathParam.annotate(Nonnull.class);

        withServicePathMethod
            .javadoc()
            .add(
                "Overrides the default service path and returns a new service instance with the specified service path. Also adjusts the respective entity URLs.");
        withServicePathMethod.javadoc().addParam(servicePathParam).add("Service path that will override the default.");
        withServicePathMethod.javadoc().addReturn().add("A new service instance with the specified service path.");

        // batch interface method
        addBatchInterfaceMethod(interfaceClass);

        return interfaceClass;
    }

    private void addBatchInterfaceMethod( final JDefinedClass serviceInterfaceClass )
    {
        final JType batchRequestBuilder = codeModel.ref(BatchRequestBuilder.class);
        final String methodName = "batch";

        final JMethod interfaceMethod = serviceInterfaceClass.method(JMod.NONE, batchRequestBuilder, methodName);
        interfaceMethod.annotate(Nonnull.class);

        interfaceMethod.javadoc().add(String.format("Creates a batch request builder object."));
        interfaceMethod
            .javadoc()
            .addReturn()
            .add(
                String
                    .format(
                        "A request builder to handle batch operation on this service. "
                            + "To perform execution, call the {@link %s#execute(Destination) execute} method on the request builder object.",
                        batchRequestBuilder.fullName()));
    }

    private JDefinedClass generateServiceImplementation(
        final JDefinedClass serviceInterfaceClass,
        final Service service,
        final String formattedClassName )
        throws JClassAlreadyExistsException
    {
        final JDefinedClass serviceClass = servicePackage._class(formattedClassName)._implements(serviceInterfaceClass);
        addClassLevelJavadoc(serviceClass.javadoc(), service);

        DeprecationUtils.addStatusInformation(serviceClass, service, customDeprecationNoticeForService);

        final JFieldVar servicePathField =
            serviceClass.field(JMod.PRIVATE | JMod.FINAL, String.class, SERVICE_PATH_FIELD_NAMING);
        servicePathField.annotate(Nonnull.class);

        // add ServiceWithNavigableEntities feature
        serviceClass._implements(ServiceWithNavigableEntities.class);
        servicePathField.annotate(Getter.class);

        final JMethod noArgsConstructor = serviceClass.constructor(JMod.PUBLIC);
        noArgsConstructor
            .body()
            .assign(
                JExpr.ref(SERVICE_PATH_FIELD_NAMING),
                serviceInterfaceClass.staticRef(DEFAULT_SERVICE_PATH_FIELD_NAMING));
        noArgsConstructor
            .javadoc()
            .add(
                String
                    .format(
                        "Creates a service using {@link %s#%s} to send the requests.",
                        serviceInterfaceClass.name(),
                        DEFAULT_SERVICE_PATH_FIELD_NAMING));

        final JMethod servicePathConstructor = serviceClass.constructor(JMod.PRIVATE);
        final JVar servicePathParameter =
            servicePathConstructor.param(JMod.FINAL, String.class, SERVICE_PATH_FIELD_NAMING);
        servicePathParameter.annotate(Nonnull.class);
        servicePathConstructor.body().assign(JExpr._this().ref(servicePathField), servicePathParameter);
        servicePathConstructor
            .javadoc()
            .add("Creates a service using the provided service path to send the requests.\n");
        servicePathConstructor.javadoc().add("<p>\n");
        servicePathConstructor.javadoc().add("Used by the fluent {@link #withServicePath(String)} method.");

        // withServicePath implementation method
        final JMethod withServicePathMethod = serviceClass.method(JMod.PUBLIC, serviceClass, "withServicePath");
        withServicePathMethod.annotate(Override.class);
        withServicePathMethod.annotate(Nonnull.class);
        final JVar servicePathParam = withServicePathMethod.param(JMod.FINAL, String.class, SERVICE_PATH_FIELD_NAMING);
        servicePathParam.annotate(Nonnull.class);
        withServicePathMethod.body()._return(JExpr._new(serviceClass).arg(servicePathParam));

        // batch implementation method
        addBatchImplementationMethod(serviceClass);

        return serviceClass;
    }

    private void addBatchImplementationMethod( final JDefinedClass serviceImplementationClass )
    {
        final JType batchRequestBuilder = codeModel.ref(BatchRequestBuilder.class);
        final String methodName = "batch";

        final JMethod implementationMethod =
            serviceImplementationClass.method(JMod.PUBLIC, batchRequestBuilder, methodName);
        implementationMethod.annotate(Override.class);
        implementationMethod.annotate(Nonnull.class);

        implementationMethod.body()._return(JExpr._new(batchRequestBuilder).arg(JExpr.ref(SERVICE_PATH_FIELD_NAMING)));
    }

    JDefinedClass getOrGenerateServiceInterfaceClass( final Service service )
    {
        final String formattedInterfaceName = String.format(SERVICE_INTERFACE_NAMING, service.getJavaClassName());
        JDefinedClass interfaceClass = generatedServiceInterfaceClasses.get(service.getName());
        if( interfaceClass == null ) {
            try {
                interfaceClass = generateServiceInterface(service, formattedInterfaceName);
            }
            catch( final JClassAlreadyExistsException e ) {
                log.error("Failed to get or generate interface: {} for service: {}", formattedInterfaceName, service);

                generatedServiceInterfaceClasses
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().name().equals(formattedInterfaceName))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .ifPresent(s -> log.error("An interface with the same name was already defined by service: {}", s));
                throw new ODataGeneratorException(e);
            }
            generatedServiceInterfaceClasses.put(service.getName(), interfaceClass);
        }
        return interfaceClass;
    }

    JDefinedClass getOrGenerateServiceImplementationClass( final Service service, final JDefinedClass interfaceClass )
    {
        final String formattedClassName = String.format(SERVICE_IMPLEMENTATION_NAMING, service.getJavaClassName());
        JDefinedClass implementationClass = generatedServiceImplementationClasses.get(service.getName());
        if( implementationClass == null ) {
            try {
                implementationClass = generateServiceImplementation(interfaceClass, service, formattedClassName);
            }
            catch( final JClassAlreadyExistsException e ) {
                log.error("Failed to get or generate class:{} for service: {}", formattedClassName, service.getName());

                generatedServiceImplementationClasses
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().name().equals(formattedClassName))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .ifPresent(s -> log.error("A class with the same name was already defined by service: {}", s));
                throw new ODataGeneratorException(e);
            }
            generatedServiceImplementationClasses.put(service.getName(), implementationClass);
        }
        return implementationClass;
    }

    /*@RequiredArgsConstructor
    private static class BatchRelevantStubs
    {
        final ServiceBatchGenerator.InterfaceStub serviceBatchInterface;
        final ServiceBatchChangeSetGenerator.InterfaceStub serviceBatchChangeSetInterface;
        final DefaultServiceBatchGenerator.ClassStub defaultServiceBatchImplementation;
        final DefaultServiceBatchChangeSetGenerator.ClassStub defaultServiceBatchChangeSetImplementation;
    }*/

    ServiceClassAmplifier getOrGenerateServiceClassesAndGetAmplifier( final Service service )
    {
        final JDefinedClass serviceInterfaceClass = getOrGenerateServiceInterfaceClass(service);
        final JDefinedClass serviceImplementationClass =
            getOrGenerateServiceImplementationClass(service, serviceInterfaceClass);

        return new ServiceClassAmplifier(
            serviceInterfaceClass,
            serviceImplementationClass,
            codeNamingStrategy,
            codeModel,
            serviceMethodsPerEntitySet,
            classScanner);
    }

    @RequiredArgsConstructor
    static final class ServiceClassAmplifier
    {
        @Getter( AccessLevel.PACKAGE )
        private final JDefinedClass serviceInterfaceClass;
        private final JDefinedClass serviceImplementationClass;
        private final NamingStrategy codeNamingStrategy;
        private final JCodeModel codeModel;
        private final boolean serviceMethodsPerEntitySet;
        private final LegacyClassScanner classScanner;

        Iterable<EntityPropertyModel> getRefinedKeyProperties(
            @Nonnull final String methodName,
            @Nonnull final Iterable<EntityPropertyModel> keyProperties )
        {
            final List<List<EntityPropertyModel>> getByKeyMethodArguments =
                classScanner
                    .determineArgumentsForMethod(
                        serviceInterfaceClass.fullName(),
                        methodName,
                        keyProperties,
                        EntityPropertyModel::getJavaFieldName);
            if( getByKeyMethodArguments.size() != 1 ) {
                final String msg =
                    String
                        .format(
                            "Entity in class %s has different key parameters than last time the code generator run.",
                            serviceInterfaceClass.fullName());
                log.error("{} Found the following key parameter groups: {}", msg, getByKeyMethodArguments);
                throw new ODataGeneratorWriteException(msg);
            }
            return getByKeyMethodArguments.get(0);
        }

        String getByKeyMethodName( final EntityMetadata entityMetadata )
        {
            final String methodNameSuffix =
                serviceMethodsPerEntitySet
                    ? getMethodNameSuffixFromEntitySet(entityMetadata.getEntitySetName())
                    : entityMetadata.getGeneratedEntityClass().name();
            return NamingUtils.deriveGetEntityServiceMethodName(methodNameSuffix);
        }

        void addGetByKeyMethod(
            final EntityMetadata entityMetadata,
            final String methodName,
            final Iterable<EntityPropertyModel> keyProperties )
        {
            final JDefinedClass entityClass = entityMetadata.getGeneratedEntityClass();
            final JType getByKeyRequestBuilder = codeModel.ref(GetByKeyRequestBuilder.class).narrow(entityClass);

            final JMethod interfaceMethod = serviceInterfaceClass.method(JMod.NONE, getByKeyRequestBuilder, methodName);
            interfaceMethod.annotate(Nonnull.class);

            interfaceMethod
                .javadoc()
                .add(
                    String
                        .format(
                            "Fetch a single {@link %s %s} entity using key fields.",
                            entityClass.fullName(),
                            entityClass.name()));
            interfaceMethod
                .javadoc()
                .addReturn()
                .add(
                    String
                        .format(
                            "A request builder to fetch a single {@link %s %s} entity using key fields. "
                                + "This request builder allows methods which modify the underlying query to be called before executing the query itself. "
                                + "To perform execution, call the {@link %s#execute execute} method on the request builder object. ",
                            entityClass.fullName(),
                            entityClass.name(),
                            getByKeyRequestBuilder.fullName()));

            final JMethod implementationMethod =
                serviceImplementationClass.method(JMod.PUBLIC, getByKeyRequestBuilder, methodName);
            implementationMethod.annotate(Override.class);
            implementationMethod.annotate(Nonnull.class);

            //Adding the key parameters to the methods
            final Map<String, JVar> generatedParameters = new HashMap<>();
            for( final EntityPropertyModel keyProperty : keyProperties ) {
                final JVar interfaceKeyParameter =
                    interfaceMethod.param(JMod.FINAL, keyProperty.getJavaFieldClass(), keyProperty.getJavaFieldName());

                final JCommentPart parameterJavadoc = interfaceMethod.javadoc().addParam(interfaceKeyParameter);
                parameterJavadoc.add(keyProperty.getBasicDescription());
                if( !Strings.isNullOrEmpty(keyProperty.getConstraintsDescription()) ) {
                    parameterJavadoc.add(String.format("<p>%s</p>", keyProperty.getConstraintsDescription()));
                }

                final JVar implementationKeyParameter =
                    implementationMethod
                        .param(JMod.FINAL, keyProperty.getJavaFieldClass(), keyProperty.getJavaFieldName());
                generatedParameters.put(keyProperty.getJavaFieldName(), implementationKeyParameter);
            }

            final JClass keyType = codeModel.ref(String.class);
            final JClass valueType = codeModel.ref(Object.class);
            final JVar key =
                implementationMethod
                    .body()
                    .decl(
                        JMod.FINAL,
                        codeModel.ref(Map.class).narrow(keyType, valueType),
                        "key",
                        JExpr._new(codeModel.ref(HashMap.class).narrow(keyType, valueType)));

            for( final EntityPropertyModel model : keyProperties ) {
                implementationMethod
                    .body()
                    .add(
                        key
                            .invoke("put")
                            .arg(model.getEdmName())
                            .arg(generatedParameters.get(model.getJavaFieldName())));
            }

            final JInvocation methodBody =
                JExpr
                    ._new(getByKeyRequestBuilder)
                    .arg(JExpr.ref(SERVICE_PATH_FIELD_NAMING))
                    .arg(entityClass.dotclass())
                    .arg(key)
                    .arg(entityMetadata.getEntitySetName());

            implementationMethod.body()._return(methodBody);
        }

        void addGetAllMethod( final EntityMetadata entityMetadata )
        {
            final JDefinedClass entityClass = entityMetadata.getGeneratedEntityClass();

            final JClass getAllRequestBuilder = codeModel.ref(GetAllRequestBuilder.class).narrow(entityClass);

            final String methodNameSuffix =
                serviceMethodsPerEntitySet
                    ? getMethodNameSuffixFromEntitySet(entityMetadata.getEntitySetName())
                    : entityClass.name();

            final String methodName = NamingUtils.deriveGetAllEntitiesServiceMethodName(methodNameSuffix);

            final JMethod interfaceMethod = serviceInterfaceClass.method(JMod.NONE, getAllRequestBuilder, methodName);
            interfaceMethod.annotate(Nonnull.class);

            interfaceMethod
                .javadoc()
                .add(
                    String
                        .format("Fetch multiple {@link %s %s} entities.", entityClass.fullName(), entityClass.name()));
            interfaceMethod
                .javadoc()
                .addReturn()
                .add(
                    String
                        .format(
                            "A request builder to fetch multiple {@link %s %s} entities. "
                                + "This request builder allows methods which modify the underlying query to be called before executing the query itself. "
                                + "To perform execution, call the {@link %s#execute execute} method on the request builder object. ",
                            entityClass.fullName(),
                            entityClass.name(),
                            getAllRequestBuilder.fullName()));

            final JMethod implementationMethod =
                serviceImplementationClass.method(JMod.PUBLIC, getAllRequestBuilder, methodName);
            implementationMethod.annotate(Override.class);
            implementationMethod.annotate(Nonnull.class);

            final JInvocation methodBody =
                JExpr
                    ._new(getAllRequestBuilder)
                    .arg(JExpr.ref(SERVICE_PATH_FIELD_NAMING))
                    .arg(entityClass.dotclass())
                    .arg(entityMetadata.getEntitySetName());

            implementationMethod.body()._return(methodBody);
        }

        void addCountMethod( final EntityMetadata entityMetadata )
        {
            final JDefinedClass entityClass = entityMetadata.getGeneratedEntityClass();
            final JClass countRequestBuilder = codeModel.ref(CountRequestBuilder.class).narrow(entityClass);

            final String methodNameSuffix =
                serviceMethodsPerEntitySet
                    ? getMethodNameSuffixFromEntitySet(entityMetadata.getEntitySetName())
                    : entityClass.name();

            final String methodName = NamingUtils.deriveCountEntitiesServiceMethodName(methodNameSuffix);

            final JMethod interfaceMethod = serviceInterfaceClass.method(JMod.NONE, countRequestBuilder, methodName);
            interfaceMethod.annotate(Nonnull.class);

            interfaceMethod
                .javadoc()
                .add(
                    String
                        .format(
                            "Fetch the number of entries from the {@link %s %s} entity collection matching the filter and search expressions.",
                            entityClass.fullName(),
                            entityClass.name()));
            interfaceMethod
                .javadoc()
                .addReturn()
                .add(
                    String
                        .format(
                            "A request builder to fetch the count of {@link %s %s} entities. "
                                + "This request builder allows methods which modify the underlying query to be called before executing the query itself. "
                                + "To perform execution, call the {@link %s#execute execute} method on the request builder object. ",
                            entityClass.fullName(),
                            entityClass.name(),
                            countRequestBuilder.fullName()));

            final JMethod implementationMethod =
                serviceImplementationClass.method(JMod.PUBLIC, countRequestBuilder, methodName);
            implementationMethod.annotate(Override.class);
            implementationMethod.annotate(Nonnull.class);

            final JInvocation methodBody =
                JExpr
                    ._new(countRequestBuilder)
                    .arg(JExpr.ref(SERVICE_PATH_FIELD_NAMING))
                    .arg(entityClass.dotclass())
                    .arg(entityMetadata.getEntitySetName());

            implementationMethod.body()._return(methodBody);
        }

        void addUnboundOperation(
            final String edmName,
            final String edmLabel,
            final String description,
            final List<OperationParameterModel> parameters,
            final NamingContext functionImportFetchMethodNamingContext,
            final boolean isCollectionReturnType,
            final JClass javaReturnType,
            final boolean isFunction )
        {
            final String methodNameFetch =
                functionImportFetchMethodNamingContext
                    .ensureUniqueName(codeNamingStrategy.generateJavaOperationMethodName(edmName, edmLabel));
            final Class<?> unboundOperationRequestBuilder;

            if( isFunction ) {
                unboundOperationRequestBuilder =
                    isCollectionReturnType
                        ? CollectionValueFunctionRequestBuilder.class
                        : SingleValueFunctionRequestBuilder.class;
            }
            //Operation is unboundAction
            else {
                unboundOperationRequestBuilder =
                    isCollectionReturnType
                        ? CollectionValueActionRequestBuilder.class
                        : SingleValueActionRequestBuilder.class;
            }

            final List<List<OperationParameterModel>> unboundOperationFactoryMethods =
                classScanner
                    .determineArgumentsForMethod(
                        serviceInterfaceClass.fullName(),
                        methodNameFetch,
                        parameters,
                        OperationParameterModel::getJavaName);

            for( final List<OperationParameterModel> factoryMethodArguments : unboundOperationFactoryMethods ) {
                final JMethod interfaceMethod =
                    serviceInterfaceClass
                        .method(
                            JMod.NONE,
                            codeModel.ref(unboundOperationRequestBuilder).narrow(javaReturnType),
                            methodNameFetch);
                interfaceMethod.annotate(Nonnull.class);

                interfaceMethod.javadoc().add(Strings.isNullOrEmpty(description) ? "" : description);
                final String operation = isFunction ? "function" : "action";
                interfaceMethod
                    .javadoc()
                    .add(
                        String
                            .format(
                                "<p>Creates a request builder for the <b>%s</b> OData %s.</p>",
                                edmName,
                                operation));
                interfaceMethod
                    .javadoc()
                    .addReturn()
                    .add(
                        String
                            .format(
                                "A request builder object that will execute the <b>%s</b> OData %s with the provided parameters. "
                                    + "To perform execution, call the {@link %s#execute execute} method on the request builder object.",
                                edmName,
                                operation,
                                unboundOperationRequestBuilder.getName()));

                final JMethod implementationMethod =
                    serviceImplementationClass
                        .method(
                            JMod.PUBLIC,
                            codeModel.ref(unboundOperationRequestBuilder).narrow(javaReturnType),
                            methodNameFetch);
                implementationMethod.annotate(Override.class);
                implementationMethod.annotate(Nonnull.class);

                final JInvocation newHelperStatement =
                    JExpr._new(codeModel.ref(unboundOperationRequestBuilder).narrow(javaReturnType));
                newHelperStatement.arg(JExpr.ref(SERVICE_PATH_FIELD_NAMING));
                newHelperStatement.arg(edmName);

                JVar functionParameters = null;
                if( !parameters.isEmpty() ) {
                    final JClass fieldMapClass = codeModel.ref(LinkedHashMap.class).narrow(String.class, Object.class);
                    functionParameters =
                        implementationMethod
                            .body()
                            .decl(
                                JMod.FINAL,
                                fieldMapClass,
                                "parameters",
                                JExpr._new(codeModel.ref(LinkedHashMap.class).narrow(String.class, Object.class)));
                    newHelperStatement.arg(functionParameters);
                }
                for( final OperationParameterModel parameter : factoryMethodArguments ) {
                    final JVar interfaceParameter =
                        interfaceMethod.param(JMod.FINAL, parameter.getJavaType(), parameter.getJavaName());

                    final JCommentPart parameterDoc = interfaceMethod.javadoc().addParam(interfaceParameter);
                    if( !Strings.isNullOrEmpty(parameter.getDescription()) ) {
                        parameterDoc.add(parameter.getDescription());
                    }

                    final JVar implementationParameter =
                        implementationMethod.param(JMod.FINAL, parameter.getJavaType(), parameter.getJavaName());
                    implementationMethod
                        .body()
                        .invoke(functionParameters, "put")
                        .arg(parameter.getEdmName())
                        .arg(implementationParameter);

                    final Class<? extends Annotation> parameterAnnotationClass =
                        parameter.isNullable() ? Nullable.class : Nonnull.class;
                    interfaceParameter.annotate(parameterAnnotationClass);
                    implementationParameter.annotate(parameterAnnotationClass);

                }
                newHelperStatement.arg(javaReturnType.dotclass());
                implementationMethod.body()._return(newHelperStatement);
            }
        }

        void addCreateMethod( final EntityMetadata entityMetadata )
        {
            final JDefinedClass entityClass = entityMetadata.getGeneratedEntityClass();
            final JType createRequestBuilder = codeModel.ref(CreateRequestBuilder.class).narrow(entityClass);

            final String methodNameSuffix =
                serviceMethodsPerEntitySet
                    ? getMethodNameSuffixFromEntitySet(entityMetadata.getEntitySetName())
                    : entityClass.name();

            final String methodName = NamingUtils.deriveCreateEntityServiceMethodName(methodNameSuffix);
            final String parameterName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, entityClass.name());

            final JMethod interfaceMethod = serviceInterfaceClass.method(JMod.NONE, createRequestBuilder, methodName);
            interfaceMethod.annotate(Nonnull.class);

            final JVar interfaceEntityParam = interfaceMethod.param(JMod.FINAL, entityClass, parameterName);
            interfaceEntityParam.annotate(Nonnull.class);

            interfaceMethod
                .javadoc()
                .add(
                    String
                        .format(
                            "Create a new {@link %s %s} entity and save it to the S/4HANA system.",
                            entityClass.fullName(),
                            entityClass.name()));
            interfaceMethod
                .javadoc()
                .addParam(interfaceEntityParam)
                .add(
                    String
                        .format(
                            "{@link %s %s} entity object that will be created in the S/4HANA system.",
                            entityClass.fullName(),
                            entityClass.name()));
            interfaceMethod
                .javadoc()
                .addReturn()
                .add(
                    String
                        .format(
                            "A request builder to create a new {@link %s %s} entity. "
                                + "To perform execution, call the {@link %s#execute execute} method on the request builder object. ",
                            entityClass.fullName(),
                            entityClass.name(),
                            createRequestBuilder.fullName()));

            final JMethod implementationMethod =
                serviceImplementationClass.method(JMod.PUBLIC, createRequestBuilder, methodName);
            implementationMethod.annotate(Override.class);
            implementationMethod.annotate(Nonnull.class);

            final JVar implementationEntityParam = implementationMethod.param(JMod.FINAL, entityClass, parameterName);
            implementationEntityParam.annotate(Nonnull.class);

            final JInvocation methodBody =
                JExpr
                    ._new(createRequestBuilder)
                    .arg(JExpr.ref(SERVICE_PATH_FIELD_NAMING))
                    .arg(implementationEntityParam)
                    .arg(entityMetadata.getEntitySetName());

            implementationMethod.body()._return(methodBody);
        }

        void addUpdateMethod( final EntityMetadata entityMetadata )
        {
            final JDefinedClass entityClass = entityMetadata.getGeneratedEntityClass();
            final JType updateRequestBuilder = codeModel.ref(UpdateRequestBuilder.class).narrow(entityClass);

            final String methodNameSuffix =
                serviceMethodsPerEntitySet
                    ? getMethodNameSuffixFromEntitySet(entityMetadata.getEntitySetName())
                    : entityClass.name();

            final String methodName = NamingUtils.deriveUpdateEntityServiceMethodName(methodNameSuffix);
            final String parameterName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, entityClass.name());

            final JMethod interfaceMethod = serviceInterfaceClass.method(JMod.NONE, updateRequestBuilder, methodName);
            interfaceMethod.annotate(Nonnull.class);

            final JVar interfaceEntityParam = interfaceMethod.param(JMod.FINAL, entityClass, parameterName);
            interfaceEntityParam.annotate(Nonnull.class);

            interfaceMethod
                .javadoc()
                .add(
                    String
                        .format(
                            "Update an existing {@link %s %s} entity and save it to the S/4HANA system.",
                            entityClass.fullName(),
                            entityClass.name()));
            interfaceMethod
                .javadoc()
                .addParam(interfaceEntityParam)
                .add(
                    String
                        .format(
                            "{@link %s %s} entity object that will be updated in the S/4HANA system.",
                            entityClass.fullName(),
                            entityClass.name()));
            interfaceMethod
                .javadoc()
                .addReturn()
                .add(
                    String
                        .format(
                            "A request builder to update an existing {@link %s %s} entity. "
                                + "To perform execution, call the {@link %s#execute execute} method on the request builder object. ",
                            entityClass.fullName(),
                            entityClass.name(),
                            updateRequestBuilder.fullName()));

            final JMethod implementationMethod =
                serviceImplementationClass.method(JMod.PUBLIC, updateRequestBuilder, methodName);
            implementationMethod.annotate(Override.class);
            implementationMethod.annotate(Nonnull.class);

            final JVar implementationEntityParam = implementationMethod.param(JMod.FINAL, entityClass, parameterName);
            implementationEntityParam.annotate(Nonnull.class);

            final JInvocation methodBody =
                JExpr
                    ._new(updateRequestBuilder)
                    .arg(JExpr.ref(SERVICE_PATH_FIELD_NAMING))
                    .arg(implementationEntityParam)
                    .arg(entityMetadata.getEntitySetName());

            implementationMethod.body()._return(methodBody);
        }

        void addDeleteMethod( final EntityMetadata entityMetadata )
        {
            final JDefinedClass entityClass = entityMetadata.getGeneratedEntityClass();
            final JType deleteRequestBuilder = codeModel.ref(DeleteRequestBuilder.class).narrow(entityClass);

            final String methodNameSuffix =
                serviceMethodsPerEntitySet
                    ? getMethodNameSuffixFromEntitySet(entityMetadata.getEntitySetName())
                    : entityClass.name();

            final String methodName = NamingUtils.deriveDeleteEntityServiceMethodName(methodNameSuffix);

            final String parameterName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, entityClass.name());

            final JMethod interfaceMethod = serviceInterfaceClass.method(JMod.NONE, deleteRequestBuilder, methodName);
            interfaceMethod.annotate(Nonnull.class);

            final JVar interfaceEntityParam = interfaceMethod.param(JMod.FINAL, entityClass, parameterName);
            interfaceEntityParam.annotate(Nonnull.class);

            interfaceMethod
                .javadoc()
                .add(
                    String
                        .format(
                            "Deletes an existing {@link %s %s} entity in the S/4HANA system.",
                            entityClass.fullName(),
                            entityClass.name()));
            interfaceMethod
                .javadoc()
                .addParam(interfaceEntityParam)
                .add(
                    String
                        .format(
                            "{@link %s %s} entity object that will be deleted in the S/4HANA system.",
                            entityClass.fullName(),
                            entityClass.name()));
            interfaceMethod
                .javadoc()
                .addReturn()
                .add(
                    String
                        .format(
                            "A request builder to delete an existing {@link %s %s} entity. "
                                + "To perform execution, call the {@link %s#execute execute} method on the request builder object. ",
                            entityClass.fullName(),
                            entityClass.name(),
                            deleteRequestBuilder.fullName()));

            final JMethod implementationMethod =
                serviceImplementationClass.method(JMod.PUBLIC, deleteRequestBuilder, methodName);
            implementationMethod.annotate(Override.class);
            implementationMethod.annotate(Nonnull.class);

            final JVar implementationEntityParam = implementationMethod.param(JMod.FINAL, entityClass, parameterName);
            implementationEntityParam.annotate(Nonnull.class);

            final JInvocation methodBody =
                JExpr
                    ._new(deleteRequestBuilder)
                    .arg(JExpr.ref(SERVICE_PATH_FIELD_NAMING))
                    .arg(implementationEntityParam)
                    .arg(entityMetadata.getEntitySetName());

            implementationMethod.body()._return(methodBody);
        }
    }

    private static String getMethodNameSuffixFromEntitySet( final String entitySetName )
    {
        return StringUtils.removeStartIgnoreCase(entitySetName, "A_");
    }

    Option<String> getQualifiedServiceInterfaceName( final String serviceName )
    {
        return Option.of(generatedServiceInterfaceClasses.get(serviceName)).map(JDefinedClass::fullName);
    }

    Option<String> getQualifiedServiceImplementationClassName( final String serviceName )
    {
        return Option.of(generatedServiceImplementationClasses.get(serviceName)).map(JDefinedClass::fullName);
    }

    boolean wasServiceGenerated( final String serviceName )
    {
        return generatedServiceInterfaceClasses.containsKey(serviceName);
    }
}
