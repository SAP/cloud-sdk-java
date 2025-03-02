package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.MegacliteServiceBindingAccessor.CONNECTIVITY_BINDING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.sap.cloud.environment.servicebinding.api.DefaultServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.connectivity.ServiceBindingDestinationOptions.Options.ProxyOptions;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;
import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;
import com.sap.cloud.sdk.cloudplatform.tenant.exception.TenantAccessException;
import com.sap.cloud.sdk.cloudplatform.util.FacadeLocator;
import com.sap.cloud.sdk.testutil.TestContext;

import io.vavr.control.Try;

class MegacliteServiceBindingDestinationLoaderTest
{
    @Nonnull
    private static final MegacliteServiceBinding BINDING_PAAS =
        MegacliteServiceBinding
            .forService(ServiceIdentifier.DESTINATION)
            .providerConfiguration()
            .name("destination-paas")
            .version("version-paas")
            .megacliteVersion("megaclite-version-paas")
            .build();
    @Nonnull
    private static final MegacliteServiceBinding BINDING_SAAS =
        MegacliteServiceBinding
            .forService(ServiceIdentifier.DESTINATION)
            .subscriberConfiguration()
            .name("destination-saas")
            .version("version-saas")
            .megacliteVersion("megaclite-version-saas")
            .build();
    @Nonnull
    private static final MegacliteServiceBinding BINDING_PAAS_AND_SAAS =
        MegacliteServiceBinding
            .forService(ServiceIdentifier.DESTINATION)
            .providerConfiguration()
            .name("destination-paas")
            .version("version-paas")
            .megacliteVersion("megaclite-version-paas")
            .and()
            .subscriberConfiguration()
            .name("destination-saas")
            .version("version-saas")
            .megacliteVersion("megaclite-version-saas")
            .build();

    private static final URI megacliteUrl = URI.create("https://megaclite.com");
    private static final String providerTenantId = "provider-tenant-id";

    @RegisterExtension
    static TestContext context = TestContext.withThreadContext();

    private MegacliteServiceBindingDestinationLoader sut;

    @BeforeEach
    void setup()
    {
        final DwcConfiguration dwcConfig = new DwcConfiguration(megacliteUrl, providerTenantId);
        final MegacliteDestinationFactory destinationFactory = new MegacliteDestinationFactory(dwcConfig);

        sut = new MegacliteServiceBindingDestinationLoader();
        sut.setDwcConfig(dwcConfig);
        sut.setDestinationFactory(destinationFactory);
        sut.setConnectivityResolver(new MegacliteConnectivityProxyInformationResolver(destinationFactory));
    }

    @Test
    void testInstanceIsPickedUpByFacadeLocator()
    {
        final List<ServiceBindingDestinationLoader> loaders =
            FacadeLocator
                .getFacades(ServiceBindingDestinationLoader.class)
                .stream()
                .filter(MegacliteServiceBindingDestinationLoader.class::isInstance)
                .collect(Collectors.toList());
        assertThat(loaders).hasSize(1);
    }

    @Test
    void testSubscriberDestination()
    {
        context.setTenant(UUID.randomUUID().toString());

        final ServiceBindingDestinationOptions options =
            ServiceBindingDestinationOptions.forService(BINDING_PAAS_AND_SAAS).build();

        final HttpDestination result = sut.getDestination(options);

        assertThat(result.getUri())
            .isEqualTo(megacliteUrl.resolve("/megaclite-version-saas/destination-saas/version-saas/"));
        assertThat(result.getSecurityConfigurationStrategy()).isEqualTo(SecurityConfigurationStrategy.FROM_PLATFORM);
        assertThat(result.getProxyType()).contains(ProxyType.INTERNET);
        assertThat(DefaultHttpDestination.fromDestination(result).customHeaderProviders)
            .hasAtLeastOneElementOfType(DwcHeaderProvider.class);
    }

    @Test
    void testProviderDestination()
    {
        final ServiceBindingDestinationOptions options =
            ServiceBindingDestinationOptions
                .forService(BINDING_PAAS_AND_SAAS)
                .onBehalfOf(OnBehalfOf.TECHNICAL_USER_PROVIDER)
                .build();

        final HttpDestination result = sut.getDestination(options);

        assertThat(result.getUri())
            .isEqualTo(megacliteUrl.resolve("/megaclite-version-paas/destination-paas/version-paas/"));
        assertThat(result.getSecurityConfigurationStrategy()).isEqualTo(SecurityConfigurationStrategy.FROM_PLATFORM);
        assertThat(result.getProxyType()).contains(ProxyType.INTERNET);
        assertThat(DefaultHttpDestination.fromDestination(result).customHeaderProviders)
            .hasAtLeastOneElementOfType(DwcHeaderProvider.class);
    }

