package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.collect.ImmutableList;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.security.BasicCredentials;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.sdk.cloudplatform.util.FacadeLocator;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

/**
 * Immutable implementation of the {@link HttpDestination} interface.
 * https://help.sap.com/docs/connectivity/sap-btp-connectivity-cf/transparent-proxy-for-kubernetes
 */
@Slf4j
public class TransparentProxyDestination implements HttpDestination
{
    static final String DESTINATION_NAME_HEADER_KEY = "x-destination-name";
    static final String FRAGMENT_NAME_HEADER_KEY = "x-fragment-name";
    static final String TENANT_SUBDOMAIN_HEADER_KEY = "x-tenant-subdomain";
    static final String TENANT_ID_HEADER_KEY = "x-tenant-id";
    static final String FRAGMENT_OPTIONAL_HEADER_KEY = "x-fragment-optional";
    static final String TOKEN_SERVICE_TENANT_HEADER_KEY = "x-token-service-tenant";
    static final String CLIENT_ASSERTION_HEADER_KEY = "x-client-assertion";
    static final String CLIENT_ASSERTION_TYPE_HEADER_KEY = "x-client-assertion-type";
    static final String CLIENT_ASSERTION_DESTINATION_NAME_HEADER_KEY = "x-client-assertion-destination-name";
    static final String AUTHORIZATION_HEADER_KEY = "authorization";
    static final String SUBJECT_TOKEN_TYPE_HEADER_KEY = "x-subject-token-type";
    static final String ACTOR_TOKEN_HEADER_KEY = "x-actor-token";
    static final String ACTOR_TOKEN_TYPE_HEADER_KEY = "x-actor-token-type";
    static final String REDIRECT_URI_HEADER_KEY = "x-redirect-uri";
    static final String CODE_VERIFIER_HEADER_KEY = "x-code-verifier";
    static final String CHAIN_NAME_HEADER_KEY = "x-chain-name";
    static final String CHAIN_VAR_SUBJECT_TOKEN_HEADER_KEY = "x-chain-var-subjectToken";
    static final String CHAIN_VAR_SUBJECT_TOKEN_TYPE_HEADER_KEY = "x-chain-var-subjectTokenType";
    static final String CHAIN_VAR_SAML_PROVIDER_DESTINATION_NAME_HEADER_KEY = "x-chain-var-samlProviderDestinationName";
    static final String TENANT_ID_AND_TENANT_SUBDOMAIN_BOTH_PASSED_ERROR_MESSAGE =
        "Tenant id and tenant subdomain cannot be passed at the same time.";

    @Delegate
    private final DestinationProperties baseProperties;

    @Nonnull
    final ImmutableList<Header> customHeaders;

    @Nonnull
    @Getter( AccessLevel.PACKAGE )
    private final ImmutableList<DestinationHeaderProvider> customHeaderProviders;

    @Nonnull
    private final ImmutableList<DestinationHeaderProvider> headerProvidersFromClassLoading;

    private TransparentProxyDestination(
        @Nonnull final DestinationProperties baseProperties,
        @Nullable final List<Header> customHeaders,
        @Nullable final List<DestinationHeaderProvider> customHeaderProviders )
    {
        this.baseProperties = baseProperties;
        this.customHeaders =
            customHeaders != null ? ImmutableList.<Header> builder().addAll(customHeaders).build() : ImmutableList.of();

        final Collection<DestinationHeaderProvider> headerProvidersFromClassLoading =
            FacadeLocator.getFacades(DestinationHeaderProvider.class);
        this.headerProvidersFromClassLoading =
            ImmutableList.<DestinationHeaderProvider> builder().addAll(headerProvidersFromClassLoading).build();

        this.customHeaderProviders =
            customHeaderProviders != null
                ? ImmutableList.<DestinationHeaderProvider> builder().addAll(customHeaderProviders).build()
                : ImmutableList.of();

    }

    @Nonnull
    @Override
    public URI getUri()
    {
        return URI.create(baseProperties.get(DestinationProperty.URI).get());
    }

