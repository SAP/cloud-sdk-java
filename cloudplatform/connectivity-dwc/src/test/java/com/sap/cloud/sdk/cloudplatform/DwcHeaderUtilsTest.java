package com.sap.cloud.sdk.cloudplatform;

import static com.sap.cloud.sdk.cloudplatform.DwcHeaderUtils.DWC_SUBDOMAIN_HEADER;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.requestheader.DefaultRequestHeaderContainer;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderAccessor;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderContainer;

class DwcHeaderUtilsTest
{
    @Test
    void testSubdomainHeaderOrNullWithoutHeaderContainer()
    {
        // sanity
        assertThat(RequestHeaderAccessor.tryGetHeaderContainer()).isEmpty();

        assertThat(DwcHeaderUtils.getDwCSubdomainOrNull()).isNull();
    }

    @Test
    void testSubdomainHeaderOrNullWithoutHeader()
    {
        RequestHeaderAccessor.executeWithHeaderContainer(RequestHeaderContainer.EMPTY, () -> {
            // sanity
            assertThat(RequestHeaderAccessor.getHeaderContainer().getHeaderValues(DWC_SUBDOMAIN_HEADER)).isEmpty();

            assertThat(DwcHeaderUtils.getDwCSubdomainOrNull()).isNull();
        });
    }

    @Test
    void testSubdomainHeaderOrNull()
    {
        final String expectedValue = "subdomain";
        RequestHeaderAccessor.executeWithHeaderContainer(Map.of(DWC_SUBDOMAIN_HEADER, expectedValue), () -> {
            // sanity
            assertThat(RequestHeaderAccessor.getHeaderContainer().getHeaderValues(DWC_SUBDOMAIN_HEADER))
                .containsExactly(expectedValue);

            assertThat(DwcHeaderUtils.getDwCSubdomainOrNull()).isEqualTo(expectedValue);
        });
    }

    @Test
    void testSubdomainHeaderOrNullWithMultipleValues()
    {
        final String firstValue = "first";
        final String secondValue = "second";
        final RequestHeaderContainer requestHeaderContainer =
            DefaultRequestHeaderContainer
                .fromMultiValueMap(Map.of(DWC_SUBDOMAIN_HEADER, List.of(firstValue, secondValue)));
        RequestHeaderAccessor.executeWithHeaderContainer(requestHeaderContainer, () -> {
            // sanity
            assertThat(RequestHeaderAccessor.getHeaderContainer().getHeaderValues(DWC_SUBDOMAIN_HEADER))
                .containsExactly(firstValue, secondValue);

            assertThat(DwcHeaderUtils.getDwCSubdomainOrNull()).isEqualTo(firstValue);
        });
    }
}
