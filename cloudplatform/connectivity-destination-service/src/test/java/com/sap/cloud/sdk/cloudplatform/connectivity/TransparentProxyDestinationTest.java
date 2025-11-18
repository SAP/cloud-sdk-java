package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.util.Collection;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

import io.vavr.control.Option;

class TransparentProxyDestinationTest
{
    private static final URI VALID_URI = URI.create("https://www.sap.de");
    private static final String TRANSPARENT_PROXY_GATEWAY = "http://destination-gateway:80";
    private static final String TEST_KEY = "someKey";
    private static final String TEST_VALUE = "someValue";
    private static final String TEST_DEST_NAME = "testDest";
    private static final String TEST_TENANT_SUBDOMAIN = "subdomainValue";
    private static final String TEST_TENANT_ID = "tenantIdValue";
    private static final String TEST_AUTHORIZATION_HEADER = "dummy-jwt-token";

    @Test
    void testGetDelegationDestination()
    {
        final TransparentProxyDestination destination =
            TransparentProxyDestination
                .gateway(TEST_DEST_NAME, VALID_URI.toString())
                .property(TEST_KEY, TEST_VALUE)
                .build();

        assertThat(destination.get(TEST_KEY)).isEqualTo(Option.some(TEST_VALUE));
    }

    @Test
    void testGetUriSuccessfully()
    {
        final TransparentProxyDestination destination =
            TransparentProxyDestination.gateway(TEST_DEST_NAME, VALID_URI.toString()).build();

        Assertions.assertThat(destination.getUri()).isEqualTo(VALID_URI);
    }

    @Test
    void testHeaders()
    {
        final Header header1 = new Header("foo", "bar");
        final Header header2 = new Header("baz", "qux");
        final Header header3 = new Header(TransparentProxyDestination.DESTINATION_NAME_HEADER_KEY, TEST_DEST_NAME);

        final TransparentProxyDestination destination =
            TransparentProxyDestination
                .gateway(TEST_DEST_NAME, VALID_URI.toString())
                .header(header1)
                .header(header2)
                .build();

        final Collection<Header> headers = destination.getHeaders(VALID_URI);
        assertThat(headers).containsExactlyInAnyOrder(header1, header2, header3);
    }

    @Test
    void testGatewayHeaders()
    {
        final TransparentProxyDestination destination =
            TransparentProxyDestination
                .gateway(TEST_DEST_NAME, VALID_URI.toString())
                .destinationLevel(DestinationServiceOptionsAugmenter.CrossLevelScope.SUBACCOUNT)
                .fragmentName("fragName")
                .fragmentLevel(DestinationServiceOptionsAugmenter.CrossLevelScope.PROVIDER_SUBACCOUNT)
                .fragmentOptional(true)
                .build();

        assertThat(destination.getHeaders(VALID_URI))
            .contains(
                new Header(TransparentProxyDestination.DESTINATION_NAME_HEADER_KEY, TEST_DEST_NAME),
                new Header(
                    TransparentProxyDestination.DESTINATION_LEVEL_HEADER_KEY,
                    DestinationServiceOptionsAugmenter.CrossLevelScope.SUBACCOUNT.toString()),
                new Header(TransparentProxyDestination.FRAGMENT_NAME_HEADER_KEY, "fragName"),
                new Header(
                    TransparentProxyDestination.FRAGMENT_LEVEL_HEADER_KEY,
                    DestinationServiceOptionsAugmenter.CrossLevelScope.PROVIDER_SUBACCOUNT.toString()),
                new Header(TransparentProxyDestination.FRAGMENT_OPTIONAL_HEADER_KEY, "true"));
    }

    @Test
    void testTenantHeaders()
    {
        final TransparentProxyDestination destWithTenantId =
            TransparentProxyDestination.destination(VALID_URI.toString()).tenantId("tenantId").build();

        assertThat(destWithTenantId.getHeaders(VALID_URI))
            .contains(new Header(TransparentProxyDestination.TENANT_ID_HEADER_KEY, "tenantId"));

        final TransparentProxyDestination destWithSubdomain =
            TransparentProxyDestination.destination(VALID_URI.toString()).tenantSubdomain("subdomain").build();

        assertThat(destWithSubdomain.getHeaders(VALID_URI))
            .contains(new Header(TransparentProxyDestination.TENANT_SUBDOMAIN_HEADER_KEY, "subdomain"));
    }

    @Test
    void testCommonHeaders()
    {
        final TransparentProxyDestination destination =
            TransparentProxyDestination
                .destination(VALID_URI.toString())
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

        assertThat(destination.getHeaders(VALID_URI))
            .contains(
                new Header(TransparentProxyDestination.TOKEN_SERVICE_TENANT_HEADER_KEY, "tokenTenant"),
                new Header(TransparentProxyDestination.CLIENT_ASSERTION_HEADER_KEY, "clientAssert"),
                new Header(TransparentProxyDestination.CLIENT_ASSERTION_TYPE_HEADER_KEY, "clientAssertType"),
                new Header(
                    TransparentProxyDestination.CLIENT_ASSERTION_DESTINATION_NAME_HEADER_KEY,
                    "clientAssertDest"),
                new Header(TransparentProxyDestination.AUTHORIZATION_HEADER_KEY, "authValue"),
                new Header(TransparentProxyDestination.SUBJECT_TOKEN_TYPE_HEADER_KEY, "subjectTokenType"),
                new Header(TransparentProxyDestination.ACTOR_TOKEN_HEADER_KEY, "actorToken"),
                new Header(TransparentProxyDestination.ACTOR_TOKEN_TYPE_HEADER_KEY, "actorTokenType"),
                new Header(TransparentProxyDestination.REDIRECT_URI_HEADER_KEY, "redirectUri"),
                new Header(TransparentProxyDestination.CODE_VERIFIER_HEADER_KEY, "codeVerifier"),
                new Header(TransparentProxyDestination.CHAIN_NAME_HEADER_KEY, "chainName"),
                new Header(TransparentProxyDestination.CHAIN_VAR_SUBJECT_TOKEN_HEADER_KEY, "chainVarSubjectToken"),
                new Header(
                    TransparentProxyDestination.CHAIN_VAR_SUBJECT_TOKEN_TYPE_HEADER_KEY,
                    "chainVarSubjectTokenType"),
                new Header(
                    TransparentProxyDestination.CHAIN_VAR_SAML_PROVIDER_DESTINATION_NAME_HEADER_KEY,
                    "chainVarSamlProviderDestName"));
    }

