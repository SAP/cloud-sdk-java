/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.helper.batch.FluentHelperBatchChangeSet;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.FluentHelperBatchEndChangeSet;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JCommentPart;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
class ServiceBatchChangeSetGenerator
{
    private final JCodeModel codeModel;

    InterfaceStub createInterfaceStub( final JClassContainer targetPackage, final JDefinedClass serviceInterface )
        throws JClassAlreadyExistsException
    {
        final JDefinedClass interfaceToCreate =
            targetPackage._interface(JMod.PUBLIC, createInterfaceName(serviceInterface));
        interfaceToCreate
            .javadoc()
            .add(
                String
                    .format(
                        "This interface enables you to combine multiple operations into one change set. For further information have a look into the {@link %s %s}.",
                        serviceInterface.fullName(),
                        serviceInterface.name()));
        return new InterfaceStub(interfaceToCreate);
    }

    /**
     * This method takes the given method of the general service interface as a blue print to create the batch
     * counterpart in the given interface.
     *
     * @param interfaceToAddTo
     *            The interface stub to add the method to.
     * @param interfaceMethod
     *            The blue print to copy the method from.
     */
    private JMethod addMethod( final JDefinedClass interfaceToAddTo, final JMethod interfaceMethod )
    {
        final JMethod methodAdded =
            interfaceToAddTo.method(interfaceMethod.mods().getValue(), interfaceToAddTo, interfaceMethod.name());

        for( final JVar paramToAdd : interfaceMethod.params() ) {
            final JVar paramAdded =
                methodAdded.param(paramToAdd.mods().getValue(), paramToAdd.type(), paramToAdd.name());
            final JCommentPart paramJavadoc = interfaceMethod.javadoc().addParam(paramToAdd);
            methodAdded.javadoc().addParam(paramAdded).addAll(paramJavadoc);
            for( final JAnnotationUse annotationToAdd : paramToAdd.annotations() ) {
                paramAdded.annotate(annotationToAdd.getAnnotationClass());
                // NOTE: we cannot copy over any annotation parameter (without effort)
            }
        }

        for( final JAnnotationUse methodAnnotationToAdd : interfaceMethod.annotations() ) {
            methodAdded.annotate(methodAnnotationToAdd.getAnnotationClass());
        }

        methodAdded.javadoc().addAll(interfaceMethod.javadoc());
        methodAdded
            .javadoc()
            .addReturn()
            .add(
                """
                    This fluent helper to continue adding operations to the change set. \
                    To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.\
                    """);

        return methodAdded;
    }

    private void addSuperClass( final JDefinedClass stubToModify, final JDefinedClass batchInterface )
    {
        stubToModify._extends(codeModel.ref(FluentHelperBatchEndChangeSet.class).narrow(batchInterface));
        stubToModify._extends(codeModel.ref(FluentHelperBatchChangeSet.class).narrow(stubToModify));
    }

    private static String createInterfaceName( final JDefinedClass serviceInterface )
    {
        return serviceInterface.name() + "BatchChangeSet";
    }

    @RequiredArgsConstructor( access = AccessLevel.PRIVATE )
    @Getter( AccessLevel.PACKAGE )
    class InterfaceStub
    {
        @Nonnull
        private final JDefinedClass serviceBatchChangeSetStub;

        JMethod addMethod( final JMethod interfaceMethod )
        {
            return ServiceBatchChangeSetGenerator.this.addMethod(serviceBatchChangeSetStub, interfaceMethod);
        }

        void addSuperClass( final ServiceBatchGenerator.InterfaceStub serviceBatchStub )
        {
            ServiceBatchChangeSetGenerator.this
                .addSuperClass(serviceBatchChangeSetStub, serviceBatchStub.getServiceBatchStub());
        }
    }

}
