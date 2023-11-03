/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.net.URI;

import javax.annotation.Nonnull;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.cache.CacheManager;
import com.sap.cloud.sdk.cloudplatform.requestheader.DefaultRequestHeaderContainer;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderAccessor;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderContainer;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEntity;

import io.vavr.control.Try;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;

@RunWith( MockitoJUnitRunner.class )
public class OutboundServiceTest
{
    private MegacliteServiceBindingDestinationLoader sut;

    @Before
    public void setup()
    {
        CacheManager.invalidateAll();
        TenantAccessor.setTenantFacade(() -> Try.success(new DefaultTenant("subscriber-tenant-id", "")));

        final DwcConfiguration dwcConfig = new DwcConfiguration(URI.create("https://localhost/"), "provider-tenant-id");
        final MegacliteDestinationFactory destinationFactory = new MegacliteDestinationFactory(dwcConfig);

        sut = new MegacliteServiceBindingDestinationLoader();
        sut.setDwcConfig(dwcConfig);
        sut.setDestinationFactory(destinationFactory);
        sut.setConnectivityResolver(new MegacliteConnectivityProxyInformationResolver(destinationFactory));
    }

    @After
    public void reset()
    {
        TenantAccessor.setTenantFacade(null);
        RequestHeaderAccessor.setHeaderFacade(null);
        HttpClientAccessor.setHttpClientFactory(null);
    }

    @Test
    public void testGetDestinationForOutboundService()
    {
        final MegacliteServiceBinding destinationServiceBinding =
            MegacliteServiceBinding
                .forService(ServiceIdentifier.of("foo"))
                .subscriberConfiguration()
                // this is what you defined in the landscape configuration
                .name("destination")
                .version("v1")
                // this is the megaclite API version (currently always "v1")
                .megacliteVersion("v1")
                .build();

        final HttpDestination megacliteDestination =
            sut.getDestination(ServiceBindingDestinationOptions.forService(destinationServiceBinding).build());

        assertThat(megacliteDestination.getUri()).hasToString("https://localhost/v1/destination/v1/");
    }

    @Test
    public void testGetDestinationForOutboundServiceWithODataRequest()
        throws IOException
    {
        final MegacliteServiceBinding destinationServiceBinding =
            MegacliteServiceBinding
                .forService(ServiceIdentifier.of("foo"))
                .subscriberConfiguration()
                .name("destination")
                .version("v1")
                .megacliteVersion("v1")
                .build();

        final HttpDestination destination =
            sut.getDestination(ServiceBindingDestinationOptions.forService(destinationServiceBinding).build());

        final CloseableHttpClient httpClient = mockHttpClientForODataRequest(destination);

        // check whether URI and headers were sent correctly
        verify(httpClient).execute(argThat(request -> {
            assertThat(request.getHeaders("dwc-foo")).extracting("value").containsOnly("bar");
            assertThat(request.getURI()).hasToString("https://localhost/v1/destination/v1/path/EntityCollection");
            return true;
        }));
    }

    @SneakyThrows
    @Test
    public void testGetDestinationForOutboundDestinationWithODataRequest()
    {
        final MegacliteServiceBinding targetService =
            MegacliteServiceBinding
                .forService(ServiceIdentifier.of("foo"))
                .subscriberConfiguration()
                .name("my-service")
                .version("v1")
                .megacliteVersion("v1")
                .build();

        final String destinationName = "My-Destination";

        final HttpDestination destination =
            sut.getDestination(ServiceBindingDestinationOptions.forService(targetService).build());

        final HttpDestination targetServiceDestination =
            DefaultHttpDestination
                .fromDestination(destination)
                .header(new Header("Destination-Name", destinationName))
                .build();

        final CloseableHttpClient httpClient = mockHttpClientForODataRequest(targetServiceDestination);

        verify(httpClient).execute(argThat(request -> {
            assertThat(request.getHeaders("Destination-Name")).extracting("value").containsOnly(destinationName);
            assertThat(request.getHeaders("dwc-foo")).extracting("value").containsOnly("bar");
            assertThat(request.getURI()).hasToString("https://localhost/v1/my-service/v1/path/EntityCollection");
            return true;
        }));
    }

    @SneakyThrows
    @Test
    public void testGetDestinationForOutboundDestinationWithoutDestinationName()
    {
        final MegacliteServiceBinding targetService =
            MegacliteServiceBinding
                .forService(ServiceIdentifier.of("foo"))
                .subscriberConfiguration()
                .name("my-service")
                .version("v1")
                .megacliteVersion("v1")
                .build();

        final HttpDestination destination =
            sut.getDestination(ServiceBindingDestinationOptions.forService(targetService).build());

        final CloseableHttpClient httpClient = mockHttpClientForODataRequest(destination);

        verify(httpClient).execute(argThat(request -> {
            assertThat(request.getHeaders("Destination-Name")).isEmpty();
            assertThat(request.getHeaders("dwc-foo")).extracting("value").containsOnly("bar");
            assertThat(request.getURI()).hasToString("https://localhost/v1/my-service/v1/path/EntityCollection");
            return true;
        }));
    }

    @SneakyThrows
    private static CloseableHttpClient mockHttpClientForODataRequest( @Nonnull final HttpDestination destination )
    {
        // prepare HTTP Client factory to return a mocked HttpClient
        final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        HttpClientAccessor.setHttpClientFactory(dest -> new HttpClientWrapper(httpClient, dest));

        // mock headers
        final RequestHeaderContainer headers =
            DefaultRequestHeaderContainer.builder().withHeader("dwc-foo", "bar").build();

        // mock OData response
        final HttpResponse response = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"));
        response.setEntity(new StringEntity("{\"value\":[]}"));
        response.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
        doReturn(new MockedHttpResponse(response)).when(httpClient).execute(any());

        // mock incoming request
        RequestHeaderAccessor
            .executeWithHeaderContainer(
                headers,
                () -> new GetAllRequestBuilder<>("/path", TestEntity.class, "EntityCollection").execute(destination));

        return httpClient;
    }

    @Getter
    private static class TestEntity extends VdmEntity<TestEntity>
    {
        private final String entityCollection = "EntityCollection";
        private final String odataType = "com.test.TestEntity";
        private final Class<TestEntity> type = TestEntity.class;
    }

    @RequiredArgsConstructor
    private static class MockedHttpResponse implements CloseableHttpResponse
    {
        @Delegate
        private final HttpResponse wrappedResponse;

        @Override
        public void close()
        {

        }
    }
}
