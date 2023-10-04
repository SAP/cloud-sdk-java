/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper;

import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.headRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okForContentType;
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
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.http.entity.ContentType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.annotations.JsonAdapter;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;

import lombok.Data;
import lombok.EqualsAndHashCode;

public class CsrfTokenHandlingTest
{
    private static final WireMockConfiguration WIREMOCK_CONFIGURATION = wireMockConfig().dynamicPort();

    private static final String SERVICE_PATH = "/remoteService";
    private static final String ENTITY_URL = SERVICE_PATH + "/People";

    private static final String X_CSRF_TOKEN_HEADER_KEY = "x-csrf-token";
    private static final String X_CSRF_TOKEN_HEADER_FETCH_VALUE = "fetch";
    private static final String X_CSRF_TOKEN_HEADER_VALUE = "awesome-csrf-token";

    private static final FluentHelperFactory FACTORY = FluentHelperFactory.withServicePath(SERVICE_PATH);

    @Rule
    public WireMockRule wireMockServer = new WireMockRule(WIREMOCK_CONFIGURATION);

    private DefaultHttpDestination destination;

    @Before
    public void setup()
    {
        destination = DefaultHttpDestination.builder(wireMockServer.baseUrl()).build();
    }

    @Test
    public void testGetAllRequestContainsNoCsrfToken()
    {
        stubFor(get(anyUrl()).willReturn(okJson("{\"d\":{\"results\":[]}}")));

        // user code
        FACTORY.read(Person.class, "People").executeRequest(destination);

        verify(0, headRequestedFor(anyUrl()));
        verify(getRequestedFor(urlEqualTo(ENTITY_URL)).withoutHeader(X_CSRF_TOKEN_HEADER_KEY));
    }

    @Test
    public void testGetByKeyRequestContainsNoCsrfToken()
    {
        stubFor(get(anyUrl()).willReturn(okJson("{\"d\":{}}")));

        // user code
        FACTORY.readByKey(Person.class, "People", Collections.singletonMap("id", 42)).executeRequest(destination);

        verify(0, headRequestedFor(anyUrl()));
        verify(getRequestedFor(urlEqualTo(ENTITY_URL + "(42)")).withoutHeader(X_CSRF_TOKEN_HEADER_KEY));
    }

    @Test
    public void testCountRequestContainsNoCsrfToken()
    {
        stubFor(get(anyUrl()).willReturn(okForContentType(ContentType.TEXT_PLAIN.getMimeType(), "42")));

        // user code
        FACTORY.read(Person.class, "People").count().executeRequest(destination);

        verify(0, headRequestedFor(anyUrl()));
        verify(getRequestedFor(urlEqualTo(ENTITY_URL + "/$count")).withoutHeader(X_CSRF_TOKEN_HEADER_KEY));
    }

    @Test
    public void testFunctionRequestContainsNoCsrfToken()
    {
        stubFor(get(anyUrl()).willReturn(okForContentType(ContentType.TEXT_PLAIN.getMimeType(), "42")));

        // user code
        FACTORY.functionSingleGet(Collections.emptyMap(), "Function", Integer.class).executeRequest(destination);

        stubFor(get(anyUrl()).willReturn(okJson("{\"d\":{\"results\":[]}}}")));

        // user code
        FACTORY.functionMultipleGet(Collections.emptyMap(), "Function", Object.class).executeRequest(destination);

        verify(0, headRequestedFor(anyUrl()));
        verify(2, getRequestedFor(urlEqualTo(SERVICE_PATH + "/Function")).withoutHeader(X_CSRF_TOKEN_HEADER_KEY));
    }

