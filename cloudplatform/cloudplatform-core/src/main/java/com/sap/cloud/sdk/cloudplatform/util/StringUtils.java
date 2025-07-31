package com.sap.cloud.sdk.cloudplatform.util;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.annotations.Beta;

/**
 * Internal utility class for common String operations.
 *
 * @since 5.21.0
 */
@Beta
public class StringUtils
{
    /**
     * Checks if the given string starts with the specified prefix, ignoring case.
     *
     * @param str
     *            the string to check
     * @param prefix
     *            the prefix to look for
     * @return true if the string starts with the prefix, ignoring case; false otherwise
     */
    public static boolean startsWithIgnoreCase( @Nullable final String str, @Nullable final String prefix )
    {
        if( str == null || prefix == null ) {
            return false;
        }
        if( str.length() < prefix.length() ) {
            return false;
        }
        return str.substring(0, prefix.length()).equalsIgnoreCase(prefix);
    }

    /**
     * Checks if the given string ends with the specified suffix, ignoring case.
     *
     * @param str
     *            the string to check
     * @param suffix
     *            the suffix to look for
     * @return true if the string ends with the suffix, ignoring case; false otherwise
     */
    public static boolean endsWithIgnoreCase( @Nullable final String str, @Nullable final String suffix )
    {
        if( str == null || suffix == null ) {
            return false;
        }
        if( str.length() < suffix.length() ) {
            return false;
        }
        return str.substring(str.length() - suffix.length()).equalsIgnoreCase(suffix);
    }

    /**
     * Removes the specified prefix from the start of the string, ignoring case.
     *
     * @param s
     *            the string to modify
     * @param prefix
     *            the prefix to remove
     * @return the modified string with the prefix removed, or the original string if it did not start with the prefix;
     */

    @Nullable
    public static String removeStartIgnoreCase( @Nullable final String s, @Nullable final String prefix )
    {
        if( startsWithIgnoreCase(s, prefix) ) {
            return s.substring(prefix.length());
        }
        return s;
    }

    /**
     * Removes the specified suffix from the end of the string, ignoring case.
     *
     * @param s
     *            the string to modify
     * @param prefix
     *            the suffix to remove
     * @return the modified string with the suffix removed, or the original string if it did not end with the suffix;
     */
    @Nonnull
    public static String removeEndIgnoreCase( @Nonnull final String s, @Nonnull final String prefix )
    {
        if( endsWithIgnoreCase(s, prefix) ) {
            return s.substring(0, s.length() - prefix.length());
        }
        return s;
    }

    /**
     * Checks if the given string is blank or empty.
     *
     * @param s
     *            the string to check
     * @return true if the string is null, empty, or contains only whitespace characters; false otherwise
     */
    public static boolean isBlankOrEmpty( @Nullable final String s )
    {
        return s == null || s.trim().isEmpty();
    }

    /**
     * Checks if the given CharSequence is blank or empty.
     *
     * @param s
     *            the CharSequence to check
     * @return true if the CharSequence is null, empty, or contains only whitespace characters; false otherwise
     */
    public static boolean isBlankOrEmpty( @Nullable final CharSequence s )
    {
        return s != null && isBlankOrEmpty(s.toString());
    }

    /**
     * Capitalizes the first character of the given string.
     *
     * @param s
     *            the string to capitalize
     * @return the string with the first character capitalized, or null if the input is null or empty
     */
    @Nullable
    public static String capitalize( @Nullable final String s )
    {
        if( s == null || s.isEmpty() ) {
            return s;
        }
        return s.substring(0, 1).toUpperCase(Locale.ROOT) + s.substring(1);
    }

    /**
     * Uncapitalizes the first character of the given string.
     *
     * @param s
     *            the string to uncapitalize
     * @return the string with the first character uncapitalized, or null if the input is null or empty
     */
    @Nonnull
    public static String uncapitalize( @Nonnull final String s )
    {
        if( s.isEmpty() ) {
            return s;
        }
        return s.substring(0, 1).toLowerCase(Locale.ROOT) + s.substring(1);
    }

    /**
     * Trims the given string and returns null if the result is empty.
     *
     * @param s
     *            the string to trim
     * @return the trimmed string, or null if the input is null or empty after trimming
     */
    @Nullable
    public static String trimToNull( @Nullable String s )
    {
        return s == null ? null : (s = s.trim()).isEmpty() ? null : s;
    }

    /**
     * Prepends the specified prefix to the string if it does not already start with that prefix, ignoring case.
     *
     * @param s
     *            the string to modify
     * @param prefix
     *            the prefix to prepend
     * @return the modified string with the prefix prepended if it was not already present; otherwise, the original
     *         string
     */
    @Nonnull
    public static String prependIfMissing( @Nonnull final String s, @Nonnull final String prefix )
    {
        return startsWithIgnoreCase(s, prefix) ? s : prefix + s;
    }

    /**
     * Returns the substring of the given string before the first occurrence of the specified suffix character.
     *
     * @param string
     *            the string to search
     * @param suffix
     *            the character to search for
     * @return the substring before the first occurrence of the suffix character, or the original string if the suffix
     *         is not found;
     */
    @Nullable
    public static String substringBefore( @Nullable final String string, final char suffix )
    {
        if( string == null ) {
            return string;
        }
        final int pos = string.indexOf(suffix);
        return pos >= 0 ? string.substring(0, pos) : string;
    }
}
