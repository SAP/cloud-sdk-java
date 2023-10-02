/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Strings;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.OrderExpression;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueBoolean;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * {@code StructuredQuery} acts as a builder for OData 2.0 or 4.0 queries. It assists with assembling request parameters
 * such as {@code $select, $filter, ...}. This API does not differentiate between OData versions. Only leveraging
 * features e.g. within filters that conform to the selected protocol version is the responsibility of the consumer.
 */
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
public final class StructuredQuery implements QuerySerializable
{
    /**
     * The structured property field name.
     */
    @Getter
    @Nonnull
    private final String entityOrPropertyName;

    @Getter
    private final boolean isRoot;

    @Getter
    private final ODataProtocol protocol;

    @Getter
    @Nonnull
    private final Collection<String> simpleSelectors = new LinkedHashSet<>();
    @Getter
    @Nonnull
    private final Collection<StructuredQuery> complexSelectors = new LinkedHashSet<>();
    @Getter
    @Nonnull
    private final Collection<ValueBoolean> filters = new ArrayList<>();
    @Getter
    @Nonnull
    private final Map<String, String> customParameters = new LinkedHashMap<>();
    @Getter
    @Nullable
    private OrderExpression orderBy = null;

    @Getter
    @Nullable
    Number top;
    @Getter
    @Nullable
    Number skip;
    @Getter
    @Nullable
    String search;

    /**
     * Create a {@code StructuredQuery} for building up OData 2.0 or 4.0 queries.
     *
     * @param entityName
     *            The entity collection to be queried.
     *
     * @param protocol
     *            The {@link ODataProtocol} version this query should conform to.
     * @return A new {@code StructuredQuery} object.
     */
    @Nonnull
    public static StructuredQuery onEntity( @Nonnull final String entityName, @Nonnull final ODataProtocol protocol )
    {
        return new StructuredQuery(entityName, true, protocol);
    }

    /**
     * Create a nested query on a property. This is an OData 4.0 specific feature.
     *
     * @param fieldName
     *            The property that is to be queried.
     * @param protocol
     *            The {@link ODataProtocol} version this query should conform to.
     *
     * @return A new {@code StructuredQuery} object.
     */
    @Nonnull
    public static
        StructuredQuery
        asNestedQueryOnProperty( @Nonnull final String fieldName, @Nonnull final ODataProtocol protocol )
    {
        return new StructuredQuery(fieldName, false, protocol);
    }

    /**
     * Query modifier to limit which field values of the entity get fetched and populated.
     *
     * @param fields
     *            Properties to be selected.
     * @return This query object with the added selections.
     */
    @Nonnull
    public StructuredQuery select( @Nonnull final String... fields )
    {
        simpleSelectors.addAll(Arrays.asList(fields));
        return this;
    }

    /**
     * Query modifier to limit which complex and navigational properties will be expanded (and thus selected). Such
     * expansions are represented again through structured queries.
     *
     * @param subqueries
     *            Query objects on properties to be expanded. The {@link StructuredQuery#getEntityOrPropertyName()} will
     *            be th
     * @return This query object with the added selections.
     */
    @Nonnull
    public StructuredQuery select( @Nonnull final StructuredQuery... subqueries )
    {
        final List<StructuredQuery> queries = Arrays.asList(subqueries);
        // If there are already expands on the same properties as the ones passed in -> remove them
        // That avoids duplications e.g. $expand=BestFriend,BestFriend,BestFriend($select=FirstName)
        final List<String> fields =
            queries.stream().map(StructuredQuery::getEntityOrPropertyName).collect(Collectors.toList());

        complexSelectors.removeIf(selector -> fields.contains(selector.getEntityOrPropertyName()));
        complexSelectors.addAll(queries);
        return this;
    }

    @Nonnull
    @SuppressWarnings( "varargs" )
    public StructuredQuery filter( @Nonnull final ValueBoolean... filters )
    {
        this.filters.addAll(Arrays.asList(filters));
        return this;
    }

    @Nonnull
    public StructuredQuery top( @Nonnull final Number top )
    {
        this.top = top;
        return this;
    }

    @Nonnull
    public StructuredQuery skip( @Nonnull final Number skip )
    {
        this.skip = skip;
        return this;
    }

    @Nonnull
    public StructuredQuery orderBy( @Nonnull final String field, @Nonnull final Order order )
    {
        if( orderBy == null ) {
            orderBy = OrderExpression.of(field, order);
        } else {
            orderBy.and(field, order);
        }
        return this;
    }

    @Nonnull
    public StructuredQuery orderBy( @Nonnull final OrderExpression ordering )
    {
        orderBy = ordering;
        return this;
    }

    @Nonnull
    public StructuredQuery search( @Nonnull final String search )
    {
        this.search = search;
        return this;
    }

    /**
     * Adds a custom query parameter in the form of a key=value pair. This will not override any parameters set via
     * {@link #select(String...) select}, {@link #filter(ValueBoolean...) filter} etc.
     *
     * @param key
     *            The parameter key. Must not be null or empty.
     * @param value
     *            The parameter value.
     * @return This query object with the added parameter
     *
     * @throws IllegalArgumentException
     *             if the key is null or empty
     * @throws IllegalStateException
     *             if this query object is a nested query
     */
    @Nonnull
    public StructuredQuery withCustomParameter( @Nonnull final String key, @Nullable final String value )
    {
        if( Strings.isNullOrEmpty(key) ) {
            throw new IllegalArgumentException("Custom parameter key must not be null or empty.");
        }
        // if the query is inside an expand it is not corresponding to an HTTP query
        // so here only odata parameters are allowed
        else if( !isRoot() ) {
            throw new IllegalStateException(
                "Custom query parameters can only be added to the HTTP query but not to nested OData queries on navigation properties.");
        }
        customParameters.put(key, value);
        return this;
    }

    /**
     * Requests an inline count by adding the system query option {@code $inlinecount} (OData V2) or {@code $count}
     * (OData V4).
     *
     * @return This query object with the added parameter
     */
    @Nonnull
    public StructuredQuery withInlineCount()
    {
        final Map.Entry<String, String> queryOption = getProtocol().getQueryOptionInlineCount(true);
        customParameters.put(queryOption.getKey(), queryOption.getValue());
        return this;
    }

    @Nonnull
    @Override
    public String getEncodedQueryString()
    {
        return QuerySerializer.serializeAndEncodeQuery(this, true);
    }

    @Nonnull
    @Override
    public String getQueryString()
    {
        return QuerySerializer.serializeAndEncodeQuery(this, false);
    }
}
