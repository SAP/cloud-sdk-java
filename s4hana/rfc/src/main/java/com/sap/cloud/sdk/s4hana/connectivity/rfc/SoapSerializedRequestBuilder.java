/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import java.util.List;

import org.apache.http.HttpHeaders;

import com.google.common.collect.Lists;
import com.sap.cloud.sdk.cloudplatform.connectivity.Header;

import lombok.Data;

@Data
@SuppressWarnings( "deprecation" )
class SoapSerializedRequestBuilder<RequestT extends AbstractRemoteFunctionRequest<RequestT, RequestResultT>, RequestResultT extends AbstractRemoteFunctionRequestResult<RequestT, RequestResultT>>
{
    private final RequestT request;
    private final String requestBody;

    private List<Header> getHeaders()
    {
        final List<Header> headers = Lists.newLinkedList();
        headers.addAll(request.getCustomHttpHeaders());
        headers.add(new Header(HttpHeaders.CONTENT_TYPE, "text/xml"));
        headers
            .add(
                new Header(
                    "SOAPAction",
                    "urn:sap-com:document:sap:soap:functions:mc-style:_--3"
                        + AbapToSoapNameConverter.abapFunctionNameToSoapServiceName(request.getFunctionName())
                        + ":"
                        + AbapToSoapNameConverter.abapFunctionNameToSoapMessageName(request.getFunctionName())));
        return headers;
    }

    com.sap.cloud.sdk.s4hana.connectivity.SerializedRequest<RequestT> build()
    {
        return new com.sap.cloud.sdk.s4hana.connectivity.SerializedRequest<>(
            request,
            com.sap.cloud.sdk.s4hana.connectivity.RequestMethod.POST,
            "/sap/bc/srt/scs_ext/sap/"
                + AbapToSoapNameConverter.abapFunctionNameToSoapServiceName(request.getFunctionName()),
            getHeaders(),
            requestBody);
    }
}
