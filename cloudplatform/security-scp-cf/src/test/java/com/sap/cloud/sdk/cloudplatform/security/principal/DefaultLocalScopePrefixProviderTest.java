package com.sap.cloud.sdk.cloudplatform.security.principal;

import java.util.Collections;

import org.assertj.vavr.api.VavrAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.auth0.jwt.JWT;
import com.sap.cloud.environment.servicebinding.SapVcapServicesServiceBindingAccessor;
import com.sap.cloud.sdk.cloudplatform.CloudPlatformAccessor;
import com.sap.cloud.sdk.cloudplatform.ScpCfCloudPlatform;
import com.sap.cloud.sdk.cloudplatform.ScpCfCloudPlatformFacade;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenFacade;
import com.sap.cloud.sdk.cloudplatform.security.ScpCfAuthTokenFacade;

import io.vavr.control.Try;

@Deprecated
public class DefaultLocalScopePrefixProviderTest
{
    // "scope": [
    //     "local.scope",
    //     "remote.scope"
    // ]
    private static final String AUTHORIZATION_TOKEN =
        "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJKb2huIERvZSIsInppZCI6IlRlbmFudCIsImNsaWVudF9pZCI6ImR1bW15IXQxMjMiLCJzY29wZSI6WyJsb2NhbC5zY29wZSIsInJlbW90ZS5zY29wZSJdfQ.dPPDI4VhezHMM2EdtdnEXLabvFd1y_-q90U-3qIl6JLtKoXt8dRL0cjBfTWWtWK3rzRoYJRWfPWWEqc_AJ-9QyjkTBOBFFcaGzd_dkJ2nvKkvnY-ViurHlVm_2Bdc1YlKtayUYdr7jllDpYgwQxJ_pv8mx5UwsqlmU1XkQaHVqkm6WpeWHbO227gtMOjHnsIUev9J1hnh9XpGf0mMcZ-G-jeZRPq9xrE-O9Sr3-kWFjvfY2HyzPAYgiBl4_XRC1rj8bBZ-JjZrRjjDIUFOWd7NUML0cll15UugGgxe9I8FoteQHGBINfIaFzaky8CsyyImrxEPoWH2quKV8oF0l_4vQOuk2uNVPx3Tpke68r-imLcrNm8bNJWSoOefx-TwPrUaGe-6eIyJ5RlD4sjt7IycPQTf6oB6No_GzB-V6Tr8irK7QZBgj4eZBZczyMY-TAVu5qnJoAPK56QxekgPDrgj0e7MfAOJi58dhoPpSzMa_9HcsNjFp-eyN1TqbNBiKU7gQk-I7g5UG3OSKOZr3D-8FlJiMAax6v1DbbW8_K6aTzE6c_1LRMDBIqYElD_f61z6Dghn3anfNI1fK3R80YKxDGvS-XPZ2BQsSpc0F_CGhZ8O--UGD_h9ke2XiuFP7qw73S8On5NlN3nzPRZDS90ZllMeLBEEgX1fyUvK3pQXQ";

    @Before
    public void before()
    {
        ScpCfCloudPlatform.invalidateCaches();
    }

    @After
    public void cleanUpAccessors()
    {
        CloudPlatformAccessor.setCloudPlatformFacade(null);
    }

    @Test
    public void defaultStrategyReturnsXsappnameOfCorrespondingXsuaaInstance()
    {
        final String VCAP_SERVICES =
            "{\"xsuaa\": ["
                + "  {"
                + "    \"credentials\": {"
                + "      \"clientid\": \"dummy!t123\","
                + "      \"clientsecret\": \"dummy\","
                + "      \"xsappname\": \"local\""
                + "    },"
                + "  \"plan\": \"application\""
                + "  }"
                + "]}";

        final ScpCfCloudPlatform scpCfCloudPlatform = Mockito.spy(ScpCfCloudPlatform.class);
        scpCfCloudPlatform
            .setServiceBindingAccessor(
                new SapVcapServicesServiceBindingAccessor(
                    Collections.singletonMap("VCAP_SERVICES", VCAP_SERVICES)::get));

        final ScpCfCloudPlatformFacade scpCfCloudPlatformFacade = Mockito.spy(new ScpCfCloudPlatformFacade());
        Mockito.doReturn(Try.success(scpCfCloudPlatform)).when(scpCfCloudPlatformFacade).tryGetCloudPlatform();

        CloudPlatformAccessor.setCloudPlatformFacade(scpCfCloudPlatformFacade);

        final AuthTokenFacade authTokenFacade = Mockito.spy(new ScpCfAuthTokenFacade());
        Mockito
            .doReturn(Try.success(new AuthToken(JWT.decode(AUTHORIZATION_TOKEN))))
            .when(authTokenFacade)
            .tryGetCurrentToken();
        AuthTokenAccessor.setAuthTokenFacade(authTokenFacade);

        VavrAssertions.assertThat(new DefaultLocalScopePrefixProvider().getLocalScopePrefix()).contains("local.");
    }
}
