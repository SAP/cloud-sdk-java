/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper;

import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.headRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.noContent;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;

@WireMockTest
class VdmComplexTest
{
    private static final String RESPONSE_CREATE_ENTITY = """
        {"d":{
          "__metadata": {
            "id": "https://127.0.0.1/path/to/service(100)",
            "uri": "https://127.0.0.1/path/to/service(100)",
            "type": "SERVICE.SomeEntity"
          },
          "IntegerValue":100,
          "StringValue":"Foo",
          "ComplexValue":{
            "__metadata": {
              "type": "SERVICE.SomeComplex"
            },
            "SomeValue":"Some",
            "OtherValue":"Another"
          }
        }}
        """;

    private static final String REQUEST_CREATE_ENTITY = """
        {
          "StringValue" : "Foo",
          "ComplexValue" : {
            "SomeValue" : "Some",
            "OtherValue" : "Another"
          }
        }
        """;

    private HttpDestination destination;

    @BeforeEach
    void setup( @Nonnull final WireMockRuntimeInfo wm )
    {
        destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
    }

    @Test
    void testCreateEntityWithComplexProperty()
    {
        stubFor(head(anyUrl()).willReturn(noContent()));
        stubFor(post(anyUrl()).willReturn(okJson(RESPONSE_CREATE_ENTITY)));

        final TestVdmComplex complex = new TestVdmComplex();
        complex.setSomeValue("Some");
        complex.setOtherValue("Another");

        final TestVdmEntity entity = new TestVdmEntity();
        entity.setStringValue("Foo");
        entity.setComplexValue(complex);

        final TestVdmEntity resultEntity =
            FluentHelperFactory
                .withServicePath("/path/to/service")
                .create(entity.getEntityCollection(), entity)
                .executeRequest(destination)
                .getModifiedEntity();

        assertThat(resultEntity).isNotNull().isNotEqualTo(entity);
        assertThat(resultEntity.getComplexValue()).isEqualTo(complex);

        verify(headRequestedFor(urlPathEqualTo("/path/to/service")).withHeader("x-csrf-token", equalTo("fetch")));
        verify(
            postRequestedFor(urlPathEqualTo("/path/to/service/Entities"))
                .withRequestBody(equalToJson(REQUEST_CREATE_ENTITY)));
    }
}
