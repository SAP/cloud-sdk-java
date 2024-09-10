/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.OnBehalfOf.NAMED_USER_CURRENT_TENANT;
import static com.sap.cloud.sdk.cloudplatform.connectivity.OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT;
import static com.sap.cloud.sdk.cloudplatform.connectivity.XsuaaTokenMocker.mockXsuaaToken;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.parallel.Isolated;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.sap.cloud.sdk.testutil.TestContext;

@Isolated( "Test interacts with global destination cache" )
class DestinationServiceAuthenticationTest
{
    private static final String OAUTH_TOKEN =
        "eyJhbGciOiJSUzI1NiIsImprdSI6Imh0dHBzOi8vczRzZGsuYXV0aGVudGljYXRpb24uc2FwLmhhbmEub25kZW1hbmQuY29tL3Rva2VuX2tleXMiLCJraWQiOiJrZXktaWQtMSIsInR5cCI6IkpXVCJ9.eyJqdGkiOiIwNWJmMWFlOTBkMTI0ZTIyODcwM2FiMzBjM2E0NWJlZCIsImV4dF9hdHRyIjp7ImVuaGFuY2VyIjoiWFNVQUEiLCJ6ZG4iOiJzNHNkayIsInNlcnZpY2VpbnN0YW5jZWlkIjoiN2Q3NTMyNzQtM2ZkMC00ZTI4LThhNjUtYTgxYmZiMzcyZDI5In0sInhzLnVzZXIuYXR0cmlidXRlcyI6e30sImdyYW50ZWRfc2NvcGVzIjpbIm9wZW5pZCIsInVhYS51c2VyIl0sInhzLnN5c3RlbS5hdHRyaWJ1dGVzIjp7InhzLnJvbGVjb2xsZWN0aW9ucyI6WyJFbmQtdG8tRW5kIFRlc3RlciJdfSwiZ2l2ZW5fbmFtZSI6IkpvaG4iLCJmYW1pbHlfbmFtZSI6IkJ1cGFfTWFzdGVyX1NwZWNpYWxpIiwic3ViIjoiMjY4NGVhZWUtMzc0My00ODI0LWJjYmQtZjAxOTNmM2E5ODllIiwic2NvcGUiOlsib3BlbmlkIiwidWFhLnVzZXIiXSwiY2xpZW50X2lkIjoic2ItY2xvbmU3ZDc1MzI3NDNmZDA0ZTI4OGE2NWE4MWJmYjM3MmQyOSFiMjQ5OXxkZXN0aW5hdGlvbi14c2FwcG5hbWUhYjQzMyIsImNpZCI6InNiLWNsb25lN2Q3NTMyNzQzZmQwNGUyODhhNjVhODFiZmIzNzJkMjkhYjI0OTl8ZGVzdGluYXRpb24teHNhcHBuYW1lIWI0MzMiLCJhenAiOiJzYi1jbG9uZTdkNzUzMjc0M2ZkMDRlMjg4YTY1YTgxYmZiMzcyZDI5IWIyNDk5fGRlc3RpbmF0aW9uLXhzYXBwbmFtZSFiNDMzIiwicmV2b2NhYmxlIjp0cnVlLCJncmFudF90eXBlIjoidXNlcl90b2tlbiIsInVzZXJfaWQiOiIyNjg0ZWFlZS0zNzQzLTQ4MjQtYmNiZC1mMDE5M2YzYTk4OWUiLCJvcmlnaW4iOiJtYS5hY2NvdW50czQwMC5vbmRlbWFuZC5jb20iLCJ1c2VyX25hbWUiOiJwMDAwMjI3IiwiZW1haWwiOiJidXBhX21hc3Rlcl9zcGVjaWFsaUBleGFtcGxlLmNvbSIsImF1dGhfdGltZSI6MTU2MjA3MzkyMCwicmV2X3NpZyI6IjVjMGRiYjdmIiwiaWF0IjoxNTYyMDczOTIwLCJleHAiOjE1NjIxMTcxMjAsImlzcyI6Imh0dHA6Ly9zNHNkay5sb2NhbGhvc3Q6ODA4MC91YWEvb2F1dGgvdG9rZW4iLCJ6aWQiOiJhODllYTkyNC1kOWMyLTRlYWItODRmYi0zZmZjYWFkZjVkMjQiLCJhdWQiOltdfQ.hXbWBNRILiXjQUx8QqYyj7jvnoUnhj379HI5ZguVc6Y1J1DXX0v-tL1OqEOpmaxvUP7F0SbHYN6Lgk6dL-9qgY-O_QpeHX7TEG8d3X6ajQGtUrsxk-ISYUNjQppcgaS0JccZd5vHQKWylm3zfTVAuiop6XopYR4JIpxFuXuR1SBgrLxUKB40eyEmhAr2D5CSgIwAlfZMmaEoc4eRtM7IPOyEEb0IjlmQSgpiYevwSfzcDE2uaxRV-BH7oM-VFlqecbil_I04zBqCNh3IN6gekUPL8Owt7IafW0s27fT1jtz9Njy6ixzOOqTaqoDe3vljN6v4jRCeTd3w32jgSpqt6A";
    private static final String BASIC_AUTH = "Zm9vOmJhcg==";
    private static final String DESTINATION_NAME = "CXT-HTTP-OAUTH";

