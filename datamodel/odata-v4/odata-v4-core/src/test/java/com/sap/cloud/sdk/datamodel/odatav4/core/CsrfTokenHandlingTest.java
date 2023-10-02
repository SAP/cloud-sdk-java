/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.headRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.patch;
import static com.github.tomakehurst.wiremock.client.WireMock.patchRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import java.util.Collections;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;
import com.sap.cloud.sdk.datamodel.odatav4.TestUtility;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person;

public class CsrfTokenHandlingTest
{
    private static final WireMockConfiguration WIREMOCK_CONFIGURATION = wireMockConfig().dynamicPort();

    private static final String SERVICE_PATH = "/remoteService";
    private static final String ENTITY_URL = SERVICE_PATH + "/People";

    private static final String X_CSRF_TOKEN_HEADER_KEY = "x-csrf-token";
    private static final String X_CSRF_TOKEN_HEADER_FETCH_VALUE = "fetch";
    private static final String X_CSRF_TOKEN_HEADER_VALUE = "awesome-csrf-token";

    @Rule
    public WireMockRule wireMockServer = new WireMockRule(WIREMOCK_CONFIGURATION);

    private DefaultHttpDestination destination;

    @Before
    public void setup()
    {
        destination = DefaultHttpDestination.builder(wireMockServer.baseUrl()).build();
    }

    private static String readResourceFile( final String resourceFileName )
    {
        return TestUtility.readResourceFile(CsrfTokenHandlingTest.class, resourceFileName);
    }

    @Test
    public void testGetAllRequestContainsNoCsrfToken()
    {
        stubFor(get(anyUrl()).willReturn(okJson("{\"value\":[]}")));

        // user code
        new GetAllRequestBuilder<>(SERVICE_PATH, Person.class, "People").execute(destination);

        verify(0, headRequestedFor(anyUrl()));
        verify(getRequestedFor(urlEqualTo(ENTITY_URL)).withoutHeader(X_CSRF_TOKEN_HEADER_KEY));
    }

    @Test
    public void testGetByKeyRequestContainsNoCsrfToken()
    {
        stubFor(get(anyUrl()).willReturn(okJson("{}")));

        // user code
        new GetByKeyRequestBuilder<>(SERVICE_PATH, Person.class, Collections.singletonMap("id", 42), "People")
            .execute(destination);

        verify(0, headRequestedFor(anyUrl()));
        verify(getRequestedFor(urlEqualTo(ENTITY_URL + "(42)")).withoutHeader(X_CSRF_TOKEN_HEADER_KEY));
    }

    @Test
    public void testCountRequestContainsNoCsrfToken()
    {
        stubFor(get(anyUrl()).willReturn(okJson("{\"value\":42}")));

        // user code
        new CountRequestBuilder<>(SERVICE_PATH, Person.class, "People").execute(destination);

        verify(0, headRequestedFor(anyUrl()));
        verify(getRequestedFor(urlEqualTo(ENTITY_URL + "/$count")).withoutHeader(X_CSRF_TOKEN_HEADER_KEY));
    }

    @Test
    public void testFunctionRequestContainsNoCsrfToken()
    {
        stubFor(get(anyUrl()).willReturn(okJson("{}")));

        // user code
        new SingleValueFunctionRequestBuilder<>(SERVICE_PATH, ODataResourcePath.of("function"), Object.class)
            .execute(destination);

        stubFor(get(anyUrl()).willReturn(okJson("{\"value\":[]}")));

        // user code
        new CollectionValueFunctionRequestBuilder<>(SERVICE_PATH, ODataResourcePath.of("function"), Object.class)
            .execute(destination);

        verify(0, headRequestedFor(anyUrl()));
        verify(2, getRequestedFor(urlEqualTo(SERVICE_PATH + "/function")).withoutHeader(X_CSRF_TOKEN_HEADER_KEY));
    }

