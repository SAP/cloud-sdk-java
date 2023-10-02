/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static java.util.Arrays.asList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import javax.annotation.Nonnull;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.sap.cloud.sdk.cloudplatform.CloudPlatformAccessor;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.security.ClientCredentials;
import com.sap.cloud.sdk.cloudplatform.security.OAuth2ServiceSettings;
import com.sap.cloud.sdk.cloudplatform.security.OAuth2TokenServiceCache;
import com.sap.cloud.sdk.cloudplatform.tenant.ScpCfTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.security.config.Service;
import com.sap.cloud.security.test.JwtGenerator;
import com.sap.cloud.security.xsuaa.client.DefaultOAuth2TokenService;

import lombok.SneakyThrows;
import lombok.experimental.Delegate;

public class XsuaaServiceTest
{
    private static final URI xsuaaUri = URI.create("http://provider.domain/");
    private static final ClientCredentials credentials = new ClientCredentials("clientId", "clientSecret");

    private final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);

    @SneakyThrows
    @Before
    public void before()
    {
        CloudPlatformAccessor.setCloudPlatformFacade(null);
        AuthTokenAccessor.setAuthTokenFacade(null);
        TenantAccessor.setTenantFacade(null);

        final Function<Integer, String> generatePayload =
            i -> String.format("{\"access_token\":\"a_tok%s\",\"refresh_token\":\"r_tok%s\",\"expires_in\":100}", i, i);

        final AtomicInteger counter = new AtomicInteger(0);
        when(httpClient.execute(any(HttpPost.class)))
            .thenAnswer(invocation -> new MockHttpResponse(200, generatePayload.apply(counter.getAndIncrement())));
    }

    @AfterClass
    public static void cleanUp()
    {
        CloudPlatformAccessor.setCloudPlatformFacade(null);
        AuthTokenAccessor.setAuthTokenFacade(null);
        TenantAccessor.setTenantFacade(null);
    }

    @SneakyThrows
    @Test
    public void getCachedClientCredentialsToken()
    {
        // setup test, enforce our mocked HttpClient
        com.sap.cloud.security.client.HttpClientFactory.services.clear();
        com.sap.cloud.security.client.HttpClientFactory.services.add(0, identity -> httpClient);

        // prepare test
        final OAuth2TokenServiceCache cache = OAuth2TokenServiceCache.create();
        final XsuaaService service = new XsuaaService(cache);
        final OAuth2ServiceSettings settings = OAuth2ServiceSettings.ofBaseUri(xsuaaUri).build();

        // user code
        final AccessToken token1 = service.retrieveAccessTokenViaClientCredentialsGrant(settings, credentials, false);

        // sanity check
        verify(httpClient, times(1)).execute(any());

        // user code
        final AccessToken token2 = service.retrieveAccessTokenViaClientCredentialsGrant(settings, credentials, false);

        // assertion: tokens are cached
        assertThat(token2).isEqualTo(token1);
        verify(httpClient, times(1)).execute(any());

        // test invalidation of cache
        cache.invalidateCache();

        // user code
        final AccessToken token3 = service.retrieveAccessTokenViaClientCredentialsGrant(settings, credentials, false);

        // assertion: new token was generated, no cache hit
        assertThat(token3).isNotEqualTo(token2);
        verify(httpClient, times(2)).execute(any());

        // clean up
        com.sap.cloud.security.client.HttpClientFactory.services.remove(0);
    }

    @SneakyThrows
    @Test
    public void clientCredentialsAreUrlEncoded()
    {
        final String specialSecret = "$ecret%";
        final String clientId = "(id<>)";
        final ClientCredentials specialCredentials = new ClientCredentials(clientId, specialSecret);

        final CloseableHttpClient mockedClient = mock(CloseableHttpClient.class);
        when(mockedClient.execute(any(HttpPost.class))).thenAnswer(invocation -> {
            final HttpPost argument = invocation.getArgument(0, HttpPost.class);
            final String actualBody = IOUtils.toString(argument.getEntity().getContent(), StandardCharsets.UTF_8);
            assertThat(actualBody)
                .isEqualTo("grant_type=client_credentials&client_secret=%24ecret%25&client_id=%28id%3C%3E%29");

            return new MockHttpResponse(
                200,
                "{\"access_token\":\"a_token\",\"refresh_token\":\"refresh_token\",\"expires_in\":100}");
        });

        // setup test, enforce our mocked HttpClient
        com.sap.cloud.security.client.HttpClientFactory.services.clear();
        com.sap.cloud.security.client.HttpClientFactory.services.add(0, identity -> mockedClient);

        // prepare test
        final OAuth2TokenServiceCache cache = OAuth2TokenServiceCache.create();
        final XsuaaService service = new XsuaaService(cache);
        final OAuth2ServiceSettings settings = OAuth2ServiceSettings.ofBaseUri(xsuaaUri).build();

        // user code
        final AccessToken token =
            service.retrieveAccessTokenViaClientCredentialsGrant(settings, specialCredentials, false);

        verify(mockedClient, times(1)).execute(any(HttpPost.class));
    }

    @SneakyThrows
    @Test
    public void getTenantSpecificClientCredentialsToken()
    {
        // prepare test
        final OAuth2TokenServiceCache cache = OAuth2TokenServiceCache.single(new DefaultOAuth2TokenService(httpClient));
        final XsuaaService service = new XsuaaService(cache);
        final OAuth2ServiceSettings settings = OAuth2ServiceSettings.ofBaseUri(xsuaaUri).build();

        // user code
        final AccessToken token0 = service.retrieveAccessTokenViaClientCredentialsGrant(settings, credentials, true);
        final AccessToken token1 =
            TenantAccessor
                .executeWithTenant(
                    new ScpCfTenant("TENANT_1", "sub1"),
                    () -> service.retrieveAccessTokenViaClientCredentialsGrant(settings, credentials, false));
        final AccessToken token2 =
            TenantAccessor
                .executeWithTenant(
                    new ScpCfTenant("TENANT_2", "sub2"),
                    () -> service.retrieveAccessTokenViaClientCredentialsGrant(settings, credentials, false));
        final AccessToken token3 =
            TenantAccessor
                .executeWithTenant(
                    new ScpCfTenant("TENANT_3", "sub3"),
                    () -> service.retrieveAccessTokenViaClientCredentialsGrant(settings, credentials, false));

        // assertion
        verify(httpClient, times(4)).execute(any());
        assertThat(asList(token0, token1, token2, token3)).doesNotHaveDuplicates();
    }

    @SneakyThrows
    @Test
    public void getUserSpecificToken()
    {
        // prepare test
        final OAuth2TokenServiceCache cache = OAuth2TokenServiceCache.single(new DefaultOAuth2TokenService(httpClient));
        final XsuaaService service = new XsuaaService(cache);
        final OAuth2ServiceSettings settings = OAuth2ServiceSettings.ofBaseUri(xsuaaUri).build();

        // user code
        final AccessToken token1 =
            AuthTokenAccessor
                .executeWithAuthToken(
                    mockAuthToken("A1", "A2"),
                    () -> service.retrieveAccessTokenViaJwtBearerGrant(settings, credentials));
        final AccessToken token2 =
            AuthTokenAccessor
                .executeWithAuthToken(
                    mockAuthToken("A2", "A3"),
                    () -> service.retrieveAccessTokenViaJwtBearerGrant(settings, credentials));
        final AccessToken token3 =
            AuthTokenAccessor
                .executeWithAuthToken(
                    mockAuthToken("A3", "A4"),
                    () -> service.retrieveAccessTokenViaJwtBearerGrant(settings, credentials));

        // assertion
        verify(httpClient, times(3)).execute(any());
        assertThat(asList(token1, token2, token3)).doesNotHaveDuplicates();
    }

    private static class MockHttpResponse implements CloseableHttpResponse
    {
        @Delegate
        final HttpResponse delegate;

        @SneakyThrows
        MockHttpResponse( final int code, @Nonnull final String body )
        {
            delegate = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, code, ""));
            delegate.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
            delegate.setEntity(new StringEntity(body));
        }

        @Override
        public void close()
        {
            // do nothing
        }
    }

    private AuthToken mockAuthToken( String... audiences )
    {
        final String token =
            JwtGenerator
                .getInstance(Service.XSUAA, "client-id")
                .withClaimValues("aud", audiences)
                .createToken()
                .getTokenValue();

        final DecodedJWT decodedJwt = mock(DecodedJWT.class);
        doReturn(token).when(decodedJwt).getToken();

        return new AuthToken(decodedJwt);
    }
}
