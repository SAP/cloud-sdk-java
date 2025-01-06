/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper.batch;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestGeneric;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultMultipartGeneric;
import com.sap.cloud.sdk.datamodel.odata.helper.CollectionValuedFluentHelperFunction;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperBasic;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperByKey;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperRead;
import com.sap.cloud.sdk.datamodel.odata.helper.SingleValuedFluentHelperFunction;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmEntity;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmEntityUtil;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Generic OData service response wrapper for Batch response.
 *
 */
@Slf4j
@RequiredArgsConstructor( staticName = "of", access = AccessLevel.PACKAGE )
public class DefaultBatchResponseResult implements BatchResponse
{
    private final List<BatchRequestOperation> requestParts;
    private final Map<FluentHelperBasic<?, ?, ?>, ODataRequestGeneric> requestMapping;
    private final ODataRequestResultMultipartGeneric result;

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
    public static DefaultBatchResponseResult of(
        @Nonnull final ODataRequestResultMultipartGeneric response,
        @Nonnull final BatchFluentHelperBasic<?, ?> initialRequest )
    {
        return DefaultBatchResponseResult
            .of(initialRequest.getRequestParts(), initialRequest.getRequestMapping(), response);
    }

    @Nonnull
    @Override
    public Try<BatchResponseChangeSet> get( final int index )
    {
        final BatchRequestChangeSet requests = getRequestPartByTypeAndIndex(BatchRequestChangeSet.class, index);
        if( requests == null ) {
            final String errorMessage = "Unable to find changeset " + index + " in batch request.";
            return Try.failure(new IllegalArgumentException(errorMessage));
        }

        // perform health check for first operation response in changeset
        final FluentHelperBasic<?, ?, ?> vdmRequest = requests.getOperations().get(0).getFluentHelper();
        final ODataRequestGeneric request = requestMapping.get(vdmRequest);
        final Try<Void> testParse = Try.run(() -> result.getResult(request));
        if( testParse.isFailure() ) {
            return Try.failure(testParse.getCause());
        }

        return Try.success(new DefaultBatchResponseChangeSet(requests.getOperations(), this::getResultingEntity));
    }

    @Nonnull
    private VdmEntity<?> getResultingEntity( @Nonnull final BatchRequestChangeSetOperation req )
    {
        final ODataRequestGeneric request = requestMapping.get(req.getFluentHelper());
        final Class<? extends VdmEntity<?>> entityClass = VdmEntityUtil.getEntityClass(req.getFluentHelper());
        return result.getResult(request).as(entityClass);
    }

    @Nonnull
    @Override
    public <EntityT extends VdmEntity<?>> List<EntityT> getReadResult(
        @Nonnull final FluentHelperRead<?, EntityT, ?> helper )
    {
        final Class<EntityT> entityClass = VdmEntityUtil.getEntityClass(helper);
        return result.getResult(requestMapping.get(helper)).asList(entityClass);
    }

    @Nonnull
    @Override
    public <
        EntityT extends VdmEntity<?>> EntityT getReadResult( @Nonnull final FluentHelperByKey<?, EntityT, ?> helper )
    {
        final Class<EntityT> entityClass = VdmEntityUtil.getEntityClass(helper);
        return result.getResult(requestMapping.get(helper)).as(entityClass);
    }

    @Nonnull
    @Override
    public <ResultT> ResultT getReadResult( @Nonnull final SingleValuedFluentHelperFunction<?, ResultT, ?> helper )
    {
        final Class<ResultT> resultClass = VdmEntityUtil.getEntityClass(helper);
        return result.getResult(requestMapping.get(helper)).as(resultClass);
    }

    @Nonnull
    @Override
    public <ResultT> List<ResultT> getReadResult(
        @Nonnull final CollectionValuedFluentHelperFunction<?, ResultT, ?> helper )
    {
        final Class<ResultT> resultClass = VdmEntityUtil.getEntityClass(helper);
        return result.getResult(requestMapping.get(helper)).asList(resultClass);
    }

    @Nullable
    private <T extends BatchRequestOperation> T getRequestPartByTypeAndIndex( final Class<T> type, final int index )
    {
        return requestParts.stream().filter(type::isInstance).map(type::cast).skip(index).findFirst().orElse(null);
    }

    /**
     * Closes the underlying HTTP response entity.
     *
     * @since 4.15.0
     */
    @Override
    public void close()
    {
        result.close();
    }
}
