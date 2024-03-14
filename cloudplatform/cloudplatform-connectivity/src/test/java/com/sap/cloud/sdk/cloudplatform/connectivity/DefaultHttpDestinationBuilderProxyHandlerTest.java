package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.ServiceBindingDestinationOptions.Options.ProxyOptions;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.List;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Answers;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableMap;
import com.sap.cloud.environment.servicebinding.api.DefaultServiceBindingBuilder;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.BasicCredentials;
import com.sap.cloud.sdk.testutil.TestContext;

import io.vavr.control.Try;

class DefaultHttpDestinationBuilderProxyHandlerTest
{
    private static final AuthToken token1 = mock(AuthToken.class, Answers.RETURNS_DEEP_STUBS);
    private static final ServiceBinding connectivityService =
        new DefaultServiceBindingBuilder()
            .withServiceIdentifier(ServiceIdentifier.CONNECTIVITY)
            .withCredentials(
                ImmutableMap
                    .<String, Object> builder()
                    .put("clientid", "CLIENT_ID")
                    .put("clientsecret", "CLIENT_SECRET")
                    .put("url", "http://localhost:8888/xsuaa")
                    .put("onpremise_proxy_host", "localhost")
                    .put("onpremise_proxy_port", "8888")
                    .build())
            .build();

    private final ServiceBindingDestinationLoader destinationLoader = spy(new ServiceBindingDestinationLoader()
    {
        @Nonnull
        @Override
        public Try<HttpDestination> tryGetDestination( @Nonnull ServiceBindingDestinationOptions options )
        {
            return Try.success(mock(DefaultHttpDestination.class));
        }
    });

    @RegisterExtension
    static TestContext context = TestContext.withThreadContext();

    private DefaultHttpDestinationBuilderProxyHandler sut;
    private DefaultHttpDestination.Builder destinationBuilderOnPremise;

    @BeforeEach
    void setUp()
    {
        sut = spy(new DefaultHttpDestinationBuilderProxyHandler());
        when(sut.getServiceBindingAccessor()).thenReturn(() -> List.of(connectivityService));
        doReturn(destinationLoader).when(sut).getServiceBindingDestinationLoader();
        destinationBuilderOnPremise = DefaultHttpDestination.builder("http://foo").proxyType(ProxyType.ON_PREMISE);
    }

    @Test
    @DisplayName( "Handler should only be invoked for destinations with proxy type ON_PREMISE" )
    void testProxyTypeNotOnPremise()
    {
        final DefaultHttpDestination.Builder builder = DefaultHttpDestination.builder("http://foo");

        assertThatThrownBy(() -> sut.handle(builder)).isExactlyInstanceOf(IllegalStateException.class);
        verifyNoInteractions(destinationLoader);
    }

    @Test
    void testNoConnectivityBinding()
    {
        when(sut.getServiceBindingAccessor()).thenReturn(List::of);

        assertThatThrownBy(() -> sut.handle(destinationBuilderOnPremise))
            .isExactlyInstanceOf(DestinationAccessException.class)
            .hasMessageContaining("No service bindings found matching Connectivity");
        verifyNoInteractions(destinationLoader);
    }

    @Test
    void testMultipleConnectivityBindings()
    {
        when(sut.getServiceBindingAccessor()).thenReturn(() -> List.of(connectivityService, connectivityService));

        assertThatThrownBy(() -> sut.handle(destinationBuilderOnPremise))
            .isExactlyInstanceOf(DestinationAccessException.class)
            .hasMessageContaining("More than one service bindings found that match Connectivity");
        verifyNoInteractions(destinationLoader);
    }

    @Test
    void testNotPrincipalPropagation()
    {
        final URI uri = URI.create("http://foo");
        final BasicCredentials basicCredentials = new BasicCredentials("foo", "bar");
        final DefaultHttpDestination.Builder builder = destinationBuilderOnPremise.basicCredentials(basicCredentials);

        // test
        final DefaultHttpDestination result = sut.handle(builder);

        assertThat(result).isNotNull();
        verify(destinationLoader).tryGetDestination(argThat(options -> {
            final Header basicHeader = new Header("Authorization", basicCredentials.getHttpHeaderValue());
            return OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT == options.getOnBehalfOf()
                && options.getServiceBinding() == connectivityService
                && uri.equals(options.getOption(ProxyOptions.class).get().getUri())
                && options.getOption(ProxyOptions.class).get().getHeaders().contains(basicHeader);
        }));
    }

    @Test
    void testPrincipalPropagationDefault()
    {
        context.setAuthToken(token1);

        final URI uri = URI.create("http://foo");
        final DefaultHttpDestination.Builder builder =
            destinationBuilderOnPremise.authenticationType(AuthenticationType.PRINCIPAL_PROPAGATION);

        // test
        final DefaultHttpDestination result = sut.handle(builder);

        assertThat(result).isNotNull();
        verify(destinationLoader).tryGetDestination(argThat(options -> {
            final HttpDestination proxied = options.getOption(ProxyOptions.class).get();
            final String expectedHeader = "SAP-Connectivity-Authentication";
            return OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT == options.getOnBehalfOf()
                && options.getServiceBinding() == connectivityService
                && uri.equals(proxied.getUri())
                && proxied.getHeaders().stream().anyMatch(h -> h.getName().equalsIgnoreCase(expectedHeader));
        }));
    }

