/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationRetrievalStrategy.withUserToken;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationRetrievalStrategy.withoutToken;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceOptionsAugmenter.DESTINATION_RETRIEVAL_STRATEGY_KEY;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceOptionsAugmenter.DESTINATION_TOKEN_EXCHANGE_STRATEGY_KEY;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceOptionsAugmenter.augmenter;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceRetrievalStrategy.ALWAYS_PROVIDER;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceRetrievalStrategy.CURRENT_TENANT;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceRetrievalStrategy.ONLY_SUBSCRIBER;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceTokenExchangeStrategy.EXCHANGE_ONLY;
import static com.sap.cloud.sdk.cloudplatform.connectivity.OnBehalfOf.NAMED_USER_CURRENT_TENANT;
import static com.sap.cloud.sdk.cloudplatform.connectivity.OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT;
import static com.sap.cloud.sdk.cloudplatform.connectivity.OnBehalfOf.TECHNICAL_USER_PROVIDER;
import static com.sap.cloud.sdk.cloudplatform.connectivity.XsuaaTokenMocker.mockXsuaaToken;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.parallel.Isolated;
import org.mockito.stubbing.Answer;

import com.auth0.jwt.JWT;
import com.google.gson.stream.MalformedJsonException;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.sdk.cloudplatform.cache.CacheKey;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration.TimeLimiterConfiguration;
import com.sap.cloud.sdk.cloudplatform.security.principal.DefaultPrincipal;
import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutors;
import com.sap.cloud.sdk.testutil.TestContext;

import io.vavr.control.Try;
import lombok.SneakyThrows;

@Isolated( "Test interacts with global destination cache" )
class DestinationServiceTest
{
    private static final int TEST_TIMEOUT = 30_000; // 5 minutes
    // region (TEST_DATA)
    private static final String destinationName = "SomeDestinationName";
    private static final String providerUrl = "https://service.provider.com";
    private static final String subscriberUrl = "https://service.subscriber.com";

    private static final String responseSubaccountDestination = """
        [{
            "Name": "CC8-HTTP-BASIC",
            "Type": "HTTP",
            "URL": "https://a.s4hana.ondemand.com",
            "Authentication": "BasicAuthentication",
            "ProxyType": "Internet",
            "TrustAll": "TRUE",
            "User": "USER",
            "Password": "pass"
          },
          {
            "Name": "CC8-HTTP-CERT",
            "Type": "HTTP",
            "URL": "https://a.s4hana.ondemand.com",
            "Authentication": "ClientCertificateAuthentication",
            "ProxyType": "Internet",
            "KeyStorePassword": "password",
            "KeyStoreLocation": "aaa"
          }]
        """;

    private static final String brokenResponseSubaccountDestination = """
        [{
            "Name": "CC8-HTTP-BASIC",
            "Type": "HTTP",
            "URL": "https://a.s4hana.ondemand.com",
            "Authentication": "BasicAuthentication",
            "ProxyType": "Internet",
            "TrustAll": "TRUE",
            "User": "USER",
            "Password": "pass"
          },
          {
            "Type": "BROKEN_TYPE",
            "URL": "https:/brok en URL!"
          }]
        """;

    private static final String responseServiceInstanceDestination = """
        [{
            "Name": "CC8-HTTP-BASIC",
            "Type": "HTTP",
            "URL": "https://a.s4hana.ondemand.com",
            "Authentication": "BasicAuthentication",
            "ProxyType": "Internet",
            "TrustAll": "TRUE",
            "User": "USER1",
            "Password": "pass"
          },
          {
            "Name": "CC8-HTTP-CERT1",
            "Type": "HTTP",
            "URL": "https://a.s4hana.ondemand.com",
            "Authentication": "ClientCertificateAuthentication",
            "ProxyType": "Internet",
            "KeyStorePassword": "password",
            "KeyStoreLocation": "aaa"
          }]
        """;

    private static final String responseDestinationWithoutAuthToken =
        """
            {
                "owner": {
                    "SubaccountId": "00000000-0000-0000-0000-000000000000",
                    "InstanceId": null
                },
                "destinationConfiguration": {
                    "Name": "CC8-HTTP-OAUTH",
                    "Type": "HTTP",
                    "URL": "https://a.s4hana.ondemand.com/",
                    "Authentication": "OAuth2SAMLBearerAssertion",
                    "ProxyType": "Internet"
                },
                "authTokens": [
                    {
                        "type": "",
                        "value": "",
                        "error": "org.apache.http.HttpException: Request to the /userinfo endpoint ended with status code 403",
                        "expires_in": ""
                    }
                ]
            }
            """;

    private static final String responseDestinationWithAuthToken = """
        {
            "owner": {
                "SubaccountId": "00000000-0000-0000-0000-000000000000",
                "InstanceId": null
            },
            "destinationConfiguration": {
                "Name": "CC8-HTTP-OAUTH",
                "Type": "HTTP",
                "URL": "https://a.s4hana.ondemand.com/",
                "Authentication": "OAuth2SAMLBearerAssertion",
                "ProxyType": "Internet"
            },
            "authTokens": [
                {
                    "type": "Bearer",
                    "value": "bearer_token",
                    "http_header": {
                        "key": "Authorization",
                        "value": "Bearer bearer_token"
                    },
                    "expires_in": "3600",
                    "scope": "API_BUSINESS_PARTNER_0001"
                }
            ]
        }
        """;

    private static final String responseDestinationWithExpiredAuthToken = """
        {
            "owner": {
                "SubaccountId": "00000000-0000-0000-0000-000000000000",
                "InstanceId": null
            },
            "destinationConfiguration": {
                "Name": "CC8-HTTP-OAUTH",
                "Type": "HTTP",
                "URL": "https://a.s4hana.ondemand.com/",
                "Authentication": "OAuth2SAMLBearerAssertion",
                "ProxyType": "Internet"
            },
            "authTokens": [
                {
                    "type": "Bearer",
                    "value": "bearer_token",
                    "http_header": {
                        "key": "Authorization",
                        "value": "Bearer bearer_token"
                    },
                    "expires_in": "5",
                    "scope": "API_BUSINESS_PARTNER_0001"
                }
            ]
        }
        """;

    //Fictional use-case where the destination service responds with multiple auth tokens
    private static final String responseDestinationWithMultipleAuthTokens = """
        {
            "owner": {
                "SubaccountId": "00000000-0000-0000-0000-000000000000",
                "InstanceId": null
            },
            "destinationConfiguration": {
                "Name": "CC8-HTTP-OAUTH",
                "Type": "HTTP",
                "URL": "https://a.s4hana.ondemand.com/",
                "Authentication": "OAuth2SAMLBearerAssertion",
                "ProxyType": "Internet"
            },
            "authTokens": [
                {
                    "type": "Bearer",
                    "value": "bearer_token",
                    "http_header": {
                        "key": "Authorization",
                        "value": "Bearer bearer_token"
                    },
                    "expires_in": "5",
                    "scope": "API_BUSINESS_PARTNER_0001"
                },
                {
                    "type": "Bearer",
                    "value": "bearer_token",
                    "http_header": {
                        "key": "Authorization",
                        "value": "Bearer bearer_token"
                    },
                    "expires_in": "50",
                    "scope": "API_BUSINESS_PARTNER_0001"
                }
            ]
        }
        """;

    private static final String responseDestinationWithBasicAuthToken = """
        {
            "owner": {
                "SubaccountId": "00000000-0000-0000-0000-000000000000",
                "InstanceId": null
            },
            "destinationConfiguration": {
                "Name": "CC8-HTTP-BASIC",
                "Type": "HTTP",
                "URL": "https://a.s4hana.ondemand.com",
                "Authentication": "BasicAuthentication",
                "ProxyType": "Internet",
                "TrustAll": "TRUE",
                "User": "USER",
                "Password": "pass"
            },
            "authTokens": [
                {
                    "type": "Basic",
                    "value": "dGVzdDpwYgXNzMTIzNDU=",
                    "http_header": {
                        "key": "Authorization",
                        "value": "Basic dGVzdDpwYgXNzMTIzNDU="
                    }
                }
            ]
        }
        """;

    private static final String responseDestinationWithNoAuthToken = """
        {
            "owner": {
                "SubaccountId": "00000000-0000-0000-0000-000000000000",
                "InstanceId": null
            },
            "destinationConfiguration": {
                "Name": "Subscriber-CCT",
                "Type": "HTTP",
                "URL": "https://wrong.com",
                "Authentication": "NoAuthentication",
                "ProxyType": "Internet",
                "Description": "Dummy destination that should be overwritten by a subscriber."
            }
        }
        """;
    // endregion

    @RegisterExtension
    static final TestContext context = TestContext.withThreadContext().resetCaches();

    private DestinationServiceAdapter destinationServiceAdapter;
    private DestinationService loader;
    private Tenant providerTenant;
    private DefaultTenant subscriberTenant;
    private DefaultPrincipal principal1;
    private DefaultPrincipal principal2;
    private String userToken;

