/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class DefaultDestinationBuilderTest
{
    @Test
    public void testFromMap()
    {
        final Map<String, Object> map = new HashMap<>();
        map.put("foo", "bar");
        map.put("bar", 42);

        final DefaultDestination.Builder sut = DefaultDestination.fromMap(map);
        assertThat(sut.get("foo", v -> (String) v)).containsExactly("bar");
        assertThat(sut.get("bar", v -> (int) v)).containsExactly(42);
    }

    @Test
    public void testToBuilder()
    {
        final DefaultDestination baseProperties =
            DefaultDestination.builder().property("foo", "bar").property("bar", 42).build();

        final DefaultDestination.Builder sut = baseProperties.toBuilder();
        assertThat(sut.properties).containsExactlyInAnyOrderEntriesOf(new HashMap<String, Object>()
        {
            {
                put("foo", "bar");
                put("bar", 42);
            }
        });
    }
}
