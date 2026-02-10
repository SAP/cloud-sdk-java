package com.sap.cloud.sdk.cloudplatform.connectivity;

import javax.annotation.Nullable;

/**
 * Interface to be implemented by classes that can provide information about the behalf upon which an action is run.
 *
 * @since 4.27.0
 */
interface IsOnBehalfOf
{
    /**
     * Returns the behalf upon which an action is run.
     *
     * @return The behalf upon which an action is run, or {@code null} if no information about the behalf is available.
     */
    @Nullable
    default OnBehalfOf getOnBehalfOf()
    {
        return null;
    }
}
