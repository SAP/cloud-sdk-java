/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;
import com.sap.cloud.sdk.cloudplatform.util.FacadeLocator;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * This implementation is a utility class that is capable of transforming a given
 * {@link ServiceBindingDestinationOptions} into a {@link HttpDestination}. Internally, this is done by leveraging the
 * {@link ServiceBindingDestinationLoader} API.
 * <p>
 * <u>Usage Example:</u>
 *
 * <pre>
 * private Try<HttpDestination> tryGetDestinationServiceBinding( ServiceBinding serviceBinding )
 * {
 *     ServiceBindingDestinationOptions options =
 *         ServiceBindingDestinationOptions.builder(BindableService.DESTINATION, serviceBinding).build();
 *     return new DelegatingServiceBindingDestinationLoader().tryGetDestination(options);
 * }
 * </pre>
 * </p>
 *
 * @since 4.16.0
 */
@Slf4j
class DefaultServiceBindingDestinationLoaderChain implements ServiceBindingDestinationLoader
{
    @Nonnull
    private static final List<ServiceBindingDestinationLoader> DEFAULT_DELEGATE_LOADERS;
    @Nonnull
    static final DefaultServiceBindingDestinationLoaderChain DEFAULT_INSTANCE;

    static {
        DEFAULT_DELEGATE_LOADERS = new ArrayList<>(FacadeLocator.getFacades(ServiceBindingDestinationLoader.class));
        DEFAULT_INSTANCE = new DefaultServiceBindingDestinationLoaderChain(DEFAULT_DELEGATE_LOADERS);
    }

    @Getter( AccessLevel.PACKAGE )
    @Nonnull
    private final List<ServiceBindingDestinationLoader> delegateLoaders;

    /**
     * Creates a new {@link DefaultServiceBindingDestinationLoaderChain} instance.
     *
     * @param delegateLoaders
     *            The {@link ServiceBindingDestinationLoader} instances to delegate the transformation logic to. The
     *            first instance that returns a {@link Try#success(Object)} will determine the output of
     *            {@link #tryGetDestination(ServiceBindingDestinationOptions)}.
     */
    public DefaultServiceBindingDestinationLoaderChain(
        @Nonnull final List<ServiceBindingDestinationLoader> delegateLoaders )
    {
        this.delegateLoaders = delegateLoaders;
    }

    @Override
    @Nonnull
    public Try<HttpDestination> tryGetDestination( @Nonnull final ServiceBindingDestinationOptions options )
    {
        if( delegateLoaders.isEmpty() ) {
            log.warn("No delegate loaders. As a consequence, the transformation will not be attempted.");
            return NoDelegateLoadersExceptionHolder.NO_DELEGATE_LOADERS;
        }

        final ServiceBinding serviceBinding = options.getServiceBinding();
        final List<Throwable> suppressedExceptions = new ArrayList<>();
        for( final ServiceBindingDestinationLoader loader : delegateLoaders ) {
            final Try<HttpDestination> result = loader.tryGetDestination(options);
            if( log.isDebugEnabled() ) {
                final String msg = "Transformation of service binding {} to a Destination using an instance of {} {}.";
                final String state = result.isSuccess() ? "succeeded" : "failed";
                log.debug(msg, serviceBindingToString(serviceBinding), loader.getClass().getName(), state);
            }

            if( result.isSuccess() ) {
                return result;
            }

            final Throwable cause = result.getCause();

            if( !hasCauseAssignableFrom(cause, DestinationAccessException.class)
                && hasCauseAssignableFrom(cause, DestinationNotFoundException.class) ) {
                suppressedExceptions.add(cause);
            } else {
                final String msg =
                    "Service Binding Destination loader %s returned an exception when loading destination for service '%s' using service binding %s.";
                final String formattedMsg =
                    String
                        .format(
                            msg,
                            loader.getClass().getName(),
                            options.getServiceBinding().getServiceIdentifier().orElse(null),
                            serviceBindingToString(serviceBinding));
                return Try.failure(new DestinationAccessException(formattedMsg, cause));
            }
        }
        final String msg =
            "None of the %s loaders could transform the service binding %s into a destination. "
                + "Check the suppressed exceptions and logs for further details.";
        final DestinationNotFoundException destinationNotFoundException =
            new DestinationNotFoundException(
                null,
                msg.formatted(delegateLoaders.size(), serviceBindingToString(serviceBinding)));
        suppressedExceptions.forEach(destinationNotFoundException::addSuppressed);

        return Try.failure(destinationNotFoundException);
    }

    private static boolean hasCauseAssignableFrom( @Nonnull final Throwable t, @Nonnull final Class<?> cls )
    {
        return ExceptionUtils.getThrowableList(t).stream().map(Throwable::getClass).anyMatch(cls::isAssignableFrom);
    }

    @Nonnull
    private static String serviceBindingToString( @Nonnull final ServiceBinding serviceBinding )
    {
        return new StringBuilder()
            .append("{ name: '")
            .append(serviceBinding.getName().orElse("<NULL>"))
            .append("', serviceName: '")
            .append(serviceBinding.getServiceName().orElse("<NULL>"))
            .append("', servicePlan: '")
            .append(serviceBinding.getServicePlan().orElse("<NULL>"))
            .append("', tags: [")
            .append(String.join(", ", serviceBinding.getTags()))
            .append("] }")
            .toString();
    }

    static final class NoDelegateLoadersExceptionHolder
    {
        @Nonnull
        static final Try<HttpDestination> NO_DELEGATE_LOADERS =
            Try
                .failure(
                    new DestinationAccessException(
                        String
                            .format(
                                "There are no delegate loaders that could perform the %s to %s transformation. Make sure at least one loader is available on the classpath by, for example, having the 'connectivity-oauth' dependency in your project.",
                                ServiceBinding.class.getSimpleName(),
                                HttpDestination.class.getSimpleName())));
    }
}
