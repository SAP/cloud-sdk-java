package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.security.KeyStore;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.connectivity.ServiceBindingDestinationOptions.OptionsEnhancer;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration.TimeLimiterConfiguration;
import com.sap.cloud.security.annotation.Beta;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents various configuration parameters for the OAuth2 destination creation.
 *
 * @since 5.5.0
 */
@AllArgsConstructor( access = AccessLevel.PRIVATE )
@EqualsAndHashCode
public final class OAuth2Options
{
    /**
     * The default timeout of 10 seconds for token retrieval.
     *
     * @since 5.12.0
     */
    public static final TimeLimiterConfiguration DEFAULT_TIMEOUT = TimeLimiterConfiguration.of(Duration.ofSeconds(10));

    /**
     * Default token cache configuration used by {@link OAuth2Service}. Effective defaults: 10 minutes duration, 1000
     * entries, 30 seconds delta and cache statistics disabled.
     *
     * @see com.sap.cloud.security.xsuaa.tokenflows.TokenCacheConfiguration#DEFAULT
     * @since 5.21.0
     */
    public static final TokenCacheParameters DEFAULT_TOKEN_CACHE_PARAMETERS =
        TokenCacheParameters.of(Duration.ofMinutes(10), 1000, Duration.ofSeconds(30));

    /**
     * The default {@link OAuth2Options} instance that does not alter the token retrieval process and does not use mTLS
     * for the target system connection.
     */
    public static final OAuth2Options DEFAULT =
        new OAuth2Options(false, Map.of(), DEFAULT_TIMEOUT, null, DEFAULT_TOKEN_CACHE_PARAMETERS);

    private final boolean skipTokenRetrieval;
    @Nonnull
    private final Map<String, String> additionalTokenRetrievalParameters;
    /**
     * A timeout to be applied for token retrieval.
     *
     * @since 5.12.0
     */
    @Nonnull
    @Getter
    private final TimeLimiterConfiguration timeLimiter;
    /**
     * The {@link KeyStore} to use for building an mTLS connection towards the <b>target system</b>. This
     * {@link KeyStore} <b>is not used</b> to build an mTLS connection towards the OAuth2 token service.
     */
    @Nullable
    @Getter
    private final KeyStore clientKeyStore;

    /**
     * Configuration for caching OAuth2 tokens.
     *
     * @since 5.21.0
     */
    @Nonnull
    @Getter
    private final TokenCacheParameters tokenCacheParameters;

    /**
     * Indicates whether to skip the OAuth2 token flow.
     *
     * @return {@code true} if the token retrieval should be <b>skipped</b>, {@code false} otherwise.
     */
    public boolean skipTokenRetrieval()
    {
        return skipTokenRetrieval;
    }

    /**
     * Returns additional parameters for the OAuth2 token flow. These parameters will be put into the URL-form-encoded
     * body <b>in addition</b> to the regular authentication information (such as the <i>clientid</i>).
     *
     * @return Additional parameters for the OAuth2 token request.
     */
    @Nonnull
    public Map<String, String> getAdditionalTokenRetrievalParameters()
    {
        return new HashMap<>(additionalTokenRetrievalParameters);
    }

    /**
     * Returns a new {@link Builder} instance that can be used to create a customized {@link OAuth2Options} instance.
     *
     * @return A new {@link Builder}.
     */
    @Nonnull
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * A builder implementation that helps with creating customized {@link OAuth2Options} instances.
     */
    @Slf4j
    public static class Builder
    {
        private boolean skipTokenRetrieval = false;
        private final Map<String, String> additionalTokenRetrievalParameters = new HashMap<>();
        private KeyStore clientKeyStore;
        private TimeLimiterConfiguration timeLimiter = DEFAULT_TIMEOUT;
        private TokenCacheParameters tokenCacheParameters = DEFAULT_TOKEN_CACHE_PARAMETERS;

        /**
         * Indicates whether to skip the OAuth2 token flow.
         *
         * @param skipTokenRetrieval
         *            {@code true} if the token retrieval should be <b>skipped</b>, {@code false} otherwise.
         * @return This {@link Builder}.
         */
        @Nonnull
        public Builder withSkipTokenRetrieval( final boolean skipTokenRetrieval )
        {
            this.skipTokenRetrieval = skipTokenRetrieval;
            return this;
        }

        /**
         * Adds (or overwrites) the provided key-value-pair to the {@link #getAdditionalTokenRetrievalParameters()} of
         * the to-be-created {@link OAuth2Options} instance.
         *
         * @param key
         *            The parameter key.
         * @param value
         *            The parameter value.
         * @return This {@link Builder}.
         */
        @Nonnull
        public Builder withTokenRetrievalParameter( @Nonnull final String key, @Nonnull final String value )
        {
            additionalTokenRetrievalParameters.put(key, value);
            return this;
        }

