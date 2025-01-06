package com.sap.cloud.sdk.s4hana.serialization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

@Deprecated
class ErpDecimalTest
{
    @Test
    void testErpDecimal()
    {
        {
            final ErpDecimal erpDecimal = new ErpDecimal(new BigDecimal(42));
            assertThat(erpDecimal.getValue()).isEqualTo(new BigDecimal(42));
        }

        {
            final ErpDecimal erpDecimal = new ErpDecimal("21");
            assertThat(erpDecimal.getValue()).isEqualTo(new BigDecimal(21));
        }

        {
            final ErpDecimal erpDecimal = new ErpDecimal("1e-5");
            assertThat(erpDecimal.getValue().doubleValue())
                .isEqualTo(new BigDecimal(0.00001).doubleValue(), Offset.offset(0.00001));
        }
    }

    @Test
    void testInvalidNumberFormatWithString()
    {
        assertThatThrownBy(() -> new ErpDecimal("abc")).isExactlyInstanceOf(NumberFormatException.class);
    }

    @Test
    void testInvalidNumberFormatWithAlphanumericString()
    {
        assertThatThrownBy(() -> new ErpDecimal("10a")).isExactlyInstanceOf(NumberFormatException.class);
    }
}
