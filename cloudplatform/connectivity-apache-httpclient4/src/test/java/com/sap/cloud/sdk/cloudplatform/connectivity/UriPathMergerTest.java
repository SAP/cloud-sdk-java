/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationPathsNotMergeableException;

class UriPathMergerTest
{
    private
        void
        testMerge( @Nonnull final String destination, @Nullable final String request, final URI expectedResult )
    {
        final URI uri = merge(destination, request);

        assertThat(uri).isEqualTo(expectedResult);
    }

    @Nonnull
    private URI merge( @Nonnull final String destination, @Nullable final String request )
    {
        return new UriPathMerger().merge(URI.create(destination), request != null ? URI.create(request) : null);
    }

    @Test
    void testMergeAbsolutePaths1()
    {
        testMerge(
            "http://foo.bar.example.com:1234/",
            "http://foo.bar.example.com:1234/foo/bar/baz/?sap-client=001&sap-language=en",
            URI.create("http://foo.bar.example.com:1234/foo/bar/baz/?sap-client=001&sap-language=en"));
    }

    @Test
    void testMergeAbsolutePaths2()
    {
        testMerge(
            "http://foo.bar.example.com:1234/foo/bar/",
            "http://foo.bar.example.com:1234/foo/bar/baz/",
            URI.create("http://foo.bar.example.com:1234/foo/bar/baz/"));
    }

    @Test
    void testMergeWithRequestAsRelativePathAndDifferentSlashes()
    {
        testMerge(
            "http://sap.com/foo/bar/",
            "sap-cloud-sdk?param=yes",
            URI.create("http://sap.com/foo/bar/sap-cloud-sdk?param=yes"));
        testMerge(
            "http://sap.com/foo/bar/",
            "/sap-cloud-sdk?param=yes",
            URI.create("http://sap.com/foo/bar/sap-cloud-sdk?param=yes"));
        testMerge(
            "http://sap.com/foo/bar",
            "/sap-cloud-sdk?param=yes",
            URI.create("http://sap.com/foo/bar/sap-cloud-sdk?param=yes"));
        testMerge(
            "http://sap.com/foo/bar",
            "sap-cloud-sdk?param=yes",
            URI.create("http://sap.com/foo/bar/sap-cloud-sdk?param=yes"));
    }

    @Test
    void testMergeWithRequestAsRelativePath()
    {
        testMerge(
            "http://localhost:60403/b2f9c7b0-a02d-4353-9d6f-70eac243eb78/",
            "some-xml.xml",
            URI.create("http://localhost:60403/b2f9c7b0-a02d-4353-9d6f-70eac243eb78/some-xml.xml"));
    }

    @Test
    void testMergeWithIPv6Request()
    {
        testMerge(
            "http://[2001:db8:3c4d:15::1a2f:1a2b]:60403/foo/",
            "some-xml.xml",
            URI.create("http://[2001:db8:3c4d:15::1a2f:1a2b]:60403/foo/some-xml.xml"));
    }

    @Test
    void testMergeWithODataRequestPath()
    {
        testMerge(
            "https://foo.bar.example.com:12345/foo/bar/baz/",
            "https://foo.bar.example.com:12345/foo/bar/BAZ?$filter=property%20ne%20''&$select=field1,field2&$format=json",
            URI
                .create(
                    "https://foo.bar.example.com:12345/foo/bar/BAZ?$filter=property%20ne%20''&$select=field1,field2&$format=json"));
    }

    @Test
    void testMergeWithSpecialCharacters()
    {
        final String encodedParameter = "%3A%2F%23%3F%26"; // encoded string ":/#?&"
        final String request = "/v01/Entity?$filter=Field%20ne%20'" + encodedParameter + "'";
        final String destinationUrl = "https://sap.com/odata/";
        final String mergedUrl = "https://sap.com/odata/v01/Entity?$filter=Field%20ne%20'" + encodedParameter + "'";
        testMerge(destinationUrl, request, URI.create(mergedUrl));
    }

    @Test
    void testMergeWithoutPath()
    {
        final String request = "https://sap.com";
        final String destinationUrl = "https://sap.com/odata/";
        final String mergedUrl = "https://sap.com/";
        testMerge(destinationUrl, request, URI.create(mergedUrl));
    }

    @Test
    void testMergeWithoutRequest()
    {
        testMerge(
            "https://user:foo@foo.bar.example.com:12345/foo/bar/baz/?foo=bar",
            null,
            URI.create("https://user:foo@foo.bar.example.com:12345/foo/bar/baz/?foo=bar"));
    }

    @Test
    void testMergeWithEncoding()
    {
        testMerge(
            "https://foo.example.com:1234",
            "https://foo.example.com:1234/odata/Entity(Product='ET%20BI%2021')?$format=json",
            URI.create("https://foo.example.com:1234/odata/Entity(Product='ET%20BI%2021')?$format=json"));

        testMerge(
            "http://foo.example.com:1234",
            "http://foo.example.com:1234/odata/EntityDescription(Language='de',Product='ET%2F%20BI')?$format=json",
            URI
                .create(
                    "http://foo.example.com:1234/odata/EntityDescription(Language='de',Product='ET%2F%20BI')?$format=json"));
    }

    @Test
    void testMergeWithSpecialCharactersInUrl()
    {
        final String request = "/v01/Entity?query=request%20whitespace#fragment%20request";
        final String destinationUrl = "https://destuser:dest%20passd@destination.host/destination%20path/";
        final String mergedUrl =
            "https://destuser:dest%20passd@destination.host/destination%20path/v01/Entity?query=request%20whitespace#fragment%20request";
        testMerge(destinationUrl, request, URI.create(mergedUrl));
    }

    @Test
    void testMergeWithEmptyDestinationUrl()
    {
        testMerge("", "https://some.url/with?parameter=foo", URI.create("https://some.url/with?parameter=foo"));
    }

    @Test
    void testDestinationMayOnlyBeEmptyIfRequestIsAbsolute()
    {
        assertThatThrownBy(() -> merge("", "/this/is/not/absolute"))
            .isExactlyInstanceOf(DestinationPathsNotMergeableException.class);
    }

    @Test
    void testDestinationMustBeAbsoluteIfNotEmpty()
    {
        assertThatThrownBy(() -> merge("/relative/path", "https://some.url/with?parameter=foo"))
            .isExactlyInstanceOf(DestinationPathsNotMergeableException.class);
    }
}
