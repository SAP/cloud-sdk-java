package com.sap.cloud.sdk.cloudplatform.security.principal;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.auth0.jwt.JWT;
import com.sap.cloud.environment.servicebinding.SapVcapServicesServiceBindingAccessor;
import com.sap.cloud.sdk.cloudplatform.CloudPlatformAccessor;
import com.sap.cloud.sdk.cloudplatform.ScpCfCloudPlatform;
import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;

import io.vavr.control.Try;

@Deprecated
public class ScpCfPrincipalAudienceScopeTest
{

    /**
     * { "jti": "ea258c68d2304a97a4af04fb20599248", "ext_attr": { "enhancer": "XSUAA", "subaccountid":
     * "628fa405-db54-473e-973f-aa5f91485b05", "zdn": "real-estate" }, "sub": "sb-sirem-ops!t9657", "authorities": [
     * "sirem-ops!t9657.Toggles.ToggleFeatures", "sirem-ops!t9657.Schemas.Export", "uaa.resource",
     * "sirem!b9657.Callback", "sirem-ops!t9657.Application.Monitor", "sirem-ops!t9657.Schemas.Import",
     * "sirem!b9657.Tenants.Configure" ], "scope": [ "sirem-ops!t9657.Toggles.ToggleFeatures",
     * "sirem-ops!t9657.Schemas.Export", "uaa.resource", "sirem!b9657.Callback", "sirem-ops!t9657.Application.Monitor",
     * "sirem-ops!t9657.Schemas.Import", "sirem!b9657.Tenants.Configure" ], "client_id": "sb-sirem-ops!t9657", "cid":
     * "sb-sirem-ops!t9657", "azp": "sb-sirem-ops!t9657", "grant_type": "client_credentials", "rev_sig": "bff38eab",
     * "iat": 1622118833, "exp": 1622205233, "iss": "http://real-estate.localhost:8080/uaa/oauth/token", "zid":
     * "628fa405-db54-473e-973f-aa5f91485b05", "aud": [ "sb-sirem-ops!t9657", "sirem-ops!t9657.Schemas", "uaa",
     * "sirem!b9657", "sirem-ops!t9657.Application", "sirem!b9657.Tenants", "sirem-ops!t9657.Toggles" ] }
     */
    private static final String AUTHORIZATION_TOKEN =
        "eyJhbGciOiJSUzI1NiIsImprdSI6Imh0dHBzOi8vcmVhbC1lc3RhdGUuYXV0aGVudGljYXRpb24uZXUxMC5oYW5hLm9uZGVtYW5kLmNvbS90"
            + "b2tlbl9rZXlzIiwia2lkIjoia2V5LWlkLTEiLCJ0eXAiOiJKV1QifQ.eyJqdGkiOiJlYTI1OGM2OGQyMzA0YTk3YTRhZjA0ZmIyM"
            + "DU5OTI0OCIsImV4dF9hdHRyIjp7ImVuaGFuY2VyIjoiWFNVQUEiLCJzdWJhY2NvdW50aWQiOiI2MjhmYTQwNS1kYjU0LTQ3M2UtO"
            + "TczZi1hYTVmOTE0ODViMDUiLCJ6ZG4iOiJyZWFsLWVzdGF0ZSJ9LCJzdWIiOiJzYi1zaXJlbS1vcHMhdDk2NTciLCJhdXRob3JpdG"
            + "llcyI6WyJzaXJlbS1vcHMhdDk2NTcuVG9nZ2xlcy5Ub2dnbGVGZWF0dXJlcyIsInNpcmVtLW9wcyF0OTY1Ny5TY2hlbWFzLkV4cG9"
            + "ydCIsInVhYS5yZXNvdXJjZSIsInNpcmVtIWI5NjU3LkNhbGxiYWNrIiwic2lyZW0tb3BzIXQ5NjU3LkFwcGxpY2F0aW9uLk1vbml0"
            + "b3IiLCJzaXJlbS1vcHMhdDk2NTcuU2NoZW1hcy5JbXBvcnQiLCJzaXJlbSFiOTY1Ny5UZW5hbnRzLkNvbmZpZ3VyZSJdLCJzY29wZ"
            + "SI6WyJzaXJlbS1vcHMhdDk2NTcuVG9nZ2xlcy5Ub2dnbGVGZWF0dXJlcyIsInNpcmVtLW9wcyF0OTY1Ny5TY2hlbWFzLkV4cG9ydC"
            + "IsInVhYS5yZXNvdXJjZSIsInNpcmVtIWI5NjU3LkNhbGxiYWNrIiwic2lyZW0tb3BzIXQ5NjU3LkFwcGxpY2F0aW9uLk1vbml0b3I"
            + "iLCJzaXJlbS1vcHMhdDk2NTcuU2NoZW1hcy5JbXBvcnQiLCJzaXJlbSFiOTY1Ny5UZW5hbnRzLkNvbmZpZ3VyZSJdLCJjbGllbnRf"
            + "aWQiOiJzYi1zaXJlbS1vcHMhdDk2NTciLCJjaWQiOiJzYi1zaXJlbS1vcHMhdDk2NTciLCJhenAiOiJzYi1zaXJlbS1vcHMhdDk2N"
            + "TciLCJncmFudF90eXBlIjoiY2xpZW50X2NyZWRlbnRpYWxzIiwicmV2X3NpZyI6ImJmZjM4ZWFiIiwiaWF0IjoxNjIyMTE4ODMzLC"
            + "JleHAiOjE2MjIyMDUyMzMsImlzcyI6Imh0dHA6Ly9yZWFsLWVzdGF0ZS5sb2NhbGhvc3Q6ODA4MC91YWEvb2F1dGgvdG9rZW4iLCJ"
            + "6aWQiOiI2MjhmYTQwNS1kYjU0LTQ3M2UtOTczZi1hYTVmOTE0ODViMDUiLCJhdWQiOlsic2Itc2lyZW0tb3BzIXQ5NjU3Iiwic2ly"
            + "ZW0tb3BzIXQ5NjU3LlNjaGVtYXMiLCJ1YWEiLCJzaXJlbSFiOTY1NyIsInNpcmVtLW9wcyF0OTY1Ny5BcHBsaWNhdGlvbiIsInNpc"
            + "mVtIWI5NjU3LlRlbmFudHMiLCJzaXJlbS1vcHMhdDk2NTcuVG9nZ2xlcyJdfQ.MsKccw56s7391GFzObbamZoQttOCvqY0zIbviNN"
            + "qVxwfA9qIjsYkXz16ToA34D-ntYhzcsFM1Jj8XpUtU4KsbzFTyiLhUVuX_8PQqSwV3gH63L5vus-rEHn5WK-f6rtkhcDidaaW4Sp4"
            + "-Pwe0vGIFhr-zKJFrS2qjhFCjLEWJL20KgASmBZ-ov4ACLMVqa5xIdK6LgNj9KdZkJBdHWVpKd-62K_EfUuhAmpzxq4t5SEBm1o6hg"
            + "ky6kLtcMPPaEzmVhO24qNmxsBuVH5ZBVW2idXXusbcFJH9uFwVzRKqQho7Qxp7EVQ00Qf4VZkl6IZ5kmnHLCPUT9rIr7mXV6iHrgS"
            + "Fyf_6K84laekwTxUnHnCrCpdO82FjDSD-zDPbOuLZyohREV6OUl7jmnONmo8OnnkU2a42ib0WYz02JpOAwK6PRJlTuu928pAGP2gU"
            + "hdW5ojyTZpCHjLGc9ktNUJM4YYV0H6lCxfH53KkhGHXG9gJMSkthzYy7moZiXCUk2L054Sp8QDASi-J-yE3E0FPBPA8D9NM4CMn6m"
            + "TZGKiQ5rA8fFiDQe4hE7RO98-9bLfeO6xw6A8tKxOngXHtU6DxJCexT3Hd_xudHE1mDKc68Xz_rx9G_pgYYRMPPo-TAI3yqE7Hq-v"
            + "GOfR7jGhwwQirooCClqSDDD_w9lCGo10cIy8c";

