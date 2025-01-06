package com.sap.cloud.sdk.typeconverter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.typeconverter.exception.ObjectNotConvertibleException;

class ConvertedObjectTest
{
    @Test
    void get()
    {
        final ConvertedObject<String> convertedObject = ConvertedObject.of("test");
        assertThat(convertedObject.get()).isEqualTo("test");
    }

    @Test
    void getNonConvertable()
    {
        final ConvertedObject<?> notConvertable = ConvertedObject.ofNotConvertible();
        assertThatThrownBy(notConvertable::get).isInstanceOf(ObjectNotConvertibleException.class);
    }

    @Test
    void orNull()
    {
        assertThat(ConvertedObject.of("test").orNull()).isEqualTo("test");
        assertThat(ConvertedObject.ofNotConvertible().orNull()).isNull();
    }

    @Test
    void or()
    {
        assertThat(ConvertedObject.of("test").orElse("something")).isEqualTo("test");
        assertThat(ConvertedObject.ofNotConvertible().orElse("something")).isEqualTo("something");
    }

    @Test
    void fromConverted()
    {
        assertThat(ConvertedObject.of(123).get()).isEqualTo(123);
        assertThat(ConvertedObject.of(null).get()).isNull();
    }

    @Test
    void fromNull()
    {
        assertThat(ConvertedObject.ofNull().get()).isNull();
    }
}
