package com.sap.cloud.sdk.testutil;

import java.net.URI;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.sap.cloud.sdk.cloudplatform.cache.CacheManager;
import com.sap.cloud.sdk.cloudplatform.connectivity.AuthenticationType;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationLoader;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationType;
import com.sap.cloud.sdk.cloudplatform.connectivity.Header;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.ProxyConfiguration;
import com.sap.cloud.sdk.cloudplatform.connectivity.ProxyType;
import com.sap.cloud.sdk.cloudplatform.connectivity.WrappedDestination;
import com.sap.cloud.sdk.cloudplatform.security.BasicCredentials;
import com.sap.cloud.sdk.cloudplatform.security.Credentials;
import com.sap.cloud.sdk.s4hana.connectivity.DefaultErpHttpDestination;
import com.sap.cloud.sdk.s4hana.connectivity.ErpHttpDestination;
import com.sap.cloud.sdk.s4hana.serialization.SapClient;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class DefaultDestinationMocker implements DestinationMocker
{
    private final TestSystemsProvider testSystemsProvider;
    private final DefaultCredentialsProvider credentialsProvider;
    private final ProxyConfiguration proxyConfiguration;

    private final Supplier<DestinationLoader> resetDestinationLoader;

    private static final String JCO_VERSION = getJcoVersion();

    @Getter( AccessLevel.PACKAGE )
    private final Map<String, Destination> erpHttpDestinations = Maps.newHashMap();

    @Getter( AccessLevel.PACKAGE )
    private final Map<String, Destination> httpDestinations = Maps.newHashMap();

    @Getter( AccessLevel.PACKAGE )
    private final Map<String, Destination> rfcDestinations = Maps.newHashMap();

    @Getter( AccessLevel.PACKAGE )
    private final Map<String, Destination> destinations = Maps.newHashMap();

    private final Map<String, Map<String, String>> rfcProperties = Maps.newHashMap();

    @Nonnull
    @Override
    public Destination mockDestination(
        @Nonnull final DestinationType destinationType,
        @Nonnull final String destinationName,
        @Nullable final Map<String, String> propertiesByName )
    {
        final DefaultDestination.Builder builder = DefaultDestination.builder().property("name", destinationName);

        if( propertiesByName != null ) {
            propertiesByName.forEach(builder::property);
        }

        final Destination destination = builder.build();

        destinations.put(destinationName, destination);
        return destination;
    }

    @Nonnull
    @Override
    public Destination mockDestination( @Nonnull final String name, @Nonnull final String systemAlias )
    {
        return mockDestination(name, testSystemsProvider.getTestSystem(systemAlias));
    }

    @Nonnull
    @Override
    public Destination mockDestination( @Nonnull final String name, @Nonnull final TestSystem<?> testSystem )
    {
        return mockDestination(
            name,
            testSystem.getUri(),
            credentialsProvider.getCredentials(testSystem),
            testSystem.getProxyConfiguration().getOrNull());
    }

    @Nonnull
    @Override
    public Destination mockDestination(
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
        @Nullable final Map<String, String> propertiesByName )
    {
        resetDestinationLoader();

        final DefaultHttpDestination.Builder destinationBuilder = DefaultHttpDestination.builder(uri);

        if( authenticationType == null ) {
            destinationBuilder.authenticationType(AuthenticationType.NO_AUTHENTICATION);
        } else {
            destinationBuilder.authenticationType(authenticationType);
        }

        // handle credentials
        if( credentials instanceof BasicCredentials ) {
            destinationBuilder.authenticationType(AuthenticationType.BASIC_AUTHENTICATION);

            final BasicCredentials basicCredentials = (BasicCredentials) credentials;
            destinationBuilder.user(basicCredentials.getUsername());
            destinationBuilder.password(basicCredentials.getPassword());
        }

        // other properties
        if( propertiesByName != null ) {
            propertiesByName.forEach(destinationBuilder::property);
        }

        // Proxy
        final ProxyConfiguration proxyConf = Option.of(proxyConfiguration).getOrElse(this.proxyConfiguration);
        if( proxyConf != null ) {
            destinationBuilder.proxy(proxyConf.getUri());
        }

        // TrustAll
        if( isTrustingAllCertificates == null || isTrustingAllCertificates ) {
            destinationBuilder.trustAllCertificates();
        }

        final HttpDestination httpDestination =
            TestHttpDestination
                .builder()
                .baseProperties(
                    destinationBuilder.proxyType(proxyType == null ? ProxyType.INTERNET : proxyType).name(name).build())
                .headers(headers)
                .keyStore(keyStore)
                .keyStorePassword(keyStorePassword)
                .trustStore(trustStore)
                .build();

        final Destination destination = WrappedDestination.of(httpDestination);
        httpDestinations.put(name, destination);
        return destination;
    }

    @Nonnull
    @Override
    public Destination mockErpDestination( @Nonnull final String destinationName, @Nonnull final String systemAlias )
    {
        return mockErpDestination(destinationName, testSystemsProvider.getErpSystem(systemAlias));
    }

    @Nonnull
    @Override
    public Destination mockErpDestination(
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
        @Nullable final Map<String, String> propertiesByName )
    {
        resetDestinationLoader();

        final ErpSystem erpSystemOrDefault = erpSystem == null ? testSystemsProvider.getErpSystem() : erpSystem;

        final URI uri = erpSystemOrDefault.getUri();
        final SapClient sapClient = erpSystemOrDefault.getSapClient();
        final Locale locale = erpSystemOrDefault.getLocale();

        final DefaultErpHttpDestination.Builder destinationBuilder =
            DefaultErpHttpDestination.builder(uri).name(destinationName);

        // handle headers
        final List<Header> headerList = new ArrayList<>();
        if( headers != null ) {
            headerList.addAll(headers);
        }
        headerList.forEach(destinationBuilder::header);

        // handle properties
        final Map<String, String> properties = new HashMap<>();
        properties.put(ErpHttpDestination.SAP_CLIENT_KEY, sapClient.getValue());
        properties.put(ErpHttpDestination.LOCALE_KEY, locale.getLanguage());

        if( propertiesByName != null ) {
            properties.putAll(propertiesByName);
        }
        properties.forEach(destinationBuilder::property);

        // Proxy
        final ProxyConfiguration proxyConf =
            Option
                .of(proxyConfiguration)
                .getOrElse(() -> erpSystemOrDefault.getProxyConfiguration().getOrElse(this.proxyConfiguration));
        if( proxyConf != null ) {
            destinationBuilder.proxy(proxyConf.getUri());
        }

        // TrustAll
        if( isTrustingAllCertificates == null || isTrustingAllCertificates ) {
            destinationBuilder.trustAllCertificates();
        }

        // handle network
        if( proxyType == null ) {
            destinationBuilder.proxyType(ProxyType.INTERNET);
        } else {
            destinationBuilder.proxyType(proxyType);
        }

        // handle authentication type
        if( authenticationType != null ) {
            destinationBuilder.authenticationType(authenticationType);
        }

        // handle credentials
        final Credentials erpCredentials = credentialsProvider.getErpCredentials(erpSystemOrDefault, credentials);
        if( erpCredentials instanceof BasicCredentials ) {
            destinationBuilder.authenticationType(AuthenticationType.BASIC_AUTHENTICATION);

            final BasicCredentials basicCredentials = (BasicCredentials) erpCredentials;
            destinationBuilder.user(basicCredentials.getUsername());
            destinationBuilder.password(basicCredentials.getPassword());
        }

        final ErpHttpDestination httpDestination =
            TestErpHttpDestination
                .builder()
                .baseProperties(destinationBuilder.build())
                .keyStore(keyStore)
                .keyStorePassword(keyStorePassword)
                .trustStore(trustStore)
                .build();

        final Destination destination = WrappedDestination.of(httpDestination);
        erpHttpDestinations.put(destinationName, destination);
        return destination;
    }

    @Override
    public void clearDestinations()
    {
        httpDestinations.clear();
        rfcDestinations.clear();
        rfcProperties.clear();
        destinations.clear();
    }

    private void resetDestinationLoader()
    {
        resetDestinationLoader.get();
        CacheManager.invalidateAll();
    }

    private void resetJCoRuntime()
    {

    }

    private static String getJcoVersion()
    {
        return "0.0.1";
    }
}
