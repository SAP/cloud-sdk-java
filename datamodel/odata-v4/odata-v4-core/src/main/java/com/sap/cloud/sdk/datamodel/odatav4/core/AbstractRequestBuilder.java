package com.sap.cloud.sdk.datamodel.odatav4.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.connectivity.CsrfTokenRetriever;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestGeneric;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestListener;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataUriFactory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Representation of a generic OData request as a fluent interface for further configuring the request and
 * {@link #execute(Destination) executing} it.
 *
 * @param <BuilderT>
 *            The specific builder type.
 * @param <ResultT>
 *            The type of the result entity, if any.
 */
@Slf4j
abstract class AbstractRequestBuilder<BuilderT extends RequestBuilder<ResultT>, ResultT>
    implements
    RequestBuilder<ResultT>
{
    @Nonnull
    @Getter( AccessLevel.PROTECTED )
    private final String servicePath;

    @Nonnull
    @Getter( AccessLevel.PROTECTED )
    private final ODataResourcePath resourcePath;

    /**
     * A map containing the headers to be used only for the actual request of this FluentHelper implementation.
     */
    @Getter( AccessLevel.PROTECTED )
    @Nonnull
    private final Map<String, Collection<String>> headers = new HashMap<>();

    /**
     * A map containing the custom query parameters to be used only for the actual request of this FluentHelper
     * implementation.
     */
    @Getter( AccessLevel.PROTECTED )
    @Nonnull
    private final Map<String, String> parametersForRequestOnly = new HashMap<>();

    @Getter( AccessLevel.PROTECTED )
    @Nonnull
    private final List<ODataRequestListener> listeners = new ArrayList<>();

    @Nonnull
    protected CsrfTokenRetriever csrfTokenRetriever = CsrfTokenRetriever.DISABLED_CSRF_TOKEN_RETRIEVER;

    /**
     * Instantiates this request builder using the given service path to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     * @param resourcePath
     *            The resource path identifying the resource to operate on.
     */
    public AbstractRequestBuilder( @Nonnull final String servicePath, @Nonnull final ODataResourcePath resourcePath )
    {
        this.servicePath = servicePath;
        this.resourcePath = resourcePath;
    }

    /**
     * Get the reference to this instance.
     *
     * @return The FluentHelper instance.
     */
    @SuppressWarnings( "unchecked" )
    @Nonnull
    protected BuilderT getThis()
    {
        return (BuilderT) this;
    }

    /**
     * An error handling class that implements the error result handler interface can be attached to this request
     * builder. This allows custom logic to be called when an error occurs in the {@link #execute execute} method. If
     * this method is not called, then an instance of ODataRequestListener is used. Only one handler can be attached at
     * a time per request builder object, so calling this multiple times will replace the handler.
     *
     * @param listener
     *            Instance of an error handler class that implements the error result handler interface.
     *
     * @return The same request builder with its error handler set to the provided object.
     */
    @Nonnull
    public BuilderT withListener( @Nonnull final ODataRequestListener listener )
    {
        this.listeners.add(listener);
        return getThis();
    }

    @Nonnull
    @Override
    public BuilderT withHeader( @Nonnull final String key, @Nullable final String value )
    {
        headers.computeIfAbsent(key, k -> new ArrayList<>(1)).add(value);
        return getThis();
    }

    @Nonnull
    @Override
    public BuilderT withHeaders( @Nonnull final Map<String, String> map )
    {
        map.forEach(this::withHeader);
        return getThis();
    }

    /**
     * Gives the option to specify custom query parameters for the request.
     *
     * <p>
     * <strong>Note:</strong> It is recommended to only use this function for query parameters which are not supported
     * by the VDM by default. Using this function to bypass request builder method calls can lead to unsupported
     * response handling. There is no contract on the order or priority of parameters added to the request.
     * </p>
     *
     * <p>
     * <strong>Example:</strong> Use the request query option <code>$search</code> to reduce the result set, leaving
     * only entities which match the specified search expression. This feature is supported in protocol OData v4.
     *
     * <pre>
     * new DefaultBusinessPartnerService().getAllBusinessPartner().withQueryParameter("$search", "Köln OR Cologne")
     * </pre>
     * </p>
     *
     * @param key
     *            Name of the query parameter.
     * @param value
     *            Value of the query parameter.
     *
     * @return The same request builder.
     */
    @Nonnull
    public BuilderT withQueryParameter( @Nonnull final String key, @Nullable final String value )
    {
        parametersForRequestOnly.put(key, value);
        return getThis();
    }

    @Nonnull
    <RequestT extends ODataRequestGeneric> RequestT toRequest( @Nonnull final RequestT request )
    {
        getHeaders().forEach(( k, values ) -> values.forEach(v -> request.addHeader(k, v)));

        getParametersForRequestOnly()
            .forEach(( key, value ) -> request.addQueryParameter(key, ODataUriFactory.encodeQuery(value)));

        getListeners().forEach(request::addListener);

        request.setCsrfTokenRetriever(csrfTokenRetriever);

        return request;
    }
}
