/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
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
 * generics. By implementing {@link AbstractStructuredPropertyQuery} the API doesn't differentiate between selections
 * via referencing complex properties and selections via sub-queries.
 *
 * @param <OwnerT>
 *            {@link VdmObject} this property is part of.
 * @param <PropertyT>
 *            {@link VdmComplex} type this property references to.
 */
public final class ComplexPropertyQuery<OwnerT extends VdmObject<?>, PropertyT extends VdmComplex<?>>
    extends
    AbstractStructuredPropertyQuery<OwnerT, PropertyT>
{
    private ComplexPropertyQuery( final StructuredQuery delegateQuery )
    {
        super(delegateQuery);
    }

    static <
        ParentEntityT extends VdmObject<?>, EntityT extends VdmComplex<?>>
        ComplexPropertyQuery<ParentEntityT, EntityT>
        onProperty( @Nonnull final String fieldName )
    {
        return new ComplexPropertyQuery<>(StructuredQuery.asNestedQueryOnProperty(fieldName, ODataProtocol.V4));
    }

    @Override
    @SafeVarargs
    @Nonnull
    @SuppressWarnings( "varargs" )
    public final ComplexPropertyQuery<OwnerT, PropertyT> select( @Nonnull final Property<PropertyT>... fields )
    {
        super.select(Arrays.asList(fields));
        return this;
    }
}
