/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextDecorator;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link ThreadContextDecorator} that ensures the correct initialization of
 * {@link com.sap.cloud.security.token.Token Tokens} when working with non-container managed threads on Cloud Foundry.
 * This decorator is only active when the class {@code com.sap.cloud.security.token.SecurityContext} is available on the
 * classpath. Check dependency {@code com.sap.cloud.security:java-api} for details.
 */
@Slf4j
public class SecurityContextThreadContextDecorator implements ThreadContextDecorator
{
    @Getter
    private final int priority = DefaultPriorities.SCP_CF_SECURITY_CONTEXT_DECORATOR;

    @Nonnull
    @Override
    public <T> Callable<T> decorateCallable( @Nonnull final Callable<T> callable )
    {
        try {
            Class.forName("com.sap.cloud.security.token.SecurityContext", true, this.getClass().getClassLoader());
        }
        catch( final ClassNotFoundException e ) {
            log.debug("SecurityContext class not found, no propagation to new Thread necessary.");
            return callable;
        }

        @Nullable
        final com.sap.cloud.security.token.Token tokenToPass = com.sap.cloud.security.token.SecurityContext.getToken();
        final com.sap.cloud.security.x509.Certificate certToPass =
            com.sap.cloud.security.token.SecurityContext.getClientCertificate();

        if( tokenToPass == null && certToPass == null ) {
            log.debug("Current SecurityContext is empty, no propagation to new Thread necessary.");
            return callable;
        }

        log.debug("Propagating current SecurityContext to new Thread.");
        return () -> {
            @Nullable
            final com.sap.cloud.security.token.Token initialToken =
                com.sap.cloud.security.token.SecurityContext.getToken();
            @Nullable
            final com.sap.cloud.security.x509.Certificate initialCert =
                com.sap.cloud.security.token.SecurityContext.getClientCertificate();

            log.debug("Setting current SecurityContext to propagated context.");
            com.sap.cloud.security.token.SecurityContext.setToken(tokenToPass);
            com.sap.cloud.security.token.SecurityContext.setClientCertificate(certToPass);
            try {
                return callable.call();
            }
            finally {
                log.debug("Resetting SecurityContext to initial value.");
                com.sap.cloud.security.token.SecurityContext.setToken(initialToken);
                com.sap.cloud.security.token.SecurityContext.setClientCertificate(initialCert);
            }
        };
    }
}
