/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.sap.cloud.sdk.result.GsonResultElementFactory;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoRecordFieldIterator;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

import lombok.extern.slf4j.Slf4j;

/**
 * Creates JSON elements.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Slf4j
@Deprecated
public class JCoFieldToResultReader
{
    private static final String DATE_TIME_PATTERN = "yyyyMMdd HHmmss";

    @Nullable
    private JsonElement toJsonPrimitive( @Nullable final Object value )
    {
        if( value == null ) {
            return null;
        }

        if( value instanceof Boolean ) {
            return new JsonPrimitive((Boolean) value);
        }

        if( value instanceof Number ) {
            return new JsonPrimitive((Number) value);
        }

        if( value instanceof Character ) {
            return new JsonPrimitive((Character) value);
        }

        if( value instanceof byte[] ) {
            final byte[] byteArray = (byte[]) value;
            return new JsonPrimitive(new String(byteArray, StandardCharsets.UTF_8));
        }

        if( value instanceof Date ) {
            final DateFormat df = new SimpleDateFormat(DATE_TIME_PATTERN);
            final String dateString = df.format((Date) value);
            return new JsonPrimitive(dateString);
        }

        return new JsonPrimitive(value.toString());
    }

    private JsonObject toJsonObject( final JCoStructure structure )
    {
        final JsonObject jsonObject = new JsonObject();

        final JCoRecordFieldIterator it = structure.getRecordFieldIterator();
        while( it.hasNextField() ) {
            final JCoField nestedField = it.nextField();
            final JsonElement nested = toJsonElement(nestedField);
            jsonObject.add(nestedField.getName(), nested);
        }

        return jsonObject;
    }

    private JsonArray toJsonArray( final JCoTable table )
    {
        final JsonArray jsonArray = new JsonArray();

        for( int rowId = 0; rowId < table.getNumRows(); ++rowId ) {
            table.setRow(rowId);

            final JsonObject jsonObject = new JsonObject();

            final JCoRecordFieldIterator it = table.getRecordFieldIterator();
            while( it.hasNextField() ) {
                final JCoField nestedField = it.nextField();

                final JsonElement nested = toJsonElement(nestedField);
                jsonObject.add(nestedField.getName(), nested);
            }

            jsonArray.add(jsonObject);
        }

        return jsonArray;
    }

    @Nullable
    private JsonElement toJsonElement( final JCoField field )
    {
        log
            .trace(
                "Converting {} with name {} to {}.",
                JCoField.class.getSimpleName(),
                field.getName(),
                JsonElement.class.getSimpleName());

        if( field.isStructure() ) {
            return toJsonObject(field.getStructure());
        } else if( field.isTable() ) {
            return toJsonArray(field.getTable());
        } else {
            return toJsonPrimitive(field.getValue());
        }
    }

    /**
     * Creates a new result.
     *
     * @param field
     *            The field to create the result from.
     * @param resultElementFactory
     *            The factory to create the result element from.
     * @return The new result.
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequestResult.Result
        newResult( @Nonnull final JCoField field, @Nonnull final GsonResultElementFactory resultElementFactory )
    {
        return new AbstractRemoteFunctionRequestResult.Result(
            field.getName(),
            resultElementFactory.create(toJsonElement(field)));
    }
}
