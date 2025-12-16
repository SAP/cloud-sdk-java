package com.sap.cloud.sdk.s4hana.connectivity.soap;

import org.apache.axis2.transport.http.HTTPSender;
import org.apache.axis2.transport.http.impl.httpclient4.HTTPClient4TransportSender;

class DefaultHttpClientTransportSender extends HTTPClient4TransportSender
{
    @Override
    protected HTTPSender createHTTPSender()
    {
        return new DefaultHttpSender();
    }
}
