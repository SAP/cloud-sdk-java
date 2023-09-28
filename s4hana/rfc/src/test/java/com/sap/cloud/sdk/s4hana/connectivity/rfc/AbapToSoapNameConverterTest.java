package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

@Deprecated
public class AbapToSoapNameConverterTest
{
    @Test
    public void testTransformAbapFieldNameToSoapName()
    {
        final String abapFieldName1 = "FIELD";
        final String abapFieldName2 = "FIEL_D";
        final String abapFieldName3 = "FIELD_3";

        final String actualSoapName1 = AbapToSoapNameConverter.abapParameterNameToSoapParameterName(abapFieldName1);
        final String actualSoapName2 = AbapToSoapNameConverter.abapParameterNameToSoapParameterName(abapFieldName2);
        final String actualSoapName3 = AbapToSoapNameConverter.abapParameterNameToSoapParameterName(abapFieldName3);

        final String expectedSoapName1 = "Field";
        final String expectedSoapName2 = "FielD";
        final String expectedSoapName3 = "Field_3";

        assertThat(actualSoapName1).isEqualTo(expectedSoapName1);
        assertThat(actualSoapName2).isEqualTo(expectedSoapName2);
        assertThat(actualSoapName3).isEqualTo(expectedSoapName3);
    }

    @Test
    public void testTransformFunctionNameToSoapMessageName()
    {
        final String functionName1 = "BAPI_FUNCTION_FIND";
        final String functionName2 = "BAPI_FUN_FUN_FUNCTION_CHECK";

        final String actualSoapMessage1 = AbapToSoapNameConverter.abapFunctionNameToSoapMessageName(functionName1);
        final String actualSoapMessage2 = AbapToSoapNameConverter.abapFunctionNameToSoapMessageName(functionName2);

        final String expectedSoapMessage1 = "FunctionFind";
        final String expectedSoapMessage2 = "FunFunFunctionCheck";

        assertThat(actualSoapMessage1).isEqualTo(expectedSoapMessage1);
        assertThat(actualSoapMessage2).isEqualTo(expectedSoapMessage2);
    }

    @Test
    public void testTransformShortAbapFunctionNameToSoapServiceName()
    {
        final String functionName1 = "UKM_FOO_BAR";
        final String functionName2 = "BAPI_FOO_BAR";
        final String functionName3 = "FC_FOO_BAR";

        final String actualSoapServiceName1 = AbapToSoapNameConverter.abapFunctionNameToSoapServiceName(functionName1);
        final String actualSoapServiceName2 = AbapToSoapNameConverter.abapFunctionNameToSoapServiceName(functionName2);
        final String actualSoapServiceName3 = AbapToSoapNameConverter.abapFunctionNameToSoapServiceName(functionName3);

        final String expectedSoapServiceName1 = "7UKM_FOO_BAR";
        final String expectedSoapServiceName2 = "7BAPI_FOO_BAR";
        final String expectedSoapServiceName3 = "7FC_FOO_BAR";

        assertThat(actualSoapServiceName1).isEqualTo(expectedSoapServiceName1);
        assertThat(actualSoapServiceName2).isEqualTo(expectedSoapServiceName2);
        assertThat(actualSoapServiceName3).isEqualTo(expectedSoapServiceName3);
    }

    @Test
    public void testTransformLongAbapFunctionNameToSoapServiceName()
    {
        final String functionName1 = "BAPI_LOOOOOOOOONG_NAAAAAAAAME";
        final String functionName2 = "FCXL_LOOOOOOOOONG_NAAAAAAAAME";

        final String actualSoapServiceName1 = AbapToSoapNameConverter.abapFunctionNameToSoapServiceName(functionName1);
        final String actualSoapServiceName2 = AbapToSoapNameConverter.abapFunctionNameToSoapServiceName(functionName2);

        final String expectedSoapServiceName1 = "7BAPI_LOOOOOOOOONG_NAAAAAAAAME";
        final String expectedSoapServiceName2 = "7FCXL_LOOOOOOOOONG_NAAAAAAAAME";

        assertThat(actualSoapServiceName1).isEqualTo(expectedSoapServiceName1);
        assertThat(actualSoapServiceName2).isEqualTo(expectedSoapServiceName2);
    }
}