    @BeforeEach
    void setup()
    {
        providerTenant = new DefaultTenant("provider-tenant");
        subscriberTenant = new DefaultTenant("subscriber-tenant");
        context.setTenant(subscriberTenant);

        principal1 = new DefaultPrincipal("principal-1");
        principal2 = new DefaultPrincipal("principal-2");
        context.setPrincipal(principal1);

        userToken = mockXsuaaToken().getToken();
        context.setAuthToken(JWT.decode(userToken));

        destinationServiceAdapter =
            spy(
                new DestinationServiceAdapter(
                    behalf -> DefaultHttpDestination.builder("").build(),
                    () -> mock(ServiceBinding.class),
                    providerTenant.getTenantId()));
        // identifier UUID added to isolate resilience states
        loader =
            new DestinationService(
                destinationServiceAdapter,
                DestinationService
                    .createResilienceConfiguration(
                        "singleDestResilience" + UUID.randomUUID(),
                        TimeLimiterConfiguration.disabled(),
                        DestinationService.DEFAULT_SINGLE_DEST_CIRCUIT_BREAKER),
                DestinationService
                    .createResilienceConfiguration(
                        "allDestResilience" + UUID.randomUUID(),
                        TimeLimiterConfiguration.disabled(),
                        DestinationService.DEFAULT_ALL_DEST_CIRCUIT_BREAKER));

        final String httResponseProvider = createHttpDestinationServiceResponse(destinationName, providerUrl);
        final String httpResponseSubscriber = createHttpDestinationServiceResponse(destinationName, subscriberUrl);
        final String destinationPath = "/destinations/" + destinationName;

        doReturn(httResponseProvider)
            .when(destinationServiceAdapter)
            .getConfigurationAsJson(
                eq(destinationPath),
                argThat(s -> s.behalf() == OnBehalfOf.TECHNICAL_USER_PROVIDER));
        doReturn(httpResponseSubscriber)
            .when(destinationServiceAdapter)
            .getConfigurationAsJson(eq(destinationPath), argThat(s -> s.behalf() == TECHNICAL_USER_CURRENT_TENANT));
    }

    @AfterEach
    void resetDestinationCache()
    {
        DestinationService.Cache.reset();
    }

    @Test
    void testInitialCacheSizeIsZero()
    {
        // this test ensures that the cache
        // 1. exists by default (i.e. out-of-the-box) and
        // 2. is empty, so that modifying the cache at application startup won't lead to a warning
        assertThat(DestinationService.Cache.isEnabled()).isTrue();
        assertThat(DestinationService.Cache.instanceSingle().estimatedSize()).isZero();
    }

    @Test
    void testLoadingHttpDestination()
    {
        final HttpDestination destination = loader.tryGetDestination(destinationName).get().asHttp();

        assertThat(destination.get(DestinationProperty.NAME)).contains(destinationName);
        assertThat(destination.getUri()).isEqualTo(URI.create(subscriberUrl));
        assertThat(destination.getProxyType()).contains(ProxyType.INTERNET);
        assertThat(destination.getAuthenticationType()).isEqualTo(AuthenticationType.NO_AUTHENTICATION);
        assertThat(destination.getBasicCredentials()).isEmpty();
        assertThat(destination.getProxyConfiguration()).isEmpty();
        assertThat(destination.getKeyStore()).isEmpty();
        assertThat(destination.getTrustStore()).isEmpty();
        assertThat(destination.getTlsVersion()).isEmpty();
        assertThat(destination.get(DestinationProperty.TENANT_ID)).contains(subscriberTenant.getTenantId());
        assertThat(destination.get(DestinationProperty.AUTH_TOKENS)).isEmpty();
        assertThat(destination.get(DestinationProperty.CERTIFICATES)).isEmpty();
        assertThat(destination.getSecurityConfigurationStrategy())
            .isEqualTo(SecurityConfigurationStrategy.FROM_DESTINATION);
        assertThat(destination.get(DestinationProperty.PROPERTIES_FOR_CHANGE_DETECTION)).isNotEmpty();
        assertThat(destination.get(DestinationProperty.PROPERTIES_FOR_CHANGE_DETECTION).get())
            .containsExactlyInAnyOrder(
                DestinationProperty.NAME.getKeyName(),
                DestinationProperty.URI.getKeyName(),
                DestinationProperty.TYPE.getKeyName(),
                DestinationProperty.PROXY_TYPE.getKeyName(),
                DestinationProperty.AUTH_TYPE.getKeyName(),
                "Description");
    }

    @Test
    void testGettingDestinationProperties()
    {
        doReturn(responseServiceInstanceDestination)
            .when(destinationServiceAdapter)
            .getConfigurationAsJson("/instanceDestinations", withoutToken(TECHNICAL_USER_CURRENT_TENANT));
        doReturn(responseSubaccountDestination)
            .when(destinationServiceAdapter)
            .getConfigurationAsJson("/subaccountDestinations", withoutToken(TECHNICAL_USER_CURRENT_TENANT));

        final Collection<DestinationProperties> destinationList = loader.getAllDestinationProperties();

        assertThat(destinationList)
            .extracting(d -> d.get(DestinationProperty.NAME).get())
            .containsExactly("CC8-HTTP-BASIC", "CC8-HTTP-CERT1", "CC8-HTTP-CERT");

        final DestinationProperties destination =
            destinationList
                .stream()
                .filter(d -> d.get(DestinationProperty.NAME).get().equalsIgnoreCase("CC8-HTTP-BASIC"))
                .findFirst()
                .get();
        //Assert additionally that of a subaccount destination and an instance destination of same name, the instance one was picked
        assertThat(destination.get("User", String.class)).contains("USER1");

        // test single destination properties convenience
        assertThat(loader.getDestinationProperties("CC8-HTTP-BASIC")).isSameAs(destination);
        assertThatThrownBy(() -> loader.getDestinationProperties("doesnt exist"))
            .isInstanceOf(DestinationNotFoundException.class);

        // verify all results are cached
        verify(destinationServiceAdapter, times(1))
            .getConfigurationAsJson("/instanceDestinations", withoutToken(TECHNICAL_USER_CURRENT_TENANT));
        verify(destinationServiceAdapter, times(1))
            .getConfigurationAsJson("/subaccountDestinations", withoutToken(TECHNICAL_USER_CURRENT_TENANT));
    }

    @Test
    // slow test, run manually if needed
    void destinationServiceTimeOutWhileGettingDestination()
        throws IOException
    {
        final HttpDestination serviceDestination = DefaultHttpDestination.builder("").build();

        // prepare slow HttpClient
        HttpClientFactory factory = HttpClientAccessor.getHttpClientFactory();
        HttpClient cl = mock(HttpClient.class);
        doAnswer(invocation -> {
            Thread.sleep(TEST_TIMEOUT);
            return null;
        }).when(cl).execute(any());
        HttpClientAccessor.setHttpClientFactory(dest -> cl);

        // prepare adapter
        final DestinationServiceAdapter adapter =
            spy(
                new DestinationServiceAdapter(
                    anyBehalf -> serviceDestination,
                    () -> mock(ServiceBinding.class),
                    providerTenant.getTenantId()));
        // prepare loader
        final DestinationService loaderToTest =
            new DestinationService(
                adapter,
                DestinationService
                    .createResilienceConfiguration(
                        "singleDestResilience",
                        TimeLimiterConfiguration.of(Duration.ofSeconds(5)),
                        DestinationService.DEFAULT_SINGLE_DEST_CIRCUIT_BREAKER),
                DestinationService
                    .createResilienceConfiguration(
                        "allDestResilience",
                        TimeLimiterConfiguration.of(Duration.ofSeconds(5)),
                        DestinationService.DEFAULT_ALL_DEST_CIRCUIT_BREAKER));

        // actual test
        assertThatThrownBy(() -> loaderToTest.tryGetDestination(destinationName).get())
            .isExactlyInstanceOf(DestinationAccessException.class)
            .hasRootCauseExactlyInstanceOf(TimeoutException.class);

        verify(cl, times(1)).execute(any());
        verify(adapter, times(1)).getConfigurationAsJson(eq("/destinations/SomeDestinationName"), any());

        // reset
        HttpClientAccessor.setHttpClientFactory(null);
    }

    @Test
    void testDestinationServiceTimeoutDisabled()
    {
        DestinationService sut = DestinationService.builder().withProviderTenant(providerTenant).build();
        assertThat(sut.getSingleDestResilience().timeLimiterConfiguration().isEnabled()).isTrue();
        assertThat(sut.getAllDestResilience().timeLimiterConfiguration().isEnabled()).isTrue();
        assertThat(sut.getSingleDestResilience().timeLimiterConfiguration())
            .isEqualTo(DestinationService.DEFAULT_TIME_LIMITER);
        assertThat(sut.getAllDestResilience().timeLimiterConfiguration())
            .isEqualTo(DestinationService.DEFAULT_TIME_LIMITER);

        sut =
            DestinationService
                .builder()
                .withTimeLimiterConfiguration(TimeLimiterConfiguration.disabled())
                .withProviderTenant(providerTenant)
                .build();
        assertThat(sut.getSingleDestResilience().timeLimiterConfiguration().isEnabled()).isFalse();
        assertThat(sut.getAllDestResilience().timeLimiterConfiguration().isEnabled()).isFalse();
    }

