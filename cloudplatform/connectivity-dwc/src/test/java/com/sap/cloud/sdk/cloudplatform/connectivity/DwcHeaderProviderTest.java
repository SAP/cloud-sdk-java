package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.cache.CacheManager;
import com.sap.cloud.sdk.cloudplatform.requestheader.DefaultRequestHeaderContainer;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderAccessor;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderContainer;

class DwcHeaderProviderTest
{
    @BeforeEach
    void setup()
    {
        CacheManager.invalidateAll();
    }

    @Test
    void testHeadersAreFiltered()
    {
        final RequestHeaderContainer headers =
            DefaultRequestHeaderContainer
                .builder()
                .withHeader("header1", "value1")
                .withHeader("dwc-header1", "dwc-value1-1", "dwc-value1-2")
                .withHeader("dwc-header2", "dwc-value2-1")
                .build();

        final DestinationRequestContext requestContext = mock(DestinationRequestContext.class);
        final List<Header> dwcHeaders =
            RequestHeaderAccessor
                .executeWithHeaderContainer(headers, () -> new DwcHeaderProvider().getHeaders(requestContext));

        assertThat(dwcHeaders).hasSize(3);
        assertThat(dwcHeaders)
            .containsExactlyInAnyOrder(
                new Header("dwc-header1", "dwc-value1-1"),
                new Header("dwc-header1", "dwc-value1-2"),
                new Header("dwc-header2", "dwc-value2-1"));
    }

    @Test
    void testCaseInsensitive()
    {
        final RequestHeaderContainer headers =
            DefaultRequestHeaderContainer
                .builder()
                .withHeader("header1", "value1")
                .withHeader("DWC-header1", "dwc-value1")
                .withHeader("dWc-header2", "dwc-value2")
                .build();

        final DestinationRequestContext requestContext = mock(DestinationRequestContext.class);
        final List<Header> dwcHeaders =
            RequestHeaderAccessor
                .executeWithHeaderContainer(headers, () -> new DwcHeaderProvider().getHeaders(requestContext));

        assertThat(dwcHeaders).hasSize(2);
        assertThat(dwcHeaders)
            .containsExactlyInAnyOrder(
                new Header("dwc-header1", "dwc-value1"),
                new Header("dwc-header2", "dwc-value2"));
    }
}
