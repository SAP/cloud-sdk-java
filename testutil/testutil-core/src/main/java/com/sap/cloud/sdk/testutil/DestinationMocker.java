package com.sap.cloud.sdk.testutil;

import java.net.URI;
import java.security.KeyStore;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.connectivity.AuthenticationType;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationType;
import com.sap.cloud.sdk.cloudplatform.connectivity.Header;
import com.sap.cloud.sdk.cloudplatform.connectivity.ProxyConfiguration;
import com.sap.cloud.sdk.cloudplatform.connectivity.ProxyType;
import com.sap.cloud.sdk.cloudplatform.security.BasicCredentials;
import com.sap.cloud.sdk.cloudplatform.security.Credentials;
import com.sap.cloud.sdk.cloudplatform.security.NoCredentials;

interface DestinationMocker
{
    /**
     * Mocks an {@link Destination} for the given parameters.
     * <p>
     * <strong>Note:</strong></strong> This invalidates all caches via CacheManager to avoid stale entries.
     *
     * @param destinationName
     *            The name of the mocked destination.
     *
     * @param propertiesByName
     *            Properties of the {@link Destination} by their name.
     */
    @Nonnull
    Destination mockDestination(
        @Nonnull final DestinationType destinationType,
        @Nonnull final String destinationName,
        @Nullable final Map<String, String> propertiesByName );

    /**
     * Mocks a destination with a given name to the given system with the registered credentials.
     * <p>
     * <strong>Note:</strong></strong> This invalidates all caches via CacheManager to avoid stale entries.
     */
    @Nonnull
    Destination mockDestination( @Nonnull final String name, @Nonnull final String systemAlias );

    /**
     * Mocks a destination with a given name to the given {@link TestSystem} with the registered credentials.
     * <p>
     * <strong>Note:</strong></strong> This invalidates all caches via CacheManager to avoid stale entries.
     */
    @Nonnull
    Destination mockDestination( @Nonnull final String name, @Nonnull final TestSystem<?> testSystem );

    /**
     * Mocks a destination with a given name to redirect to the given {@link URI} with no authentication.
     * <p>
     * <strong>Note:</strong></strong> This invalidates all caches via CacheManager to avoid stale entries.
     */
    @Nonnull
    default Destination mockDestination( @Nonnull final String name, @Nonnull final URI uri )
    {
        return mockDestination(name, uri, null, null);
    }

    /**
     * Mocks a destination with a given name to redirect to the given {@link URI} with the given {@link Credentials}.
     * <p>
     * <strong>Note:</strong></strong> This invalidates all caches via CacheManager to avoid stale entries.
     */
    @Nonnull
    default
        Destination
        mockDestination( @Nonnull final String name, @Nonnull final URI uri, @Nullable final Credentials credentials )
    {
        return mockDestination(name, uri, credentials, null);
    }

