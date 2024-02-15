/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;

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
     * @since 5.4.1
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
         * Creates an {@link OptionsEnhancer} that instructs the destination to use mTLS authentication only.
         * <p>
         * In the regular OAuth2 case, setting this option will skip the IAS token flow.
         * <p>
         * <b>Hint:</b> This option is <b>mutually exclusive</b> with {@link #withApplicationName(String)} and
         * {@link #withConsumerClient(String, String)}.
         * <p>
         * <b>Caution:</b> This option cannot be combined with {@link OnBehalfOf#NAMED_USER_CURRENT_TENANT}.
         *
         * @return An instance of {@link OptionsEnhancer} that will lead to mTLS authentication only.
         */
        @Nonnull
        public static OptionsEnhancer<?> withMTLSAuthenticationOnly()
        {
            return new IasCommunicationOptions(null, null, null, true);
        }

        /**
         * Creates an {@link OptionsEnhancer} that instructs an IAS-based destination to use the given application
         * provider name when performing token retrievals. This is needed in <b>App-To-App</b> communication scenarios.
         * <p>
         * <b>Hint:</b> This option is <b>mutually exclusive</b> with {@link #withMTLSAuthenticationOnly()} and
         * {@link #withConsumerClient(String, String)}.
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
            return new IasCommunicationOptions(applicationName, null, null, false);
        }

        /**
         * Creates an {@link OptionsEnhancer} that instructs an IAS-based destination to use the given consumer client
         * ID when performing token retrievals. This is needed in <i>Service-To-App</i> communication scenarios.
         * <p>
         * <b>Hint:</b> This option is <b>mutually exclusive</b> with {@link #withMTLSAuthenticationOnly()} and
         * {@link #withApplicationName(String)}.
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
            return new IasCommunicationOptions(null, consumerClientId, null, false);
        }

        /**
         * Creates an {@link OptionsEnhancer} that instructs an IAS-based destination to use the given consumer client
         * and tenant ID when performing token retrievals. This is needed in <i>Service-To-App</i> communication
         * scenarios.
         * <p>
         * <b>Hint:</b> This option is <b>mutually exclusive</b> with {@link #withMTLSAuthenticationOnly()} and
         * {@link #withApplicationName(String)}.
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
            return new IasCommunicationOptions(null, consumerClientId, consumerTenantId, false);
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
         * An {@link OptionsEnhancer} that contains the communication options for an IAS-based destination. Also refer
         * to {@link #withMTLSAuthenticationOnly()}, {@link #withApplicationName(String)}, and
         * {@link #withConsumerClient(String, String)}.
         */
        @Value
        @AllArgsConstructor( access = AccessLevel.PRIVATE )
        public static class IasCommunicationOptions implements OptionsEnhancer<IasCommunicationOptions>
        {
            @Nullable
            String applicationProviderName;
            @Nullable
            String consumerClientId;
            @Nullable
            String consumerTenantId;
            boolean mTLSAuthenticationOnly;

            @Nonnull
            @Override
            public IasCommunicationOptions getValue()
            {
                return this;
            }
        }
    }
}
