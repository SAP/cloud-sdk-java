/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security.secret;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.exception.UnsupportedCloudFeatureException;

/**
 * Implementation of {@link SecretStore} for SAP Business Technology Platform Cloud Foundry.
 * <p>
 * Note: This functionality is not yet supported.
 */
public class ScpCfSecretStore implements SecretStore
{
    /**
     * Creates a mocked {@link ScpCfSecretStore}.
     * <p>
     * This no-arguments constructor is required to ensure compatibility with mocking frameworks such as Mockito.
     */
    protected ScpCfSecretStore()
    {

    }

    @Override
    @Nonnull
    public char[] getSecret()
    {
        throw new UnsupportedCloudFeatureException(
            "Secret stores are not yet supported on SAP Business Technology Platform Cloud Foundry.");
    }
}
