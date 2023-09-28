package com.sap.cloud.sdk.cloudplatform.security;

import static com.sap.cloud.sdk.cloudplatform.DwcHeaderUtils.DWC_CLIENT_HEADER;
import static com.sap.cloud.sdk.cloudplatform.DwcHeaderUtils.DWC_SCOPES_HEADER;
import static com.sap.cloud.sdk.cloudplatform.DwcHeaderUtils.DWC_USER_HEADER;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

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

public class DwcPrincipalFacadeTest
{
    @Test
    public void testFacadeIsPickedUpAutomatically()
    {
        assertThat(PrincipalAccessor.getPrincipalFacade()).isInstanceOf(DwcPrincipalFacade.class);
    }

    @Test
    public void testSuccessfulPrincipalRetrieval()
    {
        final String dwcUser =
            "eyJlbWFpbCI6InNhbXBsZS51c2VyQG1haWwuY29tIiwiZ2l2ZW5OYW1lIjoiU2FtcGxlIiwiZmFtaWx5TmFtZSI6IlVzZXIiLCJsb2dvbk5hbWUiOiJzYW1wbGUudXNlckBtYWlsLmNvbSJ9";
        final String dwcScopes = "dwc-client!1234.first-scope_app non-relevant-scope dwc-client!1234.second-scope_app";
        final String dwcClient = "sb-dwc-client!1234";

        final Map<String, String> headers =
            ImmutableMap.of(DWC_USER_HEADER, dwcUser, DWC_SCOPES_HEADER, dwcScopes, DWC_CLIENT_HEADER, dwcClient);
        @SuppressWarnings( "deprecation" )
        final Set<Authorization> expectedAuthorizations =
            new HashSet<>(
                Arrays
                    .asList(
                        new Authorization("dwc-client!1234.first-scope_app"),
                        new Authorization("non-relevant-scope"),
                        new Authorization("dwc-client!1234.second-scope_app")));

        @SuppressWarnings( "deprecation" )
        final DefaultPrincipal expectedPrincipal =
            new DefaultPrincipal(
                "sample.user@mail.com",
                expectedAuthorizations,
                expectedAuthorizations,
                Collections.emptySet(),
                Collections.emptyMap());

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
    public void testUnsuccessfulPrincipalRetrieval()
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
