/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.typeconverter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.assertj.core.api.Condition;
import org.assertj.core.util.Lists;
import org.junit.Test;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.sap.cloud.sdk.result.DefaultCollectedResultCollection;
import com.sap.cloud.sdk.result.GsonResultElementFactory;
import com.sap.cloud.sdk.result.GsonResultPrimitive;
import com.sap.cloud.sdk.result.PrimitiveBasedObjectExtractor;
import com.sap.cloud.sdk.result.ResultElement;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

public class PrimitiveBasedObjectTest
{
    @Data
    private static final class MyObject
    {
        final Object value;
    }

    @Data
    private static final class MyOptional
    {
        final Boolean option;
    }

    @Data
    private static final class MyYear
    {
        final int year;
    }

    @Data
    private static final class MyYearWithLongValue
    {
        final long year;
    }

    @RequiredArgsConstructor( access = AccessLevel.PRIVATE, staticName = "of" )
    @Data
    private static final class MyYearWithPrivateConstructor
    {
        private final int year;
    }

    @Test
    public void fromString()
    {
        final ResultElement element = new GsonResultPrimitive(new JsonPrimitive("foo.txt"));
        final File f = new PrimitiveBasedObjectExtractor<>(File.class).extract(element);
        assertThat(f).isEqualTo(new File("foo.txt"));
    }

    @Test
    public void fromLong()
    {
        final ResultElement element = new GsonResultPrimitive(new JsonPrimitive(10L));
        final MyYearWithLongValue f = new PrimitiveBasedObjectExtractor<>(MyYearWithLongValue.class).extract(element);
        assertThat(f).isEqualTo(new MyYearWithLongValue(10L));
    }

    @Test
    public void fromLongList()
    {
        final List<Random> rnds = collectFromJson(Random.class, 10L, 100L, 1000L, 10000L);
        assertThat(rnds).hasSize(4).are(new Condition<Random>()
        {
            @Override
            public boolean matches( final Random random )
            {
                return Lists
                    .newArrayList(
                        new Random(10L).nextDouble(),
                        new Random(100L).nextDouble(),
                        new Random(1000L).nextDouble(),
                        new Random(10000L).nextDouble())
                    .contains(random.nextDouble());
            }
        });
    }

    @Test
    public void fromInteger()
    {
        final ResultElement element = new GsonResultPrimitive(new JsonPrimitive(1996));
        final MyYear f = new PrimitiveBasedObjectExtractor<>(MyYear.class).extract(element);
        assertThat(f).isEqualTo(new MyYear(1996));
    }

    @Test
    public void fromPrivateConstructor()
    {
        final ResultElement element = new GsonResultPrimitive(new JsonPrimitive(1996));
        final MyYearWithPrivateConstructor f =
            new PrimitiveBasedObjectExtractor<>(MyYearWithPrivateConstructor.class).extract(element);
        assertThat(f).isEqualTo(MyYearWithPrivateConstructor.of(1996));
    }

    @Test
    public void fromBooleanList()
    {
        final List<MyOptional> b = collectFromJson(MyOptional.class, true, false, false, true);
        assertThat(b)
            .hasSize(4)
            .containsExactly(new MyOptional(true), new MyOptional(false), new MyOptional(false), new MyOptional(true));
    }

    @Test( expected = UnsupportedOperationException.class )
    public void fromObject()
    {
        collectFromJson(MyObject.class, null, null);
    }

    private <T> List<T> collectFromJson( final Class<T> cls, final Object... elements )
    {
        final String attributeName = "value";
        final GsonBuilder builder = new GsonBuilder();

        final List<Map<String, Object>> input = new ArrayList<>();

        for( final Object element : elements ) {
            final Map<String, Object> o = new HashMap<>();
            o.put(attributeName, element);
            input.add(o);
        }

        final String json = builder.create().toJson(input);
        final ResultElement resultElement = new GsonResultElementFactory(builder).create(JsonParser.parseString(json));

        assertThat(resultElement).isNotNull();
        assertThat(resultElement.isResultCollection()).isTrue();

        return new DefaultCollectedResultCollection(attributeName, resultElement.getAsCollection()).asList(cls);
    }
}
