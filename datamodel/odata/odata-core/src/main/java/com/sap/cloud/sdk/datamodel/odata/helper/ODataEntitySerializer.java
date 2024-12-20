/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sap.cloud.sdk.datamodel.odata.adapter.ODataNumberSerializer;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FieldReference;

import lombok.extern.slf4j.Slf4j;

@Slf4j
final class ODataEntitySerializer
{
    private static final Gson GSON;
    private static final Gson GSON_SERIALIZING_NULLS;

    static {
        final ODataNumberSerializer numberSerializer = new ODataNumberSerializer();
        final GsonBuilder builder =
            new GsonBuilder()
                .registerTypeAdapter(Long.class, numberSerializer)
                .registerTypeAdapter(Double.class, numberSerializer)
                .registerTypeAdapter(Float.class, numberSerializer)
                .registerTypeAdapter(BigDecimal.class, numberSerializer);
        // CXSDK currently serializes Edm.SByte as plain number so the client also does it for consistency.
        // .registerTypeAdapter(Byte.class, numberSerializer)

        GSON = builder.create();
        GSON_SERIALIZING_NULLS = builder.serializeNulls().create();
    }

    /**
     * Serializes an entity for update request (PUT). Allowing null values. Removing potential "versionIdentifier"
     * fields.
     *
     * @param entity
     *            The OData V2 entity reference.
     * @param excludedFields
     *            Collection of fields to be excluded in the update (PUT) request.
     * @return The serialized JSON string for entity update request.
     */
    @Nonnull
    static String serializeEntityForUpdatePut(
        @Nonnull final VdmEntity<?> entity,
        @Nullable final Collection<FieldReference> excludedFields )
    {
        final JsonObject jsonObject = GSON_SERIALIZING_NULLS.toJsonTree(entity).getAsJsonObject();

        removeVersionIdentifier(jsonObject);

        // find field names to be removed from PUT request
        if( excludedFields != null ) {
            excludedFields.stream().map(FieldReference::getFieldName).forEach(jsonObject::remove);
        }

        return GSON_SERIALIZING_NULLS.toJson(jsonObject);
    }

    /**
     * Serializes an entity for create request. Ignoring empty collections and null values.
     *
     * @param entity
     *            The OData V2 entity reference.
     * @return The serialized JSON string for entity create request.
     */
    @Nonnull
    static String serializeEntityForCreate( @Nonnull final VdmEntity<?> entity )
    {
        // When using builder pattern, all 1:n navigation properties will be initialized with `new ArrayList()` instead of expected `null`
        final JsonObject jsonObject = GSON.toJsonTree(entity).getAsJsonObject();

        removeEmptyArrays(jsonObject);

        return GSON.toJson(jsonObject);
    }

    /**
     * Serializes an entity for update request (PATCH). Allowing null values.
     *
     * @param entity
     *            The OData V2 entity reference.
     * @param includedFields
     *            Collection of fields to be included in the update (PATCH) request.
     * @return The serialized JSON string for entity update request.
     */
    @Nonnull
    static String serializeEntityForUpdatePatchShallow(
        @Nonnull final VdmEntity<?> entity,
        @Nonnull final Collection<FieldReference> includedFields )
    {
        final JsonObject fullEntity = GSON_SERIALIZING_NULLS.toJsonTree(entity).getAsJsonObject();

        // find field names to be patched
        final Set<String> fieldNamesToPatch = new HashSet<>(entity.getChangedFields().keySet());
        includedFields.stream().map(FieldReference::getFieldName).forEach(fieldNamesToPatch::add);
        log.debug("The following fields are marked for updates: {}.", fieldNamesToPatch);

        final JsonObject partialEntity = new JsonObject();

        fieldNamesToPatch.forEach(key -> partialEntity.add(key, fullEntity.get(key)));

        return GSON_SERIALIZING_NULLS.toJson(partialEntity);
    }

