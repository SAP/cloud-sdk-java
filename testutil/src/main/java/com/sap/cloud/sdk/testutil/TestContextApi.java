package com.sap.cloud.sdk.testutil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Baseline interface for the {@code TestContext} interfaces.
 */
public interface TestContextApi
{
    /**
     * Set a property in the thread context.
     *
     * @param key
     *            the key of the property
     * @param value
     *            the value of the property. If {@code null}, the property will be removed.
     */
    void setProperty( @Nonnull final String key, @Nullable final Object value );
}
