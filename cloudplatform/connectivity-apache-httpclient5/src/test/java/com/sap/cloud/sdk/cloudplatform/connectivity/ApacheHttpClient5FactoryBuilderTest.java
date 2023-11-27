/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThatNoException;

import org.junit.jupiter.api.Test;

class ApacheHttpClient5FactoryBuilderTest
{

    @Test
    void testBuilderContainsOptionalParametersOnly()
    {
        // make sure we can build a new factory instance without supplying any parameters
        assertThatNoException().isThrownBy(() -> new ApacheHttpClient5FactoryBuilder().build());
    }
}
