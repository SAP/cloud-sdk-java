package com.sap.cloud.sdk.s4hana.serialization;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

@Deprecated
public class ErpBooleanTest
{
    @Test
    public void testErpBoolean()
    {
        {
            final ErpBoolean erpBoolean = new ErpBoolean(true);

            assertThat(erpBoolean.getValue()).isNotNull();
            assertThat(erpBoolean.getValue()).isTrue();

            assertThat(erpBoolean.isTrue()).isTrue();
            assertThat(erpBoolean.isFalse()).isFalse();
        }

        {
            final ErpBoolean erpBoolean = new ErpBoolean(false);

            assertThat(erpBoolean.getValue()).isNotNull();
            assertThat(erpBoolean.getValue()).isFalse();

            assertThat(erpBoolean.isTrue()).isFalse();
            assertThat(erpBoolean.isFalse()).isTrue();
        }

        {
            final ErpBoolean erpBoolean = new ErpBoolean("X");

            assertThat(erpBoolean.getValue()).isNotNull();
            assertThat(erpBoolean.getValue()).isTrue();

            assertThat(erpBoolean.isTrue()).isTrue();
            assertThat(erpBoolean.isFalse()).isFalse();
        }

        {
            final ErpBoolean erpBoolean = new ErpBoolean("");

            assertThat(erpBoolean.getValue()).isNotNull();
            assertThat(erpBoolean.getValue()).isFalse();

            assertThat(erpBoolean.isTrue()).isFalse();
            assertThat(erpBoolean.isFalse()).isTrue();
        }

        {
            final ErpBoolean erpBoolean = new ErpBoolean(" ");

            assertThat(erpBoolean.getValue()).isNotNull();
            assertThat(erpBoolean.getValue()).isFalse();

            assertThat(erpBoolean.isTrue()).isFalse();
            assertThat(erpBoolean.isFalse()).isTrue();
        }

        {
            final ErpBoolean erpBoolean = new ErpBoolean("-");

            assertThat(erpBoolean.getValue()).isNull();

            assertThat(erpBoolean.isTrue()).isFalse();
            assertThat(erpBoolean.isFalse()).isFalse();
        }
    }

    @Test( expected = IllegalArgumentException.class )
    public void testIllegalArgumentWithString()
    {
        new ErpBoolean("abc");
    }

    @Test( expected = IllegalArgumentException.class )
    public void testIllegalArgumentWithLowerCaseX()
    {
        new ErpBoolean("x");
    }
}
