/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.annotation.Nonnull;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.google.gson.annotations.JsonAdapter;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.LocalDateTimeCalendarConverter;
import com.sap.cloud.sdk.typeconverter.AbstractTypeConverter;
import com.sap.cloud.sdk.typeconverter.ConvertedObject;
import com.sap.cloud.sdk.typeconverter.exception.ObjectNotConvertibleException;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@WireMockTest
class CustomFieldTypeConverterTest
{
    private static final String ODATA_ENDPOINT_URL = "/service/path";
    private static final String ENTITY_SET = "A_TestEntity";
    private static final String MY_CUSTOM_FIELD = "MyCustomField";
    private static final String RESPONSE = """
        {
          "d": {
            "results": [
              {
                "__metadata": {
                  "id": "https://127.0.0.1/service/path/A_TestEntity('1')",
                  "uri": "https://127.0.0.1/service/path/A_TestEntity('1')",
                  "type": "API_TEST.A_TestEntityType"
                },
                "Id": "1",
                "SomeField": "123",
                "ETag": "SOME_ETAG",
                "MyCustomField": %1$s
              }
            ]
          }
        }
        """;

    private DefaultHttpDestination destination;

    @BeforeEach
    void before( @Nonnull final WireMockRuntimeInfo wm )
    {
        destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
    }

    @Data
    @EqualsAndHashCode( callSuper = true )
    @JsonAdapter( com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory.class )
    public static class TestEntity extends VdmEntity<TestEntity>
    {
        private final String entityCollection = ENTITY_SET;
        private final Class<TestEntity> type = TestEntity.class;
    }

    @Getter
    private static class LocalDateTimeBooleanConverter extends AbstractTypeConverter<LocalDateTime, Boolean>
    {
        private final Class<LocalDateTime> type = LocalDateTime.class;
        private final Class<Boolean> domainType = Boolean.class;

        @Nonnull
        @Override
        public ConvertedObject<Boolean> toDomainNonNull( @Nonnull final LocalDateTime object )
            throws Exception
        {
            throw new Exception("This implementation should fail.");
        }

        @Nonnull
        @Override
        public ConvertedObject<LocalDateTime> fromDomainNonNull( @Nonnull final Boolean domainObject )
            throws Exception
        {
            throw new Exception("This implementation should fail.");
        }
    }

    @Test
    void calendarToLocalDateTimeOnGet()
    {
        mockResponses("\"/Date(1507075200000)/\"");

        final EntityField<TestEntity, LocalDateTime> myCustomField =
            new EntityField<>(MY_CUSTOM_FIELD, new LocalDateTimeCalendarConverter());

        final TestEntity testEntity =
            FluentHelperFactory
                .withServicePath(ODATA_ENDPOINT_URL)
                .read(TestEntity.class, ENTITY_SET)
                .executeRequest(destination)
                .get(0);

        final LocalDateTime customField = testEntity.getCustomField(myCustomField);

        assertThat(customField).isInstanceOf(LocalDateTime.class);
        assertThat(customField).isEqualTo(LocalDateTime.of(2017, Month.OCTOBER, 4, 0, 0, 0));
    }

    @Test
    void returnsNullOnNullValue()
    {
        mockResponses("null");
        final EntityField<TestEntity, LocalDateTime> myCustomField =
            new EntityField<>(MY_CUSTOM_FIELD, new LocalDateTimeCalendarConverter());

        final TestEntity testEntity =
            FluentHelperFactory
                .withServicePath(ODATA_ENDPOINT_URL)
                .read(TestEntity.class, ENTITY_SET)
                .executeRequest(destination)
                .get(0);

        final LocalDateTime customField = testEntity.getCustomField(myCustomField);
        assertThat(customField).isNull();
    }

    @Test
    void failsOnConversionErrorOnGet()
    {
        mockResponses("\"/Date(1507075200000)/\"");
        final EntityField<TestEntity, LocalDateTime> myCustomField =
            new EntityField<>(MY_CUSTOM_FIELD, new LocalDateTimeBooleanConverter());

        final TestEntity testEntity =
            FluentHelperFactory
                .withServicePath(ODATA_ENDPOINT_URL)
                .read(TestEntity.class, ENTITY_SET)
                .executeRequest(destination)
                .get(0);

        assertThatThrownBy(() -> testEntity.getCustomField(myCustomField))
            .isExactlyInstanceOf(ObjectNotConvertibleException.class);
    }

