package com.sap.cloud.sdk.cloudplatform.security.principal;

import javax.annotation.Nonnull;

import io.vavr.control.Try;

@FunctionalInterface
interface PrincipalExtractor
{
    @Nonnull
    Try<Principal> tryGetCurrentPrincipal();
}
