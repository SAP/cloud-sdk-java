/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.adapter;

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
 * For internal use only by data model classes.
 *
 * @param <T>
 *            The entity type.
 */
public class GsonVdmEntityListAdapter<T> extends TypeAdapter<List<T>>
{
    @Nonnull
    private final Gson gson;
    @Nonnull
    private final TypeAdapter<T> entityAdapter;

    /**
     * For internal use only by data model classes.
     *
     * @param gson
     *            The GSON instance.
     * @param entityAdapter
     *            The entity adapter.
     */
    public GsonVdmEntityListAdapter( @Nonnull final Gson gson, @Nonnull final TypeAdapter<T> entityAdapter )
    {
        this.gson = gson;
        this.entityAdapter = entityAdapter;
    }

    private List<T> readArray( @Nonnull final JsonReader in )
        throws IOException
    {
        in.beginArray();
        final List<T> entityList = new ArrayList<>();

        while( in.hasNext() ) {
            final T entity = entityAdapter.read(in);
            entityList.add(entity);
        }

        in.endArray();
        return entityList;
    }

    /**
     * For internal use only by data model classes.
     *
     * @param in
     *            The JsonReader reference.
     * @return The deserialized List of entities.
     * @throws IOException
     *             When deserialization failed.
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
     * For internal use only by data model classes.
     *
     * @param out
     *            The JsonWriter reference.
     * @param entityList
     *            The list of entities.
     * @throws IOException
     *             When serialization failed.
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
