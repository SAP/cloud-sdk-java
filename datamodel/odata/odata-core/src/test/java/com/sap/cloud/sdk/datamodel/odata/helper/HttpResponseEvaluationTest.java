package com.sap.cloud.sdk.datamodel.odata.helper;

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
        mockHttpResponse(APPLICATION_JSON, "{\"d\": {}}");

        final ModificationResponse<TestVdmEntity> result =
            FluentHelperFactory
                .withServicePath("/path")
                .create(new TestVdmEntity())
                .withoutCsrfToken()
                .executeRequest(DESTINATION);

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
        mockHttpResponse(APPLICATION_JSON, "{\"d\": {}}");

        final TestVdmEntity testEntity = new TestVdmEntity();
        testEntity.setIntegerValue(42);

        final ModificationResponse<TestVdmEntity> result =
            FluentHelperFactory
                .withServicePath("/path")
                .update(testEntity)
                .withoutCsrfToken()
                .executeRequest(DESTINATION);

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
        mockHttpResponse(APPLICATION_JSON, "{\"d\": {}}");

        final ModificationResponse<TestVdmEntity> result =
            FluentHelperFactory
                .withServicePath("/path")
                .delete(new TestVdmEntity())
                .withoutCsrfToken()
                .executeRequest(DESTINATION);

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
        mockHttpResponse(APPLICATION_JSON, "{\"d\": {\"results\": []}}");

        final List<TestVdmEntity> result =
            FluentHelperFactory
                .withServicePath("/path")
                .read(TestVdmEntity.class, "TestEntitySet")
                .executeRequest(DESTINATION);

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
        mockHttpResponse(APPLICATION_JSON, "{\"d\":{}}");

        final TestVdmEntity result =
            FluentHelperFactory
                .withServicePath("/path")
                .readByKey(TestVdmEntity.class, "TestEntitySet", Map.of("IntegerValue", 42))
                .executeRequest(DESTINATION);

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
        mockHttpResponse(APPLICATION_JSON, "{\"d\": {\"results\": []}}");

        final List<String> result =
            FluentHelperFactory
                .withServicePath("/path")
                .functionMultipleGet(Map.of("para", "meter"), "functionName", String.class)
                .executeRequest(DESTINATION);

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

        final long result =
            FluentHelperFactory
                .withServicePath("/path")
                .read(TestVdmEntity.class, "TestEntitySet")
                .count()
                .executeRequest(DESTINATION);

        verify(httpClient, times(1)).execute(any(HttpUriRequest.class));
        verify(httpResponse, times(1)).getEntity();
        verify(httpEntity, times(1)).writeTo(any(OutputStream.class));
        verify(inputStream, times(1)).close();
    }
}
