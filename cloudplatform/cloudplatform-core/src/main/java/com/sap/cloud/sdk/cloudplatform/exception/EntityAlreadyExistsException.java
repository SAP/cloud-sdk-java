package com.sap.cloud.sdk.cloudplatform.exception;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.vavr.control.Option;

/**
 * Thrown when an entity is to be created that already exists.
 */
public class EntityAlreadyExistsException extends Exception
{
    private static final long serialVersionUID = -5123139237454991734L;

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
    public EntityAlreadyExistsException()
    {
        super("Entity already exists.");
    }

    /**
     * Exception constructor.
     *
     * @param type
     *            The type of the entity which already exists.
     */
    public EntityAlreadyExistsException( @Nonnull final Class<?> type )
    {
        super("Entity of type " + type.getSimpleName() + " already exists.");
    }

    /**
     * Exception constructor.
     *
     * @param entity
     *            The actual entity that already exists.
     * @param <T>
     *            The generic type of the entity.
     */
    public <T> EntityAlreadyExistsException( @Nonnull final T entity )
    {
        super("Entity " + entity + " already exists.");
    }

    /**
     * Exception constructor.
     *
     * @param entity
     *            The actual entity that already exists.
     * @param type
     *            The type of the entity.
     * @param <T>
     *            The generic type of the entity.
     */
    public <T> EntityAlreadyExistsException( @Nonnull final T entity, @Nonnull final Class<?> type )
    {
        super("Entity " + entity + " of type " + type.getSimpleName() + " already exists.");
        this.entity = entity.toString();
    }
}
