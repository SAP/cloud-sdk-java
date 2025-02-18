package com.sap.cloud.sdk.cloudplatform.tenant;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.security.config.Service;
import com.sap.cloud.security.test.JwtGenerator;
import com.sap.cloud.security.token.Token;

import io.vavr.control.Try;

class DefaultTenantFacadeIasTest
{
    private static final String TENANT_ID = "a89ea924-d9c2-4eab-84fb-3ffcaadf5d24";
    private static final String SUBDOMAIN = "some-subdomain";

    @BeforeEach
    void prepareInboundAccessToken()
    {
        final Token securityLibToken =
            JwtGenerator
                .getInstance(Service.IAS, "some arbitrary id")
                .withClaimValue("iss", "http://" + SUBDOMAIN + ".some-host")
                // According to TG02 the zone/tenant id is stored in the claim "app_tid"
                .withClaimValue("app_tid", TENANT_ID)
                .withClaimValue("zone_uuid", TENANT_ID)
                .createToken();

        final DecodedJWT decodedJWT = JWT.decode(securityLibToken.getTokenValue());
        final AuthToken token = new AuthToken(decodedJWT);
        AuthTokenAccessor.setAuthTokenFacade(() -> Try.success(token));
    }

    @AfterEach
    void resetAuthTokenFacade()
    {
        AuthTokenAccessor.setAuthTokenFacade(null);
    }

    @Test
    void tenantIdShouldBeReadFromIasToken()
    {
        final Tenant tenant = TenantAccessor.getCurrentTenant();
        assertThat(tenant.getTenantId()).isEqualTo(TENANT_ID);
    }

    @Test
    void subdomainShouldBeReadFromIasToken()
    {
        final DefaultTenant tenant = (DefaultTenant) TenantAccessor.getCurrentTenant();
        assertThat(tenant.getSubdomain()).isEqualTo(SUBDOMAIN);
    }
}