    @Nonnull
    @Override
    public Collection<Header> getHeaders( @Nonnull final URI requestUri )
    {
        final Collection<Header> allHeaders = new ArrayList<>();

        allHeaders.addAll(customHeaders);
        allHeaders
            .addAll(
                DefaultHttpDestination
                    .getHeadersFromHeaderProviders(
                        this,
                        requestUri,
                        customHeaderProviders,
                        headerProvidersFromClassLoading));

        // Automatically add tenant id if not already present
        TenantAccessor.tryGetCurrentTenant().onSuccess(tenant -> {
            if( !containsHeader(allHeaders, TENANT_ID_HEADER_KEY)
                && !containsHeader(allHeaders, TENANT_SUBDOMAIN_HEADER_KEY) ) {
                allHeaders.add(new Header(TENANT_ID_HEADER_KEY, tenant.getTenantId()));
            }
        });

        AuthTokenAccessor.tryGetCurrentToken().onSuccess(token -> {
            if( !containsHeader(allHeaders, AUTHORIZATION_HEADER_KEY) ) {
                allHeaders.add(new Header(AUTHORIZATION_HEADER_KEY, token.getJwt().getToken()));
            }
        });

        return allHeaders;
    }

    static boolean containsHeader( final Collection<Header> headers, final String headerName )
    {
        return headers.stream().anyMatch(h -> h.getName().equalsIgnoreCase(headerName));
    }

    @Nonnull
    @Override
    public Option<String> getTlsVersion()
    {
        return Option.none();
    }

    @Nonnull
    @Override
    public Option<ProxyConfiguration> getProxyConfiguration()
    {
        return Option.none();
    }

    @Nonnull
    @Override
    public Option<KeyStore> getKeyStore()
    {
        return Option.none();
    }

    @Nonnull
    @Override
    public Option<String> getKeyStorePassword()
    {
        return Option.none();
    }

    @Override
    public boolean isTrustingAllCertificates()
    {
        return false;
    }

    @Nonnull
    @Override
    public Option<BasicCredentials> getBasicCredentials()
    {
        return Option.none();
    }

    @Nonnull
    @Override
    public AuthenticationType getAuthenticationType()
    {
        return AuthenticationType.NO_AUTHENTICATION;
    }

    @Nonnull
    @Override
    public Option<ProxyType> getProxyType()
    {
        return Option.none();
    }

    @Nonnull
    @Override
    public Option<KeyStore> getTrustStore()
    {
        return Option.none();
    }

    @Nonnull
    @Override
    public Option<String> getTrustStorePassword()
    {
        return Option.none();
    }

