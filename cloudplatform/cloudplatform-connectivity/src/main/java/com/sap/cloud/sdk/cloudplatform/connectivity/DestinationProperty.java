/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationPropertyKey.createCollectionProperty;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationPropertyKey.createListProperty;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationPropertyKey.createProperty;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationPropertyKey.createStringProperty;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationPropertyKey.createUriProperty;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import com.google.common.annotations.Beta;
import com.sap.cloud.sdk.cloudplatform.servlet.LocaleAccessor;

/**
 * Class that holds the PropertyKeys which can be used to access properties of destinations. Use this class like an
 * enum.
 *
 * @since 4.3.0
 */
@SuppressWarnings( "PMD.TooManyStaticImports" )
public class DestinationProperty
{
    /**
     * Name of destination.
     */
    public static final DestinationPropertyKey<String> NAME = createStringProperty("Name");

    /**
     * {@link DestinationType} of destination, e.g. HTTP, RFC.
     */
    public static final DestinationPropertyKey<DestinationType> TYPE =
        createProperty("Type", DestinationType.class, DestinationType::ofIdentifier);

    /**
     * URI of destination.
     */
    public static final DestinationPropertyKey<String> URI = createStringProperty("URL");

    /**
     * {@link AuthenticationType} of destination.
     */
    public static final DestinationPropertyKey<AuthenticationType> AUTH_TYPE =
        createProperty("Authentication", AuthenticationType.class, AuthenticationType::ofIdentifier);

    /**
     * Fallback {@link AuthenticationType} of destination if <code>AUTH_TYPE</code> is not supported.
     */
    public static final DestinationPropertyKey<AuthenticationType> AUTH_TYPE_FALLBACK =
        createProperty("authType", AuthenticationType.class, AuthenticationType::ofIdentifier);

    /**
     * Username for basic authentication of destination.
     */
    public static final DestinationPropertyKey<String> BASIC_AUTH_USERNAME = createStringProperty("User");

    /**
     * Fallback username for basic authentication of destination if <code>BASIC_AUTH_USERNAME</code> is not supported.
     */
    public static final DestinationPropertyKey<String> BASIC_AUTH_USERNAME_FALLBACK = createStringProperty("username");

    /**
     * Password for basic authentication of destination.
     */
    public static final DestinationPropertyKey<String> BASIC_AUTH_PASSWORD = createStringProperty("Password");

    /**
     * TLS version used.
     */
    public static final DestinationPropertyKey<String> TLS_VERSION = createStringProperty("TLSVersion");

    /**
     * Proxy authentication of destination.
     */
    public static final DestinationPropertyKey<String> PROXY_AUTH = createStringProperty("ProxyAuthorization");

    /**
     * Proxy {@link URI} of destination.
     */
    public static final DestinationPropertyKey<URI> PROXY_URI = createUriProperty("Proxy");

    /**
     * Proxy host of destination.
     */
    public static final DestinationPropertyKey<String> PROXY_HOST = createStringProperty("ProxyHost");

    /**
     * Proxy port of destination.
     */
    public static final DestinationPropertyKey<Integer> PROXY_PORT =
        createProperty("ProxyPort", Integer.class, Integer::valueOf);

    /**
     * {@link ProxyType} of destination, e.g. Internet or On-Premise.
     */
    public static final DestinationPropertyKey<ProxyType> PROXY_TYPE =
        createProperty("ProxyType", ProxyType.class, ProxyType::ofIdentifierSensitive);

    /**
     * Key store location of destination.
     */
    public static final DestinationPropertyKey<String> KEY_STORE_LOCATION = createStringProperty("KeyStoreLocation");

    /**
     * Key store password of destination.
     */
    public static final DestinationPropertyKey<String> KEY_STORE_PASSWORD = createStringProperty("KeyStorePassword");

    /**
     * Trust store location of destination.
     */
    public static final DestinationPropertyKey<String> TRUST_STORE_LOCATION =
        createStringProperty("TrustStoreLocation");

    /**
     * Trust store password of destination.
     */
    public static final DestinationPropertyKey<String> TRUST_STORE_PASSWORD =
        createStringProperty("TrustStorePassword");

