package com.sap.cloud.sdk.cloudplatform.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.cloud.sdk.cloudplatform.exception.ObjectLookupFailedException;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderAccessor;
import com.sap.cloud.sdk.cloudplatform.security.exception.AuthTokenAccessException;
import com.sap.cloud.sdk.cloudplatform.util.FacadeLocator;

import io.vavr.control.Try;

public class AuthTokenAccessorTest
{
    @Before
    @After
    public void resetAccessor()
    {
        // reset on FacadeLocator requires concrete instance to be passed
        FacadeLocator.setMockableInstance(new FacadeLocator.MockableInstance());

        // reset the facade
        AuthTokenAccessor.setAuthTokenFacade(null);

        // make sure that there is no global fallback between tests
        AuthTokenAccessor.setFallbackToken(null);

        RequestHeaderAccessor.setHeaderFacade(null);
        RequestHeaderAccessor.setFallbackHeaderContainer(null);
    }

    @Test
    public void defaultFacadeShouldBeNullImplementation()
    {
        assertThat(AuthTokenAccessor.getAuthTokenFacade()).isSameAs(AuthTokenFacade.NULL);
    }

    @Test
    public void noImplementationShouldUseNullImplementation()
    {
        final FacadeLocator.MockableInstance mockedFacadeLocator = mock(FacadeLocator.MockableInstance.class);
        when(mockedFacadeLocator.getFacades(AuthTokenFacade.class)).thenReturn(Collections.emptyList());
        FacadeLocator.setMockableInstance(mockedFacadeLocator);

        AuthTokenAccessor.setAuthTokenFacade(null);

        assertThat(AuthTokenAccessor.getAuthTokenFacade()).isSameAs(AuthTokenFacade.NULL);

    }

    @Test
    public void multipleFacadesShouldThrowError()
    {
        final FacadeLocator.MockableInstance mockedFacadeLocator = mock(FacadeLocator.MockableInstance.class);
        final AuthTokenFacade mockedFacade1 = mock(AuthTokenFacade.class);
        final AuthTokenFacade mockedFacade2 = mock(AuthTokenFacade.class);
        when(mockedFacadeLocator.getFacades(AuthTokenFacade.class))
            .thenReturn(Arrays.asList(mockedFacade1, mockedFacade2));
        FacadeLocator.setMockableInstance(mockedFacadeLocator);

        assertThatThrownBy(() -> AuthTokenAccessor.setAuthTokenFacade(null))
            .isExactlyInstanceOf(ObjectLookupFailedException.class)
            .hasMessageContaining(AuthTokenFacade.class.getName());
    }

    @Test
    public void defaultSingleFacadeShouldBeUsed()
    {
        final FacadeLocator.MockableInstance mockedFacadeLocator = mock(FacadeLocator.MockableInstance.class);
        final AuthTokenFacade mockedFacade = mock(AuthTokenFacade.class);
        when(mockedFacadeLocator.getFacades(AuthTokenFacade.class)).thenReturn(Collections.singleton(mockedFacade));
        FacadeLocator.setMockableInstance(mockedFacadeLocator);

        AuthTokenAccessor.setAuthTokenFacade(null);

        assertThat(AuthTokenAccessor.getAuthTokenFacade()).isSameAs(mockedFacade);
    }

    @Test
    public void tryGetCurrentTokenSuccessShouldDelegateToFacade()
    {
        final AuthToken mockedToken = mock(AuthToken.class);
        AuthTokenAccessor.setAuthTokenFacade(() -> Try.success(mockedToken));

        assertThat(AuthTokenAccessor.tryGetCurrentToken().get()).isSameAs(mockedToken);
    }

    @Test
    public void tryGetCurrentTokenFailureShouldDelegateToFacade()
    {
        final Exception exception = new RuntimeException("No Token");
        AuthTokenAccessor.setAuthTokenFacade(() -> Try.failure(exception));

        assertThat(AuthTokenAccessor.tryGetCurrentToken().getCause()).isSameAs(exception);
    }

    @Test
    public void tryGetCurrentTokenFailureShouldUseFallbackToken()
    {
        final Exception exception = new RuntimeException("No Token");
        AuthTokenAccessor.setAuthTokenFacade(() -> Try.failure(exception));

        final AuthToken fallbackToken = mock(AuthToken.class);
        AuthTokenAccessor.setFallbackToken(() -> fallbackToken);

        assertThat(AuthTokenAccessor.tryGetCurrentToken().get()).isSameAs(fallbackToken);
    }

    @Test
    public void getCurrentTokenShouldReturnSuccessOfTryGetCurrentToken()
    {
        final AuthToken mockedToken = mock(AuthToken.class);
        AuthTokenAccessor.setAuthTokenFacade(() -> Try.success(mockedToken));

        assertThat(AuthTokenAccessor.getCurrentToken()).isSameAs(mockedToken);
    }

    @Test
    public void getCurrentTokenShouldReturnFallbackOfTryGetCurrentToken()
    {
        final Exception exception = new RuntimeException("No Token");
        AuthTokenAccessor.setAuthTokenFacade(() -> Try.failure(exception));

        final AuthToken fallbackToken = mock(AuthToken.class);
        AuthTokenAccessor.setFallbackToken(() -> fallbackToken);

        assertThat(AuthTokenAccessor.getCurrentToken()).isSameAs(fallbackToken);
    }

    @Test
    public void getCurrentTokenShouldThrowWrappedFailure()
    {
        final Exception exception = new RuntimeException("No Token");
        AuthTokenAccessor.setAuthTokenFacade(() -> Try.failure(exception));

        assertThatThrownBy(AuthTokenAccessor::getCurrentToken)
            .isInstanceOf(AuthTokenAccessException.class)
            .hasCause(exception);
    }

    @Test
    public void getCurrentTokenShouldThrowFailure()
    {
        final Exception exception = new AuthTokenAccessException("No Token");
        AuthTokenAccessor.setAuthTokenFacade(() -> Try.failure(exception));

        assertThatThrownBy(AuthTokenAccessor::getCurrentToken).isSameAs(exception);
    }
}
