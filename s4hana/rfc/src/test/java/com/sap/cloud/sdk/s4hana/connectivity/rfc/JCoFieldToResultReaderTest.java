/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.gson.GsonBuilder;
import com.sap.cloud.sdk.result.GsonResultElementFactory;
import com.sap.conn.jco.JCoField;

@Deprecated
class JCoFieldToResultReaderTest
{
    private static final String FIELD_NAME = "field";
    private static final String STRING_VALUE = "string-value";

    @Test
    void testAccessingByteArrayPrimitiveAsString()
    {
        final JCoFieldToResultReader cut = new JCoFieldToResultReader();

        final JCoField jcoField = Mockito.mock(JCoField.class);

        Mockito.when(jcoField.isStructure()).thenReturn(false);
        Mockito.when(jcoField.isTable()).thenReturn(false);

        Mockito.when(jcoField.getValue()).thenReturn(STRING_VALUE.getBytes());

        Mockito.when(jcoField.getName()).thenReturn(FIELD_NAME);

        final AbstractRemoteFunctionRequestResult.Result result =
            cut.newResult(jcoField, new GsonResultElementFactory(new GsonBuilder()));

        assertThat(result.getValue().asString()).isEqualTo(STRING_VALUE);
        assertThat(result.getName()).isEqualTo(FIELD_NAME);
    }
}
