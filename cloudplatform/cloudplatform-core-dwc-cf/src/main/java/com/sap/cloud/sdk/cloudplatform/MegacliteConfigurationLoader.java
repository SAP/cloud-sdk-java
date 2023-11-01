/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.cloudplatform;

import java.net.URI;

import javax.annotation.Nonnull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;

import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class MegacliteConfigurationLoader
{
    static final String DWC_APPLICATION = "DWC_APPLICATION";
    static final String DWC_MEGACLITE_URL = "DWC_MEGACLITE_URL";

    private static final Gson gson = new Gson();

    @Nonnull
    static DwcOutboundProxyBinding loadMegacliteUrl()
    {
        final Option<String> dwcApplication = Option.of(System.getenv(DWC_APPLICATION));
        if( dwcApplication.isDefined() ) {
            return loadFromJson(dwcApplication.get());
        }

        final Option<String> dwcMegacliteUrl = Option.of(System.getenv(DWC_MEGACLITE_URL));
        if( dwcMegacliteUrl.isEmpty() ) {
            throw new CloudPlatformException(
                "No megaclite URL found in either "
                    + DWC_APPLICATION
                    + " or "
                    + DWC_MEGACLITE_URL
                    + " environment variable.");
        }
        return loadFromLegacyConfig(dwcMegacliteUrl.get());
    }

    @Nonnull
    private static DwcOutboundProxyBinding loadFromJson( @Nonnull final String dwcApplicationContent )
    {
        try {
            final JsonObject object = gson.fromJson(dwcApplicationContent, JsonObject.class);
            if( !object.has("megaclite") || !object.get("megaclite").isJsonObject() ) {
                throw new IllegalArgumentException(
                    DWC_APPLICATION + " did not contain an entry for the megaclite object.");
            }
            final JsonObject megaclite = object.getAsJsonObject("megaclite");
            if( !megaclite.has("url") || !megaclite.get("url").isJsonPrimitive() ) {
                throw new IllegalArgumentException(
                    "Entry for megaclite in " + DWC_APPLICATION + " did not contain an url.");
            }
            final URI uri = URI.create(megaclite.getAsJsonPrimitive("url").getAsString());
            return DwcOutboundProxyBinding.builder().uri(uri).build();
        }
        catch( final JsonParseException | IllegalArgumentException e ) {
            throw new CloudPlatformException("Content of " + DWC_APPLICATION + " is malformed.", e);
        }
    }

    @Nonnull
    private static DwcOutboundProxyBinding loadFromLegacyConfig( @Nonnull final String urlAsString )
    {
        log
            .warn(
                "Found legacy environment variable {}. "
                    + "The variables are replaced by {} and will be removed in the future.",
                DWC_MEGACLITE_URL,
                DWC_APPLICATION);

        try {
            final URI uri = URI.create(urlAsString);
            return DwcOutboundProxyBinding.builder().uri(uri).build();
        }
        catch( final IllegalArgumentException e ) {
            throw new CloudPlatformException("Content of " + DWC_MEGACLITE_URL + " is not a valid URL.", e);
        }
    }
}
