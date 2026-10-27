package com.sap.cloud.sdk.datamodel.odata.client.request;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;

class ODataRequestListenerTest
{
    @Test
    void testListenerIsCalled()
        throws Exception
    {
        final ODataRequestListener listener = mock(ODataRequestListener.class);
        final HttpClient httpClient = mock(HttpClient.class);
        final ClassicHttpResponse httpResponse = mock(ClassicHttpResponse.class);
        final HttpEntity entity = new StringEntity("{\"d\":{\"results\":[]}}", ContentType.APPLICATION_JSON);

        when(httpClient.executeOpen(any(), any(), any())).thenReturn(httpResponse);
        when(httpResponse.getEntity()).thenReturn(entity);
        when(httpResponse.getCode()).thenReturn(200);

        final ODataRequestRead request =
            new ODataRequestRead("service", ODataResourcePath.of("entity"), "", ODataProtocol.V2);
        request.addListener(listener);

        request.execute(httpClient);

        verify(listener).listenOnRequest(any());
        verify(listener).listenOnResponse(any());
        verify(listener).listenOnExecutionFinished(any(Duration.class));
    }

    @Test
    void testListenerIsCalledOnError()
        throws Exception
    {
        final ODataRequestListener listener = mock(ODataRequestListener.class);
        final HttpClient httpClient = mock(HttpClient.class);

        when(httpClient.executeOpen(any(), any(), any())).thenThrow(new RuntimeException("error"));

        final ODataRequestRead request =
            new ODataRequestRead("service", ODataResourcePath.of("entity"), "", ODataProtocol.V2);
        request.addListener(listener);

        try {
            request.execute(httpClient);
        }
        catch( final Exception e ) {
            // expected
        }

        verify(listener).listenOnRequest(any());
        verify(listener).listenOnRequestError(any());
        verify(listener).listenOnExecutionFinished(any(Duration.class));
    }

    @Test
    void testListenerIsCalledOnParsingError()
        throws Exception
    {
        final ODataRequestListener listener = mock(ODataRequestListener.class);
        final HttpClient httpClient = mock(HttpClient.class);
        final ClassicHttpResponse httpResponse = mock(ClassicHttpResponse.class);
        // Valid JSON but invalid OData structure to trigger mapping error
        final HttpEntity entity = new StringEntity("{\"d\": \"invalid\"}", ContentType.APPLICATION_JSON);

        when(httpClient.executeOpen(any(), any(), any())).thenReturn(httpResponse);
        when(httpResponse.getEntity()).thenReturn(entity);
        when(httpResponse.getCode()).thenReturn(200);

        final ODataRequestRead request =
            new ODataRequestRead("service", ODataResourcePath.of("entity"), "", ODataProtocol.V2);
        request.addListener(listener);

        try {
            request.execute(httpClient).asList(Object.class);
        }
        catch( final Exception e ) {
            // expected
        }

        verify(listener).listenOnRequest(any());
        verify(listener).listenOnResponse(any());
        verify(listener).listenOnParsingError(any());
        verify(listener).listenOnExecutionFinished(any(Duration.class));
    }
}
