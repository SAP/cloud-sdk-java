/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.cloudplatform;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.google.json.JsonSanitizer;
import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents a specific {@link CloudPlatform} that is used when running on the SAP Deploy with Confidence stack hosted
 * on Cloud Foundry.
 */
@Beta
@Slf4j
public class DwcCfCloudPlatform implements DwcCloudPlatform
{
    // #CLOUDECOSYSTEM-9176 (JS): Duplicates large portions of the existing implementation of the SDK

    private static final String VCAP_APPLICATION = "VCAP_APPLICATION";
    private static final String VCAP_SERVICES = "VCAP_SERVICES";

    private static final String APPLICATION_NAME = "application_name";
    private static final String APPLICATION_PROCESS_ID = "process_id";
    private static final String APPLICATION_URL = "application_uris";

    private static final AtomicReference<Map<String, JsonElement>> vcapApplicationCache = new AtomicReference<>(null);
    private static final AtomicReference<Map<String, JsonArray>> vcapServicesCache = new AtomicReference<>(null);
    private static final ConcurrentMap<String, DwcOutboundProxyBinding> outboundProxiesCache =
        new ConcurrentHashMap<>();

    @Setter
    @Nonnull
    private Function<String, String> environmentVariableReader = System::getenv;

    /**
     * Invalidates all internal caches holding the parsed VCAP_APPLICATION and VCAP_SERVICES environment variables.
     * <p>
     * <strong>Caution:This method is not thread-safe!</strong>
     */
    public static void invalidateCaches()
    {
        vcapApplicationCache.set(null);
        vcapServicesCache.set(null);
        outboundProxiesCache.clear();
    }

    /**
     * Be aware that this method is Beta (as indicated by the annotation) and therefore subject to breaking changes.
     *
     * @return The instance of this class if executed on the Cloud Foundry (CF) version of SAP Business Technology
     *         Platform. Else throws.
     * @throws CloudPlatformException
     *             If not executed on the Cloud Foundry (CF) version of SAP Business Technology Platform.
     */
    @Beta
    @Nonnull
    public static DwcCfCloudPlatform getInstanceOrThrow()
        throws CloudPlatformException
    {
        final CloudPlatform cloudPlatform = CloudPlatformAccessor.getCloudPlatform();

        if( !(cloudPlatform instanceof DwcCfCloudPlatform) ) {
            throw new CloudPlatformException(
                "The current Cloud platform is not an instance of "
                    + DwcCfCloudPlatform.class.getSimpleName()
                    + ". Please make sure to execute on SAP Business Technology Platform with Cloud Foundry technology.");
        }

        return (DwcCfCloudPlatform) cloudPlatform;
    }

    @Nonnull
    @Override
    public String getApplicationName()
        throws CloudPlatformException
    {
        final JsonElement applicationName = getVcapApplication().get(APPLICATION_NAME);

        if( applicationName != null && applicationName.isJsonPrimitive() ) {
            return applicationName.getAsString();
        }

        throw new CloudPlatformException(
            "Failed to get application name: environment variable '" + APPLICATION_NAME + "' not defined.");
    }

    @Nonnull
    @Override
    public String getApplicationProcessId()
        throws CloudPlatformException
    {
        final JsonElement applicationProcessId = getVcapApplication().get(APPLICATION_PROCESS_ID);

        if( applicationProcessId != null && applicationProcessId.isJsonPrimitive() ) {
            return applicationProcessId.getAsString();
        }

        throw new CloudPlatformException(
            "Failed to get application process id: "
                + "environment variable '"
                + APPLICATION_PROCESS_ID
                + "' not defined.");
    }

    @Nonnull
    @Override
    public String getApplicationUrl()
        throws CloudPlatformException
    {
        final JsonElement applicationUrl = getVcapApplication().get(APPLICATION_URL);

        if( applicationUrl != null && applicationUrl.isJsonArray() ) {
            final JsonArray applicationUrlArray = applicationUrl.getAsJsonArray();
            if( applicationUrlArray.size() > 0 ) {
                return applicationUrlArray.get(0).getAsString();
            }
        }

        throw new CloudPlatformException(
            "Failed to get application url: environment variable '" + APPLICATION_URL + "' not defined.");
    }

    /**
     * Provides access to the "VCAP_APPLICATION" environment variable.
     *
     * @return A map of the {@link JsonElement} entries in "VCAP_APPLICATION" by their names.
     * @throws CloudPlatformException
     *             If there is an issue accessing the environment variable.
     */
    @Nonnull
    public Map<String, JsonElement> getVcapApplication()
        throws CloudPlatformException
    {
        if( vcapApplicationCache.get() == null ) {
            vcapApplicationCache.compareAndSet(null, parseVcapApplication());
        }

        return vcapApplicationCache.get();
    }

