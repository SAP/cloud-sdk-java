/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.services;

import java.util.UUID;
import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchService;
import testcomparison.namespaces.nameclash.TestEntityMultiLink;
import testcomparison.namespaces.nameclash.TestEntityMultiLinkByKeyFluentHelper;
import testcomparison.namespaces.nameclash.TestEntityMultiLinkCreateFluentHelper;
import testcomparison.namespaces.nameclash.TestEntityMultiLinkDeleteFluentHelper;
import testcomparison.namespaces.nameclash.TestEntityMultiLinkFluentHelper;
import testcomparison.namespaces.nameclash.TestEntityMultiLinkUpdateFluentHelper;
import testcomparison.namespaces.nameclash.TestEntityV2;
import testcomparison.namespaces.nameclash.TestEntityV2ByKeyFluentHelper;
import testcomparison.namespaces.nameclash.TestEntityV2CreateFluentHelper;
import testcomparison.namespaces.nameclash.TestEntityV2DeleteFluentHelper;
import testcomparison.namespaces.nameclash.TestEntityV2FluentHelper;
import testcomparison.namespaces.nameclash.TestEntityV2UpdateFluentHelper;
import testcomparison.namespaces.nameclash.batch.NameClashServiceBatch;


/**
 * <h3>Details:</h3><table summary='Details'><tr><td align='right'>OData Service:</td><td>name-clash</td></tr></table>
 * 
 */
