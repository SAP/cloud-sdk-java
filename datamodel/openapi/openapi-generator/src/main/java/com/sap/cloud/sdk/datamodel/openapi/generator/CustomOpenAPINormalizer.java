package com.sap.cloud.sdk.datamodel.openapi.generator;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.openapitools.codegen.OpenAPINormalizer;
import org.openapitools.codegen.utils.ModelUtils;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

/**
 * Fix Api client methods with oneOf primitive param to stay simplified from OpenAPI generator 7.22.0. Also adds OAS
 * 3.1-aware normalisation: nullable warnings, example deprecation warnings, and contentEncoding/contentMediaType →
 * format mapping for binary file uploads.
 */
public class CustomOpenAPINormalizer extends OpenAPINormalizer
{
    private final boolean isOas31;

    /**
     * Initializes OpenAPI Normalizer with a set of rules
     *
     * @param openAPI
     *            OpenAPI
     * @param inputRules
     *            a map of rules
     */
    public CustomOpenAPINormalizer( final @Nonnull OpenAPI openAPI, final @Nonnull Map<String, String> inputRules )
    {
        super(openAPI, inputRules);
        this.isOas31 = OasVersionUtil.isOas31(openAPI);
    }

    /**
     * Normalize reference schema with allOf to support sibling properties. Also warns on OAS 3.1 deprecated keywords
     * when processing a 3.1 spec.
     *
     * @param schema
     *            Schema
     */
    @Override
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    protected void normalizeReferenceSchema( final @Nonnull Schema schema )
    {
        if( schema.getType() != null || schema.getTypes() != null && !schema.getTypes().isEmpty() ) {
            // clears type(s) given that $ref is set
            schema.setType(null);
            schema.setTypes(null);
            LOGGER.warn("Type(s) cleared (set to null) given $ref is set to {}.", schema.get$ref());
        }

        // Gap 1: warn when deprecated nullable: true is used in an OAS 3.1 spec
        if( isOas31 && schema.getNullable() != null ) {
            LOGGER
                .warn(
                    "'nullable: true' is not a valid OAS 3.1 keyword on $ref schema '{}'. "
                        + "Use anyOf: [{{$ref: \"...\"}}, {{type: \"null\"}}] instead.",
                    schema.get$ref());
        }

        if( schema.getTitle() != null
            || schema.getDescription() != null
            || schema.getNullable() != null
            || schema.getDefault() != null
            || schema.getDeprecated() != null
            || schema.getMaximum() != null
            || schema.getMinimum() != null
            // Gap 2: OAS 3.0 boolean exclusiveMaximum/exclusiveMinimum
            || schema.getExclusiveMaximum() != null
            || schema.getExclusiveMinimum() != null
            // Gap 2: OAS 3.1 numeric exclusiveMaximumValue/exclusiveMinimumValue
            || schema.getExclusiveMaximumValue() != null
            || schema.getExclusiveMinimumValue() != null
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
            // Gap 4: OAS 3.1 const keyword as $ref sibling
            || schema.getConst() != null
            || schema.getMultipleOf() != null
            || schema.getPattern() != null
            || (schema.getExtensions() != null && !schema.getExtensions().isEmpty()) ) {
            // Don't wrap in allOf if the referenced schema is a primitive type.
            // The swagger-parser may copy properties (description, example, etc.) from the
            // referenced schema onto the $ref schema object. Wrapping primitives in allOf
            // prevents proper type simplification (e.g., oneOf with a single primitive).
            final Schema referencedSchema = ModelUtils.getReferencedSchema(openAPI, schema);
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

    /**
     * Normalizes any schema (not just $ref schemas). Adds OAS 3.1 specific mappings:
     * <ul>
     * <li>Gap 7: warn on deprecated singular {@code example} keyword in OAS 3.1 schemas</li>
     * <li>Gap 8: map {@code contentEncoding}/{@code contentMediaType} to {@code format} for binary file uploads</li>
     * </ul>
     */
    @Override
    @SuppressWarnings( { "rawtypes" } )
    public Schema normalizeSchema( final @Nonnull Schema schema, final @Nonnull Set<Schema> visitedSchemas )
    {
        // Gap 7: warn on deprecated singular `example` in OAS 3.1 Schema Objects
        if( isOas31 && schema.getExample() != null ) {
            LOGGER
                .warn(
                    "The 'example' keyword is deprecated in OAS 3.1 Schema Objects. "
                        + "Use 'examples: [...]' (array form) instead.");
        }

        // Gap 8: map OAS 3.1 contentEncoding/contentMediaType to legacy format keyword
        // so that downstream type-mapping (File -> byte[]) continues to work.
        if( schema.getFormat() == null ) {
            if( "base64".equalsIgnoreCase(schema.getContentEncoding()) ) {
                schema.setFormat("byte");
            } else if( schema.getContentEncoding() != null ) {
                // Any other content encoding (e.g., "binary") → treat as binary
                schema.setFormat("binary");
            } else if( schema.getContentMediaType() != null ) {
                // contentMediaType without contentEncoding → binary stream
                schema.setFormat("binary");
            }
        }

        return super.normalizeSchema(schema, visitedSchemas);
    }
}
