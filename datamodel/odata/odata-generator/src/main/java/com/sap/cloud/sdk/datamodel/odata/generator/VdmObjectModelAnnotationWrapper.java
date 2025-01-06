/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.generator.annotation.EntityAnnotationModel;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class VdmObjectModelAnnotationWrapper implements EntityAnnotationModel
{
    private final VdmObjectModel model;

    @Override
    public int getNumberOfProperties()
    {
        return model.getProperties().size();
    }

    @Nonnull
    @Override
    public String getJavaClassName()
    {
        return model.getJavaClassName();
    }
}
