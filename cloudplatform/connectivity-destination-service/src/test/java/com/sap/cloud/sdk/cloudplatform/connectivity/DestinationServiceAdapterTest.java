package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.okForJson;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationRetrievalStrategy.withUserToken;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationRetrievalStrategy.withoutToken;
import static com.sap.cloud.sdk.cloudplatform.connectivity.OnBehalfOf.NAMED_USER_CURRENT_TENANT;
import static com.sap.cloud.sdk.cloudplatform.connectivity.OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT;
import static com.sap.cloud.sdk.cloudplatform.connectivity.XsuaaTokenMocker.mockXsuaaToken;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;

import com.auth0.jwt.JWT;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.environment.servicebinding.api.DefaultServiceBinding;
import com.sap.cloud.environment.servicebinding.api.DefaultServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;
import com.sap.cloud.sdk.cloudplatform.exception.MultipleServiceBindingsException;
import com.sap.cloud.sdk.cloudplatform.exception.NoServiceBindingException;
import com.sap.cloud.sdk.cloudplatform.security.ClientCredentials;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.sdk.testutil.TestContext;
import com.sap.cloud.security.config.Service;
import com.sap.cloud.security.test.JwtGenerator;

import lombok.SneakyThrows;

@WireMockTest
class DestinationServiceAdapterTest
{
    @RegisterExtension
    static final TestContext context = TestContext.withThreadContext();
    private static final String SERVICE_NAME = "destination";
    private static final ClientCredentials CLIENT_CREDENTIALS =
        new ClientCredentials("destination-client-id", "destination-client-secret");
    private static final String PROVIDER_TENANT_ID = "provider-tenant-id";

    private static final String XSUAA_URL = "/xsuaa/oauth/token";
    private static final String DESTINATION_SERVICE_URL = "/destination-service/destination-configuration/v1/";
    private static final String DESTINATION_RESPONSE = "{ response }";

    private static final String GRANT_TYPE_JWT_BEARER = "urn:ietf:params:oauth:grant-type:jwt-bearer";
    private static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
    private static ServiceBinding DEFAULT_SERVICE_BINDING;

    private String xsuaaToken;

    @BeforeAll
    static void setupSession()
    {
        context.setPrincipal();
        context.setTenant();
    }

    @BeforeEach
    void mockResponses()
    {
        xsuaaToken = mockXsuaaToken().getToken();
        final Map<String, String> xsuaaResponse = Map.of("access_token", xsuaaToken, "expires_in", "1");
        stubFor(post(urlEqualTo(XSUAA_URL)).willReturn(okForJson(xsuaaResponse)));

        // mock destination service response
        stubFor(get(urlEqualTo(DESTINATION_SERVICE_URL)).willReturn(ok(DESTINATION_RESPONSE)));
    }

    @BeforeEach
    void createDefaultBinding( @Nonnull final WireMockRuntimeInfo wm )
    {
        DEFAULT_SERVICE_BINDING =
            serviceBinding(
                CLIENT_CREDENTIALS.getClientId(),
                CLIENT_CREDENTIALS.getClientSecret(),
                "http://localhost:" + wm.getHttpPort() + "/destination-service",
                "http://localhost:" + wm.getHttpPort() + XSUAA_URL,
                PROVIDER_TENANT_ID);
    }

    @AfterEach
    void resetServiceBindingAccessor()
    {
        DefaultServiceBindingAccessor.setInstance(null);
    }

    @Test
    void testWithoutUserTokenExchange()
    {
        final DestinationServiceAdapter adapterToTest = createSut(DEFAULT_SERVICE_BINDING);

        final String response =
            adapterToTest.getConfigurationAsJson("/", withoutToken(OnBehalfOf.TECHNICAL_USER_PROVIDER));

        assertThat(response).isEqualTo(DESTINATION_RESPONSE);
        verify(
            1,
            postRequestedFor(urlEqualTo(XSUAA_URL))
                .withRequestBody(containing("grant_type=" + GRANT_TYPE_CLIENT_CREDENTIALS))
                .withRequestBody(containing("client_secret=" + CLIENT_CREDENTIALS.getClientSecret()))
                .withRequestBody(containing("client_id=" + CLIENT_CREDENTIALS.getClientId()))
                .withoutFormParam("assertion"));

        verify(
            1,
            getRequestedFor(urlEqualTo(DESTINATION_SERVICE_URL))
                .withHeader("Authorization", equalTo("Bearer " + xsuaaToken))
                .withoutHeader("x-user-token")
                .withoutHeader("x-refresh-token"));
    }

