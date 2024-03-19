package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.MultiUrlPropertySupplier.REMOVE_PATH;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.junit.jupiter.api.Test;

class MultiUrlPropertySupplierTest
{
    @Test
    void testRemovePath()
    {
        assertThat(REMOVE_PATH.apply(URI.create("https://user:pass@foo.bar/baz")))
            .hasToString("https://user:pass@foo.bar/");
        assertThat(REMOVE_PATH.apply(URI.create("https://foo.bar/baz?$select=oof"))).hasToString("https://foo.bar/");
        assertThat(REMOVE_PATH.apply(URI.create("https://foo.bar"))).hasToString("https://foo.bar/");
        assertThat(REMOVE_PATH.apply(URI.create("https://foo.bar/"))).hasToString("https://foo.bar/");
        assertThat(REMOVE_PATH.apply(URI.create("https://foo.bar?$select=oof"))).hasToString("https://foo.bar/");
    }

}
