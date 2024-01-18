package com.sap.cloud.sdk.cloudplatform.connectivity;

import com.google.common.net.HttpHeaders;
import com.google.gson.Gson;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderAccessor;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class ForwardAuthTokenTest
{
    @Test
    void localDestinationWithPropertyAndNoAuthenticationShouldForwardIncomingAuthorizationHeader()
    {
        final Map<String, Object> properties =
            Map
                .of(
                    DestinationProperty.FORWARD_AUTH_TOKEN.getKeyName(),
                     "true",
                    DestinationProperty.AUTH_TYPE.getKeyName(),
                    AuthenticationType.NO_AUTHENTICATION);

        final HttpDestination destination = DefaultDestination.fromMap(properties).uri("sap.com").build().asHttp();
        final Collection<Header> headers =
            RequestHeaderAccessor
                .executeWithHeaderContainer(Map.of(HttpHeaders.AUTHORIZATION, "token"), () -> destination.getHeaders());

        assertThat(headers).containsExactly(new Header(HttpHeaders.AUTHORIZATION, "token"));
    }

    @Test
    void localDestinationWithoutPropertyAndNoAuthenticationShouldNotForwardIncomingAuthorizationHeader()
    {
        final Map<String, Object> properties =
            Map
                .of(
                    DestinationProperty.AUTH_TYPE.getKeyName(),
                    AuthenticationType.NO_AUTHENTICATION);

        final HttpDestination destination = DefaultDestination.fromMap(properties).uri("sap.com").build().asHttp();
        final Collection<Header> headers =
            RequestHeaderAccessor
                .executeWithHeaderContainer(Map.of(HttpHeaders.AUTHORIZATION, "token"), () -> destination.getHeaders());

        assertThat(headers).isEmpty();
    }

    @Test
    void localDestinationWithPropertyAndUnsetAuthenticationShouldForwardIncomingAuthorizationHeader()
    {
        final Map<String, Object> properties =
                Map
                        .of(
                                DestinationProperty.FORWARD_AUTH_TOKEN.getKeyName(),
                                "true");

        final HttpDestination destination = DefaultDestination.fromMap(properties).uri("sap.com").build().asHttp();
        final Collection<Header> headers =
                RequestHeaderAccessor
                        .executeWithHeaderContainer(Map.of(HttpHeaders.AUTHORIZATION, "token"), () -> destination.getHeaders());

        assertThat(headers).containsExactly(new Header(HttpHeaders.AUTHORIZATION, "token"));
    }

    @Test
    void localDestinationWithFalsePropertyAndUnsetAuthenticationShouldNotForwardIncomingAuthorizationHeader()
    {
        final Map<String, Object> properties =
                Map
                        .of(
                                DestinationProperty.FORWARD_AUTH_TOKEN.getKeyName(),
                                "false");

        final HttpDestination destination = DefaultDestination.fromMap(properties).uri("sap.com").build().asHttp();
        final Collection<Header> headers =
                RequestHeaderAccessor
                        .executeWithHeaderContainer(Map.of(HttpHeaders.AUTHORIZATION, "token"), () -> destination.getHeaders());

        assertThat(headers).isEmpty();
    }

    @Test
    void serviceDestinationWithPropertyAnNoAuthenticationShouldForwardIncomingAuthorizationHeader()
    {
        final String destinationName = "SomeDestinationName";

        final DestinationServiceAdapter adapter =
            spy(
                new DestinationServiceAdapter(
                    behalf -> DefaultHttpDestination.builder("").build(),
                    () -> mock(ServiceBinding.class),
                    "providerTenantId"));
        final DestinationLoader loader =
            new DestinationService(
                adapter,
                ResilienceConfiguration.empty("testSingle"),
                ResilienceConfiguration.empty("testMultiple"));

        doReturn("""
                {
                "owner": {
                    "SubaccountId": "someId",
                    "InstanceId": null
                },
                "destinationConfiguration": {
                    "Name": "%s",
                    "Type": "HTTP",
                    "URL": "sap.com",
                    "Authentication": "NoAuthentication",
                    "ProxyType": "Internet",
                    "Description": "Test destination",
                    "forwardAuthToken": "true"
                }
            }
                """.formatted(destinationName))
            .when(adapter)
            .getConfigurationAsJson(eq("/destinations/" + destinationName), any());

        final HttpDestination destination = loader.tryGetDestination(destinationName).get().asHttp();

        final Collection<Header> headers =
            RequestHeaderAccessor
                .executeWithHeaderContainer(Map.of(HttpHeaders.AUTHORIZATION, "token"), () -> destination.getHeaders());

        assertThat(headers).containsExactly(new Header(HttpHeaders.AUTHORIZATION, "token"));
    }
}
