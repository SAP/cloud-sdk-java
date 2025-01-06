/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;

import com.google.gson.annotations.JsonAdapter;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FieldReference;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataEntityKey;
import com.sap.cloud.sdk.result.ElementName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

class UpdateRequestHelperPutTest
{
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonAdapter( com.sap.cloud.sdk.datamodel.odatav4.adapter.GsonVdmAdapterFactory.class )
    private static class TestEntity extends VdmEntity<TestEntity>
    {
        @Getter
        private final String odataType = "TestEntity";

        @Getter
        private final String entityCollection = "EntityParentCollection";

        @Getter
        private final Class<TestEntity> type = TestEntity.class;

        @Getter
        @ElementName( "Value" )
        private BigDecimal value;

        final static SimpleProperty.NumericDecimal<TestEntity> VALUE =
            new SimpleProperty.NumericDecimal<>(TestEntity.class, "Value");

        @Getter
        @ElementName( "Emails" )
        private List<String> emails;

        final static SimpleProperty.Collection<TestEntity, String> EMAILS =
            new SimpleProperty.Collection<>(TestEntity.class, "Emails", String.class);

        @Getter
        @ElementName( "Name" )
        private String name;

        final static SimpleProperty.String<TestEntity> NAME = new SimpleProperty.String<>(TestEntity.class, "Name");

        @Getter
        @ElementName( "IsRetired" )
        private Boolean isRetired;

        final static SimpleProperty.Boolean<TestEntity> IS_RETIRED =
            new SimpleProperty.Boolean<>(TestEntity.class, "IsRetired");

        @Nonnull
        @Override
        protected ODataEntityKey getKey()
        {
            final ODataEntityKey key = new ODataEntityKey(ODataProtocol.V4);
            key.addKeyProperty("Name", name);
            return key;
        }

        public void setName( String name )
        {
            rememberChangedField("Name", this.name);
            this.name = name;
        }

        public void setIsRetired( Boolean isRetired )
        {
            rememberChangedField("IsRetired", this.isRetired);
            this.isRetired = isRetired;
        }

        public void setValue( BigDecimal value )
        {
            rememberChangedField("Value", this.value);
            this.value = value;
        }

        public void setEmails( List<String> emails )
        {
            rememberChangedField("Emails", this.emails);
            this.emails = emails;
        }

        @Nonnull
        @Override
        protected Map<String, Object> toMapOfFields()
        {
            final Map<String, Object> result = new HashMap<>();
            result.put("Name", name);
            result.put("Value", value);
            result.put("Emails", emails);
            result.put("IsRetired", isRetired);
            return result;
        }
    }

    @Test
    void testPutPayload()
        throws Exception
    {
        final TestEntity entity = TestEntity.builder().name("Foo").isRetired(true).build();
        entity.setValue(new BigDecimal("0.00000000001"));
        entity.setEmails(Collections.singletonList("sampleEmail@sap.com"));
        final String json = new UpdateRequestHelperPut().toJson(entity, null);

        assertEquals(
            "{\"@odata.type\":\"#TestEntity\",\"Value\":0.00000000001,\"Emails\":[\"sampleEmail@sap.com\"],\"Name\":\"Foo\",\"IsRetired\":true}",
            json,
            JSONCompareMode.LENIENT);
    }

    @Test
    void testPutPayloadWithExcludedFields()
        throws Exception
    {
        final TestEntity entity = TestEntity.builder().name("Foo").isRetired(true).build();
        entity.setValue(new BigDecimal("0.00000000001"));
        final List<FieldReference> excludedFields =
            Arrays.asList(TestEntity.IS_RETIRED, TestEntity.NAME, TestEntity.EMAILS);
        final String json = new UpdateRequestHelperPut().toJson(entity, excludedFields);

        assertEquals("{\"@odata.type\":\"#TestEntity\",\"Value\":0.00000000001}", json, JSONCompareMode.LENIENT);
    }

    @Test
    void testPutPayloadWithNullFields()
        throws Exception
    {
        final TestEntity entity = TestEntity.builder().name("Foo").isRetired(true).build();
        entity.setValue(new BigDecimal("0.00000000001"));
        final String json = new UpdateRequestHelperPut().toJson(entity, null);

        assertEquals(
            "{\"@odata.type\":\"#TestEntity\",\"Value\":0.00000000001,\"Name\":\"Foo\",\"IsRetired\":true}",
            json,
            JSONCompareMode.STRICT);
    }
}
