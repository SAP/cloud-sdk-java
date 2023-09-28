package com.sap.cloud.sdk.cloudplatform.security.principal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.sap.cloud.sdk.cloudplatform.security.principal.exception.PrincipalAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.Property;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutor;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutors;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextExecutionException;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextPropertyNotFoundException;

import io.vavr.control.Try;

public class PrincipalAccessorTest
{
    @Before
    @After
    public void resetAccessor()
    {
        // reset the facade
        PrincipalAccessor.setPrincipalFacade(null);

        // make sure that there is no global fallback between tests
        PrincipalAccessor.setFallbackPrincipal(null);
    }

    @SuppressWarnings( "deprecation" )
    @Test
    public void testGetCurrentPrincipal()
    {
        final Map<String, PrincipalAttribute> attributes = new HashMap<>();
        attributes.put("firstname", new SimplePrincipalAttribute<>("firstname", "Steve"));
        attributes.put("lastname", new SimplePrincipalAttribute<>("lastname", "Jobs"));

        final Principal principal =
            new DefaultPrincipal(
                "user1",
                Sets.newHashSet(new com.sap.cloud.sdk.cloudplatform.security.Role("Admin")),
                attributes);
        PrincipalAccessor.setPrincipalFacade(() -> Try.success(principal));

        final Principal currentUser = PrincipalAccessor.getCurrentPrincipal();

        assertThat(currentUser.getPrincipalId()).isEqualTo("user1");
    }

    private static class TestPrincipal extends DefaultPrincipal
    {
        public TestPrincipal( @Nonnull final String principalId )
        {
            super(principalId, Collections.emptySet(), Collections.emptyMap());
        }
    }

