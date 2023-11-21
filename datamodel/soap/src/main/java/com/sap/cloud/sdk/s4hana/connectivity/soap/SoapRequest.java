/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.soap;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Stub;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.databinding.utils.ConverterUtil;
import org.apache.axis2.description.TransportOutDescription;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.kernel.TransportSender;
import org.apache.axis2.kernel.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.message.BasicNameValuePair;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationProperty;
import com.sap.cloud.sdk.cloudplatform.connectivity.Header;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestinationProperties;
import com.sap.cloud.sdk.cloudplatform.connectivity.ProxyConfiguration;
import com.sap.cloud.sdk.cloudplatform.servlet.LocaleAccessor;

import io.vavr.CheckedFunction1;
import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;

/**
 * Class representing a request calling a SOAP service in an ERP system.
 * <p>
 * This class instantiates a service class which extends {@link Stub} from the Axis2 framework and prepares the Axis2
 * configuration context according to the provided {@link Destination}.
 * <p>
 * Use the static method {@code registerCustomConverter} to register your own custom converter class which Axis2 uses
 * for converting values from the XSD types of the SOAP envelope to Java types.
 * <p>
 * By default, the provided class {@link SoapCustomConverter} is registered at application startup.
 *
 * @param <ServiceT>
 *            Subtype of {@link Stub} representing your SOAP service.
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Slf4j
@Deprecated
public class SoapRequest<ServiceT extends Stub>
{
    private static final int DEFAULT_HTTP_PORT = 80;
    private static final int DEFAULT_HTTPS_PORT = 443;

    private static final int MAX_TOTAL_CONNECTIONS = 200;
    private static final int MAX_CONNECTIONS_PER_ROUTE = 100;

    /**
     * Returns the instance of the class {@code ServiceT} which was created by this {@code SoapQuery}.
     */
    private final ServiceT service;

    /**
     * Use this method to register your own custom converter class which Axis2 uses for converting values from the SOAP
     * envelope following XSD types to Java types.
     * <p>
     * Your converter class must be a subtype of {@link ConverterUtil}.
     *
     * @param customConverter
     *            Your converter class
     * @throws SoapException
     *             Thrown in case the custom converter class could not be registered within the Axis2 framework.
     */
    public static void registerCustomConverter( @Nonnull final Class<? extends ConverterUtil> customConverter )
        throws SoapException
    {
        try {
            if( log.isDebugEnabled() ) {
                log.debug("Registering Axis2 custom converter class " + customConverter.getSimpleName() + ".");
            }

            final Field isCustomClassPresentField = ConverterUtil.class.getDeclaredField("isCustomClassPresent");

            isCustomClassPresentField.setAccessible(true);
            isCustomClassPresentField.setBoolean(null, true);
            isCustomClassPresentField.setAccessible(false);

            final Field customClassField = ConverterUtil.class.getDeclaredField("customClass");

            customClassField.setAccessible(true);
            customClassField.set(null, customConverter);
            customClassField.setAccessible(false);
        }
        catch( final NoSuchFieldException | IllegalAccessException e ) {
            throw new SoapException(
                "Error while registering Custom Converter class "
                    + customConverter.getSimpleName()
                    + " in the Axis2 library.",
                e);
        }
    }

    /**
     * Takes the class type of the SOAP service type {@code ServiceT} as {@code serviceClass} and an
     * {@link Destination}, creates and prepares the Axis2 configuration context and instantiates the class
     * {@code ServiceT}.
     *
     * @param serviceClass
     *            Class type of {@code ServiceT}
     * @param destination
     *            An instance of {@link Destination}
     * @throws SoapException
     *             Thrown in case the Axis2 configuration context could not be prepared or the service instance could
     *             not be instantiated.
     */
    public SoapRequest( @Nonnull final Class<ServiceT> serviceClass, @Nonnull final Destination destination )
        throws SoapException
    {
        service = instantiateServiceClass(serviceClass, getServiceConfigurationContext());
        prepareSoapCall(service, destination.asHttp());
    }

    @Nonnull
    private ServiceT instantiateServiceClass(
        @Nonnull final Class<ServiceT> serviceClass,
        @Nonnull final ConfigurationContext configurationContext )
        throws SoapException
    {
        try {
            return serviceClass.getConstructor(ConfigurationContext.class).newInstance(configurationContext);
        }
        catch( final
            InstantiationException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchMethodException e ) {
            throw new SoapException(
                "Error during constructor invocation of class " + serviceClass.getSimpleName() + ".",
                e);
        }
    }

    @Nonnull
    private ConfigurationContext getServiceConfigurationContext()
        throws SoapException
    {
        try {
            final ConfigurationContext configurationContext =
                ConfigurationContextFactory.createDefaultConfigurationContext();

            specifyUsageOfHttpClient4(configurationContext);

            return configurationContext;
        }
        catch( final Exception e ) {
            throw new SoapException("Error while preparing Axis2 configuration context: " + e.getMessage() + ".", e);
        }
    }

    private void specifyUsageOfHttpClient4( @Nonnull final ConfigurationContext configurationContext )
        throws SoapException
    {
        final AxisConfiguration axisConfiguration = configurationContext.getAxisConfiguration();

        axisConfiguration.getTransportsOut().get("https").setSender(new DefaultHttpClientTransportSender());
        axisConfiguration.getTransportsOut().get("http").setSender(new DefaultHttpClientTransportSender());

        final HashMap<String, TransportOutDescription> transportsOut = axisConfiguration.getTransportsOut();

        for( final TransportOutDescription transportOut : transportsOut.values() ) {
            final TransportSender sender = transportOut.getSender();
            if( sender != null ) {
                try {
                    sender.init(configurationContext, transportOut);
                }
                catch( final AxisFault e ) {
                    throw new SoapException("Error while initializing Axis2 library.", e);
                }
            }
        }

        if( log.isDebugEnabled() ) {
            log.debug(DefaultHttpClientTransportSender.class.getSimpleName() + " set in Axis2 configuration.");
        }
    }

    private void prepareSoapCall( @Nonnull final ServiceT service, @Nonnull final HttpDestination destination )
        throws SoapException
    {
        try {
            setTargetUriOfSoapCallFromDestination(service, destination);
            setHeadersOfSoapCall(service, destination);
            setProxyOfSoapCall(service, destination);
            setTrustAllOfSoapCall(service, destination);
        }
        catch( final
            KeyManagementException
                | NoSuchAlgorithmException
                | UnrecoverableKeyException
                | KeyStoreException e ) {
            throw new SoapException(e);
        }
    }

    @SuppressWarnings( "deprecation" )
    private void setTrustAllOfSoapCall( @Nonnull final ServiceT service, @Nonnull final HttpDestination destination )
        throws UnrecoverableKeyException,
            NoSuchAlgorithmException,
            KeyStoreException,
            KeyManagementException
    {
        if( destination.isTrustingAllCertificates() ) {
            final TrustAllSslSocketFactory socketFactory = new TrustAllSslSocketFactory(destination);

            final org.apache.http.conn.ClientConnectionManager connectionManager =
                buildConnectionManager(socketFactory);

            service
                ._getServiceClient()
                .getOptions()
                .setProperty(HTTPConstants.MULTITHREAD_HTTP_CONNECTION_MANAGER, connectionManager);
        }
    }

    @SuppressWarnings( "deprecation" )
    @Nonnull
    private org.apache.http.conn.ClientConnectionManager buildConnectionManager(
        @Nonnull final TrustAllSslSocketFactory socketFactory )
    {
        final org.apache.http.conn.scheme.SchemeRegistry schemeRegistry =
            new org.apache.http.conn.scheme.SchemeRegistry();

        schemeRegistry
            .register(
                new org.apache.http.conn.scheme.Scheme(
                    "http",
                    DEFAULT_HTTP_PORT,
                    org.apache.http.conn.scheme.PlainSocketFactory.getSocketFactory()));
        schemeRegistry.register(new org.apache.http.conn.scheme.Scheme("https", DEFAULT_HTTPS_PORT, socketFactory));

        final org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager connectionManager =
            new org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager(schemeRegistry);

        connectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        connectionManager.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);

        return connectionManager;
    }

    @SuppressWarnings( "deprecation" )
    private static class TrustAllSslSocketFactory extends org.apache.http.conn.ssl.SSLSocketFactory
    {
        TrustAllSslSocketFactory( final HttpDestination destination )
            throws NoSuchAlgorithmException,
                KeyManagementException,
                KeyStoreException,
                UnrecoverableKeyException
        {
            super(new TrustAllStrategy(), new org.apache.http.conn.ssl.AllowAllHostnameVerifier());

            final Option<String> tlsVersion = destination.getTlsVersion();

            final SSLContext sslContext = SSLContext.getInstance(tlsVersion.getOrElse("TLSv1.2"));

            final TrustManager trustAllTrustManager = new TrustAllManager();

            sslContext.init(null, new TrustManager[] { trustAllTrustManager }, null);
        }
    }

    private static class TrustAllManager implements X509TrustManager
    {
        @Override
        public void checkClientTrusted( @Nullable final X509Certificate[] x509Certificates, @Nullable final String s )
        {
            // do nothing on purpose
        }

        @Override
        public void checkServerTrusted( @Nullable final X509Certificate[] x509Certificates, @Nullable final String s )
        {
            // do nothing on purpose
        }

        @Override
        @Nonnull
        public X509Certificate[] getAcceptedIssuers()
        {
            return new X509Certificate[0];
        }
    }

    private
        void
        setProxyOfSoapCall( @Nonnull final ServiceT service, @Nonnull final HttpDestinationProperties destination )
    {
        final Option<ProxyConfiguration> proxyConfiguration = destination.getProxyConfiguration();

        if( proxyConfiguration.isDefined() ) {
            final String proxyHost = proxyConfiguration.get().getUri().getHost();
            final int proxyPort = proxyConfiguration.get().getUri().getPort();

            final HttpTransportProperties.ProxyProperties proxyProperties =
                new HttpTransportProperties.ProxyProperties();

            proxyProperties.setProxyName(proxyHost);
            proxyProperties.setProxyPort(proxyPort);

            service._getServiceClient().getOptions().setProperty(HTTPConstants.PROXY, proxyProperties);

            log.debug("Setting proxy for SOAP call: {}:{}.", proxyHost, proxyPort);
        }
    }

    private void setTargetUriOfSoapCallFromDestination(
        @Nonnull final ServiceT service,
        @Nonnull final HttpDestinationProperties destination )
        throws SoapException
    {
        final URI originalSoapUri = getTargetUriOfSoapCall(service);
        log.debug("Original SOAP service URI from WSDL file: {}.", originalSoapUri);

        final List<NameValuePair> queryStringParams = Lists.newArrayList();

        destination
            .get(DestinationProperty.SAP_CLIENT)
            .map(c -> new BasicNameValuePair(DestinationProperty.SAP_CLIENT.getKeyName(), c))
            .peek(queryStringParams::add);

        destination
            .get(DestinationProperty.SAP_LANGUAGE)
            .map(Locale::new)
            .orElse(() -> Option.of(LocaleAccessor.getCurrentLocale()))
            .map(Locale::toString)
            .map(l -> new BasicNameValuePair(DestinationProperty.SAP_LANGUAGE.getKeyName(), l))
            .peek(queryStringParams::add);

        final URI destinationUri = destination.getUri();

        final URI targetUri;
        try {
            targetUri =
                new URIBuilder()
                    .setScheme(destinationUri.getScheme())
                    .setUserInfo(destinationUri.getUserInfo())
                    .setHost(destinationUri.getHost())
                    .setPort(destinationUri.getPort())
                    .setPath(originalSoapUri.getPath())
                    .setParameters(queryStringParams)
                    .build();
        }
        catch( final URISyntaxException e ) {
            throw new SoapException("Error while constructing target URI of SOAP service.", e);
        }

        log.debug("Determined target URI of SOAP service: {}.", targetUri);

        setTargetUriOfSoapCall(service, targetUri);
    }

    private void setHeadersOfSoapCall( @Nonnull final ServiceT service, @Nonnull final HttpDestination destination )
    {
        final Map<String, String> soapHeaders = Maps.newHashMap();

        final Collection<Header> destinationHeaders = destination.getHeaders(destination.getUri());

        for( final Header header : destinationHeaders ) {
            soapHeaders.put(header.getName(), header.getValue());
        }

        service._getServiceClient().getOptions().setProperty(HTTPConstants.HTTP_HEADERS, soapHeaders);
    }

    private URI getTargetUriOfSoapCall( @Nonnull final ServiceT service )
        throws SoapException
    {
        final String address = service._getServiceClient().getOptions().getTo().getAddress();

        if( address.isEmpty() ) {
            throw new SoapException(
                "URI pointing to SOAP service is empty. Ensure that XML attribute location of the XML tag soap:address inside the respective WSDL file contains as valid URI.");
        }

        final URI targetUri;
        try {
            targetUri = new URI(address);
        }
        catch( final URISyntaxException e ) {
            throw new SoapException(
                "Error while reading target URI of SOAP service coming from WSDL: " + address + " is not a valid URI.",
                e);
        }

        return targetUri;
    }

    private void setTargetUriOfSoapCall( @Nonnull final ServiceT service, @Nonnull final URI targetUri )
    {
        service._getServiceClient().getOptions().getTo().setAddress(targetUri.toString());
    }

    /**
     * Executes a request against a SOAP service based on the given function.
     *
     * @param function
     *            The function that calls the SOAP service and returns the relevant result.
     * @param <ReturnT>
     *            The result type of the given function.
     * @return The result of the function.
     * @throws SoapException
     *             If there is an issue while executing the request.
     */
    @Nonnull
    public <ReturnT> ReturnT execute( @Nonnull final CheckedFunction1<ServiceT, ReturnT> function )
        throws SoapException
    {
        try {
            return function.apply(service);
        }
        catch( final SoapException t ) {
            throw t;
        }
        catch( final Throwable t ) { // ALLOW CATCH THROWABLE
            throw new SoapException(t);
        }
    }
}
