package com.sap.cloud.sdk.cloudplatform.exception;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.Getter;

/**
 * Thrown when string conversion to a certain type fails.
 */
public class StringParsingException extends Exception
{
    private static final long serialVersionUID = 5511067187047487478L;

    @Getter
    @Nullable
    private final String stringToParse;

    private static String getExceptionString( @Nullable final String stringToParse, @Nonnull final Class<?> destType )
    {
        return "Cannot parse '" + stringToParse + "' to type " + destType.getName() + ".";
    }

    /**
     * Exception constructor.
     *
     * @param stringToParse
     *            The string that could not be parsed.
     * @param destType
     *            The type for which the parsing failed.
     */
    public StringParsingException( @Nullable final String stringToParse, @Nonnull final Class<?> destType )
    {
        super(getExceptionString(stringToParse, destType));
        this.stringToParse = stringToParse;
    }

    /**
     * Exception constructor.
     *
     * @param stringToParse
     *            The string that could not be parsed.
     * @param destType
     *            The type for which the parsing failed.
     * @param cause
     *            The exception cause.
     */
    public StringParsingException(
        @Nullable final String stringToParse,
        @Nonnull final Class<?> destType,
        @Nullable final Throwable cause )
    {
        super(getExceptionString(stringToParse, destType), cause);
        this.stringToParse = stringToParse;
    }
}
