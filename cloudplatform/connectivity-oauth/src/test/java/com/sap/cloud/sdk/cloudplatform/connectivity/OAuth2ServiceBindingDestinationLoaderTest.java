package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.OAuth2ServiceBindingDestinationLoader.DEFAULT_SERVICE_RESOLVERS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Predicate;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableMap;
import com.sap.cloud.environment.servicebinding.api.DefaultServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.security.config.ClientCertificate;
import com.sap.cloud.security.config.ClientCredentials;
import com.sap.cloud.security.config.ClientIdentity;

import io.vavr.control.Try;

class OAuth2ServiceBindingDestinationLoaderTest
{
    private static final URI baseUrl = URI.create("baseUrl");
    private static final URI tokenUrl = URI.create("tokenUrl");
    public static final ClientIdentity credentials = new ClientCredentials("id", "sec");

    private static final ServiceIdentifier TEST_SERVICE = ServiceIdentifier.of("TEST_SERVICE_IDENTIFIER");
    private static final ServiceBinding EMPTY_BINDING =
        DefaultServiceBinding.builder().copy(Collections.emptyMap()).withServiceIdentifier(TEST_SERVICE).build();
    private static final ServiceBindingDestinationOptions OPTIONS_WITH_EMPTY_BINDING =
        ServiceBindingDestinationOptions.forService(EMPTY_BINDING).build();
    private OAuth2ServiceBindingDestinationLoader sut;

    @BeforeEach
    void initializeSubjectUnderTest()
    {
        sut = new OAuth2ServiceBindingDestinationLoader();
    }

    @AfterEach
    void resetSubjectUnderTest()
    {
        OAuth2ServiceBindingDestinationLoader.resetPropertySuppliers();
    }

    @Test
    void testClassIsPickedUpByServiceLoaderPattern()
    {
        final ServiceLoader<ServiceBindingDestinationLoader> load =
            ServiceLoader.load(ServiceBindingDestinationLoader.class, getClass().getClassLoader());

        assertThat(load).filteredOn(OAuth2ServiceBindingDestinationLoader.class::isInstance).hasSize(1);
    }

    @Test
    void testKnownMappingsAreRegistered()
    {
        assertThat(DEFAULT_SERVICE_RESOLVERS)
            .containsExactlyElementsOf(BtpServicePropertySuppliers.getDefaultServiceResolvers());
    }

    @Test
    void testCustomServiceCanBeRegistered()
    {
        OAuth2ServiceBindingDestinationLoader
            .registerPropertySupplier(
                option -> TEST_SERVICE.equals(option.getServiceBinding().getServiceIdentifier().orElse(null)),
                DefaultOAuth2PropertySupplier::new);

        assertThat(DEFAULT_SERVICE_RESOLVERS).anyMatch(f -> f.matches(OPTIONS_WITH_EMPTY_BINDING));
    }

    @Test
    void testOptionsMatcher()
    {
        OAuth2ServiceBindingDestinationLoader
            .registerPropertySupplier(
                options -> options.getServiceBinding().getTags().contains("test"),
                DefaultOAuth2PropertySupplier::new);

        final ServiceBinding binding =
            DefaultServiceBinding
                .builder()
                .copy(Collections.emptyMap())
                .withServiceIdentifier(TEST_SERVICE)
                .withTags(Arrays.asList("test"))
                .build();
        final ServiceBindingDestinationOptions options = ServiceBindingDestinationOptions.forService(binding).build();

        assertThat(DEFAULT_SERVICE_RESOLVERS).anyMatch(f -> f.matches(options));
    }

    @SuppressWarnings( "unchecked" )
    @Test
    void testOptionsMatchOrder()
    {
        final OAuth2PropertySupplier supplier = mock(OAuth2PropertySupplier.class);
        final Predicate<ServiceBindingDestinationOptions> matcher1 = mock(Predicate.class);
        final Predicate<ServiceBindingDestinationOptions> matcher2 = mock(Predicate.class);
        final Predicate<ServiceBindingDestinationOptions> matcher3 = mock(Predicate.class);

        final OAuth2PropertySupplierResolver firstNativeResolver = DEFAULT_SERVICE_RESOLVERS.get(0);
        OAuth2ServiceBindingDestinationLoader.registerPropertySupplier(matcher1, opts -> supplier);
        OAuth2ServiceBindingDestinationLoader.registerPropertySupplier(matcher2, opts -> supplier);
        OAuth2ServiceBindingDestinationLoader.registerPropertySupplier(matcher3, opts -> supplier);

        // new resolvers are prepended to internal list
        assertThat(DEFAULT_SERVICE_RESOLVERS.get(3)).isSameAs(firstNativeResolver);

        final OAuth2ServiceBindingDestinationLoader sut = new OAuth2ServiceBindingDestinationLoader();
        assertThat(sut.tryGetDestination(OPTIONS_WITH_EMPTY_BINDING)).isEmpty();

        final InOrder inOrder = inOrder(matcher1, matcher2, matcher3);
        inOrder.verify(matcher3, times(1)).test(OPTIONS_WITH_EMPTY_BINDING);
        inOrder.verify(matcher2, times(1)).test(OPTIONS_WITH_EMPTY_BINDING);
        inOrder.verify(matcher1, times(1)).test(OPTIONS_WITH_EMPTY_BINDING);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void testUnknownService()
    {
        final Try<HttpDestination> result = sut.tryGetDestination(OPTIONS_WITH_EMPTY_BINDING);
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getCause()).isExactlyInstanceOf(DestinationNotFoundException.class);
    }

