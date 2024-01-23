/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.openapi.sample.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.SodaWithId;

@WireMockTest
class SerializationTest
{
    private SodasApi sut;
    private String expected;

    @BeforeEach
    void setUp( WireMockRuntimeInfo wmRuntimeInfo )
    {
        sut = new SodasApi(DefaultHttpDestination.builder(wmRuntimeInfo.getHttpBaseUrl()).build());
        WireMock.stubFor(WireMock.put(WireMock.anyUrl()).willReturn(WireMock.created()));
    }

    @Test
    void testPutPayload()
    {
        expected = """
            {
              "name": "Cola",
              "brand": "Coca-Cola",
              "quantity": 100,
              "price": 1.5,
              "id": 0
            }
            """;

        final SodaWithId obj = new SodaWithId().id(0L).name("Cola").brand("Coca-Cola").quantity(100).price(1.5f);

        sut.sodasPut(obj);

        verify(expected);
    }

    @Test
    void testJacksonSerialization()
        throws JsonProcessingException
    {
        expected = """
            {
              "name": "Cola",
              "brand": "Coca-Cola",
              "quantity": 100,
              "price": 1.5,
              "id": 0
            }
            """;

        final SodaWithId obj = new SodaWithId().id(0L).name("Cola").brand("Coca-Cola").quantity(100).price(1.5f);

        assertThat(new ObjectMapper().writeValueAsString(obj)).isEqualToIgnoringWhitespace(expected);
    }

    private void verify( String requestBody )
    {
        WireMock
            .verify(
                WireMock
                    .putRequestedFor(WireMock.urlEqualTo("/sodas"))
                    .withHeader("Content-Type", WireMock.equalTo("application/json"))
                    .withRequestBody(WireMock.equalToJson(requestBody)));
    }
}
