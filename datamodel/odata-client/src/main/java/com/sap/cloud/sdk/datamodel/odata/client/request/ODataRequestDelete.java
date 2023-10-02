/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import java.net.URI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.client.HttpClient;

import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * The executable OData delete request.
 */
@Getter
@EqualsAndHashCode( callSuper = true )
@Slf4j
public class ODataRequestDelete extends ODataRequestGeneric
{
    @Nullable
    private final String versionIdentifier;

    /**
     * Convenience constructor for OData delete requests on entity collections directly. For operations on nested
     * entities use {@link #ODataRequestDelete(String, String, ODataEntityKey, String, ODataProtocol)}.
     *
     * @param servicePath
     *            The OData service path.
     * @param entityName
     *            The name of the entity to delete.
     * @param entityKey
     *            The {@link ODataEntityKey entity key} that identifies the entity to delete.
     * @param versionIdentifier
     *            The version identifier.
     * @param protocol
     *            The OData protocol to use.
     */
    public ODataRequestDelete(
        @Nonnull final String servicePath,
        @Nonnull final String entityName,
        @Nonnull final ODataEntityKey entityKey,
        @Nullable final String versionIdentifier,
        @Nonnull final ODataProtocol protocol )
    {
        this(servicePath, ODataResourcePath.of(entityName, entityKey), versionIdentifier, protocol);
    }

    /**
     * Default constructor for OData delete requests.
     *
     * @param servicePath
     *            The OData service path.
     * @param entityPath
     *            The {@link ODataResourcePath} that identifies the entity to delete.
     * @param versionIdentifier
     *            The version identifier.
     * @param protocol
     *            The OData protocol to use.
     */
    public ODataRequestDelete(
        @Nonnull final String servicePath,
        @Nonnull final ODataResourcePath entityPath,
        @Nullable final String versionIdentifier,
        @Nonnull final ODataProtocol protocol )
    {
        super(servicePath, entityPath, protocol);
        this.versionIdentifier = versionIdentifier;
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
        final ODataHttpRequest request = ODataHttpRequest.withoutBody(this, httpClient);

        addVersionIdentifierToHeaderIfPresent(versionIdentifier);

        return tryExecuteWithCsrfToken(httpClient, request::requestDelete).get();
    }
}