    @Test
    void testMissingClientId()
    {
        final OAuth2PropertySupplier mock = new DefaultOAuth2PropertySupplier(OPTIONS_WITH_EMPTY_BINDING);

        sut = mockLoader(mock);

        final Try<HttpDestination> result = sut.tryGetDestination(OPTIONS_WITH_EMPTY_BINDING);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getCause()).isExactlyInstanceOf(DestinationNotFoundException.class);
    }

    @Test
    void testMissingRequiredProperties()
    {
        final OAuth2PropertySupplier mock = spy(new DefaultOAuth2PropertySupplier(OPTIONS_WITH_EMPTY_BINDING));
        when(mock.isOAuth2Binding()).thenReturn(true);

        sut = mockLoader(mock);

        final Try<HttpDestination> result = sut.tryGetDestination(OPTIONS_WITH_EMPTY_BINDING);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getCause()).isExactlyInstanceOf(DestinationAccessException.class);
    }

    @Test
    void testClientSecretBasedBinding()
    {
        final ClientCredentials credentials = new ClientCredentials("id", "secret");

        final OAuth2PropertySupplier mock = mock(OAuth2PropertySupplier.class);
        when(mock.isOAuth2Binding()).thenReturn(true);
        when(mock.getServiceUri()).thenReturn(baseUrl);
        when(mock.getTokenUri()).thenReturn(tokenUrl);
        when(mock.getClientIdentity()).thenReturn(credentials);

        sut = mockLoader(mock);

        final Try<HttpDestination> result = sut.tryGetDestination(OPTIONS_WITH_EMPTY_BINDING);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.get().getUri()).isEqualTo(baseUrl);

        verify(sut, times(1))
            .toDestination(
                eq(baseUrl),
                eq(tokenUrl),
                eq(credentials),
                eq(OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT),
                eq(TEST_SERVICE));
    }

    @Test
    void testInvalidCertificate()
    {
        final ClientCertificate certificate = new ClientCertificate("invalid cert", "invalid key", "id");

        final OAuth2PropertySupplier mock = mock(OAuth2PropertySupplier.class);
        when(mock.isOAuth2Binding()).thenReturn(true);
        when(mock.getServiceUri()).thenReturn(baseUrl);
        when(mock.getTokenUri()).thenReturn(tokenUrl);
        when(mock.getClientIdentity()).thenReturn(certificate);

        sut = mockLoader(mock);

        final Try<Collection<Header>> result =
            sut.tryGetDestination(OPTIONS_WITH_EMPTY_BINDING).map(HttpDestinationProperties::getHeaders);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getCause()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testErrorHandling()
    {
        final ImmutableMap<String, Object> bindingCredentials =
            ImmutableMap
                .of("clientid", "CLIENT_ID", "url", "URL", "tokenurl", "TOKEN_URL", "clientsecret", "CLIENT_SECRET");
        final ServiceBinding binding =
            DefaultServiceBinding
                .builder()
                .copy(Collections.emptyMap())
                .withServiceIdentifier(TEST_SERVICE)
                .withCredentials(bindingCredentials)
                .build();

        final ServiceBindingDestinationOptions opts = ServiceBindingDestinationOptions.forService(binding).build();
        final List<OAuth2PropertySupplierResolver> resolvers = new ArrayList<>();
        final OAuth2ServiceBindingDestinationLoader sut = new OAuth2ServiceBindingDestinationLoader(resolvers);

        // empty list of supplier-resolvers
        {
            assertThat(sut.tryGetDestination(opts).getCause()).isInstanceOf(DestinationNotFoundException.class);
        }

        // add supplier-resolver that throws an exception on "match", handled gracefully
        {
            final OAuth2PropertySupplierResolver resolver = mock(OAuth2PropertySupplierResolver.class);
            when(resolver.matches(opts)).thenThrow(new IllegalStateException());
            resolvers.add(resolver);

            assertThat(sut.tryGetDestination(opts).getCause()).isInstanceOf(DestinationNotFoundException.class);

            verify(resolvers.get(0), times(1)).matches(opts); // this resolver
            resolvers.forEach(Mockito::clearInvocations);
        }

        // add supplier-resolver that throws an exception on "resolve", handled gracefully
        {
            final OAuth2PropertySupplierResolver resolver = mock(OAuth2PropertySupplierResolver.class);
            when(resolver.matches(opts)).thenReturn(true);
            when(resolver.resolve(opts)).thenThrow(new IllegalStateException());
            resolvers.add(resolver);

            assertThat(sut.tryGetDestination(opts).getCause()).isInstanceOf(DestinationNotFoundException.class);

            verify(resolvers.get(0), times(1)).matches(opts); // previous resolver
            verify(resolvers.get(1), times(1)).matches(opts); // this resolver
            verify(resolvers.get(1), times(1)).resolve(opts);
            resolvers.forEach(Mockito::clearInvocations);
        }

        // add supplier-resolver that throws an exception on "isOAuth2Binding", handled gracefully
        {
            final OAuth2PropertySupplier supplier = mock(OAuth2PropertySupplier.class);
            when(supplier.isOAuth2Binding()).thenThrow(new IllegalStateException());
            final OAuth2PropertySupplierResolver resolver = mock(OAuth2PropertySupplierResolver.class);
            when(resolver.matches(opts)).thenReturn(true);
            when(resolver.resolve(opts)).thenReturn(supplier);
            resolvers.add(resolver);

            assertThat(sut.tryGetDestination(opts).getCause()).isInstanceOf(DestinationNotFoundException.class);

            verify(resolvers.get(0), times(1)).matches(opts); // previous resolvers
            verify(resolvers.get(1), times(1)).matches(opts);
            verify(resolvers.get(2), times(1)).matches(opts); // this resolver
            verify(resolvers.get(2), times(1)).resolve(opts);
            verify(supplier, times(1)).isOAuth2Binding();
            resolvers.forEach(Mockito::clearInvocations);
        }

        // add healthy supplier-resolver as last element of chain
        {
            final OAuth2PropertySupplier supplier = new DefaultOAuth2PropertySupplier(opts, Collections.emptyList());
            final OAuth2PropertySupplierResolver resolver =
                spy(new OAuth2PropertySupplierResolver(o -> true, o -> supplier));
            resolvers.add(resolver);

            assertThatNoException().isThrownBy(() -> sut.getDestination(opts)); // success

            verify(resolvers.get(0), times(1)).matches(opts); // previous resolvers
            verify(resolvers.get(1), times(1)).matches(opts);
            verify(resolvers.get(2), times(1)).matches(opts);
            verify(resolvers.get(3), times(1)).matches(opts); // this resolver
            verify(resolvers.get(3), times(1)).resolve(opts);
            resolvers.forEach(Mockito::clearInvocations);
        }

        // add another healthy supplier-resolver, that is not being called
        {
            final OAuth2PropertySupplier supplier = new DefaultOAuth2PropertySupplier(opts, Collections.emptyList());
            final OAuth2PropertySupplierResolver resolver =
                spy(new OAuth2PropertySupplierResolver(o -> true, o -> supplier));
            resolvers.add(resolver);

            assertThatNoException().isThrownBy(() -> sut.getDestination(opts)); // success

            verify(resolvers.get(0), times(1)).matches(opts); // previous resolvers
            verify(resolvers.get(1), times(1)).matches(opts);
            verify(resolvers.get(2), times(1)).matches(opts);
            verify(resolvers.get(3), times(1)).matches(opts);
            verify(resolvers.get(4), times(0)).matches(opts); // this resolver
            resolvers.forEach(Mockito::clearInvocations);
        }
    }

    @Test
    void testProxiedDestination()
    {
        final DefaultHttpDestination baseDestination =
            DefaultHttpDestination
                .builder(baseUrl)
                .name("foo")
                .header(HttpHeaders.AUTHORIZATION, "some-auth")
                .headerProviders(( any ) -> Collections.singletonList(new Header("foo", "bar")))
                .property(DestinationProperty.SAP_LANGUAGE, "en")
                .proxyType(ProxyType.ON_PREMISE)
                .build();

        final Header expectedProxyHeader = new Header(HttpHeaders.PROXY_AUTHORIZATION, "proxy-auth");

        final DestinationHeaderProvider headerProviderMock = mock(DestinationHeaderProvider.class);
        when(headerProviderMock.getHeaders(any())).thenReturn(Collections.singletonList(expectedProxyHeader));

        sut = spy(new OAuth2ServiceBindingDestinationLoader());
        doReturn(headerProviderMock).when(sut).createHeaderProvider(any(), any(), any(), any());

        final HttpDestination result =
            sut
                .toProxiedDestination(
                    baseDestination,
                    URI.create("proxyUrl"),
                    tokenUrl,
                    credentials,
                    OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);

        assertThat(result.getUri()).isEqualTo(baseUrl);
        assertThat(result.getHeaders(baseUrl))
            .containsExactlyInAnyOrder(
                new Header(HttpHeaders.AUTHORIZATION, "some-auth"),
                expectedProxyHeader,
                new Header("foo", "bar"),
                new Header("sap-language", "en"));

        verify(sut, times(1))
            .createHeaderProvider(
                eq(tokenUrl),
                eq(credentials),
                eq(OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT),
                eq(HttpHeaders.PROXY_AUTHORIZATION));
    }

    @Test
    void testResilienceIsAdded()
    {
        final DefaultHttpDestination baseDestination = DefaultHttpDestination.builder(baseUrl).name("foo").build();

        sut = new OAuth2ServiceBindingDestinationLoader();

        HttpDestination result =
            sut.toDestination(baseUrl, tokenUrl, credentials, OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT, TEST_SERVICE);

        assertThat(result.get(OAuth2HeaderProvider.PROPERTY_OAUTH2_RESILIENCE_CONFIG)).isNotEmpty();
        ResilienceConfiguration config =
            (ResilienceConfiguration) result.get(OAuth2HeaderProvider.PROPERTY_OAUTH2_RESILIENCE_CONFIG).get();
        assertThat(config.identifier()).startsWith(TEST_SERVICE.toString());

        result =
            sut
                .toProxiedDestination(
                    baseDestination,
                    baseUrl,
                    tokenUrl,
                    credentials,
                    OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);

        assertThat(result.get(OAuth2HeaderProvider.PROPERTY_OAUTH2_RESILIENCE_CONFIG)).isNotEmpty();
        config = (ResilienceConfiguration) result.get(OAuth2HeaderProvider.PROPERTY_OAUTH2_RESILIENCE_CONFIG).get();
        assertThat(config.identifier()).startsWith(baseDestination.get(DestinationProperty.NAME).get());
    }

    @Test
    void testExceptionInOAuth2PropertySupplierIsHandledCorrectly()
    {
        final ImmutableMap<String, Object> bindingCredentials =
            ImmutableMap
                .of("clientid", "CLIENT_ID", "url", "URL", "tokenurl", "TOKEN_URL", "clientsecret", "CLIENT_SECRET");
        final ServiceBinding binding =
            DefaultServiceBinding
                .builder()
                .copy(Collections.emptyMap())
                .withServiceIdentifier(TEST_SERVICE)
                .withCredentials(bindingCredentials)
                .build();

        final ServiceBindingDestinationOptions opts = ServiceBindingDestinationOptions.forService(binding).build();

        final OAuth2PropertySupplier supplier = mock(OAuth2PropertySupplier.class);
        when(supplier.isOAuth2Binding()).thenReturn(true);
        when(supplier.getServiceUri()).thenThrow(new IllegalStateException());

        final OAuth2PropertySupplierResolver resolver = mock(OAuth2PropertySupplierResolver.class);
        when(resolver.matches(opts)).thenReturn(true);
        when(resolver.resolve(opts)).thenReturn(supplier);

        final List<OAuth2PropertySupplierResolver> resolvers = Collections.singletonList(resolver);
        final OAuth2ServiceBindingDestinationLoader sut = new OAuth2ServiceBindingDestinationLoader(resolvers);

        assertThat(sut.tryGetDestination(opts).getCause()).isInstanceOf(DestinationAccessException.class);
    }

    private static OAuth2ServiceBindingDestinationLoader mockLoader( final OAuth2PropertySupplier s )
    {
        final OAuth2PropertySupplierResolver resolver =
            OAuth2PropertySupplierResolver
                .forServiceIdentifier(OAuth2ServiceBindingDestinationLoaderTest.TEST_SERVICE, any -> s);

        final List<OAuth2PropertySupplierResolver> list = Collections.singletonList(resolver);

        return spy(new OAuth2ServiceBindingDestinationLoader(list));
    }
}
