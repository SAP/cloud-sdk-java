package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.environment.servicebinding.api.ServiceIdentifier.IDENTITY_AUTHENTICATION;
import static com.sap.cloud.sdk.cloudplatform.connectivity.BtpServiceOptions.IasOptions.IasCommunicationOptions;
import static com.sap.cloud.sdk.cloudplatform.connectivity.BtpServiceOptions.IasOptions.IasTargetUri;
import static com.sap.cloud.sdk.cloudplatform.connectivity.BtpServiceOptions.IasOptions.NoTokenForTechnicalProviderUser;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ServiceBindingTestUtility.bindingWithCredentials;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.MapEntry.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import com.sap.cloud.environment.servicebinding.api.DefaultServiceBinding;
import com.sap.cloud.environment.servicebinding.api.DefaultServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;

import io.vavr.control.Try;

@Isolated( "because the test manipulates the global default ServiceBindingAccessor" )
class IdentityAuthenticationServiceBindingDestinationLoaderTest
{
    private static final ServiceIdentifier SERVICE_IDENTIFIER = ServiceIdentifier.of("test-service");
    private static final DefaultServiceBinding EMPTY_IDENTITY_BINDING =
        DefaultServiceBinding.builder().copy(Map.of()).withServiceIdentifier(IDENTITY_AUTHENTICATION).build();

    @BeforeEach
    void mockIasBinding()
    {
        DefaultServiceBindingAccessor.setInstance(() -> List.of(EMPTY_IDENTITY_BINDING));
    }

    @AfterEach
    void resetServiceBindingAccessor()
    {
        DefaultServiceBindingAccessor.setInstance(null);
    }

    @Test
    void testChainPicksUpIdentityLoader()
    {
        // sanity check
        final ServiceBindingDestinationLoader chain = ServiceBindingDestinationLoader.defaultLoaderChain();
        assertThat(chain).isInstanceOf(DefaultServiceBindingDestinationLoaderChain.class);

        final List<ServiceBindingDestinationLoader> loaders =
            ((DefaultServiceBindingDestinationLoaderChain) chain).getDelegateLoaders();
        assertThat(loaders)
            .describedAs("Expect the IAS loader to be present")
            .hasAtLeastOneElementOfType(IdentityAuthenticationServiceBindingDestinationLoader.class);

        final IdentityAuthenticationServiceBindingDestinationLoader loader =
            loaders
                .stream()
                .filter(IdentityAuthenticationServiceBindingDestinationLoader.class::isInstance)
                .map(IdentityAuthenticationServiceBindingDestinationLoader.class::cast)
                .findAny()
                .get();
        assertThat(loader.getDelegateLoader())
            .describedAs("The IAS loader should itself reference the chain")
            .isSameAs(chain);
    }