    @Test
    public void testExecute()
    {
        PrincipalAccessor.setPrincipalFacade(new DefaultPrincipalFacade());

        assertThat(PrincipalAccessor.tryGetCurrentPrincipal().getCause())
            .isInstanceOf(ThreadContextAccessException.class);

        PrincipalAccessor.executeWithPrincipal(new TestPrincipal("async"), () -> {
            // no ThreadContext managed
            final Try<Principal> principalTry =
                CompletableFuture.supplyAsync(PrincipalAccessor::tryGetCurrentPrincipal).get();

            assertThat(principalTry.getCause()).isInstanceOf(ThreadContextAccessException.class);
        });

        final Principal principal =
            PrincipalAccessor
                .executeWithPrincipal(
                    new TestPrincipal("async"),
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
    public void testExecuteWithFallback()
    {
        PrincipalAccessor.setPrincipalFacade(new DefaultPrincipalFacade());
        assertThat(PrincipalAccessor.tryGetCurrentPrincipal()).isEmpty();

        // check if fallback is not used if there is already a principal
        PrincipalAccessor.executeWithPrincipal(new TestPrincipal("success"), () -> {
            assertThat(PrincipalAccessor.getCurrentPrincipal().getPrincipalId()).isEqualTo("success");

            PrincipalAccessor.executeWithFallbackPrincipal(() -> new TestPrincipal("fallback"), () -> {
                assertThat(PrincipalAccessor.getCurrentPrincipal().getPrincipalId()).isEqualTo("success");
            });
        });

        // check if fallback is used
        PrincipalAccessor.setPrincipalFacade(new PrincipalAccessorTest.FailingPrincipalFacade());
        assertThat(PrincipalAccessor.tryGetCurrentPrincipal()).isEmpty();

        PrincipalAccessor.executeWithFallbackPrincipal(() -> new TestPrincipal("fallback"), () -> {
            assertThat(PrincipalAccessor.getCurrentPrincipal().getPrincipalId()).isEqualTo("fallback");
        });
    }

    @Test
    public void testExecuteWithException()
    {
        PrincipalAccessor.setPrincipalFacade(new DefaultPrincipalFacade());
        assertThat(PrincipalAccessor.tryGetCurrentPrincipal()).isEmpty();

        assertThatThrownBy(() -> PrincipalAccessor.executeWithPrincipal(new TestPrincipal("principal"), () -> {
            throw new IllegalArgumentException();
        }))
            .isExactlyInstanceOf(ThreadContextExecutionException.class)
            .hasCauseExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testExecuteWithFallbackWithException()
    {
        PrincipalAccessor.setPrincipalFacade(new DefaultPrincipalFacade());
        assertThat(PrincipalAccessor.tryGetCurrentPrincipal()).isEmpty();

        PrincipalAccessor.executeWithPrincipal(new TestPrincipal("principal"), () -> {

            // check if IllegalArgumentException is wrapped
            assertThatThrownBy(
                () -> PrincipalAccessor.executeWithFallbackPrincipal(() -> new TestPrincipal("fallback"), () -> {
                    assertThat(PrincipalAccessor.getCurrentPrincipal().getPrincipalId()).isEqualTo("principal");
                    throw new IllegalArgumentException();
                }))
                .isExactlyInstanceOf(ThreadContextExecutionException.class)
                .hasCauseExactlyInstanceOf(IllegalArgumentException.class);

            // check if ThreadContextExecutionException is not wrapped
            assertThatThrownBy(
                () -> PrincipalAccessor.executeWithFallbackPrincipal(() -> new TestPrincipal("fallback"), () -> {
                    assertThat(PrincipalAccessor.getCurrentPrincipal().getPrincipalId()).isEqualTo("principal");
                    throw new ThreadContextExecutionException();
                })).isExactlyInstanceOf(ThreadContextExecutionException.class).hasNoCause();

        });

        assertThat(PrincipalAccessor.tryGetCurrentPrincipal()).isEmpty();
    }

    @Test
    public void testGlobalFallback()
    {
        PrincipalAccessor.setFallbackPrincipal(() -> new TestPrincipal("globalFallback"));
        assertThat(PrincipalAccessor.getCurrentPrincipal().getPrincipalId()).isEqualTo("globalFallback");

        PrincipalAccessor.setPrincipalFacade(new DefaultPrincipalFacade());
        assertThat(PrincipalAccessor.getCurrentPrincipal().getPrincipalId()).isEqualTo("globalFallback");

        ThreadContextExecutor.fromNewContext().withoutDefaultListeners().execute(() -> {
            assertThat(PrincipalAccessor.getCurrentPrincipal().getPrincipalId()).isEqualTo("globalFallback");
        });

        PrincipalAccessor.executeWithPrincipal(new TestPrincipal("principal"), () -> {
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
    public void testWrongPropertyType()
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
    public void testMissingThreadContext()
    {
        PrincipalAccessor.setPrincipalFacade(new DefaultPrincipalFacade());

        ThreadContextExecutor
            .fromNewContext()
            .withoutDefaultListeners()
            .withListeners(new PrincipalThreadContextListener())
            .execute(() -> {
                assertThat(PrincipalAccessor.tryGetCurrentPrincipal().getCause())
                    .isInstanceOf(PrincipalAccessException.class)
                    .hasMessage("Failed to get current principal.");
            });
    }

    @Test
    public void testMissingThreadContextProperty()
    {
        PrincipalAccessor.setPrincipalFacade(new DefaultPrincipalFacade());

        ThreadContextExecutor.fromNewContext().withoutDefaultListeners().execute(() -> {
            assertThat(PrincipalAccessor.tryGetCurrentPrincipal().getCause())
                .isInstanceOf(ThreadContextPropertyNotFoundException.class)
                .hasMessage("Property '" + PrincipalThreadContextListener.PROPERTY_PRINCIPAL + "' does not exist.");
        });
    }

    @Test
    public void testExecuteWithThrowsExceptionIfCustomFacadeIsUsed()
    {
        final PrincipalFacade customFacade = () -> Try.failure(new IllegalStateException());
        assertThat(customFacade).isNotInstanceOf(DefaultPrincipalFacade.class);

        PrincipalAccessor.setPrincipalFacade(customFacade);

        assertThatThrownBy(() -> PrincipalAccessor.executeWithPrincipal(mock(Principal.class), () -> "foo"))
            .isExactlyInstanceOf(ThreadContextExecutionException.class)
            .hasMessageContaining("https://cap.cloud.sap/docs/java/request-contexts#defining-requestcontext");
    }

    @Test
    public void testExecuteWithSucceedsIfSubTypeOfDefaultFacadeIsUsed()
    {
        final PrincipalFacade customFacade = spy(DefaultPrincipalFacade.class);
        assertThat(customFacade).isInstanceOf(DefaultPrincipalFacade.class);

        PrincipalAccessor.setPrincipalFacade(customFacade);

        assertThat(PrincipalAccessor.executeWithPrincipal(mock(Principal.class), () -> "foo")).isEqualTo("foo");
    }
}
