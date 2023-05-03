package com.sap.cloud.sdk.datamodel.odata.helper;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestinationProperties;

/**
 * Representation of any OData function import as a fluent class for further configuring the request and
 * {@link #executeRequest(HttpDestinationProperties) executing} it.This is specifically for functions that return either
 * a collection of primitive values or entities
 *
 * @param <FluentHelperT>
 *            The fluent helper type.
 * @param <ObjectT>
 *            The type of the object this OData request operates on, if any.
 * @param <ResultT>
 *            The type of the result entity, if any.
 */
public abstract class CollectionValuedFluentHelperFunction<FluentHelperT, ObjectT, ResultT>
    extends
    FluentHelperFunction<FluentHelperT, ObjectT, ResultT>
{

    /**
     * Instantiates this fluent helper using the given service path to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     */
    public CollectionValuedFluentHelperFunction( @Nonnull final String servicePath )
    {
        super(servicePath);
    }
}
