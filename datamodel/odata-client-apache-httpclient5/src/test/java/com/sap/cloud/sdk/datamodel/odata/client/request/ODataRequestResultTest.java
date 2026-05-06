package com.sap.cloud.sdk.datamodel.odata.client.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.junit.jupiter.api.Test;

class ODataRequestResultTest
{
    @Test
    void testGetAllHeaderHeaderValuesRemovesNullValues()
    {
        final BasicClassicHttpResponse httpResponse =
            (BasicClassicHttpResponse) mockResponseWithHeaders(
                entry("Header", Arrays.asList("Value", null, "   ", null)));

        final ODataRequestResult sut = mock(ODataRequestResult.class);
        when(sut.getHttpResponse()).thenReturn(httpResponse);
        when(sut.getAllHeaderValues()).thenCallRealMethod();

        final Map<String, Iterable<String>> actual = sut.getAllHeaderValues();

        assertThat(actual).containsExactly(entry("Header", Arrays.asList("Value", "   ")));
    }

    @Test
    void testGetAllHeaderValuesDoesNotSplitValues()
    {
        final BasicClassicHttpResponse httpResponse =
            (BasicClassicHttpResponse) mockResponseWithHeaders(
                entry("Header", Collections.singletonList("Value1-1, Value1-2")));

        final ODataRequestResult sut = mock(ODataRequestResult.class);
        when(sut.getHttpResponse()).thenReturn(httpResponse);
        when(sut.getAllHeaderValues()).thenCallRealMethod();

        final Map<String, Iterable<String>> actual = sut.getAllHeaderValues();

        assertThat(actual).containsExactly(entry("Header", Collections.singletonList("Value1-1, Value1-2")));
    }

    @Test
    void testGetAllHeaderValuesDoesNotSplitCookieValues()
    {
        final BasicClassicHttpResponse httpResponse =
            (BasicClassicHttpResponse) mockResponseWithHeaders(
                entry("Set-Cookie", Collections.singletonList("Value1-1; Value1-2")));

        final ODataRequestResult sut = mock(ODataRequestResult.class);
        when(sut.getHttpResponse()).thenReturn(httpResponse);
        when(sut.getAllHeaderValues()).thenCallRealMethod();

        final Map<String, Iterable<String>> actual = sut.getAllHeaderValues();

        assertThat(actual).containsExactly(entry("Set-Cookie", Collections.singletonList("Value1-1; Value1-2")));
    }

    @Test
    void testGetAllHeaderValuesMergesNamesCaseInsensitively()
    {
        final BasicClassicHttpResponse httpResponse =
            (BasicClassicHttpResponse) mockResponseWithHeaders(
                entry("Header", Collections.singletonList("Value1-1")),
                entry("header", Collections.singletonList("Value1-2")));

        final ODataRequestResult sut = mock(ODataRequestResult.class);
        when(sut.getHttpResponse()).thenReturn(httpResponse);
        when(sut.getAllHeaderValues()).thenCallRealMethod();

        final Map<String, Iterable<String>> actual = sut.getAllHeaderValues();

        assertThat(actual).containsExactly(entry("Header", Arrays.asList("Value1-1", "Value1-2")));
    }

    @SafeVarargs
    @Nonnull
    private static BasicClassicHttpResponse mockResponseWithHeaders(
        @Nonnull final Map.Entry<String, Collection<String>>... entries )
    {
        final Collection<Header> headers = new ArrayList<>();
        for( final Map.Entry<String, Collection<String>> entry : entries ) {
            for( final String value : entry.getValue() ) {
                final Header header = mock(Header.class);
                when(header.getName()).thenReturn(entry.getKey());
                when(header.getValue()).thenReturn(value);

                headers.add(header);
            }
        }
        final BasicClassicHttpResponse response = mock(BasicClassicHttpResponse.class);
        when(response.getHeaders()).thenReturn(headers.toArray(new Header[0]));

        return response;
    }
}
