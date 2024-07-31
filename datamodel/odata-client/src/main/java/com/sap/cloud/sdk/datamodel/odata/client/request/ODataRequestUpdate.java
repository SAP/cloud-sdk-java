/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.datamodel.odata.client.request;

import static org.apache.http.HttpHeaders.CONTENT_TYPE;

import java.net.URI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataRequestException;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

/**
 * The executable OData patch update request.
 */
@Getter
@EqualsAndHashCode( callSuper = true )
@Slf4j
public class ODataRequestUpdate extends ODataRequestGeneric
{
    @Nonnull
    @Getter( AccessLevel.NONE )
    private final HttpEntity requestHttpEntity;

    /**
     * The {@link UpdateStrategy} determines if the entity will be changed or replaced.
     */
    @Nonnull
    @Setter
    private UpdateStrategy updateStrategy;

    @Nullable
    private final String versionIdentifier;

    /**
     * Convenience constructor for OData update requests on entity collections directly. For operations on nested
     * entities use
     * {@link #ODataRequestUpdate(String, ODataResourcePath, String, UpdateStrategy, String, ODataProtocol)}.
     *
     * @param servicePath
     *            The OData service path.
     * @param entityName
     *            The OData entity name.
     * @param entityKey
     *            The OData entity key.
     * @param serializedEntity
     *            The serialized OData entity.
     * @param updateStrategy
     *            The update strategy.
     * @param versionIdentifier
     *            The entity version identifier.
     * @param protocol
     *            The OData protocol to use.
     */
    public ODataRequestUpdate(
        @Nonnull final String servicePath,
        @Nonnull final String entityName,
        @Nonnull final ODataEntityKey entityKey,
        @Nonnull final String serializedEntity,
        @Nonnull final UpdateStrategy updateStrategy,
        @Nullable final String versionIdentifier,
        @Nonnull final ODataProtocol protocol )
    {
        this(
            servicePath,
            ODataResourcePath.of(entityName, entityKey),
            serializedEntity,
            updateStrategy,
            versionIdentifier,
            protocol);
    }

    /**
     * Default constructor for OData Update requests.
     *
     * @param servicePath
     *            The OData service path.
     * @param entityPath
     *            The {@link ODataResourcePath} that identifies the entity to update.
     * @param serializedEntity
     *            The serialized OData entity.
     * @param updateStrategy
     *            The update strategy.
     * @param versionIdentifier
     *            The entity version identifier.
     * @param protocol
     *            The OData protocol to use.
     */
    public ODataRequestUpdate(
        @Nonnull final String servicePath,
        @Nonnull final ODataResourcePath entityPath,
        @Nonnull final String serializedEntity,
        @Nonnull final UpdateStrategy updateStrategy,
        @Nullable final String versionIdentifier,
        @Nonnull final ODataProtocol protocol )
    {
        this(
            servicePath,
            entityPath,
            new ComparableHttpEntity(serializedEntity),
            updateStrategy,
            versionIdentifier,
            protocol);
    }

    /**
     * Default constructor for OData Update requests.
     *
     * @param servicePath
     *            The OData service path.
     * @param entityPath
     *            The {@link ODataResourcePath} that identifies the entity to update.
     * @param httpEntity
     *            The Http entity.
     * @param updateStrategy
     *            The update strategy.
     * @param versionIdentifier
     *            The entity version identifier.
     * @param protocol
     *            The OData protocol to use.
     */
    public ODataRequestUpdate(
        @Nonnull final String servicePath,
        @Nonnull final ODataResourcePath entityPath,
        @Nonnull final HttpEntity httpEntity,
        @Nonnull final UpdateStrategy updateStrategy,
        @Nullable final String versionIdentifier,
        @Nonnull final ODataProtocol protocol )
    {
        super(servicePath, entityPath, protocol);
        this.requestHttpEntity = httpEntity;
        this.updateStrategy = updateStrategy;
        this.versionIdentifier = versionIdentifier;

        final Header contentType = httpEntity.getContentType();
        if( contentType != null ) {
            headers.putIfAbsent(CONTENT_TYPE, Lists.newArrayList(contentType.getValue()));
        }
    }

    @Nonnull
    @Override
    public URI getRelativeUri( @Nonnull final UriEncodingStrategy strategy )
    {
        return ODataUriFactory.createAndEncodeUri(getServicePath(), getResourcePath(), getRequestQuery(), strategy);
    }

    @Nonnull
    @Override
    public ODataRequestResultGeneric execute( @Nonnull final HttpClient httpClient )
    {
        final ODataHttpRequest request = ODataHttpRequest.forHttpEntity(this, httpClient, requestHttpEntity);
        addVersionIdentifierToHeaderIfPresent(versionIdentifier);

        if( updateStrategy == UpdateStrategy.MODIFY_WITH_PATCH ) {
            return tryExecuteWithCsrfToken(httpClient, request::requestPatch).get();
        } else if( updateStrategy == UpdateStrategy.REPLACE_WITH_PUT ) {
            return tryExecuteWithCsrfToken(httpClient, request::requestPut).get();
        } else {
            throw new IllegalStateException("Unexpected update Strategy: " + updateStrategy);
        }
    }

    /**
     * Get the String representation of the update payload.
     *
     * @return The serialized entity.
     */
    @Nonnull
    public String getSerializedEntity()
    {
        return Try
            .of(() -> EntityUtils.toString(requestHttpEntity, Charsets.UTF_8))
            .getOrElseThrow(e -> new ODataRequestException(this, "Unable to serialize request payload.", e));
    }

    /**
     * Wrapper for an HttpEntity instance. Enable {@code Object#equals} and {@code Object#hashCode} on original data.
     */
    @EqualsAndHashCode
    @RequiredArgsConstructor
    private static class ComparableHttpEntity implements HttpEntity
    {
        @Nonnull
        private final Object data;

        @EqualsAndHashCode.Exclude
        @Delegate
        @Nonnull
        private final HttpEntity delegate;

        /**
         * Custom constructor for application/json will drop the charset information until CLOUDECOSYSTEM-9450 is done.
         *
         * @param json
         *            The serialized entity json representation.
         */
        private ComparableHttpEntity( final String json )
        {
            this(json, new StringEntity(json, ContentType.APPLICATION_JSON));
            ((StringEntity) delegate).setContentType(ContentType.APPLICATION_JSON.getMimeType());
        }
    }
}
