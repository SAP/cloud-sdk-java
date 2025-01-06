package com.sap.cloud.sdk.datamodel.odata.generator;

import static com.sap.cloud.sdk.datamodel.odata.generator.NamingContextAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class NamingContextTest
{
    @Test
    void twoNamesSameLettersDifferentCase_AreNotEqual_AndNoRenamingHappens()
    {
        final NamingContext namingContext = new NamingContext(NamingContext.NameEqualityStrategy.CASE_SENSITIVE);
        assertThat(namingContext.ensureUniqueName("abc")).isEqualTo("abc");
        assertThat(namingContext.ensureUniqueName("ABC")).isEqualTo("ABC");
    }

    @Test
    void twoNamesSameLettersDifferentCase_AreEqual_AndRenamingHappens()
    {
        final NamingContext namingContext = new NamingContext(NamingContext.NameEqualityStrategy.CASE_INSENSITIVE);
        assertThat(namingContext.ensureUniqueName("abc")).isEqualTo("abc");
        assertThat(namingContext.ensureUniqueName("ABC")).isEqualTo("ABC_2");
    }

    @Test
    void allowsSameNameDifferentCaseWithDefaultStrategy()
    {
        final NamingContext namingContext = new NamingContext(NamingContext.NameEqualityStrategy.CASE_SENSITIVE);
        assertThat(namingContext.ensureUniqueName("eMailAddress")).isEqualTo("eMailAddress");
        assertThat(namingContext.ensureUniqueName("emailAddress")).isEqualTo("emailAddress");
    }

    @Test
    void returnsNewNameWithLowercaseStrategy()
    {
        final NamingContext namingContext = new NamingContext(new LowercaseNameFormattingStrategy());
        assertThat(namingContext.ensureUniqueName("eMailAddress")).isEqualTo("eMailAddress");
        assertThat(namingContext.ensureUniqueName("emailAddress")).isEqualTo("emailAddress_2");
        assertThat(namingContext.ensureUniqueName("emailAddress")).isEqualTo("emailAddress_3");
        assertThat(namingContext.ensureUniqueName("EMAILADDRESS")).isEqualTo("EMAILADDRESS_4");
    }

    @Test
    void loadGettersAndSettersOfClassAsAlreadyPresentFieldsOnTestClass()
    {
        final NamingContext namingContext = new NamingContext();
        namingContext.loadGettersAndSettersOfClassAsAlreadyPresentFields(TestClass.class);

        try( CustomSoftAssertions softly = new CustomSoftAssertions() ) {
            // test getter
            softly.assertThat(namingContext).contains("getterForSomePublicValue");
            softly.assertThat(namingContext).contains("getterForSomeProtectedValue");
            softly.assertThat(namingContext).contains("getterForSomePackagePrivateValue");
            softly.assertThat(namingContext).doesNotContain("getterForSomePrivateValue");

            // test setter
            softly.assertThat(namingContext).contains("setterForSomePublicValue");
            softly.assertThat(namingContext).contains("setterForSomeProtectedValue");
            softly.assertThat(namingContext).contains("setterForSomePackagePrivateValue");
            softly.assertThat(namingContext).doesNotContain("setterForSomePrivateValue");

            // test "untrue" getter/setter (more then just 'get'/'set' as a prefix)
            softly.assertThat(namingContext).doesNotContain("terInDisguise");

            softly.assertThat(namingContext).contains("class");
        }
    }

    @Test
    void loadKnownGeneratedFieldsLoads()
    {
        final NamingContext namingContext = new NamingContext();
        namingContext.loadKnownGeneratedFields();

        assertThat(namingContext).contains("allFields").contains("destinationForFetch");
    }

    private static class TestClass
    {
        public void getGetterForSomePublicValue()
        {
        }

        protected void getGetterForSomeProtectedValue()
        {
        }

        void getGetterForSomePackagePrivateValue()
        {
        }

        private void getGetterForSomePrivateValue()
        {
        }

        public void setSetterForSomePublicValue()
        {
        }

        protected void setSetterForSomeProtectedValue()
        {
        }

        void setSetterForSomePackagePrivateValue()
        {
        }

        private void setSetterForSomePrivateValue()
        {
        }

        public void getterInDisguise()
        {
        }

        public void setterInDisguise()
        {
        }
    }
}
