package com.sap.cloud.sdk.datamodel.openapi.generator;

import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenProperty;
import org.openapitools.codegen.languages.JavaClientCodegen;
import org.openapitools.codegen.model.ModelMap;
import org.openapitools.codegen.model.OperationsMap;

import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;

import io.swagger.v3.oas.models.media.Schema;

/**
 * Optional feature toggles, may be used internally only.
 */
interface GeneratorCustomization
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
     * Update the model for a composed schema.
     */
    interface UpdatePropertyForArray
    {
        /**
         * Update the model for a composed schema.
         *
         * @param ref
         *            The codegen reference.
         * @param property
         *            The property.
         * @param innerProperty
         *            The inner property.
         */
        void updatePropertyForArray(
            @Nonnull final JavaClientCodegen ref,
            @Nonnull final CodegenProperty property,
            @Nonnull final CodegenProperty innerProperty );
    }

    /**
     * Get the default value.
     */
    interface ToDefaultValue
    {
        /**
         * Get the default value.
         *
         * @param ref
         *            The codegen reference.
         * @param superValue
         *            The default value.
         * @param cp
         *            The codegen property.
         * @param schema
         *            The schema.
         * @return The default value.
         */
        @Nullable
        @SuppressWarnings( "rawtypes" )
        String toDefaultValue(
            @Nonnull final JavaClientCodegen ref,
            String superValue,
            @Nonnull final CodegenProperty cp,
            @Nonnull final Schema schema );
    }

    /**
     * Get the boolean getter.
     */
    interface ToBooleanGetter
    {
        /**
         * Get the boolean getter.
         *
         * @param ref
         *            The codegen reference.
         * @param superValue
         *            The default value.
         * @param name
         *            The name.
         * @return The boolean getter.
         */
        @Nullable
        String toBooleanGetter( @Nonnull final JavaClientCodegen ref, String superValue, @Nullable final String name );
    }

    /**
     * Update the model for a composed schema.
     */
    interface UpdateModelForComposedSchema
    {
        /**
         * Update the model for a composed schema.
         *
         * @param ref
         *            The codegen reference.
         * @param m
         *            The model.
         * @param schema
         *            The schema.
         * @param allDefinitions
         *            The definitions.
         */
        @SuppressWarnings( "rawtypes" )
        void updateModelForComposedSchema(
            @Nonnull final JavaClientCodegen ref,
            @Nonnull final CodegenModel m,
            @Nonnull final Schema schema,
            @Nonnull final Map<String, Schema> allDefinitions );
    }

    /**
     * Post-process operations with models.
     */
    interface PostProcessOperationsWithModels
    {
        /**
         * Post-process operations with models.
         *
         * @param ref
         *            The codegen reference.
         * @param ops
         *            The operations.
         * @param allModels
         *            The models.
         * @return The operations.
         */
        @Nonnull
        OperationsMap postProcessOperationsWithModels(
            @Nonnull final JavaClientCodegen ref,
            @Nonnull final OperationsMap ops,
            @Nonnull final List<ModelMap> allModels );
    }

    /**
     * Update the model for an object.
     */
    interface UpdateModelForObject
    {
        /**
         * Update the model for an object.
         *
         * @param ref
         *            The codegen reference.
         * @param m
         *            The model.
         * @param schema
         *            The schema.
         */
        @SuppressWarnings( "rawtypes" )
        void updateModelForObject(
            @Nonnull final JavaClientCodegen ref,
            @Nonnull final CodegenModel m,
            @Nonnull final Schema schema );
    }
}
