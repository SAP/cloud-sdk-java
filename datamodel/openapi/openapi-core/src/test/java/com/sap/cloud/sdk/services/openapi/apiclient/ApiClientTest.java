package com.sap.cloud.sdk.services.openapi.apiclient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

public class ApiClientTest
{

    @Test
    void testExtractHeadersMap()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.add("foo", "bar");
        headers.add("baz", "qux");
        headers.add("baz", "quux");
        assertThat(ApiClient.extractHeadersMap(headers))
            .containsExactly(entry("foo", List.of("bar")), entry("baz", List.of("qux", "quux")));
    }
}
