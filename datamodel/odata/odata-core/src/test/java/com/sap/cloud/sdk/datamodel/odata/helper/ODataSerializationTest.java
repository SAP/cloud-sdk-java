package com.sap.cloud.sdk.datamodel.odata.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.data.MapEntry.entry;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataDeserializationException;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestGeneric;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataField;
import com.sap.cloud.sdk.s4hana.datamodel.odata.annotation.Key;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

class ODataSerializationTest
{
    // take note that we are NOT using the following values:
    // "LocalDateTimeValue": "datetime'1992-01-01T00:00:00'"
    // "OffsetDateTimeValue": "datetimeoffset'1992-01-01T00:00:00Z-04:00'"
    // "LocalTimeValue": "time'PT13H20M'"
    // "GuidValue": "guid'123e4567-e89b-12d3-a456-426614174000'"
    private static final String SAMPLE_PAYLOAD = """
        {
          "d" : {
              "__metadata": {
                "uri": "https://services.odata.org/OData/OData.svc/Categories(42)",
                "etag": "W/\\"some-version-id\\"",
                "type": "DataServiceProviderDemo.Category"
              },
              "SByteValue": -127,
              "Int16Value": 1337,
              "IntegerValue": 42,
              "Int64Value": 123456789000,
              "DecimalValue": "123456.789",
              "DoubleValue": 42.1,
              "BooleanValue": true,
              "StringValue": "Food",
              "LocalDateTimeValue": "/Date(694224000000)/",
              "OffsetDateTimeValue": "/Date(694224000000-0240)/",
              "LocalTimeValue": "PT13H20M",
              "GuidValue": "123e4567-e89b-12d3-a456-426614174000",
              "to_Parent": {
                "__deferred": {
                  "uri": "https://services.odata.org/OData/OData.svc/Categories(42)/to_Parent"
                }
              },
              "to_Children": {
                "__deferred": {
                  "uri": "https://services.odata.org/OData/OData.svc/Categories(42)/to_Children"
                }
              },
              "UnmappedStringValue": "foo",
              "UnmappedArrayValue": ["fizz","buzz"],
              "UnmappedComplexValue": {"bar":"fizzbuzz"}
          }
        }
        """;

