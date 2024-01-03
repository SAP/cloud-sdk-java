/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import com.google.common.annotations.Beta;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataResponseException;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestGeneric;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultMultipartGeneric;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Generic OData service response wrapper for Batch response.
 *
 */
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor( staticName = "of", access = AccessLevel.PACKAGE )
@Slf4j
public final class BatchResponse implements AutoCloseable
{
    @Nonnull
    private final ODataRequestResultMultipartGeneric result;

    @Nonnull
    private final Map<RequestBuilder<?>, ODataRequestGeneric> requestMapping;

    /**
     * Static factory method to convert from generic response to typed response.
     *
     * @param response
     *            The generic response that should be converted.
     * @param initialRequest
     *            The initial BatchRequest
     * @return The typed (high-level) BatchResponse object.
     */
    @Nonnull
    public static BatchResponse of(
        @Nonnull final ODataRequestResultMultipartGeneric response,
        @Nonnull final BatchRequestBuilder initialRequest )
    {
        return BatchResponse.of(response, initialRequest.getRequestMapping());
    }

    /**
     * Get the response status code.
     *
     * @return The integer representation of the HTTP status code.
     */
    public int getResponseStatusCode()
    {
        return result.getHttpResponse().getStatusLine().getStatusCode();
    }

    /**
     * Get the response headers.
     *
     * @return The headers of the HTTP status code.
     */
    @Nonnull
    public Map<String, Iterable<String>> getResponseHeaders()
    {
        return result.getAllHeaderValues();
    }

    /**
     * Extract the batch item result for the provided OData request.
     *
     * @param operation
     *            The OData operation that was used in the OData batch request.
     * @param <T>
     *            The generic entity type.
     * @return The list of entities as result of the request.
     * @throws ODataResponseException
     *             When the OData batch response cannot be parsed.
     * @throws IllegalArgumentException
     *             When the provided request reference could not be found in the original batch request.
     */
    @Nonnull
    public <T extends VdmEntity<?>> List<T> getReadResult( @Nonnull final GetAllRequestBuilder<T> operation )
    {
        final ODataRequestResultGeneric clientResult = result.getResult(requestMapping.get(operation));
        return clientResult.asList(operation.getEntityClass());
    }

    /**
     * Extract the batch item result for the provided OData request.
     *
     * @param operation
     *            The OData operation that was used in the OData batch request.
     * @param <T>
     *            The generic object type.
     * @return The list of objects as result of the request.
     * @throws ODataResponseException
     *             When the OData batch response cannot be parsed.
     * @throws IllegalArgumentException
     *             When the provided request reference could not be found in the original batch request.
     */
    @Nonnull
    public <T> List<T> getReadResult( @Nonnull final CollectionValueFunctionRequestBuilder<T> operation )
    {
        final ODataRequestResultGeneric clientResult = result.getResult(requestMapping.get(operation));
        return clientResult.asList(operation.getResultClass());
    }

    /**
     * Extract the batch item result for the provided OData request.
     *
     * @param operation
     *            The OData operation that was used in the OData batch request.
     * @param <T>
     *            The generic entity type.
     * @return The list of entities as result of the request.
     * @throws ODataResponseException
     *             When the OData batch response cannot be parsed.
     * @throws IllegalArgumentException
     *             When the provided request reference could not be found in the original batch request.
     */
    @Nonnull
    public <T extends VdmEntity<?>> T getReadResult( @Nonnull final GetByKeyRequestBuilder<T> operation )
    {
        final ODataRequestResultGeneric clientResult = result.getResult(requestMapping.get(operation));
        return clientResult.as(operation.getEntityClass());
    }

    /**
     * Extract the batch item result for the provided OData request.
     *
     * @param operation
     *            The OData operation that was used in the OData batch request.
     * @param <T>
     *            The generic object type.
     * @return The object as result of the request.
     * @throws ODataResponseException
     *             When the OData batch response cannot be parsed.
     * @throws IllegalArgumentException
     *             When the provided request reference could not be found in the original batch request.
     */
    @Nonnull
    public <T> T getReadResult( @Nonnull final SingleValueFunctionRequestBuilder<T> operation )
    {
        final ODataRequestResultGeneric clientResult = result.getResult(requestMapping.get(operation));
        return clientResult.as(operation.getResultClass());
    }

