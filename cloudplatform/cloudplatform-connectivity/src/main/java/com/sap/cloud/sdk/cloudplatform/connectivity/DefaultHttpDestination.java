/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationKeyStoreComparator.resolveCertificatesOnly;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationKeyStoreComparator.resolveKeyStoreHashCode;

import java.net.URI;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.net.HttpHeaders;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderAccessor;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderContainer;
import com.sap.cloud.sdk.cloudplatform.security.BasicAuthHeaderEncoder;
import com.sap.cloud.sdk.cloudplatform.security.BasicCredentials;
import com.sap.cloud.sdk.cloudplatform.security.BearerCredentials;
import com.sap.cloud.sdk.cloudplatform.security.Credentials;
import com.sap.cloud.sdk.cloudplatform.security.NoCredentials;
import com.sap.cloud.sdk.cloudplatform.util.FacadeLocator;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

/**
 * Immutable default implementation of the {@link HttpDestination} interface.
 */
@Slf4j
public final class DefaultHttpDestination implements HttpDestination
{
    @Delegate
    private final DestinationProperties baseProperties;

    @Nullable
    private final KeyStore keyStore;

    @Nullable
    private final KeyStore trustStore;

    @Nonnull
    final ImmutableList<Header> customHeaders;

    @Nonnull
    @Getter( AccessLevel.PACKAGE )
    private final ImmutableList<DestinationHeaderProvider> customHeaderProviders;

    @Nonnull
    private final ImmutableList<DestinationHeaderProvider> headerProvidersFromClassLoading;

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

    @Nonnull
    private final Option<ProxyType> cachedProxyType;

    @Nonnull
    private final Option<BasicCredentials> cachedBasicCredentials;

    @Nonnull
    private final AuthenticationType cachedAuthenticationType;

    @Nonnull
    private final ImmutableList<Header> cachedHeadersFromProperties;

    @Nonnull
    private final ImmutableList<Header> cachedProxyAuthorizationHeaders;

    private DefaultHttpDestination(
        @Nonnull final DestinationProperties baseProperties,
        @Nonnull final ComplexDestinationPropertyFactory destinationPropertyFactory,
        @Nullable final List<Header> customHeaders,
        @Nullable final KeyStore keyStore,
        @Nullable final KeyStore trustStore,
        @Nullable final List<DestinationHeaderProvider> customHeaderProviders )
    {
        if( !canBeConstructedFrom(baseProperties) ) {
            throw new IllegalArgumentException(
                baseProperties.getClass().getSimpleName()
                    + " is misconfigured. URL doesn't exist or is not a valid URI.");
        }

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

        this.keyStore = keyStore;
        this.trustStore = trustStore;

        cachedProxyConfiguration = destinationPropertyFactory.getProxyConfiguration(baseProperties);
        cachedProxyType = destinationPropertyFactory.getProxyType(baseProperties);
        cachedBasicCredentials = destinationPropertyFactory.getBasicCredentials(baseProperties);
        cachedAuthenticationType =
            destinationPropertyFactory.getAuthenticationType(baseProperties, cachedBasicCredentials);
        cachedHeadersFromProperties =
            ImmutableList
                .<Header> builder()
                .addAll(destinationPropertyFactory.getHeadersFromProperties(baseProperties))
                .build();
        cachedProxyAuthorizationHeaders =
            ImmutableList
                .<Header> builder()
                .addAll(destinationPropertyFactory.getProxyAuthorizationHeaders(cachedProxyConfiguration))
                .build();
    }

    /**
     * Verifies that the given "generic" destination might be decorated into a {@code DefaultHttpDestination}.
     *
     * @param destination
     *            The destination to check.
     * @return {@code true}, if the given destination is decoratable as a {@code DefaultHttpDestination}; {@code false}
     *         otherwise.
     */
    static boolean canBeConstructedFrom( @Nonnull final DestinationProperties destination )
    {
        final Option<String> uri = destination.get(DestinationProperty.URI);

        if( uri.isEmpty() ) {
            return false;
        }

        return Try.of(() -> new URI(uri.get())).isSuccess();
    }

