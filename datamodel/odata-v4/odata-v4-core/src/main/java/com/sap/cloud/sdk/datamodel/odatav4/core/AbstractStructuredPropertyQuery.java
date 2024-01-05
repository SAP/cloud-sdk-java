/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FieldReference;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FieldUntyped;
import com.sap.cloud.sdk.datamodel.odata.client.query.StructuredQuery;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Implementation that represents read queries and holds their state at runtime. It allows for nested queries in a
 * recursive manner. The implementation is the same for queries over single entities and collections of entities. In the
 * VDM the available functionality is limited by the interfaces.
 *
 * In order to support a fluent creation of nested queries both the entity and the parent entity type are stored via
 * generics. By implementing {@link StructuredProperty} the API doesn't differentiate between selections via referencing
 * navigational properties and selections via sub-queries.
 *
 * @param <ParentEntityT>
 *            The generic navigation property entity source type.
 * @param <EntityT>
 *            The generic navigation property entity target type.
 */
@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
abstract class AbstractStructuredPropertyQuery<ParentEntityT extends VdmObject<?>, EntityT extends VdmObject<?>>
    implements
    ProtocolQueryRead<EntityT>,
    StructuredProperty<ParentEntityT, EntityT>
{
    @Getter( AccessLevel.PROTECTED )
    protected final StructuredQuery delegateQuery;

    /**
     * Query modifier to limit which field values of the entity {@linkplain EntityT} get fetched and populated.
     *
     * @param fields
     *            Properties of {@linkplain EntityT} to be selected.
     * @return This query object with the added selections.
     */
    @Nonnull
    protected final AbstractStructuredPropertyQuery<ParentEntityT, EntityT> select(
        @Nonnull final Iterable<Property<EntityT>> fields )
    {
        for( final Property<EntityT> field : fields ) {
            if( field instanceof SimpleProperty || field instanceof ComplexProperty ) {
                delegateQuery.select(field.getFieldName());
                continue;

            }
            if( field instanceof StructuredProperty ) {
                if( field instanceof ComplexPropertyQuery ) {
                    for( final String simpleSelector : ((ComplexPropertyQuery<?, ?>) field)
                        .getDelegateQuery()
                        .getSimpleSelectors() ) {
                        final FieldUntyped untypedField = FieldReference.ofPath(field.getFieldName(), simpleSelector);
                        delegateQuery.select(untypedField.getFieldName());
                    }

                    for( final StructuredQuery subQuery : ((ComplexPropertyQuery<?, ?>) field)
                        .getDelegateQuery()
                        .getComplexSelectors() ) {

                        final FieldUntyped untypedField =
                            FieldReference.ofPath(field.getFieldName(), subQuery.getEntityOrPropertyName());
                        delegateQuery
                            .select(
                                StructuredQuery
                                    .asNestedQueryOnProperty(untypedField.getFieldName(), ODataProtocol.V4)
                                    .select(subQuery.getSimpleSelectors().toArray(new String[0]))
                                    .select(subQuery.getComplexSelectors().toArray(new StructuredQuery[0])));
                    }
                } else if( field instanceof AbstractStructuredPropertyQuery ) {
                    delegateQuery.select(((AbstractStructuredPropertyQuery<?, ?>) field).getDelegateQuery());
                } else {
                    delegateQuery
                        .select(StructuredQuery.asNestedQueryOnProperty(field.getFieldName(), ODataProtocol.V4));
                }

                continue;
            }
            throw new ShouldNotHappenException();
        }
        return this;
    }

    @Nonnull
    @Override
    public String getFieldName()
    {
        return delegateQuery.getEntityOrPropertyName();
    }

    @Nonnull
    String getEncodedQueryString()
    {
        return delegateQuery.getEncodedQueryString();
    }

    @Nonnull
    String getQueryString()
    {
        return delegateQuery.getQueryString();
    }
}