    /**
     * Extract the batch item result for the provided OData request.
     *
     * @param operation
     *            The OData operation that was used in the OData batch request.
     * @param <EntityT>
     *            The generic entity type.
     * @return The generic modification response wrapper object as result of the modification request.
     * @throws ODataResponseException
     *             When the OData batch response cannot be parsed.
     * @throws IllegalArgumentException
     *             When the provided request reference could not be found in the original batch request.
     */
    @Nonnull
    public <EntityT extends VdmEntity<?>> ModificationResponse<EntityT> getModificationResult(
        @Nonnull final CreateRequestBuilder<EntityT> operation )
    {
        final ODataRequestResultGeneric clientResult = result.getResult(requestMapping.get(operation));
        return ModificationResponse.of(clientResult, operation.getEntity());
    }

    /**
     * Extract the batch item result for the provided OData request.
     *
     * @param operation
     *            The OData operation that was used in the OData batch request.
     * @param <EntityT>
     *            The generic entity type.
     * @return The generic modification response wrapper object as result of the modification request.
     * @throws ODataResponseException
     *             When the OData batch response cannot be parsed.
     * @throws IllegalArgumentException
     *             When the provided request reference could not be found in the original batch request.
     */
    @Nonnull
    public <EntityT extends VdmEntity<?>> ModificationResponse<EntityT> getModificationResult(
        @Nonnull final UpdateRequestBuilder<EntityT> operation )
    {
        final ODataRequestResultGeneric clientResult = result.getResult(requestMapping.get(operation));
        return ModificationResponse.of(clientResult, operation.getEntity());
    }

    /**
     * Extract the batch item result for the provided OData request.
     *
     * @param operation
     *            The OData operation that was used in the OData batch request.
     * @param <EntityT>
     *            The generic entity type.
     * @return The generic modification response wrapper object as result of the modification request.
     * @throws ODataResponseException
     *             When the OData batch response cannot be parsed.
     * @throws IllegalArgumentException
     *             When the provided request reference could not be found in the original batch request.
     */
    @Nonnull
    public <EntityT extends VdmEntity<?>> ModificationResponse<EntityT> getModificationResult(
        @Nonnull final DeleteRequestBuilder<EntityT> operation )
    {
        final ODataRequestResultGeneric clientResult = result.getResult(requestMapping.get(operation));
        return ModificationResponse.of(clientResult, operation.getEntity());
    }

    /**
     * Extract the batch item result for the provided OData request.
     *
     * @param operation
     *            The OData operation that was used in the OData batch request.
     * @param <T>
     *            The generic object type.
     * @return The generic action response wrapper object as result of the OData action request.
     * @throws ODataResponseException
     *             When the OData batch response cannot be parsed.
     * @throws IllegalArgumentException
     *             When the provided request reference could not be found in the original batch request.
     */
    @Nonnull
    public <
        T> ActionResponseSingle<T> getModificationResult( @Nonnull final SingleValueActionRequestBuilder<T> operation )
    {
        final ODataRequestResultGeneric clientResult = result.getResult(requestMapping.get(operation));
        return ActionResponseSingle.of(clientResult, operation.getResultClass());
    }

    /**
     * Extract the batch item result for the provided OData request.
     *
     * @param operation
     *            The OData operation that was used in the OData batch request.
     * @param <T>
     *            The generic object type.
     * @return The generic action response wrapper object as result of the OData action request.
     * @throws ODataResponseException
     *             When the OData batch response cannot be parsed.
     * @throws IllegalArgumentException
     *             When the provided request reference could not be found in the original batch request.
     */
    @Nonnull
    public <T> ActionResponseCollection<T> getModificationResult(
        @Nonnull final CollectionValueActionRequestBuilder<T> operation )
    {
        final ODataRequestResultGeneric clientResult = result.getResult(requestMapping.get(operation));
        return ActionResponseCollection.of(clientResult, operation.getResultClass());
    }

    /**
     * Closes the underlying HTTP response entity.
     *
     * @since 4.15.0
     */
    @Beta
    @Override
    public void close()
    {
        final HttpEntity ent = result.getHttpResponse().getEntity();
        if( ent != null ) {
            Try.run(() -> EntityUtils.consume(ent)).onFailure(e -> log.warn("Failed to consume the HTTP entity.", e));
        }
    }

}
