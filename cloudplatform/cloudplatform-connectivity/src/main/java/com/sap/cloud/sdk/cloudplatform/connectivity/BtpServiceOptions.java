/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.sap.cloud.sdk.cloudplatform.connectivity.ServiceBindingDestinationOptions.OptionsEnhancer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * Options that can be used in a {@link ServiceBindingDestinationOptions} to configure the destinations for specific
 * services.
 *
 * @since 4.20.0
 */
@Beta
public final class BtpServiceOptions
{

    private static final Map<String, Function<Object[], OptionsEnhancer<?>>> GENERIC_ENHANCER_BUILDERS;

    static {
        final Map<String, Function<Object[], OptionsEnhancer<?>>> map = new HashMap<>();

        addGenericEnumEnhancerBuilder(map, BusinessRulesOptions.class);
        addGenericEnumEnhancerBuilder(map, WorkflowOptions.class);
        addGenericEnumEnhancerBuilder(map, BusinessLoggingOptions.class);
        IasOptions.addGenericEnhancerBuilder(map);

        GENERIC_ENHANCER_BUILDERS = Collections.unmodifiableMap(map);
    }

    /**
     * Generically creates a new instance of {@link OptionsEnhancer} for the given enhancer name and parameters.
     * <p>
     * <b>Note:</b> This API is meant for expert users that want to use the SDK in a more dynamic way. For the vast
     * majority of consumers, we strongly recommend using the type-safe methods defined in this class.
     *
     * @param enhancerName
     *            The name of the enhancer to be created. This should be the simple class name of the enhancer.
     * @param parameters
     *            The parameters to pass to the chosen enhancer. These should be the same (including their order) as the
     *            parameters of the type-safe methods defined in this class.
     * @return An instance of {@link OptionsEnhancer} that can be used to configure a destination.
     * @since 5.9.0
     */
    @Beta
    public static
        OptionsEnhancer<?>
        withGenericOption( @Nonnull final String enhancerName, @Nonnull final Object... parameters )
    {
        final Function<Object[], OptionsEnhancer<?>> enhancerBuilder = GENERIC_ENHANCER_BUILDERS.get(enhancerName);
        if( enhancerBuilder == null ) {
            throw new IllegalArgumentException("Unknown enhancer name: " + enhancerName);
        }

        return enhancerBuilder.apply(parameters);
    }

    /**
     * Enhancer that allows to include configuration specific to the
     * <a href="https://api.sap.com/package/SAPCPBusinessRulesAPIs/all">SAP Business Rules Service for Cloud Foundry</a>
     */
    public enum BusinessRulesOptions implements OptionsEnhancer<BusinessRulesOptions>
    {
        /**
         * Use the authoring API of the SAP Business Rules Service.
         */
        AUTHORING_API,
        /**
         * Use the execution API of the SAP Business Rules Service.
         */
        EXECUTION_API;

        @Nonnull
        @Override
        public BusinessRulesOptions getValue()
        {
            return this;
        }
    }

    /**
     * Enhancer that allows to include configuration specific to the
     * <a href="https://api.sap.com/package/SAPCPWorkflowAPIs/all">SAP Workflow Service for Cloud Foundry</a>.
     */
    public enum WorkflowOptions implements OptionsEnhancer<WorkflowOptions>
    {
        /**
         * Use the REST API of the SAP Workflow Service.
         */
        REST_API,
        /**
         * Use the ODATA API (Inbox API) of the SAP Workflow Service.
         */
        ODATA_API;

        @Nonnull
        @Override
        public WorkflowOptions getValue()
        {
            return this;
        }
    }

    /**
     * Enhancer that allows to include configuration specific to the SAP Business Logging Service.
     */
    public enum BusinessLoggingOptions implements OptionsEnhancer<BusinessLoggingOptions>
    {
        /**
         * Use the config API of the SAP Business Logging Service.
         */
        CONFIG_API,
        /**
         * Use the text API of the SAP Business Logging Service.
         */
        TEXT_API,
        /**
         * Use the read API of the SAP Business Logging Service.
         */
        READ_API,
        /**
         * Use the write API of the SAP Business Logging Service.
         */
        WRITE_API;

        @Nonnull
        @Override
        public BusinessLoggingOptions getValue()
        {
            return this;
        }
    }

    /**
     * Factory class for Identity Authentication Service
     * ({@link com.sap.cloud.environment.servicebinding.api.ServiceIdentifier#IDENTITY_AUTHENTICATION}) options.
     *
     * @since 5.5.0
     */
    public static final class IasOptions
    {
        private IasOptions()
        {
            throw new IllegalStateException("This class should not be instantiated.");
        }

        /**
         * Overwrites the target URI that is extracted from the IAS service binding.
         *
         * @param targetUri
         *            The target URI to be used.
         * @return An instance of {@link OptionsEnhancer} that is used when creating a destination from an IAS service
         *         binding and that contains the target URI.
         */
        @Nonnull
        public static OptionsEnhancer<?> withTargetUri( @Nonnull final String targetUri )
        {
            return withTargetUri(URI.create(targetUri));
        }

