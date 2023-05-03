package com.sap.cloud.sdk.testutil;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.json.JsonSanitizer;
import com.sap.cloud.sdk.cloudplatform.connectivity.ProxyConfiguration;
import com.sap.cloud.sdk.s4hana.serialization.SapClient;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
class DefaultTestSystemsProvider implements TestSystemsProvider
{
    private final Map<String, TestSystem<?>> testSystems = new HashMap<>();

    @Nullable
    private String erpAlias = null;

    @Override
    public void loadTestSystems( @Nonnull final String resourceFileName )
    {
        loadTestSystems(ConfigFileUtil.getResourceFile(getClass().getClassLoader(), resourceFileName));
    }

    @Override
    public void loadTestSystems( @Nullable final File file )
    {
        if( file != null ) {
            try {
                parseTestSystems(Files.asCharSource(file, Charset.defaultCharset()).read());
            }
            catch( final IOException e ) {
                throw new TestConfigurationError(e);
            }
        }
    }

    @Override
    public void addTestSystem( @Nonnull final TestSystem<?> testSystem )
    {
        testSystems.put(testSystem.getAlias(), testSystem);
    }

    @Override
    public void removeTestSystem( @Nonnull final TestSystem<?> testSystem )
    {
        removeTestSystem(testSystem.getAlias());
    }

    @Override
    public void removeTestSystem( @Nonnull final String systemAlias )
    {
        testSystems.remove(systemAlias);
    }

    @Nonnull
    @Override
    public TestSystem<?> getTestSystem( @Nonnull final String systemAlias )
    {
        @Nullable
        final TestSystem<?> testSystem = testSystems.get(systemAlias);

        if( testSystem == null ) {
            final StringBuilder knownAliases = new StringBuilder();
            final Iterator<String> it = testSystems.keySet().iterator();
            while( it.hasNext() ) {
                final String alias = it.next();
                knownAliases.append("\"").append(alias).append("\"");

                if( it.hasNext() ) {
                    knownAliases.append(", ");
                }
            }

            throw new TestConfigurationError(
                "Cannot find "
                    + TestSystem.class.getSimpleName()
                    + " with alias '"
                    + systemAlias
                    + "'. Known aliases: "
                    + knownAliases
                    + ". Make sure to define the alias in either the Java system property '"
                    + MockUtil.PROPERTY_TEST_SYSTEMS
                    + "' or in "
                    + ConfigFileUtil.buildMissingResourceFileMessage(MockUtil.TEST_SYSTEMS_RESOURCE_FILE)
                    + ": \n\n"
                    + getTestSystemsExampleYaml()
                    + "\n\n"
                    + getTestSystemsExampleJson());
        }

        return testSystem;
    }

    @Override
    public void addDefaultErpSystem( @Nonnull final ErpSystem erpSystem )
    {
        addTestSystem(erpSystem);
        erpAlias = erpSystem.getAlias();
    }

    @Nonnull
    @Override
    public ErpSystem getErpSystem()
    {
        return getErpSystem(getErpAlias());
    }

    @Nonnull
    @Override
    public ErpSystem getErpSystem( @Nonnull final String systemAlias )
    {
        final TestSystem<?> testSystem = getTestSystem(systemAlias);

        if( !(testSystem instanceof ErpSystem) ) {
            throw new TestConfigurationError(
                TestSystem.class.getSimpleName()
                    + " with alias '"
                    + systemAlias
                    + "' is not of type "
                    + ErpSystem.class.getSimpleName()
                    + ".");
        }

        return (ErpSystem) testSystem;
    }

    @Override
    public void clearTestSystems()
    {
        testSystems.clear();
    }

    void readErpAliasProperty()
    {
        final String alias = System.getProperty(MockUtil.PROPERTY_ERP_ALIAS);
        if( alias != null ) {
            erpAlias = alias;
        }
    }

    void loadTestSystems()
    {
        final String property = System.getProperty(MockUtil.PROPERTY_TEST_SYSTEMS);

        if( !Strings.isNullOrEmpty(property) ) {
            final File file = ConfigFileUtil.getUniqueFileForExtensions(new File(property));

            if( file != null && file.exists() ) {
                loadTestSystems(file);
            } else if( property.trim().startsWith("{") || property.trim().startsWith("---") ) {
                parseTestSystems(property);
            } else {
                ConfigFileUtil.throwFailedToParseProperty(MockUtil.PROPERTY_TEST_SYSTEMS, property);
            }
        } else {
            final File systemsFile =
                ConfigFileUtil
                    .getUniqueResourceFileForExtensions(
                        getClass().getClassLoader(),
                        MockUtil.TEST_SYSTEMS_RESOURCE_FILE);
            if( systemsFile != null ) {
                loadTestSystems(systemsFile);
            } else {
                final String systemsFileContent =
                    ConfigFileUtil
                        .getFileContentsForExtensions(getClass().getClassLoader(), MockUtil.TEST_SYSTEMS_RESOURCE_FILE);
                if( systemsFileContent != null ) {
                    parseTestSystems(systemsFileContent);
                }
            }
        }
    }