    @SuppressWarnings( "deprecation" )
    @Test
    @DisplayName( "Test getting Destination Properties for the provider" )
    void testDestinationPropertiesForProvider()
    {
        doReturn(responseServiceInstanceDestination)
            .when(destinationServiceAdapter)
            .getConfigurationAsJson("/instanceDestinations", withoutToken(TECHNICAL_USER_PROVIDER));
        doReturn(responseSubaccountDestination)
            .when(destinationServiceAdapter)
            .getConfigurationAsJson("/subaccountDestinations", withoutToken(TECHNICAL_USER_PROVIDER));

        final DestinationOptions options =
            DestinationOptions.builder().augmentBuilder(augmenter().retrievalStrategy(ALWAYS_PROVIDER)).build();
        final Try<Iterable<Destination>> destinations = loader.tryGetAllDestinations(options);

        assertThat(destinations.get().iterator()).isNotNull();

        final List<Destination> destinationList = new ArrayList<>();
        destinations.get().forEach(destinationList::add);

        assertThat(destinationList.size()).isEqualTo(3);
        assertThat(destinationList)
            .extracting(d -> d.get(DestinationProperty.NAME).get())
            .containsOnly("CC8-HTTP-BASIC", "CC8-HTTP-CERT", "CC8-HTTP-CERT1");
    }

    @SuppressWarnings( "deprecation" )
    @Test
    @DisplayName( "Test getting Destination Properties can enforce a subscriber tenant" )
    void testGetAllDestinationsOnlySubscriberStrategyReadsSubscriberDestinations()
    {
        final DestinationOptions options =
            DestinationOptions.builder().augmentBuilder(augmenter().retrievalStrategy(ONLY_SUBSCRIBER)).build();
        // set current tenant to be the provider tenant
        context.clearTenant();

        final Try<Iterable<Destination>> result =
            TenantAccessor.executeWithTenant(providerTenant, () -> loader.tryGetAllDestinations(options));

        assertThatThrownBy(result::get)
            .isInstanceOf(DestinationAccessException.class)
            .hasMessageContaining(
                "The current tenant is the provider tenant, which should not be the case with the option OnlySubscriber. Cannot retrieve destination.");
    }

    @Test
    void testDestinationRetrievalProviderOnly()
    {
        final DestinationOptions options =
            DestinationOptions.builder().augmentBuilder(augmenter().retrievalStrategy(ALWAYS_PROVIDER)).build();

        final Try<Destination> loadedDestination = loader.tryGetDestination(destinationName, options);
        final HttpDestination loadedHttpDestination = loadedDestination.get().asHttp();

        assertThat(loadedHttpDestination.getUri()).isEqualTo(URI.create(providerUrl));
    }

    @Test
    void testGetDestinationOnlySubscriberStrategyReadsSubscriberDestinations()
    {
        final DestinationOptions options =
            DestinationOptions.builder().augmentBuilder(augmenter().retrievalStrategy(ONLY_SUBSCRIBER)).build();

        final Try<Destination> loadedDestination = loader.tryGetDestination(destinationName, options);
        final HttpDestination loadedHttpDestination = loadedDestination.get().asHttp();

        assertThat(loadedHttpDestination.getUri()).isEqualTo(URI.create(subscriberUrl));
    }

    @Test
    void testGetDestinationOnlySubscriberStrategyDoesNotReadProviderDestinations()
    {
        final DestinationOptions options =
            DestinationOptions.builder().augmentBuilder(augmenter().retrievalStrategy(ONLY_SUBSCRIBER)).build();

        // set current tenant to be the provider tenant
        context.setTenant(providerTenant);

        assertThat(loader.tryGetDestination(destinationName, options).getCause())
            .isInstanceOf(DestinationAccessException.class);
    }

    @Test
    void testDestinationAugmentationByText()
    {
        final String retrievalProvider = ALWAYS_PROVIDER.getIdentifier();
        final String exchangeOnly = EXCHANGE_ONLY.getIdentifier();

        final DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(builder -> builder.parameter(DESTINATION_RETRIEVAL_STRATEGY_KEY, retrievalProvider))
                .augmentBuilder(builder -> builder.parameter(DESTINATION_TOKEN_EXCHANGE_STRATEGY_KEY, exchangeOnly))
                .build();

        assertThat(DestinationServiceOptionsAugmenter.getRetrievalStrategy(options)).contains(ALWAYS_PROVIDER);
        assertThat(DestinationServiceOptionsAugmenter.getTokenExchangeStrategy(options)).contains(EXCHANGE_ONLY);
    }

    @Test
    void testDestinationRetrievalSubscriberOnly()
    {
        final Try<Destination> loadedDestination = loader.tryGetDestination(destinationName);
        final HttpDestination loadedHttpDestination = loadedDestination.get().asHttp();

        assertThat(loadedHttpDestination.getUri()).isEqualTo(URI.create(subscriberUrl));
    }

    @Test
    void testTokenExchangeLookupThenExchange()
    {
        doReturn(responseDestinationWithoutAuthToken)
            .when(destinationServiceAdapter)
            .getConfigurationAsJson("/destinations/CC8-HTTP-OAUTH", withoutToken(TECHNICAL_USER_CURRENT_TENANT));
        doReturn(responseDestinationWithAuthToken)
            .when(destinationServiceAdapter)
            .getConfigurationAsJson("/destinations/CC8-HTTP-OAUTH", withoutToken(NAMED_USER_CURRENT_TENANT));

        @SuppressWarnings( "deprecation" )
        final DestinationOptionsAugmenter optionsStrategy =
            augmenter().tokenExchangeStrategy(DestinationServiceTokenExchangeStrategy.LOOKUP_THEN_EXCHANGE);
        final DestinationOptions options = DestinationOptions.builder().augmentBuilder(optionsStrategy).build();

        final Try<Destination> destination = loader.tryGetDestination("CC8-HTTP-OAUTH", options);
        final HttpDestinationProperties httpDestination = destination.get().asHttp();

        assertThat(httpDestination.getHeaders()).containsExactly(new Header("Authorization", "Bearer bearer_token"));

        verify(destinationServiceAdapter, times(1))
            .getConfigurationAsJson("/destinations/CC8-HTTP-OAUTH", withoutToken(TECHNICAL_USER_CURRENT_TENANT));
        verify(destinationServiceAdapter, times(1))
            .getConfigurationAsJson("/destinations/CC8-HTTP-OAUTH", withoutToken(NAMED_USER_CURRENT_TENANT));
    }

    @Test
    void testTokenExchangeLookupOnly()
    {
        doReturn(responseDestinationWithoutAuthToken)
            .when(destinationServiceAdapter)
            .getConfigurationAsJson("/destinations/CC8-HTTP-OAUTH", withoutToken(TECHNICAL_USER_CURRENT_TENANT));

        @SuppressWarnings( "deprecation" )
        final DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(augmenter().tokenExchangeStrategy(DestinationServiceTokenExchangeStrategy.LOOKUP_ONLY))
                .build();

        final Try<Destination> destination = loader.tryGetDestination("CC8-HTTP-OAUTH", options);

        assertThatThrownBy(destination::get).isExactlyInstanceOf(DestinationAccessException.class);

        verify(destinationServiceAdapter, times(1))
            .getConfigurationAsJson("/destinations/CC8-HTTP-OAUTH", withoutToken(TECHNICAL_USER_CURRENT_TENANT));
        verify(destinationServiceAdapter, times(0))
            .getConfigurationAsJson(
                eq("/destinations/CC8-HTTP-OAUTH"),
                argThat(s -> !s.equals(withoutToken(TECHNICAL_USER_CURRENT_TENANT))));
    }

    @Test
    void testTokenExchangeExchangeOnly()
    {
        doReturn(responseDestinationWithAuthToken)
            .when(destinationServiceAdapter)
            .getConfigurationAsJson("/destinations/CC8-HTTP-OAUTH", withoutToken(NAMED_USER_CURRENT_TENANT));

        final DestinationOptions options =
            DestinationOptions.builder().augmentBuilder(augmenter().tokenExchangeStrategy(EXCHANGE_ONLY)).build();

        final Try<Destination> destination = loader.tryGetDestination("CC8-HTTP-OAUTH", options);
        final HttpDestinationProperties httpDestination = destination.get().asHttp();

        assertThat(httpDestination.getHeaders()).containsExactly(new Header("Authorization", "Bearer bearer_token"));

        verify(destinationServiceAdapter, times(1))
            .getConfigurationAsJson("/destinations/CC8-HTTP-OAUTH", withoutToken(NAMED_USER_CURRENT_TENANT));
        verify(destinationServiceAdapter, times(0))
            .getConfigurationAsJson(any(), argThat(s -> !s.equals(withoutToken(NAMED_USER_CURRENT_TENANT))));
    }

