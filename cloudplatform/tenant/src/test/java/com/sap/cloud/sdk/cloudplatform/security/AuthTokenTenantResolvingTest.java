/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.vavr.api.VavrAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sap.cloud.environment.servicebinding.SapVcapServicesServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.api.DefaultServiceBindingAccessor;
import com.sap.cloud.sdk.cloudplatform.security.principal.DefaultPrincipalFacade;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenantFacade;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantWithSubdomain;

import lombok.SneakyThrows;

class AuthTokenTenantResolvingTest
{
    // Header:
    // {
    //   "typ": "JWT",
    //   "alg": "RS256"
    // }
    //
    // Claims:
    // {
    //   "grant_type": "password",
    //   "zid": "my-tenant",
    //   "iss": "https://my-subdomain.localhost:8080",
    //   "user_name": "John Doe",
    //   "client_id": "dummy"
    // }
    private static final String AUTHORIZATION_TOKEN =
        "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJncmFudF90eXBlIjoicGFzc3dvcmQiLCJ6aWQiOiJteS10ZW5hbnQiLCJpc3MiOiJodHRwczovL215LXN1YmRvbWFpbi5sb2NhbGhvc3Q6ODA4MCIsInVzZXJfbmFtZSI6IkpvaG4gRG9lIiwiY2xpZW50X2lkIjoiZHVtbXkifQ.qXUAw-oMKPHboS0FR92ngwOOrkiJ7AWqeKECjzfG30W2TBgQk0t71B8DdilVffanv_YFLdGXuuZslIb3QKUo1ye9Of_aEqXivu6nnRBe4QTxLIYwLOb6Fk-oLVkCuEK1PGKjAJBOBsW0xjbSCQ2l7nhVgcQF6S8q5NaN0C2e8zZZY7r-zq3VbmFuKN1-GMX1B-doImgi2phJT0DJbNJMENk6x7QoxlX9QQRBa1onlU0lWkBt2Ys6x7c1N5YBvaeKKNphDUxkaEBCTOJA3FPgTpfIfulZaT_vI6fum4GzWwRjNS5h545Aui-D0jyV3Coed8hbCAKaEKppWvp-972pxA";

    private static final String INVALID_TOKEN =
        "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJncmFudF90eXBlIjoicGFzc3dvcmQiLCJ6aWQiOiJteS10ZW5hbnQiLCJpc3MiOiJodHRwczovL215LXN1YmRvbWFpbi5sb2NhbGhvc3Q6ODA4MCIsInVzZXJfbmFtZSI6IkpvaG4gRG9lIiwiY2xpZW50X2lkIjoiZHVtbXkifQ.dummySignature";

    private static final DecodedJWT OAUTH_JWT =
        JWT
            .decode(
                JWT
                    .create()
                    .withClaim("client_id", "XSUAA_Client")
                    .withClaim("zid", "XSUAA_Tenant")
                    .withClaim("iss", "http://xsuaa-subdomain.localhost:8080/uaa/oauth/token")
                    .sign(Algorithm.none()));

