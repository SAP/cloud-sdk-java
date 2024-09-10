package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BtpServiceOptionsTest
{
    @Nested
    @DisplayName( "IasOptions" )
    class IasOptionsTest
    {
        @Test
        @SuppressWarnings( "deprecation" )
        @DisplayName( "Regression Tests: withTargetUri returns AuthenticationServiceOptions.TargetUri" )
        void testIasTargetUriReturnsAuthenticationServiceUri()
        {
            {
                // string-based overload
                final ServiceBindingDestinationOptions.OptionsEnhancer<?> sut =
                    BtpServiceOptions.IasOptions.withTargetUri("https://example.com");

                assertThat(sut).isNotNull();
                assertThat(sut).isExactlyInstanceOf(BtpServiceOptions.AuthenticationServiceOptions.TargetUri.class);
            }

            {
                // uri-based overload
                final ServiceBindingDestinationOptions.OptionsEnhancer<?> sut =
                    BtpServiceOptions.IasOptions.withTargetUri(URI.create("https://example.com"));

                assertThat(sut).isNotNull();
                assertThat(sut).isExactlyInstanceOf(BtpServiceOptions.AuthenticationServiceOptions.TargetUri.class);
            }
        }
    }
}
