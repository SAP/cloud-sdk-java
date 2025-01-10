package com.sap.cloud.sdk.cloudplatform.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BasicCredentialsTest
{

    @Test
    void testHttpHeaderValue()
    {
        assertThat(new BasicCredentials("user", "pass").getHttpHeaderValue()).isEqualTo("Basic dXNlcjpwYXNz");
    }
}
