/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import static java.lang.Float.NaN;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.message.BasicHeader;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.result.ElementName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

class ODataResponsePrimitiveDataParsingTest
{
    public enum ColorEnum
    {
        Yellow
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReferenceObject
    {
        @ElementName( "BooleanValue" )
        @SerializedName( "BooleanValue" )
        @JsonProperty( "BooleanValue" )
        boolean booleanValue;

        @ElementName( "BinaryValue" )
        @SerializedName( "BinaryValue" )
        @JsonProperty( "BinaryValue" )
        byte[] binaryValue;

        @ElementName( "StringValue" )
        @SerializedName( "StringValue" )
        @JsonProperty( "StringValue" )
        String stringValue;

        // Numbers
        @ElementName( "IntegerValue" )
        @SerializedName( "IntegerValue" )
        @JsonProperty( "IntegerValue" )
        int integerValue;

        @ElementName( "Int64Value" )
        @SerializedName( "Int64Value" )
        @JsonProperty( "Int64Value" )
        long int64Value;

        @ElementName( "SingleValue" )
        @SerializedName( "SingleValue" )
        @JsonProperty( "SingleValue" )
        float singleValue;

        @ElementName( "DecimalValue" )
        @SerializedName( "DecimalValue" )
        @JsonProperty( "DecimalValue" )
        double decimalValue;

        @ElementName( "DoubleValue" )
        @SerializedName( "DoubleValue" )
        @JsonProperty( "DoubleValue" )
        double doubleValue;

        @ElementName( "GuidValue" )
        @SerializedName( "GuidValue" )
        @JsonProperty( "GuidValue" )
        UUID guidValue;

        // Dates
        @ElementName( "DurationValue" )
        @SerializedName( "DurationValue" )
        @JsonProperty( "DurationValue" )
        Duration durationValue;

        @ElementName( "TimeOfDayValue" )
        @SerializedName( "TimeOfDayValue" )
        @JsonProperty( "TimeOfDayValue" )
        LocalTime timeOfDayValue;

        @ElementName( "DateValue" )
        @SerializedName( "DateValue" )
        @JsonProperty( "DateValue" )
        LocalDate dateValue;

        @ElementName( "DateTimeOffsetValue" )
        @SerializedName( "DateTimeOffsetValue" )
        @JsonProperty( "DateTimeOffsetValue" )
        OffsetDateTime dateTimeOffsetValue;

        @ElementName( "Tags" )
        @SerializedName( "Tags" )
        @JsonProperty( "Tags" )
        List<String> tags;

        @ElementName( "ColorEnumValue" )
        @SerializedName( "ColorEnumValue" )
        @JsonProperty( "ColorEnumValue" )
        ColorEnum colorEnumValue;

        // https://docs.oasis-open.org/odata/odata-json-format/v4.01/csprd06/odata-json-format-v4.01-csprd06.html#sec_PrimitiveValue
        // applied the following changes:
        // - SingleValue: Replaced "INF" with "NaN", Java float expects "Infinity" instead (with an optional plus or minus in front). We would need another type adapter for that.
        // - DurationValue: Dropped the last 3 nines before the S from the reference service example here because WTF they have picoseconds (10^-12 seconds) in there...
        // - GeographyPoint: Excluded, could be added later if needed.
        private static final String PAYLOAD_ODATA_REFERENCE = """
            {
              "BooleanValue": false,
              "BinaryValue": "T0RhdGE",
              "IntegerValue": -128,
              "DoubleValue": 3.1415926535897931,
              "SingleValue": "NaN",
              "DecimalValue": 12.3999999999999999,
              "StringValue": "Say \\"Hello\\",\\nthen go",
              "DateValue": "2012-12-03",
              "DateTimeOffsetValue": "2012-12-03T07:16:23Z",
              "DurationValue": "P12DT23H59M59.999999999S",
              "TimeOfDayValue": "07:59:59.999",
              "GuidValue": "01234567-89ab-cdef-0123-456789abcdef",
              "Int64Value": 0
            ,\
              "ColorEnumValue": "Yellow"
            }\
            """;

        private static final ReferenceObject expectedObject =
            new ReferenceObject(
                false,
                new byte[] { 79, 68, 97, 116, 97 }, // == Base64.getDecoder().decode("T0RhdGE")
                "Say \"Hello\",\nthen go",
                -128,
                0L,
                NaN,
                12.3999999999999999d, // = 12.4
                3.1415926535897931,
                UUID.fromString("01234567-89ab-cdef-0123-456789abcdef"),
                Duration.ofDays(12).plusHours(23).plusMinutes(59).plusSeconds(59).plusMillis(999).plusNanos(999999),
                LocalTime.of(7, 59, 59, 999000000),
                LocalDate.of(2012, 12, 3),
                OffsetDateTime.of(2012, 12, 3, 7, 16, 23, 0, ZoneOffset.of("Z")),
                null,
                ColorEnum.Yellow);
    }