    @Test
    void testNamedUserBehalf()
    {
        context.setTenant(UUID.randomUUID().toString());

        final ServiceBindingDestinationOptions options =
            ServiceBindingDestinationOptions
                .forService(BINDING_PAAS_AND_SAAS)
                .onBehalfOf(OnBehalfOf.NAMED_USER_CURRENT_TENANT)
                .build();

        final HttpDestination result = sut.getDestination(options);

        assertThat(result.getUri())
            .describedAs("Named user should behave the same as current tenant")
            .isEqualTo(megacliteUrl.resolve("/megaclite-version-saas/destination-saas/version-saas/"));
    }

    @Test
    void testDefaultServiceBindingIsSkipped()
    {
        final ServiceBinding binding =
            DefaultServiceBinding
                .builder()
                .copy(Collections.emptyMap())
                .withServiceIdentifier(ServiceIdentifier.DESTINATION)
                .build();
        final ServiceBindingDestinationOptions options = ServiceBindingDestinationOptions.forService(binding).build();

        final Try<HttpDestination> result = sut.tryGetDestination(options);

        assertThatThrownBy(result::get).isExactlyInstanceOf(DestinationNotFoundException.class);
    }

    @Test
    void testMissingDwcConfig()
    {
        final ServiceBindingDestinationOptions options =
            ServiceBindingDestinationOptions
                .forService(BINDING_PAAS)
                .onBehalfOf(OnBehalfOf.TECHNICAL_USER_PROVIDER)
                .build();

        // create a loader without mocked DwC config
        sut = new MegacliteServiceBindingDestinationLoader();
        final Try<HttpDestination> result = sut.tryGetDestination(options);

        assertThatThrownBy(result::get)
            .isExactlyInstanceOf(DestinationAccessException.class)
            .hasCauseInstanceOf(CloudPlatformException.class);
    }

    @Test
    void testProviderMandateIsMissing()
    {
        final ServiceBindingDestinationOptions options =
            ServiceBindingDestinationOptions
                .forService(BINDING_SAAS)
                .onBehalfOf(OnBehalfOf.TECHNICAL_USER_PROVIDER)
                .build();

        final Try<HttpDestination> result = sut.tryGetDestination(options);

        assertThatThrownBy(result::get)
            .describedAs("A SaaS binding should not be accessible on behalf of the provider.")
            .isInstanceOf(DestinationAccessException.class);
    }

    @Test
    void testSubscriberMandateIsMissing()
    {
        // current tenant != provider
        context.setTenant(UUID.randomUUID().toString());

        // implicitly OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT
        final ServiceBindingDestinationOptions options =
            ServiceBindingDestinationOptions.forService(BINDING_PAAS).build();

        final Try<HttpDestination> result = sut.tryGetDestination(options);

        assertThatThrownBy(result::get)
            .describedAs("A PaaS binding should not be accessible on behalf of a subscriber tenant.")
            .isInstanceOf(DestinationAccessException.class);
    }

    @Test
    void testSubscriberWithoutCurrentTenant()
    {
        final ServiceBindingDestinationOptions options =
            ServiceBindingDestinationOptions.forService(BINDING_SAAS).build();

        final Try<HttpDestination> result = sut.tryGetDestination(options);

        assertThatThrownBy(result::get)
            .describedAs("A SaaS binding needs the current tenant to be present.")
            .isExactlyInstanceOf(DestinationAccessException.class)
            .hasCauseInstanceOf(TenantAccessException.class);
    }

    @Test
    void testCurrentTenantIsProvider()
    {
        context.setTenant(providerTenantId);

        final ServiceBindingDestinationOptions options =
            ServiceBindingDestinationOptions.forService(BINDING_PAAS_AND_SAAS).build();

        final HttpDestination result = sut.getDestination(options);

        assertThat(result.getUri())
            .isEqualTo(megacliteUrl.resolve("/megaclite-version-paas/destination-paas/version-paas/"));
    }

