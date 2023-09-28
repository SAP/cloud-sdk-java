package com.sap.cloud.sdk.testutil;

import java.security.KeyStore;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.security.secret.SecretStore;

interface SecretStoreMocker
{
    /**
     * Mocks a {@link SecretStore} for a given instance.
     *
     * @param name
     *            The name of the {@link SecretStore}.
     * @param secretStore
     *            The instance of {@link SecretStore}.
     */
    void mockSecretStore( @Nonnull final String name, @Nonnull final SecretStore secretStore );

    /**
     * Mocks a {@link SecretStore} for a given password.
     *
     * @param name
     *            The name of the {@link SecretStore}.
     * @param password
     *            The password of the {@link SecretStore}.
     */
    @Nonnull
    SecretStore mockSecretStore( @Nonnull final String name, @Nonnull final String password );

    /**
     * Mocks a {@link KeyStore} for a given instance.
     *
     * @param name
     *            The name of the {@link KeyStore}.
     * @param password
     *            The password of the {@link KeyStore} as {@link SecretStore}.
     * @param keyStore
     *            The instance of {@link KeyStore}.
     */
    void mockKeyStore(
        @Nonnull final String name,
        @Nonnull final SecretStore password,
        @Nonnull final KeyStore keyStore );

    /**
     * Mocks a {@link KeyStore} for a given instance.
     *
     * @param name
     *            The name of the {@link KeyStore}.
     * @param password
     *            The password of the {@link KeyStore} as String.
     * @param keyStore
     *            The instance of {@link KeyStore}.
     */
    void mockKeyStore( @Nonnull final String name, @Nonnull final String password, @Nonnull final KeyStore keyStore );

    /**
     * Mocks a {@link KeyStore} from a key store resource file.
     *
     * @param name
     *            The name of the {@link KeyStore}.
     * @param password
     *            The password of the {@link KeyStore} as {@link SecretStore}.
     * @param keyStoreFileName
     *            The file name pointing to the {@link KeyStore} to be loaded.
     * @param keyStoreType
     *            The type of the {@link KeyStore} to be loaded.
     */
    @Nonnull
    KeyStore mockKeyStore(
        @Nonnull final String name,
        @Nonnull final SecretStore password,
        @Nonnull final String keyStoreFileName,
        @Nonnull final String keyStoreType );

    /**
     * Mocks a {@link KeyStore} from a key store resource file.
     *
     * @param name
     *            The name of the {@link KeyStore}.
     * @param password
     *            The password of the {@link KeyStore} as String.
     * @param keyStoreFileName
     *            The file name pointing to the {@link KeyStore} to be loaded.
     * @param keyStoreType
     *            The type of the {@link KeyStore} to be loaded.
     */
    @Nonnull
    KeyStore mockKeyStore(
        @Nonnull final String name,
        @Nonnull final String password,
        @Nonnull final String keyStoreFileName,
        @Nonnull final String keyStoreType );

    /**
     * Clears all previously mocked {@link SecretStore}s.
     */
    void clearSecretStores();

    /**
     * Clears all previously mocked {@link KeyStore}s.
     */
    void clearKeyStores();
}
