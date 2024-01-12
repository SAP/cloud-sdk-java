/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.result.ElementName;

import lombok.Data;

@Deprecated
@WireMockTest
class SoapRemoteFunctionResultTableSerializationTest
{
    private Destination destination;

    @BeforeEach
    void before( @Nonnull final WireMockRuntimeInfo wm )
    {
        destination = DefaultDestination.builder().property("URL", wm.getHttpBaseUrl()).build();
    }

    @Test
    void testDeserializeEmptyTable()
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        // Contains empty inner table is named "Messages"
        final String responsePayload = """
            <soap-env:Envelope xmlns:soap-env="http://schemas.xmlsoap.org/soap/envelope/">
               <soap-env:Header/>
               <soap-env:Body>
                  <n0:FtbiGetFxRatesResponse xmlns:n0="urn:sap-com:document:sap:soap:functions:mc-style">
                     <EtFxRates>
                        <item>
                           <Messages/>
                        </item>
                     </EtFxRates>
                     <EtMessagesGeneral/>
                  </n0:FtbiGetFxRatesResponse>
               </soap-env:Body>
            </soap-env:Envelope>
            """;

        WireMock
            .stubFor(
                WireMock
                    .post(WireMock.urlEqualTo("/sap/bc/srt/scs_ext/sap/7FTBI_GET_FX_RATES"))
                    .willReturn(WireMock.okXml(responsePayload)));

        final RfmRequest request = new RfmRequest("FTBI_GET_FX_RATES", CommitStrategy.NO_COMMIT);

        final RfmRequestResult result =
            request
                .withExportingTable("IT_REQUEST", "FTBI_T_FX_RATES_REQUEST_EXT")
                .row()
                .end()
                .withImporting("ET_FX_RATES", "FTBI_T_FX_RATES_RESPONSE_EXT")
                .withImportingAsReturn("ET_MESSAGES_GENERAL", "BAPIRET2_T")
                .execute(destination);

        final List<FxRatesList> fxRates = result.get("ET_FX_RATES").getAsCollection().asList(FxRatesList.class);

        assertThat(fxRates).hasSize(1);
        assertThat(fxRates.get(0).getMessages()).hasSize(0);
    }

    @Test
    void testDeserializeFilledTable()
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        final String responsePayload = """
            <soap-env:Envelope xmlns:soap-env="http://schemas.xmlsoap.org/soap/envelope/">
               <soap-env:Header/>
               <soap-env:Body>
                  <n0:FtbiGetFxRatesResponse xmlns:n0="urn:sap-com:document:sap:soap:functions:mc-style">
                     <EtFxRates>
                        <item>
                           <RowUuid>b251ecf65bec48ed9db39a313a7654ad</RowUuid>
                           <Messages>
                              <item>
                                 <Type>W</Type>
                                 <Id>FTBB</Id>
                                 <Number>005</Number>
                                 <Message/>
                                 <LogNo/>
                                 <LogMsgNo>000000</LogMsgNo>
                                 <MessageV1>06/01/2020</MessageV1>
                                 <MessageV2>05/26/2020</MessageV2>
                                 <MessageV3/>
                                 <MessageV4/>
                                 <Parameter/>
                                 <Row>0</Row>
                                 <Field/>
                                 <System/>
                              </item>
                           </Messages>
                        </item>
                     </EtFxRates>
                     <EtMessagesGeneral/>
                  </n0:FtbiGetFxRatesResponse>
               </soap-env:Body>
            </soap-env:Envelope>
            """;

        WireMock
            .stubFor(
                WireMock
                    .post(WireMock.urlEqualTo("/sap/bc/srt/scs_ext/sap/7FTBI_GET_FX_RATES"))
                    .willReturn(WireMock.okXml(responsePayload)));

        final RfmRequest request = new RfmRequest("FTBI_GET_FX_RATES", CommitStrategy.NO_COMMIT);

        final RfmRequestResult result =
            request
                .withExportingTable("IT_REQUEST", "FTBI_T_FX_RATES_REQUEST_EXT")
                .row()
                .end()
                .withImporting("ET_FX_RATES", "FTBI_T_FX_RATES_RESPONSE_EXT")
                .withImportingAsReturn("ET_MESSAGES_GENERAL", "BAPIRET2_T")
                .execute(destination);

        final List<FxRatesList> fxRates = result.get("ET_FX_RATES").getAsCollection().asList(FxRatesList.class);

        assertThat(fxRates).hasSize(1);
        assertThat(fxRates.get(0).getMessages()).hasSize(1);
        assertThat(fxRates.get(0).getMessages().get(0).getType()).isEqualTo("W");
    }

    @Data
    private static class FxRatesList
    {
        @ElementName( "MESSAGES" )
        private List<MessageListEntry> messages;
    }

    @Data
    private static class MessageListEntry
    {
        @ElementName( "TYPE" )
        private String type;
    }

}
