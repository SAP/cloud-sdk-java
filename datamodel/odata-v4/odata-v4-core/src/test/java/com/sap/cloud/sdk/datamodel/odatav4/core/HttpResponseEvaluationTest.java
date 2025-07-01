package com.sap.cloud.sdk.datamodel.odatav4.core;

import static java.nio.charset.StandardCharsets.UTF_8;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.apache.http.entity.ContentType.TEXT_PLAIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;

import lombok.SneakyThrows;

public class HttpResponseEvaluationTest
{
    private static final Destination DESTINATION = DefaultHttpDestination.builder("foo").build();

    private HttpClient httpClient;
    private BasicHttpResponse httpResponse;
    private InputStreamEntity httpEntity;
    private InputStream inputStream;

    @SneakyThrows
    void mockHttpResponse( final ContentType contentType, final String payload )
    {
        httpClient = mock(HttpClient.class);
        inputStream = spy(new ByteArrayInputStream(payload.getBytes(UTF_8)));
        httpEntity = spy(new InputStreamEntity(inputStream, contentType));
        httpResponse = spy(new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));
        httpResponse.setEntity(httpEntity);
        HttpClientAccessor.setHttpClientFactory(destination -> httpClient);
        when(httpClient.execute(any())).thenReturn(httpResponse);
    }

    @AfterEach
    void teardown()
    {
        HttpClientAccessor.setHttpClientFactory(null);
        HttpClientAccessor.setHttpClientCache(null);
    }

    @SneakyThrows
    @Test
    void testCreate()
    {
        mockHttpResponse(APPLICATION_JSON, "{}");

        final ModificationResponse<TestEntity> result =
            new CreateRequestBuilder<>("/path", new TestEntity(), "TestEntitySet")
                .withoutCsrfToken()
                .execute(DESTINATION);

        verify(httpClient, times(1)).execute(any(HttpUriRequest.class));
        verify(httpResponse, times(1)).getEntity();
        verify(httpEntity, times(1)).writeTo(any(OutputStream.class));
        verify(inputStream, times(1)).close();

        assertThat(result.getResponseStatusCode()).isEqualTo(200);
    }

    @SneakyThrows
    @Test
    void testUpdate()
    {
        mockHttpResponse(APPLICATION_JSON, "{}");

        final TestEntity testEntity = new TestEntity();
        testEntity.setId("id");

        final ModificationResponse<TestEntity> result =
            new UpdateRequestBuilder<>("/path", testEntity, "TestEntitySet").withoutCsrfToken().execute(DESTINATION);

        verify(httpClient, times(1)).execute(any(HttpUriRequest.class));
        verify(httpResponse, times(1)).getEntity();
        verify(httpEntity, times(1)).writeTo(any(OutputStream.class));
        verify(inputStream, times(1)).close();

        assertThat(result.getResponseStatusCode()).isEqualTo(200);
    }

    @SneakyThrows
    @Test
    void testDelete()
    {
        mockHttpResponse(APPLICATION_JSON, "{}");

        final ModificationResponse<TestEntity> result =
            new DeleteRequestBuilder<>("/path", new TestEntity(), "TestEntitySet")
                .withoutCsrfToken()
                .execute(DESTINATION);

        verify(httpClient, times(1)).execute(any(HttpUriRequest.class));
        verify(httpResponse, times(1)).getEntity();
        verify(httpEntity, times(1)).writeTo(any(OutputStream.class));
        verify(inputStream, times(1)).close();

        assertThat(result.getResponseStatusCode()).isEqualTo(200);
    }

    @SneakyThrows
    @Test
    void testReadAll()
    {
        mockHttpResponse(APPLICATION_JSON, "{\"value\": []}");

        final List<TestEntity> result =
            new GetAllRequestBuilder<>("/path", TestEntity.class, "TestEntitySet").execute(DESTINATION);

        assertThat(result).isNotNull();

        verify(httpClient, times(1)).execute(any(HttpUriRequest.class));
        verify(httpResponse, times(1)).getEntity();
        verify(httpEntity, times(1)).writeTo(any(OutputStream.class));
        verify(inputStream, times(1)).close();
    }

    @SneakyThrows
    @Test
    void testReadByKey()
    {
        mockHttpResponse(APPLICATION_JSON, "{}");

        final TestEntity result =
            new GetByKeyRequestBuilder<>("/path", TestEntity.class, Map.of("id", "foo"), "TestEntitySet")
                .execute(DESTINATION);

        assertThat(result).isNotNull();

        verify(httpClient, times(1)).execute(any(HttpUriRequest.class));
        verify(httpResponse, times(1)).getEntity();
        verify(httpEntity, times(1)).writeTo(any(OutputStream.class));
        verify(inputStream, times(1)).close();
    }

    @SneakyThrows
    @Test
    void testReadCount()
    {
        mockHttpResponse(TEXT_PLAIN, "42");

        final Long result = new CountRequestBuilder<>("/path", TestEntity.class, "TestEntitySet").execute(DESTINATION);

        assertThat(result).isNotNull();

        verify(httpClient, times(1)).execute(any(HttpUriRequest.class));
        verify(httpResponse, times(1)).getEntity();
        verify(httpEntity, times(1)).writeTo(any(OutputStream.class));
        verify(inputStream, times(1)).close();
    }

    @SneakyThrows
    @Test
    void testAction()
    {
        mockHttpResponse(APPLICATION_JSON, "{}");

        final ActionResponseSingle<TestEntity> result =
            new SingleValueActionRequestBuilder<>("/path", "ActionName", TestEntity.class)
                .withoutCsrfToken()
                .execute(DESTINATION);

        assertThat(result).isNotNull();

        verify(httpClient, times(1)).execute(any(HttpUriRequest.class));
        verify(httpResponse, times(1)).getEntity();
        verify(httpEntity, times(1)).writeTo(any(OutputStream.class));
        verify(inputStream, times(1)).close();
    }

    @SneakyThrows
    @Test
    void testFunction()
    {
        mockHttpResponse(APPLICATION_JSON, "{\"value\":[]}");

        final List<TestEntity> result =
            new CollectionValueFunctionRequestBuilder<>("/path", "FunctionName", TestEntity.class).execute(DESTINATION);

        assertThat(result).isNotNull();

        verify(httpClient, times(1)).execute(any(HttpUriRequest.class));
        verify(httpResponse, times(1)).getEntity();
        verify(httpEntity, times(1)).writeTo(any(OutputStream.class));
        verify(inputStream, times(1)).close();
    }

    // count function action
}
