package com.sap.cloud.sdk.datamodel.odata.generator;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchFluentHelperBasic;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
class DefaultServiceBatchGenerator
{
    private static final String SERVICE_FIELD_NAME = "service";
    private static final String SERVICE_PATH_FIELD_NAME = "servicePath";
    private static final String THIS = "this";

    private final JCodeModel codeModel;

    ClassStub createDefaultImplementation(
        final JClassContainer targetPackage,
        final JDefinedClass basicServiceClass,
        final ServiceBatchGenerator.InterfaceStub batchServiceStub,
        final ServiceBatchChangeSetGenerator.InterfaceStub changeSetStub,
        final Service service )
        throws JClassAlreadyExistsException
    {
        final JDefinedClass interfaceToImplement = batchServiceStub.getServiceBatchStub();
        final JDefinedClass changeSetInterface = changeSetStub.getServiceBatchChangeSetStub();

        final JDefinedClass defaultImplementation =
            createClassHeader(targetPackage, interfaceToImplement, changeSetInterface, unimport(basicServiceClass));
        createBasicServiceField(defaultImplementation, unimport(basicServiceClass), service);
        createBasicServicePathField(defaultImplementation);
        createConstructor(defaultImplementation, unimport(basicServiceClass), service);
        implementGetServicePathForBatchRequestMethod(defaultImplementation);
        implementGetThisMethod(defaultImplementation);

        return new ClassStub(defaultImplementation);
    }

    @Nonnull
    private JClass unimport( @Nonnull final JClass cl )
    {
        // avoid import due to potential type deprecation
        return codeModel.directClass(cl.fullName());
    }

    private void implementGetThisMethod( final JDefinedClass defaultImplementation )
    {
        final JMethod createdMethod = defaultImplementation.method(JMod.PROTECTED, defaultImplementation, "getThis");
        createdMethod.annotate(Nonnull.class);
        createdMethod.annotate(Override.class);

        createdMethod.body()._return(JExpr._this());
    }

    private void implementGetServicePathForBatchRequestMethod( final JDefinedClass defaultImplementation )
    {
        final JMethod method =
            defaultImplementation.method(JMod.PROTECTED, String.class, "getServicePathForBatchRequest");
        method.annotate(Nonnull.class);
        method.annotate(Override.class);
        method.body()._return(JExpr.ref(SERVICE_PATH_FIELD_NAME));
    }

    private void implementBeginChangeSetMethod(
        final JDefinedClass defaultImplementation,
        final JDefinedClass changeSetInterface,
        final JDefinedClass changeSetImplementation )
    {
        final JMethod createdMethod = defaultImplementation.method(JMod.PUBLIC, changeSetInterface, "beginChangeSet");
        createdMethod.annotate(Nonnull.class);
        createdMethod.annotate(Override.class);

        createdMethod
            .body()
            ._return(JExpr._new(changeSetImplementation).arg(JExpr._this()).arg(JExpr.ref(SERVICE_FIELD_NAME)));
    }

    private JDefinedClass createClassHeader(
        final JClassContainer targetPackage,
        final JDefinedClass interfaceToImplement,
        final JDefinedClass changeSetInterface,
        final JClass basicServiceClass )
        throws JClassAlreadyExistsException
    {
        final JDefinedClass defaultImplementation =
            targetPackage._class(JMod.PUBLIC, createClassName(interfaceToImplement));

        defaultImplementation
            ._extends(codeModel.ref(BatchFluentHelperBasic.class).narrow(interfaceToImplement, changeSetInterface));

        defaultImplementation._implements(interfaceToImplement);

        defaultImplementation.javadoc().add(String.format("""
            Default implementation of the {@link %s} interface exposed in the {@link %s %s}, allowing you to \
            create multiple changesets and finally execute the batch request.\
            """, interfaceToImplement.name(), basicServiceClass.fullName(), basicServiceClass.name()));

        return defaultImplementation;
    }

    private void createBasicServiceField(
        final JDefinedClass defaultImplementation,
        final JClass basicServiceClass,
        final Service service )
    {
        DeprecationUtils
            .createBasicServiceInterfaceField(defaultImplementation, basicServiceClass, SERVICE_FIELD_NAME, service);
    }

    private void createBasicServicePathField( final JDefinedClass defaultImplementation )
    {
        final JFieldVar serviceField =
            defaultImplementation.field(JMod.PRIVATE | JMod.FINAL, String.class, SERVICE_PATH_FIELD_NAME);
        serviceField.annotate(Nonnull.class);
    }

    private void createConstructor(
        final JDefinedClass defaultImplementation,
        final JClass basicServiceClass,
        final Service service )
    {
        createConstructorForSupportedService(defaultImplementation, basicServiceClass);

        // in case of a deprecated API we need to reference the service classes (annotated as deprecate) via the
        // full path to prevent an import of a deprecated class which would result in a warning
        if( service.isDeprecated() ) {
            defaultImplementation
                .constructors()
                .forEachRemaining(c -> c.annotate(SuppressWarnings.class).param("value", "deprecation"));
        }
    }

    private void createConstructorForSupportedService(
        final JDefinedClass defaultImplementation,
        final JClass basicServiceClass )
    {
        // first constructor using the default service path
        {
            final JMethod constructor = defaultImplementation.constructor(JMod.PUBLIC);
            final JVar param = constructor.param(JMod.FINAL, basicServiceClass, SERVICE_FIELD_NAME);
            param.annotate(Nonnull.class);
            constructor
                .body()
                .invoke(THIS)
                .arg(param)
                .arg(basicServiceClass.staticRef(ServiceClassGenerator.DEFAULT_SERVICE_PATH_FIELD_NAMING));
            constructor
                .javadoc()
                .add(String.format("Creates a new instance of this %s.", defaultImplementation.name()));
            constructor.javadoc().addParam(param).add("The service to execute all operations in this changeset on.");
        }

        // second constructor with dedicated service path
        {
            final JMethod constructor = defaultImplementation.constructor(JMod.PUBLIC);
            final JVar param1 = constructor.param(JMod.FINAL, basicServiceClass, SERVICE_FIELD_NAME);
            param1.annotate(Nonnull.class);
            final JVar param2 = constructor.param(JMod.FINAL, String.class, SERVICE_PATH_FIELD_NAME);
            param2.annotate(Nonnull.class);
            constructor.body().assign(JExpr.refthis(SERVICE_FIELD_NAME), param1);
            constructor.body().assign(JExpr.refthis(SERVICE_PATH_FIELD_NAME), param2);
            constructor
                .javadoc()
                .add(String.format("Creates a new instance of this %s.", defaultImplementation.name()));
            constructor.javadoc().addParam(param1).add("The service to execute all operations in this changeset on.");
            constructor.javadoc().addParam(param2).add("The custom service path to operate on.");
        }
    }

    private String createClassName( final JDefinedClass interfaceName )
    {
        return "Default" + interfaceName.name();
    }

    @RequiredArgsConstructor( access = AccessLevel.PRIVATE )
    @Getter( AccessLevel.PACKAGE )
    final class ClassStub
    {
        private final JDefinedClass defaultServiceBatchStub;

        void implementBeginChangeSetMethod(
            final ServiceBatchChangeSetGenerator.InterfaceStub changeSetStub,
            final DefaultServiceBatchChangeSetGenerator.ClassStub implementationStub )
        {
            DefaultServiceBatchGenerator.this
                .implementBeginChangeSetMethod(
                    defaultServiceBatchStub,
                    changeSetStub.getServiceBatchChangeSetStub(),
                    implementationStub.getDefaultBatchChangeSetStub());
        }
    }
}
