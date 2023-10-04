/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Generic OData service response wrapper for data modification requests.
 *
 * @param <EntityT>
 *            The generic entity type.
 */
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor( staticName = "of", access = AccessLevel.PUBLIC )
@Slf4j
public final class ModificationResponse<EntityT extends VdmEntity<?>>
{
    private static final Gson GSON = new Gson();

    @Nullable
    private EntityT modifiedEntity;

    @Nonnull
    private Option<EntityT> responseEntity = Option.none();

    @Nonnull
    private final ODataRequestResultGeneric result;

    @Nonnull
    private final EntityT originalRequestEntity;

    /**
     * Get an updated version of the entity. If the service responded with an entity it is returned here. If the service
     * didn't respond with an entity but send an @{code ETag} header, the entity is updated and returned. Otherwise a
     * copy of the original, unmodified entity is returned.
     *
     * @return The modified entity.
     */
    @Nonnull
    public synchronized EntityT getModifiedEntity()
    {
        if( modifiedEntity == null ) {
            evaluateResponse();
        }
        return modifiedEntity;
    }

    /**
     * Get the optional response entity parsed from the HTTP content.
     *
     * @return The parsed entity or none.
     */
    @Nonnull
    public synchronized Option<EntityT> getResponseEntity()
    {
        // We synchronize the full method here because Fortify does not like double checked locking:
        // https://vulncat.fortify.com/en/detail?id=desc.structural.java.code_correctness_double_checked_locking
        if( modifiedEntity == null ) {
            evaluateResponse();
        }
        return responseEntity;
    }

    @SuppressWarnings( "unchecked" )
    private void evaluateResponse()
    {
        responseEntity = parseEntityFromResponse();

        // create a copy before modifying the version identifier to not change existing objects
        final EntityT entityToModify = responseEntity.getOrElse(originalRequestEntity);

        // clone entity
        modifiedEntity = GSON.fromJson(GSON.toJson(entityToModify), (Class<EntityT>) originalRequestEntity.getClass());

        modifiedEntity.setVersionIdentifier(getUpdatedVersionIdentifier().getOrNull());
    }

    /**
     * Access the original entity used to make the request.
     *
     * @return The original entity object used to perform an OData request.
     */
    @Nonnull
    public EntityT getRequestEntity()
    {
        return originalRequestEntity;
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
     * Get the version identifier present in the response headers, the identifier of the original entity or none, if
     * neither exist.
     *
     * @return An up to date version identifier or none.
     */
    @Nonnull
    public Option<String> getUpdatedVersionIdentifier()
    {
        return result
            .getVersionIdentifierFromHeader()
            .orElse(() -> getResponseEntity().flatMap(entity -> entity.getVersionIdentifier()));
    }

    @Nonnull
    @SuppressWarnings( "unchecked" )
    private Option<EntityT> parseEntityFromResponse()
    {
        return Try
            .of(() -> result.as((Class<EntityT>) originalRequestEntity.getClass()))
            .onFailure(e -> log.debug("Failed to parse entity from HTTP response.", e))
            .toOption()
            .peek(entity -> result.getVersionIdentifierFromHeader().peek(entity::setVersionIdentifier));
    }
}