    // this test verifies backwards compatibility of the API changes
    @Test
    void entityFieldWithoutConverterStillWorksForGet()
    {
        mockResponses("\"/Date(1507075200000)/\"");

        final EntityField<TestEntity, GregorianCalendar> myCustomField = new EntityField<>(MY_CUSTOM_FIELD);

        final TestEntity TestEntity =
            FluentHelperFactory
                .withServicePath(ODATA_ENDPOINT_URL)
                .read(TestEntity.class, ENTITY_SET)
                .executeRequest(destination)
                .get(0);

        final GregorianCalendar customField = TestEntity.getCustomField(myCustomField);
        assertThat(customField).isNotNull();
    }

    @Test
    void localDateTimeToCalendarOnSet()
    {
        final EntityField<TestEntity, LocalDateTime> myCustomField =
            new EntityField<>(MY_CUSTOM_FIELD, new LocalDateTimeCalendarConverter());

        final TestEntity TestEntity = new TestEntity();
        TestEntity.setCustomField(myCustomField, LocalDateTime.of(2017, Month.OCTOBER, 4, 0, 0, 0));

        final Object myCustomFieldValue = TestEntity.getCustomField(MY_CUSTOM_FIELD);
        assertThat(myCustomFieldValue).isInstanceOf(GregorianCalendar.class);
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();
        calendar.set(2017, Calendar.OCTOBER, 4, 0, 0, 0);
        assertThat(myCustomFieldValue).isEqualTo(calendar);
    }

    @Test
    void setsNullOnNullValue()
    {
        final EntityField<TestEntity, LocalDateTime> myCustomField =
            new EntityField<>(MY_CUSTOM_FIELD, new LocalDateTimeCalendarConverter());

        final TestEntity testEntity = new TestEntity();
        testEntity.setCustomField(myCustomField, null);

        final Object myCustomFieldValue = testEntity.getCustomField(MY_CUSTOM_FIELD);
        assertThat(myCustomFieldValue).isNull();
        final LocalDateTime customField = testEntity.getCustomField(myCustomField);
        assertThat(customField).isNull();
    }

    @Test
    void failsOnConversionErrorOnSet()
    {
        final EntityField<TestEntity, LocalDateTime> myCustomField =
            new EntityField<>(MY_CUSTOM_FIELD, new LocalDateTimeBooleanConverter());

        final TestEntity testEntity = new TestEntity();
        assertThatThrownBy(
            () -> testEntity.setCustomField(myCustomField, LocalDateTime.of(2017, Month.OCTOBER, 4, 0, 0, 0)))
            .isExactlyInstanceOf(ObjectNotConvertibleException.class);
    }

    // this test verifies backwards compatibility of the API changes
    @Test
    void entityFieldWithoutConverterStillWorksForSet()
    {
        final EntityField<TestEntity, Calendar> myCustomField = new EntityField<>(MY_CUSTOM_FIELD);

        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();
        calendar.set(2017, Calendar.OCTOBER, 4, 0, 0, 0);

        final TestEntity testEntity = new TestEntity();
        testEntity.setCustomField(myCustomField, calendar);
    }

    @Test
    void entityFieldHasNoTypeConverterWhenRetrievedWithoutTypeConverter()
    {
        final EntityField<TestEntity, LocalDateTime> myCustomField = new EntityField<>(MY_CUSTOM_FIELD);
        Assertions.assertThat(myCustomField.getTypeConverter()).isNull();
    }

    @Test
    void entityFieldHasTypeConverterWhenRetrievedWithTypeConverter()
    {
        final EntityField<TestEntity, LocalDateTime> myCustomField =
            new EntityField<>(MY_CUSTOM_FIELD, new LocalDateTimeCalendarConverter());
        Assertions.assertThat(myCustomField.getTypeConverter()).isNotNull();
        Assertions.assertThat(myCustomField.getFieldName()).isEqualTo(MY_CUSTOM_FIELD);
    }

    private void mockResponses( final String customFieldValue )
    {
        final String mockedUrl = ODATA_ENDPOINT_URL + "/" + ENTITY_SET;
        stubFor(get(urlEqualTo(mockedUrl)).willReturn(okJson(String.format(RESPONSE, customFieldValue))));
    }
}
