/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import javax.annotation.Nonnull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Representation of a OData query parameter for Search Modifier
 */
@RequiredArgsConstructor
public class SearchExpression
{
    @Nonnull
    @Getter( AccessLevel.PROTECTED )
    private final String term;

    /**
     * Create a search expression for a single string.
     *
     * @param term
     *            The search string
     * @return Search Expression with a single search string.
     */
    @Nonnull
    public static SearchExpression of( @Nonnull final String term )
    {
        return new SearchExpression(getDoubleQuotedString(term));
    }

    /**
     * Combine current string with another search string in conjunction.
     *
     * @param term
     *            The other search string.
     * @return Search Expression with a conjunction string.
     */
    @Nonnull
    public SearchExpression and( @Nonnull final String term )
    {
        return new SearchExpression("(" + this.term + " AND " + getDoubleQuotedString(term) + ")");
    }

    /**
     * Combine current search expression with another search expression in conjunction.
     *
     * @param searchExpression
     *            The other search expression.
     * @return Search Expression with a conjunction.
     */
    @Nonnull
    public SearchExpression and( @Nonnull final SearchExpression searchExpression )
    {
        return new SearchExpression("(" + this.term + " AND " + searchExpression.getTerm() + ")");
    }

    /**
     * Combine current string with another search string in disjunction.
     *
     * @param term
     *            The other search string.
     * @return Search Expression with a disjunction string.
     */
    @Nonnull
    public SearchExpression or( @Nonnull final String term )
    {
        return new SearchExpression("(" + this.term + " OR " + getDoubleQuotedString(term) + ")");
    }

    /**
     * Combine current search expression with another search expression in disjunction.
     *
     * @param searchExpression
     *            The other search expression.
     * @return Search Expression with a disjunction.
     */
    @Nonnull
    public SearchExpression or( @Nonnull final SearchExpression searchExpression )
    {
        return new SearchExpression("(" + this.term + " OR " + searchExpression.getTerm() + ")");
    }

    /**
     * Negate the current search expression.
     *
     * @return Modified search expression with negation.
     */
    @Nonnull
    public SearchExpression not()
    {
        return new SearchExpression("NOT " + this.term);
    }

    /**
     * Escape and encapsulate String literal.
     *
     * @param text
     *            The String to be quoted.
     * @return The prepared String.
     */
    @Nonnull
    static String getDoubleQuotedString( @Nonnull final String text )
    {
        if( text.contains("&") ) {
            throw new IllegalArgumentException("Search literal contains a forbidden character '&'.");
        }

        // escape backslash \ -> \\
        String encodedText = text.replaceAll("\\\\", "$0$0");

        // escape double quotes " -> \"
        encodedText = encodedText.replaceAll("\"", "\\\\$0");

        // wrap escaped string into double quotes
        return "\"" + encodedText + "\"";
    }
}
