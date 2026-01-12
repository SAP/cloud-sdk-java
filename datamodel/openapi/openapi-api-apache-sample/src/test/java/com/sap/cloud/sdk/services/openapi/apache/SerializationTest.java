package com.sap.cloud.sdk.services.openapi.apache;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.datamodel.openapi.apache.sodastore.api.SodasApi;
import com.sap.cloud.sdk.datamodel.openapi.apache.sodastore.model.Order;
import com.sap.cloud.sdk.datamodel.openapi.apache.sodastore.model.SodaWithId;

@WireMockTest
class SerializationTest
{

    private SodasApi sut;
    private String expected;

    @BeforeEach
    void setUp( WireMockRuntimeInfo wmRuntimeInfo )
    {
        sut = new SodasApi(DefaultHttpDestination.builder(wmRuntimeInfo.getHttpBaseUrl()).build());
        WireMock.stubFor(WireMock.put("/sodas").willReturn(WireMock.created()));
    }

    @Test
    void testPutPayload()
    {
        // TODO: discuss whether to ignore null on serialization? Do via review
        expected = """
            {
              "name": "Cola",
              "brand": "Coca-Cola",
              "quantity": 100,
              "packaging" : null,
              "price": 1.5,
              "id": 0
            }
            """;

        final SodaWithId obj = SodaWithId.create().name("Cola").brand("Coca-Cola").quantity(100).price(1.5f).id(0L);

        sut.sodasPut(obj);

        verify(expected);
    }

    @Test
    void testJacksonSerializeSodaWithId()
        throws JsonProcessingException
    {
        expected = """
            {
              "name": "Cola",
              "brand": "Coca-Cola",
              "quantity": 100,
              "packaging" : "can",
              "price": 1.5,
              "id": 0
            }
            """;

        final SodaWithId obj =
            SodaWithId
                .create()
                .name("Cola")
                .brand("Coca-Cola")
                .quantity(100)
                .price(1.5f)
                .id(0L)
                .packaging(SodaWithId.PackagingEnum.CAN);

        assertThat(new ObjectMapper().writeValueAsString(obj)).isEqualToIgnoringWhitespace(expected);
    }

    @Test
    void testJacksonSerializeOrder()
        throws JsonProcessingException
    {
        expected = """
            {
              "productId": 100,
              "quantity": 5,
              "totalPrice": 6.0,
              "typelessProperty":null,
              "nullableProperty":null,
              "shoesize": 44
            }
            """;
        final Order order = Order.create().productId(100L).quantity(5).totalPrice(6.0f);
        order.setCustomField("shoesize", 44);
        assertThat(new ObjectMapper().writeValueAsString(order)).isEqualToIgnoringWhitespace(expected);
        assertThat(new ObjectMapper().readValue(expected, Order.class)).isEqualTo(order);
    }

    private void verify( String requestBody )
    {
        WireMock
            .verify(
                WireMock
                    .putRequestedFor(WireMock.urlEqualTo("/sodas"))
                    .withHeader("Content-Type", WireMock.equalTo("application/json; charset=UTF-8"))
                    .withRequestBody(WireMock.equalToJson(requestBody)));
    }

}
