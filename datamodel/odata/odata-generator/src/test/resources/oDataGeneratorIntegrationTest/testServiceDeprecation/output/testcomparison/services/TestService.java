/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.services;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchService;
import testcomparison.namespaces.test.ContinueFluentHelper;
import testcomparison.namespaces.test.CreateTestComplexFluentHelper;
import testcomparison.namespaces.test.MediaEntityFluentHelper;
import testcomparison.namespaces.test.TestEntityLvl2MultiLink;
import testcomparison.namespaces.test.TestEntityLvl2MultiLinkCreateFluentHelper;
import testcomparison.namespaces.test.TestEntityLvl2MultiLinkFluentHelper;
import testcomparison.namespaces.test.TestEntityLvl2MultiLinkUpdateFluentHelper;
import testcomparison.namespaces.test.TestEntityLvl2SingleLinkFluentHelper;
import testcomparison.namespaces.test.TestEntityMultiLink;
import testcomparison.namespaces.test.TestEntityMultiLinkCreateFluentHelper;
import testcomparison.namespaces.test.TestEntityMultiLinkFluentHelper;
import testcomparison.namespaces.test.TestEntityOtherMultiLink;
import testcomparison.namespaces.test.TestEntityOtherMultiLinkCreateFluentHelper;
import testcomparison.namespaces.test.TestEntityOtherMultiLinkFluentHelper;
import testcomparison.namespaces.test.TestEntitySingleLink;
import testcomparison.namespaces.test.TestEntitySingleLinkCreateFluentHelper;
import testcomparison.namespaces.test.TestEntitySingleLinkFluentHelper;
import testcomparison.namespaces.test.TestEntityV2;
import testcomparison.namespaces.test.TestEntityV2CreateFluentHelper;
import testcomparison.namespaces.test.TestEntityV2FluentHelper;
import testcomparison.namespaces.test.TestFunctionImportComplexReturnFluentHelper;
import testcomparison.namespaces.test.TestFunctionImportComplexReturnTypeCollectionFluentHelper;
import testcomparison.namespaces.test.TestFunctionImportEdmReturnFluentHelper;
import testcomparison.namespaces.test.TestFunctionImportEdmReturnTypeCollectionFluentHelper;
import testcomparison.namespaces.test.TestFunctionImportEntityReturnFluentHelper;
import testcomparison.namespaces.test.TestFunctionImportEntityReturnTypeCollectionFluentHelper;
import testcomparison.namespaces.test.TestFunctionImportGETFluentHelper;
import testcomparison.namespaces.test.TestFunctionImportMultipleParamsFluentHelper;
import testcomparison.namespaces.test.TestFunctionImportNoReturnFluentHelper;
import testcomparison.namespaces.test.TestFunctionImportPOSTFluentHelper;
import testcomparison.namespaces.test.UnrelatedFluentHelper;
import testcomparison.namespaces.test.batch.TestServiceBatch;


/**
 * <p>This service enables you to test OData specifications.
 * </p><p><a href='https://foo.bar/help.html'>Demo service</a></p><p>Reference: <a href='https://api.sap.com/shell/discover/contentpackage/SAPS4HANACloud/api/test_service?section=OVERVIEW'>SAP Business Accelerator Hub</a></p><h3>Details:</h3><table summary='Details'><tr><td align='right'>OData Service:</td><td>test_service</td></tr><tr><td align='right'>API Version:</td><td>1 </td></tr><tr><td align='right'>Minimum ERP Version:</td><td>1805</td></tr><tr><td align='right'>Foo:</td><td>Fizz, Buzz</td></tr><tr><td align='right'>Bar:</td><td>Bazz</td></tr></table>
 * 
 * @deprecated
 *     The service and all its related classes are deprecated as of release 9000 (AUG-1999). Please use the <a href="https://foo.bar/test/service_reloaded"> successor API</a> instead.
 */
