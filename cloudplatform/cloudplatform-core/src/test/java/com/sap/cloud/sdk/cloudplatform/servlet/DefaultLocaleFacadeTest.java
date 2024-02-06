package com.sap.cloud.sdk.cloudplatform.servlet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import com.google.common.net.HttpHeaders;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderAccessor;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderContainer;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderFacade;

import io.vavr.control.Try;

@Isolated
class DefaultLocaleFacadeTest
{
    private static final RequestHeaderFacade NO_HEADER_FACADE = () -> Try.failure(new NoSuchElementException());

    @AfterEach
    void cleanUp()
    {
        RequestHeaderAccessor.setHeaderFacade(null);
    }

    @Test
    void testFallback()
    {
        RequestHeaderAccessor.setHeaderFacade(NO_HEADER_FACADE);

        // test
        final List<Locale> locales = new DefaultLocaleFacade().getCurrentLocales();
        assertThat(locales).containsExactly(Locale.getDefault());
    }

    @Test
    void testOnlyHeaders()
    {
        final String weightedLoc = "fr-CH, fr;q=0.9, en;q=0.8, de;q=0.7, *;q=0.5";
        final RequestHeaderContainer headers = mock(RequestHeaderContainer.class);
        when(headers.getHeaderValues(eq(HttpHeaders.ACCEPT_LANGUAGE)))
            .thenReturn(Collections.singletonList(weightedLoc));

        RequestHeaderAccessor.setHeaderFacade(() -> Try.success(headers));

        // test
        final List<Locale> locales = new DefaultLocaleFacade().getCurrentLocales();
        assertThat(locales).containsExactly(Locale.forLanguageTag("fr-CH"));
    }
}
