package com.sap.cloud.sdk.datamodel.openapi.generator;

import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenProperty;
import org.openapitools.codegen.model.ModelMap;
import org.openapitools.codegen.model.OperationsMap;

import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

/**
 * Optional feature toggles, may be used internally only.
 */
public interface GeneratorCustomization
{
    /**
     * Get the configuration key.
     */
    @Nullable
    default String getConfigKey()
    {
        return null;
    }

    /**
     * Get the default active value of the feature.
     *
     * @return The default active value of the feature (false).
     */
    @Nullable
    default String getConfigValueDefault()
    {
        return null;
    }

    /**
     * Get the available customizations.
     *
     * @return The customizations.
     */
    @Nonnull
    static List<GeneratorCustomization> getCustomizations()
    {
        final var customizationLoader = ServiceLoader.load(GeneratorCustomization.class);
        return StreamSupport.stream(customizationLoader.spliterator(), false).toList();
    }

    /**
     * Get the customizations for the given configuration.
     *
     * @param config
     *            The generation configuration.
     * @return The customizations.
     */
    @Nonnull
    static List<GeneratorCustomization> getCustomizations( @Nonnull final GenerationConfiguration config )
    {
        return getCustomizations().stream().filter(c -> c.isConfigEnabled(config)).toList();
    }

    /**
     * Check if the feature is enabled.
     *
     * @param config
     *            The generation configuration.
     * @return True if the feature is enabled, false otherwise.
     */
    default boolean isConfigEnabled( @Nonnull final GenerationConfiguration config )
    {
        final var value = getConfigValue(config);
        return value != null && !value.isEmpty() && !"false".equalsIgnoreCase(value.trim());
    }

    /**
     * Get the value of the feature.
     *
     * @param config
     *            The generation configuration.
     * @return The value of the feature.
     */
    @Nullable
    default String getConfigValue( @Nonnull final GenerationConfiguration config )
    {
        return config.getAdditionalProperties().getOrDefault(getConfigKey(), getConfigValueDefault());
    }

    /**
     * Context for customization.
     */
    interface ChainElement<HandlerT>
    {
        /**
         * Get the customization handler.
         *
         * @return The customization handler.
         */
        @Nonnull
        HandlerT get();

        /**
         * Get the generation configuration.
         *
         * @return The generation configuration.
         */
        @Nonnull
        GenerationConfiguration config();
    }

    /**
     * Context for customization using chained methods without return type.
     */
    interface ChainElementVoid<HandlerT extends ChainableVoid<HandlerT>> extends ChainElement<HandlerT>
    {
        /**
         * Get next customization handler.
         *
         * @return The customization handler.
         */
        @Nullable
        ChainElementVoid<HandlerT> next();

        /**
         * Continue with the next customization.
         *
         * @param next
         *            The next customization.
         */
        default void doNext( @Nonnull final Consumer<ChainElementVoid<HandlerT>> next )
        {
            next.accept(next());
        }
    }

    /**
     * Context for customizationusing chained methods with return type.
     */
    interface ChainElementReturn<HandlerT extends ChainableReturn<HandlerT>, ValueT> extends ChainElement<HandlerT>
    {
        /**
         * Get next customization handler.
         *
         * @return The customization handler.
         */
        @Nullable
        ChainElementReturn<HandlerT, ValueT> next();

        /**
         * Continue with the next customization.
         *
         * @param next
         *            The next customization.
         * @return The return value.
         */
        @SuppressWarnings( "PMD.NullAnnotationMissingOnPublicMethod" )
        default ValueT doNext( @Nonnull final Function<ChainElementReturn<HandlerT, ValueT>, ValueT> next )
        {
            return next.apply(next());
        }
    }

    interface ChainableReturn<HandlerT extends ChainableReturn<HandlerT>>
    {
        default <ValueT> ChainElementReturn<HandlerT, ValueT> chained(
            @Nonnull final GenerationConfiguration config,
            @Nullable final ChainElementReturn<HandlerT, ValueT> next )
        {
            @SuppressWarnings( "unchecked" )
            final HandlerT self = (HandlerT) this;
            return new ChainElementReturn<>()
            {
                @Nonnull
                @Override
                public HandlerT get()
                {
                    return self;
                }

                @Nullable
                @Override
                public ChainElementReturn<HandlerT, ValueT> next()
                {
                    return next;
                }

                @Nonnull
                @Override
                public GenerationConfiguration config()
                {
                    return config;
                }
            };
        }
    }

