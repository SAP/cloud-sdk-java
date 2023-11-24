/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.google.common.io.Resources;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;

class ODataFetchAsStreamTest
{
    private static final String SERVICE_URL = "/service";
    private static final String TEXT_FILE_NAME = "test.txt";
    private static final String IMAGE_FILE_NAME = "SAP_logo.png";
    private static final String PDF_FILE_NAME = "POT01.pdf";

    @RegisterExtension
    static final WireMockExtension SERVER =
        WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();

    private void testFileAsStream( final String fileName, final ODataProtocol oDataProtocol )
        throws IOException
    {
        SERVER
            .stubFor(
                get(WireMock.anyUrl())
                    .willReturn(ok().withBody(readResourceFile(ODataFetchAsStreamTest.class, "files/" + fileName))));

        final Destination destination = DefaultHttpDestination.builder(SERVER.baseUrl()).build();

        final ODataResourcePath resource =
            ODataResourcePath
                .of("Airports", new ODataEntityKey(ODataProtocol.V2).addKeyProperty("Name", "BER"))
                .addSegment("$value");

        final ODataRequestReadByKey request = new ODataRequestReadByKey(SERVICE_URL, resource, "", oDataProtocol);

        final ODataRequestResultGeneric result = request.execute(HttpClientAccessor.getHttpClient(destination));

        try( InputStream actualFileStream = result.getHttpResponse().getEntity().getContent(); ) {
            assertThat(actualFileStream).isNotNull();
            assertThat(actualFileStream.available()).isGreaterThan(0);

            try(
                InputStream expectedFileStream =
                    getClass()
                        .getClassLoader()
                        .getResourceAsStream(ODataFetchAsStreamTest.class.getSimpleName() + "/files/" + fileName) ) {
                assertThat(expectedFileStream).hasSameContentAs(actualFileStream);
            }
        }

        SERVER.verify(getRequestedFor(urlEqualTo(SERVICE_URL + "/Airports('BER')/$value")));
    }

    @Test
    void testFetchTextFileAsStreamODataV2()
        throws IOException
    {
        testFileAsStream(TEXT_FILE_NAME, ODataProtocol.V2);
    }

    @Test
    void testFetchImageFileAsStreamODataV2()
        throws IOException
    {
        testFileAsStream(IMAGE_FILE_NAME, ODataProtocol.V2);
    }

    @Test
    void testFetchPdfFileAsStreamODataV2()
        throws IOException
    {
        testFileAsStream(PDF_FILE_NAME, ODataProtocol.V2);
    }

    @Test
    void testFetchTextFileAsStreamODataV4()
        throws IOException
    {
        testFileAsStream(TEXT_FILE_NAME, ODataProtocol.V4);
    }

    @Test
    void testFetchImageFileAsStreamODataV4()
        throws IOException
    {
        testFileAsStream(IMAGE_FILE_NAME, ODataProtocol.V4);
    }

    @Test
    void testFetchPdfFileAsStreamODataV4()
        throws IOException
    {
        testFileAsStream(PDF_FILE_NAME, ODataProtocol.V4);
    }

    private static String readResourceFile( final Class<?> cls, final String resourceFileName )
    {
        try {
            final URL resourceUrl = cls.getClassLoader().getResource(cls.getSimpleName() + "/" + resourceFileName);

            if( resourceUrl == null ) {
                throw new IllegalStateException("Cannot find resource file with name \"" + resourceFileName + "\".");
            }

            return Resources.toString(resourceUrl, StandardCharsets.UTF_8);
        }
        catch( final IOException e ) {
            throw new IllegalStateException(e);
        }
    }
}