    private static final String SERVICE_PATH_DESTINATION = "/destinations/" + DESTINATION_NAME;

    @SuppressWarnings( "deprecation" )
    private static final DestinationOptions DESTINATION_RETRIEVAL_LOOKUP_EXCHANGE =
        DestinationOptions
            .builder()
            .augmentBuilder(
                DestinationServiceOptionsAugmenter
                    .augmenter()
                    .tokenExchangeStrategy(DestinationServiceTokenExchangeStrategy.LOOKUP_THEN_EXCHANGE))
            .build();

    @RegisterExtension
    static final TestContext context = TestContext.withThreadContext().resetCaches();

    private DestinationServiceAdapter mockAdapter;
    private DestinationService sut;

    @BeforeEach
    void setMockAdapter()
    {
        mockAdapter = mock(DestinationServiceAdapter.class);
        doThrow(new AssertionError("Unexpected invocation to mocked adapter"))
            .when(mockAdapter)
            .getConfigurationAsJson(anyString(), any());
        sut = new DestinationService(mockAdapter);
    }

    @Test
    void testBasicAuth()
    {
        final DestinationRetrievalStrategy expectedStrategy =
            DestinationRetrievalStrategy.withoutToken(TECHNICAL_USER_CURRENT_TENANT);

        final Map<String, String> properties =
            Map.of("Authentication", "BasicAuthentication", "User", "foo", "Password", "bar");
        mockAdapterResponse(properties, null, expectedStrategy);

        final Destination dest = sut.tryGetDestination(DESTINATION_NAME).get();

        assertThat(dest.asHttp()).isInstanceOf(DefaultHttpDestination.class);
        assertThat(dest.asHttp().getAuthenticationType()).isEqualTo(AuthenticationType.BASIC_AUTHENTICATION);
        assertThat(dest.asHttp().getHeaders())
            .containsExactlyInAnyOrder(new Header("Authorization", "Basic " + BASIC_AUTH));

        verify(mockAdapter, times(1)).getConfigurationAsJson(eq(SERVICE_PATH_DESTINATION), eq(expectedStrategy));
        verifyNoMoreInteractions(mockAdapter);
    }