    interface ChainableVoid<HandlerT extends ChainableVoid<HandlerT>>
    {
        default
            ChainElementVoid<HandlerT>
            chained( @Nonnull final GenerationConfiguration config, @Nullable final ChainElementVoid<HandlerT> next )
        {
            @SuppressWarnings( "unchecked" )
            final HandlerT self = (HandlerT) this;
            return new ChainElementVoid<>()
            {
                @Nonnull
                @Override
                public HandlerT get()
                {
                    return self;
                }

                @Nullable
                @Override
                public ChainElementVoid<HandlerT> next()
                {
                    return next;
                }

                @Nonnull
                @Override
                public GenerationConfiguration config()
                {
                    return config;
                }
            };
        }
    }

    /**
     * Update the model for a composed schema.
     */
    interface UpdatePropertyForArray extends ChainableVoid<UpdatePropertyForArray>
    {
        /**
         * Update the model for a composed schema.
         *
         * @param chain
         *            The customization chain.
         * @param property
         *            The property.
         * @param innerProperty
         *            The inner property.
         */
        void updatePropertyForArray(
            @Nonnull final ChainElementVoid<UpdatePropertyForArray> chain,
            @Nonnull final CodegenProperty property,
            @Nonnull final CodegenProperty innerProperty );
    }

    /**
     * Get the default value.
     */
    interface ToDefaultValue extends ChainableReturn<ToDefaultValue>
    {
        /**
         * Get the default value.
         *
         * @param chain
         *            The customization chain.
         * @param cp
         *            The codegen property.
         * @param schema
         *            The schema.
         * @return The default value.
         */
        @Nullable
        @SuppressWarnings( "rawtypes" )
        String toDefaultValue(
            @Nonnull final ChainElementReturn<ToDefaultValue, String> chain,
            @Nonnull final CodegenProperty cp,
            @Nonnull final Schema schema );
    }

    /**
     * Get the boolean getter.
     */
    interface ToBooleanGetter extends ChainableReturn<ToBooleanGetter>
    {
        /**
         * Get the boolean getter.
         *
         * @param chain
         *            The customization chain.
         * @param name
         *            The name.
         * @return The boolean getter.
         */
        @Nullable
        String toBooleanGetter(
            @Nonnull final ChainElementReturn<ToBooleanGetter, String> chain,
            @Nullable final String name );
    }

    /**
     * Update the model for a composed schema.
     */
    interface UpdateModelForComposedSchema extends ChainableVoid<UpdateModelForComposedSchema>
    {
        /**
         * Update the model for a composed schema.
         *
         * @param chain
         *            The customization chain.
         * @param m
         *            The model.
         * @param schema
         *            The schema.
         * @param allDefinitions
         *            The definitions.
         */
        @SuppressWarnings( "rawtypes" )
        void updateModelForComposedSchema(
            @Nonnull final ChainElementVoid<UpdateModelForComposedSchema> chain,
            @Nonnull final CodegenModel m,
            @Nonnull final Schema schema,
            @Nonnull final Map<String, Schema> allDefinitions );
    }

    /**
     * Post-process operations with models.
     */
    interface PostProcessOperationsWithModels extends ChainableReturn<PostProcessOperationsWithModels>
    {
        /**
         * Post-process operations with models.
         *
         * @param chain
         *            The customization chain.
         * @param ops
         *            The operations.
         * @param allModels
         *            The models.
         * @return The operations.
         */
        @Nonnull
        OperationsMap postProcessOperationsWithModels(
            @Nonnull final ChainElementReturn<PostProcessOperationsWithModels, OperationsMap> chain,
            @Nonnull final OperationsMap ops,
            @Nonnull final List<ModelMap> allModels );
    }

    /**
     * Update the model for an object.
     */
    interface UpdateModelForObject extends ChainableVoid<UpdateModelForObject>
    {
        /**
         * Update the model for an object.
         *
         * @param chain
         *            The customization chain.
         * @param m
         *            The model.
         * @param schema
         *            The schema.
         */
        @SuppressWarnings( "rawtypes" )
        void updateModelForObject(
            @Nonnull final ChainElementVoid<UpdateModelForObject> chain,
            @Nonnull final CodegenModel m,
            @Nonnull final Schema schema );
    }

    /**
     * Pre-process the OpenAPI model.
     */
    interface PreProcessOpenAPI extends ChainableVoid<PreProcessOpenAPI>
    {
        /**
         * Preprocess the OpenAPI model.
         *
         * @param chain
         *            The customization chain.
         * @param openAPI
         *            The OpenAPI model.
         */
        void preprocessOpenAPI(
            @Nonnull final ChainElementVoid<PreProcessOpenAPI> chain,
            @Nonnull final OpenAPI openAPI );
    }
}