    @Nonnull
    @Override
    public Collection<Header> getHeaders( @Nonnull final URI requestUri )
    {
        final Collection<Header> allHeaders = new ArrayList<>();

        allHeaders.addAll(customHeaders);
        allHeaders.addAll(getHeadersFromHeaderProviders(requestUri));
        allHeaders.addAll(cachedHeadersFromProperties);
        if( allHeaders.stream().noneMatch(header -> header.getName().equalsIgnoreCase(HttpHeaders.AUTHORIZATION)) ) {
            allHeaders.addAll(getHeadersForAuthType());
        }
        if( allHeaders.stream().map(Header::getName).noneMatch(HttpHeaders.PROXY_AUTHORIZATION::equalsIgnoreCase) ) {
            allHeaders.addAll(cachedProxyAuthorizationHeaders);
        }
        return allHeaders;
    }

    private List<Header> getHeadersFromHeaderProviders( @Nonnull final URI requestUri )
    {
        final var allHeaderProviders = new ArrayList<DestinationHeaderProvider>();
        allHeaderProviders.addAll(customHeaderProviders);
        allHeaderProviders.addAll(headerProvidersFromClassLoading);

        final var refinedHeaderProviders = refineHeaderProviders(allHeaderProviders);
        final var msg = "Retained {} of {} found destination header providers: {}";
        log.debug(msg, refinedHeaderProviders.size(), allHeaderProviders.size(), refinedHeaderProviders);

        final var requestContext = new DestinationRequestContext(this, requestUri);

        final var result = new ArrayList<Header>();
        for( final DestinationHeaderProvider headerProvider : refinedHeaderProviders ) {
            try {
                result.addAll(headerProvider.getHeaders(requestContext));
            }
            catch( final Exception e ) {
                final var name = headerProvider.getClass().getSimpleName();
                final var err = "Header provider '%s' threw an exception: %s".formatted(name, e.getMessage());
                throw new DestinationAccessException(err, e);
            }
        }
        return result;
    }

    @Nonnull
    static <T extends DestinationHeaderProvider> List<T> refineHeaderProviders( @Nonnull final Iterable<T> providers )
    {
        final var result = new LinkedList<T>();
        final var retain = new AtomicInteger();
        for( final T provider : providers ) {
            result.add(provider);
            if( provider.getCardinality() != Integer.MAX_VALUE ) {
                retain.set(provider.getCardinality());
                final var iter = result.descendingIterator();
                while( iter.hasNext() ) {
                    if( iter.next().getClass().isInstance(provider) && retain.decrementAndGet() < 0 ) {
                        iter.remove();
                    }
                }
            }
        }
        return result;
    }

    private Collection<Header> getHeadersForAuthType()
    {
        final Collection<Header> headers = new LinkedList<>();
        switch( getAuthenticationType() ) {
            case BASIC_AUTHENTICATION:
                if( getBasicCredentials().isDefined() ) {
                    final String base64Auth =
                        BasicAuthHeaderEncoder.encodeUserPasswordBase64(getBasicCredentials().get());
                    headers.add(new Header(HttpHeaders.AUTHORIZATION, "Basic " + base64Auth));
                } else {
                    throw new DestinationAccessException(
                        "Failed to add '"
                            + HttpHeaders.AUTHORIZATION
                            + "' header for Basic authentication: no credentials available.");
                }
                break;

            case TOKEN_FORWARDING:
                final List<Header> headersToAdd =
                    RequestHeaderAccessor
                        .tryGetHeaderContainer()
                        .getOrElse(RequestHeaderContainer.EMPTY)
                        .getHeaderValues(HttpHeaders.AUTHORIZATION)
                        .stream()
                        .filter(Objects::nonNull)
                        .map(value -> new Header(HttpHeaders.AUTHORIZATION, value))
                        .toList();

                if( headersToAdd.isEmpty() ) {
                    final String msg =
                        "Did not find any '{}' headers to add to the outgoing request, even though Authentication type '{}' is set.";
                    log.warn(msg, HttpHeaders.AUTHORIZATION, AuthenticationType.TOKEN_FORWARDING);

                    if( log.isDebugEnabled() ) {
                        final Try<RequestHeaderContainer> tryReqHeaders = RequestHeaderAccessor.tryGetHeaderContainer();
                        if( tryReqHeaders.isFailure() || tryReqHeaders.get() == null ) {
                            final String msgReq = "The incoming request headers could not be accessed.";
                            log.debug(msgReq, tryReqHeaders.isFailure() ? tryReqHeaders.getCause() : null);
                        } else {
                            final String msgHeaders = "Unable to find an '{}' header in the following headers: {}";
                            log.debug(msgHeaders, HttpHeaders.AUTHORIZATION, tryReqHeaders.get().getHeaderNames());
                        }
                    }
                }

                headers.addAll(headersToAdd);
                break;

            default:
                /* do nothing */
        }

        return headers;
    }

