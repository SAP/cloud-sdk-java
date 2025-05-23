package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.assertThat;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;

@Deprecated
@WireMockTest
class SoapRemoteFunctionRequestExecutorTest
{
    private Destination destination;

    @BeforeEach
    void before( @Nonnull final WireMockRuntimeInfo wm )
    {
        destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
    }

    @Test
    void testExceptionWhenNoAuthorizationForSoapService()
        throws Exception
    {
        final String responsePayload =
            """
                <soap-env:Envelope xmlns:soap-env="http://schemas.xmlsoap.org/soap/envelope/">
                    <soap-env:Header/>
                    <soap-env:Body>
                        <soap-env:Fault>
                            <faultcode>soap-env:Server</faultcode>
                            <faultstring xml:lang="en">Authorization missing for service "urn:sap-com:document:sap:soap:functions:mc-style _--37BAPI_FIXEDASSET_GETLIST", operation "FixedassetGetlist";more details in the web service error log on provider side (UTC timestamp 20170821152434; Transaction ID AF0DC6FADCD50210E00598DB19C81681)
                            </faultstring>
                            <detail/>
                        </soap-env:Fault>
                    </soap-env:Body>
                </soap-env:Envelope>
                """;

        stubFor(
            post(anyUrl())
                .withHeader("SOAPAction", matching(".*"))
                .willReturn(aResponse().withStatus(500).withBody(responsePayload)));

        String exceptionMesssage = "";
        try {
            new DataCollectionProgramTextsRfm().execute(destination);
        }
        catch( final com.sap.cloud.sdk.s4hana.connectivity.exception.AccessDeniedException e ) {
            exceptionMesssage = e.getMessage();
        }

        assertThat(exceptionMesssage)
            .contains("The ERP user lacks authorization to call the SOAP service (Authorization Object S_SERVICE");
    }

    @Test
    void testExceptionDuringSoapServiceProcessing()
    {
        final String responsePayload =
            """
                <soap-env:Envelope xmlns:soap-env="http://schemas.xmlsoap.org/soap/envelope/">
                    <soap-env:Header/>
                    <soap-env:Body>
                        <soap-env:Fault>
                            <faultcode>soap-env:Client</faultcode>
                            <faultstring xml:lang="en">ExcNoPermission</faultstring>
                            <detail>
                                <n0:FcGlobalParamsImportRfc.Exception xmlns:n0="urn:sap-com:document:sap:soap:functions:mc-style">
                                    <Name>ExcNoPermission</Name>
                                    <Text>Authorization "Execute" missing for Transaction Code "CXGP".</Text>
                                    <Message>
                                        <ID>GK</ID>
                                        <Number>051</Number>
                                    </Message>
                                </n0:FcGlobalParamsImportRfc.Exception>
                            </detail>
                        </soap-env:Fault>
                    </soap-env:Body>
                </soap-env:Envelope>
                """;

        stubFor(
            post(anyUrl())
                .withHeader("SOAPAction", matching(".*"))
                .willReturn(aResponse().withStatus(500).withBody(responsePayload)));

        String exceptionMesssage = "";

        try {
            new DataCollectionProgramTextsRfm().execute(destination);
        }
        catch( final com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException e ) {
            exceptionMesssage = e.getMessage();
        }

        assertThat(exceptionMesssage).contains("Exception occurred during execution of SOAP service");
        assertThat(exceptionMesssage).contains("ExcNoPermission");
        assertThat(exceptionMesssage).contains("Authorization \"Execute\" missing for Transaction Code");
    }

    @Test
    void testExceptionDuringSoapServiceProcessingWithAlternativePrefix()
    {
        // save and change namespace labels
        final String prefixSoapEnv = SoapNamespace.RESPONSE_PREFIX_SOAP_ENV.toString();
        final String prefixN0 = SoapNamespace.RESPONSE_PREFIX_N0.toString();
        SoapNamespace.RESPONSE_PREFIX_SOAP_ENV.setLabel("ppp");
        SoapNamespace.RESPONSE_PREFIX_N0.setLabel("qqq");

        final String responsePayload =
            """
                <ppp:Envelope xmlns:ppp="http://schemas.xmlsoap.org/soap/envelope/">
                    <ppp:Header/>
                    <ppp:Body>
                        <ppp:Fault>
                            <faultcode>ppp:Client</faultcode>
                            <faultstring xml:lang="en">ExcNoPermission</faultstring>
                            <detail>
                                <qqq:FcGlobalParamsImportRfc.Exception xmlns:qqq="urn:sap-com:document:sap:soap:functions:mc-style">
                                    <Name>ExcNoPermission</Name>
                                    <Text>Authorization "Execute" missing for Transaction Code "CXGP".</Text>
                                    <Message>
                                        <ID>GK</ID>
                                        <Number>051</Number>
                                    </Message>
                                </qqq:FcGlobalParamsImportRfc.Exception>
                            </detail>
                        </ppp:Fault>
                    </ppp:Body>
                </ppp:Envelope>
                """;

        stubFor(
            post(anyUrl())
                .withHeader("SOAPAction", matching(".*"))
                .willReturn(aResponse().withStatus(500).withBody(responsePayload)));

        String exceptionMesssage = "";

        try {
            new DataCollectionProgramTextsRfm().execute(destination);
        }
        catch( final com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException e ) {
            exceptionMesssage = e.getMessage();
        }

        // reset namespace labels
        SoapNamespace.RESPONSE_PREFIX_SOAP_ENV.setLabel(prefixSoapEnv);
        SoapNamespace.RESPONSE_PREFIX_N0.setLabel(prefixN0);

        assertThat(exceptionMesssage).contains("Exception occurred during execution of SOAP service");
        assertThat(exceptionMesssage).contains("ExcNoPermission");
        assertThat(exceptionMesssage).contains("Authorization \"Execute\" missing for Transaction Code");
    }

