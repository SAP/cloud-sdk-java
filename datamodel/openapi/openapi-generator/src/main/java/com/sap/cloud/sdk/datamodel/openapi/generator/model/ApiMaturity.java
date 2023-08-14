package com.sap.cloud.sdk.datamodel.openapi.generator.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents the maturity of the API to generate code for. Reflects if the API is released for productive usage or
 * rather in an experimental Beta state.
 */
@Slf4j
@RequiredArgsConstructor
public enum ApiMaturity
{
    /**
     * The API is released for productive usage
     */
    RELEASED("released"),
    /**
     * The API is an experimental Beta state and not recommended for productive usage
     */
    BETA("beta");

    /**
     * The fallback API maturity, which is used whenever nothing else is specified.
     */
    @Nonnull
    public static final ApiMaturity DEFAULT = RELEASED;

    private final String identifier;

    /**
     * Returns the enum value for the given identifier.
     *
     * Returns the default {@link ApiMaturity#RELEASED} if no corresponding value is found.
     *
     * @param identifier
     *            The String identifier
     * @return The corresponding enum value or the default value
     */
    @Nonnull
    public static ApiMaturity getValueOrDefault( @Nullable final String identifier )
    {
        if( identifier == null ) {
            log.info("No ApiMaturity specified. Falling back to \"{}\" (default).", DEFAULT.identifier);
            return DEFAULT;
        }

        for( final ApiMaturity apiMaturity : values() ) {
            if( apiMaturity.identifier.equals(identifier) ) {
                return apiMaturity;
            }
        }

        throw new IllegalArgumentException(
            String
                .format(
                    "\"%s\" is not a valid ApiMaturity. Valid values are \"%s\" (default) and \"%s\".",
                    identifier,
                    RELEASED.identifier,
                    BETA.identifier));
    }

    @Override
    public String toString()
    {
        return identifier;
    }
}