    @SuppressWarnings( "deprecation" )
    @Test
    void testEmailDestination()
    {
        final String destinationName = "dummy-mail-destination";

        doReturn(
            "{"
                + "  \"owner\": {"
                + "    \"SubaccountId\": \"a89ea924-d9c2-4eab-84fb-3ffcaadf5d24\","
                + "    \"InstanceId\": null"
                + "  },"
                + "  \"destinationConfiguration\": {"
                + ("   \"Name\": \"" + destinationName + "\",")
                + "    \"Type\": \"MAIL\","
                + "    \"Authentication\": \"NoAuthentication\","
                + "    \"ProxyType\": \"OnPremise\","
                + "    \"CloudConnectorLocationId\": \"bla\","
                + "    \"mail.description\": \"delete me\""
                + "  }"
                + "}")
            .when(destinationServiceAdapter)
            .getConfigurationAsJson(eq("/destinations/" + destinationName), any());

        @SuppressWarnings( "deprecation" )
        final DestinationOptionsAugmenter optionsStrategy =
            augmenter()
                .tokenExchangeStrategy(DestinationServiceTokenExchangeStrategy.LOOKUP_ONLY)
                .retrievalStrategy(ALWAYS_PROVIDER);
        final DestinationOptions options = DestinationOptions.builder().augmentBuilder(optionsStrategy).build();

        final Destination dest = loader.tryGetDestination(destinationName, options).get();

        assertThatCode(dest::asHttp).isInstanceOf(IllegalArgumentException.class);
        // this actually works
        assertThatCode(dest::asRfc).doesNotThrowAnyException();

        assertThat(dest.get(DestinationProperty.NAME)).containsExactly(destinationName);
        assertThat(dest.get(DestinationProperty.TYPE)).containsExactly(DestinationType.MAIL);
        assertThat(dest.get(DestinationProperty.AUTH_TYPE)).containsExactly(AuthenticationType.NO_AUTHENTICATION);
        assertThat(dest.get(DestinationProperty.PROXY_TYPE)).containsExactly(ProxyType.ON_PREMISE);
        assertThat(dest.get(DestinationProperty.CLOUD_CONNECTOR_LOCATION_ID)).containsExactly("bla");
        assertThat(dest.get("mail.description")).containsExactly("delete me");

        verify(destinationServiceAdapter)
            .getConfigurationAsJson("/destinations/" + destinationName, withoutToken(TECHNICAL_USER_PROVIDER));
    }

    @Test
    void testDestinationIsCachedBasedOnOptions()
    {
        final DestinationOptions defaultOptions = DestinationOptions.builder().build();
        final DestinationOptions secondInstanceOfDefaultOptions = DestinationOptions.builder().build();
        final DestinationOptions optionsWithDefaultRetrievalStrategy =
            DestinationOptions.builder().augmentBuilder(augmenter().retrievalStrategy(CURRENT_TENANT)).build();

        assertThat(defaultOptions).isEqualTo(secondInstanceOfDefaultOptions);

        loader.tryGetDestination(destinationName);
        verify(destinationServiceAdapter, times(1))
            .getConfigurationAsJson(
                "/destinations/" + destinationName,
                withUserToken(TECHNICAL_USER_CURRENT_TENANT, userToken));

        loader.tryGetDestination(destinationName, defaultOptions);
        verify(destinationServiceAdapter, times(1))
            .getConfigurationAsJson(
                "/destinations/" + destinationName,
                withUserToken(TECHNICAL_USER_CURRENT_TENANT, userToken));

        loader.tryGetDestination(destinationName, optionsWithDefaultRetrievalStrategy);
        verify(destinationServiceAdapter, times(2))
            .getConfigurationAsJson(
                "/destinations/" + destinationName,
                withUserToken(TECHNICAL_USER_CURRENT_TENANT, userToken));

        loader.tryGetDestination(destinationName, secondInstanceOfDefaultOptions);
        verify(destinationServiceAdapter, times(2))
            .getConfigurationAsJson(
                "/destinations/" + destinationName,
                withUserToken(TECHNICAL_USER_CURRENT_TENANT, userToken));
    }

    @Test
    @SneakyThrows
    // tests the workaround described in https://jira.tools.sap/browse/CLOUDECOSYSTEM-10077
    void testWorkaroundDestinationCacheIssue()
    {
        {
            // first invocation
            final DestinationOptions options =
                DestinationOptions.builder().parameter("timestamp", Instant.now().toEpochMilli()).build();

            loader.tryGetDestination(destinationName, options);
            verify(destinationServiceAdapter, times(1))
                .getConfigurationAsJson(
                    "/destinations/" + destinationName,
                    withUserToken(TECHNICAL_USER_CURRENT_TENANT, userToken));
        }
        // sleep to guarantee at least 1ms difference
        Thread.sleep(100);
        {
            // second invocation
            final DestinationOptions options =
                DestinationOptions.builder().parameter("timestamp", Instant.now().toEpochMilli()).build();

            loader.tryGetDestination(destinationName, options);
            verify(destinationServiceAdapter, times(2))
                .getConfigurationAsJson(
                    "/destinations/" + destinationName,
                    withUserToken(TECHNICAL_USER_CURRENT_TENANT, userToken));
        }
    }

    @Test
    void testCachingHttpDestination()
    {
        loader.tryGetDestination(destinationName).get();

        for( int i = 0; i < 10; i++ ) {
            loader.tryGetDestination(destinationName).get();
        }

        verify(destinationServiceAdapter, times(1))
            .getConfigurationAsJson(
                "/destinations/" + destinationName,
                withUserToken(TECHNICAL_USER_CURRENT_TENANT, userToken));

        assertThat(DestinationService.Cache.instanceSingle().estimatedSize()).isEqualTo(1);
    }

    @Test
    void testUnknownDestinationLeadsToDestinationNotFoundException()
        throws IOException
    {
        // prepare 404 HttpClient
        final HttpClientFactory factory = HttpClientAccessor.getHttpClientFactory();
        final HttpClient client404 = mock(HttpClient.class);
        when(client404.execute(any())).thenReturn(new BasicHttpResponse(HttpVersion.HTTP_1_1, 404, "Not found"));
        HttpClientAccessor.setHttpClientFactory(dest -> client404);

        assertThatThrownBy(() -> loader.tryGetDestination("UnknownDestination").get())
            .isInstanceOf(DestinationNotFoundException.class);

        // reset
        HttpClientAccessor.setHttpClientFactory(factory);
    }

    private void tryGetDestinationTwice( String destinationName, String responseDestination, int numberOfFetches )
    {
        doReturn(responseDestination)
            .when(destinationServiceAdapter)
            .getConfigurationAsJson("/destinations/" + destinationName, withoutToken(TECHNICAL_USER_CURRENT_TENANT));

        @SuppressWarnings( "deprecation" )
        final DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(augmenter().tokenExchangeStrategy(DestinationServiceTokenExchangeStrategy.LOOKUP_ONLY))
                .build();

        loader.tryGetDestination(destinationName, options);
        loader.tryGetDestination(destinationName, options);

        verify(destinationServiceAdapter, times(numberOfFetches))
            .getConfigurationAsJson("/destinations/" + destinationName, withoutToken(TECHNICAL_USER_CURRENT_TENANT));

    }

    @Test
    void testDestinationFetchedWhenAuthTokenExpired()
    {
        // 1st fetch: Put the destination into cache
        // 2nd fetch: Expired destination in cache entry, fetch the destination again
        //Destination fetched twice
        tryGetDestinationTwice("CC8-HTTP-OAUTH", responseDestinationWithExpiredAuthToken, 2);
    }

    @Test
    void testDestinationFetchedWhenAuthTokenIsNotExpired()
    {
        // 1st fetch: Put the destination into cache
        // 2nd fetch: Destination fetched from cache as token is well within expiry time
        //Destination fetched once
        tryGetDestinationTwice("CC8-HTTP-OAUTH", responseDestinationWithAuthToken, 1);
    }

    @Test
    void testExpiresInIsSkippedWithBasicAuthToken()
    {
        // 1st fetch: Put the destination into cache
        // 2nd fetch: authToken doesn't contain expires_in field, fetch destination from cache
        //Destination fetched once
        tryGetDestinationTwice("CC8-HTTP", responseDestinationWithBasicAuthToken, 1);
    }

    @Test
    void testFetchingDestinationWithNoAuth()
    {
        // 1st fetch: Put the destination into cache
        // 2nd fetch: No auth token defined in destination, fetch destination from cache
        //Destination fetched once
        tryGetDestinationTwice("Subscriber-CCT", responseDestinationWithNoAuthToken, 1);
    }