    @Test
    void testHeadersInNormalSoapRequest()
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        final String responsePayload = """
            <soap-env:Envelope xmlns:soap-env="http://schemas.xmlsoap.org/soap/envelope/">
                <soap-env:Header/>
                <soap-env:Body>
                    <n0:RfcTestResponse xmlns:n0="urn:sap-com:document:sap:soap:functions:mc-style">
                    </n0:RfcTestResponse>
                </soap-env:Body>
            </soap-env:Envelope>
            """;

        stubFor(
            post(anyUrl()).withHeader("SOAPAction", matching(".*RfcTest$")).willReturn(ok().withBody(responsePayload)));

        stubFor(
            post(anyUrl())
                .withHeader("SOAPAction", matching(".*TransactionCommit$"))
                .willReturn(ok().withBody(responsePayload)));

        final RfmRequest rfcRequest = new RfmRequest("RFC_TEST", true);
        final RfmRequestResult rfcResult = rfcRequest.execute(destination);

        assertThat(rfcResult.wasSuccessful()).isTrue();
    }

    @Test
    void testHeadersInNormalSoapRequestWithAlternativePrefix()
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        // save and change namespace labels
        final String prefixSoapEnv = SoapNamespace.RESPONSE_PREFIX_SOAP_ENV.toString();
        final String prefixN0 = SoapNamespace.RESPONSE_PREFIX_N0.toString();
        SoapNamespace.RESPONSE_PREFIX_SOAP_ENV.setLabel("ppp");
        SoapNamespace.RESPONSE_PREFIX_N0.setLabel("qqq");

        final String responsePayload = """
            <ppp:Envelope xmlns:ppp="http://schemas.xmlsoap.org/soap/envelope/">\
            <ppp:Header/>\
            <ppp:Body>\
            <qqq:RfcTestResponse xmlns:qqq="urn:sap-com:document:sap:soap:functions:mc-style">
            </qqq:RfcTestResponse>
            </ppp:Body>\
            </ppp:Envelope>\
            """;

        stubFor(
            post(anyUrl()).withHeader("SOAPAction", matching(".*RfcTest$")).willReturn(ok().withBody(responsePayload)));

        stubFor(
            post(anyUrl())
                .withHeader("SOAPAction", matching(".*TransactionCommit$"))
                .willReturn(ok().withBody(responsePayload)));

        final RfmRequest rfcRequest = new RfmRequest("RFC_TEST", true);
        final RfmRequestResult rfcResult = rfcRequest.execute(destination);

        // reset namespace labels
        SoapNamespace.RESPONSE_PREFIX_SOAP_ENV.setLabel(prefixSoapEnv);
        SoapNamespace.RESPONSE_PREFIX_N0.setLabel(prefixN0);

        assertThat(rfcResult.wasSuccessful()).isTrue();
    }

    @Test
    void testHeadersInRollbackSoapRequest()
    {
        final String responsePayload = """
            <soap-env:Envelope xmlns:soap-env="http://schemas.xmlsoap.org/soap/envelope/">
                <soap-env:Header/>
                <soap-env:Body>
                    <n0:RfcTestResponse xmlns:n0="urn:sap-com:document:sap:soap:functions:mc-style">
                    </n0:RfcTestResponse>
                </soap-env:Body>
            </soap-env:Envelope>
            """;

        stubFor(
            post(anyUrl()).withHeader("SOAPAction", matching(".*RfcTest$")).willReturn(ok().withBody(responsePayload)));

        stubFor(
            post(anyUrl())
                .withHeader("SOAPAction", matching(".*TransactionCommit$"))
                .willReturn(aResponse().withStatus(500).withBody(responsePayload)));

        stubFor(
            post(anyUrl())
                .withHeader("SOAPAction", matching(".*TransactionRollback$"))
                .willReturn(ok().withBody(responsePayload)));

        final RfmRequest rfcRequest = new RfmRequest("RFC_TEST", true);

        String exceptionMesssage = "";
        try {
            rfcRequest.execute(destination);
        }
        catch( final com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException e ) {
            exceptionMesssage = e.getMessage();
        }

        assertThat(exceptionMesssage)
            .contains("500 Internal Server Error. Request execution failed with unexpected error.");
    }
}
