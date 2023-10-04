/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class AsciiUtilsTest
{
    @Test
    public void testRemoveNonPrintableCharacters()
    {
        final AsciiUtils asciiUtils = new AsciiUtils();

        assertThat(asciiUtils.removeNonPrintableCharacters("Some\nString")).isEqualTo("SomeString");
        assertThat(asciiUtils.removeNonPrintableCharacters("Some\rString")).isEqualTo("SomeString");
        assertThat(asciiUtils.removeNonPrintableCharacters("Some\tString")).isEqualTo("SomeString");
    }
}
