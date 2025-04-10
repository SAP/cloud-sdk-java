package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.sap.cloud.sdk.cloudplatform.security.BasicCredentials;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

import io.vavr.control.Option;
import lombok.experimental.Accessors;
import lombok.experimental.Delegate;

/**
 * Immutable implementation of the {@link HttpDestination} interface.
 */
public class TransparentProxyDestination implements HttpDestination
{
    private static final String TRANSPARENT_PROXY_GATEWAY = "http://destination-gateway:80";
    private static final String DESTINATION_NAME_HEADER_KEY = "x-destination-name";
    private static final String FRAGMENT_NAME_HEADER_KEY = "x-fragment-name";
    private static final String TENANT_SUBDOMAIN_HEADER_KEY = "x-tenant-subdomain";
    private static final String TENANT_ID_HEADER_KEY = "x-tenant-id";
    private static final String FRAGMENT_OPTIONAL_HEADER_KEY = "x-fragment-optional";
    private static final String TOKEN_SERVICE_TENANT_HEADER_KEY = "x-token-service-tenant";
    private static final String CLIENT_ASSERTION_HEADER_KEY = "x-client-assertion";
    private static final String CLIENT_ASSERTION_TYPE_HEADER_KEY = "x-client-assertion-type";
    private static final String CLIENT_ASSERTION_DESTINATION_NAME_HEADER_KEY = "x-client-assertion-destination-name";
    private static final String AUTHORIZATION_HEADER_KEY = "authorization";
    private static final String SUBJECT_TOKEN_TYPE_HEADER_KEY = "x-subject-token-type";
    private static final String ACTOR_TOKEN_HEADER_KEY = "x-actor-token";
    private static final String ACTOR_TOKEN_TYPE_HEADER_KEY = "x-actor-token-type";
    private static final String REDIRECT_URI_HEADER_KEY = "x-redirect-uri";
    private static final String CODE_VERIFIER_HEADER_KEY = "x-code-verifier";
    private static final String CHAIN_NAME_HEADER_KEY = "x-chain-name";
    private static final String CHAIN_VAR_SUBJECT_TOKEN_HEADER_KEY = "x-chain-var-subjectToken";
    private static final String CHAIN_VAR_SUBJECT_TOKEN_TYPE_HEADER_KEY = "x-chain-var-subjectTokenType";
    private static final String CHAIN_VAR_SAML_PROVIDER_DESTINATION_NAME_HEADER_KEY =
        "x-chain-var-samlProviderDestinationName";

    @Delegate
    private final DestinationProperties baseProperties;

    @Nonnull
    final ImmutableList<Header> customHeaders;

    // the following 'cached' fields are ALWAYS derived from the baseProperties and stored in the corresponding fields
    // to avoid additional computation at runtime ONLY.
    // this is why we are calling them 'cached'.
    // since these values are ALWAYS derived from the provided baseProperties, we can safely assume that their values
    // are constant over the lifetime of this destination.
    // in other words: caching the values is safe and will not lead to any inconsistencies.
    // furthermore, it is safe to exclude these fields from the equals and hashCode methods because their values are
    // purely derived from the baseProperties, which are included in the equals and hashCode methods.
    @Nonnull
    private final Option<ProxyConfiguration> cachedProxyConfiguration;

    private TransparentProxyDestination(
        @Nonnull final DestinationProperties baseProperties,
        @Nullable final List<Header> customHeaders,
        @Nonnull final ComplexDestinationPropertyFactory destinationPropertyFactory )
    {
        this.baseProperties = baseProperties;
        this.customHeaders =
            customHeaders != null ? ImmutableList.<Header> builder().addAll(customHeaders).build() : ImmutableList.of();

        cachedProxyConfiguration = destinationPropertyFactory.getProxyConfiguration(baseProperties);

    }

    @Nonnull
    @Override
    public URI getUri()
    {
        return URI.create(baseProperties.get(DestinationProperty.URI).get());
    }

    @Nonnull
    @Override
    public Collection<Header> getHeaders( @Nonnull URI requestUri )
    {
        return customHeaders;
    }

    @Nonnull
    @Override
    public Option<String> getTlsVersion()
    {
        return get(DestinationProperty.TLS_VERSION);
    }

    @Nonnull
    @Override
    public Option<ProxyConfiguration> getProxyConfiguration()
    {
        return cachedProxyConfiguration;
    }

    @Nullable
    @Override
    public Option<KeyStore> getKeyStore()
    {
        return null;
    }

    @Nullable
    @Override
    public Option<String> getKeyStorePassword()
    {
        return null;
    }

