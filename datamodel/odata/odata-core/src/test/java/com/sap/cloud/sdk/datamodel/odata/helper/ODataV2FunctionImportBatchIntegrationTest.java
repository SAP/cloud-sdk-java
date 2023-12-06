/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
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

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchChangeSetFluentHelperBasic;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchFluentHelperBasic;

import lombok.Getter;

@WireMockTest
class ODataV2FunctionImportBatchIntegrationTest
{
    private static final String ODATA_ENDPOINT_URL = "/path/to/service";
    private static final String ODATA_ENDPOINT_BATCH_URL = ODATA_ENDPOINT_URL + "/$batch";

    @Getter
    public static class TestBatch extends BatchFluentHelperBasic<TestBatch, TestBatchChangeset>
    {
        private final String servicePathForBatchRequest = ODATA_ENDPOINT_URL;

        @Nonnull
        @Override
        protected TestBatch getThis()
        {
            return this;
        }

        @Nonnull
        @Override
        public TestBatchChangeset beginChangeSet()
        {
            return new TestBatchChangeset(this);
        }
    }

    public static class TestBatchChangeset extends BatchChangeSetFluentHelperBasic<TestBatch, TestBatchChangeset>
    {
        public TestBatchChangeset( final TestBatch parent )
        {
            super(parent, parent);
        }

        @Nonnull
        @Override
        protected TestBatchChangeset getThis()
        {
            return this;
        }
    }

    @Test
    void testFunctionImportWithPostInChangeSet( @Nonnull final WireMockRuntimeInfo wm )
    {
        stubFor(head(urlEqualTo(ODATA_ENDPOINT_URL)).willReturn(noContent()));
        stubFor(post(urlEqualTo(ODATA_ENDPOINT_BATCH_URL)).willReturn(ok()));

        final DefaultHttpDestination destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();

        final Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("FileDocumentYear", "2015");
        parameters.put("FileDocument", "00281");
        parameters.put("FileDocumentItem", "1");
        parameters.put("PostingDate", LocalDateTime.of(2015, 1, 12, 12, 12));

        final FluentHelperFunction<?, ?, Void> functionImport =
            FluentHelperFactory
                .withServicePath(ODATA_ENDPOINT_URL)
                .functionSinglePost(parameters, "CancelItem", Void.class);

        new TestBatch().beginChangeSet().addFunctionImport(functionImport).endChangeSet().executeRequest(destination);

        final String functionImportPost =
            "POST CancelItem?FileDocumentYear='2015'&FileDocument='00281'&FileDocumentItem='1'&PostingDate=datetime'2015-01-12T12:12:00' HTTP/1.1";

        verify(postRequestedFor(urlEqualTo(ODATA_ENDPOINT_BATCH_URL)).withRequestBody(containing(functionImportPost)));
    }
}
