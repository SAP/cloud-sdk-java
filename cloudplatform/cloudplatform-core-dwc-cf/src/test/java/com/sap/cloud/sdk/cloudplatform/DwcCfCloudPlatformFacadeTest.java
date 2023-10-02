package com.sap.cloud.sdk.cloudplatform;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class DwcCfCloudPlatformFacadeTest
{
    @Test
    public void testFacadeIsPickedUpAutomatically()
    {
        assertThat(CloudPlatformAccessor.getCloudPlatformFacade()).isInstanceOf(DwcCfCloudPlatformFacade.class);
    }
}
