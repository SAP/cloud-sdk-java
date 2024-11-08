/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination.builder;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ProxyType.INTERNET;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ProxyType.ON_PREMISE;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.InstanceOfAssertFactories.type;

import javax.annotation.Nonnull;

import org.apache.hc.client5.http.config.Configurable;
import org.junit.jupiter.api.Test;

class ApacheHttpClient5FactoryBuilderTest
{

    @Test
    void testBuilderContainsOptionalParametersOnly()
    {
        // make sure we can build a new factory instance without supplying any parameters
        assertThatNoException().isThrownBy(() -> new ApacheHttpClient5FactoryBuilder().build());
    }

    @Test
    void testTlsUpgradeToggle()
    {
        var service = "https://servive";
        var proxy = ProxyConfiguration.of("https://proxy");

        var destInternet = builder(service).trustAllCertificates().build();
        var destOnPremise = builder(service).proxyType(ON_PREMISE).proxyConfiguration(proxy).buildInternal();
        var destProxy = builder(service).trustAllCertificates().proxyType(INTERNET).proxyConfiguration(proxy).build();
        var destTlsVersion = builder(service).trustAllCertificates().tlsVersion("TLSv1.1").build();

        var sut = new ApacheHttpClient5FactoryBuilder().build();

        // force upgrade=true
        ApacheHttpClient5FactoryBuilder.ENABLE_TLS_UPGRADE = true;
        assertProtocolUpgradeEnabled(sut, destInternet);
        assertProtocolUpgradeEnabled(sut, destOnPremise);
        assertProtocolUpgradeEnabled(sut, destProxy);
        assertProtocolUpgradeEnabled(sut, destTlsVersion);

        // force upgrade=false
        ApacheHttpClient5FactoryBuilder.ENABLE_TLS_UPGRADE = false;
        assertProtocolUpgradeDisabled(sut, destInternet);
        assertProtocolUpgradeDisabled(sut, destOnPremise);
        assertProtocolUpgradeDisabled(sut, destProxy);
        assertProtocolUpgradeDisabled(sut, destTlsVersion);

        // default
        ApacheHttpClient5FactoryBuilder.ENABLE_TLS_UPGRADE = null;
        assertProtocolUpgradeEnabled(sut, destInternet);
        assertProtocolUpgradeDisabled(sut, destOnPremise);
        assertProtocolUpgradeEnabled(sut, destProxy);
        assertProtocolUpgradeDisabled(sut, destTlsVersion);
    }

    private void assertProtocolUpgradeEnabled(
        @Nonnull final ApacheHttpClient5Factory factory,
        @Nonnull final DefaultHttpDestination dest )
    {
        assertThat(factory.createHttpClient(dest))
            .isNotNull()
            .extracting("httpClient", as(type(Configurable.class)))
            .satisfies(client -> assertThat(client.getConfig().isProtocolUpgradeEnabled()).isTrue());
    }

    private void assertProtocolUpgradeDisabled(
        @Nonnull final ApacheHttpClient5Factory factory,
        @Nonnull final DefaultHttpDestination dest )
    {
        assertThat(factory.createHttpClient(dest))
            .isNotNull()
            .extracting("httpClient", as(type(Configurable.class)))
            .satisfies(client -> assertThat(client.getConfig().isProtocolUpgradeEnabled()).isFalse());
    }
}
