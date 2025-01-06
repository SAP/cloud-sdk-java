package com.sap.cloud.sdk.datamodel.odata.client.request;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.vavr.control.Option;

/**
 * Strategy options for sending IF-MATCH headers.
 */
public enum ETagSubmissionStrategy
{
    /**
     * Send an IF-MATCH header, if and only if a version identifier is defined on an {@code VdmEntity}.
     */
    SUBMIT_ETAG_FROM_ENTITY,
    /**
     * Do not send any IF-MATCH header.
     */
    SUBMIT_NO_ETAG,
    /**
     * Send a wildcard ({@code *}) in the IF-MATCH header matching all version identifiers. This is essentially a force
     * overwrite.
     */
    SUBMIT_ANY_MATCH_ETAG;

    /**
     * Returns the value of the IF-MATCH header to be sent based on the given {@code maybeVersionIdentifier}.
     *
     * @param maybeVersionIdentifier
     *            The version identifier to be sent in the IF-MATCH header.
     * @return The value of the IF-MATCH header to be sent, or {@code null} if no header should be sent.
     */
    @Nullable
    public String getHeaderFromVersionIdentifier( @Nonnull final Option<String> maybeVersionIdentifier )
    {
        switch( this ) {
            case SUBMIT_ANY_MATCH_ETAG:
                return "*";
            case SUBMIT_NO_ETAG:
                return null;
            case SUBMIT_ETAG_FROM_ENTITY:
            default:
                return maybeVersionIdentifier.filter(s -> !s.isEmpty()).getOrNull();
        }
    }
}
