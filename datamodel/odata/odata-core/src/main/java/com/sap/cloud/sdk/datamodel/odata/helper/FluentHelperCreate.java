/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.client.HttpClient;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataSerializationException;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataEntityKey;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestCreate;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * Representation of an OData create request as a fluent interface for further configuring the request and
 * {@link #executeRequest(Destination) executing} it.
 *
 * @param <FluentHelperT>
 *            The fluent helper type.
 * @param <EntityT>
 *            The type of the entity to create.
 */
@Slf4j
public abstract class FluentHelperCreate<FluentHelperT, EntityT extends VdmEntity<?>>
    extends
    FluentHelperModification<FluentHelperT, EntityT>
{
    private EntityLink<? extends EntityLink<?, ?, EntityT>, ?, EntityT> linkFromParentEntity;
    private VdmEntity<?> parentEntity;

    /**
     * Instantiates this fluent helper using the given service path and entity collection to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     *
     * @param entityCollection
     *            The entity collection to direct the requests to.
     */
    public FluentHelperCreate( @Nonnull final String servicePath, @Nonnull final String entityCollection )
    {
        super(servicePath, entityCollection);
    }

    /**
     * Getter for the VDM representation of the entity to be created.
     *
     * @return The entity that should be created by calling the {@link #executeRequest(Destination)} method.
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
    public ODataRequestCreate toRequest()
    {
        final EntityT entity = getEntity();
        final ODataResourcePath resourcePath;

        if( linkFromParentEntity != null && parentEntity != null ) {
            resourcePath =
                ODataResourcePath
                    .of(parentEntity.getEntityCollection(), ODataEntityKey.of(parentEntity.getKey(), ODataProtocol.V2));
            resourcePath.addSegment(linkFromParentEntity.getFieldName());
        } else {
            resourcePath = ODataResourcePath.of(getEntityCollection());
        }

        final String serializedEntity =
            Try
                .of(() -> ODataEntitySerializer.serializeEntityForCreate(entity))
                .getOrElseThrow(
                    e -> new ODataSerializationException(
                        new ODataRequestCreate(getServicePath(), resourcePath, "", ODataProtocol.V2),
                        entity,
                        "Failed to serialize HTTP request entity of type " + getEntityClass().getSimpleName(),
                        e));

        final ODataRequestCreate request =
            new ODataRequestCreate(getServicePath(), resourcePath, serializedEntity, ODataProtocol.V2);

        return super.addHeadersAndCustomParameters(request);
    }

    /**
     * This function allows to create a new entity via an existing parent entity. Parent means that the existing entity
     * has to be related to the entity to be created via a navigation property. Thus, the function requires the caller
     * to provide an {@code EntityLink<?, ParentEntityT, EntityT>} that represents such a navigation property.
     * {@code ParentEntityT} can represent any entity that is related to the entity to be created. {@code EntityT}
     * represents the type of the entity to be created. Furthermore, the function requires an instance of type
     * {@code ParentEntityT}. This instance must hold the key fields used to identify it.
     *
     * NOTE: While any EntityLink provided by the OData VDM satisfying these type constraints allows you to call this
     * function, the service may NOT allow this kind of create operation for the respective navigation property. Thus,
     * calling this function without knowledge of the underlying OData service can result in failing requests.
     *
     * @param entity
     *            An instance of the related entity that MUST hold values for the respective key fields.
     * @param entityLink
     *            An {@link EntityLink} representing a navigation property.
     *
     * @return The same fluent helper configured to use the provided navigation property for creation.
     * @param <ParentEntityT>
     *            The generic parent entity type in this navigation property relation.
     */
    @Nonnull
    public <ParentEntityT extends VdmEntity<?>> FluentHelperT asChildOf(
        @Nullable final ParentEntityT entity,
        @Nullable final EntityLink<? extends EntityLink<?, ParentEntityT, EntityT>, ParentEntityT, EntityT> entityLink )
    {
        linkFromParentEntity = entityLink;
        parentEntity = entity;

        return getThis();
    }

    @Nonnull
    private String getEntityCollection()
    {
        return Option.of(entityCollection).getOrElse(() -> getEntity().getEntityCollection());
    }
}