    @Test
    void testBrokenResponse()
    {
        // setup http response
        final HttpResponse response = mock(HttpResponse.class);
        doReturn(new Header[0]).when(response).getAllHeaders();
        doReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, 201, null)).when(response).getStatusLine();
        doReturn(new StringEntity("{\"d\":{\"broken\"}}", ContentType.APPLICATION_JSON)).when(response).getEntity();

        // setup generic request for reference
        final ODataRequestGeneric request = mock(ODataRequestGeneric.class);
        doReturn(ODataProtocol.V2).when(request).getProtocol();

        // test entity deserialization
        final ODataRequestResultGeneric result = new ODataRequestResultGeneric(request, response);
        assertThatCode(() -> result.as(TestEntity.class)).isInstanceOf(ODataDeserializationException.class);
    }

    @Test
    void testNoValuesResponse()
    {
        // setup http response
        final HttpResponse response = mock(HttpResponse.class);
        doReturn(new Header[0]).when(response).getAllHeaders();
        doReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, 201, null)).when(response).getStatusLine();
        doReturn(new StringEntity("{\"d\":{}}", ContentType.APPLICATION_JSON)).when(response).getEntity();

        // setup generic request for reference
        final ODataRequestGeneric request = mock(ODataRequestGeneric.class);
        doReturn(ODataProtocol.V2).when(request).getProtocol();

        // test entity deserialization
        final ODataRequestResultGeneric result = new ODataRequestResultGeneric(request, response);
        final TestEntity entity = result.as(TestEntity.class);

        assertThat(entity).isNotNull();
        assertThat(entity.getVersionIdentifier()).isEmpty();
        assertThat(entity.getIntegerValue()).isNull();
        assertThat(entity.getDecimalValue()).isNull();
        assertThat(entity.getDoubleValue()).isNull();
        assertThat(entity.getBooleanValue()).isNull();
        assertThat(entity.getLocalTimeValue()).isNull();
        assertThat(entity.getStringValue()).isNull();
        assertThat(entity.getGuidValue()).isNull();
        assertThat(entity.getLocalDateTimeValue()).isNull();
        assertThat(entity.getOffsetDateTimeValue()).isNull();
        assertThat(entity.getCustomFields()).isEmpty();
    }

    @Test
    void testCreateResponse()
    {
        // setup http response
        final HttpResponse response = mock(HttpResponse.class);
        doReturn(new Header[0]).when(response).getAllHeaders();
        doReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, 201, null)).when(response).getStatusLine();
        doReturn(new StringEntity(SAMPLE_PAYLOAD, ContentType.APPLICATION_JSON)).when(response).getEntity();

        // setup generic request for reference
        final ODataRequestGeneric request = mock(ODataRequestGeneric.class);
        doReturn(ODataProtocol.V2).when(request).getProtocol();

        // test entity deserialization
        final ODataRequestResultGeneric result = new ODataRequestResultGeneric(request, response);
        final TestEntity entity = result.as(TestEntity.class);

        assertThat(entity).isNotNull();
        assertThat(entity.getVersionIdentifier()).containsExactly("W/\"some-version-id\"");
        assertThat(entity.getSByteValue()).isEqualTo((byte) -127);
        assertThat(entity.getInt16Value()).isEqualTo((short) 1337);
        assertThat(entity.getIntegerValue()).isEqualTo(42);
        assertThat(entity.getInt64Value()).isEqualTo(123456789000L);
        assertThat(entity.getDecimalValue()).isEqualTo("123456.789");
        assertThat(entity.getDoubleValue()).isEqualTo(42.1);
        assertThat(entity.getBooleanValue()).isTrue();
        assertThat(entity.getLocalTimeValue()).isEqualTo(LocalTime.of(13, 20, 0));
        assertThat(entity.getStringValue()).isEqualTo("Food");
        assertThat(entity.getGuidValue()).isEqualTo(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        assertThat(entity.getLocalDateTimeValue()).isEqualTo(LocalDate.of(1992, Month.JANUARY, 1).atStartOfDay());
        assertThat(entity.getOffsetDateTimeValue())
            .isEqualTo(
                LocalDate
                    .of(1992, Month.JANUARY, 1)
                    .atStartOfDay()
                    .atZone(ZoneId.ofOffset("GMT", ZoneOffset.ofHours(-4))));

        assertThat(entity.getCustomFields())
            .containsExactly(
                entry("UnmappedStringValue", "foo"),
                entry("UnmappedArrayValue", Arrays.asList("fizz", "buzz")),
                entry("UnmappedComplexValue", ImmutableMap.of("bar", "fizzbuzz")));
    }

    @Test
    void testSerialisationForDateTimeAttributes()
    {
        final String SERIALIZED_ENTITY = """
            {\
            "IntegerValue":1,\
            "OffsetDateTimeValue":"/Date(694224000000-0240)/",\
            "LocalTimeValue":"PT13H20M0S",\
            "LocalDateTimeValue":"/Date(694224000000)/"\
            }\
            """;
        final TestEntity entity = new TestEntity();

        entity.setIntegerValue(1);
        entity.setLocalDateTimeValue(LocalDate.of(1992, Month.JANUARY, 1).atStartOfDay());
        entity.setLocalTimeValue(LocalTime.of(13, 20, 0));
        entity
            .setOffsetDateTimeValue(
                LocalDate
                    .of(1992, Month.JANUARY, 1)
                    .atStartOfDay()
                    .atZone(ZoneId.ofOffset("GMT", ZoneOffset.ofHours(-4))));

        final String serialisedEntity = new Gson().toJson(entity);

        assertThat(serialisedEntity).isEqualTo(SERIALIZED_ENTITY);
    }

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString( doNotUseGetters = true, callSuper = true )
    @EqualsAndHashCode( doNotUseGetters = true, callSuper = true )
    @JsonAdapter( com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory.class )
    public static class TestEntity extends VdmEntity<TestEntity>
    {
        @Getter
        final String entityCollection = null;

        @Getter
        private final String defaultServicePath = "/";

        @Getter
        private final Class<TestEntity> type = TestEntity.class;

        @Key
        @SerializedName( "IntegerValue" )
        @JsonProperty( "IntegerValue" )
        @Nullable
        @ODataField( odataName = "IntegerValue" )
        private Integer integerValue;

        @SerializedName( "GuidValue" )
        @JsonProperty( "GuidValue" )
        @Nullable
        @ODataField( odataName = "GuidValue" )
        private UUID guidValue;

        @SerializedName( "StringValue" )
        @JsonProperty( "StringValue" )
        @Nullable
        @ODataField( odataName = "StringValue" )
        private String stringValue;

        @SerializedName( "OffsetDateTimeValue" )
        @JsonProperty( "OffsetDateTimeValue" )
        @Nullable
        @JsonSerialize( using = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.JacksonZonedDateTimeSerializer.class )
        @JsonDeserialize(
            using = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.JacksonZonedDateTimeDeserializer.class )
        @JsonAdapter( com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ZonedDateTimeAdapter.class )
        @ODataField(
            odataName = "OffsetDateTimeValue",
            converter = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ZonedDateTimeCalendarConverter.class )
        private ZonedDateTime offsetDateTimeValue;

        @SerializedName( "to_Parent" )
        @JsonProperty( "to_Parent" )
        @ODataField( odataName = "to_Parent" )
        @Nullable
        @Getter( AccessLevel.NONE )
        @Setter( AccessLevel.NONE )
        private TestEntity toParent;

        @SerializedName( "to_Children" )
        @JsonProperty( "to_Children" )
        @ODataField( odataName = "to_Children" )
        @Getter( AccessLevel.NONE )
        @Setter( AccessLevel.NONE )
        private List<TestEntity> toChildren;

        @SerializedName( "DecimalValue" )
        @JsonProperty( "DecimalValue" )
        @Nullable
        @ODataField( odataName = "DecimalValue" )
        private BigDecimal decimalValue;

        @SerializedName( "DoubleValue" )
        @JsonProperty( "DoubleValue" )
        @Nullable
        @ODataField( odataName = "DoubleValue" )
        private Double doubleValue;

        @SerializedName( "LocalTimeValue" )
        @JsonProperty( "LocalTimeValue" )
        @Nullable
        @JsonSerialize( using = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.JacksonLocalTimeSerializer.class )
        @JsonDeserialize( using = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.JacksonLocalTimeDeserializer.class )
        @JsonAdapter( com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.LocalTimeAdapter.class )
        @ODataField(
            odataName = "LocalTimeValue",
            converter = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.LocalTimeCalendarConverter.class )
        private LocalTime localTimeValue;

        @SerializedName( "LocalDateTimeValue" )
        @JsonProperty( "LocalDateTimeValue" )
        @Nullable
        @JsonSerialize( using = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.JacksonLocalDateTimeSerializer.class )
        @JsonDeserialize(
            using = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.JacksonLocalDateTimeDeserializer.class )
        @JsonAdapter( com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.LocalDateTimeAdapter.class )
        @ODataField(
            odataName = "LocalDateTimeValue",
            converter = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.LocalDateTimeCalendarConverter.class )
        private LocalDateTime localDateTimeValue;

        @SerializedName( "BooleanValue" )
        @JsonProperty( "BooleanValue" )
        @Nullable
        @JsonAdapter( com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataBooleanAdapter.class )
        @ODataField( odataName = "BooleanValue" )
        private Boolean booleanValue;

        @SerializedName( "SByteValue" )
        @JsonProperty( "SByteValue" )
        @Nullable
        @ODataField( odataName = "SByteValue" )
        private Byte sByteValue;

        @SerializedName( "Int16Value" )
        @JsonProperty( "Int16Value" )
        @Nullable
        @ODataField( odataName = "Int16Value" )
        private Short int16Value;

        @SerializedName( "Int64Value" )
        @JsonProperty( "Int64Value" )
        @Nullable
        @ODataField( odataName = "Int64Value" )
        private Long int64Value;
    }
}
