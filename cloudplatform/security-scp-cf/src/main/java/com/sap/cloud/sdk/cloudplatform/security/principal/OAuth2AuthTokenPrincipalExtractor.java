package com.sap.cloud.sdk.cloudplatform.security.principal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Payload;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.security.principal.exception.PrincipalAccessException;

import io.vavr.CheckedFunction1;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * OAuth2 protocol to extract principal from current auth token.
 */
@SuppressWarnings( "deprecation" )
@Slf4j
class OAuth2AuthTokenPrincipalExtractor implements PrincipalExtractor
{
    private static final String JWT_CLIENT_ID_CLAIM = "client_id";
    private static final String JWT_USER_NAME_CLAIM = "user_name";

    private static final String JWT_USER_ATTRIBUTES = "xs.user.attributes";

    private static final String JWT_GRANT_TYPE_CLAIM = "grant_type";
    private static final String JWT_GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";

    private static final String JWT_AUDIENCE_CLAIM = "aud";

    private final LocalScopePrefixExtractor localScopePrefixExtractor;
    private final Map<String, CheckedFunction1<DecodedJWT, String>> grantTypeToPrincipalIdExtractor = new HashMap<>();

    OAuth2AuthTokenPrincipalExtractor( @Nullable final LocalScopePrefixProvider localScopePrefixProvider )
    {
        localScopePrefixExtractor = new LocalScopePrefixExtractor(localScopePrefixProvider);

        grantTypeToPrincipalIdExtractor.put("password", jwt -> jwt.getClaim(JWT_USER_NAME_CLAIM).asString());
        grantTypeToPrincipalIdExtractor.put("client_credentials", jwt -> jwt.getClaim(JWT_CLIENT_ID_CLAIM).asString());
        grantTypeToPrincipalIdExtractor.put("authorization_code", jwt -> jwt.getClaim(JWT_USER_NAME_CLAIM).asString());
        grantTypeToPrincipalIdExtractor.put("user_token", jwt -> jwt.getClaim(JWT_USER_NAME_CLAIM).asString());
        grantTypeToPrincipalIdExtractor
            .put("urn:ietf:params:oauth:grant-type:saml2-bearer", jwt -> jwt.getClaim(JWT_USER_NAME_CLAIM).asString());
        grantTypeToPrincipalIdExtractor
            .put("urn:ietf:params:oauth:grant-type:jwt-bearer", jwt -> jwt.getClaim(JWT_USER_NAME_CLAIM).asString());
    }

    void setIdExtractorFunction(
        @Nonnull final String grantType,
        @Nonnull final CheckedFunction1<DecodedJWT, String> principalIdExtractor )
    {
        final CheckedFunction1<DecodedJWT, String> previousExtractorFunction =
            grantTypeToPrincipalIdExtractor.put(grantType, principalIdExtractor);

        if( log.isDebugEnabled() ) {
            if( previousExtractorFunction != null ) {
                log.debug("Replaced the logic for grant type '" + grantType + "' with a new one.");
            } else {
                log.debug("Added initial logic for grant type '" + grantType + "'.");
            }
        }
    }

    @Nonnull
    private Try<String> getPrincipalId( @Nonnull final DecodedJWT jwt )
    {
        return Try.of(() -> {
            final Claim grantTypeClaim = jwt.getClaim(JWT_GRANT_TYPE_CLAIM);
            if( grantTypeClaim.isMissing() || grantTypeClaim.isNull() ) {
                throw new PrincipalAccessException("The current JWT does not contain any grant type.");
            }

            if( log.isDebugEnabled() ) {
                if( grantTypeToPrincipalIdExtractor.isEmpty() ) {
                    log
                        .debug(
                            "There is no logic registered for any grant type, so no principal will get extracted from the JWT.");
                } else {
                    log
                        .debug(
                            "To extract a principal from JWT the following grant types will get handled: {}",
                            grantTypeToPrincipalIdExtractor.keySet());
                }
            }

            final String grantType = grantTypeClaim.asString();
            final CheckedFunction1<DecodedJWT, String> idSupplier = grantTypeToPrincipalIdExtractor.get(grantType);

            if( idSupplier == null ) {
                throw new PrincipalAccessException("There is no reader registered for grant type '" + grantType + "'.");
            }

            final String principalId;
            try {
                principalId = idSupplier.apply(jwt);
            }
            // The vavr CheckedFunction throws a throwable, so we need to catch/handle it
            catch( final Throwable t ) { // ALLOW CATCH THROWABLE
                throw new PrincipalAccessException("Could not read id for grant type " + grantType + " from JWT.", t);
            }

            if( principalId == null ) {
                throw new PrincipalAccessException(
                    "The principalId for grant type " + grantType + " must not be null.");
            }

            log.debug("Extracted principal '{}' from the current JWT.", principalId);
            return principalId;
        });
    }

