/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.openapi.sample.api;

import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.SodaWithId;

@WireMockTest
class DeserializationTest
{
    private SodasApi sut;
    private String responseBody;

    @BeforeEach
    void setUp( WireMockRuntimeInfo wmRuntimeInfo )
    {
        sut = new SodasApi(DefaultHttpDestination.builder(wmRuntimeInfo.getHttpBaseUrl()).build());
    }

    @Test
    void testFullResponse()
    {
        // @formatter:off
        responseBody =
                "{" +
                "  \"name\": \"Cola\",\n" +
                "  \"brand\": \"Coca-Cola\",\n" +
                "  \"quantity\": 100,\n" +
                "  \"price\": 1.5,\n" +
                "  \"id\": 0\n" +
                "}";
        // @formatter:on
        stub(responseBody);

        final SodaWithId expected = new SodaWithId().id(0L).name("Cola").brand("Coca-Cola").quantity(100).price(1.5f);

        final SodaWithId actual = sut.sodasIdGet(1L);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testUnexpectedFieldOrder()
    {
        // @formatter:off
        responseBody =
                "{\n" +
                "  \"name\": \"Cola\",\n" +
                "  \"price\": 1.5,\n" +
                "  \"id\": 0,\n" +
                "  \"brand\": \"Coca-Cola\",\n" +
                "  \"quantity\": 100\n" +
                "}";
        // @formatter:on
        stub(responseBody);

        final SodaWithId expected = new SodaWithId().id(0L).name("Cola").brand("Coca-Cola").quantity(100).price(1.5f);

        final SodaWithId actual = sut.sodasIdGet(1L);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testPartialResponse()
    {
        responseBody = "{\"name\": \"Cola\"}";
        stub(responseBody);

        final SodaWithId expected = new SodaWithId().name("Cola");

        final SodaWithId actual = sut.sodasIdGet(1L);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testUnexpectedAdditionalField()
    {
        // @formatter:off
        responseBody =
                "{\n"
              + "    \"name\": \"Cola\",\n"
              + "    \"unexpectedField\": []\n"
              + "}\n";
        // @formatter:on
        stub(responseBody);

        final SodaWithId actual = sut.sodasIdGet(1L);

        assertThat(actual.getName()).isEqualTo("Cola");
        assertThat(actual.getCustomFieldNames()).containsExactly("unexpectedField");
        assertThat(actual.getCustomField("unexpectedField")).asInstanceOf(InstanceOfAssertFactories.LIST).isEmpty();
    }

    private void stub( String responseBody )
    {
        WireMock.stubFor(WireMock.get(WireMock.anyUrl()).willReturn(okJson(responseBody)));
    }
}
