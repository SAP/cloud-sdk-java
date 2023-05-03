package com.sap.cloud.sdk.testutil;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.sap.cloud.sdk.cloudplatform.CloudPlatform;
import com.sap.cloud.sdk.cloudplatform.CloudPlatformAccessor;
import com.sap.cloud.sdk.cloudplatform.CloudPlatformFacade;
import com.sap.cloud.sdk.cloudplatform.auditlog.AuditLogFacade;
import com.sap.cloud.sdk.cloudplatform.cache.CacheManager;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationLoader;
import com.sap.cloud.sdk.cloudplatform.connectivity.ProxyConfiguration;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;
import com.sap.cloud.sdk.cloudplatform.exception.ObjectLookupFailedException;
import com.sap.cloud.sdk.cloudplatform.naming.JndiLookupAccessor;
import com.sap.cloud.sdk.cloudplatform.naming.JndiLookupFacade;
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
 * Utility class for mocking Cloud platform services, as well as for accessing test systems and credentials.
 * <p>
 * <strong>Caution:</strong> This class is not thread-safe.
 * <p>
 * Upon construction, this class automatically loads test systems and credentials from resource files
 * {@link #TEST_SYSTEMS_RESOURCE_FILE} and {@link #CREDENTIALS_RESOURCE_FILE} (appending the file extensions defined by
 * {@link #CONFIG_FILE_EXTENSIONS}). For examples of such files, refer to {@code loadTestSystems(File)} and
 * {@code loadCredentials(File)}.
 * <p>
 * In addition, test systems and credentials are read from Java system properties {@link #PROPERTY_TEST_SYSTEMS} and
 * {@link #PROPERTY_CREDENTIALS}. Both properties can be used to specify either the path to or the direct content of a
 * configuration file. For example, these can be specified via
 * <code>mvn clean install -Dtest.systems="..." -Dtest.credentials="..."</code> where <code>"..."</code> corresponds to
 * either the path to a configuration file or the content of such a file.
 * <p>
 * When specifying paths to test systems and credentials files, the file extension can be omitted (e.g.,
 * <code>test/resources/systems</code>). In this case, this class will scan for the existence of the following files:
 * <ul>
 * <li><code>test/resources/systems.json</code></li>
 * <li><code>test/resources/systems.yaml</code></li>
 * <li><code>test/resources/systems.yml</code></li>
 * </ul>
 * <p>
 * Note that the order is arbitrary here since, if more than one matching file is found, an exception is thrown
 * demanding to specify exactly one file. If test systems or credentials are specified as Java system properties while
 * configuration files exist simultaneously within the the test resources folder, only Java system properties are
 * loaded, thereby overriding configuration from files in the test resources folder.
 */
@Slf4j
public class MockUtil
    implements
    TestSystemsProvider,
    CredentialsProvider,
    JndiLookupMocker,
    LocaleMocker,
    CloudPlatformMocker,
    TenantMocker,
    PrincipalMocker,
    SecretStoreMocker,
    DestinationMocker,
    ServerMocker
{
    static final String TEST_SYSTEMS_RESOURCE_FILE = "systems";
    static final String CREDENTIALS_RESOURCE_FILE = "credentials";

    static final String PROPERTY_TEST_SYSTEMS = "test.systems";
    static final String PROPERTY_CREDENTIALS = "test.credentials";

    static final List<String> CONFIG_FILE_EXTENSIONS = ImmutableList.of(".yml", ".yaml", ".json");

    static final String PROPERTY_ERP_ALIAS = "erp";
    static final String PROPERTY_ERP_USERNAME = "erp.username";
    static final String PROPERTY_ERP_PASSWORD = "erp.password";

    static final String MOCKED_CLOUD_APP_NAME = "testapp";
    static final String MOCKED_TENANT = "00000000-0000-0000-0000-000000000000";
    static final String MOCKED_PRINCIPAL = "MockedUser";

    @Getter( AccessLevel.PACKAGE )
    @Nullable
    private ProxyConfiguration proxyConfiguration;

    @Getter( AccessLevel.PACKAGE )
    private JndiLookupFacade jndiLookupFacade;

    @Getter( AccessLevel.PACKAGE )
    private LocaleFacade localeFacade;

    @Getter( AccessLevel.PACKAGE )
    private AuditLogFacade auditLogFacade;

    @Getter( AccessLevel.PACKAGE )
    private CloudPlatformFacade cloudPlatformFacade;

    @Getter( AccessLevel.PACKAGE )
    private TenantFacade tenantFacade;

    @Getter( AccessLevel.PACKAGE )
    private PrincipalFacade principalFacade;

    @Getter( AccessLevel.PACKAGE )
    private SecretStoreFacade secretStoreFacade;

    private DestinationLoader destinationFacade;

    @Delegate
    private final DefaultJndiLookupMocker jndiLookupMocker = new DefaultJndiLookupMocker(this::resetJndiLookupFacade);

    @Delegate
    private final DefaultLocaleMocker localeMocker = new DefaultLocaleMocker(this::resetLocaleFacade);

    @Delegate
    private final DefaultCloudPlatformMocker cloudPlatformMocker =
        new DefaultCloudPlatformMocker(this::resetCloudPlatformFacade);

    @Delegate
    private final DefaultTestSystemsProvider testSystemsProvider = new DefaultTestSystemsProvider();

    @Delegate
    private final DefaultCredentialsProvider credentialsProvider = new DefaultCredentialsProvider();

    @Delegate
    private final DefaultTenantMocker tenantMocker = new DefaultTenantMocker(this::resetTenantFacade);

    @Delegate
    private final DefaultPrincipalMocker principalMocker = new DefaultPrincipalMocker(this::resetPrincipalFacade);

    @Delegate
    private final DefaultSecretStoreMocker secretStoreMocker =
        new DefaultSecretStoreMocker(this::resetSecretStoreFacade);

    @Getter( AccessLevel.PACKAGE )
    @Delegate
    private final DefaultDestinationMocker destinationMocker =
        new DefaultDestinationMocker(
            testSystemsProvider,
            credentialsProvider,
            proxyConfiguration,
            this::resetDestinationFacade);

    @Delegate
    private final ServerMocker serverMocker = new DefaultServerMocker(this::mockDestination, this::mockErpDestination);

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
        testSystemsProvider.loadTestSystems();
        credentialsProvider.loadCredentials();

        // Java system properties must be read after files to enable overriding of configuration
        testSystemsProvider.readErpAliasProperty();
        credentialsProvider.readErpCredentialsProperties();

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
     * <li>the current {@link CloudPlatform},</li>
     * <li>the current {@link Locale},</li>
     * <li>the current {@link Tenant},</li>
     * <li>the current {@link Principal}</li>
     * </ul>
     * This method should be called as the first method of {@link MockUtil} in the test setup method annotated with
     * <code>@BeforeClass</code>.
     */
    public void mockDefaults()
    {
        resetJndiLookupFacade();
        resetLocaleFacade();
        resetCloudPlatformFacade();
        resetTenantFacade();
        resetPrincipalFacade();
        resetSecretStoreFacade();

        mockCurrentCloudPlatform();
        mockCurrentLocales();
        mockCurrentTenant();
        mockCurrentPrincipal();
    }

    JndiLookupFacade resetJndiLookupFacade()
    {
        if( jndiLookupFacade == null ) {
            jndiLookupFacade =
                ( name ) -> Option
                    .of(jndiLookupMocker.getObjectsByName().get(name))
                    .toTry(
                        () -> new ObjectLookupFailedException(
                            "No JNDI lookup mocked for object with name '" + name + "'."));
        }

        JndiLookupAccessor.setJndiLookupFacade(jndiLookupFacade);
        return jndiLookupFacade;
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

    private CloudPlatformFacade resetCloudPlatformFacade()
    {
        if( cloudPlatformFacade == null ) {
            cloudPlatformFacade = () -> Try.success(cloudPlatformMocker.getCurrentCloudPlatform());
        }

        CloudPlatformAccessor.setCloudPlatformFacade(cloudPlatformFacade);
        return cloudPlatformFacade;
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

    private DestinationLoader resetDestinationFacade()
    {
        if( destinationFacade == null ) {
            destinationFacade = ( destinationName, options ) -> getDestination(destinationName);
        }

        DestinationAccessor.setLoader(destinationFacade);
        return destinationFacade;
    }

    @Nonnull
    private Try<Destination> getDestination( @Nonnull final String destinationName )
    {
        final List<Function<String, Option<Destination>>> destinationLookup =
            Arrays
                .asList(
                    n -> Option.of(destinationMocker.getErpHttpDestinations().get(n)),
                    n -> Option.of(destinationMocker.getHttpDestinations().get(n)),
                    n -> Option.of(destinationMocker.getRfcDestinations().get(n)),
                    n -> Option.of(destinationMocker.getDestinations().get(n)));

        return destinationLookup
            .stream()
            .map(f -> f.apply(destinationName))
            .filter(Option::isDefined)
            .map(Option::toTry)
            .findFirst()
            .orElseGet(() -> Try.failure(new DestinationNotFoundException(destinationName)));
    }
}
