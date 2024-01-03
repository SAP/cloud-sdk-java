/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceOptionsAugmenter.DESTINATION_RETRIEVAL_STRATEGY_KEY;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceOptionsAugmenter.DESTINATION_TOKEN_EXCHANGE_STRATEGY_KEY;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceOptionsAugmenter.augmenter;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceRetrievalStrategy.ALWAYS_PROVIDER;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceRetrievalStrategy.CURRENT_TENANT;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceRetrievalStrategy.ONLY_SUBSCRIBER;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceTokenExchangeStrategy.EXCHANGE_ONLY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.stubbing.Answer;

import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.sdk.cloudplatform.cache.CacheKey;
import com.sap.cloud.sdk.cloudplatform.cache.CacheManager;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration.TimeLimiterConfiguration;
import com.sap.cloud.sdk.cloudplatform.security.principal.DefaultPrincipal;
import com.sap.cloud.sdk.cloudplatform.security.principal.DefaultPrincipalFacade;
import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenantFacade;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutors;

import io.vavr.control.Try;
import lombok.SneakyThrows;

class DestinationServiceTest
{
    private static final int TEST_TIMEOUT = 30_000; // 5 minutes
    // region (TEST_DATA)
    private static final String destinationName = "SomeDestinationName";
    private static final String providerUrl = "https://service.provider.com";
    private static final String subscriberUrl = "https://service.subscriber.com";

    private static final String responseSubaccountDestination =
        "[{\n"
            + "    \"Name\": \"CC8-HTTP-BASIC\",\n"
            + "    \"Type\": \"HTTP\",\n"
            + "    \"URL\": \"https://a.s4hana.ondemand.com\",\n"
            + "    \"Authentication\": \"BasicAuthentication\",\n"
            + "    \"ProxyType\": \"Internet\",\n"
            + "    \"TrustAll\": \"TRUE\",\n"
            + "    \"User\": \"USER\",\n"
            + "    \"Password\": \"pass\"\n"
            + "  },\n"
            + "  {\n"
            + "    \"Name\": \"CC8-HTTP-CERT\",\n"
            + "    \"Type\": \"HTTP\",\n"
            + "    \"URL\": \"https://a.s4hana.ondemand.com\",\n"
            + "    \"Authentication\": \"ClientCertificateAuthentication\",\n"
            + "    \"ProxyType\": \"Internet\",\n"
            + "    \"KeyStorePassword\": \"password\",\n"
            + "    \"KeyStoreLocation\": \"aaa\"\n"
            + "  }]";

    private static final String brokenResponseSubaccountDestination =
        "[{\n"
            + "    \"Name\": \"CC8-HTTP-BASIC\",\n"
            + "    \"Type\": \"HTTP\",\n"
            + "    \"URL\": \"https://a.s4hana.ondemand.com\",\n"
            + "    \"Authentication\": \"BasicAuthentication\",\n"
            + "    \"ProxyType\": \"Internet\",\n"
            + "    \"TrustAll\": \"TRUE\",\n"
            + "    \"User\": \"USER\",\n"
            + "    \"Password\": \"pass\"\n"
            + "  },\n"
            + "  {\n"
            // name is missing which is not allowed
            + "    \"Type\": \"BROKEN_TYPE\",\n"
            + "    \"URL\": \"https:/brok en URL!\"\n"
            + "  }]";

    private static final String responseServiceInstanceDestination =
        "[{\n"
            + "    \"Name\": \"CC8-HTTP-BASIC\",\n"
            + "    \"Type\": \"HTTP\",\n"
            + "    \"URL\": \"https://a.s4hana.ondemand.com\",\n"
            + "    \"Authentication\": \"BasicAuthentication\",\n"
            + "    \"ProxyType\": \"Internet\",\n"
            + "    \"TrustAll\": \"TRUE\",\n"
            + "    \"User\": \"USER1\",\n"
            + "    \"Password\": \"pass\"\n"
            + "  },\n"
            + "  {\n"
            + "    \"Name\": \"CC8-HTTP-CERT1\",\n"
            + "    \"Type\": \"HTTP\",\n"
            + "    \"URL\": \"https://a.s4hana.ondemand.com\",\n"
            + "    \"Authentication\": \"ClientCertificateAuthentication\",\n"
            + "    \"ProxyType\": \"Internet\",\n"
            + "    \"KeyStorePassword\": \"password\",\n"
            + "    \"KeyStoreLocation\": \"aaa\"\n"
            + "  }]";

