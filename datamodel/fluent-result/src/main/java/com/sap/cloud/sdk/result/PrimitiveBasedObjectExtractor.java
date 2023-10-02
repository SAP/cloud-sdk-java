/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.result;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This class implements the basic {@link ObjectExtractor} interface, offering a method to create an object by
 * extracting arbitrary input data of type {@link ResultElement}. The created object will be of the same target type as
 * declared in the {@link PrimitiveBasedObjectExtractor} class constructor. The target type will be dynamically checked
 * for parametrized constructors which fit the given input data. A fitting constructor will be used for object creation.
 * <p>
 *
 * This implementation will only support extraction of {@link ResultPrimitive}.
 * <p>
 *
 * Constructors with <strong>primitive</strong> parameters are prioritized.
 *
 * @param <T>
 *            class type of target object for extraction (deserialization)
 */
@RequiredArgsConstructor
@Slf4j
public class PrimitiveBasedObjectExtractor<T> implements ObjectExtractor<T>
{
    private final Class<T> objectType;

    @Data
    private static class SingleParameterConstructor
    {
        private final Constructor<?> constructor;
        private final Object parameterValue;
    }

    @Nonnull
    @Override
    public T extract( @Nonnull final ResultElement resultElement )
    {
        if( !(resultElement instanceof ResultPrimitive) ) {
            throw new UnsupportedOperationException(
                "Failed to instantiate " + objectType.getName() + " with non-primitive value " + resultElement + ".");
        }

        try {
            SingleParameterConstructor construction = null;
            for( final Constructor<?> constructor : objectType.getDeclaredConstructors() ) {
                final Class<?>[] parameters = constructor.getParameterTypes();
                if( parameters.length == 1 ) {
                    final Object parameterValue = getParameterValue(resultElement, parameters[0]);
                    // check whether value could be instantiated and prioritize constructor with primitive argument
                    if( parameterValue != null && (construction == null || parameters[0].isPrimitive()) ) {
                        try {
                            constructor.setAccessible(true);
                            construction = new SingleParameterConstructor(constructor, parameterValue);
                        }
                        catch( final SecurityException e ) {
                            if( log.isDebugEnabled() ) {
                                log
                                    .debug(
                                        "Constructor for "
                                            + objectType.getName()
                                            + " with "
                                            + parameters[0].getSimpleName()
                                            + " parameter is not accessible. Please check the active Java Security Manager.",
                                        e);
                            }
                        }
                    }
                }
            }
            if( construction != null ) {
                return objectType.cast(construction.getConstructor().newInstance(construction.getParameterValue()));
            }
        }
        //TODO Handle this Exception to different Log Levels, based on where it is called from
        catch( final Exception e ) {
            throw new UnsupportedOperationException(
                "Failed to instantiate "
                    + objectType.getName()
                    + " with constructor by value "
                    + resultElement
                    + " - "
                    + e.getMessage()
                    + ".",
                e);
        }
        throw new UnsupportedOperationException(
            "Unsupported constructor of " + objectType.getName() + " to value " + resultElement + ".");
    }

    @Nullable
    private Object getParameterValue( final ResultElement resultElement, final Class<?> parameter )
    {
        if( parameter.isAssignableFrom(String.class) ) {
            return resultElement.asString();
        } else if( parameter.isAssignableFrom(BigDecimal.class) ) {
            return resultElement.asBigDecimal();
        } else if( parameter.isAssignableFrom(BigInteger.class) ) {
            return resultElement.asBigInteger();
        } else if( parameter == Character.TYPE || parameter.isAssignableFrom(Character.class) ) {
            return resultElement.asCharacter();
        } else if( parameter == Double.TYPE || parameter.isAssignableFrom(Double.class) ) {
            return resultElement.asDouble();
        } else if( parameter == Float.TYPE || parameter.isAssignableFrom(Float.class) ) {
            return resultElement.asFloat();
        } else if( parameter == Long.TYPE || parameter.isAssignableFrom(Long.class) ) {
            return resultElement.asLong();
        } else if( parameter == Integer.TYPE || parameter.isAssignableFrom(Integer.class) ) {
            return resultElement.asInteger();
        } else if( parameter == Short.TYPE || parameter.isAssignableFrom(Short.class) ) {
            return resultElement.asShort();
        } else if( parameter == Byte.TYPE || parameter.isAssignableFrom(Byte.class) ) {
            return resultElement.asByte();
        } else if( parameter == Boolean.TYPE || parameter.isAssignableFrom(Boolean.class) ) {
            return resultElement.asBoolean();
        } else {
            return null;
        }
    }
}
