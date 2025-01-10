package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationType.HTTP;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationType.LDAP;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationType.MAIL;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationType.RFC;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DestinationTypeTest
{
    @Test
    void getIdentifier()
    {
        assertThat(HTTP.getIdentifier()).isEqualTo("HTTP");
        assertThat(RFC.getIdentifier()).isEqualTo("RFC");
        assertThat(MAIL.getIdentifier()).isEqualTo("MAIL");
        assertThat(LDAP.getIdentifier()).isEqualTo("LDAP");
    }

    @Test
    void ofIdentifier()
    {
        assertThat(DestinationType.ofIdentifier("HTTP")).isEqualTo(HTTP);
        assertThat(DestinationType.ofIdentifier("RFC")).isEqualTo(RFC);
        assertThat(DestinationType.ofIdentifier("MAIL")).isEqualTo(MAIL);
        assertThat(DestinationType.ofIdentifier("LDAP")).isEqualTo(LDAP);
    }

    @Test
    void ofIdentifierOrDefault()
    {
        assertThat(DestinationType.ofIdentifierOrDefault("NonExisting", HTTP)).isEqualTo(HTTP);
    }
}
