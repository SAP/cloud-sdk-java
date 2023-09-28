package com.sap.cloud.sdk.testutil;

import java.io.InputStream;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.mockito.Mockito;

import com.sap.cloud.sdk.cloudplatform.security.secret.SecretStore;
import com.sap.cloud.sdk.cloudplatform.security.secret.SecretStoreFacade;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
class DefaultSecretStoreMocker implements SecretStoreMocker
{
    private final Supplier<SecretStoreFacade> resetSecretStoreFacade;

    @Getter( AccessLevel.PACKAGE )
    private final Map<String, SecretStore> secretStoresByName = new HashMap<>();

    @Getter( AccessLevel.PACKAGE )
    private final Map<String, KeyStoreWithPassword> keyStoresByName = new HashMap<>();

    @Data
    static class KeyStoreWithPassword
    {
        @Nonnull
        private final KeyStore keyStore;

        @Nonnull
        private final String password;
    }

    private void resetFacade()
    {
        resetSecretStoreFacade.get();
    }

    @Override
    public void mockSecretStore( @Nonnull final String name, @Nonnull final SecretStore secretStore )
    {
        resetFacade();
        secretStoresByName.put(name, secretStore);
    }

    @Nonnull
    @Override
    public SecretStore mockSecretStore( @Nonnull final String name, @Nonnull final String password )
    {
        resetFacade();

        final SecretStore secretStore = Mockito.mock(SecretStore.class);
        Mockito.when(secretStore.getSecret()).thenReturn(password.toCharArray());

        secretStoresByName.put(name, secretStore);
        return secretStore;
    }

    @Override
    public void mockKeyStore(
        @Nonnull final String name,
        @Nonnull final SecretStore password,
        @Nonnull final KeyStore keyStore )
    {
        mockKeyStore(name, String.valueOf(password.getSecret()), keyStore);
    }

    @Override
    public
        void
        mockKeyStore( @Nonnull final String name, @Nonnull final String password, @Nonnull final KeyStore keyStore )
    {
        resetFacade();
        keyStoresByName.put(name, new KeyStoreWithPassword(keyStore, password));
    }

    @Nonnull
    @Override
    public KeyStore mockKeyStore(
        @Nonnull final String name,
        @Nonnull final SecretStore password,
        @Nonnull final String keyStoreFileName,
        @Nonnull final String keyStoreType )
    {
        return mockKeyStore(name, String.valueOf(password.getSecret()), keyStoreFileName, keyStoreType);
    }

    @Nonnull
    @Override
    public KeyStore mockKeyStore(
        @Nonnull final String name,
        @Nonnull final String password,
        @Nonnull final String keyStoreFileName,
        @Nonnull final String keyStoreType )
    {
        resetFacade();

        final ClassLoader classLoader = getClass().getClassLoader();
        if( classLoader == null ) {
            throw new TestConfigurationError(
                "Failed to get ClassLoader in " + getClass().getName() + " for accessing local resources.");
        }
        try( InputStream inputStream = classLoader.getResourceAsStream(keyStoreFileName) ) {
            final KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(inputStream, password.toCharArray());

            keyStoresByName.put(name, new KeyStoreWithPassword(keyStore, password));
            return keyStore;
        }
        catch( final Exception e ) {
            throw new TestConfigurationError(e);
        }
    }

    @Override
    public void clearSecretStores()
    {
        resetFacade();
        secretStoresByName.clear();
    }

    @Override
    public void clearKeyStores()
    {
        resetFacade();
        keyStoresByName.clear();
    }
}
