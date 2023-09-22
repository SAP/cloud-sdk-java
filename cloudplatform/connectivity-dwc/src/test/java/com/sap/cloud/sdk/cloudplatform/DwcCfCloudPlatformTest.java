/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform;

import static com.sap.cloud.sdk.cloudplatform.MegacliteConfigurationLoader.DWC_APPLICATION;
import static com.sap.cloud.sdk.cloudplatform.MegacliteConfigurationLoader.DWC_MEGACLITE_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.net.URI;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;

import io.vavr.control.Option;
import io.vavr.control.Try;

public class DwcCfCloudPlatformTest
{
    private static final String dwcApplication =
        "{\n"
            + "  \"appName\":\"testAppName\",\n"
            + "  \"landscape\":\"testLandscape\",\n"
            + "  \"partition\":\"CF\",\n"
            + "  \"stakeholder\":\"dwc-trial\",\n"
            + "  \"megaclite\":\n"
            + "    {\"url\":\"https://url.from.dwc.application\"},\n"
            + "  \"mTLS\":\n"
            + "    {\"trustedSubaccountIds\":[\"473f4ab0-0b77-4f73-893b-10fd14aa0862\"]},\n"
            + "  \"artifactDeployment\":\n"
            + "    {\"id\":\"017ce217-8cdd-4562-f928-bf41d491e663\"},\n"
            + "  \"artifact\":\n"
            + "    {\"id\":\"017ce217-865a-8b5a-4c15-db78ce9b4148\",\"resource\":\"github.com/SAP/cloudsdk\"}\n"
            + "}";

    private static final String vcapApplication =
        "{\n"
            + "   \"cf_api\": \"https://api.cf.eu10.hana.ondemand.com\",\n"
            + "   \"limits\": {\n"
            + "   \"fds\": 32768,\n"
            + "   \"mem\": 1024,\n"
            + "   \"disk\": 1024\n"
            + "   },\n"
            + "   \"application_name\": \"sample_application\",\n"
            + "   \"application_uris\": [\n"
            + "   \"sample_application.cfapps.eu10.hana.ondemand.com\"\n"
            + "   ],\n"
            + "   \"name\": \"sample_application-01782180-de8b-494c-ea49-d3637edbec01\",\n"
            + "   \"space_name\": \"sample-trial-dev-eu10\",\n"
            + "   \"space_id\": \"caf1f447-6c18-4c47-975c-1228be32844b\",\n"
            + "   \"organization_id\": \"26a0e230-babe-4d90-a79f-a32db3b85473\",\n"
            + "   \"organization_name\": \"sample-origanization-trial-eu10\",\n"
            + "   \"uris\": [\n"
            + "   \"sample_application-MUWG3CIKBB5FS3XL.cert.cfapps.eu10.hana.ondemand.com\"\n"
            + "   ],\n"
            + "   \"users\": null,\n"
            + "   \"process_id\": \"bc150846-972c-4cff-a3db-abb3f9a9d66d\",\n"
            + "   \"process_type\": \"web\",\n"
            + "   \"application_id\": \"c0d7f122-def0-49ab-ae19-f2ec452079e5\",\n"
            + "   \"version\": \"dbdfa058-31e0-4a72-bbab-3c9219fb3b58\",\n"
            + "   \"application_version\": \"dbdfa058-31e0-4a72-bbab-3c9219fb3b58\"\n"
            + "}";

    private DwcCfCloudPlatform platform;

    @Before
    public void before()
    {
        DwcCfCloudPlatform.invalidateCaches();
        platform = spy(new DwcCfCloudPlatform());
        CloudPlatformAccessor.setCloudPlatformFacade(() -> Try.success(platform));
    }

    @AfterClass
    public static void cleanup()
    {
        CloudPlatformAccessor.setCloudPlatformFacade(() -> Try.success(new DwcCfCloudPlatform()));
    }

    @Test
    public void testGtInstanceReturnsInstance()
    {
        assertThat(DwcCfCloudPlatform.getInstanceOrThrow()).isInstanceOf(DwcCfCloudPlatform.class);
    }

