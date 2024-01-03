/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.referenceservice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.datamodel.odatav4.core.SingleValueFunctionRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airport;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.services.DefaultTrippinService;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.services.TrippinService;

@Disabled( "Test runs against a v4 reference service on odata.org. Use it only to manually verify behaviour." )
class UnboundFunctionTest
{

    private static final TrippinService service = new DefaultTrippinService();
    private HttpDestination httpDestination;

    @BeforeEach
    void configure()
    {
        httpDestination = DefaultHttpDestination.builder("https://services.odata.org").build();
    }

    @Test
    void testFunctionWithoutParameters()
    {
        final SingleValueFunctionRequestBuilder<Person> builder = service.getPersonWithMostFriends();
        final String expected = DefaultTrippinService.DEFAULT_SERVICE_PATH + "/GetPersonWithMostFriends";
        assertThat(builder.toRequest().getRelativeUri()).hasToString(expected);
        assertThat(builder.execute(httpDestination)).isInstanceOf(Person.class);
    }

    @Test
    void testFunctionWithParameters()
    {
        final SingleValueFunctionRequestBuilder<Airport> builder = service.getNearestAirport(33.0, -118.0);
        final String expected = DefaultTrippinService.DEFAULT_SERVICE_PATH + "/GetNearestAirport(lat=33.0,lon=-118.0)";
        assertThat(builder.toRequest().getRelativeUri()).hasToString(expected);
        assertThat(builder.execute(httpDestination)).isInstanceOf(Airport.class);
    }
}
