package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;

import org.apache.http.HttpHeaders;

import com.google.common.collect.Lists;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.security.OAuth2ServiceSettings;
import com.sap.cloud.sdk.cloudplatform.security.exception.TokenRequestDeniedException;
import com.sap.cloud.sdk.cloudplatform.security.exception.TokenRequestFailedException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * The principal propagation strategy decides on what request headers are attached for on-premise communication.
 */
@RequiredArgsConstructor
@Slf4j
public enum PrincipalPropagationStrategy
{
    /**
     * DISABLED, only the technical service user from the XSUAA binding is used.
     */
    DISABLED(PrincipalPropagationStrategy::getHeadersWithoutPrincipalPropagation),

    /**
     * COMPATIBILITY, use both headers {@code Proxy-Authorization} and {@code SAP-Connectivity-Authentication}.
     */
    COMPATIBILITY(PrincipalPropagationStrategy::getHeadersWithCompatibilityStrategy),

    /**
     * RECOMMENDATION, use recommended headers {@code Proxy-Authorization} with an user exchange token.
     */
    RECOMMENDATION(PrincipalPropagationStrategy::getHeadersWithRecommendedStrategy);

    private static final String SAP_CONNECTIVITY_AUTHENTICATION_HEADER = "SAP-Connectivity-Authentication";
    private static final String PROXY_AUTHORIZATION_HEADER = "Proxy-Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Strategy specific handler to invoke the request header creation.
     */
    final BiFunction<OAuth2Service, Boolean, List<Header>> headerResolver;

    /**
     * The default strategy to use, when principal propagation is applicable.
     */
    @Getter
    @Setter
    @Nonnull
    private static PrincipalPropagationStrategy defaultStrategy = PrincipalPropagationStrategy.RECOMMENDATION;

    /**
     * Resolve principal propagation strategy from destination.
     *
     * @param destination
     *            The destination.
     * @param authenticationType
     *            The optional, preferred authentication type to derive the principal propagation strategy from.
     * @return The principal propagation strategy chosen for the destination.
     */
    @Nonnull
    static
        PrincipalPropagationStrategy
        of( @Nonnull final DestinationProperties destination, @Nonnull final AuthenticationType authenticationType )
    {
        if( authenticationType == AuthenticationType.PRINCIPAL_PROPAGATION ) {
            log.debug("Using default strategy {} for OnPremise and Principal Propagation.", defaultStrategy);
            return defaultStrategy;
        }
        log
            .debug(
                "Authentication type {} for destination {} with ProxyType {} does not require"
                    + " Principal Propagation, hence using {}.",
                authenticationType,
                destination,
                ProxyType.ON_PREMISE,
                PrincipalPropagationStrategy.DISABLED);
        return PrincipalPropagationStrategy.DISABLED;
    }

    /**
     * Resolve list of request headers without principal propagation in on-premise communication. The only header
     * provided is from technical user via Client Credentials. The data is read from service binding.
     *
     * @param oauthService
     *            The OAuth2 service reference.
     * @param useProviderTenant
     *            Flag whether to use subscriber tenant or provider tenant.
     * @return The list of request headers
     * @throws TokenRequestFailedException
     *             When the token cannot be requested or correctly parsed.
     * @throws TokenRequestDeniedException
     *             When the authorization for the token request was rejected.
     */
    @Nonnull
    private static List<Header> getHeadersWithoutPrincipalPropagation(
        @Nonnull final OAuth2Service oauthService,
        final boolean useProviderTenant )
        throws TokenRequestFailedException,
            TokenRequestDeniedException
    {
        log.debug("Obtaining on-premise headers without principal propagation.");

        final ServiceCredentialsRetriever.OAuth2Credentials connectivityCredentials =
            new ServiceCredentialsRetriever().getCredentials(ConnectivityService.SERVICE_NAME);

        log.debug("Received OAuth2 credentials for client credentials grant.");

        final AccessToken technicalAccessToken =
            oauthService
                .retrieveAccessTokenViaClientCredentialsGrant(
                    OAuth2ServiceSettings.ofBaseUri(connectivityCredentials.getUri()).build(),
                    connectivityCredentials.getCredentials(),
                    useProviderTenant);

        log.debug("Received access token for HTTP header {}.", PROXY_AUTHORIZATION_HEADER);

        final String headerValue = BEARER_PREFIX + technicalAccessToken.getValue();
        log.debug("Successfully added {} header.", PROXY_AUTHORIZATION_HEADER);

        return Collections.singletonList(new Header(PROXY_AUTHORIZATION_HEADER, headerValue));
    }

