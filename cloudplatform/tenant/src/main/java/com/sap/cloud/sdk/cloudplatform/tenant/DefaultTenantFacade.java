/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.tenant;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Payload;
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import com.google.gson.JsonObject;
import com.sap.cloud.sdk.cloudplatform.CloudPlatform;
import com.sap.cloud.sdk.cloudplatform.CloudPlatformAccessor;
import com.sap.cloud.sdk.cloudplatform.ScpCfCloudPlatform;
import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.exception.TenantAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextAccessor;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutor;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract base class for {@link TenantFacade}s.
 */
@Slf4j
public class DefaultTenantFacade implements TenantFacade
{
    private static final String XSUAA_JWT_ZONE_ID = "zid";
    private static final String IAS_JWT_ZONE_ID = "zone_uuid";
    private static final List<String> TENANT_ID_CLAIMS = Arrays.asList(XSUAA_JWT_ZONE_ID, IAS_JWT_ZONE_ID);
    private static final String JWT_ISSUER = "iss";

    @Nonnull
    private String getTenantId( @Nonnull final Payload jwt )
    {
        final Optional<String> maybeTenantId =
            TENANT_ID_CLAIMS.stream().map(id -> jwt.getClaim(id).asString()).filter(Objects::nonNull).findFirst();

        if( !maybeTenantId.isPresent() ) {
            throw new TenantAccessException(
                "No tenant/zone identifier (one of these elements ["
                    + TENANT_ID_CLAIMS.toString()
                    + "]) found in JWT.");
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

        return tryGetTenantFromAuthToken(AuthTokenAccessor.tryGetCurrentToken()) // read from user token
            .orElse(() -> tryGetTenantFromServiceBinding(CloudPlatformAccessor.tryGetCloudPlatform())); // read bindings
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

        return Try.of(() -> new ScpCfTenant(tenantIdTry.get(), subdomainTry.get()));
    }

    @Nonnull
    private Try<ScpCfTenant> tryGetTenantFromServiceBinding( @Nonnull final Try<CloudPlatform> platform )
    {
        for( final ServiceBindingTenantExtractor extractor : ServiceBindingTenantExtractor.values() ) {
            final Try<Iterable<JsonObject>> bindings =
                platform
                    .map(ScpCfCloudPlatform.class::cast)
                    .map(p -> p.getServiceCredentialsByPlan(extractor.getService(), filter -> true).values())
                    .map(Iterables::concat);

            if( bindings.isFailure() ) {
                log.debug("Unable to parse service bindings for {}.", extractor.getService(), bindings.getCause());
                continue;
            }

            final Optional<ScpCfTenant> tenant =
                Streams
                    .stream(bindings.get())
                    .peek(obj -> log.trace("Trying to extract tenant information from service binding {}.", obj))
                    .flatMap(obj -> extractor.getExtractor().apply(obj).toJavaStream())
                    .findFirst();

            if( tenant.isPresent() ) {
                return Try.success(tenant.get());
            }
        }
        return Try.failure(new CloudPlatformException("Failed to extract tenant from service bindings."));
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
