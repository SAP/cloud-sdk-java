package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.junit.jupiter.api.Test;

import com.sap.cloud.security.xsuaa.client.OAuth2ServiceEndpointsProvider;

class OAuth2PropertySupplierTest
{

    @Test
    void testXsuaaTokenEndpoints()
    {
        final OAuth2ServiceEndpointsProvider sut =
            OAuth2PropertySupplier.DefaultTokenEndpoints.forXsuaa(URI.create("https://foo.bar/baz"));

        assertThat(sut.getTokenEndpoint()).hasToString("https://foo.bar/baz/oauth/token");
        assertThat(sut.getAuthorizeEndpoint()).hasToString("https://foo.bar/baz/oauth/authorize");
        assertThat(sut.getJwksUri()).hasToString("https://foo.bar/baz/token_keys");
    }

    @Test
    void testIasTokenEndpoints()
    {
        final OAuth2ServiceEndpointsProvider sut =
            OAuth2PropertySupplier.DefaultTokenEndpoints.forIas(URI.create("https://foo.bar/baz"));

        assertThat(sut.getTokenEndpoint()).hasToString("https://foo.bar/baz/oauth2/token");
        assertThat(sut.getAuthorizeEndpoint()).hasToString("https://foo.bar/baz/oauth2/authorize");
        assertThat(sut.getJwksUri()).hasToString("https://foo.bar/baz/token_keys");
    }
}
