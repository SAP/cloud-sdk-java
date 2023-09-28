package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.net.URI;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sap.cloud.sdk.cloudplatform.CloudPlatform;
import com.sap.cloud.sdk.cloudplatform.CloudPlatformAccessor;

import io.vavr.control.Try;

public class MegacliteDestinationFactoryTest
{
    private static final URI megacliteUrl = URI.create("https://megaclite.com");
    private static final DwcConfiguration dwcConfig = new DwcConfiguration(megacliteUrl, "id");

    @BeforeClass
    public static void prepareCloudPlatform()
    {
        final CloudPlatform mock = mock(CloudPlatform.class);

        CloudPlatformAccessor.setCloudPlatformFacade(() -> Try.success(mock));
    }

    @AfterClass
    public static void resetCloudPlatform()
    {
        CloudPlatformAccessor.setCloudPlatformFacade(null);
    }

    @Test
    public void testMegacliteDestination()
    {
        final MegacliteDestinationFactory sut = new MegacliteDestinationFactory(dwcConfig);

        final HttpDestination destination = sut.getMegacliteDestination("/foo");

        assertThat(destination.getUri()).hasToString(megacliteUrl + "/foo");
        assertThat(destination.getSecurityConfigurationStrategy())
            .isEqualTo(SecurityConfigurationStrategy.FROM_PLATFORM);
    }
}
