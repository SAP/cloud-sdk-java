/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import javax.annotation.Nonnull;

import org.apache.http.client.HttpClient;

import com.sap.cloud.sdk.cloudplatform.connectivity.CsrfTokenRetriever;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultCsrfTokenRetriever;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataSerializationException;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FieldReference;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;
import com.sap.cloud.sdk.datamodel.odata.client.request.ETagSubmissionStrategy;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestUpdate;
import com.sap.cloud.sdk.datamodel.odata.client.request.UpdateStrategy;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Representation of an OData request as a fluent interface for further configuring the request and
 * {@link #execute(Destination) executing} it.
 *
 * @param <EntityT>
 *            The type of the result entity.
 */
@Slf4j
public class UpdateRequestBuilder<EntityT extends VdmEntity<?>>
    extends
    AbstractEntityBasedRequestBuilder<UpdateRequestBuilder<EntityT>, EntityT, ModificationResponse<EntityT>>
    implements
    ModificationRequestBuilder<ModificationResponse<EntityT>>
{
    private final Collection<FieldReference> includedFields = new HashSet<>();

    private final Collection<FieldReference> excludedFields = new HashSet<>();

    private UpdateStrategy updateStrategy = UpdateStrategy.MODIFY_WITH_PATCH;

    private ETagSubmissionStrategy eTagSubmissionStrategy = ETagSubmissionStrategy.SUBMIT_ETAG_FROM_ENTITY;

    /**
     * The entity object to be updated by calling the {@link #execute(Destination)} method.
     */
    @Getter( AccessLevel.PROTECTED )
    @Nonnull
    private final EntityT entity;

    /**
     * Instantiate an {@code UpdateRequestBuilder}.
     *
     * @param servicePath
     *            The service path.
     * @param entity
     *            The entity to update.
     * @param entityCollection
     *            The entity collection
     */
    public UpdateRequestBuilder(
        @Nonnull final String servicePath,
        @Nonnull final EntityT entity,
        @Nonnull final String entityCollection )
    {
        this(servicePath, ODataResourcePath.of(entityCollection, entity.getKey()), entity);
    }

    /**
     * Instantiate an {@code UpdateRequestBuilder}.
     *
     * @param servicePath
     *            The service path.
     * @param entityPath
     *            The {@link ODataResourcePath} that identifies the entity to update.
     * @param entity
     *            The entity to update.
     */
    UpdateRequestBuilder(
        @Nonnull final String servicePath,
        @Nonnull final ODataResourcePath entityPath,
        @Nonnull final EntityT entity )
    {
        super(servicePath, entityPath);
        this.entity = entity;
        this.csrfTokenRetriever = new DefaultCsrfTokenRetriever();
    }

    @SuppressWarnings( "unchecked" )
    @Nonnull
    @Override
    protected Class<EntityT> getEntityClass()
    {
        return (Class<EntityT>) getEntity().getClass();
    }

    /**
     * Execute the OData update request for the provided entity.
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
     * Creates an instance of the {@link ODataRequestUpdate} based on the Entity class.
     * <p>
     * The following settings are used to build the Request Builder:
     * <ul>
     * <li>the endpoint URL</li>
     * <li>the entity collection name</li>
     * <li>the key fields of the entity</li>
     * <li>the entity JSON payload</li>
     * <li>the update strategy (full update or delta)</li>
     * </ul>
     *
     * @return An initialized {@code ODataRequestUpdate}.
     * @throws ODataSerializationException
     *             If entity cannot be serialized for HTTP request.
     */
    @Override
    @Nonnull
    public ODataRequestUpdate toRequest()
    {
        final String versionIdentifier =
            eTagSubmissionStrategy.getHeaderFromVersionIdentifier(entity.getVersionIdentifier());
        final String serializedEntity = getSerializedEntity();

        final ODataRequestUpdate request =
            new ODataRequestUpdate(
                getServicePath(),
                getResourcePath(),
                serializedEntity,
                updateStrategy,
                versionIdentifier,
                ODataProtocol.V4);

        return super.toRequest(request);
    }

    /**
     * Serialize entity to String depending on update strategy.
     *
     * @return The serialized String representing the entity.
     * @throws ODataSerializationException
     *             If entity cannot be serialized for HTTP request.
     */
    @Nonnull
    private String getSerializedEntity()
    {
        final EntityT entity = getEntity();
        try {
            switch( updateStrategy ) {
                case REPLACE_WITH_PUT:
                    return new UpdateRequestHelperPut().toJson(entity, excludedFields);
                case MODIFY_WITH_PATCH:
                    return new UpdateRequestHelperPatch().toJson(entity, includedFields);
                default:
                    throw new IllegalStateException("Unexpected update Strategy: " + updateStrategy);
            }
        }
        catch( final Exception e ) {
            final String msg =
                String
                    .format(
                        "Failed to serialize OData Update HTTP request entity for type %s with strategy %s",
                        getEntityClass().getSimpleName(),
                        updateStrategy);

            final ODataRequestUpdate request =
                new ODataRequestUpdate(
                    getServicePath(),
                    getResourcePath(),
                    "",
                    updateStrategy,
                    eTagSubmissionStrategy.getHeaderFromVersionIdentifier(entity.getVersionIdentifier()),
                    ODataProtocol.V4);

            throw new ODataSerializationException(request, entity, msg, e);
        }
    }

    /**
     * Allows to explicitly specify entity fields that shall be sent in an update request regardless if the values of
     * these fields have been changed. This is helpful in case the API requires to send certain fields in any case in an
     * update request.
     *
     * @param fields
     *            The fields to be included in the update execution.
     * @return The same request builder which will include the specified fields in an update request.
     */
    @Nonnull
    public final UpdateRequestBuilder<EntityT> includingFields( @Nonnull final FieldReference... fields )
    {
        includedFields.addAll(Arrays.asList(fields));
        return this;
    }

    /**
     * Allows to explicitly specify entity fields that should not be sent in an update request. This is helpful in case
     * some services require no read only fields to be sent for update requests. These fields are only excluded in a PUT
     * request, they are not considered in a PATCH request.
     *
     * @param fields
     *            The fields to be excluded in the update execution.
     * @return The same request builder which will exclude the specified fields in an update request.
     */
    @Nonnull
    public final UpdateRequestBuilder<EntityT> excludingFields( @Nonnull final FieldReference... fields )
    {
        Collections.addAll(excludedFields, fields);
        return this;
    }

    /**
     * Allows to control that the request to update the entity is sent with the HTTP method PUT and its payload contains
     * all fields of the entity, regardless which of them have been changed.
     *
     * @return The same request builder which will replace the entity in the remote system
     */
    @Nonnull
    public final UpdateRequestBuilder<EntityT> replacingEntity()
    {
        updateStrategy = UpdateStrategy.REPLACE_WITH_PUT;
        return this;
    }

    /**
     * Allows to control that the request to update the entity is sent with the HTTP method PATCH and its payload
     * contains the changed fields only.
     *
     * @return The same request builder which will modify the entity in the remote system.
     */
    @Nonnull
    public final UpdateRequestBuilder<EntityT> modifyingEntity()
    {
        updateStrategy = UpdateStrategy.MODIFY_WITH_PATCH;
        return this;
    }

    /**
     * The update request will ignore any version identifier present on the entity and not send an `If-Match` header.
     * <p>
     * <b>Warning:</b> This might lead to a response from the remote system that the `If-Match` header is missing.
     * <p>
     * It depends on the implementation of the remote system whether the `If-Match` header is expected.
     *
     * @return The same request builder that will not send the `If-Match` header in the update request
     */
    @Nonnull
    public UpdateRequestBuilder<EntityT> disableVersionIdentifier()
    {
        eTagSubmissionStrategy = ETagSubmissionStrategy.SUBMIT_NO_ETAG;
        return this;
    }

    /**
     * The update request will ignore any version identifier present on the entity and update the entity, regardless of
     * any changes on the remote entity.
     * <p>
     * <b>Warning:</b> Be careful with this option, as this might overwrite any changes made to the remote
     * representation of this object.
     *
     * @return The same request builder that will ignore the version identifier of the entity to update
     */
    @Nonnull
    public UpdateRequestBuilder<EntityT> matchAnyVersionIdentifier()
    {
        eTagSubmissionStrategy = ETagSubmissionStrategy.SUBMIT_ANY_MATCH_ETAG;
        return this;
    }

    @Override
    @Nonnull
    public UpdateRequestBuilder<EntityT> withoutCsrfToken()
    {
        this.csrfTokenRetriever = CsrfTokenRetriever.DISABLED_CSRF_TOKEN_RETRIEVER;
        return this;
    }
}
