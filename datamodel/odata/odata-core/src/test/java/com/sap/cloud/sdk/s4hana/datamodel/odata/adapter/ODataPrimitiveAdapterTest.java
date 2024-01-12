/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

class ODataPrimitiveAdapterTest
{
    private static final String primitivesEntityInput = """
        {
          "EdmBinaryProperty" : "Rk9PIEJBUg==",
          "EdmBooleanProperty" : true,
          "EdmByteProperty" : 200,
          "EdmDateTimeProperty" : "/Date(346833000)/",
          "EdmDateTimeOffsetProperty" : "/Date(346833000-300)/",
          "EdmDecimalProperty" : 9.223372036854776E18,
          "EdmDoubleProperty" : 3.1415926535897E93,
          "EdmGuidProperty" : "00000000-1111-2222-3333-444444444444",
          "EdmInt16Property" : 1980,
          "EdmInt32Property" : 16777216,
          "EdmInt64Property" : 9223372036854775800,
          "EdmSByteProperty" : -120,
          "EdmSingleProperty" : 3.14,
          "EdmStringProperty" : "TEST STRING",
          "EdmTimeProperty" : "PT07H30M00S"
        }
        """;

    private static final String expectedSerializedEntity = """
        {
        "versionIdentifier":null,
        "EdmBinaryProperty":"Rk9PIEJBUg==",
        "EdmBooleanProperty":true,
        "EdmByteProperty":200,
        "EdmDateTimeProperty":"/Date(1649342635567)/",
        "EdmDateTimeOffsetProperty":"/Date(1649342635567-0300)/",
        "EdmDecimalProperty":9.223372036854776E+18,
        "EdmDoubleProperty":3.1415926535897E93,
        "EdmGuidProperty":"00000000-1111-2222-3333-444444444444",
        "EdmInt16Property":1980,
        "EdmInt32Property":16777216,
        "EdmInt64Property":9223372036854775800,
        "EdmSByteProperty":-120,
        "EdmSingleProperty":3.14,
        "EdmStringProperty":"TEST STRING",
        "EdmTimeProperty":"PT14H25M34.567S"
        }
        """;

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString( doNotUseGetters = true, callSuper = true )
    @EqualsAndHashCode( doNotUseGetters = true, callSuper = true )
    @JsonAdapter( com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory.class )
    public static class PrimitivesEntity extends VdmEntity<PrimitivesEntity>
    {
        @SerializedName( "EdmBinaryProperty" )
        @JsonProperty( "EdmBinaryProperty" )
        @Nullable
        @JsonAdapter( com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataBinaryAdapter.class )
        private byte[] edmBinaryProperty;

        @SerializedName( "EdmBooleanProperty" )
        @JsonProperty( "EdmBooleanProperty" )
        @Nullable
        @JsonAdapter( com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataBooleanAdapter.class )
        private Boolean edmBooleanProperty;

        @SerializedName( "EdmByteProperty" )
        @JsonProperty( "EdmByteProperty" )
        @Nullable
        private Short edmByteProperty;

        @SerializedName( "EdmDateTimeProperty" )
        @JsonProperty( "EdmDateTimeProperty" )
        @Nullable
        @JsonAdapter( LocalDateTimeAdapter.class )
        @JsonSerialize( using = JacksonLocalDateTimeSerializer.class )
        @JsonDeserialize( using = JacksonLocalDateTimeDeserializer.class )
        private LocalDateTime edmDateTimeProperty;

        @SerializedName( "EdmDateTimeOffsetProperty" )
        @JsonProperty( "EdmDateTimeOffsetProperty" )
        @Nullable
        @JsonAdapter( ZonedDateTimeAdapter.class )
        @JsonSerialize( using = JacksonZonedDateTimeSerializer.class )
        @JsonDeserialize( using = JacksonZonedDateTimeDeserializer.class )
        private ZonedDateTime edmDateTimeOffsetProperty;

        @SerializedName( "EdmDecimalProperty" )
        @JsonProperty( "EdmDecimalProperty" )
        @Nullable
        private BigDecimal edmDecimalProperty;

        @SerializedName( "EdmDoubleProperty" )
        @JsonProperty( "EdmDoubleProperty" )
        @Nullable
        private Double edmDoubleProperty;

        @SerializedName( "EdmGuidProperty" )
        @JsonProperty( "EdmGuidProperty" )
        @Nullable
        private UUID edmGuidProperty;

        @SerializedName( "EdmInt16Property" )
        @JsonProperty( "EdmInt16Property" )
        @Nullable
        private Short edmInt16Property;

        @SerializedName( "EdmInt32Property" )
        @JsonProperty( "EdmInt32Property" )
        @Nullable
        private Integer edmInt32Property;

        @SerializedName( "EdmInt64Property" )
        @JsonProperty( "EdmInt64Property" )
        @Nullable
        private Long edmInt64Property;

