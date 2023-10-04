/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfDestinationLoader.Cache.DEFAULT_EXPIRATION_DURATION;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

import io.vavr.control.Option;
import io.vavr.control.Try;

/**
 * Builds a {@link DefaultHttpDestination} from the JSON response of the SCP Cloud Foundry Destination Service.
 */
class ScpCfDestinationFactory
{
    @Nonnull
    static Destination fromDestinationServiceV1Response( @Nonnull final ScpCfDestinationServiceV1Response response )
        throws DestinationAccessException
    {
        final Map<String, String> destConf = response.getDestinationConfiguration();

        final DefaultDestination properties =
            DefaultDestination
                .fromMap(destConf)
                .property(DestinationProperty.PROPERTIES_FOR_CHANGE_DETECTION, destConf.keySet())
                .property(
                    DestinationProperty.TENANT_ID,
                    TenantAccessor.tryGetCurrentTenant().map(Tenant::getTenantId).getOrElse(""))
                .build();

        if( properties.get(DestinationProperty.TYPE).contains(DestinationType.RFC) ) {
            return DefaultRfcDestination.fromProperties(properties);
        }

        if( !properties.get(DestinationProperty.TYPE).contains(DestinationType.HTTP) ) {
            return properties;
        }

        final DefaultHttpDestination.Builder builder = DefaultHttpDestination.fromProperties(properties);

        final List<ScpCfDestinationServiceV1Response.DestinationCertificate> certificates = response.getCertificates();
        if( certificates != null ) {
            certificates.forEach(ScpCfDestinationFactory::determineAndSetCertificateExpirationTime);
            builder.property(DestinationProperty.CERTIFICATES, certificates);

            // this is somewhat ugly
            final DefaultDestination destination =
                DefaultDestination.fromMap(destConf).property(DestinationProperty.CERTIFICATES, certificates).build();

            final DestinationKeyStoreExtractor keyStoreExtractor = new DestinationKeyStoreExtractor(destination);
            keyStoreExtractor.getKeyStore().peek(builder::keyStore);
            keyStoreExtractor.getTrustStore().peek(builder::trustStore);
        }

        final List<ScpCfDestinationServiceV1Response.DestinationAuthToken> authTokens = response.getAuthTokens();
        if( authTokens != null ) {
            final AuthenticationType authType =
                AuthenticationType
                    .ofIdentifierOrDefault(destConf.get("Authentication"), AuthenticationType.NO_AUTHENTICATION);
            authTokens.forEach(t -> setExpirationTimestamp(t, authType));

            // Note: it is important that the auth tokens are added as property here
            // for the HttpClientCache we need to include them in the cache key
            // since they may contain cookies for which we need to make sure they
            // are not shared between different http clients
            // we can't attach the tokens directly to the header provider,
            // because the header providers are excluded from the cache key
            builder.property(DestinationProperty.AUTH_TOKENS, authTokens);
            builder.headerProviders(new AuthTokenHeaderProvider());
        }
        // to be changed by https://github.com/SAP/cloud-sdk-java-backlog/issues/275
        if( properties.get(DestinationProperty.PROXY_TYPE).contains(ProxyType.ON_PREMISE) ) {
            final ProxyConfiguration onPremiseProxyConfiguration =
                Try
                    .of(ConnectivityService::getOnPremiseProxyConfiguration)
                    .getOrElseThrow(
                        e -> new DestinationAccessException(
                            "Failed to get retrieve on-premise proxy configuration for destination "
                                + properties.get(DestinationProperty.NAME),
                            e));
            builder.proxyConfiguration(onPremiseProxyConfiguration);
        }
        return builder.build();
    }

    private static void setExpirationTimestamp(
        @Nonnull final ScpCfDestinationServiceV1Response.DestinationAuthToken authToken,
        @Nonnull final AuthenticationType authType )
    {
        Option<Long> maybeExpiresIn =
            Option.of(authToken.getExpiresIn()).filter(val -> !StringUtils.isBlank(val)).map(Long::valueOf);
        // any auth token other than basic auth headers without expiration date are assumed to expire after default duration
        if( maybeExpiresIn.isEmpty() && authType != AuthenticationType.BASIC_AUTHENTICATION ) {
            maybeExpiresIn = ScpCfDestinationLoader.Cache.getExpirationDuration().map(Duration::getSeconds);
        }
        maybeExpiresIn.map(val -> LocalDateTime.now().plusSeconds(val)).peek(authToken::setExpiryTimestamp);
    }

    private static void determineAndSetCertificateExpirationTime(
        @Nonnull final ScpCfDestinationServiceV1Response.DestinationCertificate cert )
    {
        if( !ScpCfDestinationLoader.Cache.isEnabled() || !ScpCfDestinationLoader.Cache.isChangeDetectionEnabled() ) {
            // currently parsing certificates and determining the actual expiration time is not yet implemented
            return;
        }

        // Changes to a certificate in the BTP may not be detected by the caching change detection feature
        // Specifically, change detection only works, if the certificate name changes
        // To be "up to date" (according to the change detection interval configured), we write this time to the destination
        // This date is picked up by the change detection, if enabled. If not enabled, this date is disregarded
        final Duration duration =
            ScpCfDestinationLoader.Cache.getExpirationDuration().getOrElse(DEFAULT_EXPIRATION_DURATION);

        cert.setExpiryTimestamp(LocalDateTime.now().plus(duration));
    }
}
