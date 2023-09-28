package com.sap.cloud.sdk.datamodel.odatav4.generator;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.sap.cloud.sdk.result.ElementName;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

/**
 * This class contains all properties (and their JSON mapping) of Swagger 2.0 and OpenAPI 3.x.x (to be read from from
 * swagger/open api file).
 *
 * @see ServiceDetailsSwagger2
 * @see ServiceDetailsOpenApi3
 */
@Data
abstract class AbstractServiceDetails implements ServiceDetails
{
    @ElementName( "info" )
    private Info info;

    @ElementName( "externalDocs" )
    private ExternalDocs externalDocs;

    @ElementName( "x-sap-software-min-version" )
    private String minErpVersion;

    @ElementName( "x-sap-ext-overview" )
    private List<ExternalOverview> extOverview;

    // this property is at best a duplicate of the stateInfo below, at worst gives opposite information
    // therefore we should not use this field
    // it's just added for documentation purposes
    @Getter( AccessLevel.NONE )
    @ElementName( "x-sap-api-deprecated" )
    private String deprecated;

    @ElementName( "x-sap-stateInfo" )
    private StateInfo stateInfo;

    @Override
    public Option<ServiceDetails.StateInfo> getStateInfo()
    {
        return Option.of(stateInfo);
    }

    @Override
    public boolean isDeprecated()
    {
        return getStateInfo().map(info -> State.Deprecated == info.getState()).getOrElse(false);
    }

    @Data
    private static class Info implements ServiceDetails.Info
    {
        @ElementName( "title" )
        private String title;

        @ElementName( "description" )
        @JsonAdapter( DescriptionAdapter.class )
        private String description;

        @ElementName( "version" )
        private String version;
    }

    @Data
    private static class ExternalDocs implements ServiceDetails.ExternalDocs
    {
        @ElementName( "description" )
        private String description;

        @ElementName( "url" )
        @JsonAdapter( UrlAdapter.class )
        private String url;
    }

    @Data
    private static class ExternalOverview implements ServiceDetails.ExternalOverview
    {
        @ElementName( "name" )
        private String name;

        @ElementName( "values" )
        @JsonAdapter( ExtOverviewAdapter.class )
        private List<String> values;
    }

    @Data
    private static class StateInfo implements ServiceDetails.StateInfo
    {
        @ElementName( "state" )
        private State state;

        @ElementName( "deprecationrelease" )
        private String deprecationRelease;

        @ElementName( "successorApi" )
        private String successorApi;

        @ElementName( "deprecationdate" )
        private String deprecationDate;
    }

    private static class DescriptionAdapter extends TypeAdapter<String>
    {
        @Override
        public void write( @Nonnull final JsonWriter out, @Nullable final String value )
            throws IOException
        {
            // noop, we only read the ServiceDetails
        }

        @Override
        public String read( @Nonnull final JsonReader jsonReader )
            throws IOException
        {
            final String result = jsonReader.nextString();
            return JavadocUtils.formatDescriptionText(result);
        }
    }

    private static class UrlAdapter extends TypeAdapter<String>
    {
        @Override
        public void write( @Nonnull final JsonWriter out, @Nullable final String value )
            throws IOException
        {
            // noop, we only read the ServiceDetails
        }

        @Override
        public String read( @Nonnull final JsonReader jsonReader )
            throws IOException
        {
            final String result = jsonReader.nextString();
            return result.trim();
        }
    }

    private static class ExtOverviewAdapter extends TypeAdapter<List<String>>
    {
        private static final String REGEX_PATTERN = "^\\[(.*)\\]\\((.*) \"(.*)\"\\)$";

        @Override
        public void write( @Nonnull final JsonWriter out, @Nullable final List<String> entityValues )
            throws IOException
        {
            // noop, we only read the ServiceDetails
        }

        @Override
        public List<String> read( @Nonnull final JsonReader in )
            throws IOException
        {
            final List<String> result = new LinkedList<>();
            in.beginArray();
            while( in.hasNext() ) {
                if( in.peek() == JsonToken.STRING ) {
                    result.add(in.nextString());
                } else {
                    String format = null;
                    String text = null;

                    in.beginObject();
                    while( in.peek() != JsonToken.END_OBJECT ) {
                        final String propertyName = in.nextName();
                        final String propertyValue = in.nextString();
                        if( propertyName.equals("format") ) {
                            format = propertyValue;
                        } else if( propertyName.equals("text") ) {
                            text = propertyValue;
                        }
                    }
                    in.endObject();

                    if( "markdown".equals(format) && text != null ) {
                        final String description =
                            JavadocUtils.formatDescriptionText(text.replaceAll(REGEX_PATTERN, "$1"));
                        final String url = text.replaceAll(REGEX_PATTERN, "$2");
                        result.add(String.format("<a href='%s'>%s</a>", url, description));
                    } else {
                        result.add(text);
                    }
                }
            }
            in.endArray();
            return result;
        }
    }
}
