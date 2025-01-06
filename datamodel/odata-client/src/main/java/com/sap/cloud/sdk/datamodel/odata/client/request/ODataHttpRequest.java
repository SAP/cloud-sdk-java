package com.sap.cloud.sdk.datamodel.odata.client.request;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.net.URI;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.entity.StringEntity;

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
        final StringEntity requestBody = new StringEntity(json, UTF_8);
        requestBody.setContentType("application/json");
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
    private HttpResponse requestResource( @Nonnull final Function<URI, HttpRequestBase> requestCreator )
    {
        final HttpRequestBase httpRequest = requestCreator.apply(getUri());

        odataRequest.getHeaders().forEach(( k, values ) -> values.forEach(v -> httpRequest.addHeader(k, v)));

        // add optional request body
        if( httpRequest instanceof HttpEntityEnclosingRequest ) {
            if( requestBody != null ) {
                ((HttpEntityEnclosingRequest) httpRequest).setEntity(requestBody);
            } else {
                log.warn("The HTTP request {} was expecting an entity, but none was provided.", httpRequest);
            }
        }

        odataRequest.getListeners().forEach(v -> v.listenOnRequest(httpRequest));

        try {
            return httpClient.execute(httpRequest);
        }
        catch( final ClientProtocolException e ) {
            log.debug("Connection could not be established.", e);
            throw new ODataConnectionException(
                this.odataRequest,
                httpRequest,
                "Connection could not be established.",
                e);
        }
        catch( final ConnectionPoolTimeoutException e ) {
            log.debug("Connection pool timed out.", e);
            throw new ODataConnectionException(
                this.odataRequest,
                httpRequest,
                """
                    Time out occurred because of a probable connection leak. Please execute your request with try-with-resources to ensure resources are properly closed.\
                    If you are using the OData client instead to execute your request, explicitly consume the entity of the associated HttpResponse using EntityUtils.consume(httpEntity)\
                    """,
                e);

        }
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
    HttpResponse requestGet()
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
    HttpResponse requestPost()
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
    HttpResponse requestPatch()
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
    HttpResponse requestPut()
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
    HttpResponse requestDelete()
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
