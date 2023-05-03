package com.sap.cloud.sdk.testutil;

import java.net.URI;
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
 * Represents information for mocking a destination.
 */
@Builder
@Data
public class MockDestination
{
    @Nonnull
    private final String name;

    @Nonnull
    private final URI uri;

    @Builder.Default
    @Nullable
    private AuthenticationType authenticationType = null;

    @Builder.Default
    @Nullable
    private Credentials credentials = null;

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
     * The mock destination builder.
     */
    public static class MockDestinationBuilder
    {
        /**
         * Enables basic authentication with the given credentials. Sets the authentication type to
         * {@link AuthenticationType#BASIC_AUTHENTICATION}. Potentially existing "Authorization" headers are not
         * modified or removed.
         *
         * @param credentials
         *            The destination credentials.
         * @return The destination builder reference.
         */
        @Nonnull
        public MockDestinationBuilder basicAuthentication( @Nonnull final BasicCredentials credentials )
        {
            authenticationType(AuthenticationType.BASIC_AUTHENTICATION);
            credentials(credentials);
            return this;
        }
    }

    /**
     * Creates a new builder for the given mandatory parameters.
     *
     * @param name
     *            The destination name.
     * @param uri
     *            The destination URI.
     * @return The destination builder initiated with the given mandatory parameters.
     */
    @Nonnull
    public static MockDestinationBuilder builder( @Nonnull final String name, @Nonnull final URI uri )
    {
        return new MockDestinationBuilder().name(name).uri(uri);
    }
}
