/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.sap.cloud.sdk.result.ResultElement;

import io.vavr.control.Option;
import io.vavr.control.Try;

/**
 * This class provides methods to lazily iterate through the pages of an OData read request result-set. It allows for
 * memory-efficient exploration / parsing with continuous requests to the OData endpoint. It enables the consumption of
 * all data through server-driven pagination.
 */
public interface ODataRequestResultPagination extends Iterable<ResultElement>
{
    /**
     * Get the next page link of result-set.
     *
     * @return the OData protocol specific value of next link property. Or {@code null} if last page of result-set.
     */
    @Nonnull
    Option<String> getNextLink();

    /**
     * Get the next page link of result-set.
     *
     * @return the OData protocol specific value of next link property. Or {@code null} if last page of result-set.
     */
    @Nonnull
    Try<ODataRequestResultGeneric> tryGetNextPage();

    /**
     * Get the original {@link ODataRequestGeneric} instance that was used for running the OData request.
     *
     * @return The original {@link ODataRequestGeneric} instance.
     */
    @Nonnull
    ODataRequestGeneric getODataRequest();

    /**
     * Iterate over result-set pages.
     *
     * @param type
     *            The expected class reference to be used for deserializing the resulting items.
     * @param <T>
     *            The generic item type.
     * @return An instance of {@link Iterable} that allows lazy iteration through OData result pages.
     */
    @Nonnull
    @SuppressWarnings( { "StaticPseudoFunctionalStyleMethod", "ConstantConditions" } )
    default <T> Iterable<List<T>> iteratePages( @Nonnull final Class<? extends T> type )
    {
        // create a lazy iterable for multi-page responses
        final Iterable<ODataRequestResultPagination> iterable = () -> new ODataRequestResultPaginationIterator(this);

        // create a lazy iterable for multi-page result elements
        final Iterable<List<ResultElement>> pages = Iterables.transform(iterable, Lists::newArrayList);

        // cast items in lists of lazy-iterable
        return Iterables.transform(pages, list -> Lists.transform(list, item -> item.getAsObject().as(type)));
    }
}
