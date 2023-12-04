/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;

@SuppressWarnings( "unchecked" )
class DwcConfigurationTest
{
    private Function<String, String> envVarMock;
    private DwcConfiguration sut;

    @BeforeEach
    void setup()
    {
        envVarMock = mock(Function.class);
        sut = new DwcConfiguration(envVarMock);
    }

    @Test
    void testConfigIsLazyLoaded()
    {
        final String input =
            "{\"megaclite\": {\"url\": \"my.megaclite.sap\"}, \"orbitProviderTenantId\": \"some tenant id\"}";
        doReturn(input).when(envVarMock).apply(eq("DWC_APPLICATION"));

        assertThat(sut.megacliteUrl()).hasToString("my.megaclite.sap");
        assertThat(sut.providerTenant()).hasToString("some tenant id");

        // additional invocations should be served from cache but separately for url and tenant
        sut.megacliteUrl();
        sut.providerTenant();

        verify(envVarMock, times(2)).apply(eq("DWC_APPLICATION"));
    }

    @Test
    void testReadMalformedMegacliteUrlFromDwcApplication()
    {
        final String input = "{\"megaclite\": {\"url\": \"this is not an url\"}}";
        doReturn(input).when(envVarMock).apply(eq("DWC_APPLICATION"));

        assertThatThrownBy(sut::megacliteUrl).isExactlyInstanceOf(CloudPlatformException.class);
        verify(envVarMock, times(1)).apply(eq("DWC_APPLICATION"));
    }

    @Test
    void testMissingMegacliteEntryInDwcApplication()
    {
        final String input = "{}";
        doReturn(input).when(envVarMock).apply(eq("DWC_APPLICATION"));

        assertThatThrownBy(sut::megacliteUrl).isExactlyInstanceOf(CloudPlatformException.class);
        verify(envVarMock, times(1)).apply(eq("DWC_APPLICATION"));
    }

    @Test
    void testMissingUrlEntryInDwcApplication()
    {
        final String input = "{\"megaclite\": {}}";
        doReturn(input).when(envVarMock).apply(eq("DWC_APPLICATION"));

        assertThatThrownBy(sut::megacliteUrl).isExactlyInstanceOf(CloudPlatformException.class);
        verify(envVarMock, times(1)).apply(eq("DWC_APPLICATION"));
    }

    @Test
    void testLoadMegacliteUrlWithoutDwcApplicationEnvironmentVariable()
    {
        doReturn(null).when(envVarMock).apply(eq("DWC_APPLICATION"));

        assertThatThrownBy(sut::megacliteUrl).isExactlyInstanceOf(CloudPlatformException.class);
        verify(envVarMock, times(1)).apply(eq("DWC_APPLICATION"));
    }

    @Test
    void testLoadProviderTenantIdWithoutDwcApplicationEnvironmentVariable()
    {
        doReturn(null).when(envVarMock).apply(eq("DWC_APPLICATION"));

        assertThatThrownBy(sut::providerTenant).isExactlyInstanceOf(CloudPlatformException.class);
        verify(envVarMock, times(1)).apply(eq("DWC_APPLICATION"));
    }

    @Test
    void testLoadProviderTenantIdWithMalformedDwcApplication()
    {
        final String input = "{\"orbitProviderTenantId\": {\"nestedProperty\": \"value\"}}";
        doReturn(input).when(envVarMock).apply(eq("DWC_APPLICATION"));

        assertThatThrownBy(sut::providerTenant).isExactlyInstanceOf(CloudPlatformException.class);
        verify(envVarMock, times(1)).apply(eq("DWC_APPLICATION"));
    }
}
