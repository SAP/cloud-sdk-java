package com.sap.cloud.sdk.datamodel.odata.client.request;

import java.net.URI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.client.HttpClient;

import com.google.common.annotations.Beta;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;
import com.sap.cloud.sdk.datamodel.odata.client.query.StructuredQuery;

import io.vavr.control.Try;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * The result type of the OData read by key request.
 */
@Getter
@EqualsAndHashCode( callSuper = true )
public class ODataRequestReadByKey extends ODataRequestGeneric
{
    @Nonnull
    private final String queryString;

    /**
     * Convenience constructor for OData read requests on entity collections directly. For operations on nested entities
     * use {@link #ODataRequestReadByKey(String, ODataResourcePath, String, ODataProtocol)}.
     *
     * @param servicePath
     *            The OData service path.
     * @param entityName
     *            The OData entity name.
     * @param entityKey
     *            The entity key.
     * @param encodedQuery
     *            Optional: The encoded HTTP query, if any.
     * @param protocol
     *            The OData protocol to use.
     */
    public ODataRequestReadByKey(
        @Nonnull final String servicePath,
        @Nonnull final String entityName,
        @Nonnull final ODataEntityKey entityKey,
        @Nullable final String encodedQuery,
        @Nonnull final ODataProtocol protocol )
    {
        this(servicePath, ODataResourcePath.of(entityName, entityKey), encodedQuery, protocol);
    }

    /**
     * Default constructor for OData Read requests.
     *
     * @param servicePath
     *            The OData service path.
     * @param entityPath
     *            The {@link ODataResourcePath} that identifies the entity to read.
     * @param encodedQuery
     *            Optional: The encoded HTTP query, if any.
     * @param protocol
     *            The OData protocol to use.
     *
     */
    public ODataRequestReadByKey(
        @Nonnull final String servicePath,
        @Nonnull final ODataResourcePath entityPath,
        @Nullable final String encodedQuery,
        @Nonnull final ODataProtocol protocol )
    {
        super(servicePath, entityPath, protocol);
        this.queryString = encodedQuery != null ? encodedQuery : "";
    }

    /**
     * Constructor with StructuredQuery for OData read requests on entity collections directly. For operations on nested
     * entity collections use
     * {@link ODataRequestRead#ODataRequestRead(String, ODataResourcePath, String, ODataProtocol)}.
     *
     * @param servicePath
     *            The OData service path.
     * @param entityPath
     *            The {@link ODataResourcePath} that identifies the collection of entities or properties to read.
     * @param entityKey
     *            The entity key.
     * @param query
     *            The structured query.
     */
    public ODataRequestReadByKey(
        @Nonnull final String servicePath,
        @Nonnull final ODataResourcePath entityPath,
        @Nonnull final ODataEntityKey entityKey,
        @Nonnull final StructuredQuery query )
    {
        this(
            servicePath,
            entityPath.addParameterToLastSegment(entityKey),
            query.getEncodedQueryString(),
            query.getProtocol());
    }

    @Nonnull
    @Override
    public URI getRelativeUri( @Nonnull final UriEncodingStrategy strategy )
    {
        return ODataUriFactory.createAndEncodeUri(getServicePath(), getResourcePath(), getRequestQuery(), strategy);
    }

    @Override
    @Nonnull
    public String getRequestQuery()
    {
        final String genericQueryString = super.getRequestQuery();
        if( !genericQueryString.isEmpty() && !queryString.isEmpty() ) {
            return queryString + "&" + genericQueryString;
        }
        return queryString + genericQueryString;
    }

    @Override
    @Nonnull
    public ODataRequestResultGeneric execute( @Nonnull final HttpClient httpClient )
    {
        final ODataHttpRequest request = ODataHttpRequest.withoutBody(this, httpClient);
        final Try<ODataRequestResultGeneric> result =
            csrfTokenRetriever == null
                ? tryExecute(request::requestGet, httpClient)
                : tryExecuteWithCsrfToken(httpClient, request::requestGet);
        return result.get();
    }

    /**
     * Disable pre-buffering of http response entity.
     *
     * @since 5.21.0
     */
    @Beta
    @Nonnull
    public ODataRequestResultResource.Executable withoutResponseBuffering()
    {
        requestResultFactory = ODataRequestResultFactory.WITHOUT_BUFFER;
        return httpClient -> (ODataRequestResultResource) this.execute(httpClient);
    }
}
