package com.sap.cloud.sdk.testutil;

import java.net.URI;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.connectivity.ProxyConfiguration;
import com.sap.cloud.sdk.s4hana.serialization.SapClient;

import io.vavr.control.Option;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * ERP System.
 */
@Data
public class ErpSystem implements TestSystem<ErpSystem>
{
    @Nonnull
    private final String alias;

    @Nonnull
    private final URI uri;

    @Nonnull
    private final String systemId;

    @Nonnull
    private final SapClient sapClient;

    @Nonnull
    private final Locale locale;

    @Nullable
    private final ProxyConfiguration proxyConfiguration;

    @Nonnull
    private final String applicationServer;

    @Nonnull
    private final String instanceNumber;

    /**
     * Constructs a new ERP system without a proxy using
     * <ul>
     * <li>the system {@code alias = systemId + "_" + sapClient}</li>
     * <li>locale {@link Locale#ENGLISH},</li>
     * <li>an application server host equal to the host of parameter {@code uri},</li>
     * <li>and instance number {@code "00"}.</li>
     * </ul>
     *
     * Delegates to {@link #ErpSystem(String, URI, String, SapClient)}.
     *
     * @param uri
     *            The URI of the ERP.
     * @param systemId
     *            The system identifier of the ERP.
     * @param sapClient
     *            The {@link SapClient} to be used.
     */
    public ErpSystem( @Nonnull final URI uri, @Nullable final String systemId, @Nullable final SapClient sapClient )
    {
        this(systemId + "_" + sapClient, uri, systemId, sapClient);
    }

    /**
     * Constructs a new ERP system without a proxy using
     * <ul>
     * <li>locale {@link Locale#ENGLISH},</li>
     * <li>an application server host equal to the host of parameter {@code uri},</li>
     * <li>and instance number {@code "00"}.</li>
     * </ul>
     *
     * Delegates to {@link #ErpSystem(String, URI, String, SapClient, Locale, ProxyConfiguration, String, String)}.
     *
     * @param alias
     *            The alias of the ERP.
     * @param uri
     *            The URI of the ERP.
     * @param systemId
     *            The system identifier of the ERP.
     * @param sapClient
     *            The {@link SapClient} to be used.
     */
    public ErpSystem(
        @Nonnull final String alias,
        @Nonnull final URI uri,
        @Nullable final String systemId,
        @Nullable final SapClient sapClient )
    {
        this(alias, uri, systemId, sapClient, null, null, null, null);
    }

    /**
     * Constructs a new ERP system using
     * <ul>
     * <li>locale {@link Locale#ENGLISH},</li>
     * <li>an application server host equal to the host of parameter {@code uri},</li>
     * <li>and instance number {@code "00"}.</li>
     * </ul>
     *
     * Delegates to {@link #ErpSystem(String, URI, String, SapClient, Locale, ProxyConfiguration, String, String)}.
     *
     * @param alias
     *            The alias of the ERP.
     * @param uri
     *            The URI of the ERP.
     * @param systemId
     *            The system identifier of the ERP.
     * @param sapClient
     *            The {@link SapClient} to be used.
     * @param proxyConfiguration
     *            The {@link ProxyConfiguration} to be used as proxy for connecting to the ERP. If {@code null}, no
     *            proxy is required.
     */
    public ErpSystem(
        @Nonnull final String alias,
        @Nonnull final URI uri,
        @Nullable final String systemId,
        @Nullable final SapClient sapClient,
        @Nullable final ProxyConfiguration proxyConfiguration )
    {
        this(alias, uri, systemId, sapClient, null, proxyConfiguration, null, null);
    }

    /**
     * Constructs a new ERP system.
     *
     * @param alias
     *            The alias of the ERP.
     * @param uri
     *            The URI of the ERP.
     * @param systemId
     *            The system identifier of the ERP. If {@code null}, an empty String is used.
     * @param sapClient
     *            The {@link SapClient} to be used. If {@code null}, {@link SapClient#DEFAULT} is used.
     * @param locale
     *            The {@link Locale} to be used. If {@code null}, {@link Locale#US} is used.
     * @param proxyConfiguration
     *            The {@link ProxyConfiguration} to be used as proxy for connecting to the ERP. If {@code null}, no
     *            proxy is required.
     * @param applicationServer
     *            The application server host of the ERP. If {@code null}, the host of the {@code uri} parameter is
     *            used.
     * @param instanceNumber
     *            The instance number of the ERP. If {@code null}, {@code "00"} is used.
     */
    public ErpSystem(
        @Nonnull final String alias,
        @Nonnull final URI uri,
        @Nullable final String systemId,
        @Nullable final SapClient sapClient,
        @Nullable final Locale locale,
        @Nullable final ProxyConfiguration proxyConfiguration,
        @Nullable final String applicationServer,
        @Nullable final String instanceNumber )
    {
        this.alias = alias;
        this.uri = uri;

        this.systemId = systemId != null ? systemId : "";
        this.sapClient = sapClient != null ? sapClient : SapClient.DEFAULT;

        this.locale = locale != null ? locale : Locale.US;
        this.proxyConfiguration = proxyConfiguration;
        this.applicationServer = applicationServer != null ? applicationServer : uri.getHost();
        this.instanceNumber = instanceNumber != null ? instanceNumber : "00";
    }

    @Nonnull
    @Override
    public Class<ErpSystem> getType()
    {
        return ErpSystem.class;
    }

    @Nonnull
    @Override
    public Option<ProxyConfiguration> getProxyConfiguration()
    {
        return Option.of(proxyConfiguration);
    }

    /**
     * The ERP system builder.
     */
    @Accessors( fluent = true )
    @Setter
    public static class ErpSystemBuilder
    {
        private String alias;
        private URI uri;
        private String systemId;
        private SapClient sapClient;
        private Locale locale;
        private ProxyConfiguration proxyConfiguration;
        private String applicationServer;
        private String instanceNumber;

        ErpSystemBuilder()
        {
        }

        /**
         * Create a ERP system instance.
         *
         * @return The newly build ERP system instance.
         */
        @Nonnull
        public ErpSystem build()
        {
            return new ErpSystem(
                alias,
                uri,
                systemId,
                sapClient,
                locale,
                proxyConfiguration,
                applicationServer,
                instanceNumber);
        }

        /**
         * Serialize the builder to String.
         *
         * @return The String representation.
         */
        @Nonnull
        public String toString()
        {
            return "ErpSystem.ErpSystemBuilder(alias="
                + alias
                + ", uri="
                + uri
                + ", systemId="
                + systemId
                + ", sapClient="
                + sapClient
                + ", locale="
                + locale
                + ", proxyConfiguration="
                + proxyConfiguration
                + ", applicationServer="
                + applicationServer
                + ", instanceNumber="
                + instanceNumber
                + ")";
        }
    }

    /**
     * Creates a new builder without initializing mandatory parameters.
     *
     * @return The new builder without mandatory parameters.
     */
    @Nonnull
    public static ErpSystemBuilder builder()
    {
        return new ErpSystemBuilder();
    }

    /**
     * Creates a new builder for the given mandatory parameters.
     *
     * @param alias
     *            ERP System alias.
     * @param uri
     *            ERP system URI.
     * @return The builder object.
     */
    @Nonnull
    public static ErpSystemBuilder builder( @Nonnull final String alias, @Nonnull final URI uri )
    {
        return new ErpSystemBuilder().alias(alias).uri(uri);
    }
}
