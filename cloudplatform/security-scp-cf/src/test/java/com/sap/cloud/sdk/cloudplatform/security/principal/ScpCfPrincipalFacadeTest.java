package com.sap.cloud.sdk.cloudplatform.security.principal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.assertj.vavr.api.VavrAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;
import com.sap.cloud.sdk.cloudplatform.security.exception.AuthTokenAccessException;
import com.sap.cloud.sdk.cloudplatform.security.exception.BasicAuthenticationAccessException;
import com.sap.cloud.sdk.cloudplatform.security.principal.exception.PrincipalAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.DefaultThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.Property;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutor;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextPropertyNotFoundException;
import com.sap.cloud.sdk.testutil.ThrowableAssertionUtil;

import io.vavr.control.Try;

@Deprecated
class ScpCfPrincipalFacadeTest
{
    private OAuth2AuthTokenPrincipalExtractor mockedOauthAuthTokenPrincipalExtractor;
    private OidcAuthTokenPrincipalExtractor mockedOidcAuthTokenPrincipalExtractor;
    private BasicCredentialsPrincipalExtractor mockedBasicCredentialsPrincipalExtractor;

    private final Principal somePrincipal = mock(Principal.class);

    @BeforeEach
    void setUp()
    {
        mockedOauthAuthTokenPrincipalExtractor = mock(OAuth2AuthTokenPrincipalExtractor.class);
        mockedOidcAuthTokenPrincipalExtractor = mock(OidcAuthTokenPrincipalExtractor.class);
        mockedBasicCredentialsPrincipalExtractor = mock(BasicCredentialsPrincipalExtractor.class);
    }

    @Test
    void tryGetCurrentPrincipalShouldReadFromThreadContextIfGiven()
    {
        when(mockedOauthAuthTokenPrincipalExtractor.tryGetCurrentPrincipal())
            .thenReturn(Try.failure(new AuthTokenAccessException()));
        when(mockedOidcAuthTokenPrincipalExtractor.tryGetCurrentPrincipal())
            .thenReturn(Try.failure(new AuthTokenAccessException()));
        when(mockedBasicCredentialsPrincipalExtractor.tryGetCurrentPrincipal())
            .thenReturn(Try.failure(new BasicAuthenticationAccessException()));

        final ThreadContext filledContext = new DefaultThreadContext();
        filledContext
            .setPropertyIfAbsent(PrincipalThreadContextListener.PROPERTY_PRINCIPAL, Property.of(somePrincipal));

        final Try<Principal> retrievedPrincipal =
            ThreadContextExecutor
                .using(filledContext)
                .execute(
                    () -> new ScpCfPrincipalFacade(
                        mockedOauthAuthTokenPrincipalExtractor,
                        mockedOidcAuthTokenPrincipalExtractor,
                        mockedBasicCredentialsPrincipalExtractor).tryGetCurrentPrincipal());

        VavrAssertions.assertThat(retrievedPrincipal).contains(somePrincipal);
        verify(mockedOauthAuthTokenPrincipalExtractor, never()).tryGetCurrentPrincipal();
        verify(mockedOidcAuthTokenPrincipalExtractor, never()).tryGetCurrentPrincipal();
        verify(mockedBasicCredentialsPrincipalExtractor, never()).tryGetCurrentPrincipal();
    }

    @Test
    void tryGetCurrentPrincipalShouldReturnAuthTokenPrincipalIfGiven()
    {
        when(mockedOauthAuthTokenPrincipalExtractor.tryGetCurrentPrincipal()).thenReturn(Try.success(somePrincipal));
        when(mockedOidcAuthTokenPrincipalExtractor.tryGetCurrentPrincipal())
            .thenReturn(Try.failure(new ShouldNotHappenException()));
        when(mockedBasicCredentialsPrincipalExtractor.tryGetCurrentPrincipal())
            .thenReturn(Try.failure(new ShouldNotHappenException()));

        final Try<Principal> retrievedPrincipal =
            ThreadContextExecutor
                .fromNewContext()
                .withoutDefaultListeners()
                .execute(
                    () -> new ScpCfPrincipalFacade(
                        mockedOauthAuthTokenPrincipalExtractor,
                        mockedOidcAuthTokenPrincipalExtractor,
                        mockedBasicCredentialsPrincipalExtractor).tryGetCurrentPrincipal());

        VavrAssertions.assertThat(retrievedPrincipal).contains(somePrincipal);
        verify(mockedOauthAuthTokenPrincipalExtractor).tryGetCurrentPrincipal();
        verify(mockedOidcAuthTokenPrincipalExtractor, never()).tryGetCurrentPrincipal();
        verify(mockedBasicCredentialsPrincipalExtractor, never()).tryGetCurrentPrincipal();
    }

