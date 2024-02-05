/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.connectivity.BtpServiceOptions.BusinessLoggingOptions;
import com.sap.cloud.sdk.cloudplatform.connectivity.BtpServiceOptions.BusinessRulesOptions;
import com.sap.cloud.sdk.cloudplatform.connectivity.BtpServiceOptions.WorkflowOptions;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantWithSubdomain;
import com.sap.cloud.security.xsuaa.client.OAuth2ServiceEndpointsProvider;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

class BtpServicePropertySuppliers
{
    static final OAuth2PropertySupplierResolver DESTINATION =
        OAuth2PropertySupplierResolver.forServiceIdentifier(ServiceIdentifier.DESTINATION, Destination::new);

    static final OAuth2PropertySupplierResolver CONNECTIVITY =
        OAuth2PropertySupplierResolver.forServiceIdentifier(ServiceIdentifier.CONNECTIVITY, ConnectivityProxy::new);

    static final OAuth2PropertySupplierResolver IDENTITY_AUTHORIZATION =
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
        DEFAULT_SERVICE_RESOLVERS.add(IDENTITY_AUTHORIZATION);
        DEFAULT_SERVICE_RESOLVERS.add(BUSINESS_RULES);
        DEFAULT_SERVICE_RESOLVERS.add(WORKFLOW);
        DEFAULT_SERVICE_RESOLVERS.add(BUSINESS_LOGGING);
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
            super(options, Collections.emptyList());
        }

        @Nonnull
        @Override
        public URI getTokenUri()
        {
            final URI providerUrl = getCredentialOrThrow(URI.class, "url");
            final String domain = getCredentialOrThrow(String.class, "domain");

            return TenantAccessor
                .tryGetCurrentTenant()
                .filter(TenantWithSubdomain.class::isInstance)
                .map(TenantWithSubdomain.class::cast)
                .map(TenantWithSubdomain::getSubdomain)
                // TODO: this somewhat feels very fragile. Is there a better way?
                .map(subdomain -> providerUrl.getScheme() + "://" + subdomain + "." + domain)
                .map(URI::create)
                .getOrElse(providerUrl);
        }

        @Nonnull
        @Override
        public OAuth2ServiceEndpointsProvider getTokenEndpoints()
        {
            return new Endpoints(this::getTokenUri);
        }

        @RequiredArgsConstructor
        @EqualsAndHashCode( doNotUseGetters = true )
        private static class Endpoints implements OAuth2ServiceEndpointsProvider
        {
            @Nonnull
            private final Supplier<URI> baseUriSupplier;

            @Nonnull
            @EqualsAndHashCode.Include
            private URI getBaseUri()
            {
                return baseUriSupplier.get();
            }

            @Override
            public URI getTokenEndpoint()
            {
                return getBaseUri().resolve("/oauth2/token");
            }

            @Override
            public URI getAuthorizeEndpoint()
            {
                return getBaseUri().resolve("/oauth2/authorize");
            }

            @Override
            public URI getJwksUri()
            {
                return getBaseUri().resolve("/token_keys");
            }
        }
    }
}
