/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.client.HttpClient;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultCsrfTokenRetriever;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.query.StructuredQuery;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestCount;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestRead;

import lombok.extern.slf4j.Slf4j;

/**
 * Representation of an OData query as a fluent interface for further configuring the request and
 * {@link #executeRequest(Destination) executing} it.
 *
 * @param <FluentHelperT>
 *            The fluent helper type.
 * @param <EntityT>
 *            The type of the result entity.
 * @param <SelectableT>
 *            The type of the {@link EntityField class that represents fields of the entity}.
 */
@Slf4j
public abstract class FluentHelperRead<FluentHelperT, EntityT extends VdmEntity<?>, SelectableT>
    extends
    FluentHelperBasic<FluentHelperT, EntityT, List<EntityT>>
{

    private final StructuredQuery delegateQuery;

    /**
     * Instantiates this fluent helper using the given service path and entity collection to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     *
     * @param entityCollection
     *            The entity collection to direct the requests to.
     */
    public FluentHelperRead( @Nonnull final String servicePath, @Nonnull final String entityCollection )
    {
        super(servicePath, entityCollection);

        delegateQuery = StructuredQuery.onEntity(entityCollection, ODataProtocol.V2);
    }

    @Override
    @Nonnull
    public ODataRequestRead toRequest()
    {
        final String queryString = delegateQuery.getEncodedQueryString();
        final ODataRequestRead request =
            new ODataRequestRead(getServicePath(), entityCollection, queryString, ODataProtocol.V2);
        return super.addHeadersAndCustomParameters(request);
    }

    @Override
    @Nonnull
    public FluentHelperT withQueryParameter( @Nonnull final String key, @Nullable final String value )
    {
        return super.withQueryParameter(key, value);
    }

    /**
     * Query modifier to limit which field values of the entity get fetched and populated, and to specify which
     * navigation properties to expand. If this method is never called, then all fields will be fetched and populated,
     * and no navigation properties expanded. But if this method is called at least once, then only the specified fields
     * will be fetched and populated. Calling this multiple times will combine the set(s) of fields and expansions of
     * each call.
     *
     * @param fields
     *            Fields to select and/or navigation properties to expand.
     * @return The same fluent helper with this query modifier applied.
     */
    @SuppressWarnings( "unchecked" )
    @Nonnull
    public FluentHelperT select( @Nonnull final SelectableT... fields )
    {
        final Iterable<EntitySelectable<?>> selectableFields = (Iterable<EntitySelectable<?>>) Arrays.asList(fields);
        return super.select(selectableFields, delegateQuery::select, delegateQuery::select);
    }

    /**
     * Query modifier to sort the set of returned entities by one or more fields. If this method is called more than
     * once, then the result entity set will be sorted by each field in the order that the methods were called.
     *
     * @param field
     *            Field to sort by.
     * @param order
     *            Sorting direction to use (ascending or descending).
     *
     * @return The same fluent helper with this query modifier applied.
     */
    @Nonnull
    public FluentHelperT orderBy( @Nonnull final EntityField<EntityT, ?> field, @Nonnull final Order order )
    {
        final com.sap.cloud.sdk.datamodel.odata.client.query.Order clientTypeOrder =
            com.sap.cloud.sdk.datamodel.odata.client.query.Order.valueOf(order.toString());
        delegateQuery.orderBy(field.getFieldName(), clientTypeOrder);

        return getThis();
    }

    /**
     * Query modifier to limit the number of entities returned. If this method is never called, then the result entity
     * list will not be limited in size. If this method is called multiple times, then only the value of the last call
     * will be used.
     *
     * @param top
     *            Number of entities to limit the result set to.
     * @return The same fluent helper with this query modifier applied.
     */
    @Nonnull
    public FluentHelperT top( @Nonnull final Number top )
    {
        delegateQuery.top(top);
        return getThis();
    }

    /**
     * Query modifier to not return the first N entities of the result set. If this method is never called, then the
     * full list will be returned from the first entity. If this method is called multiple times, then only the value of
     * the last call will be used.
     *
     * @param skip
     *            Number of entities to skip by.
     * @return The same fluent helper with this query modifier applied.
     */
    @Nonnull
    public FluentHelperT skip( @Nonnull final Number skip )
    {
        delegateQuery.skip(skip);
        return getThis();
    }

    @Override
    @Nonnull
    public List<EntityT> executeRequest( @Nonnull final Destination destination )
        throws com.sap.cloud.sdk.datamodel.odata.client.exception.ODataException
    {
        final Iterable<List<EntityT>> iterablePages = iteratingPages().executeRequest(destination);
        final Iterable<EntityT> iterableItems = Iterables.concat(Objects.requireNonNull(iterablePages));
        return Lists.newArrayList(iterableItems);
    }

    /**
     * Manually explore the individual pages from the lazy-loading entity result-set. The returning object allows for
     * performant consumption of all data through server-driven pagination.
     *
     * @return An instance of {@link FluentHelperExecutable} that allows for lazy iteration through the result-set
     *         pages.
     */
    @Nonnull
    public FluentHelperExecutable<Iterable<List<EntityT>>> iteratingPages()
    {
        return this::executeInternal;
    }

    /**
     * Manually explore the individual entities from the lazy-loading entity result-set. The returning iterable allows
     * for performant consumption of all data through server-driven pagination.
     *
     * @return An instance of {@link FluentHelperExecutable} that allows for lazy iteration through the result-set
     *         pages.
     */
    @Nonnull
    public FluentHelperExecutable<Iterable<EntityT>> iteratingEntities()
    {
        // concat applies lazy evaluation so individual pages will still be loaded lazily
        return destination -> Iterables.concat(executeInternal(destination));
    }

    /**
     * Manually explore the individual entities from the lazy-loading entity result-set. The returning Stream allows for
     * performant consumption of all data through server-driven pagination.
     *
     * @return An instance of {@link FluentHelperExecutable} that allows for lazy iteration through the result-set
     *         pages.
     */
    @Nonnull
    public FluentHelperExecutable<Stream<EntityT>> streamingEntities()
    {
        // concat applies lazy evaluation so individual pages will still be loaded lazily
        return destination -> Streams.stream(Iterables.concat(executeInternal(destination)));
    }

    @Nonnull
    private Iterable<List<EntityT>> executeInternal( @Nonnull final Destination destination )
        throws com.sap.cloud.sdk.datamodel.odata.client.exception.ODataException
    {
        final HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);
        final Iterable<List<EntityT>> result = toRequest().execute(httpClient).iteratePages(getEntityClass());

        // Refine lazy iterable to attach destination properties to individual entities in the page lists.
        //noinspection StaticPseudoFunctionalStyleMethod,ConstantConditions
        return Iterables
            .transform(
                result,
                list -> list
                    .stream()
                    .peek(entity -> entity.attachToService(getServicePath(), destination)) // enable lazy loading for navigation properties on entity
                    .collect(Collectors.toList()));
    }

    /**
     * Query modifier to restrict the set of returned entities based on the values of one or more fields. If this method
     * is never called, then all accessible entities will be fetched. Calling this multiple times will combine the
     * filters into one that is the intersection of all filters (filter1 AND filter2 AND ... filterN).
     *
     * @param expression
     *            Fluent helper that represents a field value expression. To create this, start with an
     *            {@link com.sap.cloud.sdk.datamodel.odata.helper.EntityField EntityField} constant in your respective
     *            entity type for the desired entity field. Then call one of the comparison operators and provide a
     *            comparison value. Optionally the resulting fluent helper can be chained with other expression fluent
     *            helpers.
     *
     * @return The same fluent helper with this query modifier applied.
     */
    @Nonnull
    public FluentHelperT filter( @Nonnull final ExpressionFluentHelper<EntityT> expression )
    {
        delegateQuery.filter(expression.getDelegateExpressionWithoutOuterParentheses());
        return getThis();
    }

    /**
     * Query modifier to return only the number of tuples that match the criteria specified in the query. The actual
     * tuples are not returned. May be used with all other query modifiers. Any call to top is overloaded with top(0).
     * Any call to skip is ignored.
     *
     * @return The number of tuples that match the criteria specified in the query.
     */
    @Nonnull
    public FluentHelperCount count()
    {
        final StructuredQuery prunedQuery = StructuredQuery.onEntity(entityCollection, ODataProtocol.V2);

        delegateQuery.getFilters().forEach(prunedQuery::filter);
        delegateQuery.getCustomParameters().forEach(prunedQuery::withCustomParameter);

        final ODataRequestCount requestCount =
            new ODataRequestCount(
                getServicePath(),
                entityCollection,
                prunedQuery.getEncodedQueryString(),
                ODataProtocol.V2);

        final ODataRequestCount requestCountUpdated = addHeadersAndCustomParameters(requestCount);

        return new FluentHelperCount(requestCountUpdated);
    }

    /**
     * Set the preferred page size of the OData response. A result-set may be split into multiple pages, each including
     * a subset of the entities matching the query.
     * <p>
     * <strong>Note:</strong> The OData service might ignore the preferred page size setting and may not use pagination
     * at all.
     *
     * @param size
     *            The preferred page size
     * @return This request object with the added parameter.
     */
    @Nonnull
    public FluentHelperT withPreferredPageSize( final int size )
    {
        return withHeader("Prefer", "odata.maxpagesize=" + size);
    }

    /**
     * Activates the CSRF token retrieval for this OData request. This is useful if the server does require CSRF tokens
     * as part of the request.
     *
     * @return The same builder
     */
    @Nonnull
    public FluentHelperT withCsrfToken()
    {
        this.csrfTokenRetriever = new DefaultCsrfTokenRetriever();
        return getThis();
    }
}
