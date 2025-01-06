/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.adapter;

import java.io.IOException;
import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEnum;

import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;

/**
 * Jackson deserializer adapter for {@link VdmEnum} types.
 */
@Slf4j
public class JacksonVdmEnumDeserializer extends StdDeserializer<VdmEnum> implements ContextualDeserializer
{
    private static final long serialVersionUID = 3282694560867458205L;
    private ImmutableMap<String, VdmEnum> nameEnumLookupMap = ImmutableMap.of();
    private Class<? extends VdmEnum> valueType;

    /**
     * Default constructor.
     */
    protected JacksonVdmEnumDeserializer()
    {
        super((Class<VdmEnum>) null);
    }

    @Override
    @Nonnull
    public
        JacksonVdmEnumDeserializer
        createContextual( @Nonnull final DeserializationContext ctxt, @Nullable final BeanProperty property )
    {
        final JavaType valueType =
            Option
                .of(property)
                .map(BeanProperty::getType)
                .map(t -> t.containedType(0))
                .getOrElse(ctxt::getContextualType);
        final JacksonVdmEnumDeserializer deserializer = new JacksonVdmEnumDeserializer();

        @SuppressWarnings( "unchecked" )
        final Class<? extends VdmEnum> enumType = (Class<? extends VdmEnum>) valueType.getRawClass();
        final VdmEnum[] constants = enumType.getEnumConstants();
        if( constants == null ) {
            throw new IllegalStateException("Enum type " + enumType.getSimpleName() + " has no enum constants.");
        }
        deserializer.valueType = enumType;
        deserializer.nameEnumLookupMap = Maps.uniqueIndex(Arrays.asList(constants), VdmEnum::getName);
        return deserializer;
    }

    @Override
    @Nullable
    public VdmEnum deserialize( @Nonnull final JsonParser parser, @Nonnull final DeserializationContext ctxt )
        throws IOException
    {
        final JsonToken currentToken = parser.currentToken();
        if( currentToken == JsonToken.VALUE_NULL ) {
            return null;
        }
        if( currentToken != JsonToken.VALUE_STRING ) {
            throw new IOException(
                String.format("Failed to deserialize enum type %s from token kind %s", valueType, currentToken));
        }

        final String serializedValue = parser.getValueAsString();
        return nameEnumLookupMap.getOrDefault(serializedValue, null);
    }
}
