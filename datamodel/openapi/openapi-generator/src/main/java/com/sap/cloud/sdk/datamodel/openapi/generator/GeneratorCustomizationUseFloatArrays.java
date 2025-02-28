package com.sap.cloud.sdk.datamodel.openapi.generator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.openapitools.codegen.CodegenProperty;
import org.openapitools.codegen.languages.JavaClientCodegen;

import io.swagger.v3.oas.models.media.Schema;
import lombok.Getter;

/**
 * Use float arrays instead of big-decimal lists.
 */
@Getter
public class GeneratorCustomizationUseFloatArrays
    implements
    GeneratorCustomization,
    GeneratorCustomization.ToDefaultValue,
    GeneratorCustomization.UpdatePropertyForArray
{
    private final String configKey = "useFloatArrays";

    @Override
    public void updatePropertyForArray(
        @Nonnull final JavaClientCodegen ref,
        @Nonnull final CodegenProperty property,
        @Nonnull final CodegenProperty innerProperty )
    {
        if( innerProperty.isNumber && property.isArray ) {
            property.dataType = "float[]";
            property.datatypeWithEnum = "float[]";
            property.isArray = false; // set false to omit `add{{nameInPascalCase}}Item(...)` convenience method
            property.vendorExtensions.put("isPrimitiveArray", true);
        }
    }

    @Override
    @SuppressWarnings( "rawtypes" )
    @Nullable
    public String toDefaultValue(
        @Nonnull final JavaClientCodegen ref,
        @Nonnull final String superValue,
        @Nonnull final CodegenProperty cp,
        @Nonnull final Schema schema )
    {
        if( "float[]".equals(cp.dataType) ) {
            return null;
        }
        return superValue;
    }
}
