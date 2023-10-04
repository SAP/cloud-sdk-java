/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.soap;

import org.apache.axis2.transport.http.AbstractHTTPSender;
import org.apache.axis2.transport.http.impl.httpclient4.HTTPClient4TransportSender;

class DefaultHttpClientTransportSender extends HTTPClient4TransportSender
{
    @Override
    protected AbstractHTTPSender createHTTPSender()
    {
        return new DefaultHttpSender();
    }
}
