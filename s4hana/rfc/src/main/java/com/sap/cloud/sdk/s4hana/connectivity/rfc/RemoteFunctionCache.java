/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.RfcDestination;
import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionException;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Provides access to the Remote Function Cache and allows clearing it.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Slf4j
@Deprecated
public class RemoteFunctionCache
{
    /**
     * Clears the remote function cache for a given {@link Destination}.
     *
     * If the {@link Destination} is of type {@link HttpDestination}, nothing is done because no cache exists. If the
     * {@link Destination} is of type {@link RfcDestination}, the related {@link JCoRepository} is cleared.
     *
     * @param destination
     *            The destination for which the cache should be cleared
     * @throws RemoteFunctionException
     *             In case an error occured during clearing the cache.
     */
    public static void clearCache( @Nonnull final Destination destination )
        throws RemoteFunctionException
    {
        clearCacheInternal(destination, null, null);
    }

    /**
     * Clears the remote function cache for a given {@link Destination} and a given function name.
     *
     * If the {@link Destination} is of type {@link HttpDestination}, nothing is done because no cache exists. If the
     * {@link Destination} is of type {@link RfcDestination}, the related {@link JCoRepository} is cleared.
     *
     * @param destination
     *            The destination for which the cache should be cleared
     * @param functionName
     *            The function name for which the cache should be cleared
     * @throws RemoteFunctionException
     *             In case an error occured during clearing the cache.
     */
    public static void clearCache( @Nonnull final Destination destination, @Nonnull final String functionName )
        throws RemoteFunctionException
    {
        clearCacheInternal(destination, null, functionName);
    }

    static void clearCacheInternal(
        @Nonnull final Destination destination,
        @Nullable final JCoRepository jCoRepository,
        @Nullable final String functionName )
        throws RemoteFunctionException
    {
        log.debug("Attempting to clear the remote function cache.");

        if( destination.isHttp() ) {
            log.debug("{} instances do not use a remote function cache.", HttpDestination.class);
            return;
        }

        if( destination.isRfc() ) {
            clearCacheForRfcDestination(destination.asRfc(), jCoRepository, functionName);
            return;
        }

        log.debug("{} instances do not use a remote function cache.", destination.getClass());
    }

    private static void clearCacheForRfcDestination(
        @Nonnull final RfcDestination rfcDestination,
        @Nullable final JCoRepository jCoRepository,
        @Nullable final String functionName )
        throws RemoteFunctionException
    {
        final String destinationName =
            rfcDestination
                .get("Name", String.class)
                .getOrElseThrow(
                    () -> new RemoteFunctionException(
                        "Failed to clear the remote function cache because "
                            + RfcDestination.class
                            + " does not have a name."));
        try {
            final JCoRepository repositoryToClear =
                jCoRepository != null
                    ? jCoRepository
                    : JCoDestinationManager.getDestination(destinationName).getRepository();

            if( functionName != null ) {
                repositoryToClear.removeFunctionTemplateFromCache(functionName);

                log
                    .debug(
                        "Cleared the remote function cache for {} with name {} and function with name {}.",
                        RfcDestination.class,
                        destinationName,
                        functionName);
            } else {
                repositoryToClear.clear();

                log
                    .debug(
                        "Cleared the remote function cache for {} with name {}.",
                        RfcDestination.class,
                        destinationName);
            }
        }
        catch( final JCoException e ) {
            throw new RemoteFunctionException("Failed to clear the remote function cache.", e);
        }
    }
}