    private void parseTestSystems( @Nonnull final String str )
    {
        try {
            String cleanStr = str.trim();
            if( cleanStr.startsWith("{") ) {
                cleanStr = JsonSanitizer.sanitize(cleanStr);
            }

            final SerializedTestSystems serializedTestSystems =
                ConfigFileUtil.newObjectMapper(cleanStr).readValue(cleanStr, SerializedTestSystems.class);

            final List<SerializedTestSystem> genericSystems = serializedTestSystems.getSystems();

            if( genericSystems != null ) {
                for( final SerializedTestSystem genericSystem : genericSystems ) {
                    addTestSystem(
                        new GenericSystem(
                            genericSystem.getAlias(),
                            genericSystem.getUri(),
                            parseProxyConfiguration(genericSystem.getProxyUri())));
                }
            }

            final SerializedErpSystems erpSystems = serializedTestSystems.getErpSystems();

            if( erpSystems != null ) {
                erpAlias = erpSystems.getDefaultAlias();

                final List<SerializedErpSystem> systems = erpSystems.getSystems();

                if( systems != null ) {
                    for( final SerializedErpSystem system : systems ) {
                        final ErpSystem erpSystem =
                            ErpSystem
                                .builder()
                                .alias(system.getAlias())
                                .uri(system.getUri())
                                .systemId(system.getSystemId())
                                .sapClient(SapClient.of(system.getSapClient()))
                                .proxyConfiguration(parseProxyConfiguration(system.getProxyUri()))
                                .locale(parseLocale(system.getLocale()))
                                .applicationServer(system.getApplicationServer())
                                .instanceNumber(system.getInstanceNumber())
                                .build();

                        addTestSystem(erpSystem);
                    }
                }
            }
        }
        catch( final IOException e ) {
            throw new TestConfigurationError(e);
        }
    }

    @Nullable
    private ProxyConfiguration parseProxyConfiguration( @Nullable final URI proxyUri )
    {
        return proxyUri != null ? new ProxyConfiguration(proxyUri) : null;
    }

    @Nullable
    private Locale parseLocale( @Nullable final String language )
    {
        return language != null ? Locale.forLanguageTag(language.replace('_', '-')) : null;
    }

    private String getErpAlias()
    {
        if( erpAlias == null ) {
            throw new TestConfigurationError(
                "No default ERP system alias defined. "
                    + "Make sure to set the default alias in either the Java system property '"
                    + MockUtil.PROPERTY_TEST_SYSTEMS
                    + "' or in "
                    + ConfigFileUtil.buildMissingResourceFileMessage(MockUtil.TEST_SYSTEMS_RESOURCE_FILE)
                    + ": \n\n"
                    + getTestSystemsExampleYaml()
                    + "\n\n"
                    + getTestSystemsExampleJson());
        }

        return erpAlias;
    }

    private String getTestSystemsExampleYaml()
    {
        return "Example (YAML, recommended for hand-written files):\n---\n"
            + "systems:\n"
            + "  - alias: \"ANY_SYSTEM\"\n"
            + "    uri: \"https://any-system.com\"\n"
            + "    proxy: \"http://my-proxy:8080\"\n"
            + "\n"
            + "erp:\n"
            + "  default: \"ERP_001\"\n"
            + "  systems:\n"
            + "    - alias: \"ERP_001\"\n"
            + "      uri: \"https://my-erp.com\"\n"
            + "      systemId: \"ERP\"                  # optional, defaults to \"\"\n"
            + "      sapClient: \"001\"                 # optional, defaults to default SAP client\n"
            + "      locale: \"en\"                     # optional, defaults to English (US)\n"
            + "      erpEdition: \"cloud\"              # optional, defaults to \"cloud\"\n"
            + "      proxy: \"http://my-proxy:8080\"    # optional\n"
            + "      applicationServer: \"my-erp.com\"  # optional, defaults to URI host\n"
            + "      instanceNumber: \"00\"             # optional, defaults to \"00\"\n";
    }

    private String getTestSystemsExampleJson()
    {
        return "Example (JSON, recommended for generated files):\n{\n"
            + "  \"systems\": [\n"
            + "    {\n"
            + "      \"alias\": \"ANY_SYSTEM\",\n"
            + "      \"uri\": \"https://any-system.com\",\n"
            + "      \"proxy\": \"http://my-proxy:8080\"\n"
            + "    }\n"
            + "  ],\n"
            + "  \"erp\": {\n"
            + "    \"default\": \"ERP_001\",\n"
            + "    \"systems\": [\n"
            + "      {\n"
            + "        \"alias\": \"ERP_001\",\n"
            + "        \"uri\": \"https://my-erp.com\",\n"
            + "        \"systemId\": \"ERP\",\n"
            + "        \"sapClient\": \"001\",\n"
            + "        \"locale\": \"en\",\n"
            + "        \"erpEdition\": \"cloud\",\n"
            + "        \"proxy\": \"http://my-proxy:8080\",\n"
            + "        \"applicationServer\": \"my-erp.com\",\n"
            + "        \"instanceNumber\": \"00\"\n"
            + "      }\n"
            + "    ]\n"
            + "  }\n"
            + "}\n";
    }
}
