package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.ZeroTrustIdentityService.ZTIS_IDENTIFIER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.KeyStore;

import org.junit.jupiter.api.Test;

import com.sap.cloud.environment.servicebinding.api.DefaultServiceBinding;
import com.sap.cloud.environment.servicebinding.api.DefaultServiceBindingBuilder;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;

class ZeroTrustIdentityDestinationEnhancerTest
{
    private static final DefaultServiceBinding binding =
        new DefaultServiceBindingBuilder().withServiceIdentifier(ZTIS_IDENTIFIER).build();
    private final ZeroTrustIdentityService mockService = mock(ZeroTrustIdentityService.class);
    private final ServiceBindingDestinationLoader sut = spy(new ZeroTrustIdentityDestinationEnhancer(mockService));

    @Test
    void testEnhancerImplementsZtisBinding()
    {
        final KeyStore keyStore = mock(KeyStore.class);
        when(mockService.getOrCreateKeyStore()).thenReturn(keyStore);
        final HttpDestination destination = DefaultHttpDestination.builder("foo.com").build();

        var opts =
            ServiceBindingDestinationOptions
                .forService(binding)
                .withOption(BtpServiceOptions.ZeroTrustIdentityOptions.enhanceDestination(destination))
                .build();

        final HttpDestination result = sut.getDestination(opts);

        assertThat(result.getKeyStore()).contains(keyStore);
    }

    @Test
    void testOptionsWithoutZtisOptionThrows()
    {
        var opts = ServiceBindingDestinationOptions.forService(binding).build();

        assertThatThrownBy(() -> sut.getDestination(opts))
            .isInstanceOf(DestinationAccessException.class)
            .hasMessage("No Destination for ZeroTrust to enhance given in ServiceBindingDestinationOptions.");

        verify(sut).tryGetDestination(opts);
    }

    @Test
    void testEnhancerSkipsOtherBindings()
    {
        var opts =
            ServiceBindingDestinationOptions
                .forService(
                    new DefaultServiceBindingBuilder().withServiceIdentifier(ServiceIdentifier.of("foo")).build())
                .build();

        assertThatThrownBy(() -> sut.getDestination(opts)).isInstanceOf(DestinationNotFoundException.class);
    }

    @Test
    void testEnhancerIsLoadedByDefaultLoader()
    {
        final DefaultServiceBindingDestinationLoaderChain loaderChain =
            (DefaultServiceBindingDestinationLoaderChain) ServiceBindingDestinationLoader.defaultLoaderChain();
        assertThat(loaderChain.getDelegateLoaders())
            .hasAtLeastOneElementOfType(ZeroTrustIdentityDestinationEnhancer.class);
    }
}
