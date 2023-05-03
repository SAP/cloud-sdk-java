package com.sap.cloud.sdk.testutil;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import java.net.URI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sap.cloud.sdk.cloudplatform.security.Credentials;
import com.sap.cloud.sdk.cloudplatform.security.NoCredentials;
import com.sap.cloud.sdk.s4hana.serialization.SapClient;

import io.vavr.Function2;
import io.vavr.Function3;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
class DefaultServerMocker implements ServerMocker
{
    private final Function2<String, URI, ?> mockDestination;
    private final Function3<String, ErpSystem, Credentials, ?> mockErpDestination;

    private String prependSlashIfMissing( final String path )
    {
        return path.startsWith("/") ? path : "/" + path;
    }

    @Nonnull
    @Override
    public WireMockRule mockServer(
        @Nonnull final String destinationName,
        @Nullable final String relativePath,
        @Nullable final WireMockConfiguration configuration )
    {
        final WireMockRule server =
            new WireMockRule(configuration != null ? configuration : wireMockConfig().dynamicPort());
        server.start();

        mockDestination
            .apply(
                destinationName,
                URI.create(server.url(prependSlashIfMissing(relativePath != null ? relativePath : ""))));

        return server;
    }

    @Nonnull
    @Override
    public WireMockRule mockServer( @Nonnull final String destinationName, @Nullable final String relativePath )
    {
        return mockServer(destinationName, relativePath, null);
    }

    @Nonnull
    @Override
    public WireMockRule mockServer( @Nonnull final String destinationName )
    {
        return mockServer(destinationName, null);
    }

    @Nonnull
    @Override
    public WireMockRule mockErpServer(
        @Nonnull final String destinationName,
        @Nullable final SapClient sapClient,
        @Nullable final String relativePath,
        @Nullable final WireMockConfiguration configuration )
    {
        final WireMockRule erpServer =
            new WireMockRule(configuration != null ? configuration : wireMockConfig().dynamicPort());
        erpServer.start();

        final URI uri = URI.create(erpServer.url(prependSlashIfMissing(relativePath != null ? relativePath : "")));

        mockErpDestination
            .apply(
                destinationName,
                ErpSystem.builder().alias("Mocked ERP server [" + uri + "]").uri(uri).sapClient(sapClient).build(),
                // use NoCredentials to avoid automatic resolution of credentials
                new NoCredentials());

        return erpServer;
    }

    @Nonnull
    @Override
    public WireMockRule mockErpServer(
        @Nonnull final String destinationName,
        @Nullable final SapClient sapClient,
        @Nullable final String relativePath )
    {
        return mockErpServer(destinationName, sapClient, relativePath, null);
    }

    @Nonnull
    @Override
    public WireMockRule mockErpServer( @Nonnull final String destinationName, @Nullable final SapClient sapClient )
    {
        return mockErpServer(destinationName, sapClient, null, null);
    }

    @Nonnull
    @Override
    public WireMockRule mockErpServer( @Nonnull final String destinationName )
    {
        return mockErpServer(destinationName, null, null, null);
    }
}
