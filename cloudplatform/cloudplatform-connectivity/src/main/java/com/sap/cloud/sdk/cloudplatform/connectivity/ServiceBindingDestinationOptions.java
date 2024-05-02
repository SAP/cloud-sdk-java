/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.annotations.Beta;
import com.sap.cloud.environment.servicebinding.api.DefaultServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Representation of parameters and context information for the {@link ServiceBindingDestinationLoader} API.
 *
 * @since 4.16.0
 */
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
@Slf4j
@Beta
public final class ServiceBindingDestinationOptions
{
    /**
     * The {@link ServiceBinding} for which an {@link HttpDestination} will be created.
     */
    @Nonnull
    @Getter
    private final ServiceBinding serviceBinding;

    /**
     * Gets the {@link OnBehalfOf}, which determines how the application will authenticate at the target system.
     * <p>
     * If not set explicitly, {@link OnBehalfOf#TECHNICAL_USER_CURRENT_TENANT} will be returned.
     */
    @Nonnull
    @Getter
    private final OnBehalfOf onBehalfOf;

    /**
     * The option mapping to customize destination enhancements, e.g. apply a proxy service.
     */
    @Nonnull
    private final Map<Class<?>, OptionsEnhancer<?>> furtherOptions;

    /**
     * Start building {@code ServiceBindingDestinationOptions} for any {@link ServiceBinding} that matches the given
     * {@link ServiceIdentifier}. Use {@code .build()} to obtain the options object or configure further options via the
     * other builder methods. Note: {@link DefaultServiceBindingAccessor} will be used internally.
     *
     * @param identifier
     *            The {@link ServiceIdentifier} that will be used to look up a {@link ServiceBinding}.
     * @return A {@code Builder} to create {@code ServiceBindingDestinationOptions}.
     * @throws DestinationAccessException
     *             in case <b>not exactly one</b> service binding was found.
     * @see #forService(ServiceBinding)
     */
    @Nonnull
    public static Builder forService( @Nonnull final ServiceIdentifier identifier )
    {
        // Move this out of here once we fix that currently the actual ServiceBindingAccessor instance is on ScpCf platform
        final List<ServiceBinding> bindings =
            DefaultServiceBindingAccessor
                .getInstance()
                .getServiceBindings()
                .stream()
                .filter(binding -> identifier.equals(binding.getServiceIdentifier().orElse(null)))
                .toList();

        if( bindings.isEmpty() ) {
            throw new DestinationAccessException(
                "Could not find any matching service bindings for service identifier '" + identifier + "'");
        }

        if( bindings.size() > 1 ) {
            throw new DestinationAccessException(
                "'"
                    + identifier
                    + "' is ambiguous in this context since multiple service bindings were found matching this identifier. "
                    + "Please make sure only a single instance is bound, or use .forService(ServiceBinding binding) to pass a service binding explicitly.");
        }
        return forService(bindings.get(0));
    }

    /**
     * Start building {@code ServiceBindingDestinationOptions} for the given {@link ServiceBinding}. Use
     * {@code .build()} to obtain the options object or configure further options via the other builder methods.
     *
     * @param serviceBinding
     *            The {@link ServiceBinding} that should be transformed into a destination.
     * @return A {@code Builder} to create {@code ServiceBindingDestinationOptions}.
     * @since 4.20.0
     */
    @Nonnull
    public static Builder forService( @Nonnull final ServiceBinding serviceBinding )
    {
        return new Builder(serviceBinding);
    }

    /**
     * Get additional options by their key.
     *
     * @param cls
     *            The {@link OptionsEnhancer} that will provide the option data. See also {@link Options}.
     * @param <T>
     *            The type of content that this option will represent.
     * @return an {@link Option} containing the additional option, if it was found.
     * @see Options
     * @since 4.20.0
     */
    @SuppressWarnings( "unchecked" )
    @Nonnull
    public <T> Option<T> getOption( @Nonnull final Class<? extends OptionsEnhancer<T>> cls )
    {
        return Option.of(furtherOptions.get(cls)).map(it -> (T) it.getValue());
    }

