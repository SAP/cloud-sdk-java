package com.sap.cloud.sdk.datamodel.odatav4.generator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.datamodel.odatav4.generator.annotation.EntityPropertyAnnotationModel;

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

    @Nullable
    @Override
    public Integer getPrecision()
    {
        return model.getPrecision();
    }

    @Nullable
    @Override
    public Integer getScale()
    {
        return model.getScale();
    }
}
