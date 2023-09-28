package com.sap.cloud.sdk.cloudplatform.security.principal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.sap.cloud.sdk.cloudplatform.security.principal.exception.PrincipalAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextAccessor;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextPropertyNotFoundException;

import io.vavr.CheckedFunction1;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * Facade providing access to {@code Principal} information on SAP Business Technology Platform Cloud Foundry.
 *
 * @deprecated Please use {@code DefaultPrincipalFacade} instead.
 */
@Deprecated
@Slf4j
public class ScpCfPrincipalFacade extends DefaultPrincipalFacade
{
    private final Collection<PrincipalExtractor> principalExtractors = new ArrayList<>();
    private static final String PRINCIPAL = PrincipalThreadContextListener.PROPERTY_PRINCIPAL;

    ScpCfPrincipalFacade(
        @Nonnull final PrincipalExtractor principalExtractor,
        @Nonnull final PrincipalExtractor... additionalPrincipalExtractors )
    {
        principalExtractors.add(principalExtractor);
        principalExtractors.addAll(Arrays.asList(additionalPrincipalExtractors));
    }

    /**
     * Creates a new instance of this facade with the given prefix provider.
     *
     * @param localScopePrefixProvider
     *            The prefix provider to be used to remove prefixes from authorizations.
     */
    public ScpCfPrincipalFacade( @Nullable final LocalScopePrefixProvider localScopePrefixProvider )
    {
        this(
            new OAuth2AuthTokenPrincipalExtractor(localScopePrefixProvider),
            new OidcAuthTokenPrincipalExtractor(),
            new BasicCredentialsPrincipalExtractor());
    }

    /**
     * Default constructor with default strategies.
     */
    public ScpCfPrincipalFacade()
    {
        this(null);
    }

    /**
     * This allows to set/override the way the {@link Principal#getPrincipalId()} is obtained, given a
     * {@code grant_type} in the {@code AuthToken}.
     *
     * @param grantType
     *            The grant type to use the extractor for.
     * @param principalIdExtractor
     *            The logic to obtain an id for the {@code Principal} from the {@code AuthToken}.
     */
    public void setIdExtractorFunction(
        @Nonnull final String grantType,
        @Nonnull final CheckedFunction1<DecodedJWT, String> principalIdExtractor )
    {
        principalExtractors
            .stream()
            .filter(extractor -> extractor instanceof OAuth2AuthTokenPrincipalExtractor)
            .map(extractor -> (OAuth2AuthTokenPrincipalExtractor) extractor)
            .forEach(extractor -> extractor.setIdExtractorFunction(grantType, principalIdExtractor));
    }

    @Nonnull
    @Override
    public Try<Principal> tryGetCurrentPrincipal()
    {
        @Nullable
        final ThreadContext currentContext = ThreadContextAccessor.getCurrentContextOrNull();
        if( currentContext != null && currentContext.containsProperty(PRINCIPAL) ) {
            return currentContext.getPropertyValue(PRINCIPAL);
        }

        final List<Throwable> throwables = new ArrayList<>();
        throwables.add(new ThreadContextPropertyNotFoundException(PRINCIPAL));

        return principalExtractors
            .stream()
            .map(PrincipalExtractor::tryGetCurrentPrincipal)
            .filter(principalTry -> principalTry.onFailure(throwables::add).isSuccess())
            .findFirst()
            .orElseGet(() -> createFallbackException(throwables));
    }

    private Try<Principal> createFallbackException( @Nonnull final List<? extends Throwable> throwables )
    {
        final PrincipalAccessException resultingException =
            new PrincipalAccessException(
                "Could not read a principal from neither a given JWT nor a given Basic Authentication header.");

        throwables.forEach(resultingException::addSuppressed);

        return Try.failure(resultingException);
    }
}
