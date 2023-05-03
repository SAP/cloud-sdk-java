package com.sap.cloud.sdk.typeconverter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.sap.cloud.sdk.typeconverter.exception.ObjectNotConvertibleException;

public class ConvertedObjectTest
{
    @Test
    public void get()
    {
        final ConvertedObject<String> convertedObject = ConvertedObject.of("test");
        assertThat(convertedObject.get()).isEqualTo("test");
    }

    @Test( expected = ObjectNotConvertibleException.class )
    public void getNonConvertable()
    {
        final ConvertedObject<?> notConvertable = ConvertedObject.ofNotConvertible();
        notConvertable.get();
    }

    @Test
    public void orNull()
    {
        assertThat(ConvertedObject.of("test").orNull()).isEqualTo("test");
        assertThat(ConvertedObject.ofNotConvertible().orNull()).isNull();
    }

    @Test
    public void or()
    {
        assertThat(ConvertedObject.of("test").orElse("something")).isEqualTo("test");
        assertThat(ConvertedObject.ofNotConvertible().orElse("something")).isEqualTo("something");
    }

    @Test
    public void fromConverted()
    {
        assertThat(ConvertedObject.of(123).get()).isEqualTo(123);
        assertThat(ConvertedObject.of(null).get()).isNull();
    }

    @Test
    public void fromNull()
    {
        assertThat(ConvertedObject.ofNull().get()).isNull();
    }
}
