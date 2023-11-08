/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
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
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableMap;
import com.sap.cloud.environment.servicebinding.api.DefaultServiceBinding;
import com.sap.cloud.environment.servicebinding.api.DefaultServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.exception.MultipleServiceBindingsException;
import com.sap.cloud.sdk.cloudplatform.exception.NoServiceBindingException;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.security.ClientCredentials;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenantFacade;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.sdk.testutil.MockUtil;
import com.sap.cloud.security.config.Service;
import com.sap.cloud.security.test.JwtGenerator;

import io.vavr.control.Try;

public class DestinationServiceAdapterTest
{
    private static final MockUtil mockutil = new MockUtil();

    @Rule
    public final WireMockRule wireMockServer = new WireMockRule(wireMockConfig().dynamicPort());

    private static final String SERVICE_NAME = "destination";
    private static final ClientCredentials CLIENT_CREDENTIALS =
        new ClientCredentials("destination-client-id", "destination-client-secret");
    private static final String PROVIDER_TENANT_ID = "provider-tenant-id";

    private static final String XSUAA_SERVICE_ROOT = "/xsuaa";
    private static final String XSUAA_SERVICE_PATH = "/oauth/token";

    private static final String DESTINATION_SERVICE_ROOT = "/destination";
    private static final String DESTINATION_SERVICE_PATH = "/destination-configuration/v1";

    private static final String GRANT_TYPE_JWT_BEARER = "urn:ietf:params:oauth:grant-type:jwt-bearer";
    private static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
    private static ServiceBinding DEFAULT_SERVICE_BINDING;

    @BeforeClass
    public static void setupSession()
    {
        mockutil.mockCurrentPrincipal();
        mockutil.mockCurrentTenant();
    }

    @Before
    public void createDefaultBinding()
    {
        DEFAULT_SERVICE_BINDING =
            serviceBinding(
                CLIENT_CREDENTIALS.getClientId(),
                CLIENT_CREDENTIALS.getClientSecret(),
                "http://localhost:" + wireMockServer.port() + DESTINATION_SERVICE_ROOT + "/",
                "http://localhost:" + wireMockServer.port() + XSUAA_SERVICE_ROOT + "/",
                PROVIDER_TENANT_ID);
    }

    @AfterClass
    public static void resetFacades()
    {
        TenantAccessor.setTenantFacade(null);
        PrincipalAccessor.setPrincipalFacade(null);
    }

    @After
    public void resetServiceBindingAccessor()
    {
        DefaultServiceBindingAccessor.setInstance(null);
    }

    @Test
    public void testWithoutUserTokenExchange()
    {
        final DestinationServiceAdapter adapterToTest = createSut(DEFAULT_SERVICE_BINDING);

        // test parameters
        final String servicePath = "/service/path";
        final String destinationServiceResponse = "{ response }";

        // mocked request
        final String destinationServiceRequest = DESTINATION_SERVICE_ROOT + DESTINATION_SERVICE_PATH + servicePath;
        final String xsuaaServiceRequest = XSUAA_SERVICE_ROOT + XSUAA_SERVICE_PATH;

        // mock XSUAA service response
        final String oauthToken = "SOME-TOKEN";
        stubFor(
            post(urlEqualTo(xsuaaServiceRequest))
                .withRequestBody(containing("grant_type=" + GRANT_TYPE_CLIENT_CREDENTIALS))
                .withRequestBody(containing("client_secret=" + CLIENT_CREDENTIALS.getClientSecret()))
                .withRequestBody(containing("client_id=" + CLIENT_CREDENTIALS.getClientId()))
                .willReturn(
                    okForJson(
                        ImmutableMap
                            .<String, String> builder()
                            .put("access_token", oauthToken)
                            .put("expires_in", "1")
                            .build())));

        // mock destination service response
        stubFor(
            get(urlEqualTo(destinationServiceRequest))
                .withHeader("Authorization", equalTo("Bearer " + oauthToken))
                .willReturn(ok(destinationServiceResponse)));

        // actual request
        final String response = adapterToTest.getConfigurationAsJson(servicePath, OnBehalfOf.TECHNICAL_USER_PROVIDER);

        assertThat(response).isEqualTo(destinationServiceResponse);
        verify(1, postRequestedFor(urlEqualTo(xsuaaServiceRequest)));
        verify(1, getRequestedFor(urlEqualTo(destinationServiceRequest)));
    }

