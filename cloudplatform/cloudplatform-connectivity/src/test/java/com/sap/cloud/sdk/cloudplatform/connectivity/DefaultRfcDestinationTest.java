/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import org.assertj.core.api.Assertions;
import org.assertj.vavr.api.VavrAssertions;
import org.junit.Test;

public class DefaultRfcDestinationTest
{
    private static final String SOME_NAME = "Some Destination Name";
    private static final Object testObject = new Object();
    private static final DestinationProperties testProperties =
        DefaultDestination
            .builder()
            .name(SOME_NAME)
            .property("someKey", testObject)
            .property(DestinationProperty.TYPE, DestinationType.RFC)
            .build();

    @Test
    public void testGetDelegation()
    {
        final DestinationProperties rfcDestination = DefaultRfcDestination.fromProperties(testProperties);

        VavrAssertions.assertThat(rfcDestination.get(DestinationProperty.NAME)).contains(SOME_NAME);
        VavrAssertions.assertThat(rfcDestination.get("someKey")).contains(testObject);
    }

    @Test
    public void testNameIsRequired()
    {
        final DefaultDestination emptyDestination = DefaultDestination.builder().build();

        Assertions
            .assertThatThrownBy(() -> DefaultRfcDestination.fromProperties(emptyDestination))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testEqualsAndHashCode()
    {
        final RfcDestination firstDestination = DefaultRfcDestination.fromProperties(testProperties);
        final RfcDestination secondDestination = DefaultRfcDestination.fromProperties(testProperties);

        Assertions.assertThat(firstDestination).isEqualTo(secondDestination).isNotSameAs(secondDestination);
        Assertions.assertThat(firstDestination).hasSameHashCodeAs(secondDestination);
    }
}
