package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Collection;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import io.vavr.control.Option;

class TransparentProxyDestinationTest
{
    private static final URI VALID_URI = URI.create("https://www.sap.de");
    private static final String TLS_VERSION = "TLSv1.3";

    @Test
    void testGetDelegation()
    {
        final String someKey = "someKey";
        final Object someValue = "someValue";

        final TransparentProxyDestination destination =
            new TransparentProxyDestination.Builder().property(someKey, someValue).build();

        assertThat(destination.get(someKey)).isEqualTo(Option.some(someValue));
    }

    @Test
    void testCanBeConstructedWithUri()
    {
        final TransparentProxyDestination destination =
            new TransparentProxyDestination.Builder().property(DestinationProperty.URI, VALID_URI.toString()).build();

        assertThat(destination.getUri()).isEqualTo(VALID_URI);
    }

    @Test
    void testGetUriSuccessfully()
    {
        final TransparentProxyDestination destination =
            new TransparentProxyDestination.Builder().property(DestinationProperty.URI, VALID_URI.toString()).build();

        Assertions.assertThat(destination.getUri()).isEqualTo(VALID_URI);
    }

    @Test
    void testGetUriDefaultsIfMissing()
    {
        final TransparentProxyDestination destination = new TransparentProxyDestination.Builder().build();
        assertThat(destination.getUri()).isEqualTo(URI.create("http://destination-gateway:80"));
    }

    @Test
    void testHeaders()
    {
        final Header header1 = new Header("foo", "bar");
        final Header header2 = new Header("baz", "qux");

        final TransparentProxyDestination destination =
            new TransparentProxyDestination.Builder()
                .header(header1)
                .header(header2)
                .property(DestinationProperty.URI, VALID_URI.toString())
                .build();

        final Collection<Header> headers = destination.getHeaders(VALID_URI);
        assertThat(headers).containsExactlyInAnyOrder(header1, header2);
    }

    @Test
    void testEmptyHeaders()
    {
        final TransparentProxyDestination destination =
            new TransparentProxyDestination.Builder().property(DestinationProperty.URI, VALID_URI.toString()).build();

        assertThat(destination.getHeaders(VALID_URI)).isEmpty();
    }

    @Test
    void testProxyTypeIsInternet()
    {
        final TransparentProxyDestination destination = new TransparentProxyDestination.Builder().build();

        assertThat(destination.getProxyType()).contains(ProxyType.INTERNET);
    }

    @Test
    void testGetTlsVersion()
    {
        final TransparentProxyDestination destination =
            new TransparentProxyDestination.Builder()
                .property(DestinationProperty.URI, VALID_URI.toString())
                .property(DestinationProperty.TLS_VERSION, TLS_VERSION)
                .build();

        assertThat(destination.getTlsVersion()).isEqualTo(Option.some(TLS_VERSION));
    }

    @Test
    void testBuilderHeaderShortcuts()
    {
        TransparentProxyDestination destination =
            new TransparentProxyDestination.Builder()
                .destinationName("destName")
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
                "x-destination-name",
                "x-fragment-name",
                "x-tenant-subdomain",
                "x-tenant-id",
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
}