    // This is the private test key used for generating the authorization token above.
    // Of course, this test private key is exposed and not secure.
    //
    //  -----BEGIN RSA PRIVATE KEY-----
    //  MIIEpAIBAAKCAQEAzL56xNXQIJ6Z/rJZidgdxBLOyADAb2S03ZbilqumarhmNshG
    //  2PVe2UzclCyawrTOzKMDpUsY9sI9am2R91KyzCZjbzzHZUv78JyTQR2V/91UTPjj
    //  WVI+RAWG1YEQ2toxkBvPziZHJxJIDaf8M7x3PfWP2wiybjO4lLs5D9Ji37KlEZNt
    //  yy1CILR58kbIggQetfAUZJ2WGWMjneZC9bqAtf8iThgTbH6mzAfLnnxWxkR0nc3C
    //  ylVoA9RJNQyfzLkk4X9tCGiQpYbq+kGAeH5/7xXzXZF+S85vQrIN7UaiGTeDykhQ
    //  1+Vcr4utMwl0uBZdHGJ0vEYgZ3bJEE+0HP2mFQIDAQABAoIBAQC8uUM+1nsu5iK6
    //  c7k25z+gsVlrX84Bn8lbi643Beey7WBCVN+BMsOH92JHLspEeRc553T/0rYjFi44
    //  QOFQISlwJl53dYWyaJTFV/4gAl52Z5RCExS1C6sbViDQAlT/9inDVO96bSwe5qoF
    //  9HbzVWBwez5rvpuCGyij5+OUIvBCkqf+keOnIa0jyi+CxQJJz6Tn/F8eZJYWraxR
    //  v1jbmnBOqW+NhVBc9xIxloulYzYbwRTPN/nOr1lvu3wAtQj+QSIVt4vQv+Th+tNe
    //  v+3rknqxeknsBmoiylNrQ6JyJjLlqbL4zpUhhJhMIJB4v2Pncf5MRwFUd9tsDF3S
    //  aw4IocchAoGBAPomn8vOPrbs1WgHf3Lza/JGjn93uL83lNBpsVrQuJujBtnJz/SP
    //  9NvG+83I7aMx29BXTrjo45Aqapi3Imf51jnVGw2l3PSsym81j0E9B99sBEk2ulgI
    //  4raa5M0bm6+nDCYp2nAC4Mhes2n7nY6PnY3+WdG6uikslOSbQ1rnTJ35AoGBANGI
    //  Dh0brgOSDfwz8ik0hmOJSAPITUtbNfspi7trU+4c5D8C9lPiJbEZQgbGtXdyHr2s
    //  4++Ol1nqMvN+nBiLItKKvjcqy1sByyuNoAR5LS07W31Dip5konUrqyPrFDjFwuSv
    //  0Yem37Yia6VPsAXYO/bPuyNNhEDiDQvYE8Io33/9AoGAY8F5YhYabmpd5EFxMs8e
    //  EhzPAMgmlaK9pqsvfe7UX8SQm9NeKcIqvGZKzlK9aaIdbaUTkKvW7AvuPH5zqxE3
    //  vBJe2n91cOFjTRwHx9VlyVRTFTtM7oSEnJuCeMT0vgSY1LNGF/sd1unlFHOx7kMZ
    //  aqGvAfM7/+V2bv/3yU2KAUkCgYBVZIKEAGTh1aNXdoAppNtkM22jBP3jSS/txVfK
    //  MQqKj0HCIF3tf8vmqimLg24vWNYIbS6T0eQC4/yR2baaFhJCBgNRjmJEG64GePiO
    //  +iecOIWnvl/+/3pqEDr2L3cVUBuGgdT92NdxbgO64ogVSbPeeXiGAbB1lOrGOfCL
    //  f4berQKBgQDQO7Pss65KK9OLA/nghpLqZAiCGokUQ39QF6r0yqodyXrLzqy3iu5C
    //  YSfex0CYqil5tKR+UXCeEtAQ0tI/WUGGcGt8oUcOEYFLs+pN1EKpwnunfr0kTXUW
    //  FvuAsHb0RfpQdrkKXKScaD4xehczD4bkG7Nke6WV8HvDkuYhazck0Q==
    //  -----END RSA PRIVATE KEY-----

    private static final String VCAP_SERVICES =
        """
        {
          "xsuaa":[
            {
              "credentials": {
                "clientid": "dummy",
                "verificationkey":"-----BEGIN PUBLIC KEY-----
        MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzL56xNXQIJ6Z/rJZidgd
        xBLOyADAb2S03ZbilqumarhmNshG2PVe2UzclCyawrTOzKMDpUsY9sI9am2R91Ky
        zCZjbzzHZUv78JyTQR2V/91UTPjjWVI+RAWG1YEQ2toxkBvPziZHJxJIDaf8M7x3
        PfWP2wiybjO4lLs5D9Ji37KlEZNtyy1CILR58kbIggQetfAUZJ2WGWMjneZC9bqA
        tf8iThgTbH6mzAfLnnxWxkR0nc3CylVoA9RJNQyfzLkk4X9tCGiQpYbq+kGAeH5/
        7xXzXZF+S85vQrIN7UaiGTeDykhQ1+Vcr4utMwl0uBZdHGJ0vEYgZ3bJEE+0HP2m
        FQIDAQAB
        -----END PUBLIC KEY-----",
                "xsappname": "dummy-app",
                "tenantid": "XSUAA_Tenant",
                "identityzone": "ma"
              },
              "plan": "broker"
            }
          ]
        }
        """;

    private DefaultAuthTokenFacade mockedAuthTokenFacade;

    @BeforeEach
    void before()
    {
        DefaultServiceBindingAccessor
            .setInstance(
                new SapVcapServicesServiceBindingAccessor(
                    Collections.singletonMap("VCAP_SERVICES", VCAP_SERVICES)::get));

        // resetting the facades to the CF ones, as we want to test that the content of the JWT is actually read
        PrincipalAccessor.setPrincipalFacade(new DefaultPrincipalFacade());
        TenantAccessor.setTenantFacade(new DefaultTenantFacade());

        mockedAuthTokenFacade = Mockito.spy(new DefaultAuthTokenFacade());
        AuthTokenAccessor.setAuthTokenFacade(mockedAuthTokenFacade);
    }

    @AfterEach
    void resetFacades()
    {
        DefaultServiceBindingAccessor.setInstance(null);
        PrincipalAccessor.setPrincipalFacade(null);
        TenantAccessor.setTenantFacade(null);
        AuthTokenAccessor.setAuthTokenFacade(null);
    }

