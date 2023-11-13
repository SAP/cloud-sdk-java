/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;

import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationProperty;
import com.sap.cloud.sdk.cloudplatform.connectivity.Header;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpEntityUtil;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;
import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;

import lombok.Getter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

/**
 * A collection of methods which are commonly called during executions of a request against an ERP system.
 *
 * @param <RequestT>
 *            The type of the request to execute.
 * @param <RequestResultT>
 *            The type of the result to return.
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Slf4j
@Deprecated
public class ErpHttpRequestExecutor<RequestT extends Request<RequestT, RequestResultT>, RequestResultT extends RequestResult<RequestT, RequestResultT>>
{
    private static final int MAX_UNCOMPRESSED_PAYLOAD_LENGTH = 1400;

    @Getter
    private final RequestExecutionMeasurements measurements = new RequestExecutionMeasurements();

    @Nonnull
    private ByteArrayEntity getBodyAsCompressedEntity( @Nonnull final String body )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException
    {
        final ByteArrayEntity entity;

        final byte[] content;
        try {
            content = body.getBytes(StandardCharsets.UTF_8.toString());
        }
        catch( final UnsupportedEncodingException e ) {
            throw new com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException(
                "Failed to to convert payload from String to UTF8 byte[].",
                e);
        }

        if( content.length > MAX_UNCOMPRESSED_PAYLOAD_LENGTH ) {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            try( GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream) ) {
                gzipOutputStream.write(content);
            }
            catch( final IOException e ) {
                throw new com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException(
                    "Failed to write to GZIP-compressed stream.",
                    e);
            }

            entity = new ByteArrayEntity(outputStream.toByteArray());
            entity.setContentEncoding("gzip");

            if( log.isInfoEnabled() ) {
                log
                    .info(
                        "Compressed length of ERP request body: "
                            + entity.getContentLength()
                            + " bytes, was "
                            + content.length
                            + " bytes.");
            }
        } else {
            entity = new ByteArrayEntity(content);
            entity.setContentEncoding(StandardCharsets.UTF_8.toString());

            if( log.isInfoEnabled() ) {
                log.info("Length of ERP request body: " + entity.getContentLength() + " bytes.");
            }
        }

        return entity;
    }

    private void handleHttpStatus(
        @Nonnull final HttpDestination destination,
        final int statusCode,
        @Nullable final String responseBody,
        @Nonnull final List<Header> responseHeaders )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        if( statusCode == HttpStatus.SC_OK ) {
            if( log.isTraceEnabled() ) {
                log
                    .trace(
                        "Request execution finished successfully. Response body: "
                            + responseBody
                            + " Headers: "
                            + getNonSensitiveHeadersAsString(responseHeaders)
                            + ".");
            }
        } else {
            handleHttpError(destination, statusCode, responseBody, responseHeaders);
        }
    }

    private void handleHttpError(
        @Nonnull final HttpDestination destination,
        final int statusCode,
        @Nullable final String responseBody,
        @Nonnull final List<Header> responseHeaders )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        switch( statusCode ) {
            case HttpStatus.SC_UNAUTHORIZED:
                handleUnauthorized(responseBody, responseHeaders);
                return;

            case HttpStatus.SC_FORBIDDEN:
                handleForbidden(responseBody, responseHeaders);
                return;

            case HttpStatus.SC_INTERNAL_SERVER_ERROR:
                handleInternalServerError(responseBody, responseHeaders);
                return;

            case HttpStatus.SC_SERVICE_UNAVAILABLE:
                handleServiceUnavailableError(destination, responseBody, responseHeaders);
                return;

            case HttpStatus.SC_BAD_GATEWAY:
                handleBadGateway(responseBody, responseHeaders);
                return;

            default: {
                final String message =
                    "Request execution failed with status code "
                        + statusCode
                        + ". Response body: "
                        + responseBody
                        + " Headers: "
                        + getNonSensitiveHeadersAsString(responseHeaders)
                        + ".";

                throw new com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException(message);
            }
        }
    }

    private void handleUnauthorized( @Nullable final String responseBody, @Nonnull final List<Header> responseHeaders )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.LogonErrorException
    {
        final String message =
            HttpStatus.SC_UNAUTHORIZED
                + " Unauthorized. The connection attempt was refused. Response body: "
                + responseBody
                + " Headers: "
                + getNonSensitiveHeadersAsString(responseHeaders)
                + ".";

        throw new com.sap.cloud.sdk.s4hana.connectivity.exception.LogonErrorException(message);
    }

    @Nullable
    private String getMissingAuthorization( @Nonnull final List<Header> responseHeaders )
    {
        for( final Header header : responseHeaders ) {
            if( header.getName().equals("failed-authorization-object") ) {
                return header.getValue();
            }
        }
        return null;
    }

    private void handleForbidden( @Nullable final String responseBody, @Nonnull final List<Header> responseHeaders )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.AccessDeniedException
    {
        final String prefix = HttpStatus.SC_FORBIDDEN + " Forbidden. ";

        if( responseBody != null && responseBody.startsWith("CX_FINS_MAP_NO_AUTH_QUERY_EXEC") ) {
            @Nullable
            final String missingAuthorization = getMissingAuthorization(responseHeaders);

            throw com.sap.cloud.sdk.s4hana.connectivity.exception.AccessDeniedException
                .raiseMissingAuthorizations(null, missingAuthorization);
        }

        final String message =
            prefix
                + "Failed to establish a trusted connection to the ERP. This may be caused by "
                + "a misconfiguration of the SAP Cloud Connector or a misconfiguration "
                + "of the trust certificate. Response body: "
                + responseBody
                + " Headers: "
                + getNonSensitiveHeadersAsString(responseHeaders)
                + ".";

        throw new com.sap.cloud.sdk.s4hana.connectivity.exception.AccessDeniedException(message);
    }

    private
        void
        handleInternalServerError( @Nullable final String responseBody, @Nonnull final List<Header> responseHeaders )
            throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        final String prefix = HttpStatus.SC_INTERNAL_SERVER_ERROR + " Internal Server Error. ";

        if( responseBody != null && responseBody.contains("ICF") && responseBody.contains("HCPMAPBM") ) {
            final String message =
                prefix
                    + "Failed to invoke ICF service. Does the user have authorization HCPMAPBM? "
                    + "Response body: "
                    + responseBody
                    + " Headers: "
                    + getNonSensitiveHeadersAsString(responseHeaders)
                    + ".";

            throw new com.sap.cloud.sdk.s4hana.connectivity.exception.AccessDeniedException(message);
        }

        final String message =
            prefix
                + "Request execution failed with unexpected error. Response body: "
                + responseBody
                + " Headers: "
                + getNonSensitiveHeadersAsString(responseHeaders)
                + ".";

        throw new com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException(message);
    }

    private void handleServiceUnavailableError(
        @Nonnull final HttpDestination destination,
        @Nullable final String responseBody,
        @Nonnull final List<Header> responseHeaders )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        if( responseBody != null && responseBody.contains("No tunnels subscribed for tunnelId") ) {
            final String message =
                HttpStatus.SC_SERVICE_UNAVAILABLE
                    + " Service Unavailable. Failed to connect to ERP system. "
                    + "Please check the configuration of destination '"
                    + destination.get(DestinationProperty.NAME).getOrElse("")
                    + "'. In an on-premise setup, ensure that the cloud connector is connected.";

            throw new com.sap.cloud.sdk.s4hana.connectivity.exception.CloudConnectorException(
                HttpStatus.SC_SERVICE_UNAVAILABLE,
                message);
        } else {
            handleInternalServerError(responseBody, responseHeaders);
        }
    }

    private void handleBadGateway( @Nullable final String responseBody, @Nonnull final List<Header> responseHeaders )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        if( responseBody != null && responseBody.contains("Unable to open connection to backend system") ) {
            final String message =
                HttpStatus.SC_BAD_GATEWAY
                    + " Bad Gateway. Cloud connector failed to open connection to backend system. "
                    + "Is the internal host configured correc   tly? Response body: "
                    + responseBody
                    + " Headers: "
                    + getNonSensitiveHeadersAsString(responseHeaders)
                    + ".";

            throw new com.sap.cloud.sdk.s4hana.connectivity.exception.CloudConnectorException(
                HttpStatus.SC_BAD_GATEWAY,
                message);
        } else {
            handleInternalServerError(responseBody, responseHeaders);
        }
    }

    /**
     * Converts the given headers to a String while omitting sensitive headers to avoid leaking them to logs.
     */
    @Nonnull
    private String getNonSensitiveHeadersAsString( @Nonnull final Collection<Header> headers )
    {
        final StringBuilder sb = new StringBuilder();
        final Iterator<Header> headerIt = headers.iterator();

        while( headerIt.hasNext() ) {
            final Header header = headerIt.next();

            final String name = header.getName();
            String value = header.getValue();

            if( "set-cookie".equalsIgnoreCase(name) || "authorization".equalsIgnoreCase(name) ) {
                value = "(hidden)";
            }

            sb.append(name).append(": ").append(value).append(headerIt.hasNext() ? ", " : "");
        }

        return sb.toString();
    }

    /**
     * Serializes the given request, executes it, and the deserializes the response.
     *
     * @param destination
     *            The {@code HttpDestination} of this call.
     * @param request
     *            The {@code Request} to be executed.
     * @param requestSerializer
     *            The {@code RequestSerializer} to be used to write the request and read the response.
     * @return The body of the response received by the given request.
     * @throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException
     *             If the request could not be serialized
     * @throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
     *             If any Exception occured during execution of the request.
     * @throws DestinationNotFoundException
     *             If the Destination cannot be found.
     * @throws DestinationAccessException
     *             If the destination is not of type DestinationType.HTTP or there is an issue while accessing
     *             destination information.
     */
    @Nonnull
    public RequestResultT execute(
        @Nonnull final HttpDestination destination,
        @Nonnull final RequestT request,
        @Nonnull final RequestSerializer<RequestT, RequestResultT> requestSerializer )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException,
            com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException,
            DestinationNotFoundException,
            DestinationAccessException
    {
        measurements.resetMeasurements();
        measurements.setBeginTotal(System.nanoTime());

        try {
            final SerializedRequest<RequestT> serializedRequest = serializeRequest(request, requestSerializer);
            final String responseBody = execute(destination, serializedRequest);
            return deserializeRequest(request, requestSerializer, responseBody);
        }
        finally {
            measurements.setEndTotal(System.nanoTime());
        }
    }

    @Nonnull
    private SerializedRequest<RequestT> serializeRequest(
        @Nonnull final RequestT request,
        @Nonnull final RequestSerializer<RequestT, RequestResultT> requestSerializer )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException,
            DestinationNotFoundException,
            DestinationAccessException
    {
        final long beginBuildReq = System.nanoTime();
        try {
            return requestSerializer.serialize(request);
        }
        finally {
            final long endBuildReq = System.nanoTime();
            measurements.addBuildRequestDuration(Duration.ofNanos(endBuildReq - beginBuildReq));
        }
    }

    @Nonnull
    private RequestResultT deserializeRequest(
        @Nonnull final RequestT request,
        @Nonnull final RequestSerializer<RequestT, RequestResultT> requestSerializer,
        @Nonnull final String responseBody )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException,
            DestinationNotFoundException,
            DestinationAccessException
    {
        final long beginParseResp = System.nanoTime();
        try {
            final SerializedRequestResult<RequestT> serializedRequestResult =
                new SerializedRequestResult<>(request, responseBody);
            return requestSerializer.deserialize(serializedRequestResult);
        }
        finally {
            final long endParseResp = System.nanoTime();
            measurements.addParseResponseDuration(Duration.ofNanos(endParseResp - beginParseResp));
        }
    }

    @Nonnull
    private RequestMethod getRequestMethod( @Nonnull final SerializedRequest<RequestT> serializedRequest )
    {
        return serializedRequest.getRequestMethod();
    }

    /**
     * Get the request URI.
     *
     * @param destination
     *            The destination which is used for the HTTP request.
     * @param serializedRequest
     *            The serialized request payload.
     * @return The target request URI.
     */
    @Nonnull
    protected URI getRequestUri(
        @Nonnull final HttpDestination destination,
        @Nonnull final SerializedRequest<RequestT> serializedRequest )
    {
        return new ServiceUriBuilder().build(destination.getUri(), serializedRequest.getRequestPath());
    }

    private HttpUriRequest newRequest( final RequestMethod requestMethod, final URI requestUri )
    {
        switch( requestMethod ) {
            case GET:
                return new HttpGet(requestUri);
            case HEAD:
                return new HttpHead(requestUri);
            case POST:
                return new HttpPost(requestUri);
            case PUT:
                return new HttpPut(requestUri);
            case PATCH:
                return new HttpPatch(requestUri);
            case DELETE:
                return new HttpDelete(requestUri);
            case OPTIONS:
                return new HttpOptions(requestUri);
            default:
                throw new ShouldNotHappenException("Unsupported request method: " + requestMethod + ".");
        }
    }

    private HttpUriRequest newRequest(
        @Nonnull final RequestMethod requestMethod,
        @Nonnull final URI requestUri,
        @Nonnull final RequestBodyWithHeader bodyWithHeader )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException
    {
        final long beginBuildRequest = System.nanoTime();
        try {
            final HttpUriRequest request = newRequest(requestMethod, requestUri);

            if( request instanceof HttpEntityEnclosingRequest ) {
                ((HttpEntityEnclosingRequest) request).setEntity(getBodyAsCompressedEntity(bodyWithHeader.body));
            }

            request.setHeader(HttpHeaders.USER_AGENT, "sap-cloud-sdk");
            request.setHeader(HttpHeaders.ACCEPT_ENCODING, "gzip");

            for( final Header header : bodyWithHeader.headers ) {
                request.setHeader(header.getName(), header.getValue());
            }

            if( log.isTraceEnabled() ) {
                final Thread currentThread = Thread.currentThread();

                log
                    .trace(
                        "Successfully prepared HTTP request for request execution (thread: "
                            + currentThread
                            + ", threat id: "
                            + currentThread.getId()
                            + ") URI: "
                            + requestUri
                            + " Body: "
                            + bodyWithHeader.body
                            + " Headers: "
                            + getNonSensitiveHeadersAsString(bodyWithHeader.headers)
                            + ".");
            }

            return request;
        }
        finally {
            measurements.addBuildRequestDuration(Duration.ofNanos(System.nanoTime() - beginBuildRequest));
        }
    }

    /**
     * Logs the read access attempt of the given {@code request} for the given {@code destination}.
     *
     * @param request
     *            The request that attempted to read data.
     * @param destination
     *            The destination that was used for the read attempt.
     */
    protected void logReadAccessAttempt( final Request<?, ?> request, final HttpDestination destination )
    {
        @Nullable
        final String readAccessData = request.getReadAccessData();

        if( readAccessData != null ) {
            log
                .info(
                    "Read access attempt from class: {} for destination: {}, sap-client: {}, constructed by {}, with data: {}.",
                    request.getClass().getSimpleName(),
                    destination.get(DestinationProperty.NAME).get(),
                    destination.get(DestinationProperty.SAP_CLIENT).get(),
                    request.getConstructedByMethod(),
                    readAccessData);
        }
    }

    private String getRequestExecutionFailedMessage( final Request<?, ?> request )
    {
        return request.getClass().getSimpleName()
            + " "
            + request.getConstructedByMethod()
            + " failed ["
            + measurements.getMeasurementsString()
            + "]";
    }

    /**
     * Executes the given {@code serializedRequest} as a {@code HttpUriRequest}, returning the body of the
     * {@code HttpResponse} received.
     *
     * @param destination
     *            The {@code HttpDestination} of this call.
     * @param serializedRequest
     *            The {@code SerializedRequest} to execute.
     * @return The body of the response received by the given request.
     * @throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException
     *             If the request could not be serialized.
     * @throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
     *             If any Exception occured during execution of the request.
     * @throws DestinationNotFoundException
     *             If the Destination cannot be found.
     * @throws DestinationAccessException
     *             If the destination is not of type DestinationType.HTTP or there is an issue while accessing
     *             destination information.
     */
    @Nonnull
    public String execute(
        @Nonnull final HttpDestination destination,
        @Nonnull final SerializedRequest<RequestT> serializedRequest )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException,
            com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException,
            DestinationNotFoundException,
            DestinationAccessException
    {
        final RequestT request = serializedRequest.getRequest();

        final HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);

        final RequestMethod requestMethod = getRequestMethod(serializedRequest);
        final URI requestUri = getRequestUri(destination, serializedRequest);

        // resolve request body and header for potentially signed request body
        final RequestBodyWithHeader bodyWithHeader = getRequestBodyWithHeader(destination, serializedRequest);
        final HttpUriRequest uriRequest = newRequest(requestMethod, requestUri, bodyWithHeader);

        HttpResponse response;
        final List<Header> responseHeaders = new ArrayList<>();
        final String responseBody;

        final long beginExecute = System.nanoTime();
        try {
            if( log.isDebugEnabled() ) {
                log
                    .debug(
                        "Executing "
                            + request.getClass().getSimpleName()
                            + " constructed by: "
                            + request.getConstructedByMethod()
                            + ".");
            }

            logReadAccessAttempt(request, destination);
            response = httpClient.execute(uriRequest);

            for( final org.apache.http.Header header : response.getAllHeaders() ) {
                responseHeaders.add(new Header(header.getName(), header.getValue()));
            }

            responseBody = HttpEntityUtil.getResponseBody(response);
        }
        catch( final com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException e ) {
            if( log.isDebugEnabled() ) {
                log.debug(getRequestExecutionFailedMessage(request), e);
            }
            throw e;
        }
        catch( final Exception e ) {
            final String message = getRequestExecutionFailedMessage(request);
            throw new com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException(message, e);
        }
        finally {
            measurements.addExecuteRequestDuration(Duration.ofNanos(System.nanoTime() - beginExecute));
        }

        if( responseBody == null ) {
            throw new com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException(
                "Failed to execute request: no body returned in response.");
        }

        handleHttpStatus(destination, response.getStatusLine().getStatusCode(), responseBody, responseHeaders);
        return responseBody;
    }

    /**
     * Returns a wrapper object which encapsulates the HTTP request body and headers. This method can be overridden to
     * manipulate the request before submitting, e.g. signing queries, adding timestamps.
     *
     * @param destination
     *            The {@code HttpDestination} of this call.
     * @param request
     *            The {@code Request} to be executed.
     * @return The request body with header.
     */
    @Nonnull
    protected RequestBodyWithHeader getRequestBodyWithHeader(
        final HttpDestination destination,
        @Nonnull final SerializedRequest<RequestT> request )
    {
        return new RequestBodyWithHeader(request.getRequestHeaders(), request.getRequestBody());
    }

    /**
     * A helper class to wrap request body and headers.
     */
    @Value
    static final class RequestBodyWithHeader
    {
        private final Collection<Header> headers;
        private final String body;
    }
}
