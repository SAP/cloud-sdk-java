package com.sap.cloud.sdk.cloudplatform.security.principal;

import javax.annotation.Nonnull;

/**
 * This represents the information available an authenticated entity.
 * <p>
 * In case of basic authentication this will directly represent a user, in case of client credentials this is the
 * client.
 */
@FunctionalInterface
public interface Principal
{
    /**
     * The identifier for this Principal.
     *
     * @return The identifier.
     */
    @Nonnull
    String getPrincipalId();

}
