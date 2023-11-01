/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.annotations.Beta;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.google.json.JsonSanitizer;
import com.sap.cloud.environment.servicebinding.SapVcapServicesServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.api.DefaultServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.api.ServiceBindingMerger;
import com.sap.cloud.environment.servicebinding.api.SimpleServiceBindingCache;
import com.sap.cloud.environment.servicebinding.api.TypedListView;
import com.sap.cloud.environment.servicebinding.api.TypedMapView;
import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;
import com.sap.cloud.sdk.cloudplatform.exception.MultipleServiceBindingsException;
import com.sap.cloud.sdk.cloudplatform.exception.NoServiceBindingException;
import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation for the SAP Business Technology Platform Cloud Foundry variant.
 */
@Slf4j
public class ScpCfCloudPlatform implements CloudPlatform
{
    private static final String VCAP_APPLICATION = "VCAP_APPLICATION";
    private static final String VCAP_SERVICES = "VCAP_SERVICES";

    private static final String APPLICATION_NAME = "application_name";
    private static final String XSAPPNAME = "xsappname";
    private static final String APPLICATION_PROCESS_ID = "process_id";
    private static final String APPLICATION_URL = "application_uris";

    private static final String SERVICE_NAME_XSUAA = "xsuaa";
    private static final String SERVICE_NAME_DESTINATION = "destination";
    private static final String SERVICE_NAME_CONNECTIVITY = "connectivity";

    private static final String SERVICE_CREDENTIALS = "credentials";
    private static final String SERVICE_PLAN = "plan";
    private static final String SERVICE_PLAN_BROKER = "broker";

    private static final AtomicReference<Map<String, JsonElement>> vcapApplicationCache = new AtomicReference<>(null);

    @Nonnull
    private Function<String, String> environmentVariableReader = System::getenv;

    @Nullable
    private ServiceBindingAccessor serviceBindingAccessor;

    /**
     * Setter for the {@link ServiceBindingAccessor} to be used by the SDK to read {@link ServiceBinding}s.
     * <p>
     * <b>Note:</b> This accessor will be overwritten once an environment variable reader is set via the
     * {@link #setEnvironmentVariableReader(Function)} method.
     * <p>
     * <b>Note:</b> This API is considered <b>experimental</b> because some use cases have not been validated yet.
     *
     * @param serviceBindingAccessor
     *            The new {@link ServiceBindingAccessor} to use. If {@code null} is given, the
     *            {@link DefaultServiceBindingAccessor#getInstance()} will be queried and used every time.
     */
    @Beta
    private void setServiceBindingAccessor( @Nullable final ServiceBindingAccessor serviceBindingAccessor )
    {
        this.serviceBindingAccessor = serviceBindingAccessor;
    }

