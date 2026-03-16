package com.sap.cloud.sdk.datamodel.odata.client.request;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.net.URI;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.HttpClient;
//import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase; // instead of HttpRequestBase
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
//import org.apache.http.HttpEntityEnclosingRequest; use instead:
import org.apache.hc.core5.http.HttpEntityContainer;
//import org.apache.http.conn.ConnectionPoolTimeoutException; //
import org.apache.hc.core5.http.io.entity.StringEntity;

import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataConnectionException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataRequestException;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor( access = AccessLevel.PRIVATE )
@Slf4j
class ODataHttpRequest
{
    @Nonnull
    private final ODataRequestGeneric odataRequest;

    @Nonnull
    private final HttpClient httpClient;

    @Nullable
    private final HttpEntity requestBody;

    static
        ODataHttpRequest
        withoutBody( @Nonnull final ODataRequestGeneric requestGeneric, @Nonnull final HttpClient httpClient )
    {
        return forHttpEntity(requestGeneric, httpClient, null);
    }

    static ODataHttpRequest forBodyJson(
        @Nonnull final ODataRequestGeneric requestGeneric,
        @Nonnull final HttpClient httpClient,
        @Nonnull final String json )
    {
        final StringEntity requestBody = new StringEntity(json, ContentType.APPLICATION_JSON);
        return forHttpEntity(requestGeneric, httpClient, requestBody);
    }

    static ODataHttpRequest forBodyText(
        @Nonnull final ODataRequestGeneric requestGeneric,
        @Nonnull final HttpClient httpClient,
        @Nonnull final String text )
    {
        return forHttpEntity(requestGeneric, httpClient, new StringEntity(text, UTF_8));
    }

    static ODataHttpRequest forHttpEntity(
        @Nonnull final ODataRequestGeneric requestGeneric,
        @Nonnull final HttpClient httpClient,
        @Nullable final HttpEntity httpEntity )
    {
        return new ODataHttpRequest(requestGeneric, httpClient, httpEntity);
    }

    /**
     * Perform the request the remote resource.
     *
     * @param requestCreator
     *            The factory for HTTP requests.
     * @throws ODataRequestException
     *             When the request URI could not constructed.
     * @throws ODataConnectionException
     *             When an error occurred while handling the HTTP connection.
     * @return The HTTP response.
     */
    @Nonnull
    private ClassicHttpResponse requestResource( @Nonnull final Function<URI, HttpUriRequestBase> requestCreator )
    {
        final HttpUriRequestBase httpRequest = requestCreator.apply(getUri());

        odataRequest.getHeaders().forEach(( k, values ) -> values.forEach(v -> httpRequest.addHeader(k, v)));

        // add optional request body
        if( httpRequest instanceof HttpEntityContainer ) {
            if( requestBody != null ) {
                ((HttpEntityContainer) httpRequest).setEntity(requestBody);
            } else {
                log.warn("The HTTP request {} was expecting an entity, but none was provided.", httpRequest);
            }
        }

        odataRequest.getListeners().forEach(v -> v.listenOnRequest(httpRequest));

        try {
            return httpClient.execute(httpRequest, response -> response); // JONAS: fix this
        }
        catch( final ClientProtocolException e ) {
            log.debug("Connection could not be established.", e);
            throw new ODataConnectionException(
                this.odataRequest,
                httpRequest,
                "Connection could not be established.",
                e);
        }
        // JONAS: find out which exception to catch here
        //        catch( final ConnectionPoolTimeoutException e ) {
        //            log.debug("Connection pool timed out.", e);
        //            throw new ODataConnectionException(
        //                this.odataRequest,
        //                httpRequest,
        //                """
        //                    Time out occurred because of a probable connection leak. Please execute your request with try-with-resources to ensure resources are properly closed.\
        //                    If you are using the OData client instead to execute your request, explicitly consume the entity of the associated ClassicHttpResponse using EntityUtils.consume(httpEntity)\
        //                    """,
        //                e);
        //
        //        }
        catch( final IOException e ) {
            log.debug("Connection was aborted.", e);
            throw new ODataConnectionException(this.odataRequest, httpRequest, "Connection was aborted.", e);
        }
        catch( final Exception e ) {
            log.debug("Connection failed.", e);
            throw new ODataConnectionException(this.odataRequest, httpRequest, "Connection failed.", e);
        }
    }

    /**
     * Perform a GET request.
     *
     * @return The HTTP response.
     * @throws ODataRequestException
     *             When the request URI could not constructed.
     * @throws ODataConnectionException
     *             When an error occurred while handling the HTTP connection.
     */
    @Nonnull
    ClassicHttpResponse requestGet()
    {
        return requestResource(HttpGet::new);
    }

    /**
     * Perform a POST request.
     *
     * @return The HTTP response.
     * @throws ODataRequestException
     *             When the request URI could not constructed.
     * @throws ODataConnectionException
     *             When an error occurred while handling the HTTP connection.
     */
    @Nonnull
    ClassicHttpResponse requestPost()
    {
        return requestResource(HttpPost::new);
    }

    /**
     * Perform a PATCH request.
     *
     * @return The HTTP response.
     * @throws ODataRequestException
     *             When the request URI could not constructed.
     * @throws ODataConnectionException
     *             When an error occurred while handling the HTTP connection.
     */
    @Nonnull
    ClassicHttpResponse requestPatch()
    {
        return requestResource(HttpPatch::new);
    }

    /**
     * Perform a PUT request.
     *
     * @return The HTTP response.
     * @throws ODataRequestException
     *             When the request URI could not constructed.
     * @throws ODataConnectionException
     *             When an error occurred while handling the HTTP connection.
     */
    @Nonnull
    ClassicHttpResponse requestPut()
    {
        return requestResource(HttpPut::new);
    }

    /**
     * Perform a DELETE request.
     *
     * @return The HTTP response.
     * @throws ODataRequestException
     *             When the request URI could not constructed.
     * @throws ODataConnectionException
     *             When an error occurred while handling the HTTP connection.
     */
    @Nonnull
    ClassicHttpResponse requestDelete()
    {
        return requestResource(HttpDelete::new);
    }

    /**
     * Constructs an URI for the given request.
     *
     * @return The URI
     */
    private URI getUri()
    {
        return odataRequest.getRelativeUri();
    }
}