    @Override
    public boolean isTrustingAllCertificates()
    {
        return false;
    }

    @Nullable
    @Override
    public Option<BasicCredentials> getBasicCredentials()
    {
        return null;
    }

    @Nullable
    @Override
    public AuthenticationType getAuthenticationType()
    {
        return null;
    }

    @Nonnull
    @Override
    public Option<ProxyType> getProxyType()
    {
        return Option.of(ProxyType.INTERNET);
    }

    @Nullable
    @Override
    public Option<KeyStore> getTrustStore()
    {
        return null;
    }

    @Nullable
    @Override
    public Option<String> getTrustStorePassword()
    {
        return null;
    }

    /**
     * Builder class to allow for easy creation of an immutable {@code TransparentProxyDestination} instance.
     */
    @Accessors( fluent = true, chain = true )
    public static class Builder
    {
        final List<Header> headers = Lists.newArrayList();

        final DefaultDestination.Builder builder = DefaultDestination.builder();

        final List<DestinationHeaderProvider> customHeaderProviders = new ArrayList<>();

        /**
         * Adds the given key-value pair to the destination to be created. This will overwrite any property already
         * assigned to the key.
         *
         * @param key
         *            The key to assign a property for.
         * @param value
         *            The property value to be assigned.
         * @return This builder.
         * @since 5.0.0
         */
        @Nonnull
        public Builder property( @Nonnull final String key, @Nonnull final Object value )
        {
            builder.property(key, value);
            return this;
        }

        /**
         * Adds the given key-value pair to the destination to be created. This will overwrite any property already
         * assigned to the key.
         *
         * @param key
         *            The {@link DestinationPropertyKey} to assign a property for.
         * @param value
         *            The property value to be assigned.
         * @param <ValueT>
         *            The type of the property value.
         * @return This builder.
         * @since 5.0.0
         */
        @Nonnull
        public <
            ValueT> Builder property( @Nonnull final DestinationPropertyKey<ValueT> key, @Nonnull final ValueT value )
        {
            return property(key.getKeyName(), value);
        }

        @Nonnull
        <ValueT> Option<ValueT> get( @Nonnull final DestinationPropertyKey<ValueT> key )
        {
            return builder.get(key);
        }

        @Nonnull
        <ValueT> Option<ValueT> get( @Nonnull final String key, @Nonnull final Function<Object, ValueT> conversion )
        {
            return builder.get(key, conversion);
        }

        /**
         * Removes the property with the given key from the destination to be created. This is useful when creating a
         * builder from an existing destination and wanting to remove a property.
         *
         * @param key
         *            The {@link DestinationPropertyKey} of the property to remove.
         * @return This builder.
         */
        @Nonnull
        public Builder removeProperty( @Nonnull final DestinationPropertyKey<?> key )
        {
            builder.removeProperty(key);
            return this;
        }

        /**
         * Removes the property with the given key from the destination to be created. This is useful when creating a
         * builder from an existing destination and wanting to remove a property.
         *
         * @param key
         *            The key of the property to remove.
         * @return This builder.
         */
        @Nonnull
        public Builder removeProperty( @Nonnull final String key )
        {
            builder.removeProperty(key);
            return this;
        }

        /**
         * Adds the given headers to the list of headers added to every outgoing request for this destination.
         *
         * @param headers
         *            Headers to add to outgoing requests.
         * @return This builder.
         */
        @Nonnull
        public Builder headers( @Nonnull final Collection<Header> headers )
        {
            this.headers.addAll(headers);
            return this;
        }

        /**
         * Adds the given header to the list of headers added to every outgoing request for this destination.
         *
         * @param header
         *            A header to add to outgoing requests.
         * @return This builder.
         */
        @Nonnull
        public Builder header( @Nonnull final Header header )
        {
            headers.add(header);
            return this;
        }

        /**
         * Adds a header given by the {@code headerName} and {@code headerValue} to the list of headers added to every
         * outgoing request for this destination.
         *
         * @param headerName
         *            The name of the header to add.
         * @param headerValue
         *            The value of the header to add.
         * @return This builder.
         */
        @Nonnull
        public Builder header( @Nonnull final String headerName, @Nonnull final String headerValue )
        {
            return header(new Header(headerName, headerValue));
        }

        /**
         * Sets the destination name header for the outgoing request.
         *
         * @param destinationName
         *            The name of the destination to set in the header.
         * @return This builder.
         */
        @Nonnull
        public Builder destinationName( @Nonnull final String destinationName )
        {
            return header(new Header(DESTINATION_NAME_HEADER_KEY, destinationName));
        }

