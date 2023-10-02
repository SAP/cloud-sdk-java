/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.testutil;

import java.util.Locale;

import javax.annotation.Nonnull;

interface LocaleMocker
{
    /**
     * Mocks the current {@link Locale} with {@link Locale#US}.
     */
    void mockCurrentLocales();

    /**
     * Mocks the current {@link Locale}s with the given values.
     */
    void mockCurrentLocales( @Nonnull final Locale locale, @Nonnull final Locale... additionalLocales );
}
