/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.requestheader;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class DefaultRequestHeaderContainerBuilderTest
{
    @Test
    public void testBuildFromScratch()
    {
        final RequestHeaderContainer.Builder sut = DefaultRequestHeaderContainer.builder();

        final RequestHeaderContainer headers =
            sut
                .withHeader("Header1", "Value1-1")
                .withHeader("Header2", "Value2-1", "Value2-2")
                .withHeader("Header3", "Value3-1")
                .withHeader("header3", "Value3-2")
                .withHeader("Set-Cookie", "Cookie1=CookieValue1-1")
                .withHeader("set-cookie", "Cookie2")
                .withHeader("Header4", Collections.emptyList()) //Isn't added to the RequestHeaderContainer
                .build();

        assertThat(headers.getHeaderNames()).containsExactlyInAnyOrder("header1", "header2", "header3", "set-cookie");
        assertThat(headers.getHeaderValues("Header1")).containsExactlyInAnyOrder("Value1-1");
        assertThat(headers.getHeaderValues("Header2")).containsExactlyInAnyOrder("Value2-1", "Value2-2");
        assertThat(headers.getHeaderValues("Header3")).containsExactlyInAnyOrder("Value3-1", "Value3-2");
        assertThat(headers.getHeaderValues("Set-Cookie"))
            .containsExactlyInAnyOrder("Cookie1=CookieValue1-1", "Cookie2");
    }

    @Test
    public void testCopyAllHeaders()
    {
        final Map<String, Collection<String>> rawHeaders = new HashMap<>();
        rawHeaders.put("Header1", Collections.singletonList("Value1-1"));
        rawHeaders.put("Header2", Arrays.asList("Value2-1", "Value2-2"));
        rawHeaders.put("Set-Cookie", Arrays.asList("Cookie1=CookieValue1-1", "Cookie2"));

        final RequestHeaderContainer existingHeaders = DefaultRequestHeaderContainer.fromMultiValueMap(rawHeaders);

        final DefaultRequestHeaderContainer.Builder sut = DefaultRequestHeaderContainer.builder();

        final RequestHeaderContainer newHeaders = sut.withHeaders(existingHeaders).build();

        assertThat(newHeaders.getHeaderNames()).containsExactlyInAnyOrder("header1", "header2", "set-cookie");
        assertThat(newHeaders.getHeaderValues("Header1")).containsExactlyInAnyOrder("Value1-1");
        assertThat(newHeaders.getHeaderValues("Header2")).containsExactlyInAnyOrder("Value2-1", "Value2-2");
    }

    @Test
    public void testAddNewHeader()
    {
        final Map<String, Collection<String>> rawHeaders = new HashMap<>();
        rawHeaders.put("Header1", Collections.singletonList("Value1-1"));
        rawHeaders.put("Header2", Arrays.asList("Value2-1", "Value2-2"));
        rawHeaders.put("Set-Cookie", Arrays.asList("Cookie1=CookieValue1-1", "Cookie2"));

        final RequestHeaderContainer existingHeaders = DefaultRequestHeaderContainer.fromMultiValueMap(rawHeaders);

        final RequestHeaderContainer newHeaders = existingHeaders.toBuilder().withHeader("Header3", "Value3-1").build();

        assertThat(newHeaders.getHeaderNames())
            .containsExactlyInAnyOrder("header1", "header2", "set-cookie", "header3");
        assertThat(newHeaders.getHeaderValues("Header1")).containsExactlyInAnyOrder("Value1-1");
        assertThat(newHeaders.getHeaderValues("Header2")).containsExactlyInAnyOrder("Value2-1", "Value2-2");
        assertThat(newHeaders.getHeaderValues("Header3")).containsExactlyInAnyOrder("Value3-1");
        assertThat(newHeaders.getHeaderValues("Set-Cookie"))
            .containsExactlyInAnyOrder("Cookie1=CookieValue1-1", "Cookie2");
    }

    @Test
    public void testAddNewValue()
    {
        final Map<String, Collection<String>> rawHeaders = new HashMap<>();
        rawHeaders.put("Header1", Collections.singletonList("Value1-1"));
        rawHeaders.put("Header2", Arrays.asList("Value2-1", "Value2-2"));
        rawHeaders.put("Set-Cookie", Arrays.asList("Cookie1=CookieValue1-1", "Cookie2"));

        final RequestHeaderContainer existingHeaders = DefaultRequestHeaderContainer.fromMultiValueMap(rawHeaders);

        final RequestHeaderContainer newHeaders = existingHeaders.toBuilder().withHeader("Header1", "Value1-2").build();

        assertThat(newHeaders.getHeaderNames()).containsExactlyInAnyOrder("header1", "header2", "set-cookie");
        assertThat(newHeaders.getHeaderValues("Header1")).containsExactlyInAnyOrder("Value1-1", "Value1-2");
        assertThat(newHeaders.getHeaderValues("Header2")).containsExactlyInAnyOrder("Value2-1", "Value2-2");
        assertThat(newHeaders.getHeaderValues("Set-Cookie"))
            .containsExactlyInAnyOrder("Cookie1=CookieValue1-1", "Cookie2");
    }

    @Test
    public void testRemoveHeader()
    {
        final Map<String, Collection<String>> rawHeaders = new HashMap<>();
        rawHeaders.put("Header1", Collections.singletonList("Value1-1"));
        rawHeaders.put("Header2", Arrays.asList("Value2-1", "Value2-2"));
        rawHeaders.put("Set-Cookie", Arrays.asList("Cookie1=CookieValue1-1", "Cookie2"));

        final RequestHeaderContainer existingHeaders = DefaultRequestHeaderContainer.fromMultiValueMap(rawHeaders);

        final RequestHeaderContainer newHeaders = existingHeaders.toBuilder().withoutHeader("Header2").build();

        assertThat(newHeaders.getHeaderNames()).containsExactlyInAnyOrder("header1", "set-cookie");
        assertThat(newHeaders.getHeaderValues("Header1")).containsExactlyInAnyOrder("Value1-1");
        assertThat(newHeaders.getHeaderValues("Set-Cookie"))
            .containsExactlyInAnyOrder("Cookie1=CookieValue1-1", "Cookie2");
    }

    @Test
    public void testRemoveHeaderIsCaseInsensitive()
    {
        final Map<String, Collection<String>> rawHeaders = new HashMap<>();
        rawHeaders.put("Header1", Collections.singletonList("Value1-1"));
        rawHeaders.put("Header2", Arrays.asList("Value2-1", "Value2-2"));
        rawHeaders.put("Set-Cookie", Arrays.asList("Cookie1=CookieValue1-1", "Cookie2"));

        final RequestHeaderContainer existingHeaders = DefaultRequestHeaderContainer.fromMultiValueMap(rawHeaders);

        final RequestHeaderContainer newHeaders = existingHeaders.toBuilder().withoutHeader("header2").build();

        assertThat(newHeaders.getHeaderNames()).containsExactlyInAnyOrder("header1", "set-cookie");
        assertThat(newHeaders.getHeaderValues("Header1")).containsExactlyInAnyOrder("Value1-1");
        assertThat(newHeaders.getHeaderValues("Set-Cookie"))
            .containsExactlyInAnyOrder("Cookie1=CookieValue1-1", "Cookie2");
    }

    @Test
    public void testReplaceHeader()
    {
        final Map<String, Collection<String>> rawHeaders = new HashMap<>();
        rawHeaders.put("Header1", Collections.singletonList("Value1-1"));
        rawHeaders.put("Header2", Arrays.asList("Value2-1", "Value2-2"));
        rawHeaders.put("Set-Cookie", Arrays.asList("Cookie1=CookieValue1-1", "Cookie2"));
        rawHeaders.put("Header3", Arrays.asList("Value3-1"));

        final RequestHeaderContainer existingHeaders = DefaultRequestHeaderContainer.fromMultiValueMap(rawHeaders);

        final RequestHeaderContainer newHeaders =
            existingHeaders
                .toBuilder()
                .replaceHeader("Header1", "Value1-2", "Value1-3")
                .replaceHeader("header3", Collections.emptyList())
                .build();

        assertThat(newHeaders.getHeaderNames()).containsExactlyInAnyOrder("header1", "header2", "set-cookie");
        assertThat(newHeaders.getHeaderValues("Header1")).containsExactlyInAnyOrder("Value1-2", "Value1-3");
        assertThat(newHeaders.getHeaderValues("Header2")).containsExactlyInAnyOrder("Value2-1", "Value2-2");
        assertThat(newHeaders.getHeaderValues("Set-Cookie"))
            .containsExactlyInAnyOrder("Cookie1=CookieValue1-1", "Cookie2");
    }

    @Test
    public void testClear()
    {
        final Map<String, Collection<String>> rawHeaders = new HashMap<>();
        rawHeaders.put("Header1", Collections.singletonList("Value1-1"));
        rawHeaders.put("Header2", Arrays.asList("Value2-1", "Value2-2"));
        rawHeaders.put("Set-Cookie", Arrays.asList("Cookie1=CookieValue1-1", "Cookie1=CookieValue1-2", "Cookie2"));

        final RequestHeaderContainer existingHeaders = DefaultRequestHeaderContainer.fromMultiValueMap(rawHeaders);

        final RequestHeaderContainer newHeaders = existingHeaders.toBuilder().clear().build();

        assertThat(newHeaders.getHeaderNames()).isEmpty();
    }
}
