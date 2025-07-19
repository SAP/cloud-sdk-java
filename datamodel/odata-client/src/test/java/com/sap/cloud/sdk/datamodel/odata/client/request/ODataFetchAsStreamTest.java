package com.sap.cloud.sdk.datamodel.odata.client.request;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol.V2;
import static com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol.V4;
import static com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIOException;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.apache.http.client.entity.DecompressingEntity;
import org.apache.http.entity.BufferedHttpEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.google.common.io.Resources;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpEntityUtil;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;

import lombok.SneakyThrows;

class ODataFetchAsStreamTest
{
    private static final String URL = "/service";
    private static final String TEXT_FILE_NAME = "test.txt";
    private static final String IMAGE_FILE_NAME = "SAP_logo.png";
    private static final String PDF_FILE_NAME = "POT01.pdf";

    private static final Consumer<ODataRequestResult> VALIDATOR_BUFFERED =
        result -> assertThat(result.getHttpResponse().getEntity())
            .isInstanceOfAny(BufferedHttpEntity.class, DecompressingEntity.class);

    private static final Consumer<ODataRequestResult> VALIDATOR_LAZY =
        result -> assertThat(result.getHttpResponse().getEntity().isRepeatable()).isFalse();

    @RegisterExtension
    static final WireMockExtension SERVER =
        WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();

    @SneakyThrows
    private ODataRequestResult testStreamedFileForRequest(
        final String fileName,
        final ODataRequestExecutable request,
        final Consumer<ODataRequestResult> resultValidator )
    {
        final String responseBody = readResourceFile(fileName);
        SERVER.stubFor(get(WireMock.anyUrl()).willReturn(ok().withBody(responseBody)));

        final Destination destination = DefaultHttpDestination.builder(SERVER.baseUrl()).build();

        final ODataRequestResult result = request.execute(HttpClientAccessor.getHttpClient(destination));
        resultValidator.accept(result);

        try( InputStream actualFileStream = result.getHttpResponse().getEntity().getContent(); ) {
            assertThat(actualFileStream).isNotNull();
            assertThat(actualFileStream.available()).isGreaterThan(0);

            try( InputStream expectedFileStream = readResourceStream(fileName) ) {
                assertThat(actualFileStream).hasSameContentAs(expectedFileStream);
            }
        }

        SERVER.verify(getRequestedFor(urlEqualTo(URL + "/Airports('BER')/$value")));

        return result;
    }

    @Test
    void testFetchTextFileAsStreamODataV2()
    {
        testStreamedFileForRequest(TEXT_FILE_NAME, createRequest(V2), VALIDATOR_BUFFERED);
    }

    @Test
    void testFetchImageFileAsStreamODataV2()
    {
        testStreamedFileForRequest(IMAGE_FILE_NAME, createRequest(V2), VALIDATOR_BUFFERED);
    }

    @Test
    void testFetchPdfFileAsStreamODataV2()
    {
        testStreamedFileForRequest(PDF_FILE_NAME, createRequest(V2), VALIDATOR_BUFFERED);
    }

    @Test
    void testFetchTextFileAsStreamODataV4()
    {
        testStreamedFileForRequest(TEXT_FILE_NAME, createRequest(V4), VALIDATOR_BUFFERED);
    }

    @Test
    void testFetchImageFileAsStreamODataV4()
    {
        testStreamedFileForRequest(IMAGE_FILE_NAME, createRequest(V4), VALIDATOR_BUFFERED);
    }

    @Test
    void testFetchPdfFileAsStreamODataV4()
    {
        testStreamedFileForRequest(PDF_FILE_NAME, createRequest(V4), VALIDATOR_BUFFERED);
    }

    @SneakyThrows
    @Test
    void testLazyResponseAsStreamODataV2()
    {
        final ODataRequestReadByKey request = createRequest(V2);
        final ODataRequestExecutable lazyRequest = request.withoutResponseBuffering();
        final ODataRequestResult result = testStreamedFileForRequest(TEXT_FILE_NAME, lazyRequest, VALIDATOR_LAZY);

        assertThatIOException()
            .isThrownBy(() -> HttpEntityUtil.getResponseBody(result.getHttpResponse()))
            .withMessage("Stream closed");
    }

    @SneakyThrows
    @Test
    void testLazyResponseAsStreamODataV4()
    {
        final ODataRequestReadByKey request = createRequest(V4);
        final ODataRequestExecutable lazyRequest = request.withoutResponseBuffering();
        final ODataRequestResult result = testStreamedFileForRequest(TEXT_FILE_NAME, lazyRequest, VALIDATOR_LAZY);

        assertThatIOException()
            .isThrownBy(() -> HttpEntityUtil.getResponseBody(result.getHttpResponse()))
            .withMessage("Stream closed");
    }

    @SneakyThrows
    @Test
    void testLazyResponseWithWorkaround()
    {
        final ODataRequestReadByKey request = createRequest(V4);
        ODataRequestReadByKey.class.getMethod("withoutResponseBuffering").invoke(request); // workaround
        final ODataRequestResult result = testStreamedFileForRequest(TEXT_FILE_NAME, request, VALIDATOR_LAZY);

        assertThatIOException()
            .isThrownBy(() -> HttpEntityUtil.getResponseBody(result.getHttpResponse()))
            .withMessage("Stream closed");
    }

    @SneakyThrows
    private static String readResourceFile( final String resourceFileName )
    {
        final String fileName = ODataFetchAsStreamTest.class.getSimpleName() + "/files/" + resourceFileName;
        final URL resourceUrl = ODataFetchAsStreamTest.class.getClassLoader().getResource(fileName);

        if( resourceUrl == null ) {
            throw new IllegalStateException("Cannot find resource file with name \"" + resourceFileName + "\".");
        }
        return Resources.toString(resourceUrl, StandardCharsets.UTF_8);
    }

    @SneakyThrows
    private static InputStream readResourceStream( final String resourceFileName )
    {
        final String fileName = ODataFetchAsStreamTest.class.getSimpleName() + "/files/" + resourceFileName;
        return ODataFetchAsStreamTest.class.getClassLoader().getResourceAsStream(fileName);
    }

    private static ODataRequestReadByKey createRequest( final ODataProtocol protocol )
    {
        final ODataEntityKey key = new ODataEntityKey(protocol).addKeyProperty("Name", "BER");
        final ODataResourcePath path = of("Airports", key).addSegment("$value");
        return new ODataRequestReadByKey(URL, path, "", protocol);
    }
}
