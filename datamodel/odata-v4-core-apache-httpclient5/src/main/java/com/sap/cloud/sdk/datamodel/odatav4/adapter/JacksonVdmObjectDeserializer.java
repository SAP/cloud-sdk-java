package com.sap.cloud.sdk.datamodel.odatav4.adapter;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotationMap;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEntity;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmObject;
import com.sap.cloud.sdk.result.ElementName;
import com.sap.cloud.sdk.typeconverter.ConvertedObject;

import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;

/**
 * Jackson deserializer adapter for {@link VdmEntity} and {@link VdmObject} types.
 */
@Slf4j
public class JacksonVdmObjectDeserializer extends StdDeserializer<VdmObject<?>> implements ContextualDeserializer
{
    private static final long serialVersionUID = -8440640969356203625L;
    private JavaType valueType;

    /**
     * Default constructor.
     */
    protected JacksonVdmObjectDeserializer()
    {
        super((Class<VdmObject<?>>) null);
    }

    @Override
    @Nonnull
    public
        JacksonVdmObjectDeserializer
        createContextual( @Nonnull final DeserializationContext ctxt, @Nullable final BeanProperty property )
    {
        final Option<JavaType> beanType = Option.of(property).map(BeanProperty::getType).map(t -> t.containedType(0));
        final JavaType valueType = beanType.getOrElse(ctxt::getContextualType);
        final JacksonVdmObjectDeserializer deserializer = new JacksonVdmObjectDeserializer();
        deserializer.valueType = valueType;
        return deserializer;
    }

    @Override
    @Nullable
    public VdmObject<?> deserialize( @Nonnull final JsonParser parser, @Nonnull final DeserializationContext ctxt )
        throws IOException
    {
        final Class<?> vdmObjectType = valueType.getRawClass();
        final VdmObject<?> vdmObject = instantiateVdmObject(vdmObjectType);

        // Handle current (first) JSON token
        final JsonToken firstToken = parser.currentToken();
        if( firstToken == JsonToken.VALUE_NULL ) {
            log.trace("VDM object is null");
            return null;
        }
        if( firstToken != JsonToken.START_OBJECT ) {
            log.debug("VDM object can only be deserialized from JSON object.");
            throw new IOException(
                "Expected Start of JSON Object when deserializing an VDM object. Instead there was " + firstToken);
        }

        // Create mapping for OData element name to actual Java field(s)
        final Map<String, List<Field>> fieldValues =
            Arrays
                .stream(vdmObjectType.getDeclaredFields())
                .filter(f -> f.getAnnotation(ElementName.class) != null)
                .collect(Collectors.groupingBy(f -> f.getDeclaredAnnotation(ElementName.class).value()));

        // Iterate JSON token handling
        for( JsonToken token = parser.nextToken(); token != JsonToken.END_OBJECT; token = parser.nextToken() ) {
            if( token != JsonToken.FIELD_NAME ) {
                throw new IOException(
                    "Expected field name at current position of JSON object. Instead there was " + token);
            }
            final String fieldName = parser.currentName();

            // Step from JSON element name to element value
            parser.nextToken();

            // Handle custom value when its field name is not declared on VDM object definition
            if( !fieldValues.containsKey(fieldName) ) {
                final Object value = ctxt.readValue(parser, Object.class);
                handleCustomValue(vdmObject, fieldName, value);
                continue;
            }

            // Handle value for declared VDM object field
            Object fieldValue = null;
            for( final Field field : fieldValues.get(fieldName) ) {
                if( fieldValue == null ) {
                    fieldValue = getVdmObjectFieldValue(field, parser, ctxt);
                }
                setVdmObjectFieldValue(vdmObject, field, fieldValue);
            }
        }
        return vdmObject;
    }

    @Nonnull
    private VdmObject<?> instantiateVdmObject( @Nonnull final Class<?> vdmObjectType )
        throws IOException
    {
        try {
            final Constructor<?> declaredConstructor = vdmObjectType.getDeclaredConstructor();
            return (VdmObject<?>) declaredConstructor.newInstance();
        }
        catch( final
            NoSuchMethodException
                | InstantiationException
                | IllegalArgumentException
                | SecurityException
                | IllegalAccessException
                | InvocationTargetException e ) {
            throw new IOException("Failed to create an instance from VDM object of type " + vdmObjectType, e);
        }
    }

