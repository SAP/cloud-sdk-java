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

    public static class IasOptions
    {
        @Nonnull
        public static OptionsEnhancer<?> withTargetUri( @Nonnull final String targetUri )
        {
            return withTargetUri(URI.create(targetUri));
        }

        @Nonnull
        public static OptionsEnhancer<?> withTargetUri( @Nonnull final URI targetUri )
        {
            return new IasTargetUrl(targetUri);
        }

        @Nonnull
        public static OptionsEnhancer<?> withMTLSAuthenticationOnly()
        {
            return new IasCommunicationOptions(null, null, null, true);
        }

        @Nonnull
        public static OptionsEnhancer<?> withApplicationProvider( @Nonnull final String applicationProviderName )
        {
            return new IasCommunicationOptions(applicationProviderName, null, null, false);
        }

        @Nonnull
        public static OptionsEnhancer<?> withConsumerClient( @Nonnull final String consumerClientId )
        {
            return new IasCommunicationOptions(null, consumerClientId, null, false);
        }

        @Nonnull
        public static
            OptionsEnhancer<?>
            withConsumerClient( @Nonnull final String consumerClientId, @Nonnull final String consumerTenantId )
        {
            return new IasCommunicationOptions(null, consumerClientId, consumerTenantId, false);
        }

        @Value
        @AllArgsConstructor( access = AccessLevel.PRIVATE )
        public static class IasTargetUrl implements OptionsEnhancer<URI>
        {
            URI value;
        }

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
