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

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestContext
    implements
    AfterEachCallback,
    InvocationInterceptor,
    TenantContextApi,
    PrincipalContextApi,
    AuthTokenContextApi
{
    private final ThreadContext context = new DefaultThreadContext();

    private final boolean withThreadContext;
    private boolean resetCaches = false;
    private boolean resetFacades = false;

    /**
     * Create a new TestContext that will automatically create a new thread context for each test method.
     *
     * @return a new TestContext.
     */
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

    @SuppressWarnings( { "ProhibitedExceptionThrown", "checkstyle:IllegalCatch" } )
    @Override
    public void interceptTestMethod(
        Invocation<Void> invocation,
        ReflectiveInvocationContext<Method> invocationContext,
        ExtensionContext extensionContext )
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
            catch( final Throwable e ) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void afterEach( final ExtensionContext extensionContext )
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
