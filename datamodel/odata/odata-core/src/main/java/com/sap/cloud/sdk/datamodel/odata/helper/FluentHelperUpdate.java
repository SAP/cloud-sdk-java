/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.http.client.HttpClient;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataSerializationException;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FieldReference;
import com.sap.cloud.sdk.datamodel.odata.client.request.ETagSubmissionStrategy;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataEntityKey;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestUpdate;
import com.sap.cloud.sdk.datamodel.odata.client.request.UpdateStrategy;

import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;

/**
 * Representation of an OData update request as a fluent interface for further configuring the request and
 * {@link #executeRequest(Destination) executing} it.
 *
 * @param <FluentHelperT>
 *            The fluent helper type.
 * @param <EntityT>
 *            The type of the entity to update.
 */
@Slf4j
public abstract class FluentHelperUpdate<FluentHelperT, EntityT extends VdmEntity<?>>
    extends
    FluentHelperModification<FluentHelperT, EntityT>
{
    private final Collection<EntitySelectable<EntityT>> includedFields = new HashSet<>();
    private final Collection<EntitySelectable<EntityT>> excludedFields = new HashSet<>();
    private UpdateStrategy updateStrategy = UpdateStrategy.MODIFY_WITH_PATCH;
    private ETagSubmissionStrategy eTagSubmissionStrategy = ETagSubmissionStrategy.SUBMIT_ETAG_FROM_ENTITY;

    /**
     * Instantiates this fluent helper using the given service path to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     * @param entityCollection
     *            The entity collection to direct the requests to.
     */
    public FluentHelperUpdate( @Nonnull final String servicePath, @Nonnull final String entityCollection )
    {
        super(servicePath, entityCollection);
    }

    /**
     * The entity object to be updated by calling the {@link #executeRequest(Destination)} method.
     *
     * @return The entity to be updated.
     */
    protected abstract EntityT getEntity();

    @SuppressWarnings( "unchecked" )
    @Nonnull
    @Override
    protected Class<? extends EntityT> getEntityClass()
    {
        return (Class<? extends EntityT>) getEntity().getClass();
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
    public FluentHelperT disableVersionIdentifier()
    {
        eTagSubmissionStrategy = ETagSubmissionStrategy.SUBMIT_NO_ETAG;
        return getThis();
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

        final ODataRequestResultGeneric response = toRequest().execute(httpClient);
        return ModificationResponse.of(response, getEntity(), destination);
    }

    @Override
    @Nonnull
    public ODataRequestUpdate toRequest()
    {
        final EntityT entity = getEntity();
        final String serializedEntity = getSerializedEntity();
        final String versionIdentifier =
            eTagSubmissionStrategy.getHeaderFromVersionIdentifier(entity.getVersionIdentifier());

        final ODataRequestUpdate request =
            new ODataRequestUpdate(
                getServicePath(),
                getEntityCollection(),
                ODataEntityKey.of(entity.getKey(), ODataProtocol.V2),
                serializedEntity,
                updateStrategy,
                versionIdentifier,
                ODataProtocol.V2);

        return super.addHeadersAndCustomParameters(request);
    }

    @Nonnull
    private String getSerializedEntity()
    {
        final EntityT entity = getEntity();
        try {
            switch( updateStrategy ) {
                case REPLACE_WITH_PUT:
                    final List<FieldReference> fieldsToExcludeUpdate =
                        excludedFields
                            .stream()
                            .map(EntitySelectable::getFieldName)
                            .map(FieldReference::of)
                            .collect(Collectors.toList());
                    return ODataEntitySerializer.serializeEntityForUpdatePut(entity, fieldsToExcludeUpdate);
                case MODIFY_WITH_PATCH:
                    final List<FieldReference> fieldsToIncludeInUpdate =
                        includedFields
                            .stream()
                            .map(EntitySelectable::getFieldName)
                            .map(FieldReference::of)
                            .collect(Collectors.toList());
                    return ODataEntitySerializer.serializeEntityForUpdatePatch(entity, fieldsToIncludeInUpdate);
                default:
                    throw new IllegalStateException("Unexpected update strategy:" + updateStrategy);
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
                    entity.getEntityCollection(),
                    ODataEntityKey.of(entity.getKey(), ODataProtocol.V2),
                    "",
                    updateStrategy,
                    eTagSubmissionStrategy.getHeaderFromVersionIdentifier(entity.getVersionIdentifier()),
                    ODataProtocol.V2);
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
     *
     * @return The same fluent helper which will include the specified fields in an update request.
     */
    @Nonnull
    @SafeVarargs
    @SuppressWarnings( "varargs" )
    public final FluentHelperT includingFields( @Nonnull final EntitySelectable<EntityT>... fields )
    {
        includedFields.addAll(Arrays.asList(fields));
        return getThis();
    }

    /**
     * Allows to explicitly specify entity fields that should not be sent in an update request. This is helpful in case,
     * some services require no read only fields to be sent for update requests. These fields are only excluded in a PUT
     * request, they are not considered in a PATCH request.
     *
     * @param fields
     *            The fields to be excluded in the update execution.
     *
     * @return The same fluent helper which will exclude the specified fields in an update request.
     */
    @Nonnull
    @SafeVarargs
    @SuppressWarnings( "varargs" )
    public final FluentHelperT excludingFields( @Nonnull final EntitySelectable<EntityT>... fields )
    {
        excludedFields.addAll(Arrays.asList(fields));
        return getThis();
    }

    @Nonnull
    private String getEntityCollection()
    {
        return Option.of(entityCollection).getOrElse(() -> getEntity().getEntityCollection());
    }

    /**
     * Allows to control that the request to update the entity is sent with the HTTP method PUT and its payload contains
     * all fields of the entity, regardless which of them have been changed.
     *
     * @return The same fluent helper which will replace the entity in the remote system
     */
    @Nonnull
    public final FluentHelperT replacingEntity()
    {
        updateStrategy = UpdateStrategy.REPLACE_WITH_PUT;
        return getThis();
    }

    /**
     * Allows to control that the request to update the entity is sent with the HTTP method PATCH and its payload
     * contains the changed fields only.
     *
     * @return The same fluent helper which will modify the entity in the remote system.
     */
    @Nonnull
    public final FluentHelperT modifyingEntity()
    {
        updateStrategy = UpdateStrategy.MODIFY_WITH_PATCH;
        return getThis();
    }
}
