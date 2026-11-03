package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import static java.util.function.Predicate.not;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Strings;
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
import com.sap.cloud.sdk.datamodel.odata.helper.VdmEntity;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmObject;
import com.sap.cloud.sdk.result.ElementName;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * For internal use only by data model classes
 *
 * @param <T>
 *            The generic VDM object type
 */
@Slf4j
public class ODataVdmEntityAdapter<T> extends TypeAdapter<VdmObject<T>>
{
    @Nonnull
    private final Gson gson;

    @Nonnull
    private final Class<? super T> entityRawType;

    @Nullable
    private TypeAdapter<VdmObject<T>> delegateAdapter = null;

    @Nullable
    private ODataVdmEntityAdapter<T> superClassAdapter = null;

    @Nonnull
    private final TypeAdapter<Object> customFieldAdapter;

    // (1) Field has stateful accessibility flag
    // (2) Multiple threads may access the Map, therefore state must be isolated with ThreadLocal
    // (3) ThreadLocal is to be defined statically, therefore the properties must be mapped to a class reference
    private static final ThreadLocal<Map<Class<?>, Map<String, Field>>> fieldProperties =
        ThreadLocal.withInitial(IdentityHashMap::new);

    @Nonnull
    private Map<String, Field> getFieldProperties( @Nonnull final Class<?> type )
    {
        return fieldProperties.get().computeIfAbsent(type, ODataVdmEntityAdapter::createFieldProperties);
    }

