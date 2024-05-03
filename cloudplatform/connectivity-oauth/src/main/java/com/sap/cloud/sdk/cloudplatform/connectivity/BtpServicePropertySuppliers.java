/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.BtpServiceOptions.AuthenticationServiceOptions.TargetUri;
import static com.sap.cloud.sdk.cloudplatform.connectivity.BtpServiceOptions.IasOptions.IasCommunicationOptions;
import static com.sap.cloud.sdk.cloudplatform.connectivity.MultiUrlPropertySupplier.REMOVE_PATH;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.connectivity.BtpServiceOptions.BusinessLoggingOptions;
import com.sap.cloud.sdk.cloudplatform.connectivity.BtpServiceOptions.BusinessRulesOptions;
import com.sap.cloud.sdk.cloudplatform.connectivity.BtpServiceOptions.WorkflowOptions;
import com.sap.cloud.sdk.cloudplatform.connectivity.SecurityLibWorkarounds.ZtisClientIdentity;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.security.config.ClientCertificate;
import com.sap.cloud.security.config.ClientIdentity;
import com.sap.cloud.security.mtls.SSLContextFactory;

import lombok.extern.slf4j.Slf4j;

class BtpServicePropertySuppliers
{
    static final OAuth2PropertySupplierResolver XSUAA =
        OAuth2PropertySupplierResolver.forServiceIdentifier(ServiceIdentifier.of("xsuaa"), Xsuaa::new);

    static final OAuth2PropertySupplierResolver DESTINATION =
        OAuth2PropertySupplierResolver.forServiceIdentifier(ServiceIdentifier.DESTINATION, Destination::new);

    static final OAuth2PropertySupplierResolver CONNECTIVITY =
        OAuth2PropertySupplierResolver.forServiceIdentifier(ServiceIdentifier.CONNECTIVITY, ConnectivityProxy::new);

    /**
     * {@link ServiceIdentifier#IDENTITY_AUTHENTICATION} referenced indirectly for backwards compatibility.
     */
    static final OAuth2PropertySupplierResolver IDENTITY_AUTHENTICATION =
        OAuth2PropertySupplierResolver
            .forServiceIdentifier(ServiceIdentifier.of("identity"), IdentityAuthentication::new);

    static final OAuth2PropertySupplierResolver WORKFLOW =
        OAuth2PropertySupplierResolver
            .forServiceIdentifier(
                ServiceIdentifier.WORKFLOW,
                MultiUrlPropertySupplier
                    .of(WorkflowOptions.class)
                    .withUrlKey(WorkflowOptions.REST_API, "workflow_rest_url")
                    .withUrlKey(WorkflowOptions.ODATA_API, "workflow_odata_url")
                    .factory());

    static final OAuth2PropertySupplierResolver BUSINESS_RULES =
        OAuth2PropertySupplierResolver
            .forServiceIdentifier(
                ServiceIdentifier.BUSINESS_RULES,
                MultiUrlPropertySupplier
                    .of(BusinessRulesOptions.class)
                    .withUrlKey(BusinessRulesOptions.AUTHORING_API, "rule_repository_url")
                    .withUrlKey(BusinessRulesOptions.EXECUTION_API, "rule_runtime_url")
                    .factory());

    static final OAuth2PropertySupplierResolver BUSINESS_LOGGING =
        OAuth2PropertySupplierResolver
            .forServiceIdentifier(
                ServiceIdentifier.of("business-logging"),
                MultiUrlPropertySupplier
                    .of(BusinessLoggingOptions.class)
                    .withUrlKey(BusinessLoggingOptions.CONFIG_API, "configservice", REMOVE_PATH)
                    .withUrlKey(BusinessLoggingOptions.TEXT_API, "textresourceservice", REMOVE_PATH)
                    .withUrlKey(BusinessLoggingOptions.READ_API, "readservice", REMOVE_PATH)
                    .withUrlKey(BusinessLoggingOptions.WRITE_API, "writeservice", REMOVE_PATH)
                    .factory());
    static final OAuth2PropertySupplierResolver AI_CORE =
        OAuth2PropertySupplierResolver.forServiceIdentifier(ServiceIdentifier.of("aicore"), AiCore::new);

    private static final List<OAuth2PropertySupplierResolver> DEFAULT_SERVICE_RESOLVERS = new ArrayList<>();

    static {
        DEFAULT_SERVICE_RESOLVERS.add(XSUAA);
        DEFAULT_SERVICE_RESOLVERS.add(DESTINATION);
        DEFAULT_SERVICE_RESOLVERS.add(CONNECTIVITY);
        DEFAULT_SERVICE_RESOLVERS.add(BUSINESS_RULES);
        DEFAULT_SERVICE_RESOLVERS.add(WORKFLOW);
        DEFAULT_SERVICE_RESOLVERS.add(BUSINESS_LOGGING);
        DEFAULT_SERVICE_RESOLVERS.add(IDENTITY_AUTHENTICATION);
        DEFAULT_SERVICE_RESOLVERS.add(AI_CORE);
    }

    static List<OAuth2PropertySupplierResolver> getDefaultServiceResolvers()
    {
        return new ArrayList<>(DEFAULT_SERVICE_RESOLVERS);
    }

    private static class Xsuaa extends DefaultOAuth2PropertySupplier
    {
        public Xsuaa( @Nonnull final ServiceBindingDestinationOptions options )
        {
            super(options, List.of());
        }

        @Nonnull
        @Override
        public URI getServiceUri()
        {
            return options.getOption(TargetUri.class).getOrElse(super::getServiceUri);
        }
    }

    private static class Destination extends DefaultOAuth2PropertySupplier
    {
        Destination( @Nonnull final ServiceBindingDestinationOptions options )
        {
            super(options, Collections.emptyList());
        }

