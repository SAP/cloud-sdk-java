package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import javax.annotation.Nonnull;

import com.google.common.base.CaseFormat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility class to translate ABAP names to SOAP names.
 */
@NoArgsConstructor( access = AccessLevel.PRIVATE )
class AbapToSoapNameConverter
{
    private static final int MAX_LENGTH_FUNCTION_NAME = 30;

    static String abapFunctionNameToSoapMessageName( @Nonnull String functionName )
    {
        if( functionName.startsWith("BAPI_") ) {
            functionName = functionName.substring(4);
        }
        return abapNameToSoapName(functionName);
    }

    static String abapParameterNameToSoapParameterName( @Nonnull final String abapName )
    {
        return abapNameToSoapName(abapName);
    }

    static String soapParameterNameToAbapParameterName( final String soapName )
    {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, soapName);
    }

    // FOREX_1 as ABAP name got transformed to Forex1 beforehand but SOAP runtime expects Forex_1
    // general rule seems to be to put underscore in front of a digit at the end
    private static String abapNameToSoapName( @Nonnull final String abapName )
    {
        final String soapName;
        final int indexOfLastUnderscore = abapName.lastIndexOf("_");

        if( indexOfLastUnderscore == -1 ) {
            soapName = transformUpperUnderScoreToUpperCamel(abapName);
        } else {
            final String abapNameRightOfLastUnderscore = abapName.substring(indexOfLastUnderscore + 1);

            if( isStringOfTypeNumber(abapNameRightOfLastUnderscore) ) {
                final String abapNameLeftOfLastUnderscore = abapName.substring(0, indexOfLastUnderscore);
                soapName =
                    transformUpperUnderScoreToUpperCamel(abapNameLeftOfLastUnderscore)
                        + "_"
                        + abapNameRightOfLastUnderscore;
            } else {
                soapName = transformUpperUnderScoreToUpperCamel(abapName);
            }
        }

        return soapName;
    }

    private static String transformUpperUnderScoreToUpperCamel( @Nonnull final String stringInUpperUnderscore )
    {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, stringInUpperUnderscore);
    }

    private static boolean isStringOfTypeNumber( @Nonnull final String s )
    {
        return s.matches("\\d+");
    }

    static String abapFunctionNameToSoapServiceName( @Nonnull final String functionName )
    {
        String soapServiceName = functionName;

        if( functionName.length() >= MAX_LENGTH_FUNCTION_NAME ) {
            final StringBuilder builder = new StringBuilder(functionName);
            builder.deleteCharAt(MAX_LENGTH_FUNCTION_NAME - 2);
            soapServiceName = builder.toString();
        }

        soapServiceName = soapServiceName.replaceAll("/", "7");
        return "7" + soapServiceName;
    }
}
