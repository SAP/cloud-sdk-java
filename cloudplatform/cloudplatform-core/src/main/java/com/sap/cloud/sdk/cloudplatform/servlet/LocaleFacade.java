package com.sap.cloud.sdk.cloudplatform.servlet;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.annotation.Nonnull;

/**
 * Facade interface to provide access to single and multiple current locales.
 */
@FunctionalInterface
public interface LocaleFacade
{
    /**
     * Returns the requested locale of the current HTTP request, or default locale of the server.
     *
     * @return The locale.
     */
    @Nonnull
    default Locale getCurrentLocale()
    {
        return getCurrentLocales().stream().filter(Objects::nonNull).findFirst().orElseGet(Locale::getDefault);
    }

    /**
     * Returns a list of locales indicating, in decreasing order starting with the preferred locale, the locales that
     * are acceptable to the client based on the Accept-Language header. If the client request doesn't provide an
     * "Accept-Language" header, this method returns a list containing one locale, the default locale of the server.
     *
     * @return A list of locales.
     */
    @Nonnull
    List<Locale> getCurrentLocales();
}
