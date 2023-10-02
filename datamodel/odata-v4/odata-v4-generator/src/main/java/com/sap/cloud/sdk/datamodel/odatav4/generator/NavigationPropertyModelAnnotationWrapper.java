/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.generator;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odatav4.generator.annotation.NavigationPropertyAnnotationModel;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class NavigationPropertyModelAnnotationWrapper implements NavigationPropertyAnnotationModel
{
    private final NavigationPropertyModel model;

    @Nonnull
    @Override
    public String getEdmName()
    {
        return model.getEdmName();
    }

    @Override
    public boolean isManyMultiplicity()
    {
        return Multiplicity.MANY == model.getMultiplicity();
    }
}
