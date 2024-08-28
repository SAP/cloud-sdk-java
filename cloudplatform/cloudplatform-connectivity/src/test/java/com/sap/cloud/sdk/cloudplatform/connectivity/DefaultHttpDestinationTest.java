/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static java.util.Arrays.asList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.assertj.core.api.Assertions;
import org.assertj.vavr.api.VavrAssertions;
import org.junit.jupiter.api.Test;

import com.google.common.net.HttpHeaders;
import com.sap.cloud.sdk.cloudplatform.security.BasicAuthHeaderEncoder;
import com.sap.cloud.sdk.cloudplatform.security.BasicCredentials;
import com.sap.cloud.sdk.cloudplatform.security.BearerCredentials;
import com.sap.cloud.sdk.cloudplatform.security.ClientCertificate;
import com.sap.cloud.sdk.cloudplatform.security.ClientCredentials;
import com.sap.cloud.sdk.cloudplatform.security.Credentials;

import lombok.Getter;
import lombok.SneakyThrows;

class DefaultHttpDestinationTest
{
    private static final URI VALID_URI = URI.create("https://www.sap.de");

    @Test
    void testGetDelegation()
    {
        final String someKey = "someKey";
        final Object someValue = new Object();

        final DestinationProperties filledDestination =
            DefaultHttpDestination.builder(VALID_URI).property(someKey, someValue).build();

        final DestinationProperties httpDestination = DefaultHttpDestination.fromProperties(filledDestination).build();

        assertThat(httpDestination).isNotSameAs(filledDestination);
        VavrAssertions.assertThat(httpDestination.get(someKey)).contains(someValue);
    }

    @Test
    void testCannotBeConstructedFromDestinationWithoutUri()
    {
        final DefaultDestination emptyDestination = DefaultDestination.builder().build();

        Assertions.assertThat(DefaultHttpDestination.canBeConstructedFrom(emptyDestination)).isFalse();
    }

    @Test
    void testCanBeConstructedFromDestinationWithUri()
    {
        final DestinationProperties validHttpDestination = DefaultHttpDestination.builder(VALID_URI).build();

        Assertions.assertThat(DefaultHttpDestination.canBeConstructedFrom(validHttpDestination)).isTrue();
    }

    @Test
    void testGetUriSuccessfully()
    {
        final DestinationProperties filledDestination = DefaultHttpDestination.builder(VALID_URI).build();

        final HttpDestinationProperties httpDestination =
            DefaultHttpDestination.fromProperties(filledDestination).build();

        assertThat(httpDestination).isNotSameAs(filledDestination);
        Assertions.assertThat(httpDestination.getUri()).isEqualTo(VALID_URI);
    }

