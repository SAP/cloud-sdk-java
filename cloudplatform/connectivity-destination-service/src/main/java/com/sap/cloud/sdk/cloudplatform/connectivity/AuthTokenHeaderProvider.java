/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceV1Response.DestinationAuthToken;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.http.HttpHeaders;

import com.google.common.base.Strings;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;

import lombok.extern.slf4j.Slf4j;

/**
 * Helper class to handle the complex conversions of destination properties.
 */
@Slf4j
class AuthTokenHeaderProvider implements DestinationHeaderProvider
{
    private static final String SECURITY_SESSION_HEADER = "x-sap-security-session";
    private static final String CREATE_SESSION_VALUE = "create";

    @Nonnull
    @Override
    public List<Header> getHeaders( @Nonnull final DestinationRequestContext requestContext )
    {
        final HttpDestination destination = requestContext.getDestination();
        final AuthenticationType authenticationType = destination.getAuthenticationType();
        final List<Header> result = new ArrayList<>();

        if( authenticationType == AuthenticationType.SAML_ASSERTION ) {
            result.add(new Header(SECURITY_SESSION_HEADER, CREATE_SESSION_VALUE));
        }

        final List<DestinationAuthToken> tokens =
            destination
                .get(DestinationProperty.AUTH_TOKENS)
                .getOrElse(Collections::emptyList)
                .stream()
                .filter(DestinationAuthToken.class::isInstance)
                .map(DestinationAuthToken.class::cast)
                .collect(Collectors.toList());

        if( !tokens.isEmpty() ) {
            result.addAll(getDestinationHeaders(tokens));
        } else if( isAuthTokenExpected(authenticationType) ) {
            log.warn("The destination service did not include an auth token in the response.");
        }

        tokens
            .stream()
            .map(DestinationAuthToken::getExpiryTimestamp)
            .filter(Objects::nonNull)
            .filter(expiry -> expiry.isBefore(LocalDateTime.now()))
            .forEach(
                expiry -> log
                    .warn(
                        """
                        An authorization token of destination {} has expired. \
                        Please ensure that you don't reuse destination objects in your code for longer periods of time.\
                        """,
                        destination.get(DestinationProperty.NAME).getOrElse(destination::toString)));

        return result;
    }

    @Nonnull
    private static List<Header> getDestinationHeaders( @Nonnull final List<DestinationAuthToken> authTokens )
        throws DestinationAccessException
    {
        final List<Header> result = new ArrayList<>();
        for( final DestinationAuthToken authToken : authTokens ) {
            final Header header = authToken.getHttpHeaderSuggestion();
            if( header != null ) {
                result.add(header);
                continue;
            }
            // fallback for legacy logic, if users created an instance manually
            if( !Strings.isNullOrEmpty(authToken.getType()) && !Strings.isNullOrEmpty(authToken.getValue()) ) {
                log
                    .warn(
                        """
                        Header suggestion is missing from destination service response. \
                        Falling back to constructing an authorization header from type and value. \
                        This is unexpected, please report this at https://github.com/SAP/cloud-sdk-java/issues.\
                        """);
                result.add(new Header(HttpHeaders.AUTHORIZATION, authToken.getType() + " " + authToken.getValue()));
                continue;
            }
            throw new DestinationAccessException(
                "Failed to read authentication token. The destination service responded with an auth token that could not be interpreted.");
        }
        return result;
    }

    private static boolean isAuthTokenExpected( @Nonnull final AuthenticationType authType )
    {
        switch( authType ) {
            case NO_AUTHENTICATION:
            case PRINCIPAL_PROPAGATION:
            case CLIENT_CERTIFICATE_AUTHENTICATION:
                return false;
            default:
                return true;
        }
    }
}
