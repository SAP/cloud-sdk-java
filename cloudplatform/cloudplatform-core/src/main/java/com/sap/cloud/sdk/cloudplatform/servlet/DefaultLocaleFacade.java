/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.servlet;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.net.HttpHeaders;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderAccessor;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderContainer;

/**
 * Facade for accessing {@link Locale} information. By default, the {@link RequestHeaderAccessor} is used to read the
 * "Accept-Languages" HTTP header value(s) explicitly from the incoming request. As fallback the system default
 * {@link Locale} will be chosen.
 */
public class DefaultLocaleFacade implements LocaleFacade
{
    private static final List<Locale> SYSTEM_LOCALES_DEFAULT = Collections.singletonList(Locale.getDefault());
    private static final List<Locale> SYSTEM_LOCALES_AVAILABLE = Arrays.asList(Locale.getAvailableLocales());

    @Override
    @Nonnull
    public List<Locale> getCurrentLocales()
    {
        final List<Locale> localesByHeaders = getLocalesByHeaders();
        if( !localesByHeaders.isEmpty() ) {
            return localesByHeaders;
        }

        return SYSTEM_LOCALES_DEFAULT;
    }

    @Nonnull
    private List<Locale> getLocalesByHeaders()
    {
        return RequestHeaderAccessor
            .tryGetHeaderContainer()
            .getOrElse(RequestHeaderContainer.EMPTY)
            .getHeaderValues(HttpHeaders.ACCEPT_LANGUAGE)
            .stream()
            .map(this::getLocaleByString)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Nullable
    private Locale getLocaleByString( @Nonnull final String acceptLanguage )
    {
        final List<Locale.LanguageRange> ranges = Locale.LanguageRange.parse(acceptLanguage);
        return Locale.lookup(ranges, SYSTEM_LOCALES_AVAILABLE);
    }
}
