package com.sap.cloud.sdk.datamodel.openapi.generator;

import java.util.Map;

import org.openapitools.codegen.OpenAPINormalizer;
import org.openapitools.codegen.utils.ModelUtils;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

public class CustomOpenAPINormalizer extends OpenAPINormalizer
{

    public CustomOpenAPINormalizer( final OpenAPI openAPI, final Map<String, String> inputRules )
    {
        super(openAPI, inputRules);
    }

    /**
     * Normalize reference schema with allOf to support sibling properties
     *
     * @param schema
     *            Schema
     */
    @Override
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    protected void normalizeReferenceSchema( Schema schema )
    {
        if( schema.getType() != null || schema.getTypes() != null && !schema.getTypes().isEmpty() ) {
            // clears type(s) given that $ref is set
            schema.setType(null);
            schema.setTypes(null);
            LOGGER.warn("Type(s) cleared (set to null) given $ref is set to {}.", schema.get$ref());
        }

        if( schema.getTitle() != null
            || schema.getDescription() != null
            || schema.getNullable() != null
            || schema.getDefault() != null
            || schema.getDeprecated() != null
            || schema.getMaximum() != null
            || schema.getMinimum() != null
            || schema.getExclusiveMaximum() != null
            || schema.getExclusiveMinimum() != null
            || schema.getMaxItems() != null
            || schema.getMinItems() != null
            || schema.getMaxProperties() != null
            || schema.getMinProperties() != null
            || schema.getMaxLength() != null
            || schema.getMinLength() != null
            || schema.getWriteOnly() != null
            || schema.getReadOnly() != null
            || schema.getExample() != null
            || (schema.getExamples() != null && !schema.getExamples().isEmpty())
            || schema.getMultipleOf() != null
            || schema.getPattern() != null
            || (schema.getExtensions() != null && !schema.getExtensions().isEmpty()) ) {
            // Don't wrap in allOf if the referenced schema is a primitive type.
            // The swagger-parser may copy properties (description, example, etc.) from the
            // referenced schema onto the $ref schema object. Wrapping primitives in allOf
            // prevents proper type simplification (e.g., oneOf with a single primitive).
            Schema referencedSchema = ModelUtils.getReferencedSchema(openAPI, schema);
            if( referencedSchema != null
                && (ModelUtils.isStringSchema(referencedSchema)
                    || ModelUtils.isIntegerSchema(referencedSchema)
                    || ModelUtils.isNumberSchema(referencedSchema)
                    || ModelUtils.isBooleanSchema(referencedSchema)) ) {
                return;
            }

            // create allOf with a $ref schema
            schema.addAllOfItem(new Schema<>().$ref(schema.get$ref()));
            // clear $ref in original schema
            schema.set$ref(null);
        }
    }
}
