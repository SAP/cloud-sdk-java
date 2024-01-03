/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.assertj.vavr.api.VavrAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lombok.EqualsAndHashCode;
import lombok.Getter;

class DestinationPropertyKeyTest
{
    private static final String TEST_VALUE = "testValue";
    private static final String PROPERTY_TEST_KEY = "testKey";
    private static final TestProperty TEST_PROPERTY_INSTANCE = new TestProperty(TEST_VALUE);

    private DestinationPropertyKey<TestProperty> PROPERTY_TEST;
    private Map<String, Object> properties;
    private DestinationProperties sut;

    @BeforeEach
    void before()
    {
        PROPERTY_TEST =
            DestinationPropertyKey.createProperty(PROPERTY_TEST_KEY, TestProperty.class, TestProperty::ofIdentifier);

        properties = new HashMap<>();
    }

    @AfterEach
    void after()
    {
        properties = null;
        sut = null;
    }

    @Test
    void testClassCast()
    {
        properties.put(PROPERTY_TEST.getKeyName(), TEST_PROPERTY_INSTANCE);

        sut = DefaultDestination.fromMap(properties).build();

        VavrAssertions.assertThat(sut.get(PROPERTY_TEST)).contains(TEST_PROPERTY_INSTANCE);
    }

    @Test
    void testStringFallback()
    {
        properties.put(PROPERTY_TEST.getKeyName(), TEST_VALUE);

        sut = DefaultDestination.fromMap(properties).build();

        VavrAssertions.assertThat(sut.get(PROPERTY_TEST).map(TestProperty::getTestField)).contains(TEST_VALUE);
    }

    @Test
    void testFailingFallback()
    {
        properties.put(PROPERTY_TEST.getKeyName(), 42);

        sut = DefaultDestination.fromMap(properties).build();

        assertThatThrownBy(() -> sut.get(PROPERTY_TEST)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testNullValue()
    {
        properties.put(PROPERTY_TEST.getKeyName(), null);

        sut = DefaultDestination.fromMap(properties).build();

        VavrAssertions.assertThat(sut.get(PROPERTY_TEST)).isEmpty();
    }

    @EqualsAndHashCode
    static final class TestProperty
    {
        @Getter
        private final String testField;

        TestProperty( final String testField )
        {
            this.testField = testField;
        }

        static TestProperty ofIdentifier( @Nonnull final String str )
            throws IllegalArgumentException
        {
            if( !str.isEmpty() ) {
                return new TestProperty(str);
            } else {
                throw new IllegalArgumentException("Empty string.");
            }
        }
    }
}
