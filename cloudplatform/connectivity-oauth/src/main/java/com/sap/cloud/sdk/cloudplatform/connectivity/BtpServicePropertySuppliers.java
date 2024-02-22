/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.BtpServiceOptions.IasOptions.IasCommunicationOptions;
import static com.sap.cloud.sdk.cloudplatform.connectivity.BtpServiceOptions.IasOptions.IasTargetUri;

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
import com.sap.cloud.security.config.ClientCertificate;
import com.sap.cloud.security.config.ClientIdentity;
import com.sap.cloud.security.mtls.SSLContextFactory;

import lombok.extern.slf4j.Slf4j;

class BtpServicePropertySuppliers
{
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
            return options.getOption(IasTargetUri.class).getOrElse(super::getServiceUri);
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

            if( o.isMTLSAuthenticationOnly() && mTLSOnlyIsSupported() ) {
                optionsBuilder.withSkipTokenRetrieval(true);
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

        private boolean mTLSOnlyIsSupported()
        {
            final OnBehalfOf behalf = options.getOnBehalfOf();
            if( behalf == OnBehalfOf.NAMED_USER_CURRENT_TENANT ) {
                log.warn("""
                    Combining {} with mTLS only authentication is not supported. \
                    This is because mTLS only works without sending any authentication token. \
                    But without an authentication token, user information cannot be preserved.\
                    """, OnBehalfOf.NAMED_USER_CURRENT_TENANT);
                return false;
            }

            return true;
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
}
