package com.sap.cloud.sdk.datamodel.odata.utility;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;

import com.google.common.base.Joiner;

import lombok.extern.slf4j.Slf4j;

/**
 * This class is used to read and write service name mappings from a file.
 */
@Slf4j
public class ServiceNameMappings
{
    private static final String DELIMITER_SHORT = "=";
    private static final String DELIMITER_LONG = " = ";
    private final Path file;
    private final Map<Key, Value> mappings = new LinkedHashMap<>();

    private record Key( @Nonnull String key )
    {
    }

    private record Value( @Nonnull String value, @Nonnull String comment )
    {
    }

    /**
     * Creates a new instance of {@link ServiceNameMappings} with the specified file.
     *
     * @param file
     *            the file to read and write mappings from/to
     */
    public ServiceNameMappings( @Nonnull final Path file )
    {
        this.file = file;
        if( Files.exists(file) ) {
            populateMappings();
        }
    }

    /**
     * Saves the mappings to the file.
     *
     * @throws IOException
     *             if an error occurs while writing to the file
     */
    public void save()
        throws IOException
    {
        final StringBuilder text = new StringBuilder();
        for( final Map.Entry<Key, Value> entry : mappings.entrySet() ) {
            if( !entry.getValue().comment().isBlank() ) {
                text.append(System.lineSeparator()).append("# ").append(entry.getValue().comment());
                text.append(System.lineSeparator());
            }
            text.append(entry.getKey().key()).append(DELIMITER_LONG).append(entry.getValue().value());
            text.append(System.lineSeparator());
        }
        Files.writeString(file, text, UTF_8, CREATE, TRUNCATE_EXISTING);
    }

    /**
     * Gets the value of the specified key.
     *
     * @param key
     *            the key to get the value for
     * @return the optional value of the key.
     */
    @Nonnull
    public Optional<String> getString( @Nonnull final String key )
    {
        return Optional.ofNullable(mappings.get(new Key(key))).map(Value::value);
    }

    /**
     * Adds a new mapping to the file.
     *
     * @param key
     *            the key to add
     * @param value
     *            the value to add
     * @param comments
     *            the comments to add, optional
     */
    public void putString( @Nonnull final String key, @Nonnull final String value, @Nonnull final String... comments )
    {
        mappings.put(new Key(key), new Value(value, Joiner.on(" ").join(comments)));
    }

    private void populateMappings()
    {
        try {
            final List<String> lines = Files.readAllLines(file, UTF_8);
            final List<String> comment = new ArrayList<>();
            for( final String line : lines ) {
                if( line.startsWith("#") ) {
                    comment.add(line.substring(1).trim());
                } else if( line.contains(DELIMITER_SHORT) ) {
                    final String[] parts = line.split("=", 2);
                    putString(parts[0].trim(), parts[1].trim(), comment.toArray(new String[0]));
                    comment.clear();
                } else if( !line.isBlank() ) {
                    log.debug("Skipping line: {}", line);
                }
            }
        }
        catch( final IOException e ) {
            throw new IllegalArgumentException("Invalid mapping file: " + file, e);
        }
    }
}
