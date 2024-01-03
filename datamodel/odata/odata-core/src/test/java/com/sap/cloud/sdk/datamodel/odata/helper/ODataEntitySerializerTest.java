/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.datamodel.odata.client.expression.FieldReference;

class ODataEntitySerializerTest
{
    @Test
    void testSerializeEntityForCreate()
    {
        final TestVdmEntity entity = TestVdmEntity.builder().stringValue("string").booleanValue(false).build();
        entity.setIntegerValue(42);

        final String payload = ODataEntitySerializer.serializeEntityForCreate(entity);
        assertThat(payload).isEqualTo("{\"IntegerValue\":42,\"StringValue\":\"string\",\"BooleanValue\":false}");
    }

    @Test
    void testSerializeEntityForUpdatePut()
    {
        final TestVdmEntity entity = TestVdmEntity.builder().stringValue("string").booleanValue(false).build();
        entity.setIntegerValue(42);

        final String payload = ODataEntitySerializer.serializeEntityForUpdatePut(entity, null);
        assertThat(payload)
            .isEqualTo(
                "{\"IntegerValue\":42,\"GuidValue\":null,\"StringValue\":\"string\",\"OffsetDateTimeValue\":null,\"to_Parent\":null,\"to_Children\":null,\"DecimalValue\":null,\"DoubleValue\":null,\"LocalTimeValue\":null,\"LocalDateTimeValue\":null,\"BooleanValue\":false,\"ComplexValue\":null}");
    }

    @Test
    void testSerializeEntityForUpdatePutWithExcludedFields()
    {
        final TestVdmEntity entity = TestVdmEntity.builder().stringValue("NewString").booleanValue(true).build();
        entity.setIntegerValue(45);

        final List<FieldReference> fieldsToExclude =
            Arrays.asList(FieldReference.of("DoubleValue"), FieldReference.of("ComplexValue"));
        final String payload = ODataEntitySerializer.serializeEntityForUpdatePut(entity, fieldsToExclude);
        assertThat(payload)
            .isEqualTo(
                "{\"IntegerValue\":45,\"GuidValue\":null,\"StringValue\":\"NewString\",\"OffsetDateTimeValue\":null,\"to_Parent\":null,\"to_Children\":null,\"DecimalValue\":null,\"LocalTimeValue\":null,\"LocalDateTimeValue\":null,\"BooleanValue\":true}");
    }

    @Test
    void testSerializeEntityForUpdatePatch()
    {
        final TestVdmEntity entity = TestVdmEntity.builder().stringValue("string").booleanValue(false).build();
        entity.setIntegerValue(42);

        final Collection<FieldReference> fields = Arrays.asList(FieldReference.of("a"), FieldReference.of("b"));
        final String payload = ODataEntitySerializer.serializeEntityForUpdatePatch(entity, fields);
        assertThat(payload).isEqualTo("{\"a\":null,\"b\":null,\"IntegerValue\":42}");
    }
}
