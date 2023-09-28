package com.sap.cloud.sdk.cloudplatform.security.principal;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.auth0.jwt.interfaces.Payload;
import com.google.common.collect.Sets;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * This class contains the logic to extract the values of the {@code scope} claim of a JWT and returns it as a
 * {@code Set} of {@code Authorization}s.
 *
 * @deprecated To be removed without replacement. Please refer to release notes for more information.
 */
@Slf4j
@Deprecated
public class LocalScopePrefixExtractor
{
    static final String JWT_SCOPE_CLAIM = "scope";

    @Nonnull
    private final LocalScopePrefixProvider localScopePrefixProvider;

    /**
     * Creates a new instance with the given {@code LocalScopePrefixProvider} which provides the prefix of the scopes to
     * be used.
     *
     * @param localScopePrefixProvider
     *            The provider for the prefix of relevant scopes.
     */
    public LocalScopePrefixExtractor( @Nullable final LocalScopePrefixProvider localScopePrefixProvider )
    {
        this.localScopePrefixProvider =
            localScopePrefixProvider != null ? localScopePrefixProvider : new DefaultLocalScopePrefixProvider();
    }

    /**
     * Extracts the authorizations from the given JWT.
     * <p>
     * For that the method takes all scope entries, removes all without the prefix given in the constructor and removes
     * the prefix afterwards.
     *
     * @param jwt
     *            The JWT to extract the scopes from
     *
     * @return A {@link Try} with a set of all scopes as {@code Authorization} objects.
     */
    @Nonnull
    public Try<Set<com.sap.cloud.sdk.cloudplatform.security.Authorization>> getAuthorizations(
        @Nonnull final Payload jwt )
    {
        return Try.of(() -> {
            final Set<com.sap.cloud.sdk.cloudplatform.security.Authorization> authorizations = Sets.newHashSet();

            @Nullable
            final List<String> scopeNames = jwt.getClaim(JWT_SCOPE_CLAIM).asList(String.class);

            @Nullable
            final String localScopePrefixWithDot =
                localScopePrefixProvider
                    .getLocalScopePrefix()
                    .map(prefix -> prefix.endsWith(".") ? prefix : prefix + ".")
                    .getOrNull();

            if( scopeNames != null ) {
                for( final String scopeName : scopeNames ) {
                    if( localScopePrefixWithDot != null && scopeName.startsWith(localScopePrefixWithDot) ) {
                        log.debug("Adding local scope '{}'.", scopeName);
                        authorizations
                            .add(
                                new com.sap.cloud.sdk.cloudplatform.security.Scope(
                                    scopeName.substring(localScopePrefixWithDot.length())));
                    } else {
                        log.debug("Skipping non-local scope '{}'.", scopeName);
                    }
                }
            } else {
                log
                    .debug(
                        "Skipping reading of user authorizations: cannot find field '{}' in authorization token.",
                        JWT_SCOPE_CLAIM);
            }

            return authorizations;
        });
    }
}
