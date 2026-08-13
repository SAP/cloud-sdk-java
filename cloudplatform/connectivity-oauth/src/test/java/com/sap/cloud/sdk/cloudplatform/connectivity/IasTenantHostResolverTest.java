package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URI;

import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;

@WireMockTest
class IasTenantHostResolverTest
{
    private static final String TENANT_ID = "c992fd47-9eb4-4a7c-b932-9a8479d6c69f";

    private static final String RESPONSE = """
        {
          "status": "ACTIVE",
          "zoneId": "c992fd47-9eb4-4a7c-b932-9a8479d6c69f",
          "subaccountId": "c992fd47-9eb4-4a7c-b932-9a8479d6c69f",
          "subdomain": "btp-subaccount-subdomain",
          "authorization_endpoint": "https://test.accounts400.ondemand.com/oauth2/authorize",
          "token_endpoint": "https://test.accounts400.ondemand.com/oauth2/token",
          "userinfo_endpoint": "https://test.accounts400.ondemand.com/oauth2/userinfo",
          "end_session_endpoint": "https://test.accounts400.ondemand.com/oauth2/logout",
          "oidc_metadata": "https://test.accounts400.ondemand.com/.well-known/openid-configuration",
          "app_tid": "c992fd47-9eb4-4a7c-b932-9a8479d6c69f",
          "partitionedCookies": true
        }
        """;

    @Test
    void testResolveReturnsSubdomainFromTokenEndpoint( final WireMockRuntimeInfo wm )
    {
        stubFor(
            get(urlPathEqualTo("/sap/rest/tenantLoginInfo"))
                .withQueryParam("id", equalTo(TENANT_ID))
                .willReturn(okJson(RESPONSE)));

        final URI btpTenantApiUri = URI.create(wm.getHttpBaseUrl());

        assertThat(IasTenantHostResolver.DEFAULT_INSTANCE.resolve(btpTenantApiUri, TENANT_ID)).isEqualTo("test");

        verify(
            1,
            getRequestedFor(urlPathEqualTo("/sap/rest/tenantLoginInfo")).withQueryParam("id", equalTo(TENANT_ID)));
    }

    @Test
    void testResolveThrowsOnNonOkResponse( final WireMockRuntimeInfo wm )
    {
        stubFor(get(urlPathEqualTo("/sap/rest/tenantLoginInfo")).willReturn(serverError()));

        final URI btpTenantApiUri = URI.create(wm.getHttpBaseUrl());

        assertThatThrownBy(() -> IasTenantHostResolver.DEFAULT_INSTANCE.resolve(btpTenantApiUri, TENANT_ID))
            .isInstanceOf(DestinationAccessException.class)
            .hasMessageContaining("status code 500")
            .hasMessageContaining(TENANT_ID);
    }

    @Test
    void testExtractSubdomainFromTokenEndpointParsesFirstHostLabel()
    {
        assertThat(IasTenantHostResolver.extractSubdomainFromTokenEndpoint(RESPONSE)).isEqualTo("test");
    }

    @Test
    void testExtractSubdomainThrowsWhenTokenEndpointMissing()
    {
        final String body = """
            { "status": "ACTIVE", "zoneId": "c992fd47-9eb4-4a7c-b932-9a8479d6c69f" }
            """;
        assertThatThrownBy(() -> IasTenantHostResolver.extractSubdomainFromTokenEndpoint(body))
            .isInstanceOf(DestinationAccessException.class);
    }

    @Test
    void testExtractSubdomainThrowsWhenTokenEndpointHasNoHost()
    {
        final String body = """
            { "token_endpoint": "not-a-valid-url" }
            """;
        assertThatThrownBy(() -> IasTenantHostResolver.extractSubdomainFromTokenEndpoint(body))
            .isInstanceOf(DestinationAccessException.class);
    }
}
