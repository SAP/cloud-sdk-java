/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper.batch;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.apache.http.client.HttpClient;

import com.sap.cloud.sdk.cloudplatform.connectivity.CsrfTokenRetriever;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestAction;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestBatch;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestFunction;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestGeneric;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestRead;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestReadByKey;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultMultipartGeneric;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperBasic;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperByKey;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperCreate;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperDelete;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperFunction;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperModification;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperRead;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperUpdate;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Representation of any OData batch request as a fluent interface for managing changesets and
 * {@link #executeRequest(Destination) executing} them in a single query.
 *
 * @param <FluentHelperBatchT>
 *            The fluent helper type.
 * @param <FluentHelperBatchChangeSetT>
 *            The type of the changesets being managed in this OData batch request.
 */
public abstract class BatchFluentHelperBasic<FluentHelperBatchT extends FluentHelperServiceBatch<FluentHelperBatchT, FluentHelperBatchChangeSetT>, FluentHelperBatchChangeSetT extends FluentHelperBatchEndChangeSet<FluentHelperBatchT>>
    implements
    FluentHelperServiceBatch<FluentHelperBatchT, FluentHelperBatchChangeSetT>
{
    @Getter( AccessLevel.PACKAGE )
    private final List<BatchRequestOperation> requestParts = new ArrayList<>();
    final Map<FluentHelperBasic<?, ?, ?>, Integer> requestMappingLegacy = new IdentityHashMap<>();
    @Getter( AccessLevel.PACKAGE )
    final Map<FluentHelperBasic<?, ?, ?>, ODataRequestGeneric> requestMapping = new IdentityHashMap<>();

    Supplier<UUID> uuidProvider = UUID::randomUUID;

    private boolean skipCsrfTokenRetrieval = false;

    /**
     * Get the OData service endpoint path for the current OData batch request. Usually it can be found as static member
     * <code>DEFAULT_SERVICE_PATH</code> in the service class.
     *
     * @return The String representation of the OData service endpoint path.
     */
    @Nonnull
    protected abstract String getServicePathForBatchRequest();

    /**
     * Method to safely return the current fluent helper instance upon public method calls.
     *
     * @return The current fluent helper instance.
     */
    @Nonnull
    protected abstract FluentHelperBatchT getThis();

    @Nonnull
    @Override
    public BatchResponse executeRequest( @Nonnull final Destination destination )
    {
        final HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);
        // TODO Consider allowing custom Headers

        final ODataRequestBatch requestBatch = toRequest();
        final ODataRequestResultMultipartGeneric result = requestBatch.execute(httpClient);
        return DefaultBatchResponseResult.of(requestParts, requestMapping, result);
    }

    /**
     * Translate this OData v2 batch request into a generic {@link ODataRequestBatch}.
     *
     * @return A protocol agnostic OData batch request instance.
     */
    @Nonnull
    public ODataRequestBatch toRequest()
    {
        final String servicePath = getServicePathForBatchRequest();
        final ODataRequestBatch requestBatch = new ODataRequestBatch(servicePath, ODataProtocol.V2, uuidProvider);

        if( skipCsrfTokenRetrieval ) {
            requestBatch.setCsrfTokenRetriever(CsrfTokenRetriever.DISABLED_CSRF_TOKEN_RETRIEVER);
        }

        for( final BatchRequestOperation part : requestParts ) {
            part.addToRequestBuilder(requestBatch);
        }
        return requestBatch;
    }

    /**
     * Method handler to register a finished changeset definition.
     *
     * @param changeSet
     *            Instance of the changeset, containing OData operations modifying entities.
     */
    void addChangeSet( @Nonnull final BatchRequestChangeSet changeSet )
    {
        requestParts.add(changeSet);
    }

    @Nonnull
    @Override
    public FluentHelperBatchT addReadOperations( @Nonnull final FluentHelperRead<?, ?, ?>... readOperations )
    {
        for( final FluentHelperRead<?, ?, ?> operation : readOperations ) {
            final ODataRequestRead request = operation.toRequest();
            requestMappingLegacy.put(operation, requestParts.size());
            requestMapping.put(operation, request);
            requestParts.add(new BatchRequestRead.GetAll(operation, request));
        }
        return getThis();
    }

    @Nonnull
    @Override
    public FluentHelperBatchT addReadOperations( @Nonnull final FluentHelperByKey<?, ?, ?>... readByKeyOperations )
    {
        for( final FluentHelperByKey<?, ?, ?> operation : readByKeyOperations ) {
            final ODataRequestReadByKey request = operation.toRequest();
            requestMappingLegacy.put(operation, requestParts.size());
            requestMapping.put(operation, request);
            requestParts.add(new BatchRequestRead.GetByKey(operation, request));
        }
        return getThis();
    }

    @Nonnull
    @Override
    public FluentHelperBatchT addReadOperations( @Nonnull final FluentHelperFunction<?, ?, ?>... functionOperations )
    {
        for( final FluentHelperFunction<?, ?, ?> operation : functionOperations ) {
            final ODataRequestGeneric request = operation.toRequest();
            if( request instanceof ODataRequestAction ) {
                throw new IllegalStateException(
                    "Request for function imports while adding read operations must be "
                        + ODataRequestFunction.class.getSimpleName()
                        + ", but was "
                        + request.getClass().getSimpleName()
                        + ". Only function imports using HTTP GET are allowed.");
            }
            requestMappingLegacy.put(operation, requestParts.size());
            requestMapping.put(operation, request);
            requestParts.add(new BatchRequestRead.GetFunctionRequest(operation, (ODataRequestFunction) request));
        }
        return getThis();
    }

    @Nonnull
    @Override
    public FluentHelperBatchT addChangeSet( @Nonnull final FluentHelperModification<?, ?>... modifications )
    {
        final FluentHelperBatchChangeSetT changeSet = beginChangeSet();

        if( !(changeSet instanceof BatchChangeSetFluentHelperBasic) ) {
            throw new UnsupportedOperationException(
                String
                    .format(
                        "%1$s::beginChangeSet does not return an instance of type %2$s. To fix this issue, you may either implement %1$s::addChangeSet yourself, or use the default implementation of %3$s::beginChangeSet.",
                        getClass().getName(),
                        BatchChangeSetFluentHelperBasic.class.getName(),
                        BatchFluentHelperBasic.class.getName()));
        }

        final BatchChangeSetFluentHelperBasic<?, ?> mutableChangeSet =
            (BatchChangeSetFluentHelperBasic<?, ?>) changeSet;

        for( final FluentHelperModification<?, ?> modification : modifications ) {
            final ODataRequestGeneric request = modification.toRequest();
            requestMapping.put(modification, request);
            requestMappingLegacy.put(modification, requestMappingLegacy.size());

            if( modification instanceof FluentHelperCreate ) {
                mutableChangeSet.addRequest((FluentHelperCreate<?, ?>) modification);
            } else if( modification instanceof FluentHelperUpdate ) {
                mutableChangeSet.addRequest((FluentHelperUpdate<?, ?>) modification);
            } else if( modification instanceof FluentHelperDelete ) {
                mutableChangeSet.addRequest((FluentHelperDelete<?, ?>) modification);
            } else {
                throw new IllegalArgumentException(
                    "Failed to add unknown type of modifying operation to OData batch request: "
                        + modification.getClass().getSimpleName());
            }
        }

        changeSet.endChangeSet();
        return getThis();
    }

    /**
     * Deactivates the CSRF token retrieval for this OData request. This is useful if the server does not support or
     * require CSRF tokens as part of the request.
     *
     * @return The same builder
     */
    @Nonnull
    public FluentHelperBatchT withoutCsrfToken()
    {
        skipCsrfTokenRetrieval = true;
        return getThis();
    }
}
