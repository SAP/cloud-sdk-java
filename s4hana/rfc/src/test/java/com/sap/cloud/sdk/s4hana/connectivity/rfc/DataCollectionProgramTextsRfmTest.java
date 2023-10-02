/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import static com.github.tomakehurst.wiremock.client.WireMock.okXml;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.annotation.Nullable;

import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.io.Resources;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.result.ElementName;

import lombok.SneakyThrows;
import lombok.Value;

@Deprecated
public class DataCollectionProgramTextsRfmTest
{
    @Rule
    public WireMockRule wireMockServer = new WireMockRule(wireMockConfig().dynamicPort());

    @Value
    public static class ProgramText
    {
        @Nullable
        @ElementName( "TEXT_ID" )
        String textId;

        @Nullable
        @ElementName( "DESCR" )
        String description;
    }

    @SneakyThrows( IOException.class )
    private static String readResourceFile( final String resourceFileName )
    {
        final URL resourceUrl = getResourceUrl(DataCollectionProgramTextsRfmTest.class, resourceFileName);

        return Resources.toString(resourceUrl, StandardCharsets.UTF_8);
    }

    static URL getResourceUrl( final Class<?> cls, final String resourceFileName )
    {
        final URL resourceUrl = cls.getClassLoader().getResource(cls.getSimpleName() + "/" + resourceFileName);

        if( resourceUrl == null ) {
            throw new IllegalArgumentException("Cannot find resource file with name \"" + resourceFileName + "\".");
        }

        return resourceUrl;
    }

    @Test
    public void testLegalFormRfcTest()
        throws Exception
    {
        final String responseBody = readResourceFile("response.xml");
        final String relativePath = "/sap/bc/srt/scs_ext/sap/7FCXL_GET_PROGRAM_TEXTS";
        final int NUMBER_OF_ITEMS_IN_RESPONSE = 985;

        stubFor(post(urlEqualTo(relativePath)).willReturn(okXml(responseBody)));

        final Destination destination = DefaultDestination.builder().property("URL", wireMockServer.baseUrl()).build();

        final RfmRequestResult result = new DataCollectionProgramTextsRfm().execute(destination);

        final List<ProgramText> programTextList =
            result.get("E_PROGRAM_TEXTS").getAsCollection().asList(ProgramText.class);

        assertThat(programTextList).hasSize(NUMBER_OF_ITEMS_IN_RESPONSE);
    }
}
