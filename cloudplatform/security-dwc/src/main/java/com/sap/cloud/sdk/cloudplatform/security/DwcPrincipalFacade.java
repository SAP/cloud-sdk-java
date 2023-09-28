/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.cloudplatform.security;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.sap.cloud.sdk.cloudplatform.DwcHeaderUtils;
import com.sap.cloud.sdk.cloudplatform.security.principal.DefaultPrincipal;
import com.sap.cloud.sdk.cloudplatform.security.principal.DefaultPrincipalFacade;
import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalThreadContextListener;
import com.sap.cloud.sdk.cloudplatform.security.principal.exception.PrincipalAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextAccessor;

import io.vavr.control.Try;

/**
 * Represents a specific {@link DefaultPrincipalFacade} that is used when running on the SAP Deploy with Confidence
 * stack.
 */
@Beta
public class DwcPrincipalFacade extends DefaultPrincipalFacade
{
    private static final String PRINCIPAL = PrincipalThreadContextListener.PROPERTY_PRINCIPAL;

    @Nonnull
    @Override
    public Try<Principal> tryGetCurrentPrincipal()
    {
        @Nullable
        final ThreadContext currentContext = ThreadContextAccessor.getCurrentContextOrNull();
        if( currentContext != null && currentContext.containsProperty(PRINCIPAL) ) {
            return currentContext.getPropertyValue(PRINCIPAL);
        }
        return Try.of(DwcPrincipalFacade::extractPrincipalFromDwcHeaders);
    }

    @SuppressWarnings( "deprecation" )
    @Nonnull
    private static Principal extractPrincipalFromDwcHeaders()
    {
        try {
            final String id = DwcHeaderUtils.getDwcPrincipalIdOrThrow();
            final Set<Authorization> authorizations =
                DwcHeaderUtils.getDwcScopesOrThrow().stream().map(Authorization::new).collect(Collectors.toSet());
            return new DefaultPrincipal(
                id,
                authorizations,
                authorizations,
                Collections.emptySet(),
                Collections.emptyMap());
        }
        catch( final Exception e ) {
            throw new PrincipalAccessException("Failed to extract principal from DwC headers.", e);
        }
    }
}
