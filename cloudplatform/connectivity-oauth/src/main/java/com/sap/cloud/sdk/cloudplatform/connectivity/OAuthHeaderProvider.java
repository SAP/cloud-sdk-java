package com.sap.cloud.sdk.cloudplatform.connectivity;

import static java.util.Collections.singletonList;

import java.util.List;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class OAuthHeaderProvider implements DestinationHeaderProvider
{
    static final String PROPERTY_OAUTH2_RESILIENCE_CONFIG = "oauth-resilience-config";
    @Nonnull
    private final OAuth2ServiceImpl oauth2service;
    @Nonnull
    private final OnBehalfOf behalf;
    @Nonnull
    private final String authHeaderName;

    @Nonnull
    @Override
    public List<Header> getHeaders( @Nonnull final DestinationRequestContext requestContext )
    {
        final DestinationProperties destination = requestContext.getDestination();
        final Option<ResilienceConfiguration> resilienceConfig =
            destination.get(PROPERTY_OAUTH2_RESILIENCE_CONFIG, ResilienceConfiguration.class);

        final String accessToken =
            oauth2service.retrieveAccessToken(behalf, resilienceConfig.getOrElseThrow(IllegalStateException::new));

        return singletonList(new Header(authHeaderName, "Bearer " + accessToken));
    }
}
