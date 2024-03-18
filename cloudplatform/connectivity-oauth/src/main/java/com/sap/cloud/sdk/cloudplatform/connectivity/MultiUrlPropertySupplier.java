/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.connectivity.ServiceBindingDestinationOptions.OptionsEnhancer;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * A property supplier that selects between different URLs from a
 * {@link com.sap.cloud.environment.servicebinding.api.ServiceBinding}. To be used with an {@link OptionsEnhancer} that
 * includes the selection choice as option in the {@link ServiceBindingDestinationOptions}.
 *
 * @param <T>
 *            The class of the {@link OptionsEnhancer}.
 */
@Slf4j
final class MultiUrlPropertySupplier<T extends OptionsEnhancer<T>> extends DefaultOAuth2PropertySupplier
{
    /**
     * A URL transformation function that removes the entire path of the provided URL. Can be used with
     * {@link Builder#withUrlKey(OptionsEnhancer, String, Function)}.
     */
    static final Function<URI, URI> REMOVE_PATH = uri -> uri.resolve("/");

    private final Class<T> enhancerClass;
    private final Map<OptionsEnhancer<T>, UrlExtractor> urlExtractors;

    MultiUrlPropertySupplier(
        @Nonnull final ServiceBindingDestinationOptions options,
        @Nonnull final Class<T> enhancerClass,
        @Nonnull final Map<OptionsEnhancer<T>, UrlExtractor> urlExtractors )
    {
        super(options);
        this.enhancerClass = enhancerClass;
        this.urlExtractors = urlExtractors;
    }

    @Nonnull
    @Override
    public URI getServiceUri()
    {
        final Option<T> maybeOption = options.getOption(enhancerClass);
        if( maybeOption.isEmpty() ) {
            throw new DestinationAccessException(
                "No option given for which Business Rules API should be used. Please include an option "
                    + enhancerClass.getName()
                    + " in the service binding destination options.");
        }

        final T option = maybeOption.get();
        final UrlExtractor urlExtractor = urlExtractors.get(option);
        if( urlExtractor == null ) {
            throw new IllegalStateException(
                "Found option value "
                    + option
                    + " for "
                    + enhancerClass.getName()
                    + ", but no URL key was registered for this value. Please ensure that for each possible choice a URL key is registered.");
        }

        return urlExtractor.getUrl(key -> {
            log.debug("Option {} selected, using binding key {}.", option, key);
            return getCredentialOrThrow(URI.class, "endpoints", key);
        });
    }

    /**
     * Start building a {@link MultiUrlPropertySupplier}. You can add the keys under which the different URLs are to be
     * found in a service binding. Finally, register the implementation for the relevant service.
     *
     * @param enhancerClass
     *            The class of the {@link OptionsEnhancer} that includes the selection choice as option in the
     *            {@link ServiceBindingDestinationOptions}.
     * @param <T>
     *            The class of the {@link OptionsEnhancer}.
     * @return A builder for the {@link MultiUrlPropertySupplier}.
     */
    @Nonnull
    static <T extends OptionsEnhancer<T>> Builder<T> of( @Nonnull final Class<T> enhancerClass )
    {
        return new Builder<>(enhancerClass);
    }

    /**
     * Builder to register a {@link MultiUrlPropertySupplier}.
     *
     * @param <T>
     *            the class of the {@link OptionsEnhancer}.
     */
    @RequiredArgsConstructor
    static final class Builder<T extends OptionsEnhancer<T>>
    {
        private final Class<T> enhancerClass;
        private final Map<OptionsEnhancer<T>, UrlExtractor> urlExtractors = new HashMap<>();

        /**
         * Add a key under which the URL is to be found in a service binding for the given option. Typically, the
         * {@code enhancer} should be an enum, and you should add a key for each enum value.
         *
         * @param enhancer
         *            An instance of the {@link OptionsEnhancer} that represents one possible option choice. It will be
         *            used to select the URL and compared to the instance that is eventually passed in the
         *            {@link ServiceBindingDestinationOptions} via {@code hashCode()}. This should typically be an enum
         *            value.
         * @param urlKey
         *            The key under which the URL is to be found in a service binding. It will be looked up in the
         *            {@code endpoints} property of the {@code credentials} section of the service binding.
         * @return This builder.
         */
        @Nonnull
        Builder<T> withUrlKey( @Nonnull final OptionsEnhancer<T> enhancer, @Nonnull final String urlKey )
        {
            urlExtractors.put(enhancer, new UrlExtractor(urlKey));
            return this;
        }

        /**
         * Add a key under which the URL is to be found in a service binding for the given option. Typically, the
         * {@code enhancer} should be an enum, and you should add a key for each enum value. Upon extracting the URL,
         * apply the provided transformation.
         *
         * @param enhancer
         *            An instance of the {@link OptionsEnhancer} that represents one possible option choice. It will be
         *            used to select the URL and compared to the instance that is eventually passed in the
         *            {@link ServiceBindingDestinationOptions} via {@code hashCode()}. This should typically be an enum
         *            value.
         * @param urlKey
         *            The key under which the URL is to be found in a service binding. It will be looked up in the
         *            {@code endpoints} property of the {@code credentials} section of the service binding.
         * @param urlTransformation
         *            A transformation to apply to the extract service URL.
         * @return This builder.
         */
        @Nonnull
        Builder<T> withUrlKey(
            @Nonnull final OptionsEnhancer<T> enhancer,
            @Nonnull final String urlKey,
            @Nonnull final Function<URI, URI> urlTransformation )
        {
            urlExtractors.put(enhancer, new UrlExtractor(urlKey, urlTransformation));
            return this;
        }

        @Nonnull
        Function<ServiceBindingDestinationOptions, OAuth2PropertySupplier> factory()
        {
            return options -> new MultiUrlPropertySupplier<>(options, enhancerClass, urlExtractors);
        }
    }

    @RequiredArgsConstructor
    static final class UrlExtractor
    {
        private static final Function<URI, URI> NO_TRANSFORMATION = url -> url;

        @Nonnull
        private final String urlKey;
        @Nonnull
        private final Function<URI, URI> urlTransformation;

        UrlExtractor( @Nonnull final String urlKey )
        {
            this(urlKey, NO_TRANSFORMATION);
        }

        @Nonnull
        URI getUrl( @Nonnull final Function<String, URI> urlReader )
        {
            final URI rawUri = urlReader.apply(urlKey);
            return urlTransformation.apply(rawUri);
        }
    }
}
