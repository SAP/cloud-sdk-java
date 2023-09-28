package com.sap.cloud.sdk.testutil;

import java.lang.reflect.Method;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutor;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextExecutionException;

/**
 * Wraps a test method to have a ThreadContext present.
 */
public class ThreadContextInvocationInterceptor implements InvocationInterceptor
{
    @Override
    public void interceptTestMethod(
        Invocation<Void> invocation,
        ReflectiveInvocationContext<Method> invocationContext,
        ExtensionContext extensionContext )
    {
        ThreadContextExecutor.fromNewContext().execute(() -> {
            try {
                invocation.proceed();
            }
            catch( final ThreadContextExecutionException e ) {
                throw e;
            }
            // CHECKSTYLE:OFF
            catch( final Throwable e ) {
                throw new ThreadContextExecutionException(e);
            }
            // CHECKSTYLE:ON
        });
    }
}