    /**
     * Returns the {@link ServiceBindingAccessor} instance, which might be customized through the
     * {@link #setServiceBindingAccessor(ServiceBindingAccessor)} method.
     * <p>
     * If no explicit {@link ServiceBindingAccessor} is configured, the
     * {@link DefaultServiceBindingAccessor#getInstance()} will be used.
     * </p>
     *
     * @return The explicitly set {@link ServiceBindingAccessor} (using the
     *         {@link #setServiceBindingAccessor(ServiceBindingAccessor)} method) or whatever
     *         {@link DefaultServiceBindingAccessor#getInstance()} returns otherwise.
     * @since 4.16.0
     */
    @Nonnull
    @Beta
    private ServiceBindingAccessor getServiceBindingAccessor()
    {
        if( serviceBindingAccessor == null ) {
            return DefaultServiceBindingAccessor.getInstance();
        }

        return serviceBindingAccessor;
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>Implementation Note:</b> Using this method will reset the internally used {@link ServiceBindingAccessor} to
     * our default, where the environment variables are read via the {@link #getEnvironmentVariable(String)} method.
     *
     * @param environmentVariableReader
     *            A generic key-value mapping.
     */
    @Override
    public void setEnvironmentVariableReader( @Nonnull final Function<String, String> environmentVariableReader )
    {
        this.environmentVariableReader = environmentVariableReader;
        setServiceBindingAccessor(newServiceBindingAccessorWithCustomEnvironmentVariableReader());
    }

    /**
     * Invalidates all internal caches holding the parsed VCAP_APPLICATION environment variable.
     * <p>
     * <strong>Caution:This method is not thread-safe!</strong>
     */
    //    public static void invalidateCaches()
    //    {
    //        vcapApplicationCache.set(null);
    //    }

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
    public static ScpCfCloudPlatform getInstanceOrThrow()
        throws CloudPlatformException
    {
        final CloudPlatform cloudPlatform = CloudPlatformAccessor.getCloudPlatform();

        if( !(cloudPlatform instanceof ScpCfCloudPlatform) ) {
            throw new CloudPlatformException(
                "The current Cloud platform is not an instance of "
                    + ScpCfCloudPlatform.class.getSimpleName()
                    + ". Please make sure to execute on SAP Business Technology Platform with Cloud Foundry technology.");
        }

        return (ScpCfCloudPlatform) cloudPlatform;
    }

    /**
     * Retrieves the environment variable by its name.
     *
     * @param name
     *            The name of the environment variable.
     * @return The environment variable with the given name, if present.
     */
    @Nonnull
    private Option<String> getEnvironmentVariable( @Nonnull final String name )
    {
        return Option.of(environmentVariableReader.apply(name));
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
     * Provides access to the "VCAP_APPLICATION" environment variable.
     *
     * @return A map of the {@link JsonElement} entries in "VCAP_APPLICATION" by their names.
     * @throws CloudPlatformException
     *             If there is an issue accessing the environment variable.
     */
    @Nonnull
    private Map<String, JsonElement> getVcapApplication()
        throws CloudPlatformException
    {
        if( vcapApplicationCache.get() == null ) {
            vcapApplicationCache.compareAndSet(null, parseVcapApplication());
        }

        return vcapApplicationCache.get();
    }

    // package private for testing
    @Nonnull
    private ServiceBindingAccessor newServiceBindingAccessorWithCustomEnvironmentVariableReader()
    {
        log
            .debug(
                "Instantiating a new '{}', which uses the custom environment variable reader.",
                ServiceBindingAccessor.class.getName());

        final List<ServiceBindingAccessor> accessors =
            Lists
                .newArrayList(
                    ServiceLoader.load(ServiceBindingAccessor.class, ScpCfCloudPlatform.class.getClassLoader()));
        if( accessors.removeIf(SapVcapServicesServiceBindingAccessor.class::isInstance) ) {
            accessors.add(new SapVcapServicesServiceBindingAccessor(name -> getEnvironmentVariable(name).getOrNull()));
        }

        // This merges the service bindings, but keeps only one copy of duplicates
        final ServiceBindingMerger.EqualityComparer equalityComparer = Object::equals;
        final ServiceBindingMerger serviceBindingMerger = new ServiceBindingMerger(accessors, equalityComparer);

        return new SimpleServiceBindingCache(serviceBindingMerger);
    }

    @Nonnull
    private Map<String, JsonArray> parseVcapServices()
    {
        final List<ServiceBinding> serviceBindings = getServiceBindingAccessor().getServiceBindings();
        final Map<String, List<ServiceBinding>> groupedBindings =
            serviceBindings
                .stream()
                .filter(binding -> binding.getServiceName().isPresent())
                .collect(groupingBy(binding -> binding.getServiceName().orElseThrow(ShouldNotHappenException::new)));

        final Map<String, JsonArray> wrappedBindings = new HashMap<>();
        groupedBindings.forEach(( key, value ) -> {
            final JsonArray wrapped = new JsonArray();

            for( final ServiceBinding binding : value ) {
                wrapped.add(convert(binding));
            }
            wrappedBindings.put(key, wrapped);
        });

        return wrappedBindings;
    }

    private JsonObject convert( @Nonnull final ServiceBinding serviceBinding )
    {
        final JsonObject convertedServiceBinding = convert(TypedMapView.of(serviceBinding));

        final BiConsumer<String, Supplier<JsonElement>> converter = ( key, supplier ) -> {
            final JsonElement val = supplier.get();
            if( val != null ) {
                convertedServiceBinding.add(key, val);
            }
        };

        converter.accept("name", () -> serviceBinding.getName().map(JsonPrimitive::new).orElse(null));
        converter.accept("label", () -> serviceBinding.getServiceName().map(JsonPrimitive::new).orElse(null));
        converter.accept("plan", () -> serviceBinding.getServicePlan().map(JsonPrimitive::new).orElse(null));
        converter
            .accept(
                "tags",
                () -> serviceBinding.getTags().stream().collect(JsonArray::new, JsonArray::add, JsonArray::addAll));
        // DwC: The binding doesn't have credentials, MegacliteServiceBinding either reads the DWC_APPLICATION env var
        // and returns the map {tenantid: <ProviderTenantId>}, or, returns an empty map if DWC_APPLICATION is not found.
        converter.accept("credentials", () -> convert(TypedMapView.ofCredentials(serviceBinding)));

        return convertedServiceBinding;
    }

    private JsonObject convert( @Nonnull final TypedMapView mapView )
    {
        final JsonObject resultObject = new JsonObject();
        final Set<String> keys = mapView.getKeys();
        for( final String key : keys ) {
            final Object value = mapView.get(key);
            if( value == null ) {
                resultObject.add(key, null);
            } else if( value instanceof Number ) {
                resultObject.addProperty(key, (Number) value);
            } else if( value instanceof String ) {
                resultObject.addProperty(key, (String) value);
            } else if( value instanceof Boolean ) {
                resultObject.addProperty(key, (Boolean) value);
            } else if( value instanceof Character ) {
                resultObject.addProperty(key, (Character) value);
            } else if( value instanceof TypedListView ) {
                resultObject.add(key, convert((TypedListView) value));
            } else {
                resultObject.add(key, convert(mapView.getMapView(key)));
            }
        }
        return resultObject;
    }

    private JsonArray convert( @Nonnull final TypedListView listView )
    {
        final JsonArray resultArray = new JsonArray();
        for( int i = 0; i < listView.getSize(); i++ ) {
            final Object value = listView.get(i);
            if( value == null ) {
                // the String type here is just used to work around the method ambiguity
                resultArray.add((String) null);
            } else if( value instanceof Number ) {
                resultArray.add((Number) value);
            } else if( value instanceof String ) {
                resultArray.add((String) value);
            } else if( value instanceof Boolean ) {
                resultArray.add((Boolean) value);
            } else if( value instanceof Character ) {
                resultArray.add((Character) value);
            } else if( value instanceof TypedListView ) {
                resultArray.add(convert((TypedListView) value));
            } else {
                resultArray.add(convert(listView.getMapView(i)));
            }
        }
        return resultArray;
    }

    /**
     * Provides access to the "VCAP_SERVICES" environment variable.
     *
     * @return A map of the {@link JsonArray} entries in "VCAP_SERVICES" by their names.
     * @throws CloudPlatformException
     *             If there is an issue accessing the environment variable.
     */
    @Nonnull
    private Map<String, JsonArray> getVcapServices()
        throws CloudPlatformException
    {
        return parseVcapServices();
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
            "Failed to get application process id: environment variable '" + APPLICATION_PROCESS_ID + "' not defined.");
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
     * Retrieves the "xsappname" from the XSUAA service instance credentials. This method assumes that only one binding
     * to an XSUAA service instance exists.
     *
     * @return The XS application name.
     * @throws CloudPlatformException
     *             If there is an issue accessing XS application name.
     * @throws NoServiceBindingException
     *             If there is no binding to the XSUAA service.
     * @throws MultipleServiceBindingsException
     *             If there are multiple bindings to the XSUAA service.
     */
    @Nonnull
    public String getXsAppName()
        throws CloudPlatformException,
            NoServiceBindingException,
            MultipleServiceBindingsException
    {
        final JsonElement xsappname = getXsuaaServiceCredentials().getAsJsonObject().get(XSAPPNAME);

        if( xsappname != null && xsappname.isJsonPrimitive() ) {
            return xsappname.getAsString();
        }

        throw new CloudPlatformException(
            String
                .format(
                    "Failed to retrieve '%s' from '%s' of '%s' service.",
                    XSAPPNAME,
                    SERVICE_CREDENTIALS,
                    SERVICE_NAME_XSUAA));
    }

    /**
     * Retrieves the credentials of a service. This method assumes that only one binding to a service must exist.
     *
     * @param serviceName
     *            The name of the service.
     * @return The {@link JsonObject} for the service credentials.
     * @throws CloudPlatformException
     *             If there is an issue accessing the service credentials.
     * @throws NoServiceBindingException
     *             If there is no binding to the requested service.
     * @throws MultipleServiceBindingsException
     *             If there are multiple bindings to the requested service.
     */
    @Nonnull
    private JsonObject getServiceCredentials( @Nonnull final String serviceName )
        throws CloudPlatformException,
            NoServiceBindingException,
            MultipleServiceBindingsException
    {
        return getServiceCredentials(serviceName, null);
    }

    /**
     * Retrieves the credentials of a service. This method assumes that there may be multiple bindings to a service,
     * however, only one must exist for the requested service plan.
     *
     * @param serviceName
     *            The name of the service.
     * @param servicePlan
     *            The plan of the service (optional). If {@code null}, the service plan is ignored when retrieving the
     *            service credentials, i.e., only one binding must exist to the service.
     * @return The {@link JsonObject} for the service credentials.
     * @throws CloudPlatformException
     *             If there is an issue accessing the service credentials.
     * @throws NoServiceBindingException
     *             If there is no binding to the requested service.
     * @throws MultipleServiceBindingsException
     *             If there are multiple bindings to the requested service.
     */
    @Nonnull
    private JsonObject getServiceCredentials( @Nonnull final String serviceName, @Nullable final String servicePlan )
        throws CloudPlatformException,
            NoServiceBindingException,
            MultipleServiceBindingsException
    {
        final List<JsonObject> serviceCredentials = new ArrayList<>();

        @Nullable
        final JsonArray jsonArray = getVcapServices().get(serviceName);

        if( jsonArray != null ) {
            for( final JsonElement jsonElement : jsonArray ) {
                if( jsonElement == null || !jsonElement.isJsonObject() ) {
                    log.warn("Skipping service credentials: JSON element is not an object.");
                    continue;
                }

                @Nullable
                final JsonElement credentials = jsonElement.getAsJsonObject().get(SERVICE_CREDENTIALS);

                if( credentials == null || !credentials.isJsonObject() ) {
                    log.warn("Skipping service credentials: JSON element '{}' is not an object.", SERVICE_CREDENTIALS);
                    continue;
                }

                if( servicePlan != null ) {
                    @Nullable
                    final JsonElement plan = jsonElement.getAsJsonObject().get(SERVICE_PLAN);

                    if( plan == null || !plan.isJsonPrimitive() ) {
                        log.warn("Skipping service credentials: JSON element '{}' is not a primitive.", SERVICE_PLAN);
                        continue;
                    }

                    if( !servicePlan.equals(plan.getAsString()) ) {
                        log
                            .debug(
                                "Skipping service credentials: service plan '{}' does not match requested plan '{}'.",
                                plan.getAsString(),
                                servicePlan);
                        continue;
                    }
                }

                serviceCredentials.add(credentials.getAsJsonObject());
            }
        }

        if( serviceCredentials.isEmpty() ) {
            throw new NoServiceBindingException(
                String
                    .format(
                        "Failed to get '%s' service credentials from %s variable: no service binding found for service plan '%s'. Please make sure to correctly bind your application to a service instance of the %s service.",
                        serviceName,
                        VCAP_SERVICES,
                        servicePlan == null ? "(any)" : servicePlan,
                        serviceName));
        }

        if( serviceCredentials.size() > 1 ) {
            throw new MultipleServiceBindingsException(
                String
                    .format(
                        "Failed to get '%s' service credentials from %s variable: multiple service bindings found for service plan '%s'. Please make sure to correctly bind your application to a service instance of the %s service.",
                        serviceName,
                        VCAP_SERVICES,
                        servicePlan == null ? "(any)" : servicePlan,
                        serviceName));
        }

        return serviceCredentials.get(0);
    }

    /**
     * Retrieves the OAuth2 service credentials. This method assumes that only one binding to an XSUAA service instance
     * exists.
     *
     * @return The {@link JsonObject} for the XSUAA service credentials.
     * @throws CloudPlatformException
     *             If there is an issue accessing the service credentials.
     * @throws NoServiceBindingException
     *             If there is no binding to the XSUAA service.
     * @throws MultipleServiceBindingsException
     *             If there are multiple bindings to the XSUAA service.
     */
    @Nonnull
    private JsonObject getXsuaaServiceCredentials()
        throws CloudPlatformException,
            NoServiceBindingException,
            MultipleServiceBindingsException
    {
        // Favor the unified broker plan over other plans. But still support xsuaa instance(s) of other plans to
        // accommodate consumers who have not yet transitioned and need their old instance(s).
        return Try
            .of(() -> getServiceCredentials(SERVICE_NAME_XSUAA, SERVICE_PLAN_BROKER))
            .recover(NoServiceBindingException.class, ( e ) -> getServiceCredentials(SERVICE_NAME_XSUAA, null))
            .get();
    }

    /**
     * Retrieves the XSUAA service credentials. This method assumes that there may be multiple bindings to the XSUAA
     * service, however, only one must exist for the requested service plan.
     *
     * @param servicePlan
     *            The plan of the service (optional). If {@code null}, the service plan is ignored when retrieving the
     *            service credentials, i.e., only one binding must exist to the service.
     * @return The {@link JsonObject} for the XSUAA credentials.
     * @throws CloudPlatformException
     *             If there is an issue accessing the service credentials.
     * @throws NoServiceBindingException
     *             If there is no binding to the XSUAA service.
     * @throws MultipleServiceBindingsException
     *             If there are multiple bindings to the XSUAA service.
     */
    @Nonnull
    private JsonObject getXsuaaServiceCredentials( @Nonnull final String servicePlan )
        throws CloudPlatformException,
            NoServiceBindingException,
            MultipleServiceBindingsException
    {
        return getServiceCredentials(SERVICE_NAME_XSUAA, servicePlan);
    }

    /**
     * Retrieves the service credentials for all bound XSUAA service instances. This method does not make assumptions
     * about the number of bound service instances.
     *
     * @return A list of {@link JsonObject} containing the credentials for all bound XSUAA service instances.
     * @throws CloudPlatformException
     *             If there is an issue accessing the service credentials.
     */
    @Nonnull
    private List<JsonObject> getXsuaaServiceCredentialsList()
        throws CloudPlatformException
    {
        return getCredentialsList(SERVICE_NAME_XSUAA);
    }

    /**
     * Returns audiences from the given JWT based on the audience within the JWT.
     * <p>
     * <strong>Note:</strong> This method handles two issues with UAA where audiences can be missing for user token
     * flows or when scopes contain a dot. For these cases, audiences are inferred by using the prefix of scopes until
     * the first dot within the scope name.
     *
     * @param jwt
     *            The JWT to get the audiences from.
     * @return A list of audiences.
     */
    @Nonnull
    private Set<String> getAudiences( @Nonnull final DecodedJWT jwt )
    {
        final Set<String> audiences = new HashSet<>();

        if( jwt.getAudience() != null ) {
            for( final String audience : jwt.getAudience() ) {
                if( audience.contains(".") ) {
                    // Currently, scopes containing dots are allowed.
                    // Since the UAA builds audiences by taking the substring of scopes up to the last dot,
                    // scopes with dots will lead to an incorrect audience which is worked around here.
                    audiences.add(audience.substring(0, audience.indexOf(".")));
                } else {
                    audiences.add(audience);
                }
            }
        }

        // If a JWT contains no audience, infer audiences based on the scope names in the JWT.
        // This is currently necessary as the UAA does not correctly fill the audience in the user token flow.
        if( audiences.isEmpty() ) {
            try {
                final List<String> scopes = jwt.getClaim("scope").asList(String.class);

                if( scopes != null ) {
                    for( final String scope : scopes ) {
                        if( scope.contains(".") ) {
                            final String audience = scope.substring(0, scope.indexOf("."));
                            audiences.add(audience);
                        }
                    }
                }
            }
            catch( final JWTDecodeException e ) {
                log.warn("Unable to derive audiences: failed to get scopes from JWT.", e);
            }
        }

        return audiences;
    }

    /**
     * <p>
     * Retrieves the service credentials for all bound XSUAA service instances based on the given JSON Web Token and the
     * service plans of each instance.
     * </p>
     * <p>
     * First the xsuaa instances are matched against the client ID and audiences of the JWT token. Then the service plan
     * is checked on the matching instances. If a unified broker instance is found among the filtered instances
     * ({@code plan = "broker"}) then it is selected and returned, even if there are other bound xsuaa instances. This
     * is done to accommodate the recommended migration towards a unified broker instance, where other instances of
     * other plans (e.g. {@code application}) may still exist.
     * </p>
     * <p>
     * This method does not make assumptions about the number of bound service instances. However, only one instance of
     * plan {@code broker} and/or one instance of another plan is permitted.
     * </p>
     *
     * @param jwt
     *            The JWT for which to return the matching XSUAA service instance credentials.
     * @return The {@link JsonObject} for the XSUAA service credentials.
     * @throws CloudPlatformException
     *             If there is an issue accessing the service credentials.
     * @throws NoServiceBindingException
     *             If there is no matching binding to a XSUAA service instance.
     * @throws MultipleServiceBindingsException
     *             If there are multiple bindings to matching XSUAA service instances.
     */
    @Nonnull
    private JsonObject getXsuaaServiceCredentials( @Nonnull final DecodedJWT jwt )
        throws CloudPlatformException,
            NoServiceBindingException,
            MultipleServiceBindingsException
    {
        final Map<String, List<JsonObject>> xsuaaInstancesByPlan = getXsuaaCredentialsByPlan(jwt);
        if( xsuaaInstancesByPlan.isEmpty() ) {
            throw new NoServiceBindingException(
                "Failed to get XSUAA service instance credentials for the given JWT: no matching service binding found.");
        }

        // Favor the unified broker plan over other plans. But still support xsuaa instance(s) of other plans to
        // accommodate consumers who have not yet transitioned and need their old instance(s).
        List<JsonObject> credentials = xsuaaInstancesByPlan.get(SERVICE_PLAN_BROKER);
        if( credentials == null ) {
            credentials = xsuaaInstancesByPlan.values().stream().flatMap(List::stream).collect(toList());
        }

        if( credentials.size() > 1 ) {
            log.error("Expected one matching service binding but found {}", credentials);
            throw new MultipleServiceBindingsException(
                "Failed to get XSUAA service instance credentials for the given JWT: multiple matching service bindings found.");
        }
        return credentials.get(0);
    }

    /**
     * Create a mapping of XSUAA instance credentials grouped by service plan.
     *
     * @param filterJwt
     *            An optional filter to select specific instance for result set.
     * @return A mapping of instance credentials grouped by service plan.
     */
    @Beta
    @Nonnull
    private Map<String, List<JsonObject>> getXsuaaCredentialsByPlan( @Nullable final DecodedJWT filterJwt )
    {
        final Predicate<JsonObject> xsuaaPredicate = json -> {
            // no check required
            if( filterJwt == null ) {
                return true;
            }
            // check for matching client id
            final Option<String> jwtClientId = Option.of(filterJwt.getClaim("client_id")).map(Claim::asString);
            final Option<String> serviceClientId = Option.of(json.get("clientid")).map(JsonElement::getAsString);
            if( jwtClientId.equals(serviceClientId) ) {
                return true;
            }

            // check for matching app name
            final Set<String> audiences = getAudiences(filterJwt);
            final Option<String> serviceAppname = Option.of(json.get("xsappname")).map(JsonElement::getAsString);
            return serviceAppname.isDefined() && audiences.contains(serviceAppname.get());
        };

        return getServiceCredentialsByPlan(SERVICE_NAME_XSUAA, xsuaaPredicate);

    }

    /**
     * Extract service credentials grouped by service plan for a given service name.
     *
     * @param name
     *            The service name to look for.
     * @param filter
     *            The filter to select specific credentials.
     * @return A mapping of service plan to service binding credentials.
     * @throws CloudPlatformException
     *             If the environment variables can not be extracted.
     */
    @Nonnull
    @Beta
    private
        Map<String, List<JsonObject>>
        getServiceCredentialsByPlan( @Nonnull final String name, @Nonnull final Predicate<JsonObject> filter )
            throws CloudPlatformException
    {
        return getServiceList(name)
            .stream()
            .map(o -> Tuple.of(o.get(SERVICE_PLAN).getAsString(), o.get(SERVICE_CREDENTIALS).getAsJsonObject()))
            .filter(o -> filter.test(o._2()))
            .collect(groupingBy(Tuple2::_1, mapping(Tuple2::_2, toList())));
    }

    /**
     * Retrieves the destination service credentials. This method assumes that only one binding must exist to the
     * destination service.
     *
     * @return The {@link JsonObject} for the destination service credentials.
     * @throws CloudPlatformException
     *             If there is an issue accessing the service credentials.
     * @throws NoServiceBindingException
     *             If there is no binding to the destination service.
     * @throws MultipleServiceBindingsException
     *             If there are multiple bindings to the destination service.
     */
    @Nonnull
    private JsonObject getDestinationServiceCredentials()
        throws CloudPlatformException,
            NoServiceBindingException,
            MultipleServiceBindingsException
    {
        return getServiceCredentials(SERVICE_NAME_DESTINATION);
    }

    /**
     * Retrieves the service credentials for all bound destination service instances. This method does not make
     * assumptions about the number of bound service instances.
     *
     * @return A list of {@link JsonObject} containing the credentials for all bound destination service instances.
     * @throws CloudPlatformException
     *             If there is an issue accessing the service credentials.
     */
    @Nonnull
    private List<JsonObject> getDestinationServiceCredentialsList()
        throws CloudPlatformException
    {
        return getCredentialsList(SERVICE_NAME_DESTINATION);
    }

    /**
     * Retrieves the connectivity service credentials. This method assumes that only one binding must exist to the
     * connectivity service.
     *
     * @return The {@link JsonObject} for the connectivity service credentials.
     * @throws CloudPlatformException
     *             If there is an issue accessing the service credentials.
     * @throws NoServiceBindingException
     *             If there is no binding to the connectivity service.
     * @throws MultipleServiceBindingsException
     *             If there are multiple bindings to the connectivity service.
     */
    @Nonnull
    private JsonObject getConnectivityServiceCredentials()
        throws CloudPlatformException,
            NoServiceBindingException,
            MultipleServiceBindingsException
    {
        return getServiceCredentials(SERVICE_NAME_CONNECTIVITY);
    }

    /**
     * Retrieves the service credentials for all bound connectivity service instances. This method does not make
     * assumptions about the number of bound service instances.
     *
     * @return A list of {@link JsonObject} containing the credentials for all bound connectivity service instances.
     * @throws CloudPlatformException
     *             If there is an issue accessing the service credentials.
     */
    @Nonnull
    private List<JsonObject> getConnectivityServiceCredentialsList()
        throws CloudPlatformException
    {
        return getCredentialsList(SERVICE_NAME_CONNECTIVITY);
    }

    @Nonnull
    private List<JsonObject> getServiceList( @Nonnull final String serviceName )
        throws CloudPlatformException
    {
        if( !getVcapServices().containsKey(serviceName) ) {
            return new ArrayList<>();
        }

        final List<JsonObject> serviceInstances = new ArrayList<>();

        @Nullable
        final JsonArray jsonArray = getVcapServices().get(serviceName);

        if( jsonArray != null ) {
            for( final JsonElement jsonElement : jsonArray ) {
                if( jsonElement == null || !jsonElement.isJsonObject() ) {
                    log.warn("Skipping service credentials: JSON element is not an object.");
                    continue;
                }

                @Nullable
                final JsonElement credentials = jsonElement.getAsJsonObject().get(SERVICE_CREDENTIALS);

                if( credentials == null || !credentials.isJsonObject() ) {
                    log.warn("Skipping service credentials: JSON element '{}' is not an object.", SERVICE_CREDENTIALS);
                    continue;
                }
                serviceInstances.add(jsonElement.getAsJsonObject());
            }
        }
        return serviceInstances;
    }

    @Nonnull
    private List<JsonObject> getCredentialsList( @Nonnull final String serviceName )
        throws CloudPlatformException
    {
        return getServiceList(serviceName)
            .stream()
            .map(service -> service.getAsJsonObject(SERVICE_CREDENTIALS))
            .collect(toList());
    }
}
