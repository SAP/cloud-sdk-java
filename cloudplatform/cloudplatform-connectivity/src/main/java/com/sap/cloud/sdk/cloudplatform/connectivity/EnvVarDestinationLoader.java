package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Streams;
import com.google.json.JsonSanitizer;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link DestinationLoader} implementation reading the destination from an environment variable.
 * <p>
 * The environment variable is expected to be a JSON array of JSON object containing a field called {@code name}. All
 * other fields of the JSON object will be converted to {@code String} and put into the created {@code Destination}
 * object.
 * <p>
 * Valid example:
 *
 * <pre>
 * {@code destinations='[{"name"="destinationName", "url"="www.sap.de"}, {"name"="destinationName2", "url"="www.sap.com"}]'}
 * </pre>
 */
@Slf4j
public class EnvVarDestinationLoader implements DestinationLoader
{
    private static final String DEFAULT_DESTINATION_VARIABLE_NAME = "destinations";

    private final Function<String, String> environmentVariableAccessor;

    private final Function<String, Destination> destinationJsonParser;

    private final String variableName;

    /**
     * Creates a new instance of {@code EnvVarDestinationLoader} reading the {@code destinations} variable from the
     * system environment variables.
     */
    public EnvVarDestinationLoader()
    {
        this(null, null, null);
    }

    /**
     * Creates a new instance of {@code EnvVarDestinationLoader} reading variable with the provided name from the
     * provided function.
     *
     * @param environmentVariableAccessor
     *            A function returning the environment variable for a given name parameter. If {@code null} is passed,
     *            this will fall back to {@link System#getenv(String)}.
     *
     * @param destinationJsonParser
     *            A function returning the {@link DefaultDestination} after parsing the JSON. If {@code null} is passed,
     *            a default implementation is used.
     *
     * @param variableName
     *            The name of the environment variable to read. If {@code null} is passed this will fall back to
     *            {@code destinations}.
     */
    public EnvVarDestinationLoader(
        @Nullable final Function<String, String> environmentVariableAccessor,
        @Nullable final Function<String, Destination> destinationJsonParser,
        @Nullable final String variableName )
    {
        this.environmentVariableAccessor =
            Option.of(environmentVariableAccessor).getOrElse((Function<String, String>) System::getenv);
        this.destinationJsonParser = Option.of(destinationJsonParser).getOrElse(this::extractDestination);
        this.variableName = Option.of(variableName).getOrElse(DEFAULT_DESTINATION_VARIABLE_NAME);
    }

    /**
     * Creates a new instance of {@code EnvVarDestinationLoader} reading variable with the provided name from the
     * provided function.
     *
     * @param environmentVariableAccessor
     *            A function returning the environment variable for a given name parameter. If {@code null} is passed
     *            this will fall back to {@link System#getenv(String)}.
     * @param variableName
     *            The name of the environment variable to read. If {@code null} is passed this will fall back to
     *            {@code destinations}.
     */
    public EnvVarDestinationLoader(
        @Nullable final Function<String, String> environmentVariableAccessor,
        @Nullable final String variableName )
    {
        this.environmentVariableAccessor =
            Option.of(environmentVariableAccessor).getOrElse((Function<String, String>) System::getenv);
        destinationJsonParser = this::extractDestination;
        this.variableName = Option.of(variableName).getOrElse(DEFAULT_DESTINATION_VARIABLE_NAME);
    }

    @Nonnull
    @Override
    public
        Try<Destination>
        tryGetDestination( @Nonnull final String destinationName, @Nonnull final DestinationOptions options )
    {
        return Try.of(() -> destinationJsonParser.apply(destinationName));
    }

    /**
     * Fetches all destinations from the environment variable.
     *
     * @return A Try list of destinations.
     */
    @Nonnull
    public Try<Iterable<Destination>> tryGetAllDestinations()
    {
        final Try<Iterable<JsonNode>> tryNodes = Try.of(this::getJsonNodes);
        return tryNodes.map(nodes -> Streams.stream(nodes).map(this::parseDestination).collect(Collectors.toList()));
    }

    private Iterable<JsonNode> getJsonNodes()
    {
        final String destinationsJson = environmentVariableAccessor.apply(variableName);

        log.debug("Trying to extract destinations from environment variables.");

        if( destinationsJson == null || destinationsJson.isEmpty() ) {
            throw new DestinationNotFoundException(
                null,
                "Could not find environment variable for name '" + variableName + "'.");
        }

        final JsonNode jsonNode;
        try {
            jsonNode = new ObjectMapper().readTree(JsonSanitizer.sanitize(destinationsJson));
        }
        catch( final IOException e ) {
            throw new DestinationAccessException(
                "The environment variable with name " + variableName + " could not be parsed.",
                e);
        }

        if( !jsonNode.isArray() ) {
            throw new DestinationAccessException(
                "The content of the '" + variableName + "' environment variable has to be a JSON Array.");
        }
        return jsonNode;
    }

    private Destination extractDestination( final String destinationName )
    {
        for( final JsonNode node : getJsonNodes() ) {
            final JsonNode nameNode = node.get("name");
            if( nameNode == null ) {
                throw new DestinationAccessException(
                    "Destination with the following properties lacks a property \"name\": " + node);
            }
            if( Objects.equals(nameNode.asText(), destinationName) ) {
                return parseDestination(node);
            }
        }

        throw new DestinationNotFoundException(
            destinationName,
            "Could not find a destination with the name "
                + destinationName
                + " in the environment variable with name "
                + variableName
                + ".");
    }

    @SuppressWarnings( "deprecation" )
    private Destination parseDestination( final JsonNode destinationNode )
    {
        final HashMap<String, String> map = new HashMap<>();
        final Iterator<Map.Entry<String, JsonNode>> fields = destinationNode.fields();

        while( fields.hasNext() ) {
            final Map.Entry<String, JsonNode> field = fields.next();
            final String fieldName = field.getKey();
            final JsonNode fieldValue = field.getValue();

            final String stringValue;

            if( fieldValue.isTextual() ) {
                stringValue = fieldValue.asText();
            } else {
                stringValue = fieldValue.toString();
            }

            map.put(fieldName, stringValue);
        }

        final DefaultDestination properties = DefaultDestination.fromMap(map).build();

        if( properties.get(DestinationProperty.TYPE).contains(DestinationType.RFC) ) {
            return DefaultRfcDestination.fromProperties(properties);
        } else {
            return DefaultHttpDestination.fromProperties(properties).build();
        }
    }
}
