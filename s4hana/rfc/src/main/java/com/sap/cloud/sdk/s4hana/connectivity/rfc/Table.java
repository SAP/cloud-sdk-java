package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Gathers values that should be passed as a {@link com.sap.conn.jco.JCoTable} to a
 * {@link com.sap.conn.jco.JCoFunction}.
 * <p>
 * The values stored are grouped by a row Id that gets incremented for each row created via {@link #row()}. As a result
 * the {@link #size()} methods returns the number of such groups and not the number of actual values stored.
 *
 * @param <RequestT>
 *            The type of the request this table is used in.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
@Deprecated
public class Table<RequestT>
{
    @Nonnull
    private final RequestT request;

    @Nonnull
    private final List<List<Value<?>>> cells;

    /**
     * The number of currently stored groups of values as well as the Id of the last group of added values.
     *
     * @return The number of values stored in this table.
     */
    public int size()
    {
        return cells.size();
    }

    /**
     * Checks whether this table is empty, or has at least one value stored.
     *
     * @return True, if this table contains no element; false, else.
     */
    public boolean isEmpty()
    {
        return size() == 0;
    }

    /**
     * Translate this reference to a {@link TableAsVector} that gathers values that are added to this vector.
     *
     * @return A new {@code TableAsVector} linked to this {@code Table}.
     */
    @Nonnull
    public TableAsVector<RequestT> asVector()
    {
        if( !cells.isEmpty() ) {
            throw new IllegalStateException(
                "Cannot translate table to a vector when there are already some fields assigned.");
        }
        return new TableAsVector<>(request, cells);
    }

    /**
     * Creates and returns a new {@code TableRow} that gathers values that are added to this {@code Table}.
     *
     * @return A new {@code TableRow} linked to this {@code Table}.
     */
    @Nonnull
    public TableRow<RequestT> row()
    {
        final List<Value<?>> values = new ArrayList<>();
        cells.add(values);
        return new TableRow<>(this, values);
    }

    /**
     * Returns the initial request, finalizing the access to this {@code Table}.
     *
     * @return The original request.
     */
    @Nonnull
    public RequestT end()
    {
        return request;
    }
}
