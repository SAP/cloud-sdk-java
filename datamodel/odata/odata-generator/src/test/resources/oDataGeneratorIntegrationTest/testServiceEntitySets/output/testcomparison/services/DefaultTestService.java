package testcomparison.services;

import java.util.UUID;
import javax.annotation.Nonnull;
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
import testcomparison.namespaces.test.batch.DefaultTestServiceBatch;


/**
 * <p>Reference: <a href='https://api.sap.com/shell/discover/contentpackage/SAPS4HANACloud/api/test_service?section=OVERVIEW'>SAP Business Accelerator Hub</a></p><h3>Details:</h3><table summary='Details'><tr><td align='right'>OData Service:</td><td>test_service</td></tr></table>
 *
 */
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
    public TestEntityV2FluentHelper getAllTestEntity() {
        return new TestEntityV2FluentHelper(servicePath, "A_TestEntity");
    }

    @Override
    @Nonnull
    public TestEntityV2ByKeyFluentHelper getTestEntityByKey(final UUID keyPropertyGuid, final String keyPropertyString) {
        return new TestEntityV2ByKeyFluentHelper(servicePath, "A_TestEntity", keyPropertyGuid, keyPropertyString);
    }

    @Override
    @Nonnull
    public TestEntityV2CreateFluentHelper createTestEntity(
        @Nonnull
        final TestEntityV2 testEntityV2) {
        return new TestEntityV2CreateFluentHelper(servicePath, testEntityV2, "A_TestEntity");
    }

    @Override
    @Nonnull
    public TestEntityV2UpdateFluentHelper updateTestEntity(
        @Nonnull
        final TestEntityV2 testEntityV2) {
        return new TestEntityV2UpdateFluentHelper(servicePath, testEntityV2, "A_TestEntity");
    }

    @Override
    @Nonnull
    public TestEntityV2DeleteFluentHelper deleteTestEntity(
        @Nonnull
        final TestEntityV2 testEntityV2) {
        return new TestEntityV2DeleteFluentHelper(servicePath, testEntityV2, "A_TestEntity");
    }

    @Override
    @Nonnull
    public TestEntityV2FluentHelper getAllOtherTestEntity() {
        return new TestEntityV2FluentHelper(servicePath, "A_OtherTestEntity");
    }

    @Override
    @Nonnull
    public TestEntityV2ByKeyFluentHelper getOtherTestEntityByKey(final UUID keyPropertyGuid, final String keyPropertyString) {
        return new TestEntityV2ByKeyFluentHelper(servicePath, "A_OtherTestEntity", keyPropertyGuid, keyPropertyString);
    }

    @Override
    @Nonnull
    public TestEntityV2CreateFluentHelper createOtherTestEntity(
        @Nonnull
        final TestEntityV2 testEntityV2) {
        return new TestEntityV2CreateFluentHelper(servicePath, testEntityV2, "A_OtherTestEntity");
    }

    @Override
    @Nonnull
    public TestEntityV2UpdateFluentHelper updateOtherTestEntity(
        @Nonnull
        final TestEntityV2 testEntityV2) {
        return new TestEntityV2UpdateFluentHelper(servicePath, testEntityV2, "A_OtherTestEntity");
    }

    @Override
    @Nonnull
    public TestEntityV2DeleteFluentHelper deleteOtherTestEntity(
        @Nonnull
        final TestEntityV2 testEntityV2) {
        return new TestEntityV2DeleteFluentHelper(servicePath, testEntityV2, "A_OtherTestEntity");
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
