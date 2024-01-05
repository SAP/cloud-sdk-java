/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.io.IOException;
import java.net.URI;

import javax.annotation.Nonnull;

import org.apache.http.HeaderElement;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.ParseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;

import com.google.common.base.Joiner;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Decorates the HttpClient of a given destination. This will allow the HttpClient user to send the relative url path
 * and it will append the url configured in the destination.
 */
@Slf4j
class HttpClientWrapper extends CloseableHttpClient
{
    private final CloseableHttpClient httpClient;
    @Getter( AccessLevel.PACKAGE )
    private final HttpDestinationProperties destination;

    @Override
    public void close()
    {
        getConnectionManager().shutdown();
    }

    @EqualsAndHashCode
    @RequiredArgsConstructor
    static class ApacheHttpHeader implements org.apache.http.Header
    {
        @Nonnull
        private final Header header;

        @Override
        public String getName()
        {
            return header.getName();
        }

        @Override
        public String getValue()
        {
            return header.getValue();
        }

        @Override
        public HeaderElement[] getElements()
            throws ParseException
        {
            return new HeaderElement[0];
        }

        @Override
        public String toString()
        {
            return getClass().getSimpleName() + "(header=Header(name=" + getName() + ", value=(hidden)))";
        }
    }

    HttpClientWrapper( final CloseableHttpClient httpClient, final HttpDestinationProperties destination )
    {
        this.httpClient = httpClient;

        if( destination.getProxyType().contains(ProxyType.ON_PREMISE)
            && destination.getProxyConfiguration().isEmpty() ) {
            throw new DestinationAccessException(
                "Unable to create an HttpClient from the provided destination. "
                    + "The destination is supposed to target an on-premise system but lacks the correct proxy configuration. "
                    + "Please check the application logs for further details.");
        }
        this.destination = destination;
    }

    HttpUriRequest wrapRequest( final HttpUriRequest request )
    {
        final UriPathMerger merger = new UriPathMerger();
        URI requestUri = merger.merge(destination.getUri(), request.getURI());

        final String queryString = Joiner.on("&").join(QueryParamGetter.getQueryParameters(destination));
        requestUri = merger.merge(requestUri, URI.create("/?" + queryString));

        final RequestBuilder requestBuilder = RequestBuilder.copy(request);
        requestBuilder.setUri(requestUri);

        for( final Header header : destination.getHeaders(requestUri) ) {
            requestBuilder.addHeader(new ApacheHttpHeader(header));

            log
                .debug(
                    "Added HTTP header with key {} originating from a {} with target URI {} for new outbound HTTP request.",
                    header.getName(),
                    destination.getClass().getSimpleName(),
                    destination.getUri());
        }

        return requestBuilder.build();
    }

    @Override
    public CloseableHttpResponse execute( final HttpUriRequest request )
        throws IOException
    {
        final HttpUriRequest httpUriRequest = wrapRequest(request);
        return httpClient.execute(httpUriRequest);
    }

    @Override
    public CloseableHttpResponse execute( final HttpUriRequest request, final HttpContext context )
        throws IOException
    {
        final HttpUriRequest httpUriRequest = wrapRequest(request);
        return httpClient.execute(httpUriRequest, context);
    }

    @Override
    public CloseableHttpResponse execute( final HttpHost target, final HttpRequest request )
        throws IOException
    {
        return httpClient.execute(target, request);
    }

    @Override
    public <T> T execute( final HttpUriRequest request, final ResponseHandler<? extends T> responseHandler )
        throws IOException
    {
        final HttpUriRequest httpUriRequest = wrapRequest(request);
        return httpClient.execute(httpUriRequest, responseHandler);
    }

    @Override
    protected
        CloseableHttpResponse
        doExecute( final HttpHost httpHost, final HttpRequest httpRequest, final HttpContext httpContext )
            throws IOException
    {
        return httpClient.execute(httpHost, httpRequest, httpContext);
    }

    @Override
    public CloseableHttpResponse execute( final HttpHost target, final HttpRequest request, final HttpContext context )
        throws IOException
    {
        return httpClient.execute(target, request, context);
    }

    @Override
    public <T> T execute(
        final HttpUriRequest request,
        final ResponseHandler<? extends T> responseHandler,
        final HttpContext context )
        throws IOException
    {
        return httpClient.execute(wrapRequest(request), responseHandler, context);
    }

    @Override
    public <
        T>
        T
        execute( final HttpHost target, final HttpRequest request, final ResponseHandler<? extends T> responseHandler )
            throws IOException
    {
        return httpClient.execute(target, request, responseHandler);
    }

    @Override
    public <T> T execute(
        final HttpHost target,
        final HttpRequest request,
        final ResponseHandler<? extends T> responseHandler,
        final HttpContext context )
        throws IOException
    {
        return httpClient.execute(target, request, responseHandler, context);
    }

    @SuppressWarnings( "deprecation" )
    @Override
    public org.apache.http.conn.ClientConnectionManager getConnectionManager()
    {
        return httpClient.getConnectionManager();
    }

    @SuppressWarnings( "deprecation" )
    @Override
    public org.apache.http.params.HttpParams getParams()
    {
        return httpClient.getParams();
    }

}
