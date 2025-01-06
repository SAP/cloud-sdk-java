/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.thread;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;

import io.vavr.control.Try;

class PropertyTest
{
    @Test
    void testEquality()
    {
        assertThat(Property.of(Try.success("MyValue"))).isEqualTo(Property.of(Try.success("MyValue")));
        assertThat(Property.ofConfidential(Try.success("MyValue")))
            .isEqualTo(Property.ofConfidential(Try.success("MyValue")));

        final Exception exception = new Exception();
        assertThat(Property.of(Try.failure(exception))).isEqualTo(Property.of(Try.failure(exception)));
        assertThat(Property.ofConfidential(Try.failure(exception)))
            .isEqualTo(Property.ofConfidential(Try.failure(exception)));

        assertThat(Property.of(Try.success("MyValue"))).isNotEqualTo(Property.ofConfidential(Try.success("MyValue")));
        assertThat(Property.of(Try.failure(exception))).isNotEqualTo(Property.ofConfidential(Try.failure(exception)));
    }

    @Test
    void testConfidentialToString()
    {
        assertThat(Property.of("my value")).hasToString("Property(value=Success(my value))");
        assertThat(Property.ofTry(Try.failure(new ShouldNotHappenException("my message"))))
            .hasToString(
                "Property(value=Failure(com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException: my message))");

        assertThat(Property.ofConfidential("my secret")).hasToString("Property(value=(hidden))");
        assertThat(
            Property.ofConfidentialTry(Try.failure(new ShouldNotHappenException("my secret message"))).toString())
            .isEqualTo("Property(value=(hidden))");
    }
}
