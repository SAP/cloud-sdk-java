package com.sap.cloud.sdk.s4hana.serialization;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.assertj.core.data.Offset;
import org.junit.Test;

@Deprecated
public class ErpDecimalTest
{
    @Test
    public void testErpDecimal()
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

    @Test( expected = NumberFormatException.class )
    public void testInvalidNumberFormatWithString()
    {
        new ErpDecimal("abc");
    }

    @Test( expected = NumberFormatException.class )
    public void testInvalidNumberFormatWithAlphanumericString()
    {
        new ErpDecimal("10a");
    }
}
