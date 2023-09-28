package com.sap.cloud.sdk.datamodel.odata.client.request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nonnull;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * Generic type of an OData request result.
 */
public interface ODataRequestResult
{
    /**
     * Get the original {@link ODataRequestExecutable} instance that was used for running the OData request.
     *
     * @return The original {@link ODataRequestExecutable} instance.
     */
    @Nonnull
    ODataRequestGeneric getODataRequest();

    /**
     * Get the original OData {@link HttpResponse} instance, which holds the HttpEntity and header information.
     *
     * @return The HttpResponse.
     */
    @Nonnull
    HttpResponse getHttpResponse();

    /**
     * Get the iterable list of HTTP response header names.
     *
     * @return An iterable set of header names.
     */
    @Nonnull
    default Iterable<String> getHeaderNames()
    {
        return getAllHeaderValues().keySet();
    }

    /**
     * Get the iterable HTTP header values for a specific header name. The lookup happens case-insensitively.
     *
     * @param headerName
     *            The header name to look for.
     * @return An iterable set of header values.
     */
    @Nonnull
    default Iterable<String> getHeaderValues( @Nonnull final String headerName )
    {
        return getAllHeaderValues().getOrDefault(headerName, Collections.emptyList());
    }

    /**
     * Get all HTTP header values, grouped by the name (<b>case insensitive</b>) of the HTTP header.
     *
     * @return A <b>case insensitive</b> map of HTTP header names, where each entry is an iterable set of values for the
     *         specific header name.
     */
    @Nonnull
    default Map<String, Iterable<String>> getAllHeaderValues()
    {
        final Header[] allHeaders = getHttpResponse().getAllHeaders();
        final Multimap<String, String> result =
            Multimaps.newListMultimap(new TreeMap<>(String.CASE_INSENSITIVE_ORDER), ArrayList::new);

        for( final Header header : allHeaders ) {
            final String headerName = header.getName();

            if( headerName.equalsIgnoreCase("Set-Cookie") ) {
                /*
                handle the "Set-Cookie" special case, where multiple values of the same header field are not separated
                by a comma (",") but rather by semicolon (";").
                From the RFC 7230 - Section 3.2.2 Field Order
                (https://datatracker.ietf.org/doc/html/rfc7230#section-3.2.2):
                "Note: In practice, the "Set-Cookie" header field ([RFC6265]) often
                 appears multiple times in a response message and does not use the
                 list syntax, violating the above requirements on multiple header
                 fields with the same name.  Since it cannot be combined into a
                 single field-value, recipients ought to handle "Set-Cookie" as a
                 special case while processing header fields.  (See Appendix A.2.3
                 of [Kri2001] for details.)"
                 */
                for( final String element : header.getValue().split(";") ) {
                    result.put(headerName, element.trim());
                }
            } else {
                for( final HeaderElement element : header.getElements() ) {
                    result.put(headerName, element.toString());
                }
            }
        }
        return Collections.unmodifiableMap(result.asMap());
    }
}
