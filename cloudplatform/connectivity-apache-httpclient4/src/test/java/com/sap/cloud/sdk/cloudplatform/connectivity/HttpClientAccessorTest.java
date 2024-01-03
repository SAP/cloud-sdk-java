/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HttpClientAccessorTest
{
    @Test
    void testDefaultHttpClientCache()
    {
        assertThat(HttpClientAccessor.getHttpClientCache()).isExactlyInstanceOf(DefaultHttpClientCache.class);
    }
}
