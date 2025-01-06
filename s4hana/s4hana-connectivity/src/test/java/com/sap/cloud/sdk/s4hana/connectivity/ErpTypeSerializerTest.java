/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;
import com.sap.cloud.sdk.typeconverter.ConvertedObject;

@Deprecated
class ErpTypeSerializerTest
{
    private static final LocalDate DATE = LocalDate.of(2017, 12, 31);
    private static final LocalTime TIME = LocalTime.of(13, 13, 13);

    private static final String DATE_STRING_DEFAULT = "20171231";
    private static final String TIME_STRING_DEFAULT = "131313";

    private static final String DATE_PATTERN_ISO = "yyyy-MM-dd";
    private static final String DATE_STRING_ISO = "2017-12-31";

    private static final String TIME_PATTERN_ISO = "HH:mm:ss";
    private static final String TIME_STRING_ISO = "13:13:13";

    @Test
    void testToErp()
    {
        final ErpTypeSerializer serializer = new ErpTypeSerializer();

        assertThat(serializer.toErp(new TestEntity("123")).get()).isEqualTo("0000000123");
        assertThat(serializer.toErp(123).get()).isEqualTo("123");
        assertThat(serializer.toErp(123.4d).get()).isEqualTo("123.4 ");
        assertThat(serializer.toErp(-123.4d).get()).isEqualTo("123.4-");
        assertThat(serializer.toErp(true).get()).isEqualTo("X");
        assertThat(serializer.toErp("test").get()).isEqualTo("test");

        assertThat(serializer.toErp(DATE).get()).isEqualTo(DATE_STRING_DEFAULT);

        serializer.withTypeConverters(new com.sap.cloud.sdk.s4hana.serialization.LocalDateConverter(DATE_PATTERN_ISO));

        assertThat(serializer.toErp(DATE).get()).isEqualTo(DATE_STRING_ISO);

        assertThat(serializer.toErp(TIME).get()).isEqualTo(TIME_STRING_DEFAULT);

        serializer.withTypeConverters(new com.sap.cloud.sdk.s4hana.serialization.LocalTimeConverter(TIME_PATTERN_ISO));

        assertThat(serializer.toErp(TIME).get()).isEqualTo(TIME_STRING_ISO);
    }

    @Test
    void testFromErp()
    {
        final ErpTypeSerializer serializer = new ErpTypeSerializer();

        assertThat(serializer.fromErp("0000000123", TestEntity.class).get()).isEqualTo(new TestEntity("123"));
        assertThat(serializer.fromErp("123", Integer.class).get()).isEqualTo(123);
        assertThat(serializer.fromErp("123.4 ", Double.class).get()).isEqualTo(123.4d);
        assertThat(serializer.fromErp("123.4-", Double.class).get()).isEqualTo(-123.4d);
        assertThat(serializer.fromErp("X", Boolean.class).get()).isTrue();
        assertThat(serializer.fromErp("test", String.class).get()).isEqualTo("test");

        assertThat(serializer.fromErp(DATE_STRING_DEFAULT, LocalDate.class).get()).isEqualTo(DATE);

        serializer.withTypeConverters(new com.sap.cloud.sdk.s4hana.serialization.LocalDateConverter(DATE_PATTERN_ISO));

        assertThat(serializer.fromErp(DATE_STRING_ISO, LocalDate.class).get()).isEqualTo(DATE);

        assertThat(serializer.fromErp(TIME_STRING_DEFAULT, LocalTime.class).get()).isEqualTo(TIME);

        serializer.withTypeConverters(new com.sap.cloud.sdk.s4hana.serialization.LocalTimeConverter(TIME_PATTERN_ISO));

        assertThat(serializer.fromErp(TIME_STRING_ISO, LocalTime.class).get()).isEqualTo(TIME);
    }

    @Test
    void testGsonSerialization()
    {
        final GsonBuilder gsonBuilder =
            new GsonBuilder().registerTypeAdapterFactory(new ErpTypeGsonTypeAdapterFactory());

        final TestEntity dummy = new TestEntity("foo");

        final String serializedJson = gsonBuilder.create().toJson(dummy);

        assertThat(serializedJson).isEqualTo("\"foo\"");
    }

    @Test
    void testGsonDeserialization()
    {
        final GsonBuilder gsonBuilder =
            new GsonBuilder().registerTypeAdapterFactory(new ErpTypeGsonTypeAdapterFactory());

        final TestEntity expected = new TestEntity("foo");

        final TestEntity actual = gsonBuilder.create().fromJson("\"foo\"", TestEntity.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testJacksonSerialization()
        throws JsonProcessingException
    {
        final ObjectMapper mapper = new ObjectMapper();

        final TestEntity dummy = new TestEntity("foo");

        final String serializedJson = mapper.writeValueAsString(dummy);

        assertThat(serializedJson).isEqualTo("\"foo\"");
    }

    @Test
    void testJacksonDeserialization()
        throws IOException
    {
        final ObjectMapper mapper = new ObjectMapper();

        final TestEntity expected = new TestEntity("foo");

        final TestEntity actual = mapper.readValue("\"foo\"", TestEntity.class);

        assertThat(actual).isEqualTo(expected);
    }

    private static class TestEntity extends com.sap.cloud.sdk.s4hana.serialization.StringBasedErpType<TestEntity>
    {
        private static final long serialVersionUID = 6989848703661704569L;

        public static final TestEntity EMPTY = new TestEntity("");

        public TestEntity( final String value ) throws IllegalArgumentException
        {
            super(value);
        }

        @Nullable
        public static TestEntity of( @Nullable final String value )
            throws IllegalArgumentException
        {
            if( value == null ) {
                return null;
            }

            return new TestEntity(value);
        }

        @Nonnull
        @Override
        public com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<TestEntity> getTypeConverter()
        {
            return TestEntityConverter.INSTANCE;
        }

        @Nonnull
        @Override
        public Class<TestEntity> getType()
        {
            return TestEntity.class;
        }

        @Override
        public int getMaxLength()
        {
            return 10;
        }

        @Nonnull
        @Override
        public FillCharStrategy getFillCharStrategy()
        {
            return FillCharStrategy.FILL_LEADING_IF_NUMERIC;
        }

        @Nonnull
        public static Set<TestEntity> toTestEntities( final Collection<String> values )
        {
            return values
                .stream()
                .map(
                    com.sap.cloud.sdk.s4hana.serialization.StringBasedErpType
                        .transformToType(new TestEntityConverter()))
                .collect(Collectors.toSet());
        }

        @Nonnull
        public static Set<String> toStrings( final Collection<TestEntity> values )
        {
            return values
                .stream()
                .map(
                    com.sap.cloud.sdk.s4hana.serialization.StringBasedErpType
                        .transformToString(new TestEntityConverter()))
                .collect(Collectors.toSet());
        }
    }

    /**
     * Type converter for {@link TestEntity}.
     */
    private static class TestEntityConverter
        extends
        com.sap.cloud.sdk.s4hana.serialization.AbstractErpTypeConverter<TestEntity>
    {
        public static final TestEntityConverter INSTANCE = new TestEntityConverter();

        @Nonnull
        @Override
        public Class<TestEntity> getType()
        {
            return TestEntity.class;
        }

        @Nonnull
        @Override
        public ConvertedObject<String> toDomainNonNull( @Nonnull final TestEntity object )
        {
            return ConvertedObject.of(object.toString());
        }

        @Nonnull
        @Override
        public ConvertedObject<TestEntity> fromDomainNonNull( @Nonnull final String domainObject )
        {
            return ConvertedObject.of(new TestEntity(domainObject));
        }
    }
}
