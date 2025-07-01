package com.sap.cloud.sdk.datamodel.odatav4.core;

import javax.annotation.Nonnull;

import org.apache.http.client.HttpClient;

import com.google.gson.Gson;
import com.sap.cloud.sdk.cloudplatform.connectivity.CsrfTokenRetriever;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultCsrfTokenRetriever;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataSerializationException;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestCreate;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestGeneric;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;

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
public class CreateRequestBuilder<EntityT extends VdmEntity<?>>
    extends
    AbstractEntityBasedRequestBuilder<CreateRequestBuilder<EntityT>, EntityT, ModificationResponse<EntityT>>
    implements
    ModificationRequestBuilder<ModificationResponse<EntityT>>
{
    /**
     * Getter for the VDM representation of the entity to be created.
     */
    @Nonnull
    @Getter( AccessLevel.PROTECTED )
    private final EntityT entity;

    /**
     * Instantiates a {@code CreateRequestBuilder}.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     * @param entity
     *            The entity to create.
     * @param entityCollection
     *            The entity collection
     */
    public CreateRequestBuilder(
        @Nonnull final String servicePath,
        @Nonnull final EntityT entity,
        @Nonnull final String entityCollection )
    {
        this(servicePath, ODataResourcePath.of(entityCollection), entity);
    }

    /**
     * Instantiates a {@code CreateRequestBuilder}.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     * @param entityPath
     *            {@link ODataResourcePath} identifying the collection the entity should be created into.
     * @param entity
     *            The entity to create.
     */
    CreateRequestBuilder(
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
     * Execute the OData create request for the provided entity.
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
        response.getHttpResponse(); // ensure HTTP response consumption

        return ModificationResponse.of(response, getEntity());
    }

    /**
     * Creates an instance of the {@link ODataRequestCreate} based on the Entity class.
     * <p>
     * The following settings are used:
     * <ul>
     * <li>the endpoint URL</li>
     * <li>the entity collection name</li>
     * <li>the entity JSON payload</li>
     * </ul>
     *
     * @return An initialized {@link ODataRequestCreate}.
     * @throws ODataSerializationException
     *             If entity cannot be serialized for HTTP request.
     */
    @Override
    @Nonnull
    public ODataRequestCreate toRequest()
    {
        try {
            final String serializedEntity = new Gson().toJson(entity);

            final ODataRequestCreate request =
                new ODataRequestCreate(getServicePath(), getResourcePath(), serializedEntity, ODataProtocol.V4);

            return super.toRequest(request);
        }
        catch( final Exception e ) {
            final String msg = "Failed to serialize HTTP request entity of type " + getEntityClass().getSimpleName();
            log.debug(msg, e);
            final ODataRequestGeneric request =
                new ODataRequestCreate(getServicePath(), getResourcePath(), "", ODataProtocol.V4);
            throw new ODataSerializationException(request, entity, msg, e);
        }
    }

    /**
     * Deactivates the CSRF token retrieval for this OData request. This is useful if the server does not support or
     * require CSRF tokens as part of the request.
     *
     * @return The same builder
     */
    @Nonnull
    @Override
    public CreateRequestBuilder<EntityT> withoutCsrfToken()
    {
        this.csrfTokenRetriever = CsrfTokenRetriever.DISABLED_CSRF_TOKEN_RETRIEVER;
        return this;
    }
}