        /**
         * Adds (or overwrites) the provided parameters to the {@link #getAdditionalTokenRetrievalParameters()} of the
         * to-be-created {@link OAuth2Options} instance.
         *
         * @param parameters
         *            The parameters to add.
         * @return This {@link Builder}.
         */
        @Nonnull
        public Builder withTokenRetrievalParameters( @Nonnull final Map<String, String> parameters )
        {
            additionalTokenRetrievalParameters.putAll(parameters);
            return this;
        }

        /**
         * Sets the {@link KeyStore} to use for building an mTLS connection towards the <b>target system</b>. This
         * {@link KeyStore} <b>is not used</b> to build an mTLS connection towards the OAuth2 token service.
         *
         * @param clientKeyStore
         *            The {@link KeyStore} to use for building an mTLS connection towards the <b>target system</b>.
         * @return This {@link Builder}.
         */
        @Nonnull
        public Builder withClientKeyStore( @Nonnull final KeyStore clientKeyStore )
        {
            this.clientKeyStore = clientKeyStore;
            return this;
        }

        /**
         * Set a custom timeout for token retrieval. {@link #DEFAULT_TIMEOUT} by default.
         *
         * @param timeLimiter
         *            The custom timeout configuration.
         * @return This {@link Builder}.
         * @since 5.12.0
         */
        @Nonnull
        public Builder withTimeLimiter( @Nonnull final TimeLimiterConfiguration timeLimiter )
        {
            this.timeLimiter = timeLimiter;
            return this;
        }

        /**
         * Set a custom token cache configuration. {@link #DEFAULT_TOKEN_CACHE_PARAMETERS} by default.
         *
         * @param tokenCacheParameters
         *            The custom token cache parameters.
         * @return This {@link Builder}.
         * @since 5.21.0
         */
        @Nonnull
        public Builder withTokenCacheParameters( @Nonnull final TokenCacheParameters tokenCacheParameters )
        {
            this.tokenCacheParameters = tokenCacheParameters;
            return this;
        }

        /**
         * Creates a new {@link OAuth2Options} instance.
         *
         * @return A new {@link OAuth2Options} instance.
         */
        @Nonnull
        public OAuth2Options build()
        {
            if( skipTokenRetrieval && !additionalTokenRetrievalParameters.isEmpty() ) {
                log.warn("""
                    {} have been configured to skip the OAuth2 token retrieval, \
                    but there are also additional token request parameters. \
                    As there will be no token retrieved, the additional parameters are ignored.
                    """, OAuth2Options.class);
            }

            return new OAuth2Options(
                skipTokenRetrieval,
                new HashMap<>(additionalTokenRetrievalParameters),
                timeLimiter,
                clientKeyStore,
                tokenCacheParameters);
        }
    }

    /**
     * Configure the timeout applied to token retrieval.
     *
     * @since 5.12.0
     */
    @Getter
    @RequiredArgsConstructor( staticName = "of" )
    public static class TokenRetrievalTimeout implements OptionsEnhancer<TimeLimiterConfiguration>
    {
        @Nonnull
        private final TimeLimiterConfiguration value;
    }

    /**
     * Configuration for the token <em>response</em> cache used by {@link OAuth2Service}.
     *
     * <p>
     * <strong>Important:</strong> These values are passed to
     * {@link com.sap.cloud.security.xsuaa.tokenflows.TokenCacheConfiguration} used by XSUAAs
     * {@code DefaultOAuth2TokenService}. This cache stores the HTTP token response (including the token) and it governs
     * the cache entry, <em>not</em> the token's lifetime.
     *
     * <p>
     * Expired (or almost expired) tokens are never served, regardless of {@link #cacheDuration} as xsuaa checks
     * <code>exp - {@link #tokenExpirationDelta}</code> before returning a cached entry.
     *
     * @since 5.21.0
     */
    @Beta
    @Getter
    @RequiredArgsConstructor( staticName = "of" )
    public static class TokenCacheParameters implements OptionsEnhancer<TokenCacheParameters>
    {
        /**
         * Upper bound for how long a successful token response may remain cached. A cached entry is ignored earlier if
         * the token would be (almost) expired.
         */
        @Nonnull
        private final Duration cacheDuration;
        /**
         * The maximum number of tokens to cache.
         */
        @Nonnull
        private final Integer cacheSize;
        /**
         * The delta to be subtracted from the token expiration time to determine how early should a token be refreshed
         * before it expires.
         */
        @Nonnull
        private final Duration tokenExpirationDelta;

        @Override
        @Nonnull
        public TokenCacheParameters getValue()
        {
            return this;
        }
    }

}