    @Test
    void testFetchingDestinationWithMultipleAuthToken()
    {
        //1st fetch: Put the destination into cache
        //2nd fetch: One of the auth tokens defines in destination expires soon, fetch the destination again
        //Destination fetched twice
        tryGetDestinationTwice("CC8-HTTP-OAUTH", responseDestinationWithMultipleAuthTokens, 2);
    }

    @Test
    void testAlwaysFetchDestinationWhenCacheIsDisabled()
    {
        DestinationService.Cache.disable();
        tryGetDestinationTwice("Subscriber-CCT", responseDestinationWithNoAuthToken, 2);
    }

    @Test
    void testLookupPerformedTwiceWhenAuthTokenExpiredAndRequiresUserTokenExchange()
    {
        doReturn(responseDestinationWithoutAuthToken)
            .when(destinationServiceAdapter)
            .getConfigurationAsJson("/destinations/CC8-HTTP-OAUTH", withoutToken(TECHNICAL_USER_CURRENT_TENANT));
        doReturn(responseDestinationWithExpiredAuthToken)
            .when(destinationServiceAdapter)
            .getConfigurationAsJson("/destinations/CC8-HTTP-OAUTH", withoutToken(NAMED_USER_CURRENT_TENANT));

        @SuppressWarnings( "deprecation" )
        final DestinationOptionsAugmenter optionsStrategy =
            augmenter().tokenExchangeStrategy(DestinationServiceTokenExchangeStrategy.LOOKUP_THEN_EXCHANGE);
        final DestinationOptions options = DestinationOptions.builder().augmentBuilder(optionsStrategy).build();

        loader.tryGetDestination("CC8-HTTP-OAUTH", options);
        loader.tryGetDestination("CC8-HTTP-OAUTH", options);

        verify(destinationServiceAdapter, times(2))
            .getConfigurationAsJson("/destinations/CC8-HTTP-OAUTH", withoutToken(TECHNICAL_USER_CURRENT_TENANT));
        verify(destinationServiceAdapter, times(2))
            .getConfigurationAsJson("/destinations/CC8-HTTP-OAUTH", withoutToken(NAMED_USER_CURRENT_TENANT));
    }

    /* Test case:
     * Trying to fetch same destination concurrently from single tenant is blocking
     * ┌──────────────┐
     * │              │
     * │              │
     * │ Main Thread  │
     * │              │
     * │              │
     * └───────┬──────┘
     *         │
     *         │
     *         │
     *         │
     *         │ 1.Start
     *         ├──────────────────►┌───────────────┐
     *         │                   │               │
     *         │                   │               │
     *         │                   │  First Thread │
     *         │                   │               │
     *         │                   │               │
     * Pause   │                   └───────┬───────┘
     *         │                           │2. Call tryGetDestination(destinationName,destinationOptions)
     *         │                           │3. Call GetOrComputeSingleDestinationCommand.prepareCommand(...)
     *         │                           │4. Determine CacheKey based on exchange strategy, in this case is TenantOptionalIsolation
     *         │                           │5. Add a new Reentrant lock to the isolation lock Cache
     *         │                           │6. Acquire lock
     *         │                           │7. Try to obtain destination from mocked destination call
     *         │                           │
     *         │ 8.Unblock the main thread │9. Pause
     *         │▲──────────────────────────┤
     *         ├───────────────────────────┼──────────────────────►┌────────────────┐
     *         │ 10. Start                 │                       │                │
     *         │                           │                       │                │
     *         │                           │                       │ Second Thread  │
     *         │                           │                       │                │
     *         │                           │                       │                │
     *         │                           │                       └───────┬────────┘
     *         │                           │                               │
     *         │                           │                               │ 11.Call tryGetDestination(destinationName,destinationOptions)
     *         │                           │                               │ 12.Try and fail to obtain lock from isolation lock Cache
     *         │                           │                               │
     * Pause   │                           │                               │
     *         │                           │                               │
     *         │                           │                               │
     *         │                           │                               │
     *         │                           │                               │
     *         │                           │   13. Unblock the first thread│
     *         │                           │◄──────────────────────────────┤
     *         │                           │                               │ Pause
     *         │                           │                               │
     *         │                           │14.Fetch and add destination   │
     *         │                           │ to destinationsCache          │
     *         │                           │                               │
     *         │                           │ 15. Release lock, finish      │
     *         ◄───────────────────────────┴──                             │
     *         │                                                           │16. Obtain lock
     *         │                                                           │17. Return destination from the destinationsCache, release lock
     *         │                                                           │
     *         │                                                           │
     *         │◄──────────────────────────────────────────────────────────┴── 18. Finish execution
     *         │19. Continue and Finish execution
     *        ─┴───
     */
    @Test
    @Timeout( value = TEST_TIMEOUT, unit = TimeUnit.MILLISECONDS )
    @SneakyThrows
    void testConcurrentFetchSameDestination()
    {
        context.setTenant(providerTenant);
        context.setPrincipal(principal1);

        // setup destination options
        final String destinationName = "CC8-HTTP-OAUTH";
        @SuppressWarnings( "deprecation" )
        final DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(augmenter().tokenExchangeStrategy(DestinationServiceTokenExchangeStrategy.LOOKUP_ONLY))
                .build();

        final CountDownLatch destinationRetrievalLatch = new CountDownLatch(1);
        final CountDownLatch mainThreadLatch = new CountDownLatch(1);
        final SoftAssertions softly = new SoftAssertions();

        doAnswer((Answer<String>) invocation -> {
            try {
                destinationRetrievalLatch.countDown();
                mainThreadLatch.await();
            }
            catch( InterruptedException e ) {
                softly.fail("Thread Interrupted", e);
            }
            return responseDestinationWithBasicAuthToken;
        })
            .when(destinationServiceAdapter)
            .getConfigurationAsJson("/destinations/" + destinationName, withoutToken(TECHNICAL_USER_CURRENT_TENANT));

        final Future<Try<Destination>> firstThread = ThreadContextExecutors.submit(() -> {
            softly.assertThat(TenantAccessor.tryGetCurrentTenant()).isNotEmpty();
            softly.assertThat(PrincipalAccessor.tryGetCurrentPrincipal()).isNotEmpty();

            return loader.tryGetDestination(destinationName, options);
        });

        destinationRetrievalLatch.await();

        final CacheKey tenantCacheKey = CacheKey.ofTenantOptionalIsolation().append(destinationName, options);

        final ReentrantLock tenantLock = DestinationService.Cache.isolationLocks().getIfPresent(tenantCacheKey);
        assertThat(tenantLock).isNotNull();

        final ReentrantLock tenantLockSpy = spy(tenantLock);
        DestinationService.Cache.isolationLocks().put(tenantCacheKey, tenantLockSpy);

        doAnswer(invocation -> {
            mainThreadLatch.countDown();
            return invocation.callRealMethod();
        }).when(tenantLockSpy).lock();

        final Future<Try<Destination>> secondThread = ThreadContextExecutors.submit(() -> {
            softly.assertThat(TenantAccessor.tryGetCurrentTenant()).isNotEmpty();
            softly.assertThat(PrincipalAccessor.tryGetCurrentPrincipal()).isNotEmpty();

            return loader.tryGetDestination(destinationName, options);
        });

        assertThat(firstThread.get()).isNotEmpty();
        assertThat(secondThread.get()).isNotEmpty();
        verify(tenantLockSpy, times(1)).lock();
        verify(tenantLockSpy, times(1)).unlock();
        verify(destinationServiceAdapter, times(1))
            .getConfigurationAsJson("/destinations/" + destinationName, withoutToken(TECHNICAL_USER_CURRENT_TENANT));

        softly.assertThat(DestinationService.Cache.instanceSingle().asMap()).containsOnlyKeys(tenantCacheKey);
        softly.assertAll();
    }

