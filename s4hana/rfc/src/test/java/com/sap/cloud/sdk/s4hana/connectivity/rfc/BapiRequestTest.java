/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.Header;

@Deprecated
class BapiRequestTest
{
    private static final String FUNCTION_NAME = "BAPI_SOMETHING";

    @Test
    void testInvalidFunctionName()
    {
        assertThatThrownBy(() -> new BapiRequest("A_RFC")).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testInvalidFunctionNameWithCommittingConstructor()
    {
        assertThatThrownBy(() -> new BapiRequest("A_RFC", false)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testHeaderParameters()
    {
        final BapiRequest request = new BapiRequest(FUNCTION_NAME, false).withHeader("key", "value");

        assertThat(request.getCustomHttpHeaders()).contains(new Header("key", "value"));
    }

    @Test
    void testHeaderParametersCopy()
    {
        final BapiRequest firstRequest = new BapiRequest(FUNCTION_NAME, false).withHeader("someHeader", "someValue");

        final BapiRequest secondRequest =
            new BapiRequest(FUNCTION_NAME, false).withSameCustomHttpHeadersAs(firstRequest);

        assertThat(secondRequest.getCustomHttpHeaders()).contains(new Header("someHeader", "someValue"));
    }
}