    /**
     * Resolve list of request headers for principal propagation in on-premise communication. With the Compatibility
     * mode, the same Client Credentials header is taken as if the strategy was without principal propagation. In
     * addition the current User Access Token is added as separate header.
     *
     * @param oauthService
     *            The OAuth2 service reference.
     * @param useProviderTenant
     *            Flag whether to use subscriber tenant or provider tenant.
     * @return The list of request headers
     * @throws TokenRequestFailedException
     *             When the token cannot be requested or correctly parsed.
     * @throws TokenRequestDeniedException
     *             When the authorization for the token request was rejected.
     */
    @Nonnull
    private static List<Header> getHeadersWithCompatibilityStrategy(
        @Nonnull final OAuth2Service oauthService,
        final boolean useProviderTenant )
        throws TokenRequestFailedException,
            TokenRequestDeniedException
    {
        final List<Header> proxyAuthHeader = getHeadersWithoutPrincipalPropagation(oauthService, useProviderTenant);
        final List<Header> result = Lists.newArrayList(proxyAuthHeader);

        final AuthToken jwt =
            AuthTokenAccessor
                .tryGetCurrentToken()
                .getOrElseThrow(
                    ( e ) -> new TokenRequestFailedException(
                        String
                            .format(
                                "Failed to add '%s' header for on-premise connectivity: no JWT bearer found in '%s' header of request. Continuing without header. Connecting to on-premise systems may not be possible.",
                                SAP_CONNECTIVITY_AUTHENTICATION_HEADER,
                                HttpHeaders.AUTHORIZATION),
                        e));

        final String headerValue = BEARER_PREFIX + jwt.getJwt().getToken();
        log.debug("Successfully added {} header.", SAP_CONNECTIVITY_AUTHENTICATION_HEADER);

        result.add(new Header(SAP_CONNECTIVITY_AUTHENTICATION_HEADER, headerValue));
        return result;
    }

    /**
     * Resolve list of request headers for principal propagation in on-premise communication. With the Recommended mode,
     * the "client_id" from service binding Client Credentials is taken together with the current User Access Token, in
     * order to resolve a new User Exchange Token. It will be returned as single header.
     *
     * @param oauthService
     *            The OAuth2 service reference.
     * @param ignored
     *            Flag whether to use subscriber tenant or provider tenant. Not relevant for the recommended strategy,
     *            the tenant is derived from the user token.
     * @return The list of request headers
     * @throws TokenRequestFailedException
     *             When the token cannot be requested or correctly parsed.
     * @throws TokenRequestDeniedException
     *             When the authorization for the token request was rejected.
     */
    @Nonnull
    private static
        List<Header>
        getHeadersWithRecommendedStrategy( @Nonnull final OAuth2Service oauthService, final boolean ignored )
            throws TokenRequestFailedException,
                TokenRequestDeniedException
    {
        final ServiceCredentialsRetriever.OAuth2Credentials connectivityCredentials =
            new ServiceCredentialsRetriever().getCredentials(ConnectivityService.SERVICE_NAME);

        final AccessToken userExchangeAccessToken =
            oauthService
                .retrieveAccessTokenViaJwtBearerGrant(
                    OAuth2ServiceSettings.ofBaseUri(connectivityCredentials.getUri()).build(),
                    connectivityCredentials.getCredentials());

        final String headerValue = BEARER_PREFIX + userExchangeAccessToken.getValue();
        log.debug("Successfully added {} header.", PROXY_AUTHORIZATION_HEADER);

        return Collections.singletonList(new Header(PROXY_AUTHORIZATION_HEADER, headerValue));
    }
}