    @Test
    void testTenantIdIsAddedPerRequest()
    {
        Tenant tenant = new DefaultTenant(TEST_TENANT_ID, TEST_TENANT_SUBDOMAIN);
        TransparentProxyDestination destination =
            TransparentProxyDestination.gateway(TEST_DEST_NAME, TRANSPARENT_PROXY_GATEWAY).build();
        TenantAccessor.executeWithTenant(tenant, () -> {
            assertThat(destination.getHeaders(URI.create(TRANSPARENT_PROXY_GATEWAY)))
                .contains(new Header(TransparentProxyDestination.TENANT_ID_HEADER_KEY, TEST_TENANT_ID));
        });
    }

    @Test
    void testAuthorizationHeaderIsAddedPerRequest()
    {
        DecodedJWT mockJwt = Mockito.mock(DecodedJWT.class);
        Mockito.when(mockJwt.getToken()).thenReturn(TEST_AUTHORIZATION_HEADER);
        AuthToken token = new AuthToken(mockJwt);

        TransparentProxyDestination destination =
            TransparentProxyDestination.gateway(TEST_DEST_NAME, TRANSPARENT_PROXY_GATEWAY).build();
        assertNotNull(destination);
        AuthTokenAccessor.executeWithAuthToken(token, () -> {
            assertThat(destination.getHeaders(URI.create(TRANSPARENT_PROXY_GATEWAY)))
                .contains(new Header(TransparentProxyDestination.AUTHORIZATION_HEADER_KEY, TEST_AUTHORIZATION_HEADER));
        });
    }

    @Test
    void testBuildThrowsExceptionWhenDestinationNameMissing()
    {
        Assertions
            .assertThatThrownBy(() -> TransparentProxyDestination.gateway("", TRANSPARENT_PROXY_GATEWAY).build())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining(
                "The 'destinationName' property is required for destination-gateway but was not set.");
    }

    @Test
    void testEqualGateway()
    {
        final TransparentProxyDestination destination1 =
            TransparentProxyDestination.gateway(TEST_DEST_NAME, VALID_URI.toString()).build();

        final TransparentProxyDestination destination2 =
            TransparentProxyDestination.gateway(TEST_DEST_NAME, VALID_URI.toString()).build();
        assertThat(destination1).isEqualTo(destination2);
    }

    @Test
    void testEqualDestination()
    {
        final TransparentProxyDestination destination1 =
            TransparentProxyDestination.destination(VALID_URI.toString()).build();

        final TransparentProxyDestination destination2 =
            TransparentProxyDestination.destination(VALID_URI.toString()).build();

        assertThat(destination1).isEqualTo(destination2);
    }

    @Test
    void testTenantIdAndTenantSubdomainCannotBePassedTogether()
    {
        Assertions
            .assertThatThrownBy(
                () -> TransparentProxyDestination
                    .destination(VALID_URI.toString())
                    .tenantId("tenantId")
                    .tenantSubdomain("tenantSubdomain")
                    .build())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining(TransparentProxyDestination.TENANT_ID_AND_TENANT_SUBDOMAIN_BOTH_PASSED_ERROR_MESSAGE);

        Assertions
            .assertThatThrownBy(
                () -> TransparentProxyDestination
                    .destination(VALID_URI.toString())
                    .tenantSubdomain("tenantSubdomain")
                    .tenantId("tenantId")
                    .build())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining(TransparentProxyDestination.TENANT_ID_AND_TENANT_SUBDOMAIN_BOTH_PASSED_ERROR_MESSAGE);
    }

    @Test
    void testNoTenantHeaderWhenNoTenantPresent()
    {
        TransparentProxyDestination destination =
            TransparentProxyDestination.gateway(TEST_DEST_NAME, TRANSPARENT_PROXY_GATEWAY).build();
        Collection<Header> headers = destination.getHeaders(URI.create(TRANSPARENT_PROXY_GATEWAY));
        assertThat(
            headers
                .stream()
                .noneMatch(
                    header -> header.getName().equalsIgnoreCase(TransparentProxyDestination.TENANT_ID_HEADER_KEY)))
            .isTrue();
    }

    @Test
    void testNoAuthorizationHeaderWhenNoAuthTokenPresent()
    {
        TransparentProxyDestination destination =
            TransparentProxyDestination.gateway(TEST_DEST_NAME, TRANSPARENT_PROXY_GATEWAY).build();
        Collection<Header> headers = destination.getHeaders(URI.create(TRANSPARENT_PROXY_GATEWAY));
        assertThat(
            headers
                .stream()
                .noneMatch(
                    header -> header.getName().equalsIgnoreCase(TransparentProxyDestination.AUTHORIZATION_HEADER_KEY)))
            .isTrue();
    }
}
