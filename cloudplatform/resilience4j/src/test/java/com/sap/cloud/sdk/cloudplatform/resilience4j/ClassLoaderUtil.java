package com.sap.cloud.sdk.cloudplatform.resilience4j;

import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;

class ClassLoaderUtil
{
    static void runWithSeparateClassLoader( @Nonnull final Runnable handler )
        throws Exception
    {
        runWithClassLoader(handler, Thread.currentThread().getContextClassLoader());
    }

    static void runWithEmptyClassLoader( @Nonnull final Runnable handler )
        throws Exception
    {
        runWithClassLoader(handler, new ClassLoader(null)
        {
        });
    }

    private static void runWithClassLoader( @Nonnull final Runnable handler, @Nonnull final ClassLoader classLoader )
        throws Exception
    {
        final AtomicReference<Exception> exceptionReference = new AtomicReference<>();
        final Thread t = new Thread(handler);
        t.setContextClassLoader(classLoader);
        t.setUncaughtExceptionHandler(( thread, throwable ) -> exceptionReference.set((Exception) throwable));
        t.start();
        try {
            t.join();
        }
        catch( final InterruptedException e ) {
            throw new ShouldNotHappenException(e);
        }
        final Exception uncaughtException = exceptionReference.get();
        if( uncaughtException != null ) {
            throw uncaughtException;
        }
    }
}
