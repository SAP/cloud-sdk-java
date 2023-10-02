/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper;

import javax.annotation.Nonnull;

/**
 * Representation of any OData function import as a fluent class for further configuring the request and
 * {@link #executeRequest(Destination) executing} it. This is specifically for functions that return either a single
 * primitive value or entity value
 *
 * @param <FluentHelperT>
 *            The fluent helper type.
 * @param <ObjectT>
 *            The type of the object this OData request operates on, if any.
 * @param <ResultT>
 *            The type of the result entity, if any.
 */
public abstract class SingleValuedFluentHelperFunction<FluentHelperT, ObjectT, ResultT>
    extends
    FluentHelperFunction<FluentHelperT, ObjectT, ResultT>
{

    /**
     * Instantiates this fluent helper using the given service path to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     */
    public SingleValuedFluentHelperFunction( @Nonnull final String servicePath )
    {
        super(servicePath);
    }
}
