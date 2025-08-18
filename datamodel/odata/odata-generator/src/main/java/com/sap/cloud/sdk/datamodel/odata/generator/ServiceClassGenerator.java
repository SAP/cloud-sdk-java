package com.sap.cloud.sdk.datamodel.odata.generator;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.google.common.escape.Escaper;
import com.google.common.html.HtmlEscapers;
import com.sap.cloud.sdk.cloudplatform.util.StringUtils;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchService;
import com.sap.cloud.sdk.datamodel.odata.utility.LegacyClassScanner;
import com.sap.cloud.sdk.datamodel.odata.utility.NamingStrategy;
import com.sap.cloud.sdk.datamodel.odata.utility.NamingUtils;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JClassContainer;
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
import com.sun.codemodel.JVar;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
class ServiceClassGenerator
{
    private static final String SERVICE_INTERFACE_NAMING = "%sService";
    private static final String SERVICE_IMPLEMENTATION_NAMING = "Default%sService";
    static final String DEFAULT_SERVICE_PATH_FIELD_NAMING = "DEFAULT_SERVICE_PATH";
    private static final String SERVICE_PATH_FIELD_NAMING = "servicePath";

    private static final String BUSINESS_HUB_LINK_TEMPLATE =
        """
            <p>Reference: <a href='https://api.sap.com/shell/discover/contentpackage/SAPS4HANACloud/api/%s?section=OVERVIEW'>\
            SAP Business Accelerator Hub\
            </a></p>\
            """;

    private final Map<String, JDefinedClass> generatedServiceInterfaceClasses = new HashMap<>();
    private final Map<String, JDefinedClass> generatedServiceImplementationClasses = new HashMap<>();
    private final Map<String, BatchRelevantStubs> generatedBatchRelevantStubs = new HashMap<>();

    private final Escaper htmlEscaper = HtmlEscapers.htmlEscaper();