    @Nonnull
    @Override
    public URI getUri()
    {
        return URI.create(baseProperties.get(DestinationProperty.URI).get());
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

    @Nonnull
    @Override
    public Option<KeyStore> getKeyStore()
    {
        return Option.of(keyStore);
    }

    @Nonnull
    @Override
    public Option<String> getKeyStorePassword()
    {
        return baseProperties.get(DestinationProperty.KEY_STORE_PASSWORD);
    }

    @Override
    public boolean isTrustingAllCertificates()
    {
        return get(DestinationProperty.TRUST_ALL)
            .orElse(() -> get(DestinationProperty.TRUST_ALL_FALLBACK))
            .getOrElse(false);
    }

    @Nonnull
    @Override
    public Option<KeyStore> getTrustStore()
    {
        return Option.of(trustStore);
    }

    @Nonnull
    @Override
    public Option<String> getTrustStorePassword()
    {
        return baseProperties.get(DestinationProperty.TRUST_STORE_PASSWORD);
    }

    @Nonnull
    @Override
    public SecurityConfigurationStrategy getSecurityConfigurationStrategy()
    {
        return baseProperties
            .get(DestinationProperty.SECURITY_CONFIGURATION)
            .getOrElse(SecurityConfigurationStrategy::getDefault);
    }

    @Nonnull
    @Override
    public Option<BasicCredentials> getBasicCredentials()
    {
        return cachedBasicCredentials;
    }

    @Nonnull
    @Override
    public AuthenticationType getAuthenticationType()
    {
        return cachedAuthenticationType;
    }

    @Nonnull
    @Override
    public Option<ProxyType> getProxyType()
    {
        return cachedProxyType;
    }

    @Nonnull
    @Override
    public String toString()
    {
        // we don't know which properties exactly may contain sensitive information,
        // so we are only printing some essential properties known to be safe
        final StringBuilder sb = new StringBuilder();
        sb.append("DefaultHttpDestination(");
        baseProperties.get(DestinationProperty.NAME).peek(name -> sb.append("name=").append(name).append(", "));
        baseProperties.get(DestinationProperty.URI).peek(uri -> sb.append("uri=").append(uri).append(", "));
        baseProperties
            .get(DestinationProperty.PROXY_TYPE)
            .peek(proxyType -> sb.append("proxyType=").append(proxyType).append(", "));
        baseProperties
            .get(DestinationProperty.AUTH_TYPE)
            .peek(authType -> sb.append("authType=").append(authType).append(", "));
        sb.append("...)");
        return sb.toString();
    }

    /**
     * Returns a new {@link Builder} instance that is initialized with this {@code DefaultHttpDestination}.
     * <p>
     * Please note that this operation performs a <b>shallow copy only</b>. As a consequence, complex objects (such as
     * the {@link KeyStore}s) will be copied <b>by reference only</b>, which leads to a shared state between the
     * {@code destination} and the {@link DefaultHttpDestination} to be created.
     * </p>
     *
     * @return A new {@link Builder} instance.
     * @see Builder#fromDestination(Destination)
     * @since 5.0.0
     */
    @Nonnull
    public Builder toBuilder()
    {
        return fromDestination(this);
    }

    /**
     * Starts a builder to be used to create a {@code DefaultHttpDestination} with property: .
     *
     * @param uri
     *            The uri of the {@code DefaultHttpDestination} to be created. In case this is no valid URI an
     *            {@link IllegalArgumentException} will be thrown.
     * @return A new {@code Builder} instance.
     * @throws IllegalArgumentException
     *             if the given {@code uri} is no valid URI.
     */
    @Nonnull
    public static Builder builder( @Nonnull final String uri )
    {
        return new Builder().uri(uri);
    }

    /**
     * Starts a builder to be used to create a {@code DefaultHttpDestination} with some properties.
     *
     * @param uri
     *            The uri of the {@code DefaultHttpDestination} to be created.
     * @return A new {@code Builder} instance.
     */
    @Nonnull
    public static Builder builder( @Nonnull final URI uri )
    {
        return new Builder().uri(uri);
    }

    /**
     * Creates a new {@link DefaultHttpDestination} instance from the given map of properties.
     *
     * @param map
     *            The map of properties to create the builder from.
     * @return A new {@link Builder} instance.
     * @since 5.0.0
     */
    @Nonnull
    public static Builder fromMap( @Nonnull final Map<String, ?> map )
    {
        final Builder builder = new Builder();
        map.forEach(builder::property);

        return builder;
    }

    /**
     * Creates a new {@link DefaultHttpDestination} instance from the given {@link DestinationProperties} by copying all
     * properties returned by {@link DestinationProperties#getPropertyNames()}. In addition, if the given
     * {@code destination} is an instance of {@link DefaultHttpDestination}, all additional properties that are specific
     * to the {@link HttpDestinationProperties} instance will be copied as well. This especially also includes any
     * statically added {@link DestinationHeaderProvider}s and {@link Header}s attached to the {@code destination}.
     * <p>
     * Please note that this operation performs a <b>shallow copy only</b>. As a consequence, complex objects (such as
     * the {@link KeyStore}s) will be copied <b>by reference only</b>, which leads to a shared state between the
     * {@code destination} and the {@link DefaultHttpDestination} to be created.
     * </p>
     *
     * @param properties
     *            The {@link DestinationProperties} to create the builder from.
     * @return A new {@link Builder} instance.
     * @throws IllegalArgumentException
     *             if the provided {@code properties} are <b>not</b> an instance of either {@link DefaultDestination} or
     *             {@link DefaultHttpDestination}.
     * @since 5.0.0
     */
    @Nonnull
    public static Builder fromProperties( @Nonnull final DestinationProperties properties )
    {
        if( properties instanceof DefaultDestination || properties instanceof DefaultHttpDestination ) {
            return fromDestination((Destination) properties);
        }

        final Builder builder = new Builder();
        properties
            .getPropertyNames()
            .forEach(propertyName -> builder.property(propertyName, properties.get(propertyName).get()));

        return builder;
    }

    /**
     * Creates a new {@link DefaultHttpDestination} instance from the given {@link Destination} by copying all
     * properties returned by {@link Destination#getPropertyNames()}. In addition, if the given {@code destination} is
     * an instance of {@link DefaultHttpDestination}, all additional properties that are specific to the
     * {@link HttpDestinationProperties} instance will be copied as well. This especially also includes any statically
     * added {@link DestinationHeaderProvider}s and {@link Header}s attached to the {@code destination}.
     * <p>
     * Please note that this operation performs a <b>shallow copy only</b>. As a consequence, complex objects (such as
     * the {@link KeyStore}s) will be copied <b>by reference only</b>, which leads to a shared state between the
     * {@code destination} and the {@link DefaultHttpDestination} to be created.
     * </p>
     *
     * @param destination
     *            The {@link Destination} to create the builder from.
     * @return A new {@link Builder} instance.
     * @throws IllegalArgumentException
     *             if the provided {@code destination} is <b>not</b> an instance of either {@link DefaultDestination} or
     *             {@link DefaultHttpDestination}.
     * @since 5.0.0
     */
    @Nonnull
    public static Builder fromDestination( @Nonnull final Destination destination )
    {
        if( !(destination instanceof DefaultDestination) && !(destination instanceof DefaultHttpDestination) ) {
            throw new IllegalArgumentException(
                "The provided destination is not supported. Only DefaultDestination and DefaultHttpDestination are supported.");
        }

        final Builder builder = new Builder();
        destination
            .getPropertyNames()
            .forEach(propertyName -> builder.property(propertyName, destination.get(propertyName).get()));

        if( destination instanceof DefaultHttpDestination ) {
            final DefaultHttpDestination httpDestination = (DefaultHttpDestination) destination;
            builder.headers(httpDestination.customHeaders);
            builder
                .headerProviders(httpDestination.getCustomHeaderProviders().toArray(new DestinationHeaderProvider[0]));

            httpDestination.getKeyStore().map(builder::keyStore);
            httpDestination.getTrustStore().map(builder::trustStore);
        }

        return builder;
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

        final DefaultHttpDestination that = (DefaultHttpDestination) o;
        return new EqualsBuilder()
            .append(baseProperties, that.baseProperties)
            .append(customHeaders, that.customHeaders)
            .append(resolveCertificatesOnly(keyStore), resolveCertificatesOnly(that.keyStore))
            .append(resolveCertificatesOnly(trustStore), resolveCertificatesOnly(that.trustStore))
            .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
            .append(baseProperties)
            .append(customHeaders)
            .append(resolveKeyStoreHashCode(keyStore))
            .append(resolveKeyStoreHashCode(trustStore))
            .toHashCode();
    }

    /**
     * Builder class to allow for easy creation of an immutable {@code DefaultHttpDestination} instance.
     */
    public static class Builder
    {
        final List<Header> headers = Lists.newArrayList();
        final DefaultDestination.Builder builder = DefaultDestination.builder();
        final DefaultHttpDestinationBuilderProxyHandler proxyHandler = new DefaultHttpDestinationBuilderProxyHandler();
        KeyStore keyStore = null;
        KeyStore trustStore = null;
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
         * Sets the name of the {@code DefaultHttpDestination}.
         *
         * @param name
         *            The destination name
         * @return This builder.
         */
        @Nonnull
        public Builder name( @Nonnull final String name )
        {
            return property(DestinationProperty.NAME, name);
        }

        /**
         * Sets the URI of the to-be-built {@link DefaultHttpDestination}.
         *
         * @param uri
         *            The URI to set.
         * @return This builder.
         * @since 5.0.0
         */
        @Nonnull
        public Builder uri( @Nonnull final URI uri )
        {
            return uri(uri.toString());
        }

        /**
         * Sets the URI of the to-be-built {@link DefaultHttpDestination}.
         *
         * @param uri
         *            The URI to set.
         * @return This builder.
         * @since 5.0.0
         */
        @Nonnull
        public Builder uri( @Nonnull final String uri )
        {
            return property(DestinationProperty.URI, uri);
        }

        /**
         * Sets the TLS version used by the {@code DefaultHttpDestination} to the given value.
         *
         * @param value
         *            The TLS version that should be used.
         * @return This builder.
         */
        @Nonnull
        public Builder tlsVersion( @Nonnull final String value )
        {
            return property(DestinationProperty.TLS_VERSION, value);
        }

        /**
         * Sets the key store password for the corresponding {@link KeyStore} used by the {@link DefaultHttpDestination}
         * to access the key store.
         *
         * @param value
         *            The keyStore password that should be used.
         * @return This builder.
         */
        @Nonnull
        public Builder keyStorePassword( @Nonnull final String value )
        {
            return property(DestinationProperty.KEY_STORE_PASSWORD, value);
        }

        /**
         * Sets the {@link KeyStore} to be used when communicating over HTTP.
         *
         * @param keyStore
         *            The keyStore that should be used for HTTP communication
         * @return This builder.
         */
        @Nonnull
        public Builder keyStore( @Nonnull final KeyStore keyStore )
        {
            this.keyStore = keyStore;
            return this;
        }

        /**
         * Sets the Trust Store to be used when communicating over HTTP.
         *
         * @param trustStore
         *            The Trust Store that should be used. for HTTP communication
         * @return This builder.
         */
        @Nonnull
        public Builder trustStore( @Nonnull final KeyStore trustStore )
        {
            this.trustStore = trustStore;
            return this;
        }

        /**
         * Lets the {@code DefaultHttpDestination} trust all server certificates.
         *
         * @return This builder.
         */
        @Nonnull
        public Builder trustAllCertificates()
        {
            return property(DestinationProperty.TRUST_ALL, true);
        }

        /**
         * Sets the password of the trust store.
         *
         * @param value
         *            The trust store password that should be used.
         * @return This builder.
         */
        @Nonnull
        public Builder trustStorePassword( @Nonnull final String value )
        {
            return property(DestinationProperty.TRUST_STORE_PASSWORD, value);
        }

        /**
         * Sets the connectivity location id of the destination.
         *
         * @param locationId
         *            The location identifier for connecting to SAP Cloud Connector.
         * @return This builder.
         */
        @Nonnull
        public Builder cloudConnectorLocationId( @Nonnull final String locationId )
        {
            return property(DestinationProperty.CLOUD_CONNECTOR_LOCATION_ID, locationId);
        }

        /**
         * Sets the proxy used by the destination to the given value.
         *
         * @param value
         *            The proxy that should be used.
         * @return This builder.
         */
        @Nonnull
        public Builder proxyConfiguration( @Nonnull final ProxyConfiguration value )
        {
            setProxyAuth(value.getCredentials());

            return property(DestinationProperty.PROXY_URI, value.getUri());
        }

        private void setProxyAuth( @Nonnull final Option<Credentials> maybeCredentials )
        {
            if( maybeCredentials.isEmpty() ) {
                removeProperty(DestinationProperty.PROXY_AUTH);
                return;
            }

            final Credentials credentials = maybeCredentials.get();

            if( credentials instanceof NoCredentials ) {
                removeProperty(DestinationProperty.PROXY_AUTH);
                return;
            }
            if( credentials instanceof BasicCredentials ) {
                final String headerValue = ((BasicCredentials) credentials).getHttpHeaderValue();
                property(DestinationProperty.PROXY_AUTH, headerValue);
                return;
            }
            if( credentials instanceof BearerCredentials ) {
                final String headerValue = ((BearerCredentials) credentials).getHttpHeaderValue();
                property(DestinationProperty.PROXY_AUTH, headerValue);
                return;
            }

            throw new IllegalArgumentException(
                String
                    .format(
                        "The provided proxy credentials (%s) are not supported. Consider adding the PROXY_AUTHORIZATION header yourself using the `header` method.",
                        credentials.getClass().getSimpleName()));
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
         * Sets the proxy URI of the {@code DefaultHttpDestination}.
         *
         * @param proxyUri
         *            The URI of the proxy
         * @return This builder.
         */
        @Nonnull
        public Builder proxy( @Nonnull final URI proxyUri )
        {
            return property(DestinationProperty.PROXY_URI, proxyUri);
        }

        /**
         * Sets the proxy authorization header of the {@code DefaultHttpDestination}.
         *
         * @param proxyAuthorization
         *            The authorization header value
         * @return This builder.
         */
        @Nonnull
        public Builder proxyAuthorization( @Nonnull final String proxyAuthorization )
        {
            return property(DestinationProperty.PROXY_AUTH, proxyAuthorization);
        }

        /**
         * Sets the proxy type (Internet or On-Premise).
         *
         * @param proxyType
         *            Type of proxy this destination is configured for.
         * @return This builder.
         */
        @Nonnull
        public Builder proxyType( @Nonnull final ProxyType proxyType )
        {
            return property(DestinationProperty.PROXY_TYPE, proxyType);
        }

        /**
         * Sets the proxy host and proxy port of the {@code DefaultHttpDestination}.
         *
         * @param proxyHost
         *            The host of the proxy
         * @param proxyPort
         *            The port of the proxy
         * @return This builder.
         */
        @Nonnull
        public Builder proxy( @Nonnull final String proxyHost, final int proxyPort )
        {
            return property(DestinationProperty.PROXY_HOST, proxyHost)
                .property(DestinationProperty.PROXY_PORT, proxyPort);
        }

        /**
         * Sets the credentials for accessing the destination when basic authentication is used.
         *
         * @param basicCredentials
         *            Username and password represented as a {@link BasicCredentials} object.
         * @return This builder.
         **/
        @Nonnull
        public Builder basicCredentials( @Nonnull final BasicCredentials basicCredentials )
        {
            return basicCredentials(basicCredentials.getUsername(), basicCredentials.getPassword());
        }

        /**
         * Sets the credentials for accessing the destination using {@link AuthenticationType#BASIC_AUTHENTICATION}.
         *
         * @param user
         *            The user.
         * @param password
         *            The password.
         * @return This builder.
         */
        @Nonnull
        public Builder basicCredentials( @Nonnull final String user, @Nonnull final String password )
        {
            return authenticationType(AuthenticationType.BASIC_AUTHENTICATION)
                .property(DestinationProperty.BASIC_AUTH_USERNAME, user)
                .property(DestinationProperty.BASIC_AUTH_PASSWORD, password);
        }

        /**
         * Sets the expected authentication type of the {@code DefaultHttpDestination}.
         *
         * @param authenticationType
         *            The type of authentication for the destination
         * @return This builder.
         */
        @Nonnull
        public Builder authenticationType( @Nonnull final AuthenticationType authenticationType )
        {
            return property(DestinationProperty.AUTH_TYPE, authenticationType);
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
         * Sets the {@link SecurityConfigurationStrategy} for outbound calls via this Destination to decide if the
         * {@link SSLContext} should be derived from the Destination Configuration or from the cloud platform (i.e. the
         * environment).
         *
         * @param securityConfigurationStrategy
         *            The strategy to use
         * @return This builder
         */
        @Nonnull
        public Builder securityConfiguration(
            @Nonnull final SecurityConfigurationStrategy securityConfigurationStrategy )
        {
            return property(DestinationProperty.SECURITY_CONFIGURATION, securityConfigurationStrategy);
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

        /**
         * Finally creates the {@code DefaultHttpDestination} with the properties retrieved via the
         * {@link #property(String, Object)} method.
         *
         * @return A fully instantiated {@code DefaultHttpDestination}.
         */
        @Nonnull
        public DefaultHttpDestination build()
        {
            if( get(DestinationProperty.URI).isEmpty() ) {
                throw new IllegalArgumentException("Cannot build a HttpDestination without a URL.");
            }

            if( get(DestinationProperty.TYPE).isEmpty() ) {
                property(DestinationProperty.TYPE, DestinationType.HTTP);
            }

            if( builder.get(DestinationProperty.PROXY_TYPE).contains(ProxyType.ON_PREMISE) ) {
                try {
                    return proxyHandler.handle(this);
                }
                catch( final Exception e ) {
                    final String msg =
                        """
                            Unable to resolve proxy configuration for destination. \
                            This destination cannot be used for anything other than reading its properties. \
                            This is unexpected and will be changed to fail instead in a future version of Cloud SDK. \
                            Please analyze the attached stack trace and resolve the issue. \
                            In case only the properties of a destination should be accessed, without performing authorization flows, please use the 'getDestinationProperties'  method on 'DestinationService' instead.\
                            """;
                    log.error(msg, e);
                }
            }
            return buildInternal();
        }

        DefaultHttpDestination buildInternal()
        {
            return new DefaultHttpDestination(
                builder.build(),
                new ComplexDestinationPropertyFactory(),
                headers,
                keyStore,
                trustStore,
                customHeaderProviders);
        }
    }
}
