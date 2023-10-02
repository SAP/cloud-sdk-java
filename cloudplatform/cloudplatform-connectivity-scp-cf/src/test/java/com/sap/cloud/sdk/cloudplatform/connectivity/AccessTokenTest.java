/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.Test;

public class AccessTokenTest
{
    @Test
    public void testEquals()
    {
        final Instant now = Instant.now();

        final AccessToken accessToken1 = new AccessToken("secret", now);
        final AccessToken accessToken2 = new AccessToken("secret", now);

        final AccessToken accessToken3 = new AccessToken("other", now);
        final AccessToken accessToken4 = new AccessToken("secret", now.plusNanos(1000));

        assertThat(accessToken1).isEqualTo(accessToken2);
        assertThat(accessToken1).isNotEqualTo(accessToken3);

        assertThat(accessToken1).isNotEqualTo(accessToken3);
        assertThat(accessToken1).isNotEqualTo(accessToken4);
    }

    @Test
    public void testIsValid()
    {
        assertThat(new AccessToken("secret", Instant.now()).isValid()).isFalse();
        assertThat(new AccessToken("secret", Instant.now().plusSeconds(3600)).isValid()).isTrue();
    }

    @Test
    public void testNonSensitiveToString()
    {
        final AccessToken accessToken = new AccessToken("secret", Instant.now());
        assertThat(accessToken.toString()).doesNotContain("secret");
    }
}