        /**
         * Overwrites the target URI that is extracted from the IAS service binding.
         *
         * @param targetUri
         *            The target URI to be used.
         * @return An instance of {@link OptionsEnhancer} that is used when creating a destination from an IAS service
         */
        @Nonnull
        public static OptionsEnhancer<?> withTargetUri( @Nonnull final URI targetUri )
        {
            return new IasTargetUri(targetUri);
        }

        /**
         * Creates an instance of {@link NoTokenForTechnicalProviderUser}.
         *
         * @return A new {@link NoTokenForTechnicalProviderUser} instance.
         */
        @Nonnull
        public static OptionsEnhancer<?> withoutTokenForTechnicalProviderUser()
        {
            return new NoTokenForTechnicalProviderUser(true);
        }

        /**
         * Creates an {@link OptionsEnhancer} that instructs an IAS-based destination to use the given application
         * provider name when performing token retrievals. This is needed in <b>App-To-App</b> communication scenarios.
         * <p>
         * <b>Hint:</b> This option is <b>mutually exclusive</b> with {@link #withConsumerClient(String, String)}.
         *
         * @param applicationName
         *            The name of the application provider to be used. This is the name that was used to register the
         *            to-be-called application within the IAS tenant.
         * @return An instance of {@link OptionsEnhancer} that will lead to the given application provider being used
         *         when retrieving an authentication token from the IAS service.
         */
        @Nonnull
        public static OptionsEnhancer<?> withApplicationName( @Nonnull final String applicationName )
        {
            return new IasCommunicationOptions(applicationName, null, null);
        }

        /**
         * Creates an {@link OptionsEnhancer} that instructs an IAS-based destination to use the given consumer client
         * ID when performing token retrievals. This is needed in <i>Service-To-App</i> communication scenarios.
         * <p>
         * <b>Hint:</b> This option is <b>mutually exclusive</b> with {@link #withApplicationName(String)}.
         *
         * @param consumerClientId
         *            The client ID of the consumer application. This client ID is usually extracted from an incoming
         *            IAS authentication token sent by the consumer application upon calling this application.
         * @return An instance of {@link OptionsEnhancer} that will lead to the given consumer client ID being used when
         *         retrieving an authentication token from the IAS service.
         */
        @Nonnull
        public static OptionsEnhancer<?> withConsumerClient( @Nonnull final String consumerClientId )
        {
            return new IasCommunicationOptions(null, consumerClientId, null);
        }

        /**
         * Creates an {@link OptionsEnhancer} that instructs an IAS-based destination to use the given consumer client
         * and tenant ID when performing token retrievals. This is needed in <i>Service-To-App</i> communication
         * scenarios.
         * <p>
         * <b>Hint:</b> This option is <b>mutually exclusive</b> with {@link #withApplicationName(String)}.
         *
         * @param consumerClientId
         *            The client ID of the consumer application. This client ID is usually extracted from an incoming
         *            IAS authentication token sent by the consumer application upon calling this application.
         * @param consumerTenantId
         *            The tenant ID of the consumer application. This tenant ID is usually extracted from an incoming
         *            IAS authentication token sent by the consumer application upon calling this application.
         * @return An instance of {@link OptionsEnhancer} that will lead to the given consumer client ID and tenant ID
         *         being used when retrieving an authentication token from the IAS service.
         */
        @Nonnull
        public static
            OptionsEnhancer<?>
            withConsumerClient( @Nonnull final String consumerClientId, @Nonnull final String consumerTenantId )
        {
            return new IasCommunicationOptions(null, consumerClientId, consumerTenantId);
        }

        /**
         * An {@link OptionsEnhancer} that contains the target URI for an IAS-based destination. Also refer to
         * {@link #withTargetUri(String)}.
         */
        @Value
        @AllArgsConstructor( access = AccessLevel.PRIVATE )
        public static class IasTargetUri implements OptionsEnhancer<URI>
        {
            URI value;
        }

        /**
         * An {@link OptionsEnhancer} that indicates whether <b>no</b> token is required for authenticating at the
         * target system <b>iff</b> the authentication happens on behalf of a <b>technical provider user</b>. In this
         * case, retrieving a token from the IAS service is optional and, therefore, can be skipped. Instead,
         * authentication is done via mTLS only. In every other case (i.e. <i>named user</i> for either the provider or
         * a subscriber, or <i>technical user</i> on behalf of a subscriber), a token is always required to not lose any
         * tenancy information at the target system.
         *
         * @since 5.6.0
         */
        @Value
        @AllArgsConstructor( access = AccessLevel.PRIVATE )
        public static class NoTokenForTechnicalProviderUser implements OptionsEnhancer<Boolean>
        {
            Boolean value;
        }

