/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Serializable and deserializable response type for SCP CF Destination Service queries.
 */
@Data
@Beta
public class DestinationServiceV1Response
{
    @SerializedName( "owner" )
    private DestinationOwner owner;

    @SerializedName( "destinationConfiguration" )
    private Map<String, String> destinationConfiguration = Maps.newHashMap();

    @SerializedName( "authTokens" )
    private List<DestinationAuthToken> authTokens;

    @SerializedName( "certificates" )
    private List<DestinationCertificate> certificates;

    /**
     * Owner of the destination object with SCP CF subaccount and instance identifier reference.
     */
    @Data
    public static class DestinationOwner
    {
        @SerializedName( "SubaccountId" )
        private String subaccountId;

        @SerializedName( "InstanceId" )
        private String InstanceId;
    }

    /**
     * Destination authentication token with optional error message and expiration value.
     */
    @Data
    public static class DestinationAuthToken
    {
        @SerializedName( "type" )
        private String type; // ignored

        @SerializedName( "value" )
        private String value; // ignored

        @SerializedName( "error" )
        private String error;

        @SerializedName( "expires_in" )
        private String expiresIn;

        @SerializedName( "http_header" )
        @JsonAdapter( GsonHeaderDeserializer.class )
        private Header httpHeaderSuggestion;

        @Nullable
        @Getter( AccessLevel.PACKAGE )
        @Setter( AccessLevel.PACKAGE )
        private transient LocalDateTime expiryTimestamp = null; // ignored for deserialization and serialization
    }

    private static final class GsonHeaderDeserializer implements JsonDeserializer<Header>
    {
        @Nullable
        @Override
        public Header deserialize(
            @Nonnull final JsonElement json,
            @Nullable final Type typeOfT,
            @Nullable final JsonDeserializationContext context )
            throws JsonParseException
        {
            final JsonObject jsonObject = json.getAsJsonObject();

            if( jsonObject == null ) {
                return null;
            }
            final String name = jsonObject.get("key").getAsString();
            final String value = jsonObject.get("value").getAsString();

            if( name == null ) {
                return null;
            }
            return new Header(name, value);
        }
    }

    /**
     * Named destination certificate containing optional base64 encoded binary content.
     */
    @Data
    public static class DestinationCertificate
    {
        @SerializedName( "Name" )
        private String name;

        @SerializedName( "Content" )
        private String content;

        @SerializedName( "Type" )
        private String type;

        @Nullable
        @Getter( AccessLevel.PACKAGE )
        @Setter( AccessLevel.PACKAGE )
        private transient LocalDateTime expiryTimestamp = null; // ignored for deserialization and serialization
    }
}
