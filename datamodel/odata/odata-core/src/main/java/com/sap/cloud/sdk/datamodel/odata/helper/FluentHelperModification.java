package com.sap.cloud.sdk.datamodel.odata.helper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.connectivity.CsrfTokenRetriever;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultCsrfTokenRetriever;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestinationProperties;

/**
 * Representation of an OData modification request (Create, Update, Delete) as a fluent interface for further
 * configuring the request and {@link #executeRequest(HttpDestinationProperties) executing} it.
 *
 * @param <FluentHelperT>
 *            The fluent helper type.
 * @param <EntityT>
 *            The type of the entity this OData request operates on, if any.
 */
public abstract class FluentHelperModification<FluentHelperT, EntityT extends VdmEntity<?>>
    extends
    FluentHelperBasic<FluentHelperT, EntityT, ModificationResponse<EntityT>>
{
    /**
     * Instantiates this fluent helper using the given service path to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     * @param entityCollection
     *            The entity collection to direct the requests to.
     */
    public FluentHelperModification( @Nonnull final String servicePath, @Nullable final String entityCollection )
    {
        super(servicePath, entityCollection);
        csrfTokenRetriever = new DefaultCsrfTokenRetriever();
    }

    /**
     * Deactivates the CSRF token retrieval for this OData request. This is useful if the server does not support or
     * require CSRF tokens as part of the request.
     *
     * @return The same builder
     */
    @Nonnull
    public FluentHelperT withoutCsrfToken()
    {
        csrfTokenRetriever = CsrfTokenRetriever.DISABLED_CSRF_TOKEN_RETRIEVER;
        return getThis();
    }
}
