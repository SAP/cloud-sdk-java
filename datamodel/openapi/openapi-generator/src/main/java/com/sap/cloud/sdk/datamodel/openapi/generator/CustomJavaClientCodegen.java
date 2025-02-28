package com.sap.cloud.sdk.datamodel.openapi.generator;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenProperty;
import org.openapitools.codegen.languages.JavaClientCodegen;
import org.openapitools.codegen.model.ModelMap;
import org.openapitools.codegen.model.OperationsMap;

import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;

import io.swagger.v3.oas.models.media.Schema;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class CustomJavaClientCodegen extends JavaClientCodegen
{
    private final List<GeneratorCustomization> customizations;

    public CustomJavaClientCodegen( final GenerationConfiguration config )
    {
        this.customizations = GeneratorCustomization.getCustomizations(config);
    }

    @Override
    protected
        void
        updatePropertyForArray( @Nonnull final CodegenProperty property, @Nonnull final CodegenProperty innerProperty )
    {
        for( final GeneratorCustomization customization : customizations ) {
            if( customization instanceof final GeneratorCustomization.UpdatePropertyForArray custom ) {
                custom.updatePropertyForArray(this, property, innerProperty);
            }
        }
        super.updatePropertyForArray(property, innerProperty);
    }

    @SuppressWarnings( { "rawtypes", "RedundantSuppression" } )
    @Override
    @Nullable
    public String toDefaultValue( @Nonnull final CodegenProperty cp, @Nonnull final Schema schema )
    {
        final String superValue = super.toDefaultValue(cp, schema);
        for( final GeneratorCustomization customization : customizations ) {
            if( customization instanceof final GeneratorCustomization.ToDefaultValue custom ) {
                return custom.toDefaultValue(this, superValue, cp, schema);
            }
        }
        return superValue;
    }

    @Override
    @Nullable
    public String toBooleanGetter( @Nullable final String name )
    {
        final String superValue = super.toBooleanGetter(name);
        for( final GeneratorCustomization customization : customizations ) {
            if( customization instanceof final GeneratorCustomization.ToBooleanGetter custom ) {
                return custom.toBooleanGetter(this, superValue, name);
            }
        }
        return superValue;
    }

    @Override
    @Nonnull
    public
        OperationsMap
        postProcessOperationsWithModels( @Nonnull OperationsMap ops, @Nonnull final List<ModelMap> allModels )
    {
        for( final GeneratorCustomization customization : customizations ) {
            if( customization instanceof final GeneratorCustomization.PostProcessOperationsWithModels custom ) {
                ops = custom.postProcessOperationsWithModels(this, ops, allModels);
            }
        }
        return super.postProcessOperationsWithModels(ops, allModels);
    }

    @SuppressWarnings( { "rawtypes", "RedundantSuppression" } )
    @Override
    protected void updateModelForComposedSchema(
        @Nonnull final CodegenModel m,
        @Nonnull final Schema schema,
        @Nonnull final Map<String, Schema> allDefinitions )
    {
        super.updateModelForComposedSchema(m, schema, allDefinitions);
        for( final GeneratorCustomization customization : customizations ) {
            if( customization instanceof final GeneratorCustomization.UpdateModelForComposedSchema custom ) {
                custom.updateModelForComposedSchema(this, m, schema, allDefinitions);
            }
        }
    }

    @SuppressWarnings( { "rawtypes", "RedundantSuppression" } )
    @Override
    protected void updateModelForObject( @Nonnull final CodegenModel m, @Nonnull final Schema schema )
    {
        for( final GeneratorCustomization customization : customizations ) {
            if( customization instanceof final GeneratorCustomization.UpdateModelForObject custom ) {
                custom.updateModelForObject(this, m, schema);
            }
        }
        super.updateModelForObject(m, schema);
    }
}
