package com.sap.cloud.sdk.datamodel.odata.helper;

import java.util.Arrays;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.client.HttpClient;

import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultCsrfTokenRetriever;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.query.StructuredQuery;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataEntityKey;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestReadByKey;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;

/**
 * Representation of an OData request to retrieve an entity by its key as a fluent interface for further configuring the
 * request and {@link #executeRequest(Destination) executing} it.
 *
 * @param <FluentHelperT>
 *            The fluent helper type.
 * @param <EntityT>
 *            The type of the result entity.
 * @param <SelectableT>
 *            The type of the class that represents fields of the entity.
 */
public abstract class FluentHelperByKey<FluentHelperT, EntityT extends VdmEntity<?>, SelectableT>
    extends
    FluentHelperBasic<FluentHelperT, EntityT, EntityT>
{
    private final StructuredQuery delegateQuery;

    /**
     * Instantiates this fluent helper using the given service path and entity collection to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     *
     * @param entityCollection
     *            The entity collection to direct the requests to.
     */
    public FluentHelperByKey( @Nonnull final String servicePath, @Nonnull final String entityCollection )
    {
        super(servicePath, entityCollection);
        delegateQuery = StructuredQuery.onEntity(entityCollection, ODataProtocol.V2);
    }

    /**
     * Getter for a map containing the OData name of key properties, each mapped to the value to search by.
     *
     * @return A name-value mapping for the OData key properties.
     */
    @Nonnull
    protected abstract Map<String, Object> getKey();

    @Override
    @Nonnull
    public ODataRequestReadByKey toRequest()
    {
        final ODataEntityKey entityKey = ODataEntityKey.of(getKey(), ODataProtocol.V2);
        final String queryString = delegateQuery.getEncodedQueryString();

        final ODataRequestReadByKey request =
            new ODataRequestReadByKey(getServicePath(), entityCollection, entityKey, queryString, ODataProtocol.V2);

        return super.addHeadersAndCustomParameters(request);
    }

    @Override
    @Nonnull
    public FluentHelperT withQueryParameter( @Nonnull final String key, @Nullable final String value )
    {
        return super.withQueryParameter(key, value);
    }

    /**
     * Query modifier to limit which field values of the entity get fetched &amp; populated. If this method is never
     * called, then all fields will be fetched &amp; populated. But if this method is called at least once, then only
     * the specified fields will be fetched &amp; populated. Calling this multiple times will combine the set(s) of
     * fields of each call.
     *
     * @param fields
     *            Array of fields to select.
     *
     * @return The same fluent helper with the provided fields selected.
     */
    @SuppressWarnings( "unchecked" )
    @Nonnull
    public FluentHelperT select( @Nonnull final SelectableT... fields )
    {
        final Iterable<EntitySelectable<?>> selectableFields = (Iterable<EntitySelectable<?>>) Arrays.asList(fields);
        return super.select(selectableFields, delegateQuery::select, delegateQuery::select);
    }

    @Override
    @Nonnull
    public EntityT executeRequest( @Nonnull final Destination destination )
    {
        final HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);
        final ODataRequestResultGeneric response = toRequest().execute(httpClient);

        final EntityT result = response.as(getEntityClass());

        // use version identifier from header if present
        response.getVersionIdentifierFromHeader().peek(result::setVersionIdentifier);

        result.attachToService(getServicePath(), destination);
        return result;
    }

    /**
     * Activates the CSRF token retrieval for this OData request. This is useful if the server does require CSRF tokens
     * as part of the request.
     *
     * @return The same builder
     */
    @Nonnull
    public FluentHelperT withCsrfToken()
    {
        this.csrfTokenRetriever = new DefaultCsrfTokenRetriever();
        return getThis();
    }
}