        @SerializedName( "EdmSByteProperty" )
        @JsonProperty( "EdmSByteProperty" )
        @Nullable
        private Byte edmSByteProperty;

        @SerializedName( "EdmSingleProperty" )
        @JsonProperty( "EdmSingleProperty" )
        @Nullable
        private Float edmSingleProperty;

        @SerializedName( "EdmStringProperty" )
        @JsonProperty( "EdmStringProperty" )
        @Nullable
        private String edmStringProperty;

        @SerializedName( "EdmTimeProperty" )
        @JsonProperty( "EdmTimeProperty" )
        @Nullable
        @JsonSerialize( using = JacksonLocalTimeSerializer.class )
        @JsonDeserialize( using = JacksonLocalTimeDeserializer.class )
        @JsonAdapter( LocalTimeAdapter.class )
        private LocalTime edmTimeProperty;

        @Getter
        @Setter
        @Builder.Default
        private transient String servicePath = "NOT_APPLICABLE";

        @Override
        protected String getEntityCollection()
        {
            return "PrimitivesCollection";
        }

        @Nonnull
        @Override
        public Class<PrimitivesEntity> getType()
        {
            return PrimitivesEntity.class;
        }
    }

    @Test
    void testGsonDeserialization()
    {
        final PrimitivesEntity primitivesEntity = new Gson().fromJson(primitivesEntityInput, PrimitivesEntity.class);

        assertThat(primitivesEntity).isNotNull();

        assertThat(primitivesEntity.getEdmBinaryProperty()).containsExactly('F', 'O', 'O', ' ', 'B', 'A', 'R');
        assertThat(primitivesEntity.getEdmBooleanProperty()).isTrue();
        assertThat(primitivesEntity.getEdmByteProperty()).isEqualTo((short) 200);

        assertThat(primitivesEntity.getEdmDecimalProperty()).isEqualTo(new BigDecimal("9.223372036854776E18"));
        assertThat(primitivesEntity.getEdmDoubleProperty()).isEqualTo(3.1415926535897E+93d);
        assertThat(primitivesEntity.getEdmGuidProperty())
            .isEqualTo(UUID.fromString("00000000-1111-2222-3333-444444444444"));
        assertThat(primitivesEntity.getEdmInt16Property()).isEqualTo((short) 1980);
        assertThat(primitivesEntity.getEdmInt32Property()).isEqualTo(16777216);
        assertThat(primitivesEntity.getEdmInt64Property()).isEqualTo(9223372036854775800L);
        assertThat(primitivesEntity.getEdmSByteProperty()).isEqualTo((byte) -120);
        assertThat(primitivesEntity.getEdmSingleProperty()).isEqualTo(3.14f);
        assertThat(primitivesEntity.getEdmStringProperty()).isEqualTo("TEST STRING");
        assertThat(primitivesEntity.getEdmTimeProperty()).isEqualTo("07:30");
    }

    @Test
    void testGsonSerialization()
        throws Exception
    {
        final LocalDateTime expectedEdmDateTimeProperty =
            LocalDateTime.of(2022, Month.APRIL, 7, 14, 43, 55, 567 * 1000000);

        final ZonedDateTime expectedEdmDateTimeOffsetProperty =
            expectedEdmDateTimeProperty.atZone(ZoneId.of("GMT-05:00"));

        final LocalTime expectedEdmTimeProperty = LocalTime.of(14, 25, 34, 567 * 1000000);

        final PrimitivesEntity primitivesEntity =
            PrimitivesEntity
                .builder()
                .edmBinaryProperty(new byte[] { 'F', 'O', 'O', ' ', 'B', 'A', 'R' })
                .edmBooleanProperty(true)
                .edmByteProperty((short) 200)
                .edmDateTimeProperty(expectedEdmDateTimeProperty)
                .edmDateTimeOffsetProperty(expectedEdmDateTimeOffsetProperty)
                .edmDecimalProperty(new BigDecimal("9.223372036854776E18"))
                .edmDoubleProperty(3.1415926535897E93)
                .edmGuidProperty(UUID.fromString("00000000-1111-2222-3333-444444444444"))
                .edmInt16Property((short) 1980)
                .edmInt32Property(16777216)
                .edmInt64Property(9223372036854775800L)
                .edmSByteProperty((byte) -120)
                .edmSingleProperty(3.14f)
                .edmStringProperty("TEST STRING")
                .edmTimeProperty(expectedEdmTimeProperty)
                .build();

        // test gson
        final GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls().disableHtmlEscaping();
        final String actualGsonSerializedEntity = gsonBuilder.create().toJson(primitivesEntity);
        assertThat(actualGsonSerializedEntity).isEqualTo(expectedSerializedEntity);

        // test jackson
        final ObjectMapper jacksonMapper = new ObjectMapper();
        final String actualJacksonSerializedEntity = jacksonMapper.writeValueAsString(primitivesEntity);
        assertThat(actualJacksonSerializedEntity).isEqualTo(expectedSerializedEntity);
    }
}
