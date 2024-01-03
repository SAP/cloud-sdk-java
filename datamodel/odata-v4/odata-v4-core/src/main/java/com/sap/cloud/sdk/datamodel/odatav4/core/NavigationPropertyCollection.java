/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odatav4.expression.FieldOrdering;
import com.sap.cloud.sdk.datamodel.odatav4.expression.FilterableBoolean;

/**
 * Abstract class representing the query capabilities of a collection of structured properties of {@link EntityT} with a
 * type of {@link TargetT}.
 *
 * @param <EntityT>
 *            Entity this property is part of.
 * @param <TargetT>
 *            Entity type of the collection this property references to.
 */
public abstract class NavigationPropertyCollection<EntityT extends VdmObject<?>, TargetT extends VdmEntity<?>>
    implements
    StructuredProperty<EntityT, TargetT>,
    ProtocolQueryReadCollection<TargetT>,
    ProtocolQueryRead<TargetT>
{
    @Override
    @SafeVarargs
    @Nonnull
    @SuppressWarnings( "varargs" )
    public final NavigationPropertyCollectionQuery<EntityT, TargetT> select(
        @Nonnull final Property<TargetT>... fields )
    {
        return NavigationPropertyCollectionQuery.<EntityT, TargetT> ofSubQuery(getFieldName()).select(fields);
    }

    @Override
    @SafeVarargs
    @Nonnull
    @SuppressWarnings( "varargs" )
    public final NavigationPropertyCollectionQuery<EntityT, TargetT> filter(
        @Nonnull final FilterableBoolean<TargetT>... filters )
    {
        return NavigationPropertyCollectionQuery.<EntityT, TargetT> ofSubQuery(getFieldName()).filter(filters);
    }

    @Override
    @Nonnull
    public NavigationPropertyCollectionQuery<EntityT, TargetT> top( final int top )
    {
        return NavigationPropertyCollectionQuery.<EntityT, TargetT> ofSubQuery(getFieldName()).top(top);
    }

    @Override
    @Nonnull
    public NavigationPropertyCollectionQuery<EntityT, TargetT> skip( final int skip )
    {
        return NavigationPropertyCollectionQuery.<EntityT, TargetT> ofSubQuery(getFieldName()).skip(skip);
    }

    @Override
    @SafeVarargs
    @Nonnull
    @SuppressWarnings( "varargs" )
    public final NavigationPropertyCollectionQuery<EntityT, TargetT> orderBy(
        @Nonnull final FieldOrdering<TargetT>... ordering )
    {
        return NavigationPropertyCollectionQuery.<EntityT, TargetT> ofSubQuery(getFieldName()).orderBy(ordering);
    }

    @Override
    @Nonnull
    public NavigationPropertyCollectionQuery<EntityT, TargetT> search( @Nonnull final String search )
    {
        return NavigationPropertyCollectionQuery.<EntityT, TargetT> ofSubQuery(getFieldName()).search(search);
    }

    @Override
    @Nonnull
    public NavigationPropertyCollectionQuery<EntityT, TargetT> search( @Nonnull final SearchExpression expression )
    {
        return NavigationPropertyCollectionQuery.<EntityT, TargetT> ofSubQuery(getFieldName()).search(expression);
    }
}
