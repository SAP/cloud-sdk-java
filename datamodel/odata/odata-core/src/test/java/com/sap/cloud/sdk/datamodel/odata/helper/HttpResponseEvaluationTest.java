package com.sap.cloud.sdk.datamodel.odata.helper;

import static java.nio.charset.StandardCharsets.UTF_8;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;

import lombok.SneakyThrows;

public class HttpResponseEvaluationTest
{

    private static final Destination DESTINATION = DefaultHttpDestination.builder("foo").build();

    private HttpClient httpClient;
    private InputStreamEntity httpEntity;
    private BasicHttpResponse httpResponse;

    @SneakyThrows
    @BeforeEach
    void setup()
    {
        httpClient = mock(HttpClient.class);

        final String payload = "{\"d\": {\"results\": []}}";
        final ByteArrayInputStream payloadBytes = new ByteArrayInputStream(payload.getBytes(UTF_8));
        httpEntity = spy(new InputStreamEntity(payloadBytes, ContentType.APPLICATION_JSON));
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
        final ModificationResponse<TestVdmEntity> result =
            FluentHelperFactory
                .withServicePath("/path")
                .create(new TestVdmEntity())
                .withoutCsrfToken()
                .executeRequest(DESTINATION);

        verify(httpClient, times(1)).execute(any(HttpUriRequest.class));
        verify(httpResponse, times(1)).getEntity();
        verify(httpEntity, times(1)).writeTo(any(OutputStream.class));

        assertThat(result.getResponseStatusCode()).isEqualTo(200);
    }

    @SneakyThrows
    @Test
    void testUpdate()
    {
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

        assertThat(result.getResponseStatusCode()).isEqualTo(200);
    }

    @SneakyThrows
    @Test
    void testDelete()
    {
        final ModificationResponse<TestVdmEntity> result =
            FluentHelperFactory
                .withServicePath("/path")
                .delete(new TestVdmEntity())
                .withoutCsrfToken()
                .executeRequest(DESTINATION);

        verify(httpClient, times(1)).execute(any(HttpUriRequest.class));
        verify(httpResponse, times(1)).getEntity();
        verify(httpEntity, times(1)).writeTo(any(OutputStream.class));

        assertThat(result.getResponseStatusCode()).isEqualTo(200);
    }

    @SneakyThrows
    @Test
    void testReadAll()
    {
        final List<TestVdmEntity> result =
            FluentHelperFactory
                .withServicePath("/path")
                .read(TestVdmEntity.class, "TestEntitySet")
                .executeRequest(DESTINATION);

        assertThat(result).isNotNull();

        verify(httpClient, times(1)).execute(any(HttpUriRequest.class));
        verify(httpResponse, times(1)).getEntity();
        verify(httpEntity, times(1)).writeTo(any(OutputStream.class));
    }

    @SneakyThrows
    @Test
    void testReadByKey()
    {
        final TestVdmEntity result =
            FluentHelperFactory
                .withServicePath("/path")
                .readByKey(TestVdmEntity.class, "TestEntitySet", Map.of("IntegerValue", 42))
                .executeRequest(DESTINATION);

        assertThat(result).isNotNull();

        verify(httpClient, times(1)).execute(any(HttpUriRequest.class));
        verify(httpResponse, times(1)).getEntity();
        verify(httpEntity, times(1)).writeTo(any(OutputStream.class));
    }
}
