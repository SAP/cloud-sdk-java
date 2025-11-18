package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Contains parameters to configure the behaviour of destination loaders. Instances are intended to be immutable, and
 * are created using the builder pattern.
 */
@EqualsAndHashCode
@AllArgsConstructor( access = AccessLevel.PRIVATE )
public final class DestinationOptions
{
    @Nonnull
    private final Map<String, Object> parameters;

    /**
     * Retrieves the value of a configuration parameter.
     *
     * @param key
     *            Name of the configuration parameter to retrieve.
     * @return An {@link Option} that contains the value if present, otherwise {@link io.vavr.control.Option.None}
     */
    @Nonnull
    public Option<Object> get( @Nonnull final String key )
    {
        return Option.of(parameters.get(key));
    }

    /**
     * Get all defined options.
     *
     * @return A set of all option keys.
     * @since 5.22.0
     */
    @Nonnull
    public Set<String> getOptionKeys()
    {
        return Set.copyOf(parameters.keySet());
    }

    /**
     * Creates a builder for instantiating {@link DestinationOptions} objects.
     *
     * @return A new builder.
     */
    @Nonnull
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Creates a builder, copying the given options as a starting point.
     *
     * @param baseOptions
     *            The {@code DestinationOptions} to copy the values from.
     * @return A new builder with the initialized values from the given {@code DestinationOptions}.
     */
    @Nonnull
    public static Builder builder( @Nonnull final DestinationOptions baseOptions )
    {
        return new Builder(baseOptions);
    }

    /**
     * Used for setting up new {@link DestinationOptions} instances.
     */
    @NoArgsConstructor( access = AccessLevel.PRIVATE )
    public static final class Builder
    {
        private final Map<String, Object> builderParameters = new HashMap<>();

        private Builder( @Nonnull final DestinationOptions baseOptions )
        {
            builderParameters.putAll(baseOptions.parameters);
        }

        /**
         * Sets a configuration parameter. If the parameter already exists it will be overwritten with the newly
         * provided value.
         *
         * @param key
         *            Name of the configuration parameter to set.
         * @param value
         *            Value to assign to the configuration parameter.
         * @return The same builder that called this method.
         */
        @Nonnull
        public Builder parameter( @Nonnull final String key, @Nullable final Object value )
        {
            builderParameters.put(key, value);
            return this;
        }

        /**
         * Sets a configuration parameter. If the parameter already exists it will be not be overwritten.
         *
         * @param key
         *            Name of the configuration parameter to set.
         * @param value
         *            Value to assign to the configuration parameter.
         * @return The same builder that called this method.
         */
        @Nonnull
        public Builder parameterIfAbsent( @Nonnull final String key, @Nullable final Object value )
        {
            builderParameters.putIfAbsent(key, value);
            return this;
        }

        /**
         * Use another class that implements {@link DestinationOptionsAugmenter} to set configuration parameters that
         * are specific to a platform/environment/etc.
         *
         * Once called, any parameter setting methods of the augmenter class will affect this builder and the resulting
         * {@link DestinationOptions} object.
         *
         * @param augmenter
         *            An instance of an augmenter class that provides specialized parameter setting
         * @return The augmenter class instance, which is now aware of this builder.
         */
        @Nonnull
        public Builder augmentBuilder( @Nonnull final DestinationOptionsAugmenter augmenter )
        {
            augmenter.augmentBuilder(this);
            return this;
        }

        /**
         * Creates an immutable instance of {@link DestinationOptions} with whatever parameters have been set through
         * the builder and any augmenters attached to it.
         *
         * @return An immutable instance of {@link DestinationOptions}.
         */
        @Nonnull
        public DestinationOptions build()
        {
            return new DestinationOptions(builderParameters);
        }
    }
}