    private static final String responseDestinationWithoutAuthToken =
        "{\n"
            + "    \"owner\": {\n"
            + "        \"SubaccountId\": \"00000000-0000-0000-0000-000000000000\",\n"
            + "        \"InstanceId\": null\n"
            + "    },\n"
            + "    \"destinationConfiguration\": {\n"
            + "        \"Name\": \"CC8-HTTP-OAUTH\",\n"
            + "        \"Type\": \"HTTP\",\n"
            + "        \"URL\": \"https://a.s4hana.ondemand.com/\",\n"
            + "        \"Authentication\": \"OAuth2SAMLBearerAssertion\",\n"
            + "        \"ProxyType\": \"Internet\"\n"
            + "    },\n"
            + "    \"authTokens\": [\n"
            + "        {\n"
            + "            \"type\": \"\",\n"
            + "            \"value\": \"\",\n"
            + "            \"error\": \"org.apache.http.HttpException: Request to the /userinfo endpoint ended with status code 403\",\n"
            + "            \"expires_in\": \"\"\n"
            + "        }\n"
            + "    ]\n"
            + "}";

    private static final String responseDestinationWithAuthToken =
        "{\n"
            + "    \"owner\": {\n"
            + "        \"SubaccountId\": \"00000000-0000-0000-0000-000000000000\",\n"
            + "        \"InstanceId\": null\n"
            + "    },\n"
            + "    \"destinationConfiguration\": {\n"
            + "        \"Name\": \"CC8-HTTP-OAUTH\",\n"
            + "        \"Type\": \"HTTP\",\n"
            + "        \"URL\": \"https://a.s4hana.ondemand.com/\",\n"
            + "        \"Authentication\": \"OAuth2SAMLBearerAssertion\",\n"
            + "        \"ProxyType\": \"Internet\"\n"
            + "    },\n"
            + "    \"authTokens\": [\n"
            + "        {\n"
            + "            \"type\": \"Bearer\",\n"
            + "            \"value\": \"bearer_token\",\n"
            + "            \"http_header\": {\n"
            + "                \"key\": \"Authorization\",\n"
            + "                \"value\": \"Bearer bearer_token\"\n"
            + "            },\n"
            + "            \"expires_in\": \"3600\",\n"
            + "            \"scope\": \"API_BUSINESS_PARTNER_0001\"\n"
            + "        }\n"
            + "    ]\n"
            + "}";

    private static final String responseDestinationWithExpiredAuthToken =
        "{\n"
            + "    \"owner\": {\n"
            + "        \"SubaccountId\": \"00000000-0000-0000-0000-000000000000\",\n"
            + "        \"InstanceId\": null\n"
            + "    },\n"
            + "    \"destinationConfiguration\": {\n"
            + "        \"Name\": \"CC8-HTTP-OAUTH\",\n"
            + "        \"Type\": \"HTTP\",\n"
            + "        \"URL\": \"https://a.s4hana.ondemand.com/\",\n"
            + "        \"Authentication\": \"OAuth2SAMLBearerAssertion\",\n"
            + "        \"ProxyType\": \"Internet\"\n"
            + "    },\n"
            + "    \"authTokens\": [\n"
            + "        {\n"
            + "            \"type\": \"Bearer\",\n"
            + "            \"value\": \"bearer_token\",\n"
            + "            \"http_header\": {\n"
            + "                \"key\": \"Authorization\",\n"
            + "                \"value\": \"Bearer bearer_token\"\n"
            + "            },\n"
            + "            \"expires_in\": \"5\",\n"
            + "            \"scope\": \"API_BUSINESS_PARTNER_0001\"\n"
            + "        }\n"
            + "    ]\n"
            + "}";

    //Fictional use-case where the destination service responds with multiple auth tokens
    private static final String responseDestinationWithMultipleAuthTokens =
        "{\n"
            + "    \"owner\": {\n"
            + "        \"SubaccountId\": \"00000000-0000-0000-0000-000000000000\",\n"
            + "        \"InstanceId\": null\n"
            + "    },\n"
            + "    \"destinationConfiguration\": {\n"
            + "        \"Name\": \"CC8-HTTP-OAUTH\",\n"
            + "        \"Type\": \"HTTP\",\n"
            + "        \"URL\": \"https://a.s4hana.ondemand.com/\",\n"
            + "        \"Authentication\": \"OAuth2SAMLBearerAssertion\",\n"
            + "        \"ProxyType\": \"Internet\"\n"
            + "    },\n"
            + "    \"authTokens\": [\n"
            + "        {\n"
            + "            \"type\": \"Bearer\",\n"
            + "            \"value\": \"bearer_token\",\n"
            + "            \"http_header\": {\n"
            + "                \"key\": \"Authorization\",\n"
            + "                \"value\": \"Bearer bearer_token\"\n"
            + "            },\n"
            + "            \"expires_in\": \"5\",\n"
            + "            \"scope\": \"API_BUSINESS_PARTNER_0001\"\n"
            + "        },\n"
            + "        {\n"
            + "            \"type\": \"Bearer\",\n"
            + "            \"value\": \"bearer_token\",\n"
            + "            \"http_header\": {\n"
            + "                \"key\": \"Authorization\",\n"
            + "                \"value\": \"Bearer bearer_token\"\n"
            + "            },\n"
            + "            \"expires_in\": \"50\",\n"
            + "            \"scope\": \"API_BUSINESS_PARTNER_0001\"\n"
            + "        }\n"
            + "    ]\n"
            + "}";

