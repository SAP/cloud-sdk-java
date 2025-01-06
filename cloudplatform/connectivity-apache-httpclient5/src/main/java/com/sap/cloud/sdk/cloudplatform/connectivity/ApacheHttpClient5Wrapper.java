package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hc.client5.http.config.Configurable;
import org.apache.hc.client5.http.config.RequestConfig;
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
import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Decorates the HttpClient of a given destination. This will allow the HttpClient user to send the relative url path
 * and it will append the url configured in the destination.
 */
@Slf4j
class ApacheHttpClient5Wrapper extends CloseableHttpClient implements Configurable
{
    private final CloseableHttpClient httpClient;
    @Getter( AccessLevel.PACKAGE )
    private final HttpDestinationProperties destination;
    private final RequestConfig requestConfig;

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

    ApacheHttpClient5Wrapper(
        final CloseableHttpClient httpClient,
        final HttpDestinationProperties destination,
        final RequestConfig requestConfig )
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
        this.requestConfig = requestConfig;
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

    ApacheHttpClient5Wrapper withDestination( final HttpDestinationProperties destination )
    {
        // explicitly check the reference equality, since equals doesn't check header providers
        // this is a slight improvement, avoiding unnecessary wrapper instantiation
        // in cases where destination objects are reused / served from cache
        if( !destination.equals(this.destination) ) {
            throw new ShouldNotHappenException(
                "This method must not be used outside of updating an instance of ApacheHttpClient5Wrapper for http clients served from the ApacheHttpClient5Cache.");
        }
        if( destination == this.destination ) {
            return this;
        }
        return new ApacheHttpClient5Wrapper(httpClient, destination, requestConfig);
    }

    ClassicHttpRequest wrapRequest( final ClassicHttpRequest request )
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

    @Override
    public RequestConfig getConfig()
    {
        return requestConfig;
    }
}
