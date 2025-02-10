package com.sap.cloud.sdk.datamodel.odatav4.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.annotations.JsonAdapter;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEntity;
import com.sap.cloud.sdk.result.ElementName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

class BigDecimalSerialisationTest
{
    private static final String TEST_DEFAULT_SERVICE_PATH = "/odata/default";

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonAdapter( GsonVdmAdapterFactory.class )
    @JsonSerialize( using = JacksonVdmObjectSerializer.class )
    @JsonDeserialize( using = JacksonVdmObjectDeserializer.class )
    @EqualsAndHashCode( doNotUseGetters = true, callSuper = true )
    @Data
    private static class TestEntity extends VdmEntity<TestEntity>
    {
        @Getter
        private final String odataType = "TestEntity";

        @Getter
        private final String entityCollection = "EntityCollection";

        @Getter
        private final Class<TestEntity> type = TestEntity.class;

        @Getter
        private final String defaultServicePath = TEST_DEFAULT_SERVICE_PATH;

        @ElementName( "BigDecimalField" )
        BigDecimal bigDecimalField;

        public void setBigDecimalField( BigDecimal bigDecimalField )
        {
            rememberChangedField("BigDecimalField", this.bigDecimalField);
            this.bigDecimalField = bigDecimalField;
        }

        @Nonnull
        @Override
        protected Map<String, Object> toMapOfFields()
        {
            return ImmutableMap
                .<String, Object> builder()
                .putAll(super.toMapOfFields())
                .put("BigDecimalField", getBigDecimalField())
                .build();
        }
    }

    @Test
    void testSerialization()
        throws JsonProcessingException
    {
        final TestEntity testEntity = new TestEntity();
        testEntity.setBigDecimalField(new BigDecimal("0.000000003"));
        // GSON
        final String jsonGson = new Gson().toJson(testEntity);
        assertThat(jsonGson).doesNotContain("3E-9").contains("0.000000003");
        // Jackson
        final String jsonJackson = new ObjectMapper().writeValueAsString(testEntity);
        assertThat(jsonJackson).doesNotContain("3E-9").contains("0.000000003");
    }

    @Test
    void testBigDecimalEquals()
    {
        final TestEntity testEntity = TestEntity.builder().bigDecimalField(new BigDecimal("1.0")).build();
        testEntity.setBigDecimalField(new BigDecimal("1"));
        assertThat(testEntity.getChangedFields()).isEmpty();
    }
}
