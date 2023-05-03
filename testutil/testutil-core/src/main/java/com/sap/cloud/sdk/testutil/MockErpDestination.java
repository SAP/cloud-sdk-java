package com.sap.cloud.sdk.testutil;

import java.security.KeyStore;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.connectivity.AuthenticationType;
import com.sap.cloud.sdk.cloudplatform.connectivity.Header;
import com.sap.cloud.sdk.cloudplatform.connectivity.ProxyConfiguration;
import com.sap.cloud.sdk.cloudplatform.connectivity.ProxyType;
import com.sap.cloud.sdk.cloudplatform.security.BasicCredentials;
import com.sap.cloud.sdk.cloudplatform.security.Credentials;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/**
 * Represents information for mocking an ERP destination.
 */
@Builder
@Data
public class MockErpDestination
{
    @Nonnull
    private final String name;

    @Nullable
    private final ErpSystem erpSystem;

    @Builder.Default
    @Nullable
    private Credentials credentials = null;

    @Builder.Default
    @Nullable
    private AuthenticationType authenticationType = null;

    @Builder.Default
    @Nullable
    private ProxyType proxyType = null;

    @Builder.Default
    @Nullable
    private ProxyConfiguration proxyConfiguration = null;

    @Singular( "header" )
    @Nullable
    private List<Header> headers;

    @Builder.Default
    @Nullable
    private KeyStore trustStore = null;

    @Builder.Default
    @Nullable
    private String trustStorePassword = null;

    @Builder.Default
    @Nullable
    private Boolean isTrustingAllCertificates = null;

    @Builder.Default
    @Nullable
    private KeyStore keyStore = null;

    @Builder.Default
    @Nullable
    private String keyStorePassword = null;

    @Singular( "property" )
    @Nullable
    private Map<String, String> properties;

    /**
     * The Mock ERP destination builder.
     */
    public static class MockErpDestinationBuilder
    {
        /**
         * Enables basic authentication with the given credentials.
         *
         * @param credentials
         *            The destination credentials
         * @return The destination builder reference.
         */
        @Nonnull
        public MockErpDestinationBuilder basicAuthentication( @Nonnull final BasicCredentials credentials )
        {
            authenticationType(AuthenticationType.BASIC_AUTHENTICATION);
            credentials(credentials);
            return this;
        }
    }

    /**
     * Creates a new builder for the given mandatory parameters.
     *
     *
     * @param name
     *            The destination name.
     * @return The destination builder initiated with the given mandatory parameters.
     */
    @Nonnull
    public static MockErpDestinationBuilder builder( @Nonnull final String name )
    {
        return new MockErpDestinationBuilder().name(name);
    }
}
