package com.sap.cloud.sdk.datamodel.openapi.generator;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;

import lombok.RequiredArgsConstructor;

/**
 * Optional feature toggles, may be used internally only.
 */
@RequiredArgsConstructor
enum GeneratorCustomProperties
{
    /**
     * Use JsonCreator instead of sub-type deduction for oneOf and anyOf schemas.
     */
    USE_ONE_OF_CREATORS("useOneOfCreators", "false"),

    /**
     * Fix isIsBoolean() to isBoolean() for fields specified as `"isBoolean":{"type":"boolean"}`.
     */
    FIX_REDUNDANT_IS_BOOLEAN_PREFIX("fixRedundantIsBooleanPrefix", "false");

    private final String key;
    private final String defaultValue;

    /**
     * Check if the feature is enabled.
     *
     * @param config
     *            The generation configuration.
     * @return True if the feature is enabled, false otherwise.
     */
    public boolean isEnabled( @Nonnull final GenerationConfiguration config )
    {
        final var value = getValue(config);
        return !value.isEmpty() && !"false".equalsIgnoreCase(value.trim());
    }

    /**
     * Get the value of the feature.
     *
     * @param config
     *            The generation configuration.
     * @return The value of the feature.
     */
    @Nonnull
    public String getValue( @Nonnull final GenerationConfiguration config )
    {
        return config.getAdditionalProperties().getOrDefault(key, defaultValue);
    }
}
