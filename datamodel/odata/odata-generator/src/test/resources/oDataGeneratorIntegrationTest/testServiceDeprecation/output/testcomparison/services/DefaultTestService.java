/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.services;

import javax.annotation.Nonnull;
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
import testcomparison.namespaces.test.batch.DefaultTestServiceBatch;


/**
 * <p>This service enables you to test OData specifications.
 * </p><p><a href='https://foo.bar/help.html'>Demo service</a></p><p>Reference: <a href='https://api.sap.com/shell/discover/contentpackage/SAPS4HANACloud/api/test_service?section=OVERVIEW'>SAP Business Accelerator Hub</a></p><h3>Details:</h3><table summary='Details'><tr><td align='right'>OData Service:</td><td>test_service</td></tr><tr><td align='right'>API Version:</td><td>1 </td></tr><tr><td align='right'>Minimum ERP Version:</td><td>1805</td></tr><tr><td align='right'>Foo:</td><td>Fizz, Buzz</td></tr><tr><td align='right'>Bar:</td><td>Bazz</td></tr></table>
 * 
 * @deprecated
 *     The service and all its related classes are deprecated as of release 9000 (AUG-1999). Please use the <a href="https://foo.bar/test/service_reloaded"> successor API</a> instead.
 */
@Deprecated
public class DefaultTestService
    implements TestService
{

    @Nonnull
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
    public DefaultTestServiceBatch batch() {
        return new DefaultTestServiceBatch(this, servicePath);
    }

    @Override
    @Nonnull
    public TestEntityV2FluentHelper getAllTestEntityV2() {
        return new TestEntityV2FluentHelper(servicePath, "A_TestEntity");
    }

    @Override
    @Nonnull
    public TestEntityV2CreateFluentHelper createTestEntityV2(
        @Nonnull
        final TestEntityV2 testEntityV2) {
        return new TestEntityV2CreateFluentHelper(servicePath, testEntityV2, "A_TestEntity");
    }

    @Override
    @Nonnull
    public TestEntityMultiLinkFluentHelper getAllTestEntityMultiLink() {
        return new TestEntityMultiLinkFluentHelper(servicePath, "A_TestEntityMultiLink");
    }

    @Override
    @Nonnull
    public TestEntityMultiLinkCreateFluentHelper createTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink) {
        return new TestEntityMultiLinkCreateFluentHelper(servicePath, testEntityMultiLink, "A_TestEntityMultiLink");
    }

    @Override
    @Nonnull
    public TestEntityOtherMultiLinkFluentHelper getAllTestEntityOtherMultiLink() {
        return new TestEntityOtherMultiLinkFluentHelper(servicePath, "A_TestEntityOtherMultiLink");
    }

    @Override
    @Nonnull
    public TestEntityOtherMultiLinkCreateFluentHelper createTestEntityOtherMultiLink(
        @Nonnull
        final TestEntityOtherMultiLink testEntityOtherMultiLink) {
        return new TestEntityOtherMultiLinkCreateFluentHelper(servicePath, testEntityOtherMultiLink, "A_TestEntityOtherMultiLink");
    }

    @Override
    @Nonnull
    public TestEntityLvl2MultiLinkFluentHelper getAllTestEntityLvl2MultiLink() {
        return new TestEntityLvl2MultiLinkFluentHelper(servicePath, "A_TestEntityLvl2MultiLink");
    }

    @Override
    @Nonnull
    public TestEntityLvl2MultiLinkCreateFluentHelper createTestEntityLvl2MultiLink(
        @Nonnull
        final TestEntityLvl2MultiLink testEntityLvl2MultiLink) {
        return new TestEntityLvl2MultiLinkCreateFluentHelper(servicePath, testEntityLvl2MultiLink, "A_TestEntityLvl2MultiLink");
    }

    @Override
    @Nonnull
    public TestEntityLvl2MultiLinkUpdateFluentHelper updateTestEntityLvl2MultiLink(
        @Nonnull
        final TestEntityLvl2MultiLink testEntityLvl2MultiLink) {
        return new TestEntityLvl2MultiLinkUpdateFluentHelper(servicePath, testEntityLvl2MultiLink, "A_TestEntityLvl2MultiLink");
    }

    @Override
    @Nonnull
    public TestEntitySingleLinkFluentHelper getAllTestEntitySingleLink() {
        return new TestEntitySingleLinkFluentHelper(servicePath, "A_TestEntitySingleLink");
    }

    @Override
    @Nonnull
    public TestEntitySingleLinkCreateFluentHelper createTestEntitySingleLink(
        @Nonnull
        final TestEntitySingleLink testEntitySingleLink) {
        return new TestEntitySingleLinkCreateFluentHelper(servicePath, testEntitySingleLink, "A_TestEntitySingleLink");
    }

    @Override
    @Nonnull
    public TestEntityLvl2SingleLinkFluentHelper getAllTestEntityLvl2SingleLink() {
        return new TestEntityLvl2SingleLinkFluentHelper(servicePath, "A_TestEntityLvl2SingleLink");
    }

    @Override
    @Nonnull
    public MediaEntityFluentHelper getAllMediaEntity() {
        return new MediaEntityFluentHelper(servicePath, "A_MediaEntity");
    }

    @Override
    @Nonnull
    public UnrelatedFluentHelper getAllUnrelated() {
        return new UnrelatedFluentHelper(servicePath, "A_TestEntityEndsWithCollection");
    }

    @Override
    @Nonnull
    public TestFunctionImportNoReturnFluentHelper testFunctionImportNoReturnType() {
        return new TestFunctionImportNoReturnFluentHelper(servicePath);
    }

    @Override
    @Nonnull
    public TestFunctionImportEdmReturnFluentHelper testFunctionImportEdmReturnType() {
        return new TestFunctionImportEdmReturnFluentHelper(servicePath);
    }

    @Override
    @Nonnull
    public TestFunctionImportEdmReturnTypeCollectionFluentHelper testFunctionImportEdmReturnTypeCollection() {
        return new TestFunctionImportEdmReturnTypeCollectionFluentHelper(servicePath);
    }

    @Override
    @Nonnull
    public TestFunctionImportEntityReturnFluentHelper testFunctionImportEntityReturnType() {
        return new TestFunctionImportEntityReturnFluentHelper(servicePath);
    }

    @Override
    @Nonnull
    public TestFunctionImportEntityReturnTypeCollectionFluentHelper testFunctionImportEntityReturnTypeCollection() {
        return new TestFunctionImportEntityReturnTypeCollectionFluentHelper(servicePath);
    }

    @Override
    @Nonnull
    public TestFunctionImportComplexReturnFluentHelper testFunctionImportComplexReturnType() {
        return new TestFunctionImportComplexReturnFluentHelper(servicePath);
    }

    @Override
    @Nonnull
    public TestFunctionImportComplexReturnTypeCollectionFluentHelper testFunctionImportComplexReturnTypeCollection() {
        return new TestFunctionImportComplexReturnTypeCollectionFluentHelper(servicePath);
    }

    @Override
    @Nonnull
    public TestFunctionImportGETFluentHelper testFunctionImportGET(
        @Nonnull
        final String simpleParam) {
        return new TestFunctionImportGETFluentHelper(servicePath, simpleParam);
    }

    @Override
    @Nonnull
    public TestFunctionImportPOSTFluentHelper testFunctionImportPOST(
        @Nonnull
        final String simpleParam) {
        return new TestFunctionImportPOSTFluentHelper(servicePath, simpleParam);
    }

    @Override
    @Nonnull
    public TestFunctionImportMultipleParamsFluentHelper testFunctionImportMultipleParams(
        @Nonnull
        final String stringParam,
        @Nonnull
        final Boolean booleanParam) {
        return new TestFunctionImportMultipleParamsFluentHelper(servicePath, stringParam, booleanParam);
    }

    @Override
    @Nonnull
    public CreateTestComplexFluentHelper createTestComplexType() {
        return new CreateTestComplexFluentHelper(servicePath);
    }

    @Override
    @Nonnull
    public ContinueFluentHelper continueFunction() {
        return new ContinueFluentHelper(servicePath);
    }

}