    @Test
    void testMinimalServiceBinding()
    {
        final ServiceBinding binding = enhanceMinimalBinding();

        final ServiceBindingDestinationLoader delegate = mockDelegateLoader(delegateOptions -> {
            assertThat(delegateOptions.getServiceBinding()).isSameAs(EMPTY_IDENTITY_BINDING);
            assertThat(delegateOptions.getOnBehalfOf()).isEqualTo(OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
            assertThat(delegateOptions.getOption(IasTargetUri.class)).containsExactly(URI.create("https://foo.uri"));
            assertThat(delegateOptions.getOption(NoTokenForTechnicalProviderUser.class)).isEmpty();
            assertThat(delegateOptions.getOption(IasCommunicationOptions.class)).isEmpty();
        });

        final IdentityAuthenticationServiceBindingDestinationLoader sut =
            new IdentityAuthenticationServiceBindingDestinationLoader(delegate);

        final ServiceBindingDestinationOptions options = ServiceBindingDestinationOptions.forService(binding).build();
        sut.tryGetDestination(options);

        verify(delegate).tryGetDestination(any());
    }

    @Test
    void testAlwaysRequiresTokenIsSetToFalse()
    {
        final ServiceBinding binding = enhanceMinimalBinding(entry("endpoints.foo.always-requires-token", false));

        final ServiceBindingDestinationLoader delegate =
            mockDelegateLoader(
                delegateOptions -> assertThat(delegateOptions.getOption(NoTokenForTechnicalProviderUser.class))
                    .contains(true));

        final IdentityAuthenticationServiceBindingDestinationLoader sut =
            new IdentityAuthenticationServiceBindingDestinationLoader(delegate);

        final ServiceBindingDestinationOptions options = ServiceBindingDestinationOptions.forService(binding).build();
        sut.tryGetDestination(options);

        verify(delegate).tryGetDestination(any());
    }

    @Test
    void testAlwaysRequiresTokenIsSetToTrue()
    {
        final ServiceBinding binding = enhanceMinimalBinding(entry("endpoints.foo.always-requires-token", true));

        final ServiceBindingDestinationLoader delegate =
            mockDelegateLoader(
                delegateOptions -> assertThat(delegateOptions.getOption(NoTokenForTechnicalProviderUser.class))
                    .isEmpty());

        final IdentityAuthenticationServiceBindingDestinationLoader sut =
            new IdentityAuthenticationServiceBindingDestinationLoader(delegate);

        final ServiceBindingDestinationOptions options = ServiceBindingDestinationOptions.forService(binding).build();
        sut.tryGetDestination(options);

        verify(delegate).tryGetDestination(any());
    }

    @Test
    void testOnBehalfIsForwarded()
    {
        final ServiceBinding binding = enhanceMinimalBinding();

        final ServiceBindingDestinationLoader delegate =
            mockDelegateLoader(
                delegateOptions -> assertThat(delegateOptions.getOnBehalfOf())
                    .isEqualTo(OnBehalfOf.NAMED_USER_CURRENT_TENANT));

        final IdentityAuthenticationServiceBindingDestinationLoader sut =
            new IdentityAuthenticationServiceBindingDestinationLoader(delegate);

        final ServiceBindingDestinationOptions options =
            ServiceBindingDestinationOptions
                .forService(binding)
                .onBehalfOf(OnBehalfOf.NAMED_USER_CURRENT_TENANT)
                .build();
        sut.tryGetDestination(options);

        verify(delegate).tryGetDestination(any());
    }

    @Test
    void testMultiEndpoints()
    {
        final ServiceBinding binding = enhanceMinimalBinding(entry("endpoints.bar.uri", "https://bar.uri"));

        final ServiceBindingDestinationLoader delegate = mock(ServiceBindingDestinationLoader.class);

        final IdentityAuthenticationServiceBindingDestinationLoader sut =
            new IdentityAuthenticationServiceBindingDestinationLoader(delegate);

        final ServiceBindingDestinationOptions options = ServiceBindingDestinationOptions.forService(binding).build();
        final Try<HttpDestination> result = sut.tryGetDestination(options);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getCause()).isExactlyInstanceOf(DestinationAccessException.class);

        verify(delegate, never()).tryGetDestination(any());
    }

    @Test
    void testNoAuthenticationService()
    {
        final ServiceBinding binding =
            bindingWithCredentials(SERVICE_IDENTIFIER, entry("clientid", "foo"), entry("clientsecret", "bar"));

        final ServiceBindingDestinationLoader delegate = mock(ServiceBindingDestinationLoader.class);

        final IdentityAuthenticationServiceBindingDestinationLoader sut =
            new IdentityAuthenticationServiceBindingDestinationLoader(delegate);

        final ServiceBindingDestinationOptions options = ServiceBindingDestinationOptions.forService(binding).build();
        final Try<HttpDestination> result = sut.tryGetDestination(options);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getCause()).isExactlyInstanceOf(DestinationNotFoundException.class);

