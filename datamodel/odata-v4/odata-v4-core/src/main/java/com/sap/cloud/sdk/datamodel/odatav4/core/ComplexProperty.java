/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odatav4.expression.FilterableCollection;
import com.sap.cloud.sdk.datamodel.odatav4.expression.FilterableComplex;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Interface representing a complex property of {@link EntityT} that holds a complex type of {@link TargetT}.
 *
 * @param <EntityT>
 *            Entity this property is part of.
 * @param <TargetT>
 *            {@link VdmComplex} this property represents.
 */
public interface ComplexProperty<EntityT extends VdmObject<?>, TargetT extends VdmComplex<?>>
    extends
    StructuredProperty<EntityT, TargetT>
{
    /**
     * A collection of complex objects.
     *
     * @param <EntityT>
     *            The entity type.
     * @param <ValueT>
     *            The complex type.
     */
    @Getter
    @RequiredArgsConstructor
    class Collection<EntityT extends VdmObject<EntityT>, ValueT extends VdmComplex<ValueT>>
        implements
        ComplexProperty<EntityT, ValueT>,
        ProtocolQueryRead<ValueT>,
        FilterableCollection<EntityT, ValueT>
    {
        private final Class<EntityT> entityType;
        private final String fieldName;
        private final Class<ValueT> itemType;

        @Override
        @Nonnull
        @SafeVarargs
        @SuppressWarnings( "varargs" )
        public final ComplexPropertyQuery<EntityT, ValueT> select( @Nonnull final Property<ValueT>... fields )
        {
            return ComplexPropertyQuery.<EntityT, ValueT> onProperty(getFieldName()).select(fields);
        }
    }

    /**
     * A navigational property to a single other entity reference.
     *
     * @param <EntityT>
     *            The entity type.
     * @param <ValueT>
     *            The navigable entity type.
     */
    @Getter
    @RequiredArgsConstructor
    class Single<EntityT extends VdmObject<EntityT>, ValueT extends VdmComplex<ValueT>>
        implements
        ComplexProperty<EntityT, ValueT>,
        ProtocolQueryRead<ValueT>,
        FilterableComplex<EntityT, ValueT>
    {
        private final Class<EntityT> entityType;
        private final String fieldName;
        private final Class<ValueT> itemType;

        @Override
        @Nonnull
        @SafeVarargs
        @SuppressWarnings( "varargs" )
        public final ComplexPropertyQuery<EntityT, ValueT> select( @Nonnull final Property<ValueT>... fields )
        {
            return ComplexPropertyQuery.<EntityT, ValueT> onProperty(getFieldName()).select(fields);
        }
    }
}
