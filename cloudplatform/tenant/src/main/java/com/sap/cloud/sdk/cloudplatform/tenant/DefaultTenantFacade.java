/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.tenant;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Payload;
import com.sap.cloud.environment.servicebinding.api.DefaultServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.exception.TenantAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextAccessor;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutor;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * Default implementation of {@link TenantFacade} which provides access to tenant information on SAP Business Technology
 * Platform Cloud Foundry.
 */
@Slf4j
public class DefaultTenantFacade implements TenantFacade
{
    private static final String XSUAA_JWT_ZONE_ID = "zid";
    private static final String IAS_JWT_ZONE_ID = "zone_uuid";
    private static final String IAS_JWT_APP_TID = "app_tid";
    private static final List<String> TENANT_ID_CLAIMS =
        Arrays.asList(XSUAA_JWT_ZONE_ID, IAS_JWT_APP_TID, IAS_JWT_ZONE_ID);
    private static final String JWT_ISSUER = "iss";

    @Nonnull
    private String getTenantId( @Nonnull final Payload jwt )
    {
        final Optional<String> maybeTenantId =
            TENANT_ID_CLAIMS.stream().map(id -> jwt.getClaim(id).asString()).filter(Objects::nonNull).findFirst();

        if( !maybeTenantId.isPresent() ) {
            throw new TenantAccessException(
                "No tenant/zone identifier (one of these elements [" + TENANT_ID_CLAIMS + "]) found in JWT.");
        }

        return maybeTenantId.get();
    }

    @Nonnull
    private String getIssuer( @Nonnull final DecodedJWT jwt )
    {
        final String issuer = jwt.getClaim(JWT_ISSUER).asString();

        if( issuer == null ) {
            throw new TenantAccessException("No subdomain ('" + JWT_ISSUER + "' element) found in JWT.");
        }

        return issuer;
    }

    @Nonnull
    private String getSubdomain( @Nonnull final URI issuerUri )
    {
        final String host = issuerUri.getHost();
        if( host == null || !host.contains(".") ) {
            throw new TenantAccessException("Failed to get subdomain from issuer URI '" + issuerUri + "'.");
        }
        return host.split("\\.")[0];
    }

    @Nonnull
    @Override
    public Try<Tenant> tryGetCurrentTenant()
    {
        final Try<Tenant> tenantFromThreadContextTry =
            ThreadContextAccessor
                .tryGetCurrentContext()
                .flatMap(c -> c.getPropertyValue(TenantThreadContextListener.PROPERTY_TENANT));

        if( tenantFromThreadContextTry.isSuccess() ) {
            return tenantFromThreadContextTry;
        }

        final List<Throwable> throwables = new ArrayList<>();
        throwables.add(tenantFromThreadContextTry.getCause());

        return tryGetTenantFromAuthToken(AuthTokenAccessor.tryGetCurrentToken())
            .onFailure(throwables::add) // read from user token
            .orElse(this::tryGetTenantFromServiceBinding)
            .onFailure(throwables::add) // read bindings
            .orElse(() -> createFallbackException(throwables));
    }

    private Try<Tenant> createFallbackException( @Nonnull final List<? extends Throwable> throwables )
    {
        final TenantAccessException resultingException = new TenantAccessException("Failed to get current tenant.");

        throwables.forEach(resultingException::addSuppressed);

        return Try.failure(resultingException);
    }

    @Nonnull
    private Try<Tenant> tryGetTenantFromAuthToken( @Nonnull final Try<AuthToken> authToken )
    {
        final Try<DecodedJWT> jwtTry = authToken.map(AuthToken::getJwt);

        if( jwtTry.isFailure() ) {
            return Try.failure(jwtTry.getCause());
        }

        final Try<String> tenantIdTry = jwtTry.map(this::getTenantId);

        if( tenantIdTry.isFailure() ) {
            return Try.failure(tenantIdTry.getCause());
        }

        final Try<String> subdomainTry = jwtTry.map(this::getIssuer).map(URI::create).map(this::getSubdomain);

        if( subdomainTry.isFailure() ) {
            return Try.failure(subdomainTry.getCause());
        }

        return Try.of(() -> new DefaultTenant(tenantIdTry.get(), subdomainTry.get()));
    }

    @Nonnull
    private Try<DefaultTenant> tryGetTenantFromServiceBinding()
    {
        final List<ServiceBinding> serviceBindings = DefaultServiceBindingAccessor.getInstance().getServiceBindings();

        for( final ServiceBindingTenantExtractor extractor : ServiceBindingTenantExtractor.values() ) {
            final Optional<DefaultTenant> tenant =
                streamServiceCredentialsByPlan(serviceBindings, extractor.getService())
                    .peek(obj -> log.trace("Trying to extract tenant information from service binding {}.", obj))
                    .flatMap(obj -> extractor.getExtractor().apply(obj).toJavaStream())
                    .findFirst();

            if( tenant.isPresent() ) {
                return Try.success(tenant.get());
            }
        }
        return Try.failure(new CloudPlatformException("Failed to extract tenant from service bindings."));
    }

    @Nonnull
    private Stream<Map<String, Object>> streamServiceCredentialsByPlan(
        @Nonnull final Collection<ServiceBinding> serviceBindings,
        @Nonnull final String serviceName )
    {
        return serviceBindings
            .stream()
            .filter(binding -> serviceName.equals(binding.getServiceName().orElse(null)))
            .map(ServiceBinding::getCredentials);
    }

    @Nullable
    <T> T executeWithTenant( @Nonnull final Tenant tenant, @Nonnull final Callable<T> callable )
    {
        return ThreadContextExecutor
            .fromCurrentOrNewContext()
            .withListeners(new TenantThreadContextListener(tenant))
            .execute(callable);
    }
}
