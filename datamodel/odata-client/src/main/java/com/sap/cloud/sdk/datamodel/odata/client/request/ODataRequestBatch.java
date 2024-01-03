/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import static lombok.AccessLevel.PRIVATE;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;

import com.google.common.collect.ImmutableMap;
import com.sap.cloud.sdk.cloudplatform.connectivity.CsrfToken;
import com.sap.cloud.sdk.cloudplatform.connectivity.CsrfTokenRetriever;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultCsrfTokenRetriever;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataRequestException;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * The OData Batch request.
 */
@Getter
@EqualsAndHashCode( callSuper = true )
@Slf4j
public class ODataRequestBatch extends ODataRequestGeneric
{
    private static final String DEFAULT_ODATA_BATCH_FORMAT_NEWLINE = "\r\n";

    @Nonnull
    private final List<BatchItem> requests = new ArrayList<>();

    private final AtomicInteger contentId = new AtomicInteger(1);

    private final UUID batchUuid;

    private final Supplier<UUID> uuidProvider;

    /**
     * Default constructor for OData Batch request.
     *
     * The service path will be URL-encoded during request serialization.
     *
     * @param servicePath
     *            The unencoded OData service path
     * @param protocol
     *            The OData protocol
     */
    public ODataRequestBatch( @Nonnull final String servicePath, @Nonnull final ODataProtocol protocol )
    {
        this(servicePath, protocol, UUID::randomUUID);
    }

    /**
     * Default constructor for OData Batch request.
     *
     * The service path will be URL-encoded during request serialization.
     *
     * @param servicePath
     *            The unencoded OData service path
     * @param protocol
     *            The OData protocol
     * @param uuidProvider
     *            A generic UUID provider, customizable for testing
     */
    public ODataRequestBatch(
        @Nonnull final String servicePath,
        @Nonnull final ODataProtocol protocol,
        @Nonnull final Supplier<UUID> uuidProvider )
    {
        super(servicePath, ODataResourcePath.of("$batch"), protocol);
        this.uuidProvider = uuidProvider;
        this.batchUuid = uuidProvider.get();
        this.headers.remove(HttpHeaders.ACCEPT); // batch request does not require Accept header
    }

    @Nonnull
    @Override
    public URI getRelativeUri( @Nonnull final UriEncodingStrategy strategy )
    {
        return ODataUriFactory.createAndEncodeUri(getServicePath(), getResourcePath(), getRequestQuery(), strategy);
    }

    /**
     * Add an OData Read request to the current OData Batch request.
     *
     * @param request
     *            The Read request.
     * @return The Batch request fluent helper instance.
     */
    @Nonnull
    public ODataRequestBatch addRead( @Nonnull final ODataRequestRead request )
    {
        final BatchItem item = new BatchItemSingle(this, request, "GET", null);
        requests.add(item);
        return this;
    }

    /**
     * Add an OData Read-By-Key request to the current OData Batch request.
     *
     * @param request
     *            The Read-By-Key request.
     * @return The Batch request fluent helper instance.
     */
    @Nonnull
    public ODataRequestBatch addReadByKey( @Nonnull final ODataRequestReadByKey request )
    {
        final BatchItem item = new BatchItemSingle(this, request, "GET", null);
        requests.add(item);
        return this;
    }

    /**
     * Add an OData Function request to the current OData Batch request.
     *
     * @param request
     *            The Function request.
     * @return The Batch request fluent helper instance.
     */
    @Nonnull
    public ODataRequestBatch addFunction( @Nonnull final ODataRequestFunction request )
    {
        final BatchItem item = new BatchItemSingle(this, request, "GET", null);
        requests.add(item);
        return this;
    }

    /**
     * Instantiate a new changeset to the current OData Batch request. As per specification if any data modifying
     * operation fails within one changeset, then the incomplete changes will be reverted.
     *
     * @return A new Changeset fluent helper instance.
     */
    @Nonnull
    public Changeset beginChangeset()
    {
        return new Changeset(this);
    }

    @Override
    @Nonnull
    public ODataRequestResultMultipartGeneric execute( @Nonnull final HttpClient httpClient )
    {
        final CsrfTokenRetriever csrfTokenRetriever =
            Option.of(this.csrfTokenRetriever).getOrElse(DefaultCsrfTokenRetriever::new);

        if( !csrfTokenRetriever.isEnabled()
            || getHeaders().containsKey(DefaultCsrfTokenRetriever.X_CSRF_TOKEN_HEADER_KEY) ) {
            log.debug("CSRF token already present, skipping retrieval.");
            return tryExecute(httpClient).get();
        }

        final Try<CsrfToken> csrfToken = tryGetCsrfToken(httpClient, csrfTokenRetriever);
        csrfToken.onSuccess(token -> addHeader(DefaultCsrfTokenRetriever.X_CSRF_TOKEN_HEADER_KEY, token.getToken()));

        final Try<ODataRequestResultMultipartGeneric> batchRequest = tryExecute(httpClient);

        if( batchRequest.isFailure() && csrfToken.isFailure() ) {
            batchRequest.getCause().addSuppressed(csrfToken.getCause());
        }
        return batchRequest.get();
    }

