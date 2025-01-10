package com.sap.cloud.sdk.s4hana.serialization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

@Deprecated
class ErpBooleanTest
{
    @Test
    void testErpBoolean()
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

    @Test
    void testIllegalArgumentWithString()
    {
        assertThatThrownBy(() -> new ErpBoolean("abc")).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testIllegalArgumentWithLowerCaseX()
    {
        assertThatThrownBy(() -> new ErpBoolean("x")).isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
