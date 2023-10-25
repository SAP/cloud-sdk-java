/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Collections;

import org.assertj.vavr.api.VavrAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.net.HttpHeaders;
import com.sap.cloud.sdk.cloudplatform.CloudPlatformAccessor;
import com.sap.cloud.sdk.cloudplatform.CloudPlatformFacade;
import com.sap.cloud.sdk.cloudplatform.requestheader.DefaultRequestHeaderContainer;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderAccessor;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderContainer;
import com.sap.cloud.sdk.cloudplatform.security.exception.AuthTokenAccessException;
import com.sap.cloud.security.config.Service;
import com.sap.cloud.security.test.JwtGenerator;
import com.sap.cloud.security.test.RSAKeys;
import com.sap.cloud.security.token.Token;
import com.sap.cloud.security.token.TokenHeader;

import io.vavr.control.Try;

@RunWith( MockitoJUnitRunner.class )
public class AuthTokenDecoderIasTest
{
    @Rule
    public final WireMockRule wireMockServer = new WireMockRule(wireMockConfig().dynamicPort());

    @Mock
    private CloudPlatformFacade cloudPlatformFacade;

    private final RSAKeys RSA_KEYS = RSAKeys.generate();

    private String tokenA;

    private static final String TEMPLATE_OPENID_CONFIGURATION =
        "{"
            + "  \"issuer\" : \"HOST\","
            + "  \"authorization_endpoint\" : \"HOST/oauth2/authorize\","
            + "  \"token_endpoint\" : \"HOST/oauth2/token\","
            + "  \"userinfo_endpoint\" : \"HOST/oauth2/userinfo\","
            + "  \"end_session_endpoint\" : \"HOST/oauth2/logout\","
            + "  \"jwks_uri\" : \"HOST/oauth2/certs\","
            + "  \"response_types_supported\" : [ \"code\", \"id_token\", \"token\" ],"
            + "  \"grant_types_supported\" : [ \"password\", \"authorization_code\", \"refresh_token\", \"client_credentials\" ],"
            + "  \"subject_types_supported\" : [ \"public\" ],"
            + "  \"id_token_signing_alg_values_supported\" : [ \"RS256\" ],"
            + "  \"scopes_supported\" : [ \"openid\", \"email\", \"profile\", \"offline_access\" ],"
            + "  \"token_endpoint_auth_methods_supported\" : [ \"tls_client_auth_subject_dn\", \"client_secret_basic\", \"client_secret_post\" ],"
            + "  \"claims_supported\" : [ \"sub\", \"iss\", \"exp\", \"iat\", \"nonce\", \"email\", \"email_verified\", \"given_name\", \"family_name\", \"zone_uuid\", \"app_tid\", \"user_uuid\", \"preferred_username\", \"name\" ],"
            + "  \"code_challenge_methods_supported\" : [ \"plain\", \"S256\" ],"
            + "  \"tls_client_certificate_bound_access_tokens\" : true"
            + "}";

    private static final String TEMPLATE_VCAP_SERVICES =
        "{"
            + "    \"identity\": ["
            + "      {"
            + "        \"label\": \"identity\","
            + "        \"provider\": null,"
            + "        \"plan\": \"application\","
            + "        \"name\": \"cf-sprias-ias\","
            + "        \"tags\": [],"
            + "        \"instance_guid\": \"9b2e9264-78ef-4316-b776-3a91d4d4b717\","
            + "        \"instance_name\": \"cf-sprias-ias\","
            + "        \"binding_guid\": \"68835e70-0342-4e24-91c3-5ba54b12a0cd\","
            + "        \"binding_name\": null,"
            + "        \"credentials\": {"
            + "          \"clientid\": \"2aba8ab2-edc3-4666-b2ed-90b2b47e60e3\","
            + "          \"clientsecret\": \"xnDE_A.8@Bi-?ZP8W/jsW2::w:CmlbTjU\","
            + "          \"domain\": \"localhost\","
            + "          \"zone_uuid\": \"a89ea924-d9c2-4eab-84fb-3ffcaadf5d24\","
            + "          \"app_tid\": \"a89ea924-d9c2-4eab-84fb-3ffcaadf5d24\","
            + "          \"domains\": ["
            + "            \"localhost\""
            + "          ],"
            + "          \"url\": \"HOST\""
            + "        },"
            + "        \"syslog_drain_url\": null,"
            + "        \"volume_mounts\": []"
            + "      }"
            + "    ]"
            + "  }";

    private static final String TEMPLATE_TOKEN_KEYS =
        "{"
            + "  \"keys\" : [ {"
            + "    \"kty\" : \"RSA\","
            + "    \"e\" : \"AQAB\","
            + "    \"use\" : \"sig\","
            + "    \"kid\" : \"testKey\","
            + "    \"alg\" : \"RS256\","
            + "    \"value\" : \"-----BEGIN PUBLIC KEY-----\\n%s\\n-----END PUBLIC KEY-----\""
            + "  } ]"
            + "}";