    private Try<ODataRequestResultMultipartGeneric> tryExecute( @Nonnull final HttpClient httpClient )
    {
        final String requestBody = getBatchRequestBody();
        final ODataHttpRequest request = ODataHttpRequest.forBodyText(this, httpClient, requestBody);

        return Try
            .of(request::requestPost)
            .map(response -> new ODataRequestResultMultipartGeneric(this, response))
            .andThenTry(ODataHealthyResponseValidator::requireHealthyResponse);
    }

    @Override
    @Nonnull
    public Map<String, Collection<String>> getHeaders()
    {
        final Map<String, Collection<String>> headers = super.getHeaders();

        // replace select existing headers with custom batch headers
        for( final Map.Entry<String, String> batchHeader : getBatchHeaders().entrySet() ) {
            headers.compute(batchHeader.getKey(), ( k, v ) -> new ArrayList<>(1)).add(batchHeader.getValue());
        }

        return headers;
    }

    @Nonnull
    private Map<String, String> getBatchHeaders()
    {
        return ImmutableMap
            .of(
                "Content-Type",
                "multipart/mixed;boundary=batch_" + batchUuid,
                "OData-Version",
                getProtocol().getProtocolVersion());
    }

    @Nonnull
    String getBatchRequestBody()
    {
        final String batchDelimiter = "--batch_" + batchUuid;
        final String batchDelimiterEnd = batchDelimiter + "--";

        final List<String> resultLines = new ArrayList<>();
        for( final BatchItem item : requests ) {
            resultLines.add(batchDelimiter);
            resultLines.addAll(item.getLines());
        }

        // closing delimiter
        resultLines.add(batchDelimiterEnd);
        resultLines.add("");
        return String.join(DEFAULT_ODATA_BATCH_FORMAT_NEWLINE, resultLines);
    }

    /**
     * The Changeset representation of the OData Batch operation.
     */
    @RequiredArgsConstructor( access = PRIVATE )
    public static final class Changeset
    {
        private final ODataRequestBatch originalRequest;
        private final List<BatchItemSingle> queries = new ArrayList<>();

        /**
         * Add an OData Create request to the current OData Batch changeset.
         *
         * @param request
         *            The Create request.
         * @return The Changeset fluent helper instance.
         */
        @Nonnull
        public Changeset addCreate( @Nonnull final ODataRequestCreate request )
        {
            final BatchItemSingle item =
                new BatchItemSingle(originalRequest, request, "POST", request::getSerializedEntity);
            queries.add(item);
            return this;
        }

        /**
         * Add an OData Update request to the current OData Batch changeset.
         *
         * @param request
         *            The Update request.
         * @return The Changeset fluent helper instance.
         */
        @Nonnull
        public Changeset addUpdate( @Nonnull final ODataRequestUpdate request )
        {
            final String versionIdentifier = request.getVersionIdentifier();
            request.addVersionIdentifierToHeaderIfPresent(versionIdentifier);
            final String httpMethod = request.getUpdateStrategy() == UpdateStrategy.MODIFY_WITH_PATCH ? "PATCH" : "PUT";
            final BatchItemSingle item =
                new BatchItemSingle(originalRequest, request, httpMethod, request::getSerializedEntity);
            queries.add(item);
            return this;
        }

        /**
         * Add an OData Delete request to the current OData Batch changeset.
         *
         * @param request
         *            The Delete request.
         * @return The Changeset fluent helper instance.
         */
        @Nonnull
        public Changeset addDelete( @Nonnull final ODataRequestDelete request )
        {
            final String versionIdentifier = request.getVersionIdentifier();
            request.addVersionIdentifierToHeaderIfPresent(versionIdentifier);
            final BatchItemSingle item = new BatchItemSingle(originalRequest, request, "DELETE", null);
            queries.add(item);
            return this;
        }

        /**
         * Add an OData Action request to the current OData Batch changeset.
         *
         * @param request
         *            The Action request.
         * @return The Changeset fluent helper instance.
         */
        @Nonnull
        public Changeset addAction( @Nonnull final ODataRequestAction request )
        {
            final BatchItemSingle item =
                new BatchItemSingle(originalRequest, request, "POST", request::getActionParameters);
            queries.add(item);
            return this;
        }

        /**
         * Finalizes the current changeset.
         *
         * @return The original Batch request fluent helper instance.
         */
        @Nonnull
        public ODataRequestBatch endChangeset()
        {
            final UUID changeSetId = originalRequest.uuidProvider.get();
            final BatchItem item = new BatchItemChangeset(changeSetId, queries);
            originalRequest.requests.add(item);

            return originalRequest;
        }
    }

    @Getter
    static final class BatchItemSingle implements BatchItem
    {
        private final int contentId;
        @Nonnull
        final ODataRequestGeneric request;
        @Nonnull
        private final String resourcePath;
        @Nonnull
        private final String httpMethod;
        @Nullable
        private final Supplier<String> payload;

