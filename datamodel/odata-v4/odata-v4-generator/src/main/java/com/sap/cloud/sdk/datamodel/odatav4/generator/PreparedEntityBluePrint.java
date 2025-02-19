package com.sap.cloud.sdk.datamodel.odatav4.generator;

import java.util.List;

import javax.annotation.Nonnull;

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
