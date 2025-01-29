package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.github.tomakehurst.wiremock.client.WireMock.absent;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.assertj.vavr.api.VavrAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.HttpClientInstantiationException;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

@WireMockTest
class CookieCachingTest
{
    private Destination destination;

    @BeforeEach
    void setupDestination( @Nonnull final WireMockRuntimeInfo wm )
    {
        destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
    }

    @Test
    void testCachingWithDifferentTenants()
        throws IOException
    {
        final String tenantId1 = "t1";
        final String tenantId2 = "t2";
        stubFor(
            get(urlPathEqualTo("/getCookies"))
                .withQueryParam("tenant", absent())
                .willReturn(ok().withHeader("Set-Cookie", "cookie1=val1", "tenant=none")));
        stubFor(
            get(urlPathEqualTo("/getCookies"))
                .withQueryParam("tenant", equalTo(tenantId1))
                .willReturn(ok().withHeader("Set-Cookie", "cookie1=val1", "tenant=t1")));
        stubFor(
            get(urlPathEqualTo("/getCookies"))
                .withQueryParam("tenant", equalTo(tenantId2))
                .willReturn(ok().withHeader("Set-Cookie", "cookie1=val1", "tenant=t2")));
        stubFor(get(urlPathEqualTo("/testCookies")).willReturn(ok()));

        final Tenant t1 = () -> tenantId1;
        final Tenant t2 = () -> tenantId2;

        // sanity check: No tenant present
        VavrAssertions.assertThat(TenantAccessor.tryGetCurrentTenant()).isFailure();

        HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);
        httpClient.execute(new HttpGet("getCookies"));
        httpClient = HttpClientAccessor.getHttpClient(destination);
        httpClient.execute(new HttpGet("testCookies"));

        TenantAccessor.executeWithTenant(t1, () -> {
            final String param = "?tenant=" + TenantAccessor.getCurrentTenant().getTenantId();
            HttpClient client = HttpClientAccessor.getHttpClient(destination);
            client.execute(new HttpGet("getCookies" + param));
            client = HttpClientAccessor.getHttpClient(destination);
            client.execute(new HttpGet("testCookies" + param));
        });

        TenantAccessor.executeWithTenant(t2, () -> {
            final String param = "?tenant=" + TenantAccessor.getCurrentTenant().getTenantId();
            HttpClient client = HttpClientAccessor.getHttpClient(destination);
            client.execute(new HttpGet("getCookies" + param));
            client = HttpClientAccessor.getHttpClient(destination);
            client.execute(new HttpGet("testCookies" + param));
        });

        verify(getRequestedFor(urlEqualTo("/getCookies")).withCookie("cookie1", absent()));

        verify(
            1,
            getRequestedFor(urlPathEqualTo("/testCookies"))
                .withCookie("cookie1", equalTo("val1"))
                .withCookie("tenant", equalTo("none"))
                .withQueryParam("tenant", absent()));
        verify(
            1,
            getRequestedFor(urlPathEqualTo("/testCookies"))
                .withCookie("cookie1", equalTo("val1"))
                .withCookie("tenant", equalTo(tenantId1))
                .withQueryParam("tenant", equalTo(t1.getTenantId())));
        verify(
            1,
            getRequestedFor(urlPathEqualTo("/testCookies"))
                .withCookie("cookie1", equalTo("val1"))
                .withCookie("tenant", equalTo(tenantId2))
                .withQueryParam("tenant", equalTo(t2.getTenantId())));
    }

    @Test
    void testCustomCache()
        throws IOException
    {
        stubFor(
            get(urlPathEqualTo("/getCookies"))
                .willReturn(ok().withHeader("Set-Cookie", "cookie1=val1", "tenant=none")));
        final HttpClientFactory defaultFactory = HttpClientAccessor.getHttpClientFactory();
        HttpClientAccessor.setHttpClientFactory(new NoCookiesHttpClientFactory());

        final HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);
        final HttpResponse response = httpClient.execute(new HttpGet("getCookies"));
        final Header firstHeader = response.getFirstHeader("Set-Cookie");

        assertThat(Arrays.stream(firstHeader.getElements()).findFirst().get().toString()).isEqualTo("cookie1=val1");

        HttpClientAccessor.setHttpClientFactory(defaultFactory);
    }

    private static class NoCookiesHttpClientFactory extends DefaultHttpClientFactory
    {
        @Override
        protected HttpClientBuilder getHttpClientBuilder( @Nullable final HttpDestinationProperties destination )
            throws HttpClientInstantiationException
        {
            return super.getHttpClientBuilder(destination).disableCookieManagement();
        }
    }

}
