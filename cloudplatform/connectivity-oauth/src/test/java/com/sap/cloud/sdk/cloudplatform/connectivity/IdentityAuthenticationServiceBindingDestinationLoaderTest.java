package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.ServiceBindingTestUtility.bindingWithCredentials;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ServiceBindingTestUtility.nestedMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import com.sap.cloud.environment.servicebinding.api.DefaultServiceBinding;
import com.sap.cloud.environment.servicebinding.api.DefaultServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;

import io.vavr.control.Try;

@Isolated( "because the tests manipulate the global default ServiceBindingAccessor" )
class IdentityAuthenticationServiceBindingDestinationLoaderTest
{
    private static final ServiceIdentifier SERVICE_IDENTIFIER = ServiceIdentifier.of("test-service");
    private static final ServiceIdentifier IDENTITY_AUTHENTICATION = ServiceIdentifier.of("identity");
    private static final DefaultServiceBinding IDENTITY_BINDING =
        DefaultServiceBinding.builder().copy(Map.of()).withServiceIdentifier(IDENTITY_AUTHENTICATION).build();

    @BeforeAll
    static void mockIasBinding()
    {
        DefaultServiceBindingAccessor.setInstance(() -> List.of(IDENTITY_BINDING));
    }

    @AfterAll
    static void resetServiceBindingAccessor()
    {
        DefaultServiceBindingAccessor.setInstance(null);
    }