    private final JCodeModel codeModel;
    private final JPackage servicePackage;
    private final JPackage namespaceParentPackage;
    private final NamingStrategy codeNamingStrategy;
    private final ServiceBatchChangeSetGenerator serviceBatchChangeSetGenerator;
    private final ServiceBatchGenerator serviceBatchGenerator;
    private final DefaultServiceBatchGenerator defaultServiceBatchGenerator;
    private final DefaultServiceBatchChangeSetGenerator defaultServiceBatchChangeSetGenerator;
    private final boolean serviceMethodsPerEntitySet;
    private final LegacyClassScanner classScanner;
    @Setter( AccessLevel.PACKAGE )
    private String customDeprecationNoticeForService = null;

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
                                    String.join(", ", entry.getValues())));
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

        return interfaceClass;
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

        return serviceClass;
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

                throw new ODataGeneratorException("Failed to generate service interface " + formattedInterfaceName, e);
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
                log.error("Failed to get or generate class: {} for service: {}", formattedClassName, service.getName());
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

    @RequiredArgsConstructor
    private static class BatchRelevantStubs
    {
        final ServiceBatchGenerator.InterfaceStub serviceBatchInterface;
        final ServiceBatchChangeSetGenerator.InterfaceStub serviceBatchChangeSetInterface;
        final DefaultServiceBatchGenerator.ClassStub defaultServiceBatchImplementation;
        final DefaultServiceBatchChangeSetGenerator.ClassStub defaultServiceBatchChangeSetImplementation;
    }

    private BatchRelevantStubs getOrCreateBatchRelevantStubs(
        final JClassContainer targetPackage,
        final JDefinedClass serviceInterface,
        final JDefinedClass serviceImplementation,
        final Service service )
    {
        BatchRelevantStubs batchRelevantStubs = generatedBatchRelevantStubs.get(serviceInterface.name());
        if( batchRelevantStubs == null ) {
            try {
                final ServiceBatchGenerator.InterfaceStub serviceBatchInterface =
                    serviceBatchGenerator.createInterfaceStub(targetPackage, serviceInterface);
                final ServiceBatchChangeSetGenerator.InterfaceStub serviceBatchChangeSetInterface =
                    serviceBatchChangeSetGenerator.createInterfaceStub(targetPackage, serviceInterface);
                final DefaultServiceBatchGenerator.ClassStub defaultServiceBatchImplementation =
                    defaultServiceBatchGenerator
                        .createDefaultImplementation(
                            targetPackage,
                            serviceInterface,
                            serviceBatchInterface,
                            serviceBatchChangeSetInterface,
                            service);
                final DefaultServiceBatchChangeSetGenerator.ClassStub defaultServiceBatchChangeSetImplementation =
                    defaultServiceBatchChangeSetGenerator
                        .createDefaultImplementation(
                            targetPackage,
                            serviceInterface,
                            serviceBatchChangeSetInterface,
                            serviceBatchInterface,
                            defaultServiceBatchImplementation,
                            service);

                serviceBatchChangeSetInterface.addSuperClass(serviceBatchInterface);
                serviceBatchInterface.addSuperClass(serviceBatchChangeSetInterface);
                defaultServiceBatchImplementation
                    .implementBeginChangeSetMethod(
                        serviceBatchChangeSetInterface,
                        defaultServiceBatchChangeSetImplementation);

                addBatchSupport(
                    serviceInterface,
                    serviceImplementation,
                    serviceBatchInterface,
                    defaultServiceBatchImplementation);

                batchRelevantStubs =
                    new BatchRelevantStubs(
                        serviceBatchInterface,
                        serviceBatchChangeSetInterface,
                        defaultServiceBatchImplementation,
                        defaultServiceBatchChangeSetImplementation);
                generatedBatchRelevantStubs.put(serviceInterface.name(), batchRelevantStubs);
            }
            catch( final JClassAlreadyExistsException e ) {
                // should not happen, as we checked for existence before generating the implementation
                throw new ODataGeneratorException(e);
            }
        }
        return batchRelevantStubs;
    }

    private void addBatchSupport(
        final JDefinedClass serviceInterface,
        final JDefinedClass serviceImplementation,
        final ServiceBatchGenerator.InterfaceStub batchInterface,
        final DefaultServiceBatchGenerator.ClassStub batchImplementation )
    {
        serviceInterface._extends(codeModel.ref(BatchService.class).narrow(batchInterface.getServiceBatchStub()));

        final JMethod batchMethodImplementation =
            serviceImplementation.method(JMod.PUBLIC, batchImplementation.getDefaultServiceBatchStub(), "batch");

        batchMethodImplementation.annotate(Override.class);
        batchMethodImplementation.annotate(Nonnull.class);

        batchMethodImplementation
            .body()
            ._return(
                JExpr
                    ._new(batchImplementation.getDefaultServiceBatchStub())
                    .arg(JExpr._this())
                    .arg(JExpr.ref(SERVICE_PATH_FIELD_NAMING)));
    }

    ServiceClassAmplifier getOrGenerateServiceClassesAndGetAmplifier( final Service service )
    {
        final JPackage concreteBatchPackage =
            namespaceParentPackage.subPackage(service.getJavaPackageName()).subPackage("batch");

        final JDefinedClass serviceInterfaceClass = getOrGenerateServiceInterfaceClass(service);
        final JDefinedClass serviceImplementationClass =
            getOrGenerateServiceImplementationClass(service, serviceInterfaceClass);
        final BatchRelevantStubs batchRelevantStubs =
            getOrCreateBatchRelevantStubs(
                concreteBatchPackage,
                serviceInterfaceClass,
                serviceImplementationClass,
                service);

        return new ServiceClassAmplifier(
            serviceInterfaceClass,
            serviceImplementationClass,
            batchRelevantStubs.serviceBatchChangeSetInterface,
            batchRelevantStubs.defaultServiceBatchChangeSetImplementation,
            codeNamingStrategy,
            serviceMethodsPerEntitySet,
            classScanner);
    }

    @RequiredArgsConstructor
    static final class ServiceClassAmplifier
    {
        @Getter( AccessLevel.PACKAGE )
        private final JDefinedClass serviceInterfaceClass;
        private final JDefinedClass serviceImplementationClass;
        private final ServiceBatchChangeSetGenerator.InterfaceStub serviceBatchChangeSetInterface;
        private final DefaultServiceBatchChangeSetGenerator.ClassStub defaultServiceBatchChangeSetImplementationImplementation;
        private final NamingStrategy codeNamingStrategy;
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
            final JDefinedClass generatedEntityByKeyFluentHelperClass,
            final String methodName,
            final Iterable<EntityPropertyModel> keyProperties )
        {
            final JDefinedClass entityClass = entityMetadata.getGeneratedEntityClass();

            final JMethod interfaceMethod =
                serviceInterfaceClass.method(JMod.NONE, generatedEntityByKeyFluentHelperClass, methodName);
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
                            """
                                A fluent helper to fetch a single {@link %s %s} entity using key fields. \
                                This fluent helper allows methods which modify the underlying query to be called before executing the query itself. \
                                To perform execution, call the {@link %s#execute execute} method on the fluent helper object. \
                                """,
                            entityClass.fullName(),
                            entityClass.name(),
                            generatedEntityByKeyFluentHelperClass.fullName()));

            final JMethod implementationMethod =
                serviceImplementationClass.method(JMod.PUBLIC, generatedEntityByKeyFluentHelperClass, methodName);
            implementationMethod.annotate(Override.class);
            implementationMethod.annotate(Nonnull.class);

            final Map<String, JVar> generatedParameters = new HashMap<>();
            for( final EntityPropertyModel keyProperty : keyProperties ) {
                final JVar interfaceKeyParameter =
                    interfaceMethod.param(JMod.FINAL, keyProperty.getJavaFieldType(), keyProperty.getJavaFieldName());

                final JCommentPart parameterJavadoc = interfaceMethod.javadoc().addParam(interfaceKeyParameter);
                parameterJavadoc.add(keyProperty.getBasicDescription());
                if( !Strings.isNullOrEmpty(keyProperty.getBasicDescription()) ) {
                    parameterJavadoc.add(String.format("<p>%s</p>", keyProperty.getConstraintsDescription()));
                }

                final JVar implementationKeyParameter =
                    implementationMethod
                        .param(JMod.FINAL, keyProperty.getJavaFieldType(), keyProperty.getJavaFieldName());

                generatedParameters.put(keyProperty.getJavaFieldName(), implementationKeyParameter);
            }

            final JInvocation newExp = JExpr._new(generatedEntityByKeyFluentHelperClass);
            newExp.arg(JExpr.ref(SERVICE_PATH_FIELD_NAMING)).arg(entityMetadata.getEntitySetName());
            for( final EntityPropertyModel model : keyProperties ) {
                newExp.arg(generatedParameters.get(model.getJavaFieldName()));
            }
            implementationMethod.body()._return(newExp);
        }

        void
            addGetAllMethod( final EntityMetadata entityMetadata, final JDefinedClass generatedEntityFluentHelperClass )
        {
            final JDefinedClass entityClass = entityMetadata.getGeneratedEntityClass();

            final String methodNameSuffix =
                serviceMethodsPerEntitySet
                    ? getMethodNameSuffixFromEntitySet(entityMetadata.getEntitySetName())
                    : entityClass.name();

            final String methodName = NamingUtils.deriveGetAllEntitiesServiceMethodName(methodNameSuffix);

            final JMethod interfaceMethod =
                serviceInterfaceClass.method(JMod.NONE, generatedEntityFluentHelperClass, methodName);
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
                            """
                                A fluent helper to fetch multiple {@link %s %s} entities. \
                                This fluent helper allows methods which modify the underlying query to be called before executing the query itself. \
                                To perform execution, call the {@link %s#execute execute} method on the fluent helper object. \
                                """,
                            entityClass.fullName(),
                            entityClass.name(),
                            generatedEntityFluentHelperClass.fullName()));

            final JMethod implementationMethod =
                serviceImplementationClass.method(JMod.PUBLIC, generatedEntityFluentHelperClass, methodName);
            implementationMethod.annotate(Override.class);
            implementationMethod.annotate(Nonnull.class);

            implementationMethod
                .body()
                ._return(
                    JExpr
                        ._new(generatedEntityFluentHelperClass)
                        .arg(JExpr.ref(SERVICE_PATH_FIELD_NAMING))
                        .arg(entityMetadata.getEntitySetName()));
        }

        void addFunctionImportMethod(
            final String edmName,
            final String edmLabel,
            final String description,
            final Iterable<FunctionImportParameterModel> parameters,
            final JDefinedClass functionImportFluentHelperClass,
            final NamingContext functionImportFetchMethodNamingContext )
        {
            final String methodNameFetch =
                functionImportFetchMethodNamingContext
                    .ensureUniqueName(codeNamingStrategy.generateJavaOperationMethodName(edmName, edmLabel));

            final List<List<FunctionImportParameterModel>> functionImportFactoryMethods =
                classScanner
                    .determineArgumentsForMethod(
                        serviceInterfaceClass.fullName(),
                        methodNameFetch,
                        parameters,
                        FunctionImportParameterModel::getJavaName);

            for( final List<FunctionImportParameterModel> factoryMethodArguments : functionImportFactoryMethods ) {
                final JMethod interfaceMethod =
                    serviceInterfaceClass.method(JMod.NONE, functionImportFluentHelperClass, methodNameFetch);
                interfaceMethod.annotate(Nonnull.class);

                interfaceMethod.javadoc().add(Strings.isNullOrEmpty(description) ? "" : description);
                interfaceMethod
                    .javadoc()
                    .add(
                        String
                            .format(
                                "<p>Creates a fluent helper for the <b>%s</b> OData function import.</p>",
                                edmName));
                interfaceMethod
                    .javadoc()
                    .addReturn()
                    .add(
                        String
                            .format(
                                """
                                    A fluent helper object that will execute the <b>%s</b> OData function import with the provided parameters. \
                                    To perform execution, call the {@link %s#execute execute} method on the fluent helper object.\
                                    """,
                                edmName,
                                functionImportFluentHelperClass.fullName()));

                final JMethod implementationMethod =
                    serviceImplementationClass.method(JMod.PUBLIC, functionImportFluentHelperClass, methodNameFetch);
                implementationMethod.annotate(Override.class);
                implementationMethod.annotate(Nonnull.class);

                final JInvocation newHelperStatement = JExpr._new(functionImportFluentHelperClass);
                newHelperStatement.arg(JExpr.ref(SERVICE_PATH_FIELD_NAMING));

                for( final FunctionImportParameterModel parameter : factoryMethodArguments ) {
                    final JVar interfaceParameter =
                        interfaceMethod.param(JMod.FINAL, parameter.getJavaType(), parameter.getJavaName());
                    interfaceParameter.annotate(parameter.isNonnull() ? Nonnull.class : Nullable.class);

                    final JCommentPart parameterDoc = interfaceMethod.javadoc().addParam(interfaceParameter);
                    if( !Strings.isNullOrEmpty(parameter.getDescription()) ) {
                        parameterDoc.add(parameter.getDescription());
                    }

                    final JVar implementationParameter =
                        implementationMethod.param(JMod.FINAL, parameter.getJavaType(), parameter.getJavaName());
                    implementationParameter.annotate(parameter.isNonnull() ? Nonnull.class : Nullable.class);
                    newHelperStatement.arg(implementationParameter);
                }

                implementationMethod.body()._return(newHelperStatement);
            }
        }

        void
            addCreateMethod( final EntityMetadata entityMetadata, final JDefinedClass generatedCreateFluentHelperClass )
        {
            final JDefinedClass entityClass = entityMetadata.getGeneratedEntityClass();

            final String methodNameSuffix =
                serviceMethodsPerEntitySet
                    ? getMethodNameSuffixFromEntitySet(entityMetadata.getEntitySetName())
                    : entityClass.name();

            final String methodName = NamingUtils.deriveCreateEntityServiceMethodName(methodNameSuffix);
            final String parameterName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, entityClass.name());

            final JMethod interfaceMethod =
                serviceInterfaceClass.method(JMod.NONE, generatedCreateFluentHelperClass, methodName);
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
            interfaceMethod.javadoc().addReturn().add(String.format("""
                A fluent helper to create a new {@link %s %s} entity. \
                To perform execution, call the {@link %s#execute execute} method on the fluent helper object. \
                """, entityClass.fullName(), entityClass.name(), generatedCreateFluentHelperClass.fullName()));

            final JMethod batchInterfaceMethod = serviceBatchChangeSetInterface.addMethod(interfaceMethod);
            defaultServiceBatchChangeSetImplementationImplementation
                .addCreateMethodImplementation(interfaceMethod, batchInterfaceMethod);

            final JMethod implementationMethod =
                serviceImplementationClass.method(JMod.PUBLIC, generatedCreateFluentHelperClass, methodName);
            implementationMethod.annotate(Override.class);
            implementationMethod.annotate(Nonnull.class);

            final JVar implementationEntityParam = implementationMethod.param(JMod.FINAL, entityClass, parameterName);
            implementationEntityParam.annotate(Nonnull.class);

            implementationMethod
                .body()
                ._return(
                    JExpr
                        ._new(generatedCreateFluentHelperClass)
                        .arg(JExpr.ref(SERVICE_PATH_FIELD_NAMING))
                        .arg(implementationEntityParam)
                        .arg(entityMetadata.getEntitySetName()));

        }

        void
            addUpdateMethod( final EntityMetadata entityMetadata, final JDefinedClass generatedUpdateFluentHelperClass )
        {
            final JDefinedClass entityClass = entityMetadata.getGeneratedEntityClass();

            final String methodNameSuffix =
                serviceMethodsPerEntitySet
                    ? getMethodNameSuffixFromEntitySet(entityMetadata.getEntitySetName())
                    : entityClass.name();

            final String methodName = NamingUtils.deriveUpdateEntityServiceMethodName(methodNameSuffix);
            final String parameterName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, entityClass.name());

            final JMethod interfaceMethod =
                serviceInterfaceClass.method(JMod.NONE, generatedUpdateFluentHelperClass, methodName);
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
            interfaceMethod.javadoc().addReturn().add(String.format("""
                A fluent helper to update an existing {@link %s %s} entity. \
                To perform execution, call the {@link %s#execute execute} method on the fluent helper object. \
                """, entityClass.fullName(), entityClass.name(), generatedUpdateFluentHelperClass.fullName()));

            final JMethod batchInterfaceMethod = serviceBatchChangeSetInterface.addMethod(interfaceMethod);
            defaultServiceBatchChangeSetImplementationImplementation
                .addUpdateMethodImplementation(interfaceMethod, batchInterfaceMethod);

            final JMethod implementationMethod =
                serviceImplementationClass.method(JMod.PUBLIC, generatedUpdateFluentHelperClass, methodName);
            implementationMethod.annotate(Override.class);
            implementationMethod.annotate(Nonnull.class);

            final JVar implementationEntityParam = implementationMethod.param(JMod.FINAL, entityClass, parameterName);
            implementationEntityParam.annotate(Nonnull.class);

            implementationMethod
                .body()
                ._return(
                    JExpr
                        ._new(generatedUpdateFluentHelperClass)
                        .arg(JExpr.ref(SERVICE_PATH_FIELD_NAMING))
                        .arg(implementationEntityParam)
                        .arg(entityMetadata.getEntitySetName()));
        }

        void
            addDeleteMethod( final EntityMetadata entityMetadata, final JDefinedClass generatedDeleteFluentHelperClass )
        {
            final JDefinedClass entityClass = entityMetadata.getGeneratedEntityClass();

            final String methodNameSuffix =
                serviceMethodsPerEntitySet
                    ? getMethodNameSuffixFromEntitySet(entityMetadata.getEntitySetName())
                    : entityClass.name();

            final String methodName = NamingUtils.deriveDeleteEntityServiceMethodName(methodNameSuffix);
            final String parameterName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, entityClass.name());

            final JMethod interfaceMethod =
                serviceInterfaceClass.method(JMod.NONE, generatedDeleteFluentHelperClass, methodName);
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
            interfaceMethod.javadoc().addReturn().add(String.format("""
                A fluent helper to delete an existing {@link %s %s} entity. \
                To perform execution, call the {@link %s#execute execute} method on the fluent helper object. \
                """, entityClass.fullName(), entityClass.name(), generatedDeleteFluentHelperClass.fullName()));

            final JMethod batchInterfaceMethod = serviceBatchChangeSetInterface.addMethod(interfaceMethod);
            defaultServiceBatchChangeSetImplementationImplementation
                .addDeleteMethodImplementation(interfaceMethod, batchInterfaceMethod);

            final JMethod implementationMethod =
                serviceImplementationClass.method(JMod.PUBLIC, generatedDeleteFluentHelperClass, methodName);
            implementationMethod.annotate(Override.class);
            implementationMethod.annotate(Nonnull.class);

            final JVar implementationEntityParam = implementationMethod.param(JMod.FINAL, entityClass, parameterName);
            implementationEntityParam.annotate(Nonnull.class);

            implementationMethod
                .body()
                ._return(
                    JExpr
                        ._new(generatedDeleteFluentHelperClass)
                        .arg(JExpr.ref(SERVICE_PATH_FIELD_NAMING))
                        .arg(implementationEntityParam)
                        .arg(entityMetadata.getEntitySetName()));
        }
    }

    private static String getMethodNameSuffixFromEntitySet( final String entitySetName )
    {
        return StringUtils.removeStartIgnoreCase(entitySetName, "A_");
    }

    Option<String> getQualifiedServiceInterfaceName( final String serviceName )
    {
        return Option.of(generatedServiceInterfaceClasses.get(serviceName).fullName());
    }

    Option<String> getQualifiedServiceImplementationClassName( final String serviceName )
    {
        return Option.of(generatedServiceImplementationClasses.get(serviceName).fullName());
    }

    boolean wasServiceGenerated( final String serviceName )
    {
        return generatedServiceInterfaceClasses.containsKey(serviceName);
    }
}
