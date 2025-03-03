package com.sap.cloud.sdk.datamodel.openapi.generator;

import javax.annotation.Nonnull;

import org.openapitools.codegen.CodegenModel;

import io.swagger.v3.oas.models.media.Schema;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Fix generation of <code>additionalProperties:true</code> leading to
 * <code>class Foo extends HashMap&lt;String,Object&gt;</code>
 */
@Slf4j
@Getter
public class GeneratorCustomizationFixAdditionalProperties
    implements
    GeneratorCustomization,
    GeneratorCustomization.UpdateModelForObject
{
    private final String configKey = "fixAdditionalProperties";
    private final String configValueDefault = "true";

    @SuppressWarnings( "rawtypes" )
    @Override
    public void updateModelForObject(
        @Nonnull final ChainElementVoid<UpdateModelForObject> chain,
        @Nonnull final CodegenModel m,
        @Nonnull final Schema schema )
    {
        // Disable additional attributes to prevent model classes from extending "HashMap"
        // SAP Cloud SDK offers custom field APIs to handle additional attributes already
        schema.setAdditionalProperties(Boolean.FALSE);
        chain.doNext(next -> next.get().updateModelForObject(next, m, schema));
    }
}
