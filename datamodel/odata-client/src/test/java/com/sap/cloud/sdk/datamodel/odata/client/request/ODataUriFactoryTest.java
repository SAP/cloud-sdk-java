package com.sap.cloud.sdk.datamodel.odata.client.request;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sap.cloud.sdk.datamodel.odata.client.request.UriEncodingStrategy.REGULAR;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.collect.ImmutableMap;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;

import lombok.SneakyThrows;

class ODataUriFactoryTest
{
    private static final String servicePath = "/A_Service";
    private static final String entityName = "A_Entity";

    @Test
    void testDoubleSlashesInPath()
    {
        final String specialPath = "//" + servicePath + "////";
        final String expected = servicePath + "/" + entityName;

        final URI actual = ODataUriFactory.createAndEncodeUri(specialPath, entityName, "", REGULAR);
        assertThat(actual.toString()).isEqualTo(expected);
    }

    @Test
    void testSpecialCharactersInPath()
    {
        final String specialPath = "Ä$_?Se&rv iß%ë#";
        final String expected = "/%C3%84$_%3FSe&rv%20i%C3%9F%25%C3%AB%23/" + entityName;

        final URI actual = ODataUriFactory.createAndEncodeUri(specialPath, entityName, "", REGULAR);
        assertThat(actual.toString()).isEqualTo(expected);
    }

    @Test
    void testSafeCharactersInPath()
    {
        final String specialPath = "A_Service/-._~!$'()*,;&=@:+";
        final String entityPath = "B_entityPath/-._~!$'()*,;&=@:+";
        final String expected = "/A_Service/-._~!$'()*,;&=@:+/" + entityPath;

        final URI actual = ODataUriFactory.createAndEncodeUri(specialPath, entityPath, "", REGULAR);
        assertThat(actual.toString()).isEqualTo(expected);
    }

    @Test
    void testEmptyEntityPath()
    {
        final String expected = servicePath + "/";

        final URI actual = ODataUriFactory.createAndEncodeUri(servicePath, new ODataResourcePath(), null, REGULAR);
        assertThat(actual.toString()).isEqualTo(expected);
    }

    @Test
    void testSpecialCharactersInEntityPath()
    {
        final String specialEntityPath = "A$_?En&ti t%y#";
        final String expected = servicePath + "/A$_%3FEn&ti%20t%25y%23";

        final URI actual =
            ODataUriFactory.createAndEncodeUri(servicePath, ODataResourcePath.of(specialEntityPath), null, REGULAR);
        assertThat(actual.toString()).isEqualTo(expected);
    }

    @Test
    void testNoDoubleEncodingInQuery()
    {
        final String specialEntityPath = "A$_?En&ti t%y#";
        final String query =
            "$expand=BestFriend($expand=Trips($filter=contains(Name,'%25%20%24%26%23%3F%22%5C+''')))&$filter=contains(FirstName,'%25%20%24%26%23%3F%22%5C+''')";
        final String expected = servicePath + "/A$_%3FEn&ti%20t%25y%23" + "?" + query;

        final URI actual =
            ODataUriFactory
                .createAndEncodeUri(
                    servicePath,
                    ODataResourcePath.of(specialEntityPath).toEncodedPathString(),
                    query,
                    REGULAR);
        assertThat(actual.toString()).isEqualTo(expected);
    }

    @Test
    void testNoDoubleEncodingInParameter()
    {
        final String specialEntityPath = "A$_?En&ti t%y#";
        final String parameters = "('%25abc%2B')";
        final String expected = servicePath + "/A$_%3FEn&ti%20t%25y%23" + parameters;

        final ODataResourcePath path =
            ODataResourcePath.of(specialEntityPath, new ODataEntityKey(ODataProtocol.V4).addKeyProperty("key", "%abc+"));

        final URI actual = ODataUriFactory.createAndEncodeUri(servicePath, path, null, REGULAR);
        assertThat(actual.toString()).isEqualTo(expected);
    }

    @Test
    void testFilterWithForeignCharactersInQuery()
    {
        //Maps unencoded value to corresponding encoded value
        final ImmutableMap<String, String> encodingMap =
            ImmutableMap
                .of(
                    "Brontë",
                    "Bront%C3%AB",
                    "위키백과:대문",
                    "%EC%9C%84%ED%82%A4%EB%B0%B1%EA%B3%BC:%EB%8C%80%EB%AC%B8",
                    "Günter",
                    "G%C3%BCnter");

        encodingMap.forEach(( unencoded, encoded ) -> {
            final String unencodedFilterCondition = "(field-name eq '" + unencoded + "')";

            final String expectedEncodedFilterQueryParameter = "$filter=(field-name%20eq%20'" + encoded + "')";

            final String actualEncodedFilterQueryParameter =
                "$filter=" + (ODataUriFactory.encodeQuery(unencodedFilterCondition));

            assertThat(actualEncodedFilterQueryParameter).isEqualTo(expectedEncodedFilterQueryParameter);

            final URI resultUri =
                ODataUriFactory.createAndEncodeUri("service", "entity", actualEncodedFilterQueryParameter, REGULAR);

            assertThat(resultUri.getRawQuery()).isEqualTo(expectedEncodedFilterQueryParameter);
        });
    }

    @SneakyThrows
    @Test
    void testSpecialCharactersAgainstEndpoint()
    {
        final WireMockServer wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();

        final String query = "$filter=" + ODataUriFactory.encodeQuery("(Formula eq 'Foo +Bar')");
        final String subPath = ODataUriFactory.encodePath("sub-path/Entity(Key=123,Value='?')");

        final String rootPath = wireMockServer.url("root-path/");
        final URI uri = URI.create(rootPath).resolve(subPath + "?" + query);
        HttpClients.createDefault().execute(new HttpGet(uri));

        wireMockServer.stop();
        wireMockServer.verify(getRequestedFor(urlEqualTo("/root-path/sub-path/Entity(Key=123,Value='%3F')" + // escaped question mark
            "?$filter=(Formula%20eq%20'Foo%20%2BBar')")));
    }

    // Regression test for https://github.com/SAP/cloud-sdk/issues/741
    @Test
    void testPipeIsNotASafeQueryCharacter()
    {
        final String query = "(Name eq 'Version|1')";
        final String expectedEncodedQuery = "(Name%20eq%20'Version%7C1')";
        final String actualEncodedQuery = ODataUriFactory.encodeQuery(query);

        assertThat(actualEncodedQuery).isEqualTo(expectedEncodedQuery);

        final URI uri =
            ODataUriFactory.createAndEncodeUri(servicePath, entityName, "$filter=" + actualEncodedQuery, REGULAR);
        assertThat(uri.toString()).isEqualTo("/A_Service/A_Entity?$filter=" + expectedEncodedQuery);
    }
}
