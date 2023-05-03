package com.sap.cloud.sdk.testutil;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.io.Files;

class ConfigFileUtil
{
    static ObjectMapper newObjectMapper( final String value )
    {
        final ObjectMapper objectMapper;

        if( value.trim().startsWith("{") ) {
            objectMapper = new ObjectMapper();
        } else {
            objectMapper = new ObjectMapper(new YAMLFactory());
        }

        return objectMapper;
    }

    @Nullable
    static File getResourceFile( @Nonnull final ClassLoader classLoader, @Nonnull final String resourceFileName )
    {
        @Nullable
        final URL resourceUrl = classLoader.getResource(resourceFileName);

        if( resourceUrl == null || !"file".equalsIgnoreCase(resourceUrl.getProtocol()) ) {
            return null;
        } else {
            final String formattedPath;
            try {
                formattedPath = URLDecoder.decode(resourceUrl.getFile(), "UTF-8");
            }
            catch( final UnsupportedEncodingException e ) {
                throw new TestConfigurationError("Failed to get path to " + resourceFileName + ".", e);
            }
            final String separator = File.separator.equals("\\") ? "\\\\" : File.separator;
            return new File(formattedPath.replaceAll("/+", separator));
        }
    }

    @Nullable
    static File getUniqueResourceFileForExtensions(
        @Nonnull final ClassLoader classLoader,
        @Nullable final String resourceFileName )
    {
        if( resourceFileName == null ) {
            return null;
        }

        final List<File> existingFiles = new ArrayList<>();
        final String fileNameWithoutExtension = Files.getNameWithoutExtension(resourceFileName);

        for( final String fileExtension : MockUtil.CONFIG_FILE_EXTENSIONS ) {
            final File candidate = getResourceFile(classLoader, fileNameWithoutExtension + fileExtension);

            if( candidate != null && candidate.exists() ) {
                existingFiles.add(candidate);
            }
        }

        if( existingFiles.size() > 1 ) {
            throw new TestConfigurationError(
                "Found multiple equivalent resource files: " + existingFiles + ". Make sure to specify only one file.");
        }

        if( existingFiles.size() == 1 ) {
            return existingFiles.get(0);
        }

        return null;
    }

    @Nullable
    static File getUniqueFileForExtensions( @Nullable final File file )
    {
        if( file == null ) {
            return null;
        }

        final List<File> existingFiles = new ArrayList<>();
        final String filePathWithoutExtension =
            file.getParentFile() + File.separator + Files.getNameWithoutExtension(file.getPath());

        for( final String fileExtension : MockUtil.CONFIG_FILE_EXTENSIONS ) {
            final File candidate = new File(filePathWithoutExtension + fileExtension);

            if( candidate.exists() ) {
                existingFiles.add(candidate);
            }
        }

        if( existingFiles.size() > 1 ) {
            throw new TestConfigurationError(
                "Found multiple equivalent files: " + existingFiles + ". Make sure to specify only one file.");
        }

        if( existingFiles.size() == 1 ) {
            return existingFiles.get(0);
        }

        return null;
    }

    @Nullable
    static
        String
        getFileContentsForExtensions( @Nonnull final ClassLoader classLoader, @Nullable final String fileName )
    {
        if( fileName == null ) {
            return null;
        }

        final List<InputStream> existingFiles =
            MockUtil.CONFIG_FILE_EXTENSIONS
                .stream()
                .map(ext -> classLoader.getResourceAsStream(fileName + ext))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if( existingFiles.size() > 1 ) {
            throw new TestConfigurationError(
                "Found multiple equivalent files: " + existingFiles + ". Make sure to specify only one file.");
        }

        if( existingFiles.size() == 1 ) {
            return stringContentFromInputStream(existingFiles.get(0));
        }

        return null;
    }

    private static String stringContentFromInputStream( final InputStream existingFiles )
    {
        return new Scanner(existingFiles, "UTF-8").useDelimiter("\\A").next();
    }

    static void throwFailedToParseProperty( final String propertyName, final String propertyValue )
    {
        throw new TestConfigurationError(
            "Failed to parse property '"
                + propertyName
                + "' with value '"
                + propertyValue
                + "'. "
                + "Have you specified an existing file or configuration in either JSON or YAML format?");
    }

    static void throwFailedToLoadFile( final String fileName )
    {
        throw new TestConfigurationError(
            "Unable to load resource file '" + fileName + "' as either .yml, .yaml or .json.");
    }

    static String buildMissingResourceFileMessage( @Nonnull final String fileName )
    {
        final StringBuilder sb =
            new StringBuilder(
                "src/test/resources/" + fileName + MockUtil.CONFIG_FILE_EXTENSIONS.get(0) + " (alternatively: ");

        for( int i = 1; i < MockUtil.CONFIG_FILE_EXTENSIONS.size(); ++i ) {
            sb.append(fileName).append(MockUtil.CONFIG_FILE_EXTENSIONS.get(i));

            if( i < MockUtil.CONFIG_FILE_EXTENSIONS.size() - 1 ) {
                sb.append(", ");
            } else {
                sb.append(")");
            }
        }

        return sb.toString();
    }
}
