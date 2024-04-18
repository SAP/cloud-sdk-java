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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.parallel.Isolated;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.sap.cloud.sdk.testutil.TestContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
        sut = new DestinationService(mockAdapter);
    }
    @BeforeEach
    void mockUser()
    {
        context.setPrincipal();
        context.setTenant();
        context.setAuthToken(mockXsuaaToken());
    }

    @Test
    void testBasicAuth()
    {
        // basic authentication should also work without a user
        context.clearPrincipal();
        context.clearTenant();
        context.clearAuthToken();

        final Map<String, String> properties = Map.of("Authentication", "BasicAuthentication", "User", "foo", "Password", "bar");
        mockAdapterResponse(properties, null);

        final Destination dest = sut.tryGetDestination(DESTINATION_NAME).get();

        assertThat(dest.asHttp()).isInstanceOf(DefaultHttpDestination.class);
        assertThat(dest.asHttp().getAuthenticationType()).isEqualTo(AuthenticationType.BASIC_AUTHENTICATION);
        assertThat(dest.asHttp().getHeaders())
            .containsExactlyInAnyOrder(new Header("Authorization", "Basic " + BASIC_AUTH));

        final DestinationRetrievalStrategy expectedStrategy = DestinationRetrievalStrategy.withoutToken(TECHNICAL_USER_CURRENT_TENANT);
        verify(mockAdapter, times(1)).getConfigurationAsJson(eq(SERVICE_PATH_DESTINATION), eq(expectedStrategy));
        verifyNoMoreInteractions(mockAdapter);
    }

    @Test
    void testOAuthWithUserTokenExchange()
    {
        final Object payloadBroken =
            ImmutableMap
                .<String, Object> builder()
                .put("owner", ImmutableMap.of("SubaccountId", "a89ea924-d9c2-4eab", "InstanceId", "foobar"))
                .put(
                    "destinationConfiguration",
                    ImmutableMap
                        .<String, String> builder()
                        .put("Name", DESTINATION_NAME)
                        .put("Type", "HTTP")
                        .put("URL", "https://a.s4hana.ondemand.com/some/path/SOME_API")
                        .put("Authentication", "OAuth2SAMLBearerAssertion")
                        .put("ProxyType", "Internet")
                        .put("audience", "https://a.s4hana.ondemand.com")
                        .put("authnContextClassRef", "urn:oasis:names:tc:SAML:2.0:ac:classes:X509")
                        .put("clientKey", "S4SDK-TEST-ABC-USER-IB")
                        .put("nameIdFormat", "urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress")
                        .put("scope", "API_BUSINESS_PARTNER_0001")
                        .put("tokenServiceUser", "S4SDK-TEST-ABC_USER")
                        .put("tokenServiceURL", "https://a.s4hana.ondemand.com/sap/bc/sec/oauth2/token?sap-client=100")
                        .put("userIdSource", "email")
                        .put("tokenServicePassword", "gVS7rPdqDSpRyTErxKGs$NhPebtntzbMCVyAAcij")
                        .build())
                .put(
                    "authTokens",
                    Collections
                        .singletonList(
                            ImmutableMap
                                .<String, String> builder()
                                .put("type", "")
                                .put("value", "")
                                .put(
                                    "error",
                                    "org.apache.http.HttpException: Request to the /userinfo endpoint ended with status code 403")
                                .put("expires_in", "0")
                                .build()))
                .build();

        final Object payloadSuccess =
            ImmutableMap
                .<String, Object> builder()
                .put("owner", ImmutableMap.of("SubaccountId", "a89ea924-d9c2-4eab", "InstanceId", "foobar"))
                .put(
                    "destinationConfiguration",
                    ImmutableMap
                        .<String, String> builder()
                        .put("Name", DESTINATION_NAME)
                        .put("Type", "HTTP")
                        .put("URL", "https://a.s4hana.ondemand.com/some/path/SOME_API")
                        .put("Authentication", "OAuth2SAMLBearerAssertion")
                        .put("ProxyType", "Internet")
                        .put("audience", "https://a.s4hana.ondemand.com")
                        .put("authnContextClassRef", "urn:oasis:names:tc:SAML:2.0:ac:classes:X509")
                        .put("clientKey", "S4SDK-TEST-ABC-USER-IB")
                        .put("nameIdFormat", "urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress")
                        .put("scope", "API_BUSINESS_PARTNER_0001")
                        .put("tokenServiceUser", "S4SDK-TEST-ABC_USER")
                        .put("tokenServiceURL", "https://a.s4hana.ondemand.com/sap/bc/sec/oauth2/token?sap-client=100")
                        .put("userIdSource", "email")
                        .put("tokenServicePassword", "gVS7rPdqDSpRyTErxKGs$NhPebtntzbMCVyAAcij")
                        .build())
                .put(
                    "authTokens",
                    Collections
                        .singletonList(
                            Collections
                                .singletonMap(
                                    "http_header",
                                    ImmutableMap
                                        .<String, String> builder()
                                        .put("key", HttpHeaders.AUTHORIZATION)
                                        .put("value", "Bearer " + OAUTH_TOKEN)
                                        .build())))
                .build();
        final DestinationRetrievalStrategy expectedFirstStrategy = DestinationRetrievalStrategy.withoutToken(TECHNICAL_USER_CURRENT_TENANT);
        final DestinationRetrievalStrategy expectedSecondStrategy = DestinationRetrievalStrategy.withoutToken(NAMED_USER_CURRENT_TENANT);
        // first request without user propagation results in broken authTokens
        doReturn(new Gson().toJson(payloadBroken))
            .when(mockAdapter)
            .getConfigurationAsJson(any(), eq(expectedFirstStrategy));

        // second request with user propagation gives authTokens
        doReturn(new Gson().toJson(payloadSuccess))
            .when(mockAdapter)
            .getConfigurationAsJson(any(), eq(expectedSecondStrategy));

        final Destination dest = sut.tryGetDestination(DESTINATION_NAME, DESTINATION_RETRIEVAL_LOOKUP_EXCHANGE)
                .get();

        assertThat(dest.asHttp()).isInstanceOf(DefaultHttpDestination.class);
        assertThat(dest.asHttp().getAuthenticationType()).isEqualTo(AuthenticationType.OAUTH2_SAML_BEARER_ASSERTION);
        assertThat(dest.asHttp().getHeaders())
            .containsExactlyInAnyOrder(new Header("Authorization", "Bearer " + OAUTH_TOKEN));

        final DestinationServiceV1Response.DestinationAuthToken token =
            (DestinationServiceV1Response.DestinationAuthToken) dest.get(DestinationProperty.AUTH_TOKENS).get().get(0);
        assertThat(token.getExpiryTimestamp()).isNotNull();

        verify(mockAdapter, times(1))
            .getConfigurationAsJson(eq(SERVICE_PATH_DESTINATION), eq(expectedFirstStrategy));
        verify(mockAdapter, times(1))
            .getConfigurationAsJson(eq(SERVICE_PATH_DESTINATION), eq(expectedSecondStrategy));
        verifyNoMoreInteractions(mockAdapter);
    }

    @Test
    void testOAuthWithProvidedSystemUser()
    {
        // OAuth2 SAML Bearer Assertion should work without a user when a system user is provided
        context.clearAuthToken();
        context.clearPrincipal();

        final ImmutableMap<String, String> properties = ImmutableMap
                .<String, String>builder()
                .put("Authentication", "OAuth2SAMLBearerAssertion")
                .put("audience", "https://a.s4hana.ondemand.com")
                .put("authnContextClassRef", "urn:oasis:names:tc:SAML:2.0:ac:classes:X509")
                .put("clientKey", "S4SDK-TEST-ABC-USER-IB")
                .put("nameIdFormat", "urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress")
                .put("scope", "API_BUSINESS_PARTNER_0001")
                .put("tokenServiceUser", "S4SDK-TEST-ABC_USER")
                .put("tokenServiceURL", "https://a.s4hana.ondemand.com/sap/bc/sec/oauth2/token?sap-client=100")
                .put("userIdSource", "email")
                .put("tokenServicePassword", "gVS7rPdqDSpRyTErxKGs$NhPebtntzbMCVyAAcij")
                .put("SystemUser", "admin")
                .build();
        final Map<String, Map<String, String>> token = Collections
                .singletonMap(
                        "http_header",
                        ImmutableMap
                                .<String, String>builder()
                                .put("key", HttpHeaders.AUTHORIZATION)
                                .put("value", "Bearer " + OAUTH_TOKEN)
                                .build());
        mockAdapterResponse(properties, token);

        final Destination dest = sut.tryGetDestination(DESTINATION_NAME).get();

        assertThat(dest.asHttp()).isInstanceOf(DefaultHttpDestination.class);
        assertThat(dest.asHttp().getAuthenticationType()).isEqualTo(AuthenticationType.OAUTH2_SAML_BEARER_ASSERTION);
        assertThat(dest.asHttp().getHeaders())
            .containsExactlyInAnyOrder(new Header("Authorization", "Bearer " + OAUTH_TOKEN));

        final DestinationRetrievalStrategy expectedStrategy = DestinationRetrievalStrategy.withoutToken(TECHNICAL_USER_CURRENT_TENANT);
        verify(mockAdapter, times(1)).getConfigurationAsJson(eq(SERVICE_PATH_DESTINATION), eq(expectedStrategy));
        verifyNoMoreInteractions(mockAdapter);
    }

    @Test
    void testOAuth2JwtBearer()
    {
        final DestinationServiceAdapter destinationService = mock(DestinationServiceAdapter.class);

        final String oAuthToken = "testToken";

        final Object payloadBroken =
            ImmutableMap
                .<String, Object> builder()
                .put("owner", ImmutableMap.of("SubaccountId", "a89ea924-d9c2-4eab", "InstanceId", "foobar"))
                .put(
                    "destinationConfiguration",
                    ImmutableMap
                        .<String, String> builder()
                        .put("Name", DESTINATION_NAME)
                        .put("Type", "HTTP")
                        .put("URL", "https://a.s4hana.ondemand.com/some/path/SOME_API")
                        .put("Authentication", "OAuth2JWTBearer")
                        .put("ProxyType", "Internet")
                        .put("tokenServiceURLType", "Dedicated")
                        .put("Description", "XSUAA Client Credentials on behalf of spring-oauth")
                        .put("clientId", "clientIdString")
                        .put("tokenServiceURL", "https://s4sdk.authentication.sap.hana.ondemand.com/oauth/token")
                        .put("clientSecret", "clientSecretString=")
                        .build())
                .put(
                    "authTokens",
                    Collections
                        .singletonList(
                            ImmutableMap
                                .<String, String> builder()
                                .put("type", "")
                                .put("value", "")
                                .put(
                                    "error",
                                    "org.apache.http.HttpException: Request to the /userinfo endpoint ended with status code 403")
                                .put("expires_in", "0")
                                .build()))
                .build();

        final Object payloadSuccess =
            ImmutableMap
                .<String, Object> builder()
                .put("owner", ImmutableMap.of("SubaccountId", "a89ea924-d9c2-4eab", "InstanceId", "foobar"))
                .put(
                    "destinationConfiguration",
                    ImmutableMap
                        .<String, String> builder()
                        .put("Name", DESTINATION_NAME)
                        .put("Type", "HTTP")
                        .put("URL", "https://a.s4hana.ondemand.com/some/path/SOME_API")
                        .put("Authentication", "OAuth2JWTBearer")
                        .put("ProxyType", "Internet")
                        .put("tokenServiceURLType", "Dedicated")
                        .put("Description", "XSUAA Client Credentials on behalf of spring-oauth")
                        .put("clientId", "ckientIdString")
                        .put("tokenServiceURL", "https://s4sdk.authentication.sap.hana.ondemand.com/oauth/token")
                        .put("clientSecret", "clientSecretString=")
                        .build())
                .put(
                    "authTokens",
                    Collections
                        .singletonList(
                            ImmutableMap
                                .<String, Object> builder()
                                .put("type", "bearer")
                                .put("value", oAuthToken)
                                .put(
                                    "http_header",
                                    ImmutableMap
                                        .builder()
                                        .put("key", "Authorization")
                                        .put("value", "Bearer " + oAuthToken)
                                        .build())
                                .put("expires_in", "43199")
                                .put("scope", "openid user_attributes uaa.user")
                                .build()))
                .build();

        doReturn(new Gson().toJson(payloadBroken))
            .when(destinationService)
            .getConfigurationAsJson(SERVICE_PATH_DESTINATION, TECHNICAL_USER_CURRENT_TENANT);

        doReturn(new Gson().toJson(payloadSuccess))
            .when(destinationService)
            .getConfigurationAsJson(SERVICE_PATH_DESTINATION, OnBehalfOf.NAMED_USER_CURRENT_TENANT);

        final Destination dest =
            new DestinationService(destinationService)
                .tryGetDestination(DESTINATION_NAME, DESTINATION_RETRIEVAL_LOOKUP_EXCHANGE)
                .get();

        assertThat(dest.asHttp()).isInstanceOf(DefaultHttpDestination.class);
        assertThat(dest.asHttp().getAuthenticationType()).isEqualTo(AuthenticationType.OAUTH2_JWT_BEARER);
        assertThat(dest.asHttp().getHeaders())
            .containsExactlyInAnyOrder(new Header("Authorization", "Bearer " + oAuthToken));

        verify(destinationService, times(1))
            .getConfigurationAsJson(anyString(), eq(TECHNICAL_USER_CURRENT_TENANT));
        verify(destinationService, times(1))
            .getConfigurationAsJson(anyString(), eq(OnBehalfOf.NAMED_USER_CURRENT_TENANT));
        verify(destinationService, never()).getConfigurationAsJsonWithUserToken(anyString(), any(OnBehalfOf.class));
    }

    @Test
    void testOAuth2Password()
    {
        // OAuth2 Password should work without a user
        context.clearTenant();
        context.clearPrincipal();

        final DestinationServiceAdapter destinationService = mock(DestinationServiceAdapter.class);
        final String oAuthToken = "testToken";

        final Object payloadSuccess =
            ImmutableMap
                .<String, Object> builder()
                .put("owner", ImmutableMap.of("SubaccountId", "a89ea924-d9c2-4eab", "InstanceId", "foobar"))
                .put(
                    "destinationConfiguration",
                    ImmutableMap
                        .<String, String> builder()
                        .put("Name", DESTINATION_NAME)
                        .put("Type", "HTTP")
                        .put("URL", "https://a.s4hana.ondemand.com/some/path/SOME_API")
                        .put("Authentication", "OAuth2Password")
                        .put("ProxyType", "Internet")
                        .put("clientId", "clientIdString")
                        .put("User", "user@sap.com")
                        .put("tokenServiceURL", "https://s4sdk.authentication.sap.hana.ondemand.com/oauth/token")
                        .put("clientSecret", "clientSecretString=")
                        .put("Password", "password")
                        .build())
                .put(
                    "authTokens",
                    Collections
                        .singletonList(
                            ImmutableMap
                                .<String, Object> builder()
                                .put("type", "Bearer")
                                .put("value", oAuthToken)
                                .put(
                                    "http_header",
                                    ImmutableMap
                                        .builder()
                                        .put("key", "Authorization")
                                        .put("value", "Bearer " + oAuthToken)
                                        .build())
                                .build()))
                .build();

        doReturn(new Gson().toJson(payloadSuccess))
            .when(destinationService)
            .getConfigurationAsJsonWithUserToken(SERVICE_PATH_DESTINATION, TECHNICAL_USER_CURRENT_TENANT);

        final Destination dest = new DestinationService(destinationService).tryGetDestination(DESTINATION_NAME).get();

        assertThat(dest.asHttp()).isInstanceOf(DefaultHttpDestination.class);
        assertThat(dest.asHttp().getAuthenticationType()).isEqualTo(AuthenticationType.OAUTH2_PASSWORD);
        assertThat(dest.asHttp().getHeaders())
            .containsExactlyInAnyOrder(new Header("Authorization", "Bearer " + oAuthToken));

        verify(destinationService, never()).getConfigurationAsJson(anyString(), any(OnBehalfOf.class));
        verify(destinationService, times(1))
            .getConfigurationAsJsonWithUserToken(anyString(), eq(TECHNICAL_USER_CURRENT_TENANT));
    }

    @Test
    void testSAMLAssertion()
    {
        final DestinationServiceAdapter destinationService = mock(DestinationServiceAdapter.class);
        final String samlToken = "testToken";

        final Object payloadFailure =
            ImmutableMap
                .<String, Object> builder()
                .put("owner", ImmutableMap.of("SubaccountId", "a89ea924-d9c2-4eab", "InstanceId", "foobar"))
                .put(
                    "destinationConfiguration",
                    ImmutableMap
                        .<String, String> builder()
                        .put("Name", DESTINATION_NAME)
                        .put("Type", "HTTP")
                        .put("URL", "https://a.s4hana.ondemand.com/some/path/SOME_API")
                        .put("Authentication", "SAMLAssertion")
                        .put("ProxyType", "Internet")
                        .put("audience", "https://a.s4hana.ondemand.com")
                        .put("authnContextClassRef", "urn:oasis:names:tc:SAML:2.0:ac:classes:X509")
                        .build())
                .put(
                    "authTokens",
                    Collections
                        .singletonList(
                            ImmutableMap
                                .<String, Object> builder()
                                .put("type", "")
                                .put("value", "")
                                .put(
                                    "error",
                                    "org.apache.http.HttpException: Request to the /userinfo endpoint ended with status code 403")
                                .put("expires_in", "0")
                                .build()))
                .build();

        final Object payloadSuccess =
            ImmutableMap
                .<String, Object> builder()
                .put("owner", ImmutableMap.of("SubaccountId", "a89ea924-d9c2-4eab", "InstanceId", "foobar"))
                .put(
                    "destinationConfiguration",
                    ImmutableMap
                        .<String, String> builder()
                        .put("Name", DESTINATION_NAME)
                        .put("Type", "HTTP")
                        .put("URL", "https://a.s4hana.ondemand.com/some/path/SOME_API")
                        .put("Authentication", "SAMLAssertion")
                        .put("ProxyType", "Internet")
                        .put("audience", "https://a.s4hana.ondemand.com")
                        .put("authnContextClassRef", "urn:oasis:names:tc:SAML:2.0:ac:classes:X509")
                        .build())
                .put(
                    "authTokens",
                    Collections
                        .singletonList(
                            ImmutableMap
                                .<String, Object> builder()
                                .put("type", "SAML2.0")
                                .put("value", samlToken)
                                .put(
                                    "http_header",
                                    ImmutableMap
                                        .builder()
                                        .put("key", "Authorization")
                                        .put("value", "SAML2.0 " + samlToken)
                                        .build())
                                .build()))
                .build();

        doReturn(new Gson().toJson(payloadFailure))
            .when(destinationService)
            .getConfigurationAsJson(SERVICE_PATH_DESTINATION, TECHNICAL_USER_CURRENT_TENANT);

        doReturn(new Gson().toJson(payloadSuccess))
            .when(destinationService)
            .getConfigurationAsJson(SERVICE_PATH_DESTINATION, OnBehalfOf.NAMED_USER_CURRENT_TENANT);

        final Destination dest =
            new DestinationService(destinationService)
                .tryGetDestination(DESTINATION_NAME, DESTINATION_RETRIEVAL_LOOKUP_EXCHANGE)
                .get();

        assertThat(dest.asHttp()).isInstanceOf(DefaultHttpDestination.class);
        assertThat(dest.asHttp().getAuthenticationType()).isEqualTo(AuthenticationType.SAML_ASSERTION);

        //Security session header expected for SAMLAssertion requests
        assertThat(dest.asHttp().getHeaders())
            .containsExactlyInAnyOrder(
                new Header("Authorization", "SAML2.0 " + samlToken),
                new Header("x-sap-security-session", "create"));

        verify(destinationService, times(1))
            .getConfigurationAsJson(anyString(), eq(TECHNICAL_USER_CURRENT_TENANT));
        verify(destinationService, times(1))
            .getConfigurationAsJson(anyString(), eq(OnBehalfOf.NAMED_USER_CURRENT_TENANT));
        verify(destinationService, never()).getConfigurationAsJsonWithUserToken(anyString(), any(OnBehalfOf.class));
    }

    @Test
    void testSAPAssertionSSOSuccess()
    {
        final String assertionCookie = "MYSAPSSO2=SomeInterestingStringHere1234";

        final DestinationServiceAdapter destinationService = mock(DestinationServiceAdapter.class);

        final Object payloadSuccess =
            ImmutableMap
                .<String, Object> builder()
                .put("owner", ImmutableMap.of("SubaccountId", "a89ea924-d9c2-4eab", "InstanceId", "foobar"))
                .put(
                    "destinationConfiguration",
                    ImmutableMap
                        .<String, String> builder()
                        .put("Name", DESTINATION_NAME)
                        .put("Type", "HTTP")
                        .put("URL", "https://a.s4hana.ondemand.com/some/path/SOME_API")
                        .put("Authentication", "SAPAssertionSSO")
                        .put("ProxyType", "Internet")
                        .put("IssuerSID", "iss")
                        .put("SigningKey", "ABCDEFG12345678")
                        .put("IssuerClient", "420")
                        .put("RecipientSID", "rec")
                        .put("RecipientClient", "007")
                        .put("Certificate", "SomeCertificateKeyHere1234")
                        .put("SystemUser", "SomeUser")
                        .build())
                .put(
                    "authTokens",
                    Collections
                        .singletonList(
                            Collections
                                .singletonMap(
                                    "http_header",
                                    ImmutableMap
                                        .<String, Object> builder()
                                        .put("key", "Cookie")
                                        .put("value", "MYSAPSSO2=SomeInterestingStringHere1234")
                                        .build())))
                .build();

        doReturn(new Gson().toJson(payloadSuccess))
            .when(destinationService)
            .getConfigurationAsJsonWithUserToken(SERVICE_PATH_DESTINATION, TECHNICAL_USER_CURRENT_TENANT);

        final Destination dest = new DestinationService(destinationService).tryGetDestination(DESTINATION_NAME).get();

        assertThat(dest.asHttp()).isInstanceOf(DefaultHttpDestination.class);

        assertThat(dest.asHttp().getAuthenticationType()).isEqualTo(AuthenticationType.SAP_ASSERTION_SSO);

        assertThat(dest.asHttp().getHeaders()).containsExactlyInAnyOrder(new Header("Cookie", assertionCookie));
    }

    @Test
    void testSAPAssertionSSOWithUserTokenExchange()
    {
        final String assertionCookie = "MYSAPSSO2=SomeInterestingStringHere1234";

        final DestinationServiceAdapter destinationService = mock(DestinationServiceAdapter.class);

        final Object payloadFailure =
            ImmutableMap
                .<String, Object> builder()
                .put("owner", ImmutableMap.of("SubaccountId", "a89ea924-d9c2-4eab", "InstanceId", "foobar"))
                .put(
                    "destinationConfiguration",
                    ImmutableMap
                        .<String, String> builder()
                        .put("Name", DESTINATION_NAME)
                        .put("Type", "HTTP")
                        .put("URL", "https://a.s4hana.ondemand.com/some/path/SOME_API")
                        .put("Authentication", "SAPAssertionSSO")
                        .put("ProxyType", "Internet")
                        .put("IssuerSID", "iss")
                        .put("SigningKey", "ABCDEFG12345678")
                        .put("IssuerClient", "420")
                        .put("RecipientSID", "rec")
                        .put("RecipientClient", "007")
                        .put("Certificate", "SomeCertificateKeyHere1234")
                        .build())
                .put(
                    "authTokens",
                    Collections
                        .singletonList(
                            ImmutableMap
                                .<String, Object> builder()
                                .put("type", "")
                                .put("value", "")
                                .put("error", "No user specified")
                                .build()))
                .build();

        final Object payloadSuccess =
            ImmutableMap
                .<String, Object> builder()
                .put("owner", ImmutableMap.of("SubaccountId", "a89ea924-d9c2-4eab", "InstanceId", "foobar"))
                .put(
                    "destinationConfiguration",
                    ImmutableMap
                        .<String, String> builder()
                        .put("Name", DESTINATION_NAME)
                        .put("Type", "HTTP")
                        .put("URL", "https://a.s4hana.ondemand.com/some/path/SOME_API")
                        .put("Authentication", "SAPAssertionSSO")
                        .put("ProxyType", "Internet")
                        .put("IssuerSID", "iss")
                        .put("SigningKey", "ABCDEFG12345678")
                        .put("IssuerClient", "420")
                        .put("RecipientSID", "rec")
                        .put("RecipientClient", "007")
                        .put("Certificate", "SomeCertificateKeyHere1234")
                        .put("SystemUser", "SomeUser")
                        .build())
                .put(
                    "authTokens",
                    Collections
                        .singletonList(
                            Collections
                                .singletonMap(
                                    "http_header",
                                    ImmutableMap
                                        .<String, Object> builder()
                                        .put("key", "Cookie")
                                        .put("value", "MYSAPSSO2=SomeInterestingStringHere1234")
                                        .build())))
                .build();

        doReturn(new Gson().toJson(payloadFailure))
            .when(destinationService)
            .getConfigurationAsJson(SERVICE_PATH_DESTINATION, TECHNICAL_USER_CURRENT_TENANT);

        doReturn(new Gson().toJson(payloadSuccess))
            .when(destinationService)
            .getConfigurationAsJson(SERVICE_PATH_DESTINATION, OnBehalfOf.NAMED_USER_CURRENT_TENANT);

        final Destination dest =
            new DestinationService(destinationService)
                .tryGetDestination(DESTINATION_NAME, DESTINATION_RETRIEVAL_LOOKUP_EXCHANGE)
                .get();

        assertThat(dest.asHttp()).isInstanceOf(DefaultHttpDestination.class);

        assertThat(dest.asHttp().getAuthenticationType()).isEqualTo(AuthenticationType.SAP_ASSERTION_SSO);

        assertThat(dest.asHttp().getHeaders()).containsExactlyInAnyOrder(new Header("Cookie", assertionCookie));

        verify(destinationService, times(1))
            .getConfigurationAsJson(anyString(), eq(TECHNICAL_USER_CURRENT_TENANT));
        verify(destinationService, times(1))
            .getConfigurationAsJson(anyString(), eq(OnBehalfOf.NAMED_USER_CURRENT_TENANT));
        verify(destinationService, never()).getConfigurationAsJsonWithUserToken(anyString(), any(OnBehalfOf.class));
    }

    private void mockAdapterResponse(@Nonnull Map<String, String> properties, @Nullable Map<String, Map<String, String>> tokens) {
        final ImmutableMap.Builder<String, Object> builder =
                ImmutableMap
                        .<String, Object> builder()
                        .put("owner", ImmutableMap.of("SubaccountId", "a89ea924-d9c2-4eab", "InstanceId", "foobar"))
                        .put(
                                "destinationConfiguration",
                                ImmutableMap
                                        .<String, String> builder()
                                        .put("Name", DESTINATION_NAME)
                                        .put("Type", "HTTP")
                                        .put("URL", "https://a.s4hana.ondemand.com/some/path/SOME_API")
                                        .put("ProxyType", "Internet")
                                        .putAll(properties)
                                        .build());

        builder.put("authTokens", Objects.requireNonNullElse(tokens, Collections.emptyList()));
        final Object payload = builder.build();

        doReturn(new Gson().toJson(payload))
                .when(mockAdapter)
                .getConfigurationAsJson(any(), any());
    }

}
