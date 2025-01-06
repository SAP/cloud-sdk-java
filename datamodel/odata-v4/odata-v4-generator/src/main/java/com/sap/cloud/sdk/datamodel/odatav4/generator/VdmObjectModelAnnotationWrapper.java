package com.sap.cloud.sdk.datamodel.odatav4.generator;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odatav4.generator.annotation.EntityAnnotationModel;

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
