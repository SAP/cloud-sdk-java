/*
* Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
*/
package com.sap.cloud.sdk.datamodel.odatav4.generator;

/*import javax.annotation.Nonnull;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
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
            createClassHeader(targetPackage, interfaceToImplement, changeSetInterface, basicServiceClass);
        createBasicServiceField(defaultImplementation, basicServiceClass, service);
        createConstructor(defaultImplementation, basicServiceClass, service);
        implementGetServicePathForBatchRequestMethod(defaultImplementation, basicServiceClass, service);
        implementGetThisMethod(defaultImplementation);

        return new ClassStub(defaultImplementation);
    }

    private void implementGetThisMethod( final JDefinedClass defaultImplementation )
    {
        final JMethod createdMethod = defaultImplementation.method(JMod.PROTECTED, defaultImplementation, "getThis");
        createdMethod.annotate(Nonnull.class);
        createdMethod.annotate(Override.class);

        createdMethod.body()._return(JExpr._this());

        JavadocUtils.inheritJavadoc(createdMethod);
    }

    private void implementGetServicePathForBatchRequestMethod(
        final JDefinedClass defaultImplementation,
        final JDefinedClass basicServiceClass,
        final Service service )
    {
        final JMethod createdMethod =
            defaultImplementation.method(JMod.PROTECTED, String.class, "getServicePathForBatchRequest");
        createdMethod.annotate(Nonnull.class);
        createdMethod.annotate(Override.class);

        DeprecationUtils.addGetDefaultServicePathBody(createdMethod, basicServiceClass, service);

        JavadocUtils.inheritJavadoc(createdMethod);
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

        JavadocUtils.inheritJavadoc(createdMethod);
    }

    private JDefinedClass createClassHeader(
        final JClassContainer targetPackage,
        final JDefinedClass interfaceToImplement,
        final JDefinedClass changeSetInterface,
        final JDefinedClass basicServiceClass )
        throws JClassAlreadyExistsException
    {
        final JDefinedClass defaultImplementation =
            targetPackage._class(JMod.PUBLIC, createClassName(interfaceToImplement));

        defaultImplementation
            ._extends(codeModel.ref(BatchFluentHelperBasic.class).narrow(interfaceToImplement, changeSetInterface));

        defaultImplementation._implements(interfaceToImplement);

        defaultImplementation
            .javadoc()
            .add(
                String
                    .format(
                        "Default implementation of the {@link %s} interface exposed in the {@link %s %s}, allowing you to "
                            + "create multiple changesets and finally execute the batch request.",
                        interfaceToImplement.name(),
                        basicServiceClass.fullName(),
                        basicServiceClass.name()));

        return defaultImplementation;
    }

    private void createBasicServiceField(
        final JDefinedClass defaultImplementation,
        final JDefinedClass basicServiceClass,
        final Service service )
    {
        DeprecationUtils
            .createBasicServiceInterfaceField(defaultImplementation, basicServiceClass, SERVICE_FIELD_NAME, service);
    }

    private void createConstructor(
        final JDefinedClass defaultImplementation,
        final JDefinedClass basicServiceClass,
        final Service service )
    {
        // in case of a deprecated API we need to reference the service classes (annotated as deprecate) via the
        // full path to prevent an import of a deprecated class which would result in a warning
        if( service.isDeprecated() ) {
            createConstructorForDeprecatedService(defaultImplementation, basicServiceClass);
        } else {
            createConstructorForSupportedService(defaultImplementation, basicServiceClass);
        }
    }

    private void createConstructorForSupportedService(
        final JDefinedClass defaultImplementation,
        final JDefinedClass basicServiceClass )
    {
        final JMethod constructor = defaultImplementation.constructor(JMod.PUBLIC);
        final JVar param = constructor.param(JMod.FINAL, basicServiceClass, SERVICE_FIELD_NAME);
        param.annotate(Nonnull.class);
        constructor.body().assign(JExpr.refthis(SERVICE_FIELD_NAME), param);

        constructor.javadoc().add(String.format("Creates a new instance of this %s.", defaultImplementation.name()));
        constructor.javadoc().addParam(param).add("The service to execute all operations in this changeset on.");
    }

    private void createConstructorForDeprecatedService(
        final JDefinedClass defaultImplementation,
        final JDefinedClass basicServiceClass )
    {
        defaultImplementation
            .direct(
                "\n"
                    + DeprecationUtils.INDENT
                    + "/**\n"
                    + DeprecationUtils.INDENT
                    + " * Creates a new instance of this "
                    + defaultImplementation.name()
                    + "\n"
                    + DeprecationUtils.INDENT
                    + " *\n"
                    + DeprecationUtils.INDENT
                    + " * @param service\n"
                    + DeprecationUtils.INDENT
                    + " * The service to execute all operations in this changeset on.\n"
                    + DeprecationUtils.INDENT
                    + " *\n"
                    + DeprecationUtils.INDENT
                    + "@SuppressWarnings( \"deprecation\" )\n"
                    + DeprecationUtils.INDENT
                    + "public "
                    + defaultImplementation.name()
                    + "(@Nonnull final "
                    + basicServiceClass.fullName()
                    + " "
                    + SERVICE_FIELD_NAME
                    + "){\n"
                    + DeprecationUtils.INDENT
                    + DeprecationUtils.INDENT
                    + "this."
                    + SERVICE_FIELD_NAME
                    + " = "
                    + SERVICE_FIELD_NAME
                    + ";\n"
                    + DeprecationUtils.INDENT
                    + "}");
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
            DefaultServiceBatchGenerator.this.implementBeginChangeSetMethod(
                defaultServiceBatchStub,
                changeSetStub.getServiceBatchChangeSetStub(),
                implementationStub.getDefaultBatchChangeSetStub());
        }
    }
}*/