        verify(delegate, never()).tryGetDestination(any());
    }

    @Test
    void testNoEndpoints()
    {
        final ServiceBinding binding =
            bindingWithCredentials(SERVICE_IDENTIFIER, entry("authentication-service.service-label", "identity"));

        final ServiceBindingDestinationOptions options =
            ServiceBindingDestinationOptions
                .forService(binding)
                .onBehalfOf(OnBehalfOf.NAMED_USER_CURRENT_TENANT)
                .build();

        final ServiceBindingDestinationLoader delegate = mock(ServiceBindingDestinationLoader.class);
        final IdentityAuthenticationServiceBindingDestinationLoader sut =
            new IdentityAuthenticationServiceBindingDestinationLoader(delegate);

        final Try<HttpDestination> result = sut.tryGetDestination(options);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getCause()).isExactlyInstanceOf(DestinationAccessException.class);

        verify(delegate, never()).tryGetDestination(any());
    }

    @Test
    void testNoIasBindingPresent()
    {
        // clear the IAS binding that is mocked by default for this test
        DefaultServiceBindingAccessor.setInstance(null);

        final ServiceBinding binding = enhanceMinimalBinding();
        final ServiceBindingDestinationOptions opts = ServiceBindingDestinationOptions.forService(binding).build();

        final IdentityAuthenticationServiceBindingDestinationLoader sut =
            new IdentityAuthenticationServiceBindingDestinationLoader();

        assertThatThrownBy(() -> sut.getDestination(opts))
            .isInstanceOf(DestinationAccessException.class)
            .hasMessageContaining(
                "Failed to create a destination for service '%s' using IAS OAuth credentials",
                SERVICE_IDENTIFIER.toString())
            .hasCauseInstanceOf(DestinationAccessException.class)
            .hasRootCauseMessage("Could not find any matching service bindings for service identifier 'identity'");
    }

    @Test
    void testIasDestinationLoadingFails()
    {
        // the mocked IAS binding is empty, so the delegateLoader within 'sut' should return a DestinationNotFoundException
        final ServiceBinding binding = enhanceMinimalBinding();
        final ServiceBindingDestinationOptions opts = ServiceBindingDestinationOptions.forService(binding).build();

        final IdentityAuthenticationServiceBindingDestinationLoader sut =
            new IdentityAuthenticationServiceBindingDestinationLoader();

        assertThatThrownBy(() -> sut.getDestination(opts))
            .isInstanceOf(DestinationAccessException.class)
            .hasMessageContaining(
                "Failed to create a destination for service '%s' using IAS OAuth credentials",
                SERVICE_IDENTIFIER.toString())
            .hasCauseInstanceOf(DestinationNotFoundException.class);
    }

    @Test
    void testIasDestinationLoadingSucceeds()
    {
        final DefaultServiceBinding iasBinding =
            DefaultServiceBinding
                .builder()
                .copy(Map.of())
                .withServiceIdentifier(IDENTITY_AUTHENTICATION)
                .withCredentials(
                    Map.of("clientid", "foo", "clientsecret", "bar", "url", "https://foo.com", "app_tid", "provider"))
                .build();
        DefaultServiceBindingAccessor.setInstance(() -> List.of(iasBinding));

        final ServiceBinding binding = enhanceMinimalBinding();
        final ServiceBindingDestinationOptions opts = ServiceBindingDestinationOptions.forService(binding).build();

        final IdentityAuthenticationServiceBindingDestinationLoader sut =
            new IdentityAuthenticationServiceBindingDestinationLoader();

        assertThatCode(() -> sut.getDestination(opts)).doesNotThrowAnyException();
    }

    @SuppressWarnings( { "unchecked", "varargs" } )
    @Nonnull
    @SafeVarargs
    private static ServiceBinding enhanceMinimalBinding( @Nonnull final Map.Entry<String, Object>... additionalEntries )
    {
        final Collection<Map.Entry<String, Object>> allEntries = new ArrayList<>();
        allEntries.add(entry("authentication-service.service-label", "identity"));
        allEntries.add(entry("endpoints.foo.uri", "https://foo.uri"));
        allEntries.addAll(Arrays.stream(additionalEntries).toList());

        return bindingWithCredentials(SERVICE_IDENTIFIER, allEntries.toArray(Map.Entry[]::new));
    }

    @Nonnull
    private static ServiceBindingDestinationLoader mockDelegateLoader(
        @Nonnull final Consumer<ServiceBindingDestinationOptions> assertions )
    {
        final ServiceBindingDestinationLoader delegate = mock(ServiceBindingDestinationLoader.class);
        doAnswer(invocation -> {
            assertions.accept(invocation.getArgument(0, ServiceBindingDestinationOptions.class));
            return Try.failure(new DestinationAccessException("test exception"));
        }).when(delegate).tryGetDestination(any());
        return delegate;
    }
}
