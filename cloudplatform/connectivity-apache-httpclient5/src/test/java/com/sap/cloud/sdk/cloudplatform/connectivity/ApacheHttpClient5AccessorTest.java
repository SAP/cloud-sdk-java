/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ApacheHttpClient5AccessorTest
{
    @Test
    public void testDefaultHttpClientCache()
    {
        assertThat(ApacheHttpClient5Accessor.getHttpClientCache())
            .isExactlyInstanceOf(DefaultApacheHttpClient5Cache.class);
    }

    @Test
    public void testDefaultHttpClientFactory()
    {
        assertThat(ApacheHttpClient5Accessor.getHttpClientFactory())
            .isExactlyInstanceOf(DefaultApacheHttpClient5Factory.class);
    }
}