        private BatchItemSingle(
            @Nonnull final ODataRequestBatch requestBatch,
            @Nonnull final ODataRequestGeneric requestSingle,
            @Nonnull final String httpMethod,
            @Nullable final Supplier<String> payload )
        {
            final String encodedRelativeUriSingleRequest =
                requestSingle.getRelativeUri(UriEncodingStrategy.BATCH).toString();
            final String encodedServicePathBatchRequest = requestBatch.getEncodedServicePath(UriEncodingStrategy.BATCH);

            assertSingleAndBatchRequestAreConsistent(
                requestBatch,
                requestSingle,
                encodedRelativeUriSingleRequest,
                encodedServicePathBatchRequest);

            this.contentId = requestBatch.contentId.getAndIncrement();
            this.request = requestSingle;
            this.resourcePath =
                StringUtils.removeStart(encodedRelativeUriSingleRequest, encodedServicePathBatchRequest);
            this.httpMethod = httpMethod;
            this.payload = payload;
        }

        private void assertSingleAndBatchRequestAreConsistent(
            final ODataRequestBatch requestBatch,
            final ODataRequestGeneric requestSingle,
            final String encodedRelativeUriSingleRequest,
            final String encodedServicePathBatchRequest )
        {
            if( !encodedRelativeUriSingleRequest.startsWith(encodedServicePathBatchRequest) ) {
                throw new ODataRequestException(
                    requestBatch,
                    "Batch request contains requests to different service paths (batch request: "
                        + encodedServicePathBatchRequest
                        + ", single request: "
                        + encodedRelativeUriSingleRequest,
                    null);
            }
            if( !Objects.equals(requestSingle.getProtocol(), requestBatch.getProtocol()) ) {
                throw new ODataRequestException(
                    requestBatch,
                    "Batch request contains requests with different protocol versions,",
                    null);
            }
        }

        @Nonnull
        @Override
        public List<String> getLines()
        {
            final List<String> lines = new ArrayList<>();
            lines.add("Content-Type: application/http");
            lines.add("Content-Transfer-Encoding: binary");
            lines.add("Content-ID: " + contentId);

            lines.add("");
            lines.add(String.format("%s %s HTTP/1.1", httpMethod, resourcePath));
            request.getHeaders().forEach(( k, values ) -> values.forEach(v -> lines.add(k + ": " + v)));
            lines.add("");

            if( payload != null ) {
                lines.add(payload.get());
            }
            lines.add("");
            return lines;
        }
    }

    @RequiredArgsConstructor( access = PRIVATE )
    @Getter
    static final class BatchItemChangeset implements BatchItem
    {
        @Nonnull
        final UUID changeSetId;
        @Nonnull
        final List<BatchItemSingle> requests;

        @Nonnull
        @Override
        public List<String> getLines()
        {
            final String changesetDelimiter = "--changeset_" + changeSetId;
            final String changesetDelimiterEnd = changesetDelimiter + "--";

            final List<String> lines = new ArrayList<>();
            lines.add("Content-Type: multipart/mixed;boundary=changeset_" + changeSetId);
            lines.add("");

            for( final BatchItem request : requests ) {
                lines.add(changesetDelimiter);
                lines.addAll(request.getLines());
            }
            lines.add(changesetDelimiterEnd);
            lines.add("");
            return lines;
        }
    }

    interface BatchItem
    {
        @Nonnull
        List<String> getLines();
    }

    /**
     * Gets the position for a single request inside a batch request. This is important for mapping the individual
     * responses later.
     *
     * @param batchRequest
     *            The batch request.
     * @param singleRequest
     *            The request item that was batched.
     * @return {@code null} if the position cannot be found. {@code Tuple2<Integer, null>} if the single request is not
     *         part of a changeset. {@code Tuple2<Integer, Integer>} if the single request is part of a changeset.
     */
    @Nullable
    static Tuple2<Integer, Integer> getBatchItemPosition(
        @Nonnull final ODataRequestBatch batchRequest,
        @Nonnull final ODataRequestGeneric singleRequest )
    {
        final List<BatchItem> batchItems = batchRequest.getRequests();

        for( int i = 0; i < batchItems.size(); i++ ) {
            final BatchItem item = batchItems.get(i);
            if( item instanceof BatchItemChangeset ) {
                final List<BatchItemSingle> nestedRequests = ((BatchItemChangeset) item).getRequests();
                final OptionalInt pos =
                    IntStream
                        .range(0, nestedRequests.size())
                        .filter(p -> singleRequest == nestedRequests.get(p).getRequest())
                        .findFirst();
                if( pos.isPresent() ) {
                    return Tuple.of(i, pos.getAsInt());
                }
            } else if( item instanceof BatchItemSingle && singleRequest == ((BatchItemSingle) item).getRequest() ) {
                return Tuple.of(i, null);
            }
        }
        return null;
    }

    @Nonnull
    String getEncodedServicePath( @Nonnull final UriEncodingStrategy encodingStrategy )
    {
        return ODataUriFactory.createAndEncodeUri(servicePath, "", null, encodingStrategy).toString();
    }
}
