package com.sap.cloud.sdk.cloudplatform.security.principal;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * An implementation of the {@code Principal} interface for SAP Business Technology Platform Cloud Foundry.
 *
 * @deprecated Please use {@code DefaultPrincipal} instead.
 */
@Deprecated
@EqualsAndHashCode( callSuper = true )
@ToString( callSuper = true )
public class ScpCfPrincipal extends DefaultPrincipal
{
    /**
     * Creates a new {@link ScpCfPrincipal}.
     *
     * @param principalId
     *            The identifier of the principal.
     * @param authorizations
     *            The authorizations of the principal.
     * @param attributes
     *            The attributes of the principal.
     */
    public ScpCfPrincipal(
        @Nonnull final String principalId,
        @Nonnull final Set<com.sap.cloud.sdk.cloudplatform.security.Authorization> authorizations,
        @Nonnull final Map<String, PrincipalAttribute> attributes )
    {
        this(principalId, authorizations, authorizations, Collections.emptySet(), attributes);
    }

    ScpCfPrincipal(
        @Nonnull final String principalId,
        @Nonnull final Set<com.sap.cloud.sdk.cloudplatform.security.Authorization> localAuthorizations,
        @Nonnull final Set<com.sap.cloud.sdk.cloudplatform.security.Authorization> allAuthorizations,
        @Nonnull final Set<com.sap.cloud.sdk.cloudplatform.security.Audience> audiences,
        @Nonnull final Map<String, PrincipalAttribute> attributes )
    {
        super(principalId, localAuthorizations, allAuthorizations, audiences, attributes);
    }

    /**
     * Creates a {@link ScpCfPrincipal} with an empty principal identifier, no authorizations, and no attributes.
     */
    public ScpCfPrincipal()
    {
        this("", Collections.emptySet(), Collections.emptyMap());
    }
}