        /**
         * Sets the fragment name header for the outgoing request.
         *
         * @param fragmentName
         *            The fragment name to set in the header.
         * @return This builder.
         */
        @Nonnull
        public Builder fragmentName( @Nonnull final String fragmentName )
        {
            return header(new Header(FRAGMENT_NAME_HEADER_KEY, fragmentName));
        }

        /**
         * Sets the tenant subdomain header for the outgoing request.
         *
         * @param tenantSubdomain
         *            The tenant subdomain to set in the header.
         * @return This builder.
         */
        @Nonnull
        public Builder tenantSubdomain( @Nonnull final String tenantSubdomain )
        {
            return header(new Header(TENANT_SUBDOMAIN_HEADER_KEY, tenantSubdomain));
        }

        /**
         * Sets the tenant ID header for the outgoing request.
         *
         * @param tenantId
         *            The tenant ID to set in the header.
         * @return This builder.
         */
        @Nonnull
        public Builder tenantId( @Nonnull final String tenantId )
        {
            return header(new Header(TENANT_ID_HEADER_KEY, tenantId));
        }

        /**
         * Sets the fragment optional header for the outgoing request.
         *
         * @param fragmentOptional
         *            The fragment optional value to set in the header.
         * @return This builder.
         */
        @Nonnull
        public Builder fragmentOptional( @Nonnull final String fragmentOptional )
        {
            return header(new Header(FRAGMENT_OPTIONAL_HEADER_KEY, fragmentOptional));
        }

        /**
         * Sets the token service tenant header for the outgoing request.
         *
         * @param tokenServiceTenant
         *            The token service tenant to set in the header.
         * @return This builder.
         */
        @Nonnull
        public Builder tokenServiceTenant( @Nonnull final String tokenServiceTenant )
        {
            return header(new Header(TOKEN_SERVICE_TENANT_HEADER_KEY, tokenServiceTenant));
        }

        /**
         * Sets the client assertion header for the outgoing request.
         *
         * @param clientAssertion
         *            The client assertion to set in the header.
         * @return This builder.
         */
        @Nonnull
        public Builder clientAssertion( @Nonnull final String clientAssertion )
        {
            return header(new Header(CLIENT_ASSERTION_HEADER_KEY, clientAssertion));
        }

        /**
         * Sets the client assertion type header for the outgoing request.
         *
         * @param clientAssertionType
         *            The client assertion type to set in the header.
         * @return This builder.
         */
        @Nonnull
        public Builder clientAssertionType( @Nonnull final String clientAssertionType )
        {
            return header(new Header(CLIENT_ASSERTION_TYPE_HEADER_KEY, clientAssertionType));
        }

        /**
         * Sets the client assertion destination name header for the outgoing request.
         *
         * @param clientAssertionDestinationName
         *            The client assertion destination name to set in the header.
         * @return This builder.
         */
        @Nonnull
        public Builder clientAssertionDestinationName( @Nonnull final String clientAssertionDestinationName )
        {
            return header(new Header(CLIENT_ASSERTION_DESTINATION_NAME_HEADER_KEY, clientAssertionDestinationName));
        }

        /**
         * Sets the authorization header for the outgoing request.
         *
         * @param authorization
         *            The authorization value to set in the header.
         * @return This builder.
         */
        @Nonnull
        public Builder authorization( @Nonnull final String authorization )
        {
            return header(new Header(AUTHORIZATION_HEADER_KEY, authorization));
        }

        /**
         * Sets the subject token type header for the outgoing request.
         *
         * @param subjectTokenType
         *            The subject token type to set in the header.
         * @return This builder.
         */
        @Nonnull
        public Builder subjectTokenType( @Nonnull final String subjectTokenType )
        {
            return header(new Header(SUBJECT_TOKEN_TYPE_HEADER_KEY, subjectTokenType));
        }

        /**
         * Sets the actor token header for the outgoing request.
         *
         * @param actorToken
         *            The actor token to set in the header.
         * @return This builder.
         */
        @Nonnull
        public Builder actorToken( @Nonnull final String actorToken )
        {
            return header(new Header(ACTOR_TOKEN_HEADER_KEY, actorToken));
        }

        /**
         * Sets the actor token type header for the outgoing request.
         *
         * @param actorTokenType
         *            The actor token type to set in the header.
         * @return This builder.
         */
        @Nonnull
        public Builder actorTokenType( @Nonnull final String actorTokenType )
        {
            return header(new Header(ACTOR_TOKEN_TYPE_HEADER_KEY, actorTokenType));
        }