public interface NameClashService
    extends BatchService<NameClashServiceBatch>
{

    /**
     * If no other path was provided via the {@link #withServicePath(String)} method, this is the default service path used to access the endpoint.
     * 
     */
    String DEFAULT_SERVICE_PATH = "/sap/opu/odata/sap/API_TEST_SRV";

    /**
     * Overrides the default service path and returns a new service instance with the specified service path. Also adjusts the respective entity URLs.
     * 
     * @param servicePath
     *     Service path that will override the default.
     * @return
     *     A new service instance with the specified service path.
     */
    @Nonnull
    NameClashService withServicePath(
        @Nonnull
        final String servicePath);

    /**
     * Fetch multiple {@link testcomparison.namespaces.nameclash.TestEntityV2 TestEntityV2} entities.
     * 
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.nameclash.TestEntityV2 TestEntityV2} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.nameclash.TestEntityV2FluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    TestEntityV2FluentHelper getAllTestEntity();

    /**
     * Fetch a single {@link testcomparison.namespaces.nameclash.TestEntityV2 TestEntityV2} entity using key fields.
     * 
     * @param keyPropertyGuid
     *     
     * @return
     *     A fluent helper to fetch a single {@link testcomparison.namespaces.nameclash.TestEntityV2 TestEntityV2} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.nameclash.TestEntityV2ByKeyFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    TestEntityV2ByKeyFluentHelper getTestEntityByKey(final UUID keyPropertyGuid);

    /**
     * Create a new {@link testcomparison.namespaces.nameclash.TestEntityV2 TestEntityV2} entity and save it to the S/4HANA system.
     * 
     * @param testEntityV2
     *     {@link testcomparison.namespaces.nameclash.TestEntityV2 TestEntityV2} entity object that will be created in the S/4HANA system.
     * @return
     *     A fluent helper to create a new {@link testcomparison.namespaces.nameclash.TestEntityV2 TestEntityV2} entity. To perform execution, call the {@link testcomparison.namespaces.nameclash.TestEntityV2CreateFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    TestEntityV2CreateFluentHelper createTestEntity(
        @Nonnull
        final TestEntityV2 testEntityV2);

    /**
     * Update an existing {@link testcomparison.namespaces.nameclash.TestEntityV2 TestEntityV2} entity and save it to the S/4HANA system.
     * 
     * @param testEntityV2
     *     {@link testcomparison.namespaces.nameclash.TestEntityV2 TestEntityV2} entity object that will be updated in the S/4HANA system.
     * @return
     *     A fluent helper to update an existing {@link testcomparison.namespaces.nameclash.TestEntityV2 TestEntityV2} entity. To perform execution, call the {@link testcomparison.namespaces.nameclash.TestEntityV2UpdateFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    TestEntityV2UpdateFluentHelper updateTestEntity(
        @Nonnull
        final TestEntityV2 testEntityV2);

    /**
     * Deletes an existing {@link testcomparison.namespaces.nameclash.TestEntityV2 TestEntityV2} entity in the S/4HANA system.
     * 
     * @param testEntityV2
     *     {@link testcomparison.namespaces.nameclash.TestEntityV2 TestEntityV2} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A fluent helper to delete an existing {@link testcomparison.namespaces.nameclash.TestEntityV2 TestEntityV2} entity. To perform execution, call the {@link testcomparison.namespaces.nameclash.TestEntityV2DeleteFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    TestEntityV2DeleteFluentHelper deleteTestEntity(
        @Nonnull
        final TestEntityV2 testEntityV2);

    /**
     * Fetch multiple {@link testcomparison.namespaces.nameclash.TestEntityMultiLink TestEntityMultiLink} entities.
     * 
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.nameclash.TestEntityMultiLink TestEntityMultiLink} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.nameclash.TestEntityMultiLinkFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    TestEntityMultiLinkFluentHelper getAllTestEntityMultiLink();

    /**
     * Fetch a single {@link testcomparison.namespaces.nameclash.TestEntityMultiLink TestEntityMultiLink} entity using key fields.
     * 
     * @param keyProperty
     *     
     * @return
     *     A fluent helper to fetch a single {@link testcomparison.namespaces.nameclash.TestEntityMultiLink TestEntityMultiLink} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.nameclash.TestEntityMultiLinkByKeyFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    TestEntityMultiLinkByKeyFluentHelper getTestEntityMultiLinkByKey(final String keyProperty);

    /**
     * Create a new {@link testcomparison.namespaces.nameclash.TestEntityMultiLink TestEntityMultiLink} entity and save it to the S/4HANA system.
     * 
     * @param testEntityMultiLink
     *     {@link testcomparison.namespaces.nameclash.TestEntityMultiLink TestEntityMultiLink} entity object that will be created in the S/4HANA system.
     * @return
     *     A fluent helper to create a new {@link testcomparison.namespaces.nameclash.TestEntityMultiLink TestEntityMultiLink} entity. To perform execution, call the {@link testcomparison.namespaces.nameclash.TestEntityMultiLinkCreateFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    TestEntityMultiLinkCreateFluentHelper createTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink);

    /**
     * Update an existing {@link testcomparison.namespaces.nameclash.TestEntityMultiLink TestEntityMultiLink} entity and save it to the S/4HANA system.
     * 
     * @param testEntityMultiLink
     *     {@link testcomparison.namespaces.nameclash.TestEntityMultiLink TestEntityMultiLink} entity object that will be updated in the S/4HANA system.
     * @return
     *     A fluent helper to update an existing {@link testcomparison.namespaces.nameclash.TestEntityMultiLink TestEntityMultiLink} entity. To perform execution, call the {@link testcomparison.namespaces.nameclash.TestEntityMultiLinkUpdateFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    TestEntityMultiLinkUpdateFluentHelper updateTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink);

    /**
     * Deletes an existing {@link testcomparison.namespaces.nameclash.TestEntityMultiLink TestEntityMultiLink} entity in the S/4HANA system.
     * 
     * @param testEntityMultiLink
     *     {@link testcomparison.namespaces.nameclash.TestEntityMultiLink TestEntityMultiLink} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A fluent helper to delete an existing {@link testcomparison.namespaces.nameclash.TestEntityMultiLink TestEntityMultiLink} entity. To perform execution, call the {@link testcomparison.namespaces.nameclash.TestEntityMultiLinkDeleteFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    TestEntityMultiLinkDeleteFluentHelper deleteTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink);

}
