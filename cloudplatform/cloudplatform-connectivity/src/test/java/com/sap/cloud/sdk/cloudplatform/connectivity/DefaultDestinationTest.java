/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

class DefaultDestinationTest
{
    @Test
    void testSimpleConstruction()
    {
        final String someKey = "someKey";
        final String someValue = "someValue";

        final DestinationProperties destination = DefaultDestination.builder().property(someKey, someValue).build();

        assertThat(destination.get(someKey)).contains(someValue);
    }

    @Test
    void testIdentity()
    {
        final Destination destination =
            DefaultDestination.builder().property("Name", "foo").property("URL", "http://bar.com").build();

        assertThat(destination.isHttp()).isTrue();
        assertThat(destination.isRfc()).isTrue();
        assertThat(destination.get(DestinationProperty.TYPE)).isEmpty();

        assertThat(destination.asHttp()).isEqualTo(destination.asHttp());
        assertThat(destination.asHttp()).hasSameHashCodeAs(destination.asHttp());

        assertThat(destination.asRfc()).isEqualTo(destination.asRfc());
        assertThat(destination.asRfc()).hasSameHashCodeAs(destination.asRfc());
    }

    @Test
    void testToString()
    {
        final Destination dest = DefaultDestination.builder().name("foo").property("some_password", "bar").build();
        assertThat(dest).hasToString("DefaultDestination(properties={some_password=(hidden), Name=foo})");
    }

    @Test
    void testGetPropertyNames()
    {
        final String someKey = "someKey";
        final String someValue = "someValue";
        final DestinationProperties destination = DefaultDestination.builder().property(someKey, someValue).build();
        Iterable<String> propertiesKeysResult = destination.getPropertyNames();
        Iterable<String> expectedKeys = Arrays.asList(someKey);
        assertThat(propertiesKeysResult).containsAll(expectedKeys);
    }

    @Test
    void testMissingKeyReturnsEmptyOptional()
    {
        final DestinationProperties destination = DefaultDestination.builder().build();

        assertThat(destination.get("someKey")).isEmpty();
    }

    @Test
    void testCaseInsensitiveKey()
    {
        final DestinationProperties destination = DefaultDestination.builder().property("foo", "bar").build();

        assertThat(destination.get("FoO")).contains("bar");
    }

    @Test
    void testModifiedMapDoesntInfluenceDestination()
    {
        final String someKey = "someKey";
        final String originalValue = "someValue";

        final HashMap<String, Object> properties = new HashMap<>();
        properties.put(someKey, originalValue);

        final DestinationProperties destination = DefaultDestination.fromMap(properties).build();

        // should not change the contents of the destination
        properties.put(someKey, "someOtherValue");

        assertThat(destination.get(someKey)).contains(originalValue);
    }

    @Test
    void testEqualsIsImplemented()
    {
        final DefaultDestination firstDestination = DefaultDestination.builder().build();
        final DefaultDestination secondDestination = DefaultDestination.builder().build();

        assertThat(firstDestination).isEqualTo(secondDestination).isNotSameAs(secondDestination);
    }

    @Test
    void testHashCodeIsImplemented()
    {
        final DefaultDestination firstDestination = DefaultDestination.builder().build();
        final DefaultDestination secondDestination = DefaultDestination.builder().build();

        assertThat(firstDestination).hasSameHashCodeAs(secondDestination);
    }

    @Test
    void testHttpConverter()
    {
        final Destination destination =
            DefaultDestination
                .builder()
                .property(DestinationProperty.URI, URI.create("https://www.sap.de").toString())
                .build();

        final HttpDestination httpDestination = destination.asHttp();

        assertThat(httpDestination).isInstanceOf(DefaultHttpDestination.class);
    }

    @Test
    void testRfcConverter()
    {
        final Destination destination = DefaultDestination.builder().property("Name", "someName").build();

        final RfcDestination rfcDestination = destination.asRfc();

        assertThat(rfcDestination).isInstanceOf(DefaultRfcDestination.class);
    }

}
