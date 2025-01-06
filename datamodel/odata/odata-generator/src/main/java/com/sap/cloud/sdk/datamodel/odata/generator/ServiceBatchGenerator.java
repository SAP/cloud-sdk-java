/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.helper.batch.FluentHelperServiceBatch;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMod;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
class ServiceBatchGenerator
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
                        "Interface to the batch object of an {@code %s %s} service.",
                        serviceInterface.fullName(),
                        serviceInterface.name()));

        return new InterfaceStub(interfaceToCreate);
    }

    private void addSuperClass( final JDefinedClass stubToModify, final JDefinedClass batchChangeSetInterface )
    {
        stubToModify
            ._extends(codeModel.ref(FluentHelperServiceBatch.class).narrow(stubToModify, batchChangeSetInterface));
    }

    private String createInterfaceName( final JDefinedClass serviceInterface )
    {
        return serviceInterface.name() + "Batch";
    }

    @RequiredArgsConstructor( access = AccessLevel.PRIVATE )
    @Getter( AccessLevel.PACKAGE )
    class InterfaceStub
    {
        @Nonnull
        private final JDefinedClass serviceBatchStub;

        void addSuperClass( final ServiceBatchChangeSetGenerator.InterfaceStub changeSetInterfaceStub )
        {
            ServiceBatchGenerator.this
                .addSuperClass(serviceBatchStub, changeSetInterfaceStub.getServiceBatchChangeSetStub());
        }
    }

}
