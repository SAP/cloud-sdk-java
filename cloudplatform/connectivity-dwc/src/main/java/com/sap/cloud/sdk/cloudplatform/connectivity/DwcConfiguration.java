/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;

import io.vavr.Lazy;
import io.vavr.control.Option;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
final class DwcConfiguration
{
    private static final String DWC_APPLICATION = "DWC_APPLICATION";
    private static final String PROVIDER_ID = "orbitProviderTenantId";
    private static final Gson gson = new Gson();

    @Nonnull
    @Getter
    private static final DwcConfiguration instance = new DwcConfiguration();

    @Nonnull
    private final Lazy<URI> megacliteUrl;
    @Nonnull
    private final Lazy<String> providerTenant;

    private DwcConfiguration()
    {
        this(System::getenv);
    }

    DwcConfiguration( @Nonnull final Function<String, String> environmentVariableReader )
    {
        megacliteUrl = Lazy.of(() -> loadMegacliteUri(environmentVariableReader));
        providerTenant = Lazy.of(() -> loadProviderTenantId(environmentVariableReader));
    }

    DwcConfiguration( @Nonnull final URI megacliteUrl, @Nonnull final String providerTenant )
    {
        this.megacliteUrl = Lazy.of(() -> megacliteUrl);
        this.providerTenant = Lazy.of(() -> providerTenant);
    }

    @Nonnull
    URI megacliteUrl()
        throws CloudPlatformException,
            IllegalArgumentException
    {
        return megacliteUrl.get();
    }

    @Nonnull
    String providerTenant()
        throws CloudPlatformException,
            IllegalArgumentException
    {
        return providerTenant.get();
    }

    @Nonnull
    private static URI loadMegacliteUri( @Nonnull final Function<String, String> environmentVariableReader )
        throws CloudPlatformException,
            IllegalArgumentException
    {
        final Option<String> dwcApplication = Option.of(environmentVariableReader.apply(DWC_APPLICATION));
        if( dwcApplication.isEmpty() ) {
            throw new CloudPlatformException(
                "No " + DWC_APPLICATION + " environment variable defined. Cannot determine the Megaclite service URI.");
        }

        return extractMegacliteUrlFromJson(dwcApplication.get());
    }

    @Nonnull
    private static String loadProviderTenantId( @Nonnull final Function<String, String> environmentVariableReader )
        throws CloudPlatformException,
            IllegalArgumentException
    {
        final Option<String> dwcApplication = Option.of(environmentVariableReader.apply(DWC_APPLICATION));
        if( dwcApplication.isEmpty() ) {
            throw new CloudPlatformException(
                "No " + DWC_APPLICATION + " environment variable found. Cannot determine provider account id.");
        }

        try {
            final JsonObject jsonObject = gson.fromJson(dwcApplication.get(), JsonObject.class);
            if( !jsonObject.has(PROVIDER_ID) || !jsonObject.get(PROVIDER_ID).isJsonPrimitive() ) {
                throw new IllegalArgumentException(
                    DWC_APPLICATION
                        + " did not contain an entry for the provider account id (expected name: "
                        + PROVIDER_ID
                        + ").");
            }

            return jsonObject.get(PROVIDER_ID).getAsString();
        }
        catch( final JsonParseException | IllegalArgumentException e ) {
            throw new CloudPlatformException(
                "Unable to determine the provider account id from the "
                    + DWC_APPLICATION
                    + " environment variable. The content does not match the expected format.",
                e);
        }
    }

    @Nonnull
    private static URI extractMegacliteUrlFromJson( @Nonnull final String dwcApplicationContent )
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
            return URI.create(megaclite.getAsJsonPrimitive("url").getAsString());
        }
        catch( final JsonParseException | IllegalArgumentException e ) {
            throw new CloudPlatformException("Content of " + DWC_APPLICATION + " is malformed.", e);
        }
    }
}
