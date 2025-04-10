package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.util.Collection;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

import io.vavr.control.Option;

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

    @Test
    void testGetDelegation()
    {
        final String someKey = TEST_KEY;
        final Object someValue = TEST_VALUE;

        final TransparentProxyDestination destination =
            new TransparentProxyDestination.Builder()
                .property(someKey, someValue)
                .destinationName(TEST_DEST_NAME)
                .build();

        assertThat(destination.get(someKey)).isEqualTo(Option.some(someValue));
    }

    @Test
    void testCanBeConstructedWithUri()
    {
        final TransparentProxyDestination destination =
            new TransparentProxyDestination.Builder()
                .property(DestinationProperty.URI, VALID_URI.toString())
                .destinationName(TEST_DEST_NAME)
                .build();

        assertThat(destination.getUri()).isEqualTo(VALID_URI);
    }

    @Test
    void testGetUriSuccessfully()
    {
        final TransparentProxyDestination destination =
            new TransparentProxyDestination.Builder()
                .property(DestinationProperty.URI, VALID_URI.toString())
                .destinationName(TEST_DEST_NAME)
                .build();

        Assertions.assertThat(destination.getUri()).isEqualTo(VALID_URI);
    }

    @Test
    void testGetUriDefaultsIfMissing()
    {
        final TransparentProxyDestination destination =
            new TransparentProxyDestination.Builder().destinationName(TEST_DEST_NAME).build();
        assertThat(destination.getUri()).isEqualTo(URI.create(TRANSPARENT_PROXY_GATEWAY));
    }

    @Test
    void testHeaders()
    {
        final Header header1 = new Header("foo", "bar");
        final Header header2 = new Header("baz", "qux");
        final Header header3 = new Header(DESTINATION_NAME_HEADER_KEY, TEST_DEST_NAME);

        final TransparentProxyDestination destination =
            new TransparentProxyDestination.Builder()
                .header(header1)
                .header(header2)
                .property(DestinationProperty.URI, VALID_URI.toString())
                .destinationName(TEST_DEST_NAME)
                .build();

        final Collection<Header> headers = destination.getHeaders(VALID_URI);
        assertThat(headers).containsExactlyInAnyOrder(header1, header2, header3);
    }

    @Test
    void testBuilderHeaderShortcuts()
    {
        TransparentProxyDestination destination =
            new TransparentProxyDestination.Builder()
                .destinationName(TEST_DEST_NAME)
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
                .property(DestinationProperty.URI, "https://example.com")
                .build();

        assertThat(destination.getHeaders(URI.create("https://example.com")))
            .extracting(Header::getName)
            .contains(
                DESTINATION_NAME_HEADER_KEY,
                "x-fragment-name",
                TENANT_SUBDOMAIN_HEADER_KEY,
                TENANT_ID_HEADER_KEY,
                "x-fragment-optional",
                "x-token-service-tenant",
                "x-client-assertion",
                "x-client-assertion-type",
                "x-client-assertion-destination-name",
                "authorization",
                "x-subject-token-type",
                "x-actor-token",
                "x-actor-token-type",
                "x-redirect-uri",
                "x-code-verifier",
                "x-chain-name",
                "x-chain-var-subjectToken",
                "x-chain-var-subjectTokenType",
                "x-chain-var-samlProviderDestinationName");
    }

    @Test
    void testBuildAddsTenantIdHeaderIfTenantPresent()
    {
        Tenant tenant = new DefaultTenant(TEST_TENANT_ID, TEST_TENANT_SUBDOMAIN);
        TransparentProxyDestination destination =
            TenantAccessor
                .executeWithTenant(
                    tenant,
                    () -> new TransparentProxyDestination.Builder().destinationName(TEST_DEST_NAME).build());

        assertNotNull(destination);
        assertThat(destination.getHeaders(URI.create(TRANSPARENT_PROXY_GATEWAY))).anyMatch(header -> {
            if( !header.getName().equalsIgnoreCase(TENANT_ID_HEADER_KEY) )
                return false;
            assertNotNull(header.getValue());
            return header.getValue().equals(TEST_TENANT_ID);
        });
    }

    @Test
    void testBuildAddsTenantSubdomainHeaderIfNoTenantIdHeaderAndSubdomainPresent()
    {
        Tenant tenant = new DefaultTenant("", TEST_TENANT_SUBDOMAIN);
        TransparentProxyDestination destination =
            TenantAccessor
                .executeWithTenant(
                    tenant,
                    () -> new TransparentProxyDestination.Builder().destinationName(TEST_DEST_NAME).build());

        assertNotNull(destination);
        assertThat(destination.getHeaders(URI.create(TRANSPARENT_PROXY_GATEWAY))).anyMatch(header -> {
            if( !header.getName().equalsIgnoreCase(TENANT_SUBDOMAIN_HEADER_KEY) )
                return false;
            assertNotNull(header.getValue());
            return header.getValue().equals(TEST_TENANT_SUBDOMAIN);
        });
    }

    @Test
    void testBuildThrowsExceptionWhenDestinationNameMissing()
    {
        Assertions
            .assertThatThrownBy(() -> new TransparentProxyDestination.Builder().build())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("The 'destinationName' property is required but was not set.");
    }
}
