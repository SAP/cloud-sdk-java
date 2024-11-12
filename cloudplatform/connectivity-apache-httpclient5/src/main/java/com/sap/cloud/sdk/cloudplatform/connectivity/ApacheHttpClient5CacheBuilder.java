/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.time.Duration;

import javax.annotation.Nonnull;

import org.apache.hc.client5.http.classic.HttpClient;

import com.google.common.annotations.Beta;

/**
 * Builder class for a default implementation of the {@link ApacheHttpClient5Cache} interface.
 *
 * @since 4.20.0
 */
public class ApacheHttpClient5CacheBuilder
{
    @Nonnull
    private Duration duration = DefaultApacheHttpClient5Cache.DEFAULT_DURATION;

    /**
     * Sets the duration for which {@link HttpClient} instances will be cached.
     * <p>
     * This is an <b>optional</b> parameter. By default, the cache duration is set to 5 minutes.
     * </p>
     *
     * @param durationInMilliseconds
     *            The duration in milliseconds.
     * @return This builder.
     */
    @Nonnull
    @Beta
    public ApacheHttpClient5CacheBuilder durationInMilliseconds( final int durationInMilliseconds )
    {
        return duration(Duration.ofMillis(durationInMilliseconds));
    }

    /**
     * Sets the duration for which {@link HttpClient} instances will be cached.
     * <p>
     * This is an <b>optional</b> parameter. By default, the cache duration is set to 5 minutes.
     * </p>
     *
     * @param duration
     *            The duration.
     * @return This builder.
     */
    @Nonnull
    public ApacheHttpClient5CacheBuilder duration( @Nonnull final Duration duration )
    {
        this.duration = duration;
        return this;
    }

    /**
     * Builds a new {@link ApacheHttpClient5Cache} instance with the previously configured parameters.
     *
     * @return The new {@link ApacheHttpClient5Cache} instance.
     */
    @Nonnull
    public ApacheHttpClient5Cache build()
    {
        return new DefaultApacheHttpClient5Cache(duration);
    }
}
