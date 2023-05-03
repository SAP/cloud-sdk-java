package com.sap.cloud.sdk.testutil;

import java.net.URI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.connectivity.ProxyConfiguration;

import io.vavr.control.Option;
import lombok.Data;
import lombok.Getter;

/**
 * The Generic Test System.
 */
@Data
public class GenericSystem implements TestSystem<GenericSystem>
{
    @Getter
    private final String alias;

    @Getter
    private final URI uri;

    @Nullable
    private final ProxyConfiguration proxyConfiguration;

    /**
     * The type constructor.
     *
     * @param alias
     *            The generic system alias.
     * @param uri
     *            The generic system URI.
     * @param proxyConfiguration
     *            The optional proxy configuration.
     */
    public GenericSystem(
        @Nonnull final String alias,
        @Nonnull final URI uri,
        @Nullable final ProxyConfiguration proxyConfiguration )
    {
        this.alias = alias;
        this.uri = uri;
        this.proxyConfiguration = proxyConfiguration;
    }

    @Nonnull
    @Override
    public Option<ProxyConfiguration> getProxyConfiguration()
    {
        return Option.of(proxyConfiguration);
    }

    @Nonnull
    @Override
    public Class<GenericSystem> getType()
    {
        return GenericSystem.class;
    }
}