    @Nonnull
    private Try<Map<String, PrincipalAttribute>> getAttributes( @Nonnull final Payload jwt )
    {
        return Try.of(() -> {
            final Map<String, PrincipalAttribute> attributes = new HashMap<>();

            @Nullable
            final Map<String, Object> attributesMap;

            try {
                attributesMap = jwt.getClaim(JWT_USER_ATTRIBUTES).asMap();
            }
            catch( final JWTDecodeException e ) {
                throw new PrincipalAccessException("Failed to get user attributes.", e);
            }

            if( attributesMap != null ) {
                @Nullable
                final String grantType = jwt.getClaim(JWT_GRANT_TYPE_CLAIM).asString();

                if( JWT_GRANT_TYPE_CLIENT_CREDENTIALS.equals(grantType) ) {
                    throw new PrincipalAccessException(
                        "Retrieving '"
                            + JWT_USER_ATTRIBUTES
                            + "' is not supported for grant type "
                            + JWT_GRANT_TYPE_CLIENT_CREDENTIALS
                            + ".");
                }

                for( final Map.Entry<String, Object> entry : attributesMap.entrySet() ) {
                    if( !(entry.getValue() instanceof Iterable) ) {
                        throw new PrincipalAccessException(
                            "Failed to get user attributes: value of attribute map entry is not an instance of Iterable.");
                    }

                    final Iterable<?> iterable = (Iterable<?>) entry.getValue();
                    final List<String> values = new ArrayList<>();

                    for( final Object value : iterable ) {
                        if( !(value instanceof String) ) {
                            throw new PrincipalAccessException("Failed to get user attributes: value is not a String.");
                        }
                        values.add((String) value);
                    }

                    final String name = entry.getKey();
                    attributes.put(name, new StringCollectionPrincipalAttribute(name, values));
                }
            } else {
                log
                    .debug(
                        "Skipping reading of user attributes: cannot find field '{}' in authorization token.",
                        JWT_USER_ATTRIBUTES);
            }

            return attributes;
        });
    }

    @Nonnull
    private Try<Set<com.sap.cloud.sdk.cloudplatform.security.Authorization>> getLocalAuthorizations(
        @Nonnull final Payload jwt )
    {
        return localScopePrefixExtractor.getAuthorizations(jwt);
    }

    private static Try<Set<com.sap.cloud.sdk.cloudplatform.security.Audience>> getAudiences(
        @Nonnull final Payload jwt )
    {
        return Try.of(() -> extractAudiences(jwt));
    }

    @Nonnull
    private static Set<com.sap.cloud.sdk.cloudplatform.security.Audience> extractAudiences( @Nonnull final Payload jwt )
    {
        final Claim claim = jwt.getClaim(JWT_AUDIENCE_CLAIM);

        @Nullable
        final List<String> audiences = claim.asList(String.class);
        if( audiences != null ) {
            return audiences
                .stream()
                .map(com.sap.cloud.sdk.cloudplatform.security.Audience::new)
                .collect(Collectors.toSet());
        }

        @Nullable
        final String audience = claim.asString();
        if( audience != null ) {
            return Collections.singleton(new com.sap.cloud.sdk.cloudplatform.security.Audience(audience));
        }

        throw new IllegalArgumentException("Could not find audiences in the JWT.");
    }

    private static Try<Set<com.sap.cloud.sdk.cloudplatform.security.Authorization>> getAllAuthorizations(
        @Nonnull final Payload jwt )
    {
        return Try
            .of(
                () -> Option
                    .of(jwt.getClaim(LocalScopePrefixExtractor.JWT_SCOPE_CLAIM).asList(String.class))
                    .getOrElseThrow(() -> new IllegalArgumentException("JWT does not contain any scopes."))
                    .stream()
                    .map(com.sap.cloud.sdk.cloudplatform.security.Authorization::new)
                    .collect(Collectors.toSet()));
    }

    @Override
    @Nonnull
    public Try<Principal> tryGetCurrentPrincipal()
    {
        final Try<DecodedJWT> jwtTry = AuthTokenAccessor.tryGetCurrentToken().map(AuthToken::getJwt);

        if( jwtTry.isFailure() ) {
            return Try.failure(jwtTry.getCause());
        }

        final DecodedJWT jwt = jwtTry.get();

        final Try<String> principalId = getPrincipalId(jwt);

        if( principalId.isFailure() ) {
            return Try.failure(principalId.getCause());
        }

        final Set<com.sap.cloud.sdk.cloudplatform.security.Authorization> localAuthorizations =
            getLocalAuthorizations(jwt).getOrElse(Collections::emptySet);
        final Map<String, PrincipalAttribute> attributes = getAttributes(jwt).getOrElse(Collections::emptyMap);

        final Try<Set<com.sap.cloud.sdk.cloudplatform.security.Authorization>> allAuthorizationsTry =
            getAllAuthorizations(jwt).onFailure(cause -> log.error(cause.getMessage(), cause));
        final Try<Set<com.sap.cloud.sdk.cloudplatform.security.Audience>> audiencesTry =
            getAudiences(jwt).onFailure(cause -> log.error(cause.getMessage(), cause));

        return Try
            .of(
                () -> new ScpCfPrincipal(
                    principalId.get(),
                    localAuthorizations,
                    allAuthorizationsTry.getOrElse(Collections::emptySet),
                    audiencesTry.getOrElse(Collections::emptySet),
                    attributes));
    }
}
