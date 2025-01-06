/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.adapter;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * Jackson serializer adapter for {@link VdmEnum} types.
 */
@Slf4j
public class JacksonVdmEnumSerializer extends StdSerializer<VdmEnum> implements ContextualSerializer
{
    private static final long serialVersionUID = -5794503059647076980L;

    /**
     * Default constructor.
     */
    protected JacksonVdmEnumSerializer()
    {
        super((Class<VdmEnum>) null);
    }

    @Override
    public void serialize(
        @Nullable final VdmEnum object,
        @Nonnull final JsonGenerator gen,
        @Nonnull final SerializerProvider prov )
        throws IOException
    {
        if( object == null ) {
            gen.writeNull();
            return;
        }
        final String enumName = object.getName();
        gen.writeString(enumName);
    }

    @Override
    @Nonnull
    public
        JsonSerializer<?>
        createContextual( @Nonnull final SerializerProvider prov, @Nullable final BeanProperty property )
    {
        final JacksonVdmEnumSerializer serializer = new JacksonVdmEnumSerializer();
        return serializer;
    }
}
