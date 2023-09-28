package com.sap.cloud.sdk.cloudplatform.security;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.base.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Class providing access to the authorization token of a request.
 */
@RequiredArgsConstructor
public class AuthToken
{
    /**
     * The {@link DecodedJWT} bearer contained in the Authorization header of the given request.
     */
    @Getter
    @Nonnull
    private final DecodedJWT jwt;

    @Override
    public boolean equals( @Nullable final Object o )
    {
        if( this == o ) {
            return true;
        }
        if( o == null || getClass() != o.getClass() ) {
            return false;
        }
        final AuthToken authToken = (AuthToken) o;
        return Objects.equal(jwt.getHeader(), authToken.jwt.getHeader())
            && Objects.equal(jwt.getPayload(), authToken.jwt.getPayload())
            && Objects.equal(jwt.getSignature(), authToken.jwt.getSignature());
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(jwt.getHeader(), jwt.getPayload(), jwt.getSignature());
    }

    // TODO create builder / constructor
}
