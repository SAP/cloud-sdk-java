/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationService.Cache.DEFAULT_EXPIRATION_DURATION;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * Builds a {@link DefaultHttpDestination} from the JSON response of the SCP Cloud Foundry Destination Service.
 */
@Slf4j
class DestinationServiceFactory
{
    private static final String PROVIDER_TENANT_ID = ""; // as per contract in DestinationProperty.TENANT_ID

    // for testing only
    @Nonnull
    static Destination fromDestinationServiceV1Response( @Nonnull final DestinationServiceV1Response response )
        throws DestinationAccessException
    {
        return fromDestinationServiceV1Response(response, OnBehalfOf.NAMED_USER_CURRENT_TENANT);
    }

    @Nonnull
    static Destination fromDestinationServiceV1Response(
        @Nonnull final DestinationServiceV1Response response,
        @Nonnull final OnBehalfOf onBehalfOf )
        throws DestinationAccessException
    {
        final Map<String, String> destConf = response.getDestinationConfiguration();

        final DefaultDestination.Builder baseBuilder = DefaultDestination.fromMap(destConf);

        // enable change detection
        baseBuilder.property(DestinationProperty.PROPERTIES_FOR_CHANGE_DETECTION, destConf.keySet());

        // enable tenant id
        baseBuilder.property(DestinationProperty.TENANT_ID, getDestinationTenantId(onBehalfOf));

        // finalize RFC destination
        if( baseBuilder.get(DestinationProperty.TYPE).contains(DestinationType.RFC) ) {
            return handleRfcDestination(baseBuilder.build());
        }

        // finalize HTTP destination
        if( baseBuilder.get(DestinationProperty.TYPE).contains(DestinationType.HTTP) ) {
            return handleHttpDestination(baseBuilder.build(), response.getCertificates(), response.getAuthTokens());
        }

        return baseBuilder.build();
    }

    private static String getDestinationTenantId( @Nonnull final OnBehalfOf onBehalfOf )
    {
        switch( onBehalfOf ) {
            case TECHNICAL_USER_PROVIDER:
                return PROVIDER_TENANT_ID; // TECHNICAL_USER_PROVIDER <- ALWAYS_PROVIDER
            case NAMED_USER_CURRENT_TENANT:
            case TECHNICAL_USER_CURRENT_TENANT:
                final Try<String> tenantId = TenantAccessor.tryGetCurrentTenant().map(Tenant::getTenantId);
                return tenantId.getOrElse(PROVIDER_TENANT_ID);
            default:
                throw new IllegalStateException("Unknown OnBehalfOf: " + onBehalfOf);
        }
    }

    @Deprecated
    private static Destination handleRfcDestination( final DestinationProperties baseProperties )
    {
        return DefaultRfcDestination.fromProperties(baseProperties);
    }

    private static Destination handleHttpDestination(
        @Nonnull final DefaultDestination baseProperties,
        @Nullable final List<DestinationServiceV1Response.DestinationCertificate> certificates,
        @Nullable final List<DestinationServiceV1Response.DestinationAuthToken> authTokens )
    {
        final DefaultHttpDestination.Builder builder = DefaultHttpDestination.fromProperties(baseProperties);

        // enable certificates and truststore/keystore
        if( certificates != null && !certificates.isEmpty() ) {
            certificates.forEach(DestinationServiceFactory::determineAndSetCertificateExpirationTime);
            builder.property(DestinationProperty.CERTIFICATES, certificates);

            final DestinationKeyStoreExtractor keyStoreExtractor = new DestinationKeyStoreExtractor(builder::get);
            keyStoreExtractor.getKeyStore().peek(builder::keyStore);
            keyStoreExtractor.getTrustStore().peek(builder::trustStore);
        }

        // enable auth tokens
        if( authTokens != null && !authTokens.isEmpty() ) {
            final AuthenticationType authType =
                builder.get(DestinationProperty.AUTH_TYPE).getOrElse(AuthenticationType.NO_AUTHENTICATION);
            authTokens.forEach(t -> throwOnTokenError(builder.get(DestinationProperty.NAME).getOrNull(), t));
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

        return builder.build();
    }

    private static void throwOnTokenError(
        final String destinationName,
        final DestinationServiceV1Response.DestinationAuthToken destinationAuthToken )
    {
        if( destinationAuthToken.getError() != null ) {
            final String msg =
                """
                    Failed to read authentication token of destination '%s'. The destination service responded with an error: '%s'.
                    In case only the properties of a destination should be accessed, without performing authorization flows, please use the 'getDestinationProperties'  method on 'DestinationService' instead.\
                    """;
            throw new DestinationAccessException(msg.formatted(destinationName, destinationAuthToken.getError()));
        }
    }

    private static void setExpirationTimestamp(
        @Nonnull final DestinationServiceV1Response.DestinationAuthToken authToken,
        @Nonnull final AuthenticationType authType )
    {
        Option<Long> maybeExpiresIn =
            Option.of(authToken.getExpiresIn()).filter(val -> !StringUtils.isBlank(val)).map(Long::valueOf);
        // any auth token other than basic auth headers without expiration date are assumed to expire after default duration
        if( maybeExpiresIn.isEmpty() && authType != AuthenticationType.BASIC_AUTHENTICATION ) {
            maybeExpiresIn = DestinationService.Cache.getExpirationDuration().map(Duration::getSeconds);
        }
        maybeExpiresIn.map(val -> LocalDateTime.now().plusSeconds(val)).peek(authToken::setExpiryTimestamp);
    }

    private static void determineAndSetCertificateExpirationTime(
        @Nonnull final DestinationServiceV1Response.DestinationCertificate cert )
    {
        if( !DestinationService.Cache.isEnabled() || !DestinationService.Cache.isChangeDetectionEnabled() ) {
            // currently parsing certificates and determining the actual expiration time is not yet implemented
            return;
        }

        // Changes to a certificate in the BTP may not be detected by the caching change detection feature
        // Specifically, change detection only works, if the certificate name changes
        // To be "up to date" (according to the change detection interval configured), we write this time to the destination
        // This date is picked up by the change detection, if enabled. If not enabled, this date is disregarded
        final Duration duration =
            DestinationService.Cache.getExpirationDuration().getOrElse(DEFAULT_EXPIRATION_DURATION);

        cert.setExpiryTimestamp(LocalDateTime.now().plus(duration));
    }
}
