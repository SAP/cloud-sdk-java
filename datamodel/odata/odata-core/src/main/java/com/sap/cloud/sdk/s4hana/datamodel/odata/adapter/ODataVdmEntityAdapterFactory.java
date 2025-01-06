/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmObject;

/**
 * For internal use only by data model classes.
 */
@SuppressWarnings( "unchecked" )
public class ODataVdmEntityAdapterFactory implements TypeAdapterFactory
{
    /**
     * For internal use only by data model classes.
     *
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public <T> TypeAdapter<T> create( @Nonnull final Gson gson, @Nonnull final TypeToken<T> type )
    {
        final Class<? super T> entityType = type.getRawType();

        if( VdmObject.class.isAssignableFrom(entityType) ) {
            return (TypeAdapter<T>) new ODataVdmEntityAdapter<>(this, gson, entityType);
        }
        return null;
    }
}
