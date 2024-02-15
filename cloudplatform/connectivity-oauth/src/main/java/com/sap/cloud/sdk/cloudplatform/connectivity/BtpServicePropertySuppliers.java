/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.BtpServiceOptions.IasOptions.IasCommunicationOptions;
import static com.sap.cloud.sdk.cloudplatform.connectivity.BtpServiceOptions.IasOptions.IasTargetUrl;

import java.io.StringReader;
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
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.security.config.CredentialType;

class BtpServicePropertySuppliers
{
    static final OAuth2PropertySupplierResolver DESTINATION =
        OAuth2PropertySupplierResolver.forServiceIdentifier(ServiceIdentifier.DESTINATION, Destination::new);

    static final OAuth2PropertySupplierResolver CONNECTIVITY =
        OAuth2PropertySupplierResolver.forServiceIdentifier(ServiceIdentifier.CONNECTIVITY, ConnectivityProxy::new);

    static final OAuth2PropertySupplierResolver IDENTITY_AUTHENTICATION =
        OAuth2PropertySupplierResolver
            .forServiceIdentifier(ServiceBindingLibWorkarounds.IAS_IDENTIFIER, IdentityAuthentication::new);

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
                    .withUrlKey(BusinessLoggingOptions.CONFIG_API, "configservice")
                    .withUrlKey(BusinessLoggingOptions.TEXT_API, "textresourceservice")
                    .withUrlKey(BusinessLoggingOptions.READ_API, "readservice")
                    .withUrlKey(BusinessLoggingOptions.WRITE_API, "writeservice")
                    .factory());

    private static final List<OAuth2PropertySupplierResolver> DEFAULT_SERVICE_RESOLVERS = new ArrayList<>();

    static {
        DEFAULT_SERVICE_RESOLVERS.add(DESTINATION);
        DEFAULT_SERVICE_RESOLVERS.add(CONNECTIVITY);
        DEFAULT_SERVICE_RESOLVERS.add(BUSINESS_RULES);
        DEFAULT_SERVICE_RESOLVERS.add(WORKFLOW);
        DEFAULT_SERVICE_RESOLVERS.add(BUSINESS_LOGGING);
        DEFAULT_SERVICE_RESOLVERS.add(IDENTITY_AUTHENTICATION);
    }

    static List<OAuth2PropertySupplierResolver> getDefaultServiceResolvers()
    {
        return new ArrayList<>(DEFAULT_SERVICE_RESOLVERS);
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
            return options.getOption(IasTargetUrl.class).getOrElse(super::getServiceUri);
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

            attachIasCommunicationOptions(oAuth2OptionsBuilder);
            attachClientKeyStore(oAuth2OptionsBuilder);

            return oAuth2OptionsBuilder.build();
        }

        private void attachIasCommunicationOptions( @Nonnull final OAuth2Options.Builder optionsBuilder )
        {
            final IasCommunicationOptions o = options.getOption(IasCommunicationOptions.class).getOrNull();
            if( o == null ) {
                return;
            }

            if( o.isMTLSAuthenticationOnly() ) {
                optionsBuilder.withSkipTokenRetrieval(true);
                return;
            }

            if( o.getApplicationProviderName() != null ) {
                optionsBuilder
                    .withTokenRetrievalParameter(
                        "resource",
                        "urn:sap:identity:application:provider:name:" + o.getApplicationProviderName());
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
            if( !isX509CredentialType() ) {
                return null;
            }

            final String cert = getOAuthCredentialOrThrow(String.class, "certificate");
            final String key = getOAuthCredentialOrThrow(String.class, "key");

            try {
                return KeyStoreReader
                    .createKeyStore(
                        "IAS-CERTIFICATE",
                        "changeit".toCharArray(),
                        new StringReader(cert),
                        new StringReader(key));
            }
            catch( final Exception e ) {
                throw new DestinationAccessException("Unable to extract client key store from IAS service binding.", e);
            }
        }

        private boolean isX509CredentialType()
        {
            final CredentialType credentialType = getCredentialType();
            switch( credentialType ) {
                case X509:
                    return true;
                case BINDING_SECRET: // fallthrough
                case INSTANCE_SECRET:
                    return false;
                default:
                    throw new DestinationAccessException("""
                        Unable to determine whether IAS binding uses x509 authentication: \
                        Unhandled credential type '%s'.\
                        """.formatted(credentialType));
            }
        }
    }
}
