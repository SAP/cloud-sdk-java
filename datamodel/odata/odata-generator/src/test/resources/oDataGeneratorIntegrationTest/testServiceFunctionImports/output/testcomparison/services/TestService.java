package testcomparison.services;

import java.util.UUID;
import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchService;
import testcomparison.namespaces.test.MediaEntity;
import testcomparison.namespaces.test.MediaEntityByKeyFluentHelper;
import testcomparison.namespaces.test.MediaEntityCreateFluentHelper;
import testcomparison.namespaces.test.MediaEntityDeleteFluentHelper;
import testcomparison.namespaces.test.MediaEntityFluentHelper;
import testcomparison.namespaces.test.MediaEntityUpdateFluentHelper;
import testcomparison.namespaces.test.TestEntityLvl2MultiLink;
import testcomparison.namespaces.test.TestEntityLvl2MultiLinkByKeyFluentHelper;
import testcomparison.namespaces.test.TestEntityLvl2MultiLinkCreateFluentHelper;
import testcomparison.namespaces.test.TestEntityLvl2MultiLinkDeleteFluentHelper;
import testcomparison.namespaces.test.TestEntityLvl2MultiLinkFluentHelper;
import testcomparison.namespaces.test.TestEntityLvl2MultiLinkUpdateFluentHelper;
import testcomparison.namespaces.test.TestEntityLvl2SingleLink;
import testcomparison.namespaces.test.TestEntityLvl2SingleLinkByKeyFluentHelper;
import testcomparison.namespaces.test.TestEntityLvl2SingleLinkCreateFluentHelper;
import testcomparison.namespaces.test.TestEntityLvl2SingleLinkDeleteFluentHelper;
import testcomparison.namespaces.test.TestEntityLvl2SingleLinkFluentHelper;
import testcomparison.namespaces.test.TestEntityLvl2SingleLinkUpdateFluentHelper;
import testcomparison.namespaces.test.TestEntityMultiLink;
import testcomparison.namespaces.test.TestEntityMultiLinkByKeyFluentHelper;
import testcomparison.namespaces.test.TestEntityMultiLinkCreateFluentHelper;
import testcomparison.namespaces.test.TestEntityMultiLinkDeleteFluentHelper;
import testcomparison.namespaces.test.TestEntityMultiLinkFluentHelper;
import testcomparison.namespaces.test.TestEntityMultiLinkUpdateFluentHelper;
import testcomparison.namespaces.test.TestEntityOtherMultiLink;
import testcomparison.namespaces.test.TestEntityOtherMultiLinkByKeyFluentHelper;
import testcomparison.namespaces.test.TestEntityOtherMultiLinkCreateFluentHelper;
import testcomparison.namespaces.test.TestEntityOtherMultiLinkDeleteFluentHelper;
import testcomparison.namespaces.test.TestEntityOtherMultiLinkFluentHelper;
import testcomparison.namespaces.test.TestEntityOtherMultiLinkUpdateFluentHelper;
import testcomparison.namespaces.test.TestEntitySingleLink;
import testcomparison.namespaces.test.TestEntitySingleLinkByKeyFluentHelper;
import testcomparison.namespaces.test.TestEntitySingleLinkCreateFluentHelper;
import testcomparison.namespaces.test.TestEntitySingleLinkDeleteFluentHelper;
import testcomparison.namespaces.test.TestEntitySingleLinkFluentHelper;
import testcomparison.namespaces.test.TestEntitySingleLinkUpdateFluentHelper;
import testcomparison.namespaces.test.TestEntityV2;
import testcomparison.namespaces.test.TestEntityV2ByKeyFluentHelper;
import testcomparison.namespaces.test.TestEntityV2CreateFluentHelper;
import testcomparison.namespaces.test.TestEntityV2DeleteFluentHelper;
import testcomparison.namespaces.test.TestEntityV2FluentHelper;
import testcomparison.namespaces.test.TestEntityV2UpdateFluentHelper;
import testcomparison.namespaces.test.TestFunctionImportNoReturnFluentHelper;
import testcomparison.namespaces.test.Unrelated;
import testcomparison.namespaces.test.UnrelatedByKeyFluentHelper;
import testcomparison.namespaces.test.UnrelatedCreateFluentHelper;
import testcomparison.namespaces.test.UnrelatedDeleteFluentHelper;
import testcomparison.namespaces.test.UnrelatedFluentHelper;
import testcomparison.namespaces.test.UnrelatedUpdateFluentHelper;
import testcomparison.namespaces.test.batch.TestServiceBatch;


