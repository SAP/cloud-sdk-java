package com.sap.cloud.sdk.datamodel.odata.client.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;

/**
 * Helper class to manage read operations on a Reader object.
 */
class MultipartParserReader
{
    @Nonnull
    private final BufferedReader reader;

    @Nonnull
    final String delimiter;

    @Nonnull
    private final String delimiterEnd;

    @Getter
    private boolean started = false;

    @Getter
    private boolean finished = false;

    MultipartParserReader( @Nonnull final BufferedReader reader, @Nonnull final String delimiter )
    {
        this.reader = reader;
        this.delimiter = delimiter;
        this.delimiterEnd = delimiter + "--";
    }

    /**
     * Reads and returns the String with new-line separator "\n" until (including) delimiter.
     *
     * @return The contents until (including) next separator.
     */
    @Nonnull
    public String untilDelimiter()
    {
        return readWhile(line -> !line.equals(delimiter) && !line.equals(delimiterEnd));
    }

    /**
     * Read and return the String with new-line separator "\n" until next empty line. This signals the start of HTTP
     * payload.
     *
     * @return The contents until (excluding) start of HTTP payload.
     */
    @Nonnull
    public String untilPayload()
    {
        return readWhile(StringUtils::isNotEmpty);
    }

    @Nonnull
    private String readWhile( @Nonnull final Predicate<String> recordFlag )
    {
        started = true;
        final StringBuilder sb = new StringBuilder();
        String line;
        try {
            while( !finished && (line = reader.readLine()) != null ) {
                if( !recordFlag.test(line) ) {
                    if( delimiterEnd.equals(line) ) {
                        finished = true;
                    }
                    return sb.toString();
                }
                sb.append(line).append('\n');
            }
        }
        catch( final IOException e ) {
            throw new UncheckedIOException("Unable to parse multi-part text.", e);
        }
        finished = true;
        return sb.toString();
    }
}
