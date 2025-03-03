package com.sap.cloud.sdk.datamodel.openapi.generator;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenProperty;
import org.openapitools.codegen.languages.JavaClientCodegen;
import org.openapitools.codegen.model.ModelMap;
import org.openapitools.codegen.model.OperationsMap;

import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class CustomJavaClientCodegen extends JavaClientCodegen
{
    private final List<GeneratorCustomization> customizations;
    private final GenerationConfiguration config;

    public CustomJavaClientCodegen( final GenerationConfiguration config )
    {
        this.config = config;
        this.customizations = GeneratorCustomization.getCustomizations(config);
    }

    private <HandlerT extends GeneratorCustomization.ContextualReturn<HandlerT>, ValueT> ValueT chainedContextReturn(
        @Nonnull final Class<? extends HandlerT> handlerClass,
        @Nonnull final HandlerT rootHandler,
        @Nonnull final Function<GeneratorCustomization.ContextReturn<HandlerT, ValueT>, ValueT> initiator )
    {
        var chainedContext = rootHandler.<ValueT> createContext(config, null);
        for( final GeneratorCustomization customization : customizations ) {
            if( handlerClass.isInstance(customization) ) {
                chainedContext = handlerClass.cast(customization).createContext(config, chainedContext);
            }
        }
        return initiator.apply(chainedContext);
    }

    private <HandlerT extends GeneratorCustomization.ContextualVoid<HandlerT>> void chainedContextVoid(
        @Nonnull final Class<? extends HandlerT> handlerClass,
        @Nonnull final HandlerT rootHandler,
        @Nonnull final Consumer<GeneratorCustomization.ContextVoid<HandlerT>> initiator )
    {
        var chainedContext = rootHandler.createContext(config, null);
        for( final GeneratorCustomization customization : customizations ) {
            if( handlerClass.isInstance(customization) ) {
                chainedContext = handlerClass.cast(customization).createContext(config, chainedContext);
            }
        }
        initiator.accept(chainedContext);
    }

    @Override
    public void preprocessOpenAPI( @Nonnull final OpenAPI openAPI )
    {
        chainedContextVoid(
            GeneratorCustomization.PreProcessOpenAPI.class,
            ( context, openAPI1 ) -> super.preprocessOpenAPI(openAPI1),
            context -> context.get().preprocessOpenAPI(context, openAPI));
    }

    @Override
    protected
        void
        updatePropertyForArray( @Nonnull final CodegenProperty property, @Nonnull final CodegenProperty innerProperty )
    {
        chainedContextVoid(
            GeneratorCustomization.UpdatePropertyForArray.class,
            ( context, property1, innerProperty1 ) -> super.updatePropertyForArray(property1, innerProperty1),
            context -> context.get().updatePropertyForArray(context, property, innerProperty));
    }

    @SuppressWarnings( { "rawtypes", "RedundantSuppression" } )
    @Override
    @Nullable
    public String toDefaultValue( @Nonnull final CodegenProperty cp, @Nonnull final Schema schema )
    {
        return chainedContextReturn(
            GeneratorCustomization.ToDefaultValue.class,
            ( context, cp1, schema1 ) -> super.toDefaultValue(cp1, schema1),
            context -> context.get().toDefaultValue(context, cp, schema));
    }

    @Override
    @Nullable
    public String toBooleanGetter( @Nullable final String name )
    {
        return chainedContextReturn(
            GeneratorCustomization.ToBooleanGetter.class,
            ( context, name1 ) -> super.toBooleanGetter(name1),
            context -> context.get().toBooleanGetter(context, name));
    }

    @Override
    @Nonnull
    public
        OperationsMap
        postProcessOperationsWithModels( @Nonnull final OperationsMap ops, @Nonnull final List<ModelMap> allModels )
    {
        return chainedContextReturn(
            GeneratorCustomization.PostProcessOperationsWithModels.class,
            ( context, ops1, allModels1 ) -> super.postProcessOperationsWithModels(ops1, allModels1),
            context -> context.get().postProcessOperationsWithModels(context, ops, allModels));
    }

    @SuppressWarnings( { "rawtypes", "RedundantSuppression" } )
    @Override
    protected void updateModelForComposedSchema(
        @Nonnull final CodegenModel m,
        @Nonnull final Schema schema,
        @Nonnull final Map<String, Schema> allDefinitions )
    {
        chainedContextVoid(
            GeneratorCustomization.UpdateModelForComposedSchema.class,
            ( context, m1, schema1, allDefinitions1 ) -> super.updateModelForComposedSchema(
                m1,
                schema1,
                allDefinitions1),
            context -> context.get().updateModelForComposedSchema(context, m, schema, allDefinitions));
    }

    @SuppressWarnings( { "rawtypes", "RedundantSuppression" } )
    @Override
    protected void updateModelForObject( @Nonnull final CodegenModel m, @Nonnull final Schema schema )
    {
        chainedContextVoid(
            GeneratorCustomization.UpdateModelForObject.class,
            ( context, m1, schema1 ) -> super.updateModelForObject(m1, schema1),
            context -> context.get().updateModelForObject(context, m, schema));
    }
}