    private static final String responseDestinationWithBasicAuthToken =
        "{\n"
            + "    \"owner\": {\n"
            + "        \"SubaccountId\": \"00000000-0000-0000-0000-000000000000\",\n"
            + "        \"InstanceId\": null\n"
            + "    },\n"
            + "    \"destinationConfiguration\": {\n"
            + "        \"Name\": \"CC8-HTTP-BASIC\",\n"
            + "        \"Type\": \"HTTP\",\n"
            + "        \"URL\": \"https://a.s4hana.ondemand.com\",\n"
            + "        \"Authentication\": \"BasicAuthentication\",\n"
            + "        \"ProxyType\": \"Internet\",\n"
            + "        \"TrustAll\": \"TRUE\",\n"
            + "        \"User\": \"USER\",\n"
            + "        \"Password\": \"pass\"\n"
            + "    },\n"
            + "    \"authTokens\": [\n"
            + "        {\n"
            + "            \"type\": \"Basic\",\n"
            + "            \"value\": \"dGVzdDpwYgXNzMTIzNDU=\",\n"
            + "            \"http_header\": {\n"
            + "                \"key\": \"Authorization\",\n"
            + "                \"value\": \"Basic dGVzdDpwYgXNzMTIzNDU=\"\n"
            + "            }\n"
            + "        }\n"
            + "    ]\n"
            + "}";

    private static final String responseDestinationWithNoAuthToken =
        "{\n"
            + "    \"owner\": {\n"
            + "        \"SubaccountId\": \"00000000-0000-0000-0000-000000000000\",\n"
            + "        \"InstanceId\": null\n"
            + "    },\n"
            + "    \"destinationConfiguration\": {\n"
            + "        \"Name\": \"Subscriber-CCT\",\n"
            + "        \"Type\": \"HTTP\",\n"
            + "        \"URL\": \"https://wrong.com\",\n"
            + "        \"Authentication\": \"NoAuthentication\",\n"
            + "        \"ProxyType\": \"Internet\",\n"
            + "        \"Description\": \"Dummy destination that should be overwritten by a subscriber.\"\n"
            + "    }\n"
            + "}";
    // endregion

    @RegisterExtension
    TokenRule token = TokenRule.createXsuaa();

    private DestinationServiceAdapter scpCfDestinationServiceAdapter;
    private DestinationService loader;
    private Tenant providerTenant;
    private DefaultTenant subscriberTenant;
    private DefaultPrincipal principal1;
    private DefaultPrincipal principal2;

