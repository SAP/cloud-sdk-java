package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.BtpServiceOptions.IasOptions.IasCommunicationOptions;
import static com.sap.cloud.sdk.cloudplatform.connectivity.BtpServiceOptions.IasOptions.IasTargetUri;
import static com.sap.cloud.sdk.cloudplatform.connectivity.BtpServiceOptions.IasOptions.NoTokenForTechnicalProviderUser;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ServiceBindingTestUtility.bindingWithCredentials;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
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
        final ServiceBinding binding = enhanceMinimalBinding();

        final ServiceBindingDestinationLoader delegate = mockDelegateLoader(delegateOptions -> {
            assertThat(delegateOptions.getServiceBinding()).isSameAs(IDENTITY_BINDING);
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

        final ServiceBindingDestinationLoader delegate = mockDelegateLoader(delegateOptions -> {
            assertThat(delegateOptions.getOption(NoTokenForTechnicalProviderUser.class)).contains(true);
        });

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

        final ServiceBindingDestinationLoader delegate = mockDelegateLoader(delegateOptions -> {
            assertThat(delegateOptions.getOption(NoTokenForTechnicalProviderUser.class)).isEmpty();
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
        final ServiceBinding binding = enhanceMinimalBinding();

        final ServiceBindingDestinationLoader delegate = mockDelegateLoader(delegateOptions -> {
            assertThat(delegateOptions.getOnBehalfOf()).isEqualTo(OnBehalfOf.NAMED_USER_CURRENT_TENANT);
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

    @SuppressWarnings( "unchecked" )
    @Nonnull
    @SafeVarargs
    private static ServiceBinding enhanceMinimalBinding( @Nonnull final Map.Entry<String, Object>... additionalEntries )
    {
        final List<Map.Entry<String, Object>> allEntries = new ArrayList<>();
        allEntries.add(entry("authentication-service.service-label", "identity"));
        allEntries.add(entry("endpoints.foo.uri", "https://foo.uri"));
        allEntries.addAll(Arrays.stream(additionalEntries).toList());

        return bindingWithCredentials(IDENTITY_AUTHENTICATION, allEntries.toArray(new Map.Entry[0]));
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
