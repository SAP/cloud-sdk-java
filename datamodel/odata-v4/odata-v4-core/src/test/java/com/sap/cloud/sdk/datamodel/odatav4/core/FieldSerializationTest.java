package com.sap.cloud.sdk.datamodel.odatav4.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestGeneric;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;
import com.sap.cloud.sdk.datamodel.odatav4.adapter.GsonVdmAdapterFactory;
import com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectDeserializer;
import com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectSerializer;
import com.sap.cloud.sdk.result.ElementName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

class FieldSerializationTest
{
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode( doNotUseGetters = true, callSuper = true )
    @JsonAdapter( GsonVdmAdapterFactory.class )
    @JsonSerialize( using = JacksonVdmObjectSerializer.class )
    @JsonDeserialize( using = JacksonVdmObjectDeserializer.class )
    public static class ReferenceObject extends VdmEntity<ReferenceObject>
    {
        @Getter
        private final String odataType = "TestEntity";

        @Getter
        private final String entityCollection = "EntityCollection";

        @Getter
        private final Class<ReferenceObject> type = ReferenceObject.class;

        @ElementName( "ByteValue" )
        @SerializedName( "ByteValue" )
        @JsonProperty( "ByteValue" )
        short byteValue;

        @ElementName( "SByteValue" )
        @SerializedName( "SByteValue" )
        @JsonProperty( "SByteValue" )
        Byte sByteValue;

        @ElementName( "Int16Value" )
        @SerializedName( "Int16Value" )
        @JsonProperty( "Int16Value" )
        short int16Value;

        @ElementName( "Int32Value" )
        @SerializedName( "Int32Value" )
        @JsonProperty( "Int32Value" )
        int int32Value;

        @ElementName( "Int64Value" )
        @SerializedName( "Int64Value" )
        @JsonProperty( "Int64Value" )
        long int64Value;

        @ElementName( "SingleValue" )
        @SerializedName( "SingleValue" )
        @JsonProperty( "SingleValue" )
        float singleValue;

        @ElementName( "DoubleValue" )
        @SerializedName( "DoubleValue" )
        @JsonProperty( "DoubleValue" )
        double doubleValue;

        @ElementName( "DecimalValue" )
        @SerializedName( "DecimalValue" )
        @JsonProperty( "DecimalValue" )
        BigDecimal decimalValue;

        @ElementName( "BooleanValue" )
        @SerializedName( "BooleanValue" )
        @JsonProperty( "BooleanValue" )
        boolean booleanValue;

        @ElementName( "StringValue" )
        @SerializedName( "StringValue" )
        @JsonProperty( "StringValue" )
        String stringValue;

        @ElementName( "BinaryValue" )
        @SerializedName( "BinaryValue" )
        @JsonProperty( "BinaryValue" )
        byte[] binaryValue;

        @ElementName( "GuidValue" )
        @SerializedName( "GuidValue" )
        @JsonProperty( "GuidValue" )
        UUID guidValue;

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

        // https://docs.oasis-open.org/odata/odata-json-format/v4.01/csprd06/odata-json-format-v4.01-csprd06.html#sec_PrimitiveValue
        private static final String PAYLOAD_ODATA_REFERENCE =
            """
            {\
            "@odata.type":"#TestEntity",\
            "ByteValue":255,\
            "SByteValue":-128,\
            "Int16Value":1,\
            "Int32Value":-1234,\
            "Int64Value":1234567890,\
            "SingleValue":1234.5677,\
            "DoubleValue":1234.5678,\
            "DecimalValue":110,\
            "BooleanValue":false,\
            "StringValue":"test",\
            "BinaryValue":"AQID",\
            "GuidValue":"00000000-1111-2222-3333-444444444444",\
            "TimeOfDayValue":"12:00:00",\
            "DateValue":"1999-03-14",\
            "DateTimeOffsetValue":"1999-03-14T00:00:00Z",\
            "GeographyPoint":{"type":"Point","coordinates":[142.1,64.1]}\
            }\
            """;
    }

    @Test
    void testBinaryFieldParsingFromResponsePayload()
    {
        final ODataRequestResultGeneric result = mockRequestResult(ReferenceObject.PAYLOAD_ODATA_REFERENCE);
        final ReferenceObject referenceResult = result.as(ReferenceObject.class);

        Objects.requireNonNull(referenceResult);
        assertThat(referenceResult.getBinaryValue()).isEqualTo(new byte[] { 1, 2, 3 });

        final String ser =
            new CreateRequestBuilder<>("/", referenceResult, "EntityCollection").toRequest().getSerializedEntity();
        assertThat(ser).isEqualTo(ReferenceObject.PAYLOAD_ODATA_REFERENCE);
    }

    @Test
    void testCustomFieldParsingFromResponsePayload()
    {
        final ODataRequestResultGeneric result = mockRequestResult(ReferenceObject.PAYLOAD_ODATA_REFERENCE);

        final ReferenceObject referenceResult = result.as(ReferenceObject.class);

        Objects.requireNonNull(referenceResult);
        assertThat(referenceResult.getCustomFieldNames()).containsExactly("GeographyPoint");
        assertThat(referenceResult.<Object> getCustomField("GeographyPoint")).isInstanceOf(Map.class);
    }

    @SneakyThrows
    private static ODataRequestResultGeneric mockRequestResult( final String payload )
    {
        final ODataRequestGeneric request = mock(ODataRequestGeneric.class);
        when(request.getProtocol()).thenReturn(ODataProtocol.V4);

        final HttpResponse response = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"));
        response.setEntity(new StringEntity(payload));
        response.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());

        return new ODataRequestResultGeneric(request, response);
    }
}
