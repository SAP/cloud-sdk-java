/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.services;

import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.sap.cloud.sdk.datamodel.odatav4.core.BatchRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CollectionValueFunctionRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.SingleValueActionRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.SingleValueFunctionRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder;
import testcomparison.namespaces.test.A_TestComplexType;
import testcomparison.namespaces.test.TestEntityCircularLinkChild;
import testcomparison.namespaces.test.TestEntityCircularLinkParent;
import testcomparison.namespaces.test.TestEntityLvl2MultiLink;
import testcomparison.namespaces.test.TestEntityLvl2SingleLink;
import testcomparison.namespaces.test.TestEntityMultiLink;
import testcomparison.namespaces.test.TestEntityOtherMultiLink;
import testcomparison.namespaces.test.TestEntitySingleLink;
import testcomparison.namespaces.test.TestEntityV4;


/**
 * <p>Reference: <a href='https://api.sap.com/shell/discover/contentpackage/SAPS4HANACloud/api/test_service?section=OVERVIEW'>SAP Business Accelerator Hub</a></p><h3>Details:</h3><table summary='Details'><tr><td align='right'>OData Service:</td><td>test_service</td></tr></table>
 * 
 */
public interface TestService {

    /**
     * If no other path was provided via the {@link #withServicePath(String)} method, this is the default service path used to access the endpoint.
     * 
     */
    String DEFAULT_SERVICE_PATH = "/API_TEST_SRV";

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
     * Creates a batch request builder object.
     * 
     * @return
     *     A request builder to handle batch operation on this service. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.BatchRequestBuilder#execute(Destination) execute} method on the request builder object.
     */
    @Nonnull
    BatchRequestBuilder batch();

    /**
     * Fetch multiple {@link testcomparison.namespaces.test.TestEntityV4 TestEntityV4} entities.
     * 
     * @return
     *     A request builder to fetch multiple {@link testcomparison.namespaces.test.TestEntityV4 TestEntityV4} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<testcomparison.namespaces.test.TestEntityV4>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetAllRequestBuilder<TestEntityV4> getAllTestEntity();

    /**
     * Fetch the number of entries from the {@link testcomparison.namespaces.test.TestEntityV4 TestEntityV4} entity collection matching the filter and search expressions.
     * 
     * @return
     *     A request builder to fetch the count of {@link testcomparison.namespaces.test.TestEntityV4 TestEntityV4} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<testcomparison.namespaces.test.TestEntityV4>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CountRequestBuilder<TestEntityV4> countTestEntity();

    /**
     * Fetch a single {@link testcomparison.namespaces.test.TestEntityV4 TestEntityV4} entity using key fields.
     * 
     * @param keyPropertyString
     *     <p>Constraints: Not nullable</p>
     * @param keyPropertyGuid
     *     <p>Constraints: Not nullable</p>
     * @return
     *     A request builder to fetch a single {@link testcomparison.namespaces.test.TestEntityV4 TestEntityV4} entity using key fields. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<testcomparison.namespaces.test.TestEntityV4>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetByKeyRequestBuilder<TestEntityV4> getTestEntityByKey(final UUID keyPropertyGuid, final String keyPropertyString);

    /**
     * Create a new {@link testcomparison.namespaces.test.TestEntityV4 TestEntityV4} entity and save it to the S/4HANA system.
     * 
     * @param testEntityV4
     *     {@link testcomparison.namespaces.test.TestEntityV4 TestEntityV4} entity object that will be created in the S/4HANA system.
     * @return
     *     A request builder to create a new {@link testcomparison.namespaces.test.TestEntityV4 TestEntityV4} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<testcomparison.namespaces.test.TestEntityV4>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CreateRequestBuilder<TestEntityV4> createTestEntity(
        @Nonnull
        final TestEntityV4 testEntityV4);

    /**
     * Update an existing {@link testcomparison.namespaces.test.TestEntityV4 TestEntityV4} entity and save it to the S/4HANA system.
     * 
     * @param testEntityV4
     *     {@link testcomparison.namespaces.test.TestEntityV4 TestEntityV4} entity object that will be updated in the S/4HANA system.
     * @return
     *     A request builder to update an existing {@link testcomparison.namespaces.test.TestEntityV4 TestEntityV4} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<testcomparison.namespaces.test.TestEntityV4>#execute execute} method on the request builder object. 
     */
    @Nonnull
    UpdateRequestBuilder<TestEntityV4> updateTestEntity(
        @Nonnull
        final TestEntityV4 testEntityV4);