    /**
     * Serializes an entity for update request (PATCH) including changes in nested properties. Allowing null values.
     * Resulting JSON contains the full value of complex fields for changing any nested field.
     *
     * @param entity
     *            The OData V2 entity reference.
     * @param includedFields
     *            Collection of fields to be included in the update (PATCH) request.
     * @return The serialized JSON string for entity update request.
     */
    @Nonnull
    static String serializeEntityForUpdatePatchRecursiveFull(
        @Nonnull final VdmEntity<?> entity,
        @Nonnull final Collection<FieldReference> includedFields )
    {
        final JsonObject fullEntityJson = GSON_SERIALIZING_NULLS.toJsonTree(entity).getAsJsonObject();
        final JsonObject patchObject = new JsonObject();

        final Set<String> changedFieldNames = new HashSet<>(entity.getChangedFields().keySet());
        includedFields.stream().map(FieldReference::getFieldName).forEach(changedFieldNames::add);
        changedFieldNames.forEach(key -> patchObject.add(key, fullEntityJson.get(key)));

        entity
            .toMapOfFields()
            .entrySet()
            .stream()
            .filter(entry -> !patchObject.has(entry.getKey()))
            .filter(entry -> entry.getValue() instanceof VdmComplex<?>)
            .filter(entry -> containsNestedChangedFields((VdmComplex<?>) entry.getValue()))
            .forEach(entry -> patchObject.add(entry.getKey(), fullEntityJson.get(entry.getKey())));

        log.debug("The following object is serialized for update : {}.", patchObject);

        return GSON_SERIALIZING_NULLS.toJson(patchObject);
    }

    /**
     * Checks if the given complex object contains any changed fields in its nested fields.
     *
     * @param vdmComplex
     *            the complex object to check
     * @return true if the complex object contains any changed fields, false otherwise
     */
    private static boolean containsNestedChangedFields( final VdmComplex<?> vdmComplex )
    {
        if( !vdmComplex.getChangedFields().isEmpty() ) {
            return true;
        }

        return vdmComplex
            .toMapOfFields()
            .values()
            .stream()
            .filter(complexField -> complexField instanceof VdmComplex<?>)
            .map(complexField -> (VdmComplex<?>) complexField)
            .anyMatch(ODataEntitySerializer::containsNestedChangedFields);
    }

    /**
     * Serializes an entity for update request (PATCH) including changes in nested properties. Allowing null values.
     * Resulting JSON contains only the changed fields (including nested changes).
     *
     * @param entity
     *            The OData V2 entity reference.
     * @param includedFields
     *            Collection of fields to be included in the update (PATCH) request.
     * @return The serialized JSON string for entity update request.
     */
    @Nonnull
    static String serializeEntityForUpdatePatchRecursiveDelta(
        @Nonnull final VdmEntity<?> entity,
        @Nonnull final Collection<FieldReference> includedFields )
    {
        final JsonObject fullEntityJson = GSON_SERIALIZING_NULLS.toJsonTree(entity).getAsJsonObject();
        final JsonObject patchObject = new JsonObject();

        // Recursively build patch object from changed fields
        final JsonObject tempPatchObject = createPatchObjectRecursiveDelta(entity, fullEntityJson);

        // Add included fields (from the root only)
        includedFields
            .stream()
            .map(FieldReference::getFieldName)
            .forEach(key -> patchObject.add(key, fullEntityJson.get(key)));

        // Merge all fields from the tempPatchObject if not already present
        tempPatchObject
            .entrySet()
            .stream()
            .filter(entry -> !patchObject.has(entry.getKey()))
            .forEach(entry -> patchObject.add(entry.getKey(), entry.getValue()));

        log.debug("The following delta object is serialized for update : {}.", patchObject);

        return GSON_SERIALIZING_NULLS.toJson(patchObject);
    }

