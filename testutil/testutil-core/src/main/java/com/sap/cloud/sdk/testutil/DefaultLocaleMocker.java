package com.sap.cloud.sdk.testutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.servlet.LocaleFacade;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
class DefaultLocaleMocker implements LocaleMocker
{
    private final Supplier<LocaleFacade> resetLocaleFacade;

    @Getter( AccessLevel.PACKAGE )
    @Nullable
    private Locale currentLocale;

    @Getter( AccessLevel.PACKAGE )
    @Nonnull
    private final List<Locale> additionalLocales = new ArrayList<>();

    @Override
    public void mockCurrentLocales()
    {
        mockCurrentLocales(Locale.US);
    }

    @Override
    public void mockCurrentLocales( @Nonnull final Locale currentLocale, @Nonnull final Locale... additionalLocales )
    {
        resetLocaleFacade.get();

        this.currentLocale = currentLocale;
        this.additionalLocales.addAll(Arrays.asList(additionalLocales));
    }
}
