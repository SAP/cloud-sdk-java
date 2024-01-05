/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BasicAuthHeaderEncoderTest
{
    @Test
    void testEncodeUserNamePassword()
    {
        final String username = "63b2dc0c-5c37-30a3-af7b-cc40c305484d";
        final String password = "1db8ec85-75b4-3593-9035-dc3b5b1aca73";

        final String expected =
            "NjNiMmRjMGMtNWMzNy0zMGEzLWFmN2ItY2M0MGMzMDU0ODRkOjFkYjhlYzg1LTc1YjQtMzU5My05MDM1LWRjM2I1YjFhY2E3Mw==";

        final String result = BasicAuthHeaderEncoder.encodeUserPasswordBase64(username, password);
        assertThat(result).isEqualTo(expected);
    }
}
