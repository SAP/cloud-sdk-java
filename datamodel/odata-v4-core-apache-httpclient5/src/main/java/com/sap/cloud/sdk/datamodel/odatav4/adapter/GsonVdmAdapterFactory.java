package com.sap.cloud.sdk.datamodel.odatav4.adapter;

import java.math.BigDecimal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEnum;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmObject;

import lombok.extern.slf4j.Slf4j;

/**
 * General purpose VDM adapter factory.
 */
@Slf4j
public class GsonVdmAdapterFactory implements TypeAdapterFactory
{
    @SuppressWarnings( "unchecked" )
    @Override
    @Nullable
    public <T> TypeAdapter<T> create( @Nonnull final Gson gson, @Nonnull final TypeToken<T> type )
    {
        final Class<? super T> rawType = type.getRawType();
        if( VdmEnum.class.isAssignableFrom(rawType) ) {
            return (TypeAdapter<T>) new GsonVdmEnumAdapter<>((Class<VdmEnum>) rawType);
        }
        if( VdmObject.class.isAssignableFrom(rawType) ) {
            return (TypeAdapter<T>) new GsonVdmEntityAdapter<>(this, gson, rawType);
        }
        if( BigDecimal.class.isAssignableFrom(rawType) ) {
            return (TypeAdapter<T>) new CustomBigDecimalTypeAdapter();
        }

        for( final ODataGenericConverter<?> converter : ODataGenericConverter.DEFAULT_CONVERTERS ) {
            if( converter.getType().isAssignableFrom(rawType) ) {
                return (TypeAdapter<T>) GsonODataConverterAdapter.of(converter);
            }
        }
        log.trace("Could not find custom type adapter for type {}.", rawType);
        return null;
    }
}
