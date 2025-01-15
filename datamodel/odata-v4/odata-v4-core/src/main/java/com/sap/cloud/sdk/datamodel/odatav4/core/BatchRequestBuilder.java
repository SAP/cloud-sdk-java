package com.sap.cloud.sdk.datamodel.odatav4.core;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.apache.http.client.HttpClient;

import com.sap.cloud.sdk.cloudplatform.connectivity.CsrfTokenRetriever;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultCsrfTokenRetriever;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestAction;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestBatch;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestCreate;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestDelete;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestFunction;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestGeneric;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestRead;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestReadByKey;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultMultipartGeneric;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestUpdate;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Representation of an OData Batch request as a fluent interface for combining multiple data reading and modifying
 * operations in one HTTP request.
 */
public class BatchRequestBuilder extends AbstractRequestBuilder<BatchRequestBuilder, BatchResponse> implements ModificationRequestBuilder<BatchResponse>
{
    private final ODataRequestBatch delegate;

    @Getter( AccessLevel.PROTECTED )
    private final Supplier<UUID> uuidProvider = UUID::randomUUID;

    @Nonnull
    @Getter( AccessLevel.PACKAGE )
    private final Map<RequestBuilder<?>, ODataRequestGeneric> requestMapping = new IdentityHashMap<>();

    /**
     * Instantiates this request builder using the given service path to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     */
    @SuppressWarnings( "this-escape" ) // getUuidProvider() is designed to be overridable.
    public BatchRequestBuilder( @Nonnull final String servicePath )
    {
        super(servicePath, ODataResourcePath.of("$batch"));
        this.delegate = new ODataRequestBatch(servicePath, ODataProtocol.V4, getUuidProvider());
        this.csrfTokenRetriever = new DefaultCsrfTokenRetriever();
    }

    /**
     * Add read operations to the OData batch request.
     *
     * @param operations
     *            A var-arg array of read operations.
     * @return The current reference of batch request builder.
     * @see GetAllRequestBuilder
     * @see GetByKeyRequestBuilder
     * @see FunctionRequestBuilder
     */
    @Nonnull
    public BatchRequestBuilder addReadOperations( @Nonnull final ReadRequestBuilder<?>... operations )
    {
        for( final ReadRequestBuilder<?> operation : operations ) {
            final ODataRequestGeneric request = operation.toRequest();
            requestMapping.put(operation, request);
            if( request instanceof ODataRequestRead ) {
                delegate.addRead((ODataRequestRead) request);
            } else if( request instanceof ODataRequestReadByKey ) {
                delegate.addReadByKey((ODataRequestReadByKey) request);
            } else if( request instanceof ODataRequestFunction ) {
                delegate.addFunction((ODataRequestFunction) request);
            } else {
                throw new IllegalArgumentException(
                    "Failed to add unknown type of read operation to OData batch request: "
                        + operation.getClass().getSimpleName());
            }
        }
        return this;
    }

    /**
     * Add modifying operations to the OData batch request as combined changeset.
     *
     * @param operations
     *            A var-arg array of modifying operations.
     * @return The current reference of batch request builder.
     * @see CreateRequestBuilder
     * @see DeleteRequestBuilder
     * @see UpdateRequestBuilder
     * @see ActionRequestBuilder
     */
    @Nonnull
    public BatchRequestBuilder addChangeset( @Nonnull final ModificationRequestBuilder<?>... operations )
    {
        final ODataRequestBatch.Changeset changeset = delegate.beginChangeset();
        for( final ModificationRequestBuilder<?> operation : operations ) {
            final ODataRequestGeneric request = operation.toRequest();
            requestMapping.put(operation, request);
            if( request instanceof ODataRequestCreate ) {
                changeset.addCreate((ODataRequestCreate) request);
            } else if( request instanceof ODataRequestUpdate ) {
                changeset.addUpdate((ODataRequestUpdate) request);
            } else if( request instanceof ODataRequestDelete ) {
                changeset.addDelete((ODataRequestDelete) request);
            } else if( request instanceof ODataRequestAction ) {
                changeset.addAction((ODataRequestAction) request);
            } else {
                throw new IllegalArgumentException(
                    "Failed to add unknown type of modifying operation to OData batch request: "
                        + operation.getClass().getSimpleName());
            }
        }
        changeset.endChangeset();
        return this;
    }

    @Override
    @Nonnull
    public BatchRequestBuilder withoutCsrfToken()
    {
        this.csrfTokenRetriever = CsrfTokenRetriever.DISABLED_CSRF_TOKEN_RETRIEVER;
        return this;
    }

    @Override
    @Nonnull
    public ODataRequestBatch toRequest()
    {
        return super.toRequest(delegate);
    }

    @Nonnull
    @Override
    public BatchResponse execute( @Nonnull final Destination destination )
    {
        final HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);

        @SuppressWarnings( "PMD.CloseResource" ) // The ODataRequestResultMultipartGeneric is closed by BatchResponse
        final ODataRequestResultMultipartGeneric response = toRequest().execute(httpClient);

        return BatchResponse.of(response, requestMapping);
    }
}
