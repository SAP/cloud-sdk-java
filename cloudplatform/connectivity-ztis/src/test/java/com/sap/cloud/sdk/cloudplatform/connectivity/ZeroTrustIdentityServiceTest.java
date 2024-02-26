package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

import org.junit.jupiter.api.Test;

class ZeroTrustIdentityServiceTest
{
    private final ZeroTrustIdentityService sut = spy(new ZeroTrustIdentityService());

    @Test
    void test()
    {
        /*var binding = new DefaultServiceBindingBuilder().withServiceIdentifier(ZTIS_IDENTIFIER).build();
        var d = DefaultHttpDestination.builder("foo.com").build();

        var opts =
            ServiceBindingDestinationOptions
                .forService(binding)
                .withOption(BtpServiceOptions.ZeroTrustIdentityOptions.enhanceDestination(d))
                .build();
        */

        assertThat(sut).isNotNull();
    }

}