    @Test
    void testWithUserTokenExchange()
    {
        final DestinationServiceAdapter adapterToTest = createSut(DEFAULT_SERVICE_BINDING);

        // mock AuthTokenFacade for current user token
        final String currentUserToken =
            JwtGenerator
                .getInstance(Service.XSUAA, "client-id")
                .withClaimValue("zid", "tenant-id")
                .createToken()
                .getTokenValue();
        context.setAuthToken(JWT.decode(currentUserToken));

        // actual request, ensure that the tenant matches the one in the User JWT
        final String destinationResponse =
            TenantAccessor
                .executeWithTenant(
                    () -> "tenant-id",
                    () -> adapterToTest.getConfigurationAsJson("/", withoutToken(NAMED_USER_CURRENT_TENANT)));

        assertThat(destinationResponse).isEqualTo(DESTINATION_RESPONSE);
        verify(
            1,
            postRequestedFor(urlEqualTo(XSUAA_URL))
                .withRequestBody(containing("grant_type=" + GRANT_TYPE_JWT_BEARER.replaceAll(":", "%3A")))
                .withRequestBody(containing("client_id=" + CLIENT_CREDENTIALS.getClientId()))
                .withRequestBody(containing("client_secret=" + CLIENT_CREDENTIALS.getClientSecret()))
                .withRequestBody(containing("assertion=" + currentUserToken)));

        verify(
            1,
            getRequestedFor(urlEqualTo(DESTINATION_SERVICE_URL))
                .withHeader("Authorization", equalTo("Bearer " + xsuaaToken))
                .withoutHeader("x-user-token")
                .withoutHeader("x-refresh-token"));
    }

    @Test
    void testWithUserTokenForwarding()
    {
        final DestinationServiceAdapter adapterToTest = createSut(DEFAULT_SERVICE_BINDING);

        // mock AuthTokenFacade for current user token
        final String token = mockXsuaaToken().getToken();
        context.setAuthToken(JWT.decode(token));

        // actual request, ensure that the tenant matches the one in the User JWT
        final String destinationResponse =
            adapterToTest.getConfigurationAsJson("/", withUserToken(TECHNICAL_USER_CURRENT_TENANT, token));

        assertThat(destinationResponse).isEqualTo(DESTINATION_RESPONSE);
        verify(
            1,
            postRequestedFor(urlEqualTo(XSUAA_URL))
                .withRequestBody(containing("grant_type=" + GRANT_TYPE_CLIENT_CREDENTIALS))
                .withRequestBody(containing("client_secret=" + CLIENT_CREDENTIALS.getClientSecret()))
                .withRequestBody(containing("client_id=" + CLIENT_CREDENTIALS.getClientId()))
                .withoutFormParam("assertion"));

        verify(
            1,
            getRequestedFor(urlEqualTo(DESTINATION_SERVICE_URL))
                .withHeader("Authorization", equalTo("Bearer " + xsuaaToken))
                .withHeader("x-user-token", equalTo(token))
                .withoutHeader("x-refresh-token"));
    }

    @Test
    void testRefreshTokenFlow()
    {
        final DestinationServiceAdapter adapterToTest = createSut(DEFAULT_SERVICE_BINDING);

        final String refreshToken = "refreshToken";

        final String destinationResponse =
            adapterToTest
                .getConfigurationAsJson(
                    "/",
                    DestinationRetrievalStrategy.withRefreshToken(TECHNICAL_USER_CURRENT_TENANT, refreshToken));

        assertThat(destinationResponse).isEqualTo(DESTINATION_RESPONSE);
        verify(
            1,
            postRequestedFor(urlEqualTo(XSUAA_URL))
                .withRequestBody(containing("grant_type=" + GRANT_TYPE_CLIENT_CREDENTIALS))
                .withRequestBody(containing("client_secret=" + CLIENT_CREDENTIALS.getClientSecret()))
                .withRequestBody(containing("client_id=" + CLIENT_CREDENTIALS.getClientId()))
                .withoutFormParam("assertion"));

        verify(
            1,
            getRequestedFor(urlEqualTo(DESTINATION_SERVICE_URL))
                .withHeader("Authorization", equalTo("Bearer " + xsuaaToken))
                .withHeader("x-refresh-token", equalTo(refreshToken))
                .withoutHeader("x-user-token"));
    }

