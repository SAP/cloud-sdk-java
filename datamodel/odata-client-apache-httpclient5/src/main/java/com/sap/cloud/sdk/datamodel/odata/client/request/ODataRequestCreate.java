package com.sap.cloud.sdk.datamodel.odata.client.request;

import java.net.URI;

import javax.annotation.Nonnull;

import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;

import com.google.common.collect.Lists;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * The executable OData create request.
 */
@Getter
@EqualsAndHashCode( callSuper = true )
public class ODataRequestCreate extends ODataRequestGeneric
{
    @Nonnull
    private final String serializedEntity;

    /**
     * Convenience constructor for OData delete requests on entity collections directly. For operations on nested
     * entities use {@link #ODataRequestCreate(String, ODataResourcePath, String, ODataProtocol)}.
     *
     * @param servicePath
     *            The OData service path.
     * @param entityName
     *            The OData entity name.
     * @param serializedEntity
     *            The serialized query payload.
     * @param protocol
     *            The OData protocol to use.
     */
    public ODataRequestCreate(
        @Nonnull final String servicePath,
        @Nonnull final String entityName,
        @Nonnull final String serializedEntity,
        @Nonnull final ODataProtocol protocol )
    {
        this(servicePath, ODataResourcePath.of(entityName), serializedEntity, protocol);
    }

    /**
     * Default constructor for OData Create request.
     *
     * @param servicePath
     *            The OData service path.
     * @param entityPath
     *            The {@link ODataResourcePath path} to the OData entity.
     * @param serializedEntity
     *            The serialized query payload.
     * @param protocol
     *            The OData protocol to use.
     */
    public ODataRequestCreate(
        @Nonnull final String servicePath,
        @Nonnull final ODataResourcePath entityPath,
        @Nonnull final String serializedEntity,
        @Nonnull final ODataProtocol protocol )
    {
        super(servicePath, entityPath, protocol);
        this.serializedEntity = serializedEntity;
        headers.putIfAbsent(HttpHeaders.CONTENT_TYPE, Lists.newArrayList("application/json"));
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
        final ODataHttpRequest request = ODataHttpRequest.forBodyJson(this, httpClient, serializedEntity);

        return tryExecuteWithCsrfToken(httpClient, request::requestPost).get();
    }
}
