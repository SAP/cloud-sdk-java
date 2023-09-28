package com.sap.cloud.sdk.cloudplatform.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;

import org.assertj.vavr.api.VavrAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.net.HttpHeaders;
import com.sap.cloud.sdk.cloudplatform.requestheader.DefaultRequestHeaderContainer;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderAccessor;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderContainer;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderFacade;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderThreadContextListener;
import com.sap.cloud.sdk.cloudplatform.security.exception.AuthTokenAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.Property;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutor;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutors;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextExecutionException;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextPropertyNotFoundException;

import io.vavr.control.Try;

public class ScpCfAuthTokenFacadeTest
{
    @Before
    @After
    public void resetAccessor()
    {
        // reset the facade
        AuthTokenAccessor.setAuthTokenFacade(null);

        // make sure that there is no global fallback between tests
        AuthTokenAccessor.setFallbackToken(null);

        RequestHeaderAccessor.setHeaderFacade(null);
        RequestHeaderAccessor.setFallbackHeaderContainer(null);
    }

    private final ScpCfAuthTokenFacade facade = new ScpCfAuthTokenFacade();

    @Test
    public void testExecute()
    {
        VavrAssertions
            .assertThat(facade.tryGetCurrentToken())
            .isFailure()
            .failBecauseOf(ThreadContextAccessException.class);

        final AuthToken mockedAuthToken = mock(AuthToken.class);

        facade.executeWithAuthToken(mockedAuthToken, () -> {
            // no ThreadContext managed
            final Try<AuthToken> requestTry = CompletableFuture.supplyAsync(facade::tryGetCurrentToken).get();

            VavrAssertions.assertThat(requestTry).isFailure().failBecauseOf(ThreadContextAccessException.class);

            return null;
        });

        final AuthToken request =
            facade
                .executeWithAuthToken(
                    mockedAuthToken,
                    () -> ThreadContextExecutors.submit(facade::tryGetCurrentToken).get().get());

        assertThat(request).isSameAs(mockedAuthToken);
    }

    private static class FailingAuthTokenFacade extends ScpCfAuthTokenFacade
    {
        @Nonnull
        @Override
        public Try<AuthToken> tryGetCurrentToken()
        {
            return super.tryGetCurrentToken().orElse(Try.failure(new AuthTokenAccessException()));
        }
    }

    @Test
    public void testExecuteWithFallback()
    {
        AuthTokenAccessor.setAuthTokenFacade(new ScpCfAuthTokenFacade());
        VavrAssertions.assertThat(AuthTokenAccessor.tryGetCurrentToken()).isFailure();

        final AuthToken successAuthToken = mock(AuthToken.class);
        final AuthToken fallbackAuthToken = mock(AuthToken.class);

        // check if fallback is not used if there is already a request
        AuthTokenAccessor.executeWithAuthToken(successAuthToken, () -> {
            assertThat(AuthTokenAccessor.getCurrentToken()).isSameAs(successAuthToken);

            AuthTokenAccessor.executeWithFallbackAuthToken(() -> fallbackAuthToken, () -> {
                assertThat(AuthTokenAccessor.getCurrentToken()).isSameAs(successAuthToken);
            });
        });

        // check if fallback is used
        AuthTokenAccessor.setAuthTokenFacade(new ScpCfAuthTokenFacadeTest.FailingAuthTokenFacade());
        VavrAssertions.assertThat(AuthTokenAccessor.tryGetCurrentToken()).isFailure();

        AuthTokenAccessor.executeWithFallbackAuthToken(() -> fallbackAuthToken, () -> {
            assertThat(AuthTokenAccessor.getCurrentToken()).isSameAs(fallbackAuthToken);
        });
    }

