package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestinationBuilderProxyHandler.SapConnectivityAuthenticationHeaderProvider;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestinationBuilderProxyHandler.SapConnectivityLocationIdHeaderProvider;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceOptionsAugmenter.augmenter;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceRetrievalStrategy.ALWAYS_PROVIDER;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceRetrievalStrategy.CURRENT_TENANT;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceRetrievalStrategy.ONLY_SUBSCRIBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.parallel.Isolated;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.google.common.collect.ImmutableMap;
import com.sap.cloud.environment.servicebinding.api.DefaultServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.api.DefaultServiceBindingBuilder;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.security.exception.AuthTokenAccessException;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.testutil.TestContext;

import io.vavr.control.Try;
import lombok.Value;

@Isolated( "Test modifies global service bindings" )
@WireMockTest
class DestinationServicePrincipalPropagationTest
{
    private static final Tenant NO_TENANT = null;
    private static final Tenant SOME_TENANT_1 = new DefaultTenant("foo");
    private static final Tenant SOME_TENANT_2 = new DefaultTenant("bar");
    private static final String DESTINATION = """
        {
          "destinationConfiguration": {
            "URL": "https://example.com",
            "Name": "test",
            "ProxyType": "OnPremise",
            "Authentication": "PrincipalPropagation",
            "CloudConnectorLocationId":"LOC1",
            "Type": "HTTP"
          }
        }
        """;

    @RegisterExtension
    static TestContext context = TestContext.withThreadContext().resetCaches();

    private final DestinationServiceAdapter destinationServiceAdapter = mock(DestinationServiceAdapter.class);

    @BeforeEach
    void setupConnectivity( @Nonnull final WireMockRuntimeInfo wm )
    {
        final ImmutableMap<String, Object> credentials =
            ImmutableMap
                .<String, Object> builder()
                .put("clientid", "CLIENT_ID")
                .put("clientsecret", "CLIENT_SECRET")
                .put("url", "http://localhost:" + wm.getHttpPort() + "/xsuaa/oauth/token")
                .put("onpremise_proxy_host", "localhost")
                .put("onpremise_proxy_port", "8888")
                .build();

        final ServiceBinding connectivityService =
            new DefaultServiceBindingBuilder()
                .withServiceIdentifier(ServiceIdentifier.CONNECTIVITY)
                .withCredentials(credentials)
                .build();

        DefaultServiceBindingAccessor.setInstance(() -> List.of(connectivityService));

       // doReturn(DESTINATION)
       //     .when(destinationServiceAdapter)
       //     .getConfigurationAsJson(anyString(), any(OnBehalfOf.class));

        stubFor(
            post("/xsuaa/oauth/token")
                .withRequestBody(
                    equalTo("grant_type=client_credentials&client_secret=CLIENT_SECRET&client_id=CLIENT_ID"))
                .willReturn(okJson("{\"access_token\":\"provider-client-credentials\",\"expires_in\":3600}")));
    }

    @AfterEach
    void tearDownConnectivity()
    {
        DefaultServiceBindingAccessor.setInstance(null);
    }

    @Test
    void testFailingHeadersWithNoCurrentToken()
    {
        final DestinationService sut = new DestinationService(destinationServiceAdapter);
        final Destination result = sut.tryGetDestination("test").get();

        // assertions
        assertThat(result).isInstanceOf(DefaultHttpDestination.class);
        assertThatThrownBy(((DefaultHttpDestination) result)::getHeaders)
            .isInstanceOf(AuthTokenAccessException.class)
            .hasMessage("Failed to get current authorization token.");

        // assert mocks
        // verify(destinationServiceAdapter)
        //     .getConfigurationAsJson(contains("test"), eq(OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT));
    }

