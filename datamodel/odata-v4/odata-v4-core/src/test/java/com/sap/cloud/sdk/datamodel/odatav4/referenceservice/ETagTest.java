/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.referenceservice;

import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.anyRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.noContent;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.patch;
import static com.github.tomakehurst.wiremock.client.WireMock.patchRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.apache.http.HttpHeaders;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.datamodel.odatav4.TestUtility;
import com.sap.cloud.sdk.datamodel.odatav4.core.ModificationResponse;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.services.DefaultTrippinService;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.services.TrippinService;

/**
 * Tests the proper submission of the entity version identifier, transmitted as ETag in the HTTP headers.
 */
public class ETagTest
{
    private static final String SERVICE_URL = "/some/service";
    private static final String ENTITY_COLLECTION = "People";
    private static final String GET_ALL_REQUEST_URL = SERVICE_URL + "/" + ENTITY_COLLECTION;
    private static final String CREATE_URL = SERVICE_URL + "/" + ENTITY_COLLECTION;
    private static final String GET_BY_KEY_REQUEST_URL = SERVICE_URL + "/" + ENTITY_COLLECTION + "('russellwhyte')";

    private static final String GET_ALL_RESPONSE_BODY = readResourceFile("GetAllResponseBody.json");
    private static final String GET_BY_KEY_RESPONSE_BODY = readResourceFile("GetSingleResponseBody.json");

    private static final String ETAG_RUSSELL = "W/\"123\"";
    // the jetty under wiremock appends a '--gzip' to the ETag, therefore we need to check this adjusted ETag
    private static final String ETAG_RUSSELL_GZIPPED = "W/\"123--gzip\"";
    private static final String ETAG_RUSSELL_UPDATED = "W/\"456\"";
    private static final String ETAG_SCOTT = "W/\"999\"";

    private static final String X_CSRF_TOKEN_HEADER_KEY = "x-csrf-token";
    private static final String X_CSRF_TOKEN_HEADER_FETCH_VALUE = "fetch";
    private static final String X_CSRF_TOKEN_HEADER_VALUE = "awesome-csrf-token";

    private static final WireMockConfiguration WIREMOCK_CONFIGURATION = wireMockConfig().dynamicPort();

    @Rule
    public WireMockRule wireMockServer = new WireMockRule(WIREMOCK_CONFIGURATION);

    private DefaultHttpDestination destination;
    private final TrippinService service = new DefaultTrippinService().withServicePath(SERVICE_URL);

    @Before
    public void setup()
    {
        destination = DefaultHttpDestination.builder(wireMockServer.baseUrl()).build();

        tellWiremockToReturnCsrfToken();
    }

