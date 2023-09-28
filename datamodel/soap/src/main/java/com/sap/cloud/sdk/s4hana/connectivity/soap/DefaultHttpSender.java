package com.sap.cloud.sdk.s4hana.connectivity.soap;

import java.net.URL;

import javax.annotation.Nullable;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.axis2.transport.http.impl.httpclient4.HTTPSenderImpl;
import org.apache.http.HttpHost;

class DefaultHttpSender extends HTTPSenderImpl
{
    @SuppressWarnings( "deprecation" )
    @Override
    protected HttpHost getHostConfiguration(
        final org.apache.http.impl.client.AbstractHttpClient httpClient,
        final MessageContext messageContext,
        final URL targetUrl )
        throws AxisFault
    {
        final HttpHost hostConfiguration = super.getHostConfiguration(httpClient, messageContext, targetUrl);

        @Nullable
        final HttpTransportProperties.ProxyProperties proxyProperties =
            (HttpTransportProperties.ProxyProperties) messageContext.getProperty(HTTPConstants.PROXY);

        if( proxyProperties != null ) {
            final String proxyHost = proxyProperties.getProxyHostName();
            final int proxyPort = proxyProperties.getProxyPort();

            final HttpHost proxy = new HttpHost(proxyHost, proxyPort);
            httpClient.getParams().setParameter(org.apache.http.conn.params.ConnRoutePNames.DEFAULT_PROXY, proxy);
        }

        return hostConfiguration;
    }
}
