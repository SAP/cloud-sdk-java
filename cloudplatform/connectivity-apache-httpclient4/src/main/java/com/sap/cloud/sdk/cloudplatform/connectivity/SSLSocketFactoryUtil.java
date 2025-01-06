package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class SSLSocketFactoryUtil
{
    @Nullable
    static Registry<ConnectionSocketFactory> getConnectionSocketFactoryRegistry(
        @Nullable final HttpDestinationProperties destination )
        throws GeneralSecurityException,
            IOException
    {
        if( !supportsTls(destination) ) {
            return null;
        }

        log.debug("The destination uses HTTPS for target \"{}\".", destination.getUri());
        final LayeredConnectionSocketFactory sslConnectionFactory = getConnectionSocketFactory(destination);

        final RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.create();
        registryBuilder.register("https", sslConnectionFactory);
        registryBuilder.register("http", PlainConnectionSocketFactory.getSocketFactory());
        return registryBuilder.build();
    }

    private static boolean supportsTls( @Nullable final HttpDestinationProperties destination )
    {
        if( destination == null ) {
            return false;
        }
        final String scheme = destination.getUri().getScheme();
        return "https".equalsIgnoreCase(scheme) || StringUtils.isEmpty(scheme);
    }

    @Nonnull
    private static LayeredConnectionSocketFactory getConnectionSocketFactory(
        @Nonnull final HttpDestinationProperties destination )
        throws GeneralSecurityException,
            IOException
    {
        final SSLContext sslContext = new SSLContextFactory().createSSLContext(destination);

        final HostnameVerifier hostnameVerifier = getHostnameVerifier(destination);

        return new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
    }

    private static HostnameVerifier getHostnameVerifier( final HttpDestinationProperties destination )
    {
        return destination.isTrustingAllCertificates() ? new NoopHostnameVerifier() : new DefaultHostnameVerifier();
    }
}