        /**
         * Sets the redirect URI header for the outgoing request.
         *
         * @param redirectUri
         *            The redirect URI to set in the header.
         * @return This builder.
         */
        @Nonnull
        public Builder redirectUri( @Nonnull final String redirectUri )
        {
            return header(new Header(REDIRECT_URI_HEADER_KEY, redirectUri));
        }

        /**
         * Sets the code verifier header for the outgoing request.
         *
         * @param codeVerifier
         *            The code verifier to set in the header.
         * @return This builder.
         */
        @Nonnull
        public Builder codeVerifier( @Nonnull final String codeVerifier )
        {
            return header(new Header(CODE_VERIFIER_HEADER_KEY, codeVerifier));
        }

        /**
         * Sets the chain name header for the outgoing request.
         *
         * @param chainName
         *            The chain name to set in the header.
         * @return This builder.
         */
        @Nonnull
        public Builder chainName( @Nonnull final String chainName )
        {
            return header(new Header(CHAIN_NAME_HEADER_KEY, chainName));
        }

        /**
         * Sets the chain variable subject token header for the outgoing request.
         *
         * @param subjectToken
         *            The subject token to set in the header.
         * @return This builder.
         */
        @Nonnull
        public Builder chainVarSubjectToken( @Nonnull final String subjectToken )
        {
            return header(new Header(CHAIN_VAR_SUBJECT_TOKEN_HEADER_KEY, subjectToken));
        }

        /**
         * Sets the chain variable subject token type header for the outgoing request.
         *
         * @param subjectTokenType
         *            The subject token type to set in the header.
         * @return This builder.
         */
        @Nonnull
        public Builder chainVarSubjectTokenType( @Nonnull final String subjectTokenType )
        {
            return header(new Header(CHAIN_VAR_SUBJECT_TOKEN_TYPE_HEADER_KEY, subjectTokenType));
        }

        /**
         * Sets the chain variable SAML provider destination name header for the outgoing request.
         *
         * @param samlProviderDestinationName
         *            The SAML provider destination name to set in the header.
         * @return This builder.
         */
        @Nonnull
        public Builder chainVarSamlProviderDestinationName( @Nonnull final String samlProviderDestinationName )
        {
            return header(new Header(CHAIN_VAR_SAML_PROVIDER_DESTINATION_NAME_HEADER_KEY, samlProviderDestinationName));
        }

        /**
         * Registers the provided {@link DestinationHeaderProvider} instances on this Destination.
         * <p>
         * For all outgoing requests, the registered header providers are invoked and the returned {@link Header
         * headers} are added to the request.
         *
         * @param headerProviders
         *            The header provider instances
         * @return This builder
         */
        @Nonnull
        public Builder headerProviders( @Nonnull final DestinationHeaderProvider... headerProviders )
        {
            customHeaderProviders.addAll(Arrays.asList(headerProviders));
            return this;
        }

        boolean containsHeader( final String headerName )
        {
            return headers.stream().anyMatch(h -> h.getName().equalsIgnoreCase(headerName));
        }

        /**
         * Finally creates the {@code TransparentProxyDestination} with the properties retrieved via the
         * {@link #property(String, Object)} method.
         *
         * @return A fully instantiated {@code TransparentProxyDestination}.
         */
        @Nonnull
        public TransparentProxyDestination build()
        {
            if( !builder.get(DestinationProperty.URI).isDefined() ) {
                this.property(DestinationProperty.URI, TRANSPARENT_PROXY_GATEWAY);
            }

            TenantAccessor.tryGetCurrentTenant().onSuccess(tenant -> {
                if( !containsHeader(TENANT_ID_HEADER_KEY) && !tenant.getTenantId().isEmpty() ) {
                    headers.add(new Header(TENANT_ID_HEADER_KEY, tenant.getTenantId()));
                }

                final DefaultTenant defaultTenant = (DefaultTenant) tenant;
                if( !containsHeader(TENANT_SUBDOMAIN_HEADER_KEY)
                    && !containsHeader(TENANT_ID_HEADER_KEY)
                    && defaultTenant.getSubdomain() != null
                    && !defaultTenant.getSubdomain().isEmpty() ) {
                    headers.add(new Header(TENANT_SUBDOMAIN_HEADER_KEY, defaultTenant.getSubdomain()));
                }
            });

            return buildInternal();
        }

        TransparentProxyDestination buildInternal()
        {
            return new TransparentProxyDestination(builder.build(), headers, new ComplexDestinationPropertyFactory());
        }
    }
}
