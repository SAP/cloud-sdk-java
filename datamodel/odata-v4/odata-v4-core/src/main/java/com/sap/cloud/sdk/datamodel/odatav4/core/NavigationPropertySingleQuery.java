/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import java.util.Arrays;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.query.StructuredQuery;

/**
 * Implementation that represents read queries and holds their state at runtime. It allows for nested queries in a
 * recursive manner. The implementation is the same for queries over single entities and collections of entities. In the
 * VDM the available functionality is limited by the interfaces.
 *
 * In order to support a fluent creation of nested queries both the entity and the parent entity type are stored via
 * generics. By implementing {@link NavigationProperty} the API doesn't differentiate between selections via referencing
 * navigational properties and selections via sub-queries.
 *
 * @param <ParentEntityT>
 *            The generic navigation property entity source type.
 * @param <EntityT>
 *            The generic navigation property entity target type.
 */
public final class NavigationPropertySingleQuery<ParentEntityT extends VdmObject<?>, EntityT extends VdmEntity<?>>
    extends
    AbstractStructuredPropertyQuery<ParentEntityT, EntityT>
{
    private NavigationPropertySingleQuery( final StructuredQuery delegateQuery )
    {
        super(delegateQuery);
    }

    static <
        ParentEntityT extends VdmObject<?>, EntityT extends VdmEntity<?>>
        NavigationPropertySingleQuery<ParentEntityT, EntityT>
        ofRootQuery( @Nonnull final String fieldName )
    {
        return new NavigationPropertySingleQuery<>(StructuredQuery.onEntity(fieldName, ODataProtocol.V4));
    }

    static <
        ParentEntityT extends VdmObject<?>, EntityT extends VdmEntity<?>>
        NavigationPropertySingleQuery<ParentEntityT, EntityT>
        ofSubQuery( @Nonnull final String fieldName )
    {
        return new NavigationPropertySingleQuery<>(
            StructuredQuery.asNestedQueryOnProperty(fieldName, ODataProtocol.V4));
    }

    @Override
    @SafeVarargs
    @Nonnull
    @SuppressWarnings( "varargs" )
    public final NavigationPropertySingleQuery<ParentEntityT, EntityT> select(
        @Nonnull final Property<EntityT>... fields )
    {
        super.select(Arrays.asList(fields));
        return this;
    }
}
