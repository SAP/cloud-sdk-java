/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.vavr.control.Option;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Base class for an OData entity.
 *
 * @param <EntityT>
 *            The entity type.
 */
@EqualsAndHashCode( doNotUseGetters = true, callSuper = true )
public abstract class VdmEntity<EntityT> extends VdmObject<EntityT>
{
    @Nullable
    private String versionIdentifier = null;

    /**
     * Select all properties of an entity.
     *
     * @param <EntityT>
     *            The entity type.
     * @return A selector for all entity fields.
     */
    @Nonnull
    protected static <EntityT> SimpleProperty<EntityT> all()
    {
        return new SimpleProperty.All<>();
    }

    /**
     *
     * Getter for the version identifier of this entity.
     * <p>
     * This identifier can be used to compare this entity with a remote one. As not the whole entity has to be sent this
     * reduces the request overhead.
     * <p>
     * Actual use cases can be checking whether this entity is still current with regards to the remote entity, and
     * ensuring that a update/delete operation is done on the expected version of the remote entity.
     *
     * @return The version identifier.
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
     * Used by request builders and navigation property methods to construct OData requests.
     *
     * @return EDMX name of the entity collection identifier.
     */
    @Nonnull
    protected abstract String getEntityCollection();

    /**
     * Used by request builders and navigation property methods to construct OData requests.
     *
     * @return Default context path to the OData service. In other words, everything in between the
     *         {@code protocol://hostname:port} and the OData resource name (entity set, {@code $metadata}, etc.)
     */
    @Nullable
    protected String getDefaultServicePath()
    {
        return null;
    }
}
