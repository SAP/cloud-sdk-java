package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DestinationOptionsTest
{
    @Test
    void testEmpty()
    {
        final DestinationOptions options = DestinationOptions.builder().build();
        assertThat(options).isNotNull();
        assertThat(options.get("foo")).isEmpty();
    }

    @Test
    void testEquality()
    {
        final DestinationOptions options1 = DestinationOptions.builder().parameterIfAbsent("foo", "bar").build();
        final DestinationOptions options2 = DestinationOptions.builder(options1).parameterIfAbsent("foo", 42).build();
        final DestinationOptions options3 =
            DestinationOptions.builder().parameter("foo", "bar").parameterIfAbsent("foo", 42).build();

        final DestinationOptions optionsX = DestinationOptions.builder().build();

        assertThat(options1).isEqualTo(options2);
        assertThat(options1).isEqualTo(options3);
        assertThat(options1).isNotEqualTo(optionsX);
    }
}
