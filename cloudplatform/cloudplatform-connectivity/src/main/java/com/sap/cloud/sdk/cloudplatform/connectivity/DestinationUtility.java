/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationProperty.SYSTEM_USER;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.google.common.annotations.Beta;

import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for destination related functionality.
 * <p>
 * <b>This class is meant for internal usage only.</b>
 *
 * @since 4.6.0
 */
@Beta
@Slf4j
public final class DestinationUtility
{
    /**
     * Helper method to identify if a destination requires user token exchange
     *
     * @param destination
     *            The destination to be checked
     * @return a boolean value depending on if the destination requires user token exchange
     */
    public static boolean requiresUserTokenExchange( @Nonnull final DestinationProperties destination )
    {
        final Option<AuthenticationType> maybeAuthType =
            destination
                .get(DestinationProperty.AUTH_TYPE)
                .orElse(() -> destination.get(DestinationProperty.AUTH_TYPE_FALLBACK));

        if( maybeAuthType.isEmpty() ) {
            return false;
        }

        final AuthenticationType authType = maybeAuthType.get();
        final String systemUserPropertyValue = destination.get(SYSTEM_USER).getOrNull();

        return requiresUserTokenExchange(authType, systemUserPropertyValue);
    }

    /**
     * Helper method to identify if an {@link AuthenticationType} requires user token exchange
     *
     * @param authType
     *            The {@link AuthenticationType} to be examined
     * @param systemUser
     *            The <strong>SystemUser</strong> property value maintained in a destination
     * @return a boolean value depending on if the authentication type requires user token exchange
     */
    public static
        boolean
        requiresUserTokenExchange( @Nonnull final AuthenticationType authType, @Nullable final String systemUser )
    {
        // Handle special case for SAML Bearer Assertion and SAP Assertion SSO
        if( !StringUtils.isBlank(systemUser)
            && (authType == AuthenticationType.OAUTH2_SAML_BEARER_ASSERTION
                || authType == AuthenticationType.SAP_ASSERTION_SSO) ) {
            final String msg =
                "Destination properties do not qualify for user token exchange: authentication type is {}, but property {} is set.";
            log.debug(msg, authType, SYSTEM_USER.getKeyName());
            return false;
        }

        switch( authType ) {
            case OAUTH2_JWT_BEARER:
            case OAUTH2_SAML_BEARER_ASSERTION:
            case SAP_ASSERTION_SSO:
            case OAUTH2_USER_TOKEN_EXCHANGE:
            case SAML_ASSERTION:
                return true;
            default:
                return false;
        }
    }

    private DestinationUtility()
    {
        throw new IllegalStateException("This utility class must not be instantiated.");
    }
}
