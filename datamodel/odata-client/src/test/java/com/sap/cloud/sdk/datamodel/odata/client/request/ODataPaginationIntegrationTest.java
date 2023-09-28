package com.sap.cloud.sdk.datamodel.odata.client.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.apache.http.client.HttpClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;

@Disabled( "Test runs against a reference service on odata.org. Use it only to manually verify behaviour." )
public class ODataPaginationIntegrationTest
{
    private static final Destination destination =
        DefaultHttpDestination.builder("https://services.odata.org/").build();

    private static final HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);

    @Test
    public void testCountOverPages()
    {
        final ODataRequestRead request =
            new ODataRequestRead("V4/Northwind/Northwind.svc", "Customers", "$count=true", ODataProtocol.V4);

        final ODataRequestResultGeneric initialResponse = request.execute(httpClient);
        final int initialCount = initialResponse.asListOfMaps().size();

        // assertion: entity count of initial response is less than inline-count
        final long overallCount = initialResponse.getInlineCount();
        assertThat(initialCount).isLessThan((int) overallCount);

        // iterate through pages and increment item count
        int countItems = 0;
        for( final List<Object> nextPage : initialResponse.iteratePages(Object.class) ) {
            countItems += nextPage.size();
        }

        // assertion: aggregated item count is equal to inline-count
        assertThat(countItems).isEqualTo(overallCount);
    }
}