    @Nullable
    private Object getVdmObjectFieldValue(
        @Nonnull final Field field,
        @Nonnull final JsonParser parser,
        @Nonnull final DeserializationContext ctxt )
        throws IOException
    {
        final JsonToken valueToken = parser.currentToken();

        final JsonDeserialize customDeserialize = field.getAnnotation(JsonDeserialize.class);
        if( customDeserialize != null ) {
            try {
                final AnnotationMap annotations = AnnotationMap.of(JsonDeserialize.class, customDeserialize);
                final AnnotatedField annotated = new AnnotatedField(null, field, annotations);
                final Object rawDeserializerType = ctxt.getAnnotationIntrospector().findDeserializer(annotated);
                final Object rawDeserializer = ((Class<?>) rawDeserializerType).getDeclaredConstructor().newInstance();
                @SuppressWarnings( "unchecked" )
                final JsonDeserializer<Object> deserializer = (JsonDeserializer<Object>) rawDeserializer;
                return deserializer.deserialize(parser, ctxt);
            }
            catch( final Exception e ) {
                final String msg =
                    String.format("Failed to use custom deserializer %s for field %s.", customDeserialize, field);
                log.debug(msg, e);
                throw new IOException(msg, e);
            }
        }

        if( valueToken == JsonToken.VALUE_STRING ) {
            final String fieldValueString = parser.getText();
            final Option<?> convertedObject = withCustomConverter(field.getType(), fieldValueString);
            if( convertedObject.isDefined() ) {
                return convertedObject.get();
            }
        }
        if( valueToken == JsonToken.VALUE_NULL ) {
            return null;
        } else {
            final JavaType javaType = ctxt.constructType(field.getGenericType());
            return ctxt.readValue(parser, javaType);
        }
    }

    private void setVdmObjectFieldValue(
        @Nonnull final VdmObject<?> vdmObject,
        @Nonnull final Field field,
        @Nullable final Object value )
        throws IOException
    {
        try {
            field.setAccessible(true);
            field.set(vdmObject, value);
        }
        catch( final IllegalArgumentException | SecurityException | IllegalAccessException e ) {
            final String className = vdmObject.getClass().getSimpleName();
            final String msg = String.format("Failed to set value for field %s on instance of %s", field, className);
            log.debug(msg, e);
            throw new IOException(msg, e);
        }
    }

    private void handleCustomValue(
        @Nonnull final VdmObject<?> object,
        @Nonnull final String name,
        @Nullable final Object value )
    {
        if( Arrays.asList(VdmObject.ODATA_TYPE_ANNOTATIONS).contains(name) ) {
            // do nothing
            return;
        }
        if( object instanceof VdmEntity && Arrays.asList(VdmObject.ODATA_VERSION_ANNOTATIONS).contains(name) ) {
            ((VdmEntity<?>) object).setVersionIdentifier(String.valueOf(value));
            return;
        }
        object.setCustomField(name, value);
    }

    @Nonnull
    private <T> Option<T> withCustomConverter( @Nonnull final Class<T> targetType, @Nonnull final String value )
        throws IOException
    {
        final Optional<ODataGenericConverter<?>> matchingConverter =
            Arrays
                .stream(ODataGenericConverter.DEFAULT_CONVERTERS)
                .filter(c -> targetType.isAssignableFrom(c.getType()))
                .findFirst();

        if( !matchingConverter.isPresent() ) {
            return Option.none();
        }

        @SuppressWarnings( "unchecked" )
        final ConvertedObject<T> convertedObject =
            matchingConverter
                .map(oDataGenericConverter -> ((ODataGenericConverter<T>) oDataGenericConverter).fromDomain(value))
                .orElseGet(ConvertedObject::ofNotConvertible);
        if( convertedObject.isNotConvertible() ) {
            throw new IOException(String.format("Failed to convert %s to %s", value, targetType.getSimpleName()));
        }
        return Option.of(convertedObject.get());
    }
}
