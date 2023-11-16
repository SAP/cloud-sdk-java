/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.utility;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class S4HanaNamingStrategyTest
{
    @Test
    void testDefaultNameSourceIsLabel()
    {
        assertThat(new S4HanaNamingStrategy().getNameSource()).isEqualTo(NameSource.LABEL);
    }

    @Test
    void testGenerateJavaClassNameFromName()
    {
        final NamingStrategy sut = new S4HanaNamingStrategy(NameSource.NAME);

        assertThat(sut.generateJavaClassName("A_AddressEmailAddressType", null)).isEqualTo("AddressEmailAddress");
        assertThat(sut.generateJavaClassName("ComparisonResult", null)).isEqualTo("ComparisonResult");
        assertThat(sut.generateJavaClassName("C_TRIALBALANCEResult", null)).isEqualTo("TRIALBALANCEResult");
        assertThat(sut.generateJavaClassName("GLAccount", null)).isEqualTo("GLAccount");
        assertThat(sut.generateJavaClassName("I_BusObjectsType", null)).isEqualTo("BusObjects");
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaClassName("To_", null));
    }

    @Test
    void testSuffixAppendedWhenJavaClassNameIsKeyword()
    {
        final String suffix = "Entity";

        final NamingStrategy sut = new S4HanaNamingStrategy(NameSource.NAME);
        assertThat(sut.generateJavaClassName("Package", null)).isEqualTo("Package" + suffix);

        final NamingStrategy strategy = new S4HanaNamingStrategy(NameSource.LABEL);
        assertThat(sut.generateJavaClassName("Package", "case")).isEqualTo("Package" + suffix);
    }

    @Test
    void testSuffixAppendedWhenNavigationPropertyBuilderIsKeyword()
    {
        final String suffix = "Property";

        final NamingStrategy sut = new S4HanaNamingStrategy(NameSource.NAME);
        assertThat(sut.generateJavaBuilderMethodName("Package")).isEqualTo("package" + suffix);
    }

    @Test
    void testGenerateJavaClassNameFromLabel()
    {
        final String someEntityName = "SomeEntityName";
        final NamingStrategy sut = new S4HanaNamingStrategy(NameSource.LABEL);

        assertThat(sut.generateJavaClassName(someEntityName, "Some label")).isEqualTo("SomeLabel");
        assertThat(sut.generateJavaClassName(someEntityName, "")).isEqualTo(someEntityName);
        assertThat(sut.generateJavaClassName(someEntityName, null)).isEqualTo(someEntityName);
    }

    @ParameterizedTest( name = "{0} -> {1}" )
    @MethodSource( "provideEntityPrefixData" )
    void testRemoveEntityPrefixesInGenerateJavaClassName( final String name, final String expectedJavaName )
    {
        final NamingStrategy sut = new S4HanaNamingStrategy(NameSource.NAME);

        assertThat(sut.generateJavaClassName(name, null)).isEqualToIgnoringCase(expectedJavaName);
    }

    @ParameterizedTest( name = "{0} -> {1}" )
    @MethodSource( "provideEntityPrefixData" )
    void testKeepEntityPrefixesInGenerateJavaClassNameFromLabel( final String label, final String expectedJavaName )
    {
        final String someEntityName = "SomeEntityName";
        final NamingStrategy sut = new S4HanaNamingStrategy(NameSource.LABEL);

        assertThat(sut.generateJavaClassName(someEntityName, label)).isEqualToIgnoringCase(label);
    }

    @ParameterizedTest( name = "{0} -> {1}" )
    @MethodSource( "provideEntitySuffixData" )
    void testRemoveClassSuffixesInGenerateJavaClassName( final String name, final String expectedJavaName )
    {
        assertThat(new S4HanaNamingStrategy(NameSource.NAME).generateJavaClassName(name, null))
            .isEqualToIgnoringCase(expectedJavaName);
        assertThat(new S4HanaNamingStrategy(NameSource.LABEL).generateJavaClassName(name, null))
            .isEqualToIgnoringCase(expectedJavaName);
    }

    @Test
    void testGenerateJavaFieldNameFromName()
    {
        final NamingStrategy sut = new S4HanaNamingStrategy(NameSource.NAME);

        assertThat(sut.generateJavaFieldName("ABCIndicator", null)).isEqualTo("aBCIndicator");
        assertThat(sut.generateJavaFieldName("APARToleranceGroup", null)).isEqualTo("aPARToleranceGroup");
        assertThat(sut.generateJavaFieldName("AdditionalCurrency1", null)).isEqualTo("additionalCurrency1");
        assertThat(sut.generateJavaFieldName("AlternativeGLAccount_T", null)).isEqualTo("alternativeGLAccount_T");
        assertThat(sut.generateJavaFieldName("AvailabilityCheckType", null)).isEqualTo("availabilityCheckType");
        assertThat(sut.generateJavaFieldName("Cancel_ac", null)).isEqualTo("cancel_ac");
        assertThat(sut.generateJavaFieldName("CharacteristicsMetaDataUUID_Text", null))
            .isEqualTo("characteristicsMetaDataUUID_Text");
        assertThat(sut.generateJavaFieldName("Class", null)).isEqualTo("classProperty");
        assertThat(sut.generateJavaFieldName("StrtgBalAmtInFreeDfndCrcy1_E", null))
            .isEqualTo("strtgBalAmtInFreeDfndCrcy1_E");
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaFieldName("SAP_", null));
    }

    @Test
    void testGenerateJavaFieldNameFromLabel()
    {
        final String someEntityName = "someEntityName";
        final NamingStrategy sut = new S4HanaNamingStrategy(NameSource.LABEL);

        assertThat(sut.generateJavaFieldName(someEntityName, "Some label")).isEqualTo("someLabel");
    }

    @ParameterizedTest( name = "{0} -> {1}" )
    @MethodSource( "providePropertyPrefixData" )
    void testRemovePropertyPrefixesInGenerateJavaFieldName( final String name, final String expectedJavaName )
    {
        final NamingStrategy sut = new S4HanaNamingStrategy(NameSource.NAME);

        assertThat(sut.generateJavaFieldName(name, null)).isEqualToIgnoringCase(expectedJavaName);
    }

    @Test
    void testGenerateJavaConstantNameFromName()
    {
        final NamingStrategy sut = new S4HanaNamingStrategy(NameSource.NAME);

        assertThat(sut.generateJavaConstantName("APARToleranceGroup", null)).isEqualTo("APAR_TOLERANCE_GROUP");
        assertThat(sut.generateJavaConstantName("AccountAssignmentType", null)).isEqualTo("ACCOUNT_ASSIGNMENT_TYPE");
        assertThat(sut.generateJavaConstantName("ActivityTypeIsBlocked", null)).isEqualTo("ACTIVITY_TYPE_IS_BLOCKED");
        assertThat(sut.generateJavaConstantName("AdditionalCustomerGroup1", null))
            .isEqualTo("ADDITIONAL_CUSTOMER_GROUP1");
        assertThat(sut.generateJavaConstantName("Cancel_ac", null)).isEqualTo("CANCEL_AC");
        assertThat(sut.generateJavaConstantName("StrtgBalAmtInFreeDfndCrcy1_E", null))
            .isEqualTo("STRTG_BAL_AMT_IN_FREE_DFND_CRCY1_E");
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaConstantName("SAP_", null));
    }

    @Test
    void testGenerateJavaConstantNameFromLabel()
    {
        final String someEntityName = "someEntityName";
        final NamingStrategy sut = new S4HanaNamingStrategy(NameSource.LABEL);

        assertThat(sut.generateJavaConstantName(someEntityName, "Some label")).isEqualTo("SOME_LABEL");
    }

    @Test
    void testGenerateJavaNavigationPropertyFieldName()
    {
        final NamingStrategy sut = new S4HanaNamingStrategy(NameSource.NAME);

        assertThat(sut.generateJavaNavigationPropertyFieldName("CompanyCodeDetails")).isEqualTo("toCompanyCodeDetails");
        assertThat(sut.generateJavaNavigationPropertyFieldName("to_DocStatusVH")).isEqualTo("toDocStatusVH");
        assertThat(sut.generateJavaNavigationPropertyFieldName("to_DocStatusVH")).isEqualTo("toDocStatusVH");
        assertThat(sut.generateJavaNavigationPropertyFieldName("to_to_DocStatusVH")).isEqualTo("toDocStatusVH");
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaNavigationPropertyFieldName("SAP_"));
    }

    @Test
    void testGenerateJavaNavigationPropertyConstantName()
    {
        final NamingStrategy sut = new S4HanaNamingStrategy(NameSource.NAME);

        assertThat(sut.generateJavaNavigationPropertyConstantName("Results")).isEqualTo("TO_RESULTS");
        assertThat(sut.generateJavaNavigationPropertyConstantName("CompanyCodeDetails"))
            .isEqualTo("TO_COMPANY_CODE_DETAILS");
        assertThat(sut.generateJavaNavigationPropertyConstantName("to_RefBOM")).isEqualTo("TO_REF_BOM");
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaNavigationPropertyConstantName("SAP_"));
    }

    @Test
    void testGenerateJavaMethodName()
    {
        final NamingStrategy sut = new S4HanaNamingStrategy(NameSource.NAME);

        assertThat(sut.generateJavaMethodName("Results")).isEqualTo("results");
        assertThat(sut.generateJavaMethodName("CompanyCodeDetails")).isEqualTo("companyCodeDetails");
        assertThat(sut.generateJavaMethodName("to_RefBOM")).isEqualTo("refBOM");
        assertThat(sut.generateJavaMethodName("to_URLAddress")).isEqualTo("uRLAddress");
        assertThat(sut.generateJavaMethodName("to_Class")).isEqualTo("classObjects");
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaMethodName("SAP_"));
    }

    @ParameterizedTest( name = "{0} -> {1}" )
    @MethodSource( "providePropertyPrefixData" )
    void testRemovePropertyPrefixesInGenerateJavaMethodName( final String name, final String expectedJavaName )
    {
        final NamingStrategy sut = new S4HanaNamingStrategy(NameSource.NAME);

        assertThat(sut.generateJavaMethodName(name)).isEqualToIgnoringCase(expectedJavaName);
    }

    @Test
    void testGenerateJavaBuilderMethodName()
    {
        final NamingStrategy sut = new S4HanaNamingStrategy(NameSource.NAME);

        assertThat(sut.generateJavaBuilderMethodName("Results")).isEqualTo("results");
        assertThat(sut.generateJavaBuilderMethodName("CompanyCodeDetails")).isEqualTo("companyCodeDetails");
        assertThat(sut.generateJavaBuilderMethodName("to_RefBOM")).isEqualTo("refBOM");
        assertThat(sut.generateJavaBuilderMethodName("to_URLAddress")).isEqualTo("urlAddress");
        assertThat(sut.generateJavaBuilderMethodName("TO_URLAddress")).isEqualTo("urlAddress");
        assertThat(sut.generateJavaBuilderMethodName("toURLAddress")).isEqualTo("urlAddress");
        assertThat(sut.generateJavaBuilderMethodName("TOURLAddress")).isEqualTo("urlAddress");
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaBuilderMethodName("SAP_"));
    }

    @ParameterizedTest( name = "{0} -> {1}" )
    @MethodSource( "providePropertyPrefixData" )
    void testRemovePropertyPrefixesInGenerateJavaBuilderMethodName( final String name, final String expectedJavaName )
    {
        final NamingStrategy sut = new S4HanaNamingStrategy(NameSource.NAME);

        assertThat(sut.generateJavaBuilderMethodName(name)).isEqualToIgnoringCase(expectedJavaName);
    }

    @Test
    void testGenerateJavaOperationMethodNameFromName()
    {
        final NamingStrategy sut = new S4HanaNamingStrategy(NameSource.NAME);

        assertThat(sut.generateJavaOperationMethodName("Release", null)).isEqualTo("release");
        assertThat(sut.generateJavaOperationMethodName("BOMComparison", null)).isEqualTo("bOMComparison");
        assertThat(sut.generateJavaOperationMethodName("Class", null)).isEqualTo("classFunction");
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaOperationMethodName("To_", null));
    }

    @Test
    void testGenerateJavaOperationMethodNameFromLabel()
    {
        final String someEntityName = "someEntityName";
        final NamingStrategy sut = new S4HanaNamingStrategy(NameSource.LABEL);

        assertThat(sut.generateJavaOperationMethodName(someEntityName, "Some label")).isEqualTo("someLabel");
    }

    @ParameterizedTest( name = "{0} -> {1}" )
    @MethodSource( "provideEntityPrefixData" )
    void testRemoveEntityPrefixesInGenerateJavaOperationMethodName( final String name, final String expectedJavaName )
    {
        final NamingStrategy sut = new S4HanaNamingStrategy(NameSource.NAME);

        assertThat(sut.generateJavaOperationMethodName(name, null)).isEqualToIgnoringCase(expectedJavaName);
    }

    @Test
    void testGenerateJavaMethodParamNameFromName()
    {
        final NamingStrategy sut = new S4HanaNamingStrategy(NameSource.NAME);

        assertThat(sut.generateJavaMethodParameterName("Release", null)).isEqualTo("release");
        assertThat(sut.generateJavaMethodParameterName("BOMComparison", null)).isEqualTo("bOMComparison");
        assertThat(sut.generateJavaMethodParameterName("Class", null)).isEqualTo("classParameter");
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaMethodParameterName("SAP_", null));
    }

    @Test
    void testGenerateJavaMethodParamNameWithSpacesInLabels()
    {
        final String someEntityName = "someEntityName";
        final NamingStrategy sut = new S4HanaNamingStrategy(NameSource.LABEL);

        assertThat(sut.generateJavaMethodParameterName(someEntityName, "Some label")).isEqualTo("someLabel");
    }

    @ParameterizedTest( name = "{0} -> {1}" )
    @MethodSource( "providePropertyPrefixData" )
    void testRemovePropertyPrefixesInGenerateJavaMethodParameterName( final String name, final String expectedJavaName )
    {
        final NamingStrategy sut = new S4HanaNamingStrategy(NameSource.NAME);

        assertThat(sut.generateJavaMethodParameterName(name, null)).isEqualToIgnoringCase(expectedJavaName);
    }

    @Test
    void testGenerateJavaFluentHelperClassNameFromName()
    {
        final NamingStrategy sut = new S4HanaNamingStrategy(NameSource.NAME);

        assertThat(sut.generateJavaFluentHelperClassName("Release", null)).isEqualTo("ReleaseFluentHelper");
        assertThat(sut.generateJavaFluentHelperClassName("BOMComparison", null)).isEqualTo("BOMComparisonFluentHelper");
        assertThat(sut.generateJavaFluentHelperClassName("Class", null)).isEqualTo("ClassFluentHelper");
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaFluentHelperClassName("To_", null));
    }

    @Test
    void testGenerateJavaFluentHelperClassNameFromLabel()
    {
        final String someEntityName = "someEntityName";
        final NamingStrategy sut = new S4HanaNamingStrategy(NameSource.LABEL);

        assertThat(sut.generateJavaFluentHelperClassName(someEntityName, "Some label"))
            .isEqualTo("SomeLabelFluentHelper");
    }

    @ParameterizedTest( name = "{0} -> {1}" )
    @MethodSource( "provideEntityPrefixData" )
    void testRemoveEntityPrefixesInGenerateJavaFluentHelperClassName( final String name, final String expectedJavaName )
    {
        final NamingStrategy sut = new S4HanaNamingStrategy(NameSource.NAME);

        assertThat(sut.generateJavaFluentHelperClassName(name, null))
            .isEqualToIgnoringCase(expectedJavaName + "FluentHelper");
    }

    @ParameterizedTest( name = "{0} -> {1}" )
    @MethodSource( "provideEntityPrefixData" )
    void testKeepEntityPrefixesInGenerateJavaFluentHelperClassNameFromLabel(
        final String label,
        final String expectedJavaName )
    {
        final String someEntityName = "SomeEntityName";
        final NamingStrategy sut = new S4HanaNamingStrategy(NameSource.LABEL);

        assertThat(sut.generateJavaFluentHelperClassName(someEntityName, label))
            .isEqualToIgnoringCase(label + "FluentHelper");
    }

    @ParameterizedTest( name = "{0} -> {1}" )
    @MethodSource( "provideEntitySuffixData" )
    void testRemoveClassSuffixesInGenerateJavaFluentHelperClassName( final String name, final String expectedJavaName )
    {
        assertThat(new S4HanaNamingStrategy(NameSource.NAME).generateJavaFluentHelperClassName(name, null))
            .isEqualToIgnoringCase(expectedJavaName + "FluentHelper");
        assertThat(new S4HanaNamingStrategy(NameSource.LABEL).generateJavaFluentHelperClassName("SomeEntityName", name))
            .isEqualToIgnoringCase(expectedJavaName + "FluentHelper");
    }

    private static Stream<Arguments> provideEntityPrefixData()
    {
        return Stream
            .of(
                // name/label, expected name
                // remove prefix
                Arguments.of("A_SomeEntity", "SomeEntity"),
                Arguments.of("C_SomeEntity", "SomeEntity"),
                Arguments.of("D_SomeEntity", "SomeEntity"),
                Arguments.of("E_SomeEntity", "SomeEntity"),
                Arguments.of("I_SomeEntity", "SomeEntity"),
                Arguments.of("P_SomeEntity", "SomeEntity"),
                Arguments.of("R_SomeEntity", "SomeEntity"),
                Arguments.of("S_SomeEntity", "SomeEntity"),
                Arguments.of("to_SomeEntity", "SomeEntity"),
                Arguments.of("To_SomeEntity", "SomeEntity"),
                Arguments.of("X_SomeEntity", "SomeEntity"),
                Arguments.of("X_A_SomeEntity", "A_SomeEntity"),
                Arguments.of("YY1_SomeEntity", "SomeEntity"),
                Arguments.of("Z_SomeEntity", "SomeEntity"),
                // keep prefix
                Arguments.of("B_SomeEntity", "B_SomeEntity"),
                Arguments.of("SAP_SomeEntity", "SAP_SomeEntity"),
                Arguments.of("B_A_SomeEntity", "B_A_SomeEntity"));
    }

    private static Stream<Arguments> providePropertyPrefixData()
    {
        return Stream
            .of(
                // name/label, expected name
                // remove prefix
                Arguments.of("SAP_SomeEntity", "SomeEntity"),
                Arguments.of("to_SomeEntity", "SomeEntity"),
                Arguments.of("To_SomeEntity", "SomeEntity"),
                Arguments.of("YY1_SomeEntity", "SomeEntity"),
                // keep prefixes
                Arguments.of("A_SomeEntity", "A_SomeEntity"),
                Arguments.of("A_YY1_SomeEntity", "A_YY1_SomeEntity"));
    }

    private static Stream<Arguments> provideEntitySuffixData()
    {
        return Stream
            .of(
                // name/label, expected name
                // remove suffix
                Arguments.of("SomeEntityType", "SomeEntity"),
                Arguments.of("SomeEntity_", "SomeEntity"),
                Arguments.of("SomeEntity_Type", "SomeEntity"),
                // keep suffixes
                Arguments.of("SomeEntityType_", "SomeEntityType"));
    }
}
