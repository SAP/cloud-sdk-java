/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import javax.annotation.Nonnull;

import org.apache.http.client.HttpClient;

import com.sap.cloud.sdk.cloudplatform.connectivity.CsrfTokenRetriever;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultCsrfTokenRetriever;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;
import com.sap.cloud.sdk.datamodel.odata.client.request.ETagSubmissionStrategy;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestDelete;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Representation of an OData delete request as a fluent interface for further configuring the request and
 * {@link #execute(Destination) executing} it.
 *
 * @param <EntityT>
 *            The type of the entity to delete.
 */
@Slf4j
public class DeleteRequestBuilder<EntityT extends VdmEntity<?>>
    extends
    AbstractEntityBasedRequestBuilder<DeleteRequestBuilder<EntityT>, EntityT, ModificationResponse<EntityT>>
    implements
    ModificationRequestBuilder<ModificationResponse<EntityT>>
{
    /**
     * The entity object to be deleted by calling the {@link #execute(Destination)} method.
     */
    @Nonnull
    @Getter( AccessLevel.PROTECTED )
    private final EntityT entity;

    private ETagSubmissionStrategy eTagSubmissionStrategy = ETagSubmissionStrategy.SUBMIT_ETAG_FROM_ENTITY;

    /**
     * Instantiates this request builder using the given service path to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     * @param entity
     *            The entity to delete.
     * @param entityCollection
     *            The entity collection
     */
    public DeleteRequestBuilder(
        @Nonnull final String servicePath,
        @Nonnull final EntityT entity,
        @Nonnull final String entityCollection )
    {
        this(servicePath, ODataResourcePath.of(entityCollection, entity.getKey()), entity);
    }

    /**
     * Instantiates this request builder using the given service path to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     * @param entityPath
     *            The {@link ODataResourcePath} that identifies the entity to delete.
     * @param entity
     *            The entity to delete.
     */
    DeleteRequestBuilder(
        @Nonnull final String servicePath,
        @Nonnull final ODataResourcePath entityPath,
        @Nonnull final EntityT entity )
    {
        super(servicePath, entityPath);
        this.entity = entity;
        this.csrfTokenRetriever = new DefaultCsrfTokenRetriever();
    }

    /**
     * Execute the OData delete request for the provided entity.
     *
     * {@inheritDoc}
     *
     * @return The wrapped service response, exposing response headers, status code and entity references. If the HTTP
     *         response is not within healthy bounds, then one of the declared runtime exceptions will be thrown with
     *         further details.
     */
    @Nonnull
    @Override
    public ModificationResponse<EntityT> execute( @Nonnull final Destination destination )
    {
        final HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);
        final ODataRequestResultGeneric response = toRequest().execute(httpClient);

        return ModificationResponse.of(response, getEntity());
    }

    /**
     * Creates an instance of the {@link ODataRequestDelete} based on the Entity class.
     * <p>
     * The following settings are used:
     * <ul>
     * <li>the endpoint URL</li>
     * <li>the entity collection name</li>
     * <li>the key fields of the entity</li>
     * </ul>
     *
     * @return An initialized {@link ODataRequestDelete}.
     */
    @Override
    @Nonnull
    public ODataRequestDelete toRequest()
    {
        final String versionIdentifier =
            eTagSubmissionStrategy.getHeaderFromVersionIdentifier(entity.getVersionIdentifier());

        final ODataRequestDelete request =
            new ODataRequestDelete(getServicePath(), getResourcePath(), versionIdentifier, ODataProtocol.V4);

        return super.toRequest(request);
    }

    /**
     * The delete request will ignore any version identifier present on the entity and not send an `If-Match` header.
     * <p>
     * <b>Warning:</b> This might lead to a response from the remote system that the `If-Match` header is missing.
     * <p>
     * It depends on the implementation of the remote system whether the `If-Match` header is expected.
     *
     * @return The same request builder that will not send the `If-Match` header in the delete request
     */
    @Nonnull
    public DeleteRequestBuilder<EntityT> disableVersionIdentifier()
    {
        eTagSubmissionStrategy = ETagSubmissionStrategy.SUBMIT_NO_ETAG;
        return this;
    }

    /**
     * The delete request will ignore any version identifier present on the entity and delete the entity, regardless of
     * any changes on the remote entity.
     * <p>
     * <b>Warning:</b> Be careful with this option, as this might overwrite any changes made to the remote
     * representation of this object.
     *
     * @return The same request builder that will ignore the version identifier of the entity to delete
     */
    @Nonnull
    public DeleteRequestBuilder<EntityT> matchAnyVersionIdentifier()
    {
        eTagSubmissionStrategy = ETagSubmissionStrategy.SUBMIT_ANY_MATCH_ETAG;
        return this;
    }

    @SuppressWarnings( "unchecked" )
    @Nonnull
    @Override
    protected Class<EntityT> getEntityClass()
    {
        return (Class<EntityT>) entity.getClass();
    }

    @Nonnull
    @Override
    public DeleteRequestBuilder<EntityT> withoutCsrfToken()
    {
        this.csrfTokenRetriever = CsrfTokenRetriever.DISABLED_CSRF_TOKEN_RETRIEVER;
        return this;
    }
}
