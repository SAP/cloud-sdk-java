package com.sap.cloud.sdk.cloudplatform.security.secret;

import java.security.KeyStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.security.secret.exception.KeyStoreAccessException;
import com.sap.cloud.sdk.cloudplatform.security.secret.exception.SecretStoreAccessException;
import com.sap.cloud.sdk.cloudplatform.util.FacadeLocator;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Accessor for retrieving secrets and key stores.
 */
@NoArgsConstructor( access = AccessLevel.PRIVATE )
public final class SecretStoreAccessor
{
    @Nonnull
    private static Try<SecretStoreFacade> secretStoreFacade = FacadeLocator.getFacade(SecretStoreFacade.class);

    /**
     * Returns the {@link SecretStoreFacade} instance.
     *
     * @return The {@link SecretStoreFacade} instance, or {@code null}.
     */
    @Nullable
    public static SecretStoreFacade getSecretStoreFacade()
    {
        return secretStoreFacade.getOrNull();
    }

    /**
     * Returns a {@link Try} of the {@link SecretStoreFacade} instance.
     *
     * @return A {@link Try} of the {@link SecretStoreFacade} instance.
     */
    @Nonnull
    public static Try<SecretStoreFacade> tryGetSecretStoreFacade()
    {
        return secretStoreFacade;
    }

    /**
     * Replaces the default {@link SecretStoreFacade} instance.
     *
     * @param secretStoreFacade
     *            An instance of {@link SecretStoreFacade}. Use {@code null} to reset the facade.
     */
    public static void setSecretStoreFacade( @Nullable final SecretStoreFacade secretStoreFacade )
    {
        if( secretStoreFacade == null ) {
            SecretStoreAccessor.secretStoreFacade = FacadeLocator.getFacade(SecretStoreFacade.class);
        } else {
            SecretStoreAccessor.secretStoreFacade = Try.success(secretStoreFacade);
        }
    }

    /**
     * Retrieves a {@link SecretStore} by its name.
     *
     * @param name
     *            The name identifying a {@link SecretStore}.
     *
     * @return The {@link SecretStore} for the given name.
     *
     * @throws SecretStoreAccessException
     *             If there is an issue while accessing the {@link SecretStore}.
     */
    @Nonnull
    public static SecretStore getSecretStore( @Nonnull final String name )
        throws SecretStoreAccessException
    {
        return tryGetSecretStore(name).getOrElseThrow(failure -> {
            if( failure instanceof SecretStoreAccessException ) {
                throw (SecretStoreAccessException) failure;
            } else {
                throw new SecretStoreAccessException("Failed to get secret store with name'" + name + "'.", failure);
            }
        });
    }

    /**
     * Retrieves a {@link SecretStore} by its name.
     *
     * @param name
     *            The name identifying a {@link SecretStore}.
     *
     * @return A {@link Try} of the {@link SecretStore} for the given name.
     */
    @Nonnull
    public static Try<SecretStore> tryGetSecretStore( @Nonnull final String name )
    {
        return secretStoreFacade.flatMap(facade -> facade.tryGetSecretStore(name));
    }

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
     *
     * @throws KeyStoreAccessException
     *             If there is an issue while accessing the {@link KeyStore}.
     */
    @Nonnull
    public static KeyStore getKeyStore( @Nonnull final String name, @Nonnull final SecretStore password )
        throws KeyStoreAccessException
    {
        return tryGetKeyStore(name, password).getOrElseThrow(failure -> {
            if( failure instanceof SecretStoreAccessException ) {
                throw (SecretStoreAccessException) failure;
            } else {
                throw new SecretStoreAccessException("Failed to get key store with name '" + name + "'.", failure);
            }
        });
    }

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
    public static Try<KeyStore> tryGetKeyStore( @Nonnull final String name, @Nonnull final SecretStore password )
    {
        return secretStoreFacade.flatMap(facade -> facade.tryGetKeyStore(name, password));
    }
}
