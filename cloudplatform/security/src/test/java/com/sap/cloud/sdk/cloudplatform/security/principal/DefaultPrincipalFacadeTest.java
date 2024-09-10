package com.sap.cloud.sdk.cloudplatform.security.principal;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Base64;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.common.net.HttpHeaders;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderAccessor;

class DefaultPrincipalFacadeTest
{
    @Test
    void testExtractPrincipalFromBasicAuthHeader()
    {
        final String basicAuthValue = "username:pass";
        final String basicAuthHeaderValue = "Basic " + Base64.getEncoder().encodeToString(basicAuthValue.getBytes());

        RequestHeaderAccessor
            .executeWithHeaderContainer(Map.of(HttpHeaders.AUTHORIZATION, basicAuthHeaderValue), () -> {
                final PrincipalFacade sut = new DefaultPrincipalFacade();
                final Principal principal = sut.tryGetCurrentPrincipal().get();

                assertThat(principal.getPrincipalId()).isEqualTo("username");
            });
    }
}
