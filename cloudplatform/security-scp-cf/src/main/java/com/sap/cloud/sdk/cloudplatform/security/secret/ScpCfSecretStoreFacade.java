/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security.secret;

import java.security.KeyStore;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.exception.UnsupportedCloudFeatureException;

import io.vavr.control.Try;

/**
 * Implementation of {@link SecretStoreFacade} for SAP Business Technology Platform Cloud Foundry.
 * <p>
 * Note: This functionality is not yet supported.
 */
public class ScpCfSecretStoreFacade implements SecretStoreFacade
{
    @Nonnull
    @Override
    public Try<SecretStore> tryGetSecretStore( @Nonnull final String name )
    {
        return Try
            .failure(
                new UnsupportedCloudFeatureException(
                    "Secret stores are not yet supported on SAP Business Technology Platform Cloud Foundry."));
    }

    @Nonnull
    @Override
    public Try<KeyStore> tryGetKeyStore( @Nonnull final String name, @Nonnull final SecretStore password )
    {
        return Try
            .failure(
                new UnsupportedCloudFeatureException(
                    "Key stores are not yet supported on SAP Business Technology Platform Cloud Foundry."));
    }
}
