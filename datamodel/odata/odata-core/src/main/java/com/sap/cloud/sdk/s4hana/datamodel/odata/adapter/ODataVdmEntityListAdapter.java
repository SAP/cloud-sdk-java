/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * For internal use only by data model classes
 *
 * @param <T>
 *            The generic value type.
 */
public class ODataVdmEntityListAdapter<T> extends TypeAdapter<List<T>>
{
    private final Gson gson;
    private final TypeAdapter<T> entityAdapter;

    /**
     * For internal use only by data model classes
     *
     * @param gson
     *            The GSON instance to access serialization and desrialization.
     * @param entityAdapter
     *            The entity type adapter to be used.
     */
    public ODataVdmEntityListAdapter( @Nonnull final Gson gson, @Nonnull final TypeAdapter<T> entityAdapter )
    {
        this.gson = gson;
        this.entityAdapter = entityAdapter;
    }

    private List<T> readArray( final JsonReader in )
        throws IOException
    {
        in.beginArray();
        final List<T> entityList = new ArrayList<>();

        while( in.hasNext() ) {
            @SuppressWarnings( "unchecked" )
            final T entity = entityAdapter.read(in);
            entityList.add(entity);
        }

        in.endArray();
        return entityList;
    }

    /**
     * For internal use only by data model classes
     */
    @Override
    @Nullable
    public List<T> read( @Nonnull final JsonReader in )
        throws IOException
    {
        List<T> entityList = null;
        if( in.peek() == JsonToken.BEGIN_OBJECT ) {
            in.beginObject();
            if( in.peek() == JsonToken.NAME ) {
                final String resultsKey = in.nextName();
                if( "results".equals(resultsKey) && in.peek() == JsonToken.BEGIN_ARRAY ) {
                    entityList = readArray(in);
                } else {
                    in.skipValue();
                }
            }
            in.endObject();
        } else if( in.peek() == JsonToken.BEGIN_ARRAY ) {
            entityList = readArray(in);
        } else {
            in.skipValue();
        }
        return entityList;
    }

    /**
     * For internal use only by data model classes
     */
    @Override
    public void write( @Nonnull final JsonWriter out, @Nullable final List<T> entityList )
        throws IOException
    {
        if( entityList != null ) {
            final JsonArray entityListAsJson = new JsonArray();
            for( final T entity : entityList ) {
                entityListAsJson.add(entityAdapter.toJsonTree(entity));
            }
            gson.toJson(entityListAsJson, out);
        } else {
            out.nullValue();
        }
    }
}