    /*
     * Test case:
     * Fetching the same destination but from different tenants concurrently are non-blocking
     * ┌──────────────┐
     * │              │
     * │              │
     * │ Main Thread  │
     * │              │
     * │              │
     * └───────┬──────┘
     *         │
     *         │
     *         │
     *         │
     *         │ 1.Start
     *         ├──────────────────►┌───────────────┐
     *         │                   │               │
     *         │                   │               │
     *         │                   │  First Thread │
     *         │                   │               │
     *         │                   │               │
     * Pause   │                   └───────┬───────┘
     *         │                           │2. Call tryGetDestination(destinationName,destinationOptions) with TenantA
     *         │                           │3. Call GetOrComputeSingleDestinationCommand.prepareCommand(...)
     *         │                           │4. Determine CacheKey based on exchange strategy, in this case is TenantOptionalIsolation
     *         │                           │5. Add a new Reentrant lock to the isolation lock Cache
     *         │                           │6. Acquire lock
     *         │                           │7. Try to obtain destination from mocked destination call
     *         │                           │
     *         │                           │
     *         │ 9.Unblock the main thread │8. Pause
     *         │▲──────────────────────────┤
     *         ├───────────────────────────┼──────────────────────►┌────────────────┐
     *         │ 10. Start                 │                       │                │
     *         │                           │                       │                │
     *         │                           │                       │ Second Thread  │
     *         │                           │                       │                │
     *         │                           │                       │                │
     *         │                           │                       └──────┬─────────┘
     *         │                           │                              │
     *         │                           │ Pause                        │  11. Call tryGetDestination(destinationName,destinationOptions)with TenantB
     *         │                           │                              │  12. Call GetOrComputeSingleDestinationCommand.prepareCommand(...)
     *         │                           │                              │  13. Determine CacheKey based on exchange strategy, in this case is TenantOptionalIsolation
     *         │                           │                              │  14. Add a new Reentrant lock to the isolation lock Cache
     *         │                           │                              │  15. Acquire lock
     * Pause   │                           │                              │  16. Try to obtain destination from mocked destination call
     *         │                           │                              │  17. Unblock first thread
     *         │                           │                              │  18. Fetch and add destination to destinationsCache with TenantOptionalIsolation cache key
     *         │                           │                              │  19. Release lock
     *         │                           │                              │  20. Finish execution
     *         │                           │                              │
     *         ◄───────────────────────────┼──────────────────────────────┴───
     *         │                           │
     *         │                           │ 21. Fetch and add destination to destinationCache with TenantOptionalIsolation cache key
     *         │                           │ 22. Release lock
     *         │                           │
     *         │                           │
     *         │                           │
     *         │                           │
     *         │                           │
     *         │                           │
     *         ◄───────────────────────────┴────────────────  23. Finish execution
     *         │
     *         │
     *         │24. Continue and Finish execution
     *        ─┴───
     */
    @Test
    @Timeout( value = TEST_TIMEOUT, unit = TimeUnit.MILLISECONDS )
    void testConcurrentFetchSameDestinationButDifferentTenant()
    {
        final CountDownLatch countDownLatch1 = new CountDownLatch(2);
        final SoftAssertions softly = new SoftAssertions();
        final String destinationName = "CC8-HTTP-OAUTH";
        doAnswer((Answer<String>) invocation -> {
            try {
                countDownLatch1.countDown();
                countDownLatch1.await();
            }
            catch( InterruptedException e ) {
                softly.fail("Thread Interrupted", e);
            }
            return responseDestinationWithBasicAuthToken;
        })
            .when(destinationServiceAdapter)
            .getConfigurationAsJson("/destinations/" + destinationName, withoutToken(TECHNICAL_USER_CURRENT_TENANT));
        @SuppressWarnings( "deprecation" )
        final DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(augmenter().tokenExchangeStrategy(DestinationServiceTokenExchangeStrategy.LOOKUP_ONLY))
                .build();

        final CompletableFuture<Void> firstThread =
            CompletableFuture
                .runAsync(
                    () -> TenantAccessor
                        .executeWithTenant(() -> "TenantA", () -> loader.tryGetDestination(destinationName, options)));

        final CompletableFuture<Void> secondThread =
            CompletableFuture
                .runAsync(
                    () -> TenantAccessor
                        .executeWithTenant(() -> "TenantB", () -> loader.tryGetDestination(destinationName, options)));

        secondThread.join();
        firstThread.join();

        // assert cache isolation locks are created for each tenant, for both get-single and get-all commands
        assertThat(DestinationService.Cache.isolationLocks().asMap())
            .containsOnlyKeys(
                CacheKey.fromIds("TenantA", null).append(destinationName, options),
                CacheKey.fromIds("TenantA", null).append(options),
                CacheKey.fromIds("TenantB", null).append(destinationName, options),
                CacheKey.fromIds("TenantB", null).append(options));

        // assert cache entries for one get-single command for each tenant
        assertThat(DestinationService.Cache.instanceSingle().asMap())
            .containsOnlyKeys(
                CacheKey.fromIds("TenantA", null).append(destinationName, options),
                CacheKey.fromIds("TenantB", null).append(destinationName, options));

        // assert no cache entries for get-all commands
        assertThat(DestinationService.Cache.instanceAll().asMap()).isEmpty();

        verify(destinationServiceAdapter, times(2))
            .getConfigurationAsJson("/destinations/" + destinationName, withoutToken(TECHNICAL_USER_CURRENT_TENANT));

        // we are not performing further requests (i.e. there are no 'get-all' requests)
        verifyNoMoreInteractions(destinationServiceAdapter);

        softly.assertAll();
    }

    @Test
    void testPrincipalsShareDestinationWithoutUserPropagation()
    {
        doReturn(responseDestinationWithBasicAuthToken)
            .when(destinationServiceAdapter)
            .getConfigurationAsJson(
                "/destinations/" + destinationName,
                withUserToken(TECHNICAL_USER_CURRENT_TENANT, userToken));

        context.setTenant(providerTenant);
        context.setPrincipal(principal1);

        final Try<Destination> firstDestination = loader.tryGetDestination(destinationName);

        context.setPrincipal(principal2);
        final Try<Destination> secondDestination = loader.tryGetDestination(destinationName);

        assertThat(firstDestination).isNotEmpty();
        assertThat(secondDestination).isNotEmpty();
        assertThat(firstDestination.get()).isSameAs(secondDestination.get());

        verify(destinationServiceAdapter, times(1))
            .getConfigurationAsJson(
                "/destinations/" + destinationName,
                withUserToken(TECHNICAL_USER_CURRENT_TENANT, userToken));
    }

    @Test
    void testPrincipalIsolationForDestinationWithUserPropagationWithExchangeOnlyStrategy()
    {
        final DestinationOptions options =
            DestinationOptions.builder().augmentBuilder(augmenter().tokenExchangeStrategy(EXCHANGE_ONLY)).build();

        doReturn(responseDestinationWithAuthToken)
            .when(destinationServiceAdapter)
            .getConfigurationAsJson("/destinations/" + destinationName, withoutToken(NAMED_USER_CURRENT_TENANT));

        final Try<Destination> firstDestination = loader.tryGetDestination(destinationName, options);

        context.setPrincipal(principal2);
        final Try<Destination> secondDestination = loader.tryGetDestination(destinationName, options);

        assertThat(firstDestination).isNotEmpty();
        assertThat(secondDestination).isNotEmpty();
        assertThat(firstDestination.get()).isNotSameAs(secondDestination.get());

        assertThat(DestinationService.Cache.isolationLocks().asMap())
            .containsOnlyKeys(
                CacheKey.of(subscriberTenant, principal1).append(destinationName, options),
                CacheKey.of(subscriberTenant, principal2).append(destinationName, options),
                CacheKey.of(subscriberTenant, null).append(options));

        assertThat(DestinationService.Cache.instanceSingle().asMap())
            .containsOnlyKeys(
                CacheKey.of(subscriberTenant, principal1).append(destinationName, options),
                CacheKey.of(subscriberTenant, principal2).append(destinationName, options));

        assertThat(DestinationService.Cache.instanceAll().asMap()).isEmpty();

        verify(destinationServiceAdapter, times(2))
            .getConfigurationAsJson("/destinations/" + destinationName, withoutToken(NAMED_USER_CURRENT_TENANT));

        // we are not performing further requests (i.e. there are no 'get-all' requests)
        verifyNoMoreInteractions(destinationServiceAdapter);
    }

    @Test
    void testPrincipalIsolationForDestinationWithUserPropagationWithDefaultExchangeStrategy()
    {
        @SuppressWarnings( "deprecation" )
        final DestinationOptionsAugmenter optionsStrategy =
            augmenter().tokenExchangeStrategy(DestinationServiceTokenExchangeStrategy.LOOKUP_THEN_EXCHANGE);
        final DestinationOptions options = DestinationOptions.builder().augmentBuilder(optionsStrategy).build();

        doReturn(responseDestinationWithoutAuthToken)
            .when(destinationServiceAdapter)
            .getConfigurationAsJson("/destinations/" + destinationName, withoutToken(TECHNICAL_USER_CURRENT_TENANT));

        doReturn(responseDestinationWithAuthToken)
            .when(destinationServiceAdapter)
            .getConfigurationAsJson("/destinations/" + destinationName, withoutToken(NAMED_USER_CURRENT_TENANT));

        final Try<Destination> firstDestination = loader.tryGetDestination(destinationName, options);

        context.setPrincipal(principal2);
        final Try<Destination> secondDestination = loader.tryGetDestination(destinationName, options);

        assertThat(firstDestination).isNotEmpty();
        assertThat(secondDestination).isNotEmpty();
        assertThat(firstDestination.get()).isNotSameAs(secondDestination.get());

        assertThat(DestinationService.Cache.isolationLocks().asMap())
            .containsOnlyKeys(
                CacheKey.of(subscriberTenant, null).append(destinationName, options),
                CacheKey.of(subscriberTenant, null).append(options));

        assertThat(DestinationService.Cache.instanceSingle().asMap())
            .containsOnlyKeys(
                CacheKey.of(subscriberTenant, principal1).append(destinationName, options),
                CacheKey.of(subscriberTenant, principal2).append(destinationName, options));

        assertThat(DestinationService.Cache.instanceAll().asMap()).isEmpty();

        verify(destinationServiceAdapter, times(2))
            .getConfigurationAsJson("/destinations/" + destinationName, withoutToken(TECHNICAL_USER_CURRENT_TENANT));
        verify(destinationServiceAdapter, times(2))
            .getConfigurationAsJson("/destinations/" + destinationName, withoutToken(NAMED_USER_CURRENT_TENANT));

        // we are not performing further requests (i.e. there are no 'get-all' requests)
        verifyNoMoreInteractions(destinationServiceAdapter);
    }