    @Nonnull
    private static Map<String, Field> createFieldProperties( @Nonnull final Class<?> type )
    {
        final Map<String, Field> result = new LinkedHashMap<>();
        for( final Field field : type.getDeclaredFields() ) {
            if( field.isAnnotationPresent(ElementName.class) || field.isAnnotationPresent(SerializedName.class) ) {
                final String odataName =
                    field.isAnnotationPresent(ElementName.class)
                        ? field.getAnnotation(ElementName.class).value()
                        : field.getAnnotation(SerializedName.class).value();
                result.put(odataName, field);
            }
        }
        return result;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class ODataV2Metadata
    {
        @Nullable
        @SerializedName( "uri" )
        @ODataField( odataName = "uri" )
        private String uri;

        @Nullable
        @SerializedName( "etag" )
        @ODataField( odataName = "etag" )
        private String etag;

        @Nullable
        @SerializedName( "type" )
        @ODataField( odataName = "type" )
        private String type;
    }

    @Nonnull
    private static TypeAdapter<?> getAdapterFromField( @Nonnull final Field entityField, @Nonnull final Gson gson )
    {
        if( entityField.isAnnotationPresent(JsonAdapter.class) ) {
            try {
                return (TypeAdapter<?>) entityField
                    .getAnnotation(JsonAdapter.class)
                    .value()
                    .getDeclaredConstructor()
                    .newInstance();
            }
            catch( final
                InstantiationException
                    | IllegalAccessException
                    | NoSuchMethodException
                    | InvocationTargetException e ) {
                log.warn("Could not instantiate the field '" + entityField.getName() + "'.", e);
            }
        }
        if( Iterable.class.isAssignableFrom(entityField.getType()) ) {
            final ParameterizedType entityFieldTypeParams = (ParameterizedType) entityField.getGenericType();
            final Type listEntityType = entityFieldTypeParams.getActualTypeArguments()[0];
            final TypeAdapter<?> innerTypeAdapter = gson.getAdapter(TypeToken.get(listEntityType));
            return new ODataVdmEntityListAdapter<>(gson, innerTypeAdapter);
        }
        return gson.getAdapter(entityField.getType());
    }

    /**
     * For internal use only by data model classes
     *
     * @param adapterFactory
     *            The GSON type adapter factory
     * @param gson
     *            The GSON reference
     * @param entityRawType
     *            The generic entity raw type
     */
    @SuppressWarnings( "unchecked" )
    public ODataVdmEntityAdapter(
        @Nonnull final TypeAdapterFactory adapterFactory,
        @Nonnull final Gson gson,
        @Nonnull final Class<? super T> entityRawType )
    {
        this.gson = gson;
        this.entityRawType = entityRawType;
        customFieldAdapter = new ODataCustomFieldAdapter(gson);

        final Class<? super T> entityRawSuperType = entityRawType.getSuperclass();
        if( Object.class == entityRawSuperType ) {
            delegateAdapter =
                (TypeAdapter<VdmObject<T>>) gson.getDelegateAdapter(adapterFactory, TypeToken.get(entityRawType));
        } else {
            superClassAdapter = new ODataVdmEntityAdapter<>(adapterFactory, gson, entityRawSuperType);
        }
    }

    /**
     * For internal use only by data model classes
     */
    @Override
    @Nullable
    public VdmObject<T> read( @Nonnull final JsonReader jsonReader )
        throws IOException
    {
        try {
            @SuppressWarnings( "unchecked" )
            final VdmObject<T> entity = (VdmObject<T>) entityRawType.getDeclaredConstructor().newInstance();

            if( jsonReader.peek() == JsonToken.BEGIN_OBJECT ) {
                jsonReader.beginObject();

                while( jsonReader.hasNext() ) {
                    final String propertyKey = jsonReader.nextName();

                    if( "__metadata".equals(propertyKey) ) {
                        if( jsonReader.peek() == JsonToken.BEGIN_OBJECT ) {
                            final ODataV2Metadata metadata = new Gson().fromJson(jsonReader, ODataV2Metadata.class);
                            final Option<String> maybeEtag =
                                Option.of(metadata).map(ODataV2Metadata::getEtag).filter(not(Strings::isNullOrEmpty));
                            if( maybeEtag.isDefined() && entity instanceof VdmEntity ) {
                                ((VdmEntity<T>) entity).setVersionIdentifier(maybeEtag.get());
                            }
                        } else {
                            log.warn("Expected JSON value \"__metadata\" to be an object.");
                            jsonReader.skipValue();
                        }
                    } else if( "__deferred".equals(propertyKey) ) {
                        jsonReader.skipValue();
                        jsonReader.endObject();
                        return null;
                    } else {
                        final Field entityField = getPropertySerializationInfo(propertyKey);
                        if( entityField != null ) {
                            final TypeAdapter<?> fieldAdapter = getAdapterFromField(entityField, gson);
                            final Object attributeValue = fieldAdapter.read(jsonReader);

                            // To be safe/secure, since fields are declared private in the VDM.
                            final boolean oldAccessibleValue = entityField.canAccess(entity);
                            entityField.setAccessible(true);
                            entityField.set(entity, attributeValue);
                            entityField.setAccessible(oldAccessibleValue);
                        } else {
                            entity.getCustomFields().put(propertyKey, customFieldAdapter.read(jsonReader));
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
        catch( final
            InstantiationException
                | IllegalAccessException
                | NoSuchMethodException
                | InvocationTargetException e ) {
            log.error("Could not instantiate or initialize '{}'. Returning null instead.", entityRawType.getName(), e);
        }
        return null;
    }

    @Nullable
    private Field getPropertySerializationInfo( final String propertyKey )
    {
        Field result = getFieldProperties(entityRawType).get(propertyKey);
        if( result == null && superClassAdapter != null ) {
            result = superClassAdapter.getPropertySerializationInfo(propertyKey);
        }
        return result;
    }

    /**
     * For internal use only by data model classes
     */
    @Override
    public void write( @Nonnull final JsonWriter out, @Nullable final VdmObject<T> value )
        throws IOException
    {
        if( value != null ) {
            final JsonObject entityAsJson = getEntityAsJsonObject(value);
            final JsonObject customFieldsAsJson = gson.toJsonTree(value.getCustomFields()).getAsJsonObject();

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

            for( final Map.Entry<String, Field> entityProperty : getFieldProperties(entityRawType).entrySet() ) {
                try {
                    final Field propertyField = entityProperty.getValue();

                    // To be safe/secure, since fields are declared private in the VDM.
                    final boolean oldAccessibleValue = propertyField.canAccess(value);
                    propertyField.setAccessible(true);
                    final Object propertyValue = propertyField.get(value);
                    propertyField.setAccessible(oldAccessibleValue);

                    final TypeAdapter<Object> fieldAdapter =
                        (TypeAdapter<Object>) getAdapterFromField(propertyField, gson);
                    final JsonElement propertyValueAsJson = fieldAdapter.toJsonTree(propertyValue);

                    // Overwrites JSON property from the superclass if this class has a property with the same name.
                    entityAsJson.add(entityProperty.getKey(), propertyValueAsJson);
                }
                catch( final IllegalAccessException e ) {
                    log.error("Could not serialize property '{}'. Returning null instead.", entityProperty.getKey(), e);
                }
            }
            return entityAsJson;
        }
    }
}
