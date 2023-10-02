/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.security.xsuaa.client.OAuth2TokenServiceConstants.GRANT_TYPE;
import static com.sap.cloud.security.xsuaa.client.OAuth2TokenServiceConstants.GRANT_TYPE_CLIENT_CREDENTIALS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import com.sap.cloud.environment.servicebinding.SapVcapServicesServiceBindingAccessor;
import com.sap.cloud.sdk.cloudplatform.CloudPlatformAccessor;
import com.sap.cloud.sdk.cloudplatform.ScpCfCloudPlatform;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderAccessor;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAccessor;
import com.sap.cloud.sdk.cloudplatform.servlet.RequestAccessorFilter;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.security.config.Service;
import com.sap.cloud.security.test.SecurityTestRule;
import com.sap.cloud.security.token.SecurityContext;
import com.sap.cloud.security.token.Token;
import com.sap.cloud.security.token.TokenClaims;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

public class XsuaaSecurityTest
{
    @Slf4j
    public static class TestServlet extends HttpServlet
    {
        private static final long serialVersionUID = -6969662768672381400L;

        @Override
        protected void doGet( @Nonnull final HttpServletRequest req, @Nonnull final HttpServletResponse resp )
        {
            log.info("Principal: {}", PrincipalAccessor.getCurrentPrincipal());
            log.info("Tenant: {}", TenantAccessor.getCurrentTenant());
            log.info("AuthToken: {}", AuthTokenAccessor.getCurrentToken());
            log.info("RequestHeaders: {}", RequestHeaderAccessor.getHeaderContainer());
            resp.setStatus(200);
        }
    }

    @ClassRule
    public static final SecurityTestRule rule =
        SecurityTestRule
            .getInstance(Service.XSUAA)
            .useApplicationServer()
            .addApplicationServlet(TestServlet.class, "/app")
            .addApplicationServletFilter(RequestAccessorFilter.class);

    @After
    public void tearDown()
    {
        SecurityContext.clearToken();
    }

    @Test
    public void requestWithValidTokenRequest()
        throws IOException
    {
        final Token token =
            rule
                .getPreconfiguredJwtGenerator()
                .withClaimValue("origin", "foo")
                .withClaimValue("client_id", SecurityTestRule.DEFAULT_CLIENT_ID)
                .withClaimValue("zid", "tenantId/zoneId")
                .withClaimValue("iss", "http://tenant.localhost/")
                .withClaimValue(TokenClaims.USER_NAME, "bar")
                .withClaimValue(TokenClaims.EMAIL, "tester@mail.com")
                .withClaimValue(GRANT_TYPE, GRANT_TYPE_CLIENT_CREDENTIALS)
                .withScopes(SecurityTestRule.DEFAULT_APP_ID + ".Read", "uaa.user")
                .createToken();

        final HttpGet request = new HttpGet(rule.getApplicationServerUri() + "/app");
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token.getTokenValue());

        try( CloseableHttpResponse response = HttpClients.createDefault().execute(request) ) {
            final String responseBody = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
            assertThat(responseBody).isEmpty();
            assertThat(response.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);
        }
    }

    // Workaround to mock local VCAP services lookup
    @Before
    public void mockCloudPlatform()
    {
        final ScpCfCloudPlatform platform = spy(ScpCfCloudPlatform.class);

        final Token templateToken = rule.getPreconfiguredJwtGenerator().createToken();
        final String xsuaaUrl = templateToken.getHeaderParameterAsString("jku").replaceAll("token_keys$", "");

        final String connectivity =
            String
                .format(
                    "[{\"credentials\":{\"clientid\":\"%s\",\"clientsecret\":\"%s\",\"url\":\"%s\"}}]",
                    "connectivityClientId",
                    "connectivityClientSecret",
                    xsuaaUrl);

        final String xsuaa =
            String
                .format(
                    "[{\"plan\":\"application\",\"credentials\":{\"xsappname\":\"%s\",\"clientid\":\"%s\",\"clientsecret\":\"%s\",\"url\":\"%s\"}}]",
                    SecurityTestRule.DEFAULT_APP_ID,
                    SecurityTestRule.DEFAULT_CLIENT_ID,
                    "xsuaaClientSecret",
                    xsuaaUrl);

        final String vcap = String.format("{\"connectivity\":%s,\"xsuaa\":%s}", connectivity, xsuaa);
        platform
            .setServiceBindingAccessor(
                new SapVcapServicesServiceBindingAccessor(Collections.singletonMap("VCAP_SERVICES", vcap)::get));

        CloudPlatformAccessor.setCloudPlatformFacade(() -> Try.success(platform));
    }

    @After
    public void resetCloudPlatform()
    {
        CloudPlatformAccessor.setCloudPlatformFacade(null);
    }
}
