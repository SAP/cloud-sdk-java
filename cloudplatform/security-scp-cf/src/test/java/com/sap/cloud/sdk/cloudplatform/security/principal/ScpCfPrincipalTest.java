package com.sap.cloud.sdk.cloudplatform.security.principal;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.assertj.vavr.api.VavrAssertions;
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
import com.sap.cloud.sdk.cloudplatform.security.principal.exception.PrincipalAttributeException;

import io.vavr.control.Try;

@Deprecated
public class ScpCfPrincipalTest
{
    /**
     * { "sub": "1234567890", "user_name": "Max", "scope": [ "appname.scope1", "appname.scope2" ], "grant_type":
     * "password", "xs.user.attributes": { "attribute1": ["value1"], "attribute2": ["value2"] } }
     */
    private static final String AUTHORIZATION_TOKEN =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9."
            + "eyJzdWIiOiIxMjM0NTY3ODkwIiwidXNlcl9uYW1lIjoiTWF4Iiwic2NvcGUiOlsiYXBwbmFtZS5"
            + "zY29wZTEiLCJhcHBuYW1lLnNjb3BlMiJdLCJjbGllbnRfaWQiOiJkdW1teSIsImdyYW50X3"
            + "R5cGUiOiJwYXNzd29yZCIsInhzLnVzZXIuYXR0cmlidXRlcyI6eyJhdHRyaWJ1dGUxIjpbI"
            + "nZhbHVlMSJdLCJhdHRyaWJ1dGUyIjpbInZhbHVlMiJdfX0."
            + "58tVZXd78CUjm08U9V4KUuGwDzlxrN9W0LBkq6dXiNA";

    private static final String principalId = "Max";

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
            (ScpCfPrincipal) new ScpCfPrincipalFacade(new AppnameLocalScopePrefixProvider())
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
    public void testGetPrincipalId()
    {
        assertThat(testPrincipal.getPrincipalId()).isEqualTo(principalId);
    }

    @Test
    public void testHasAuthorizationWithScopes()
    {
        assertThat(
            testPrincipal.getAuthorizations().contains(new com.sap.cloud.sdk.cloudplatform.security.Scope("scope1")))
            .isTrue();
        assertThat(
            testPrincipal.getAuthorizations().contains(new com.sap.cloud.sdk.cloudplatform.security.Scope("scope2")))
            .isTrue();
        assertThat(
            testPrincipal.getAuthorizations().contains(new com.sap.cloud.sdk.cloudplatform.security.Scope("scope3")))
            .isFalse();
    }

    @Test
    public void testGetAuthorizations()
    {
        final Set<com.sap.cloud.sdk.cloudplatform.security.Authorization> authorizations =
            testPrincipal.getAuthorizations();

        assertThat(authorizations.size()).isEqualTo(2);
        assertThat(authorizations.contains(new com.sap.cloud.sdk.cloudplatform.security.Scope("scope1"))).isTrue();
        assertThat(authorizations.contains(new com.sap.cloud.sdk.cloudplatform.security.Scope("scope2"))).isTrue();
    }

    @Test
    public void testGetAuthorizationsByAudience()
    {
        final Map<com.sap.cloud.sdk.cloudplatform.security.Audience, Set<com.sap.cloud.sdk.cloudplatform.security.Authorization>> authorizationsByAudience =
            testPrincipal.getAuthorizationsByAudience();

        final com.sap.cloud.sdk.cloudplatform.security.Audience audience =
            new com.sap.cloud.sdk.cloudplatform.security.Audience("appname");

        final com.sap.cloud.sdk.cloudplatform.security.Authorization scope1 =
            new com.sap.cloud.sdk.cloudplatform.security.Authorization("scope1");
        final com.sap.cloud.sdk.cloudplatform.security.Authorization scope2 =
            new com.sap.cloud.sdk.cloudplatform.security.Authorization("scope2");

        assertThat(authorizationsByAudience).containsOnlyKeys(audience);
        assertThat(authorizationsByAudience.get(audience)).containsExactlyInAnyOrder(scope1, scope2);
    }

    @Test
    public void testGetAttribute()
    {
        final PrincipalAttribute attribute = testPrincipal.getAttribute("attribute1").getOrNull();

        assertThat(attribute).isNotNull();
        assertThat(attribute.getName()).isEqualTo("attribute1");

        assertThat(attribute).isInstanceOf(StringCollectionPrincipalAttribute.class);
        assertThat(((StringCollectionPrincipalAttribute) attribute).getValues()).containsExactlyInAnyOrder("value1");

        VavrAssertions
            .assertThat(testPrincipal.getAttribute("missingAttributeName"))
            .isFailure()
            .failBecauseOf(PrincipalAttributeException.class);
    }

    @Test
    public void testUserEmptyNameAndEmptyJwtToken()
    {
        final ScpCfPrincipal principal = new ScpCfPrincipal("", Collections.emptySet(), Collections.emptyMap());

        assertThat(principal.getPrincipalId()).isEmpty();
        assertThat(principal.getAuthorizations()).isEmpty();
    }

    private static class AppnameLocalScopePrefixProvider implements LocalScopePrefixProvider
    {
        @Nonnull
        @Override
        public Try<String> getLocalScopePrefix()
        {
            return Try.success("appname");
        }
    }
}