    private ScpCfPrincipal testPrincipal;

    @BeforeClass
    public static void beforeClass()
    {
        final String VCAP_SERVICES =
            "{\"destination\": ["
                + "  {"
                + "    \"credentials\": {"
                + "      \"clientid\": \"dummy\","
                + "      \"clientsecret\": \"dummy\","
                + "      \"uri\": \""
                + "/"
                + "\","
                + "      \"url\": \""
                + "/"
                + "\""
                + "    }"
                + "  }"
                + "],"
                + "\"xsuaa\": ["
                + "  {"
                + "    \"credentials\": {"
                + "      \"clientid\": \"dummy\","
                + "      \"clientsecret\": \"dummy\","
                + "      \"xsappname\": \"appname\","
                + "      \"url\": \""
                + "/"
                + "\""
                + "    },"
                + "  \"plan\": \"application\""
                + "  },"
                + "  {"
                + "    \"credentials\": {"
                + "      \"clientid\": \"dummy2\","
                + "      \"clientsecret\": \"dummy\","
                + "      \"xsappname\": \"broker\","
                + "      \"url\": \""
                + "/"
                + "\""
                + "    },"
                + "  \"plan\": \"broker\""
                + "  },"
                + "]}";

        final ScpCfCloudPlatform cloudPlatformSpy = Mockito.spy(ScpCfCloudPlatform.class);
        cloudPlatformSpy
            .setServiceBindingAccessor(
                new SapVcapServicesServiceBindingAccessor(
                    Collections.singletonMap("VCAP_SERVICES", VCAP_SERVICES)::get));

        CloudPlatformAccessor.setCloudPlatformFacade(() -> Try.success(cloudPlatformSpy));
    }

