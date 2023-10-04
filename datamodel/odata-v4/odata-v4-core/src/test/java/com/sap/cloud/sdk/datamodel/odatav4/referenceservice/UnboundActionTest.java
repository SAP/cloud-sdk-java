/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.referenceservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.datamodel.odatav4.core.ActionResponseSingle;
import com.sap.cloud.sdk.datamodel.odatav4.core.SingleValueActionRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.services.DefaultTrippinService;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.services.TrippinService;

@Ignore( "Test runs against a v4 reference service on odata.org. Use it only to manually verify behaviour." )
public class UnboundActionTest
{

    private static final TrippinService service = new DefaultTrippinService();
    private HttpDestination httpDestination;

    @Before
    public void configure()
        throws IOException
    {
        httpDestination = TripPinUtility.getDestination();
    }

    @Test
    public void testActionWithoutParameters()
    {
        final SingleValueActionRequestBuilder<Void> builder = service.resetDataSource();
        final ActionResponseSingle<Void> actionResponse = builder.execute(httpDestination);
        assertThat(actionResponse).isNotNull();
        assertThat(actionResponse.getResponseStatusCode()).isEqualTo(204);
    }
}
