/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Represents a (remote) service that is bound to Megaclite. Therefore, the application may reach the bound service by
 * using Megaclite as an egress proxy.
 * <p>
 * Instances of this class require manual (i.e. programmer) input because the configuration cannot be automatically
 * derived. Instead, the configuration is part of the Deploy With Confidence product configuration.
 * </p>
 * <p>
 * <u>Usage Example:</u>
 * </p>
 * <p>
 *
 * <pre>
 * MegacliteServiceBinding megacliteServiceBinding =
 *     MegacliteServiceBinding
 *         .forService(BindableService.DESTINATION)
 *         .providerConfiguration()
 *         .name("destination")
 *         .version("v1")
 *         .build();
 *
 * MegacliteServiceBindingAccessor.registerServiceBinding(megacliteServiceBinding);
 * </pre>
 * </p>
 *
 * @see MegacliteServiceBindingAccessor#registerServiceBinding(MegacliteServiceBinding)
 * @since 4.17.0
 */
@Beta
@Getter( AccessLevel.PACKAGE )
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
@EqualsAndHashCode
@ToString
public final class MegacliteServiceBinding implements ServiceBinding
{
    @Nonnull
    private final ServiceIdentifier service;

    @Nullable
    private final MandateConfiguration providerConfiguration;
    @Nullable
    private final MandateConfiguration subscriberConfiguration;

    @Nonnull
    @Override
    public Set<String> getKeys()
    {
        return Collections.emptySet();
    }

    @Override
    public boolean containsKey( @Nonnull final String key )
    {
        return false;
    }

