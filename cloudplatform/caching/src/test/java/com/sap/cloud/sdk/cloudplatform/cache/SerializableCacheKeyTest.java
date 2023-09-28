package com.sap.cloud.sdk.cloudplatform.cache;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;
import org.assertj.core.util.Lists;
import org.junit.Test;

import io.vavr.control.Option;

public class SerializableCacheKeyTest
{

    @Test
    public void testSerializableCacheKey()
    {
        final String tenant = "tenantOrZone1";
        final String principal = "principal1";

        final SerializableCacheKey key = SerializableCacheKey.of(tenant, principal);
        assertThat(key).isInstanceOf(Serializable.class);

        // without additional parameters
        {
            final byte[] bytes = SerializationUtils.serialize(key);
            final SerializableCacheKey deserializedKey = SerializationUtils.deserialize(bytes);
            assertThat(deserializedKey.getComponents()).isEmpty();
            assertThat(deserializedKey.getTenantId()).isEqualTo(Option.some(tenant));
            assertThat(deserializedKey.getPrincipalId()).isEqualTo(Option.some(principal));
            assertThat(deserializedKey).isEqualTo(key).hasSameHashCodeAs(key);
        }

        // with additional parameters
        final List<Serializable> parameters = Lists.newArrayList("foo", "bar", 123, LocalDate.now());
        key.append(parameters);
        {
            final byte[] bytes = SerializationUtils.serialize(key);
            final SerializableCacheKey deserializedKey = SerializationUtils.deserialize(bytes);
            assertThat(deserializedKey.getComponents()).isEqualTo(parameters);
            assertThat(deserializedKey.getTenantId()).isEqualTo(Option.some(tenant));
            assertThat(deserializedKey.getPrincipalId()).isEqualTo(Option.some(principal));
            assertThat(deserializedKey).isEqualTo(key).hasSameHashCodeAs(key);
        }
    }
}
