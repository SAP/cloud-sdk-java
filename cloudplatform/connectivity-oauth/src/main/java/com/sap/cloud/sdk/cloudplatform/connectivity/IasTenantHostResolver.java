package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONObject;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * Resolves the IAS tenant host for a given tenant ID by querying the BTP tenant API.
 * <p>
 * The endpoint returns OIDC metadata including a {@code token_endpoint}. The IAS host subdomain is extracted from the
 * first host label of that URL, which identifies the IAS tenant.
 */
@Slf4j
class IasTenantHostResolver
{
    static final IasTenantHostResolver DEFAULT_INSTANCE = new IasTenantHostResolver();
    private static final String TENANT_INFO_ENDPOINT_TEMPLATE = "/sap/rest/tenantLoginInfo?id=%s";

    private final CloseableHttpClient httpClient;

    private IasTenantHostResolver()
    {
        this.httpClient = HttpClients.createDefault();
    }

    /**
     * Queries {@code btpTenantApiUri} with {@code ?id=<tenantId>} and extracts the IAS tenant subdomain from the
     * {@code token_endpoint} field in the JSON response.
     *
     * @param btpTenantApiUri
     *            The full URL of the BTP tenant login-info endpoint.
     * @param tenantId
     *            The tenant ID (app_tid/subaccount ID) to look up.
     * @return The subdomain extracted from the {@code token_endpoint} host.
     * @throws DestinationAccessException
     *             if the HTTP request fails, the response is not 200, or the subdomain cannot be parsed from the
     *             response.
     */
    @Nonnull
    String resolve( @Nonnull final URI btpTenantApiUri, @Nonnull final String tenantId )
    {
        val url = btpTenantApiUri.resolve(TENANT_INFO_ENDPOINT_TEMPLATE.formatted(tenantId));
        log.debug("Dynamically resolving IAS tenant host for tenant '{}' via {}.", tenantId, url);
        val req = new HttpGet(url);
        try {
            return httpClient.execute(req, response -> {
                if( response.getCode() != HttpStatus.SC_OK ) {
                    throw new DestinationAccessException(
                        "Failed to query BTP tenant API: Server returned status code %d for GET request to '%s'."
                            .formatted(response.getCode(), url));
                }
                val body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                return extractSubdomainFromTokenEndpoint(body);
            });
        }
        catch( IOException e ) {
            throw new DestinationAccessException("Failed to query BTP tenant API: " + e.getMessage(), e);
        }
    }

    @Nonnull
    static String extractSubdomainFromTokenEndpoint( @Nonnull final String responseBody )
    {
        try {
            final String tokenEndpoint = new JSONObject(responseBody).getString("token_endpoint");
            final String host = URI.create(tokenEndpoint).getHost();
            return host.substring(0, host.indexOf('.'));
        }
        catch( final Exception e ) {
            throw new DestinationAccessException(
                "Failed to extract IAS tenant host from the BTP tenant API response. The response did not conform to the expected format: "
                    + responseBody,
                e);
        }
    }
}
