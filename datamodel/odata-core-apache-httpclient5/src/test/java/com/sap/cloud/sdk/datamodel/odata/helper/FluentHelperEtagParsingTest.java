package com.sap.cloud.sdk.datamodel.odata.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.annotations.JsonAdapter;
import com.sap.cloud.sdk.cloudplatform.connectivity.ApacheHttpClient5Accessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.ApacheHttpClient5CacheBuilder;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestinationProperties;

import lombok.Getter;
import lombok.SneakyThrows;

class FluentHelperEtagParsingTest
{
    private static final String SERVICE_PATH = "/service";
    private static final String ENTITY_COLLECTION = "/EntityCollection";
    private static final HttpDestinationProperties DESTINATION = DefaultHttpDestination.builder("http://1").build();
    private static final String FUNCTION_NAME = "FUNCTION_NAME";
    private static final String ETAG_HEAD = "foo";
    private static final String ETAG_BODY = "bar";
    private static final FluentHelperFactory FACTORY = FluentHelperFactory.withServicePath(SERVICE_PATH);
    private static final Map<String, String> HEADERS_NONE = Collections.emptyMap();
    private static final Map<String, String> HEADERS_WITH_ETAG = Collections.singletonMap("Etag", ETAG_HEAD);

    private HttpClient httpClient;

    @BeforeEach
    void setupConnectivity()
    {
        httpClient = mock(HttpClient.class);
        ApacheHttpClient5Accessor
            .setHttpClientCache(new ApacheHttpClient5CacheBuilder().durationInMilliseconds(0).build());
        ApacheHttpClient5Accessor.setHttpClientFactory(dest -> {
            assertThat(dest).isSameAs(DESTINATION);
            return httpClient;
        });
    }

    @AfterEach
    void teardownConnectivity()
    {
        ApacheHttpClient5Accessor
            .setHttpClientCache(new ApacheHttpClient5CacheBuilder().duration(Duration.ofMinutes(5)).build());
        ApacheHttpClient5Accessor.setHttpClientFactory(null);
    }

    /**
     * ETag via HTTP header.
     */
    @SneakyThrows
    @Test
    void testParseNoEtagGetByKey()
    {
        when(httpClient.executeOpen(isNull(), any(), isNull())).thenReturn(response("{d:{}}", HEADERS_NONE));

        final TestEntity entity =
            FACTORY
                .readByKey(TestEntity.class, ENTITY_COLLECTION, Collections.singletonMap("key", "val"))
                .executeRequest(DESTINATION);

        assertThat(entity).isNotNull();
        assertThat(entity.getVersionIdentifier()).isEmpty();
    }

    @SneakyThrows
    @Test
    void testParseNoEtagFunctionImport()
    {
        when(httpClient.executeOpen(isNull(), any(), isNull())).thenReturn(response(String.format("{d:{%s:{}}}", FUNCTION_NAME), HEADERS_NONE));

        // GET
        TestEntity entity =
            FACTORY
                .functionSingleGet(Collections.singletonMap("key", "val"), FUNCTION_NAME, TestEntity.class)
                .executeRequest(DESTINATION);

        assertThat(entity).isNotNull();
        assertThat(entity.getVersionIdentifier()).isEmpty();

        // POST
        entity =
            FACTORY
                .functionSinglePost(Collections.singletonMap("key", "val"), FUNCTION_NAME, TestEntity.class)
                .executeRequest(DESTINATION);

        assertThat(entity).isNotNull();
        assertThat(entity.getVersionIdentifier()).isEmpty();
    }

    @SneakyThrows
    @Test
    void testParseNoEtagUpdate()
    {
        when(httpClient.executeOpen(isNull(), any(), isNull())).thenReturn(response("{d:{}}", HEADERS_NONE));

        final TestEntity requestEntity = new TestEntity();

        final ModificationResponse<TestEntity> result =
            FACTORY.update(ENTITY_COLLECTION, requestEntity).executeRequest(DESTINATION);

        assertThat(result).isNotNull();
        assertThat(result.getModifiedEntity()).isNotNull();
        assertThat(result.getResponseEntity()).isNotNull().isNotEmpty();
        assertThat(result.getRequestEntity()).isNotNull().isSameAs(requestEntity);

        assertThat(result.getModifiedEntity().getVersionIdentifier()).isEmpty();
        assertThat(result.getResponseEntity().get().getVersionIdentifier()).isEmpty();
        assertThat(result.getRequestEntity().getVersionIdentifier()).isEmpty();
    }

