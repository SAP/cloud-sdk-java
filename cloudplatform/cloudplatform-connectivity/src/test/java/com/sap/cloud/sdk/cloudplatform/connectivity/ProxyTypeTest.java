package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.ProxyType.INTERNET;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ProxyType.ON_PREMISE;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ProxyType.PRIVATE_LINK;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ProxyTypeTest
{
    @Test
    public void getIdentifier()
    {
        assertThat(ON_PREMISE.getIdentifier()).isEqualTo("OnPremise");
        assertThat(INTERNET.getIdentifier()).isEqualTo("Internet");
        assertThat(PRIVATE_LINK.getIdentifier()).isEqualTo("PrivateLink");
    }

    @Test
    public void ofIdentifier()
    {
        assertThat(ProxyType.ofIdentifier("OnPremise")).isEqualTo(ON_PREMISE);
        assertThat(ProxyType.ofIdentifier("Internet")).isEqualTo(INTERNET);
        assertThat(ProxyType.ofIdentifier("PrivateLink")).isEqualTo(PRIVATE_LINK);
    }

    @Test
    public void ofIdentifierOrDefault()
    {
        assertThat(ProxyType.ofIdentifierOrDefault("NonExisting", INTERNET)).isEqualTo(INTERNET);
    }
}
