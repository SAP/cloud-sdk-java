package com.sap.cloud.sdk.datamodel.odatav4.adapter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEntity;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmObject;
import com.sap.cloud.sdk.result.ElementName;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * For internal use only by data model classes
 *
 * @param <T>
 *            The entity type.
 */
@Slf4j
public class GsonVdmEntityAdapter<T> extends TypeAdapter<VdmObject<T>>
{
    @Nonnull
    private final Gson gson;
    @Nonnull
    private final Class<? super T> entityRawType;
    @Nonnull
    private final TypeAdapterFactory adapterFactory;

    @Nullable
    private TypeAdapter<VdmObject<T>> delegateAdapter = null;
    @Nullable
    private GsonVdmEntityAdapter<T> superClassAdapter = null;

    @Nonnull
    private final Map<String, PropertySerializationInfo> entityProperties;
    @Nonnull
    private final TypeAdapter<Object> customFieldAdapter;

    @AllArgsConstructor
    private static class PropertySerializationInfo
    {
        @Getter
        private final Field javaField;
        @Getter
        private final TypeAdapter<?> fieldAdapter;
    }

    private TypeAdapter<?> getAdapterFromField( final Field entityField, final Gson gson )
    {
        if( entityField.isAnnotationPresent(JsonAdapter.class) ) {
            try {
                return (TypeAdapter<?>) entityField.getAnnotation(JsonAdapter.class).value().newInstance();
            }
            catch( final InstantiationException | IllegalAccessException e ) {
                log.warn("Could not instantiate the field '" + entityField.getName() + "'.", e);
            }
        }
        if( Iterable.class.isAssignableFrom(entityField.getType()) ) {
            final ParameterizedType entityFieldTypeParams = (ParameterizedType) entityField.getGenericType();
            final Type listEntityType = entityFieldTypeParams.getActualTypeArguments()[0];
            final TypeAdapter<?> innerTypeAdapter = gson.getAdapter(TypeToken.get(listEntityType));
            return new GsonVdmEntityListAdapter<>(gson, innerTypeAdapter);
        }
        final TypeAdapter<?> fieldAdapter = adapterFactory.create(gson, TypeToken.get(entityField.getType()));
        if( fieldAdapter != null ) {
            return fieldAdapter;
        }

        return gson.getAdapter(entityField.getType());
    }

    /**
     * For internal use only by data model classes.
     *
     * @param adapterFactory
     *            The adapter type factory.
     * @param gson
     *            The GSON instance.
     * @param entityRawType
     *            The entity type reference.
     */
    @SuppressWarnings( "unchecked" )
    public GsonVdmEntityAdapter(
        @Nonnull final TypeAdapterFactory adapterFactory,
        @Nonnull final Gson gson,
        @Nonnull final Class<? super T> entityRawType )
    {
        this.gson = gson;
        this.entityRawType = entityRawType;
        this.adapterFactory = adapterFactory;

        entityProperties = new LinkedHashMap<>();
        for( final Field entityField : entityRawType.getDeclaredFields() ) {
            if( entityField.isAnnotationPresent(ElementName.class)
                || entityField.isAnnotationPresent(SerializedName.class) ) {

                TypeAdapter<?> fieldAdapter = null;

                // Don't get the adapter yet if the field is a navigation property.
                // Otherwise an infinite loop can happen if there are circular navigation properties.
                if( !VdmObject.class.isAssignableFrom(entityField.getType())
                    && !Iterable.class.isAssignableFrom(entityField.getType()) ) {

                    fieldAdapter = getAdapterFromField(entityField, gson);
                }

                final String entityFieldKey =
                    entityField.isAnnotationPresent(ElementName.class)
                        ? entityField.getAnnotation(ElementName.class).value()
                        : entityField.getAnnotation(SerializedName.class).value();

                entityProperties.put(entityFieldKey, new PropertySerializationInfo(entityField, fieldAdapter));
            }
        }

        customFieldAdapter = new GsonCustomFieldAdapter(gson);

        final Class<? super T> entityRawSuperType = entityRawType.getSuperclass();
        if( Object.class == entityRawSuperType ) {
            delegateAdapter =
                (TypeAdapter<VdmObject<T>>) gson.getDelegateAdapter(adapterFactory, TypeToken.get(entityRawType));
        } else {
            superClassAdapter = new GsonVdmEntityAdapter<>(adapterFactory, gson, entityRawSuperType);
        }
    }

