/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
    private final Class<T> enhancerClass;
    private final Map<OptionsEnhancer<T>, Map<String, String>> urlKeys;

    private static final String URL_KEY = "urlKey";
    private static final String REDUNDANT_PATH = "redundantPath";

    private MultiUrlPropertySupplier(
        @Nonnull final ServiceBindingDestinationOptions options,
        @Nonnull final Class<T> enhancerClass,
        @Nonnull final Map<OptionsEnhancer<T>, Map<String, String>> urlKeys )
    {
        super(options);
        this.enhancerClass = enhancerClass;
        this.urlKeys = urlKeys;
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
        final String bindingKey = urlKeys.get(option).get(URL_KEY);
        if( bindingKey == null ) {
            throw new IllegalStateException(
                "Found option value "
                    + option
                    + " for "
                    + enhancerClass.getName()
                    + ", but no URL key was registered for this value. Please ensure that for each possible choice a URL key is registered.");
        }
        log.debug("Option {} selected, using binding key {}.", option, bindingKey);

        final URI endPointUrl = getCredential(URI.class, "endpoints", bindingKey).get();

        final Option<URI> processedURI =
            Option
                .of(urlKeys.get(option).get(REDUNDANT_PATH))
                .map(path -> removeProvidedPathFromURI(endPointUrl, path));
        return processedURI.getOrElse(endPointUrl);
    }

    private URI removeProvidedPathFromURI( @Nonnull final URI endpointUrl, @Nonnull String redundantPath )
    {
        final String originalPath = endpointUrl.getPath();
        if( !redundantPath.startsWith("/") && !redundantPath.isEmpty() ) {
            redundantPath = "/" + redundantPath;
        }
        // Remove the specified path from the original path
        final String newPath = originalPath.replaceFirst(redundantPath, "");
        final URI newUri;
        try {
            newUri =
                new URI(
                    endpointUrl.getScheme(),
                    endpointUrl.getAuthority(),
                    newPath,
                    endpointUrl.getQuery(),
                    endpointUrl.getFragment());
        }
        catch( URISyntaxException e ) {
            throw new DestinationAccessException(
                "Unable to create a URI from '" + endpointUrl + "' by removing path '" + redundantPath + "'",
                e);
        }
        return newUri;
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
        private final Map<OptionsEnhancer<T>, Map<String, String>> urlKeys = new HashMap<>();

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
            return withUrlKey(enhancer, urlKey, null);
        }

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
         * @param redundantPath
         *            The redundantPath that is provided in a service binding in {@code endpoints} property, A base path
         *            gets added when OpenAPI/OData clients get generated and doesn't have to be included in destination
         *            built from the binding.
         * @return This builder.
         */
        @Nonnull
        Builder<T> withUrlKey(
            @Nonnull final OptionsEnhancer<T> enhancer,
            @Nonnull final String urlKey,
            @Nullable final String redundantPath )
        {
            final Map<String, String> keysHashMap = new HashMap<>();
            keysHashMap.put(URL_KEY, urlKey);
            Option.of(redundantPath).map(val -> keysHashMap.put(REDUNDANT_PATH, val));

            urlKeys.put(enhancer, keysHashMap);
            return this;
        }

        @Nonnull
        Function<ServiceBindingDestinationOptions, OAuth2PropertySupplier> factory()
        {
            return options -> new MultiUrlPropertySupplier<>(options, enhancerClass, urlKeys);
        }
    }
}