    @Test
    void testOfTenantWithIdAndSubdomain()
    {
        final String tenantId = "my-tenant";
        final String subdomain = "my-subdomain";
        final String subscriberIssuerUrl = "https://" + subdomain + ".localhost:8080/uaa/oauth/token";
        final List<String> audiences = Collections.singletonList("my-app!t123");

        final String jwt =
            JWT
                .create()
                .withClaim("zid", tenantId)
                .withClaim("iss", subscriberIssuerUrl)
                .withAudience(audiences.toArray(new String[0]))
                .sign(Algorithm.none());
        mockedAuthTokenFacade.executeWithAuthToken(new AuthToken(JWT.decode(jwt)), () -> {
            VavrAssertions.assertThat(mockedAuthTokenFacade.tryGetCurrentToken()).isSuccess();

            assertThat(mockedAuthTokenFacade.tryGetCurrentToken().get().getJwt().getClaim("user_name"))
                .matches(Claim::isMissing, "missing 'user_name' claim");
            assertThat(mockedAuthTokenFacade.tryGetCurrentToken().get().getJwt().getClaim("zid").asString())
                .isEqualTo(tenantId);
            assertThat(mockedAuthTokenFacade.tryGetCurrentToken().get().getJwt().getClaim("iss").asString())
                .isEqualTo(subscriberIssuerUrl);
            assertThat(mockedAuthTokenFacade.tryGetCurrentToken().get().getJwt().getAudience())
                .hasSameElementsAs(audiences);

            final Tenant tenant = TenantAccessor.getCurrentTenant();
            assertThat(tenant.getTenantId()).isEqualTo(tenantId);
            assertThat(tenant).isInstanceOf(DefaultTenant.class);
            assertThat(((TenantWithSubdomain) tenant).getSubdomain()).isEqualTo(subdomain);

            VavrAssertions.assertThat(PrincipalAccessor.tryGetCurrentPrincipal()).isFailure();

            return null;
        });
    }

    @Test
    void testOfTenantWithAllArguments()
    {
        final String tenantId = "my-tenant";
        final String subdomain = "my-subdomain";
        final String xsAppName = "my-app!t123";

        final String providerIssuerUrl = "https://my-provider.authentication.sap.hana.ondemand.com/oauth/token";

        final String jwt = createJwtForTenantAndSubdomain(tenantId, subdomain, xsAppName, providerIssuerUrl);
        mockedAuthTokenFacade.executeWithAuthToken(new AuthToken(JWT.decode(jwt)), () -> {
            VavrAssertions.assertThat(mockedAuthTokenFacade.tryGetCurrentToken()).isSuccess();

            assertThat(mockedAuthTokenFacade.tryGetCurrentToken().get().getJwt().getClaim("user_name"))
                .matches(Claim::isMissing, "missing 'user_name' claim");
            assertThat(mockedAuthTokenFacade.tryGetCurrentToken().get().getJwt().getClaim("zid").asString())
                .isEqualTo(tenantId);
            assertThat(mockedAuthTokenFacade.tryGetCurrentToken().get().getJwt().getClaim("iss").asString())
                .isEqualTo("https://" + subdomain + ".authentication.sap.hana.ondemand.com/oauth/token");
            assertThat(mockedAuthTokenFacade.tryGetCurrentToken().get().getJwt().getAudience()).containsOnly(xsAppName);

            final Tenant tenant = TenantAccessor.getCurrentTenant();
            assertThat(tenant.getTenantId()).isEqualTo(tenantId);
            assertThat(tenant).isInstanceOf(DefaultTenant.class);
            assertThat(((TenantWithSubdomain) tenant).getSubdomain()).isEqualTo(subdomain);

            VavrAssertions.assertThat(PrincipalAccessor.tryGetCurrentPrincipal()).isFailure();

            return null;
        });
    }

    @SneakyThrows
    private static String createJwtForTenantAndSubdomain(
        final String tenantId,
        final String subdomain,
        final String xsAppName,
        final String xsuaaUrl )
    {
        URI uri = new URI(xsuaaUrl);
        final String newHost = subdomain + uri.getHost().substring(uri.getHost().indexOf("."));
        uri = new URI(uri.getScheme(), null, newHost, uri.getPort(), uri.getPath(), null, null);
        return JWT
            .create()
            .withClaim("zid", tenantId)
            .withClaim("iss", uri.toString())
            .withAudience(xsAppName)
            .sign(Algorithm.none());
    }

    @Test
    void testWithoutJwt()
    {
        assertThat(TenantAccessor.getCurrentTenant().getTenantId()).isEqualTo("XSUAA_Tenant");
        VavrAssertions.assertThat(PrincipalAccessor.tryGetCurrentPrincipal()).isFailure();
    }
}
