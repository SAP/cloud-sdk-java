package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import lombok.RequiredArgsConstructor;

/**
 * For internal use only by data model classes
 */
@RequiredArgsConstructor
public class ODataCustomFieldAdapter extends TypeAdapter<Object>
{
    private final Gson gson;

    /**
     * For internal use only by data model classes
     */
    @Override
    @Nullable
    public Object read( @Nonnull final JsonReader in )
        throws IOException
    {
        switch( in.peek() ) {
            case NUMBER: {
                try {
                    return in.nextInt();
                }
                catch( final NumberFormatException | IllegalStateException notAnInteger ) {
                    try {
                        return in.nextLong();
                    }
                    catch( final NumberFormatException | IllegalStateException notALong ) {
                        return in.nextDouble();
                    }
                }
            }
            case BOOLEAN: {
                return in.nextBoolean();
            }
            case STRING: {
                final String value = in.nextString();

                if( !value.matches("/Date\\((-?\\p{Digit}+)\\)/") ) {
                    return value;
                }

                return new ODataDateTimeStringCalendarConverter().toDomainNonNull(value).orNull();
            }
            case BEGIN_ARRAY: {
                in.beginArray();
                final List<Object> valueList = new ArrayList<>();

                while( in.hasNext() ) {
                    valueList.add(read(in));
                }

                in.endArray();
                return valueList;
            }
            case BEGIN_OBJECT: {
                in.beginObject();
                final Map<String, Object> valueObject = new HashMap<>();

                while( in.hasNext() ) {
                    final String key = in.nextName();
                    if( "__deferred".equals(key) ) {
                        in.skipValue();
                        in.endObject();
                        return null;
                    } else if( "__metadata".equals(key) ) {
                        in.skipValue();
                    } else if( "results".equals(key) ) {
                        final Object valueList = read(in);
                        in.endObject();
                        return valueList;
                    } else {
                        valueObject.put(key, read(in));
                    }
                }

                in.endObject();
                return valueObject;
            }
            default: {
                in.skipValue();
            }
        }
        return null;
    }

    /**
     * For internal use only by data model classes
     */
    @Override
    public void write( @Nonnull final JsonWriter out, @Nullable final Object value )
        throws IOException
    {
        // No need to do anything here. Serialization to JSON is handled generically by Gson.
    }
}
