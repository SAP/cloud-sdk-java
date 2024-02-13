package com.sap.cloud.sdk.testutil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderAccessor;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.sdk.cloudplatform.thread.DefaultThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.Property;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextAccessor;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutor;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextExecutionException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * A JUnit 5 extension that provides a thread context for each test method.
 */
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
public final class TestContext
    implements
    AfterEachCallback,
    InvocationInterceptor,
    TenantContext,
    PrincipalContext,
    AuthTokenContext
{
    private final ThreadContext context = new DefaultThreadContext();

    private final boolean withThreadContext;
    private boolean resetCaches = false;
    private boolean resetFacades = false;

    /**
     * Create a new TestContext that will automatically create a new thread context for each test method. Note:
     * <ul>
     * <li>Setting values <strong>before</strong> test execution will affect all test methods.</li>
     * <li>Setting values <strong>during</strong> a test execution will only affect the reminder of the test
     * method.</li>
     * </ul>
     *
     * @return a new TestContext.
     */
    @Nonnull
    public static TestContext withThreadContext()
    {
        return new TestContext(true);
    }

    /**
     * Also clear any caches registered with the CacheManager.
     * <p>
     * <strong>WARNING:</strong> This method should only be used in tests that are not executed in parallel. Make sure
     * to annotate your test class with {@code @Isolated} when using this, since this might impact other tests running
     * in parallel.
     *
     * @return this context
     */
    @Nonnull
    public TestContext resetCaches()
    {
        resetCaches = true;
        return this;
    }

    /**
     * Also resets the facades for {@link AuthTokenAccessor}, {@link TenantAccessor}, {@link PrincipalAccessor} and
     * {@link RequestHeaderAccessor}.
     * <p>
     * <strong>WARNING:</strong> This method should only be used in tests that are not executed in parallel. Make sure
     * to annotate your test class with {@code @Isolated} when using this, since this might impact other tests running
     * in parallel.
     *
     * @return this context
     */
    @Nonnull
    public TestContext resetFacades()
    {
        resetFacades = true;
        return this;
    }

    @Override
    public void setProperty( @Nonnull final String key, @Nullable final Object value )
    {
        final ThreadContext ctx = ThreadContextAccessor.tryGetCurrentContext().getOrElse(context);
        if( value != null ) {
            ctx.setProperty(key, Property.of(value));
        } else {
            ctx.removeProperty(key);
        }
    }

    @Override
    public void interceptTestMethod(
        @Nonnull final Invocation<Void> invocation,
        @Nonnull final ReflectiveInvocationContext<Method> invocationContext,
        @Nonnull final ExtensionContext extensionContext )
        throws Throwable
    {
        if( !withThreadContext ) {
            invocation.proceed();
            return;
        }
        ThreadContextExecutor.using(context).execute(() -> {
            try {
                invocation.proceed();
            }
            catch( final RuntimeException e ) {
                throw e;
            }
            catch( final Throwable e ) { // ALLOW CATCH THROWABLE
                throw new ThreadContextExecutionException(e);
            }
        });
    }

    @Override
    @SuppressWarnings( "PMD.EmptyCatchBlock" )
    public void afterEach( @Nonnull final ExtensionContext extensionContext )
    {
        if( resetCaches ) {
            try {
                final Class<?> clazz =
                    getClass().getClassLoader().loadClass("com.sap.cloud.sdk.cloudplatform.cache.CacheManager");
                // use reflection to invoke invalidateAll
                final Method method = clazz.getMethod("invalidateAll");
                method.invoke(null);
            }
            catch( final ClassNotFoundException e ) {
                // CacheManager is not available, no need to reset caches
            }
            catch( final NoSuchMethodException | IllegalAccessException | InvocationTargetException e ) {
                throw new ShouldNotHappenException(
                    "You changed the CacheManager but didn't update this code, didn't ya?",
                    e);
            }
        }
        if( resetFacades ) {
            AuthTokenAccessor.setAuthTokenFacade(null);
            AuthTokenAccessor.setFallbackToken(null);

            TenantAccessor.setTenantFacade(null);
            TenantAccessor.setFallbackTenant(null);

            PrincipalAccessor.setPrincipalFacade(null);
            PrincipalAccessor.setFallbackPrincipal(null);

            RequestHeaderAccessor.setHeaderFacade(null);
            RequestHeaderAccessor.setFallbackHeaderContainer(null);
        }
    }
}