        @Nonnull
        @Override
        public URI getServiceUri()
        {
            return getCredentialOrThrow(URI.class, "uri");
        }
    }

    private static class ConnectivityProxy extends DefaultOAuth2PropertySupplier
    {
        ConnectivityProxy( @Nonnull final ServiceBindingDestinationOptions options )
        {
            super(options, Collections.emptyList());
        }

        @Nonnull
        @Override
        public URI getServiceUri()
        {
            final String host = getCredentialOrThrow(String.class, "onpremise_proxy_host");
            final Integer port =
                getCredential(Integer.class, "onpremise_proxy_http_port")
                    .getOrElse(() -> getCredentialOrThrow(Integer.class, "onpremise_proxy_port"));
            try {
                return new URI("http", null, host, port, null, null, null);
            }
            catch( final URISyntaxException e ) {
                throw new DestinationAccessException("Failed to construct proxy URL", e);
            }
        }
    }

    @Slf4j
    private static class IdentityAuthentication extends DefaultOAuth2PropertySupplier
    {

        IdentityAuthentication( @Nonnull final ServiceBindingDestinationOptions options )
        {
            super(options, List.of());
        }

        @Nonnull
        @Override
        public URI getServiceUri()
        {
            return options.getOption(TargetUri.class).getOrElse(super::getServiceUri);
        }

        @Nonnull
        @Override
        public URI getTokenUri()
        {
            String providerUrl = getCredentialOrThrow(String.class, "url");
            if( providerUrl.endsWith("/") ) {
                providerUrl = providerUrl.substring(0, providerUrl.length() - 1);
            }

            return URI.create(providerUrl + "/oauth2/token");
        }

        @Nonnull
        @Override
        public OAuth2Options getOAuth2Options()
        {
            final OAuth2Options.Builder oAuth2OptionsBuilder = OAuth2Options.builder();

            if( skipTokenRetrieval() ) {
                oAuth2OptionsBuilder.withSkipTokenRetrieval(true);
            } else {
                attachIasCommunicationOptions(oAuth2OptionsBuilder);
                oAuth2OptionsBuilder
                    .withTokenRetrievalParameter("app_tid", getCredentialOrThrow(String.class, "app_tid"));
            }
            attachClientKeyStore(oAuth2OptionsBuilder);

            return oAuth2OptionsBuilder.build();
        }

        private void attachIasCommunicationOptions( @Nonnull final OAuth2Options.Builder optionsBuilder )
        {
            final IasCommunicationOptions o = options.getOption(IasCommunicationOptions.class).getOrNull();
            if( o == null ) {
                return;
            }

            if( o.getApplicationName() != null ) {
                optionsBuilder
                    .withTokenRetrievalParameter(
                        "resource",
                        "urn:sap:identity:application:provider:name:" + o.getApplicationName());
                return;
            }

            if( o.getConsumerClientId() != null ) {
                String value = "urn:sap:identity:consumer:clientid:" + o.getConsumerClientId();
                if( o.getConsumerTenantId() != null ) {
                    value += ":apptid:" + o.getConsumerTenantId();
                }

                optionsBuilder.withTokenRetrievalParameter("resource", value);
            }
        }

        private boolean skipTokenRetrieval()
        {
            final OnBehalfOf behalf = options.getOnBehalfOf();
            final Boolean noTokenRequired =
                options.getOption(BtpServiceOptions.IasOptions.NoTokenForTechnicalProviderUser.class).getOrElse(false);

            final boolean tokenIsAlwaysRequired = !noTokenRequired;
            if( tokenIsAlwaysRequired ) {
                return false;
            }

            return switch( behalf ) {
                case NAMED_USER_CURRENT_TENANT -> false;
                case TECHNICAL_USER_PROVIDER -> true;
                case TECHNICAL_USER_CURRENT_TENANT -> currentTenantIsProvider();
            };
        }

        private boolean currentTenantIsProvider()
        {
            final String maybeTenantId = TenantAccessor.tryGetCurrentTenant().map(Tenant::getTenantId).getOrNull();
            if( maybeTenantId == null ) {
                // there is no current tenant --> assume we are running in the provider context
                return true;
            }

            final String providerTenantId = getCredentialOrThrow(String.class, "app_tid");
            return maybeTenantId.equalsIgnoreCase(providerTenantId);
        }

        private void attachClientKeyStore( @Nonnull final OAuth2Options.Builder optionsBuilder )
        {
            final KeyStore maybeClientStore = getClientKeyStore();
            if( maybeClientStore != null ) {
                optionsBuilder.withClientKeyStore(maybeClientStore);
            }
        }

        @Nullable
        private KeyStore getClientKeyStore()
        {
            final ClientIdentity clientIdentity = getClientIdentity();
            if( clientIdentity instanceof ZtisClientIdentity ) {
                return ((ZtisClientIdentity) clientIdentity).getKeyStore();
            }
            if( !(clientIdentity instanceof ClientCertificate) ) {
                return null;
            }

            try {
                return SSLContextFactory.getInstance().createKeyStore(clientIdentity);
            }
            catch( final Exception e ) {
                throw new DestinationAccessException("Unable to extract client key store from IAS service binding.", e);
            }
        }
    }

    private static class AiCore extends DefaultOAuth2PropertySupplier
    {
        AiCore( @Nonnull final ServiceBindingDestinationOptions options )
        {
            super(options, Collections.emptyList());
        }

        @Nonnull
        @Override
        public URI getServiceUri()
        {
            return getCredentialOrThrow(URI.class, "serviceurls", "AI_API_URL");
        }
    }
}
