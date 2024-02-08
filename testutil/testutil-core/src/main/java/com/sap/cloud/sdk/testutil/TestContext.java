package com.sap.cloud.sdk.testutil;

import com.sap.cloud.sdk.cloudplatform.cache.CacheManager;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.security.principal.DefaultPrincipal;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAccessor;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalThreadContextListener;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantThreadContextListener;
import com.sap.cloud.sdk.cloudplatform.thread.DefaultThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.Property;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextAccessor;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import java.lang.reflect.Method;

@RequiredArgsConstructor
public class TestContext implements AfterEachCallback, InvocationInterceptor
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

    public void setTenant(String tenant) {
        setProperty(TenantThreadContextListener.PROPERTY_TENANT, new DefaultTenant(tenant));
    }
    public void setPrincipal(String principal) {
        setProperty(PrincipalThreadContextListener.PROPERTY_PRINCIPAL, new DefaultPrincipal(principal));
    }

    private void setProperty(String key, Object value) {
        final ThreadContext ctx = ThreadContextAccessor.tryGetCurrentContext()
                .getOrElse(context);
        if (value != null) {
            ctx.setProperty(key, Property.of(value));
        }
        else {
            ctx.removeProperty(key);
        }
    }

    @Override
    public void interceptTestMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
        if (!withThreadContext) {
            invocation.proceed();
            return;
        }
        ThreadContextExecutor.using(context)
                .execute(() -> {
                    try {
                        invocation.proceed();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public void afterEach(final ExtensionContext extensionContext) {
        if (resetCaches){
            CacheManager.invalidateAll();
        }
        if (resetFacades){
            AuthTokenAccessor.setAuthTokenFacade(null);
            TenantAccessor.setTenantFacade(null);
            PrincipalAccessor.setPrincipalFacade(null);
        }
    }
}
