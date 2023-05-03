package com.sap.cloud.sdk.testutil;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.security.Credentials;

interface CredentialsProvider
{
    /**
     * @return The credentials for the given {@link TestSystem}. Delegates to {@link #getCredentials(String)}.
     */
    @Nonnull
    Credentials getCredentials( @Nonnull final TestSystem<?> testSystem );

    /**
     * Returns the credentials for the given alias. By default, {@link Credentials} are loaded from the Java system
     * property {@link MockUtil#PROPERTY_CREDENTIALS} and the test resource file
     * {@link MockUtil#CREDENTIALS_RESOURCE_FILE}. For registering additional {@link Credentials}, use
     * {@link #loadCredentials(String)} or {@link #addCredentials(String, Credentials)}.
     */
    @Nonnull
    Credentials getCredentials( @Nonnull final String systemAlias );

    /**
     * Delegates to {@link #loadCredentials(File)} and loads credential configuration from the resource file with the
     * given name. The file is assumed to be in JSON format and expected to be located in {@code /src/test/resources/}.
     *
     * @param resourceFileName
     *            The name of the file to be loaded from the resources folder.
     */
    void loadCredentials( @Nonnull final String resourceFileName );

    /**
     * Loads credential configuration from the resource file with the given name.
     * <p>
     * Note: Configuration specified as Java system properties always overrides configuration loaded from files.
     * <p>
     * Example (YAML): <code>
     * <pre>
     * ---
     * credentials:
     *
     * - alias: "ABC_001"
     *   username: "(username)"
     *   password: "(password)"
     * </pre>
     * </code>
     * <p>
     * Example (JSON): <code>
     * <pre>
     * {
     *   "credentials": [
     *     {
     *       "alias": "ABC_001",
     *       "username": "(username)",
     *       "password": "(password)"
     *     }
     *   ]
     * }
     * </pre>
     * </code>
     *
     * @param file
     *            The file to be loaded.
     */
    void loadCredentials( @Nullable final File file );

    /**
     * Registers the given {@link Credentials} to enable retrieval by its alias. Replaces {@link Credentials} that may
     * already exist for the given alias.
     */
    void addCredentials( @Nonnull final String systemAlias, @Nonnull final Credentials credentials );

    /**
     * Unregisters the {@link Credentials} for the given alias.
     */
    void removeCredentials( @Nonnull final String systemAlias );

    /**
     * Clears all {@link Credentials}.
     */
    void clearCredentials();
}
