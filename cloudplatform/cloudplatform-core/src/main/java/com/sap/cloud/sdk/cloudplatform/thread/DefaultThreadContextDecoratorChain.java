/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.thread;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.util.FacadeLocator;

import lombok.extern.slf4j.Slf4j;

/**
 * Default ThreadContext decorator chain.
 */
@Slf4j
public class DefaultThreadContextDecoratorChain implements ThreadContextDecoratorChain
{
    private static final Map<Integer, ThreadContextDecorator> defaultDecorators = new HashMap<>();

    private static final Comparator<ThreadContextDecorator> priorityComparator =
        Comparator.comparingInt(ThreadContextDecorator::getPriority);

    private final Map<Integer, ThreadContextDecorator> decorators = new HashMap<>(defaultDecorators);

    static {
        final Collection<ThreadContextDecorator> decorators = FacadeLocator.getFacades(ThreadContextDecorator.class);

        for( final ThreadContextDecorator decorator : decorators ) {
            registerDefaultDecorator(decorator);
        }
    }

    /**
     * Registers a default {@link ThreadContextDecorator}.
     *
     * @param decorator
     *            The default decorator to be added.
     */
    public static synchronized void registerDefaultDecorator( @Nonnull final ThreadContextDecorator decorator )
    {
        @Nullable
        final ThreadContextDecorator replaced = defaultDecorators.put(decorator.getPriority(), decorator);

        if( log.isDebugEnabled() ) {
            if( replaced != null ) {
                log
                    .debug(
                        "Registered default decorator {} with priority {}, replacing existing {}.",
                        decorator.getClass().getName(),
                        decorator.getPriority(),
                        replaced.getClass().getName());
            } else {
                log
                    .debug(
                        "Registered default decorator {} with priority {}.",
                        decorator.getClass().getName(),
                        decorator.getPriority());
            }
        }
    }

    /**
     * Unregisters a default {@link ThreadContextDecorator} by its priority.
     *
     * @param decoratorPriority
     *            The priority of the decorator to be removed.
     */
    public static synchronized void unregisterDefaultDecorator( final int decoratorPriority )
    {
        @Nullable
        final ThreadContextDecorator removed = defaultDecorators.remove(decoratorPriority);

        if( log.isInfoEnabled() ) {
            if( removed != null ) {
                log
                    .info(
                        "Unregistered default decorator {} with priority {}.",
                        removed.getClass().getName(),
                        decoratorPriority);
            } else {
                log.info("No default decorator to unregister for priority {}.", decoratorPriority);
            }
        }
    }

    /**
     * Returns all registered default decorators by priority.
     *
     * @return The list of decorators.
     */
    @Nonnull
    public static List<ThreadContextDecorator> getDefaultDecorators()
    {
        final List<ThreadContextDecorator> result = new ArrayList<>(defaultDecorators.values());
        result.sort(priorityComparator);
        return result;
    }

    @Override
    public void addDecorator( @Nonnull final ThreadContextDecorator decorator )
    {
        @Nullable
        final ThreadContextDecorator replaced = decorators.put(decorator.getPriority(), decorator);

        if( log.isDebugEnabled() ) {
            if( replaced != null ) {
                log
                    .debug(
                        "Added decorator {} with priority {}, replacing existing {}.",
                        decorator.getClass().getName(),
                        decorator.getPriority(),
                        replaced.getClass().getName());
            } else {
                log
                    .debug(
                        "Added decorator {} with priority {}.",
                        decorator.getClass().getName(),
                        decorator.getPriority());
            }
        }
    }

    @Override
    public void removeDecorator( final int decoratorPriority )
    {
        @Nullable
        final ThreadContextDecorator removed = decorators.remove(decoratorPriority);

        if( log.isInfoEnabled() ) {
            if( removed != null ) {
                log.info("Removed decorator {} with priority {}.", removed.getClass().getName(), decoratorPriority);
            } else {
                log.info("No decorator to remove for priority {}.", decoratorPriority);
            }
        }
    }

    /**
     * Removes the default decorators from this chain.
     */
    public void removeDefaultDecorators()
    {
        for( final Integer priority : defaultDecorators.keySet() ) {
            removeDecorator(priority);
        }
    }

    @Nonnull
    @Override
    public List<ThreadContextDecorator> getDecoratorsOrderedByPriority()
    {
        final List<ThreadContextDecorator> result = new ArrayList<>(decorators.values());
        result.sort(priorityComparator);
        return result;
    }
}