    @Nonnull
    private Map<String, JsonElement> parseVcapApplication()
    {
        final String environmentVariable =
            getEnvironmentVariable(VCAP_APPLICATION)
                .getOrElseThrow(
                    () -> new CloudPlatformException(
                        "Environment variable '" + VCAP_APPLICATION + "' is not defined."));

        try {
            @Nullable
            final Map<String, JsonElement> result =
                new Gson()
                    .fromJson(JsonSanitizer.sanitize(environmentVariable), new TypeToken<Map<String, JsonElement>>()
                    {
                    }.getType());

            if( result == null ) {
                throw new CloudPlatformException(
                    "Environment variable '" + VCAP_APPLICATION + "' is defined, but empty.");
            }

            return result;
        }
        catch( final SecurityException e ) {
            throw new CloudPlatformException("Failed to access environment variable '" + VCAP_APPLICATION + "'.", e);
        }
        catch( final JsonParseException e ) {
            throw new CloudPlatformException("Failed to parse environment variable '" + VCAP_APPLICATION + "'.", e);
        }
    }

    /**
     * Provides access to the "VCAP_SERVICES" environment variable.
     *
     * @return A map of the {@link JsonArray} entries in "VCAP_SERVICES" by their names.
     * @throws CloudPlatformException
     *             If there is an issue accessing the environment variable.
     */
    @Nonnull
    public Map<String, JsonArray> getVcapServices()
        throws CloudPlatformException
    {
        if( vcapServicesCache.get() == null ) {
            vcapServicesCache.compareAndSet(null, parseVcapServices());
        }

        return vcapServicesCache.get();
    }

    @Nonnull
    private Map<String, JsonArray> parseVcapServices()
    {
        final String environmentVariable =
            getEnvironmentVariable(VCAP_SERVICES)
                .getOrElseThrow(
                    () -> new CloudPlatformException("Environment variable '" + VCAP_SERVICES + "' is not defined."));

        try {
            @Nullable
            final Map<String, JsonArray> result =
                new Gson().fromJson(JsonSanitizer.sanitize(environmentVariable), new TypeToken<Map<String, JsonArray>>()
                {
                }.getType());

            if( result == null ) {
                throw new CloudPlatformException("Environment variable '" + VCAP_SERVICES + "' is defined, but empty.");
            }

            return result;
        }
        catch( final SecurityException e ) {
            throw new CloudPlatformException("Failed to access environment variable '" + VCAP_SERVICES + "'.", e);
        }
        catch( final JsonParseException e ) {
            throw new CloudPlatformException("Failed to parse environment variable '" + VCAP_SERVICES + "'.", e);
        }
    }

    /**
     * Retrieves the environment variable by its name.
     *
     * @param name
     *            The name of the environment variable.
     * @return The environment variable with the given name, if present.
     */
    @Nonnull
    public Option<String> getEnvironmentVariable( @Nonnull final String name )
    {
        return Option.of(environmentVariableReader.apply(name));
    }

    @Override
    @Nonnull
    public Option<DwcOutboundProxyBinding> getOutboundProxyBinding()
    {
        return getOutboundProxyBinding(DwcOutboundProxyBinding.DEFAULT_SERVICE_BINDING_NAME);
    }

    @Override
    @Nonnull
    public DwcOutboundProxyBinding getOutboundProxyBindingOrThrow()
    {
        return getOutboundProxyBindingOrThrow(DwcOutboundProxyBinding.DEFAULT_SERVICE_BINDING_NAME);
    }

    @Override
    @Nonnull
    public DwcOutboundProxyBinding getOutboundProxyBindingOrThrow( @Nonnull final String bindingName )
    {
        return getOutboundProxyBinding(bindingName)
            .getOrElseThrow(
                () -> new CloudPlatformException(
                    String
                        .format(
                            "Unable to load the %s (Megaclite service URL). If you are running in a local environment, you may define an environment variable called \"%s\" that contains the Megaclite URL in a JSON object.",
                            DwcOutboundProxyBinding.class.getSimpleName(),
                            MegacliteConfigurationLoader.DWC_APPLICATION)));
    }

    @Override
    @Nonnull
    public Option<DwcOutboundProxyBinding> getOutboundProxyBinding( @Nonnull final String bindingName )
    {
        return Option.of(outboundProxiesCache.computeIfAbsent(bindingName, any -> loadOutboundProxyBinding()));
    }

    /**
     * Configure a custom {@link DwcOutboundProxyBinding} to be used. This allows for overriding the megaclite URL used
     * by the SDK. For example, pass:
     * {@code DwcOutboundProxyBinding.builder().uri(URI.create("http://localhost:4000")).build();}
     *
     * @param outboundProxy
     *            The name and URI of the proxy to use for outbound service calls.
     */
    public void setOutboundProxyBinding( @Nonnull final DwcOutboundProxyBinding outboundProxy )
    {
        outboundProxiesCache.put(outboundProxy.getName(), outboundProxy);
    }

    @Nullable
    private DwcOutboundProxyBinding loadOutboundProxyBinding()
    {
        return Try
            .of(MegacliteConfigurationLoader::loadMegacliteUrl)
            .onFailure(
                cause -> log
                    .warn(
                        "Failed to load URL of outbound proxy (megaclite). Proceeding without outbound proxy.",
                        cause))
            .getOrNull();
    }
}
