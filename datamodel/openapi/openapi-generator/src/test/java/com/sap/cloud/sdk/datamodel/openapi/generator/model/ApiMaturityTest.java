package com.sap.cloud.sdk.datamodel.openapi.generator.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ApiMaturityTest
{
    @Test
    void testParseString()
    {
        assertThat(ApiMaturity.getValueOrDefault("released")).isEqualTo(ApiMaturity.RELEASED);
        assertThat(ApiMaturity.getValueOrDefault("beta")).isEqualTo(ApiMaturity.BETA);
    }

    @Test
    void testUnspecifiedValueReturnsDefault()
    {
        assertThat(ApiMaturity.getValueOrDefault(null)).isEqualTo(ApiMaturity.DEFAULT);
    }

    @Test
    void testUnexpectedValueCausesException()
    {
        assertThatThrownBy(() -> ApiMaturity.getValueOrDefault("not-a-valid-maturity"))
            .isInstanceOf(IllegalArgumentException.class);
    }

}