        /**
         * An {@link OptionsEnhancer} that contains the communication options for an IAS-based destination. Also refer
         * to {@link #withoutTokenForTechnicalProviderUser()}, {@link #withApplicationName(String)}, and
         * {@link #withConsumerClient(String, String)}.
         */
        @Value
        @AllArgsConstructor( access = AccessLevel.PRIVATE )
        public static class IasCommunicationOptions implements OptionsEnhancer<IasCommunicationOptions>
        {
            @Nullable
            String applicationName;
            @Nullable
            String consumerClientId;
            @Nullable
            String consumerTenantId;

            @Nonnull
            @Override
            public IasCommunicationOptions getValue()
            {
                return this;
            }
        }

        // package-private for testing
        static void addGenericEnhancerBuilder( @Nonnull final Map<String, Function<Object[], OptionsEnhancer<?>>> map )
        {
            final String key = IasOptions.class.getSimpleName();
            if( map.containsKey(key) ) {
                throw new IllegalStateException("Generic enhancer builder for " + key + " already registered.");
            }

            map.put(key, args -> castParameters(args, 0, String.class, method -> {
                if( "withTargetUri".equals(method) ) {
                    try {
                        return castParameters(args, 1, String.class, IasOptions::withTargetUri);
                    }
                    catch( final IllegalArgumentException e ) {
                        // ignored - try to apply overload
                    }

                    try {
                        return castParameters(args, 1, URI.class, IasOptions::withTargetUri);
                    }
                    catch( final IllegalArgumentException e ) {
                        throw new IllegalArgumentException(
                            "Expected parameter 1 to be of type String or URI, but got %s instead."
                                .formatted(
                                    args.length > 1 && args[1] != null ? args[1].getClass().getName() : "<null>"));
                    }
                }

                if( "withoutTokenForTechnicalProviderUser".equals(method) ) {
                    return withoutTokenForTechnicalProviderUser();
                }

                if( "withApplicationName".equals(method) ) {
                    return castParameters(args, 1, String.class, IasOptions::withApplicationName);
                }

                if( "withConsumerClient".equals(method) ) {
                    if( args.length == 2 ) {
                        return castParameters(args, 1, String.class, IasOptions::withConsumerClient);
                    }

                    return castParameters(args, 1, String.class, String.class, IasOptions::withConsumerClient);
                }

                throw new IllegalArgumentException("Unknown Ias Options method: " + method);
            }));
        }
    }

    // package-private for testing
    static <T extends Enum<T> & OptionsEnhancer<T>> void addGenericEnumEnhancerBuilder(
        @Nonnull final Map<String, Function<Object[], OptionsEnhancer<?>>> map,
        @Nonnull final Class<T> enumType )
    {
        final String key = enumType.getSimpleName();
        if( map.containsKey(key) ) {
            throw new IllegalStateException("Generic enhancer builder for " + key + " already registered.");
        }

        map.put(key, args -> castParameters(args, 0, String.class, v -> Enum.valueOf(enumType, v)));
    }

    @Nonnull
    private static <T1> OptionsEnhancer<?> castParameters(
        @Nonnull final Object[] parameters,
        final int parametersOffset,
        @Nonnull final Class<T1> type1,
        @Nonnull final Function<T1, OptionsEnhancer<?>> enhancer )
    {
        throwIfParametersCannotBeCast(parameters, parametersOffset, type1);
        return enhancer.apply(type1.cast(parameters[parametersOffset]));
    }

    @Nonnull
    private static <T1, T2> OptionsEnhancer<?> castParameters(
        @Nonnull final Object[] parameters,
        final int parametersOffset,
        @Nonnull final Class<T1> type1,
        @Nonnull final Class<T2> type2,
        @Nonnull final BiFunction<T1, T2, OptionsEnhancer<?>> enhancer )
    {
        throwIfParametersCannotBeCast(parameters, parametersOffset, type1, type2);
        return enhancer.apply(type1.cast(parameters[parametersOffset]), type2.cast(parameters[parametersOffset + 1]));
    }

    private static void throwIfParametersCannotBeCast(
        @Nonnull final Object[] parameters,
        final int parametersOffset,
        @Nonnull final Class<?>... types )
    {
        if( parameters.length - parametersOffset < types.length ) {
            throw new IllegalArgumentException(
                "Expected %d parameter(s), but got %d parameter(s) instead."
                    .formatted(types.length, parameters.length));
        }

        for( int i = 0; i < types.length; i++ ) {
            if( !types[i].isInstance(parameters[i + parametersOffset]) ) {
                final String actualType =
                    parameters[i + parametersOffset] == null
                        ? "<null>"
                        : parameters[i + parametersOffset].getClass().getName();
                throw new IllegalArgumentException(
                    "Expected parameter %d to be of type %s, but got %s instead."
                        .formatted(i + 1, types[i].getName(), actualType));
            }
        }
    }
}
