package com.sap.cloud.sdk.cloudplatform.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BearerCredentialsTest
{
    @Test
    void testBearerPrefixIsRemovedFromToken()
    {
        assertThat(new BearerCredentials("Bearer token").getToken()).isEqualTo("token");
        assertThat(new BearerCredentials("bearer token").getToken()).isEqualTo("token");
        assertThat(new BearerCredentials("BEARER token").getToken()).isEqualTo("token");
        assertThat(new BearerCredentials("bEaReR token").getToken()).isEqualTo("token");
        assertThat(new BearerCredentials(" Bearer  token").getToken()).isEqualTo("token");
        assertThat(new BearerCredentials("\tBearer \ttoken").getToken()).isEqualTo("token");
        assertThat(new BearerCredentials("token").getToken()).isEqualTo("token");
    }

    @Test
    void testBearerPrefixRequiresAWhitespace()
    {
        assertThat(new BearerCredentials("Bearertoken").getToken()).isEqualTo("Bearertoken");
    }

    @Test
    void testTokenIsTrimmed()
    {
        assertThat(new BearerCredentials("token").getToken()).isEqualTo("token");
        assertThat(new BearerCredentials(" token ").getToken()).isEqualTo("token");
        assertThat(new BearerCredentials("\ttoken\n").getToken()).isEqualTo("token");
    }

    @Test
    void testBearerPrefixIsAddedToHttpHeaderValue()
    {
        assertThat(new BearerCredentials("token").getHttpHeaderValue()).isEqualTo("Bearer token");
    }

    @Test
    void testHttpHeaderValueIsTrimmed()
    {
        assertThat(new BearerCredentials(" token ").getHttpHeaderValue()).isEqualTo("Bearer token");
        assertThat(new BearerCredentials("bearer token ").getHttpHeaderValue()).isEqualTo("Bearer token");
        assertThat(new BearerCredentials("BEARER token ").getHttpHeaderValue()).isEqualTo("Bearer token");
        assertThat(new BearerCredentials(" BEARER token ").getHttpHeaderValue()).isEqualTo("Bearer token");
    }
}
