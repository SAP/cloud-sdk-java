/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.assertj.vavr.api.VavrAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.net.HttpHeaders;
import com.sap.cloud.sdk.cloudplatform.CloudPlatformAccessor;
import com.sap.cloud.sdk.cloudplatform.requestheader.DefaultRequestHeaderContainer;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderContainer;
import com.sap.cloud.sdk.cloudplatform.security.exception.AuthTokenAccessException;

import io.vavr.control.Try;

public class AuthTokenDecoderTest
{
    private static final String BEARER_PREFIX = "Bearer ";

    private static final String AUTHORIZATION_TOKEN =
        "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJKb2huIERvZSIsInppZCI6IlRlbmFudCIsImNsaWVudF9pZCI6ImR1bW15IXQxMjMifQ.cOPkmQ7Fg7BTCG_VPgjPE7q6XKtMyJ7nvMryxVHWofocvJMGMAxVFNX1HFzfKotAl-2F_lQBoIU4Ot-qvxaaEE4q_g0I7TxR2Yku8JwFCTKI37kbA30NO6B5d9uWhZ9oTeTqsTI9bKvG4PWEhFW8-ESIDx4-mjwXp9mbbO8IF_1OoHFkXZ9aya1zvOst5vGtbliwMXldSJqIzvINWfBi9qVhHf9qqf7WcFeFJjAoq0U3eN5ANEwdP-q9gcNdcWInnXJkPW1zkIfdoxjfW8ZAxLxqHo5fIg6J32WvzGqVWvSsraJPgywcpwWAFQnZHIXKeFsoxQHM8HLIyho21e4G3XK4rO0D-cQSXY7_Q7bWDnVLQxsqOcsaXLak03HczQz2gR8RQenRh9P3vk5bMnz8rmg9bKa7pd4itKEyZWjnHaXJjLT3ThKEtYs-J5_6Pxf1kf5pAs3wb_wFkxCARnBGey-f_i_JD3iEgudy_HRLLKWKpN0xn9Qb0AQ5YXl6lx7KEwXljb1ObstFqUpm6HqYWTzc5uGJBQP6HcKzz4LfPcSfAnjwGK0Q9ecd_0ct7VnFfoAeQ2fdQPLPDUXJ8p-uHd8d16i_9lkktAcgimB3hNF7QvmqY--4udeg8uaev7eImX_QNA8s27ANv6HsqSGz3OkV1C4CDW0Xpwwb3BgBR7g";

    private static final String AUTHORIZATION_BEARER_TOKEN = BEARER_PREFIX + AUTHORIZATION_TOKEN;

    @Before
    public void before()
    {
        CloudPlatformAccessor.setCloudPlatformFacade(null);
    }

    @After
    public void cleanupAccessors()
    {
        CloudPlatformAccessor.setCloudPlatformFacade(null);
    }

    @Test
    public void testGetToken()
    {
        final RequestHeaderContainer headers =
            DefaultRequestHeaderContainer
                .fromSingleValueMap(Collections.singletonMap(HttpHeaders.AUTHORIZATION, AUTHORIZATION_BEARER_TOKEN));

        final AuthToken authToken = new AuthTokenDecoderDefault().decode(headers).getOrNull();
        assertThat(authToken).isNotNull();

        final DecodedJWT jwt = authToken.getJwt();
        assertThat(jwt).isNotNull();

        assertThat(jwt.getAlgorithm()).isEqualTo("RS256");
        assertThat(jwt.getType()).isEqualTo("JWT");

        assertThat(jwt.getClaim("user_name").asString()).isEqualTo("John Doe");
        assertThat(jwt.getClaim("zid").asString()).isEqualTo("Tenant");
    }

    @Test
    public void testMissingAuthorizationHeader()
    {
        final RequestHeaderContainer headers = DefaultRequestHeaderContainer.fromSingleValueMap(Collections.emptyMap());

        final AuthToken authToken = new AuthTokenDecoderDefault().decode(headers).getOrNull();
        assertThat(authToken).isNull();
    }

    @Test
    public void testMultipleAuthorizationHeaders()
    {
        final RequestHeaderContainer headers =
            DefaultRequestHeaderContainer
                .builder()
                .withHeader(HttpHeaders.AUTHORIZATION, AUTHORIZATION_BEARER_TOKEN, AUTHORIZATION_BEARER_TOKEN)
                .build();

        final Try<AuthToken> authTokenTry = new AuthTokenDecoderDefault().decode(headers);
        VavrAssertions.assertThat(authTokenTry).isFailure().failBecauseOf(AuthTokenAccessException.class);
    }

    @Test
    public void testInvalidJwt()
    {
        final String invalidBearer = BEARER_PREFIX + "INVALID";
        final RequestHeaderContainer headers =
            DefaultRequestHeaderContainer
                .fromSingleValueMap(Collections.singletonMap(HttpHeaders.AUTHORIZATION, invalidBearer));

        final Try<AuthToken> authTokenTry = new AuthTokenDecoderDefault().decode(headers);
        VavrAssertions.assertThat(authTokenTry).isFailure().failBecauseOf(AuthTokenAccessException.class);
    }

    @Test
    public void testCaseInsensitiveBearer()
    {
        final String Bearer_prefix = "bEaRer ";
        final String AuthenticationToken = Bearer_prefix + AUTHORIZATION_TOKEN;
        final RequestHeaderContainer headers =
            DefaultRequestHeaderContainer
                .fromSingleValueMap(Collections.singletonMap(HttpHeaders.AUTHORIZATION, AuthenticationToken));

        final AuthToken authToken = new AuthTokenDecoderDefault().decode(headers).getOrNull();
        assertThat(authToken).isNotNull();
        final DecodedJWT jwt = authToken.getJwt();
        assertThat(jwt).isNotNull();
        assertThat(jwt.getClaim("user_name").asString()).isEqualTo("John Doe");
    }

    @Test
    public void testCaseInsensitiveHeaderName()
    {
        final RequestHeaderContainer authorizationHeader =
            DefaultRequestHeaderContainer.builder().withHeader("aUtHoRiZaTiOn", AUTHORIZATION_BEARER_TOKEN).build();

        final AuthToken authToken = new AuthTokenDecoderDefault().decode(authorizationHeader).getOrNull();
        assertThat(authToken).isNotNull();
        final DecodedJWT jwt = authToken.getJwt();
        assertThat(jwt).isNotNull();
        assertThat(jwt.getClaim("user_name").asString()).isEqualTo("John Doe");
    }
}
