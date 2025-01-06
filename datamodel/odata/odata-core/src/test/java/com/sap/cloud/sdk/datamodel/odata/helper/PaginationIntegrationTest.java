package com.sap.cloud.sdk.datamodel.odata.helper;

import static com.sap.cloud.sdk.datamodel.odata.helper.PaginationUnitTest.Customer;
import static com.sap.cloud.sdk.datamodel.odata.helper.PaginationUnitTest.newCustomerRead;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;

@Disabled( "Test runs against a v4 reference service on odata.org. Use it only to manually verify behaviour." )
class PaginationIntegrationTest
{
    private static final int PAGE_SIZE = 20;
    private static final ArgumentMatcher<URI> URI_WITHOUT_SKIP_TOKEN = uri -> !uri.getQuery().contains("$skiptoken");
    private static final ArgumentMatcher<URI> URL_WITH_SKIP_TOKEN = uri -> uri.getQuery().contains("$skiptoken");

    private final HttpDestination destination = DefaultHttpDestination.builder("https://services.odata.org").build();

    @Test
    void testGetAll()
    {
        final HttpDestination destination = spy(this.destination);

        final List<PaginationUnitTest.Customer> result =
            newCustomerRead().select(Customer.CUSTOMER_ID).withPreferredPageSize(PAGE_SIZE).executeRequest(destination);

        verify(destination, times(1)).getHeaders(argThat(URI_WITHOUT_SKIP_TOKEN));
        verify(destination, atLeastOnce()).getHeaders(argThat(URL_WITH_SKIP_TOKEN));

        assertThat(result)
            .isNotEmpty()
            .extracting(PaginationUnitTest.Customer::getCustomerId)
            .allMatch(Objects::nonNull);
    }

    @Test
    void testGetAllIteratingEntities()
    {
        final HttpDestination destination = spy(this.destination);

        final Iterable<Customer> result =
            newCustomerRead()
                .select(Customer.CUSTOMER_ID)
                .withPreferredPageSize(20)
                .iteratingEntities()
                .executeRequest(destination);

        verify(destination, times(1)).getHeaders(argThat(URI_WITHOUT_SKIP_TOKEN));
        verify(destination, never()).getHeaders(argThat(URL_WITH_SKIP_TOKEN));
        reset(destination);

        int countEntities = 0;
        for( final Customer entity : result ) {
            countEntities++;
            assertThat(entity).extracting(Customer::getCustomerId).isNotNull();
        }

        verify(destination, never()).getHeaders(argThat(URI_WITHOUT_SKIP_TOKEN));
        verify(destination, atLeastOnce()).getHeaders(argThat(URL_WITH_SKIP_TOKEN));
        assertThat(countEntities).isGreaterThan(0);
    }

    @Test
    void testGetAllStreamingEntities()
    {
        final HttpDestination destination = spy(this.destination);

        final Stream<Customer> result =
            newCustomerRead()
                .select(Customer.CUSTOMER_ID)
                .withPreferredPageSize(20)
                .streamingEntities()
                .executeRequest(destination);

        verify(destination, times(1)).getHeaders(argThat(URI_WITHOUT_SKIP_TOKEN));
        verify(destination, never()).getHeaders(argThat(URL_WITH_SKIP_TOKEN));
        reset(destination);

        final Stream<String> intermediateStream = result.map(Customer::getCustomerId).peek(Objects::requireNonNull);
        verify(destination, never()).getHeaders(any());

        final long countEntities = intermediateStream.count();
        assertThat(countEntities).isGreaterThan(0);
        verify(destination, never()).getHeaders(argThat(URI_WITHOUT_SKIP_TOKEN));
        verify(destination, atLeastOnce()).getHeaders(argThat(URL_WITH_SKIP_TOKEN));
    }

    @Test
    void testGetAllIteratingPages()
    {
        final HttpDestination destination = spy(this.destination);

        final Iterable<List<Customer>> result =
            newCustomerRead()
                .select(Customer.CUSTOMER_ID)
                .withPreferredPageSize(PAGE_SIZE)
                .iteratingPages()
                .executeRequest(destination);

        verify(destination, times(1)).getHeaders(argThat(URI_WITHOUT_SKIP_TOKEN));
        verify(destination, never()).getHeaders(argThat(URL_WITH_SKIP_TOKEN));
        reset(destination);

        int countPages = 0;
        int countEntities = 0;
        for( final List<Customer> entities : result ) {
            countPages++;
            countEntities += entities.size();
            assertThat(entities).extracting(Customer::getCustomerId).allMatch(Objects::nonNull);
        }

        verify(destination, never()).getHeaders(argThat(URI_WITHOUT_SKIP_TOKEN));
        verify(destination, times(countPages - 1)).getHeaders(argThat(URL_WITH_SKIP_TOKEN));
        assertThat(countEntities).isGreaterThan(0);
        assertThat(countPages).isGreaterThan(1);
    }
}
