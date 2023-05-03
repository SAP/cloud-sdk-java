package com.sap.cloud.sdk.testutil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import javax.annotation.Nonnull;

/**
 * Utility class to formulate test assertions on {@link Throwable} instances.
 */
public class ThrowableAssertionUtil
{
    /**
     * Asserts that the given {@link Throwable} has suppressed exceptions of the given types. The exception types are
     * checked in the given order.
     *
     * @param throwable
     *            The {@link Throwable} that shall have the suppressed exceptions
     * @param expectedSuppressedExceptionTypes
     *            The types which the suppressed exceptions shall belong to
     */
    @SafeVarargs
    @SuppressWarnings( "varargs" )
    public static void assertHasSuppressedExceptionTypes(
        @Nonnull final Throwable throwable,
        @Nonnull final Class<? extends Throwable>... expectedSuppressedExceptionTypes )
    {
        final Throwable[] actualSuppressedExceptions = throwable.getSuppressed();

        assertThat(actualSuppressedExceptions).hasSameSizeAs(expectedSuppressedExceptionTypes);

        for( int i = 0; i < expectedSuppressedExceptionTypes.length; i++ ) {
            final Class<? extends Throwable> expectedClass = expectedSuppressedExceptionTypes[i];
            final Throwable actual = actualSuppressedExceptions[i];

            if( !expectedClass.isAssignableFrom(actual.getClass()) ) {
                fail(
                    "Expected exception class "
                        + expectedClass.getSimpleName()
                        + ", but found "
                        + actual.getClass().getSimpleName()
                        + ".");
            }
        }
    }
}
