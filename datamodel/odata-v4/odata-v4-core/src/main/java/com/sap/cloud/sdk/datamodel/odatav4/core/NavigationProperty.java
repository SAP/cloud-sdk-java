/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odatav4.expression.FilterableCollection;
import com.sap.cloud.sdk.datamodel.odatav4.expression.FilterableComplex;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Interface representing a navigational property of {@link EntityT} that points towards an entity of {@link TargetT}.
 *
 * @param <EntityT>
 *            Entity this property is part of.
 * @param <TargetT>
 *            Entity this property references.
 */
public interface NavigationProperty<EntityT extends VdmObject<?>, TargetT extends VdmEntity<?>>
    extends
    StructuredProperty<EntityT, TargetT>
{
    /**
     * A navigational property to a Collection of another entity reference.
     *
     * @param <EntityT>
     *            The entity type.
     * @param <ValueT>
     *            The navigable entity type.
     */
    @Getter
    @RequiredArgsConstructor
    class Collection<EntityT extends VdmObject<EntityT>, ValueT extends VdmEntity<ValueT>>
        extends
        NavigationPropertyCollection<EntityT, ValueT>
        implements
        NavigationProperty<EntityT, ValueT>,
        FilterableCollection<EntityT, ValueT>
    {
        private final Class<EntityT> entityType;
        private final String fieldName;
        private final Class<ValueT> itemType;
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
    class Single<EntityT extends VdmObject<EntityT>, ValueT extends VdmEntity<ValueT>>
        implements
        NavigationProperty<EntityT, ValueT>,
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
        public final NavigationPropertySingleQuery<EntityT, ValueT> select( @Nonnull final Property<ValueT>... fields )
        {
            return NavigationPropertySingleQuery.<EntityT, ValueT> ofSubQuery(getFieldName()).select(fields);
        }
    }
}
