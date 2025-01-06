/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.services;

import java.util.UUID;
import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchService;
import testcomparison.namespaces.test.ContinueFluentHelper;
import testcomparison.namespaces.test.CreateTestComplexFluentHelper;
import testcomparison.namespaces.test.TestEntityV2;
import testcomparison.namespaces.test.TestEntityV2ByKeyFluentHelper;
import testcomparison.namespaces.test.TestEntityV2CreateFluentHelper;
import testcomparison.namespaces.test.TestEntityV2DeleteFluentHelper;
import testcomparison.namespaces.test.TestEntityV2FluentHelper;
import testcomparison.namespaces.test.TestEntityV2UpdateFluentHelper;
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
    TestEntityV2FluentHelper getAllTestEntity();

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
    TestEntityV2ByKeyFluentHelper getTestEntityByKey(final UUID keyPropertyGuid, final String keyPropertyString);

    /**
     * Create a new {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity and save it to the S/4HANA system.
     * 
     * @param testEntityV2
     *     {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity object that will be created in the S/4HANA system.
     * @return
     *     A fluent helper to create a new {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityV2CreateFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    TestEntityV2CreateFluentHelper createTestEntity(
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
    TestEntityV2UpdateFluentHelper updateTestEntity(
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
    TestEntityV2DeleteFluentHelper deleteTestEntity(
        @Nonnull
        final TestEntityV2 testEntityV2);

    /**
     * Fetch multiple {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entities.
     * 
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityV2FluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    TestEntityV2FluentHelper getAllOtherTestEntity();

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
    TestEntityV2ByKeyFluentHelper getOtherTestEntityByKey(final UUID keyPropertyGuid, final String keyPropertyString);

    /**
     * Create a new {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity and save it to the S/4HANA system.
     * 
     * @param testEntityV2
     *     {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity object that will be created in the S/4HANA system.
     * @return
     *     A fluent helper to create a new {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity. To perform execution, call the {@link testcomparison.namespaces.test.TestEntityV2CreateFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    TestEntityV2CreateFluentHelper createOtherTestEntity(
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
    TestEntityV2UpdateFluentHelper updateOtherTestEntity(
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
    TestEntityV2DeleteFluentHelper deleteOtherTestEntity(
        @Nonnull
        final TestEntityV2 testEntityV2);

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
