/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper;

import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.noContent;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

import java.util.Collections;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchFluentHelperBasic;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.FluentHelperBatchEndChangeSet;

@WireMockTest
class ODataV2FunctionImportGetBatchIntegrationTest
{
    private static final String ODATA_ENDPOINT_URL = "/path/to/service";
    private static final String ODATA_ENDPOINT_BATCH_URL = ODATA_ENDPOINT_URL + "/$batch";

    public static class TestBatch extends BatchFluentHelperBasic<TestBatch, TestBatch>
        implements
        FluentHelperBatchEndChangeSet<TestBatch>
    {
        @Nonnull
        @Override
        protected String getServicePathForBatchRequest()
        {
            return ODATA_ENDPOINT_URL;
        }

        @Nonnull
        @Override
        protected TestBatch getThis()
        {
            return this;
        }

        @Nonnull
        @Override
        public TestBatch beginChangeSet()
        {
            return this;
        }

        @Nonnull
        @Override
        public TestBatch endChangeSet()
        {
            return this;
        }
    }

    @Test
    void testFunctionImportWithGetInReadOperation( @Nonnull final WireMockRuntimeInfo wm )
    {
        stubFor(head(urlEqualTo(ODATA_ENDPOINT_URL)).willReturn(noContent()));
        stubFor(post(urlEqualTo(ODATA_ENDPOINT_BATCH_URL)).willReturn(ok()));

        final DefaultHttpDestination destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();

        final FluentHelperFunction<?, ?, String> functionImport =
            FluentHelperFactory
                .withServicePath(ODATA_ENDPOINT_URL)
                .functionSingleGet(Collections.emptyMap(), "Get_TestQueue", String.class);

        new TestBatch().addReadOperations(functionImport).executeRequest(destination);

        final String functionImportGet = "GET Get_TestQueue HTTP/1.1";

        verify(postRequestedFor(urlEqualTo(ODATA_ENDPOINT_BATCH_URL)).withRequestBody(containing(functionImportGet)));
    }
}