    @Test
    void testOAuthWithUserTokenExchange()
    {
        context.setPrincipal();
        context.setTenant();
        context.setAuthToken(mockXsuaaToken());

        final Map<String, String> properties =
            Map
                .of(
                    "Authentication",
                    "OAuth2SAMLBearerAssertion",
                    "audience",
                    "https://a.s4hana.ondemand.com",
                    "authnContextClassRef",
                    "urn:oasis:names:tc:SAML:2.0:ac:classes:X509",
                    "clientKey",
                    "S4SDK-TEST-ABC-USER-IB",
                    "nameIdFormat",
                    "urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress",
                    "scope",
                    "API_BUSINESS_PARTNER_0001",
                    "tokenServiceUser",
                    "S4SDK-TEST-ABC_USER",
                    "tokenServiceURL",
                    "https://a.s4hana.ondemand.com/sap/bc/sec/oauth2/token?sap-client=100",
                    "userIdSource",
                    "email",
                    "tokenServicePassword",
                    "gVS7rPdqDSpRyTErxKGs$NhPebtntzbMCVyAAcij");

        // first request without user propagation results in broken authTokens
        final DestinationRetrievalStrategy expectedFirstStrategy =
            DestinationRetrievalStrategy.withoutToken(TECHNICAL_USER_CURRENT_TENANT);
        final Map<String, String> tokenFailure = Map.of("type", "", "value", "", "error", "error", "expires_in", "0");
        mockAdapterResponse(properties, tokenFailure, expectedFirstStrategy);
        // second request with user propagation gives authTokens
        final DestinationRetrievalStrategy expectedSecondStrategy =
            DestinationRetrievalStrategy.withoutToken(NAMED_USER_CURRENT_TENANT);
        final Map<String, Map<String, String>> tokenSuccess =
            Map.of("http_header", Map.of("key", HttpHeaders.AUTHORIZATION, "value", "Bearer " + OAUTH_TOKEN));
        mockAdapterResponse(properties, tokenSuccess, expectedSecondStrategy);

        // actual test
        final Destination dest = sut.tryGetDestination(DESTINATION_NAME, DESTINATION_RETRIEVAL_LOOKUP_EXCHANGE).get();

        assertThat(dest.asHttp()).isInstanceOf(DefaultHttpDestination.class);
        assertThat(dest.asHttp().getAuthenticationType()).isEqualTo(AuthenticationType.OAUTH2_SAML_BEARER_ASSERTION);
        assertThat(dest.asHttp().getHeaders())
            .containsExactlyInAnyOrder(new Header("Authorization", "Bearer " + OAUTH_TOKEN));

        final DestinationServiceV1Response.DestinationAuthToken token =
            (DestinationServiceV1Response.DestinationAuthToken) dest.get(DestinationProperty.AUTH_TOKENS).get().get(0);
        assertThat(token.getExpiryTimestamp()).isNotNull();

        verify(mockAdapter, times(1)).getConfigurationAsJson(eq(SERVICE_PATH_DESTINATION), eq(expectedFirstStrategy));
        verify(mockAdapter, times(1)).getConfigurationAsJson(eq(SERVICE_PATH_DESTINATION), eq(expectedSecondStrategy));
        verifyNoMoreInteractions(mockAdapter);
    }

    @Test
    void testOAuthWithProvidedSystemUser()
    {
        final Map<String, String> properties =
            Map
                .of(
                    "Authentication",
                    "OAuth2SAMLBearerAssertion",
                    "audience",
                    "https://a.s4hana.ondemand.com",
                    "authnContextClassRef",
                    "urn:oasis:names:tc:SAML:2.0:ac:classes:X509",
                    "clientKey",
                    "S4SDK-TEST-ABC-USER-IB",
                    "nameIdFormat",
                    "urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress",
                    "scope",
                    "API_BUSINESS_PARTNER_0001",
                    "tokenServiceUser",
                    "S4SDK-TEST-ABC_USER",
                    "tokenServiceURL",
                    "https://a.s4hana.ondemand.com/sap/bc/sec/oauth2/token?sap-client=100",
                    "userIdSource",
                    "email",
                    "SystemUser",
                    "admin");
        final Map<String, Map<String, String>> token =
            Map.of("http_header", Map.of("key", HttpHeaders.AUTHORIZATION, "value", "Bearer " + OAUTH_TOKEN));

        // OAuth2 SAML Bearer Assertion should work without a user when a system user is provided
        final DestinationRetrievalStrategy expectedStrategy =
            DestinationRetrievalStrategy.withoutToken(TECHNICAL_USER_CURRENT_TENANT);
        mockAdapterResponse(properties, token, expectedStrategy);

        final Destination dest = sut.tryGetDestination(DESTINATION_NAME).get();

        assertThat(dest.asHttp()).isInstanceOf(DefaultHttpDestination.class);
        assertThat(dest.asHttp().getAuthenticationType()).isEqualTo(AuthenticationType.OAUTH2_SAML_BEARER_ASSERTION);
        assertThat(dest.asHttp().getHeaders())
            .containsExactlyInAnyOrder(new Header("Authorization", "Bearer " + OAUTH_TOKEN));

        verify(mockAdapter, times(1)).getConfigurationAsJson(eq(SERVICE_PATH_DESTINATION), eq(expectedStrategy));
        verifyNoMoreInteractions(mockAdapter);
    }

