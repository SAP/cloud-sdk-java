package com.sap.cloud.sdk.cloudplatform.connectivity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Enum representing different ways a user may be authenticated .
 */
@Slf4j
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
public enum AuthenticationType
{
    /**
     * NoAuthentication
     */
    NO_AUTHENTICATION("NoAuthentication"),

    /**
     * BasicAuthentication
     */
    BASIC_AUTHENTICATION("BasicAuthentication"),

    /**
     * PrincipalPropagation
     */
    PRINCIPAL_PROPAGATION("PrincipalPropagation"),

    /**
     * ClientCertificateAuthentication
     */
    CLIENT_CERTIFICATE_AUTHENTICATION("ClientCertificateAuthentication"),

    /**
     * OAuth2ClientCredentials
     */
    OAUTH2_CLIENT_CREDENTIALS("OAuth2ClientCredentials"),

    /**
     * OAuth2SAMLBearerAssertion
     */
    OAUTH2_SAML_BEARER_ASSERTION("OAuth2SAMLBearerAssertion"),

    /**
     * OAuth2UserTokenExchange
     */
    OAUTH2_USER_TOKEN_EXCHANGE("OAuth2UserTokenExchange"),

    /**
     * OAuth2JWTBearer
     */
    OAUTH2_JWT_BEARER("OAuth2JWTBearer"),

    /**
     * SAPAssertionSSO
     */
    SAP_ASSERTION_SSO("SAPAssertionSSO"),

    /**
     * OAuth2Password
     */
    OAUTH2_PASSWORD("OAuth2Password"),

    /**
     * SAML Assertion
     */
    SAML_ASSERTION("SAMLAssertion"),

    /**
     * OAuth2TechnicalUserPropagation
     */
    OAUTH2_TECHNICAL_USER_PROPAGATION("OAuth2TechnicalUserPropagation"),

    /**
     * SDK specific authentication type to forward a given current Authentication Token directly to the destination
     */
    TOKEN_FORWARDING("TokenForwarding");

    @Getter
    private final String identifier;

    @Override
    public String toString()
    {
        return identifier;
    }

    /**
     * Returns the {@code AuthenticationType} which equals the given identifier.
     *
     * @param identifier
     *            The identifier to get a {@code AuthenticationType} for.
     *
     * @return The matching {@code AuthenticationType}.
     *
     * @throws IllegalArgumentException
     *             If the given identifier does not map to a {@code AuthenticationType}.
     */
    @Nonnull
    public static AuthenticationType ofIdentifier( @Nonnull final String identifier )
        throws IllegalArgumentException
    {
        for( final AuthenticationType authenticationType : values() ) {
            if( authenticationType.getIdentifier().equals(identifier) ) {
                return authenticationType;
            }
        }

        throw new IllegalArgumentException(
            "Unknown " + AuthenticationType.class.getSimpleName() + ": " + identifier + ".");
    }

    /**
     * Returns the {@code AuthenticationType} which equals the given identifier, or the
     * {@code defaultAuthenticationType} in case there is none.
     *
     * @param identifier
     *            The identifier to get a {@code AuthenticationType} for.
     * @param defaultAuthenticationType
     *            The {@code AuthenticationType} to return if no matching {@code AuthenticationType} could be found.
     *
     * @return The matching {@code AuthenticationType} or the default, if none is matching.
     */
    @Nonnull
    public static AuthenticationType ofIdentifierOrDefault(
        @Nullable final String identifier,
        @Nonnull final AuthenticationType defaultAuthenticationType )
    {
        if( identifier == null ) {
            return defaultAuthenticationType;
        }

        try {
            return ofIdentifier(identifier);
        }
        catch( final IllegalArgumentException e ) {
            if( log.isWarnEnabled() ) {
                log
                    .warn(
                        AuthenticationType.class.getSimpleName()
                            + " '"
                            + identifier
                            + "' is not supported. Falling back to "
                            + defaultAuthenticationType
                            + ".");
            }
            return defaultAuthenticationType;
        }
    }
}