    @Before
    public void before()
    {
        ScpCfCloudPlatform.invalidateCaches();

        final AuthToken authToken = Mockito.mock(AuthToken.class);
        Mockito.when(authToken.getJwt()).thenReturn(JWT.decode(AUTHORIZATION_TOKEN));

        AuthTokenAccessor.setAuthTokenFacade(() -> Try.success(authToken));

        testPrincipal =
            (ScpCfPrincipal) new ScpCfPrincipalFacade(
                new ScpCfPrincipalAudienceScopeTest.TestLocalScopePrefixProvider())
                .tryGetCurrentPrincipal()
                .getOrElseThrow(failure -> new ShouldNotHappenException("Principal not mocked correctly.", failure));
    }

    @After
    public void cleanupAccessors()
    {
        CloudPlatformAccessor.setCloudPlatformFacade(null);
        AuthTokenAccessor.setAuthTokenFacade(null);
    }

    @Test
    public void testGetAuthorizations()
    {
        final Set<com.sap.cloud.sdk.cloudplatform.security.Authorization> authorizations =
            testPrincipal.getAuthorizations();

        assertThat(authorizations)
            .containsExactlyInAnyOrder(
                new com.sap.cloud.sdk.cloudplatform.security.Authorization("Application.Monitor"),
                new com.sap.cloud.sdk.cloudplatform.security.Authorization("Schemas.Import"),
                new com.sap.cloud.sdk.cloudplatform.security.Authorization("Schemas.Export"),
                new com.sap.cloud.sdk.cloudplatform.security.Authorization("Toggles.ToggleFeatures"));
    }

    @Test
    public void testGetAuthorizationsByAudience()
    {
        final Map<com.sap.cloud.sdk.cloudplatform.security.Audience, Set<com.sap.cloud.sdk.cloudplatform.security.Authorization>> authorizationsByAudience =
            testPrincipal.getAuthorizationsByAudience();

        final com.sap.cloud.sdk.cloudplatform.security.Audience sbSiremOpsAudience =
            new com.sap.cloud.sdk.cloudplatform.security.Audience("sb-sirem-ops!t9657");
        final com.sap.cloud.sdk.cloudplatform.security.Audience siremOpsAudience =
            new com.sap.cloud.sdk.cloudplatform.security.Audience("sirem-ops!t9657");
        final com.sap.cloud.sdk.cloudplatform.security.Audience siremAudience =
            new com.sap.cloud.sdk.cloudplatform.security.Audience("sirem!b9657");
        final com.sap.cloud.sdk.cloudplatform.security.Audience uuaAudience =
            new com.sap.cloud.sdk.cloudplatform.security.Audience("uaa");

        final com.sap.cloud.sdk.cloudplatform.security.Authorization siremOpsToggleFeaturesAuthorization =
            new com.sap.cloud.sdk.cloudplatform.security.Authorization("Toggles.ToggleFeatures");
        final com.sap.cloud.sdk.cloudplatform.security.Authorization siremOpsSchemasExportAuthorization =
            new com.sap.cloud.sdk.cloudplatform.security.Authorization("Schemas.Export");
        final com.sap.cloud.sdk.cloudplatform.security.Authorization uaaResourceAuthorization =
            new com.sap.cloud.sdk.cloudplatform.security.Authorization("resource");
        final com.sap.cloud.sdk.cloudplatform.security.Authorization siremCallbackAuthorization =
            new com.sap.cloud.sdk.cloudplatform.security.Authorization("Callback");
        final com.sap.cloud.sdk.cloudplatform.security.Authorization siremOpsApplicationMonitorAuthorization =
            new com.sap.cloud.sdk.cloudplatform.security.Authorization("Application.Monitor");
        final com.sap.cloud.sdk.cloudplatform.security.Authorization siremOpsSchemasImportAuthorization =
            new com.sap.cloud.sdk.cloudplatform.security.Authorization("Schemas.Import");
        final com.sap.cloud.sdk.cloudplatform.security.Authorization siremConfigureTenantsAuthorization =
            new com.sap.cloud.sdk.cloudplatform.security.Authorization("Tenants.Configure");

        assertThat(authorizationsByAudience)
            .containsOnlyKeys(sbSiremOpsAudience, siremAudience, siremOpsAudience, uuaAudience);

        assertThat(authorizationsByAudience.get(sbSiremOpsAudience)).isEmpty();
        assertThat(authorizationsByAudience.get(siremOpsAudience))
            .containsExactlyInAnyOrder(
                siremOpsToggleFeaturesAuthorization,
                siremOpsSchemasExportAuthorization,
                siremOpsSchemasImportAuthorization,
                siremOpsApplicationMonitorAuthorization);
        assertThat(authorizationsByAudience.get(siremAudience))
            .containsExactlyInAnyOrder(siremCallbackAuthorization, siremConfigureTenantsAuthorization);
        assertThat(authorizationsByAudience.get(uuaAudience)).containsExactlyInAnyOrder(uaaResourceAuthorization);
    }

    private static class TestLocalScopePrefixProvider implements LocalScopePrefixProvider
    {
        @Nonnull
        @Override
        public Try<String> getLocalScopePrefix()
        {
            return Try.success("sirem-ops!t9657");
        }
    }
}
