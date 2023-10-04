/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.servlet;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Accessor for retrieving the current {@link Locale}.
 */
@NoArgsConstructor( access = AccessLevel.PRIVATE )
public final class LocaleAccessor
{
    /**
     * The {@link LocaleFacade} instance.
     */
    @Getter
    @Nonnull
    private static LocaleFacade localeFacade = new DefaultLocaleFacade();

    /**
     * Replaces the default {@link LocaleFacade} instance.
     *
     * @param localeFacade
     *            An instance of {@link LocaleFacade}. Use {@code null} to reset the facade.
     */
    public static void setLocaleFacade( @Nullable final LocaleFacade localeFacade )
    {
        if( localeFacade == null ) {
            LocaleAccessor.localeFacade = new DefaultLocaleFacade();
        } else {
            LocaleAccessor.localeFacade = localeFacade;
        }
    }

    /**
     * Returns the requested locale of the current HTTP request, or default locale of the server.
     *
     * @return The locale.
     */
    @Nonnull
    public static Locale getCurrentLocale()
    {
        return localeFacade.getCurrentLocale();
    }

    /**
     * Returns a list of locales indicating, in decreasing order starting with the preferred locale, the locales that
     * are acceptable to the client based on the Accept-Language header. If the client request doesn't provide an
     * "Accept-Language" header, this method returns a list containing one locale, the default locale of the server.
     *
     * @return A list of locales.
     */
    @Nonnull
    public static List<Locale> getCurrentLocales()
    {
        return localeFacade.getCurrentLocales();
    }
}
