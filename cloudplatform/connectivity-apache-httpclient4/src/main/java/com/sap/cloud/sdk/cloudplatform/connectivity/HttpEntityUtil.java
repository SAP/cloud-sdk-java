/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.util.EntityUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Util class extracting the entity of an {@link HttpResponse}, unzipping it if necessary, and finally consuming the
 * response.
 */
@Slf4j
public class HttpEntityUtil
{
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /**
     * Extracts the body string using UTF-8 and consumes the {@link HttpEntity}.
     *
     * @param response
     *            The response to extract the entity from.
     *
     * @return The extracted body.
     *
     * @throws IOException
     *             if an error occurred while reading the response.
     * @throws ParseException
     *             if the entity of the response could not be parsed.
     */
    @Nullable
    public static String getResponseBody( @Nonnull final HttpResponse response )
        throws IOException,
            ParseException
    {
        HttpEntity responseEntity = response.getEntity();

        if( responseEntity == null ) {
            return null;
        }

        try {
            final Header[] encodingHeaders = response.getHeaders("Content-Encoding");

            if( encodingHeaders.length > 1 ) {
                log.warn("Multiple headers for Content-Encoding: " + Arrays.toString(encodingHeaders) + ".");
            }

            if( encodingHeaders.length >= 1 && encodingHeaders[0].getValue().equalsIgnoreCase("gzip") ) {
                responseEntity = new GzipDecompressingEntity(response.getEntity());
            }

            return EntityUtils.toString(responseEntity, DEFAULT_CHARSET);
        }
        finally {
            try {
                EntityUtils.consume(responseEntity);
            }
            catch( final IOException e ) {
                log.warn("Failed to consume HTTP response entity.", e);
            }
        }
    }
}