    @Test
    void testDataTypeParsingByReferenceObject()
    {
        final ODataRequestResultGeneric result = mockRequestResult(ReferenceObject.PAYLOAD_ODATA_REFERENCE);

        final ReferenceObject referenceResult = result.as(ReferenceObject.class);

        Objects.requireNonNull(referenceResult);

        assertThat(referenceResult.booleanValue).isEqualTo(ReferenceObject.expectedObject.booleanValue);
        assertThat(referenceResult.binaryValue).isEqualTo(ReferenceObject.expectedObject.binaryValue);
        assertThat(referenceResult.stringValue).isEqualTo(ReferenceObject.expectedObject.stringValue);
        assertThat(referenceResult.integerValue).isEqualTo(ReferenceObject.expectedObject.integerValue);
        assertThat(referenceResult.int64Value).isEqualTo(ReferenceObject.expectedObject.int64Value);
        assertThat((Object) referenceResult.singleValue).isEqualTo(ReferenceObject.expectedObject.singleValue);
        assertThat(referenceResult.decimalValue).isEqualTo(ReferenceObject.expectedObject.decimalValue);
        assertThat(referenceResult.doubleValue).isEqualTo(ReferenceObject.expectedObject.doubleValue);
        assertThat(referenceResult.guidValue).isEqualTo(ReferenceObject.expectedObject.guidValue);
        assertThat(referenceResult.durationValue).isEqualTo(ReferenceObject.expectedObject.durationValue);
        assertThat(referenceResult.timeOfDayValue).isEqualTo(ReferenceObject.expectedObject.timeOfDayValue);
        assertThat(referenceResult.dateValue).isEqualTo(ReferenceObject.expectedObject.dateValue);
        assertThat(referenceResult.dateTimeOffsetValue).isEqualTo(ReferenceObject.expectedObject.dateTimeOffsetValue);
        assertThat(referenceResult.tags).isEqualTo(ReferenceObject.expectedObject.tags);
        assertThat(referenceResult.colorEnumValue).isEqualTo(ReferenceObject.expectedObject.colorEnumValue);
    }

    @Test
    void testDataTypeParsingByNumberDeserializationStrategy()
    {
        final Map<NumberDeserializationStrategy, Consumer<Object>> assertions =
            ImmutableMap
                .of(
                    NumberDeserializationStrategy.DOUBLE,
                    number -> assertThat(number).isEqualTo(12.4),
                    NumberDeserializationStrategy.BIG_DECIMAL,
                    number -> assertThat(number).isEqualTo(new BigDecimal("12.3999999999999999")));

        for( final NumberDeserializationStrategy strategy : assertions.keySet() ) {
            final ODataRequestResultGeneric result = mockRequestResult(ReferenceObject.PAYLOAD_ODATA_REFERENCE);
            final Map<String, Object> referenceResult = result.withNumberDeserializationStrategy(strategy).asMap();
            Objects.requireNonNull(referenceResult);
            assertions.get(strategy).accept(referenceResult.get("DecimalValue"));
        }
    }

    private static ODataRequestResultGeneric mockRequestResult( final String payload )
    {
        try {
            final ODataRequestGeneric request = mock(ODataRequestGeneric.class);
            when(request.getProtocol()).thenReturn(ODataProtocol.V4);

            final HttpEntity httpEntity = spy(HttpEntity.class);
            when(httpEntity.getContentLength()).thenReturn(0L);
            when(httpEntity.isRepeatable()).thenReturn(true);
            when(httpEntity.getContentType()).thenReturn(new BasicHeader("Content-Type", "application/json"));
            when(httpEntity.getContentEncoding()).thenReturn(new BasicHeader("Content-Encoding", "identity"));
            when(httpEntity.getContent()).thenReturn(new ByteArrayInputStream(payload.getBytes()));
            final HttpEntity bufferedHttpEntity = new BufferedHttpEntity(httpEntity);

            final HttpResponse httpResponse = mock(HttpResponse.class);
            when(httpResponse.getAllHeaders()).thenReturn(new Header[0]);
            when(httpResponse.getEntity()).thenReturn(bufferedHttpEntity);

            return new ODataRequestResultGeneric(request, httpResponse);
        }
        catch( final Exception e ) {
            throw new ShouldNotHappenException("Failed to run tests");
        }
    }
}