    @Test
    void testOAuth2JwtBearerWithUserTokenForwarding()
    {
        context.setPrincipal();
        context.setTenant();
        final DecodedJWT jwt = mockXsuaaToken();
        context.setAuthToken(jwt);

        final Map<String, String> properties =
            Map
                .of(
                    "Authentication",
                    "OAuth2JWTBearer",
                    "tokenServiceURLType",
                    "Dedicated",
                    "Description",
                    "XSUAA Client Credentials on behalf of spring-oauth",
                    "clientId",
                    "ckientIdString",
                    "tokenServiceURL",
                    "https://s4sdk.authentication.sap.hana.ondemand.com/oauth/token",
                    "clientSecret",
                    "clientSecretString=");

        final String oAuthToken = "testToken";
        final Map<String, Object> token =
            Map
                .of(
                    "type",
                    "bearer",
                    "value",
                    oAuthToken,
                    "expires_in",
                    "43199",
                    "scope",
                    "openid user_attributes uaa.user",
                    "http_header",
                    Map.of("key", "Authorization", "value", "Bearer " + oAuthToken));

        final DestinationRetrievalStrategy expectedStrategy =
            DestinationRetrievalStrategy.withUserToken(TECHNICAL_USER_CURRENT_TENANT, jwt.getToken());
        mockAdapterResponse(properties, token, expectedStrategy);

        final Destination dest = sut.tryGetDestination(DESTINATION_NAME).get();

        assertThat(dest.asHttp()).isInstanceOf(DefaultHttpDestination.class);
        assertThat(dest.asHttp().getAuthenticationType()).isEqualTo(AuthenticationType.OAUTH2_JWT_BEARER);
        assertThat(dest.asHttp().getHeaders())
            .containsExactlyInAnyOrder(new Header("Authorization", "Bearer " + oAuthToken));

        verify(mockAdapter, times(1)).getConfigurationAsJson(anyString(), eq(expectedStrategy));
        verifyNoMoreInteractions(mockAdapter);
    }

    @Test
    void testOAuth2PasswordWithoutUserToken()
    {
        final Map<String, String> properties =
            Map
                .of(
                    "Authentication",
                    "OAuth2Password",
                    "clientId",
                    "clientIdString",
                    "User",
                    "user@sap.com",
                    "tokenServiceURL",
                    "https://s4sdk.authentication.sap.hana.ondemand.com/oauth/token",
                    "clientSecret",
                    "clientSecretString=",
                    "Password",
                    "password");

        final String oAuthToken = "testToken";
        final Map<String, Object> token =
            Map
                .of(
                    "type",
                    "Bearer",
                    "value",
                    oAuthToken,
                    "http_header",
                    Map.of("key", "Authorization", "value", "Bearer " + oAuthToken));

        // OAuth2 Password should work without a user
        final DestinationRetrievalStrategy expectedStrategy =
            DestinationRetrievalStrategy.withoutToken(TECHNICAL_USER_CURRENT_TENANT);
        mockAdapterResponse(properties, token, expectedStrategy);

        final Destination dest = sut.tryGetDestination(DESTINATION_NAME).get();

        assertThat(dest.asHttp()).isInstanceOf(DefaultHttpDestination.class);
        assertThat(dest.asHttp().getAuthenticationType()).isEqualTo(AuthenticationType.OAUTH2_PASSWORD);
        assertThat(dest.asHttp().getHeaders())
            .containsExactlyInAnyOrder(new Header("Authorization", "Bearer " + oAuthToken));

        verify(mockAdapter, times(1)).getConfigurationAsJson(anyString(), eq(expectedStrategy));
        verifyNoMoreInteractions(mockAdapter);
    }

    @Test
    void testSAMLAssertion()
    {
        context.setPrincipal();
        context.setTenant();
        final DecodedJWT jwt = mockXsuaaToken();
        context.setAuthToken(jwt);

        final Map<String, String> properties =
            Map
                .of(
                    "Authentication",
                    "SAMLAssertion",
                    "audience",
                    "https://a.s4hana.ondemand.com",
                    "authnContextClassRef",
                    "urn:oasis:names:tc:SAML:2.0:ac:classes:X509");

        final String samlToken = "testToken";
        final Map<String, Object> token =
            Map
                .of(
                    "type",
                    "SAML2.0",
                    "value",
                    samlToken,
                    "expires_in",
                    "0",
                    "http_header",
                    Map.of("key", "Authorization", "value", "SAML2.0 " + samlToken));

        final DestinationRetrievalStrategy expectedStrategy =
            DestinationRetrievalStrategy.withUserToken(TECHNICAL_USER_CURRENT_TENANT, jwt.getToken());
        mockAdapterResponse(properties, token, expectedStrategy);

        // actual test
        final Destination dest = sut.tryGetDestination(DESTINATION_NAME).get();

        assertThat(dest.asHttp()).isInstanceOf(DefaultHttpDestination.class);
        assertThat(dest.asHttp().getAuthenticationType()).isEqualTo(AuthenticationType.SAML_ASSERTION);

        //Security session header expected for SAMLAssertion requests
        assertThat(dest.asHttp().getHeaders())
            .containsExactlyInAnyOrder(
                new Header("Authorization", "SAML2.0 " + samlToken),
                new Header("x-sap-security-session", "create"));

        verify(mockAdapter, times(1)).getConfigurationAsJson(anyString(), eq(expectedStrategy));
        verifyNoMoreInteractions(mockAdapter);
    }

