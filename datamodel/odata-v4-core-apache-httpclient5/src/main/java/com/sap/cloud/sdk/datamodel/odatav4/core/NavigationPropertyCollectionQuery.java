package com.sap.cloud.sdk.datamodel.odatav4.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.OrderExpression;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueBoolean;
import com.sap.cloud.sdk.datamodel.odata.client.query.StructuredQuery;
import com.sap.cloud.sdk.datamodel.odatav4.expression.FieldOrdering;
import com.sap.cloud.sdk.datamodel.odatav4.expression.FilterableBoolean;

/**
 * Implementation that represents read queries and holds their state at runtime. It allows for nested queries in a
 * recursive manner. The implementation is the same for queries over single entities and collections of entities. In the
 * VDM the available functionality is limited by the interfaces.
 *
 * In order to support a fluent creation of nested queries both the entity and the parent entity type are stored via
 * generics. By implementing {@link ProtocolQueryReadCollection} the API doesn't differentiate between selections via
 * referencing navigational properties and selections via sub-queries.
 *
 * @param <ParentEntityT>
 *            The generic navigation property entity source type.
 * @param <EntityT>
 *            The generic navigation property entity target type.
 */
public final class NavigationPropertyCollectionQuery<ParentEntityT extends VdmObject<?>, EntityT extends VdmEntity<?>>
    extends
    AbstractStructuredPropertyQuery<ParentEntityT, EntityT>
    implements
    ProtocolQueryReadCollection<EntityT>
{
    private NavigationPropertyCollectionQuery( final StructuredQuery delegateQuery )
    {
        super(delegateQuery);
    }

    static <
        ParentEntityT extends VdmObject<?>, EntityT extends VdmEntity<?>>
        NavigationPropertyCollectionQuery<ParentEntityT, EntityT>
        ofRootQuery( @Nonnull final String fieldName )
    {
        return new NavigationPropertyCollectionQuery<>(StructuredQuery.onEntity(fieldName, ODataProtocol.V4));
    }

    static <
        ParentEntityT extends VdmObject<?>, EntityT extends VdmEntity<?>>
        NavigationPropertyCollectionQuery<ParentEntityT, EntityT>
        ofSubQuery( @Nonnull final String fieldName )
    {
        return new NavigationPropertyCollectionQuery<>(
            StructuredQuery.asNestedQueryOnProperty(fieldName, ODataProtocol.V4));
    }

    @Override
    @SafeVarargs
    @Nonnull
    @SuppressWarnings( "varargs" )
    public final NavigationPropertyCollectionQuery<ParentEntityT, EntityT> select(
        @Nonnull final Property<EntityT>... fields )
    {
        super.select(Arrays.asList(fields));
        return this;
    }

    @Override
    @SafeVarargs
    @Nonnull
    @SuppressWarnings( "varargs" )
    public final NavigationPropertyCollectionQuery<ParentEntityT, EntityT> filter(
        @Nonnull final FilterableBoolean<EntityT>... filters )
    {
        final Collection<ValueBoolean> untypedFilters = new ArrayList<>();
        for( final FilterableBoolean<EntityT> fb : filters ) {
            untypedFilters.add(fb::getExpression);
        }
        untypedFilters.forEach(delegateQuery::filter);
        return this;
    }

    @Override
    @Nonnull
    public NavigationPropertyCollectionQuery<ParentEntityT, EntityT> top( final int top )
    {
        delegateQuery.top(top);
        return this;
    }

    @Override
    @Nonnull
    public NavigationPropertyCollectionQuery<ParentEntityT, EntityT> skip( final int skip )
    {
        delegateQuery.skip(skip);
        return this;
    }

    @SafeVarargs
    @Override
    @Nonnull
    public final NavigationPropertyCollectionQuery<ParentEntityT, EntityT> orderBy(
        @Nonnull final FieldOrdering<EntityT>... ordering )
    {
        final OrderExpression expression = FieldOrdering.toOrderExpression(ordering);
        if( expression != null ) {
            delegateQuery.orderBy(expression);
        }
        return this;
    }

    @Nonnull
    @Override
    public NavigationPropertyCollectionQuery<ParentEntityT, EntityT> search( @Nonnull final String search )
    {
        delegateQuery.search(SearchExpression.getDoubleQuotedString(search));
        return this;
    }

    @Nonnull
    @Override
    public NavigationPropertyCollectionQuery<ParentEntityT, EntityT> search(
        @Nonnull final SearchExpression expression )
    {
        delegateQuery.search(expression.getTerm());
        return this;
    }
}
