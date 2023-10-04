/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security.secret;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.security.secret.exception.SecretStoreAccessException;

/**
 * Interface encapsulating the storage of a secret char sequence.
 * <p>
 * This secret is then used to access a key in the {@link java.security.KeyStore}.
 */
public interface SecretStore
{
    /**
     * The secret char sequence stored in this {@link SecretStore}.
     *
     * @throws SecretStoreAccessException
     *             If there is an issue while accessing the secret.
     *
     * @return The secret char sequence.
     */
    @Nonnull
    char[] getSecret()
        throws SecretStoreAccessException;
}