    @Test
    public void testWithUserTokenExchange()
    {
        final DestinationServiceAdapter adapterToTest = createSut(DEFAULT_SERVICE_BINDING);

        // test parameters
        final String servicePath = "/service/path";
        final String destinationServiceResponse = "{ response }";

        final String currentUserToken =
            JwtGenerator.getInstance(Service.XSUAA, "client-id").createToken().getTokenValue();
        final String oauthAccessToken = "EXCHANGED-USER-ACCESS-TOKEN";

        // mocked request
        final String destinationServiceRequest = DESTINATION_SERVICE_ROOT + DESTINATION_SERVICE_PATH + servicePath;
        final String xsuaaServiceRequest = XSUAA_SERVICE_ROOT + XSUAA_SERVICE_PATH;

        // mock AuthTokenFacade for current user token
        final DecodedJWT decodedJwt = mock(DecodedJWT.class);
        doReturn(currentUserToken).when(decodedJwt).getToken();
        AuthTokenAccessor.setAuthTokenFacade(() -> Try.success(new AuthToken(decodedJwt)));

        // mock XSUAA service responses
        stubFor(
            post(urlEqualTo(xsuaaServiceRequest))
                .withRequestBody(containing("grant_type=" + GRANT_TYPE_JWT_BEARER.replaceAll(":", "%3A")))
                .withRequestBody(containing("client_id=" + CLIENT_CREDENTIALS.getClientId()))
                .withRequestBody(containing("client_secret=" + CLIENT_CREDENTIALS.getClientSecret()))
                .withRequestBody(containing("assertion=" + currentUserToken))
                .willReturn(
                    okForJson(
                        ImmutableMap
                            .<String, String> builder()
                            .put("access_token", oauthAccessToken)
                            .put("expires_in", "1")
                            .build())));

        // mock destination service response
        stubFor(
            get(urlEqualTo(destinationServiceRequest))
                .withHeader("Authorization", equalTo("Bearer " + oauthAccessToken))
                .willReturn(ok(destinationServiceResponse)));

        TenantAccessor.setTenantFacade(new DefaultTenantFacade());
        // actual request, ensure that the tenant matches the one in the User JWT
        final String destinationResponse =
            TenantAccessor
                .executeWithTenant(
                    () -> "the-zone-id",
                    () -> adapterToTest.getConfigurationAsJson(servicePath, OnBehalfOf.NAMED_USER_CURRENT_TENANT));

        assertThat(destinationResponse).isEqualTo(destinationServiceResponse);
        verify(1, postRequestedFor(urlEqualTo(xsuaaServiceRequest)));
        verify(1, getRequestedFor(urlEqualTo(destinationServiceRequest)));
    }

    @Test
    public void getDestinationServiceProviderTenantShouldReturnProviderTenantFromServiceBinding()
    {
        final DestinationServiceAdapter adapterToTest = createSut(DEFAULT_SERVICE_BINDING);
        final String providerTenantId = adapterToTest.getProviderTenantId();
        assertThat(providerTenantId).isEqualTo(PROVIDER_TENANT_ID);
    }

    @Test
    public void getDestinationServiceProviderTenantShouldThrowExceptionIfTenantIdIsNotFound()
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
    public void testGetDestinationServiceBindingChecksForServiceName()
    {
        final ServiceBinding binding = mock(ServiceBinding.class);
        when(binding.getServiceIdentifier()).thenReturn(Optional.empty());
        mockServiceBindingAccessor(binding);

        assertThatThrownBy(DestinationServiceAdapter::getDestinationServiceBinding)
            .isExactlyInstanceOf(NoServiceBindingException.class);
        Mockito.verify(binding, times(1)).getServiceIdentifier();
    }

    @Test
    public void testGetDestinationServiceBindingUsesCorrectServiceIdentifier()
    {
        final ServiceBinding binding = mock(ServiceBinding.class);
        when(binding.getServiceIdentifier()).thenReturn(Optional.of(ServiceIdentifier.DESTINATION));
        mockServiceBindingAccessor(binding);

        assertThat(DestinationServiceAdapter.getDestinationServiceBinding()).isSameAs(binding);
        Mockito.verify(binding, times(1)).getServiceIdentifier();
    }

    @Test
    public void testGetDestinationServiceBindingWithoutBinding()
    {
        mockServiceBindingAccessor();

        assertThatThrownBy(DestinationServiceAdapter::getDestinationServiceBinding)
            .isExactlyInstanceOf(NoServiceBindingException.class);
    }

    @Test
    public void testGetDestinationServiceBindingWithMultipleBindings()
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
    public void getDestinationServiceProviderTenantShouldThrowForMissingId()
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
            .hasMessage(
                "The provider tenant id is not defined in the service binding."
                    + " Please verify that the service binding contains the field 'tenantid' in the credentials list.");
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
