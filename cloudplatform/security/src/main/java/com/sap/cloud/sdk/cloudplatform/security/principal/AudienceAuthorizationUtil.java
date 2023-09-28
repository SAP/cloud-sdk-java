package com.sap.cloud.sdk.cloudplatform.security.principal;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;

@Deprecated
class AudienceAuthorizationUtil
{
    static
        Map<com.sap.cloud.sdk.cloudplatform.security.Audience, Set<com.sap.cloud.sdk.cloudplatform.security.Authorization>>
        getAuthorizationsByAudience(
            @Nonnull final Set<com.sap.cloud.sdk.cloudplatform.security.Audience> audiences,
            @Nonnull final Set<com.sap.cloud.sdk.cloudplatform.security.Authorization> authorizations )
    {
        final Set<com.sap.cloud.sdk.cloudplatform.security.Audience> modifiedAudiences;

        if( audiences.isEmpty() ) {
            modifiedAudiences =
                authorizations
                    .stream()
                    .map(com.sap.cloud.sdk.cloudplatform.security.Authorization::getName)
                    .map(
                        authorization -> authorization.contains(".")
                            ? authorization.substring(0, authorization.indexOf("."))
                            : authorization)
                    .map(com.sap.cloud.sdk.cloudplatform.security.Audience::new)
                    .collect(Collectors.toSet());
        } else {
            modifiedAudiences =
                audiences
                    .stream()
                    .map(com.sap.cloud.sdk.cloudplatform.security.Audience::getAudience)
                    .map(audience -> audience.contains(".") ? audience.substring(0, audience.indexOf(".")) : audience)
                    .map(com.sap.cloud.sdk.cloudplatform.security.Audience::new)
                    .collect(Collectors.toSet());
        }

        final Map<com.sap.cloud.sdk.cloudplatform.security.Audience, Set<com.sap.cloud.sdk.cloudplatform.security.Authorization>> authorizationsByAudience =
            Maps.newHashMap();

        modifiedAudiences
            .stream()
            .map(com.sap.cloud.sdk.cloudplatform.security.Audience::getAudience)
            .forEach(audience -> {
                final String authorizationPrefix = audience.endsWith(".") ? audience : audience + ".";

                final Set<com.sap.cloud.sdk.cloudplatform.security.Authorization> authorizationsForAudience =
                    authorizations
                        .stream()
                        .map(com.sap.cloud.sdk.cloudplatform.security.Authorization::getName)
                        .filter(authorization -> authorization.startsWith(authorizationPrefix))
                        .map(authorization -> StringUtils.removeStart(authorization, audience + "."))
                        .map(com.sap.cloud.sdk.cloudplatform.security.Authorization::new)
                        .collect(Collectors.toSet());

                authorizationsByAudience
                    .put(new com.sap.cloud.sdk.cloudplatform.security.Audience(audience), authorizationsForAudience);
            });

        return authorizationsByAudience;
    }
}