    private void tellWiremockToReturnCsrfToken()
    {
        stubFor(
            head(urlEqualTo(SERVICE_URL))
                .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalTo(X_CSRF_TOKEN_HEADER_FETCH_VALUE))
                .willReturn(ok().withHeader(X_CSRF_TOKEN_HEADER_KEY, X_CSRF_TOKEN_HEADER_VALUE)));
    }

    private static String readResourceFile( final String resourceFileName )
    {
        return TestUtility.readResourceFile(ETagTest.class, resourceFileName);
    }

    @Test
    public void testParseETagFromGetAllResponse()
    {
        stubFor(get(urlEqualTo(GET_ALL_REQUEST_URL)).willReturn(okJson(GET_ALL_RESPONSE_BODY)));

        final List<Person> result = service.getAllPeople().execute(destination);
        assertThat(result.get(0).getVersionIdentifier().get()).isEqualTo(ETAG_RUSSELL);
        assertThat(result.get(1).getVersionIdentifier().get()).isEqualTo(ETAG_SCOTT);
    }

    @Test
    public void testParseETagFromCreateResponse()
    {
        stubFor(
            post(urlEqualTo(CREATE_URL)).willReturn(okJson(GET_BY_KEY_RESPONSE_BODY).withHeader("ETag", ETAG_RUSSELL)));
        final Person person = Person.builder().firstName("Russel").build();
        final ModificationResponse<Person> result = service.createPeople(person).execute(destination);

        assertThat(
            result
                .getModifiedEntity()
                .getVersionIdentifier()
                .getOrElseThrow(
                    () -> new AssertionError("Expected version identifier to be present on Create response.")))
            .isEqualTo(ETAG_RUSSELL_GZIPPED);
    }

    @Test
    public void testParseETagFromUpdateResponse()
    {
        stubFor(
            patch(urlEqualTo(GET_BY_KEY_REQUEST_URL))
                .willReturn(okJson(GET_BY_KEY_RESPONSE_BODY).withHeader("ETag", ETAG_RUSSELL)));
        final Person person = Person.builder().userName("russellwhyte").build();
        final ModificationResponse<Person> result = service.updatePeople(person).execute(destination);

        assertThat(
            result
                .getModifiedEntity()
                .getVersionIdentifier()
                .getOrElseThrow(
                    () -> new AssertionError("Expected version identifier to be present on Update response.")))
            .isEqualTo(ETAG_RUSSELL_GZIPPED);
    }

    @Test
    public void testParseETagFromGetByKeyResponse()
    {
        stubFor(
            get(urlEqualTo(GET_BY_KEY_REQUEST_URL))
                .willReturn(okJson(GET_BY_KEY_RESPONSE_BODY).withHeader("ETag", ETAG_RUSSELL)));

        final Person russel = service.getPeopleByKey("russellwhyte").execute(destination);

        assertThat(
            russel
                .getVersionIdentifier()
                .getOrElseThrow(
                    () -> new AssertionError("Expected version identifier to be present on GetByKey response.")))
            .isEqualTo(ETAG_RUSSELL_GZIPPED);
    }

    @Test
    public void testUpdateWithPATCHContainsEtagByDefault()
    {
        stubFor(
            patch(urlEqualTo(GET_BY_KEY_REQUEST_URL))
                .withHeader(HttpHeaders.IF_MATCH, equalTo(ETAG_RUSSELL))
                .willReturn(noContent().withHeader("ETag", ETAG_RUSSELL_UPDATED)));

        final Person russel = new Person();
        russel.setUserName("russellwhyte");
        russel.setVersionIdentifier(ETAG_RUSSELL);

        final ModificationResponse<Person> result = service.updatePeople(russel).modifyingEntity().execute(destination);

        assertThat(russel.getVersionIdentifier()).containsExactly(ETAG_RUSSELL); // origin entity remains unchanged
        assertThat(result.getUpdatedVersionIdentifier()).containsExactly(ETAG_RUSSELL_UPDATED);
        assertThat(result.getModifiedEntity().getVersionIdentifier()).containsExactly(ETAG_RUSSELL_UPDATED);

        verify(
            patchRequestedFor(urlEqualTo(GET_BY_KEY_REQUEST_URL))
                .withHeader(HttpHeaders.IF_MATCH, equalTo(ETAG_RUSSELL)));
    }

    @Test
    public void testUpdateWithPATCHLacksETagIfDisabled()
    {
        stubFor(
            patch(urlEqualTo(GET_BY_KEY_REQUEST_URL)).willReturn(noContent().withHeader("ETag", ETAG_RUSSELL_UPDATED)));

        final Person russel = new Person();
        russel.setUserName("russellwhyte");
        russel.setVersionIdentifier(ETAG_RUSSELL);

        final ModificationResponse<Person> result =
            service.updatePeople(russel).modifyingEntity().disableVersionIdentifier().execute(destination);

        assertThat(russel.getVersionIdentifier()).containsExactly(ETAG_RUSSELL); // origin entity remains unchanged
        assertThat(result.getUpdatedVersionIdentifier()).containsExactly(ETAG_RUSSELL_UPDATED);
        assertThat(result.getModifiedEntity().getVersionIdentifier()).containsExactly(ETAG_RUSSELL_UPDATED);

        verify(patchRequestedFor(urlEqualTo(GET_BY_KEY_REQUEST_URL)).withoutHeader(HttpHeaders.IF_MATCH));
    }

    @Test
    public void testUpdateWithPATCHMatchesAnyETagIfChosen()
    {
        stubFor(
            patch(urlEqualTo(GET_BY_KEY_REQUEST_URL)).willReturn(noContent().withHeader("ETag", ETAG_RUSSELL_UPDATED)));

        final Person russel = new Person();
        russel.setUserName("russellwhyte");
        russel.setVersionIdentifier(ETAG_RUSSELL);

        final ModificationResponse<Person> result =
            service.updatePeople(russel).modifyingEntity().matchAnyVersionIdentifier().execute(destination);

        assertThat(russel.getVersionIdentifier()).containsExactly(ETAG_RUSSELL); // origin entity remains unchanged
        assertThat(result.getUpdatedVersionIdentifier()).containsExactly(ETAG_RUSSELL_UPDATED);
        assertThat(result.getModifiedEntity().getVersionIdentifier()).containsExactly(ETAG_RUSSELL_UPDATED);

        verify(patchRequestedFor(urlEqualTo(GET_BY_KEY_REQUEST_URL)).withHeader(HttpHeaders.IF_MATCH, equalTo("*")));
    }

    @Test
    public void testUpdateWithPUTContainsETagByDefault()
    {
        stubFor(
            put(urlEqualTo(GET_BY_KEY_REQUEST_URL))
                .withHeader(HttpHeaders.IF_MATCH, equalTo(ETAG_RUSSELL))
                .willReturn(noContent().withHeader("ETag", ETAG_RUSSELL_UPDATED)));

        final Person russel = new Person();
        russel.setUserName("russellwhyte");
        russel.setVersionIdentifier(ETAG_RUSSELL);

        final ModificationResponse<Person> result = service.updatePeople(russel).replacingEntity().execute(destination);

        assertThat(russel.getVersionIdentifier()).containsExactly(ETAG_RUSSELL); // origin entity remains unchanged
        assertThat(result.getUpdatedVersionIdentifier()).containsExactly(ETAG_RUSSELL_UPDATED);
        assertThat(result.getModifiedEntity().getVersionIdentifier()).containsExactly(ETAG_RUSSELL_UPDATED);

        verify(
            putRequestedFor(urlEqualTo(GET_BY_KEY_REQUEST_URL))
                .withHeader(HttpHeaders.IF_MATCH, equalTo(ETAG_RUSSELL)));
    }

    @Test
    public void testUpdateWithPUTLacksETagIfDisabled()
    {
        stubFor(
            put(urlEqualTo(GET_BY_KEY_REQUEST_URL)).willReturn(noContent().withHeader("ETag", ETAG_RUSSELL_UPDATED)));

        final Person russel = new Person();
        russel.setUserName("russellwhyte");
        russel.setVersionIdentifier(ETAG_RUSSELL);

        final ModificationResponse<Person> result =
            service.updatePeople(russel).replacingEntity().disableVersionIdentifier().execute(destination);

        assertThat(russel.getVersionIdentifier()).containsExactly(ETAG_RUSSELL); // origin entity remains unchanged
        assertThat(result.getUpdatedVersionIdentifier()).containsExactly(ETAG_RUSSELL_UPDATED);
        assertThat(result.getModifiedEntity().getVersionIdentifier()).containsExactly(ETAG_RUSSELL_UPDATED);

        verify(putRequestedFor(urlEqualTo(GET_BY_KEY_REQUEST_URL)).withoutHeader(HttpHeaders.IF_MATCH));
    }

    @Test
    public void testUpdateWithPUTLacksMatchesAnyETagIfChosen()
    {
        stubFor(
            put(urlEqualTo(GET_BY_KEY_REQUEST_URL)).willReturn(noContent().withHeader("ETag", ETAG_RUSSELL_UPDATED)));

        final Person russel = new Person();
        russel.setUserName("russellwhyte");
        russel.setVersionIdentifier(ETAG_RUSSELL);

        final ModificationResponse<Person> result =
            service.updatePeople(russel).replacingEntity().matchAnyVersionIdentifier().execute(destination);

        assertThat(russel.getVersionIdentifier()).containsExactly(ETAG_RUSSELL); // origin entity remains unchanged
        assertThat(result.getUpdatedVersionIdentifier()).containsExactly(ETAG_RUSSELL_UPDATED);
        assertThat(result.getModifiedEntity().getVersionIdentifier()).containsExactly(ETAG_RUSSELL_UPDATED);

        verify(putRequestedFor(urlEqualTo(GET_BY_KEY_REQUEST_URL)).withHeader(HttpHeaders.IF_MATCH, equalTo("*")));
    }

    @Test
    public void testDeleteContainsETagByDefault()
    {
        stubFor(
            delete(urlEqualTo(GET_BY_KEY_REQUEST_URL))
                .withHeader(HttpHeaders.IF_MATCH, equalTo(ETAG_RUSSELL))
                .willReturn(noContent()));

        final Person russel = new Person();
        russel.setUserName("russellwhyte");
        russel.setVersionIdentifier(ETAG_RUSSELL);

        service.deletePeople(russel).execute(destination);

        verify(
            deleteRequestedFor(urlEqualTo(GET_BY_KEY_REQUEST_URL))
                .withHeader(HttpHeaders.IF_MATCH, equalTo(ETAG_RUSSELL)));
    }

    @Test
    public void testDeleteLacksEtagIfDisabled()
    {
        stubFor(delete(urlEqualTo(GET_BY_KEY_REQUEST_URL)).willReturn(noContent()));

        final Person russel = new Person();
        russel.setUserName("russellwhyte");
        russel.setVersionIdentifier(ETAG_RUSSELL);

        service.deletePeople(russel).disableVersionIdentifier().execute(destination);

        verify(deleteRequestedFor(urlEqualTo(GET_BY_KEY_REQUEST_URL)).withoutHeader(HttpHeaders.IF_MATCH));
    }

    @Test
    public void testDeleteMatchesAnyETagIfChosen()
    {
        stubFor(delete(urlEqualTo(GET_BY_KEY_REQUEST_URL)).willReturn(noContent()));

        final Person russel = new Person();
        russel.setUserName("russellwhyte");
        russel.setVersionIdentifier(ETAG_RUSSELL);

        service.deletePeople(russel).matchAnyVersionIdentifier().execute(destination);

        verify(deleteRequestedFor(urlEqualTo(GET_BY_KEY_REQUEST_URL)).withHeader(HttpHeaders.IF_MATCH, equalTo("*")));
    }

    @Test
    public void testNoETagReceived()
    {
        stubFor(get(urlEqualTo(GET_BY_KEY_REQUEST_URL)).willReturn(okJson(GET_BY_KEY_RESPONSE_BODY)));
        final Person person = service.getPeopleByKey("russellwhyte").execute(destination);

        assertThat(person.getVersionIdentifier()).isEmpty();
    }

    @Test
    public void testNoIfMatchIsSentOnEmptyETag()
    {
        stubFor(any(urlEqualTo(GET_BY_KEY_REQUEST_URL)).willReturn(noContent()));

        final Person person = new Person();
        person.setUserName("russellwhyte");

        service.updatePeople(person).execute(destination);
        service.deletePeople(person).execute(destination);

        verify(2, anyRequestedFor(urlEqualTo(GET_BY_KEY_REQUEST_URL)).withoutHeader(HttpHeaders.IF_MATCH));
    }

    @Test
    public void testOverwritingExistingETagOnEntity()
    {
        stubFor(
            any(urlEqualTo(GET_BY_KEY_REQUEST_URL))
                .withHeader(HttpHeaders.IF_MATCH, equalTo(ETAG_RUSSELL_UPDATED))
                .willReturn(noContent()));

        final Person person = new Person();
        person.setUserName("russellwhyte");
        person.setVersionIdentifier(ETAG_RUSSELL);

        service.deletePeople(person).withHeader(HttpHeaders.IF_MATCH, ETAG_RUSSELL_UPDATED).execute(destination);
        service
            .updatePeople(person)
            .modifyingEntity()
            .withHeader(HttpHeaders.IF_MATCH, ETAG_RUSSELL_UPDATED)
            .execute(destination);

        verify(
            2,
            anyRequestedFor(urlEqualTo(GET_BY_KEY_REQUEST_URL))
                .withHeader(HttpHeaders.IF_MATCH, equalTo(ETAG_RUSSELL_UPDATED)));
    }
}
