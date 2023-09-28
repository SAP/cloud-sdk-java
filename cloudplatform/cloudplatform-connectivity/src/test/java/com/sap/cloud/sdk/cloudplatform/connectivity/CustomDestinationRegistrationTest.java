package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Before;
import org.junit.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;

public class CustomDestinationRegistrationTest
{
    private static final HttpDestination httpDestination =
        DefaultHttpDestination.builder("foo").name("httpDestination").build();

    private static final RfcDestination rfcDestination =
        DefaultDestination.builder().name("rfcDestination").build().asRfc();

    @Before
    public void resetDestinationAccessor()
    {
        DestinationAccessor.setLoader(null);
    }

    @Test
    public void testBasicUseCase()
    {
        final DefaultDestinationLoader loader =
            new DefaultDestinationLoader().registerDestination(httpDestination).registerDestination(rfcDestination);

        DestinationAccessor.appendDestinationLoader(loader);

        final HttpDestination actualHttpDestination = DestinationAccessor.getDestination("httpDestination").asHttp();
        final RfcDestination actualRfcDestination = DestinationAccessor.getDestination("rfcDestination").asRfc();

        assertThat(actualHttpDestination).isSameAs(httpDestination);
        assertThat(actualRfcDestination).isSameAs(rfcDestination);
    }

    @Test
    public void testGetNonExistingDestination()
    {
        assertThatThrownBy(() -> DestinationAccessor.getDestination("foo"))
            .isInstanceOf(DestinationNotFoundException.class);
    }

    @Test
    public void testDestinationsWithSameName()
    {
        final HttpDestination httpDestination1 = httpDestination;
        final HttpDestination httpDestination2 = DefaultHttpDestination.fromProperties(httpDestination1).build();

        final DefaultDestinationLoader loader =
            new DefaultDestinationLoader().registerDestination(httpDestination1).registerDestination(httpDestination2);

        DestinationAccessor.appendDestinationLoader(loader);

        assertThat(DestinationAccessor.getDestination("httpDestination")).isSameAs(httpDestination2);
    }

    @Test
    public void testDestinationsWithoutName()
    {
        final DefaultDestinationLoader loader = new DefaultDestinationLoader();
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> loader.registerDestination(DefaultHttpDestination.builder("").build()));
    }
}
