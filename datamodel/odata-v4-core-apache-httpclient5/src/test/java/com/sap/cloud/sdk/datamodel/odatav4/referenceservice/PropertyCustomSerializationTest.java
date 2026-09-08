package com.sap.cloud.sdk.datamodel.odatav4.referenceservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sap.cloud.sdk.datamodel.odatav4.adapter.GsonVdmAdapterFactory;
import com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectDeserializer;
import com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectSerializer;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEntity;
import com.sap.cloud.sdk.result.ElementName;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

class PropertyCustomSerializationTest
{
    private static final User USER =
        new User(
            LocalDate.of(1960, Month.MAY, 31).atTime(18, 0).atOffset(ZoneOffset.ofHours(1)),
            LocalDate.of(2020, Month.FEBRUARY, 20).atStartOfDay().atZone(ZoneOffset.UTC),
            LocalDate.of(2005, Month.DECEMBER, 24).atStartOfDay());

    @EqualsAndHashCode( callSuper = true )
    @RequiredArgsConstructor
    @AllArgsConstructor
    @JsonAdapter( GsonVdmAdapterFactory.class )
    @JsonSerialize( using = JacksonVdmObjectSerializer.class )
    @JsonDeserialize( using = JacksonVdmObjectDeserializer.class )
    public static class User extends VdmEntity<User>
    {
        @Getter
        private final String entityCollection = "People";

        @Getter
        private final String odataType = "Example.User";

        @Getter
        private final Class<User> type = User.class;

        @ElementName( "DateOfBirth" )
        private OffsetDateTime dateOfBirth;

        @ElementName( "LocaleTime" )
        @JsonSerialize( using = CustomZonedDateSerializer.class )
        @JsonDeserialize( using = CustomZonedDateDeserializer.class )
        @JsonAdapter( ZonedDateTimeAdapter.class )
        private ZonedDateTime localeTime;

        @ElementName( "RegistrationDate" )
        @JsonSerialize( using = CustomLocalDateSerializer.class )
        @JsonDeserialize( using = CustomLocalDateDeserializer.class )
        @JsonAdapter( LocalDateTimeAdapter.class )
        private LocalDateTime registrationDate;
    }

    @Test
    void testSerializationWithGson()
    {
        final String json = new Gson().toJson(USER);
        final User actual = new Gson().fromJson(json, User.class);
        assertThat(actual).isEqualTo(USER);
    }

    @Test
    void testSerializationWithJackson()
        throws JsonProcessingException
    {
        final String json = new ObjectMapper().writeValueAsString(USER);
        final User actual = new ObjectMapper().readValue(json, User.class);
        assertThat(actual).isEqualTo(USER);
    }

    // Custom serializers and deserializers

    public static class CustomLocalDateSerializer extends JsonSerializer<LocalDateTime>
    {
        @Override
        public void serialize( final LocalDateTime val, final JsonGenerator gen, final SerializerProvider serializers )
            throws IOException
        {
            gen.writeString(val.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }
    }

    public static class CustomZonedDateSerializer extends JsonSerializer<ZonedDateTime>
    {
        @Override
        public void serialize( final ZonedDateTime val, final JsonGenerator gen, final SerializerProvider serializers )
            throws IOException
        {
            gen.writeString(val.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }
    }

    public static class CustomLocalDateDeserializer extends JsonDeserializer<LocalDateTime>
    {
        @Override
        public LocalDateTime deserialize( final JsonParser p, final DeserializationContext ctxt )
            throws IOException
        {
            return OffsetDateTime.parse(p.getValueAsString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();
        }
    }

    public static class CustomZonedDateDeserializer extends JsonDeserializer<ZonedDateTime>
    {
        @Override
        public ZonedDateTime deserialize( final JsonParser p, final DeserializationContext ctxt )
            throws IOException
        {
            return OffsetDateTime.parse(p.getValueAsString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).toZonedDateTime();
        }
    }

    public static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime>
    {
        @Override
        public void write( final JsonWriter out, final LocalDateTime value )
            throws IOException
        {
            out.value(value.atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }

        @Override
        public LocalDateTime read( final JsonReader in )
            throws IOException
        {
            return OffsetDateTime.parse(in.nextString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();
        }
    }

    public static class ZonedDateTimeAdapter extends TypeAdapter<ZonedDateTime>
    {
        @Override
        public void write( final JsonWriter out, final ZonedDateTime value )
            throws IOException
        {
            out.value(value.toOffsetDateTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }

        @Override
        public ZonedDateTime read( final JsonReader in )
            throws IOException
        {
            return OffsetDateTime.parse(in.nextString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).toZonedDateTime();
        }
    }
}
