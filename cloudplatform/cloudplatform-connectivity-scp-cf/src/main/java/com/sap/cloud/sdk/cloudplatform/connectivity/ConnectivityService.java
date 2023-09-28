package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sap.cloud.sdk.cloudplatform.CloudPlatform;
import com.sap.cloud.sdk.cloudplatform.CloudPlatformAccessor;
import com.sap.cloud.sdk.cloudplatform.ScpCfCloudPlatform;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;
import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration.CircuitBreakerConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration.TimeLimiterConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceDecorator;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceIsolationMode;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Helper class to resolve request headers for connectivity service interactions.
 */
@Slf4j
@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
class ConnectivityService
{
    // to be changed by https://github.com/SAP/cloud-sdk-java-backlog/issues/275
    static final String SERVICE_NAME = "connectivity";
    private static final ResilienceConfiguration DEFAULT_RESILIENCE_CONFIG =
        ResilienceConfiguration
            .of(ConnectivityService.class)
            .isolationMode(ResilienceIsolationMode.TENANT_OPTIONAL)
            .timeLimiterConfiguration(TimeLimiterConfiguration.of().timeoutDuration(Duration.ofSeconds(6)))
            .circuitBreakerConfiguration(CircuitBreakerConfiguration.of().waitDuration(Duration.ofSeconds(6)));

    private final OAuth2Service oauthService;

    private final ResilienceConfiguration resilienceConfiguration;

    ConnectivityService()
    {
        oauthService = new XsuaaService();
        resilienceConfiguration = DEFAULT_RESILIENCE_CONFIG;
    }

    /**
     * Get the request headers to on-premise systems using the connectivity service.
     *
     * @param useProviderTenant
     *            The flag whether to use provider tenant or subscriber tenant.
     * @param strategy
     *            The request header strategy to use for principal propagation.
     * @return A list of request headers to enable on-premise interaction.
     * @throws DestinationAccessException
     *             When on-premise request headers cannot be resolved. This may happen due to an invalid user token or a
     *             broken OAuth2 service communication.
     */
    List<Header> getHeadersForOnPremiseSystem(
        final boolean useProviderTenant,
        @Nonnull final PrincipalPropagationStrategy strategy )
        throws DestinationAccessException
    {
        try {
            return ResilienceDecorator.executeCallable(() -> {
                final List<Header> headers = strategy.headerResolver.apply(oauthService, useProviderTenant);
                log.trace("The following headers are used for on-premise communication: {}", headers);
                return headers;
            }, resilienceConfiguration);
        }
        catch( final Exception e ) {
            throw new DestinationAccessException("Failed to get on-premise proxy headers.", e);
        }
    }

    @Nonnull
    static ProxyConfiguration getOnPremiseProxyConfiguration()
        throws DestinationAccessException
    {
        String proxyHost = null;
        Integer proxyPort = null;

        final JsonObject connectivityServiceCredentials;
        try {
            final CloudPlatform cloudPlatform = CloudPlatformAccessor.getCloudPlatform();

            if( !(cloudPlatform instanceof ScpCfCloudPlatform) ) {
                throw new ShouldNotHappenException(
                    "The current Cloud platform is not an instance of "
                        + ScpCfCloudPlatform.class.getSimpleName()
                        + ". Please make sure to specify a dependency to com.sap.cloud.sdk.cloudplatform:cloudplatform-core-scp-cf.");
            }

            connectivityServiceCredentials = ((ScpCfCloudPlatform) cloudPlatform).getConnectivityServiceCredentials();
        }
        catch( final CloudPlatformException e ) {
            throw new DestinationAccessException(e);
        }

        @Nullable
        final JsonElement onPremiseHost = connectivityServiceCredentials.get("onpremise_proxy_host");

        if( onPremiseHost != null && onPremiseHost.isJsonPrimitive() ) {
            proxyHost = onPremiseHost.getAsString();
        }

        @Nullable
        final JsonElement onPremisePort = connectivityServiceCredentials.get("onpremise_proxy_port");

        if( onPremisePort != null && onPremisePort.isJsonPrimitive() ) {
            try {
                proxyPort = Integer.valueOf(onPremisePort.getAsString());
            }
            catch( final NumberFormatException e ) {
                throw new DestinationAccessException("Failed to parse on-premise port", e);
            }
        }

        if( proxyHost == null || proxyPort == null ) {
            throw new DestinationAccessException(
                "Failed to configure on-premise proxy. Please make sure to correctly bind your application to a connectivity service instance.");
        }

        try {
            final URI uri = new URI("http://" + proxyHost + ":" + proxyPort);
            return new ProxyConfiguration(uri);
        }
        catch( final URISyntaxException e ) {
            throw new DestinationAccessException("Invalid proxy URI in connectivity service binding.", e);
        }
    }
}