    /**
     * Deletes an existing {@link testcomparison.namespaces.test.TestEntityV4 TestEntityV4} entity in the S/4HANA system.
     * 
     * @param testEntityV4
     *     {@link testcomparison.namespaces.test.TestEntityV4 TestEntityV4} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A request builder to delete an existing {@link testcomparison.namespaces.test.TestEntityV4 TestEntityV4} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<testcomparison.namespaces.test.TestEntityV4>#execute execute} method on the request builder object. 
     */
    @Nonnull
    DeleteRequestBuilder<TestEntityV4> deleteTestEntity(
        @Nonnull
        final TestEntityV4 testEntityV4);

    /**
     * Fetch multiple {@link testcomparison.namespaces.test.TestEntityV4 TestEntityV4} entities.
     * 
     * @return
     *     A request builder to fetch multiple {@link testcomparison.namespaces.test.TestEntityV4 TestEntityV4} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<testcomparison.namespaces.test.TestEntityV4>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetAllRequestBuilder<TestEntityV4> getAllOtherTestEntity();

    /**
     * Fetch the number of entries from the {@link testcomparison.namespaces.test.TestEntityV4 TestEntityV4} entity collection matching the filter and search expressions.
     * 
     * @return
     *     A request builder to fetch the count of {@link testcomparison.namespaces.test.TestEntityV4 TestEntityV4} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<testcomparison.namespaces.test.TestEntityV4>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CountRequestBuilder<TestEntityV4> countOtherTestEntity();

    /**
     * Fetch a single {@link testcomparison.namespaces.test.TestEntityV4 TestEntityV4} entity using key fields.
     * 
     * @param keyPropertyString
     *     <p>Constraints: Not nullable</p>
     * @param keyPropertyGuid
     *     <p>Constraints: Not nullable</p>
     * @return
     *     A request builder to fetch a single {@link testcomparison.namespaces.test.TestEntityV4 TestEntityV4} entity using key fields. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<testcomparison.namespaces.test.TestEntityV4>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetByKeyRequestBuilder<TestEntityV4> getOtherTestEntityByKey(final UUID keyPropertyGuid, final String keyPropertyString);

    /**
     * Create a new {@link testcomparison.namespaces.test.TestEntityV4 TestEntityV4} entity and save it to the S/4HANA system.
     * 
     * @param testEntityV4
     *     {@link testcomparison.namespaces.test.TestEntityV4 TestEntityV4} entity object that will be created in the S/4HANA system.
     * @return
     *     A request builder to create a new {@link testcomparison.namespaces.test.TestEntityV4 TestEntityV4} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<testcomparison.namespaces.test.TestEntityV4>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CreateRequestBuilder<TestEntityV4> createOtherTestEntity(
        @Nonnull
        final TestEntityV4 testEntityV4);

    /**
     * Update an existing {@link testcomparison.namespaces.test.TestEntityV4 TestEntityV4} entity and save it to the S/4HANA system.
     * 
     * @param testEntityV4
     *     {@link testcomparison.namespaces.test.TestEntityV4 TestEntityV4} entity object that will be updated in the S/4HANA system.
     * @return
     *     A request builder to update an existing {@link testcomparison.namespaces.test.TestEntityV4 TestEntityV4} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<testcomparison.namespaces.test.TestEntityV4>#execute execute} method on the request builder object. 
     */
    @Nonnull
    UpdateRequestBuilder<TestEntityV4> updateOtherTestEntity(
        @Nonnull
        final TestEntityV4 testEntityV4);

    /**
     * Deletes an existing {@link testcomparison.namespaces.test.TestEntityV4 TestEntityV4} entity in the S/4HANA system.
     * 
     * @param testEntityV4
     *     {@link testcomparison.namespaces.test.TestEntityV4 TestEntityV4} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A request builder to delete an existing {@link testcomparison.namespaces.test.TestEntityV4 TestEntityV4} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<testcomparison.namespaces.test.TestEntityV4>#execute execute} method on the request builder object. 
     */
    @Nonnull
    DeleteRequestBuilder<TestEntityV4> deleteOtherTestEntity(
        @Nonnull
        final TestEntityV4 testEntityV4);

