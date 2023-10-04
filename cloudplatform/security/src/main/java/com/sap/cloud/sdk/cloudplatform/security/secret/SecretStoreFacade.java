/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security.secret;

import java.security.KeyStore;

import javax.annotation.Nonnull;

import io.vavr.control.Try;

/**
 * Facade interface encapsulating the access to concrete {@link SecretStore} and {@link KeyStore} classes.
 */
public interface SecretStoreFacade
{
    /**
     * Retrieves a {@link SecretStore} by its name.
     *
     * @param name
     *            The name identifying a {@link SecretStore}.
     *
     * @return A {@link Try} of the {@link SecretStore} for the given name.
     */
    @Nonnull
    Try<SecretStore> tryGetSecretStore( @Nonnull final String name );

    /**
     * Retrieves a {@link KeyStore} by its name.
     *
     * @param name
     *            The name identifying a {@link KeyStore}.
     *
     * @param password
     *            The password to unlock the {@link KeyStore}.
     *
     * @return The {@link KeyStore} for the given name.
     */
    @Nonnull
    Try<KeyStore> tryGetKeyStore( @Nonnull final String name, @Nonnull final SecretStore password );
}