    /**
     * Builder class to construct a {@code ServiceBindingDestinationOptions} instance.
     *
     * @see #forService(ServiceBinding)
     */
    @RequiredArgsConstructor( access = AccessLevel.PRIVATE )
    public static final class Builder
    {
        @Nonnull
        private final ServiceBinding binding;
        @Nonnull
        private OnBehalfOf behalf = OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT;
        @Nonnull
        private final Map<Class<?>, OptionsEnhancer<?>> furtherOptions = new HashMap<>();

        /**
         * Specify the behalf on which the destination should act. By default
         * {@link OnBehalfOf#TECHNICAL_USER_CURRENT_TENANT} will be used.
         *
         * @param behalf
         *            The {@link OnBehalfOf behalf} to be used.
         * @return This builder instance.
         */
        @Nonnull
        public Builder onBehalfOf( @Nonnull final OnBehalfOf behalf )
        {
            this.behalf = behalf;
            return this;
        }

        /**
         * Add a custom option to the {@code ServiceBindingDestinationOptions}. The options for specific BTP services
         * can be found here: {@link BtpServiceOptions}.
         *
         * @param option
         *            The custom option.
         * @return This builder instance.
         * @throws IllegalArgumentException
         *             in case the option (identified by its Java class) is already set or a lambda function was passed.
         * @see BtpServiceOptions
         * @see #getOption(Class)
         */
        @Nonnull
        public Builder withOption( @Nonnull final OptionsEnhancer<?> option )
        {
            final Class<?> cls = option.getClass();
            if( cls.isAnonymousClass() || cls.isSynthetic() ) {
                throw new IllegalArgumentException("Lambdas or anonymous classes are not supported here.");
            }
            if( furtherOptions.containsKey(cls) ) {
                throw new IllegalArgumentException("The given option " + cls.getSimpleName() + " is already in use.");
            }
            furtherOptions.put(cls, option);
            return this;
        }

        /**
         * Create the {@code ServiceBindingDestinationOptions} instance based on the previously given properties.
         *
         * @return A new {@code ServiceBindingDestinationOptions} object.
         */
        @Nonnull
        public ServiceBindingDestinationOptions build()
        {
            return new ServiceBindingDestinationOptions(binding, behalf, furtherOptions);
        }
    }

    /**
     * An enhancer that can add and retrieve additional options from {@code ServiceBindingDestinationOptions}.
     * <p>
     * A simple example would be an {@code enum MyOption implements OptionsEnhancer<MyOption>} to represent a choice
     * between different values. See {@link BtpServiceOptions} for specific examples.
     *
     * @param <T>
     *            The type of content that this option will represent.
     * @since 4.20.0
     */
    @SuppressWarnings( "InterfaceMayBeAnnotatedFunctional" )
    public interface OptionsEnhancer<T>
    {
        /**
         * Get the value of the option.
         *
         * @return The value of the option.
         */
        @Nonnull
        T getValue();
    }

    /**
     * A set of further options that can be configured using
     * {@link ServiceBindingDestinationOptions.Builder#withOption(OptionsEnhancer)}.
     *
     * @since 4.20.0
     * @see BtpServiceOptions
     */
    public static final class Options
    {
        /**
         * Enhancer that allows to include destinations in the options object that should be decorated with proxy
         * capabilities.
         */
        @RequiredArgsConstructor( access = AccessLevel.PACKAGE, staticName = "destinationToBeProxied" )
        static final class ProxyOptions implements OptionsEnhancer<HttpDestination>
        {
            @Nonnull
            @Getter
            private final HttpDestination value;
        }

        /**
         * Enhancer that allows to include a resilience configuration in the options object.
         */
        @RequiredArgsConstructor( access = AccessLevel.PACKAGE, staticName = "of" )
        static final class ResilienceOptions implements OptionsEnhancer<ResilienceConfiguration>
        {
            @Nonnull
            @Getter
            private final ResilienceConfiguration value;
        }
    }
}