    /**
     * Mocks a destination with a given name to redirect to the given {@link URI} with the given {@link Credentials}.
     * <p>
     * <strong>Note:</strong></strong> This invalidates all caches via CacheManager to avoid stale entries.
     */
    @Nonnull
    default Destination mockDestination(
        @Nonnull final String name,
        @Nonnull final URI uri,
        @Nullable final Credentials credentials,
        @Nullable final ProxyConfiguration proxyConfiguration )
    {
        if( credentials == null || credentials instanceof NoCredentials ) {
            return mockDestination(name, uri, null, null, null, null, null, null, null, null, null, null, null);
        }

        if( credentials instanceof BasicCredentials ) {
            return mockDestination(
                name,
                uri,
                AuthenticationType.BASIC_AUTHENTICATION,
                credentials,
                null,
                proxyConfiguration,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        }

        throw new TestConfigurationError(
            "Unsupported credentials of type " + credentials.getClass().getSimpleName() + ": " + credentials + ".");
    }

    /**
     * Mocks a destination based on the given parameters.
     * <p>
     * <strong>Note:</strong></strong> This invalidates all caches via CacheManager to avoid stale entries.
     */
    @Nonnull
    Destination mockDestination(
        @Nonnull final String name,
        @Nonnull final URI uri,
        @Nullable final AuthenticationType authenticationType,
        @Nullable final Credentials credentials,
        @Nullable final ProxyType proxyType,
        @Nullable final ProxyConfiguration proxyConfiguration,
        @Nullable final List<Header> headers,
        @Nullable final KeyStore trustStore,
        @Nullable final String trustStorePassword,
        @Nullable final Boolean isTrustingAllCertificates,
        @Nullable final KeyStore keyStore,
        @Nullable final String keyStorePassword,
        @Nullable final Map<String, String> propertiesByName );

    /**
     * Mocks a destination based on the given {@link MockDestination}.
     * <p>
     * <strong>Note:</strong></strong> This invalidates all caches via CacheManager to avoid stale entries.
     */
    @Nonnull
    default Destination mockDestination( @Nonnull final MockDestination destination )
    {
        return mockDestination(
            destination.getName(),
            destination.getUri(),
            destination.getAuthenticationType(),
            destination.getCredentials(),
            destination.getProxyType(),
            destination.getProxyConfiguration(),
            destination.getHeaders(),
            destination.getTrustStore(),
            destination.getTrustStorePassword(),
            destination.getIsTrustingAllCertificates(),
            destination.getKeyStore(),
            destination.getKeyStorePassword(),
            destination.getProperties());
    }

    /**
     * Mocks an ERP {@link Destination} by redirecting to an actual ERP system for the given destination name.
     * <p>
     * <strong>Note:</strong> This invalidates all caches via CacheManager to avoid stale entries.
     *
     * @param destinationName
     *            The name of the mocked destination.
     *
     * @param systemAlias
     *            The alias of the {@link ErpSystem} that should be used. Must not be {@code null}.
     */
    @Nonnull
    Destination mockErpDestination( @Nonnull final String destinationName, @Nonnull final String systemAlias );

    /**
     * Mocks an ERP {@link Destination} by redirecting to an actual ERP system for the given destination name.
     * <p>
     * <strong>Note:</strong> This invalidates all caches via CacheManager to avoid stale entries.
     *
     * @param destinationName
     *            The name of the mocked destination.
     *
     * @param erpSystem
     *            The {@link ErpSystem} to use for mocking. If {@code null}, {{@link MockUtil#getErpSystem()} is used.
     */
    @Nonnull
    default Destination mockErpDestination( @Nonnull final String destinationName, @Nullable final ErpSystem erpSystem )
    {
        return mockErpDestination(destinationName, erpSystem, null);
    }

    /**
     * Mocks an ERP {@link Destination} for the given destination name, {@link ErpSystem} and {@link Credentials}.
     * <p>
     * <strong>Note:</strong> This invalidates all caches via CacheManager to avoid stale entries.
     *
     * @param destinationName
     *            The name of the mocked destination.
     *
     * @param erpSystem
     *            The {@link ErpSystem} to use for mocking. If {@code null}, {{@link MockUtil#getErpSystem()} is used.
     *
     * @param credentials
     *            The {@link Credentials} to be used for accessing the system. If {@code null}, they are resolved from
     *            configuration files or explicit registration via {@link MockUtil#addCredentials(String, Credentials)}.
     *            Only adds the respective HTTP header for the given credentials if the header does not yet exist.
     */
    @Nonnull
    default Destination mockErpDestination(
        @Nonnull final String destinationName,
        @Nullable final ErpSystem erpSystem,
        @Nullable final Credentials credentials )
    {
        return mockErpDestination(
            destinationName,
            erpSystem,
            credentials,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null);
    }

    /**
     * Mocks an ERP {@link Destination} for the given parameters.
     * <p>
     * <strong>Note:</strong> This invalidates all caches via CacheManager to avoid stale entries.
     *
     * @param destinationName
     *            The name of the mocked destination.
     *
     * @param erpSystem
     *            The {@link ErpSystem} to use for mocking. If {@code null}, {{@link MockUtil#getErpSystem()} is used.
     *
     * @param credentials
     *            The {@link Credentials} to be used for accessing the system. If {@code null}, they are resolved from
     *            configuration files or explicit registration via {@link MockUtil#addCredentials(String, Credentials)}.
     *            Only adds the respective HTTP header for the given credentials if the header does not yet exist.
     *
     * @param authenticationType
     *            The {@link AuthenticationType} to be used.
     *
     * @param proxyType
     *            The {@link ProxyType} to be used. If not {@code null}, overrides the {@link ProxyType} inferred from
     *            the {@link ErpSystem}.
     *
     * @param proxyConfiguration
     *            The {@link ProxyConfiguration} to be used.
     *
     * @param headers
     *            {@link Header}s to be used.
     *
     * @param trustStore
     *            The trust store to be used.
     *
     * @param trustStorePassword
     *            The trust store password to be used.
     *
     * @param isTrustingAllCertificates
     *            Decides whether all certificates are trusted.
     *
     * @param keyStore
     *            The {@link KeyStore} to be used.
     *
     * @param keyStorePassword
     *            The {@link KeyStore} password to be used.
     *
     * @param propertiesByName
     *            Properties of the {@link Destination} by their name. If a property with the respective name is
     *            provided, it will override pre-configured properties such as "sap-client".
     */
    @Nonnull
    Destination mockErpDestination(
        @Nonnull final String destinationName,
        @Nullable final ErpSystem erpSystem,
        @Nullable final Credentials credentials,
        @Nullable final AuthenticationType authenticationType,
        @Nullable final ProxyType proxyType,
        @Nullable final ProxyConfiguration proxyConfiguration,
        @Nullable final List<Header> headers,
        @Nullable final KeyStore trustStore,
        @Nullable final String trustStorePassword,
        @Nullable final Boolean isTrustingAllCertificates,
        @Nullable final KeyStore keyStore,
        @Nullable final String keyStorePassword,
        @Nullable final Map<String, String> propertiesByName );

    /**
     * Mocks an ERP {@link Destination} for the given {@link MockErpDestination}.
     * <p>
     * <strong>Note:</strong> This invalidates all caches via CacheManager to avoid stale entries.
     */
    @Nonnull
    default Destination mockErpDestination( @Nonnull final MockErpDestination destination )
    {
        return mockErpDestination(
            destination.getName(),
            destination.getErpSystem(),
            destination.getCredentials(),
            destination.getAuthenticationType(),
            destination.getProxyType(),
            destination.getProxyConfiguration(),
            destination.getHeaders(),
            destination.getTrustStore(),
            destination.getTrustStorePassword(),
            destination.getIsTrustingAllCertificates(),
            destination.getKeyStore(),
            destination.getKeyStorePassword(),
            destination.getProperties());
    }

    /**
     * Clears all previously mocked {@link Destination}s.
     */
    void clearDestinations();
}
