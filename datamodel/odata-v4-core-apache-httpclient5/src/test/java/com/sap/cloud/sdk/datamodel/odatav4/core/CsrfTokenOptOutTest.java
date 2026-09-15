package com.sap.cloud.sdk.datamodel.odatav4.core;

import static com.github.tomakehurst.wiremock.client.WireMock.absent;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.headRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.noContent;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.patch;
import static com.github.tomakehurst.wiremock.client.WireMock.patchRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.sdk.cloudplatform.connectivity.ApacheHttpClient5Accessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person;

/**
 * Verifies the backward-compatible CSRF opt-out ({@code withoutCsrfToken()}) and the deprecated no-op
 * ({@code withCsrfToken()}) methods on the OData v4 request builders.
 */
@WireMockTest
@SuppressWarnings( "deprecation" )
class CsrfTokenOptOutTest
{
    private static final String SERVICE_PATH = "/remoteService";
    private static final String ENTITY_URL = SERVICE_PATH + "/People('usr')";
    private static final String PEOPLE_URL = SERVICE_PATH + "/People";

    private static final String X_CSRF_TOKEN_HEADER_KEY = "x-csrf-token";

    private DefaultHttpDestination destination;

    @BeforeEach
    void setup( @Nonnull final WireMockRuntimeInfo wm )
    {
        destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
    }

    @Test
    void withoutCsrfTokenSkipsHeadProbeAndSendsNoToken()
    {
        stubFor(head(anyUrl()).willReturn(ok().withHeader(X_CSRF_TOKEN_HEADER_KEY, "should-not-be-used")));
        stubFor(patch(urlEqualTo(ENTITY_URL)).willReturn(ok()));

        final Person person = new Person();
        person.setUserName("usr");

        new UpdateRequestBuilder<>(SERVICE_PATH, person, "People").withoutCsrfToken().execute(destination);

        // no CSRF HEAD probe was fired
        verify(0, headRequestedFor(anyUrl()));
        // the actual write carries neither a CSRF token nor the internal skip marker
        verify(
            patchRequestedFor(urlEqualTo(ENTITY_URL))
                .withHeader(X_CSRF_TOKEN_HEADER_KEY, absent())
                .withHeader(ApacheHttpClient5Accessor.SKIP_CSRF_TOKEN_HEADER, absent()));
    }

    @Test
    void createWithoutCsrfTokenSkipsHeadProbe()
    {
        stubFor(head(anyUrl()).willReturn(ok().withHeader(X_CSRF_TOKEN_HEADER_KEY, "should-not-be-used")));
        stubFor(post(urlPathEqualTo(PEOPLE_URL)).willReturn(ok().withHeader("Content-Type", "application/json")));

        final Person person = new Person();
        person.setUserName("usr");

        new CreateRequestBuilder<>(SERVICE_PATH, person, "People").withoutCsrfToken().execute(destination);

        verify(0, headRequestedFor(anyUrl()));
        verify(
            postRequestedFor(urlPathEqualTo(PEOPLE_URL))
                .withHeader(ApacheHttpClient5Accessor.SKIP_CSRF_TOKEN_HEADER, absent()));
    }

    @Test
    void batchWithoutCsrfTokenSkipsHeadProbe()
    {
        stubFor(head(anyUrl()).willReturn(ok().withHeader(X_CSRF_TOKEN_HEADER_KEY, "should-not-be-used")));
        stubFor(post(urlPathEqualTo(SERVICE_PATH + "/$batch")).willReturn(ok()));

        final Person person = new Person();
        person.setUserName("usr");

        new BatchRequestBuilder(SERVICE_PATH)
            .withoutCsrfToken()
            .addChangeset(new CreateRequestBuilder<>(SERVICE_PATH, person, "People"))
            .execute(destination);

        verify(0, headRequestedFor(anyUrl()));
        verify(
            postRequestedFor(urlPathEqualTo(SERVICE_PATH + "/$batch"))
                .withHeader(ApacheHttpClient5Accessor.SKIP_CSRF_TOKEN_HEADER, absent()));
    }

    @Test
    void actionWithoutCsrfTokenSkipsHeadProbe()
    {
        final String actionUrl = SERVICE_PATH + "/TestAction";
        stubFor(head(anyUrl()).willReturn(ok().withHeader(X_CSRF_TOKEN_HEADER_KEY, "should-not-be-used")));
        stubFor(post(urlPathEqualTo(actionUrl)).willReturn(noContent()));

        new SingleValueActionRequestBuilder<>(SERVICE_PATH, "TestAction", Void.class)
            .withoutCsrfToken()
            .execute(destination);

        verify(0, headRequestedFor(anyUrl()));
        verify(
            postRequestedFor(urlPathEqualTo(actionUrl))
                .withHeader(X_CSRF_TOKEN_HEADER_KEY, absent())
                .withHeader(ApacheHttpClient5Accessor.SKIP_CSRF_TOKEN_HEADER, absent()));
    }

    @Test
    void withCsrfTokenIsNoOpAndReturnsConcreteType()
    {
        stubFor(head(anyUrl()).willReturn(ok().withHeader(X_CSRF_TOKEN_HEADER_KEY, "should-not-be-used")));

        // Compile-time assertion: withCsrfToken() returns the concrete builder type.
        final GetAllRequestBuilder<Person> builder =
            new GetAllRequestBuilder<>(SERVICE_PATH, Person.class, "People").withCsrfToken();

        // A read request never triggers a CSRF HEAD probe.
        stubFor(
            get(urlPathEqualTo(PEOPLE_URL))
                .willReturn(ok().withHeader("Content-Type", "application/json").withBody("{\"value\":[]}")));

        builder.execute(destination);

        verify(0, headRequestedFor(anyUrl()));
    }
}
