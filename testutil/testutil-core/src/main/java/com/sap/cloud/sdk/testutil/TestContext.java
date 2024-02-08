package com.sap.cloud.sdk.testutil;

import java.lang.reflect.Method;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import com.sap.cloud.sdk.cloudplatform.cache.CacheManager;
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
    private final boolean resetCaches = true;
    private final boolean resetFacades = true;

    public static TestContext withThreadContext()
    {
        return new TestContext(true);
    }

    public static TestContext withoutThreadContext()
    {
        return new TestContext(false);
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
            catch( Throwable e ) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void afterEach( final ExtensionContext extensionContext )
    {
        if( resetCaches ) {
            CacheManager.invalidateAll();
        }
        if( resetFacades ) {
            AuthTokenAccessor.setAuthTokenFacade(null);
            AuthTokenAccessor.setFallbackToken(null);

            TenantAccessor.setTenantFacade(null);
            TenantAccessor.setFallbackTenant(null);

            PrincipalAccessor.setPrincipalFacade(null);
            PrincipalAccessor.setFallbackPrincipal(null);
        }
    }
}
