/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataRequestException;

import lombok.extern.slf4j.Slf4j;

/**
 * The implementation for the pagination based iterator of OData result-set, The methods are acting lazily.
 */
@Slf4j
class ODataRequestResultPaginationIterator implements Iterator<ODataRequestResultPagination>
{
    // stateful reference to access the next page request
    // if null: no next page, current page is last
    @Nullable
    private Supplier<ODataRequestResultPagination> nextPageLazy;

    /**
     * Default constructor.
     *
     * @param firstPage
     *            First page of the result-set.
     */
    ODataRequestResultPaginationIterator( @Nonnull final ODataRequestResultPagination firstPage )
    {
        nextPageLazy = () -> firstPage;
    }

    @Override
    public boolean hasNext()
    {
        return nextPageLazy != null;
    }

    @Override
    @Nonnull
    public ODataRequestResultPagination next()
        throws NoSuchElementException,
            ODataException
    {
        log.debug("Getting next page of OData request.");
        if( !hasNext() ) {
            throw new NoSuchElementException("No next page of OData result-set defined.");
        }

        final ODataRequestResultPagination page = Objects.requireNonNull(nextPageLazy).get();

        log.debug("Retrieved new page from OData service.");
        nextPageLazy = page.getNextLink().isDefined() ? () -> requestNextPage(page) : null;
        return page;
    }

    /**
     * Request the next page of the result-set.
     *
     * @param page
     *            The current page.
     * @return The next page.
     */
    @Nonnull
    protected ODataRequestResultPagination requestNextPage( @Nonnull final ODataRequestResultPagination page )
    {
        return page
            .tryGetNextPage()
            .andThenTry(ODataHealthyResponseValidator::requireHealthyResponse)
            .getOrElseThrow(e -> new ODataRequestException(page.getODataRequest(), "Failed to handle next page.", e));
    }
}
