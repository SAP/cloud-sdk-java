package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.util.Collection;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

import io.vavr.control.Option;
import org.mockito.Mockito;

class TransparentProxyDestinationTest
{
    private static final URI VALID_URI = URI.create("https://www.sap.de");
    private static final String TRANSPARENT_PROXY_GATEWAY = "http://destination-gateway:80";
    private static final String TENANT_SUBDOMAIN_HEADER_KEY = "x-tenant-subdomain";
    private static final String TENANT_ID_HEADER_KEY = "x-tenant-id";
    private static final String TEST_KEY = "someKey";
    private static final String TEST_VALUE = "someValue";
    private static final String TEST_DEST_NAME = "testDest";

    private static final String DESTINATION_NAME_HEADER_KEY = "x-destination-name";
    private static final String TEST_TENANT_SUBDOMAIN = "subdomainValue";
    private static final String TEST_TENANT_ID = "tenantIdValue";
    private static final String AUTHORIZATION_HEADER_KEY = "authorization";
    private static final String TEST_AUTHORIZATION_HEADER = "dummy-jwt-token";

    private static final String FRAGMENT_NAME_HEADER_KEY = "x-fragment-name";
    private static final String FRAGMENT_OPTIONAL_HEADER_KEY = "x-fragment-optional";
    private static final String TOKEN_SERVICE_TENANT_HEADER_KEY = "x-token-service-tenant";
    private static final String CLIENT_ASSERTION_HEADER_KEY = "x-client-assertion";
    private static final String CLIENT_ASSERTION_TYPE_HEADER_KEY = "x-client-assertion-type";
    private static final String CLIENT_ASSERTION_DESTINATION_NAME_HEADER_KEY = "x-client-assertion-destination-name";
    private static final String SUBJECT_TOKEN_TYPE_HEADER_KEY = "x-subject-token-type";
    private static final String ACTOR_TOKEN_HEADER_KEY = "x-actor-token";
    private static final String ACTOR_TOKEN_TYPE_HEADER_KEY = "x-actor-token-type";
    private static final String REDIRECT_URI_HEADER_KEY = "x-redirect-uri";
    private static final String CODE_VERIFIER_HEADER_KEY = "x-code-verifier";
    private static final String CHAIN_NAME_HEADER_KEY = "x-chain-name";
    private static final String CHAIN_VAR_SUBJECT_TOKEN_HEADER_KEY = "x-chain-var-subjectToken";
    private static final String CHAIN_VAR_SUBJECT_TOKEN_TYPE_HEADER_KEY = "x-chain-var-subjectTokenType";
    private static final String CHAIN_VAR_SAML_PROVIDER_DESTINATION_NAME_HEADER_KEY =
        "x-chain-var-samlProviderDestinationName";

    @Test
    void testGetDelegationDestination()
    {
        final TransparentProxyDestination destination =
            TransparentProxyDestination
                .dynamicDestination(TEST_DEST_NAME, VALID_URI.toString())
                .property(TEST_KEY, TEST_VALUE)
                .build();

        assertThat(destination.get(TEST_KEY)).isEqualTo(Option.some(TEST_VALUE));
    }

    @Test
    void testGetUriSuccessfully()
    {
        final TransparentProxyDestination destination =
            TransparentProxyDestination.dynamicDestination(TEST_DEST_NAME, VALID_URI.toString()).build();

        Assertions.assertThat(destination.getUri()).isEqualTo(VALID_URI);
    }

    @Test
    void testHeaders()
    {
        final Header header1 = new Header("foo", "bar");
        final Header header2 = new Header("baz", "qux");
        final Header header3 = new Header(DESTINATION_NAME_HEADER_KEY, TEST_DEST_NAME);

        final TransparentProxyDestination destination =
            TransparentProxyDestination
                .dynamicDestination(TEST_DEST_NAME, VALID_URI.toString())
                .header(header1)
                .header(header2)
                .build();

        final Collection<Header> headers = destination.getHeaders(VALID_URI);
        assertThat(headers).containsExactlyInAnyOrder(header1, header2, header3);
    }

