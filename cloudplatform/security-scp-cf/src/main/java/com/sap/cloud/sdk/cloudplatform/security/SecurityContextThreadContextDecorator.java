package com.sap.cloud.sdk.cloudplatform.security;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextDecorator;
import com.sap.cloud.security.token.SecurityContext;
import com.sap.cloud.security.token.Token;
import com.sap.cloud.security.x509.Certificate;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link ThreadContextDecorator} that ensures the correct initialization of {@link Token Tokens} when
 * working with non-container managed threads on Cloud Foundry.
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
        @Nullable
        final Token tokenToPass = SecurityContext.getToken();
        final Certificate certToPass = SecurityContext.getClientCertificate();

        if( tokenToPass == null && certToPass == null ) {
            log.debug("Current SecurityContext is empty, no propagation to new Thread necessary.");
            return callable;
        }

        log.debug("Propagating current SecurityContext to new Thread.");
        return () -> {
            @Nullable
            final Token initialToken = SecurityContext.getToken();
            @Nullable
            final Certificate initialCert = SecurityContext.getClientCertificate();

            log.debug("Setting current SecurityContext to propagated context.");
            SecurityContext.setToken(tokenToPass);
            SecurityContext.setClientCertificate(certToPass);
            try {
                return callable.call();
            }
            finally {
                log.debug("Resetting SecurityContext to initial value.");
                SecurityContext.setToken(initialToken);
                SecurityContext.setClientCertificate(initialCert);
            }
        };
    }
}
