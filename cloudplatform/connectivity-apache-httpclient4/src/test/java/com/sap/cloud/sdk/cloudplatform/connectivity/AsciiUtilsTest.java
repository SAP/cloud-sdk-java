/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AsciiUtilsTest
{
    @Test
    void testRemoveNonPrintableCharacters()
    {
        final AsciiUtils asciiUtils = new AsciiUtils();

        assertThat(asciiUtils.removeNonPrintableCharacters("Some\nString")).isEqualTo("SomeString");
        assertThat(asciiUtils.removeNonPrintableCharacters("Some\rString")).isEqualTo("SomeString");
        assertThat(asciiUtils.removeNonPrintableCharacters("Some\tString")).isEqualTo("SomeString");
    }
}
