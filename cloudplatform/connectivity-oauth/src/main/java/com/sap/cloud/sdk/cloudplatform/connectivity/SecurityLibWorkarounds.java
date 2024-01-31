package com.sap.cloud.sdk.cloudplatform.connectivity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.security.config.CredentialType;

final class SecurityLibWorkarounds
{
    private static final String X509_GENERATED = "X509_GENERATED";

    private SecurityLibWorkarounds()
    {
        throw new IllegalStateException("This utility class should never be instantiated.");
    }

    @Nullable
    static CredentialType getCredentialType( @Nonnull final String rawType )
    {
        if( rawType.equalsIgnoreCase(X509_GENERATED) ) {
            // this particular credential type is currently (2024-01-31) NOT supported by the Security Client Lib.
            return CredentialType.X509;
        }

        return CredentialType.from(rawType);
    }
}