/**
 * <p>Reference: <a href='https://api.sap.com/shell/discover/contentpackage/SAPS4HANACloud/api/test_service?section=OVERVIEW'>SAP Business Accelerator Hub</a></p><h3>Details:</h3><table summary='Details'><tr><td align='right'>OData Service:</td><td>test_service</td></tr></table>
 *
 */
public interface TestService
    extends BatchService<TestServiceBatch>
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
    TestService withServicePath(
        @Nonnull
        final String servicePath);

    /**
     * Fetch multiple {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entities.
     *
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityV2FluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestEntityV2FluentHelper getAllTestEntityV2();

    /**
     * Fetch a single {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity using key fields.
     *
     * @param keyPropertyString
     *
     * @param keyPropertyGuid
     *
     * @return
     *     A fluent helper to fetch a single {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityV2ByKeyFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestEntityV2ByKeyFluentHelper getTestEntityV2ByKey(final UUID keyPropertyGuid, final String keyPropertyString);

    /**
     * Create a new {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity and save it to the S/4HANA system.
     *
     * @param testEntityV2
     *     {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity object that will be created in the S/4HANA system.
     * @return
     *     A fluent helper to create a new {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityV2CreateFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestEntityV2CreateFluentHelper createTestEntityV2(
        @Nonnull
        final TestEntityV2 testEntityV2);

    /**
     * Update an existing {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity and save it to the S/4HANA system.
     *
     * @param testEntityV2
     *     {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity object that will be updated in the S/4HANA system.
     * @return
     *     A fluent helper to update an existing {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityV2UpdateFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestEntityV2UpdateFluentHelper updateTestEntityV2(
        @Nonnull
        final TestEntityV2 testEntityV2);

    /**
     * Deletes an existing {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity in the S/4HANA system.
     *
     * @param testEntityV2
     *     {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A fluent helper to delete an existing {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityV2DeleteFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestEntityV2DeleteFluentHelper deleteTestEntityV2(
        @Nonnull
        final TestEntityV2 testEntityV2);

    /**
     * Fetch multiple {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entities.
     *
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityMultiLinkFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestEntityMultiLinkFluentHelper getAllTestEntityMultiLink();

    /**
     * Fetch a single {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entity using key fields.
     *
     * @param keyProperty
     *
     * @return
     *     A fluent helper to fetch a single {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityMultiLinkByKeyFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestEntityMultiLinkByKeyFluentHelper getTestEntityMultiLinkByKey(final String keyProperty);

    /**
     * Create a new {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entity and save it to the S/4HANA system.
     *
     * @param testEntityMultiLink
     *     {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entity object that will be created in the S/4HANA system.
     * @return
     *     A fluent helper to create a new {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entity. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityMultiLinkCreateFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestEntityMultiLinkCreateFluentHelper createTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink);

    /**
     * Update an existing {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entity and save it to the S/4HANA system.
     *
     * @param testEntityMultiLink
     *     {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entity object that will be updated in the S/4HANA system.
     * @return
     *     A fluent helper to update an existing {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entity. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityMultiLinkUpdateFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestEntityMultiLinkUpdateFluentHelper updateTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink);

    /**
     * Deletes an existing {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entity in the S/4HANA system.
     *
     * @param testEntityMultiLink
     *     {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A fluent helper to delete an existing {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entity. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityMultiLinkDeleteFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestEntityMultiLinkDeleteFluentHelper deleteTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink);

    /**
     * Fetch multiple {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entities.
     *
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityOtherMultiLinkFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestEntityOtherMultiLinkFluentHelper getAllTestEntityOtherMultiLink();

    /**
     * Fetch a single {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity using key fields.
     *
     * @param keyProperty
     *
     * @return
     *     A fluent helper to fetch a single {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityOtherMultiLinkByKeyFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestEntityOtherMultiLinkByKeyFluentHelper getTestEntityOtherMultiLinkByKey(final String keyProperty);

    /**
     * Create a new {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity and save it to the S/4HANA system.
     *
     * @param testEntityOtherMultiLink
     *     {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity object that will be created in the S/4HANA system.
     * @return
     *     A fluent helper to create a new {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityOtherMultiLinkCreateFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestEntityOtherMultiLinkCreateFluentHelper createTestEntityOtherMultiLink(
        @Nonnull
        final TestEntityOtherMultiLink testEntityOtherMultiLink);

    /**
     * Update an existing {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity and save it to the S/4HANA system.
     *
     * @param testEntityOtherMultiLink
     *     {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity object that will be updated in the S/4HANA system.
     * @return
     *     A fluent helper to update an existing {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityOtherMultiLinkUpdateFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestEntityOtherMultiLinkUpdateFluentHelper updateTestEntityOtherMultiLink(
        @Nonnull
        final TestEntityOtherMultiLink testEntityOtherMultiLink);

    /**
     * Deletes an existing {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity in the S/4HANA system.
     *
     * @param testEntityOtherMultiLink
     *     {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A fluent helper to delete an existing {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityOtherMultiLinkDeleteFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestEntityOtherMultiLinkDeleteFluentHelper deleteTestEntityOtherMultiLink(
        @Nonnull
        final TestEntityOtherMultiLink testEntityOtherMultiLink);

    /**
     * Fetch multiple {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entities.
     *
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityLvl2MultiLinkFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestEntityLvl2MultiLinkFluentHelper getAllTestEntityLvl2MultiLink();

    /**
     * Fetch a single {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity using key fields.
     *
     * @param keyProperty
     *
     * @return
     *     A fluent helper to fetch a single {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityLvl2MultiLinkByKeyFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestEntityLvl2MultiLinkByKeyFluentHelper getTestEntityLvl2MultiLinkByKey(final String keyProperty);

    /**
     * Create a new {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity and save it to the S/4HANA system.
     *
     * @param testEntityLvl2MultiLink
     *     {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity object that will be created in the S/4HANA system.
     * @return
     *     A fluent helper to create a new {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityLvl2MultiLinkCreateFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestEntityLvl2MultiLinkCreateFluentHelper createTestEntityLvl2MultiLink(
        @Nonnull
        final TestEntityLvl2MultiLink testEntityLvl2MultiLink);

    /**
     * Update an existing {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity and save it to the S/4HANA system.
     *
     * @param testEntityLvl2MultiLink
     *     {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity object that will be updated in the S/4HANA system.
     * @return
     *     A fluent helper to update an existing {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityLvl2MultiLinkUpdateFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestEntityLvl2MultiLinkUpdateFluentHelper updateTestEntityLvl2MultiLink(
        @Nonnull
        final TestEntityLvl2MultiLink testEntityLvl2MultiLink);

    /**
     * Deletes an existing {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity in the S/4HANA system.
     *
     * @param testEntityLvl2MultiLink
     *     {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A fluent helper to delete an existing {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityLvl2MultiLinkDeleteFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestEntityLvl2MultiLinkDeleteFluentHelper deleteTestEntityLvl2MultiLink(
        @Nonnull
        final TestEntityLvl2MultiLink testEntityLvl2MultiLink);

    /**
     * Fetch multiple {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entities.
     *
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.test.TestEntitySingleLinkFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestEntitySingleLinkFluentHelper getAllTestEntitySingleLink();

    /**
     * Fetch a single {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entity using key fields.
     *
     * @param keyProperty
     *
     * @return
     *     A fluent helper to fetch a single {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.test.TestEntitySingleLinkByKeyFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestEntitySingleLinkByKeyFluentHelper getTestEntitySingleLinkByKey(final String keyProperty);

    /**
     * Create a new {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entity and save it to the S/4HANA system.
     *
     * @param testEntitySingleLink
     *     {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entity object that will be created in the S/4HANA system.
     * @return
     *     A fluent helper to create a new {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entity. To perform execution, call the {@link testcomparison.namespaces.test.TestEntitySingleLinkCreateFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestEntitySingleLinkCreateFluentHelper createTestEntitySingleLink(
        @Nonnull
        final TestEntitySingleLink testEntitySingleLink);

    /**
     * Update an existing {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entity and save it to the S/4HANA system.
     *
     * @param testEntitySingleLink
     *     {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entity object that will be updated in the S/4HANA system.
     * @return
     *     A fluent helper to update an existing {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entity. To perform execution, call the {@link testcomparison.namespaces.test.TestEntitySingleLinkUpdateFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestEntitySingleLinkUpdateFluentHelper updateTestEntitySingleLink(
        @Nonnull
        final TestEntitySingleLink testEntitySingleLink);

    /**
     * Deletes an existing {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entity in the S/4HANA system.
     *
     * @param testEntitySingleLink
     *     {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A fluent helper to delete an existing {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entity. To perform execution, call the {@link testcomparison.namespaces.test.TestEntitySingleLinkDeleteFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestEntitySingleLinkDeleteFluentHelper deleteTestEntitySingleLink(
        @Nonnull
        final TestEntitySingleLink testEntitySingleLink);

    /**
     * Fetch multiple {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entities.
     *
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityLvl2SingleLinkFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestEntityLvl2SingleLinkFluentHelper getAllTestEntityLvl2SingleLink();

    /**
     * Fetch a single {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity using key fields.
     *
     * @param keyProperty
     *
     * @return
     *     A fluent helper to fetch a single {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityLvl2SingleLinkByKeyFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestEntityLvl2SingleLinkByKeyFluentHelper getTestEntityLvl2SingleLinkByKey(final String keyProperty);

    /**
     * Create a new {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity and save it to the S/4HANA system.
     *
     * @param testEntityLvl2SingleLink
     *     {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity object that will be created in the S/4HANA system.
     * @return
     *     A fluent helper to create a new {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityLvl2SingleLinkCreateFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestEntityLvl2SingleLinkCreateFluentHelper createTestEntityLvl2SingleLink(
        @Nonnull
        final TestEntityLvl2SingleLink testEntityLvl2SingleLink);

    /**
     * Update an existing {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity and save it to the S/4HANA system.
     *
     * @param testEntityLvl2SingleLink
     *     {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity object that will be updated in the S/4HANA system.
     * @return
     *     A fluent helper to update an existing {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityLvl2SingleLinkUpdateFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestEntityLvl2SingleLinkUpdateFluentHelper updateTestEntityLvl2SingleLink(
        @Nonnull
        final TestEntityLvl2SingleLink testEntityLvl2SingleLink);

    /**
     * Deletes an existing {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity in the S/4HANA system.
     *
     * @param testEntityLvl2SingleLink
     *     {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A fluent helper to delete an existing {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityLvl2SingleLinkDeleteFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestEntityLvl2SingleLinkDeleteFluentHelper deleteTestEntityLvl2SingleLink(
        @Nonnull
        final TestEntityLvl2SingleLink testEntityLvl2SingleLink);

    /**
     * Fetch multiple {@link testcomparison.namespaces.test.MediaEntity MediaEntity} entities.
     *
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.test.MediaEntity MediaEntity} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.test.MediaEntityFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    MediaEntityFluentHelper getAllMediaEntity();

    /**
     * Fetch a single {@link testcomparison.namespaces.test.MediaEntity MediaEntity} entity using key fields.
     *
     * @param keyProperty
     *
     * @return
     *     A fluent helper to fetch a single {@link testcomparison.namespaces.test.MediaEntity MediaEntity} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.test.MediaEntityByKeyFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    MediaEntityByKeyFluentHelper getMediaEntityByKey(final String keyProperty);

    /**
     * Create a new {@link testcomparison.namespaces.test.MediaEntity MediaEntity} entity and save it to the S/4HANA system.
     *
     * @param mediaEntity
     *     {@link testcomparison.namespaces.test.MediaEntity MediaEntity} entity object that will be created in the S/4HANA system.
     * @return
     *     A fluent helper to create a new {@link testcomparison.namespaces.test.MediaEntity MediaEntity} entity. To perform execution, call the {@link testcomparison.namespaces.test.MediaEntityCreateFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    MediaEntityCreateFluentHelper createMediaEntity(
        @Nonnull
        final MediaEntity mediaEntity);

    /**
     * Update an existing {@link testcomparison.namespaces.test.MediaEntity MediaEntity} entity and save it to the S/4HANA system.
     *
     * @param mediaEntity
     *     {@link testcomparison.namespaces.test.MediaEntity MediaEntity} entity object that will be updated in the S/4HANA system.
     * @return
     *     A fluent helper to update an existing {@link testcomparison.namespaces.test.MediaEntity MediaEntity} entity. To perform execution, call the {@link testcomparison.namespaces.test.MediaEntityUpdateFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    MediaEntityUpdateFluentHelper updateMediaEntity(
        @Nonnull
        final MediaEntity mediaEntity);

    /**
     * Deletes an existing {@link testcomparison.namespaces.test.MediaEntity MediaEntity} entity in the S/4HANA system.
     *
     * @param mediaEntity
     *     {@link testcomparison.namespaces.test.MediaEntity MediaEntity} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A fluent helper to delete an existing {@link testcomparison.namespaces.test.MediaEntity MediaEntity} entity. To perform execution, call the {@link testcomparison.namespaces.test.MediaEntityDeleteFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    MediaEntityDeleteFluentHelper deleteMediaEntity(
        @Nonnull
        final MediaEntity mediaEntity);

    /**
     * Fetch multiple {@link testcomparison.namespaces.test.Unrelated Unrelated} entities.
     *
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.test.Unrelated Unrelated} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.test.UnrelatedFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    UnrelatedFluentHelper getAllUnrelated();

    /**
     * Fetch a single {@link testcomparison.namespaces.test.Unrelated Unrelated} entity using key fields.
     *
     * @param keyProperty
     *
     * @return
     *     A fluent helper to fetch a single {@link testcomparison.namespaces.test.Unrelated Unrelated} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.test.UnrelatedByKeyFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    UnrelatedByKeyFluentHelper getUnrelatedByKey(final String keyProperty);

    /**
     * Create a new {@link testcomparison.namespaces.test.Unrelated Unrelated} entity and save it to the S/4HANA system.
     *
     * @param unrelated
     *     {@link testcomparison.namespaces.test.Unrelated Unrelated} entity object that will be created in the S/4HANA system.
     * @return
     *     A fluent helper to create a new {@link testcomparison.namespaces.test.Unrelated Unrelated} entity. To perform execution, call the {@link testcomparison.namespaces.test.UnrelatedCreateFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    UnrelatedCreateFluentHelper createUnrelated(
        @Nonnull
        final Unrelated unrelated);

    /**
     * Update an existing {@link testcomparison.namespaces.test.Unrelated Unrelated} entity and save it to the S/4HANA system.
     *
     * @param unrelated
     *     {@link testcomparison.namespaces.test.Unrelated Unrelated} entity object that will be updated in the S/4HANA system.
     * @return
     *     A fluent helper to update an existing {@link testcomparison.namespaces.test.Unrelated Unrelated} entity. To perform execution, call the {@link testcomparison.namespaces.test.UnrelatedUpdateFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    UnrelatedUpdateFluentHelper updateUnrelated(
        @Nonnull
        final Unrelated unrelated);

    /**
     * Deletes an existing {@link testcomparison.namespaces.test.Unrelated Unrelated} entity in the S/4HANA system.
     *
     * @param unrelated
     *     {@link testcomparison.namespaces.test.Unrelated Unrelated} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A fluent helper to delete an existing {@link testcomparison.namespaces.test.Unrelated Unrelated} entity. To perform execution, call the {@link testcomparison.namespaces.test.UnrelatedDeleteFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    UnrelatedDeleteFluentHelper deleteUnrelated(
        @Nonnull
        final Unrelated unrelated);

    /**
     * <p>Creates a fluent helper for the <b>TestFunctionImportNoReturnType</b> OData function import.</p>
     *
     * @return
     *     A fluent helper object that will execute the <b>TestFunctionImportNoReturnType</b> OData function import with the provided parameters. To perform execution, call the {@link testcomparison.namespaces.test.TestFunctionImportNoReturnFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestFunctionImportNoReturnFluentHelper testFunctionImportNoReturnType();

}
