package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationRetrievalStrategy.TokenForwarding.NONE;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationRetrievalStrategy.TokenForwarding.REFRESH_TOKEN;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationRetrievalStrategy.TokenForwarding.USER_TOKEN;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Accessors( fluent = true )
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
@EqualsAndHashCode
@ToString
final class DestinationRetrievalStrategy
{
    @Nonnull
    private final OnBehalfOf behalf;
    @Nonnull
    private final TokenForwarding tokenForwarding;
    @Nullable
    @ToString.Exclude
    private final String token;

    static DestinationRetrievalStrategy withoutToken( @Nonnull final OnBehalfOf behalf )
    {
        return new DestinationRetrievalStrategy(behalf, NONE, null);
    }

    static DestinationRetrievalStrategy withUserToken( @Nonnull final OnBehalfOf behalf, @Nonnull final String token )
    {
        Objects.requireNonNull(token, "User token must not be null.");
        return new DestinationRetrievalStrategy(behalf, USER_TOKEN, token);
    }

    static
        DestinationRetrievalStrategy
        withRefreshToken( @Nonnull final OnBehalfOf behalf, @Nonnull final String token )
    {
        Objects.requireNonNull(token, "Refresh token must not be null.");
        return new DestinationRetrievalStrategy(behalf, REFRESH_TOKEN, token);
    }

    enum TokenForwarding
    {
        USER_TOKEN,
        REFRESH_TOKEN,
        NONE
    }
}
