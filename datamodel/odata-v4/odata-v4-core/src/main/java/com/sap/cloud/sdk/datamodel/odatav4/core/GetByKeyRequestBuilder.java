/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.http.client.HttpClient;

import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultCsrfTokenRetriever;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataEntityKey;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestReadByKey;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Representation of an OData request to retrieve an entity by its key as a fluent interface for further configuring the
 * request and {@link #execute(Destination) executing} it.
 *
 * @param <EntityT>
 *            The type of the result entity.
 */
@Slf4j
public class GetByKeyRequestBuilder<EntityT extends VdmEntity<?>>
    extends
    AbstractEntityBasedRequestBuilder<GetByKeyRequestBuilder<EntityT>, EntityT, EntityT>
    implements
    ProtocolQueryRead<EntityT>,
    ReadRequestBuilder<EntityT>
{
    private final NavigationPropertySingleQuery<EntityT, EntityT> delegateQuery;

    @Getter( AccessLevel.PROTECTED )
    @Nonnull
    private final Class<EntityT> entityClass;

    /**
     * Instantiates this request builder using the given service path to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     * @param entityClass
     *            The expected entity type.
     * @param entityKey
     *            The composite entity key.
     * @param entityCollection
     *            The entity collection
     * @throws IllegalArgumentException
     *             When there is no mapping found for one of the provided Java literal in the composite key.
     */
    public GetByKeyRequestBuilder(
        @Nonnull final String servicePath,
        @Nonnull final Class<EntityT> entityClass,
        @Nonnull final Map<String, Object> entityKey,
        @Nonnull final String entityCollection )
    {
        this(servicePath, entityClass, ODataEntityKey.of(entityKey, ODataProtocol.V4), entityCollection);
    }

    /**
     * Instantiates a request builder using the given service path to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     * @param entityClass
     *            The expected entity type.
     * @param entityKey
     *            The composite entity key.
     * @param entityCollection
     *            The entity collection
     */
    GetByKeyRequestBuilder(
        @Nonnull final String servicePath,
        @Nonnull final Class<EntityT> entityClass,
        @Nonnull final ODataEntityKey entityKey,
        @Nonnull final String entityCollection )
    {
        this(servicePath, ODataResourcePath.of(entityCollection, entityKey), entityClass);
    }

    /**
     * Instantiates a request builder using the given service path to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     * @param entityClass
     *            The expected entity type.
     * @param entityPath
     *            The {@link ODataResourcePath} that identifies the entity to read.
     */
    @SuppressWarnings( "this-escape" )
    GetByKeyRequestBuilder(
        @Nonnull final String servicePath,
        @Nonnull final ODataResourcePath entityPath,
        @Nonnull final Class<EntityT> entityClass )
    {
        super(servicePath, entityPath);
        this.entityClass = entityClass;
        this.delegateQuery = NavigationPropertySingleQuery.ofRootQuery(getResourcePath().toString());
    }

    /**
     * Creates an instance of {@link ODataRequestReadByKey} based on the Entity class.
     * <p>
     * The following settings are used:
     * <ul>
     * <li>the endpoint URL</li>
     * <li>the entity collection name</li>
     * <li>the key fields of the entity</li>
     * <li>the fields to be selected</li>
     * </ul>
     *
     * @return An initialized {@link ODataRequestReadByKey}.
     */
    @Override
    @Nonnull
    public ODataRequestReadByKey toRequest()
    {
        final ODataRequestReadByKey request =
            new ODataRequestReadByKey(
                getServicePath(),
                getResourcePath(),
                delegateQuery.getEncodedQueryString(),
                ODataProtocol.V4);

        return super.toRequest(request);
    }

    @Override
    @Nonnull
    public EntityT execute( @Nonnull final Destination destination )
    {
        final HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);
        final ODataRequestResultGeneric response = toRequest().execute(httpClient);
        final EntityT entity = response.as(getEntityClass());

        response.getVersionIdentifierFromHeader().peek(entity::setVersionIdentifier);

        return entity;
    }

    @Override
    @SafeVarargs
    @Nonnull
    @SuppressWarnings( "varargs" )
    public final GetByKeyRequestBuilder<EntityT> select( @Nonnull final Property<EntityT>... fields )
    {
        delegateQuery.select(fields);
        return this;
    }

    @Nonnull
    @Override
    public GetByKeyRequestBuilder<EntityT> withCsrfToken()
    {
        this.csrfTokenRetriever = new DefaultCsrfTokenRetriever();
        return this;
    }
}