    /**
     * Fetch multiple {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entities.
     * 
     * @return
     *     A request builder to fetch multiple {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<testcomparison.namespaces.test.TestEntityMultiLink>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetAllRequestBuilder<TestEntityMultiLink> getAllTestEntityMultiLink();

    /**
     * Fetch the number of entries from the {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entity collection matching the filter and search expressions.
     * 
     * @return
     *     A request builder to fetch the count of {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<testcomparison.namespaces.test.TestEntityMultiLink>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CountRequestBuilder<TestEntityMultiLink> countTestEntityMultiLink();

    /**
     * Fetch a single {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entity using key fields.
     * 
     * @param keyProperty
     *     <p>Constraints: Not nullable, Maximum length: 10</p>
     * @return
     *     A request builder to fetch a single {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entity using key fields. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<testcomparison.namespaces.test.TestEntityMultiLink>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetByKeyRequestBuilder<TestEntityMultiLink> getTestEntityMultiLinkByKey(final String keyProperty);

    /**
     * Create a new {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entity and save it to the S/4HANA system.
     * 
     * @param testEntityMultiLink
     *     {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entity object that will be created in the S/4HANA system.
     * @return
     *     A request builder to create a new {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<testcomparison.namespaces.test.TestEntityMultiLink>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CreateRequestBuilder<TestEntityMultiLink> createTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink);

    /**
     * Update an existing {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entity and save it to the S/4HANA system.
     * 
     * @param testEntityMultiLink
     *     {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entity object that will be updated in the S/4HANA system.
     * @return
     *     A request builder to update an existing {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<testcomparison.namespaces.test.TestEntityMultiLink>#execute execute} method on the request builder object. 
     */
    @Nonnull
    UpdateRequestBuilder<TestEntityMultiLink> updateTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink);

    /**
     * Deletes an existing {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entity in the S/4HANA system.
     * 
     * @param testEntityMultiLink
     *     {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A request builder to delete an existing {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<testcomparison.namespaces.test.TestEntityMultiLink>#execute execute} method on the request builder object. 
     */
    @Nonnull
    DeleteRequestBuilder<TestEntityMultiLink> deleteTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink);

    /**
     * Fetch multiple {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entities.
     * 
     * @return
     *     A request builder to fetch multiple {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<testcomparison.namespaces.test.TestEntityOtherMultiLink>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetAllRequestBuilder<TestEntityOtherMultiLink> getAllTestEntityOtherMultiLink();

    /**
     * Fetch the number of entries from the {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity collection matching the filter and search expressions.
     * 
     * @return
     *     A request builder to fetch the count of {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<testcomparison.namespaces.test.TestEntityOtherMultiLink>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CountRequestBuilder<TestEntityOtherMultiLink> countTestEntityOtherMultiLink();

    /**
     * Fetch a single {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity using key fields.
     * 
     * @param keyProperty
     *     <p>Constraints: Not nullable, Maximum length: 10</p>
     * @return
     *     A request builder to fetch a single {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity using key fields. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<testcomparison.namespaces.test.TestEntityOtherMultiLink>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetByKeyRequestBuilder<TestEntityOtherMultiLink> getTestEntityOtherMultiLinkByKey(final String keyProperty);

    /**
     * Create a new {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity and save it to the S/4HANA system.
     * 
     * @param testEntityOtherMultiLink
     *     {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity object that will be created in the S/4HANA system.
     * @return
     *     A request builder to create a new {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<testcomparison.namespaces.test.TestEntityOtherMultiLink>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CreateRequestBuilder<TestEntityOtherMultiLink> createTestEntityOtherMultiLink(
        @Nonnull
        final TestEntityOtherMultiLink testEntityOtherMultiLink);

    /**
     * Update an existing {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity and save it to the S/4HANA system.
     * 
     * @param testEntityOtherMultiLink
     *     {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity object that will be updated in the S/4HANA system.
     * @return
     *     A request builder to update an existing {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<testcomparison.namespaces.test.TestEntityOtherMultiLink>#execute execute} method on the request builder object. 
     */
    @Nonnull
    UpdateRequestBuilder<TestEntityOtherMultiLink> updateTestEntityOtherMultiLink(
        @Nonnull
        final TestEntityOtherMultiLink testEntityOtherMultiLink);

    /**
     * Deletes an existing {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity in the S/4HANA system.
     * 
     * @param testEntityOtherMultiLink
     *     {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A request builder to delete an existing {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<testcomparison.namespaces.test.TestEntityOtherMultiLink>#execute execute} method on the request builder object. 
     */
    @Nonnull
    DeleteRequestBuilder<TestEntityOtherMultiLink> deleteTestEntityOtherMultiLink(
        @Nonnull
        final TestEntityOtherMultiLink testEntityOtherMultiLink);

    /**
     * Fetch multiple {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entities.
     * 
     * @return
     *     A request builder to fetch multiple {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<testcomparison.namespaces.test.TestEntityLvl2MultiLink>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetAllRequestBuilder<TestEntityLvl2MultiLink> getAllTestEntityLvl2MultiLink();

    /**
     * Fetch the number of entries from the {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity collection matching the filter and search expressions.
     * 
     * @return
     *     A request builder to fetch the count of {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<testcomparison.namespaces.test.TestEntityLvl2MultiLink>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CountRequestBuilder<TestEntityLvl2MultiLink> countTestEntityLvl2MultiLink();

    /**
     * Fetch a single {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity using key fields.
     * 
     * @param keyProperty
     *     <p>Constraints: Not nullable, Maximum length: 10</p>
     * @return
     *     A request builder to fetch a single {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity using key fields. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<testcomparison.namespaces.test.TestEntityLvl2MultiLink>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetByKeyRequestBuilder<TestEntityLvl2MultiLink> getTestEntityLvl2MultiLinkByKey(final String keyProperty);

    /**
     * Create a new {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity and save it to the S/4HANA system.
     * 
     * @param testEntityLvl2MultiLink
     *     {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity object that will be created in the S/4HANA system.
     * @return
     *     A request builder to create a new {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<testcomparison.namespaces.test.TestEntityLvl2MultiLink>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CreateRequestBuilder<TestEntityLvl2MultiLink> createTestEntityLvl2MultiLink(
        @Nonnull
        final TestEntityLvl2MultiLink testEntityLvl2MultiLink);

    /**
     * Update an existing {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity and save it to the S/4HANA system.
     * 
     * @param testEntityLvl2MultiLink
     *     {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity object that will be updated in the S/4HANA system.
     * @return
     *     A request builder to update an existing {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<testcomparison.namespaces.test.TestEntityLvl2MultiLink>#execute execute} method on the request builder object. 
     */
    @Nonnull
    UpdateRequestBuilder<TestEntityLvl2MultiLink> updateTestEntityLvl2MultiLink(
        @Nonnull
        final TestEntityLvl2MultiLink testEntityLvl2MultiLink);

    /**
     * Deletes an existing {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity in the S/4HANA system.
     * 
     * @param testEntityLvl2MultiLink
     *     {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A request builder to delete an existing {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<testcomparison.namespaces.test.TestEntityLvl2MultiLink>#execute execute} method on the request builder object. 
     */
    @Nonnull
    DeleteRequestBuilder<TestEntityLvl2MultiLink> deleteTestEntityLvl2MultiLink(
        @Nonnull
        final TestEntityLvl2MultiLink testEntityLvl2MultiLink);

    /**
     * Fetch multiple {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entities.
     * 
     * @return
     *     A request builder to fetch multiple {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<testcomparison.namespaces.test.TestEntitySingleLink>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetAllRequestBuilder<TestEntitySingleLink> getAllTestEntitySingleLink();

    /**
     * Fetch the number of entries from the {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entity collection matching the filter and search expressions.
     * 
     * @return
     *     A request builder to fetch the count of {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<testcomparison.namespaces.test.TestEntitySingleLink>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CountRequestBuilder<TestEntitySingleLink> countTestEntitySingleLink();

    /**
     * Fetch a single {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entity using key fields.
     * 
     * @param keyProperty
     *     <p>Constraints: Not nullable, Maximum length: 10</p>
     * @return
     *     A request builder to fetch a single {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entity using key fields. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<testcomparison.namespaces.test.TestEntitySingleLink>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetByKeyRequestBuilder<TestEntitySingleLink> getTestEntitySingleLinkByKey(final String keyProperty);

    /**
     * Create a new {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entity and save it to the S/4HANA system.
     * 
     * @param testEntitySingleLink
     *     {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entity object that will be created in the S/4HANA system.
     * @return
     *     A request builder to create a new {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<testcomparison.namespaces.test.TestEntitySingleLink>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CreateRequestBuilder<TestEntitySingleLink> createTestEntitySingleLink(
        @Nonnull
        final TestEntitySingleLink testEntitySingleLink);

    /**
     * Update an existing {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entity and save it to the S/4HANA system.
     * 
     * @param testEntitySingleLink
     *     {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entity object that will be updated in the S/4HANA system.
     * @return
     *     A request builder to update an existing {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<testcomparison.namespaces.test.TestEntitySingleLink>#execute execute} method on the request builder object. 
     */
    @Nonnull
    UpdateRequestBuilder<TestEntitySingleLink> updateTestEntitySingleLink(
        @Nonnull
        final TestEntitySingleLink testEntitySingleLink);

    /**
     * Deletes an existing {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entity in the S/4HANA system.
     * 
     * @param testEntitySingleLink
     *     {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A request builder to delete an existing {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<testcomparison.namespaces.test.TestEntitySingleLink>#execute execute} method on the request builder object. 
     */
    @Nonnull
    DeleteRequestBuilder<TestEntitySingleLink> deleteTestEntitySingleLink(
        @Nonnull
        final TestEntitySingleLink testEntitySingleLink);

    /**
     * Fetch multiple {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entities.
     * 
     * @return
     *     A request builder to fetch multiple {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<testcomparison.namespaces.test.TestEntityLvl2SingleLink>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetAllRequestBuilder<TestEntityLvl2SingleLink> getAllTestEntityLvl2SingleLink();

    /**
     * Fetch the number of entries from the {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity collection matching the filter and search expressions.
     * 
     * @return
     *     A request builder to fetch the count of {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<testcomparison.namespaces.test.TestEntityLvl2SingleLink>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CountRequestBuilder<TestEntityLvl2SingleLink> countTestEntityLvl2SingleLink();

    /**
     * Fetch a single {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity using key fields.
     * 
     * @param keyProperty
     *     <p>Constraints: Not nullable, Maximum length: 10</p>
     * @return
     *     A request builder to fetch a single {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity using key fields. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<testcomparison.namespaces.test.TestEntityLvl2SingleLink>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetByKeyRequestBuilder<TestEntityLvl2SingleLink> getTestEntityLvl2SingleLinkByKey(final String keyProperty);

    /**
     * Create a new {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity and save it to the S/4HANA system.
     * 
     * @param testEntityLvl2SingleLink
     *     {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity object that will be created in the S/4HANA system.
     * @return
     *     A request builder to create a new {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<testcomparison.namespaces.test.TestEntityLvl2SingleLink>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CreateRequestBuilder<TestEntityLvl2SingleLink> createTestEntityLvl2SingleLink(
        @Nonnull
        final TestEntityLvl2SingleLink testEntityLvl2SingleLink);

    /**
     * Update an existing {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity and save it to the S/4HANA system.
     * 
     * @param testEntityLvl2SingleLink
     *     {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity object that will be updated in the S/4HANA system.
     * @return
     *     A request builder to update an existing {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<testcomparison.namespaces.test.TestEntityLvl2SingleLink>#execute execute} method on the request builder object. 
     */
    @Nonnull
    UpdateRequestBuilder<TestEntityLvl2SingleLink> updateTestEntityLvl2SingleLink(
        @Nonnull
        final TestEntityLvl2SingleLink testEntityLvl2SingleLink);

    /**
     * Deletes an existing {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity in the S/4HANA system.
     * 
     * @param testEntityLvl2SingleLink
     *     {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A request builder to delete an existing {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<testcomparison.namespaces.test.TestEntityLvl2SingleLink>#execute execute} method on the request builder object. 
     */
    @Nonnull
    DeleteRequestBuilder<TestEntityLvl2SingleLink> deleteTestEntityLvl2SingleLink(
        @Nonnull
        final TestEntityLvl2SingleLink testEntityLvl2SingleLink);

    /**
     * Fetch multiple {@link testcomparison.namespaces.test.TestEntityCircularLinkParent TestEntityCircularLinkParent} entities.
     * 
     * @return
     *     A request builder to fetch multiple {@link testcomparison.namespaces.test.TestEntityCircularLinkParent TestEntityCircularLinkParent} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<testcomparison.namespaces.test.TestEntityCircularLinkParent>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetAllRequestBuilder<TestEntityCircularLinkParent> getAllTestEntityCircularLinkParent();

    /**
     * Fetch the number of entries from the {@link testcomparison.namespaces.test.TestEntityCircularLinkParent TestEntityCircularLinkParent} entity collection matching the filter and search expressions.
     * 
     * @return
     *     A request builder to fetch the count of {@link testcomparison.namespaces.test.TestEntityCircularLinkParent TestEntityCircularLinkParent} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<testcomparison.namespaces.test.TestEntityCircularLinkParent>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CountRequestBuilder<TestEntityCircularLinkParent> countTestEntityCircularLinkParent();

    /**
     * Fetch a single {@link testcomparison.namespaces.test.TestEntityCircularLinkParent TestEntityCircularLinkParent} entity using key fields.
     * 
     * @param keyProperty
     *     <p>Constraints: Not nullable, Maximum length: 10</p>
     * @return
     *     A request builder to fetch a single {@link testcomparison.namespaces.test.TestEntityCircularLinkParent TestEntityCircularLinkParent} entity using key fields. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<testcomparison.namespaces.test.TestEntityCircularLinkParent>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetByKeyRequestBuilder<TestEntityCircularLinkParent> getTestEntityCircularLinkParentByKey(final String keyProperty);

    /**
     * Create a new {@link testcomparison.namespaces.test.TestEntityCircularLinkParent TestEntityCircularLinkParent} entity and save it to the S/4HANA system.
     * 
     * @param testEntityCircularLinkParent
     *     {@link testcomparison.namespaces.test.TestEntityCircularLinkParent TestEntityCircularLinkParent} entity object that will be created in the S/4HANA system.
     * @return
     *     A request builder to create a new {@link testcomparison.namespaces.test.TestEntityCircularLinkParent TestEntityCircularLinkParent} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<testcomparison.namespaces.test.TestEntityCircularLinkParent>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CreateRequestBuilder<TestEntityCircularLinkParent> createTestEntityCircularLinkParent(
        @Nonnull
        final TestEntityCircularLinkParent testEntityCircularLinkParent);

    /**
     * Update an existing {@link testcomparison.namespaces.test.TestEntityCircularLinkParent TestEntityCircularLinkParent} entity and save it to the S/4HANA system.
     * 
     * @param testEntityCircularLinkParent
     *     {@link testcomparison.namespaces.test.TestEntityCircularLinkParent TestEntityCircularLinkParent} entity object that will be updated in the S/4HANA system.
     * @return
     *     A request builder to update an existing {@link testcomparison.namespaces.test.TestEntityCircularLinkParent TestEntityCircularLinkParent} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<testcomparison.namespaces.test.TestEntityCircularLinkParent>#execute execute} method on the request builder object. 
     */
    @Nonnull
    UpdateRequestBuilder<TestEntityCircularLinkParent> updateTestEntityCircularLinkParent(
        @Nonnull
        final TestEntityCircularLinkParent testEntityCircularLinkParent);

    /**
     * Deletes an existing {@link testcomparison.namespaces.test.TestEntityCircularLinkParent TestEntityCircularLinkParent} entity in the S/4HANA system.
     * 
     * @param testEntityCircularLinkParent
     *     {@link testcomparison.namespaces.test.TestEntityCircularLinkParent TestEntityCircularLinkParent} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A request builder to delete an existing {@link testcomparison.namespaces.test.TestEntityCircularLinkParent TestEntityCircularLinkParent} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<testcomparison.namespaces.test.TestEntityCircularLinkParent>#execute execute} method on the request builder object. 
     */
    @Nonnull
    DeleteRequestBuilder<TestEntityCircularLinkParent> deleteTestEntityCircularLinkParent(
        @Nonnull
        final TestEntityCircularLinkParent testEntityCircularLinkParent);

    /**
     * Fetch multiple {@link testcomparison.namespaces.test.TestEntityCircularLinkChild TestEntityCircularLinkChild} entities.
     * 
     * @return
     *     A request builder to fetch multiple {@link testcomparison.namespaces.test.TestEntityCircularLinkChild TestEntityCircularLinkChild} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<testcomparison.namespaces.test.TestEntityCircularLinkChild>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetAllRequestBuilder<TestEntityCircularLinkChild> getAllTestEntityCircularLinkChild();

    /**
     * Fetch the number of entries from the {@link testcomparison.namespaces.test.TestEntityCircularLinkChild TestEntityCircularLinkChild} entity collection matching the filter and search expressions.
     * 
     * @return
     *     A request builder to fetch the count of {@link testcomparison.namespaces.test.TestEntityCircularLinkChild TestEntityCircularLinkChild} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<testcomparison.namespaces.test.TestEntityCircularLinkChild>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CountRequestBuilder<TestEntityCircularLinkChild> countTestEntityCircularLinkChild();

    /**
     * Fetch a single {@link testcomparison.namespaces.test.TestEntityCircularLinkChild TestEntityCircularLinkChild} entity using key fields.
     * 
     * @param keyProperty
     *     <p>Constraints: Not nullable, Maximum length: 10</p>
     * @return
     *     A request builder to fetch a single {@link testcomparison.namespaces.test.TestEntityCircularLinkChild TestEntityCircularLinkChild} entity using key fields. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<testcomparison.namespaces.test.TestEntityCircularLinkChild>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetByKeyRequestBuilder<TestEntityCircularLinkChild> getTestEntityCircularLinkChildByKey(final String keyProperty);

    /**
     * Create a new {@link testcomparison.namespaces.test.TestEntityCircularLinkChild TestEntityCircularLinkChild} entity and save it to the S/4HANA system.
     * 
     * @param testEntityCircularLinkChild
     *     {@link testcomparison.namespaces.test.TestEntityCircularLinkChild TestEntityCircularLinkChild} entity object that will be created in the S/4HANA system.
     * @return
     *     A request builder to create a new {@link testcomparison.namespaces.test.TestEntityCircularLinkChild TestEntityCircularLinkChild} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<testcomparison.namespaces.test.TestEntityCircularLinkChild>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CreateRequestBuilder<TestEntityCircularLinkChild> createTestEntityCircularLinkChild(
        @Nonnull
        final TestEntityCircularLinkChild testEntityCircularLinkChild);

    /**
     * Update an existing {@link testcomparison.namespaces.test.TestEntityCircularLinkChild TestEntityCircularLinkChild} entity and save it to the S/4HANA system.
     * 
     * @param testEntityCircularLinkChild
     *     {@link testcomparison.namespaces.test.TestEntityCircularLinkChild TestEntityCircularLinkChild} entity object that will be updated in the S/4HANA system.
     * @return
     *     A request builder to update an existing {@link testcomparison.namespaces.test.TestEntityCircularLinkChild TestEntityCircularLinkChild} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<testcomparison.namespaces.test.TestEntityCircularLinkChild>#execute execute} method on the request builder object. 
     */
    @Nonnull
    UpdateRequestBuilder<TestEntityCircularLinkChild> updateTestEntityCircularLinkChild(
        @Nonnull
        final TestEntityCircularLinkChild testEntityCircularLinkChild);

    /**
     * Deletes an existing {@link testcomparison.namespaces.test.TestEntityCircularLinkChild TestEntityCircularLinkChild} entity in the S/4HANA system.
     * 
     * @param testEntityCircularLinkChild
     *     {@link testcomparison.namespaces.test.TestEntityCircularLinkChild TestEntityCircularLinkChild} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A request builder to delete an existing {@link testcomparison.namespaces.test.TestEntityCircularLinkChild TestEntityCircularLinkChild} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<testcomparison.namespaces.test.TestEntityCircularLinkChild>#execute execute} method on the request builder object. 
     */
    @Nonnull
    DeleteRequestBuilder<TestEntityCircularLinkChild> deleteTestEntityCircularLinkChild(
        @Nonnull
        final TestEntityCircularLinkChild testEntityCircularLinkChild);

    /**
     * <p>Creates a request builder for the <b>TestFunctionImportEdmReturnType</b> OData function.</p>
     * 
     * @return
     *     A request builder object that will execute the <b>TestFunctionImportEdmReturnType</b> OData function with the provided parameters. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.SingleValueFunctionRequestBuilder#execute execute} method on the request builder object.
     */
    @Nonnull
    SingleValueFunctionRequestBuilder<Boolean> testFunctionImportEdmReturnType();

    /**
     * <p>Creates a request builder for the <b>TestFunctionImportEdmReturnTypeCollection</b> OData function.</p>
     * 
     * @return
     *     A request builder object that will execute the <b>TestFunctionImportEdmReturnTypeCollection</b> OData function with the provided parameters. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CollectionValueFunctionRequestBuilder#execute execute} method on the request builder object.
     */
    @Nonnull
    CollectionValueFunctionRequestBuilder<String> testFunctionImportEdmReturnTypeCollection();

    /**
     * <p>Creates a request builder for the <b>TestFunctionImportEntityReturnType</b> OData function.</p>
     * 
     * @return
     *     A request builder object that will execute the <b>TestFunctionImportEntityReturnType</b> OData function with the provided parameters. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.SingleValueFunctionRequestBuilder#execute execute} method on the request builder object.
     */
    @Nonnull
    SingleValueFunctionRequestBuilder<TestEntityV4> testFunctionImportEntityReturnType();

    /**
     * <p>Creates a request builder for the <b>TestFunctionImportEntityReturnTypeCollection</b> OData function.</p>
     * 
     * @return
     *     A request builder object that will execute the <b>TestFunctionImportEntityReturnTypeCollection</b> OData function with the provided parameters. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CollectionValueFunctionRequestBuilder#execute execute} method on the request builder object.
     */
    @Nonnull
    CollectionValueFunctionRequestBuilder<TestEntityV4> testFunctionImportEntityReturnTypeCollection();

    /**
     * <p>Creates a request builder for the <b>TestFunctionImportComplexReturnType</b> OData function.</p>
     * 
     * @return
     *     A request builder object that will execute the <b>TestFunctionImportComplexReturnType</b> OData function with the provided parameters. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.SingleValueFunctionRequestBuilder#execute execute} method on the request builder object.
     */
    @Nonnull
    SingleValueFunctionRequestBuilder<A_TestComplexType> testFunctionImportComplexReturnType();

    /**
     * <p>Creates a request builder for the <b>TestFunctionImportComplexReturnTypeCollection</b> OData function.</p>
     * 
     * @return
     *     A request builder object that will execute the <b>TestFunctionImportComplexReturnTypeCollection</b> OData function with the provided parameters. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CollectionValueFunctionRequestBuilder#execute execute} method on the request builder object.
     */
    @Nonnull
    CollectionValueFunctionRequestBuilder<A_TestComplexType> testFunctionImportComplexReturnTypeCollection();

    /**
     * <p>Creates a request builder for the <b>StringParam</b> OData function.</p>
     * 
     * @param stringParam
     *     Constraints: Nullable<p>Original parameter name from the Odata EDM: <b>StringParam</b></p>
     * @param nonNullableStringParam
     *     Constraints: Not nullable<p>Original parameter name from the Odata EDM: <b>NonNullableStringParam</b></p>
     * @param nullableBooleanParam
     *     Constraints: Nullable<p>Original parameter name from the Odata EDM: <b>NullableBooleanParam</b></p>
     * @return
     *     A request builder object that will execute the <b>StringParam</b> OData function with the provided parameters. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.SingleValueFunctionRequestBuilder#execute execute} method on the request builder object.
     */
    @Nonnull
    SingleValueFunctionRequestBuilder<Boolean> stringParam(
        @Nullable
        final String stringParam,
        @Nonnull
        final String nonNullableStringParam,
        @Nullable
        final Boolean nullableBooleanParam);

    /**
     * <p>Creates a request builder for the <b>TestActionImportNoParameterNoReturnType</b> OData action.</p>
     * 
     * @return
     *     A request builder object that will execute the <b>TestActionImportNoParameterNoReturnType</b> OData action with the provided parameters. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.SingleValueActionRequestBuilder#execute execute} method on the request builder object.
     */
    @Nonnull
    SingleValueActionRequestBuilder<Void> testActionImportNoParameterNoReturnType();

    /**
     * <p>Creates a request builder for the <b>TestActionImportMultipleParameterComplexReturnType</b> OData action.</p>
     * 
     * @param stringParam
     *     Constraints: Nullable<p>Original parameter name from the Odata EDM: <b>StringParam</b></p>
     * @param nonNullableStringParam
     *     Constraints: Not nullable<p>Original parameter name from the Odata EDM: <b>NonNullableStringParam</b></p>
     * @param nullableBooleanParam
     *     Constraints: Nullable<p>Original parameter name from the Odata EDM: <b>NullableBooleanParam</b></p>
     * @return
     *     A request builder object that will execute the <b>TestActionImportMultipleParameterComplexReturnType</b> OData action with the provided parameters. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.SingleValueActionRequestBuilder#execute execute} method on the request builder object.
     */
    @Nonnull
    SingleValueActionRequestBuilder<A_TestComplexType> testActionImportMultipleParameterComplexReturnType(
        @Nullable
        final String stringParam,
        @Nonnull
        final String nonNullableStringParam,
        @Nullable
        final Boolean nullableBooleanParam);

}
