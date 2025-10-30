package com.sap.cloud.sdk.datamodel.odata.helper;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.client.HttpClient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Iterables;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataRequestException;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataEntityKey;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestRead;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a {@link VdmObject} which is an entity. Entities may have a version identifier.
 *
 * @param <EntityT>
 *            The specific entity data type.
 */
@EqualsAndHashCode( callSuper = true, doNotUseGetters = true )
public abstract class VdmEntity<EntityT> extends VdmObject<EntityT>
{
    @SerializedName( "versionIdentifier" )
    @JsonProperty( "versionIdentifier" )
    @Nullable
    private String versionIdentifier = null;

    /**
     * The service path only used for the fetch commands of this entity.
     * <p>
     * <b>Note:</b> Use with caution, as this can easily break the fetch call on this entity. See the interface of the
     * corresponding service for the default service path.
     */
    @Getter( AccessLevel.PROTECTED )
    @Setter( AccessLevel.PROTECTED )
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private transient String servicePathForFetch;

    /**
     * Convenience field for reusing the same destination with multiple queries (e.g. fetching associated entities).
     */
    @Getter( AccessLevel.PROTECTED )
    @Setter( AccessLevel.PROTECTED )
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private transient Destination destinationForFetch;

    /**
     * Getter for the version identifier of this entity.
     * <p>
     * This identifier can be used to compare this entity with a remote one. As not the whole entity has to be sent this
     * reduces the request overhead.
     * <p>
     * Actual use cases can be checking whether this entity is still current with regards to the remote entity, and
     * ensuring that a update/delete operation is done on the expected version of the remote entity.
     *
     * @return The optional version identifier.
     */
    @Nonnull
    public Option<String> getVersionIdentifier()
    {
        return Option.of(versionIdentifier);
    }

    /**
     * Setter for the version identifier of this entity.
     * <p>
     * This identifier can be used to compare this entity with a remote one. As not the whole entity has to be sent this
     * reduces the request overhead.
     * <p>
     * Actual use cases can be checking whether this entity is still current with regards to the remote entity, and
     * ensuring that a update/delete operation is done on the expected version of the remote entity.
     *
     * @param versionIdentifier
     *            The version identifier of this entity.
     */
    public void setVersionIdentifier( @Nullable final String versionIdentifier )
    {
        this.versionIdentifier = versionIdentifier;
    }

    /**
     * Used by fluent helpers and navigation property methods to construct OData queries.
     *
     * @return EDMX name of the entity collection identifier.
     */
    @Nonnull
    protected abstract String getEntityCollection();

    /**
     * Used by fluent helpers and navigation property methods to construct OData queries.
     *
     * @return Default context path to the OData service. In other words, everything in between the
     *         {@code protocol://hostname:port} and the OData resource name (entity set, {@code $metadata}, etc.)
     */
    @Nullable
    protected String getDefaultServicePath()
    {
        return null;
    }

    /**
     * Sets the service path and destination for the fetch commands of this entity.
     * <p>
     * Also applies to any associated entities (navigation properties) that were previously fetched.
     * <p>
     * <b>Note:</b> Use with caution, as this can easily break the fetch calls on this entity. See the interface of the
     * corresponding service for the default service path.
     *
     * @param servicePath
     *            Optional parameter. New service path to apply to this entity and any associated entities that were
     *            previously fetched. If a null value is provided and the service path has never been set, then the
     *            service path will be set to the default defined in the corresponding service interface.
     * @param destination
     *            New destination to apply to this entity and any associated entities that were previously fetched.
     */
    protected void attachToService( @Nullable final String servicePath, @Nonnull final Destination destination )
    {
        if( servicePath != null ) {
            servicePathForFetch = servicePath;
        } else if( servicePathForFetch == null ) {
            servicePathForFetch = getDefaultServicePath();
        }

        destinationForFetch = destination;

        toMapOfNavigationProperties().values().forEach(navProperty -> {
            if( navProperty instanceof Iterable ) {
                final Iterable<?> navPropertyList = (Iterable<?>) navProperty;
                final boolean itemTypeIsEntity = Iterables.getFirst(navPropertyList, null) instanceof VdmEntity;

                if( itemTypeIsEntity ) {
                    for( final Object childEntity : navPropertyList ) {
                        final VdmEntity<?> vdmEntity = (VdmEntity<?>) childEntity;
                        vdmEntity.attachToService(servicePathForFetch, destinationForFetch);
                    }
                }
            } else if( navProperty instanceof VdmEntity ) {
                final VdmEntity<?> vdmEntity = (VdmEntity<?>) navProperty;
                vdmEntity.attachToService(servicePathForFetch, destinationForFetch);
            }
        });
    }

    /**
     * Helper method to lazily resolve a field value from current entity.
     *
     * @param fieldName
     *            The field name to lookup.
     * @param fieldType
     *            The field type to cast the value to.
     * @param <T>
     *            The generic type parameter.
     * @return A list of requested values.
     */
    @Nonnull
    protected <T extends VdmEntity<T>> List<T> fetchFieldAsList(
        @Nonnull final String fieldName,
        @Nonnull final Class<T> fieldType )
    {
        final Destination destination = getDestinationForFetch();
        final ODataRequestResultGeneric response = fetchField(fieldName, destination);
        final List<T> entityList = response.asList(fieldType);
        for( final T entity : entityList ) {
            entity.attachToService(getServicePathForFetch(), destination);
        }
        return entityList;
    }

    /**
     * Helper method to lazily resolve a field value from current entity.
     *
     * @param fieldName
     *            The field name to lookup.
     * @param fieldType
     *            The field type to cast the value to.
     * @param <T>
     *            The generic type parameter.
     * @return The requested values.
     */
    @Nonnull
    protected <T extends VdmEntity<T>> T fetchFieldAsSingle(
        @Nonnull final String fieldName,
        @Nonnull final Class<T> fieldType )
    {
        final Destination destination = getDestinationForFetch();
        final ODataRequestResultGeneric response = fetchField(fieldName, destination);
        final T entity = response.as(fieldType);
        entity.attachToService(getServicePathForFetch(), destination);
        return entity;
    }

    @Nonnull
    private ODataRequestResultGeneric fetchField( final String fieldName, final Destination destination )
    {
        final ODataEntityKey entityKey = ODataEntityKey.of(getKey(), ODataProtocol.V2);
        final ODataResourcePath path = ODataResourcePath.of(getEntityCollection(), entityKey).addSegment(fieldName);
        final ODataRequestRead request = new ODataRequestRead(getServicePathForFetch(), path, null, ODataProtocol.V2);
        if( destination == null ) {
            throw new ODataRequestException(
                request,
                "Failed to fetch related objects from field name "
                    + fieldName
                    + ": The entity was created locally without an assigned HttpDestination. This method is applicable only on entities which were retrieved or created using the OData VDM.",
                null);
        }
        final HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);
        return request.execute(httpClient);
    }
}
