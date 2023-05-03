package com.sap.cloud.sdk.testutil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.json.JsonSanitizer;
import com.sap.cloud.sdk.cloudplatform.security.BasicCredentials;
import com.sap.cloud.sdk.cloudplatform.security.Credentials;
import com.sap.cloud.sdk.cloudplatform.security.NoCredentials;

class DefaultCredentialsProvider implements CredentialsProvider
{
    private final Map<String, Credentials> credentials = new HashMap<>();

    @Nullable
    private String erpUsername = null;

    @Nullable
    private String erpPassword = null;

    @Nonnull
    @Override
    public Credentials getCredentials( @Nonnull final TestSystem<?> testSystem )
    {
        return getCredentials(testSystem.getAlias());
    }

    @Nonnull
    @Override
    public Credentials getCredentials( @Nonnull final String systemAlias )
    {
        @Nullable
        final Credentials credentials = this.credentials.get(systemAlias);

        if( credentials == null ) {
            throw new TestConfigurationError(
                "No credentials found for alias '"
                    + systemAlias
                    + "'. Make sure to specify credentials in the Java system property '"
                    + MockUtil.PROPERTY_CREDENTIALS
                    + "' or in "
                    + ConfigFileUtil.buildMissingResourceFileMessage(MockUtil.CREDENTIALS_RESOURCE_FILE)
                    + ": \n\n"
                    + getCredentialsExampleYaml(systemAlias)
                    + "\n\n"
                    + getCredentialsExampleJson(systemAlias));
        }

        return credentials;
    }

    @Override
    public void loadCredentials( @Nonnull final String resourceFileName )
    {
        loadCredentials(ConfigFileUtil.getResourceFile(getClass().getClassLoader(), resourceFileName));
    }

    @Override
    public void loadCredentials( @Nullable final File file )
    {
        if( file != null ) {
            try {
                parseCredentials(Files.asCharSource(file, Charset.defaultCharset()).read());
            }
            catch( final IOException e ) {
                throw new TestConfigurationError(e);
            }
        }
    }

    @Override
    public void addCredentials( @Nonnull final String systemAlias, @Nonnull final Credentials credentials )
    {
        this.credentials.put(systemAlias, credentials);
    }

    @Override
    public void removeCredentials( @Nonnull final String systemAlias )
    {
        credentials.remove(systemAlias);
    }

    @Override
    public void clearCredentials()
    {
        erpUsername = null;
        erpPassword = null;

        credentials.clear();
    }

    Credentials getErpCredentials( @Nonnull final ErpSystem erpSystem, @Nullable final Credentials credentials )
    {
        Credentials erpCredentials = credentials;

        if( erpCredentials == null ) {
            if( erpUsername != null && erpPassword != null ) {
                erpCredentials = new BasicCredentials(erpUsername, erpPassword);
            } else {
                erpCredentials = getCredentials(erpSystem);
            }
        }

        if( !(erpCredentials instanceof NoCredentials) && !(erpCredentials instanceof BasicCredentials) ) {
            throw new TestConfigurationError(
                "Unsupported credentials of type "
                    + erpCredentials.getClass().getSimpleName()
                    + ": "
                    + erpCredentials
                    + ".");
        }

        return erpCredentials;
    }

    void readErpCredentialsProperties()
    {
        final String username = System.getProperty(MockUtil.PROPERTY_ERP_USERNAME);
        if( username != null ) {
            erpUsername = username;
        }

        final String password = System.getProperty(MockUtil.PROPERTY_ERP_PASSWORD);
        if( password != null ) {
            erpPassword = password;
        }
    }

    void loadCredentials()
    {
        final String property = System.getProperty(MockUtil.PROPERTY_CREDENTIALS);

        if( !Strings.isNullOrEmpty(property) ) {
            final File file = ConfigFileUtil.getUniqueFileForExtensions(new File(property));

            if( file != null && file.exists() ) {
                loadCredentials(file);
            } else if( property.trim().startsWith("{") || property.trim().startsWith("---") ) {
                parseCredentials(property);
            } else {
                ConfigFileUtil.throwFailedToParseProperty(MockUtil.PROPERTY_CREDENTIALS, property);
            }
        } else {
            final File credentialsFile =
                ConfigFileUtil
                    .getUniqueResourceFileForExtensions(
                        getClass().getClassLoader(),
                        MockUtil.CREDENTIALS_RESOURCE_FILE);
            if( credentialsFile != null ) {
                loadCredentials(credentialsFile);
            } else {
                final String credentialsFileContent =
                    ConfigFileUtil
                        .getFileContentsForExtensions(getClass().getClassLoader(), MockUtil.CREDENTIALS_RESOURCE_FILE);
                if( credentialsFileContent != null ) {
                    parseCredentials(credentialsFileContent);
                }
            }
        }
    }

    private String getCredentialsExampleYaml( final String systemAlias )
    {
        return "Example (YAML, recommended for hand-written files):\n---\n"
            + "credentials:\n\n"
            + "- alias: \""
            + systemAlias
            + "\"\n"
            + "  username: \"(username)\"\n"
            + "  password: \"(password)\"\n";
    }

    private String getCredentialsExampleJson( final String systemAlias )
    {
        return "Example (JSON, recommended for generated files):\n{\n"
            + "  \"credentials\": [\n"
            + "    {\n"
            + "      \"alias\": \""
            + systemAlias
            + "\",\n"
            + "      \"username\": \"(username)\",\n"
            + "      \"password\": \"(password)\"\n"
            + "    }\n"
            + "  ]\n"
            + "}\n";
    }

    private void parseCredentials( @Nonnull final String str )
    {
        try {
            String cleanStr = str.trim();
            if( cleanStr.startsWith("{") ) {
                cleanStr = JsonSanitizer.sanitize(cleanStr);
            }

            final SerializedCredentialsList serializedCredentialsList =
                ConfigFileUtil.newObjectMapper(cleanStr).readValue(cleanStr, SerializedCredentialsList.class);

            final List<SerializedCredentials> serializedCredentials = serializedCredentialsList.getCredentials();

            if( serializedCredentials != null ) {
                for( final SerializedCredentials credentials : serializedCredentials ) {
                    final String alias = credentials.getAlias();
                    final String username = credentials.getUsername();
                    final String password = credentials.getPassword();

                    if( !Strings.isNullOrEmpty(alias)
                        && !Strings.isNullOrEmpty(username)
                        && !Strings.isNullOrEmpty(password) ) {
                        this.credentials.put(alias, new BasicCredentials(username, password));
                    } else {
                        this.credentials.put(alias, new NoCredentials());
                    }
                }
            }
        }
        catch( final IOException e ) {
            throw new TestConfigurationError(e);
        }
    }
}