    @Test
    void testSmartCacheServesAllPrincipalsWithSameDestination()
    {
        doReturn("[]").when(destinationServiceAdapter).getConfigurationAsJson(eq("/instanceDestinations"), any());
        doReturn(responseSubaccountDestination)
            .when(destinationServiceAdapter)
            .getConfigurationAsJson(eq("/subaccountDestinations"), any());

        doReturn(responseDestinationWithBasicAuthToken)
            .when(destinationServiceAdapter)
            .getConfigurationAsJson(eq("/destinations/CC8-HTTP-BASIC"), any());

        final AtomicReference<Destination> destination = new AtomicReference<>();

        for( int i = 0; i < 10; ++i ) {
            final Principal principal = new DefaultPrincipal("principal-" + i);
            context.setPrincipal(principal);
            final Try<Destination> tryDestination = loader.tryGetDestination("CC8-HTTP-BASIC");

            assertThat(tryDestination.isSuccess()).isTrue();
            destination.compareAndSet(null, tryDestination.get());
            //Always compares if the first destination fetched and the current destination fetched are same
            assertThat(tryDestination.get()).isSameAs(destination.get());
        }

        verify(destinationServiceAdapter, times(1))
            .getConfigurationAsJson("/instanceDestinations", withoutToken(TECHNICAL_USER_CURRENT_TENANT));
        verify(destinationServiceAdapter, times(1))
            .getConfigurationAsJson("/subaccountDestinations", withoutToken(TECHNICAL_USER_CURRENT_TENANT));
        verify(destinationServiceAdapter, times(1))
            .getConfigurationAsJson(
                "/destinations/CC8-HTTP-BASIC",
                withUserToken(TECHNICAL_USER_CURRENT_TENANT, userToken));
    }

    @Test
    void testChangeDetectionWithMisconfiguredDestination()
    {
        doReturn("[]").when(destinationServiceAdapter).getConfigurationAsJson(eq("/instanceDestinations"), any());
        // One of the getAll destinations will be misconfigured (the smart cache will implicitly call getAll)
        doReturn(brokenResponseSubaccountDestination)
            .when(destinationServiceAdapter)
            .getConfigurationAsJson(eq("/subaccountDestinations"), any());

        // normal response for the requested destination
        doReturn(responseDestinationWithBasicAuthToken)
            .when(destinationServiceAdapter)
            .getConfigurationAsJson(eq("/destinations/CC8-HTTP-BASIC"), any());

        final int circuitBreakerBuffer =
            ResilienceConfiguration.CircuitBreakerConfiguration.DEFAULT_CLOSED_BUFFER_SIZE
                * Math.round(ResilienceConfiguration.CircuitBreakerConfiguration.DEFAULT_FAILURE_RATE_THRESHOLD)
                / 100;

        // the smart cache circuit breaker will try 5 times, then open and not call again
        for( int i = 0; i < 2 * circuitBreakerBuffer; ++i ) {
            final Try<Destination> tryDestination = loader.tryGetDestination("CC8-HTTP-BASIC");
            tryDestination.get();
        }

        // the circuit breaker will getAll, receive a misconfigured destination and open
        verify(destinationServiceAdapter, times(circuitBreakerBuffer))
            .getConfigurationAsJson("/instanceDestinations", withoutToken(TECHNICAL_USER_CURRENT_TENANT));
        verify(destinationServiceAdapter, times(circuitBreakerBuffer))
            .getConfigurationAsJson("/subaccountDestinations", withoutToken(TECHNICAL_USER_CURRENT_TENANT));
        // the requested destination is unaffected
        verify(destinationServiceAdapter, times(1))
            .getConfigurationAsJson(
                "/destinations/CC8-HTTP-BASIC",
                withUserToken(TECHNICAL_USER_CURRENT_TENANT, userToken));

        // we are not performing further requests
        verifyNoMoreInteractions(destinationServiceAdapter);
    }

    @Test
    void testChangeDetectionDisabledDoesNotGetAll()
    {
        DestinationService.Cache.disableChangeDetection();

        loader.tryGetDestination(destinationName).get();

        assertThat(DestinationService.Cache.isolationLocks().asMap())
            .containsOnlyKeys(
                CacheKey.of(subscriberTenant, null).append(destinationName, DestinationOptions.builder().build()));

        assertThat(DestinationService.Cache.instanceSingle().asMap())
            .containsOnlyKeys(
                CacheKey.of(subscriberTenant, null).append(destinationName, DestinationOptions.builder().build()));

        assertThat(DestinationService.Cache.instanceAll().asMap()).isEmpty();

        verify(destinationServiceAdapter, times(1))
            .getConfigurationAsJson(eq("/destinations/" + destinationName), any());

        // we are not performing further requests (i.e. there are no 'get-all' requests)
        verifyNoMoreInteractions(destinationServiceAdapter);
    }

    /**
     * Test case: Trying to fetch same destination concurrently from same tenant but by different principals is blocking
     */
    @Test
    @Timeout( value = TEST_TIMEOUT, unit = TimeUnit.MILLISECONDS )
    @SneakyThrows
    void testConcurrentFetchSameDestinationSameTenantButDifferentPrincipal()
    {
        // setup destination options
        final String destinationName = "CC8-HTTP-OAUTH";
        @SuppressWarnings( "deprecation" )
        final DestinationOptionsAugmenter optionsStrategy =
            augmenter().tokenExchangeStrategy(DestinationServiceTokenExchangeStrategy.LOOKUP_THEN_EXCHANGE);
        final DestinationOptions options = DestinationOptions.builder().augmentBuilder(optionsStrategy).build();

        final CountDownLatch destinationRetrievalLatch = new CountDownLatch(1);
        final CountDownLatch mainThreadLatch = new CountDownLatch(1);
        final SoftAssertions softly = new SoftAssertions();

        doReturn(responseDestinationWithoutAuthToken)
            .when(destinationServiceAdapter)
            .getConfigurationAsJson("/destinations/" + destinationName, withoutToken(TECHNICAL_USER_CURRENT_TENANT));
        doAnswer((Answer<String>) invocation -> {
            try {
                destinationRetrievalLatch.countDown();
                mainThreadLatch.await();
            }
            catch( InterruptedException e ) {
                softly.fail("Thread Interrupted", e);
            }
            return responseDestinationWithAuthToken;
        })
            .when(destinationServiceAdapter)
            .getConfigurationAsJson("/destinations/" + destinationName, withoutToken(NAMED_USER_CURRENT_TENANT));

        final Future<Try<Destination>> firstThread = ThreadContextExecutors.submit(() -> {
            softly.assertThat(TenantAccessor.tryGetCurrentTenant()).isNotEmpty();
            return PrincipalAccessor
                .executeWithPrincipal(principal1, () -> loader.tryGetDestination(destinationName, options));
        });

        destinationRetrievalLatch.await();

        final CacheKey tenantCacheKey = CacheKey.ofTenantOptionalIsolation().append(destinationName, options);

        final ReentrantLock tenantLock = DestinationService.Cache.isolationLocks().getIfPresent(tenantCacheKey);
        assertThat(tenantLock).isNotNull();

        final ReentrantLock tenantLockSpy = spy(tenantLock);
        DestinationService.Cache.isolationLocks().put(tenantCacheKey, tenantLockSpy);

        doAnswer(invocation -> {
            mainThreadLatch.countDown();
            return invocation.callRealMethod();
        }).when(tenantLockSpy).lock();

        final Future<Try<Destination>> secondThread = ThreadContextExecutors.submit(() -> {
            softly.assertThat(TenantAccessor.tryGetCurrentTenant()).isNotEmpty();

            return PrincipalAccessor
                .executeWithPrincipal(principal2, () -> loader.tryGetDestination(destinationName, options));
        });

        softly.assertThat(firstThread.get()).isNotEmpty();
        softly.assertThat(secondThread.get()).isNotEmpty();
        verify(tenantLockSpy, times(1)).lock();
        verify(tenantLockSpy, times(1)).unlock();
        verify(destinationServiceAdapter, times(2))
            .getConfigurationAsJson("/destinations/" + destinationName, withoutToken(TECHNICAL_USER_CURRENT_TENANT));
        verify(destinationServiceAdapter, times(2))
            .getConfigurationAsJson("/destinations/" + destinationName, withoutToken(NAMED_USER_CURRENT_TENANT));
        verifyNoMoreInteractions(destinationServiceAdapter);

        softly
            .assertThat(DestinationService.Cache.instanceSingle().asMap())
            .containsOnlyKeys(
                CacheKey.of(subscriberTenant, principal1).append(destinationName, options),
                CacheKey.of(subscriberTenant, principal2).append(destinationName, options));
        softly.assertThat(DestinationService.Cache.instanceAll().asMap()).isEmpty();

        softly.assertAll();
    }

