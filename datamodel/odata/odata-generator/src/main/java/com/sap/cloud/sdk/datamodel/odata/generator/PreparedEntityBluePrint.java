/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sun.codemodel.JDefinedClass;

import lombok.Data;

/**
 * This class contains the CodeModel classes and further models needed to add navigation properties and fetch methods.
 * <p>
 * This data is collected (for each entity) in a first run over all entities. In a next run all properties etc., that
 * rely on other entities such as links, are added based on this blue print.
 */
@Data
final class PreparedEntityBluePrint
{
    /**
     * The (partly) created entity.
     */
    @Nonnull
    private final JDefinedClass entityClass;

    /**
     * The more concrete sub-interface of the {@link com.sap.cloud.sdk.datamodel.odata.helper.EntitySelectable
     * EntitySelectable} interface for this entity.
     */
    @Nullable // in case of "POJO only"
    private final JDefinedClass selectableInterface;

    /**
     * The concrete sub-class of the {@link com.sap.cloud.sdk.datamodel.odata.helper.EntityLink EntityLink} class for
     * this entity used for one-to-many links.
     */
    @Nullable // in case of "POJO only"
    private final JDefinedClass entityOneToManyLinkClass;

    /**
     * The concrete sub-class of the {@link com.sap.cloud.sdk.datamodel.odata.helper.EntityLink EntityLink} class for
     * this entity used for one-to–one links.
     */
    @Nullable // in case of "POJO only"
    private final JDefinedClass entityOneToOneLinkClass;

    /**
     * A list containing all navigation properties that should be added to the entity.
     */
    @Nonnull
    private final List<NavigationPropertyModel> navigationProperties;

    /**
     * A list of all key properties used in the fetch call of the entity.
     */
    @Nonnull
    private final List<EntityPropertyModel> keyProperties;
}