    /**
     * Value of TrustAll property of destination.
     */
    public static final DestinationPropertyKey<Boolean> TRUST_ALL =
        createProperty("TrustAll", Boolean.class, Boolean::valueOf);

    /**
     * Fallback of TrustAll value of destination. Denotes trusting of all certificates when communicating with
     * destination.
     */
    public static final DestinationPropertyKey<Boolean> TRUST_ALL_FALLBACK =
        createProperty("isTrustingAllCertificates", Boolean.class, Boolean::valueOf);

    /**
     * Principal Propagation Strategy of destination, in case of Proxy Type = On-Premise.
     */
    @Beta
    public static final DestinationPropertyKey<PrincipalPropagationMode> PRINCIPAL_PROPAGATION_MODE =
        createProperty(
            "cloudsdk.principalPropagationMode",
            PrincipalPropagationMode.class,
            PrincipalPropagationMode::ofIdentifier);

    /**
     * List of trusted certificates of destination used for authentication.
     */
    public static final DestinationPropertyKey<List<?>> CERTIFICATES = createListProperty("cloudsdk.certificates");

    /**
     * List of authentication tokens returned from destination service.
     */
    public static final DestinationPropertyKey<List<?>> AUTH_TOKENS = createListProperty("cloudsdk.authTokens");

    /**
     * Cloud Connector Location ID of destination to be used for connection to an On-Premise system.
     */
    public static final DestinationPropertyKey<String> CLOUD_CONNECTOR_LOCATION_ID =
        createStringProperty("CloudConnectorLocationId");

    /**
     * Value of ForwardAuthToken property of destination. Denotes forwarding of the authentication token provided to the
     * request execution to the destination target.
     */
    public static final DestinationPropertyKey<Boolean> FORWARD_AUTH_TOKEN =
        createProperty("forwardAuthToken", Boolean.class, Boolean::valueOf);

    /**
     * If the destination with authentication type "OAuth2SAMLBearerAssertion" contains this property this user will be
     * used by the destination service to retrieve a authTokens from the bound XSUAA. This means no user propagation is
     * needed.
     */
    public static final DestinationPropertyKey<String> SYSTEM_USER = createStringProperty("SystemUser");
    /**
     * SAP client for S/4HANA systems. Will be sent as header, if set.
     *
     * @since 4.16.0
     */
    public static final DestinationPropertyKey<String> SAP_CLIENT = createStringProperty("sap-client");
    /**
     * SAP language for S/4HANA systems. Will be sent as header if set, unless {@link #DYNAMIC_SAP_LANGUAGE} is set.
     *
     * @see #DYNAMIC_SAP_LANGUAGE
     * @since 4.16.0
     */
    public static final DestinationPropertyKey<String> SAP_LANGUAGE = createStringProperty("sap-language");
    /**
     * If set, the {@code sap-language} header to be sent will be derived from the current context via
     * {@link LocaleAccessor#getCurrentLocale()}. If set, this will take precedence over {@link #SAP_LANGUAGE}.
     *
     * @see LocaleAccessor#getCurrentLocale()
     * @since 4.16.0
     */
    public static final DestinationPropertyKey<Boolean> DYNAMIC_SAP_LANGUAGE =
        createProperty("cloudsdk.dynamicSapLanguage", Boolean.class, Boolean::valueOf);
    /**
     * Set of keys from the original property map at the creation of the destination
     */
    static final DestinationPropertyKey<Collection<String>> PROPERTIES_FOR_CHANGE_DETECTION =
        createCollectionProperty("cloudsdk.propertiesForChangeDetection");
    /**
     * If defined, indicates that the destination instance is specific to this tenant ID. An empty string represents the
     * provider tenant.
     */
    static final DestinationPropertyKey<String> TENANT_ID = createStringProperty("cloudsdk.tenantId");

    /**
     * The {@link SecurityConfigurationStrategy} to apply for the destination.
     *
     * @since 5.0.0
     */
    static final DestinationPropertyKey<SecurityConfigurationStrategy> SECURITY_CONFIGURATION =
        createProperty(
            "cloudsdk.securityConfiguration",
            SecurityConfigurationStrategy.class,
            SecurityConfigurationStrategy::ofIdentifierOrDefault);
}