    /**
     * Recursively builds a patch object for a VdmObject by including only changed fields. Complex fields are traversed
     * recursively.
     *
     * @param vdmObject
     *            the VdmObject (entity or complex) to build the patch from
     * @param jsonObject
     *            the full JSON representation of this object
     * @return a JsonObject that contains only changed fields (including nested changes)
     */
    @Nonnull
    private static
        JsonObject
        createPatchObjectRecursiveDelta( @Nonnull final VdmObject<?> vdmObject, @Nonnull final JsonObject jsonObject )
    {
        final JsonObject patch = new JsonObject();

        // Process all complex fields
        vdmObject
            .toMapOfFields()
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue() instanceof VdmComplex<?>)
            .map(entry -> {
                final String fieldName = entry.getKey();
                final VdmComplex<?> complexField = (VdmComplex<?>) entry.getValue();
                // Recursively build patch for the complex field
                final JsonObject childJsonObject =
                    createPatchObjectRecursiveDelta(complexField, jsonObject.getAsJsonObject(fieldName));
                return Map.entry(fieldName, childJsonObject);
            })
            .filter(entry -> !entry.getValue().isEmpty())
            .forEach(entry -> patch.add(entry.getKey(), entry.getValue()));

        // Add changed primitive fields
        vdmObject.getChangedFields().keySet().forEach(key -> patch.add(key, jsonObject.get(key)));

        return patch;
    }

    private static void removeVersionIdentifier( @Nonnull final JsonObject jsonObject )
    {
        log.debug("Removing redundant \"versionIdentifier\" recursively from JSON object: {}", jsonObject);

        final Predicate<JsonElement> isNullOrString =
            obj -> obj.isJsonNull() || obj.isJsonPrimitive() && obj.getAsJsonPrimitive().isString();

        traverseJsonObject(
            jsonObject,
            ( element, name ) -> "versionIdentifier".equals(name) && isNullOrString.test(element),
            JsonObject::remove);

        log.debug("JSON object after removing redundant \"versionIdentifier\": {}", jsonObject);
    }

    private static void removeEmptyArrays( @Nonnull final JsonObject jsonObject )
    {
        log.debug("Removing empty arrays recursively from JSON object: {}", jsonObject);

        traverseJsonObject(
            jsonObject,
            ( element, name ) -> element.isJsonArray() && element.getAsJsonArray().size() == 0,
            JsonObject::remove);

        log.debug("JSON object after removing empty arrays: {}", jsonObject);
    }

    /**
     * Traverse the JSON object tree to apply an action on filtered items.
     *
     * @param jsonObject
     *            The current JSON object to check properties for.
     * @param filter
     *            The filter as lambda (predicate) for property value and property key.
     * @param action
     *            The action as lambda (consumer) for parent object and filtered property key.
     */
    private static void traverseJsonObject(
        @Nonnull final JsonObject jsonObject,
        @Nonnull final BiPredicate<JsonElement, String> filter,
        @Nonnull final BiConsumer<JsonObject, String> action )
    {
        final List<String> filteredChildKeys = new ArrayList<>();

        jsonObject.entrySet().forEach(entry -> {
            if( entry.getValue().isJsonObject() ) {
                // Apply this logic recursively for all nested objects
                traverseJsonObject(entry.getValue().getAsJsonObject(), filter, action);
            } else if( filter.test(entry.getValue(), entry.getKey()) ) {
                // Collect key names for children that tested against the provided filter predicate
                filteredChildKeys.add(entry.getKey());
            } else if( entry.getValue().isJsonArray() ) {
                // Apply this logic recursively for lists of nested objects
                final JsonArray jsonArray = entry.getValue().getAsJsonArray();
                StreamSupport
                    .stream(jsonArray.spliterator(), false)
                    .filter(JsonElement::isJsonObject)
                    .map(JsonElement::getAsJsonObject)
                    .forEach(o -> traverseJsonObject(o, filter, action));
            }
        });

        log
            .trace(
                "Applying the provided action on parent element {} for the following child items: {}",
                jsonObject,
                filteredChildKeys);

        filteredChildKeys.forEach(affectedChildKey -> action.accept(jsonObject, affectedChildKey));
    }
}
