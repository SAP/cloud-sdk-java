package com.sap.cloud.sdk.datamodel.odata.utility;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class NamingUtilsTest
{
    private static Stream<Arguments> getClassTestCases()
    {
        return Stream
            .of(
                // \u2013 is an 'en dash'
                Arguments.of("Something with special \u2013 unicode stuff", "SomethingWithSpecialUnicodeStuff"),
                // \u2014 is an 'em dash'
                Arguments.of("Something with other \u2014 unicode stuff", "SomethingWithOtherUnicodeStuff"),
                Arguments.of("Something with an & symbol", "SomethingWithAnAndSymbol"),
                Arguments.of("Something with an / symbol", "SomethingWithAnOrSymbol"),
                Arguments.of("Northwind.svc", "NorthwindSvc"),
                Arguments.of("01 Some Service", "A01Some"),
                Arguments.of("01234", "A01234"),
                Arguments.of("Some,Enumeration", "SomeEnumeration"),
                Arguments.of("Lower,enumeration", "LowerEnumeration"),
                Arguments.of("KeepCamelCase", "KeepCamelCase"),
                Arguments.of("handle_snake_case", "HandleSnakeCase"),
                Arguments.of("OData Service for Business Partner", "BusinessPartner"));
    }

    @ParameterizedTest
    @MethodSource( "getClassTestCases" )
    void testServiceNameToJavaClassName( final String input, final String expectedOutput )
    {
        assertThat(NamingUtils.serviceNameToBaseJavaClassName(input)).isEqualTo(expectedOutput);
    }

    private static Stream<Arguments> getPackageTestCases()
    {
        return Stream
            .of(
                // \u2013 is an 'en dash'
                Arguments.of("Something with special \u2013 unicode stuff", "somethingwithspecialunicodestuff"),
                // \u2014 is an 'em dash'
                Arguments.of("Northwind.svc", "northwindsvc"),
                Arguments.of("01 Some Service", "a01some"),
                Arguments.of("01234", "a01234"),
                Arguments.of("Some,Enumeration", "someenumeration"),
                Arguments.of("Lower,enumeration", "lowerenumeration"),
                Arguments.of("KeepCamelCase", "keepcamelcase"),
                Arguments.of("handle_snake_case", "handlesnakecase"),
                Arguments.of("OData Service for Business Partner", "businesspartner"));
    }

    @ParameterizedTest
    @MethodSource( "getPackageTestCases" )
    void testServiceNameToJavaPackageName( final String input, final String expectedOutput )
    {
        assertThat(NamingUtils.serviceNameToJavaPackageName(input)).isEqualTo(expectedOutput);
    }

    @Test
    void testCapitalize()
    {
        assertThat(NamingUtils.capitalize("")).isEqualTo("");
        assertThat(NamingUtils.capitalize("all lower case words")).isEqualTo("All Lower Case Words");
        assertThat(NamingUtils.capitalize("ALL UPPER CASE WORDS")).isEqualTo("ALL UPPER CASE WORDS");
        assertThat(NamingUtils.capitalize("words-use-whitespace")).isEqualTo("Words-use-whitespace");
        assertThat(NamingUtils.capitalize("special chars / ? : \u2013 ; \""))
            .isEqualTo("Special Chars / ? : \u2013 ; \"");
        assertThat(NamingUtils.capitalize("multiple     whitespaces    are  kept"))
            .isEqualTo("Multiple     Whitespaces    Are  Kept");
        assertThat(NamingUtils.capitalize("different kinds\tof\nwhitespace"))
            .isEqualTo("Different Kinds\tOf\nWhitespace");
        assertThat(NamingUtils.capitalize(" whitespace as first")).isEqualTo(" Whitespace As First");
        assertThat(NamingUtils.capitalize("whitespace as last ")).isEqualTo("Whitespace As Last ");
        assertThat(NamingUtils.capitalize("last word is character a")).isEqualTo("Last Word Is Character A");
    }

    @Test
    void testUncapitalize()
    {
        assertThat(NamingUtils.uncapitalize("")).isEqualTo("");
        assertThat(NamingUtils.uncapitalize("all lower case words")).isEqualTo("all lower case words");
        assertThat(NamingUtils.uncapitalize("ALL UPPER CASE WORDS")).isEqualTo("aLL uPPER cASE wORDS");
        assertThat(NamingUtils.uncapitalize("Words-Use-Whitespace")).isEqualTo("words-Use-Whitespace");
        assertThat(NamingUtils.uncapitalize("Special Chars / ? : \u2013 ; \""))
            .isEqualTo("special chars / ? : \u2013 ; \"");
        assertThat(NamingUtils.uncapitalize("Multiple     Whitespaces    Are  Kept"))
            .isEqualTo("multiple     whitespaces    are  kept");
        assertThat(NamingUtils.uncapitalize("Different Kinds\tOf\nWhitespace"))
            .isEqualTo("different kinds\tof\nwhitespace");
        assertThat(NamingUtils.uncapitalize(" Whitespace As First")).isEqualTo(" whitespace as first");
        assertThat(NamingUtils.uncapitalize("Whitespace As Last ")).isEqualTo("whitespace as last ");
        assertThat(NamingUtils.uncapitalize("Last Word Is Character A")).isEqualTo("last word is character a");
    }
}
