/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.adapter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotationMap;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.base.Functions;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEntity;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmObject;
import com.sap.cloud.sdk.result.ElementName;
import com.sap.cloud.sdk.typeconverter.ConvertedObject;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * Jackson serializer adapter for {@link VdmEntity} and {@link VdmObject} types.
 */
@Slf4j
public class JacksonVdmObjectSerializer extends StdSerializer<VdmObject<?>> implements ContextualSerializer
{
    private static final long serialVersionUID = 3559044362941940279L;

    /**
     * Default constructor.
     */
    protected JacksonVdmObjectSerializer()
    {
        super((Class<VdmObject<?>>) null);
    }

    @Override
    public void serialize(
        @Nullable final VdmObject<?> object,
        @Nonnull final JsonGenerator gen,
        @Nonnull final SerializerProvider prov )
        throws IOException
    {
        if( object == null ) {
            gen.writeNull();
            return;
        }
        final boolean nonNull =
            prov.getDefaultPropertyInclusion(object.getType()).getContentInclusion() == JsonInclude.Include.NON_NULL;

        gen.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
        gen.writeStartObject();

        // odata type
        final String odataType = "#" + object.getOdataType();
        gen.writeStringField(VdmObject.ODATA_TYPE_ANNOTATIONS[0], odataType);

        // odata version
        if( object instanceof VdmEntity ) {
            final Option<String> versionIdentifier = ((VdmEntity<?>) object).getVersionIdentifier();
            if( versionIdentifier.isDefined() ) {
                gen.writeStringField(VdmObject.ODATA_TYPE_ANNOTATIONS[0], versionIdentifier.get());
            }
        }

        final Map<Field, Option<Object>> fieldValues =
            Arrays
                .stream(object.getClass().getDeclaredFields())
                .filter(f -> f.getAnnotation(ElementName.class) != null)
                .collect(Collectors.toMap(Functions.identity(), f -> getFieldValue(f, object)));

        for( final Map.Entry<Field, Option<Object>> propertyEntry : fieldValues.entrySet() ) {
            final Option<Object> propertyValueOption = propertyEntry.getValue();
            final Field propertyField = propertyEntry.getKey();
            if( propertyValueOption.isEmpty() ) {
                log.trace("Field value for {} is empty.", propertyField);
                if( !nonNull ) {
                    gen.writeNull();
                }
            } else {
                final Object propertyValue = propertyValueOption.get();
                writePropertyValue(propertyField, propertyValue, nonNull, gen, prov);
            }
        }
        gen.writeEndObject();
    }

    private void writePropertyValue(
        @Nonnull final Field propertyField,
        @Nullable final Object propertyValue,
        final boolean isNonNull,
        @Nonnull final JsonGenerator gen,
        @Nonnull final SerializerProvider prov )
        throws IOException
    {
        final String propertyName = propertyField.getAnnotation(ElementName.class).value();
        final JsonSerialize customSerialize = propertyField.getAnnotation(JsonSerialize.class);
        if( customSerialize != null ) {
            try {
                gen.writeFieldName(propertyName);
                final AnnotationMap annotations = AnnotationMap.of(JsonSerialize.class, customSerialize);
                final AnnotatedField annotated = new AnnotatedField(null, propertyField, annotations);
                final Object rawSerializerType = prov.getAnnotationIntrospector().findSerializer(annotated);
                final Object rawSerializer = ((Class<?>) rawSerializerType).getDeclaredConstructor().newInstance();
                @SuppressWarnings( "unchecked" )
                final JsonSerializer<Object> serializer = (JsonSerializer<Object>) rawSerializer;
                serializer.serialize(propertyValue, gen, prov);
                return;
            }
            catch( final Exception e ) {
                final String msg =
                    String
                        .format(
                            "Failed to use custom serializer %s for field %s and value %s",
                            customSerialize,
                            propertyField,
                            propertyValue);
                log.debug(msg, e);
                throw new IOException(msg, e);
            }
        }

        final ConvertedObject<String> convertedValue = withCustomConverter(propertyValue);
        if( convertedValue.isConvertible() ) {
            final String value = convertedValue.get();
            if( value != null || !isNonNull ) {
                gen.writeStringField(propertyName, value);
            }
        } else {
            if( propertyValue != null || !isNonNull ) {
                prov.defaultSerializeField(propertyName, propertyValue, gen);
            }
        }
    }

    @SuppressWarnings( "unchecked" )
    @Nonnull
    private ConvertedObject<String> withCustomConverter( @Nullable final Object v )
    {
        final Optional<ODataGenericConverter<?>> matchingConverter =
            Arrays.stream(ODataGenericConverter.DEFAULT_CONVERTERS).filter(c -> c.getType().isInstance(v)).findFirst();
        return matchingConverter
            .map(oDataGenericConverter -> ((ODataGenericConverter<Object>) oDataGenericConverter).toDomain(v))
            .orElseGet(ConvertedObject::ofNotConvertible);
    }

    @Nonnull
    private Option<Object> getFieldValue( @Nonnull final Field f, @Nonnull final VdmObject<?> object )
    {
        return Try.of(() -> {
            f.setAccessible(true);
            return f.get(object);
        }).onFailure(e -> log.error("Failed to get value for field {} in object {}", f, object, e)).toOption();
    }

    @Override
    @Nonnull
    public
        JsonSerializer<?>
        createContextual( @Nonnull final SerializerProvider prov, @Nullable final BeanProperty property )
    {
        return new JacksonVdmObjectSerializer();
    }
}
