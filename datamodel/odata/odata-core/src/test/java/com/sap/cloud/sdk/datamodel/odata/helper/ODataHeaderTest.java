package com.sap.cloud.sdk.datamodel.odata.helper;

import static com.github.tomakehurst.wiremock.client.WireMock.delete;
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
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;

@WireMockTest
class ODataHeaderTest
{
    private static final String DEFAULT_SERVICE_PATH = new TestVdmEntity().getDefaultServicePath();
    private static final String ENTITY_COLLECTION_PATH = new TestVdmEntity().getEntityCollection();
    private static final UrlPathPattern GET_ALL = urlPathEqualTo(DEFAULT_SERVICE_PATH + '/' + ENTITY_COLLECTION_PATH);
    private static final UrlPathPattern CREATE = urlPathEqualTo(DEFAULT_SERVICE_PATH + '/' + ENTITY_COLLECTION_PATH);
    private static final UrlPathPattern UPDATE =
        urlPathEqualTo(DEFAULT_SERVICE_PATH + '/' + ENTITY_COLLECTION_PATH + "(123)");
    private static final UrlPathPattern DELETE = UPDATE;
    private static final UrlPathPattern CSRF = urlPathEqualTo(DEFAULT_SERVICE_PATH);

    private DefaultHttpDestination destination;
    private TestVdmEntity entity;

    @BeforeEach
    void setup( @Nonnull final WireMockRuntimeInfo wm )
    {
        destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
        stubFor(get(GET_ALL).willReturn(okJson("{\"d\":{\"results\":[]}}")));
        stubFor(patch(UPDATE).willReturn(okJson("{\"d\":{}")));
        stubFor(post(CREATE).willReturn(okJson("{\"d\":{}")));
        stubFor(delete(DELETE).willReturn(ok()));
        stubFor(
            head(CSRF).withHeader("x-csrf-token", equalTo("fetch")).willReturn(ok().withHeader("x-csrf-token", "abc")));

        entity = TestVdmEntity.builder().integerValue(123).build();
    }

    // Test for implicitly missing CSRF header token and ETag header when GETTING an entity
    @Test
    void testNonExistingHeadersForGetDefault()
    {
        new TestEntityReadFluentHelper().executeRequest(destination);
        verify(0, headRequestedFor(CSRF));
        verify(getRequestedFor(GET_ALL).withoutHeader("If-Match").withoutHeader("x-csrf-token"));
    }

    // Test for CSRF token header and implicitly without ETag header when UPDATING an entity
    @Test
    void testUpdateDefaultHeader()
    {
        new TestEntityUpdateFluentHelper(entity).executeRequest(destination);
        verify(headRequestedFor(CSRF).withHeader("x-csrf-token", equalTo("fetch")));
        verify(patchRequestedFor(UPDATE).withHeader("x-csrf-token", equalTo("abc")).withoutHeader("If-Match"));
    }

    // Test for CSRF token header and explicit ETag header when UPDATING an entity
    @Test
    void testUpdateSpecificVersionVersionHeader()
    {
        entity.setVersionIdentifier("ver");
        new TestEntityUpdateFluentHelper(entity).executeRequest(destination);
        verify(headRequestedFor(CSRF).withHeader("x-csrf-token", equalTo("fetch")));

        verify(
            patchRequestedFor(UPDATE)
                .withHeader("x-csrf-token", equalTo("abc"))
                .withHeader("If-Match", equalTo("ver")));
    }

    // Test for CSRF token header and explicit ETag-wildcard header when UPDATING an entity
    @Test
    void testUpdateWildcardVersionVersionHeader()
    {
        new TestEntityUpdateFluentHelper(entity).matchAnyVersionIdentifier().executeRequest(destination);
        verify(headRequestedFor(CSRF).withHeader("x-csrf-token", equalTo("fetch")));

        verify(
            patchRequestedFor(UPDATE).withHeader("x-csrf-token", equalTo("abc")).withHeader("If-Match", equalTo("*")));
    }

    // Test for CSRF token header, explicitly without ETag header when UPDATING an entity
    @Test
    void testUpdateWithoutVersionHeader()
    {
        new TestEntityUpdateFluentHelper(entity).disableVersionIdentifier().executeRequest(destination);
        verify(headRequestedFor(CSRF).withHeader("x-csrf-token", equalTo("fetch")));
        verify(patchRequestedFor(UPDATE).withHeader("x-csrf-token", equalTo("abc")).withoutHeader("If-Match"));
    }

