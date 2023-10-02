/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.exception;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.vavr.control.Option;

/**
 * Thrown when an entity that is requested does not exist.
 */
public class EntityNotFoundException extends Exception
{
    private static final long serialVersionUID = -5891897716381404148L;

    @Nullable
    private String entity = null;

    /**
     * Get the contained entity.
     *
     * @return An optional String to represent the entity.
     */
    @Nonnull
    public Option<String> getEntity()
    {
        return Option.of(entity);
    }

    /**
     * Exception constructor.
     */
    public EntityNotFoundException()
    {
        super("Entity not found.");
    }

    /**
     * Exception constructor.
     *
     * @param type
     *            The type of the entity which was not found.
     */
    public EntityNotFoundException( @Nonnull final Class<?> type )
    {
        super("Entity of type " + type.getSimpleName() + " not found.");
    }

    /**
     * Exception constructor.
     *
     * @param entity
     *            The actual entity that was not found.
     * @param <T>
     *            The generic type of the entity.
     */
    public <T> EntityNotFoundException( @Nonnull final T entity )
    {
        super("Entity " + entity + " not found.");
    }

    /**
     * Exception constructor.
     *
     * @param entity
     *            The actual entity that was not found.
     * @param type
     *            The type of the entity.
     * @param <T>
     *            The generic type of the entity.
     */
    public <T> EntityNotFoundException( @Nonnull final T entity, @Nonnull final Class<?> type )
    {
        super("Entity " + entity + " of type " + type.getSimpleName() + " not found.");
        this.entity = entity.toString();
    }
}
