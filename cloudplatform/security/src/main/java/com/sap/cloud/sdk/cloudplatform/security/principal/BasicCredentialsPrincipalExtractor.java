package com.sap.cloud.sdk.cloudplatform.security.principal;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.security.BasicAuthenticationAccessor;
import com.sap.cloud.sdk.cloudplatform.security.BasicCredentials;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class BasicCredentialsPrincipalExtractor implements PrincipalExtractor
{
    @Override
    @Nonnull
    public Try<Principal> tryGetCurrentPrincipal()
    {
        return BasicAuthenticationAccessor
            .tryGetCurrentBasicCredentials()
            .map(BasicCredentials::getUsername)
            .map(this::toPrincipal);
    }

    private Principal toPrincipal( @Nonnull final String userName )
    {
        log.debug("Extracted principal '{}' from the current Basic Credentials.", userName);
        return new DefaultPrincipal(userName);
    }
}