    @Test
    public void testGetApplicationProperties()
    {
        when(platform.getEnvironmentVariable("VCAP_APPLICATION")).thenReturn(Option.of(vcapApplication));

        assertThat(platform.getApplicationName()).isEqualTo("sample_application");
        assertThat(platform.getApplicationUrl()).isEqualTo("sample_application.cfapps.eu10.hana.ondemand.com");
        assertThat(platform.getApplicationProcessId()).isEqualTo("bc150846-972c-4cff-a3db-abb3f9a9d66d");
    }

    @Test
    public void testGetDefaultOutboundProxyBinding()
    {
        when(platform.getEnvironmentVariable(DWC_APPLICATION)).thenReturn(Option.of(dwcApplication));

        final DwcOutboundProxyBinding expectedBinding =
            DwcOutboundProxyBinding
                .builder()
                .name("megaclite")
                .uri(URI.create("https://url.from.dwc.application"))
                .build();

        assertThat(platform.getOutboundProxyBinding().get()).isEqualTo(expectedBinding);
    }

    @Test
    public void testGetLegacyOutboundProxyBinding()
    {
        when(platform.getEnvironmentVariable(DWC_MEGACLITE_URL)).thenReturn(Option.of("https://uri.from.legacy.com/"));

        final DwcOutboundProxyBinding expectedBinding =
            DwcOutboundProxyBinding.builder().name("megaclite").uri(URI.create("https://uri.from.legacy.com/")).build();

        assertThat(platform.getOutboundProxyBinding().get()).isEqualTo(expectedBinding);
    }

    @Test
    public void testGetOutboundProxyBindingWithBrokenLegacyUri()
    {
        when(platform.getEnvironmentVariable(DWC_MEGACLITE_URL)).thenReturn(Option.of("this is not a valid URI"));

        assertThatThrownBy(platform::getOutboundProxyBindingOrThrow).isInstanceOf(CloudPlatformException.class);
    }

    @Test
    public void testGetOutboundProxyBindingThrowsCloudPlatformException()
    {
        when(platform.getEnvironmentVariable("VCAP_SERVICES")).thenReturn(Option.none());
        when(platform.getEnvironmentVariable(DWC_MEGACLITE_URL)).thenReturn(Option.none());

        assertThatThrownBy(platform::getOutboundProxyBindingOrThrow).isExactlyInstanceOf(CloudPlatformException.class);
        assertThatThrownBy(() -> platform.getOutboundProxyBindingOrThrow("megaclite-binding"))
            .isExactlyInstanceOf(CloudPlatformException.class);

        assertThat(platform.getOutboundProxyBinding().isEmpty()).isTrue();
    }

    @Test
    public void testGetEnvironmentVariable()
    {
        final ImmutableMap<String, String> customEnvironmentVar = ImmutableMap.of("foo", "bar");

        final DwcCfCloudPlatformFacade facade = new DwcCfCloudPlatformFacade();
        final CloudPlatform platform1 = facade.tryGetCloudPlatform().get();
        platform1.setEnvironmentVariableReader(customEnvironmentVar::get);

        final DwcCfCloudPlatform platform2 = (DwcCfCloudPlatform) facade.tryGetCloudPlatform().get();

        assertThat(platform1).isEqualTo(platform2);
        assertThat(platform1 == platform2).isTrue();
        assertThat(platform2.getEnvironmentVariable("foo")).isEqualTo(Option.of("bar"));
    }

    @Test
    public void testProxyBindingWithoutVcapServices()
    {
        assertThat(platform.getOutboundProxyBinding().isEmpty()).isTrue();
    }

    @Test
    public void testCustomProxyBinding()
    {
        when(platform.getEnvironmentVariable("DWC_MEGACLITE_URL")).thenReturn(Option.of("https://uri.from.env.var/"));

        assertThat(platform.getOutboundProxyBindingOrThrow().getUri()).hasToString("https://uri.from.env.var/");

        final URI customUri = URI.create("http://localhost:1234");
        platform.setOutboundProxyBinding(DwcOutboundProxyBinding.builder().uri(customUri).build());

        assertThat(platform.getOutboundProxyBindingOrThrow().getUri()).isEqualTo(customUri);
    }
}
