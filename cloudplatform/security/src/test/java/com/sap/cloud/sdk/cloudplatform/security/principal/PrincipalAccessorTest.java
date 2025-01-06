/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security.principal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.security.principal.exception.PrincipalAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.Property;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutor;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutors;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextExecutionException;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextPropertyNotFoundException;

import io.vavr.control.Try;

class PrincipalAccessorTest
{
    @BeforeEach
    @AfterEach
    void resetAccessor()
    {
        // reset the facade
        PrincipalAccessor.setPrincipalFacade(null);

        // make sure that there is no global fallback between tests
        PrincipalAccessor.setFallbackPrincipal(null);
    }

    @Test
    void testGetCurrentPrincipal()
    {
        final Principal principal = new DefaultPrincipal("user1");
        PrincipalAccessor.setPrincipalFacade(() -> Try.success(principal));

        final Principal currentUser = PrincipalAccessor.getCurrentPrincipal();

        assertThat(currentUser.getPrincipalId()).isEqualTo("user1");
    }

    @Test
    void testExecute()
    {
        PrincipalAccessor.setPrincipalFacade(new DefaultPrincipalFacade());

        assertThat(PrincipalAccessor.tryGetCurrentPrincipal().getCause()).isInstanceOf(PrincipalAccessException.class);

        PrincipalAccessor.executeWithPrincipal(new DefaultPrincipal("async"), () -> {
            // no ThreadContext managed
            final Try<Principal> principalTry =
                CompletableFuture.supplyAsync(PrincipalAccessor::tryGetCurrentPrincipal).get();

            assertThat(principalTry.getCause()).isInstanceOf(PrincipalAccessException.class);
            assertThat(principalTry.getCause().getSuppressed())
                .anyMatch(e -> e instanceof ThreadContextAccessException);
        });

        final Principal principal =
            PrincipalAccessor
                .executeWithPrincipal(
                    new DefaultPrincipal("async"),
                    () -> ThreadContextExecutors.submit(PrincipalAccessor::getCurrentPrincipal).get());

        assertThat(principal.getPrincipalId()).isEqualTo("async");
    }

    private static class FailingPrincipalFacade extends DefaultPrincipalFacade
    {
        @Nonnull
        @Override
        public Try<Principal> tryGetCurrentPrincipal()
        {
            return super.tryGetCurrentPrincipal().orElse(Try.failure(new PrincipalAccessException()));
        }
    }

    @Test
    void testExecuteWithFallback()
    {
        PrincipalAccessor.setPrincipalFacade(new DefaultPrincipalFacade());
        assertThat(PrincipalAccessor.tryGetCurrentPrincipal()).isEmpty();

        // check if fallback is not used if there is already a principal
        PrincipalAccessor.executeWithPrincipal(new DefaultPrincipal("success"), () -> {
            assertThat(PrincipalAccessor.getCurrentPrincipal().getPrincipalId()).isEqualTo("success");

            PrincipalAccessor.executeWithFallbackPrincipal(() -> new DefaultPrincipal("fallback"), () -> {
                assertThat(PrincipalAccessor.getCurrentPrincipal().getPrincipalId()).isEqualTo("success");
            });
        });

        // check if fallback is used
        PrincipalAccessor.setPrincipalFacade(new PrincipalAccessorTest.FailingPrincipalFacade());
        assertThat(PrincipalAccessor.tryGetCurrentPrincipal()).isEmpty();

        PrincipalAccessor.executeWithFallbackPrincipal(() -> new DefaultPrincipal("fallback"), () -> {
            assertThat(PrincipalAccessor.getCurrentPrincipal().getPrincipalId()).isEqualTo("fallback");
        });
    }

