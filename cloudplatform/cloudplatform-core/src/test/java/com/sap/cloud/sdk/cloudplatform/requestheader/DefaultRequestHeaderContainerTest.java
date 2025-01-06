/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.requestheader;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class DefaultRequestHeaderContainerTest
{
    @Test
    void creation()
    {
        final Map<String, Collection<String>> input = new HashMap<>();
        input.put("Key 1", Arrays.asList("Value 1-1", "Value 1-2"));
        input.put("Key 2", Collections.singletonList("Value 2-1"));

        final RequestHeaderContainer sut = DefaultRequestHeaderContainer.fromMultiValueMap(input);

        assertThat(sut.containsHeader("Key 1")).isTrue();
        assertThat(sut.containsHeader("Key 2")).isTrue();
        assertThat(sut.getHeaderValues("Key 1")).containsExactlyInAnyOrder("Value 1-1", "Value 1-2");
        assertThat(sut.getHeaderValues("Key 2")).containsExactlyInAnyOrder("Value 2-1");
        assertThat(sut).hasToString("DefaultRequestHeaderContainer(headerNames=[key 1, key 2])");
    }

    @Test
    void creationTransformsKeysToLowerCase()
    {
        final Map<String, Collection<String>> input = new HashMap<>();
        input.put("Key 1", Arrays.asList("Value 1-1", "Value 1-2"));
        input.put("Key 2", Collections.singletonList("Value 2-1"));

        final RequestHeaderContainer sut = DefaultRequestHeaderContainer.fromMultiValueMap(input);
        assertThat(sut.getHeaderNames()).containsExactlyInAnyOrder("key 1", "key 2");
    }

    @Test
    void creationCombinesValuesForSameKeys()
    {
        final Map<String, Collection<String>> input = new HashMap<>();
        input.put("Key 1", Arrays.asList("Value 1-1", "Value 1-2"));
        input.put("key 1", Collections.singletonList("Value 2-1"));

        final RequestHeaderContainer sut = DefaultRequestHeaderContainer.fromMultiValueMap(input);

        assertThat(sut.getHeaderNames()).containsExactlyInAnyOrder("key 1");
        assertThat(sut.getHeaderValues("Key 1")).containsExactlyInAnyOrder("Value 1-1", "Value 1-2", "Value 2-1");
    }

    @Test
    void creationCopiesValues()
    {
        final Map<String, Collection<String>> input = new HashMap<>();
        input.put("Key 1", Arrays.asList("Value 1-1", "Value 1-2"));

        final RequestHeaderContainer sut = DefaultRequestHeaderContainer.fromMultiValueMap(input);

        assertThat(sut.getHeaderNames()).containsExactlyInAnyOrder("key 1");

        // modify the input map
        input.put("Key 2", Collections.singletonList("Value 2-1"));

        // assert that the HeaderCollection is unmodified
        assertThat(sut.getHeaderNames()).containsExactlyInAnyOrder("key 1");
        assertThat(sut.containsHeader("Key 2")).isFalse();
    }

    @Test
    void creationRemovesNullAndEmptyValues()
    {
        final Map<String, Collection<String>> input = new HashMap<>();
        input.put("Key1", Arrays.asList("Value1", null));
        input.put("Key2", Collections.singletonList(null));
        input.put("Key3", Collections.emptyList());

        final RequestHeaderContainer sut = DefaultRequestHeaderContainer.fromMultiValueMap(input);

        assertThat(sut.getHeaderNames()).doesNotContain("key2", "key3").containsExactlyInAnyOrder("key1");
        assertThat(sut.getHeaderValues("Key1")).containsExactlyInAnyOrder("Value1");
        assertThat(sut.getHeaderValues("Key2")).isNotNull().isEmpty();
        assertThat(sut.getHeaderValues("Key3")).isNotNull().isEmpty();
        assertThat(sut.getHeaderValues("Key4")).isNotNull().isEmpty();
    }

    @Test
    void creationDoesNotSplitHeaderValues()
    {
        final Map<String, Collection<String>> input = new HashMap<>();
        input.put("Key", Arrays.asList("Value1", "Value2,Value3"));

        final RequestHeaderContainer sut = DefaultRequestHeaderContainer.fromMultiValueMap(input);

        assertThat(sut.getHeaderValues("Key")).containsExactlyInAnyOrder("Value1", "Value2,Value3");
    }

    @Test
    void creationDoesNotSplitCookieValues()
    {
        final Map<String, Collection<String>> input = new HashMap<>();
        input.put("Set-Cookie", Arrays.asList("cookie1=value1", "cookie2=value2;cookie3;"));

        final RequestHeaderContainer sut = DefaultRequestHeaderContainer.fromMultiValueMap(input);

        assertThat(sut.getHeaderValues("Set-Cookie"))
            .containsExactlyInAnyOrder("cookie1=value1", "cookie2=value2;cookie3;");
    }

    @Test
    void creationDoesNotTrimValues()
    {
        final Map<String, Collection<String>> input = new HashMap<>();
        input.put("Header1", Collections.singletonList("  Value1-1   "));
        input.put("Header2", Collections.singletonList("  Value2-1  ,  Value2-2   "));
        input.put("Set-Cookie", Collections.singletonList("  cookie1  = cookie-value1  "));
        input.put("set-cookie", Collections.singletonList("  cookie2 = cookie-value2  ; cookie3  "));

        final RequestHeaderContainer sut = DefaultRequestHeaderContainer.fromMultiValueMap(input);

        assertThat(sut.getHeaderValues("Header1")).containsExactlyInAnyOrder("  Value1-1   ");
        assertThat(sut.getHeaderValues("Header2")).containsExactlyInAnyOrder("  Value2-1  ,  Value2-2   ");
        assertThat(sut.getHeaderValues("Set-Cookie"))
            .containsExactlyInAnyOrder("  cookie1  = cookie-value1  ", "  cookie2 = cookie-value2  ; cookie3  ");
    }

    @Test
    void creationRemovesNullValues()
    {
        final Map<String, Collection<String>> input = new HashMap<>();
        input.put("Header", Arrays.asList("Value1-1", "", null, "   ", null));

        final RequestHeaderContainer sut = DefaultRequestHeaderContainer.fromMultiValueMap(input);

        assertThat(sut.getHeaderValues("Header")).containsExactlyInAnyOrder("Value1-1", "", "   ");
    }

    @Test
    void getHeaderValuesReturnsEmptyCollectionIfHeaderIsNotPresent()
    {
        final RequestHeaderContainer sut = DefaultRequestHeaderContainer.fromMultiValueMap(Collections.emptyMap());

        assertThat(sut.getHeaderNames()).isEmpty();
        assertThat(sut.containsHeader("Some Key")).isFalse();
        assertThat(sut.getHeaderValues("Some Key")).isEmpty();
    }

    @Test
    void getHeaderValuesIsCaseInsensitive()
    {
        final Map<String, String> input = Collections.singletonMap("Key", "Value");

        final RequestHeaderContainer sut = DefaultRequestHeaderContainer.fromSingleValueMap(input);

        assertThat(sut.getHeaderValues("Key")).containsExactlyInAnyOrder("Value");
        assertThat(sut.getHeaderValues("KEY")).containsExactlyInAnyOrder("Value");
        assertThat(sut.getHeaderValues("key")).containsExactlyInAnyOrder("Value");
        assertThat(sut.getHeaderValues("kEy")).containsExactlyInAnyOrder("Value");
    }

    @Test
    void containsHeaderIsCaseInsensitive()
    {
        final Map<String, String> input = Collections.singletonMap("Key", "Value");

        final RequestHeaderContainer sut = DefaultRequestHeaderContainer.fromSingleValueMap(input);

        assertThat(sut.containsHeader("Key")).isTrue();
        assertThat(sut.containsHeader("KEY")).isTrue();
        assertThat(sut.containsHeader("key")).isTrue();
        assertThat(sut.containsHeader("kEy")).isTrue();
    }
}