    @Test
    void testFragmentName()
    {
        final DestinationServiceAdapter adapterToTest = createSut(DEFAULT_SERVICE_BINDING);

        final String fragment = "my-fragment";

        final String destinationResponse =
            adapterToTest
                .getConfigurationAsJson("/", withoutToken(TECHNICAL_USER_CURRENT_TENANT).withFragmentName(fragment));

        assertThat(destinationResponse).isEqualTo(DESTINATION_RESPONSE);

        verify(
            1,
            getRequestedFor(urlEqualTo(DESTINATION_SERVICE_URL))
                .withHeader("Authorization", equalTo("Bearer " + xsuaaToken))
                .withHeader("x-fragment-name", equalTo(fragment))
                .withoutHeader("x-user-token"));
    }

    @Test
    void getDestinationServiceProviderTenantShouldReturnProviderTenantFromServiceBinding()
    {
        final DestinationServiceAdapter adapterToTest = createSut(DEFAULT_SERVICE_BINDING);
        final String providerTenantId = adapterToTest.getProviderTenantId();
        assertThat(providerTenantId).isEqualTo(PROVIDER_TENANT_ID);
    }

    @Test
    void getDestinationServiceProviderTenantShouldThrowExceptionIfTenantIdIsNotFound()
    {
        // case: no service binding
        assertThatThrownBy(() -> createSut().getProviderTenantId())
            .isInstanceOf(DestinationAccessException.class)
            .hasCauseExactlyInstanceOf(NoServiceBindingException.class);

        // case: multiple service bindings
        final ServiceBinding firstBindingWithTenantId =
            serviceBinding("foo", "bar", "http://foo.bar", "http://foo.bar", "foo");
        final ServiceBinding secondBindingWithTenantId =
            serviceBinding("foo", "bar", "http://foo.bar", "http://foo.bar", "foo");
        assertThatThrownBy(() -> createSut(firstBindingWithTenantId, secondBindingWithTenantId).getProviderTenantId())
            .isExactlyInstanceOf(DestinationAccessException.class)
            .hasCauseExactlyInstanceOf(MultipleServiceBindingsException.class);

        // case: service binding without tenantid
        final ServiceBinding bindingWithoutTenantId =
            serviceBinding("foo", "bar", "http://foo.bar", "http://foo.bar", null);
        assertThatThrownBy(() -> createSut(bindingWithoutTenantId).getProviderTenantId())
            .isInstanceOf(DestinationAccessException.class);

        // case: service binding with tenantid
        assertThat(createSut(firstBindingWithTenantId).getProviderTenantId()).isEqualTo("foo");
    }

    @Test
    void testGetDestinationServiceBindingChecksForServiceName()
    {
        final ServiceBinding binding = mock(ServiceBinding.class);
        when(binding.getServiceIdentifier()).thenReturn(Optional.empty());
        mockServiceBindingAccessor(binding);

        assertThatThrownBy(DestinationServiceAdapter::getDestinationServiceBinding)
            .isExactlyInstanceOf(NoServiceBindingException.class);
        Mockito.verify(binding, times(1)).getServiceIdentifier();
    }

    @Test
    void testGetDestinationServiceBindingUsesCorrectServiceIdentifier()
    {
        final ServiceBinding binding = mock(ServiceBinding.class);
        when(binding.getServiceIdentifier()).thenReturn(Optional.of(ServiceIdentifier.DESTINATION));
        mockServiceBindingAccessor(binding);

        assertThat(DestinationServiceAdapter.getDestinationServiceBinding()).isSameAs(binding);
        Mockito.verify(binding, times(1)).getServiceIdentifier();
    }

    @Test
    void testGetDestinationServiceBindingWithoutBinding()
    {
        mockServiceBindingAccessor();

        assertThatThrownBy(DestinationServiceAdapter::getDestinationServiceBinding)
            .isExactlyInstanceOf(NoServiceBindingException.class);
    }

    @Test
    void testGetDestinationServiceBindingWithMultipleBindings()
    {
        final ServiceBinding firstBinding = mock(ServiceBinding.class);
        final ServiceBinding secondBinding = mock(ServiceBinding.class);
        when(firstBinding.getServiceIdentifier()).thenReturn(Optional.of(ServiceIdentifier.DESTINATION));
        when(secondBinding.getServiceIdentifier()).thenReturn(Optional.of(ServiceIdentifier.DESTINATION));

        mockServiceBindingAccessor(firstBinding, secondBinding);

        assertThatThrownBy(DestinationServiceAdapter::getDestinationServiceBinding)
            .isExactlyInstanceOf(MultipleServiceBindingsException.class);
    }

