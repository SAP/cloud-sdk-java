package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationRetrievalStrategy.TokenForwarding.NONE;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationRetrievalStrategy.TokenForwarding.REFRESH_TOKEN;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationRetrievalStrategy.TokenForwarding.USER_TOKEN;

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
    @Nullable
    private String fragment;

    static DestinationRetrievalStrategy withoutToken( @Nonnull final OnBehalfOf behalf )
    {
        return new DestinationRetrievalStrategy(behalf, NONE, null);
    }

    static DestinationRetrievalStrategy withUserToken( @Nonnull final OnBehalfOf behalf, @Nonnull final String token )
    {
        if( token.isBlank() ) {
            throw new IllegalArgumentException("User token must not be empty.");
        }

        return new DestinationRetrievalStrategy(behalf, USER_TOKEN, token);
    }

    static
        DestinationRetrievalStrategy
        withRefreshToken( @Nonnull final OnBehalfOf behalf, @Nonnull final String token )
    {
        if( token.isBlank() ) {
            throw new IllegalArgumentException("Refresh token must not be empty.");
        }
        return new DestinationRetrievalStrategy(behalf, REFRESH_TOKEN, token);
    }

    DestinationRetrievalStrategy withFragmentName( @Nonnull final String fragmentName )
    {
        if( fragmentName.isBlank() ) {
            throw new IllegalArgumentException("Fragment name must not be empty");
        }
        // sanity check to enforce this is only ever set once
        if( fragment != null ) {
            throw new IllegalStateException("Attempted to change an already set fragment name");
        }
        fragment = fragmentName;
        return this;
    }

    enum TokenForwarding
    {
        USER_TOKEN,
        REFRESH_TOKEN,
        NONE
    }
}
