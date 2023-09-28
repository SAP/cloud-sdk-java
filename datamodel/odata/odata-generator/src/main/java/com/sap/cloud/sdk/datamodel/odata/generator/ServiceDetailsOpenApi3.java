package com.sap.cloud.sdk.datamodel.odata.generator;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.sap.cloud.sdk.result.ElementName;

import io.vavr.control.Option;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * This class contains any OpenAPI 3.x.x specific fields and how to retrieve them from the JSON OpenAPI 3.x.x file.
 */
@Slf4j
@Data
@EqualsAndHashCode( callSuper = true )
class ServiceDetailsOpenApi3 extends AbstractServiceDetails
{
    @ElementName( "servers" )
    @JsonAdapter( ServersAdapter.class )
    private String serviceUrl;

    private static class ServersAdapter extends TypeAdapter<String>
    {
        @Override
        public void write( final JsonWriter out, final String value )
            throws IOException
        {
            // noop, we only read the ServiceDetails
        }

        @Override
        public String read( final JsonReader in )
            throws IOException
        {
            final List<String> serverUrls = extractServerUrls(in);

            if( serverUrls.isEmpty() ) {
                // according to the OpenAPI 3.x.x spec, an empty array of servers can be interpreted as '/'
                return "/";
            }
            final Option<String> basePathFromServerUrlTemplate = getBasePathFromServerUrlTemplate(serverUrls);

            if( basePathFromServerUrlTemplate.isDefined() ) {
                return basePathFromServerUrlTemplate.get();
            }

            log.warn("No server URL template supplied. Expected the format \"{protocol}://{host{:port}}/base/path\".");
            final Option<String> basePathFromApiHubSandbox = getBasePathFromApiHubSandbox(serverUrls);

            if( basePathFromApiHubSandbox.isDefined() ) {
                log.warn("Falling back to base path from the API Hub sandbox URL.");
                return basePathFromApiHubSandbox.get();
            }
            throw new ODataGeneratorReadException(
                "Could not read base path from the 'servers' property of the swagger file.");
        }

        private static Option<String> getBasePathFromServerUrlTemplate( @Nonnull final List<String> serverUrls )
        {
            return Option
                .ofOptional(
                    serverUrls
                        .stream()
                        .map(ServersAdapter::extractPath)
                        .distinct()
                        .filter(Option::isDefined)
                        .map(Option::get)
                        .findFirst());
        }

        private static Option<String> getBasePathFromApiHubSandbox( @Nonnull final List<String> serverUrls )
        {
            return Option
                .ofOptional(
                    serverUrls
                        .stream()
                        .filter(s -> s.contains("sandbox.api.sap.com"))
                        .map(s -> URI.create(s).getPath())
                        .distinct()
                        .findFirst());
        }

        // expected format: <any>://<any>/base/path/taken
        // in case of: <any>://<any> this will produce "/"
        private static Option<String> extractPath( final String urlString )
        {
            return Option
                .some(urlString)
                .filter(s -> s.contains("://"))
                .filter(s -> !s.endsWith("://"))
                .map(s -> urlString.split("://")[1])
                .map(s -> s.indexOf("/") > 0 ? s.substring(s.indexOf("/")) : "")
                .map(path -> path.isEmpty() ? "/" : path);
        }

        /**
         * Reads the url property servers object, according to the
         * <a href='https://swagger.io/specification/#serverObject'>specification</a>. <br>
         * The relevant part of the swagger file looks something like this (other properties not read are excluded):
         *
         * <pre>
         * {@code
         * "servers": [
         *   {
         *     "url": "https://sandbox.api.sap.com/othercloud/sap/opu/odata/my/custom/path/CompleteBusinessObject"
         *   },
         *   {
         *     "url": "https://{host}:{port}/othercloud/sap/opu/odata/my/custom/path/CompleteBusinessObject"
         *   }
         * ]
         * }
         * </pre>
         *
         * @param in
         *            The GSON reader at the position of the 'servers' property of the open api file
         * @return A list of the content of the url properties inside the servers array.
         * @throws IOException
         *             If any of the GSON methods encounter an error.
         */
        private List<String> extractServerUrls( final JsonReader in )
            throws IOException
        {
            final List<String> serverUrls = new ArrayList<>();
            in.beginArray();
            while( in.hasNext() ) {
                in.beginObject();
                while( in.peek() != JsonToken.END_OBJECT ) {
                    final String propertyName = in.nextName();
                    if( propertyName.equals("url") ) {
                        final String url = in.nextString();
                        serverUrls.add(url);
                    } else {
                        in.skipValue();
                    }
                }
                in.endObject();
            }
            in.endArray();
            return serverUrls;
        }
    }
}
