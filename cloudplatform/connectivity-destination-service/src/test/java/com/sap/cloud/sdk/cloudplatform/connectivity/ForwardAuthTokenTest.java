package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.net.HttpHeaders;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderAccessor;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;

public class ForwardAuthTokenTest
{
    static Collection<TestCase> createTestCases()
    {
        return List
            .of(
                // forwardAuthToken = true && authType = NoAuthentication
                new TestCase(
                    Map
                        .of(
                            DestinationProperty.FORWARD_AUTH_TOKEN.getKeyName(),
                            "true",
                            DestinationProperty.AUTH_TYPE.getKeyName(),
                            AuthenticationType.NO_AUTHENTICATION),
                    AssertionType.TOKEN_IS_FORWARDED),
                // forwardAuthToken = true && unset authType
                new TestCase(
                    Map.of(DestinationProperty.FORWARD_AUTH_TOKEN.getKeyName(), "true"),
                    AssertionType.TOKEN_IS_FORWARDED),
                // forwardAuthToken = false && authType = NoAuthentication
                new TestCase(
                    Map
                        .of(
                            DestinationProperty.FORWARD_AUTH_TOKEN.getKeyName(),
                            "false",
                            DestinationProperty.AUTH_TYPE.getKeyName(),
                            AuthenticationType.NO_AUTHENTICATION),
                    AssertionType.TOKEN_IS_NOT_FORWARDED),
                // forwardAuthToken = false && unset authType
                new TestCase(
                    Map.of(DestinationProperty.FORWARD_AUTH_TOKEN.getKeyName(), "false"),
                    AssertionType.TOKEN_IS_NOT_FORWARDED),
                // unset forwardAuthToken && authType = NoAuthentication
                new TestCase(
                    Map.of(DestinationProperty.AUTH_TYPE.getKeyName(), AuthenticationType.NO_AUTHENTICATION),
                    AssertionType.TOKEN_IS_NOT_FORWARDED),
                // unset forwardAuthToken && unset authType
                new TestCase(Map.of(), AssertionType.TOKEN_IS_NOT_FORWARDED)

            );
    }

    private enum AssertionType
    {
        TOKEN_IS_FORWARDED(ForwardAuthTokenTest::assertThatTokenIsForwarded),
        TOKEN_IS_NOT_FORWARDED(ForwardAuthTokenTest::assertThatTokenIsNotForwarded);

        private final Consumer<HttpDestination> assertion;

        AssertionType( final Consumer<HttpDestination> assertion )
        {
            this.assertion = assertion;
        }
    }

    private record TestCase( Map<String, Object> properties, AssertionType forwardingAssertion )
    {
    }

    @ParameterizedTest
    @MethodSource( "createTestCases" )
    void localDestinationShouldFulfillTestCase( @Nonnull final TestCase testCase )
    {
        final HttpDestination destination = buildLocalDestination(testCase.properties);
        testCase.forwardingAssertion.assertion.accept(destination);
    }

    @ParameterizedTest
    @MethodSource( "createTestCases" )
    void destinationServiceDestinationShouldFulfillTestCase( @Nonnull final TestCase testCase )
    {
        final HttpDestination destination = buildDestinationServiceDestination(testCase.properties);
        testCase.forwardingAssertion.assertion.accept(destination);
    }

    private HttpDestination buildDestinationServiceDestination( final Map<String, Object> properties )
    {
        // to circumvent caching in the destination loader we generate a random destination name here
        final String destinationName = "SomeDestinationName" + UUID.randomUUID();
        final String serviceResponse = buildServiceResponse(destinationName, properties);

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

        doReturn(serviceResponse).when(adapter).getConfigurationAsJson(eq("/destinations/" + destinationName), any());

        return loader.tryGetDestination(destinationName).get().asHttp();
    }

    private String buildServiceResponse( final String destinationName, final Map<String, Object> properties )
    {
        final JsonElement response = JsonParser.parseString("""
            {
                "owner": {
                    "SubaccountId": "someId",
                    "InstanceId": null
                },
                "destinationConfiguration": {
                    "Name": "%s",
                    "Type": "HTTP",
                    "URL": "sap.com",
                    "ProxyType": "Internet",
                    "Description": "Test destination"
                }
            }
            """.formatted(destinationName));
        final JsonObject config = response.getAsJsonObject().get("destinationConfiguration").getAsJsonObject();
        for( final Map.Entry<String, Object> entry : properties.entrySet() ) {
            config.add(entry.getKey(), new JsonPrimitive(entry.getValue().toString()));
        }
        return response.toString();
    }

    private HttpDestination buildLocalDestination( Map<String, Object> properties )
    {
        return DefaultDestination.fromMap(properties).uri("sap.com").build().asHttp();
    }

    private static void assertThatTokenIsNotForwarded( final HttpDestination destination )
    {
        final Collection<Header> headers =
            RequestHeaderAccessor
                .executeWithHeaderContainer(Map.of(HttpHeaders.AUTHORIZATION, "token"), () -> destination.getHeaders());

        assertThat(headers).isEmpty();
    }

    private static void assertThatTokenIsForwarded( final HttpDestination destination )
    {
        final Collection<Header> headers =
            RequestHeaderAccessor
                .executeWithHeaderContainer(Map.of(HttpHeaders.AUTHORIZATION, "token"), () -> destination.getHeaders());

        assertThat(headers).containsExactly(new Header(HttpHeaders.AUTHORIZATION, "token"));
    }
}
