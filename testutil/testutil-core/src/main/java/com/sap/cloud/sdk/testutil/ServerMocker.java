package com.sap.cloud.sdk.testutil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sap.cloud.sdk.s4hana.serialization.SapClient;

interface ServerMocker
{
    /**
     * Mocks a destination and starts a mock server pointing all calls against the destination towards the mock server
     * instance.
     *
     * @param destinationName
     *            The name of the destination to be mocked.
     * @param relativePath
     *            An optional relative path to be appended to the ERP URI.
     * @param configuration
     *            An optional configuration to customize mock server runtime.
     *
     * @return The started mock server. <strong>Make sure to stop the server after your test.</strong>
     */
    @Nonnull
    WireMockRule mockServer(
        @Nonnull final String destinationName,
        @Nullable final String relativePath,
        @Nullable final WireMockConfiguration configuration );

    /**
     * Mocks a destination and starts a mock server pointing all calls against the destination towards the mock server
     * instance.
     *
     * @param destinationName
     *            The name of the destination to be mocked.
     * @param relativePath
     *            An optional relative path to be appended to the ERP URI.
     *
     * @return The started mock server. <strong>Make sure to stop the server after your test.</strong>
     */
    @Nonnull
    WireMockRule mockServer( @Nonnull final String destinationName, @Nullable final String relativePath );

    /**
     * Mocks a destination and starts a mock server pointing all calls against the destination towards the mock server
     * instance.
     *
     * @param destinationName
     *            The name of the destination to be mocked.
     *
     * @return The started mock server. <strong>Make sure to stop the server after your test.</strong>
     */
    @Nonnull
    WireMockRule mockServer( @Nonnull final String destinationName );

    /**
     * Mocks an ERP destination and starts a mock server pointing all calls against the ERP destination towards the mock
     * server instance.
     *
     * @param destinationName
     *            The name of the destination to be mocked.
     * @param sapClient
     *            The {@link SapClient} to be set as a destination property of the mocked destination. If {@code null},
     *            {@link SapClient#DEFAULT} is used.
     * @param relativePath
     *            An optional relative path to be appended to the ERP URI.
     * @param configuration
     *            An optional configuration to customize mock server runtime.
     *
     * @return The started mock server. <strong>Make sure to stop the server after your test.</strong>
     */
    @Nonnull
    WireMockRule mockErpServer(
        @Nonnull final String destinationName,
        @Nullable final SapClient sapClient,
        @Nullable final String relativePath,
        @Nullable final WireMockConfiguration configuration );

    /**
     * Mocks an ERP destination and starts a mock server pointing all calls against the ERP destination towards the mock
     * server instance.
     *
     * @param destinationName
     *            The name of the destination to be mocked.
     * @param sapClient
     *            The {@link SapClient} to be set as a destination property of the mocked destination. If {@code null},
     *            {@link SapClient#DEFAULT} is used.
     * @param relativePath
     *            An optional relative path to be appended to the ERP URI.
     *
     * @return The started mock server. <strong>Make sure to stop the server after your test.</strong>
     */
    @Nonnull
    WireMockRule mockErpServer(
        @Nonnull final String destinationName,
        @Nullable final SapClient sapClient,
        @Nullable final String relativePath );

    /**
     * Mocks an ERP destination and starts a mock server pointing all calls against the ERP destination towards the mock
     * server instance.
     *
     * @param destinationName
     *            The name of the destination to be mocked.
     * @param sapClient
     *            The {@link SapClient} to be set as a destination property of the mocked destination. If {@code null},
     *            {@link SapClient#DEFAULT} is used.
     *
     * @return The started mock server. <strong>Make sure to stop the server after your test.</strong>
     */
    @Nonnull
    WireMockRule mockErpServer( @Nonnull final String destinationName, @Nullable final SapClient sapClient );

    /**
     * Mocks an ERP destination and starts a mock server pointing all calls against the ERP destination towards the mock
     * server instance.
     *
     * @param destinationName
     *            The name of the destination to be mocked.
     *
     * @return The started mock server. <strong>Make sure to stop the server after your test.</strong>
     */
    @Nonnull
    WireMockRule mockErpServer( @Nonnull final String destinationName );
}