    @Test
    void testSuccessWithOnlyToken()
    {
        context.setAuthToken();

        // test
        final DestinationService sut = new DestinationService(destinationServiceAdapter);
        final Destination result = sut.tryGetDestination("test").get();

        // assertions
        assertThat(result).isNotNull();
        assertThat(result.get(DestinationProperty.URI)).containsExactly("https://example.com");
        assertThat(result.get(DestinationProperty.PROXY_TYPE)).containsExactly(ProxyType.ON_PREMISE);
        assertThat(result.get(DestinationProperty.PROXY_URI)).containsExactly(URI.create("http://localhost:8888"));
        assertThat(result.get(DestinationProperty.TENANT_ID)).containsExactly(""); // empty tenant id = provider

        assertThat(result).isInstanceOf(DefaultHttpDestination.class);
        assertThat(((DefaultHttpDestination) result).getCustomHeaderProviders())
            .satisfiesExactly(
                provider -> assertThat(provider).isInstanceOf(SapConnectivityLocationIdHeaderProvider.class),
                provider -> assertThat(provider).isInstanceOf(SapConnectivityAuthenticationHeaderProvider.class),
                provider -> assertThat(provider).isInstanceOf(OAuth2HeaderProvider.class));

        // assert headers
        WireMock.verify(0, postRequestedFor(anyUrl()));
        assertThat(((DefaultHttpDestination) result).getHeaders())
            .containsExactlyInAnyOrder(
                new Header("SAP-Connectivity-SCC-Location_ID", "LOC1"),
                new Header("SAP-Connectivity-Authentication", "Bearer eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.e30."),
                new Header("Proxy-Authorization", "Bearer provider-client-credentials"));
        WireMock.verify(1, postRequestedFor(anyUrl()));

        // assert mocks
        // verify(destinationServiceAdapter)
        //     .getConfigurationAsJson(contains("test"), eq(OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT));
    }

    @Test
    void testFailingHeadersWithProviderDestinationAndSubscriberTenant()
    {
        context.setAuthToken();

        // test
        final DestinationService sut = new DestinationService(destinationServiceAdapter);
        final Destination result = sut.tryGetDestination("test").get();

        // change tenant to subscriber
        context.setTenant("subscriber");

        // assertion
        assertThat(result).isInstanceOf(DefaultHttpDestination.class);
        assertThatThrownBy(((DefaultHttpDestination) result)::getHeaders)
            .isInstanceOf(IllegalStateException.class)
            .hasMessage(
                "Tenant ID of destination 'test' does not match the current tenant ID. Destination was created specifically for tenant '', but the current tenant is 'subscriber'.");

        // assert mocks
        // verify(destinationServiceAdapter)
        //     .getConfigurationAsJson(contains("test"), eq(OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT));
    }

    @Test
    void testDestinationOptions()
    {
        context.setAuthToken();

        final DestinationServiceRetrievalStrategy NO_STRATEGY = null;
        final Case[] cases =
            {
                //  TEST        TENANT GET   TENANT GET   RETRIEVAL
                //  ASSERTION   DESTINATION  HEADERS      STRATEGY
                // =========== ============ ============ ============

                // same provider tenant
                Case.successful(NO_TENANT, NO_TENANT, NO_STRATEGY),
                Case.successful(NO_TENANT, NO_TENANT, ALWAYS_PROVIDER),
                Case.successful(NO_TENANT, NO_TENANT, CURRENT_TENANT),
                Case.failRetrie(NO_TENANT, ONLY_SUBSCRIBER), // only-subscriber cannot be satisfied without current  tenant

                // same current tenant
                Case.successful(SOME_TENANT_1, SOME_TENANT_1, NO_STRATEGY),
                Case.failHeader(SOME_TENANT_1, SOME_TENANT_1, ALWAYS_PROVIDER),
                Case.successful(SOME_TENANT_1, SOME_TENANT_1, CURRENT_TENANT),
                Case.successful(SOME_TENANT_1, SOME_TENANT_1, ONLY_SUBSCRIBER),

                // different current tenants
                Case.failHeader(SOME_TENANT_1, SOME_TENANT_2, NO_STRATEGY),
                Case.failHeader(SOME_TENANT_1, SOME_TENANT_2, ALWAYS_PROVIDER),
                Case.failHeader(SOME_TENANT_1, SOME_TENANT_2, CURRENT_TENANT),
                Case.failHeader(SOME_TENANT_1, SOME_TENANT_2, ONLY_SUBSCRIBER),

                // mixed provider and current tenants
                Case.failHeader(NO_TENANT, SOME_TENANT_1, NO_STRATEGY),
                Case.failHeader(NO_TENANT, SOME_TENANT_1, ALWAYS_PROVIDER),
                Case.failHeader(NO_TENANT, SOME_TENANT_1, CURRENT_TENANT),
                Case.failHeader(SOME_TENANT_1, NO_TENANT, NO_STRATEGY),
                Case.successful(SOME_TENANT_1, NO_TENANT, ALWAYS_PROVIDER),
                Case.failHeader(SOME_TENANT_1, NO_TENANT, CURRENT_TENANT),
                Case.failHeader(SOME_TENANT_1, NO_TENANT, ONLY_SUBSCRIBER), };

        for( final Case c : cases ) {
            // set initial tenant
            context.setTenant(c.destinationTenant);

            // test
            final DestinationService sut = new DestinationService(destinationServiceAdapter);
            final Try<Destination> tryGetDestination = sut.tryGetDestination("test", c.options);

            // assertion retrieval
            c.assertRetrieval.accept(tryGetDestination);

            // change runtime tenant
            context.setTenant(c.runtimeTenant);

            // assertion headers
            final Try<Collection<Header>> tryGetHeaders =
                tryGetDestination.map(DefaultHttpDestination.class::cast).map(DefaultHttpDestination::getHeaders);
            c.assertHeaders.accept(tryGetHeaders);
        }
    }

