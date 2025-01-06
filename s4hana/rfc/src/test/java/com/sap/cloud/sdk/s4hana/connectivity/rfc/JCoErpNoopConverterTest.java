/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import lombok.Data;

@Deprecated
class JCoErpNoopConverterTest
{
    @Data
    private static class TestParameters
    {
        @Nonnull
        Number inputNumber;
        @Nonnull
        String expectedResult;
    }

    private com.sap.cloud.sdk.s4hana.connectivity.ErpTypeSerializer serializer;

    @BeforeEach
    void initialize()
    {
        serializer = JCoErpNoopConverter.overrideNumbers(new com.sap.cloud.sdk.s4hana.connectivity.ErpTypeSerializer());
    }

    @ParameterizedTest
    @MethodSource( "createTestNumbers" )
    void testSerialization( @Nonnull final Number inputNumber, @Nonnull final String expectedResult )
    {
        assertThat(serializer.toErp(inputNumber).get()).isEqualTo(expectedResult);
    }

    static List<Object[]> createTestNumbers()
    {
        return Arrays
            .asList(
                new Object[][] {
                    // integer
                    { 0, "0" },
                    { 42, "42" },
                    { -42, "-42" },
                    { 420000000, "420000000" },
                    { -420000000, "-420000000" },

                    // long
                    { 4200000000000l, "4200000000000" },
                    { -4200000000000l, "-4200000000000" },

                    // BigInteger
                    { new BigInteger("42000000000000000"), "42000000000000000" },
                    { new BigInteger("-42000000000000000"), "-42000000000000000" },

                    // float
                    { 0f, "0" },
                    { 42f, "42" },
                    { -42f, "-42" },
                    { 420000000f, "420000000" },
                    { -420000000f, "-420000000" },
                    { 0.5f, "0.5" },
                    { -0.5f, "-0.5" },
                    { 42.000f, "42" },
                    { -42.000f, "-42" },
                    { 420000000.000f, "420000000" },
                    { -420000000.000f, "-420000000" },
                    { 42.5f, "42.5" },
                    { -42.5f, "-42.5" },
                    { 420000000.00042f, "420000000" },
                    { -420000000.00042f, "-420000000" },

                    // double
                    { 0d, "0" },
                    { 42d, "42" },
                    { -42d, "-42" },
                    { 420000000d, "420000000" },
                    { -420000000d, "-420000000" },
                    { 0.00042d, "0.00042" },
                    { -0.00042d, "-0.00042" },
                    { 42.000d, "42" },
                    { -42.000d, "-42" },
                    { 420000000.000d, "420000000" },
                    { -420000000.000d, "-420000000" },
                    { 42.42d, "42.42" },
                    { -42.42d, "-42.42" },
                    { 420000000.00042d, "420000000.00042" },
                    { -420000000.00042d, "-420000000.00042" },

                    // BigDecimal
                    { new BigDecimal("420000000000.00000000042"), "420000000000.00000000042" },
                    { new BigDecimal("-420000000000.00000000042"), "-420000000000.00000000042" }, });
    }

}