    /**
     * For internal use only by data model classes.
     *
     * @param jsonReader
     *            The JsonReader reference.
     * @return The deserialized entity instance.
     * @throws IOException
     *             When deserialization failed.
     */
    @Override
    @Nullable
    public VdmObject<T> read( @Nonnull final JsonReader jsonReader )
        throws IOException
    {
        try {
            @SuppressWarnings( "unchecked" )
            final VdmObject<T> entity = (VdmObject<T>) entityRawType.newInstance();

            if( jsonReader.peek() == JsonToken.BEGIN_OBJECT ) {
                jsonReader.beginObject();

                while( jsonReader.hasNext() ) {
                    final String propertyKey = jsonReader.nextName();

                    if( "__metadata".equals(propertyKey) ) {
                        jsonReader.skipValue();
                    } else if( "__deferred".equals(propertyKey) ) {
                        jsonReader.skipValue();
                        jsonReader.endObject();
                        return null;
                    } else {
                        final PropertySerializationInfo propertyInfo = getPropertySerializationInfo(propertyKey);
                        if( propertyInfo != null ) {
                            final Field entityField = propertyInfo.getJavaField();
                            TypeAdapter<?> fieldAdapter = propertyInfo.getFieldAdapter();

                            if( fieldAdapter == null ) {
                                fieldAdapter = getAdapterFromField(entityField, gson);
                            }

                            if( fieldAdapter != null ) {
                                final Object attributeValue = fieldAdapter.read(jsonReader);

                                // To be safe/secure, since fields are declared private in the VDM.
                                final boolean oldAccessibleValue = entityField.isAccessible();
                                entityField.setAccessible(true);
                                entityField.set(entity, attributeValue);
                                entityField.setAccessible(oldAccessibleValue);
                            }
                        } else {
                            final Object customValue = customFieldAdapter.read(jsonReader);
                            handleCustomField(entity, propertyKey, customValue);
                        }
                    }
                }

                jsonReader.endObject();
            } else if( jsonReader.peek() == JsonToken.NULL ) {
                jsonReader.nextNull();
                return null;
            }

            return entity;
        }
        catch( final InstantiationException | IllegalAccessException e ) {
            log
                .error(
                    "Could not instantiate or initialize '"
                        + entityRawType.getName()
                        + "'. "
                        + "Returning null instead.",
                    e);
        }
        return null;
    }

    private void handleCustomField( final VdmObject<T> object, final String name, final Object value )
    {
        if( Arrays.asList(VdmObject.ODATA_TYPE_ANNOTATIONS).contains(name) ) {
            // do nothing
            return;
        }
        if( object instanceof VdmEntity && Arrays.asList(VdmObject.ODATA_VERSION_ANNOTATIONS).contains(name) ) {
            ((VdmEntity<?>) object).setVersionIdentifier(value.toString());
            return;
        }
        object.setCustomField(name, value);
    }

    @Nullable
    private PropertySerializationInfo getPropertySerializationInfo( final String propertyKey )
    {
        if( entityProperties.containsKey(propertyKey) ) {
            return entityProperties.get(propertyKey);
        }
        if( superClassAdapter != null ) {
            return superClassAdapter.getPropertySerializationInfo(propertyKey);
        }
        return null;
    }

    /**
     * For internal use only by data model classes.
     *
     * @param out
     *            The JsonWriter reference.
     * @param value
     *            The entity instance to be serialized.
     * @throws IOException
     *             When serialization failed.
     */
    @Override
    public void write( @Nonnull final JsonWriter out, @Nullable final VdmObject<T> value )
        throws IOException
    {
        if( value != null ) {
            final JsonObject entityAsJson = getEntityAsJsonObject(value);
            final JsonObject customFieldsAsJson = gson.toJsonTree(value.getCustomFields()).getAsJsonObject();

            for( final Map.Entry<String, String> annotationProperty : value.getAnnotationProperties().entrySet() ) {
                entityAsJson.add(annotationProperty.getKey(), gson.toJsonTree(annotationProperty.getValue()));
            }

            for( final Map.Entry<String, JsonElement> customField : customFieldsAsJson.entrySet() ) {
                entityAsJson.add(customField.getKey(), customField.getValue());
            }

            gson.toJson(entityAsJson, out);
        } else {
            out.nullValue();
        }
    }

    @SuppressWarnings( "unchecked" )
    private JsonObject getEntityAsJsonObject( final VdmObject<T> value )
    {
        if( delegateAdapter != null ) {
            return delegateAdapter.toJsonTree(value).getAsJsonObject();
        } else {
            final JsonObject entityAsJson = superClassAdapter.getEntityAsJsonObject(value);
            for( final Map.Entry<String, PropertySerializationInfo> entityProperty : entityProperties.entrySet() ) {
                try {
                    final PropertySerializationInfo serializationInfo = entityProperty.getValue();

                    // To be safe/secure, since fields are declared private in the VDM.
                    final Field propertyField = serializationInfo.getJavaField();
                    final boolean oldAccessibleValue = propertyField.isAccessible();
                    propertyField.setAccessible(true);
                    final Object propertyValue = propertyField.get(value);
                    propertyField.setAccessible(oldAccessibleValue);

                    TypeAdapter<Object> fieldAdapter = (TypeAdapter<Object>) serializationInfo.getFieldAdapter();

                    if( fieldAdapter == null ) {
                        fieldAdapter = (TypeAdapter<Object>) getAdapterFromField(propertyField, gson);
                    }

                    final JsonElement propertyValueAsJson =
                        (fieldAdapter != null) ? fieldAdapter.toJsonTree(propertyValue) : null;

                    // Overwrites JSON property from the superclass if this class has a property with the same name.
                    entityAsJson.add(entityProperty.getKey(), propertyValueAsJson);
                }
                catch( final IllegalAccessException e ) {
                    log
                        .error(
                            "Could not serialize property '"
                                + entityProperty.getKey()
                                + "'. "
                                + "Returning null instead.",
                            e);
                }
            }

            // odata type
            final String odataType = "#" + value.getOdataType();
            entityAsJson.addProperty(VdmObject.ODATA_TYPE_ANNOTATIONS[0], odataType);

            // odata version
            if( value instanceof VdmEntity ) {
                final Option<String> versionIdentifier = ((VdmEntity<?>) value).getVersionIdentifier();
                if( versionIdentifier.isDefined() ) {
                    entityAsJson.addProperty(VdmObject.ODATA_VERSION_ANNOTATIONS[0], versionIdentifier.get());
                }
            }

            return entityAsJson;
        }
    }
}