    // Test for custom header, implicitly without CSRF token header or ETag header when GETTING an entity
    @Test
    void testGetCustomHeaderHeader()
    {
        new TestEntityReadFluentHelper()
            .withHeader("Authentication", "yes")
            .withHeader("Cookie", "tasty")
            .executeRequest(destination);
        verify(0, headRequestedFor(CSRF));

        verify(
            getRequestedFor(GET_ALL)
                .withHeader("Authentication", equalTo("yes"))
                .withHeader("Cookie", equalTo("tasty"))
                .withoutHeader("x-csrf-token")
                .withoutHeader("If-Match"));
    }

    // Test for CSRF token header, explicitly with custom headers when UPDATING an entity
    @Test
    void testUpdateCustomHeader()
    {
        new TestEntityUpdateFluentHelper(entity).withHeader("Authentication", "yes").executeRequest(destination);

        verify(
            headRequestedFor(CSRF)
                .withHeader("Authentication", equalTo("yes"))
                .withHeader("x-csrf-token", equalTo("fetch")));

        verify(
            patchRequestedFor(UPDATE)
                .withHeader("Authentication", equalTo("yes"))
                .withHeader("x-csrf-token", equalTo("abc")));
    }

    @Test
    void testUpdateWithoutCsrfTokenIfSkipped()
    {
        new TestEntityUpdateFluentHelper(entity).withoutCsrfToken().executeRequest(destination);

        verify(0, headRequestedFor(CSRF).withHeader("x-csrf-token", equalTo("fetch")));
    }

    @Test
    void testCreateWithoutCsrfTokenIfSkipped()
    {
        new TestEntityCreateFluentHelper(entity).withoutCsrfToken().executeRequest(destination);

        verify(0, headRequestedFor(CSRF).withHeader("x-csrf-token", equalTo("fetch")));
    }

    @Test
    void testDeleteWithoutCsrfTokenIfSkipped()
    {
        new TestEntityDeleteFluentHelper(entity).withoutCsrfToken().executeRequest(destination);

        verify(0, headRequestedFor(CSRF).withHeader("x-csrf-token", equalTo("fetch")));
    }

    // fluent helpers

    private static class TestEntityDeleteFluentHelper
        extends
        FluentHelperDelete<TestEntityDeleteFluentHelper, TestVdmEntity>
    {
        private final TestVdmEntity entity;

        @SuppressWarnings( "deprecation" )
        private TestEntityDeleteFluentHelper( @Nonnull final TestVdmEntity entity )
        {
            super(DEFAULT_SERVICE_PATH, entity.getEntityCollection());
            this.entity = entity;
        }

        @Override
        protected TestVdmEntity getEntity()
        {
            return entity;
        }
    }

    private static class TestEntityUpdateFluentHelper
        extends
        FluentHelperUpdate<TestEntityUpdateFluentHelper, TestVdmEntity>
    {
        private final TestVdmEntity entity;

        @SuppressWarnings( "deprecation" )
        private TestEntityUpdateFluentHelper( @Nonnull final TestVdmEntity entity )
        {
            super(DEFAULT_SERVICE_PATH, entity.getEntityCollection());
            this.entity = entity;
        }

        @Override
        protected TestVdmEntity getEntity()
        {
            return entity;
        }
    }

    private static class TestEntityCreateFluentHelper
        extends
        FluentHelperCreate<TestEntityCreateFluentHelper, TestVdmEntity>
    {
        private final TestVdmEntity entity;

        @SuppressWarnings( "deprecation" )
        private TestEntityCreateFluentHelper( @Nonnull final TestVdmEntity entity )
        {
            super(DEFAULT_SERVICE_PATH, entity.getEntityCollection());
            this.entity = entity;
        }

        @Override
        protected TestVdmEntity getEntity()
        {
            return entity;
        }
    }

    private static class TestEntityReadFluentHelper
        extends
        FluentHelperRead<TestEntityReadFluentHelper, TestVdmEntity, Object>
    {
        @SuppressWarnings( "deprecation" )
        private TestEntityReadFluentHelper()
        {
            super(DEFAULT_SERVICE_PATH, TestVdmEntity.builder().build().getEntityCollection());
        }

        @Nonnull
        @Override
        protected Class<TestVdmEntity> getEntityClass()
        {
            return TestVdmEntity.class;
        }
    }
}
