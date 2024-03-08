/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import com.google.common.annotations.Beta;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataDeserializationException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataResponseException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceErrorException;

import io.vavr.Lazy;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * OData request result for reading entities.
 */
@Slf4j
public class ODataRequestResultMultipartGeneric
    implements
    ODataRequestResultMultipart,
    ODataRequestResult,
    AutoCloseable
{
    @Getter( AccessLevel.PRIVATE )
    @Nonnull
    private final ODataRequestBatch batchRequest;

    @Getter
    @Nonnull
    private final HttpResponse httpResponse;

    @Nonnull
    private final Lazy<Try<List<List<HttpResponse>>>> batchResponses = Lazy.of(this::loadBatchResponses);

    @Nonnull
    private final List<Runnable> closeHandlers = new ArrayList<>();

    /**
     * Create an instance of OData request result for multipart/mixed responses.
     *
     * @param oDataRequest
     *            The original OData request instance.
     * @param httpResponse
     *            The native HTTP response object.
     */
    ODataRequestResultMultipartGeneric(
        @Nonnull final ODataRequestBatch oDataRequest,
        @Nonnull final HttpResponse httpResponse )
    {
        batchRequest = oDataRequest;
        this.httpResponse = httpResponse;
    }

    /**
     * Get the original {@link ODataRequestBatch batch request} that was used for running the OData request.
     *
     * @return The batch request this
     */
    @Nonnull
    @Override
    public ODataRequestBatch getODataRequest()
    {
        return batchRequest;
    }

    /**
     * Gets the {@link ODataRequestResultGeneric} for the given {@code request}.
     *
     * @param request
     *            The request for which the result should be returned.
     * @return The result for the given {@code request}.
     * @throws ODataResponseException
     *             When the OData batch response cannot be parsed or HTTP response is not healthy.
     * @throws IllegalArgumentException
     *             When the provided request reference could not be found in the original batch request.
     * @throws ODataServiceErrorException
     *             When the response contains an OData error message according to specification.
     */
    @Nonnull
    @Override
    public ODataRequestResultGeneric getResult( @Nonnull final ODataRequestGeneric request )
        throws ODataResponseException,
            IllegalArgumentException
    {
        @Nullable
        final Tuple2<Integer, Integer> responsePosition = ODataRequestBatch.getBatchItemPosition(batchRequest, request);
        if( responsePosition == null ) {
            throw new IllegalArgumentException(
                "Incorrect API usage. Please pass the original OData request reference that was handled as batch request item.");
        }

        log.debug("Looking for request {} in batch response at position {}", request, responsePosition);
        final List<List<HttpResponse>> batchResponseItems = getBatchedResponses();
        if( responsePosition._1() >= batchResponseItems.size() ) {
            String msg = "Unable to extract batch response item at position %s. The response contains only %s items.";
            msg = String.format(msg, responsePosition._1() + 1, batchResponseItems.size());
            throw new ODataResponseException(batchRequest, httpResponse, msg, null);
        }
        final List<HttpResponse> subResponses = batchResponseItems.get(responsePosition._1());

        final boolean isSingleResponse = responsePosition._2() == null || responsePosition._2() >= subResponses.size();
        final HttpResponse response = subResponses.get(isSingleResponse ? 0 : responsePosition._2());

        if( response == null ) {
            final String msg = "Illegal payload for " + batchRequest.getProtocol() + " batch response item.";
            throw new ODataDeserializationException(batchRequest, httpResponse, msg, null);
        }

        final ODataRequestResultGeneric result = new ODataRequestResultGeneric(request, response);
        ODataHealthyResponseValidator.requireHealthyResponse(result);
        return result;
    }

    /**
     * Get the multi-part segments as raw HTTP response object. Response objects of same changesets are grouped.
     * <p>
     * <b>Please note:</b> The returned list is lazily evaluated. The underlying HTTP response entity is being parsed,
     * upon list access.
     *
     * @return The virtual HTTP response objects.
     */
    @Nonnull
    public List<List<HttpResponse>> getBatchedResponses()
    {
        return batchResponses
            .get()
            .getOrElseThrow(
                e -> new ODataResponseException(
                    getBatchRequest(),
                    getHttpResponse(),
                    "Failed to read " + batchRequest.getProtocol() + " batch response.",
                    e));
    }

    @Nonnull
    private Try<List<List<HttpResponse>>> loadBatchResponses()
    {
        return Try.of(() -> {
            @SuppressWarnings( "resource" ) // resource will be registered in the close handlers
            final MultipartParser parser = MultipartParser.ofHttpResponse(getHttpResponse());
            closeHandlers.add(parser::close);
            return parser.toList(MultipartHttpResponse::ofHttpContent);
        });
    }

    /**
     * Closes the underlying HTTP response entity.
     *
     * @since 5.5.0
     */
    @Beta
    @Override
    public void close()
    {
        // close HTTP entity
        final HttpEntity entity = getHttpResponse().getEntity();
        Try.run(() -> EntityUtils.consume(entity)).onFailure(e -> log.warn("Failed to consume the HTTP entity.", e));

        // close any additional registered handler
        closeHandlers.forEach(Runnable::run);
    }
}
