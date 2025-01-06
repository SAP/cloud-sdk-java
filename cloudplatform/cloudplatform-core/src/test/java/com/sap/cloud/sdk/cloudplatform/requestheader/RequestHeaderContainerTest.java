package com.sap.cloud.sdk.cloudplatform.requestheader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Maps;

class RequestHeaderContainerTest
{
    @Test
    void testEmptyDoesNotContainAnyHeaders()
    {
        assertThat(RequestHeaderContainer.EMPTY.getHeaderNames()).isEmpty();
    }

    @Test
    void testEmptyCanBeUsedToConstructADefaultRequestHeaderContainer()
    {
        final RequestHeaderContainer.Builder sut = RequestHeaderContainer.EMPTY.toBuilder();

        assertThat(sut).isExactlyInstanceOf(DefaultRequestHeaderContainer.Builder.class);

        // assert the builder is actually empty by default
        final RequestHeaderContainer emptyHeaders = sut.build();
        assertThat(emptyHeaders).isExactlyInstanceOf(DefaultRequestHeaderContainer.class);
        assertThat(emptyHeaders.getHeaderNames()).isEmpty();

        // assert that the builder can be modified
        final RequestHeaderContainer customHeaders = sut.withHeader("x-custom-header", "custom value").build();
        assertThat(customHeaders.getHeaderNames()).containsExactlyInAnyOrder("x-custom-header");
        assertThat(customHeaders.getHeaderValues("x-custom-header")).containsExactlyInAnyOrder("custom value");
    }

    @Test
    void testContainerToMap()
    {
        final RequestHeaderContainer container =
            RequestHeaderContainer.EMPTY
                .toBuilder()
                .withHeader("foo", "bar", "baz")
                .withHeader("yin", "yang")
                .withHeader("empty", Collections.emptyList())
                .build();

        // show-case: translate container to map
        final Map<String, Collection<String>> map = Maps.toMap(container.getHeaderNames(), container::getHeaderValues);

        assertThat(map)
            .containsOnly(entry("foo", Arrays.asList("bar", "baz")), entry("yin", Collections.singletonList("yang")));
    }
}
