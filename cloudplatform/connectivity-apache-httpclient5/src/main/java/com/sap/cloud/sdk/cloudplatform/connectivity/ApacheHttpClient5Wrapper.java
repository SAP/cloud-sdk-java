/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.io.CloseMode;

import com.google.common.base.Joiner;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;

import lombok.extern.slf4j.Slf4j;

/**
 * Decorates the HttpClient of a given destination. This will allow the HttpClient user to send the relative url path
 * and it will append the url configured in the destination.
 */
@Slf4j
class ApacheHttpClient5Wrapper extends CloseableHttpClient
{
    private final CloseableHttpClient httpClient;
    private final HttpDestinationProperties destination;

    @Override
    public void close()
        throws IOException
    {
        httpClient.close();
    }

    @Override
    public void close( final CloseMode closeMode )
    {
        httpClient.close(closeMode);
    }

    ApacheHttpClient5Wrapper( final CloseableHttpClient httpClient, final HttpDestinationProperties destination )
    {
        this.httpClient = httpClient;

        if( destination.getProxyType().contains(ProxyType.ON_PREMISE)
            && destination.getProxyConfiguration().isEmpty() ) {
            throw new DestinationAccessException("""
                Unable to create an HttpClient from the provided destination. \
                The destination is supposed to target an on-premise system but lacks the correct proxy configuration. \
                Please check the application logs for further details.\
                """);
        }
        this.destination = destination;
    }

    @Override
    @SuppressWarnings( "deprecation" )
    protected
        CloseableHttpResponse
        doExecute( final HttpHost target, final ClassicHttpRequest request, final HttpContext context )
            throws IOException
    {
        return httpClient.execute(target, wrapRequest(request), context);
    }

    private ClassicHttpRequest wrapRequest( final ClassicHttpRequest request )
    {
        final UriPathMerger merger = new UriPathMerger();
        URI requestUri;
        try {
            requestUri = merger.merge(destination.getUri(), request.getUri());
        }
        catch( final URISyntaxException e ) {
            throw new IllegalStateException("Failed to merge destination URI with request URI.", e);
        }

        final String queryString = Joiner.on("&").join(QueryParamGetter.getQueryParameters(destination));
        requestUri = merger.merge(requestUri, URI.create("/?" + queryString));

        final ClassicRequestBuilder requestBuilder = ClassicRequestBuilder.copy(request);
        requestBuilder.setUri(requestUri);

        for( final Header header : destination.getHeaders(requestUri) ) {
            requestBuilder.addHeader(new BasicHeader(header.getName(), header.getValue()));

            log
                .debug(
                    "Added HTTP header with key {} originating from a {} with target URI {} for new outbound HTTP request.",
                    header.getName(),
                    destination.getClass().getSimpleName(),
                    destination.getUri());
        }

        return requestBuilder.build();
    }
}