    @Test
    void testGetUriThrowsExceptionOnFailedCast()
    {
        final String defaultUriKey = "url";
        final Object somethingNonUri = 42;

        final DefaultDestination baseDestination =
            DefaultDestination.builder().property(defaultUriKey, somethingNonUri).build();

        Assertions
            .assertThatThrownBy(() -> DefaultHttpDestination.fromProperties(baseDestination).build())
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testFromMapThrowsExceptionOnMissingUri()
    {
        assertThatThrownBy(() -> DefaultHttpDestination.fromMap(Collections.emptyMap()).build())
            .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testFromPropertiesThrowsExceptionOnMissingUri()
    {
        final DefaultDestination baseDestination = DefaultDestination.builder().build();

        assertThatThrownBy(() -> DefaultHttpDestination.fromProperties(baseDestination).build())
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testEqual()
    {
        final DefaultHttpDestination dest1 = DefaultHttpDestination.builder(URI.create("https://uri1")).build();
        final DefaultHttpDestination equal1 = DefaultHttpDestination.builder(URI.create("https://uri1")).build();

        assertThat(dest1).isEqualTo(equal1);
    }

    @Test
    void testNotEqual()
    {
        final DefaultHttpDestination dest1 = DefaultHttpDestination.builder(URI.create("https://uri1")).build();
        final DefaultHttpDestination dest2 = DefaultHttpDestination.builder(URI.create("https://uri2")).build();

        assertThat(dest1).isNotEqualTo(dest2);
    }

    @Test
    void testEqualsIsImplemented()
    {
        final Header dummyHeader = new Header("name", "value");

        final DefaultHttpDestination firstDestination =
            DefaultHttpDestination.builder(VALID_URI).header(dummyHeader).build();
        final DefaultHttpDestination secondDestination =
            DefaultHttpDestination.builder(VALID_URI).header(dummyHeader).build();

        assertThat(firstDestination).isEqualTo(secondDestination).isNotSameAs(secondDestination);
    }

    @SneakyThrows
    @Test
    void testEqualsWithKeyStore()
    {
        final KeyPair keyPair = DestinationKeyStoreComparatorTest.generateKeyPair();
        final Certificate cert = DestinationKeyStoreComparatorTest.generateCertificate(keyPair, "a");

        final KeyStore keystore1 = KeyStore.getInstance("JKS");
        keystore1.load(null);
        keystore1.setKeyEntry("a", keyPair.getPrivate(), new char[0], new Certificate[] { cert });

        final KeyStore keystore2 = KeyStore.getInstance("JKS");
        keystore2.load(null);
        keystore2.setKeyEntry("a", keyPair.getPrivate(), new char[0], new Certificate[] { cert });

        // check for destinations with comparable key-stores
        final DefaultHttpDestination dest1 = DefaultHttpDestination.builder(VALID_URI).keyStore(keystore1).build();
        final DefaultHttpDestination dest2 = DefaultHttpDestination.builder(VALID_URI).keyStore(keystore2).build();

        assertThat(dest1).isEqualTo(dest2);
        assertThat(dest1).hasSameHashCodeAs(dest2);

        // check for destination with empty key-store
        final KeyStore keystore3 = KeyStore.getInstance("JKS");
        keystore3.load(null);

        final DefaultHttpDestination dest3 = DefaultHttpDestination.builder(VALID_URI).keyStore(keystore3).build();
        assertThat(dest1).isNotEqualTo(dest3);
        assertThat(dest1).doesNotHaveSameHashCodeAs(dest3);
    }

    @Test
    void testHashCodeIsImplemented()
    {
        final Header dummyHeader = new Header("name", "value");

        final DefaultHttpDestination firstDestination =
            DefaultHttpDestination.builder(VALID_URI).header(dummyHeader).build();
        final DefaultHttpDestination secondDestination =
            DefaultHttpDestination.builder(VALID_URI).header(dummyHeader).build();

        Assertions.assertThat(firstDestination).hasSameHashCodeAs(secondDestination);
    }

    @Test
    void testKeyStoreAndKeyStorePasswordCanBeSetAndRead()
    {
        final KeyStore keyStore = mock(KeyStore.class);
        final String keyStorePassword = "some-password";
        final String uri = "some-uri";

        final DefaultHttpDestination defaultHttpDestination =
            DefaultHttpDestination.builder(uri).keyStore(keyStore).keyStorePassword(keyStorePassword).build();

        assertThat(defaultHttpDestination.getKeyStore().get()).isSameAs(keyStore);
        assertThat(defaultHttpDestination.getKeyStorePassword().get()).isEqualTo(keyStorePassword);
    }

    @Test
    void testTrustStoreCanBeSetAndRead()
    {
        final KeyStore trustStore = mock(KeyStore.class);
        final String uri = "some-uri";

        final DefaultHttpDestination defaultHttpDestination =
            DefaultHttpDestination.builder(uri).trustStore(trustStore).build();

        assertThat(defaultHttpDestination.getTrustStore().get()).isSameAs(trustStore);
    }

    @Test
    void testSetSecurityConfigurationStrategy()
    {
        final DefaultHttpDestination destinationWithPlatformStrategy =
            DefaultHttpDestination
                .builder("some-uri")
                .securityConfiguration(SecurityConfigurationStrategy.FROM_PLATFORM)
                .build();

        assertThat(destinationWithPlatformStrategy.getSecurityConfigurationStrategy())
            .isEqualTo(SecurityConfigurationStrategy.FROM_PLATFORM);

        final DefaultHttpDestination destinationWithDefaultStrategy =
            DefaultHttpDestination.builder("some-uri").build();
        assertThat(destinationWithDefaultStrategy.getSecurityConfigurationStrategy())
            .isEqualTo(SecurityConfigurationStrategy.FROM_DESTINATION);

        final DefaultHttpDestination destinationWithDestinationStrategy =
            DefaultHttpDestination
                .builder("some-uri")
                .securityConfiguration(SecurityConfigurationStrategy.FROM_DESTINATION)
                .build();
        assertThat(destinationWithDestinationStrategy.getSecurityConfigurationStrategy())
            .isEqualTo(SecurityConfigurationStrategy.FROM_DESTINATION);

    }

    @Test
    void testCopyPropertiesOfExistingHttpDestinationWithModification()
    {
        final URI uri = URI.create("foo");
        final Header header = new Header("foo", "bar");

        final DefaultHttpDestination firstDestination =
            DefaultHttpDestination
                .builder(uri)
                .authenticationType(AuthenticationType.BASIC_AUTHENTICATION)
                .basicCredentials(new BasicCredentials("foo", "bar"))
                .keyStorePassword("bar")
                .header(header)
                .property("foo", "bar")
                .build();

        final DefaultHttpDestination secondDestination =
            DefaultHttpDestination
                .fromDestination(firstDestination)
                .authenticationType(AuthenticationType.OAUTH2_SAML_BEARER_ASSERTION)
                .build();

        assertThat(secondDestination.getUri()).isEqualTo(uri);
        assertThat(secondDestination.getAuthenticationType())
            .isEqualTo(AuthenticationType.OAUTH2_SAML_BEARER_ASSERTION);
        assertThat(secondDestination.getKeyStorePassword().get()).isEqualTo("bar");
        assertThat(secondDestination.get("foo").get()).isEqualTo("bar");
        assertThat(secondDestination.getHeaders(uri)).contains(header);
        assertThat(secondDestination.getBasicCredentials().get().getUsername()).isEqualTo("foo");
        assertThat(secondDestination.getBasicCredentials().get().getPassword()).isEqualTo("bar");

        assertThat(firstDestination).isNotEqualTo(secondDestination);
        assertThat(firstDestination.hashCode()).isNotEqualTo(secondDestination.hashCode());
    }

    @Test
    void testCopyPropertiesOfExistingHttpDestinationWithoutModifications()
    {
        final URI uri = URI.create("URI");
        final Header customHeader = new Header("custom-header", "custom-header-value");

        final List<Header> headerProviderHeader =
            Collections.singletonList(new Header("header-from-header-provider", "value-from-header-provider"));

        final KeyStore mockTrustStore = mock(KeyStore.class);
        final KeyStore mockKeyStore = mock(KeyStore.class);

        final DefaultHttpDestination firstDestination =
            DefaultHttpDestination
                .builder(uri)
                .authenticationType(AuthenticationType.BASIC_AUTHENTICATION)
                .basicCredentials(new BasicCredentials("basic-username", "basic-password"))
                .keyStorePassword("keystore-password")
                .header(customHeader)
                .property("foo", "bar")
                .headerProviders(( any ) -> headerProviderHeader)
                .securityConfiguration(SecurityConfigurationStrategy.FROM_PLATFORM)
                .trustStore(mockTrustStore)
                .keyStore(mockKeyStore)
                .proxyAuthorization("Bearer some-token")
                .proxy(URI.create("http://proxy:8080"))
                .property("URL.headers.foo1", "bar1")
                .build();

        final DefaultHttpDestination secondDestination =
            DefaultHttpDestination.fromDestination(firstDestination).build();

        assertThat(secondDestination.getUri()).isEqualTo(uri);
        assertThat(secondDestination.getAuthenticationType()).isEqualTo(AuthenticationType.BASIC_AUTHENTICATION);
        assertThat(secondDestination.getBasicCredentials().get().getUsername()).isEqualTo("basic-username");
        assertThat(secondDestination.getBasicCredentials().get().getPassword()).isEqualTo("basic-password");
        assertThat(secondDestination.getKeyStorePassword().get()).isEqualTo("keystore-password");
        assertThat(secondDestination.customHeaders).contains(customHeader);
        assertThat(secondDestination.get("foo").get()).isEqualTo("bar");

        assertThat(secondDestination.getCustomHeaderProviders().size()).isEqualTo(1);

        assertThat(
            secondDestination.getCustomHeaderProviders().get(0).getHeaders(mock(DestinationRequestContext.class)))
            .contains(headerProviderHeader.get(0));
        assertThat(firstDestination.getCustomHeaderProviders())
            .isNotSameAs(secondDestination.getCustomHeaderProviders());
        // although the `List` instances are not the same, they still contains the exact same `HeaderProvider` instances
        assertThat(firstDestination.getCustomHeaderProviders().get(0))
            .isSameAs(secondDestination.getCustomHeaderProviders().get(0));

        assertThat(secondDestination.getSecurityConfigurationStrategy())
            .isEqualTo(SecurityConfigurationStrategy.FROM_PLATFORM);
        assertThat(secondDestination.getTrustStore().getOrNull()).isEqualTo(mockTrustStore);
        assertThat(secondDestination.getKeyStore().getOrNull()).isEqualTo(mockKeyStore);
        assertThat(secondDestination.getProxyConfiguration().get().getUri()).isEqualTo(URI.create("http://proxy:8080"));
        assertThat(secondDestination.getProxyConfiguration().get().getCredentials().get())
            .isEqualTo(new BearerCredentials("Bearer some-token"));
        assertThat(secondDestination.get("URL.headers.foo1").get()).isEqualTo("bar1");

        final Collection<Header> allHeaders = secondDestination.getHeaders(uri);
        assertThat(allHeaders.size()).isEqualTo(5);
        //Test that new headers get evaluated from properties and header providers
        assertThat(allHeaders).contains(new Header("Proxy-Authorization", "Bearer some-token"));
        assertThat(allHeaders).contains(new Header("foo1", "bar1"));
        assertThat(allHeaders.stream().filter(header -> header.getName().equals("Authorization")))
            .isNotEmpty()
            .size()
            .isEqualTo(1);
        assertThat(allHeaders).contains(new Header("custom-header", "custom-header-value"));
        assertThat(allHeaders).contains(new Header("header-from-header-provider", "value-from-header-provider"));

        assertThat(firstDestination).isEqualTo(secondDestination);
    }

    @Test
    void testCopyPropertiesOfExistingHttpDestinationsAndAddHeaderProviders()
    {
        final Header header1 = new Header("foo", "bar");
        final Header header2 = new Header("foo1", "bar1");

        final Header customHeader1 = new Header("custom-header1", "custom-value1");
        final Header customHeader2 = new Header("custom-header2", "custom-value2");

        final DefaultHttpDestination firstDestination =
            DefaultHttpDestination
                .builder("some-uri")
                .headerProviders(( any ) -> Collections.singletonList(header1))
                .header(customHeader1)
                .build();

        final DefaultHttpDestination secondDestination =
            DefaultHttpDestination
                .fromDestination(firstDestination)
                .headerProviders(( any ) -> Collections.singletonList(header2))
                .header(customHeader2)
                .build();

        assertThat(secondDestination.getCustomHeaderProviders()).hasSize(2);
        final DestinationRequestContext mockRequestContext = mock(DestinationRequestContext.class);

        assertThat(secondDestination.getCustomHeaderProviders().get(0).getHeaders(mockRequestContext))
            .contains(header1);
        assertThat(secondDestination.getCustomHeaderProviders().get(1).getHeaders(mockRequestContext))
            .contains(header2);
        assertThat(secondDestination.customHeaders).hasSize(2);
        assertThat(secondDestination.customHeaders).contains(customHeader1);
        assertThat(secondDestination.customHeaders).contains(customHeader2);
        assertThat(secondDestination.getHeaders())
            .containsExactlyInAnyOrder(customHeader1, customHeader2, header1, header2);

        assertThat(firstDestination.getCustomHeaderProviders()).hasSize(1);
        assertThat(firstDestination.getCustomHeaderProviders().get(0).getHeaders(mockRequestContext)).contains(header1);
        assertThat(firstDestination.customHeaders).hasSize(1);
        assertThat(firstDestination.customHeaders).contains(customHeader1);
        assertThat(firstDestination.getHeaders()).containsExactlyInAnyOrder(customHeader1, header1);

        assertThat(firstDestination.getCustomHeaderProviders())
            .isNotSameAs(secondDestination.getCustomHeaderProviders());
        assertThat(firstDestination.customHeaders).isNotSameAs(secondDestination.customHeaders);

        assertThat(firstDestination).isNotEqualTo(secondDestination);
        assertThat(firstDestination.hashCode()).isNotEqualTo(secondDestination.hashCode());
    }

    @Test
    void testGetInternetProxyConfiguration()
        throws URISyntaxException
    {
        final HttpDestination setViaConvenience =
            DefaultHttpDestination
                .builder(VALID_URI)
                .proxyConfiguration(new ProxyConfiguration(new URI("http://proxy.internet.com:8080")))
                .build();

        final HttpDestination setThroughUriProperty =
            DefaultHttpDestination
                .builder(VALID_URI)
                .property(DestinationProperty.PROXY_URI, URI.create("http://proxy.internet.com:8080"))
                .build();

        final HttpDestination setThroughSeparateProperties =
            DefaultHttpDestination
                .builder(VALID_URI)
                .property(DestinationProperty.PROXY_HOST, "proxy.internet.com")
                .property(DestinationProperty.PROXY_PORT, 8080)
                .build();

        final ProxyConfiguration expectedProxyConfig =
            new ProxyConfiguration(new URI("http://proxy.internet.com:8080"));

        assertThat(setViaConvenience.getProxyConfiguration()).contains(expectedProxyConfig);
        assertThat(setThroughUriProperty.getProxyConfiguration()).contains(expectedProxyConfig);
        assertThat(setThroughSeparateProperties.getProxyConfiguration()).contains(expectedProxyConfig);
    }

    @Test
    void testProxyInformationIsLoaded()
    {
        final HttpDestination destination =
            DefaultHttpDestination
                .builder("foo")
                .property(DestinationProperty.PROXY_URI, URI.create("https://proxy"))
                .property(DestinationProperty.PROXY_AUTH, "Bearer 1234")
                .build();

        final Set<Header> headers =
            destination
                .getHeaders()
                .stream()
                .filter(header -> HttpHeaders.PROXY_AUTHORIZATION.equalsIgnoreCase(header.getName()))
                .collect(Collectors.toSet());

        assertThat(headers).hasSize(1);
        assertThat(headers.stream().findAny().get().getValue()).isEqualTo("Bearer 1234");
    }

    @Test
    void testDestinationPropertiesToHeaderParameters()
    {
        final URI uri = URI.create("https://www.example.com");
        final Header header = new Header("foo3", "bar3");

        final DefaultHttpDestination destination =
            DefaultHttpDestination
                .builder(uri)
                .property("URL.HeaDeRs.foo1", "bar1")
                .property("url.headers.foo2", "bar2")
                .property("URL.headers.foo2", "bar2")
                .header(header)
                .property("foo4", "bar4")
                .build();

        final Collection<Header> headers = destination.getHeaders(uri);

        assertThat(headers)
            .containsExactlyInAnyOrder(
                new Header("foo1", "bar1"),
                new Header("foo2", "bar2"),
                new Header("foo3", "bar3"));
    }

    @Test
    void testDuplicatesSameHeaderAddedAsPropertiesAndHeader()
    {
        final URI uri = URI.create("https://www.example.com");
        final Header header = new Header("foo", "bar");

        final DefaultHttpDestination destination =
            DefaultHttpDestination.builder(uri).property("URL.headers.foo", "bar").header(header).build();

        final Collection<Header> headers = destination.getHeaders(uri);

        assertThat(headers).containsExactlyInAnyOrder(new Header("foo", "bar"), new Header("foo", "bar"));
    }

    @Test
    void testOverwriteHeaderFromDestinationProperties()
    {
        final URI uri = URI.create("https://www.example.com");

        final DefaultHttpDestination destination =
            DefaultHttpDestination
                .builder(uri)
                .property("URL.headers.foo", "bar")
                .property("URL.headers.foo", "bar2")
                .build();

        final Collection<Header> headers = destination.getHeaders(uri);

        assertThat(headers).containsExactlyInAnyOrder(new Header("foo", "bar2"));
    }

    @Test
    @SneakyThrows
    void testProxyConfigurationOverwritesExistingProperties()
    {
        final DefaultDestination baseProperties =
            DefaultDestination
                .builder()
                .name("name")
                .uri("my-target.com")
                .property(DestinationProperty.PROXY_URI, URI.create("http://initial.uri:1234"))
                .property(DestinationProperty.PROXY_HOST, "initial.host")
                .property(DestinationProperty.PROXY_PORT, 4321)
                .property(DestinationProperty.PROXY_AUTH, "Bearer initial-token")
                .property(DestinationProperty.PROXY_TYPE, ProxyType.ON_PREMISE)
                .build();

        final ProxyConfiguration proxyConfiguration =
            new ProxyConfiguration(
                URI.create("http://overwritten.uri:5678"),
                new BearerCredentials("Bearer overwritten-token"));
        final DefaultHttpDestination destination =
            DefaultHttpDestination
                .fromProperties(baseProperties)
                .uri("my-target.com")
                .proxyConfiguration(proxyConfiguration)
                .build();

        assertThat(destination.getProxyConfiguration()).containsExactly(proxyConfiguration);
        // sanity check
        assertThat(destination.get(DestinationProperty.PROXY_URI))
            .containsExactly(URI.create("http://overwritten.uri:5678"));
        assertThat(destination.get(DestinationProperty.PROXY_AUTH)).containsExactly("Bearer overwritten-token");
        assertThat(destination.get(DestinationProperty.PROXY_TYPE)).containsExactly(ProxyType.ON_PREMISE);
        // "fallback" properties ARE NOT overwritten - this is done for compatibility with Cloud SDK v4 behavior
        assertThat(destination.get(DestinationProperty.PROXY_HOST)).containsExactly("initial.host");
        assertThat(destination.get(DestinationProperty.PROXY_PORT)).containsExactly(4321);
    }

    @Test
    void testProxyConfigurationWithUnsupportedCredentials()
    {
        final URI uri = URI.create("foo.bar");
        final DefaultHttpDestination.Builder builder = DefaultHttpDestination.builder(uri);

        final Credentials[] unsupportedCredentials = { mock(ClientCertificate.class), mock(ClientCredentials.class) };

        for( final Credentials credentials : unsupportedCredentials ) {
            assertThatThrownBy(() -> builder.proxyConfiguration(ProxyConfiguration.of(uri, credentials)))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("The provided proxy credentials")
                .hasMessageContaining(String.format(" (%s) ", credentials.getClass().getSimpleName()))
                .hasMessageContaining("are not supported.");
        }
    }

    @Test
    void testBasicCredentialsAreEncodedInProxyAuth()
    {
        final URI uri = URI.create("foo.bar");

        final DefaultHttpDestination destination =
            DefaultHttpDestination
                .builder(uri)
                .proxyConfiguration(ProxyConfiguration.of(uri, new BasicCredentials("user", "pass")))
                .build();

        assertThat(destination.get(DestinationProperty.PROXY_AUTH))
            .containsExactly("Basic " + BasicAuthHeaderEncoder.encodeUserPasswordBase64("user", "pass"));
        assertThat(destination.get(DestinationProperty.PROXY_AUTH)).containsExactly("Basic dXNlcjpwYXNz"); // sanity check
    }

    @Test
    void testBearerPrefixIsAddedToProxyAuth()
    {
        final URI uri = URI.create("foo.bar");

        {
            // BearerCredentials without "Bearer" prefix
            final DefaultHttpDestination destination =
                DefaultHttpDestination
                    .builder(uri)
                    .proxyConfiguration(ProxyConfiguration.of(uri, new BearerCredentials("token")))
                    .build();

            assertThat(destination.get(DestinationProperty.PROXY_AUTH)).containsExactly("Bearer token");
        }

        {
            // BearerCredentials with "Bearer" prefix
            final DefaultHttpDestination destination =
                DefaultHttpDestination
                    .builder(uri)
                    .proxyConfiguration(ProxyConfiguration.of(uri, new BearerCredentials("Bearer token")))
                    .build();

            assertThat(destination.get(DestinationProperty.PROXY_AUTH)).containsExactly("Bearer token");
        }

        {
            // BearerCredentials with unexpected "Bearer" casing
            final DefaultHttpDestination destination =
                DefaultHttpDestination
                    .builder(uri)
                    .proxyConfiguration(ProxyConfiguration.of(uri, new BearerCredentials("bEArEr token")))
                    .build();

            assertThat(destination.get(DestinationProperty.PROXY_AUTH)).containsExactly("Bearer token");
        }

        {
            // BearerCredentials without whitespace after "Bearer" prefix
            final DefaultHttpDestination destination =
                DefaultHttpDestination
                    .builder(uri)
                    .proxyConfiguration(ProxyConfiguration.of(uri, new BearerCredentials("Bearertoken")))
                    .build();

            assertThat(destination.get(DestinationProperty.PROXY_AUTH)).containsExactly("Bearer Bearertoken");
        }
    }

    @Test
    void testToBuilderContainsAllProperties()
    {
        final Header header = new Header("StaticHeader", "value");
        final DestinationHeaderProvider headerProvider = mock(DestinationHeaderProvider.class);
        final KeyStore keyStore = mock(KeyStore.class);
        final KeyStore trustStore = mock(KeyStore.class);

        final DefaultHttpDestination baseDestination =
            DefaultHttpDestination
                .builder("foo.bar")
                .property("foo", "bar")
                .property("bar", 42)
                .header(header)
                .headerProviders(headerProvider)
                .keyStore(keyStore)
                .trustStore(trustStore)
                .trustAllCertificates()
                .build();

        final DefaultHttpDestination.Builder sut = baseDestination.toBuilder();
        assertThat(sut.get(DestinationProperty.URI)).containsExactly("foo.bar");
        assertThat(sut.get("foo", v -> (String) v)).containsExactly("bar");
        assertThat(sut.get("bar", v -> (int) v)).containsExactly(42);
        assertThat(sut.headers).containsExactly(header);
        assertThat(sut.customHeaderProviders).containsExactly(headerProvider);
        assertThat(sut.keyStore).isSameAs(keyStore);
        assertThat(sut.trustStore).isSameAs(trustStore);
        assertThat(sut.get(DestinationProperty.TRUST_ALL)).containsExactly(true);
    }

    @Test
    void testRefineHeaderProviders()
    {
        // simple
        {
            final var all =
                asList(
                    new ErpDestinationHeaderProvider(),
                    new ErpDestinationHeaderProvider(),
                    new ErpDestinationHeaderProvider());
            final var retained = DefaultHttpDestination.refineHeaderProviders(all);
            assertThat(retained).hasSize(1).hasExactlyElementsOfTypes(ErpDestinationHeaderProvider.class);
        }

        // complex
        {
            final var all =
                asList(
                    new ErpDestinationHeaderProvider(),
                    new ErpDestinationHeaderProvider(),
                    new DefaultHttpDestinationBuilderProxyHandler.SapConnectivityAuthenticationHeaderProvider(),
                    new DefaultHttpDestinationBuilderProxyHandler.SapConnectivityAuthenticationHeaderProvider(),
                    new DefaultHttpDestinationBuilderProxyHandler.SapConnectivityLocationIdHeaderProvider(),
                    new DefaultHttpDestinationBuilderProxyHandler.SapConnectivityLocationIdHeaderProvider());
            final var retained = DefaultHttpDestination.refineHeaderProviders(all);
            assertThat(retained)
                .hasSize(3)
                .hasExactlyElementsOfTypes(
                    ErpDestinationHeaderProvider.class,
                    DefaultHttpDestinationBuilderProxyHandler.SapConnectivityAuthenticationHeaderProvider.class,
                    DefaultHttpDestinationBuilderProxyHandler.SapConnectivityLocationIdHeaderProvider.class);
        }

        // lambda
        {
            final List<DestinationHeaderProvider> all =
                asList(
                    new ErpDestinationHeaderProvider(),
                    ( ctxt ) -> List.of(),
                    ( ctxt ) -> List.of(),
                    ( ctxt ) -> List.of());
            final var retained = DefaultHttpDestination.refineHeaderProviders(all);
            assertThat(retained).hasSize(4);
        }

        // custom
        {
            class MyHeaderProviderCardinatlityDefault implements DestinationHeaderProvider
            {
                @Nonnull
                @Override
                public List<Header> getHeaders( @Nonnull DestinationRequestContext requestContext )
                {
                    return List.of();
                }
            }
            class MyHeaderProviderCardinatlity1 implements DestinationHeaderProvider
            {
                @Getter
                private final int cardinality = 1;

                @Nonnull
                @Override
                public List<Header> getHeaders( @Nonnull DestinationRequestContext requestContext )
                {
                    return List.of();
                }
            }
            class MyHeaderProviderCardinatlity3 implements DestinationHeaderProvider
            {
                @Getter
                private final int cardinality = 3;

                @Nonnull
                @Override
                public List<Header> getHeaders( @Nonnull DestinationRequestContext requestContext )
                {
                    return List.of();
                }
            }
            final var all =
                asList(
                    new MyHeaderProviderCardinatlity1(), // remove
                    new MyHeaderProviderCardinatlity3(), // remove
                    new MyHeaderProviderCardinatlityDefault(), // retained
                    new MyHeaderProviderCardinatlity1(), // remove
                    new MyHeaderProviderCardinatlity3(), // retained
                    new MyHeaderProviderCardinatlityDefault(), // retained
                    new MyHeaderProviderCardinatlity1(), // remove
                    new MyHeaderProviderCardinatlity3(), // retained
                    new MyHeaderProviderCardinatlityDefault(), // retained
                    new MyHeaderProviderCardinatlity1(), // retained
                    new MyHeaderProviderCardinatlity3(), // retained
                    new MyHeaderProviderCardinatlityDefault());// retained
            final var retained = DefaultHttpDestination.refineHeaderProviders(all);
            assertThat(retained)
                .hasExactlyElementsOfTypes(
                    MyHeaderProviderCardinatlityDefault.class,
                    MyHeaderProviderCardinatlity3.class,
                    MyHeaderProviderCardinatlityDefault.class,
                    MyHeaderProviderCardinatlity3.class,
                    MyHeaderProviderCardinatlityDefault.class,
                    MyHeaderProviderCardinatlity1.class,
                    MyHeaderProviderCardinatlity3.class,
                    MyHeaderProviderCardinatlityDefault.class);
        }

        // extension: remove
        {
            final var all =
                asList(
                    new ErpDestinationHeaderProvider(),
                    new ErpDestinationHeaderProvider(),
                    new ErpDestinationHeaderProvider()
                    {
                        @Getter
                        private final int cardinality = 0;
                    });
            final var retained = DefaultHttpDestination.refineHeaderProviders(all);
            assertThat(retained).isEmpty();
        }
    }
}