    @SneakyThrows
    @Test
    void testParseHeaderEtagGetByKey()
    {
        when(httpClient.executeOpen(isNull(), any(), isNull())).thenReturn(response("{d:{}}", HEADERS_WITH_ETAG));

        final TestEntity entity =
            FACTORY
                .readByKey(TestEntity.class, ENTITY_COLLECTION, Collections.singletonMap("key", "val"))
                .executeRequest(DESTINATION);

        assertThat(entity).isNotNull();
        assertThat(entity.getVersionIdentifier()).containsExactly(ETAG_HEAD);
    }

    @SneakyThrows
    @Test
    void testParseHeaderEtagFunctionImport()
    {
        when(httpClient.executeOpen(isNull(), any(), isNull()))
            .thenReturn(response(String.format("{d:{%s:{}}}", FUNCTION_NAME), HEADERS_WITH_ETAG));

        // GET
        TestEntity entity =
            FACTORY
                .functionSingleGet(Collections.singletonMap("key", "val"), FUNCTION_NAME, TestEntity.class)
                .executeRequest(DESTINATION);

        assertThat(entity).isNotNull();
        assertThat(entity.getVersionIdentifier()).containsExactly(ETAG_HEAD);

        // POST
        entity =
            FACTORY
                .functionSinglePost(Collections.singletonMap("key", "val"), FUNCTION_NAME, TestEntity.class)
                .executeRequest(DESTINATION);

        assertThat(entity).isNotNull();
        assertThat(entity.getVersionIdentifier()).containsExactly(ETAG_HEAD);
    }

    @SneakyThrows
    @Test
    void testParseHeaderEtagUpdate()
    {
        when(httpClient.executeOpen(isNull(), any(), isNull())).thenReturn(response("{d:{}}", HEADERS_WITH_ETAG));

        final TestEntity requestEntity = new TestEntity();

        final ModificationResponse<TestEntity> result =
            FACTORY.update(ENTITY_COLLECTION, requestEntity).executeRequest(DESTINATION);

        assertThat(result).isNotNull();
        assertThat(result.getModifiedEntity()).isNotNull();
        assertThat(result.getResponseEntity()).isNotNull().isNotEmpty();
        assertThat(result.getRequestEntity()).isNotNull().isSameAs(requestEntity);

        assertThat(result.getModifiedEntity().getVersionIdentifier()).containsExactly(ETAG_HEAD);
        assertThat(result.getResponseEntity().get().getVersionIdentifier()).containsExactly(ETAG_HEAD);
        assertThat(result.getRequestEntity().getVersionIdentifier()).isEmpty();
    }

    /**
     * ETag via HTTP payload.
     */
    @SneakyThrows
    @Test
    void testParsePayloadEtagGetAll()
    {
        final String payload = String.format("{d:{results:[{__metadata:{etag:\"%s\"}}]}}", ETAG_BODY);
        when(httpClient.executeOpen(isNull(), any(), isNull())).thenReturn(response(payload, HEADERS_NONE));

        final List<TestEntity> entities = FACTORY.read(TestEntity.class, ENTITY_COLLECTION).executeRequest(DESTINATION);

        assertThat(entities).isNotNull().hasSize(1);
        assertThat(entities.get(0).getVersionIdentifier()).containsExactly(ETAG_BODY);
    }

    @SneakyThrows
    @Test
    void testParsePayloadEtagGetByKey()
    {
        when(httpClient.executeOpen(isNull(), any(), isNull()))
            .thenReturn(response(String.format("{d:{__metadata:{etag:\"%s\"}}}", ETAG_BODY), HEADERS_NONE));

        final TestEntity entity =
            FACTORY
                .readByKey(TestEntity.class, ENTITY_COLLECTION, Collections.singletonMap("key", "val"))
                .executeRequest(DESTINATION);

        assertThat(entity).isNotNull();
        assertThat(entity.getVersionIdentifier()).containsExactly(ETAG_BODY);
    }

    @SneakyThrows
    @Test
    void testParsePayloadEtagFunctionImport()
    {
        when(httpClient.executeOpen(isNull(), any(), isNull()))
            .thenReturn(
                response(String.format("{d:{%s:{__metadata:{etag:\"%s\"}}}}", FUNCTION_NAME, ETAG_BODY), HEADERS_NONE));

        // GET
        TestEntity entity =
            FACTORY
                .functionSingleGet(Collections.singletonMap("key", "val"), FUNCTION_NAME, TestEntity.class)
                .executeRequest(DESTINATION);

        assertThat(entity).isNotNull();
        assertThat(entity.getVersionIdentifier()).containsExactly(ETAG_BODY);

        // POST
        entity =
            FACTORY
                .functionSinglePost(Collections.singletonMap("key", "val"), FUNCTION_NAME, TestEntity.class)
                .executeRequest(DESTINATION);

        assertThat(entity).isNotNull();
        assertThat(entity.getVersionIdentifier()).containsExactly(ETAG_BODY);
    }

