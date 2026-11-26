package com.sap.cloud.sdk.datamodel.openapi.generator;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.openapitools.codegen.OpenAPINormalizer;
import org.openapitools.codegen.utils.ModelUtils;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

/**
 * Fix Api client methods with oneOf primitive param to stay simplified from OpenAPI generator 7.22.0. Also adds OAS
 * 3.1-aware normalisation: nullable warnings, example deprecation warnings, and contentEncoding/contentMediaType →
 * format mapping for binary file uploads. Object-only unions containing an unrestricted object alternative are
 * represented as {@code Map<String, Object>} instead of competing model subtypes.
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

        // warn when deprecated nullable: true is used in an OAS 3.1 spec
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
            // OAS 3.0 boolean exclusiveMaximum/exclusiveMinimum
            || schema.getExclusiveMaximum() != null
            || schema.getExclusiveMinimum() != null
            // OAS 3.1 numeric exclusiveMaximumValue/exclusiveMinimumValue
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
            // OAS 3.1 const keyword as $ref sibling
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
     * <li>warn on deprecated singular {@code example} keyword in OAS 3.1 schemas</li>
     * <li>map {@code contentEncoding}/{@code contentMediaType} to {@code format} for binary file uploads</li>
     * </ul>
     * Also simplifies overlapping object unions using {@link #simplifyFreeFormObjectUnion(Schema)}.
     *
     * @param schema
     *            The schema to normalize.
     * @param visitedSchemas
     *            Schemas already visited while traversing the document.
     * @return The normalized schema, including the inherited OpenAPI normalization rules.
     */
    @Override
    @Nonnull
    @SuppressWarnings( { "rawtypes" } )
    public Schema normalizeSchema( final @Nonnull Schema schema, final @Nonnull Set<Schema> visitedSchemas )
    {
        // warn on deprecated singular `example` in OAS 3.1 Schema Objects
        if( isOas31 && schema.getExample() != null ) {
            LOGGER
                .warn(
                    "The 'example' keyword is deprecated in OAS 3.1 Schema Objects. "
                        + "Use 'examples: [...]' (array form) instead.");
        }

        // map OAS 3.1 contentEncoding/contentMediaType to legacy format keyword
        // so that downstream type-mapping (File -> byte[]) continues to work.
        if( isOas31 && schema.getFormat() == null ) {
            if( "base64".equalsIgnoreCase(schema.getContentEncoding()) ) {
                schema.setFormat("byte");
            } else if( schema.getContentEncoding() != null ) {
                // Any other content encoding (e.g., "binary") → treat as binary
                schema.setFormat("binary");
            } else if( schema.getContentMediaType() != null
                && !"application/json".equalsIgnoreCase(schema.getContentMediaType()) ) {
                // contentMediaType without contentEncoding → binary stream
                schema.setFormat("binary");
            }
        }

        simplifyFreeFormObjectUnion(schema);
        return super.normalizeSchema(schema, visitedSchemas);
    }

    /**
     * Simplifies a {@code oneOf} or {@code anyOf} union of object schemas when one alternative accepts every object.
     * The schema is changed in place to {@code type: object} with {@code additionalProperties: true}, which generates a
     * {@code Map<String, Object>} property without a union model or subtype annotations.
     *
     * <p>
     * For example, both alternatives below are represented by a single map:
     *
     * <pre>{@code
     * oneOf:
     *   - type: object
     *     additionalProperties: true
     *   - type: object
     *     properties:
     *       enabled:
     *         type: boolean
     * }</pre>
     *
     * <p>
     * For {@code oneOf}, this deliberately broadens the client-side representation rather than preserving exactly-one
     * validation. Unions containing non-object types, constrained references, or no unrestricted alternative are
     * retained. Existing schema metadata, such as its description, is preserved.
     *
     * @param schema
     *            The schema whose union alternatives are inspected and, when applicable, replaced.
     */
    @SuppressWarnings( { "rawtypes", "unchecked" } )
    private void simplifyFreeFormObjectUnion( final Schema schema )
    {
        final List<Schema> alternatives = schema.getOneOf() != null ? schema.getOneOf() : schema.getAnyOf();
        if( alternatives == null || alternatives.isEmpty() || schema.getOneOf() != null && schema.getAnyOf() != null ) {
            return;
        }
        final List<Schema> resolved = alternatives.stream().map(s -> resolveSchema(s)).toList();
        if( resolved.stream().anyMatch(s -> s == null || !isObjectOnly(s))
            || resolved.stream().noneMatch(this::acceptsAnyObject) ) {
            return;
        }
        schema.setOneOf(null);
        schema.setAnyOf(null);
        schema.setDiscriminator(null);
        schema.setType("object");
        schema.setTypes(null);
        schema.setAdditionalProperties(true);
    }

    /**
     * Checks whether the schema declares only the object type. A type set containing strings, numbers, or {@code null}
     * must not be narrowed to a map. Property restrictions are checked separately.
     *
     * @param schema
     *            The schema to inspect.
     * @return Whether the declared type or type set contains only {@code object}.
     */
    @SuppressWarnings( "rawtypes" )
    private boolean isObjectOnly( final Schema schema )
    {
        return ("object".equals(schema.getType()) || Set.of("object").equals(schema.getTypes()))
            && (schema.getTypes() == null || schema.getTypes().isEmpty() || Set.of("object").equals(schema.getTypes()));
    }

    /**
     * Follows unrestricted component {@code $ref} chains in the current OpenAPI document. A reference carrying property
     * restrictions is returned unchanged so its sibling constraints are not discarded.
     *
     * @param schema
     *            The schema or reference to resolve.
     * @return The resolved schema, the original constrained reference, or {@code null} for a missing target or cycle.
     */
    @Nullable
    @SuppressWarnings( "rawtypes" )
    private Schema resolveSchema( final Schema schema )
    {
        Schema current = schema;
        final Set<Schema> visited = new HashSet<>();
        while( current != null && current.get$ref() != null ) {
            if( !acceptsAnyObject(current) ) {
                return current;
            }
            if( !visited.add(current) ) {
                return null;
            }
            current = ModelUtils.getReferencedSchema(openAPI, current);
        }
        return current;
    }

    /**
     * Checks whether the inspected keywords allow arbitrary object properties with unrestricted values. Closed objects,
     * declared or required properties, typed map values, and object validation constraints do not qualify. This check
     * does not follow references or inspect the declared type; callers handle those separately.
     *
     * <p>
     * For example, {@code type: object, additionalProperties: true} with no properties qualifies, whereas
     * {@code additionalProperties: false} or an additional-properties schema of {@code type: string} does not.
     *
     * @param schema
     *            The schema, or reference siblings, whose object constraints are inspected.
     * @return Whether no inspected keyword restricts the contents of an arbitrary object.
     */
    @SuppressWarnings( "rawtypes" )
    private boolean acceptsAnyObject( final Schema schema )
    {
        final Object additionalProperties = schema.getAdditionalProperties();
        final boolean arbitraryValues =
            additionalProperties == null
                || Boolean.TRUE.equals(additionalProperties)
                || new Schema<>().equals(additionalProperties);
        return arbitraryValues
            && (schema.getProperties() == null || schema.getProperties().isEmpty())
            && (schema.getRequired() == null || schema.getRequired().isEmpty())
            && !ModelUtils.isComposedSchema(schema)
            && schema.getEnum() == null
            && schema.getConst() == null
            && schema.getNot() == null
            && schema.getMaxProperties() == null
            && (schema.getMinProperties() == null || schema.getMinProperties() == 0)
            && (schema.getPatternProperties() == null || schema.getPatternProperties().isEmpty())
            && schema.getPropertyNames() == null
            && schema.getUnevaluatedProperties() == null
            && (schema.getDependentSchemas() == null || schema.getDependentSchemas().isEmpty())
            && (schema.getDependentRequired() == null || schema.getDependentRequired().isEmpty())
            && schema.getIf() == null
            && schema.getThen() == null
            && schema.getElse() == null
            && !Boolean.FALSE.equals(schema.getBooleanSchemaValue());
    }
}