    @Test
    public void testGetAllRequestContainsCsrfTokenIfEnabled()
    {
        stubFor(head(anyUrl()).willReturn(ok().withHeader(X_CSRF_TOKEN_HEADER_KEY, X_CSRF_TOKEN_HEADER_VALUE)));
        stubFor(get(anyUrl()).willReturn(okJson("{\"d\":{\"results\":[]}}")));

        // user code
        FACTORY.read(Person.class, "People").withCsrfToken().executeRequest(destination);

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
        stubFor(get(anyUrl()).willReturn(okJson("{\"d\":{}}")));

        // user code
        FACTORY
            .readByKey(Person.class, "People", Collections.singletonMap("id", 42))
            .withCsrfToken()
            .executeRequest(destination);

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
        stubFor(get(anyUrl()).willReturn(okForContentType(ContentType.TEXT_PLAIN.getMimeType(), "42")));

        // user code
        FACTORY.read(Person.class, "People").count().withCsrfToken().executeRequest(destination);

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
        stubFor(get(anyUrl()).willReturn(okForContentType(ContentType.TEXT_PLAIN.getMimeType(), "42")));

        // user code
        FACTORY
            .functionSingleGet(Collections.emptyMap(), "Function", Integer.class)
            .withCsrfToken()
            .executeRequest(destination);

        stubFor(get(anyUrl()).willReturn(okJson("{\"d\":{\"results\":[]}}}")));

        // user code
        FACTORY
            .functionMultipleGet(Collections.emptyMap(), "Function", Object.class)
            .withCsrfToken()
            .executeRequest(destination);

        verify(
            headRequestedFor(urlEqualTo(SERVICE_PATH))
                .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalTo(X_CSRF_TOKEN_HEADER_FETCH_VALUE)));
        verify(
            getRequestedFor(urlEqualTo(SERVICE_PATH + "/Function"))
                .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalTo(X_CSRF_TOKEN_HEADER_VALUE)));
    }

    @Test
    public void testCreateRequestContainsCsrfToken()
    {
        stubFor(head(anyUrl()).willReturn(ok().withHeader(X_CSRF_TOKEN_HEADER_KEY, X_CSRF_TOKEN_HEADER_VALUE)));
        stubFor(post(anyUrl()).willReturn(okJson("{\"d\":{}}")));

        // user code
        FACTORY.create("People", new Person()).executeRequest(destination);

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
        FACTORY.delete("People", person).executeRequest(destination);

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
        stubFor(post(anyUrl()).willReturn(okForContentType(ContentType.TEXT_PLAIN.getMimeType(), "42")));

        // user code
        FACTORY.functionSinglePost(Collections.emptyMap(), "Function", Integer.class).executeRequest(destination);

        stubFor(post(anyUrl()).willReturn(okJson("{\"d\":{\"results\":[]}}}")));

        // user code
        FACTORY.functionMultiplePost(Collections.emptyMap(), "Function", Object.class).executeRequest(destination);

        verify(
            headRequestedFor(urlEqualTo(SERVICE_PATH))
                .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalTo(X_CSRF_TOKEN_HEADER_FETCH_VALUE)));
        verify(
            2,
            postRequestedFor(urlEqualTo(SERVICE_PATH + "/Function"))
                .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalTo(X_CSRF_TOKEN_HEADER_VALUE)));
    }

    @Test
    public void testUpdateRequestContainsCsrfToken()
    {
        stubFor(head(anyUrl()).willReturn(ok().withHeader(X_CSRF_TOKEN_HEADER_KEY, X_CSRF_TOKEN_HEADER_VALUE)));
        stubFor(patch(anyUrl()).willReturn(okJson("{\"d\":{}}")));

        // user code
        final Person person = new Person();
        person.setUserName("usr");
        FACTORY.update("People", person).executeRequest(destination);

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
        stubFor(patch(anyUrl()).willReturn(okJson("{\"d\":{}}")));

        // user code
        final Person person = new Person();
        person.setUserName("usr");
        FACTORY.update("People", person).withoutCsrfToken().executeRequest(destination);

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
        FACTORY.delete("People", person).withoutCsrfToken().executeRequest(destination);

        verify(0, headRequestedFor(urlEqualTo(SERVICE_PATH)));
        verify(deleteRequestedFor(urlEqualTo(ENTITY_URL + "('usr')")).withoutHeader(X_CSRF_TOKEN_HEADER_KEY));
    }

    @Test
    public void testActionRequestContainsNoCsrfTokenIfDisabled()
    {
        stubFor(post(anyUrl()).willReturn(okForContentType(ContentType.TEXT_PLAIN.getMimeType(), "42")));

        // user code
        FACTORY
            .functionSinglePost(Collections.emptyMap(), "Function", Integer.class)
            .withoutCsrfToken()
            .executeRequest(destination);

        stubFor(post(anyUrl()).willReturn(okJson("{\"d\":{\"results\":[]}}}")));

        // user code
        FACTORY
            .functionMultiplePost(Collections.emptyMap(), "Function", Object.class)
            .withoutCsrfToken()
            .executeRequest(destination);

        verify(0, headRequestedFor(urlEqualTo(SERVICE_PATH)));
        verify(2, postRequestedFor(urlEqualTo(SERVICE_PATH + "/Function")).withoutHeader(X_CSRF_TOKEN_HEADER_KEY));
    }

    @Test
    public void testCreateRequestContainsNoCsrfTokenIfDisabled()
    {
        stubFor(post(anyUrl()).willReturn(okJson("{\"d\":{}}")));

        // user code
        FACTORY.create("People", new Person()).withoutCsrfToken().executeRequest(destination);

        verify(0, headRequestedFor(urlEqualTo(SERVICE_PATH)));
        verify(postRequestedFor(urlEqualTo(ENTITY_URL)).withoutHeader(X_CSRF_TOKEN_HEADER_KEY));
    }

    @Data
    @EqualsAndHashCode( callSuper = true )
    @JsonAdapter( com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory.class )
    public static class Person extends VdmEntity<Person>
    {
        private final String entityCollection = "ignore";
        private final Class<Person> type = Person.class;
        private String userName;

        @Nonnull
        @Override
        protected Map<String, Object> getKey()
        {
            return Collections.singletonMap("UserName", userName);
        }
    }
}