    @Test
    public void testExecuteWithException()
    {
        AuthTokenAccessor.setAuthTokenFacade(new ScpCfAuthTokenFacade());
        VavrAssertions.assertThat(AuthTokenAccessor.tryGetCurrentToken()).isFailure();

        assertThatThrownBy(() -> AuthTokenAccessor.executeWithAuthToken(mock(AuthToken.class), () -> {
            throw new IllegalArgumentException();
        }))
            .isExactlyInstanceOf(ThreadContextExecutionException.class)
            .hasCauseExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testExecuteWithFallbackWithException()
    {
        AuthTokenAccessor.setAuthTokenFacade(new ScpCfAuthTokenFacade());
        VavrAssertions.assertThat(AuthTokenAccessor.tryGetCurrentToken()).isFailure();

        final AuthToken request = mock(AuthToken.class);
        final AuthToken fallbackAuthToken = mock(AuthToken.class);

        AuthTokenAccessor.executeWithAuthToken(request, () -> {

            // check if IllegalArgumentException is wrapped
            assertThatThrownBy(() -> AuthTokenAccessor.executeWithFallbackAuthToken(() -> fallbackAuthToken, () -> {
                assertThat(AuthTokenAccessor.getCurrentToken()).isSameAs(request);
                throw new IllegalArgumentException();

            }))
                .isExactlyInstanceOf(ThreadContextExecutionException.class)
                .hasCauseExactlyInstanceOf(IllegalArgumentException.class);

            // check if ThreadContextExecutionException is not wrapped
            assertThatThrownBy(() -> AuthTokenAccessor.executeWithFallbackAuthToken(() -> fallbackAuthToken, () -> {
                assertThat(AuthTokenAccessor.getCurrentToken()).isSameAs(request);
                throw new ThreadContextExecutionException();
            })).isExactlyInstanceOf(ThreadContextExecutionException.class).hasNoCause();

        });

        VavrAssertions.assertThat(AuthTokenAccessor.tryGetCurrentToken()).isFailure();
    }

    @Test
    public void testGlobalFallback()
    {
        final AuthToken globalFallback = mock(AuthToken.class);
        final AuthToken request = mock(AuthToken.class);

        AuthTokenAccessor.setFallbackToken(() -> globalFallback);
        assertThat(AuthTokenAccessor.getCurrentToken()).isSameAs(globalFallback);

        AuthTokenAccessor.setAuthTokenFacade(new ScpCfAuthTokenFacade());
        assertThat(AuthTokenAccessor.getCurrentToken()).isSameAs(globalFallback);

        ThreadContextExecutor.fromNewContext().withoutDefaultListeners().execute(() -> {
            assertThat(AuthTokenAccessor.getCurrentToken()).isSameAs(globalFallback);
        });

        AuthTokenAccessor.executeWithAuthToken(request, () -> {
            assertThat(AuthTokenAccessor.getCurrentToken()).isSameAs(request);
        });
    }

    private static class BrokenAuthTokenThreadContextListener extends AuthTokenThreadContextListener
    {
        @Override
        public void beforeInitialize( @Nonnull final ThreadContext threadContext )
        {
            threadContext
                .setPropertyIfAbsent(
                    AuthTokenThreadContextListener.PROPERTY_AUTH_TOKEN,
                    Property.of("this-is-not-a-token-object"));
        }
    }

    @Test
    public void testWrongPropertyType()
    {
        AuthTokenAccessor.setAuthTokenFacade(new ScpCfAuthTokenFacade());

        ThreadContextExecutor
            .fromNewContext()
            .withoutDefaultListeners()
            .withListeners(new BrokenAuthTokenThreadContextListener())
            .execute(() -> {
                assertThatThrownBy(AuthTokenAccessor::getCurrentToken).isExactlyInstanceOf(ClassCastException.class);
            });
    }

    @Test
    public void testMissingRequest()
    {
        AuthTokenAccessor.setAuthTokenFacade(new ScpCfAuthTokenFacade());

        ThreadContextExecutor.fromNewContext().withoutDefaultListeners().execute(() -> {
            VavrAssertions
                .assertThat(AuthTokenAccessor.tryGetCurrentToken())
                .isFailure()
                .failBecauseOf(ThreadContextPropertyNotFoundException.class)
                .failReasonHasMessage(
                    "Property '" + RequestHeaderThreadContextListener.PROPERTY_REQUEST_HEADERS + "' does not exist.");
        });
    }

    @Test
    public void testMissingRequestHeader()
    {
        AuthTokenAccessor.setAuthTokenFacade(new ScpCfAuthTokenFacade());

        ThreadContextExecutor
            .fromNewContext()
            .withoutDefaultListeners()
            .withListeners(new RequestHeaderThreadContextListener(RequestHeaderContainer.EMPTY))
            .execute(() -> {
                VavrAssertions
                    .assertThat(AuthTokenAccessor.tryGetCurrentToken())
                    .isFailure()
                    .failBecauseOf(AuthTokenAccessException.class)
                    .failReasonHasMessage("Failed to decode JWT bearer: no 'Authorization' header present in request.");
            });
    }

    @Test
    public void testFromRequestHeaders()
    {
        final RequestHeaderContainer headers =
            DefaultRequestHeaderContainer
                .builder()
                .withHeader(HttpHeaders.AUTHORIZATION, "bearer some auth token")
                .build();
        final AuthToken authToken = mock(AuthToken.class);

        // setup RequestHeaderAccessor
        final RequestHeaderFacade headerFacade = mock(RequestHeaderFacade.class);
        when(headerFacade.tryGetRequestHeaders()).thenReturn(Try.success(headers));
        RequestHeaderAccessor.setHeaderFacade(headerFacade);

        // setup AuthTokenAccessor
        final AuthTokenDecoder authTokenDecoder = mock(AuthTokenDecoder.class);
        when(authTokenDecoder.decode(eq(headers))).thenReturn(Try.success(authToken));

        final ScpCfAuthTokenFacade authTokenFacade = spy(new ScpCfAuthTokenFacade(authTokenDecoder));
        AuthTokenAccessor.setAuthTokenFacade(authTokenFacade);

        // perform the actual test
        final AuthToken actualAuthToken = AuthTokenAccessor.getCurrentToken();

        // assert on the result
        assertThat(actualAuthToken).isSameAs(authToken);

        verify(headerFacade, times(1)).tryGetRequestHeaders();
        verify(authTokenDecoder, times(1)).decode(eq(headers));
    }

    @Test
    public void testExecuteWithThrowsExceptionIfCustomFacadeIsUsed()
    {
        final AuthTokenFacade customFacade = mock(AuthTokenFacade.class);
        assertThat(customFacade).isNotInstanceOf(ScpCfAuthTokenFacade.class);

        AuthTokenAccessor.setAuthTokenFacade(customFacade);

        assertThatThrownBy(() -> AuthTokenAccessor.executeWithAuthToken(mock(AuthToken.class), () -> "foo"))
            .isExactlyInstanceOf(ThreadContextExecutionException.class)
            .hasMessageContaining("https://cap.cloud.sap/docs/java/request-contexts#defining-requestcontext");
    }

    @Test
    public void testExecuteWithSucceedsIfSubTypeOfDefaultFacadeIsUsed()
    {
        final AuthTokenFacade customFacade = spy(ScpCfAuthTokenFacade.class);
        assertThat(customFacade).isInstanceOf(ExecutableAuthTokenFacade.class);

        AuthTokenAccessor.setAuthTokenFacade(customFacade);

        assertThat(AuthTokenAccessor.executeWithAuthToken(mock(AuthToken.class), () -> "foo")).isEqualTo("foo");
    }

    @Test
    public void testMissingPropertiesAreComputedFromRequestHeaderOnlyOnce()
    {
        final RequestHeaderContainer headers =
            DefaultRequestHeaderContainer
                .builder()
                .withHeader(HttpHeaders.AUTHORIZATION, "bearer some auth token")
                .build();

        // setup RequestHeaderAccessor
        final RequestHeaderFacade headerFacade = mock(RequestHeaderFacade.class);
        when(headerFacade.tryGetRequestHeaders()).thenReturn(Try.success(headers));
        RequestHeaderAccessor.setHeaderFacade(headerFacade);

        // setup AuthTokenAccessor

        final AuthTokenDecoder authTokenDecoder = mock(AuthTokenDecoder.class);
        final AuthToken authToken = mock(AuthToken.class);
        when(authTokenDecoder.decode(eq(headers))).thenReturn(Try.success(authToken));

        final ScpCfAuthTokenFacade authTokenFacade = spy(new ScpCfAuthTokenFacade(authTokenDecoder));
        AuthTokenAccessor.setAuthTokenFacade(authTokenFacade);

        // spy AuthTokenThreadContextListener
        final AuthTokenThreadContextListener authTokenThreadContextListener = spy(new AuthTokenThreadContextListener());

        ThreadContextExecutor
            .fromNewContext()
            .withoutDefaultListeners()
            .withListeners(authTokenThreadContextListener)
            .withListeners(new RequestHeaderThreadContextListener(headers))
            .execute(() -> {
                // Verify computation happens only once
                verify(headerFacade, times(1)).tryGetRequestHeaders();
                verify(authTokenThreadContextListener, times(1)).afterInitialize(any(ThreadContext.class));

                VavrAssertions.assertThat(AuthTokenAccessor.tryGetCurrentToken()).isSuccess();
                VavrAssertions.assertThat(AuthTokenAccessor.tryGetCurrentToken()).isSuccess();

                verify(headerFacade, times(1)).tryGetRequestHeaders();
            });
    }

}
