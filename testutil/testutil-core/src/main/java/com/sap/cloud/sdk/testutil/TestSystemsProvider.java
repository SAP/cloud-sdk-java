package com.sap.cloud.sdk.testutil;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

interface TestSystemsProvider
{
    /**
     * Returns the {@link TestSystem} for the given alias. By default, {@link TestSystem}s are loaded from the system
     * property {@link MockUtil#PROPERTY_TEST_SYSTEMS} and the test resource file
     * {@link MockUtil#TEST_SYSTEMS_RESOURCE_FILE} (with extension .json/.yml for JSON/YAML format). For registering
     * additional {@link TestSystem}s, use {@link #loadTestSystems(String)} or {@link #addTestSystem(TestSystem)}.
     */
    @Nonnull
    TestSystem<?> getTestSystem( @Nonnull final String systemAlias );

    /**
     * @return The default ERP system.
     */
    @Nonnull
    ErpSystem getErpSystem();

    /**
     * @return The {@link ErpSystem} for the given alias. Delegates to {@link #getTestSystem(String)}.
     */
    @Nonnull
    ErpSystem getErpSystem( @Nonnull final String systemAlias );

    /**
     * Delegates to {@link #loadTestSystems(File)} and loads test system configuration from the resource file with the
     * given name. The file is assumed to be in JSON format and expected to be located in {@code src/test/resources/}.
     *
     * @param resourceFileName
     *            The name of the file to be loaded from the resources folder.
     */
    void loadTestSystems( @Nonnull final String resourceFileName );

    /**
     * Loads test system configuration from the given file. Note that among all loaded configurations, the
     * {@code "alias"} field must be unique.
     * <p>
     * Note: Configuration specified as Java system properties always overrides configuration loaded from files.
     * <p>
     * Example (YAML): <code>
     * <pre>
     * ---
     * systems:
     *   - alias: "ANY_SYSTEM"
     *     uri: "https://any-system.com"
     *     proxy: "http://my-proxy:8080"
     *
     * erp:
     *   default: "ERP_001"
     *   systems:
     *     - alias: "ERP_001"
     *       uri: "https://my-erp.com"
     *       systemId: "ERP"                  # optional, defaults to ""
     *       sapClient: "001"                 # optional, defaults to default SAP client
     *       locale: "en"                     # optional, defaults to English (US)
     *       erpEdition: "cloud"              # optional, defaults to "cloud"
     *       proxy: "http://my-proxy:8080"    # optional
     *       applicationServer: "my-erp.com"  # optional, defaults to URI host
     *       instanceNumber: "00"             # optional, defaults to "00"
     * </pre>
     * </code>
     * <p>
     * Example (JSON): <code>
     * <pre>
     * {
     *   "systems": [
     *     {
     *       "alias": "ANY_SYSTEM",
     *       "uri": "https://any-system.com",
     *       "proxy": "http://my-proxy:8080"
     *     }
     *   ],
     *   "erp": {
     *     "default": "ERP_001",
     *     "systems": [
     *       {
     *         "alias": "ERP_001",
     *         "systemId": "ERP",
     *         "sapClient": "001",
     *         "locale": "en-US",
     *         "erpEdition": "cloud",
     *         "uri": "https://my-erp.com",
     *         "proxy": "http://my-proxy:8080",
     *         "applicationServer": "my-erp.com",
     *         "instanceNumber": "00"
     *       }
     *     ]
     *   }
     * }
     * </pre>
     * </code>
     *
     * @param file
     *            The file to be loaded.
     */
    void loadTestSystems( @Nullable final File file );

    /**
     * Registers the given {@link TestSystem} to enable retrieval by its alias. Replaces {@link TestSystem}s that may
     * already exist for the given alias.
     */
    void addTestSystem( @Nonnull final TestSystem<?> testSystem );

    /**
     * Unregisters the given {@link TestSystem}.
     */
    void removeTestSystem( @Nonnull final TestSystem<?> testSystem );

    /**
     * Unregisters the {@link TestSystem} for the given alias.
     */
    void removeTestSystem( @Nonnull final String systemAlias );

    /**
     * Adds and sets the default ERP system.
     */
    void addDefaultErpSystem( @Nonnull final ErpSystem erpSystem );

    /**
     * Clears all {@link TestSystem}s.
     */
    void clearTestSystems();
}
