/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odatav4.expression.FieldOrdering;
import com.sap.cloud.sdk.datamodel.odatav4.expression.FilterableBoolean;
import com.sap.cloud.sdk.datamodel.odatav4.expression.FilterableCollection;
import com.sap.cloud.sdk.datamodel.odatav4.expression.FilterableDate;
import com.sap.cloud.sdk.datamodel.odatav4.expression.FilterableDateTime;
import com.sap.cloud.sdk.datamodel.odatav4.expression.FilterableDuration;
import com.sap.cloud.sdk.datamodel.odatav4.expression.FilterableEnum;
import com.sap.cloud.sdk.datamodel.odatav4.expression.FilterableGuid;
import com.sap.cloud.sdk.datamodel.odatav4.expression.FilterableNumericDecimal;
import com.sap.cloud.sdk.datamodel.odatav4.expression.FilterableNumericInteger;
import com.sap.cloud.sdk.datamodel.odatav4.expression.FilterableString;
import com.sap.cloud.sdk.datamodel.odatav4.expression.FilterableTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Simple property.
 *
 * @param <EntityT>
 */
@SuppressWarnings( "PMD.UnnecessaryFullyQualifiedName" ) // due to java.lang.String
public interface SimpleProperty<EntityT> extends Property<EntityT>
{
    /**
     * A pseudo property referencing all fields.
     *
     * @param <Entity>
     *            The entity type.
     */
    class All<Entity> implements SimpleProperty<Entity>
    {
        @Getter
        private final java.lang.String fieldName = "";
        @Getter
        private final List<java.lang.String> selections = Collections.singletonList("*");
    }

    /**
     * A String property.
     *
     * @param <EntityT>
     *            The entity type.
     */
    @Getter
    @RequiredArgsConstructor
    class String<EntityT> implements SimpleProperty<EntityT>, FilterableString<EntityT>
    {
        private final Class<EntityT> entityType;
        private final java.lang.String fieldName;
    }

    /**
     * A Boolean property.
     *
     * @param <EntityT>
     *            The entity type.
     */
    @Getter
    @RequiredArgsConstructor
    class Boolean<EntityT> implements SimpleProperty<EntityT>, FilterableBoolean<EntityT>
    {
        private final Class<EntityT> entityType;
        private final java.lang.String fieldName;
    }

    /**
     * A Decimal property.
     *
     * @param <EntityT>
     *            The entity type.
     */
    @Getter
    @RequiredArgsConstructor
    class NumericDecimal<EntityT> implements SimpleProperty<EntityT>, FilterableNumericDecimal<EntityT>
    {
        private final Class<EntityT> entityType;
        private final java.lang.String fieldName;
    }

    /**
     * An Integer property.
     *
     * @param <EntityT>
     *            The entity type.
     */
    @Getter
    @RequiredArgsConstructor
    class NumericInteger<EntityT> implements SimpleProperty<EntityT>, FilterableNumericInteger<EntityT>
    {
        private final Class<EntityT> entityType;
        private final java.lang.String fieldName;
    }

    /**
     * A Guid property.
     *
     * @param <EntityT>
     *            The entity type.
     */
    @Getter
    @RequiredArgsConstructor
    class Guid<EntityT> implements SimpleProperty<EntityT>, FilterableGuid<EntityT>
    {
        private final Class<EntityT> entityType;
        private final java.lang.String fieldName;
    }

    /**
     * A Binary property.
     *
     * @param <EntityT>
     *            The entity type.
     */
    @Getter
    @RequiredArgsConstructor
    class Binary<EntityT> implements SimpleProperty<EntityT>
    {
        private final Class<EntityT> entityType;
        private final java.lang.String fieldName;
    }

    /**
     * A Duration property.
     *
     * @param <EntityT>
     *            The entity type.
     */
    @Getter
    @RequiredArgsConstructor
    class Duration<EntityT> implements SimpleProperty<EntityT>, FilterableDuration<EntityT>
    {
        private final Class<EntityT> entityType;
        private final java.lang.String fieldName;
    }

    /**
     * A DateTime property.
     *
     * @param <EntityT>
     *            The entity type.
     */
    @Getter
    @RequiredArgsConstructor
    class DateTime<EntityT> implements SimpleProperty<EntityT>, FilterableDateTime<EntityT>
    {
        private final Class<EntityT> entityType;
        private final java.lang.String fieldName;
    }

    /**
     * A Date property.
     *
     * @param <EntityT>
     *            The entity type.
     */
    @Getter
    @RequiredArgsConstructor
    class Date<EntityT> implements SimpleProperty<EntityT>, FilterableDate<EntityT>
    {
        private final Class<EntityT> entityType;
        private final java.lang.String fieldName;
    }

    /**
     * A Time property.
     *
     * @param <EntityT>
     *            The entity type.
     */
    @Getter
    @RequiredArgsConstructor
    class Time<EntityT> implements SimpleProperty<EntityT>, FilterableTime<EntityT>
    {
        private final Class<EntityT> entityType;
        private final java.lang.String fieldName;
    }

    /**
     * A composite property holding a collection of values.
     *
     * @param <EntityT>
     *            The entity type.
     * @param <ValueT>
     *            The collection item type.
     */
    @Getter
    @RequiredArgsConstructor
    class Collection<EntityT, ValueT> implements SimpleProperty<EntityT>, FilterableCollection<EntityT, ValueT>
    {
        private final Class<EntityT> entityType;
        private final java.lang.String fieldName;
        private final Class<ValueT> itemType;
    }

    /**
     * A property with predefined possible values.
     *
     * @param <EntityT>
     *            The entity type.
     * @param <EnumT>
     *            The Enum type.
     */
    @Getter
    @RequiredArgsConstructor
    class Enum<EntityT, EnumT extends VdmEnum> implements SimpleProperty<EntityT>, FilterableEnum<EntityT, EnumT>
    {
        private final Class<EntityT> entityType;
        private final java.lang.String fieldName;
        private final java.lang.String enumType;
    }

    /**
     * A property for order ascending.
     *
     * @return The FieldOrdering which has the field and ordering.
     */
    @Nonnull
    default FieldOrdering<EntityT> asc()
    {
        return FieldOrdering.asc(this);
    }

    /**
     * A property for order descending.
     *
     * @return The FieldOrdering which has the field and ordering.
     */
    @Nonnull
    default FieldOrdering<EntityT> desc()
    {
        return FieldOrdering.desc(this);
    }
}