    @Test
    void testExecuteWithException()
    {
        PrincipalAccessor.setPrincipalFacade(new DefaultPrincipalFacade());
        assertThat(PrincipalAccessor.tryGetCurrentPrincipal()).isEmpty();

        assertThatThrownBy(() -> PrincipalAccessor.executeWithPrincipal(new DefaultPrincipal("principal"), () -> {
            throw new IllegalArgumentException();
        }))
            .isExactlyInstanceOf(ThreadContextExecutionException.class)
            .hasCauseExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testExecuteWithFallbackWithException()
    {
        PrincipalAccessor.setPrincipalFacade(new DefaultPrincipalFacade());
        assertThat(PrincipalAccessor.tryGetCurrentPrincipal()).isEmpty();

        PrincipalAccessor.executeWithPrincipal(new DefaultPrincipal("principal"), () -> {

            // check if IllegalArgumentException is wrapped
            assertThatThrownBy(
                () -> PrincipalAccessor.executeWithFallbackPrincipal(() -> new DefaultPrincipal("fallback"), () -> {
                    assertThat(PrincipalAccessor.getCurrentPrincipal().getPrincipalId()).isEqualTo("principal");
                    throw new IllegalArgumentException();
                }))
                .isExactlyInstanceOf(ThreadContextExecutionException.class)
                .hasCauseExactlyInstanceOf(IllegalArgumentException.class);

            // check if ThreadContextExecutionException is not wrapped
            assertThatThrownBy(
                () -> PrincipalAccessor.executeWithFallbackPrincipal(() -> new DefaultPrincipal("fallback"), () -> {
                    assertThat(PrincipalAccessor.getCurrentPrincipal().getPrincipalId()).isEqualTo("principal");
                    throw new ThreadContextExecutionException();
                })).isExactlyInstanceOf(ThreadContextExecutionException.class).hasNoCause();

        });

        assertThat(PrincipalAccessor.tryGetCurrentPrincipal()).isEmpty();
    }

    @Test
    void testGlobalFallback()
    {
        PrincipalAccessor.setFallbackPrincipal(() -> new DefaultPrincipal("globalFallback"));
        assertThat(PrincipalAccessor.getCurrentPrincipal().getPrincipalId()).isEqualTo("globalFallback");

        PrincipalAccessor.setPrincipalFacade(new DefaultPrincipalFacade());
        assertThat(PrincipalAccessor.getCurrentPrincipal().getPrincipalId()).isEqualTo("globalFallback");

        ThreadContextExecutor.fromNewContext().withoutDefaultListeners().execute(() -> {
            assertThat(PrincipalAccessor.getCurrentPrincipal().getPrincipalId()).isEqualTo("globalFallback");
        });

        PrincipalAccessor.executeWithPrincipal(new DefaultPrincipal("principal"), () -> {
            assertThat(PrincipalAccessor.getCurrentPrincipal().getPrincipalId()).isEqualTo("principal");
        });
    }

    private static class BrokenPrincipalThreadContextListener extends PrincipalThreadContextListener
    {
        @Override
        public void beforeInitialize( @Nonnull final ThreadContext threadContext )
        {
            threadContext
                .setPropertyIfAbsent(
                    PrincipalThreadContextListener.PROPERTY_PRINCIPAL,
                    Property.of("this-is-not-a-principal-object"));
        }
    }

    @Test
    void testWrongPropertyType()
    {
        PrincipalAccessor.setPrincipalFacade(new DefaultPrincipalFacade());

        ThreadContextExecutor
            .fromNewContext()
            .withoutDefaultListeners()
            .withListeners(new BrokenPrincipalThreadContextListener())
            .execute(() -> {
                assertThatThrownBy(PrincipalAccessor::getCurrentPrincipal)
                    .isExactlyInstanceOf(ClassCastException.class);
            });
    }

    @Test
    void testMissingThreadContext()
    {
        PrincipalAccessor.setPrincipalFacade(new DefaultPrincipalFacade());

        ThreadContextExecutor
            .fromNewContext()
            .withoutDefaultListeners()
            .withListeners(new PrincipalThreadContextListener())
            .execute(() -> {
                assertThat(PrincipalAccessor.tryGetCurrentPrincipal().getCause())
                    .isInstanceOf(PrincipalAccessException.class)
                    .hasMessage("Could not read a principal from thread context, JWT, nor Basic Auth header.");
            });
    }

    @Test
    void testMissingThreadContextProperty()
    {
        PrincipalAccessor.setPrincipalFacade(new DefaultPrincipalFacade());

        ThreadContextExecutor.fromNewContext().withoutDefaultListeners().execute(() -> {
            final Throwable principalTryCause = PrincipalAccessor.tryGetCurrentPrincipal().getCause();

            assertThat(principalTryCause)
                .isInstanceOf(PrincipalAccessException.class)
                .hasMessage("Could not read a principal from thread context, JWT, nor Basic Auth header.");

            final Predicate<Throwable> expectedSuppressedException =
                e -> e instanceof ThreadContextPropertyNotFoundException
                    && e
                        .getMessage()
                        .equals("Property '" + PrincipalThreadContextListener.PROPERTY_PRINCIPAL + "' does not exist.");

            assertThat(principalTryCause.getSuppressed()).anyMatch(expectedSuppressedException);
        });
    }

    @Test
    void testExecuteWithThrowsExceptionIfCustomFacadeIsUsed()
    {
        final PrincipalFacade customFacade = () -> Try.failure(new IllegalStateException());
        assertThat(customFacade).isNotInstanceOf(DefaultPrincipalFacade.class);

        PrincipalAccessor.setPrincipalFacade(customFacade);

        assertThatThrownBy(() -> PrincipalAccessor.executeWithPrincipal(mock(Principal.class), () -> "foo"))
            .isExactlyInstanceOf(ThreadContextExecutionException.class)
            .hasMessageContaining("https://cap.cloud.sap/docs/java/request-contexts#defining-requestcontext");
    }

    @Test
    void testExecuteWithSucceedsIfSubTypeOfDefaultFacadeIsUsed()
    {
        final PrincipalFacade customFacade = spy(DefaultPrincipalFacade.class);
        assertThat(customFacade).isInstanceOf(DefaultPrincipalFacade.class);

        PrincipalAccessor.setPrincipalFacade(customFacade);

        assertThat(PrincipalAccessor.executeWithPrincipal(mock(Principal.class), () -> "foo")).isEqualTo("foo");
    }
}