    @Test
    void testSAPAssertionSSO()
    {
        final Map<String, String> properties =
            Map
                .of(
                    "Authentication",
                    "SAPAssertionSSO",
                    "IssuerSID",
                    "iss",
                    "SigningKey",
                    "ABCDEFG12345678",
                    "IssuerClient",
                    "420",
                    "RecipientSID",
                    "rec",
                    "RecipientClient",
                    "007",
                    "Certificate",
                    "SomeCertificateKeyHere1234",
                    "SystemUser",
                    "SomeUser");

        final String assertionCookie = "MYSAPSSO2=SomeInterestingStringHere1234";
        final Map<String, Object> token = Map.of("http_header", Map.of("key", "Cookie", "value", assertionCookie));

        // system user is given, no token needed
        final DestinationRetrievalStrategy expectedStrategy =
            DestinationRetrievalStrategy.withoutToken(TECHNICAL_USER_CURRENT_TENANT);
        mockAdapterResponse(properties, token, expectedStrategy);

        final Destination dest = sut.tryGetDestination(DESTINATION_NAME).get();

        assertThat(dest.asHttp()).isInstanceOf(DefaultHttpDestination.class);
        assertThat(dest.asHttp().getAuthenticationType()).isEqualTo(AuthenticationType.SAP_ASSERTION_SSO);
        assertThat(dest.asHttp().getHeaders()).containsExactlyInAnyOrder(new Header("Cookie", assertionCookie));

        verify(mockAdapter, times(1)).getConfigurationAsJson(anyString(), eq(expectedStrategy));
        verifyNoMoreInteractions(mockAdapter);
    }

    @Test
    void testOAuth2RefreshToken()
    {
        final Map<String, String> properties = Map.of("Authentication", "OAuth2RefreshToken");

        final Map<String, Object> token =
            Map.of("http_header", Map.of("key", "Authorization", "value", "Bearer ey1234"));

        final String refreshToken = "refreshToken";
        final DestinationRetrievalStrategy expectedStrategy =
            DestinationRetrievalStrategy.withRefreshToken(TECHNICAL_USER_CURRENT_TENANT, refreshToken);
        mockAdapterResponse(properties, token, expectedStrategy);

        final DestinationOptions opts =
            DestinationOptions
                .builder()
                .augmentBuilder(DestinationServiceOptionsAugmenter.augmenter().refreshToken(refreshToken))
                .build();
        final Destination dest = sut.tryGetDestination(DESTINATION_NAME, opts).get();

        assertThat(dest.asHttp()).isInstanceOf(DefaultHttpDestination.class);
        assertThat(dest.asHttp().getAuthenticationType()).isEqualTo(AuthenticationType.OAUTH2_REFRESH_TOKEN);
        assertThat(dest.asHttp().getHeaders()).containsExactlyInAnyOrder(new Header("Authorization", "Bearer ey1234"));

        verify(mockAdapter, times(1)).getConfigurationAsJson(anyString(), eq(expectedStrategy));
        verifyNoMoreInteractions(mockAdapter);
    }

    private void mockAdapterResponse(
        @Nonnull Map<String, String> properties,
        @Nullable Map<String, ?> tokens,
        DestinationRetrievalStrategy expectedStrategy )
    {
        final Map<String, Object> baseProperties =
            Map
                .of(
                    "Name",
                    DESTINATION_NAME,
                    "Type",
                    "HTTP",
                    "URL",
                    "https://a.s4hana.ondemand.com/some/path/SOME_API",
                    "ProxyType",
                    "Internet");
        final Map<String, Object> destinationConfiguration = new TreeMap<>(baseProperties);
        destinationConfiguration.putAll(properties);

        final Map<String, Object> destination =
            Map
                .of(
                    "owner",
                    Map.of("SubaccountId", "a89ea924-d9c2-4eab", "InstanceId", "foobar"),
                    "destinationConfiguration",
                    destinationConfiguration,
                    "authTokens",
                    tokens != null ? List.of(tokens) : Collections.emptyList());

        doReturn(new Gson().toJson(destination)).when(mockAdapter).getConfigurationAsJson(any(), eq(expectedStrategy));
    }
}