    @Nonnull
    @Override
    public Optional<Object> get( @Nonnull final String key )
    {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<String> getName()
    {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<String> getServiceName()
    {
        return Optional.of(service.toString());
    }

    @Nonnull
    @Override
    public Optional<String> getServicePlan()
    {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public List<String> getTags()
    {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public Map<String, Object> getCredentials()
    {
        return Collections.emptyMap();
    }

    /**
     * Creates a new {@link Builder1} instance that can be used to configure access to the provided {@code service} that
     * is bound to Megaclite.
     * <p>
     * <i>Usage Example:</i> Assume you have the following two configurations in your <i>Deploy with Confidence</i>
     * product configuration:
     *
     * <pre>{@code
     * # destination-paas.yaml
     * communication:
     *   cacheResponse: true
     *   type: reuse
     *   typeSpecificProperties:
     *     authorizeOnBehalf: paas
     *     grantType: client_credentials
     *     serviceUrlKey: uri
     *     vcapServiceName: destination
     * headers:
     *   forwardDwcHeaders: false
     * name: destination-paas
     * version: paas-v1
     *
     * ---
     *
     * # destination-saas.yaml
     * communication:
     *   cacheResponse: true
     *   type: reuse
     *   typeSpecificProperties:
     *     authorizeOnBehalf: saas
     *     grantType: client_credentials
     *     serviceUrlKey: uri
     *     vcapServiceName: destination
     * headers:
     *   forwardDwcHeaders: false
     * name: destination-saas
     * version: saas-v1}</pre>
     * <p>
     * Then you could use the builder as follows:
     *
     * <pre>{@code
     * MegacliteServiceBinding destinationServiceBinding =
     *     MegacliteServiceBinding
     *         .forService(ServiceIdentifier.DESTINATION)
     *         .providerConfiguration()
     *         .name("destination-paas")
     *         .version("paas-v1")
     *         .and()
     *         .subscriberConfiguration()
     *         .name("destination-saas")
     *         .version("saas-v1")
     *         .build();
     * MegacliteServiceBindingAccessor.registerServiceBinding(destinationServiceBinding);
     * }</pre>
     * </p>
     *
     * @param service
     *            The {@link ServiceIdentifier service} that the configuration is about.
     * @return A new {@link Builder1} instance that can be used to construct new {@link MegacliteServiceBinding}
     *         instances.
     */
    @Nonnull
    public static Builder1 forService( @Nonnull final ServiceIdentifier service )
    {
        return new Builder(service);
    }

    /**
     * First builder step to construct new {@link MegacliteServiceBinding} instances.
     */
    public interface Builder1
    {
        /**
         * Initializes the configuration for the provider account (PaaS).
         *
         * @return A {@link Builder2} instance.
         */
        @Nonnull
        Builder2 providerConfiguration();

        /**
         * Initializes the configuration for the subscriber account (SaaS).
         *
         * @return A {@link Builder2} instance.
         */
        @Nonnull
        Builder2 subscriberConfiguration();
    }

    /**
     * Second builder step to construct new {@link MegacliteServiceBinding} instances.
     */
    public interface Builder2
    {
        /**
         * Configures the name of the outbound service, as specified in the {@code name} property of the <i>Deploy with
         * Confidence</i> outbound service specification.
         *
         * @param name
         *            The name of the outbound service.
         * @return A {@link Builder3} instance.
         */
        @Nonnull
        Builder3 name( @Nonnull final String name );
    }

    /**
     * Third builder step to construct new {@link MegacliteServiceBinding} instances.
     */
    public interface Builder3
    {
        /**
         * Configures the version of the outbound service, as specified in the {@code version} property of the <i>Deploy
         * with Confidence</i> outbound service specification.
         *
         * @param version
         *            The version of the outbound service,
         * @return A {@link Builder4} instance.
         */
        @Nonnull
        Builder4 version( @Nonnull final String version );
    }

    /**
     * Fourth builder step to construct new {@link MegacliteServiceBinding} instances.
     */
    public interface Builder4
    {
        /**
         * Configures the Megaclite version, that should be used to reach the outbound service.
         * <p>
         * If not specified explicitly, the default value of {@code "v1"} will be used.
         * </p>
         *
         * @param megacliteVersion
         *            The Megaclite version.
         * @return This {@link Builder4} instance.
         */
        @Nonnull
        Builder4 megacliteVersion( @Nonnull final String megacliteVersion );

        /**
         * Initializes a new builder step for configuring access for another account (either provider or subscriber).
         *
         * @return A {@link Builder1} instance.
         */
        @Nonnull
        Builder1 and();

        /**
         * Creates a new {@link MegacliteServiceBinding} instance based on the provided configuration.
         * <p>
         * <b>Hint:</b> To use the returned {@link MegacliteServiceBinding} productively, don't forget to register it
         * using the {@link MegacliteServiceBindingAccessor#registerServiceBinding(MegacliteServiceBinding)} method.
         * </p>
         *
         * @return A new {@link MegacliteServiceBinding} instance.
         */
        @Nonnull
        MegacliteServiceBinding build();
    }

    @RequiredArgsConstructor
    private static final class Builder implements Builder1, Builder2, Builder3, Builder4
    {
        @Nonnull
        private final ServiceIdentifier service;
        @Nullable
        private MandateConfiguration providerConfiguration;
        @Nullable
        private MandateConfiguration subscriberConfiguration;
        @Nullable
        private MandateConfiguration.MandateConfigurationBuilder currentConfigurationBuilder;

        @Nonnull
        @Override
        public Builder2 providerConfiguration()
        {
            if( providerConfiguration != null ) {
                throw new IllegalArgumentException(
                    "The access on behalf of provider account has already been configured.");
            }

            currentConfigurationBuilder = MandateConfiguration.builder().isProviderConfiguration(true);
            return this;
        }

        @Nonnull
        @Override
        public Builder2 subscriberConfiguration()
        {
            if( subscriberConfiguration != null ) {
                throw new IllegalArgumentException(
                    "The access on behalf of subscriber account has already been configured.");
            }

            currentConfigurationBuilder = MandateConfiguration.builder().isProviderConfiguration(false);
            return this;
        }

        @Nonnull
        @Override
        public Builder3 name( @Nonnull final String name )
        {
            getCurrentConfigurationBuilder().name(name);
            return this;
        }

        @Nonnull
        @Override
        public Builder4 version( @Nonnull final String version )
        {
            getCurrentConfigurationBuilder().version(version);
            return this;
        }

        @Nonnull
        @Override
        public Builder4 megacliteVersion( @Nonnull final String megacliteVersion )
        {
            getCurrentConfigurationBuilder().megacliteVersion(megacliteVersion);
            return this;
        }

        @Nonnull
        @Override
        public Builder1 and()
        {
            final MandateConfiguration currentConfiguration = getCurrentConfigurationBuilder().build();
            if( currentConfiguration.isProviderConfiguration() ) {
                providerConfiguration = currentConfiguration;
            } else {
                subscriberConfiguration = currentConfiguration;
            }
            return this;
        }

        @Nonnull
        @Override
        public MegacliteServiceBinding build()
        {
            final MandateConfiguration currentConfiguration = getCurrentConfigurationBuilder().build();
            if( currentConfiguration.isProviderConfiguration() ) {
                providerConfiguration = currentConfiguration;
            } else {
                subscriberConfiguration = currentConfiguration;
            }
            return new MegacliteServiceBinding(service, providerConfiguration, subscriberConfiguration);
        }

        @Nonnull
        private MandateConfiguration.MandateConfigurationBuilder getCurrentConfigurationBuilder()
        {
            if( currentConfigurationBuilder == null ) {
                throw new IllegalStateException("The current configuration builder is null. This should never happen!");
            }

            return currentConfigurationBuilder;
        }
    }

    @Data
    @RequiredArgsConstructor( access = AccessLevel.PRIVATE )
    @lombok.Builder
    static final class MandateConfiguration
    {
        @Nonnull
        private static final String DEFAULT_MEGACLITE_VERSION = "v1";

        private final boolean isProviderConfiguration;
        @Nonnull
        private final String name;
        @Nonnull
        private final String version;
        @Nonnull
        @lombok.Builder.Default
        private final String megacliteVersion = DEFAULT_MEGACLITE_VERSION;
    }
}
