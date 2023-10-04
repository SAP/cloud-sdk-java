/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.thread;

import java.util.concurrent.ConcurrentMap;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextPropertyException;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextPropertyNotFoundException;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * This class represents the default implementation of {@link ThreadContext}.
 */
@ToString
@Slf4j
@EqualsAndHashCode
public class DefaultThreadContext implements ThreadContext
{
    @Getter
    private final ConcurrentMap<String, Property<?>> properties = Maps.newConcurrentMap();

    @Nonnull
    @Override
    @SuppressWarnings( "unchecked" )
    public <T> Try<T> getPropertyValue( @Nonnull final String name )
    {
        if( !containsProperty(name) ) {
            return Try.failure(new ThreadContextPropertyNotFoundException(name));
        }
        return (Try<T>) properties.get(name).getValue();
    }

    @Override
    public void setPropertyIfAbsent( @Nonnull final String name, @Nonnull final Property<?> value )
        throws ThreadContextPropertyException
    {
        log.debug("Setting property '{}' to value: {}.", name, value);
        properties.putIfAbsent(name, value);
    }

    @Nonnull
    @Override
    @SuppressWarnings( "unchecked" )
    public <T> Option<Property<T>> removeProperty( @Nonnull final String name )
        throws ClassCastException
    {
        return (Option<Property<T>>) (Option<?>) Option.of(properties.remove(name));
    }

    @Override
    public boolean containsProperty( @Nonnull final String name )
    {
        return properties.containsKey(name);
    }

    @Nonnull
    @Override
    public ThreadContext duplicate()
    {
        final DefaultThreadContext duplicate = new DefaultThreadContext();
        properties.forEach(( k, v ) -> duplicate.properties.put(k, v.copy()));
        return duplicate;
    }
}