    @Before
    public void prepareInboundAccessToken()
    {
        final String oauthUrl = wireMockServer.baseUrl();
        final Token tokenTenantA =
            JwtGenerator
                .getInstance(Service.IAS, "2aba8ab2-edc3-4666-b2ed-90b2b47e60e3")
                .withHeaderParameter(TokenHeader.KEY_ID, "testKey")
                .withClaimValue("iss", oauthUrl)
                .withClaimValues("aud", "1668e3ce-e2b8-4d27-a05b-3db47e7a4152", "2aba8ab2-edc3-4666-b2ed-90b2b47e60e3")
                .withClaimValue("sub", "5a21dfa1-fd90-45ff-a5d3-d1cfa446d25a")
                .withClaimValue("user_uuid", "5a21dfa1-fd90-45ff-a5d3-d1cfa446d25a")
                .withClaimValue("azp", "2aba8ab2-edc3-4666-b2ed-90b2b47e60e3")
                .withClaimValue("zone_uuid", "a89ea924-d9c2-4eab-84fb-3ffcaadf5d24")
                .withClaimValue("app_tid", "a89ea924-d9c2-4eab-84fb-3ffcaadf5d24")
                .withClaimValue("given_name", "Foo")
                .withClaimValue("family_name", "Bar")
                .withClaimValue("email", "foo.bar@sap.com")
                .withClaimValue("jti", "7d46d219-a7ce-47e9-974f-33a5d988ff5d")
                .withExpiration(OffsetDateTime.now().plusDays(1).toInstant())
                .withPrivateKey(RSA_KEYS.getPrivate())
                .createToken();
        tokenA = tokenTenantA.getTokenValue();
    }

    @Before
    public void prepareOAuthCertEndpoint()
    {
        final String encodedPublicKey = Base64.getEncoder().encodeToString(RSA_KEYS.getPublic().getEncoded());
        final String tokenKeys = String.format(TEMPLATE_TOKEN_KEYS, encodedPublicKey);
        wireMockServer.stubFor(get(urlPathEqualTo("/oauth2/certs")).willReturn(okJson(tokenKeys)));
    }

    @Before
    public void prepareOpenIdConfiguration()
    {
        final String oauthUrl = wireMockServer.baseUrl();
        final String openIdCOnfiguration = TEMPLATE_OPENID_CONFIGURATION.replaceAll("HOST", oauthUrl);
        wireMockServer
            .stubFor(get(urlPathEqualTo("/.well-known/openid-configuration")).willReturn(okJson(openIdCOnfiguration)));
    }

    @Before
    public void prepareVcapServices()
    {
        CloudPlatformAccessor.setCloudPlatformFacade(cloudPlatformFacade);

        AuthTokenAccessor.setAuthTokenFacade(new ScpCfAuthTokenFacade(new AuthTokenDecoderDefault()));
    }

    @After
    public void cleanPlatform()
    {
        CloudPlatformAccessor.setCloudPlatformFacade(null);
        AuthTokenAccessor.setAuthTokenFacade(null); // reset auth token facade to clear underlying VCAP_SERVICES data
    }

    @Test
    public void testAuthTokenAccessorWithHeaders()
    {
        final RequestHeaderContainer headers =
            DefaultRequestHeaderContainer.builder().withHeader("Authorization", "bearer " + tokenA).build();

        RequestHeaderAccessor.executeWithHeaderContainer(headers, () -> {
            assertThat(AuthTokenAccessor.getCurrentToken().getJwt().getToken()).isEqualTo(tokenA);
            assertThat(AuthTokenAccessor.tryGetCurrentToken().get().getJwt().getToken()).isEqualTo(tokenA);
        });
    }

    @Test
    public void givenIasTokenThenDecodeAndValidateShouldSucceedWithThatToken()
    {
        final RequestHeaderContainer headers =
            DefaultRequestHeaderContainer
                .fromSingleValueMap(Collections.singletonMap(HttpHeaders.AUTHORIZATION, "Bearer " + tokenA));
        final AuthToken authToken = new AuthTokenDecoderDefault().decode(headers).getOrNull();
        assertThat(authToken).isNotNull();

        final DecodedJWT jwt = authToken.getJwt();
        assertThat(jwt).isNotNull();
        assertThat(jwt.getToken()).isEqualTo(tokenA);
    }

    @Test
    public void testMissingAuthorizationHeader()
    {
        final RequestHeaderContainer headers = DefaultRequestHeaderContainer.fromSingleValueMap(Collections.emptyMap());

        VavrAssertions
            .assertThat(new AuthTokenDecoderDefault().decode(headers))
            .failBecauseOf(AuthTokenAccessException.class);
    }

    @Test
    public void testMultipleAuthorizationHeaders()
    {
        final RequestHeaderContainer headers =
            DefaultRequestHeaderContainer
                .builder()
                .withHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tokenA, "Bearer " + tokenA)
                .build();

        final Try<AuthToken> authTokenTry = new AuthTokenDecoderDefault().decode(headers);
        VavrAssertions.assertThat(authTokenTry).isFailure().failBecauseOf(AuthTokenAccessException.class);
    }

    @Test
    public void testInvalidJwt()
    {
        final RequestHeaderContainer headers =
            DefaultRequestHeaderContainer
                .fromSingleValueMap(Collections.singletonMap(HttpHeaders.AUTHORIZATION, "Bearer INVALID"));

        final Try<AuthToken> authTokenTry = new AuthTokenDecoderDefault().decode(headers);
        VavrAssertions.assertThat(authTokenTry).isFailure().failBecauseOf(AuthTokenAccessException.class);
    }

    @Test
    public void testCaseInsensitiveBearer()
    {
        final RequestHeaderContainer headers =
            DefaultRequestHeaderContainer
                .fromSingleValueMap(Collections.singletonMap(HttpHeaders.AUTHORIZATION, "bEaRer " + tokenA));

        final AuthToken authToken = new AuthTokenDecoderDefault().decode(headers).getOrNull();
        assertThat(authToken).isNotNull();

        final DecodedJWT jwt = authToken.getJwt();
        assertThat(jwt).isNotNull();
        assertThat(jwt.getClaim("user_uuid").asString()).isEqualTo("5a21dfa1-fd90-45ff-a5d3-d1cfa446d25a");
    }
}