    @Test
    void testPrincipalPropagationCompatibility()
    {
        context.setAuthToken(token1);

        final URI uri = URI.create("http://foo");
        final DefaultHttpDestination.Builder builder =
            destinationBuilderOnPremise
                .authenticationType(AuthenticationType.PRINCIPAL_PROPAGATION)
                .property(DestinationProperty.PRINCIPAL_PROPAGATION_MODE, PrincipalPropagationMode.TOKEN_FORWARDING);

        // test
        final DefaultHttpDestination result = sut.handle(builder);

        assertThat(result).isNotNull();
        verify(destinationLoader).tryGetDestination(argThat(options -> {
            final HttpDestination proxied = options.getOption(ProxyOptions.class).get();
            final String expectedHeader = "SAP-Connectivity-Authentication";
            return OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT == options.getOnBehalfOf()
                && options.getServiceBinding() == connectivityService
                && uri.equals(options.getOption(ProxyOptions.class).get().getUri())
                && proxied.getHeaders().stream().anyMatch(h -> h.getName().equalsIgnoreCase(expectedHeader));
        }));
    }

    @Test
    void testPrincipalPropagationRecommended()
    {
        final URI uri = URI.create("http://foo");
        final DefaultHttpDestination.Builder builder =
            destinationBuilderOnPremise
                .authenticationType(AuthenticationType.PRINCIPAL_PROPAGATION)
                .property(DestinationProperty.PRINCIPAL_PROPAGATION_MODE, PrincipalPropagationMode.TOKEN_EXCHANGE);

        // test
        final DefaultHttpDestination result = sut.handle(builder);

        assertThat(result).isNotNull();
        verify(destinationLoader).tryGetDestination(argThat(options -> {
            return OnBehalfOf.NAMED_USER_CURRENT_TENANT == options.getOnBehalfOf()
                && options.getServiceBinding() == connectivityService
                && uri.equals(options.getOption(ProxyOptions.class).get().getUri())
                && options.getOption(ProxyOptions.class).get().getHeaders().isEmpty();
        }));
    }

    @Test
    void testNoAuthWithTenantId()
    {
        final URI uri = URI.create("http://foo");

        // provider tenant
        {
            final DefaultHttpDestination.Builder builder =
                destinationBuilderOnPremise.property(DestinationProperty.TENANT_ID, ""); // "" = provider

            // test
            final DefaultHttpDestination result = sut.handle(builder);

            assertThat(result).isNotNull();
            verify(destinationLoader).tryGetDestination(argThat(options -> {
                return OnBehalfOf.TECHNICAL_USER_PROVIDER == options.getOnBehalfOf()
                    && options.getServiceBinding() == connectivityService
                    && uri.equals(options.getOption(ProxyOptions.class).get().getUri());
            }));
        }

        Mockito.clearInvocations(destinationLoader);

        // subscriber tenant
        {
            final DefaultHttpDestination.Builder builder =
                DefaultHttpDestination
                    .builder(uri)
                    .proxyType(ProxyType.ON_PREMISE)
                    .property(DestinationProperty.TENANT_ID, "subscriber"); // "" = provider

            // test
            final DefaultHttpDestination result = sut.handle(builder);

            assertThat(result).isNotNull();
            verify(destinationLoader).tryGetDestination(argThat(options -> {
                return OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT == options.getOnBehalfOf()
                    && options.getServiceBinding() == connectivityService
                    && uri.equals(options.getOption(ProxyOptions.class).get().getUri());
            }));
        }
    }

    @Test
    void testPrincipalPropagationWithTenantId()
    {
        context.setAuthToken(token1);

        final URI uri = URI.create("http://foo");

        // provider tenant
        {
            final DefaultHttpDestination.Builder builder =
                destinationBuilderOnPremise
                    .authenticationType(AuthenticationType.PRINCIPAL_PROPAGATION)
                    .property(DestinationProperty.TENANT_ID, ""); // "" = provider

            // test
            final DefaultHttpDestination result = sut.handle(builder);

            assertThat(result).isNotNull();
            verify(destinationLoader).tryGetDestination(argThat(options -> {
                return OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT == options.getOnBehalfOf()
                    && options.getServiceBinding() == connectivityService
                    && uri.equals(options.getOption(ProxyOptions.class).get().getUri());
            }));
        }

        Mockito.clearInvocations(destinationLoader);

        // subscriber tenant
        {
            final DefaultHttpDestination.Builder builder =
                destinationBuilderOnPremise
                    .authenticationType(AuthenticationType.PRINCIPAL_PROPAGATION)
                    .property(DestinationProperty.TENANT_ID, "subscriber"); // "" = provider

            // test
            final DefaultHttpDestination result = sut.handle(builder);

            assertThat(result).isNotNull();
            verify(destinationLoader).tryGetDestination(argThat(options -> {
                return OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT == options.getOnBehalfOf()
                    && options.getServiceBinding() == connectivityService
                    && uri.equals(options.getOption(ProxyOptions.class).get().getUri());
            }));
        }
    }
}
