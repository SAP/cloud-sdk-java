package com.sap.cloud.sdk.cloudplatform.security.principal;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sap.cloud.sdk.cloudplatform.security.principal.exception.PrincipalAttributeException;

import io.vavr.control.Try;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * This implementation of {@link Principal} represents the information available for an authenticated entity.
 */
@Data
@RequiredArgsConstructor
@SuppressWarnings( "deprecation" )
public class DefaultPrincipal implements Principal
{
    @Getter
    @Nonnull
    private final String principalId;

    @Deprecated
    @Nonnull
    private final Set<com.sap.cloud.sdk.cloudplatform.security.Authorization> localAuthorizations;

    @Deprecated
    @Nonnull
    private final Map<String, PrincipalAttribute> attributes;

    @Deprecated
    @Nonnull
    Map<com.sap.cloud.sdk.cloudplatform.security.Audience, Set<com.sap.cloud.sdk.cloudplatform.security.Authorization>> authorizationsByAudience =
        Maps.newHashMap();

    /**
     * Creates a new {@link DefaultPrincipal}.
     *
     * @param principalId
     *            The ID of the principal
     * @since 4.24.0
     */
    public DefaultPrincipal( @Nonnull final String principalId )
    {
        this.principalId = principalId;
        localAuthorizations = Collections.emptySet();
        attributes = Collections.emptyMap();
    }

    /**
     * Creates a new {@link DefaultPrincipal}.
     *
     * @param principalId
     *            The ID of the principal
     * @param localAuthorizations
     *            The local authorizations of the principal
     * @param allAuthorizations
     *            All authorizations of the principal
     * @param audiences
     *            The audiences of the principal
     * @param attributes
     *            The attributes of the principal
     */
    @Deprecated
    public DefaultPrincipal(
        @Nonnull final String principalId,
        @Nonnull final Set<com.sap.cloud.sdk.cloudplatform.security.Authorization> localAuthorizations,
        @Nonnull final Set<com.sap.cloud.sdk.cloudplatform.security.Authorization> allAuthorizations,
        @Nonnull final Set<com.sap.cloud.sdk.cloudplatform.security.Audience> audiences,
        @Nonnull final Map<String, PrincipalAttribute> attributes )
    {
        this.principalId = principalId;
        this.localAuthorizations = localAuthorizations;
        this.attributes = attributes;
        this.authorizationsByAudience =
            AudienceAuthorizationUtil.getAuthorizationsByAudience(audiences, allAuthorizations);
    }

    @Deprecated
    @Nonnull
    @Override
    public Set<com.sap.cloud.sdk.cloudplatform.security.Authorization> getAuthorizations()
    {
        return Sets.newHashSet(localAuthorizations);
    }

    @Deprecated
    @Nonnull
    @Override
    public
        Map<com.sap.cloud.sdk.cloudplatform.security.Audience, Set<com.sap.cloud.sdk.cloudplatform.security.Authorization>>
        getAuthorizationsByAudience()
    {
        return Maps.newHashMap(authorizationsByAudience);
    }

    @Deprecated
    @Nonnull
    @Override
    public Try<PrincipalAttribute> getAttribute( @Nonnull final String attributeName )
    {
        @Nullable
        final PrincipalAttribute attribute = attributes.get(attributeName);

        if( attribute == null ) {
            return Try
                .failure(new PrincipalAttributeException("No attribute found with name '" + attributeName + "'."));
        }

        return Try.success(attribute);
    }
}
