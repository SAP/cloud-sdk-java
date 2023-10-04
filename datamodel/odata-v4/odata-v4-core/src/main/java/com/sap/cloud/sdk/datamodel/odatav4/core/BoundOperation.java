/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import javax.annotation.Nonnull;

/**
 * Interface representing OData operations (functions and actions) bound to a specific type.
 *
 * @param <BindingT>
 *            The type the function is bound to.
 * @param <ResultT>
 *            The type this function returns.
 */
public interface BoundOperation<BindingT, ResultT>
{
    /**
     * The fully qualified name of the bound operation.
     *
     * @return The fully qualified name of bound operation.
     */
    @Nonnull
    String getQualifiedName();

    /**
     * The type this operations is bound to.
     *
     * @return The type this operations is bound to.
     */
    @Nonnull
    Class<BindingT> getBindingType();

    /**
     * The type this operations returns.
     *
     * @return The type this operations returns.
     */
    @Nonnull
    Class<ResultT> getReturnType();
}