@Deprecated
public interface TestService
    extends BatchService<TestServiceBatch>
{

    /**
     * If no other path was provided via the {@link #withServicePath(String)} method, this is the default service path used to access the endpoint.
     * 
     */
    String DEFAULT_SERVICE_PATH = "/test/service";

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
     * Fetch multiple {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entities.
     * 
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityMultiLinkFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    TestEntityMultiLinkFluentHelper getAllTestEntityMultiLink();

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
     * Fetch multiple {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entities.
     * 
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityOtherMultiLinkFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    TestEntityOtherMultiLinkFluentHelper getAllTestEntityOtherMultiLink();

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
     * Fetch multiple {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entities.
     * 
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityLvl2MultiLinkFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    TestEntityLvl2MultiLinkFluentHelper getAllTestEntityLvl2MultiLink();

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
     * Fetch multiple {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entities.
     * 
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.test.TestEntitySingleLinkFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    TestEntitySingleLinkFluentHelper getAllTestEntitySingleLink();

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
     * Fetch multiple {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entities.
     * 
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityLvl2SingleLinkFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    TestEntityLvl2SingleLinkFluentHelper getAllTestEntityLvl2SingleLink();

    /**
     * Fetch multiple {@link testcomparison.namespaces.test.MediaEntity MediaEntity} entities.
     * 
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.test.MediaEntity MediaEntity} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.test.MediaEntityFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    MediaEntityFluentHelper getAllMediaEntity();

    /**
     * Fetch multiple {@link testcomparison.namespaces.test.Unrelated Unrelated} entities.
     * 
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.test.Unrelated Unrelated} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.test.UnrelatedFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    UnrelatedFluentHelper getAllUnrelated();

    /**
     * <p>Creates a fluent helper for the <b>TestFunctionImportNoReturnType</b> OData function import.</p>
     * 
     * @return
     *     A fluent helper object that will execute the <b>TestFunctionImportNoReturnType</b> OData function import with the provided parameters. To perform execution, call the {@link testcomparison.namespaces.test.TestFunctionImportNoReturnFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestFunctionImportNoReturnFluentHelper testFunctionImportNoReturnType();

    /**
     * <p>Creates a fluent helper for the <b>TestFunctionImportEdmReturnType</b> OData function import.</p>
     * 
     * @return
     *     A fluent helper object that will execute the <b>TestFunctionImportEdmReturnType</b> OData function import with the provided parameters. To perform execution, call the {@link testcomparison.namespaces.test.TestFunctionImportEdmReturnFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestFunctionImportEdmReturnFluentHelper testFunctionImportEdmReturnType();

    /**
     * <p>Creates a fluent helper for the <b>TestFunctionImportEdmReturnTypeCollection</b> OData function import.</p>
     * 
     * @return
     *     A fluent helper object that will execute the <b>TestFunctionImportEdmReturnTypeCollection</b> OData function import with the provided parameters. To perform execution, call the {@link testcomparison.namespaces.test.TestFunctionImportEdmReturnTypeCollectionFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestFunctionImportEdmReturnTypeCollectionFluentHelper testFunctionImportEdmReturnTypeCollection();

    /**
     * <p>Creates a fluent helper for the <b>TestFunctionImportEntityReturnType</b> OData function import.</p>
     * 
     * @return
     *     A fluent helper object that will execute the <b>TestFunctionImportEntityReturnType</b> OData function import with the provided parameters. To perform execution, call the {@link testcomparison.namespaces.test.TestFunctionImportEntityReturnFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestFunctionImportEntityReturnFluentHelper testFunctionImportEntityReturnType();

    /**
     * <p>Creates a fluent helper for the <b>TestFunctionImportEntityReturnTypeCollection</b> OData function import.</p>
     * 
     * @return
     *     A fluent helper object that will execute the <b>TestFunctionImportEntityReturnTypeCollection</b> OData function import with the provided parameters. To perform execution, call the {@link testcomparison.namespaces.test.TestFunctionImportEntityReturnTypeCollectionFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestFunctionImportEntityReturnTypeCollectionFluentHelper testFunctionImportEntityReturnTypeCollection();

    /**
     * <p>Creates a fluent helper for the <b>TestFunctionImportComplexReturnType</b> OData function import.</p>
     * 
     * @return
     *     A fluent helper object that will execute the <b>TestFunctionImportComplexReturnType</b> OData function import with the provided parameters. To perform execution, call the {@link testcomparison.namespaces.test.TestFunctionImportComplexReturnFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestFunctionImportComplexReturnFluentHelper testFunctionImportComplexReturnType();

    /**
     * <p>Creates a fluent helper for the <b>TestFunctionImportComplexReturnTypeCollection</b> OData function import.</p>
     * 
     * @return
     *     A fluent helper object that will execute the <b>TestFunctionImportComplexReturnTypeCollection</b> OData function import with the provided parameters. To perform execution, call the {@link testcomparison.namespaces.test.TestFunctionImportComplexReturnTypeCollectionFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestFunctionImportComplexReturnTypeCollectionFluentHelper testFunctionImportComplexReturnTypeCollection();

    /**
     * <p>Creates a fluent helper for the <b>TestFunctionImportGET</b> OData function import.</p>
     * 
     * @param simpleParam
     *     Constraints: none<p>Original parameter name from the Odata EDM: <b>SimpleParam</b></p>
     * @return
     *     A fluent helper object that will execute the <b>TestFunctionImportGET</b> OData function import with the provided parameters. To perform execution, call the {@link testcomparison.namespaces.test.TestFunctionImportGETFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestFunctionImportGETFluentHelper testFunctionImportGET(
        @Nonnull
        final String simpleParam);

    /**
     * <p>Creates a fluent helper for the <b>TestFunctionImportPOST</b> OData function import.</p>
     * 
     * @param simpleParam
     *     Constraints: none<p>Original parameter name from the Odata EDM: <b>SimpleParam</b></p>
     * @return
     *     A fluent helper object that will execute the <b>TestFunctionImportPOST</b> OData function import with the provided parameters. To perform execution, call the {@link testcomparison.namespaces.test.TestFunctionImportPOSTFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestFunctionImportPOSTFluentHelper testFunctionImportPOST(
        @Nonnull
        final String simpleParam);

    /**
     * <p>Creates a fluent helper for the <b>TestFunctionImportMultipleParams</b> OData function import.</p>
     * 
     * @param stringParam
     *     Constraints: none<p>Original parameter name from the Odata EDM: <b>StringParam</b></p>
     * @param booleanParam
     *     Constraints: none<p>Original parameter name from the Odata EDM: <b>BooleanParam</b></p>
     * @return
     *     A fluent helper object that will execute the <b>TestFunctionImportMultipleParams</b> OData function import with the provided parameters. To perform execution, call the {@link testcomparison.namespaces.test.TestFunctionImportMultipleParamsFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    TestFunctionImportMultipleParamsFluentHelper testFunctionImportMultipleParams(
        @Nonnull
        final String stringParam,
        @Nonnull
        final Boolean booleanParam);

    /**
     * <p>Creates a fluent helper for the <b>CreateTestComplexType</b> OData function import.</p>
     * 
     * @return
     *     A fluent helper object that will execute the <b>CreateTestComplexType</b> OData function import with the provided parameters. To perform execution, call the {@link testcomparison.namespaces.test.CreateTestComplexFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    CreateTestComplexFluentHelper createTestComplexType();

    /**
     * <p>Creates a fluent helper for the <b>Continue</b> OData function import.</p>
     * 
     * @return
     *     A fluent helper object that will execute the <b>Continue</b> OData function import with the provided parameters. To perform execution, call the {@link testcomparison.namespaces.test.ContinueFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    ContinueFluentHelper continueFunction();

}
