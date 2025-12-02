package com.sap.cloud.sdk.datamodel.odata.generator;

import static com.sap.cloud.sdk.datamodel.odata.generator.ODataToVdmGenerator.excludePatternMatch;

import java.io.File;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

public class ODataToVdmGeneratorTest
{
    @Test
    void testExcludePatternMatch()
    {
        // Get file-name only, without path
        final String fileName = new File("src/test/resources/sample/Spec.edmx").getName();

        final SoftAssertions softly = new SoftAssertions();
        for( final String pattern : new String[] { "*.edmx", "*.*", "sp*c.edmx", "*.EDMX" } )
            softly
                .assertThat(excludePatternMatch(pattern, fileName))
                .describedAs("%s matches %s", pattern, fileName)
                .isTrue();

        for( final String pattern : new String[] { "F", "*/*", "/resources/*/*.edmx", "/resources/*/*" } )
            softly
                .assertThat(excludePatternMatch(pattern, fileName))
                .describedAs("%s not matches %s", pattern, fileName)
                .isFalse();

        softly.assertThat(excludePatternMatch("A,B,spec.edmx", fileName)).describedAs("fallback").isTrue();
        softly.assertAll();
    }
}