    @BeforeEach
    void setup()
    {
        // IMPORTANT: Do not remove or move mock() calls up to the class fields!  Mocks need to be reset for each test
        // to reset execution counters. Remember that all test methods are executed in parallel.
        CacheManager.invalidateAll();

        providerTenant = new DefaultTenant("provider-tenant");
        subscriberTenant = new DefaultTenant("subscriber-tenant");
        TenantAccessor.setTenantFacade(() -> Try.success(subscriberTenant));

        principal1 = new DefaultPrincipal("principal-1");
        principal2 = new DefaultPrincipal("principal-2");
        PrincipalAccessor.setPrincipalFacade(() -> Try.success(principal1));

        scpCfDestinationServiceAdapter =
            spy(
                new DestinationServiceAdapter(
                    behalf -> DefaultHttpDestination.builder("").build(),
                    () -> mock(ServiceBinding.class),
                    providerTenant.getTenantId()));
        loader =
            new DestinationService(
                scpCfDestinationServiceAdapter,
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
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson(destinationPath, OnBehalfOf.TECHNICAL_USER_PROVIDER);
        doReturn(httResponseProvider)
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJsonWithUserToken(destinationPath, OnBehalfOf.TECHNICAL_USER_PROVIDER);
        doReturn(httpResponseSubscriber)
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson(destinationPath, OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
        doReturn(httpResponseSubscriber)
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson(destinationPath, OnBehalfOf.NAMED_USER_CURRENT_TENANT);
        doReturn(httpResponseSubscriber)
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJsonWithUserToken(destinationPath, OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
    }

    @BeforeEach
    @AfterEach
    void resetDestinationCache()
    {
        DestinationService.Cache.reset();
    }

    @AfterAll
    static void resetFacades()
    {
        TenantAccessor.setTenantFacade(null);
        PrincipalAccessor.setPrincipalFacade(null);
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
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson("/instanceDestinations", OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
        doReturn(responseSubaccountDestination)
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson("/subaccountDestinations", OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);

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
        verify(scpCfDestinationServiceAdapter, times(1))
            .getConfigurationAsJson("/instanceDestinations", OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
        verify(scpCfDestinationServiceAdapter, times(1))
            .getConfigurationAsJson("/subaccountDestinations", OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
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
        verify(adapter, times(1))
            .getConfigurationAsJsonWithUserToken(
                "/destinations/SomeDestinationName",
                OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);

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
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson("/instanceDestinations", OnBehalfOf.TECHNICAL_USER_PROVIDER);
        doReturn(responseSubaccountDestination)
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson("/subaccountDestinations", OnBehalfOf.TECHNICAL_USER_PROVIDER);

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
        TenantAccessor.setTenantFacade(null);

        // set current tenant to be the provider tenant
        TenantAccessor.setTenantFacade(new DefaultTenantFacade());
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
        TenantAccessor.setTenantFacade(() -> Try.success(providerTenant));

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
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson("/destinations/CC8-HTTP-OAUTH", OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
        doReturn(responseDestinationWithAuthToken)
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson("/destinations/CC8-HTTP-OAUTH", OnBehalfOf.NAMED_USER_CURRENT_TENANT);

        @SuppressWarnings( "deprecation" )
        final DestinationOptionsAugmenter optionsStrategy =
            augmenter().tokenExchangeStrategy(DestinationServiceTokenExchangeStrategy.LOOKUP_THEN_EXCHANGE);
        final DestinationOptions options = DestinationOptions.builder().augmentBuilder(optionsStrategy).build();

        final Try<Destination> destination = loader.tryGetDestination("CC8-HTTP-OAUTH", options);
        final HttpDestinationProperties httpDestination = destination.get().asHttp();

        assertThat(httpDestination.getHeaders()).containsExactly(new Header("Authorization", "Bearer bearer_token"));

        verify(scpCfDestinationServiceAdapter, times(1))
            .getConfigurationAsJson("/destinations/CC8-HTTP-OAUTH", OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
        verify(scpCfDestinationServiceAdapter, times(1))
            .getConfigurationAsJson("/destinations/CC8-HTTP-OAUTH", OnBehalfOf.NAMED_USER_CURRENT_TENANT);
    }

    @Test
    void testTokenExchangeLookupOnly()
    {
        doReturn(responseDestinationWithoutAuthToken)
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson("/destinations/CC8-HTTP-OAUTH", OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);

        @SuppressWarnings( "deprecation" )
        final DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(augmenter().tokenExchangeStrategy(DestinationServiceTokenExchangeStrategy.LOOKUP_ONLY))
                .build();

        final Try<Destination> destination = loader.tryGetDestination("CC8-HTTP-OAUTH", options);

        assertThatThrownBy(destination::get).isExactlyInstanceOf(DestinationAccessException.class);

        verify(scpCfDestinationServiceAdapter, times(1))
            .getConfigurationAsJson("/destinations/CC8-HTTP-OAUTH", OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
        verify(scpCfDestinationServiceAdapter, times(0))
            .getConfigurationAsJson("/destinations/CC8-HTTP-OAUTH", OnBehalfOf.NAMED_USER_CURRENT_TENANT);
        verify(scpCfDestinationServiceAdapter, times(0))
            .getConfigurationAsJsonWithUserToken(
                "/destinations/CC8-HTTP-OAUTH",
                OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
    }

    @Test
    void testTokenExchangeExchangeOnly()
    {
        doReturn(responseDestinationWithAuthToken)
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson("/destinations/CC8-HTTP-OAUTH", OnBehalfOf.NAMED_USER_CURRENT_TENANT);

        final DestinationOptions options =
            DestinationOptions.builder().augmentBuilder(augmenter().tokenExchangeStrategy(EXCHANGE_ONLY)).build();

        final Try<Destination> destination = loader.tryGetDestination("CC8-HTTP-OAUTH", options);
        final HttpDestinationProperties httpDestination = destination.get().asHttp();

        assertThat(httpDestination.getHeaders()).containsExactly(new Header("Authorization", "Bearer bearer_token"));

        verify(scpCfDestinationServiceAdapter, times(1))
            .getConfigurationAsJson("/destinations/CC8-HTTP-OAUTH", OnBehalfOf.NAMED_USER_CURRENT_TENANT);
        verify(scpCfDestinationServiceAdapter, times(0)).getConfigurationAsJsonWithUserToken(any(), any());
    }

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
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson(eq("/destinations/" + destinationName), any(OnBehalfOf.class));

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

        verify(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson("/destinations/" + destinationName, OnBehalfOf.TECHNICAL_USER_PROVIDER);
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
        verify(scpCfDestinationServiceAdapter, times(1))
            .getConfigurationAsJsonWithUserToken(
                "/destinations/" + destinationName,
                OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);

        loader.tryGetDestination(destinationName, defaultOptions);
        verify(scpCfDestinationServiceAdapter, times(1))
            .getConfigurationAsJsonWithUserToken(
                "/destinations/" + destinationName,
                OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);

        loader.tryGetDestination(destinationName, optionsWithDefaultRetrievalStrategy);
        verify(scpCfDestinationServiceAdapter, times(2))
            .getConfigurationAsJsonWithUserToken(
                "/destinations/" + destinationName,
                OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);

        loader.tryGetDestination(destinationName, secondInstanceOfDefaultOptions);
        verify(scpCfDestinationServiceAdapter, times(2))
            .getConfigurationAsJsonWithUserToken(
                "/destinations/" + destinationName,
                OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
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
            verify(scpCfDestinationServiceAdapter, times(1))
                .getConfigurationAsJsonWithUserToken(
                    "/destinations/" + destinationName,
                    OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
        }
        // sleep to guarantee at least 1ms difference
        Thread.sleep(100);
        {
            // second invocation
            final DestinationOptions options =
                DestinationOptions.builder().parameter("timestamp", Instant.now().toEpochMilli()).build();

            loader.tryGetDestination(destinationName, options);
            verify(scpCfDestinationServiceAdapter, times(2))
                .getConfigurationAsJsonWithUserToken(
                    "/destinations/" + destinationName,
                    OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
        }
    }

    @Test
    void testCachingHttpDestination()
    {
        loader.tryGetDestination(destinationName).get();

        for( int i = 0; i < 10; i++ ) {
            loader.tryGetDestination(destinationName).get();
        }

        verify(scpCfDestinationServiceAdapter, times(1))
            .getConfigurationAsJsonWithUserToken(
                "/destinations/" + destinationName,
                OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);

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
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson("/destinations/" + destinationName, OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);

        @SuppressWarnings( "deprecation" )
        final DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(augmenter().tokenExchangeStrategy(DestinationServiceTokenExchangeStrategy.LOOKUP_ONLY))
                .build();

        loader.tryGetDestination(destinationName, options);
        loader.tryGetDestination(destinationName, options);

        verify(scpCfDestinationServiceAdapter, times(numberOfFetches))
            .getConfigurationAsJson("/destinations/" + destinationName, OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);

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
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson("/destinations/CC8-HTTP-OAUTH", OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
        doReturn(responseDestinationWithExpiredAuthToken)
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJsonWithUserToken(
                "/destinations/CC8-HTTP-OAUTH",
                OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);

        @SuppressWarnings( "deprecation" )
        final DestinationOptionsAugmenter optionsStrategy =
            augmenter().tokenExchangeStrategy(DestinationServiceTokenExchangeStrategy.LOOKUP_THEN_EXCHANGE);
        final DestinationOptions options = DestinationOptions.builder().augmentBuilder(optionsStrategy).build();

        loader.tryGetDestination("CC8-HTTP-OAUTH", options);
        loader.tryGetDestination("CC8-HTTP-OAUTH", options);

        verify(scpCfDestinationServiceAdapter, times(2))
            .getConfigurationAsJson("/destinations/CC8-HTTP-OAUTH", OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
        verify(scpCfDestinationServiceAdapter, times(2))
            .getConfigurationAsJson("/destinations/CC8-HTTP-OAUTH", OnBehalfOf.NAMED_USER_CURRENT_TENANT);
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
        // mock tenant and principal
        TenantAccessor.setTenantFacade(() -> Try.success(providerTenant));
        PrincipalAccessor.setPrincipalFacade(() -> Try.success(principal1));

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
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson("/destinations/" + destinationName, OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);

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
        verify(scpCfDestinationServiceAdapter, times(1))
            .getConfigurationAsJson("/destinations/" + destinationName, OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);

        final Destination cachedDestination = DestinationService.Cache.instanceSingle().getIfPresent(tenantCacheKey);

        softly.assertThat(cachedDestination).isNotNull();
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
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson("/destinations/" + destinationName, OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
        @SuppressWarnings( "deprecation" )
        final DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(augmenter().tokenExchangeStrategy(DestinationServiceTokenExchangeStrategy.LOOKUP_ONLY))
                .build();

        final DefaultTenantFacade mockedTenantFacade = spy(DefaultTenantFacade.class);
        TenantAccessor.setTenantFacade(mockedTenantFacade);

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

        final CacheKey firstTenantCacheKey = CacheKey.fromIds("TenantA", null).append(destinationName, options);
        final CacheKey secondTenantCacheKey = CacheKey.fromIds("TenantB", null).append(destinationName, options);

        assertThat(DestinationService.Cache.isolationLocks()).isNotNull();
        assertThat(DestinationService.Cache.isolationLocks().estimatedSize()).isEqualTo(2L);
        assertThat(DestinationService.Cache.isolationLocks().getIfPresent(firstTenantCacheKey)).isNotNull();
        assertThat(DestinationService.Cache.isolationLocks().getIfPresent(secondTenantCacheKey)).isNotNull();

        assertThat(DestinationService.Cache.instanceSingle().estimatedSize()).isEqualTo(2L);
        assertThat(DestinationService.Cache.instanceSingle().getIfPresent(firstTenantCacheKey)).isNotNull();
        assertThat(DestinationService.Cache.instanceSingle().getIfPresent(secondTenantCacheKey)).isNotNull();

        verify(scpCfDestinationServiceAdapter, times(2))
            .getConfigurationAsJson("/destinations/" + destinationName, OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);

        softly.assertAll();
    }

    @Test
    void testPrincipalsShareDestinationWithoutUserPropagation()
    {
        doReturn(responseDestinationWithBasicAuthToken)
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJsonWithUserToken(
                "/destinations/" + destinationName,
                OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);

        TenantAccessor.setTenantFacade(() -> Try.success(providerTenant));
        PrincipalAccessor.setPrincipalFacade(() -> Try.success(principal1));
        final Try<Destination> firstDestination = loader.tryGetDestination(destinationName);

        PrincipalAccessor.setPrincipalFacade(() -> Try.success(principal2));
        final Try<Destination> secondDestination = loader.tryGetDestination(destinationName);

        assertThat(firstDestination).isNotEmpty();
        assertThat(secondDestination).isNotEmpty();
        assertThat(firstDestination.get()).isSameAs(secondDestination.get());

        verify(scpCfDestinationServiceAdapter, times(1))
            .getConfigurationAsJsonWithUserToken(
                "/destinations/" + destinationName,
                OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
    }

    @Test
    void testPrincipalIsolationForDestinationWithUserPropagationWithExchangeOnlyStrategy()
    {
        final DestinationOptions options =
            DestinationOptions.builder().augmentBuilder(augmenter().tokenExchangeStrategy(EXCHANGE_ONLY)).build();

        doReturn(responseDestinationWithAuthToken)
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson("/destinations/" + destinationName, OnBehalfOf.NAMED_USER_CURRENT_TENANT);

        final Tenant tenant = new DefaultTenant("tenant");
        TenantAccessor.setTenantFacade(() -> Try.success(tenant));
        PrincipalAccessor.setPrincipalFacade(() -> Try.success(principal1));
        final Try<Destination> firstDestination = loader.tryGetDestination(destinationName, options);

        PrincipalAccessor.setPrincipalFacade(() -> Try.success(principal2));
        final Try<Destination> secondDestination = loader.tryGetDestination(destinationName, options);

        assertThat(firstDestination).isNotEmpty();
        assertThat(secondDestination).isNotEmpty();
        assertThat(firstDestination.get()).isNotSameAs(secondDestination.get());

        final CacheKey firstCacheKey = CacheKey.of(tenant, principal1).append(destinationName, options);
        final CacheKey secondCacheKey = CacheKey.of(tenant, principal2).append(destinationName, options);

        assertThat(DestinationService.Cache.isolationLocks()).isNotNull();
        assertThat(DestinationService.Cache.isolationLocks().estimatedSize()).isEqualTo(2L);
        assertThat(DestinationService.Cache.isolationLocks().getIfPresent(firstCacheKey)).isNotNull();
        assertThat(DestinationService.Cache.isolationLocks().getIfPresent(secondCacheKey)).isNotNull();

        assertThat(DestinationService.Cache.instanceSingle().estimatedSize()).isEqualTo(2L);
        assertThat(DestinationService.Cache.instanceSingle().getIfPresent(firstCacheKey)).isNotNull();
        assertThat(DestinationService.Cache.instanceSingle().getIfPresent(secondCacheKey)).isNotNull();

        verify(scpCfDestinationServiceAdapter, times(2))
            .getConfigurationAsJson("/destinations/" + destinationName, OnBehalfOf.NAMED_USER_CURRENT_TENANT);
    }

    @Test
    void testPrincipalIsolationForDestinationWithUserPropagationWithDefaultExchangeStrategy()
    {
        @SuppressWarnings( "deprecation" )
        final DestinationOptionsAugmenter optionsStrategy =
            augmenter().tokenExchangeStrategy(DestinationServiceTokenExchangeStrategy.LOOKUP_THEN_EXCHANGE);
        final DestinationOptions options = DestinationOptions.builder().augmentBuilder(optionsStrategy).build();

        doReturn(responseDestinationWithoutAuthToken)
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson("/destinations/" + destinationName, OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);

        doReturn(responseDestinationWithAuthToken)
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson("/destinations/" + destinationName, OnBehalfOf.NAMED_USER_CURRENT_TENANT);

        final Tenant tenant = new DefaultTenant("tenant");
        TenantAccessor.setTenantFacade(() -> Try.success(tenant));
        PrincipalAccessor.setPrincipalFacade(() -> Try.success(principal1));
        final Try<Destination> firstDestination = loader.tryGetDestination(destinationName, options);

        PrincipalAccessor.setPrincipalFacade(() -> Try.success(principal2));
        final Try<Destination> secondDestination = loader.tryGetDestination(destinationName, options);

        assertThat(firstDestination).isNotEmpty();
        assertThat(secondDestination).isNotEmpty();
        assertThat(firstDestination.get()).isNotSameAs(secondDestination.get());

        final CacheKey isolationLockKey = CacheKey.of(tenant, null).append(destinationName, options);
        final CacheKey firstCacheKey = CacheKey.of(tenant, principal1).append(destinationName, options);
        final CacheKey secondCacheKey = CacheKey.of(tenant, principal2).append(destinationName, options);

        assertThat(DestinationService.Cache.isolationLocks()).isNotNull();
        //If exchange strategy is LOOKUP_THEN_EXCHANGE, then isolation locks are obtained per tenant
        assertThat(DestinationService.Cache.isolationLocks().estimatedSize()).isEqualTo(1L);
        assertThat(DestinationService.Cache.isolationLocks().getIfPresent(isolationLockKey)).isNotNull();

        assertThat(DestinationService.Cache.instanceSingle().estimatedSize()).isEqualTo(2L);
        assertThat(DestinationService.Cache.instanceSingle().getIfPresent(firstCacheKey)).isNotNull();
        assertThat(DestinationService.Cache.instanceSingle().getIfPresent(secondCacheKey)).isNotNull();

        verify(scpCfDestinationServiceAdapter, times(2))
            .getConfigurationAsJson("/destinations/" + destinationName, OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
        verify(scpCfDestinationServiceAdapter, times(2))
            .getConfigurationAsJson("/destinations/" + destinationName, OnBehalfOf.NAMED_USER_CURRENT_TENANT);
    }

    @Test
    void testSmartCacheServesAllPrincipalsWithSameDestination()
    {
        doReturn("[]")
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson("/instanceDestinations", OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
        doReturn(responseSubaccountDestination)
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson("/subaccountDestinations", OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);

        doReturn(responseDestinationWithBasicAuthToken)
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJsonWithUserToken(
                "/destinations/CC8-HTTP-BASIC",
                OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);

        DestinationService.Cache.enableChangeDetection();
        final Tenant tenant = new DefaultTenant("tenant");
        TenantAccessor.setTenantFacade(() -> Try.success(tenant));

        final AtomicReference<Destination> destination = new AtomicReference<>();

        for( int i = 0; i < 10; ++i ) {
            final Principal principal = new DefaultPrincipal("principal-" + i);
            PrincipalAccessor.setPrincipalFacade(() -> Try.success(principal));
            final Try<Destination> tryDestination = loader.tryGetDestination("CC8-HTTP-BASIC");

            assertThat(tryDestination.isSuccess()).isTrue();
            destination.compareAndSet(null, tryDestination.get());
            //Always compares if the first destination fetched and the current destination fetched are same
            assertThat(tryDestination.get()).isSameAs(destination.get());
        }

        verify(scpCfDestinationServiceAdapter, times(1))
            .getConfigurationAsJson("/instanceDestinations", OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
        verify(scpCfDestinationServiceAdapter, times(1))
            .getConfigurationAsJson("/subaccountDestinations", OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
        verify(scpCfDestinationServiceAdapter, times(1))
            .getConfigurationAsJsonWithUserToken(
                "/destinations/CC8-HTTP-BASIC",
                OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
    }

    @Test
    void testChangeDetectionWithMisconfiguredDestination()
    {
        doReturn("[]")
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson("/instanceDestinations", OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
        // One of the getAll destinations will be misconfigured (the smart cache will implicitly call getAll)
        doReturn(brokenResponseSubaccountDestination)
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson("/subaccountDestinations", OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);

        // normal response for the requested destination
        doReturn(responseDestinationWithBasicAuthToken)
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJsonWithUserToken(
                "/destinations/CC8-HTTP-BASIC",
                OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);

        DestinationService.Cache.enableChangeDetection();
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
        verify(scpCfDestinationServiceAdapter, times(circuitBreakerBuffer))
            .getConfigurationAsJson("/instanceDestinations", OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
        verify(scpCfDestinationServiceAdapter, times(circuitBreakerBuffer))
            .getConfigurationAsJson("/subaccountDestinations", OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
        // the requested destination is unaffected
        verify(scpCfDestinationServiceAdapter, times(1))
            .getConfigurationAsJsonWithUserToken(
                "/destinations/CC8-HTTP-BASIC",
                OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
    }

    /**
     * Test case: Trying to fetch same destination concurrently from same tenant but by different principals is blocking
     */
    @Test
    @Timeout( value = TEST_TIMEOUT, unit = TimeUnit.MILLISECONDS )
    @SneakyThrows
    void testConcurrentFetchSameDestinationSameTenantButDifferentPrincipal()
    {
        // mock tenant and principal
        final Principal principalA = new DefaultPrincipal("PrincipalA");
        final Principal principalB = new DefaultPrincipal("PrincipalB");
        PrincipalAccessor.setPrincipalFacade(() -> Try.success(principalB));
        TenantAccessor.setTenantFacade(() -> Try.success(new DefaultTenant("tenant")));

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
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson("/destinations/" + destinationName, OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
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
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson("/destinations/" + destinationName, OnBehalfOf.NAMED_USER_CURRENT_TENANT);

        PrincipalAccessor.setPrincipalFacade(new DefaultPrincipalFacade());
        final Future<Try<Destination>> firstThread = ThreadContextExecutors.submit(() -> {
            softly.assertThat(TenantAccessor.tryGetCurrentTenant()).isNotEmpty();
            return PrincipalAccessor
                .executeWithPrincipal(principalA, () -> loader.tryGetDestination(destinationName, options));
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
                .executeWithPrincipal(principalB, () -> loader.tryGetDestination(destinationName, options));
        });

        assertThat(firstThread.get()).isNotEmpty();
        assertThat(secondThread.get()).isNotEmpty();
        verify(tenantLockSpy, times(1)).lock();
        verify(tenantLockSpy, times(1)).unlock();
        verify(scpCfDestinationServiceAdapter, times(2))
            .getConfigurationAsJson("/destinations/" + destinationName, OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
        verify(scpCfDestinationServiceAdapter, times(2))
            .getConfigurationAsJson("/destinations/" + destinationName, OnBehalfOf.NAMED_USER_CURRENT_TENANT);

        final CacheKey principalACacheKey = CacheKey.fromIds("tenant", "PrincipalA").append(destinationName, options);
        final CacheKey principalBCacheKey = CacheKey.fromIds("tenant", "PrincipalB").append(destinationName, options);

        final Destination cachedPrincipalADestination =
            DestinationService.Cache.instanceSingle().getIfPresent(principalACacheKey);
        softly.assertThat(cachedPrincipalADestination).isNotNull();

        final Destination cachedPrincipalBDestination =
            DestinationService.Cache.instanceSingle().getIfPresent(principalBCacheKey);
        softly.assertThat(cachedPrincipalBDestination).isNotNull();

        softly.assertAll();
    }

    @Test
    void testDestinationWithInvalidJsonProperty()
    {
        doReturn("{ \"destinationConfiguration\" : { \"Name\" : null } }")
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson("/destinations/BadDestination", OnBehalfOf.TECHNICAL_USER_PROVIDER);

        assertThatThrownBy(() -> loader.tryGetDestination("BadDestination").get())
            .isInstanceOf(DestinationAccessException.class);
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
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson("/instanceDestinations", OnBehalfOf.TECHNICAL_USER_PROVIDER);
        doThrow(new DestinationAccessException("Error"))
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson("/subaccountDestinations", OnBehalfOf.TECHNICAL_USER_PROVIDER);

        assertThatThrownBy(loader::getAllDestinationProperties).isExactlyInstanceOf(DestinationAccessException.class);
    }

    @Test
    void testAuthTokenFailureIsNotCached()
    {
        doReturn(responseDestinationWithoutAuthToken)
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJsonWithUserToken(
                "/destinations/CC8-HTTP-OAUTH",
                OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);

        final DestinationOptions options = DestinationOptions.builder().build();

        final Try<Destination> destination = loader.tryGetDestination("CC8-HTTP-OAUTH", options);

        assertThatThrownBy(destination::get).isExactlyInstanceOf(DestinationAccessException.class);

        loader.tryGetDestination("CC8-HTTP-OAUTH", options);
        loader.tryGetDestination("CC8-HTTP-OAUTH", options);

        // verify the result is not cached
        verify(scpCfDestinationServiceAdapter, times(3))
            .getConfigurationAsJsonWithUserToken(
                "/destinations/CC8-HTTP-OAUTH",
                OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
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
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson(any(), eq(OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT));
        doAnswer(response)
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJsonWithUserToken(any(), eq(OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT));

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
        doAnswer(response)
            .when(scpCfDestinationServiceAdapter)
            .getConfigurationAsJson(any(), eq(OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT));

        HttpClient httpClient = null;
        Destination destination = null;

        final int iterations = 1000;
        for( int i = 1; i <= iterations; i++ ) {
            final String name = "any";
            final Principal principal = new DefaultPrincipal("principal-" + i);
            PrincipalAccessor.setPrincipalFacade(() -> Try.success(principal));
            assertThat(PrincipalAccessor.getCurrentPrincipal().getPrincipalId()).isEqualTo("principal-" + i);
            destination = loader.tryGetDestination(name, DestinationOptions.builder().build()).get().asHttp();

            httpClient = HttpClientAccessor.getHttpClient(destination);
            System.out.println("[" + LocalDateTime.now() + "] Got " + name);
            PrincipalAccessor.setPrincipalFacade(null);
        }
        assertThat(DestinationService.Cache.instanceSingle().estimatedSize()).isEqualTo(1);
        assertThat(httpClient).isSameAs(HttpClientAccessor.getHttpClient(destination));
    }

    private String createHttpDestinationServiceResponse( final String name, final String url )
    {
        return String
            .format(
                "{\n"
                    + "    \"owner\": {\n"
                    + "        \"SubaccountId\": \"someId\",\n"
                    + "        \"InstanceId\": null\n"
                    + "    },\n"
                    + "    \"destinationConfiguration\": {\n"
                    + "        \"Name\": \"%s\",\n"
                    + "        \"Type\": \"HTTP\",\n"
                    + "        \"URL\": \"%s\",\n"
                    + "        \"Authentication\": \"NoAuthentication\",\n"
                    + "        \"ProxyType\": \"Internet\",\n"
                    + "        \"Description\": \"Test destination\"\n"
                    + "    }\n"
                    + "}",
                name,
                url);
    }
}