    @Test
    void tryGetCurrentPrincipalShouldReturnIasAuthTokenPrincipalIfGiven()
    {
        when(mockedOauthAuthTokenPrincipalExtractor.tryGetCurrentPrincipal())
            .thenReturn(Try.failure(new PrincipalAccessException()));
        when(mockedOidcAuthTokenPrincipalExtractor.tryGetCurrentPrincipal()).thenReturn(Try.success(somePrincipal));
        when(mockedBasicCredentialsPrincipalExtractor.tryGetCurrentPrincipal())
            .thenReturn(Try.failure(new ShouldNotHappenException()));

        final Try<Principal> retrievedPrincipal =
            ThreadContextExecutor
                .fromNewContext()
                .withoutDefaultListeners()
                .execute(
                    () -> new ScpCfPrincipalFacade(
                        mockedOauthAuthTokenPrincipalExtractor,
                        mockedOidcAuthTokenPrincipalExtractor,
                        mockedBasicCredentialsPrincipalExtractor).tryGetCurrentPrincipal());

        VavrAssertions.assertThat(retrievedPrincipal).contains(somePrincipal);
        verify(mockedOauthAuthTokenPrincipalExtractor).tryGetCurrentPrincipal();
        verify(mockedOidcAuthTokenPrincipalExtractor).tryGetCurrentPrincipal();
        verify(mockedBasicCredentialsPrincipalExtractor, never()).tryGetCurrentPrincipal();
    }

    @Test
    void tryGetCurrentPrincipalShouldReturnBasicCredentialsPrincipalIfGiven()
    {
        when(mockedOauthAuthTokenPrincipalExtractor.tryGetCurrentPrincipal())
            .thenReturn(Try.failure(new ShouldNotHappenException()));
        when(mockedOidcAuthTokenPrincipalExtractor.tryGetCurrentPrincipal())
            .thenReturn(Try.failure(new ShouldNotHappenException()));
        when(mockedBasicCredentialsPrincipalExtractor.tryGetCurrentPrincipal()).thenReturn(Try.success(somePrincipal));

        final Try<Principal> retrievedPrincipal =
            ThreadContextExecutor
                .fromNewContext()
                .withoutDefaultListeners()
                .execute(
                    () -> new ScpCfPrincipalFacade(
                        mockedOauthAuthTokenPrincipalExtractor,
                        mockedOidcAuthTokenPrincipalExtractor,
                        mockedBasicCredentialsPrincipalExtractor).tryGetCurrentPrincipal());

        VavrAssertions.assertThat(retrievedPrincipal).contains(somePrincipal);
        verify(mockedOauthAuthTokenPrincipalExtractor).tryGetCurrentPrincipal();
        verify(mockedOidcAuthTokenPrincipalExtractor).tryGetCurrentPrincipal();
        verify(mockedBasicCredentialsPrincipalExtractor).tryGetCurrentPrincipal();
    }

    @Test
    void tryGetCurrentPrincipalShouldThrowExceptionIfNoPrincipalWasGiven()
    {
        when(mockedOauthAuthTokenPrincipalExtractor.tryGetCurrentPrincipal())
            .thenReturn(Try.failure(new AuthTokenAccessException()));
        when(mockedOidcAuthTokenPrincipalExtractor.tryGetCurrentPrincipal())
            .thenReturn(Try.failure(new AuthTokenAccessException()));
        when(mockedBasicCredentialsPrincipalExtractor.tryGetCurrentPrincipal())
            .thenReturn(Try.failure(new BasicAuthenticationAccessException()));

        final Try<Principal> retrievedPrincipal =
            ThreadContextExecutor
                .fromNewContext()
                .withoutDefaultListeners()
                .execute(
                    () -> new ScpCfPrincipalFacade(
                        mockedOauthAuthTokenPrincipalExtractor,
                        mockedOidcAuthTokenPrincipalExtractor,
                        mockedBasicCredentialsPrincipalExtractor).tryGetCurrentPrincipal());

        verify(mockedOauthAuthTokenPrincipalExtractor).tryGetCurrentPrincipal();
        verify(mockedOidcAuthTokenPrincipalExtractor).tryGetCurrentPrincipal();
        verify(mockedBasicCredentialsPrincipalExtractor).tryGetCurrentPrincipal();

        VavrAssertions.assertThat(retrievedPrincipal).isFailure();
        retrievedPrincipal.onFailure(rootException -> {
            assertThat(rootException).isInstanceOf(PrincipalAccessException.class);
            ThrowableAssertionUtil
                .assertHasSuppressedExceptionTypes(
                    rootException,
                    ThreadContextPropertyNotFoundException.class,
                    AuthTokenAccessException.class,
                    AuthTokenAccessException.class,
                    BasicAuthenticationAccessException.class);
        });
    }

    @Test
    void tryGetCurrentPrincipalShouldReadFromThreadContextIfExceptionGiven()
    {
        final Try<Principal> someExceptionTry = Try.failure(mock(PrincipalAccessException.class));
        final ThreadContext filledContext = new DefaultThreadContext();
        filledContext
            .setPropertyIfAbsent(PrincipalThreadContextListener.PROPERTY_PRINCIPAL, Property.ofTry(someExceptionTry));

        final Try<Principal> retrievedPrincipal =
            ThreadContextExecutor
                .using(filledContext)
                .execute(
                    () -> new ScpCfPrincipalFacade(
                        mockedOauthAuthTokenPrincipalExtractor,
                        mockedOidcAuthTokenPrincipalExtractor,
                        mockedBasicCredentialsPrincipalExtractor).tryGetCurrentPrincipal());

        VavrAssertions.assertThat(retrievedPrincipal).isFailure();
        verify(mockedOauthAuthTokenPrincipalExtractor, never()).tryGetCurrentPrincipal();
        verify(mockedOidcAuthTokenPrincipalExtractor, never()).tryGetCurrentPrincipal();
        verify(mockedBasicCredentialsPrincipalExtractor, never()).tryGetCurrentPrincipal();
    }
}
