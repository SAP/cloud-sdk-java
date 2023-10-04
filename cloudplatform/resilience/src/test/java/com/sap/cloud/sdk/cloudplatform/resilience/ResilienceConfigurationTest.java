/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.resilience;

import static com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration.CacheConfiguration;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.Test;

import com.sap.cloud.sdk.cloudplatform.thread.Property;

public class ResilienceConfigurationTest
{
    @Test
    public void testCacheConfigurationSerializable()
    {
        final CacheConfiguration.CacheConfigurationBuilder builder = CacheConfiguration.of(Duration.ofDays(1));

        // no parameters

        final CacheConfiguration configNoParameters = builder.withoutParameters();
        assertThat(configNoParameters.serializable()).isTrue();
        assertThat(configNoParameters.expirationStrategy()).isEqualTo(CacheConfiguration.DEFAULT_EXPIRATION_STRATEGY);

        // serializable parameters

        final CacheConfiguration configSerializableParameter = builder.withParameters("one");
        assertThat(configSerializableParameter.serializable()).isTrue();

        final CacheConfiguration configSerializableParameters = builder.withParameters("one", "two", "three");
        assertThat(configSerializableParameters.serializable()).isTrue();

        // non-serializable parameters

        final CacheConfiguration configNonSerializableParameter =
            builder.withParameters(Property.ofConfidential("secret"));
        assertThat(configNonSerializableParameter.serializable()).isFalse();

        final CacheConfiguration configNonSerializableParameters =
            builder.withParameters(Property.ofConfidential("secret"), Property.of("public"));
        assertThat(configNonSerializableParameters.serializable()).isFalse();
    }
}
