/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThatNoException;

import org.junit.Test;

public class ApacheHttpClient5CacheBuilderTest
{
    @Test
    public void testBuilderContainsOptionalParametersOnly()
    {
        // make sure we can build a new cache instance without supplying any parameters
        assertThatNoException().isThrownBy(() -> new ApacheHttpClient5CacheBuilder().build());
    }
}
