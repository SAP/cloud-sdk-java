package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConnectivityServiceHeaderProviderTest
{
    private static final URI requestUri = URI.create("http://example.com");
    private HttpDestination destination;
    private DestinationRequestContext context;
    private ConnectivityServiceHeaderProvider sut;

    @BeforeEach
    void setUp()
    {
        final ConnectivityService mock = mock(ConnectivityService.class);
        doReturn(new ArrayList<>())
            .when(mock)
            .getHeadersForOnPremiseSystem(false, PrincipalPropagationStrategy.RECOMMENDATION);

        sut = new ConnectivityServiceHeaderProvider(mock);
    }

    @AfterEach
    void tearDown()
    {
        destination = null;
        context = null;
        sut = null;
    }

    @Test
    void testNoProxyTypeIsSkipped()
    {
        destination = DefaultHttpDestination.builder(requestUri).build();
        context = new DestinationRequestContext(destination, requestUri);

        final List<Header> result = sut.getHeaders(context);

        assertThat(result).isEmpty();
    }

    @Test
    void testGetOnPremiseProxyHeaders()
    {
        destination =
            DefaultHttpDestination
                .builder(requestUri)
                .proxyType(ProxyType.ON_PREMISE)
                .cloudConnectorLocationId("testSCC")
                .build();

        context = new DestinationRequestContext(destination, requestUri);

        final Collection<Header> actualHeaders = sut.getHeaders(context);

        assertThat(actualHeaders).contains(new Header("SAP-Connectivity-SCC-Location_ID", "testSCC"));
    }

    @Test
    void testThrowsOnTenantMismatch()
    {
        destination =
            DefaultHttpDestination
                .builder(requestUri)
                .proxyType(ProxyType.ON_PREMISE)
                .property(DestinationProperty.TENANT_ID, "tenant1")
                .build();
        context = new DestinationRequestContext(destination, requestUri);

        // call this implicitly with provider tenant
        assertThatThrownBy(() -> sut.getHeaders(context)).isInstanceOf(IllegalStateException.class);
    }
}
