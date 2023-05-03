package com.sap.cloud.sdk.testutil;

import java.net.URI;
import java.security.KeyStore;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.assertj.core.util.Lists;

import com.google.common.collect.Iterables;
import com.sap.cloud.sdk.cloudplatform.connectivity.Header;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestinationProperties;
import com.sap.cloud.sdk.s4hana.connectivity.DefaultErpHttpDestination;

import io.vavr.control.Option;
import lombok.Getter;

class TestErpHttpDestination extends DefaultErpHttpDestination
{
    @Nullable
    private final Iterable<Header> headers;

    @Getter
    private final Option<KeyStore> keyStore;

    @Getter
    private final Option<KeyStore> trustStore;

    @Getter
    private final Option<String> keyStorePassword;

    /**
     * Creates a new instance of based on the given "generic" destination.
     *
     * @param baseProperties
     *            The destination to take the properties from.
     */
    @lombok.Builder
    public TestErpHttpDestination(
        @Nonnull final HttpDestinationProperties baseProperties,
        @Nullable final Iterable<Header> headers,
        @Nullable final KeyStore trustStore,
        @Nullable final KeyStore keyStore,
        @Nullable final String keyStorePassword )
    {
        super(baseProperties);
        this.headers = headers;
        this.trustStore = Option.of(trustStore);
        this.keyStore = Option.of(keyStore);
        this.keyStorePassword = Option.of(keyStorePassword);
    }

    @Nonnull
    @Override
    public Collection<Header> getHeaders( @Nonnull final URI requestUri )
    {
        final List<Header> result = Lists.newArrayList(super.getHeaders(requestUri));
        if( headers != null ) {
            Iterables.addAll(result, headers);
        }
        return result;
    }
}
