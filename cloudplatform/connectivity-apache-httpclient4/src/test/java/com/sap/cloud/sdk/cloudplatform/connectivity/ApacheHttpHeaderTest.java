/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ApacheHttpHeaderTest
{
    @Test
    void testToString()
    {
        final Header header = new Header("key", "value");

        final HttpClientWrapper.ApacheHttpHeader apacheHttpHeader = new HttpClientWrapper.ApacheHttpHeader(header);

        assertThat(apacheHttpHeader.toString()).isEqualTo("ApacheHttpHeader(header=Header(name=key, value=(hidden)))");
    }
}
