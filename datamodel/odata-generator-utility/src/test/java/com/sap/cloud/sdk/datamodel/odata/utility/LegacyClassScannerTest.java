
package com.sap.cloud.sdk.datamodel.odata.utility;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class LegacyClassScannerTest
{
    private static final LegacyClassScanner SCANNER = new LegacyClassScanner(new File("src/test/java"));
    private static final String CLASS_NAME = LegacyClass.class.getName();

    @Test
    void testDetermineArgumentsForConstructor()
    {
        final List<List<String>> argumentSets =
            SCANNER
                .determineArgumentsForConstructor(
                    CLASS_NAME,
                    Arrays.asList("a", "c", "q", "b"),
                    Function.identity(),
                    2);

        Assertions
            .assertThat(argumentSets)
            .containsExactly(Arrays.asList("a", "b", "c"), Arrays.asList("a", "b", "c", "q"));
    }

    @Test
    void testDetermineArgumentsForMethod()
    {
        final List<List<String>> argumentSets =
            SCANNER
                .determineArgumentsForMethod(
                    CLASS_NAME,
                    "testMethod",
                    Arrays.asList("a", "c", "q", "b"),
                    Function.identity());

        Assertions
            .assertThat(argumentSets)
            .containsExactly(Arrays.asList("a", "b", "c"), Arrays.asList("a", "b", "c", "q"));
    }

    @Test
    void testNoChange()
    {
        final List<List<String>> argumentSets =
            SCANNER
                .determineArgumentsForMethod(
                    CLASS_NAME,
                    "testMethod",
                    Arrays.asList("a", "c", "b"),
                    Function.identity());

        Assertions.assertThat(argumentSets).containsExactly(Arrays.asList("a", "b", "c"));
    }

    @Test
    void testErrorCase()
    {
        Assertions
            .assertThatIllegalStateException()
            .isThrownBy(
                () -> SCANNER
                    .determineArgumentsForMethod(
                        CLASS_NAME,
                        "testMethod",
                        Arrays.asList("a", "c", "q"),
                        Function.identity()))
            .havingCause()
            .withMessage("b");
    }
}
