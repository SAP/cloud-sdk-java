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
 * Default ThreadContext listener chain.
 */
@Slf4j
public class DefaultThreadContextListenerChain implements ThreadContextListenerChain
{
    private static final Map<Integer, ThreadContextListener> defaultListeners = new HashMap<>();

    private static final Comparator<ThreadContextListener> priorityComparator =
        Comparator.comparingInt(ThreadContextListener::getPriority);

    private final Map<Integer, ThreadContextListener> listeners = new HashMap<>(defaultListeners);

    static {
        final Collection<ThreadContextListener> listeners = FacadeLocator.getFacades(ThreadContextListener.class);

        for( final ThreadContextListener listener : listeners ) {
            registerDefaultListener(listener);
        }
    }

    /**
     * Registers a default {@link ThreadContextListener}.
     *
     * @param listener
     *            The default listener to be added.
     */
    public static synchronized void registerDefaultListener( @Nonnull final ThreadContextListener listener )
    {
        @Nullable
        final ThreadContextListener replaced = defaultListeners.put(listener.getPriority(), listener);

        if( log.isDebugEnabled() ) {
            if( replaced != null ) {
                log
                    .debug(
                        "Registered default listener {} with priority {}, replacing existing {}.",
                        listener.getClass().getName(),
                        listener.getPriority(),
                        replaced.getClass().getName());
            } else {
                log
                    .debug(
                        "Registered default listener {} with priority {}.",
                        listener.getClass().getName(),
                        listener.getPriority());
            }
        }
    }

    /**
     * Unregisters a default {@link ThreadContextListener} by its priority.
     *
     * @param listenerPriority
     *            The priority of the listener to be removed.
     */
    public static synchronized void unregisterDefaultListener( final int listenerPriority )
    {
        @Nullable
        final ThreadContextListener removed = defaultListeners.remove(listenerPriority);

        if( log.isDebugEnabled() ) {
            if( removed != null ) {
                log
                    .debug(
                        "Unregistered default listener {} with priority {}.",
                        removed.getClass().getName(),
                        listenerPriority);
            } else {
                log.debug("No default listener to unregister for priority {}.", listenerPriority);
            }
        }
    }

    /**
     * Returns all registered default listeners by priority.
     *
     * @return The list of listeners.
     */
    @Nonnull
    public static List<ThreadContextListener> getDefaultListeners()
    {
        final List<ThreadContextListener> result = new ArrayList<>(defaultListeners.values());
        result.sort(priorityComparator);
        return result;
    }

    @Override
    public void addListener( @Nonnull final ThreadContextListener listener )
    {
        @Nullable
        final ThreadContextListener replaced = listeners.put(listener.getPriority(), listener);

        if( log.isDebugEnabled() ) {
            if( replaced != null ) {
                log
                    .debug(
                        "Added listener {} with priority {}, replacing existing {}.",
                        listener.getClass().getName(),
                        listener.getPriority(),
                        replaced.getClass().getName());
            } else {
                log.debug("Added listener {} with priority {}.", listener.getClass().getName(), listener.getPriority());
            }
        }
    }

    @Override
    public void removeListener( final int listenerPriority )
    {
        @Nullable
        final ThreadContextListener removed = listeners.remove(listenerPriority);

        if( log.isInfoEnabled() ) {
            if( removed != null ) {
                log.info("Removed listener {} with priority {}.", removed.getClass().getName(), listenerPriority);
            } else {
                log.info("No listener to remove for priority {}.", listenerPriority);
            }
        }
    }

    /**
     * Removes the default listeners from this chain.
     */
    public void removeDefaultListeners()
    {
        for( final Integer priority : defaultListeners.keySet() ) {
            removeListener(priority);
        }
    }

    @Nonnull
    @Override
    public List<ThreadContextListener> getListenersOrderedByPriority()
    {
        final List<ThreadContextListener> result = new ArrayList<>(listeners.values());
        result.sort(priorityComparator);
        return result;
    }

    @Nullable
    @Override
    public ThreadContextListener getListener( final int priority )
    {
        return listeners.get(priority);
    }
}