    @Test
    void testMinimalServiceBinding()
    {
        final DefaultServiceBinding binding =
            DefaultServiceBinding
                .builder()
                .copy(nestedMap(entry("endpoints.foo.uri", "https://foo.uri")))
                .withServiceIdentifier(SERVICE_IDENTIFIER)
                .withCredentials(nestedMap(entry("authentication-service", "identity")))
                .build();

        final ServiceBindingDestinationOptions options = ServiceBindingDestinationOptions.forService(binding).build();

        final ServiceBindingDestinationLoader delegate = mockDelegateLoader(delegateOptions -> {
            assertThat(delegateOptions.getServiceBinding()).isSameAs(IDENTITY_BINDING);
            assertThat(delegateOptions.getOnBehalfOf()).isEqualTo(OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
            assertThat(delegateOptions.getOption(BtpServiceOptions.IasOptions.IasTargetUri.class))
                .containsExactly(URI.create("https://foo.uri"));
            assertThat(delegateOptions.getOption(BtpServiceOptions.IasOptions.IasCommunicationOptions.class)).isEmpty();
        });

        final IdentityAuthenticationServiceBindingDestinationLoader sut =
            new IdentityAuthenticationServiceBindingDestinationLoader(delegate);

        sut.tryGetDestination(options);

        verify(delegate).tryGetDestination(any());
    }

    @Test
    void testServiceBindingWithMTLSOnly()
    {
        final DefaultServiceBinding binding =
            DefaultServiceBinding
                .builder()
                .copy(
                    nestedMap(
                        entry("endpoints.foo.uri", "https://foo.uri"),
                        entry("endpoints.foo.requires-token", false)))
                .withServiceIdentifier(SERVICE_IDENTIFIER)
                .withCredentials(nestedMap(entry("authentication-service", "identity")))
                .build();

        final ServiceBindingDestinationOptions options = ServiceBindingDestinationOptions.forService(binding).build();

        final ServiceBindingDestinationLoader delegate = mockDelegateLoader(delegateOptions -> {
            assertThat(delegateOptions.getServiceBinding()).isSameAs(IDENTITY_BINDING);
            assertThat(delegateOptions.getOnBehalfOf()).isEqualTo(OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
            assertThat(delegateOptions.getOption(BtpServiceOptions.IasOptions.IasTargetUri.class))
                .containsExactly(URI.create("https://foo.uri"));
            assertThat(delegateOptions.getOption(BtpServiceOptions.IasOptions.IasCommunicationOptions.class).get())
                .isEqualTo(BtpServiceOptions.IasOptions.withMutualTlsOnly());
        });

        final IdentityAuthenticationServiceBindingDestinationLoader sut =
            new IdentityAuthenticationServiceBindingDestinationLoader(delegate);

        sut.tryGetDestination(options);

        verify(delegate).tryGetDestination(any());
    }

    @Test
    void testOnBehalfIsForwarded()
    {
        final DefaultServiceBinding binding =
            DefaultServiceBinding
                .builder()
                .copy(nestedMap(entry("endpoints.foo.uri", "https://foo.uri")))
                .withServiceIdentifier(SERVICE_IDENTIFIER)
                .withCredentials(nestedMap(entry("authentication-service", "identity")))
                .build();

        final ServiceBindingDestinationOptions options =
            ServiceBindingDestinationOptions
                .forService(binding)
                .onBehalfOf(OnBehalfOf.NAMED_USER_CURRENT_TENANT)
                .build();

        final ServiceBindingDestinationLoader delegate = mockDelegateLoader(delegateOptions -> {
            assertThat(delegateOptions.getServiceBinding()).isSameAs(IDENTITY_BINDING);
            assertThat(delegateOptions.getOnBehalfOf()).isEqualTo(OnBehalfOf.NAMED_USER_CURRENT_TENANT);
            assertThat(delegateOptions.getOption(BtpServiceOptions.IasOptions.IasTargetUri.class))
                .containsExactly(URI.create("https://foo.uri"));
            assertThat(delegateOptions.getOption(BtpServiceOptions.IasOptions.IasCommunicationOptions.class)).isEmpty();
        });

        final IdentityAuthenticationServiceBindingDestinationLoader sut =
            new IdentityAuthenticationServiceBindingDestinationLoader(delegate);

        sut.tryGetDestination(options);

        verify(delegate).tryGetDestination(any());
    }

    @Test
    void testServiceBindingWithoutAuthenticationService()
    {
        final ServiceBinding binding =
            bindingWithCredentials(SERVICE_IDENTIFIER, entry("clientid", "foo"), entry("clientsecret", "bar"));

        final ServiceBindingDestinationOptions options = ServiceBindingDestinationOptions.forService(binding).build();

        final IdentityAuthenticationServiceBindingDestinationLoader sut =
            new IdentityAuthenticationServiceBindingDestinationLoader();

        final Try<HttpDestination> result = sut.tryGetDestination(options);
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getCause()).isExactlyInstanceOf(DestinationNotFoundException.class);
    }

    @Test
    void testServiceBindingWithoutEndpoints()
    {
        final ServiceBinding binding =
            bindingWithCredentials(SERVICE_IDENTIFIER, entry("authentication-service", "identity"));

        final ServiceBindingDestinationOptions options = ServiceBindingDestinationOptions.forService(binding).build();

        final IdentityAuthenticationServiceBindingDestinationLoader sut =
            new IdentityAuthenticationServiceBindingDestinationLoader();

        final Try<HttpDestination> result = sut.tryGetDestination(options);
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getCause()).isExactlyInstanceOf(DestinationAccessException.class);
    }

    @Test
    void testServiceBindingWithMultipleEndpoints()
    {
        final DefaultServiceBinding binding =
            DefaultServiceBinding
                .builder()
                .copy(
                    nestedMap(
                        entry("endpoints.foo.uri", "https://foo.uri"),
                        entry("endpoints.bar.uri", "https://bar.uri")))
                .withServiceIdentifier(SERVICE_IDENTIFIER)
                .withCredentials(nestedMap(entry("authentication-service", "identity")))
                .build();

        final ServiceBindingDestinationOptions options = ServiceBindingDestinationOptions.forService(binding).build();

        final IdentityAuthenticationServiceBindingDestinationLoader sut =
            new IdentityAuthenticationServiceBindingDestinationLoader();

        final Try<HttpDestination> result = sut.tryGetDestination(options);
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getCause()).isExactlyInstanceOf(DestinationAccessException.class);
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
