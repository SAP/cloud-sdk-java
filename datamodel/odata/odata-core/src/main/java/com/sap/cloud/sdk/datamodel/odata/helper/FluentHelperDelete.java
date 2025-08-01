package com.sap.cloud.sdk.datamodel.odata.helper;

import javax.annotation.Nonnull;

import org.apache.http.client.HttpClient;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.request.ETagSubmissionStrategy;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataEntityKey;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestDelete;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;

import io.vavr.control.Option;

/**
 * Representation of an OData delete request as a fluent interface for further configuring the request and
 * {@link #executeRequest(Destination) executing} it.
 *
 * @param <FluentHelperT>
 *            The fluent helper type.
 * @param <EntityT>
 *            The type of the entity to delete.
 */
public abstract class FluentHelperDelete<FluentHelperT, EntityT extends VdmEntity<?>>
    extends
    FluentHelperModification<FluentHelperT, EntityT>
{
    private ETagSubmissionStrategy eTagSubmissionStrategy = ETagSubmissionStrategy.SUBMIT_ETAG_FROM_ENTITY;

    /**
     * Instantiates this fluent helper using the given service path and entity collection to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     * @param entityCollection
     *            The entity collection to direct the requests to.
     */
    public FluentHelperDelete( @Nonnull final String servicePath, @Nonnull final String entityCollection )
    {
        super(servicePath, entityCollection);
    }

    /**
     * The entity object to be deleted by calling the {@link #executeRequest(Destination)} method.
     *
     * @return The entity to be deleted.
     */
    @Nonnull
    protected abstract EntityT getEntity();

    @SuppressWarnings( "unchecked" )
    @Override
    @Nonnull
    protected Class<? extends EntityT> getEntityClass()
    {
        return (Class<? extends EntityT>) getEntity().getClass();
    }

    /**
     * The delete request will ignore any version identifier present on the entity and not send an `If-Match` header.
     * <p>
     * <b>Warning:</b> This might lead to a response from the remote system that the `If-Match` header is missing.
     * <p>
     * It depends on the implementation of the remote system whether the `If-Match` header is expected.
     *
     * @return The same request builder that will not send the `If-Match` header in the update request
     */
    @Nonnull
    public FluentHelperT disableVersionIdentifier()
    {
        eTagSubmissionStrategy = ETagSubmissionStrategy.SUBMIT_NO_ETAG;
        return getThis();
    }

    /**
     * The delete request will ignore any version identifier present on the entity and delete the entity, regardless of
     * any changes on the remote entity.
     * <p>
     * <b>Warning:</b> Be careful with this option, as this might overwrite any changes made to the remote
     * representation of this object.
     *
     * @return The same request builder that will ignore the version identifier of the entity to update
     */
    @Nonnull
    public FluentHelperT matchAnyVersionIdentifier()
    {
        eTagSubmissionStrategy = ETagSubmissionStrategy.SUBMIT_ANY_MATCH_ETAG;
        return getThis();
    }

    @Override
    @Nonnull
    public ModificationResponse<EntityT> executeRequest( @Nonnull final Destination destination )
    {
        final HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);

        final ODataRequestResultGeneric result = toRequest().execute(httpClient);

        return ModificationResponse.of(result, getEntity(), destination);
    }

    @Override
    @Nonnull
    public ODataRequestDelete toRequest()
    {
        final EntityT entity = getEntity();
        final String versionIdentifier =
            eTagSubmissionStrategy.getHeaderFromVersionIdentifier(entity.getVersionIdentifier());

        final ODataRequestDelete request =
            new ODataRequestDelete(
                getServicePath(),
                getEntityCollection(),
                ODataEntityKey.of(entity.getKey(), ODataProtocol.V2),
                versionIdentifier,
                ODataProtocol.V2);

        return super.addHeadersAndCustomParameters(request);
    }

    @Nonnull
    private String getEntityCollection()
    {
        return Option.of(entityCollection).getOrElse(() -> getEntity().getEntityCollection());
    }
}