    @Test
    public void testGetAllRequestContainsCsrfTokenIfEnabled()
    {
        stubFor(head(anyUrl()).willReturn(ok().withHeader(X_CSRF_TOKEN_HEADER_KEY, X_CSRF_TOKEN_HEADER_VALUE)));
        stubFor(get(anyUrl()).willReturn(okJson("{\"value\":[]}")));

        // user code
        new GetAllRequestBuilder<>(SERVICE_PATH, Person.class, "People").withCsrfToken().execute(destination);

        verify(
            headRequestedFor(urlEqualTo(SERVICE_PATH))
                .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalTo(X_CSRF_TOKEN_HEADER_FETCH_VALUE)));
        verify(
            getRequestedFor(urlEqualTo(ENTITY_URL))
                .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalTo(X_CSRF_TOKEN_HEADER_VALUE)));
    }

    @Test
    public void testGetByKeyRequestContainsCsrfTokenIfEnabled()
    {
        stubFor(head(anyUrl()).willReturn(ok().withHeader(X_CSRF_TOKEN_HEADER_KEY, X_CSRF_TOKEN_HEADER_VALUE)));
        stubFor(get(anyUrl()).willReturn(okJson("{}")));

        // user code
        new GetByKeyRequestBuilder<>(SERVICE_PATH, Person.class, Collections.singletonMap("id", 42), "People")
            .withCsrfToken()
            .execute(destination);

        verify(
            headRequestedFor(urlEqualTo(SERVICE_PATH))
                .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalTo(X_CSRF_TOKEN_HEADER_FETCH_VALUE)));
        verify(
            getRequestedFor(urlEqualTo(ENTITY_URL + "(42)"))
                .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalTo(X_CSRF_TOKEN_HEADER_VALUE)));
    }

    @Test
    public void testCountRequestContainsCsrfTokenIfEnabled()
    {
        stubFor(head(anyUrl()).willReturn(ok().withHeader(X_CSRF_TOKEN_HEADER_KEY, X_CSRF_TOKEN_HEADER_VALUE)));
        stubFor(get(anyUrl()).willReturn(okJson("{\"value\":42}")));

        // user code
        new CountRequestBuilder<>(SERVICE_PATH, Person.class, "People").withCsrfToken().execute(destination);

        verify(
            headRequestedFor(urlEqualTo(SERVICE_PATH))
                .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalTo(X_CSRF_TOKEN_HEADER_FETCH_VALUE)));
        verify(
            getRequestedFor(urlEqualTo(ENTITY_URL + "/$count"))
                .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalTo(X_CSRF_TOKEN_HEADER_VALUE)));
    }

    @Test
    public void testFunctionRequestContainsCsrfTokenIfEnabled()
    {
        stubFor(head(anyUrl()).willReturn(ok().withHeader(X_CSRF_TOKEN_HEADER_KEY, X_CSRF_TOKEN_HEADER_VALUE)));
        stubFor(get(anyUrl()).willReturn(okJson("{}")));

        // user code
        new SingleValueFunctionRequestBuilder<>(SERVICE_PATH, ODataResourcePath.of("function"), Object.class)
            .withCsrfToken()
            .execute(destination);

        stubFor(get(anyUrl()).willReturn(okJson("{\"value\":[]}")));

        // user code
        new CollectionValueFunctionRequestBuilder<>(SERVICE_PATH, ODataResourcePath.of("function"), Object.class)
            .withCsrfToken()
            .execute(destination);

        verify(
            headRequestedFor(urlEqualTo(SERVICE_PATH))
                .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalTo(X_CSRF_TOKEN_HEADER_FETCH_VALUE)));
        verify(
            getRequestedFor(urlEqualTo(SERVICE_PATH + "/function"))
                .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalTo(X_CSRF_TOKEN_HEADER_VALUE)));
    }

    @Test
    public void testCreateRequestContainsCsrfToken()
    {
        stubFor(head(anyUrl()).willReturn(ok().withHeader(X_CSRF_TOKEN_HEADER_KEY, X_CSRF_TOKEN_HEADER_VALUE)));
        stubFor(post(anyUrl()).willReturn(ok().withBody(readResourceFile("CreateResponse.json"))));

        // user code
        new CreateRequestBuilder<>(SERVICE_PATH, new Person(), "People").execute(destination);

        verify(
            headRequestedFor(urlEqualTo(SERVICE_PATH))
                .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalTo(X_CSRF_TOKEN_HEADER_FETCH_VALUE)));
        verify(
            postRequestedFor(urlEqualTo(ENTITY_URL))
                .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalTo(X_CSRF_TOKEN_HEADER_VALUE)));
    }

    @Test
    public void testDeleteRequestContainsCsrfToken()
    {
        stubFor(head(anyUrl()).willReturn(ok().withHeader(X_CSRF_TOKEN_HEADER_KEY, X_CSRF_TOKEN_HEADER_VALUE)));
        stubFor(delete(anyUrl()).willReturn(ok()));

        // user code
        final Person person = new Person();
        person.setUserName("usr");
        new DeleteRequestBuilder<>(SERVICE_PATH, person, "People").execute(destination);

        verify(
            headRequestedFor(urlEqualTo(SERVICE_PATH))
                .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalTo(X_CSRF_TOKEN_HEADER_FETCH_VALUE)));
        verify(
            deleteRequestedFor(urlEqualTo(ENTITY_URL + "('usr')"))
                .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalTo(X_CSRF_TOKEN_HEADER_VALUE)));
    }

    @Test
    public void testActionRequestContainsCsrfToken()
    {
        stubFor(head(anyUrl()).willReturn(ok().withHeader(X_CSRF_TOKEN_HEADER_KEY, X_CSRF_TOKEN_HEADER_VALUE)));
        stubFor(post(anyUrl()).willReturn(okJson("{}")));

        // user code
        new SingleValueActionRequestBuilder<>(SERVICE_PATH, "ActionName", Collections.emptyMap(), Object.class)
            .execute(destination);

        stubFor(post(anyUrl()).willReturn(okJson("{\"value\":[]}")));

        // user code
        new CollectionValueActionRequestBuilder<>(SERVICE_PATH, "ActionName", Collections.emptyMap(), Object.class)
            .execute(destination);

        verify(
            headRequestedFor(urlEqualTo(SERVICE_PATH))
                .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalTo(X_CSRF_TOKEN_HEADER_FETCH_VALUE)));
        verify(
            2,
            postRequestedFor(urlEqualTo(SERVICE_PATH + "/ActionName"))
                .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalTo(X_CSRF_TOKEN_HEADER_VALUE)));
    }

    @Test
    public void testUpdateRequestContainsCsrfToken()
    {
        stubFor(head(anyUrl()).willReturn(ok().withHeader(X_CSRF_TOKEN_HEADER_KEY, X_CSRF_TOKEN_HEADER_VALUE)));
        stubFor(patch(anyUrl()).willReturn(ok()));

        // user code
        final Person person = new Person();
        person.setUserName("usr");
        new UpdateRequestBuilder<>(SERVICE_PATH, person, "People").execute(destination);

        verify(
            headRequestedFor(urlEqualTo(SERVICE_PATH))
                .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalTo(X_CSRF_TOKEN_HEADER_FETCH_VALUE)));
        verify(
            patchRequestedFor(urlEqualTo(ENTITY_URL + "('usr')"))
                .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalTo(X_CSRF_TOKEN_HEADER_VALUE)));
    }

    @Test
    public void testUpdateRequestContainsNoCsrfTokenIfDisabled()
    {
        stubFor(patch(anyUrl()).willReturn(ok()));

        // user code
        final Person person = new Person();
        person.setUserName("usr");
        new UpdateRequestBuilder<>(SERVICE_PATH, person, "People").withoutCsrfToken().execute(destination);

        verify(0, headRequestedFor(urlEqualTo(SERVICE_PATH)));
        verify(patchRequestedFor(urlEqualTo(ENTITY_URL + "('usr')")).withoutHeader(X_CSRF_TOKEN_HEADER_KEY));
    }

    @Test
    public void testDeleteRequestContainsNoCsrfTokenIfDisabled()
    {
        stubFor(delete(anyUrl()).willReturn(ok()));

        // user code
        final Person person = new Person();
        person.setUserName("usr");
        new DeleteRequestBuilder<>(SERVICE_PATH, person, "People").withoutCsrfToken().execute(destination);

        verify(0, headRequestedFor(urlEqualTo(SERVICE_PATH)));
        verify(deleteRequestedFor(urlEqualTo(ENTITY_URL + "('usr')")).withoutHeader(X_CSRF_TOKEN_HEADER_KEY));
    }

    @Test
    public void testActionRequestContainsNoCsrfTokenIfDisabled()
    {
        stubFor(post(anyUrl()).willReturn(okJson("{}")));

        // user code
        new SingleValueActionRequestBuilder<>(SERVICE_PATH, "ActionName", Collections.emptyMap(), Object.class)
            .withoutCsrfToken()
            .execute(destination);

        stubFor(post(anyUrl()).willReturn(okJson("{\"value\":[]}")));

        // user code
        new CollectionValueActionRequestBuilder<>(SERVICE_PATH, "ActionName", Collections.emptyMap(), Object.class)
            .withoutCsrfToken()
            .execute(destination);

        verify(0, headRequestedFor(urlEqualTo(SERVICE_PATH)));
        verify(2, postRequestedFor(urlEqualTo(SERVICE_PATH + "/ActionName")).withoutHeader(X_CSRF_TOKEN_HEADER_KEY));
    }

    @Test
    public void testCreateRequestContainsNoCsrfTokenIfDisabled()
    {
        stubFor(post(anyUrl()).willReturn(ok().withBody(readResourceFile("CreateResponse.json"))));

        // user code
        new CreateRequestBuilder<>(SERVICE_PATH, new Person(), "People").withoutCsrfToken().execute(destination);

        verify(0, headRequestedFor(urlEqualTo(SERVICE_PATH)));
        verify(postRequestedFor(urlEqualTo(ENTITY_URL)).withoutHeader(X_CSRF_TOKEN_HEADER_KEY));
    }
}
