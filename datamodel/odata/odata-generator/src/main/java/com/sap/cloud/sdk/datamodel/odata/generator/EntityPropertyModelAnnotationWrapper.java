/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.generator.annotation.EntityPropertyAnnotationModel;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class EntityPropertyModelAnnotationWrapper implements EntityPropertyAnnotationModel
{
    private final EntityPropertyModel model;

    @Nonnull
    @Override
    public String getEdmName()
    {
        return model.getEdmName();
    }

    @Override
    public boolean isSimpleType()
    {
        return model.isSimpleType();
    }

    @Nonnull
    @Override
    public String getEdmType()
    {
        return model.getEdmType();
    }

    @Override
    public boolean isKeyField()
    {
        return model.isKeyField();
    }
}