    @Test
    void testBuilderHeaderShortcutsDynamic()
    {
        TransparentProxyDestination destination =
            TransparentProxyDestination
                .dynamicDestination(TEST_DEST_NAME, VALID_URI.toString())
                .fragmentName("fragName")
                .tenantSubdomain("subdomain")
                .tenantId("tenantId")
                .fragmentOptional("fragOpt")
                .tokenServiceTenant("tokenTenant")
                .clientAssertion("clientAssert")
                .clientAssertionType("clientAssertType")
                .clientAssertionDestinationName("clientAssertDest")
                .authorization("authValue")
                .subjectTokenType("subjectTokenType")
                .actorToken("actorToken")
                .actorTokenType("actorTokenType")
                .redirectUri("redirectUri")
                .codeVerifier("codeVerifier")
                .chainName("chainName")
                .chainVarSubjectToken("chainVarSubjectToken")
                .chainVarSubjectTokenType("chainVarSubjectTokenType")
                .chainVarSamlProviderDestinationName("chainVarSamlProviderDestName")
                .build();

        assertThat(destination.getHeaders(URI.create(VALID_URI.toString())))
            .extracting(Header::getName)
            .contains(
                DESTINATION_NAME_HEADER_KEY,
                FRAGMENT_NAME_HEADER_KEY,
                TENANT_SUBDOMAIN_HEADER_KEY,
                TENANT_ID_HEADER_KEY,
                FRAGMENT_OPTIONAL_HEADER_KEY,
                TOKEN_SERVICE_TENANT_HEADER_KEY,
                CLIENT_ASSERTION_HEADER_KEY,
                CLIENT_ASSERTION_TYPE_HEADER_KEY,
                CLIENT_ASSERTION_DESTINATION_NAME_HEADER_KEY,
                AUTHORIZATION_HEADER_KEY,
                SUBJECT_TOKEN_TYPE_HEADER_KEY,
                ACTOR_TOKEN_HEADER_KEY,
                ACTOR_TOKEN_TYPE_HEADER_KEY,
                REDIRECT_URI_HEADER_KEY,
                CODE_VERIFIER_HEADER_KEY,
                CHAIN_NAME_HEADER_KEY,
                CHAIN_VAR_SUBJECT_TOKEN_HEADER_KEY,
                CHAIN_VAR_SUBJECT_TOKEN_TYPE_HEADER_KEY,
                CHAIN_VAR_SAML_PROVIDER_DESTINATION_NAME_HEADER_KEY);
    }

    @Test
    void testBuilderHeaderShortcutsStatic()
    {
        TransparentProxyDestination destination =
            TransparentProxyDestination
                .staticDestination(VALID_URI.toString())
                .tenantSubdomain("subdomain")
                .tenantId("tenantId")
                .tokenServiceTenant("tokenTenant")
                .clientAssertion("clientAssert")
                .clientAssertionType("clientAssertType")
                .clientAssertionDestinationName("clientAssertDest")
                .authorization("authValue")
                .subjectTokenType("subjectTokenType")
                .actorToken("actorToken")
                .actorTokenType("actorTokenType")
                .redirectUri("redirectUri")
                .codeVerifier("codeVerifier")
                .chainName("chainName")
                .chainVarSubjectToken("chainVarSubjectToken")
                .chainVarSubjectTokenType("chainVarSubjectTokenType")
                .chainVarSamlProviderDestinationName("chainVarSamlProviderDestName")
                .build();

        assertThat(destination.getHeaders(URI.create(VALID_URI.toString())))
            .extracting(Header::getName)
            .contains(
                TENANT_SUBDOMAIN_HEADER_KEY,
                TENANT_ID_HEADER_KEY,
                TOKEN_SERVICE_TENANT_HEADER_KEY,
                CLIENT_ASSERTION_HEADER_KEY,
                CLIENT_ASSERTION_TYPE_HEADER_KEY,
                CLIENT_ASSERTION_DESTINATION_NAME_HEADER_KEY,
                AUTHORIZATION_HEADER_KEY,
                SUBJECT_TOKEN_TYPE_HEADER_KEY,
                ACTOR_TOKEN_HEADER_KEY,
                ACTOR_TOKEN_TYPE_HEADER_KEY,
                REDIRECT_URI_HEADER_KEY,
                CODE_VERIFIER_HEADER_KEY,
                CHAIN_NAME_HEADER_KEY,
                CHAIN_VAR_SUBJECT_TOKEN_HEADER_KEY,
                CHAIN_VAR_SUBJECT_TOKEN_TYPE_HEADER_KEY,
                CHAIN_VAR_SAML_PROVIDER_DESTINATION_NAME_HEADER_KEY);
    }

