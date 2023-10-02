/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.openapi.generator.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class ApiMaturityTest
{
    @Test
    public void testParseString()
    {
        assertThat(ApiMaturity.getValueOrDefault("released")).isEqualTo(ApiMaturity.RELEASED);
        assertThat(ApiMaturity.getValueOrDefault("beta")).isEqualTo(ApiMaturity.BETA);
    }

    @Test
    public void testUnspecifiedValueReturnsDefault()
    {
        assertThat(ApiMaturity.getValueOrDefault(null)).isEqualTo(ApiMaturity.DEFAULT);
    }

    @Test
    public void testUnexpectedValueCausesException()
    {
        assertThatThrownBy(() -> ApiMaturity.getValueOrDefault("not-a-valid-maturity"))
            .isInstanceOf(IllegalArgumentException.class);
    }

}
