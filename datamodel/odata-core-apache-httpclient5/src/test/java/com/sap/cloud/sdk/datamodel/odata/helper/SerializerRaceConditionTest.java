package com.sap.cloud.sdk.datamodel.odata.helper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;

class SerializerRaceConditionTest
{
    @SneakyThrows
    @Test
    void testRaceCondition()
    {
        final TestVdmEntity entity = new TestVdmEntity();
        entity.setStringValue("string");
        entity.setDoubleValue(13.37);
        entity.setBooleanValue(true);
        entity.setIntegerValue(42);
        entity.setDecimalValue(BigDecimal.ONE);
        entity.setLocalDateTimeValue(LocalDateTime.of(2000, 1, 1, 0, 0));
        entity.setOffsetDateTimeValue(ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC));

        final String EXPECTED_JSON =
            "{\"IntegerValue\":42,\"StringValue\":\"string\",\"OffsetDateTimeValue\":\"/Date(946684800000)/\",\"DecimalValue\":\"1\",\"DoubleValue\":\"13.37\",\"LocalDateTimeValue\":\"/Date(946684800000)/\",\"BooleanValue\":true}";

        final Collection<Callable<String>> tasks =
            Collections.nCopies(10_000, () -> ODataEntitySerializer.serializeEntityForCreate(entity));

        final ExecutorService executor = Executors.newCachedThreadPool();
        for( final Future<String> future : executor.invokeAll(tasks) ) {
            assertThat(future.get()).isEqualTo(EXPECTED_JSON);
        }
    }
}
