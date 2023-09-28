package com.sap.cloud.sdk.datamodel.odata.helper;

import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;

import com.google.gson.JsonElement;
import com.sap.cloud.sdk.cloudplatform.connectivity.CsrfTokenRetriever;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultCsrfTokenRetriever;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataException;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataFunctionParameters;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestAction;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestFunction;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestGeneric;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;

import io.vavr.Lazy;
import lombok.extern.slf4j.Slf4j;

/**
 * Representation of any OData function import as a fluent interface for further configuring the request and
 * {@link #executeRequest(Destination) executing} it.
 *
 * @param <FluentHelperT>
 *            The fluent helper type.
 * @param <ObjectT>
 *            The type of the object this OData request operates on, if any.
 * @param <ResultT>
 *            The type of the result entity, if any.
 */
@Slf4j
public abstract class FluentHelperFunction<FluentHelperT, ObjectT, ResultT>
    extends
    FluentHelperBasic<FluentHelperT, ObjectT, ResultT>
{
    /**
     * Upper-case HTTP method derived from {@link #createRequest(URI)} method.
     */
    private final Lazy<String> httpMethod = Lazy.of(() -> createRequest(URI.create("")).getMethod().toUpperCase());

    private boolean csrfTokenRetrieverUpdated = false;

    @Nonnull
    @Override
    protected CsrfTokenRetriever getCsrfTokenRetriever()
    {
        if( csrfTokenRetrieverUpdated ) {
            return super.getCsrfTokenRetriever();
        }
        switch( httpMethod.get() ) {
            case HttpGet.METHOD_NAME:
                return CsrfTokenRetriever.DISABLED_CSRF_TOKEN_RETRIEVER;
            case HttpPost.METHOD_NAME:
                return new DefaultCsrfTokenRetriever();
            default:
                log.warn("Encountered unexpected HTTP request method for function import: {}", httpMethod);
                return super.getCsrfTokenRetriever();
        }
    }

    /**
     * Instantiates this fluent helper using the given service path to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     */
    public FluentHelperFunction( @Nonnull final String servicePath )
    {
        super(servicePath, null);
    }

    /**
     * Getter for the map of parameters to be used in the function call.
     * <p>
     * The map maps the ODataName of a parameter to the corresponding unserialized value.
     * <p>
     * <strong>Only literal values are allowed as value. No complex or entity objects.</strong>
     *
     * @return A map containing the parameter for the function call.
     */
    @Nonnull
    protected abstract Map<String, Object> getParameters();

    /**
     * The exact name of the function to be called on the OData Endpoint.
     *
     * @return The function name on the endpoint.
     */
    @Nonnull
    protected abstract String getFunctionName();

    /**
     * Creates a request for this function based on the given {@code URI}.
     * <p>
     * Examples for such requests are {@code HttpGet} and {@code HttpPost}.
     *
     * @param uri
     *            The {@code URI} the request should target.
     * @return The instantiated request.
     */
    @Nonnull
    protected abstract HttpUriRequest createRequest( @Nonnull final URI uri );

    /**
     * {@inheritDoc}
     * <p>
     * The function import arguments are encoded as HTTP query expressions.
     */
    @Nonnull
    @Override
    public ODataRequestGeneric toRequest()
    {
        final ODataRequestGeneric functionImportRequest = instantiateRequest();
        return super.addHeadersAndCustomParameters(functionImportRequest);
    }

    @Nonnull
    private ODataRequestGeneric instantiateRequest()
    {
        final String servicePath = getServicePath();
        final ODataResourcePath resourcePath = ODataResourcePath.of(getFunctionName());

        // get and encode function import parameters as query expressions
        final String encodedQuery = ODataFunctionParameters.of(getParameters(), ODataProtocol.V2).toEncodedString();

        switch( httpMethod.get() ) {
            case HttpGet.METHOD_NAME:
                return new ODataRequestFunction(servicePath, resourcePath, encodedQuery, ODataProtocol.V2);
            case HttpPost.METHOD_NAME:
                return new ODataRequestAction(servicePath, resourcePath, null, encodedQuery, ODataProtocol.V2);
            default:
                throw new IllegalStateException("Unexpected HTTP request method for function import: " + httpMethod);
        }
    }

    /**
     * Default implementation for the case that this function returns a single type.
     * <p>
     * This method can be used in subclasses to implement the {@link #executeRequest(Destination)} method.
     *
     * @param destination
     *            The destination to run the function against.
     *
     * @return The single object returned by the function call. Returns {@code null} if the function did not return a
     *         result.
     *
     * @throws ODataException
     *             If the execution of the function failed.
     */
    @SuppressWarnings( "checkstyle:IllegalCatch" )
    @Nullable
    protected ObjectT executeSingle( @Nonnull final Destination destination )
        throws ODataException
    {
        final ODataRequestResultGeneric result = executeInternal(destination);
        final Class<? extends ObjectT> resultType = getEntityClass();

        if( resultType.equals(Void.class) ) {
            return null;
        }

        final ObjectT resultObject = result.as(resultType, this::refineJsonResponse);

        if( resultObject instanceof VdmEntity ) {
            final VdmEntity<?> entity = (VdmEntity<?>) resultObject;

            // use version identifier from header if present
            result.getVersionIdentifierFromHeader().peek(entity::setVersionIdentifier);

            entity.attachToService(getServicePath(), destination);
        }
        return resultObject;
    }

    /**
     * Default implementation for the case that this function returns a collection of entries.
     * <p>
     * This method can be used in subclasses to implement the {@link #executeRequest(Destination)} method.
     *
     * @param destination
     *            The destination to run the function against.
     *
     * @return A list of the objects returned by the function call. Returns an empty list if the function did not return
     *         a result.
     *
     * @throws ODataException
     *             If the execution of the function failed.
     */
    @Nonnull
    @SuppressWarnings( "unchecked" )
    protected List<ObjectT> executeMultiple( @Nonnull final Destination destination )
        throws ODataException
    {
        final ODataRequestResultGeneric result = executeInternal(destination);

        final List<ObjectT> resultObjectList = (List<ObjectT>) result.asList(getEntityClass());

        if( !resultObjectList.isEmpty() && resultObjectList.get(0) instanceof VdmEntity ) {
            resultObjectList.forEach(entity -> ((VdmEntity<?>) entity).attachToService(getServicePath(), destination));
        }
        return resultObjectList;
    }

    @Nonnull
    private ODataRequestResultGeneric executeInternal( final Destination destination )
        throws ODataException
    {
        final ODataRequestGeneric functionImportRequest = toRequest();
        return (ODataRequestResultGeneric) functionImportRequest.execute(HttpClientAccessor.getHttpClient(destination));
    }

    /**
     * Transform the JSON element from the response to extract a result entity. By default this method returns the
     * original object.
     *
     * @param jsonElement
     *            The optional response JSON element
     * @return The refined JSON element
     */
    @Nullable
    protected JsonElement refineJsonResponse( @Nullable final JsonElement jsonElement )
    {
        return jsonElement;
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
        this.csrfTokenRetrieverUpdated = true;
        return getThis();
    }

    /**
     * Deactivates the CSRF token retrieval for this OData request. This is useful if the server does not support or
     * require CSRF tokens as part of the request.
     *
     * @return The same builder
     */
    @Nonnull
    public FluentHelperT withoutCsrfToken()
    {
        this.csrfTokenRetriever = CsrfTokenRetriever.DISABLED_CSRF_TOKEN_RETRIEVER;
        this.csrfTokenRetrieverUpdated = true;
        return getThis();
    }
}
