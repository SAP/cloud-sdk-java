/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.connectivity.CsrfTokenRetriever;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.query.StructuredQuery;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestGeneric;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataUriFactory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Representation of any OData request as a fluent interface for further configuring the request and
 * {@link #executeRequest(Destination) executing} it.
 *
 * @param <FluentHelperT>
 *            The fluent helper type.
 * @param <EntityT>
 *            The type of the entity this OData request operates on, if any.
 * @param <ResultT>
 *            The type of the result entity, if any.
 */
@Slf4j
public abstract class FluentHelperBasic<FluentHelperT, EntityT, ResultT> implements FluentHelperExecutable<Object>
{
    @Nonnull
    @Getter( AccessLevel.PROTECTED )
    private final String servicePath;

    /**
     * The entity collection to send the OData requests to
     */
    @Nullable
    protected String entityCollection = null;

    /**
     * A map containing the headers to be used for all explicit and implicit requests that are part of this FluentHelper
     * implementation (csrf).
     */
    private final Map<String, String> headers = new LinkedHashMap<>();

    /**
     * A map containing the custom query parameters to be used only for the actual request of this FluentHelper
     * implementation.
     */
    @Getter( AccessLevel.PROTECTED )
    private final Map<String, String> parametersForRequestOnly = new LinkedHashMap<>();

    /**
     * The CSRF token retriever to be used for all explicit and implicit requests that are part of this FluentHelper
     * implementation.
     */
    @Nonnull
    @Getter( AccessLevel.PROTECTED )
    protected CsrfTokenRetriever csrfTokenRetriever = CsrfTokenRetriever.DISABLED_CSRF_TOKEN_RETRIEVER;

    /**
     * Returns a class object of the type this fluent helper works with.
     *
     * @return A class object of the handled type.
     */
    @Nonnull
    protected abstract Class<? extends EntityT> getEntityClass();

    /**
     * Instantiates this fluent helper using the given service path to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     * @param entityCollection
     *            The entity collection to direct the requests to.
     */
    public FluentHelperBasic( @Nonnull final String servicePath, @Nullable final String entityCollection )
    {
        this.servicePath = servicePath;
        this.entityCollection = entityCollection;
    }

    /**
     * Returns the current fluent helper instance.
     *
     * @return The current fluent helper instance.
     */
    @SuppressWarnings( "unchecked" )
    @Nonnull
    protected FluentHelperT getThis()
    {
        return (FluentHelperT) this;
    }

    @Nullable
    @Override
    public abstract ResultT executeRequest( @Nonnull final Destination destination );

    /**
     * Get all headers for explicit and implicit requests.
     *
     * @return a map containing the headers for actual request and implicit requests, e.g. csrf token request. A map
     *         containing the headers to be used for all implicit requests that are part of this FluentHelper
     *         implementation (csrf, ...).
     */
    protected Map<String, String> getHeaders()
    {
        return headers;
    }

    /**
     * Gives the option to specify custom HTTP headers. The returned object allows to specify the requests the headers
     * should be used in.
     *
     * @param key
     *            Name of the (first) desired HTTP header parameter.
     * @param value
     *            Value of the (first) desired HTTP header parameter.
     *
     * @return A fluent helper to specify further headers and their intended usage.
     */
    @Nonnull
    public FluentHelperT withHeader( @Nonnull final String key, @Nullable final String value )
    {
        headers.put(key, value);
        return getThis();
    }

    /**
     * Gives the option to specify a map of custom HTTP headers. The returned object allows to specify the requests the
     * headers should be used in.
     *
     * @param map
     *            A map of HTTP header key/value pairs.
     * @return A fluent helper to specify further headers and their intended usage.
     */
    @Nonnull
    public FluentHelperT withHeaders( @Nonnull final Map<String, String> map )
    {
        headers.putAll(map);
        return getThis();
    }

    /**
     * Gives the option to specify custom query parameters for the request. The passed parameter value will be encoded
     * with percentage encoding.
     *
     * <p>
     * <strong>Note:</strong> It is recommended to only use this function for query parameters which are not supported
     * by the VDM by default. Using this function to bypass fluent helper method calls can lead to unsupported response
     * handling. There is no contract on the order or priority of parameters added to the query.
     * </p>
     *
     * <p>
     * <strong>Example:</strong> Use the query option <code>$search</code> to reduce the result set, leaving only
     * entities which match the specified search expression. This feature is supported in protocol OData v4.
     *
     * <pre>
     * new DefaultBusinessPartnerService().getAllBusinessPartner().withQueryParameter("$search", "Köln OR Cologne")
     * </pre>
     * </p>
     *
     * @param key
     *            Name of the query parameter.
     * @param value
     *            Unencoded value of the query parameter.
     *
     * @return The same fluent helper.
     */
    @Nonnull
    protected FluentHelperT withQueryParameter( @Nonnull final String key, @Nullable final String value )
    {
        parametersForRequestOnly.put(key, value);
        return getThis();
    }

    /**
     * Query modifier to limit which field values of the entity get fetched and populated, and to specify which
     * navigation properties to expand. If this method is never called, then all fields will be fetched and populated,
     * and no navigation properties expanded. But if this method is called at least once, then only the specified fields
     * will be fetched and populated. Calling this multiple times will combine the set(s) of fields and expansions of
     * each call.
     *
     * @param fields
     *            Fields to select and/or navigation properties to expand.
     *
     * @param delegateSelect
     *            Handler to accept simple property names being put inside a delegate "select" query option.
     * @param delegateExpand
     *            Handler to accept navigation properties being put inside a delegate "expand" query option.
     * @return The same fluent helper with this query modifier applied.
     */
    @Nonnull
    FluentHelperT select(
        @Nonnull final Iterable<? extends EntitySelectable<?>> fields,
        @Nonnull final Consumer<String> delegateSelect,
        @Nonnull final Consumer<StructuredQuery> delegateExpand )
    {
        for( final EntitySelectable<?> field : fields ) {
            final List<String> selections = field.getSelections();

            // add items to selections
            selections.forEach(delegateSelect);

            // add items to expansions
            for( final String fieldName : selections ) {
                final int lastSlash = fieldName.lastIndexOf("/");
                if( lastSlash > 0 ) {
                    final String expandString = fieldName.substring(0, lastSlash);
                    final StructuredQuery q = StructuredQuery.asNestedQueryOnProperty(expandString, ODataProtocol.V2);
                    delegateExpand.accept(q);
                }
            }
        }

        return getThis();
    }

    /**
     * Translate this OData v2 request into a OData request object extending {@link ODataRequestGeneric}.
     *
     * @return A protocol agnostic OData request instance.
     */
    @Nonnull
    public abstract ODataRequestGeneric toRequest();

    @Nonnull
    <RequestT extends ODataRequestGeneric> RequestT addHeadersAndCustomParameters( @Nonnull final RequestT request )
    {
        getHeaders().forEach(request::addHeader);

        getParametersForRequestOnly()
            .forEach(( key, value ) -> request.addQueryParameter(key, ODataUriFactory.encodeQuery(value)));

        request.setCsrfTokenRetriever(getCsrfTokenRetriever());

        return request;
    }
}