    @Test
    void testBuildAddsTenantIdHeaderIfTenantPresent()
    {
        Tenant tenant = new DefaultTenant(TEST_TENANT_ID, TEST_TENANT_SUBDOMAIN);
        TransparentProxyDestination destination =
            TransparentProxyDestination.dynamicDestination(TEST_DEST_NAME, TRANSPARENT_PROXY_GATEWAY).build();
        assertNotNull(destination);
        TenantAccessor.executeWithTenant(tenant, () -> {
            assertThat(destination.getHeaders(URI.create(TRANSPARENT_PROXY_GATEWAY))).anyMatch(header -> {
                if( !header.getName().equalsIgnoreCase(TENANT_ID_HEADER_KEY) )
                    return false;
                assertNotNull(header.getValue());
                return header.getValue().equals(TEST_TENANT_ID);
            });
        });
    }

    @Test
    void testBuildAddsAuthorizationHeaderIfPresent()
    {
        DecodedJWT mockJwt = Mockito.mock(DecodedJWT.class);
        Mockito.when(mockJwt.getToken()).thenReturn(TEST_AUTHORIZATION_HEADER);
        AuthToken token = new AuthToken(mockJwt);

        TransparentProxyDestination destination =
            TransparentProxyDestination.dynamicDestination(TEST_DEST_NAME, TRANSPARENT_PROXY_GATEWAY).build();
        assertNotNull(destination);
        AuthTokenAccessor.executeWithAuthToken(token, () -> {
            assertThat(destination.getHeaders(URI.create(TRANSPARENT_PROXY_GATEWAY))).anyMatch(header -> {
                if( !header.getName().equalsIgnoreCase(AUTHORIZATION_HEADER_KEY) )
                    return false;
                assertNotNull(header.getValue());
                return header.getValue().equals(TEST_AUTHORIZATION_HEADER);
            });
        });
    }

    @Test
    void testBuildAddsTenantSubdomainHeaderIfNoTenantIdHeaderAndSubdomainPresent()
    {
        Tenant tenant = new DefaultTenant("", TEST_TENANT_SUBDOMAIN);
        TransparentProxyDestination destination =
            TransparentProxyDestination.dynamicDestination(TEST_DEST_NAME, TRANSPARENT_PROXY_GATEWAY).build();
        assertNotNull(destination);
        TenantAccessor.executeWithTenant(tenant, () -> {
            assertThat(destination.getHeaders(URI.create(TRANSPARENT_PROXY_GATEWAY))).anyMatch(header -> {
                if( !header.getName().equalsIgnoreCase(TENANT_SUBDOMAIN_HEADER_KEY) )
                    return false;
                assertNotNull(header.getValue());
                return header.getValue().equals(TEST_TENANT_SUBDOMAIN);
            });
        });
    }

    @Test
    void testBuildThrowsExceptionWhenDestinationNameMissing()
    {
        Assertions
            .assertThatThrownBy(
                () -> TransparentProxyDestination.dynamicDestination("", TRANSPARENT_PROXY_GATEWAY).build())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("The 'destinationName' property is required but was not set.");
    }
}