    @SneakyThrows
    @Test
    void testParsePayloadEtagUpdate()
    {
        when(httpClient.executeOpen(isNull(), any(), isNull()))
            .thenReturn(response(String.format("{d:{__metadata:{etag:\"%s\"}}}", ETAG_BODY), HEADERS_NONE));

        final TestEntity requestEntity = new TestEntity();

        final ModificationResponse<TestEntity> result =
            FACTORY.update(ENTITY_COLLECTION, requestEntity).executeRequest(DESTINATION);

        assertThat(result).isNotNull();
        assertThat(result.getModifiedEntity()).isNotNull();
        assertThat(result.getResponseEntity()).isNotNull().isNotEmpty();
        assertThat(result.getRequestEntity()).isNotNull().isSameAs(requestEntity);

        assertThat(result.getModifiedEntity().getVersionIdentifier()).containsExactly(ETAG_BODY);
        assertThat(result.getResponseEntity().get().getVersionIdentifier()).containsExactly(ETAG_BODY);
        assertThat(result.getRequestEntity().getVersionIdentifier()).isEmpty();
    }

    /**
     * ETag via HTTP header + HTTP payload.
     */
    @SneakyThrows
    @Test
    void testParseHeaderAndPayloadEtagGetByKey()
    {
        when(httpClient.executeOpen(isNull(), any(), isNull()))
            .thenReturn(response(String.format("{d:{__metadata:{etag:\"%s\"}}}", ETAG_BODY), HEADERS_WITH_ETAG));

        final TestEntity entity =
            FACTORY
                .readByKey(TestEntity.class, ENTITY_COLLECTION, Collections.singletonMap("key", "val"))
                .executeRequest(DESTINATION);

        assertThat(entity).isNotNull();
        assertThat(entity.getVersionIdentifier()).containsExactly(ETAG_HEAD);
    }

    @SneakyThrows
    @Test
    void testParseHeaderAndPayloadEtagFunctionImport()
    {
        when(httpClient.executeOpen(isNull(), any(), isNull()))
            .thenReturn(
                response(
                    String.format("{d:{%s:{__metadata:{etag:\"%s\"}}}}", FUNCTION_NAME, ETAG_BODY),
                    HEADERS_WITH_ETAG));

        // GET
        TestEntity entity =
            FACTORY
                .functionSingleGet(Collections.singletonMap("key", "val"), FUNCTION_NAME, TestEntity.class)
                .executeRequest(DESTINATION);

        assertThat(entity).isNotNull();
        assertThat(entity.getVersionIdentifier()).containsExactly(ETAG_HEAD);

        // POST
        entity =
            FACTORY
                .functionSinglePost(Collections.singletonMap("key", "val"), FUNCTION_NAME, TestEntity.class)
                .executeRequest(DESTINATION);

        assertThat(entity).isNotNull();
        assertThat(entity.getVersionIdentifier()).containsExactly(ETAG_HEAD);
    }

    @SneakyThrows
    @Test
    void testParseHeaderAndPayloadEtagUpdate()
    {
        when(httpClient.executeOpen(isNull(), any(), isNull()))
            .thenReturn(response(String.format("{d:{__metadata:{etag:\"%s\"}}}", ETAG_BODY), HEADERS_WITH_ETAG));

        final TestEntity requestEntity = new TestEntity();

        final ModificationResponse<TestEntity> result =
            FACTORY.update(ENTITY_COLLECTION, requestEntity).executeRequest(DESTINATION);

        assertThat(result).isNotNull();
        assertThat(result.getModifiedEntity()).isNotNull();
        assertThat(result.getResponseEntity()).isNotNull().isNotEmpty();
        assertThat(result.getRequestEntity()).isNotNull().isSameAs(requestEntity);

        assertThat(result.getModifiedEntity().getVersionIdentifier()).containsExactly(ETAG_HEAD);
        assertThat(result.getResponseEntity().get().getVersionIdentifier()).containsExactly(ETAG_HEAD);
        assertThat(result.getRequestEntity().getVersionIdentifier()).isEmpty();
    }

    /**
     * HELPER METHODS.
     */
    @JsonAdapter( com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory.class )
    public static class TestEntity extends VdmEntity<TestEntity>
    {
        @Getter
        private final String entityCollection = ENTITY_COLLECTION;
        @Getter
        private final Class<TestEntity> type = TestEntity.class;
    }

    @Nonnull
    static ClassicHttpResponse response( @Nonnull final String payload, @Nonnull final Map<String, String> headers )
    {
        final BasicClassicHttpResponse result = new BasicClassicHttpResponse(200, "Ok");
        result.setEntity(new StringEntity(payload, StandardCharsets.UTF_8));
        result.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
        headers.forEach(result::setHeader);
        return result;
    }
}
