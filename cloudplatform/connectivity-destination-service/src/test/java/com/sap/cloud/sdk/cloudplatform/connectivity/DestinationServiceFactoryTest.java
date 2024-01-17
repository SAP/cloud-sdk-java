package com.sap.cloud.sdk.cloudplatform.connectivity;

import static java.util.Collections.singletonList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceV1Response.DestinationAuthToken;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceV1Response.DestinationCertificate;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

class DestinationServiceFactoryTest
{
    private DestinationServiceV1Response response;

    @BeforeEach
    void setup()
    {
        response = new DestinationServiceV1Response();
        response.setDestinationConfiguration(new HashMap<>());
    }

    @Test
    void testTenantIsSet()
    {
        final Tenant tenant = new DefaultTenant("tenant1");

        response.getDestinationConfiguration().put("Type", "HTTP");
        response.getDestinationConfiguration().put("URL", "http://foo/");

        final Destination result =
            TenantAccessor
                .executeWithTenant(tenant, () -> DestinationServiceFactory.fromDestinationServiceV1Response(response));

        assertThat(result).isInstanceOf(DefaultHttpDestination.class);
        assertThat(result.get(DestinationProperty.TENANT_ID)).contains(tenant.getTenantId());
    }

    @Test
    void testProviderTenantIsEmptyString()
    {
        response.getDestinationConfiguration().put("Type", "HTTP");
        response.getDestinationConfiguration().put("URL", "http://foo/");

        final Destination result = DestinationServiceFactory.fromDestinationServiceV1Response(response);

        assertThat(result).isInstanceOf(DefaultHttpDestination.class);
        assertThat(result.get(DestinationProperty.TENANT_ID)).contains("");
    }

    @Test
    void testDefaultDestination()
    {
        response.getDestinationConfiguration().put("foo", "bar");

        final Destination result = DestinationServiceFactory.fromDestinationServiceV1Response(response);

        assertThat(result).isInstanceOf(DefaultDestination.class);
        assertThat(result.get("foo")).contains("bar");
    }

    @Test
    void testRfcDestination()
    {
        response.getDestinationConfiguration().put("Type", "RFC");
        response.getDestinationConfiguration().put("Name", "rfcDestination");

        final Destination result = DestinationServiceFactory.fromDestinationServiceV1Response(response);

        assertThat(result).isInstanceOf(DefaultRfcDestination.class);
        assertThat(result.get(DestinationProperty.TYPE)).contains(DestinationType.RFC);
        assertThat(result.get(DestinationProperty.NAME)).contains("rfcDestination");
    }

    @Test
    void testHttpDestination()
    {
        response.getDestinationConfiguration().put("Type", "HTTP");
        response.getDestinationConfiguration().put("Name", "httpDestination");
        response.getDestinationConfiguration().put("URL", "https://example.com");

        final DestinationAuthToken token = new DestinationAuthToken();
        response.setAuthTokens(singletonList(token));

        final DestinationCertificate certificate = new DestinationCertificate();
        response.setCertificates(singletonList(certificate));

        final Destination result = DestinationServiceFactory.fromDestinationServiceV1Response(response);

        assertThat(result).isInstanceOf(DefaultHttpDestination.class);
        assertThat(result.get(DestinationProperty.TYPE)).contains(DestinationType.HTTP);
        assertThat(result.get(DestinationProperty.NAME)).contains("httpDestination");
        assertThat(result.get(DestinationProperty.URI)).contains("https://example.com");
        assertThat(result.get(DestinationProperty.AUTH_TOKENS)).containsExactly(singletonList(token));
        assertThat(result.get(DestinationProperty.CERTIFICATES)).containsExactly(singletonList(certificate));
    }

    @Test
    void testTokenErrorsAreThrown()
    {
        response.getDestinationConfiguration().put("Type", "HTTP");
        response.getDestinationConfiguration().put("Name", "httpDestination");
        response.getDestinationConfiguration().put("URL", "https://example.com");

        final DestinationAuthToken tokenSuccess = new DestinationAuthToken();
        final DestinationAuthToken tokenFailure = new DestinationAuthToken();
        tokenSuccess.setValue("success");
        tokenFailure.setError("some-error-message");
        response.setAuthTokens(List.of(tokenSuccess, tokenFailure));

        assertThatThrownBy(() -> DestinationServiceFactory.fromDestinationServiceV1Response(response))
            .isInstanceOf(DestinationAccessException.class)
            .hasMessageContaining("some-error-message");
    }
}
