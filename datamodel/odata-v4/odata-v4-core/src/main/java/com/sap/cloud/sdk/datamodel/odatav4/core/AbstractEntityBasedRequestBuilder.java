package com.sap.cloud.sdk.datamodel.odatav4.core;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;

import lombok.extern.slf4j.Slf4j;

/**
 * Representation of OData requests that operate on entities as a fluent interface for further configuring the request
 * and {@link #execute(com.sap.cloud.sdk.cloudplatform.connectivity.Destination) executing} it.
 *
 * @param <BuilderT>
 *            The specific request builder type.
 * @param <EntityT>
 *            The type of the entity this OData request operates on, if any.
 * @param <ResultT>
 *            The type of the result entity, if any.
 */
@Slf4j
abstract class AbstractEntityBasedRequestBuilder<BuilderT extends AbstractEntityBasedRequestBuilder<BuilderT, EntityT, ResultT>, EntityT extends VdmEntity<?>, ResultT>
    extends
    AbstractRequestBuilder<BuilderT, ResultT>
{
    /**
     * Returns a class object of the type this request builder works with.
     *
     * @return A class object of the handled type.
     */
    @Nonnull
    protected abstract Class<EntityT> getEntityClass();

    /**
     * Instantiates this request builder using the given service path to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     * @param entityPath
     *            The resource path pointing to the entity (collection) this request should operate on.
     */
    AbstractEntityBasedRequestBuilder( @Nonnull final String servicePath, @Nonnull final ODataResourcePath entityPath )
    {
        super(servicePath, entityPath);
    }

    @Nonnull
    static <EntityT extends VdmEntity<?>> String getEntityCollectionFromEntityClass(
        @Nonnull final Class<EntityT> entityClass )
    {
        return new VdmEntityUtil<>(entityClass).newInstance().getEntityCollection();
    }
}
