package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.ServiceBindingTestUtility.bindingWithCredentials;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
        final ServiceBinding binding =
            bindingWithCredentials(
                IDENTITY_AUTHENTICATION,
                entry("authentication-service.service-label", "identity"),
                entry("endpoints.foo.protocol", "http"),
                entry("endpoints.foo.uri", "https://foo.uri"));

        final ServiceBindingDestinationLoader delegate = mockDelegateLoader(delegateOptions -> {
            assertThat(delegateOptions.getServiceBinding()).isSameAs(IDENTITY_BINDING);
            assertThat(delegateOptions.getOnBehalfOf()).isEqualTo(OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
            assertThat(delegateOptions.getOption(BtpServiceOptions.IasOptions.IasTargetUri.class))
                .containsExactly(URI.create("https://foo.uri"));
            assertThat(delegateOptions.getOption(BtpServiceOptions.IasOptions.IasCommunicationOptions.class)).isEmpty();
        });

        final IdentityAuthenticationServiceBindingDestinationLoader sut =
            new IdentityAuthenticationServiceBindingDestinationLoader(delegate);

        final ServiceBindingDestinationOptions options = ServiceBindingDestinationOptions.forService(binding).build();
        sut.tryGetDestination(options);

        verify(delegate).tryGetDestination(any());
    }

    @Test
    void testEndpointWithoutTokenForTechnicalUser()
    {
        final ServiceBinding binding =
            bindingWithCredentials(
                IDENTITY_AUTHENTICATION,
                entry("authentication-service.service-label", "identity"),
                entry("endpoints.foo.protocol", "http"),
                entry("endpoints.foo.uri", "https://foo.uri"),
                entry("endpoints.foo.requires-token-for-technical-access", false));

        final ServiceBindingDestinationLoader delegate = mockDelegateLoader(delegateOptions -> {
            assertThat(delegateOptions.getServiceBinding()).isSameAs(IDENTITY_BINDING);
            assertThat(delegateOptions.getOnBehalfOf()).isEqualTo(OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
            assertThat(delegateOptions.getOption(BtpServiceOptions.IasOptions.IasTargetUri.class))
                .containsExactly(URI.create("https://foo.uri"));
            assertThat(delegateOptions.getOption(BtpServiceOptions.IasOptions.IasCommunicationOptions.class).get())
                .isEqualTo(BtpServiceOptions.IasOptions.withMutualTlsOnly().getValue());
        });

        final IdentityAuthenticationServiceBindingDestinationLoader sut =
            new IdentityAuthenticationServiceBindingDestinationLoader(delegate);

        final ServiceBindingDestinationOptions options = ServiceBindingDestinationOptions.forService(binding).build();
        sut.tryGetDestination(options);

        verify(delegate).tryGetDestination(any());
    }

    @Test
    void testEndpointWithoutTokenForTechnicalUserButNamedUserBehalf()
    {
        final ServiceBinding binding =
            bindingWithCredentials(
                IDENTITY_AUTHENTICATION,
                entry("authentication-service.service-label", "identity"),
                entry("endpoints.foo.protocol", "http"),
                entry("endpoints.foo.uri", "https://foo.uri"),
                entry("endpoints.foo.requires-token-for-technical-access", false));

        final ServiceBindingDestinationLoader delegate = mockDelegateLoader(delegateOptions -> {
            assertThat(delegateOptions.getServiceBinding()).isSameAs(IDENTITY_BINDING);
            assertThat(delegateOptions.getOnBehalfOf()).isEqualTo(OnBehalfOf.NAMED_USER_CURRENT_TENANT);
            assertThat(delegateOptions.getOption(BtpServiceOptions.IasOptions.IasTargetUri.class))
                .containsExactly(URI.create("https://foo.uri"));
            assertThat(delegateOptions.getOption(BtpServiceOptions.IasOptions.IasCommunicationOptions.class)).isEmpty();
        });

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
    void testDelegateLoaderIsCalledForMultipleEndpoints()
    {
        // this test makes sure that our `IdentityAuthenticationServiceBindingDestinationLoader` is capable of dealing
        // with multiple HTTP endpoints
        final ServiceBinding binding =
            bindingWithCredentials(
                IDENTITY_AUTHENTICATION,
                entry("authentication-service.service-label", "identity"),
                entry("endpoints.first.protocol", "http"),
                entry("endpoints.first.uri", "https://first.uri"),
                entry("endpoints.second.protocol", "http"),
                entry("endpoints.second.uri", "https://second.uri"));

        final ServiceBindingDestinationLoader delegate = mockDelegateLoader(delegateOptions -> {
            assertThat(delegateOptions.getServiceBinding()).isSameAs(IDENTITY_BINDING);
            assertThat(delegateOptions.getOnBehalfOf()).isEqualTo(OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
            assertThat(delegateOptions.getOption(BtpServiceOptions.IasOptions.IasCommunicationOptions.class)).isEmpty();
        });

        final IdentityAuthenticationServiceBindingDestinationLoader sut =
            new IdentityAuthenticationServiceBindingDestinationLoader(delegate);

        final ServiceBindingDestinationOptions options = ServiceBindingDestinationOptions.forService(binding).build();
        sut.tryGetDestination(options);

        verify(delegate).tryGetDestination(any());
    }

    @Test
    void testOnBehalfIsForwarded()
    {
        final ServiceBinding binding =
            bindingWithCredentials(
                IDENTITY_AUTHENTICATION,
                entry("authentication-service.service-label", "identity"),
                entry("endpoints.foo.protocol", "http"),
                entry("endpoints.foo.uri", "https://foo.uri"));

        final ServiceBindingDestinationLoader delegate = mockDelegateLoader(delegateOptions -> {
            assertThat(delegateOptions.getServiceBinding()).isSameAs(IDENTITY_BINDING);
            assertThat(delegateOptions.getOnBehalfOf()).isEqualTo(OnBehalfOf.NAMED_USER_CURRENT_TENANT);
            assertThat(delegateOptions.getOption(BtpServiceOptions.IasOptions.IasTargetUri.class))
                .containsExactly(URI.create("https://foo.uri"));
            assertThat(delegateOptions.getOption(BtpServiceOptions.IasOptions.IasCommunicationOptions.class)).isEmpty();
        });

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
    void testBindingWithApplicationName()
    {
        final ServiceBinding binding =
            bindingWithCredentials(
                IDENTITY_AUTHENTICATION,
                entry("authentication-service.service-label", "identity"),
                entry("authentication-service.app-name", "foo"),
                entry("endpoints.foo.protocol", "http"),
                entry("endpoints.foo.uri", "https://foo.uri"));

        final ServiceBindingDestinationLoader delegate = mockDelegateLoader(delegateOptions -> {
            assertThat(delegateOptions.getServiceBinding()).isSameAs(IDENTITY_BINDING);
            assertThat(delegateOptions.getOnBehalfOf()).isEqualTo(OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
            assertThat(delegateOptions.getOption(BtpServiceOptions.IasOptions.IasTargetUri.class))
                .containsExactly(URI.create("https://foo.uri"));
            assertThat(delegateOptions.getOption(BtpServiceOptions.IasOptions.IasCommunicationOptions.class).get())
                .isEqualTo(BtpServiceOptions.IasOptions.withApplicationName("foo"));
        });

        final IdentityAuthenticationServiceBindingDestinationLoader sut =
            new IdentityAuthenticationServiceBindingDestinationLoader(delegate);

        final ServiceBindingDestinationOptions options = ServiceBindingDestinationOptions.forService(binding).build();
        sut.tryGetDestination(options);

        verify(delegate).tryGetDestination(any());
    }

    @Test
    void testApplicationNameIsIgnoredForMutualTlsOnly()
    {
        final ServiceBinding binding =
            bindingWithCredentials(
                IDENTITY_AUTHENTICATION,
                entry("authentication-service.service-label", "identity"),
                entry("authentication-service.app-name", "foo"),
                entry("endpoints.foo.protocol", "http"),
                entry("endpoints.foo.uri", "https://foo.uri"),
                entry("endpoints.foo.requires-token-for-technical-access", false));

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

        final ServiceBindingDestinationOptions options = ServiceBindingDestinationOptions.forService(binding).build();
        sut.tryGetDestination(options);

        verify(delegate).tryGetDestination(any());
    }

    @Test
    void testApplicationNameTechnicalMutualTlsOnlyButNamedUserBehalf()
    {
        final ServiceBinding binding =
            bindingWithCredentials(
                IDENTITY_AUTHENTICATION,
                entry("authentication-service.service-label", "identity"),
                entry("authentication-service.app-name", "foo"),
                entry("endpoints.foo.protocol", "http"),
                entry("endpoints.foo.uri", "https://foo.uri"),
                entry("endpoints.foo.requires-token-for-technical-access", false));

        final ServiceBindingDestinationLoader delegate = mockDelegateLoader(delegateOptions -> {
            assertThat(delegateOptions.getServiceBinding()).isSameAs(IDENTITY_BINDING);
            assertThat(delegateOptions.getOnBehalfOf()).isEqualTo(OnBehalfOf.NAMED_USER_CURRENT_TENANT);
            assertThat(delegateOptions.getOption(BtpServiceOptions.IasOptions.IasTargetUri.class))
                .containsExactly(URI.create("https://foo.uri"));
            assertThat(delegateOptions.getOption(BtpServiceOptions.IasOptions.IasCommunicationOptions.class).get())
                .isEqualTo(BtpServiceOptions.IasOptions.withApplicationName("foo"));
        });

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
    void testServiceBindingWithoutAuthenticationService()
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
    void testServiceBindingWithoutEndpoints()
    {
        final ServiceBinding binding =
            bindingWithCredentials(IDENTITY_AUTHENTICATION, entry("authentication-service.service-label", "identity"));

        final ServiceBindingDestinationLoader delegate = mock(ServiceBindingDestinationLoader.class);

        final IdentityAuthenticationServiceBindingDestinationLoader sut =
            new IdentityAuthenticationServiceBindingDestinationLoader(delegate);

        final ServiceBindingDestinationOptions options =
            ServiceBindingDestinationOptions
                .forService(binding)
                .onBehalfOf(OnBehalfOf.NAMED_USER_CURRENT_TENANT)
                .build();
        final Try<HttpDestination> result = sut.tryGetDestination(options);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getCause()).isExactlyInstanceOf(DestinationAccessException.class);

        verify(delegate, never()).tryGetDestination(any());
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
