package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ServiceBindingDestinationOptions.Options.ProxyOptions;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ServiceBindingTestUtility.bindingWithCredentials;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.Collection;
import java.util.Map;

import org.apache.http.HttpHeaders;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;

@SuppressWarnings( "unchecked" )
public class OAuth2OnPremiseIntegrationTest
{

    @Rule
    public WireMockRule csMockServer = new WireMockRule(wireMockConfig().dynamicPort());

    @Test
    public void destinationGetHeadersShouldRetrieveProxyAuthHeaderJustOnce()
    {
        stubFor(
            post("/oauth/token")
                .willReturn(
                    okJson(
                        "{\"access_token\": \"token\", \"token_type\": \"Bearer\", \"expires_in\": 50000, \"scope\": \"uaa.resource\", \"jti\": \"abc456\"}")));

        final DefaultHttpDestination destToBeProxied = DefaultHttpDestination.builder("target.system").build();

        final ServiceBindingDestinationOptions connectivityOptions =
            createOptionsWithCredentials(
                destToBeProxied,
                entry("url", csMockServer.baseUrl()),
                entry("clientid", "clientid"),
                entry("clientsecret", "clientsecret"),
                entry("onpremise_proxy_host", "some.host"),
                entry("onpremise_proxy_http_port", 1234));

        final OAuth2ServiceBindingDestinationLoader loader = new OAuth2ServiceBindingDestinationLoader();

        final HttpDestination destination1 = loader.getDestination(connectivityOptions);
        final HttpDestination destination2 = loader.getDestination(connectivityOptions);

        // No caching of destinations
        assertThat(destination1).isNotSameAs(destination2);

        final Collection<Header> headers1 = destination1.getHeaders();
        final Collection<Header> headers2 = destination2.getHeaders();

        assertThat(headers1).containsExactlyInAnyOrderElementsOf(headers2);
        assertThat(headers1).contains(new Header(HttpHeaders.PROXY_AUTHORIZATION, "Bearer token"));

        verify(1, postRequestedFor(urlEqualTo("/oauth/token")));
    }

    private ServiceBindingDestinationOptions createOptionsWithCredentials(
        final HttpDestination destToBeProxied,
        final Map.Entry<String, Object>... entries )
    {
        final ServiceBinding binding = bindingWithCredentials(ServiceIdentifier.CONNECTIVITY, entries);

        return ServiceBindingDestinationOptions
            .forService(binding)
            .withOption(ProxyOptions.destinationToBeProxied(destToBeProxied))
            .build();
    }
}