    @Test
    void testDestinationWithInvalidJson()
    {
        doReturn("{ \"destinationConfiguration\" : { \"Name ... } }")
            .when(destinationServiceAdapter)
            .getConfigurationAsJson(eq("/destinations/BadDestination"), any());

        assertThatThrownBy(() -> loader.tryGetDestination("BadDestination").get())
            .isInstanceOf(DestinationAccessException.class)
            .hasRootCauseInstanceOf(MalformedJsonException.class);
    }

    @Test
    void testSetTimeLimiterTimeout()
    {
        final DestinationService loader =
            DestinationService
                .builder()
                .withTimeLimiterConfiguration(TimeLimiterConfiguration.of(Duration.ofSeconds(100)))
                .withProviderTenant(new DefaultTenant("Foo"))
                .build();
        assertThat(loader.getSingleDestResilience().timeLimiterConfiguration().timeoutDuration())
            .isEqualTo(Duration.ofSeconds(100));
        assertThat(loader.getAllDestResilience().timeLimiterConfiguration().timeoutDuration())
            .isEqualTo(Duration.ofSeconds(100));
    }

    @Test
    void testGetAllDestinationsHandlesFailure()
    {
        doThrow(new DestinationAccessException("Error"))
            .when(destinationServiceAdapter)
            .getConfigurationAsJson(
                eq("/instanceDestinations"),
                argThat(s -> s.behalf() == OnBehalfOf.TECHNICAL_USER_PROVIDER));
        doThrow(new DestinationAccessException("Error"))
            .when(destinationServiceAdapter)
            .getConfigurationAsJson(
                eq("/subaccountDestinations"),
                argThat(s -> s.behalf() == OnBehalfOf.TECHNICAL_USER_PROVIDER));

        assertThatThrownBy(loader::getAllDestinationProperties).isExactlyInstanceOf(DestinationAccessException.class);
    }

    @Test
    void testAuthTokenFailureIsNotCached()
    {
        doReturn(responseDestinationWithoutAuthToken)
            .when(destinationServiceAdapter)
            .getConfigurationAsJson(
                "/destinations/CC8-HTTP-OAUTH",
                withUserToken(TECHNICAL_USER_CURRENT_TENANT, userToken));

        final DestinationOptions options = DestinationOptions.builder().build();

        final Try<Destination> destination = loader.tryGetDestination("CC8-HTTP-OAUTH", options);

        assertThatThrownBy(destination::get).isExactlyInstanceOf(DestinationAccessException.class);

        loader.tryGetDestination("CC8-HTTP-OAUTH", options);
        loader.tryGetDestination("CC8-HTTP-OAUTH", options);

        // verify the result is not cached
        verify(destinationServiceAdapter, times(3))
            .getConfigurationAsJson(
                "/destinations/CC8-HTTP-OAUTH",
                withUserToken(TECHNICAL_USER_CURRENT_TENANT, userToken));
    }

    @Test
    void testFragmentDestinationsAreCacheIsolated()
    {
        DestinationService.Cache.disableChangeDetection();
        final String destinationTemplate = """
            {
                "owner": {
                    "SubaccountId": "00000000-0000-0000-0000-000000000000",
                    "InstanceId": null
                },
                "destinationConfiguration": {
                    "Name": "destination",
                    %s
                    "Type": "HTTP",
                    "URL": "https://%s.com/",
                    "Authentication": "NoAuthentication",
                    "ProxyType": "Internet"
                }
            }
            """;

        doReturn(destinationTemplate.formatted("\"FragmentName\": \"a-fragment\",", "a.fragment"))
            .when(destinationServiceAdapter)
            .getConfigurationAsJson(any(), argThat(it -> "a-fragment".equals(it.fragment())));
        doReturn(destinationTemplate.formatted("\"FragmentName\": \"b-fragment\",", "b.fragment"))
            .when(destinationServiceAdapter)
            .getConfigurationAsJson(any(), argThat(it -> "b-fragment".equals(it.fragment())));
        doReturn(destinationTemplate.formatted("", "destination"))
            .when(destinationServiceAdapter)
            .getConfigurationAsJson(any(), argThat(it -> it.fragment() == null));

        final Function<String, DestinationOptions> optsBuilder =
            frag -> DestinationOptions
                .builder()
                .augmentBuilder(DestinationServiceOptionsAugmenter.augmenter().fragmentName(frag))
                .build();

        final Destination dA = loader.tryGetDestination("destination", optsBuilder.apply("a-fragment")).get();
        final Destination dB = loader.tryGetDestination("destination", optsBuilder.apply("b-fragment")).get();
        final Destination d = loader.tryGetDestination("destination").get();

        assertThat(dA).isNotEqualTo(dB).isNotEqualTo(d);
        assertThat(dA.get("FragmentName")).contains("a-fragment");
        assertThat(dB).isNotEqualTo(d);
        assertThat(dB.get("FragmentName")).contains("b-fragment");

        assertThat(d.get("FragmentName")).isEmpty();

        assertThat(dA)
            .describedAs("Destinations with fragments should be cached")
            .isSameAs(loader.tryGetDestination("destination", optsBuilder.apply("a-fragment")).get());
        verify(destinationServiceAdapter, times(3)).getConfigurationAsJson(any(), any());
    }

    // @Test
    // Performance test is unreliable on Jenkins
    void runLoadTest()
        throws InterruptedException
    {
        final Random random = new Random();
        final Answer<String> response = ( any ) -> {
            try {
                if( random.nextInt(100) < 1 ) {
                    Thread.sleep(5000);
                }
            }
            catch( final InterruptedException e ) {
                throw new RuntimeException(e);
            }
            return responseDestinationWithAuthToken;
        };
        doAnswer(response)
            .when(destinationServiceAdapter)
            .getConfigurationAsJson(any(), withoutToken(TECHNICAL_USER_CURRENT_TENANT));
        doAnswer(response)
            .when(destinationServiceAdapter)
            .getConfigurationAsJson(any(), withUserToken(TECHNICAL_USER_CURRENT_TENANT, userToken));

        final ExecutorService executorService = Executors.newFixedThreadPool(100);
        final int iterations = 10000;
        for( int i = 1; i <= iterations; i++ ) {
            final String name = "" + i;
            executorService.submit(() -> {
                loader.tryGetDestination(name, DestinationOptions.builder().build()).get();
                System.out.println("[" + LocalDateTime.now() + "] Got " + name);
            });
        }

        // most destinations should load immediately
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.MINUTES);
    }

    // Run performance test only when required
    //@Test
    public void performanceTest()
    {
        final Answer<String> response = ( any ) -> {
            try {
                Thread.sleep(300);
            }
            catch( final InterruptedException e ) {
                throw new RuntimeException(e);
            }
            return responseDestinationWithBasicAuthToken;
        };
        doAnswer(response).when(destinationServiceAdapter).getConfigurationAsJson(any(), any());

        HttpClient httpClient = null;
        Destination destination = null;

        final int iterations = 1000;
        for( int i = 1; i <= iterations; i++ ) {
            final String name = "any";
            final Principal principal = new DefaultPrincipal("principal-" + i);
            context.setPrincipal(principal);
            assertThat(PrincipalAccessor.getCurrentPrincipal().getPrincipalId()).isEqualTo("principal-" + i);
            destination = loader.tryGetDestination(name, DestinationOptions.builder().build()).get().asHttp();

            httpClient = HttpClientAccessor.getHttpClient(destination);
            System.out.println("[" + LocalDateTime.now() + "] Got " + name);
        }
        assertThat(DestinationService.Cache.instanceSingle().estimatedSize()).isEqualTo(1);
        assertThat(httpClient).isSameAs(HttpClientAccessor.getHttpClient(destination));
    }

    private String createHttpDestinationServiceResponse( final String name, final String url )
    {
        return String.format("""
            {
                "owner": {
                    "SubaccountId": "someId",
                    "InstanceId": null
                },
                "destinationConfiguration": {
                    "Name": "%s",
                    "Type": "HTTP",
                    "URL": "%s",
                    "Authentication": "NoAuthentication",
                    "ProxyType": "Internet",
                    "Description": "Test destination"
                }
            }
            """, name, url);
    }
}
