package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceV1Response.DestinationAuthToken;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;

class AuthTokenHeaderProviderTest
{
    private DestinationAuthToken token;
    private HttpDestination destination;
    private DestinationRequestContext context;
    private AuthTokenHeaderProvider sut;

    @BeforeEach
    void setUp()
    {
        sut = new AuthTokenHeaderProvider();
        token = new DestinationAuthToken();
    }

    @AfterEach
    void reset()
    {
        destination = null;
        context = null;
    }

    @Test
    void testSecuritySessionHeader()
    {
        destination =
            DefaultHttpDestination.builder("foo").authenticationType(AuthenticationType.SAML_ASSERTION).build();
        context = new DestinationRequestContext(destination, URI.create("https://www.sap.de"));

        final Collection<Header> result = sut.getHeaders(context);

        assertThat(result)
            .describedAs("A security session 'create' header should always be added for AuthType SamlAssertion")
            .containsExactly(new Header("x-sap-security-session", "create"));
    }

    @Test
    void testHeaderSuggestion()
    {
        token.setHttpHeaderSuggestion(new Header("foo", "bar"));

        prepareDestination(token);

        final Collection<Header> result = sut.getHeaders(context);

        assertThat(result).containsExactly(new Header("foo", "bar"));
    }

    @Test
    void testMultipleHeaderSuggestions()
    {
        final DestinationAuthToken token1 = token;
        final Header header1 = new Header("header", "value");
        token1.setHttpHeaderSuggestion(header1);
        final DestinationAuthToken token2 = new DestinationAuthToken();
        final Header header2 = new Header("header-duplicate", "foo");
        token2.setHttpHeaderSuggestion(header2);
        final Header header3 = new Header("header-duplicate", "bar");
        final DestinationAuthToken token3 = new DestinationAuthToken();
        token3.setHttpHeaderSuggestion(header3);
        final Header header4 = new Header("header-special-chars", "?!$^(:);");
        final DestinationAuthToken token4 = new DestinationAuthToken();
        token4.setHttpHeaderSuggestion(header4);

        prepareDestination(token1, token2, token3, token4);

        final Collection<Header> result = sut.getHeaders(context);

        assertThat(result).containsExactlyInAnyOrder(header1, header2, header3, header4);
    }

    @Test
    void testLegacyFallbackLogic()
    {
        token.setType("foo");
        token.setValue("bar");

        prepareDestination(token);

        final Collection<Header> result = sut.getHeaders(context);

        assertThat(result).containsExactly(new Header(HttpHeaders.AUTHORIZATION, "foo bar"));
    }

    @Test
    @DisplayName( "An empty token should cause a failure" )
    void testEmptyToken()
    {
        prepareDestination(token);

        assertThatThrownBy(() -> sut.getHeaders(context)).isInstanceOf(DestinationAccessException.class);
    }

    @Test
    @DisplayName( "A token with only an error should cause a failure" )
    void testTokenError()
    {
        token.setError("error");

        prepareDestination(token);

        assertThatThrownBy(() -> sut.getHeaders(context)).isInstanceOf(DestinationAccessException.class);
    }

    private void prepareDestination( DestinationAuthToken... tokens )
    {
        destination =
            DefaultHttpDestination
                .builder(URI.create("https://www.sap.de"))
                .property(DestinationProperty.AUTH_TOKENS, Arrays.asList(tokens))
                .build();
        context = new DestinationRequestContext(destination, URI.create("https://www.sap.de"));
    }
}
