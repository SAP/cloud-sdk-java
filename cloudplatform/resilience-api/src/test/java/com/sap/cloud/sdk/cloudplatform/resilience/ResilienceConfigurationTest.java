package com.sap.cloud.sdk.cloudplatform.resilience;

import static com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration.CacheConfiguration;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.thread.Property;

class ResilienceConfigurationTest
{
    @Test
    void testCacheConfigurationSerializable()
    {
        final CacheConfiguration.CacheConfigurationBuilder builder = CacheConfiguration.of(Duration.ofDays(1));

        // no parameters

        final CacheConfiguration configNoParameters = builder.withoutParameters();
        assertThat(configNoParameters.serializable()).isTrue();
        assertThat(configNoParameters.expirationStrategy()).isEqualTo(CacheConfiguration.DEFAULT_EXPIRATION_STRATEGY);
        assertThat(configNoParameters).isEqualTo(builder.withoutParameters());
        assertThat(configNoParameters).hasSameHashCodeAs(builder.withoutParameters());

        // serializable parameters

        final CacheConfiguration configSerializableParameter = builder.withParameters("one");
        assertThat(configSerializableParameter.serializable()).isTrue();
        assertThat(configSerializableParameter).isEqualTo(builder.withParameters("one"));
        assertThat(configSerializableParameter).hasSameHashCodeAs(builder.withParameters("one"));

        final CacheConfiguration configSerializableParameters = builder.withParameters("one", "two", "three");
        assertThat(configSerializableParameters.serializable()).isTrue();
        assertThat(configSerializableParameters).isNotEqualTo(configSerializableParameter);
        assertThat(configSerializableParameters).doesNotHaveSameHashCodeAs(configSerializableParameter);

        // non-serializable parameters

        final CacheConfiguration configNonSerializableParameter =
            builder.withParameters(Property.ofConfidential("secret"));
        assertThat(configNonSerializableParameter.serializable()).isFalse();
        assertThat(configNonSerializableParameter).isEqualTo(builder.withParameters(Property.ofConfidential("secret")));
        assertThat(configNonSerializableParameter)
            .hasSameHashCodeAs(builder.withParameters(Property.ofConfidential("secret")));

        final CacheConfiguration configNonSerializableParameters =
            builder.withParameters(Property.ofConfidential("secret"), Property.of("public"));
        assertThat(configNonSerializableParameters.serializable()).isFalse();
        assertThat(configNonSerializableParameters).isNotEqualTo(configNonSerializableParameter);
        assertThat(configNonSerializableParameters).doesNotHaveSameHashCodeAs(configNonSerializableParameter);
    }

    @Test
    void testEqualsAndHashCode()
    {
        final ResilienceConfiguration config1 = ResilienceConfiguration.of("foo");
        final ResilienceConfiguration config2 = ResilienceConfiguration.of("foo");
        assertThat(config1).isEqualTo(config2);
        assertThat(config1).hasSameHashCodeAs(config2);

        // retry is disabled by default, so test it individually here
        assertThat(ResilienceConfiguration.RetryConfiguration.of(5))
            .isEqualTo(ResilienceConfiguration.RetryConfiguration.of(5));
        assertThat(ResilienceConfiguration.RetryConfiguration.of(5))
            .hasSameHashCodeAs(ResilienceConfiguration.RetryConfiguration.of(5));
    }
}
