package com.sap.cloud.sdk.s4hana.connectivity;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of GSON {@link TypeAdapterFactory} that is able to generically handle all implementations of
 * {@link com.sap.cloud.sdk.s4hana.serialization.ErpType}.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class ErpTypeGsonTypeAdapterFactory implements TypeAdapterFactory
{
    @Slf4j
    private static class ErpTypeAdapter<ErpTypeT extends com.sap.cloud.sdk.s4hana.serialization.ErpType<ErpTypeT>>
        extends
        TypeAdapter<ErpTypeT>
    {
        @Nonnull
        private final Class<ErpTypeT> rawType;

        public ErpTypeAdapter( @Nonnull final Class<ErpTypeT> rawType )
        {
            this.rawType = rawType;
        }

        @Override
        public void write( @Nonnull final JsonWriter out, @Nullable final ErpTypeT value )
            throws IOException
        {
            if( value == null ) {
                out.nullValue();
            } else {
                out.value(value.getTypeConverter().toDomain(value).orNull());
            }
        }

        @SuppressWarnings( "unchecked" )
        @Override
        @Nullable
        public ErpTypeT read( @Nonnull final JsonReader reader )
            throws IOException
        {
            if( reader.peek() == JsonToken.NULL ) {
                reader.nextNull();
                return null;
            } else {
                try {
                    final Class<?> cls = Class.forName(rawType.getName());
                    final String erpObject = reader.nextString();

                    if( cls.isEnum() ) {
                        final ErpTypeT enumConstant = (ErpTypeT) cls.getEnumConstants()[0];
                        return enumConstant.getTypeConverter().fromDomain(erpObject).orNull();
                    } else {
                        final Constructor<?> stringConstructor = cls.getConstructor(String.class);

                        // an empty string is the "safest" string constructor parameter here
                        final ErpTypeT erpType = (ErpTypeT) stringConstructor.newInstance("");

                        return erpType.getTypeConverter().fromDomain(erpObject).orNull();
                    }
                }
                catch( final
                    InstantiationException
                        | IllegalAccessException
                        | ClassNotFoundException
                        | NoSuchMethodException
                        | InvocationTargetException e ) {
                    if( log.isWarnEnabled() ) {
                        log.warn("Failed to instantiate ERP type: " + rawType.getName() + ".", e);
                    }
                    return null;
                }
            }
        }
    }

    /**
     * Creates ERP type adapter for the given {@link com.sap.cloud.sdk.s4hana.serialization.ErpType}.
     */
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    @Override
    @Nullable
    public <T> TypeAdapter<T> create( @Nonnull final Gson gson, @Nonnull final TypeToken<T> type )
    {
        final Class<? super T> rawType = type.getRawType();

        if( com.sap.cloud.sdk.s4hana.serialization.ErpType.class.isAssignableFrom(rawType) ) {
            return (TypeAdapter<T>) new ErpTypeAdapter<>(
                (Class<com.sap.cloud.sdk.s4hana.serialization.ErpType>) rawType);
        }

        return null;
    }
}