    @Value
    static class Case
    {
        Tenant destinationTenant;
        Tenant runtimeTenant;
        DestinationOptions options;
        Consumer<Try<Destination>> assertRetrieval;
        Consumer<Try<Collection<Header>>> assertHeaders;

        // test case: successfully get destination and on-premise headers
        static Case successful(
            final Tenant destinationTenant,
            final Tenant runtimeTenant,
            final DestinationServiceRetrievalStrategy strategy )
        {
            final DestinationOptions.Builder options = DestinationOptions.builder();
            if( strategy != null ) {
                options.augmentBuilder(augmenter().retrievalStrategy(strategy));
            }
            final Consumer<Try<Destination>> assertDestination = dest -> {
                assertThat(dest).isNotEmpty();
                assertThat(dest.get()).isInstanceOf(DefaultHttpDestination.class);
            };
            final Consumer<Try<Collection<Header>>> assertHeaders = headers -> assertThat(headers.get()).isNotEmpty();
            return new Case(destinationTenant, runtimeTenant, options.build(), assertDestination, assertHeaders);
        }

        // test case: successfully get destination, but failing to get on-premise headers
        static Case failHeader(
            final Tenant destinationTenant,
            final Tenant runtimeTenant,
            final DestinationServiceRetrievalStrategy strategy )
        {
            final DestinationOptions.Builder options = DestinationOptions.builder();
            if( strategy != null ) {
                options.augmentBuilder(augmenter().retrievalStrategy(strategy));
            }
            final Consumer<Try<Destination>> assertDestination = dest -> {
                assertThat(dest).isNotEmpty();
                assertThat(dest.get()).isInstanceOf(DefaultHttpDestination.class);
            };
            final Consumer<Try<Collection<Header>>> assertHeaders = headers -> {
                final String descr = "Expecting header error for dest tenant %s, runtime tenant %s and strategy %s.";
                assertThat(headers).describedAs(descr, destinationTenant, runtimeTenant, strategy).isEmpty();
                assertThat(headers.getCause()).isInstanceOf(IllegalStateException.class);
            };
            return new Case(destinationTenant, runtimeTenant, options.build(), assertDestination, assertHeaders);
        }

        // test case: failing to get destination
        static Case failRetrie( final Tenant destinationTenant, final DestinationServiceRetrievalStrategy strategy )
        {
            final DestinationOptions.Builder options = DestinationOptions.builder();
            if( strategy != null ) {
                options.augmentBuilder(augmenter().retrievalStrategy(strategy));
            }
            final Consumer<Try<Destination>> assertDestination = dest -> {
                final String descr = "Expecting retrieval error for tenant %s and strategy %s.";
                assertThat(dest).describedAs(descr, destinationTenant, strategy).isEmpty();
                assertThat(dest.getCause()).isInstanceOf(DestinationAccessException.class);
            };
            final Consumer<Try<Collection<Header>>> assertHeaders = headers -> {
            };
            return new Case(destinationTenant, destinationTenant, options.build(), assertDestination, assertHeaders);
        }
    }
}
