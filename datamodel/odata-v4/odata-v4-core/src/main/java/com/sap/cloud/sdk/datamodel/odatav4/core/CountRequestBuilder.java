/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import javax.annotation.Nonnull;

import org.apache.http.client.HttpClient;

import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultCsrfTokenRetriever;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestCount;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;
import com.sap.cloud.sdk.datamodel.odatav4.expression.FilterableBoolean;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Representation of an OData request as a fluent interface for further configuring the request and
 * {@link #execute(Destination) executing} it.
 *
 * @param <EntityT>
 *            The generic entity type.
 */
@Slf4j
public class CountRequestBuilder<EntityT extends VdmEntity<?>>
    extends
    AbstractEntityBasedRequestBuilder<CountRequestBuilder<EntityT>, EntityT, Long>
    implements
    ProtocolQueryFilter<EntityT>
{
    private final NavigationPropertyCollectionQuery<EntityT, EntityT> delegateQuery;

    @Getter( AccessLevel.PROTECTED )
    private final Class<EntityT> entityClass;

    /**
     * Instantiates this request builder using the given service path to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     * @param entityClass
     *            The expected entity type.
     * @param entityCollection
     *            The entity collection
     */
    public CountRequestBuilder(
        @Nonnull final String servicePath,
        @Nonnull final Class<EntityT> entityClass,
        @Nonnull final String entityCollection )
    {
        this(servicePath, ODataResourcePath.of(entityCollection), entityClass);
    }

    /**
     * Instantiates this request builder using the given service path to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     * @param resourceToCount
     *            {@link ODataResourcePath} that identifies the collection to count.
     * @param entityClass
     *            The expected entity type.
     */
    CountRequestBuilder(
        @Nonnull final String servicePath,
        @Nonnull final ODataResourcePath resourceToCount,
        @Nonnull final Class<EntityT> entityClass )
    {
        super(servicePath, resourceToCount);
        this.entityClass = entityClass;
        this.delegateQuery = NavigationPropertyCollectionQuery.ofRootQuery(getResourcePath().toString());
    }

    /**
     * Creates an instance of the {@link ODataRequestCount} based on the Entity class.
     * <p>
     * The following settings are used:
     * <ul>
     * <li>the endpoint URL</li>
     * <li>the entity collection name</li>
     * <li>the filters to be applied</li>
     * </ul>
     *
     * @return An initialized {@link ODataRequestCount}.
     */
    @Override
    @Nonnull
    public ODataRequestCount toRequest()
    {
        final ODataRequestCount request =
            new ODataRequestCount(
                getServicePath(),
                getResourcePath(),
                delegateQuery.getEncodedQueryString(),
                ODataProtocol.V4);

        return super.toRequest(request);
    }

    @Override
    @Nonnull
    public Long execute( @Nonnull final Destination destination )
    {
        final HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);
        final ODataRequestResultGeneric response = toRequest().execute(httpClient);
        return response.as(Long.class);
    }

    @Override
    @SafeVarargs
    @Nonnull
    @SuppressWarnings( "varargs" )
    public final CountRequestBuilder<EntityT> filter( @Nonnull final FilterableBoolean<EntityT>... filters )
    {
        delegateQuery.filter(filters);
        return this;
    }

    @Override
    @Nonnull
    public CountRequestBuilder<EntityT> search( @Nonnull final String search )
    {
        delegateQuery.search(search);
        return this;
    }

    @Override
    @Nonnull
    public CountRequestBuilder<EntityT> search( @Nonnull final SearchExpression expression )
    {
        delegateQuery.search(expression);
        return this;
    }

    /**
     * Activates the CSRF token retrieval for this OData request. This is useful if the server does require CSRF tokens
     * as part of the request.
     *
     * @return The same builder
     */
    @Nonnull
    public CountRequestBuilder<EntityT> withCsrfToken()
    {
        this.csrfTokenRetriever = new DefaultCsrfTokenRetriever();
        return this;
    }
}
