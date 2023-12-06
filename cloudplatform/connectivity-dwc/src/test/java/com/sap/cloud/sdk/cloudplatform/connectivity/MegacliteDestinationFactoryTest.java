/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.junit.jupiter.api.Test;

class MegacliteDestinationFactoryTest
{
    private static final URI megacliteUrl = URI.create("https://megaclite.com");
    private static final DwcConfiguration dwcConfig = new DwcConfiguration(megacliteUrl, "id");

    @Test
    void testMegacliteDestination()
    {
        final MegacliteDestinationFactory sut = new MegacliteDestinationFactory(dwcConfig);

        final HttpDestination destination = sut.getMegacliteDestination("/foo");

        assertThat(destination.getUri()).hasToString(megacliteUrl + "/foo");
        assertThat(destination.getSecurityConfigurationStrategy())
            .isEqualTo(SecurityConfigurationStrategy.FROM_PLATFORM);
    }
}
