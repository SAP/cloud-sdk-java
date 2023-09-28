package com.sap.cloud.sdk.datamodel.odatav4.core;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultCsrfTokenRetriever;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestFunction;

/**
 * Representation of a non-CRUD OData request as a fluent interface for further configuring the request and
 * {@link #execute(Destination) executing} it.
 *
 * @param <BuilderT>
 *            The request builder type.
 * @param <ResultT>
 *            The type of the result entity, if any.
 */
public abstract class FunctionRequestBuilder<BuilderT extends FunctionRequestBuilder<BuilderT, ResultT>, ResultT>
    extends
    AbstractRequestBuilder<BuilderT, ResultT>
    implements
    ReadRequestBuilder<ResultT>
{
    /**
     * Instantiates this request builder using the given service path to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     * @param functionPath
     *            The {@link ODataResourcePath} identifying the function to invoke.
     */
    public FunctionRequestBuilder( @Nonnull final String servicePath, @Nonnull final ODataResourcePath functionPath )
    {
        super(servicePath, functionPath);
    }

    /**
     * Creates an instance of {@link ODataRequestFunction} based on the Entity class.
     * <p>
     * The following settings are used:
     * <ul>
     * <li>the endpoint URL</li>
     * <li>the function name</li>
     * <li>the parameters if applicable</li>
     * </ul>
     *
     * @return An initialized {@link ODataRequestFunction}.
     */
    @Override
    @Nonnull
    public ODataRequestFunction toRequest()
    {
        final ODataRequestFunction request =
            new ODataRequestFunction(getServicePath(), getResourcePath(), null, ODataProtocol.V4);

        return super.toRequest(request);
    }

    @Nonnull
    @Override
    public BuilderT withCsrfToken()
    {
        this.csrfTokenRetriever = new DefaultCsrfTokenRetriever();
        return getThis();
    }
}
