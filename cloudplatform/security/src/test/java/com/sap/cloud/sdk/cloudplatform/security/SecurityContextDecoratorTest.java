package com.sap.cloud.sdk.cloudplatform.security;

import static org.mockito.Mockito.mock;

import java.util.concurrent.CompletableFuture;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutors;
import com.sap.cloud.security.token.SecurityContext;
import com.sap.cloud.security.token.Token;
import com.sap.cloud.security.x509.Certificate;

class SecurityContextDecoratorTest
{
    private static final Token token = Mockito.mock(Token.class);
    private static final Certificate cert = Mockito.mock(Certificate.class);

    @BeforeEach
    void setUp()
    {
        SecurityContext.setToken(token);
        SecurityContext.setClientCertificate(cert);
    }

    @AfterEach
    void tearDown()
    {
        SecurityContext.clear();
    }

    private void asserContextIsAvailable()
    {
        Assertions.assertThat(SecurityContext.getToken()).isEqualTo(token);
        Assertions.assertThat(SecurityContext.getClientCertificate()).isEqualTo(cert);
    }

    private void asserContextIsNOTAvailable()
    {
        Assertions.assertThat(SecurityContext.getToken()).isNull();
        Assertions.assertThat(SecurityContext.getClientCertificate()).isNull();
    }

    @Test
    void testBaseCases()
    {
        asserContextIsAvailable();
        CompletableFuture.runAsync(this::asserContextIsNOTAvailable);
    }

    @Test
    @DisplayName( "SDK executors should propagate SecurityContext to new thread" )
    void threadContextAsyncTest()
    {
        ThreadContextExecutors.execute(this::asserContextIsAvailable);
    }
}
