package com.sap.cloud.sdk.datamodel.odatav4.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FieldReference;
import com.sap.cloud.sdk.datamodel.odatav4.adapter.GsonVdmAdapterFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class UpdateRequestHelperPatch
{
    String toJson( @Nonnull final VdmEntity<?> entity, @Nonnull final Collection<FieldReference> includedFields )
    {
        return new SerializeHelper(entity, includedFields).toJson();
    }

    @RequiredArgsConstructor
    private static class SerializeHelper
    {
        @Nonnull
        private final VdmEntity<?> entity;

        @Nonnull
        private final Collection<FieldReference> includedFields;

        @Nonnull
        private final Gson gson = new GsonBuilder().serializeNulls().create();

        @Nonnull
        private final Map<VdmObject<?>, JsonObject> cache = new IdentityHashMap<>();

        @Nonnull
        private static final TypeAdapterFactory GSON_VDM_ADAPTER_FACTORY = new GsonVdmAdapterFactory();

        @Nonnull
        String toJson()
        {
            // find field names to be patched
            final Set<String> fieldNamesToPatch = new HashSet<>(entity.getChangedFields().keySet());
            includedFields.stream().map(FieldReference::getFieldName).forEach(fieldNamesToPatch::add);
            log.debug("The following fields are marked for updates: {}.", fieldNamesToPatch);

            // create the key-value map for entity properties to be patched
            final Map<String, Object> patchValues = Maps.filterKeys(entity.toMap(), fieldNamesToPatch::contains);

            // starting helper collection to identify of recursion
            final Set<VdmObject<?>> parentObjects = Collections.newSetFromMap(new IdentityHashMap<>());
            parentObjects.add(entity);

            // serialize key-value properties map to GSON object.
            final JsonObject o = Objects.requireNonNull((JsonObject) serializeComplexValue(patchValues, parentObjects));

            if( o.size() < 1 ) {
                log
                    .warn(
                        """
                            Update strategy is to modify with PATCH, but no fields have changed. \
                            Make sure to modify the entity via its setters or by naming them explicitly via 'includingFields(fields ...)'. \
                            This request may be bound to fail in the target system.\
                            """);
            }

            // add default OData annotation properties, e.g. @odata.type
            entity.getAnnotationProperties().forEach(o::addProperty);

            // translate JsonObject to Json String
            return gson.toJson(o);
        }

        @Nullable
        private
            JsonElement
            serializeComplexValue( @Nullable final Object input, @Nonnull final Set<VdmObject<?>> parentObjects )
        {
            if( input == null ) {
                return JsonNull.INSTANCE;
            }

            if( input instanceof Map ) {
                return ((Map<?, ?>) input)
                    .entrySet()
                    .stream()
                    .collect(
                        JsonObject::new,
                        ( m, v ) -> m.add(v.getKey().toString(), serializeComplexValue(v.getValue(), parentObjects)),
                        ( m1, m2 ) -> m2.keySet().forEach(k2 -> m1.add(k2, m2.get(k2))));
            }
            if( input instanceof List ) {
                return ((Collection<?>) input)
                    .stream()
                    .map(v -> serializeComplexValue(v, parentObjects))
                    .collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
            }
            if( input instanceof VdmObject ) {
                final JsonObject cachedValue = cache.get(input);
                if( cachedValue != null ) {
                    return cachedValue;
                }

                final JsonObject valueMap = new JsonObject();
                if( !parentObjects.contains(input) ) {
                    cache.put((VdmObject<?>) input, valueMap);

                    // for complex type consider all fields, for entity types filter for changed fields
                    final Map<String, Object> changedFields;
                    if( input instanceof VdmComplex ) {
                        changedFields = ((VdmObject<?>) input).toMapOfFields();
                        changedFields.putAll(((VdmObject<?>) input).getCustomFields());
                    } else {
                        changedFields = ((VdmObject<?>) input).getChangedFields();
                    }

                    // derive helper collection to identify of recursion
                    final Set<VdmObject<?>> nestedParentObjects = Collections.newSetFromMap(new IdentityHashMap<>());
                    nestedParentObjects.addAll(parentObjects);
                    nestedParentObjects.add((VdmObject<?>) input);

                    // serialize changed fields of nested VDM object
                    final JsonElement serializedMapRaw = serializeComplexValue(changedFields, nestedParentObjects);
                    final JsonObject serializedMap = Objects.requireNonNull((JsonObject) serializedMapRaw);
                    serializedMap.keySet().forEach(key -> valueMap.add(key, serializedMap.get(key)));
                }

                // if nested VDM object is a VDM entity, handle the key properties
                if( input instanceof VdmEntity ) {
                    final VdmEntity<?> entity = (VdmEntity<?>) input;

                    // if no key fields are marked as changed, then add the @id property
                    if( entity.getKey().getFieldNames().stream().noneMatch(valueMap::has) ) {
                        valueMap.addProperty("@id", entity.getEntityCollection() + entity.getKey().toEncodedString());
                    }
                }

                return valueMap;
            }

            return serializeSimpleValue(input);
        }

        @Nullable
        private <T> JsonElement serializeSimpleValue( @Nonnull final T object )
        {
            @SuppressWarnings( "unchecked" )
            final TypeToken<T> typeToken = TypeToken.get((Class<T>) object.getClass());

            final TypeAdapter<T> typeAdapter = GSON_VDM_ADAPTER_FACTORY.create(gson, typeToken);
            if( typeAdapter != null ) {
                final JsonElement jsonObject = typeAdapter.toJsonTree(object);
                log.trace("Simple entity property value {} is serialized to {}.", object, jsonObject);
                return jsonObject;
            }

            log.debug("GSON type adapter could not be found for entity property value of type {}.", typeToken);

            final JsonPrimitive jsonPrimitive = convertToJsonPrimitive(object, typeToken);

            if( jsonPrimitive == null ) {
                log
                    .warn(
                        "Could not convert value of type {} to a {} representation.",
                        typeToken,
                        JsonElement.class.getSimpleName());
            }

            return jsonPrimitive;
        }

        private <T> JsonPrimitive convertToJsonPrimitive( final T value, final TypeToken<T> typeToken )
        {
            if( Number.class.isAssignableFrom(typeToken.getRawType()) ) {
                final Number numberPrimitive = (Number) value;
                return new JsonPrimitive(numberPrimitive);
            }

            if( Boolean.class.isAssignableFrom(typeToken.getRawType()) ) {
                final Boolean booleanPrimitive = (Boolean) value;
                return new JsonPrimitive(booleanPrimitive);
            }

            if( Character.class.isAssignableFrom(typeToken.getRawType()) ) {
                final Character characterPrimitive = (Character) value;
                return new JsonPrimitive(characterPrimitive);
            }

            return null;
        }
    }
}