    @Test
    void testConnectivityInformationIsAdded()
    {
        final URI baseUrl = URI.create("baseUrl");
        final URI proxyUrl = URI.create("baseUrl");

        final MegacliteConnectivityProxyInformationResolver mock =
            mock(MegacliteConnectivityProxyInformationResolver.class);
        when(mock.getProxyUrl()).thenReturn(proxyUrl);
        when(mock.getAuthorizationToken()).thenReturn("proxy-auth");
        when(mock.getHeaders(any())).thenCallRealMethod();

        sut.setConnectivityResolver(mock);

        final DefaultHttpDestinationBuilderProxyHandler proxyHandler =
            spy(new DefaultHttpDestinationBuilderProxyHandler());
        when(proxyHandler.getServiceBindingDestinationLoader()).thenReturn(sut);
        when(proxyHandler.getServiceBindingAccessor()).thenReturn(() -> List.of(CONNECTIVITY_BINDING));

        final DefaultHttpDestination.Builder builder =
            DefaultHttpDestination
                .builder(baseUrl)
                .name("foo")
                .header(HttpHeaders.AUTHORIZATION, "some-auth")
                .headerProviders(( any ) -> Collections.singletonList(new Header("foo", "bar")))
                .property(DestinationProperty.SAP_LANGUAGE.getKeyName(), "en")
                .proxyType(ProxyType.ON_PREMISE);

        final DefaultHttpDestination result = proxyHandler.handle(builder);

        assertThat(result.getUri()).isEqualTo(baseUrl);
        assertThat(result.get(DestinationProperty.PROXY_URI).get()).isEqualTo(proxyUrl);
        assertThat(result.getHeaders())
            .containsExactlyInAnyOrder(
                new Header(HttpHeaders.AUTHORIZATION, "some-auth"),
                new Header(HttpHeaders.PROXY_AUTHORIZATION, "proxy-auth"),
                new Header("sap-language", "en"),
                new Header("foo", "bar"));
    }

    @Test
    void testMissingDestinationToBeProxied()
    {
        final ServiceBindingDestinationOptions options =
            ServiceBindingDestinationOptions.forService(CONNECTIVITY_BINDING).build();

        assertThatThrownBy(() -> sut.getDestination(options)).isInstanceOf(DestinationAccessException.class);
    }

    @Test
    void testConnectivityWithProviderTenant()
    {
        final ServiceBindingDestinationOptions options =
            ServiceBindingDestinationOptions
                .forService(CONNECTIVITY_BINDING)
                .onBehalfOf(OnBehalfOf.TECHNICAL_USER_PROVIDER)
                .withOption(ProxyOptions.destinationToBeProxied(mock(HttpDestination.class)))
                .build();

        assertThatThrownBy(() -> sut.getDestination(options)).isInstanceOf(DestinationAccessException.class);
    }

    @Test
    @DisplayName( "An already existing proxy configuration should not be overridden." )
    void testConnectivityWithExistingProxyConfig()
    {
        final DefaultHttpDestination destination =
            DefaultHttpDestination
                .builder("foo")
                .proxy(URI.create("http://bar"))
                .proxyType(ProxyType.ON_PREMISE)
                .buildInternal();

        final ServiceBindingDestinationOptions options =
            ServiceBindingDestinationOptions
                .forService(CONNECTIVITY_BINDING)
                .withOption(ProxyOptions.destinationToBeProxied(destination))
                .build();
        final HttpDestination result = sut.getDestination(options);

        assertThat(result.getProxyConfiguration()).isNotEmpty();
        assertThat(result.getProxyConfiguration().get().getUri()).isEqualTo(URI.create("http://bar"));
    }

    @Test
    void testGetMandateConfiguration()
    {
        // make sure this test covers all OnBehalfOf values
        assertThat(OnBehalfOf.values())
            .containsExactlyInAnyOrder(
                OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT,
                OnBehalfOf.TECHNICAL_USER_PROVIDER,
                OnBehalfOf.NAMED_USER_CURRENT_TENANT);

        testGetMandateConfiguration(OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT, false);
        testGetMandateConfiguration(OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT, true);

        testGetMandateConfiguration(OnBehalfOf.NAMED_USER_CURRENT_TENANT, false);
        testGetMandateConfiguration(OnBehalfOf.NAMED_USER_CURRENT_TENANT, true);

        testGetMandateConfiguration(OnBehalfOf.TECHNICAL_USER_PROVIDER, true);
    }

    private void testGetMandateConfiguration( @Nonnull final OnBehalfOf onBehalfOf, final boolean expectProvider )
    {
        final MegacliteServiceBinding.MandateConfiguration providerConfiguration =
            mock(MegacliteServiceBinding.MandateConfiguration.class);
        final MegacliteServiceBinding.MandateConfiguration subscriberConfiguration =
            mock(MegacliteServiceBinding.MandateConfiguration.class);

        final MegacliteServiceBinding binding = mock(MegacliteServiceBinding.class);
        when(binding.getProviderConfiguration()).thenReturn(providerConfiguration);
        when(binding.getSubscriberConfiguration()).thenReturn(subscriberConfiguration);

        if( expectProvider ) {
            context.setTenant(providerTenantId);
        } else {
            context.setTenant(UUID.randomUUID().toString());
        }

        final MegacliteServiceBinding.MandateConfiguration result =
            sut.getMandateConfigurationOrThrow(binding, onBehalfOf);

        if( expectProvider ) {
            assertThat(result).isSameAs(providerConfiguration);
        } else {
            assertThat(result).isSameAs(subscriberConfiguration);
        }

        verify(binding, times(expectProvider ? 1 : 0)).getProviderConfiguration();
        verify(binding, times(expectProvider ? 0 : 1)).getSubscriberConfiguration();
    }
}
