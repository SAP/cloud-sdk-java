package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.ZeroTrustIdentityService.ZTIS_IDENTIFIER;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.sap.cloud.environment.servicebinding.api.DefaultServiceBindingBuilder;

class ZeroTrustIdentityServiceTest
{

    @Test
    void test()
    {
        var binding = new DefaultServiceBindingBuilder().withServiceIdentifier(ZTIS_IDENTIFIER).build();
        var d = DefaultHttpDestination.builder("foo.com").build();

        var opts =
            ServiceBindingDestinationOptions
                .forService(binding)
                .withOption(BtpServiceOptions.ZeroTrustIdentityOptions.enhanceDestination(d))
                .build();

        final HttpDestination destination = ServiceBindingDestinationLoader.defaultLoaderChain().getDestination(opts);

        assertThat(destination.getKeyStore()).isNotEmpty();
    }

}
