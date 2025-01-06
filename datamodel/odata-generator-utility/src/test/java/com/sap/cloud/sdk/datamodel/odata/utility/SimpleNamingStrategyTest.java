/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.utility;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import org.junit.jupiter.api.Test;

class SimpleNamingStrategyTest
{
    @Test
    void testDefaultNameSourceIsLabel()
    {
        assertThat(new SimpleNamingStrategy().getNameSource()).isEqualTo(NameSource.LABEL);
    }

    @Test
    void testGenerateJavaClassNameFromName()
    {
        final SimpleNamingStrategy sut = new SimpleNamingStrategy(NameSource.NAME);

        assertThat(sut.generateJavaClassName("SimpleName", null)).isEqualTo("SimpleName");
        assertThat(sut.generateJavaClassName("_LeadingUnderscore", null)).isEqualTo("_LeadingUnderscore");
        assertThat(sut.generateJavaClassName("Type", null)).isEqualTo("Type");
        assertThat(sut.generateJavaClassName("class", null)).isEqualTo("Class");
        assertThat(sut.generateJavaClassName("URLAddress", null)).isEqualTo("UrlAddress");
        assertThat(sut.generateJavaClassName("C_TRIALBALANCEResult", null)).isEqualTo("C_TRIALBALANCEResult");
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaClassName("", null));
    }

    @Test
    void testGenerateJavaClassNameFromLabel()
    {
        final String someEntityName = "SomeEntityName";
        final SimpleNamingStrategy sut = new SimpleNamingStrategy(NameSource.LABEL);

        assertThat(sut.generateJavaClassName(someEntityName, "SimpleName")).isEqualTo("SimpleName");
        assertThat(sut.generateJavaClassName(someEntityName, "Simple Name")).isEqualTo("SimpleName");
        assertThat(sut.generateJavaClassName(someEntityName, "")).isEqualTo(someEntityName);
        assertThat(sut.generateJavaClassName(someEntityName, null)).isEqualTo(someEntityName);
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaClassName("", null));
    }

    @Test
    void testGenerateJavaFieldNameFromName()
    {
        final SimpleNamingStrategy sut = new SimpleNamingStrategy(NameSource.NAME);

        assertThat(sut.generateJavaFieldName("SimpleName", null)).isEqualTo("simpleName");
        assertThat(sut.generateJavaFieldName("_LeadingUnderscore", null)).isEqualTo("_LeadingUnderscore");
        assertThat(sut.generateJavaFieldName("Type", null)).isEqualTo("type");
        assertThat(sut.generateJavaFieldName("URLAddress", null)).isEqualTo("urlAddress");
        assertThat(sut.generateJavaFieldName("C_TRIALBALANCEResult", null)).isEqualTo("c_TRIALBALANCEResult");
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaFieldName("", null));
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaFieldName("class", null));
    }

    @Test
    void testGenerateJavaFieldNameFromLabel()
    {
        final String someFieldName = "someFieldName";
        final SimpleNamingStrategy sut = new SimpleNamingStrategy(NameSource.LABEL);

        assertThat(sut.generateJavaFieldName(someFieldName, "SimpleName")).isEqualTo("simpleName");
        assertThat(sut.generateJavaFieldName(someFieldName, "Simple Name")).isEqualTo("simpleName");
        assertThat(sut.generateJavaFieldName(someFieldName, "")).isEqualTo(someFieldName);
        assertThat(sut.generateJavaFieldName(someFieldName, null)).isEqualTo(someFieldName);
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaFieldName("", null));
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaFieldName("", ""));
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaFieldName(someFieldName, "class"));
    }

    @Test
    void testGenerateJavaConstantNameFromName()
    {
        final SimpleNamingStrategy sut = new SimpleNamingStrategy(NameSource.NAME);

        assertThat(sut.generateJavaConstantName("SimpleName", null)).isEqualTo("SIMPLE_NAME");
        assertThat(sut.generateJavaConstantName("_LeadingUnderscore", null)).isEqualTo("_LEADING_UNDERSCORE");
        assertThat(sut.generateJavaConstantName("Type", null)).isEqualTo("TYPE");
        assertThat(sut.generateJavaConstantName("class", null)).isEqualTo("CLASS");
        assertThat(sut.generateJavaConstantName("URLAddress", null)).isEqualTo("URL_ADDRESS");
        assertThat(sut.generateJavaConstantName("C_TRIALBALANCEResult", null)).isEqualTo("C_TRIALBALANCE_RESULT");
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaConstantName("", null));
    }

    @Test
    void testGenerateJavaConstantNameFromLabel()
    {
        final String someConstantName = "SOME_CONSTANT_NAME";
        final SimpleNamingStrategy sut = new SimpleNamingStrategy(NameSource.LABEL);

        assertThat(sut.generateJavaConstantName(someConstantName, "SimpleName")).isEqualTo("SIMPLE_NAME");
        assertThat(sut.generateJavaConstantName(someConstantName, "Simple Name")).isEqualTo("SIMPLE_NAME");
        assertThat(sut.generateJavaConstantName(someConstantName, "")).isEqualTo(someConstantName);
        assertThat(sut.generateJavaConstantName(someConstantName, null)).isEqualTo(someConstantName);
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaConstantName("", null));
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaConstantName("", ""));
    }

    @Test
    void testGenerateJavaNavigationPropertyFieldName()
    {
        final SimpleNamingStrategy sut = new SimpleNamingStrategy(NameSource.NAME);

        assertThat(sut.generateJavaNavigationPropertyFieldName("SimpleName")).isEqualTo("simpleName");
        assertThat(sut.generateJavaNavigationPropertyFieldName("_LeadingUnderscore")).isEqualTo("_LeadingUnderscore");
        assertThat(sut.generateJavaNavigationPropertyFieldName("Type")).isEqualTo("type");
        assertThat(sut.generateJavaNavigationPropertyFieldName("URLAddress")).isEqualTo("urlAddress");
        assertThat(sut.generateJavaNavigationPropertyFieldName("C_TRIALBALANCEResult"))
            .isEqualTo("c_TRIALBALANCEResult");
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaNavigationPropertyFieldName(""));
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaNavigationPropertyFieldName("class"));
    }

    @Test
    void testGenerateJavaNavigationPropertyConstantName()
    {
        final SimpleNamingStrategy sut = new SimpleNamingStrategy(NameSource.NAME);

        assertThat(sut.generateJavaNavigationPropertyConstantName("SimpleName")).isEqualTo("SIMPLE_NAME");
        assertThat(sut.generateJavaNavigationPropertyConstantName("_LeadingUnderscore"))
            .isEqualTo("_LEADING_UNDERSCORE");
        assertThat(sut.generateJavaNavigationPropertyConstantName("Type")).isEqualTo("TYPE");
        assertThat(sut.generateJavaNavigationPropertyConstantName("class")).isEqualTo("CLASS");
        assertThat(sut.generateJavaNavigationPropertyConstantName("URLAddress")).isEqualTo("URL_ADDRESS");
        assertThat(sut.generateJavaNavigationPropertyConstantName("C_TRIALBALANCEResult"))
            .isEqualTo("C_TRIALBALANCE_RESULT");
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaNavigationPropertyConstantName(""));
    }

    @Test
    void testGenerateJavaMethodName()
    {
        final SimpleNamingStrategy sut = new SimpleNamingStrategy(NameSource.NAME);

        assertThat(sut.generateJavaMethodName("SimpleName")).isEqualTo("simpleName");
        assertThat(sut.generateJavaMethodName("_LeadingUnderscore")).isEqualTo("_LeadingUnderscore");
        assertThat(sut.generateJavaMethodName("Type")).isEqualTo("type");
        assertThat(sut.generateJavaMethodName("URLAddress")).isEqualTo("urlAddress");
        assertThat(sut.generateJavaMethodName("C_TRIALBALANCEResult")).isEqualTo("c_TRIALBALANCEResult");
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaMethodName(""));
    }

    @Test
    void testGenerateJavaBuilderMethodName()
    {
        final SimpleNamingStrategy sut = new SimpleNamingStrategy(NameSource.NAME);

        assertThat(sut.generateJavaBuilderMethodName("SimpleName")).isEqualTo("simpleName");
        assertThat(sut.generateJavaBuilderMethodName("_LeadingUnderscore")).isEqualTo("_LeadingUnderscore");
        assertThat(sut.generateJavaBuilderMethodName("Type")).isEqualTo("type");
        assertThat(sut.generateJavaBuilderMethodName("URLAddress")).isEqualTo("urlAddress");
        assertThat(sut.generateJavaBuilderMethodName("C_TRIALBALANCEResult")).isEqualTo("c_TRIALBALANCEResult");
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaBuilderMethodName(""));
    }

    @Test
    void testGenerateJavaOperationMethodNameFromName()
    {
        final SimpleNamingStrategy sut = new SimpleNamingStrategy(NameSource.NAME);

        assertThat(sut.generateJavaOperationMethodName("SimpleName", null)).isEqualTo("simpleName");
        assertThat(sut.generateJavaOperationMethodName("_LeadingUnderscore", null)).isEqualTo("_LeadingUnderscore");
        assertThat(sut.generateJavaOperationMethodName("Type", null)).isEqualTo("type");
        assertThat(sut.generateJavaOperationMethodName("URLAddress", null)).isEqualTo("urlAddress");
        assertThat(sut.generateJavaOperationMethodName("C_TRIALBALANCEResult", null)).isEqualTo("c_TRIALBALANCEResult");
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaOperationMethodName("", null));
    }

    @Test
    void testGenerateJavaOperationMethodNameFromLabel()
    {
        final String someMethodName = "someMethodName";
        final SimpleNamingStrategy sut = new SimpleNamingStrategy(NameSource.LABEL);

        assertThat(sut.generateJavaOperationMethodName(someMethodName, "SimpleName")).isEqualTo("simpleName");
        assertThat(sut.generateJavaOperationMethodName(someMethodName, "Simple Name")).isEqualTo("simpleName");
        assertThat(sut.generateJavaOperationMethodName(someMethodName, "")).isEqualTo(someMethodName);
        assertThat(sut.generateJavaOperationMethodName(someMethodName, null)).isEqualTo(someMethodName);
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaOperationMethodName("", null));
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaOperationMethodName("", ""));
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaOperationMethodName("", "class"));
    }

    @Test
    void testGenerateJavaMethodParameterNameFromName()
    {
        final SimpleNamingStrategy sut = new SimpleNamingStrategy(NameSource.NAME);

        assertThat(sut.generateJavaMethodParameterName("SimpleName", null)).isEqualTo("simpleName");
        assertThat(sut.generateJavaMethodParameterName("_LeadingUnderscore", null)).isEqualTo("_LeadingUnderscore");
        assertThat(sut.generateJavaMethodParameterName("Type", null)).isEqualTo("type");
        assertThat(sut.generateJavaMethodParameterName("URLAddress", null)).isEqualTo("urlAddress");
        assertThat(sut.generateJavaMethodParameterName("C_TRIALBALANCEResult", null)).isEqualTo("c_TRIALBALANCEResult");
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaMethodParameterName("", null));
    }

    @Test
    void testGenerateJavaMethodParameterNameFromLabel()
    {
        final String someParameterName = "someParameterName";
        final SimpleNamingStrategy sut = new SimpleNamingStrategy(NameSource.LABEL);

        assertThat(sut.generateJavaMethodParameterName(someParameterName, "SimpleName")).isEqualTo("simpleName");
        assertThat(sut.generateJavaMethodParameterName(someParameterName, "Simple Name")).isEqualTo("simpleName");
        assertThat(sut.generateJavaMethodParameterName(someParameterName, "")).isEqualTo(someParameterName);
        assertThat(sut.generateJavaMethodParameterName(someParameterName, null)).isEqualTo(someParameterName);
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaMethodParameterName("", null));
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaMethodParameterName("", ""));
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaMethodParameterName("", "class"));
    }

    @Test
    void testGenerateJavaFluentHelperClassNameFromName()
    {
        final SimpleNamingStrategy sut = new SimpleNamingStrategy(NameSource.NAME);

        assertThat(sut.generateJavaFluentHelperClassName("SimpleName", null)).isEqualTo("SimpleName");
        assertThat(sut.generateJavaFluentHelperClassName("_LeadingUnderscore", null)).isEqualTo("_LeadingUnderscore");
        assertThat(sut.generateJavaFluentHelperClassName("Type", null)).isEqualTo("Type");
        assertThat(sut.generateJavaFluentHelperClassName("class", null)).isEqualTo("Class");
        assertThat(sut.generateJavaFluentHelperClassName("URLAddress", null)).isEqualTo("UrlAddress");
        assertThat(sut.generateJavaFluentHelperClassName("C_TRIALBALANCEResult", null))
            .isEqualTo("C_TRIALBALANCEResult");
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaFluentHelperClassName("", null));
    }

    @Test
    void testGenerateJavaFluentHelperClassNameFromLabel()
    {
        final String someEntityName = "SomeEntityName";
        final SimpleNamingStrategy sut = new SimpleNamingStrategy(NameSource.LABEL);

        assertThat(sut.generateJavaFluentHelperClassName(someEntityName, "SimpleName")).isEqualTo("SimpleName");
        assertThat(sut.generateJavaFluentHelperClassName(someEntityName, "Simple Name")).isEqualTo("SimpleName");
        assertThat(sut.generateJavaFluentHelperClassName(someEntityName, "")).isEqualTo(someEntityName);
        assertThat(sut.generateJavaFluentHelperClassName(someEntityName, null)).isEqualTo(someEntityName);
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaFluentHelperClassName("", null));
        assertThatIllegalStateException().isThrownBy(() -> sut.generateJavaFluentHelperClassName("", ""));
    }
}
