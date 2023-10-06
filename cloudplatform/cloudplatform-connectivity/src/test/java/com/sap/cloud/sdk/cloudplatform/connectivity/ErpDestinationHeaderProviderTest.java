/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Collections;
import java.util.Locale;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.servlet.LocaleAccessor;

class ErpDestinationHeaderProviderTest
{
    private static final URI testUri = URI.create("https://sap.com");

    @BeforeAll
    static void prepareLocaleAccessor()
    {
        LocaleAccessor.setLocaleFacade(() -> Collections.singletonList(Locale.ITALIAN));
    }

    @AfterAll
    static void resetLocaleAccessor()
    {
        LocaleAccessor.setLocaleFacade(null);
    }

    @Test
    void testDefaultSapHeadersAreEmpty()
    {
        final DefaultHttpDestination destinationToTest = DefaultHttpDestination.builder(testUri).build();

        // sanity check
        assertThat(destinationToTest.get(DestinationProperty.SAP_CLIENT)).isEmpty();
        assertThat(destinationToTest.get(DestinationProperty.SAP_LANGUAGE)).isEmpty();

        assertThat(destinationToTest.getHeaders(testUri)).isEmpty();
    }

    @Test
    void testClientAndLanguageAreGiven()
    {
        final String testClient = "123";
        final String testLanguage = Locale.ITALIAN.getLanguage();

        final DefaultHttpDestination destinationToTest =
            DefaultHttpDestination
                .builder(testUri)
                .property(DestinationProperty.SAP_CLIENT, testClient)
                .property(DestinationProperty.SAP_LANGUAGE, testLanguage)
                .build();

        // sanity check
        assertThat(destinationToTest.get(DestinationProperty.SAP_CLIENT)).contains(testClient);
        assertThat(destinationToTest.get(DestinationProperty.SAP_LANGUAGE)).contains(testLanguage);

        final Header expectedClientHeader = new Header("sap-client", testClient);
        final Header expectedLanguageHeader = new Header("sap-language", testLanguage);

        assertThat(destinationToTest.getHeaders(testUri)).containsOnly(expectedClientHeader, expectedLanguageHeader);
    }

    @Test
    void testLanguageIsDerived()
    {
        final DefaultHttpDestination destinationToTest =
            DefaultHttpDestination
                .builder(testUri)
                .property(DestinationProperty.SAP_LANGUAGE, Locale.FRENCH.getLanguage())
                .property(DestinationProperty.DYNAMIC_SAP_LANGUAGE, true)
                .build();

        final Header expectedLanguageHeader = new Header("sap-language", Locale.ITALIAN.getLanguage());

        assertThat(destinationToTest.getHeaders(testUri)).containsOnly(expectedLanguageHeader);
    }
}
