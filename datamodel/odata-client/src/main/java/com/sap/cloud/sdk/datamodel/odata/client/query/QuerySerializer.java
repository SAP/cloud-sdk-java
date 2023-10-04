/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.OrderExpression;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataUriFactory;

import io.vavr.control.Option;

class QuerySerializer
{
    private static final String SEPARATOR_SUB_QUERY = ";";
    private static final String SEPARATOR_ROOT_QUERY = "&";

    private static final Map<String, BiFunction<StructuredQuery, Boolean, Object>> QUERY_TO_STRING_MAP =
        ImmutableMap
            .<String, BiFunction<StructuredQuery, Boolean, Object>> builder()
            .put("$select=%s", QuerySerializer::selectorsToQueryString)
            .put("$expand=%s", QuerySerializer::expansionsToQueryString)
            .put("$filter=%s", QuerySerializer::filtersToQueryString)
            .put("$top=%s", ( q, applyEncoding ) -> q.top)
            .put("$skip=%s", ( q, applyEncoding ) -> q.skip)
            .put("$orderby=%s", QuerySerializer::orderByToQueryString)
            .put("$search=%s", ( q, applyEncoding ) -> conditionalEncode(q.search, applyEncoding))
            .build();

    @Nonnull
    static String serializeAndEncodeQuery( @Nonnull final StructuredQuery query, final boolean applyEncoding )
    {
        final List<String> parameters = new ArrayList<>();

        // For every system query (e.g. select, expand) apply the provided StructuredQuery object.
        // Add the computed String to the list of HTTP query parameters. Optionally apply URI encoding to the value.
        QUERY_TO_STRING_MAP
            .forEach(
                ( parameterString, valueFunction ) -> Option
                    .of(valueFunction.apply(query, applyEncoding))
                    .filter(q -> q instanceof String ? !"".equals(q) : q != null)
                    .map(q -> String.format(parameterString, q))
                    .forEach(parameters::add));

        if( query.isRoot() ) {
            query
                .getCustomParameters()
                .forEach(( key, value ) -> parameters.add(key + "=" + conditionalEncode(value, applyEncoding)));
        }

        final String queryElementSeparator = query.isRoot() ? SEPARATOR_ROOT_QUERY : SEPARATOR_SUB_QUERY;
        return Joiner.on(queryElementSeparator).join(parameters);
    }

    /**
     * Helper method to translate the simple selector expressions to query String.
     *
     * @return The part of the query string dedicated to selects.
     */
    @Nonnull
    private static String selectorsToQueryString( @Nonnull final StructuredQuery q, final boolean applyEncoding )
    {
        final List<String> selectors = getSelectors(q, applyEncoding);
        return Joiner.on(",").join(selectors);
    }

    /**
     * Helper method to translate selector expressions of a structured query to query String.
     *
     * @param query
     *            The structured query which will be translated
     * @param applyEncoding
     *            Boolean flag for encoding values to enable HTTP URI compatibility
     *
     * @return The part of the query string dedicated to $select
     */
    @Nonnull
    private static List<String> getSelectors( @Nonnull final StructuredQuery query, final boolean applyEncoding )
    {
        // common simple selectors for query in OData V2 + OData V4
        final List<String> selectors =
            query
                .getSimpleSelectors()
                .stream()
                .map(result -> applyEncoding ? ODataUriFactory.encodeQuery(result) : result)
                .collect(Collectors.toList());

        if( query.getProtocol() == ODataProtocol.V2 ) {
            // derive selectors from expansions and add to result list
            for( final StructuredQuery childQuery : query.getComplexSelectors() ) {
                String propertyName = childQuery.getEntityOrPropertyName();
                if( applyEncoding ) {
                    propertyName = ODataUriFactory.encodeQuery(propertyName);
                }
                for( final String select : getSelectors(childQuery, applyEncoding) ) {
                    selectors.add(propertyName + "/" + select);
                }
            }
        }
        return selectors;
    }

    /**
     * Helper method to translate the complex selector expressions to encoded query String.
     *
     * @return The part of the query string dedicated to selects.
     */
    @Nonnull
    private static String expansionsToQueryString( @Nonnull final StructuredQuery q, final boolean applyEncoding )
    {
        final List<String> filters = getExpansions(q, applyEncoding);
        return Joiner.on(",").join(filters);
    }

    /**
     * Helper method to translate expansion expressions of a structured query to query String.
     *
     * @param query
     *            The structured query which will be translated
     * @param applyEncoding
     *            Boolean flag for encoding values to enable HTTP URI compatibility
     *
     * @return The part of the query string dedicated to $expand
     */
    @Nonnull
    private static List<String> getExpansions( @Nonnull final StructuredQuery query, final boolean applyEncoding )
    {
        final List<String> expansions = new ArrayList<>();
        final Collection<StructuredQuery> complexSelectors = query.getComplexSelectors();
        for( final StructuredQuery subQuery : complexSelectors ) {
            String propertyName = subQuery.getEntityOrPropertyName();
            if( applyEncoding ) {
                propertyName = ODataUriFactory.encodeQuery(propertyName);
            }

            if( query.getProtocol() == ODataProtocol.V2 ) {
                expansions.add(propertyName);
                for( final String expand : getExpansions(subQuery, applyEncoding) ) {
                    expansions.add(propertyName + "/" + expand);
                }
            } else {
                final String subQueryString =
                    applyEncoding ? subQuery.getEncodedQueryString() : subQuery.getQueryString();
                if( !subQueryString.isEmpty() ) {
                    propertyName += "(" + subQueryString + ")";
                }
                expansions.add(propertyName);
            }
        }
        return expansions;
    }

    /**
     * Helper method to translate the filter expressions to query String.
     *
     * @return The part of the query string dedicated to filters.
     */
    @Nonnull
    private static String filtersToQueryString( @Nonnull final StructuredQuery q, final boolean applyEncoding )
    {
        final List<String> filters =
            q.getFilters().stream().map(filter -> filter.getExpression(q.getProtocol())).collect(Collectors.toList());

        return conditionalEncode(Joiner.on(" and ").join(filters), applyEncoding);
    }

    /**
     * Helper method to translate the order expressions to query String.
     *
     * @return The part of the query string dedicated to filters.
     */
    @Nullable
    private static String orderByToQueryString( @Nonnull final StructuredQuery q, final boolean applyEncoding )
    {
        final String orderBy = Option.of(q.getOrderBy()).map(OrderExpression::toOrderByString).getOrNull();
        return conditionalEncode(orderBy, applyEncoding);
    }

    @Nullable
    private static String conditionalEncode( @Nullable final String input, final boolean applyEncoding )
    {
        return applyEncoding && input != null ? ODataUriFactory.encodeQuery(input) : input;
    }
}
