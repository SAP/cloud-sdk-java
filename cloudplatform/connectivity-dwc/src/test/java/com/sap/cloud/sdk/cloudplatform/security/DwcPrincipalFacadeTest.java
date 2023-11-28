package com.sap.cloud.sdk.cloudplatform.security;

import static com.sap.cloud.sdk.cloudplatform.DwcHeaderUtils.DWC_USER_HEADER;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderAccessor;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderContainer;
import com.sap.cloud.sdk.cloudplatform.security.principal.DefaultPrincipal;
import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAccessor;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalThreadContextListener;
import com.sap.cloud.sdk.cloudplatform.security.principal.exception.PrincipalAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextAccessor;

import io.vavr.control.Try;

class DwcPrincipalFacadeTest
{
    @Test
    void testFacadeIsPickedUpAutomatically()
    {
        assertThat(PrincipalAccessor.getPrincipalFacade()).isInstanceOf(DwcPrincipalFacade.class);
    }

    @Test
    void testSuccessfulPrincipalRetrieval()
    {
        final String dwcUser =
            "eyJlbWFpbCI6InNhbXBsZS51c2VyQG1haWwuY29tIiwiZ2l2ZW5OYW1lIjoiU2FtcGxlIiwiZmFtaWx5TmFtZSI6IlVzZXIiLCJsb2dvbk5hbWUiOiJzYW1wbGUudXNlckBtYWlsLmNvbSJ9";

        final Map<String, String> headers = ImmutableMap.of(DWC_USER_HEADER, dwcUser);

        final DefaultPrincipal expectedPrincipal = new DefaultPrincipal("sample.user@mail.com");

        RequestHeaderAccessor.executeWithHeaderContainer(headers, () -> {
            final ThreadContext currentContext = ThreadContextAccessor.getCurrentContext();
            final Principal currentPrincipal = PrincipalAccessor.getCurrentPrincipal();
            final Try<Principal> maybePrincipalFromContext =
                currentContext.getPropertyValue(PrincipalThreadContextListener.PROPERTY_PRINCIPAL);

            assertThat(currentPrincipal).isEqualTo(expectedPrincipal);
            assertThat(maybePrincipalFromContext.isSuccess()).isTrue();
            assertThat(maybePrincipalFromContext.get()).isEqualTo(expectedPrincipal);
        });
    }

    @Test
    void testUnsuccessfulPrincipalRetrieval()
    {
        RequestHeaderAccessor.executeWithHeaderContainer(RequestHeaderContainer.EMPTY, () -> {
            final ThreadContext currentContext = ThreadContextAccessor.getCurrentContext();
            final Try<Principal> principalFailure = PrincipalAccessor.tryGetCurrentPrincipal();
            final Try<Principal> shouldBeFailure =
                currentContext.getPropertyValue(PrincipalThreadContextListener.PROPERTY_PRINCIPAL);

            assertThat(principalFailure.isFailure()).isTrue();
            assertThat(principalFailure.getCause()).isInstanceOf(PrincipalAccessException.class);
            assertThat(shouldBeFailure.isFailure()).isTrue();
            assertThat(principalFailure).isSameAs(shouldBeFailure);
        });
    }
}
