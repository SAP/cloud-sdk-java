/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.security.KeyStore;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class DefaultHttpDestinationBuilderTest
{
    @Test
    void testFromMap()
    {
        final Map<String, Object> map = new HashMap<>();
        map.put("foo", "bar");
        map.put("bar", 42);

        final DefaultHttpDestination.Builder sut = DefaultHttpDestination.fromMap(map);
        assertThat(sut.get("foo", v -> (String) v)).containsExactly("bar");
        assertThat(sut.get("bar", v -> (int) v)).containsExactly(42);
    }

    @Test
    void testFromProperties()
    {
        final DefaultDestination baseProperties =
            DefaultDestination.builder().property("foo", "bar").property("bar", 42).build();

        final DefaultHttpDestination.Builder sut = DefaultHttpDestination.fromProperties(baseProperties);
        assertThat(sut.get("foo", v -> (String) v)).containsExactly("bar");
        assertThat(sut.get("bar", v -> (int) v)).containsExactly(42);
    }

    @Test
    void testFromPropertiesWithDefaultHttpDestination()
    {
        final Header header = new Header("StaticHeader", "value");
        final DefaultHttpDestination baseDestination =
            DefaultHttpDestination
                .builder("foo.bar")
                .property("foo", "bar")
                .property("bar", 42)
                .header(header)
                .headerProviders(any -> Collections.emptyList())
                .build();

        final DefaultHttpDestination.Builder sut = DefaultHttpDestination.fromProperties(baseDestination);
        assertThat(sut.get(DestinationProperty.URI)).containsExactly("foo.bar");
        assertThat(sut.get("foo", v -> (String) v)).containsExactly("bar");
        assertThat(sut.get("bar", v -> (int) v)).containsExactly(42);
        assertThat(sut.headers).containsExactly(header);
        assertThat(sut.customHeaderProviders).isNotEmpty();
    }

    @Test
    void testFromDestinationWithDefaultDestination()
    {
        final DefaultDestination baseProperties =
            DefaultDestination.builder().property("foo", "bar").property("bar", 42).build();

        final DefaultHttpDestination.Builder sut = DefaultHttpDestination.fromDestination(baseProperties);
        assertThat(sut.get("foo", v -> (String) v)).containsExactly("bar");
        assertThat(sut.get("bar", v -> (int) v)).containsExactly(42);
    }

    @Test
    void testFromDestination()
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

        final DefaultHttpDestination.Builder sut = DefaultHttpDestination.fromDestination(baseDestination);
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
    void testFromDestinationThrowsOnIllegalInputType()
    {
        assertThatThrownBy(() -> DefaultHttpDestination.fromDestination(mock(Destination.class)))
            .isExactlyInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("The provided destination is not supported");
    }
}