    @Override
    public boolean equals( @Nullable final Object o )
    {
        if( this == o ) {
            return true;
        }

        if( o == null || getClass() != o.getClass() ) {
            return false;
        }

        final TransparentProxyDestination that = (TransparentProxyDestination) o;
        return new EqualsBuilder()
            .append(baseProperties, that.baseProperties)
            .append(customHeaders, that.customHeaders)
            .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37).append(baseProperties).append(customHeaders).toHashCode();
    }

    /**
     * Creates a new builder for a "static" destination.
     * <p>
     * A static destination connects directly to a specified URL and does not use the destination-gateway. It allows
     * setting generic headers but does not support gateway-specific properties like destination name or fragments.
     *
     * @return A new {@link StaticBuilder} instance.
     */
    @Nonnull
    public static StaticBuilder staticDestination( @Nonnull final String uri )
    {
        return new StaticBuilder(uri);
    }

    /**
     * Creates a new builder for a "dynamic" destination that is resolved via the destination-gateway.
     * <p>
     * A dynamic destination requires a destination name and will be routed through the central destination-gateway. It
     * supports all gateway-specific properties like fragments, tenant context, and authentication flows.
     *
     * @param destinationName
     *            The name of the destination to be resolved by the gateway.
     * @return A new {@link DynamicBuilder} instance.
     */
    @Nonnull
    public static DynamicBuilder dynamicDestination( @Nonnull final String destinationName, @Nonnull final String uri )
    {
        return new DynamicBuilder(destinationName, uri);
    }

    /**
     * Abstract base class for builders to share common functionality like adding headers and properties.
     *
     * @param <B>
     *            The type of the builder subclass, used for fluent method chaining.
     */
    @Accessors( fluent = true, chain = true )
    public abstract static class AbstractBuilder<B extends AbstractBuilder<B>>
    {
        final List<Header> headers = new ArrayList<>();
        final List<DestinationHeaderProvider> customHeaderProviders = new ArrayList<>();
        final DefaultDestination.Builder propertiesBuilder = DefaultDestination.builder();

        /**
         * Returns the current builder instance.
         * <p>
         * This method is used to support fluent method chaining in subclasses of {@code AbstractBuilder}.
         *
         * @return the current builder instance
         */
        protected abstract B getThis();

        /**
         * Adds the given key-value pair to the destination to be created. This will overwrite any property already
         * assigned to the key.
         *
         * @param key
         *            The key to assign a property for.
         * @param value
         *            The property value to be assigned.
         * @return This builder.
         * @since 5.22.0
         */
        @Nonnull
        public B property( @Nonnull final String key, @Nonnull final Object value )
        {
            propertiesBuilder.property(key, value);
            return getThis();
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
         * @since 5.22.0
         */
        @Nonnull
        public <ValueT> B property( @Nonnull final DestinationPropertyKey<ValueT> key, @Nonnull final ValueT value )
        {
            return property(key.getKeyName(), value);
        }

        /**
         * Adds the given headers to the list of headers added to every outgoing request for this destination.
         *
         * @param headers
         *            Headers to add to outgoing requests.
         * @return This builder.
         */
        @Nonnull
        public B headers( @Nonnull final Collection<Header> headers )
        {
            this.headers.addAll(headers);
            return getThis();
        }

        /**
         * Adds the given header to the list of headers added to every outgoing request for this destination.
         *
         * @param header
         *            A header to add to outgoing requests.
         * @return This builder.
         */
        @Nonnull
        public B header( @Nonnull final Header header )
        {
            this.headers.add(header);
            return getThis();
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
        public B header( @Nonnull final String headerName, @Nonnull final String headerValue )
        {
            return header(new Header(headerName, headerValue));
        }

        /**
         * Adds a tenant subdomain header to the destination. See
         * https://help.sap.com/docs/connectivity/sap-btp-connectivity-cf/multitenancy
         * <p>
         * Note: Tenant subdomain and tenant ID cannot be set at the same time. Calling this method when a tenant ID
         * header is already present will throw an exception.
         *
         * @param tenantSubdomain
         *            The tenant subdomain value.
         * @return This builder instance for method chaining.
         * @throws IllegalStateException
         *             if tenant ID header is already set
         */
        @Nonnull
        public B tenantSubdomain( @Nonnull final String tenantSubdomain )
        {
            if( containsHeader(headers, TENANT_ID_HEADER_KEY) ) {
                throw new IllegalStateException(TENANT_ID_AND_TENANT_SUBDOMAIN_BOTH_PASSED_ERROR_MESSAGE);
            }

            return header(new Header(TENANT_SUBDOMAIN_HEADER_KEY, tenantSubdomain));
        }

        /**
         * Adds a tenant ID header to the destination. Set automatically by the Cloud SDK per-request, if both tenant id
         * and tenant subdomain are left unset. See
         * https://help.sap.com/docs/connectivity/sap-btp-connectivity-cf/multitenancy
         * <p>
         * Note: Tenant subdomain and tenant ID cannot be set at the same time. Calling this method when a tenant ID
         * header is already present will throw an exception.
         *
         * @param tenantId
         *            The tenant ID value.
         * @return This builder instance for method chaining.
         * @throws IllegalStateException
         *             if tenant ID header is already set
         */
        @Nonnull
        public B tenantId( @Nonnull final String tenantId )
        {
            if( containsHeader(headers, TENANT_SUBDOMAIN_HEADER_KEY) ) {
                throw new IllegalStateException(TENANT_ID_AND_TENANT_SUBDOMAIN_BOTH_PASSED_ERROR_MESSAGE);
            }
            return header(new Header(TENANT_ID_HEADER_KEY, tenantId));
        }

        /**
         * Adds a token service tenant header to the destination. Is send to the destination service as x-tenant header
         * and should be used when tokenServiceURLType in the destination service is common. See
         * https://help.sap.com/docs/connectivity/sap-btp-connectivity-cf/technical-user-propagation and
         * https://help.sap.com/docs/connectivity/sap-btp-connectivity-cf/oauth-client-credentials-authentication-cf15900ca39242fb87a1fb081a54b9ca
         *
         * @param tokenServiceTenant
         *            The token service tenant value.
         * @return This builder instance for method chaining.
         */
        @Nonnull
        public B tokenServiceTenant( @Nonnull final String tokenServiceTenant )
        {
            return header(new Header(TOKEN_SERVICE_TENANT_HEADER_KEY, tokenServiceTenant));
        }

        /**
         * Adds a client assertion header to the destination. See
         * https://help.sap.com/docs/connectivity/sap-btp-connectivity-cf/provide-client-assertion-properties-as-headers
         *
         * @param clientAssertion
         *            The client assertion value.
         * @return This builder instance for method chaining.
         */
        @Nonnull
        public B clientAssertion( @Nonnull final String clientAssertion )
        {
            return header(new Header(CLIENT_ASSERTION_HEADER_KEY, clientAssertion));
        }

        /**
         * Adds a client assertion type header to the destination. See
         * https://help.sap.com/docs/connectivity/sap-btp-connectivity-cf/provide-client-assertion-properties-as-headers
         *
         * @param clientAssertionType
         *            The client assertion type value.
         * @return This builder instance for method chaining.
         */
        @Nonnull
        public B clientAssertionType( @Nonnull final String clientAssertionType )
        {
            return header(new Header(CLIENT_ASSERTION_TYPE_HEADER_KEY, clientAssertionType));
        }

        /**
         * Adds a client assertion destination name header to the destination. See
         * https://help.sap.com/docs/connectivity/sap-btp-connectivity-cf/client-assertion-with-automated-assertion-fetching-by-service
         *
         * @param clientAssertionDestinationName
         *            The client assertion destination name value.
         * @return This builder instance for method chaining.
         */
        @Nonnull
        public B clientAssertionDestinationName( @Nonnull final String clientAssertionDestinationName )
        {
            return header(new Header(CLIENT_ASSERTION_DESTINATION_NAME_HEADER_KEY, clientAssertionDestinationName));
        }

        /**
         * Adds an authorization header to the destination. Will be used for OAuth 2.0 token exchange and principal
         * propagation by the transparent proxy. Will be sent to the destination service as x-user-token, x-code,
         * x-refresh-token, x-subject-token or x-chain-var-subjectToken header depending on the destination
         * authentication type. Set automatically by the Cloud SDK per-request, if unset here.
         *
         * @param authorization
         *            The authorization value.
         * @return This builder instance for method chaining.
         */
        @Nonnull
        public B authorization( @Nonnull final String authorization )
        {
            return header(new Header(AUTHORIZATION_HEADER_KEY, authorization));
        }

        /**
         * Adds a subject token type header to the destination. See
         * https://help.sap.com/docs/connectivity/sap-btp-connectivity-cf/oauth-token-exchange-authentication-8813df7e39e5472ca5bdcdd34598592d
         *
         * @param subjectTokenType
         *            The subject token type value.
         * @return This builder instance for method chaining.
         */
        @Nonnull
        public B subjectTokenType( @Nonnull final String subjectTokenType )
        {
            return header(new Header(SUBJECT_TOKEN_TYPE_HEADER_KEY, subjectTokenType));
        }

        /**
         * Adds an actor token header to the destination. See
         * https://help.sap.com/docs/connectivity/sap-btp-connectivity-cf/oauth-token-exchange-authentication-8813df7e39e5472ca5bdcdd34598592d
         *
         * @param actorToken
         *            The actor token value.
         * @return This builder instance for method chaining.
         */
        @Nonnull
        public B actorToken( @Nonnull final String actorToken )
        {
            return header(new Header(ACTOR_TOKEN_HEADER_KEY, actorToken));
        }

        /**
         * Adds an actor token type header to the destination. See
         * https://help.sap.com/docs/connectivity/sap-btp-connectivity-cf/oauth-token-exchange-authentication-8813df7e39e5472ca5bdcdd34598592d
         *
         * @param actorTokenType
         *            The actor token type value.
         * @return This builder instance for method chaining.
         */
        @Nonnull
        public B actorTokenType( @Nonnull final String actorTokenType )
        {
            return header(new Header(ACTOR_TOKEN_TYPE_HEADER_KEY, actorTokenType));
        }

        /**
         * Adds a redirect URI header to the destination. See
         * https://help.sap.com/docs/connectivity/sap-btp-connectivity-cf/oauth-authorization-code-authentication-7bdfed49c6d0451b8aafe1c94da8c770
         *
         * @param redirectUri
         *            The redirect URI value.
         * @return This builder instance for method chaining.
         */
        @Nonnull
        public B redirectUri( @Nonnull final String redirectUri )
        {
            return header(new Header(REDIRECT_URI_HEADER_KEY, redirectUri));
        }

        /**
         * Adds a code verifier header to the destination. See
         * https://help.sap.com/docs/connectivity/sap-btp-connectivity-cf/oauth-authorization-code-authentication-7bdfed49c6d0451b8aafe1c94da8c770
         *
         * @param codeVerifier
         *            The code verifier value.
         * @return This builder instance for method chaining.
         */
        @Nonnull
        public B codeVerifier( @Nonnull final String codeVerifier )
        {
            return header(new Header(CODE_VERIFIER_HEADER_KEY, codeVerifier));
        }

        /**
         * Sets the chain name header for the destination. See
         * https://help.sap.com/docs/connectivity/sap-btp-connectivity-cf/ias-signed-saml-bearer-assertion
         *
         * @param chainName
         *            The name of the chain.
         * @return This builder instance for method chaining.
         */
        @Nonnull
        public B chainName( @Nonnull final String chainName )
        {
            return header(new Header(CHAIN_NAME_HEADER_KEY, chainName));
        }

        /**
         * Sets the chain variable subject token header for the destination. See
         * https://help.sap.com/docs/connectivity/sap-btp-connectivity-cf/ias-signed-saml-bearer-assertion
         *
         * @param subjectToken
         *            The subject token value.
         * @return This builder instance for method chaining.
         */
        @Nonnull
        public B chainVarSubjectToken( @Nonnull final String subjectToken )
        {
            return header(new Header(CHAIN_VAR_SUBJECT_TOKEN_HEADER_KEY, subjectToken));
        }

        /**
         * Sets the chain variable subject token type header for the destination. See
         * https://help.sap.com/docs/connectivity/sap-btp-connectivity-cf/ias-signed-saml-bearer-assertion
         *
         * @param subjectTokenType
         *            The subject token type value.
         * @return This builder instance for method chaining.
         */
        @Nonnull
        public B chainVarSubjectTokenType( @Nonnull final String subjectTokenType )
        {
            return header(new Header(CHAIN_VAR_SUBJECT_TOKEN_TYPE_HEADER_KEY, subjectTokenType));
        }

        /**
         * Sets the chain variable SAML provider destination name header for the destination. See
         * https://help.sap.com/docs/connectivity/sap-btp-connectivity-cf/ias-signed-saml-bearer-assertion
         *
         * @param samlProviderDestinationName
         *            The SAML provider destination name.
         * @return This builder instance for method chaining.
         */
        @Nonnull
        public B chainVarSamlProviderDestinationName( @Nonnull final String samlProviderDestinationName )
        {
            return header(new Header(CHAIN_VAR_SAML_PROVIDER_DESTINATION_NAME_HEADER_KEY, samlProviderDestinationName));
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
            return new TransparentProxyDestination(propertiesBuilder.build(), headers, customHeaderProviders);
        }
    }

    /**
     * Builder for creating a "static" {@link TransparentProxyDestination}. See
     * https://help.sap.com/docs/connectivity/sap-btp-connectivity-cf/destination-custom-resource
     */
    public static final class StaticBuilder extends AbstractBuilder<StaticBuilder>
    {
        private StaticBuilder( @Nonnull final String uri )
        {
            property(DestinationProperty.URI, uri);
        }

        @Override
        protected StaticBuilder getThis()
        {
            return this;
        }
    }

    /**
     * Builder for creating a "dynamic" {@link TransparentProxyDestination}. See
     * https://help.sap.com/docs/connectivity/sap-btp-connectivity-cf/dynamic-lookup-of-destinations
     */
    public static final class DynamicBuilder extends AbstractBuilder<DynamicBuilder>
    {
        private DynamicBuilder( @Nonnull final String destinationName, @Nonnull final String uri )
        {
            if( destinationName.isEmpty() ) {
                throw new IllegalArgumentException(
                    "The 'destinationName' property is required for dynamic destinations but was not set.");
            }

            this.header(DESTINATION_NAME_HEADER_KEY, destinationName);
            property(DestinationProperty.URI, uri);
        }

        @Override
        protected DynamicBuilder getThis()
        {
            return this;
        }

        /**
         * Sets the fragment name for the dynamic destination. See
         * https://help.sap.com/docs/connectivity/sap-btp-connectivity-cf/dynamic-lookup-of-destinations
         *
         * @param fragmentName
         *            The name of the fragment to use.
         * @return This builder instance for method chaining.
         */
        @Nonnull
        public DynamicBuilder fragmentName( @Nonnull final String fragmentName )
        {
            return header(new Header(FRAGMENT_NAME_HEADER_KEY, fragmentName));
        }

        /**
         * Sets the fragment optional flag for the dynamic destination. See
         * https://help.sap.com/docs/connectivity/sap-btp-connectivity-cf/dynamic-lookup-of-destinations
         *
         * @param fragmentOptional
         *            The value indicating if the fragment is optional.
         * @return This builder instance for method chaining.
         */
        @Nonnull
        public DynamicBuilder fragmentOptional( final boolean fragmentOptional )
        {
            return header(new Header(FRAGMENT_OPTIONAL_HEADER_KEY, Boolean.toString(fragmentOptional)));
        }
    }
}
