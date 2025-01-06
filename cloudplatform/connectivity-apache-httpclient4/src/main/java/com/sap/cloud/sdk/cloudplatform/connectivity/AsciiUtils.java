package com.sap.cloud.sdk.cloudplatform.connectivity;

import javax.annotation.Nonnull;

/**
 * Provides convenience to remove non-printable characters from a String.
 */
public class AsciiUtils
{
    private static final String PRINTABLE_CHARACTER_RANGE = " -~";
    private static final String NON_PRINTABLE_CHARACTER_GROUP = "[^" + PRINTABLE_CHARACTER_RANGE + "]";

    /**
     * Removes non-printable characters from a String
     *
     * @param stringToClean
     *            The String to clean from non-printable characters
     * @return The cleaned String containing printable characters only
     */
    @Nonnull
    public String removeNonPrintableCharacters( @Nonnull final String stringToClean )
    {
        return stringToClean.replaceAll(NON_PRINTABLE_CHARACTER_GROUP, "");
    }
}
