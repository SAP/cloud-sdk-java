package com.sap.cloud.sdk.cloudplatform.security.principal;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.CloudPlatformAccessor;

import io.vavr.control.Try;

/**
 * This represents the information available an authenticated entity.
 * <p>
 * In case of basic authentication this will directly represent a user, in case of client credentials this is the
 * client.
 */
public interface Principal
{
    /**
     * The identifier for this Principal.
     *
     * @return The identifier.
     */
    @Nonnull
    String getPrincipalId();

    /**
     * The authorizations this Principal has.
     *
     * @return A {@code Set} with the authentications.
     */
    @Deprecated
    @Nonnull
    Set<com.sap.cloud.sdk.cloudplatform.security.Authorization> getAuthorizations();

    /**
     * The authorizations this Principal has grouped by audience.
     *
     * @return A {@code Map} with the authentications grouped by audience.
     */
    @Deprecated
    @Nonnull
    default
        Map<com.sap.cloud.sdk.cloudplatform.security.Audience, Set<com.sap.cloud.sdk.cloudplatform.security.Authorization>>
        getAuthorizationsByAudience()
    {
        final String applicationName = CloudPlatformAccessor.getCloudPlatform().getApplicationName();

        return Collections
            .singletonMap(new com.sap.cloud.sdk.cloudplatform.security.Audience(applicationName), getAuthorizations());
    }

    /**
     * Returns the attribute specified by the given name, wrapped in a {@code Try}.
     *
     * @param attributeName
     *            The name of the attribute to get.
     *
     * @return The attribute of a user by the attribute name.
     */
    @Deprecated
    @Nonnull
    Try<PrincipalAttribute> getAttribute( @Nonnull final String attributeName );
}
