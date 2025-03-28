package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.Collection;
import java.util.LinkedList;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import com.google.common.escape.Escaper;
import com.google.common.net.PercentEscaper;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class offering the ability to get Query Parameters from Destination Properties.
 */
@Slf4j
class QueryParamGetter
{
    private static final String URL_QUERIES = "URL.queries.";

    private static final Escaper URL_QUERY_ESCAPER = new PercentEscaper("_*-:,/'().|", false);

    /**
     * Get the headers defined on the destination via properties.
     *
     * @param baseProperties
     *            The destination properties
     * @return A {@code Collection} of the headers defined on the destination.
     */
    @Nonnull
    static Collection<String> getQueryParameters( final HttpDestinationProperties baseProperties )
    {
        final Iterable<String> propertyKeys = baseProperties.getPropertyNames();
        final Collection<String> queryParams = new LinkedList<>();
        for( final String propertyKey : propertyKeys ) {
            if( StringUtils.startsWithIgnoreCase(propertyKey, URL_QUERIES) ) {
                final String queryParam;
                final Try<String> propertyValue = baseProperties.get(propertyKey).toTry().map(String.class::cast);
                if( propertyValue.isSuccess() ) {
                    queryParam =
                        URL_QUERY_ESCAPER.escape(propertyKey.substring(URL_QUERIES.length()))
                            + "="
                            + URL_QUERY_ESCAPER.escape(propertyValue.get());
                    queryParams.add(queryParam);
                } else {
                    final String msg =
                        "Cannot find value for property \"{}\" defined in destination. Skipping the query parameter.";
                    log.debug(msg, propertyKey);
                }
            }
        }
        return queryParams;
    }
}
