package com.sap.cloud.sdk.datamodel.odata.client;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation to deserialize OData responses based on a given {@link ODataProtocol}.
 */
@Slf4j
@RequiredArgsConstructor
public class ODataResponseDeserializer
{
    @Nonnull
    private final ODataProtocol protocol;

    /**
     * Position the {@see JsonReader} to the response result set.
     *
     * @param reader
     *            The internal JsonReader instance.
     * @throws IOException
     *             If response cannot be read.
     */
    public void positionReaderToResultSet( @Nonnull final JsonReader reader )
        throws IOException
    {
        final List<String> nodes = protocol.getPathToResultSet().getPaths().get(0).getNodes();
        reader.beginObject();
        for( int i = 0; i < nodes.size(); i++ ) {
            while( reader.peek() == JsonToken.NAME && !nodes.get(i).equals(reader.nextName()) ) {
                JsonParser.parseReader(reader);
            }
            if( i < nodes.size() - 1 ) {
                reader.beginObject();
            }
        }
        reader.beginArray();
    }

    /**
     * Get the element to the response result set.
     *
     * @param element
     *            The root element.
     * @return The optional result as JsonArray.
     */
    @Nonnull
    public Option<JsonArray> getElementToResultSet( @Nonnull final JsonElement element )
    {
        final JsonElement resultElement = getResultJsonElement(element, protocol.getPathToResultSet());
        return Option.of(resultElement).filter(JsonElement::isJsonArray).map(JsonElement::getAsJsonArray);
    }

    /**
     * Get the element to the single response result item.
     *
     * @param element
     *            The root element.
     * @return The optional result as JsonObject.
     */
    @Nonnull
    public Option<JsonObject> getElementToResultSingle( @Nonnull final JsonElement element )
    {
        final JsonElement resultElement = getResultJsonElement(element, protocol.getPathToResultSingle());
        return Option.of(resultElement).filter(JsonElement::isJsonObject).map(JsonElement::getAsJsonObject);
    }

    /**
     * Get the element to the response result set.
     *
     * @param element
     *            The root element.
     * @return The optional result as JsonArray.
     */
    @Nonnull
    public Option<JsonArray> getElementToResultPrimitiveSet( @Nonnull final JsonElement element )
    {
        final JsonElement resultElement = getResultJsonElement(element, protocol.getPathToResultPrimitive());
        return Option.of(resultElement).filter(JsonElement::isJsonArray).map(JsonElement::getAsJsonArray);
    }

    /**
     * Get the element to the single response result item.
     *
     * @param element
     *            The root element.
     * @return The optional result as JsonPrimitive.
     */
    @Nonnull
    public Option<JsonPrimitive> getElementToResultPrimitiveSingle( @Nonnull final JsonElement element )
    {
        final JsonElement resultElement = getResultJsonElement(element, protocol.getPathToResultPrimitive());
        return Option.of(resultElement).filter(JsonElement::isJsonPrimitive).map(JsonElement::getAsJsonPrimitive);
    }

    @Nullable
    private JsonElement getResultJsonElement( @Nonnull final JsonElement element, @Nonnull final JsonLookup lookup )
    {
        for( final JsonPath path : lookup.getPaths() ) {
            final JsonElement result = getResultJsonElement(element, path);
            if( result != null ) {
                return result;
            }
        }
        return null;
    }

    @Nullable
    private JsonElement getResultJsonElement( @Nonnull final JsonElement element, @Nonnull final JsonPath path )
    {
        final List<String> nodes = path.getNodes();
        JsonElement resultElement = element;
        for( int i = 0; i < nodes.size(); i++ ) {
            if( resultElement == null || !resultElement.isJsonObject() ) {
                log.warn("JSON path {} could not be resolved for {} at position {}.", path, resultElement, i);
                return null;
            }
            if( nodes.get(i).equals("*") ) {
                if( !resultElement.getAsJsonObject().entrySet().isEmpty() ) {
                    resultElement = resultElement.getAsJsonObject().entrySet().iterator().next().getValue();
                } else {
                    log.warn("Wildcard in JSON path {}  did not match anything for {}.", path, resultElement);
                    return null;
                }
            } else {
                resultElement = resultElement.getAsJsonObject().get(nodes.get(i));
            }
        }
        return resultElement;
    }
}
