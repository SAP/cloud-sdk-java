/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.services;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
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
import com.sap.cloud.sdk.datamodel.odatav4.core.ServiceWithNavigableEntities;
import com.sap.cloud.sdk.datamodel.odatav4.core.SingleValueActionRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.SingleValueFunctionRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder;
import lombok.Getter;
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
public class DefaultTestService
    implements ServiceWithNavigableEntities, TestService
{

    @Nonnull
    @Getter
    private final String servicePath;

    /**
     * Creates a service using {@link TestService#DEFAULT_SERVICE_PATH} to send the requests.
     * 
     */
    public DefaultTestService() {
        servicePath = TestService.DEFAULT_SERVICE_PATH;
    }

    /**
     * Creates a service using the provided service path to send the requests.
     * <p>
     * Used by the fluent {@link #withServicePath(String)} method.
     * 
     */
    private DefaultTestService(
        @Nonnull
        final String servicePath) {
        this.servicePath = servicePath;
    }

    @Override
    @Nonnull
    public DefaultTestService withServicePath(
        @Nonnull
        final String servicePath) {
        return new DefaultTestService(servicePath);
    }

    @Override
    @Nonnull
    public BatchRequestBuilder batch() {
        return new BatchRequestBuilder(servicePath);
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<TestEntityV4> getAllTestEntity() {
        return new GetAllRequestBuilder<TestEntityV4>(servicePath, TestEntityV4 .class, "A_TestEntity");
    }

    @Override
    @Nonnull
    public CountRequestBuilder<TestEntityV4> countTestEntity() {
        return new CountRequestBuilder<TestEntityV4>(servicePath, TestEntityV4 .class, "A_TestEntity");
    }

    @Override
    @Nonnull
    public GetByKeyRequestBuilder<TestEntityV4> getTestEntityByKey(final UUID keyPropertyGuid, final String keyPropertyString) {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("KeyPropertyGuid", keyPropertyGuid);
        key.put("KeyPropertyString", keyPropertyString);
        return new GetByKeyRequestBuilder<TestEntityV4>(servicePath, TestEntityV4 .class, key, "A_TestEntity");
    }

    @Override
    @Nonnull
    public CreateRequestBuilder<TestEntityV4> createTestEntity(
        @Nonnull
        final TestEntityV4 testEntityV4) {
        return new CreateRequestBuilder<TestEntityV4>(servicePath, testEntityV4, "A_TestEntity");
    }

    @Override
    @Nonnull
    public UpdateRequestBuilder<TestEntityV4> updateTestEntity(
        @Nonnull
        final TestEntityV4 testEntityV4) {
        return new UpdateRequestBuilder<TestEntityV4>(servicePath, testEntityV4, "A_TestEntity");
    }

    @Override
    @Nonnull
    public DeleteRequestBuilder<TestEntityV4> deleteTestEntity(
        @Nonnull
        final TestEntityV4 testEntityV4) {
        return new DeleteRequestBuilder<TestEntityV4>(servicePath, testEntityV4, "A_TestEntity");
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<TestEntityV4> getAllOtherTestEntity() {
        return new GetAllRequestBuilder<TestEntityV4>(servicePath, TestEntityV4 .class, "A_OtherTestEntity");
    }

    @Override
    @Nonnull
    public CountRequestBuilder<TestEntityV4> countOtherTestEntity() {
        return new CountRequestBuilder<TestEntityV4>(servicePath, TestEntityV4 .class, "A_OtherTestEntity");
    }

    @Override
    @Nonnull
    public GetByKeyRequestBuilder<TestEntityV4> getOtherTestEntityByKey(final UUID keyPropertyGuid, final String keyPropertyString) {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("KeyPropertyGuid", keyPropertyGuid);
        key.put("KeyPropertyString", keyPropertyString);
        return new GetByKeyRequestBuilder<TestEntityV4>(servicePath, TestEntityV4 .class, key, "A_OtherTestEntity");
    }

    @Override
    @Nonnull
    public CreateRequestBuilder<TestEntityV4> createOtherTestEntity(
        @Nonnull
        final TestEntityV4 testEntityV4) {
        return new CreateRequestBuilder<TestEntityV4>(servicePath, testEntityV4, "A_OtherTestEntity");
    }

    @Override
    @Nonnull
    public UpdateRequestBuilder<TestEntityV4> updateOtherTestEntity(
        @Nonnull
        final TestEntityV4 testEntityV4) {
        return new UpdateRequestBuilder<TestEntityV4>(servicePath, testEntityV4, "A_OtherTestEntity");
    }

    @Override
    @Nonnull
    public DeleteRequestBuilder<TestEntityV4> deleteOtherTestEntity(
        @Nonnull
        final TestEntityV4 testEntityV4) {
        return new DeleteRequestBuilder<TestEntityV4>(servicePath, testEntityV4, "A_OtherTestEntity");
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<TestEntityMultiLink> getAllTestEntityMultiLink() {
        return new GetAllRequestBuilder<TestEntityMultiLink>(servicePath, TestEntityMultiLink.class, "A_TestEntityMultiLink");
    }

    @Override
    @Nonnull
    public CountRequestBuilder<TestEntityMultiLink> countTestEntityMultiLink() {
        return new CountRequestBuilder<TestEntityMultiLink>(servicePath, TestEntityMultiLink.class, "A_TestEntityMultiLink");
    }

    @Override
    @Nonnull
    public GetByKeyRequestBuilder<TestEntityMultiLink> getTestEntityMultiLinkByKey(final String keyProperty) {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("KeyProperty", keyProperty);
        return new GetByKeyRequestBuilder<TestEntityMultiLink>(servicePath, TestEntityMultiLink.class, key, "A_TestEntityMultiLink");
    }

    @Override
    @Nonnull
    public CreateRequestBuilder<TestEntityMultiLink> createTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink) {
        return new CreateRequestBuilder<TestEntityMultiLink>(servicePath, testEntityMultiLink, "A_TestEntityMultiLink");
    }

    @Override
    @Nonnull
    public UpdateRequestBuilder<TestEntityMultiLink> updateTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink) {
        return new UpdateRequestBuilder<TestEntityMultiLink>(servicePath, testEntityMultiLink, "A_TestEntityMultiLink");
    }

    @Override
    @Nonnull
    public DeleteRequestBuilder<TestEntityMultiLink> deleteTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink) {
        return new DeleteRequestBuilder<TestEntityMultiLink>(servicePath, testEntityMultiLink, "A_TestEntityMultiLink");
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<TestEntityOtherMultiLink> getAllTestEntityOtherMultiLink() {
        return new GetAllRequestBuilder<TestEntityOtherMultiLink>(servicePath, TestEntityOtherMultiLink.class, "A_TestEntityOtherMultiLink");
    }

    @Override
    @Nonnull
    public CountRequestBuilder<TestEntityOtherMultiLink> countTestEntityOtherMultiLink() {
        return new CountRequestBuilder<TestEntityOtherMultiLink>(servicePath, TestEntityOtherMultiLink.class, "A_TestEntityOtherMultiLink");
    }

    @Override
    @Nonnull
    public GetByKeyRequestBuilder<TestEntityOtherMultiLink> getTestEntityOtherMultiLinkByKey(final String keyProperty) {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("KeyProperty", keyProperty);
        return new GetByKeyRequestBuilder<TestEntityOtherMultiLink>(servicePath, TestEntityOtherMultiLink.class, key, "A_TestEntityOtherMultiLink");
    }

    @Override
    @Nonnull
    public CreateRequestBuilder<TestEntityOtherMultiLink> createTestEntityOtherMultiLink(
        @Nonnull
        final TestEntityOtherMultiLink testEntityOtherMultiLink) {
        return new CreateRequestBuilder<TestEntityOtherMultiLink>(servicePath, testEntityOtherMultiLink, "A_TestEntityOtherMultiLink");
    }

    @Override
    @Nonnull
    public UpdateRequestBuilder<TestEntityOtherMultiLink> updateTestEntityOtherMultiLink(
        @Nonnull
        final TestEntityOtherMultiLink testEntityOtherMultiLink) {
        return new UpdateRequestBuilder<TestEntityOtherMultiLink>(servicePath, testEntityOtherMultiLink, "A_TestEntityOtherMultiLink");
    }

    @Override
    @Nonnull
    public DeleteRequestBuilder<TestEntityOtherMultiLink> deleteTestEntityOtherMultiLink(
        @Nonnull
        final TestEntityOtherMultiLink testEntityOtherMultiLink) {
        return new DeleteRequestBuilder<TestEntityOtherMultiLink>(servicePath, testEntityOtherMultiLink, "A_TestEntityOtherMultiLink");
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<TestEntityLvl2MultiLink> getAllTestEntityLvl2MultiLink() {
        return new GetAllRequestBuilder<TestEntityLvl2MultiLink>(servicePath, TestEntityLvl2MultiLink.class, "A_TestEntityLvl2MultiLink");
    }

    @Override
    @Nonnull
    public CountRequestBuilder<TestEntityLvl2MultiLink> countTestEntityLvl2MultiLink() {
        return new CountRequestBuilder<TestEntityLvl2MultiLink>(servicePath, TestEntityLvl2MultiLink.class, "A_TestEntityLvl2MultiLink");
    }

    @Override
    @Nonnull
    public GetByKeyRequestBuilder<TestEntityLvl2MultiLink> getTestEntityLvl2MultiLinkByKey(final String keyProperty) {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("KeyProperty", keyProperty);
        return new GetByKeyRequestBuilder<TestEntityLvl2MultiLink>(servicePath, TestEntityLvl2MultiLink.class, key, "A_TestEntityLvl2MultiLink");
    }

    @Override
    @Nonnull
    public CreateRequestBuilder<TestEntityLvl2MultiLink> createTestEntityLvl2MultiLink(
        @Nonnull
        final TestEntityLvl2MultiLink testEntityLvl2MultiLink) {
        return new CreateRequestBuilder<TestEntityLvl2MultiLink>(servicePath, testEntityLvl2MultiLink, "A_TestEntityLvl2MultiLink");
    }

    @Override
    @Nonnull
    public UpdateRequestBuilder<TestEntityLvl2MultiLink> updateTestEntityLvl2MultiLink(
        @Nonnull
        final TestEntityLvl2MultiLink testEntityLvl2MultiLink) {
        return new UpdateRequestBuilder<TestEntityLvl2MultiLink>(servicePath, testEntityLvl2MultiLink, "A_TestEntityLvl2MultiLink");
    }

    @Override
    @Nonnull
    public DeleteRequestBuilder<TestEntityLvl2MultiLink> deleteTestEntityLvl2MultiLink(
        @Nonnull
        final TestEntityLvl2MultiLink testEntityLvl2MultiLink) {
        return new DeleteRequestBuilder<TestEntityLvl2MultiLink>(servicePath, testEntityLvl2MultiLink, "A_TestEntityLvl2MultiLink");
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<TestEntitySingleLink> getAllTestEntitySingleLink() {
        return new GetAllRequestBuilder<TestEntitySingleLink>(servicePath, TestEntitySingleLink.class, "A_TestEntitySingleLink");
    }

    @Override
    @Nonnull
    public CountRequestBuilder<TestEntitySingleLink> countTestEntitySingleLink() {
        return new CountRequestBuilder<TestEntitySingleLink>(servicePath, TestEntitySingleLink.class, "A_TestEntitySingleLink");
    }

    @Override
    @Nonnull
    public GetByKeyRequestBuilder<TestEntitySingleLink> getTestEntitySingleLinkByKey(final String keyProperty) {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("KeyProperty", keyProperty);
        return new GetByKeyRequestBuilder<TestEntitySingleLink>(servicePath, TestEntitySingleLink.class, key, "A_TestEntitySingleLink");
    }

    @Override
    @Nonnull
    public CreateRequestBuilder<TestEntitySingleLink> createTestEntitySingleLink(
        @Nonnull
        final TestEntitySingleLink testEntitySingleLink) {
        return new CreateRequestBuilder<TestEntitySingleLink>(servicePath, testEntitySingleLink, "A_TestEntitySingleLink");
    }

    @Override
    @Nonnull
    public UpdateRequestBuilder<TestEntitySingleLink> updateTestEntitySingleLink(
        @Nonnull
        final TestEntitySingleLink testEntitySingleLink) {
        return new UpdateRequestBuilder<TestEntitySingleLink>(servicePath, testEntitySingleLink, "A_TestEntitySingleLink");
    }

    @Override
    @Nonnull
    public DeleteRequestBuilder<TestEntitySingleLink> deleteTestEntitySingleLink(
        @Nonnull
        final TestEntitySingleLink testEntitySingleLink) {
        return new DeleteRequestBuilder<TestEntitySingleLink>(servicePath, testEntitySingleLink, "A_TestEntitySingleLink");
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<TestEntityLvl2SingleLink> getAllTestEntityLvl2SingleLink() {
        return new GetAllRequestBuilder<TestEntityLvl2SingleLink>(servicePath, TestEntityLvl2SingleLink.class, "A_TestEntityLvl2SingleLink");
    }

    @Override
    @Nonnull
    public CountRequestBuilder<TestEntityLvl2SingleLink> countTestEntityLvl2SingleLink() {
        return new CountRequestBuilder<TestEntityLvl2SingleLink>(servicePath, TestEntityLvl2SingleLink.class, "A_TestEntityLvl2SingleLink");
    }

    @Override
    @Nonnull
    public GetByKeyRequestBuilder<TestEntityLvl2SingleLink> getTestEntityLvl2SingleLinkByKey(final String keyProperty) {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("KeyProperty", keyProperty);
        return new GetByKeyRequestBuilder<TestEntityLvl2SingleLink>(servicePath, TestEntityLvl2SingleLink.class, key, "A_TestEntityLvl2SingleLink");
    }

    @Override
    @Nonnull
    public CreateRequestBuilder<TestEntityLvl2SingleLink> createTestEntityLvl2SingleLink(
        @Nonnull
        final TestEntityLvl2SingleLink testEntityLvl2SingleLink) {
        return new CreateRequestBuilder<TestEntityLvl2SingleLink>(servicePath, testEntityLvl2SingleLink, "A_TestEntityLvl2SingleLink");
    }

    @Override
    @Nonnull
    public UpdateRequestBuilder<TestEntityLvl2SingleLink> updateTestEntityLvl2SingleLink(
        @Nonnull
        final TestEntityLvl2SingleLink testEntityLvl2SingleLink) {
        return new UpdateRequestBuilder<TestEntityLvl2SingleLink>(servicePath, testEntityLvl2SingleLink, "A_TestEntityLvl2SingleLink");
    }

    @Override
    @Nonnull
    public DeleteRequestBuilder<TestEntityLvl2SingleLink> deleteTestEntityLvl2SingleLink(
        @Nonnull
        final TestEntityLvl2SingleLink testEntityLvl2SingleLink) {
        return new DeleteRequestBuilder<TestEntityLvl2SingleLink>(servicePath, testEntityLvl2SingleLink, "A_TestEntityLvl2SingleLink");
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<TestEntityCircularLinkParent> getAllTestEntityCircularLinkParent() {
        return new GetAllRequestBuilder<TestEntityCircularLinkParent>(servicePath, TestEntityCircularLinkParent.class, "A_TestEntityCircularLinkParent");
    }

    @Override
    @Nonnull
    public CountRequestBuilder<TestEntityCircularLinkParent> countTestEntityCircularLinkParent() {
        return new CountRequestBuilder<TestEntityCircularLinkParent>(servicePath, TestEntityCircularLinkParent.class, "A_TestEntityCircularLinkParent");
    }

    @Override
    @Nonnull
    public GetByKeyRequestBuilder<TestEntityCircularLinkParent> getTestEntityCircularLinkParentByKey(final String keyProperty) {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("KeyProperty", keyProperty);
        return new GetByKeyRequestBuilder<TestEntityCircularLinkParent>(servicePath, TestEntityCircularLinkParent.class, key, "A_TestEntityCircularLinkParent");
    }

    @Override
    @Nonnull
    public CreateRequestBuilder<TestEntityCircularLinkParent> createTestEntityCircularLinkParent(
        @Nonnull
        final TestEntityCircularLinkParent testEntityCircularLinkParent) {
        return new CreateRequestBuilder<TestEntityCircularLinkParent>(servicePath, testEntityCircularLinkParent, "A_TestEntityCircularLinkParent");
    }

    @Override
    @Nonnull
    public UpdateRequestBuilder<TestEntityCircularLinkParent> updateTestEntityCircularLinkParent(
        @Nonnull
        final TestEntityCircularLinkParent testEntityCircularLinkParent) {
        return new UpdateRequestBuilder<TestEntityCircularLinkParent>(servicePath, testEntityCircularLinkParent, "A_TestEntityCircularLinkParent");
    }

    @Override
    @Nonnull
    public DeleteRequestBuilder<TestEntityCircularLinkParent> deleteTestEntityCircularLinkParent(
        @Nonnull
        final TestEntityCircularLinkParent testEntityCircularLinkParent) {
        return new DeleteRequestBuilder<TestEntityCircularLinkParent>(servicePath, testEntityCircularLinkParent, "A_TestEntityCircularLinkParent");
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<TestEntityCircularLinkChild> getAllTestEntityCircularLinkChild() {
        return new GetAllRequestBuilder<TestEntityCircularLinkChild>(servicePath, TestEntityCircularLinkChild.class, "A_TestEntityCircularLinkChild");
    }

    @Override
    @Nonnull
    public CountRequestBuilder<TestEntityCircularLinkChild> countTestEntityCircularLinkChild() {
        return new CountRequestBuilder<TestEntityCircularLinkChild>(servicePath, TestEntityCircularLinkChild.class, "A_TestEntityCircularLinkChild");
    }

    @Override
    @Nonnull
    public GetByKeyRequestBuilder<TestEntityCircularLinkChild> getTestEntityCircularLinkChildByKey(final String keyProperty) {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("KeyProperty", keyProperty);
        return new GetByKeyRequestBuilder<TestEntityCircularLinkChild>(servicePath, TestEntityCircularLinkChild.class, key, "A_TestEntityCircularLinkChild");
    }

    @Override
    @Nonnull
    public CreateRequestBuilder<TestEntityCircularLinkChild> createTestEntityCircularLinkChild(
        @Nonnull
        final TestEntityCircularLinkChild testEntityCircularLinkChild) {
        return new CreateRequestBuilder<TestEntityCircularLinkChild>(servicePath, testEntityCircularLinkChild, "A_TestEntityCircularLinkChild");
    }

    @Override
    @Nonnull
    public UpdateRequestBuilder<TestEntityCircularLinkChild> updateTestEntityCircularLinkChild(
        @Nonnull
        final TestEntityCircularLinkChild testEntityCircularLinkChild) {
        return new UpdateRequestBuilder<TestEntityCircularLinkChild>(servicePath, testEntityCircularLinkChild, "A_TestEntityCircularLinkChild");
    }

    @Override
    @Nonnull
    public DeleteRequestBuilder<TestEntityCircularLinkChild> deleteTestEntityCircularLinkChild(
        @Nonnull
        final TestEntityCircularLinkChild testEntityCircularLinkChild) {
        return new DeleteRequestBuilder<TestEntityCircularLinkChild>(servicePath, testEntityCircularLinkChild, "A_TestEntityCircularLinkChild");
    }

    @Override
    @Nonnull
    public SingleValueFunctionRequestBuilder<Boolean> testFunctionImportEdmReturnType() {
        return new SingleValueFunctionRequestBuilder<Boolean>(servicePath, "TestFunctionImportEdmReturnType", Boolean.class);
    }

    @Override
    @Nonnull
    public CollectionValueFunctionRequestBuilder<String> testFunctionImportEdmReturnTypeCollection() {
        return new CollectionValueFunctionRequestBuilder<String>(servicePath, "TestFunctionImportEdmReturnTypeCollection", String.class);
    }

    @Override
    @Nonnull
    public SingleValueFunctionRequestBuilder<TestEntityV4> testFunctionImportEntityReturnType() {
        return new SingleValueFunctionRequestBuilder<TestEntityV4>(servicePath, "TestFunctionImportEntityReturnType", TestEntityV4 .class);
    }

    @Override
    @Nonnull
    public CollectionValueFunctionRequestBuilder<TestEntityV4> testFunctionImportEntityReturnTypeCollection() {
        return new CollectionValueFunctionRequestBuilder<TestEntityV4>(servicePath, "TestFunctionImportEntityReturnTypeCollection", TestEntityV4 .class);
    }

    @Override
    @Nonnull
    public SingleValueFunctionRequestBuilder<A_TestComplexType> testFunctionImportComplexReturnType() {
        return new SingleValueFunctionRequestBuilder<A_TestComplexType>(servicePath, "TestFunctionImportComplexReturnType", A_TestComplexType.class);
    }

    @Override
    @Nonnull
    public CollectionValueFunctionRequestBuilder<A_TestComplexType> testFunctionImportComplexReturnTypeCollection() {
        return new CollectionValueFunctionRequestBuilder<A_TestComplexType>(servicePath, "TestFunctionImportComplexReturnTypeCollection", A_TestComplexType.class);
    }

    @Override
    @Nonnull
    public SingleValueFunctionRequestBuilder<Boolean> stringParam(
        @Nullable
        final String stringParam,
        @Nonnull
        final String nonNullableStringParam,
        @Nullable
        final Boolean nullableBooleanParam) {
        final LinkedHashMap<String, Object> parameters = new LinkedHashMap<String, Object>();
        parameters.put("StringParam", stringParam);
        parameters.put("NonNullableStringParam", nonNullableStringParam);
        parameters.put("NullableBooleanParam", nullableBooleanParam);
        return new SingleValueFunctionRequestBuilder<Boolean>(servicePath, "StringParam", parameters, Boolean.class);
    }

    @Override
    @Nonnull
    public SingleValueActionRequestBuilder<Void> testActionImportNoParameterNoReturnType() {
        return new SingleValueActionRequestBuilder<Void>(servicePath, "TestActionImportNoParameterNoReturnType", Void.class);
    }

    @Override
    @Nonnull
    public SingleValueActionRequestBuilder<A_TestComplexType> testActionImportMultipleParameterComplexReturnType(
        @Nullable
        final String stringParam,
        @Nonnull
        final String nonNullableStringParam,
        @Nullable
        final Boolean nullableBooleanParam) {
        final LinkedHashMap<String, Object> parameters = new LinkedHashMap<String, Object>();
        parameters.put("StringParam", stringParam);
        parameters.put("NonNullableStringParam", nonNullableStringParam);
        parameters.put("NullableBooleanParam", nullableBooleanParam);
        return new SingleValueActionRequestBuilder<A_TestComplexType>(servicePath, "TestActionImportMultipleParameterComplexReturnType", parameters, A_TestComplexType.class);
    }

}
