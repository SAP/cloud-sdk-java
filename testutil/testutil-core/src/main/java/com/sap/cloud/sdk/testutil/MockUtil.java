/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.testutil;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.sap.cloud.sdk.cloudplatform.cache.CacheManager;
import com.sap.cloud.sdk.cloudplatform.connectivity.ProxyConfiguration;
import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAccessor;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalFacade;
import com.sap.cloud.sdk.cloudplatform.security.principal.exception.PrincipalAccessException;
import com.sap.cloud.sdk.cloudplatform.security.secret.SecretStore;
import com.sap.cloud.sdk.cloudplatform.security.secret.SecretStoreAccessor;
import com.sap.cloud.sdk.cloudplatform.security.secret.SecretStoreFacade;
import com.sap.cloud.sdk.cloudplatform.security.secret.exception.KeyStoreAccessException;
import com.sap.cloud.sdk.cloudplatform.security.secret.exception.SecretStoreAccessException;
import com.sap.cloud.sdk.cloudplatform.servlet.LocaleAccessor;
import com.sap.cloud.sdk.cloudplatform.servlet.LocaleFacade;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantFacade;
import com.sap.cloud.sdk.cloudplatform.tenant.exception.TenantAccessException;
import com.sap.cloud.sdk.testutil.DefaultSecretStoreMocker.KeyStoreWithPassword;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for mocking the cloud platform and tenant/user contexts.
 * <p>
 * <strong>Caution:</strong> This class is not thread-safe.
 * <p>
 */
@Slf4j
public class MockUtil implements LocaleMocker, TenantMocker, PrincipalMocker, SecretStoreMocker
{
    static final List<String> CONFIG_FILE_EXTENSIONS = ImmutableList.of(".yml", ".yaml", ".json");

    static final String MOCKED_TENANT = "00000000-0000-0000-0000-000000000000";
    static final String MOCKED_PRINCIPAL = "MockedUser";

    @Getter( AccessLevel.PACKAGE )
    @Nullable
    private ProxyConfiguration proxyConfiguration;

    @Getter( AccessLevel.PACKAGE )
    private LocaleFacade localeFacade;

    @Getter( AccessLevel.PACKAGE )
    private TenantFacade tenantFacade;

    @Getter( AccessLevel.PACKAGE )
    private PrincipalFacade principalFacade;

    @Getter( AccessLevel.PACKAGE )
    private SecretStoreFacade secretStoreFacade;

    @Delegate
    private final DefaultLocaleMocker localeMocker = new DefaultLocaleMocker(this::resetLocaleFacade);

    @Delegate
    private final DefaultTenantMocker tenantMocker = new DefaultTenantMocker(this::resetTenantFacade);

    @Delegate
    private final DefaultPrincipalMocker principalMocker = new DefaultPrincipalMocker(this::resetPrincipalFacade);

    @Delegate
    private final DefaultSecretStoreMocker secretStoreMocker =
        new DefaultSecretStoreMocker(this::resetSecretStoreFacade);

    /**
     * Instantiates a new instance of {@link MockUtil}, invalidates caches.
     * <p>
     * Note: To avoid potential side effects, only one instance of MockUtil should exist within a test class. It is
     * therefore recommended to maintain an instance of this class as a static member of the test class. Example usage:
     * <code>
     * <pre>
     * private static final MockUtil mockUtil = new MockUtil();
     *
     * {@literal @}BeforeClass
     * public static void beforeClass()
     * {
     *     mockUtil.mockDefaults();
     * }
     * </pre>
     * </code>
     */
    public MockUtil()
    {
        CacheManager.invalidateAll();
    }

    /**
     * Set the proxy configuration.
     *
     * @param proxyConfiguration
     *            The proxy configuration.
     * @return The MockUtil reference.
     */
    @Nonnull
    public MockUtil withProxy( @Nullable final ProxyConfiguration proxyConfiguration )
    {
        this.proxyConfiguration = proxyConfiguration;
        return this;
    }

    /**
     * Mocks common defaults for testing, in particular:
     * <ul>
     * <li>facades that are used to realize Cloud platform abstractions,</li>
     * <li>the current {@link Locale},</li>
     * <li>the current {@link Tenant},</li>
     * <li>the current {@link Principal}</li>
     * </ul>
     * This method should be called as the first method of {@link MockUtil} in the test setup method annotated with
     * <code>@BeforeClass</code>.
     */
    public void mockDefaults()
    {
        resetLocaleFacade();
        resetTenantFacade();
        resetPrincipalFacade();
        resetSecretStoreFacade();

        mockCurrentLocales();
        mockCurrentTenant();
        mockCurrentPrincipal();
    }

    private LocaleFacade resetLocaleFacade()
    {
        if( localeFacade == null ) {
            localeFacade = () -> {
                final ArrayList<Locale> result = new ArrayList<>(localeMocker.getAdditionalLocales());
                Option.of(localeMocker.getCurrentLocale()).peek(l -> result.add(0, l));
                return result;
            };
        }

        LocaleAccessor.setLocaleFacade(localeFacade);
        return localeFacade;
    }

    private TenantFacade resetTenantFacade()
    {
        if( tenantFacade == null ) {
            tenantFacade =
                () -> Try
                    .of(tenantMocker::getCurrentTenant)
                    .filter(Objects::nonNull, () -> new TenantAccessException("No current tenant mocked."));
        }
        TenantAccessor.setTenantFacade(tenantFacade);
        return tenantFacade;
    }

    private PrincipalFacade resetPrincipalFacade()
    {
        if( principalFacade == null ) {
            principalFacade =
                () -> Option
                    .of(principalMocker.getCurrentPrincipal())
                    .toTry(() -> new PrincipalAccessException("No current principal mocked."));
        }
        PrincipalAccessor.setPrincipalFacade(principalFacade);
        return principalFacade;
    }

    private SecretStoreFacade resetSecretStoreFacade()
    {
        if( secretStoreFacade == null ) {
            secretStoreFacade = new SecretStoreFacade()
            {

                @Nonnull
                @Override
                public Try<SecretStore> tryGetSecretStore( final @Nonnull String name )
                {
                    return Option
                        .of(secretStoreMocker.getSecretStoresByName().get(name))
                        .toTry(
                            () -> new SecretStoreAccessException(
                                "Failed to find secret store with name '"
                                    + name
                                    + "'. Have you mocked this secret store?"));
                }

                @Nonnull
                @Override
                public Try<KeyStore> tryGetKeyStore( final @Nonnull String name, final @Nonnull SecretStore password )
                {
                    return Option
                        .of(secretStoreMocker.getKeyStoresByName().get(name))
                        .toTry(
                            () -> new SecretStoreAccessException(
                                "Failed to find key store with name '" + name + "'. Have you mocked this key store?"))
                        .filter(
                            keyStore -> keyStore.getPassword().equals(String.valueOf(password.getSecret())),
                            () -> new KeyStoreAccessException(
                                "Failed to access key store with name '" + name + "': mocked password doesn't match."))
                        .map(KeyStoreWithPassword::getKeyStore);
                }
            };
        }

        SecretStoreAccessor.setSecretStoreFacade(secretStoreFacade);
        return secretStoreFacade;
    }
}
