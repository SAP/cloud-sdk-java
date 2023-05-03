package com.sap.cloud.sdk.testutil;

import java.net.URI;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.connectivity.ProxyConfiguration;

import io.vavr.control.Option;

/**
 * The test system.
 *
 * @param <T>
 *            The test system type.
 */
public interface TestSystem<T>
{
    /**
     * The alias of the test system. The alias is an identifier that allows to reference a test system in the systems
     * and credentials files.
     *
     * @return The alias.
     */
    @Nonnull
    String getAlias();

    /**
     * The URI of the test system.
     *
     * @return The alias.
     */
    @Nonnull
    URI getUri();

    /**
     * The {@link ProxyConfiguration} of the test system, if present.
     *
     * @return An {@link Option} of the {@link ProxyConfiguration}.
     */
    @Nonnull
    Option<ProxyConfiguration> getProxyConfiguration();

    /**
     * The type of the test system.
     *
     * @return The type.
     */
    @Nonnull
    Class<T> getType();
}