    @Test
    void getDestinationServiceProviderTenantShouldThrowForMissingId()
    {
        final ServiceBinding serviceBinding =
            DefaultServiceBinding
                .builder()
                .copy(Collections.singletonMap("credentials", Collections.emptyMap()))
                .withServiceName(SERVICE_NAME)
                .withCredentialsKey("credentials")
                .build();

        final DestinationServiceAdapter adapterToTest = createSut(serviceBinding);
        assertThatThrownBy(adapterToTest::getProviderTenantId)
            .isInstanceOf(DestinationAccessException.class)
            .hasMessage("""
                The provider tenant id is not defined in the service binding.\
                 Please verify that the service binding contains the field 'tenantid' in the credentials list.\
                """);
    }

    @SneakyThrows
    @Test
    void testErrorHandling()
    {
        final var httpClient = mock(HttpClient.class);
        final var destination = DefaultHttpDestination.builder("http://foo").build();
        HttpClientAccessor.setHttpClientFactory(( dest ) -> dest == destination ? httpClient : null);

        final var destinations = Collections.singletonMap(OnBehalfOf.TECHNICAL_USER_PROVIDER, destination);
        final var SUT = new DestinationServiceAdapter(destinations::get, () -> null, null);

        // setup 400 response
        var stream400 = spy(new ByteArrayInputStream("bad, evil request".getBytes(StandardCharsets.UTF_8)));
        var response400 = new BasicHttpResponse(HttpVersion.HTTP_1_1, 400, "Bad Request");
        response400.setEntity(new InputStreamEntity(stream400));
        doReturn(response400).when(httpClient).execute(any());

        // test
        assertThatThrownBy(
            () -> SUT.getConfigurationAsJson("/service-path", withoutToken(OnBehalfOf.TECHNICAL_USER_PROVIDER)))
            .isInstanceOf(DestinationAccessException.class)
            .hasMessageContaining("status 400");

        // verify closed stream
        Mockito.verify(stream400, atLeastOnce()).close();

        // setup 404 response
        var stream404 = spy(new ByteArrayInputStream("Nothing here.".getBytes(StandardCharsets.UTF_8)));
        var response404 = new BasicHttpResponse(HttpVersion.HTTP_1_1, 404, "Not Found");
        response404.setEntity(new InputStreamEntity(stream404));
        doReturn(response404).when(httpClient).execute(any());

        // test
        assertThatThrownBy(
            () -> SUT.getConfigurationAsJson("/service-path", withoutToken(OnBehalfOf.TECHNICAL_USER_PROVIDER)))
            .describedAs("A 404 should produce a DestinationNotFoundException")
            .isInstanceOf(DestinationNotFoundException.class)
            .hasMessageContaining("Destination could not be found");

        // verify closed stream
        Mockito.verify(stream404, atLeastOnce()).close();

        HttpClientAccessor.setHttpClientFactory(null);
    }

    private static DestinationServiceAdapter createSut( @Nonnull final ServiceBinding... serviceBindings )
    {
        return new DestinationServiceAdapter(null, () -> {
            if( serviceBindings.length == 0 ) {
                throw new NoServiceBindingException();
            }

            if( serviceBindings.length > 1 ) {
                throw new MultipleServiceBindingsException();
            }

            return serviceBindings[0];
        }, null);
    }

    static ServiceBinding serviceBinding(
        @Nonnull final String clientId,
        @Nonnull final String clientSecret,
        @Nonnull final String serviceUri,
        @Nonnull final String tokenUri,
        @Nullable final String tenantId )
    {
        final Map<String, Object> rawBinding = new HashMap<>();

        final Map<String, Object> credentials = new HashMap<>();
        credentials.put("credential-type", "binding-secret");
        credentials.put("clientid", clientId);
        credentials.put("clientsecret", clientSecret);
        credentials.put("uri", serviceUri);
        credentials.put("url", tokenUri);

        if( tenantId != null ) {
            credentials.put("tenantid", tenantId);
        }

        rawBinding.put("credentials", credentials);
        return DefaultServiceBinding
            .builder()
            .copy(rawBinding)
            .withServiceName(SERVICE_NAME)
            .withCredentialsKey("credentials")
            .build();
    }

    private static void mockServiceBindingAccessor( @Nonnull final ServiceBinding... serviceBindings )
    {
        final ServiceBindingAccessor serviceBindingAccessor = () -> Arrays.asList(serviceBindings);
        DefaultServiceBindingAccessor.setInstance(serviceBindingAccessor);
    }
}
