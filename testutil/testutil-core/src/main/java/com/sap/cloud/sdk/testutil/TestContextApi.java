package com.sap.cloud.sdk.testutil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface TestContextApi
{
    void setProperty(@Nonnull final String key, @Nullable final Object value );
}
