/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.result.ElementName;

import lombok.Data;

@Deprecated
public class SoapRemoteFunctionResultTableSerializationTest
{
    @Rule
    public final WireMockRule wireMockServer = new WireMockRule(WireMockConfiguration.wireMockConfig().dynamicPort());

    private Destination destination;

    @Before
    public void before()
    {
        destination = DefaultDestination.builder().property("URL", wireMockServer.baseUrl()).build();
    }

    @Test
    public void testDeserializeEmptyTable()
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        final String responsePayload =
            "<soap-env:Envelope xmlns:soap-env=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                + "   <soap-env:Header/>\n"
                + "   <soap-env:Body>\n"
                + "      <n0:FtbiGetFxRatesResponse xmlns:n0=\"urn:sap-com:document:sap:soap:functions:mc-style\">\n"
                + "         <EtFxRates>\n"
                + "            <item>\n"
                +
                //Here is the empty inner table named "Messages"
                "               <Messages/>\n"
                + "            </item>\n"
                + "         </EtFxRates>\n"
                + "         <EtMessagesGeneral/>\n"
                + "      </n0:FtbiGetFxRatesResponse>\n"
                + "   </soap-env:Body>\n"
                + "</soap-env:Envelope>";

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
    public void testDeserializeFilledTable()
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        final String responsePayload =
            "<soap-env:Envelope xmlns:soap-env=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                + "   <soap-env:Header/>\n"
                + "   <soap-env:Body>\n"
                + "      <n0:FtbiGetFxRatesResponse xmlns:n0=\"urn:sap-com:document:sap:soap:functions:mc-style\">\n"
                + "         <EtFxRates>\n"
                + "            <item>\n"
                + "               <RowUuid>b251ecf65bec48ed9db39a313a7654ad</RowUuid>\n"
                +
                //Here is the filled inner table with one entry
                "               <Messages>\n"
                + "                  <item>\n"
                + "                     <Type>W</Type>\n"
                + "                     <Id>FTBB</Id>\n"
                + "                     <Number>005</Number>\n"
                + "                     <Message/>\n"
                + "                     <LogNo/>\n"
                + "                     <LogMsgNo>000000</LogMsgNo>\n"
                + "                     <MessageV1>06/01/2020</MessageV1>\n"
                + "                     <MessageV2>05/26/2020</MessageV2>\n"
                + "                     <MessageV3/>\n"
                + "                     <MessageV4/>\n"
                + "                     <Parameter/>\n"
                + "                     <Row>0</Row>\n"
                + "                     <Field/>\n"
                + "                     <System/>\n"
                + "                  </item>\n"
                + "               </Messages>\n"
                + "            </item>\n"
                + "         </EtFxRates>\n"
                + "         <EtMessagesGeneral/>\n"
                + "      </n0:FtbiGetFxRatesResponse>\n"
                + "   </soap-env:Body>\n"
                + "</soap-env:Envelope>";

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
