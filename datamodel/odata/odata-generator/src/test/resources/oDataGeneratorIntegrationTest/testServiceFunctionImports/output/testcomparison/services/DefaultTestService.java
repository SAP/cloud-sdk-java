/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.services;

import java.util.UUID;
import javax.annotation.Nonnull;
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
    public TestEntityV2FluentHelper getAllTestEntityV2() {
        return new TestEntityV2FluentHelper(servicePath, "A_TestEntity");
    }

    @Override
    @Nonnull
    public TestEntityV2ByKeyFluentHelper getTestEntityV2ByKey(final UUID keyPropertyGuid, final String keyPropertyString) {
        return new TestEntityV2ByKeyFluentHelper(servicePath, "A_TestEntity", keyPropertyGuid, keyPropertyString);
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
    public TestEntityV2UpdateFluentHelper updateTestEntityV2(
        @Nonnull
        final TestEntityV2 testEntityV2) {
        return new TestEntityV2UpdateFluentHelper(servicePath, testEntityV2, "A_TestEntity");
    }

    @Override
    @Nonnull
    public TestEntityV2DeleteFluentHelper deleteTestEntityV2(
        @Nonnull
        final TestEntityV2 testEntityV2) {
        return new TestEntityV2DeleteFluentHelper(servicePath, testEntityV2, "A_TestEntity");
    }

    @Override
    @Nonnull
    public TestEntityMultiLinkFluentHelper getAllTestEntityMultiLink() {
        return new TestEntityMultiLinkFluentHelper(servicePath, "A_TestEntityMultiLink");
    }

    @Override
    @Nonnull
    public TestEntityMultiLinkByKeyFluentHelper getTestEntityMultiLinkByKey(final String keyProperty) {
        return new TestEntityMultiLinkByKeyFluentHelper(servicePath, "A_TestEntityMultiLink", keyProperty);
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
    public TestEntityMultiLinkUpdateFluentHelper updateTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink) {
        return new TestEntityMultiLinkUpdateFluentHelper(servicePath, testEntityMultiLink, "A_TestEntityMultiLink");
    }

    @Override
    @Nonnull
    public TestEntityMultiLinkDeleteFluentHelper deleteTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink) {
        return new TestEntityMultiLinkDeleteFluentHelper(servicePath, testEntityMultiLink, "A_TestEntityMultiLink");
    }

    @Override
    @Nonnull
    public TestEntityOtherMultiLinkFluentHelper getAllTestEntityOtherMultiLink() {
        return new TestEntityOtherMultiLinkFluentHelper(servicePath, "A_TestEntityOtherMultiLink");
    }

    @Override
    @Nonnull
    public TestEntityOtherMultiLinkByKeyFluentHelper getTestEntityOtherMultiLinkByKey(final String keyProperty) {
        return new TestEntityOtherMultiLinkByKeyFluentHelper(servicePath, "A_TestEntityOtherMultiLink", keyProperty);
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
    public TestEntityOtherMultiLinkUpdateFluentHelper updateTestEntityOtherMultiLink(
        @Nonnull
        final TestEntityOtherMultiLink testEntityOtherMultiLink) {
        return new TestEntityOtherMultiLinkUpdateFluentHelper(servicePath, testEntityOtherMultiLink, "A_TestEntityOtherMultiLink");
    }

    @Override
    @Nonnull
    public TestEntityOtherMultiLinkDeleteFluentHelper deleteTestEntityOtherMultiLink(
        @Nonnull
        final TestEntityOtherMultiLink testEntityOtherMultiLink) {
        return new TestEntityOtherMultiLinkDeleteFluentHelper(servicePath, testEntityOtherMultiLink, "A_TestEntityOtherMultiLink");
    }

    @Override
    @Nonnull
    public TestEntityLvl2MultiLinkFluentHelper getAllTestEntityLvl2MultiLink() {
        return new TestEntityLvl2MultiLinkFluentHelper(servicePath, "A_TestEntityLvl2MultiLink");
    }

    @Override
    @Nonnull
    public TestEntityLvl2MultiLinkByKeyFluentHelper getTestEntityLvl2MultiLinkByKey(final String keyProperty) {
        return new TestEntityLvl2MultiLinkByKeyFluentHelper(servicePath, "A_TestEntityLvl2MultiLink", keyProperty);
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
    public TestEntityLvl2MultiLinkDeleteFluentHelper deleteTestEntityLvl2MultiLink(
        @Nonnull
        final TestEntityLvl2MultiLink testEntityLvl2MultiLink) {
        return new TestEntityLvl2MultiLinkDeleteFluentHelper(servicePath, testEntityLvl2MultiLink, "A_TestEntityLvl2MultiLink");
    }

    @Override
    @Nonnull
    public TestEntitySingleLinkFluentHelper getAllTestEntitySingleLink() {
        return new TestEntitySingleLinkFluentHelper(servicePath, "A_TestEntitySingleLink");
    }

    @Override
    @Nonnull
    public TestEntitySingleLinkByKeyFluentHelper getTestEntitySingleLinkByKey(final String keyProperty) {
        return new TestEntitySingleLinkByKeyFluentHelper(servicePath, "A_TestEntitySingleLink", keyProperty);
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
    public TestEntitySingleLinkUpdateFluentHelper updateTestEntitySingleLink(
        @Nonnull
        final TestEntitySingleLink testEntitySingleLink) {
        return new TestEntitySingleLinkUpdateFluentHelper(servicePath, testEntitySingleLink, "A_TestEntitySingleLink");
    }

    @Override
    @Nonnull
    public TestEntitySingleLinkDeleteFluentHelper deleteTestEntitySingleLink(
        @Nonnull
        final TestEntitySingleLink testEntitySingleLink) {
        return new TestEntitySingleLinkDeleteFluentHelper(servicePath, testEntitySingleLink, "A_TestEntitySingleLink");
    }

    @Override
    @Nonnull
    public TestEntityLvl2SingleLinkFluentHelper getAllTestEntityLvl2SingleLink() {
        return new TestEntityLvl2SingleLinkFluentHelper(servicePath, "A_TestEntityLvl2SingleLink");
    }

    @Override
    @Nonnull
    public TestEntityLvl2SingleLinkByKeyFluentHelper getTestEntityLvl2SingleLinkByKey(final String keyProperty) {
        return new TestEntityLvl2SingleLinkByKeyFluentHelper(servicePath, "A_TestEntityLvl2SingleLink", keyProperty);
    }

    @Override
    @Nonnull
    public TestEntityLvl2SingleLinkCreateFluentHelper createTestEntityLvl2SingleLink(
        @Nonnull
        final TestEntityLvl2SingleLink testEntityLvl2SingleLink) {
        return new TestEntityLvl2SingleLinkCreateFluentHelper(servicePath, testEntityLvl2SingleLink, "A_TestEntityLvl2SingleLink");
    }

    @Override
    @Nonnull
    public TestEntityLvl2SingleLinkUpdateFluentHelper updateTestEntityLvl2SingleLink(
        @Nonnull
        final TestEntityLvl2SingleLink testEntityLvl2SingleLink) {
        return new TestEntityLvl2SingleLinkUpdateFluentHelper(servicePath, testEntityLvl2SingleLink, "A_TestEntityLvl2SingleLink");
    }

    @Override
    @Nonnull
    public TestEntityLvl2SingleLinkDeleteFluentHelper deleteTestEntityLvl2SingleLink(
        @Nonnull
        final TestEntityLvl2SingleLink testEntityLvl2SingleLink) {
        return new TestEntityLvl2SingleLinkDeleteFluentHelper(servicePath, testEntityLvl2SingleLink, "A_TestEntityLvl2SingleLink");
    }

    @Override
    @Nonnull
    public MediaEntityFluentHelper getAllMediaEntity() {
        return new MediaEntityFluentHelper(servicePath, "A_MediaEntity");
    }

    @Override
    @Nonnull
    public MediaEntityByKeyFluentHelper getMediaEntityByKey(final String keyProperty) {
        return new MediaEntityByKeyFluentHelper(servicePath, "A_MediaEntity", keyProperty);
    }

    @Override
    @Nonnull
    public MediaEntityCreateFluentHelper createMediaEntity(
        @Nonnull
        final MediaEntity mediaEntity) {
        return new MediaEntityCreateFluentHelper(servicePath, mediaEntity, "A_MediaEntity");
    }

    @Override
    @Nonnull
    public MediaEntityUpdateFluentHelper updateMediaEntity(
        @Nonnull
        final MediaEntity mediaEntity) {
        return new MediaEntityUpdateFluentHelper(servicePath, mediaEntity, "A_MediaEntity");
    }

    @Override
    @Nonnull
    public MediaEntityDeleteFluentHelper deleteMediaEntity(
        @Nonnull
        final MediaEntity mediaEntity) {
        return new MediaEntityDeleteFluentHelper(servicePath, mediaEntity, "A_MediaEntity");
    }

    @Override
    @Nonnull
    public UnrelatedFluentHelper getAllUnrelated() {
        return new UnrelatedFluentHelper(servicePath, "A_TestEntityEndsWithCollection");
    }

    @Override
    @Nonnull
    public UnrelatedByKeyFluentHelper getUnrelatedByKey(final String keyProperty) {
        return new UnrelatedByKeyFluentHelper(servicePath, "A_TestEntityEndsWithCollection", keyProperty);
    }

    @Override
    @Nonnull
    public UnrelatedCreateFluentHelper createUnrelated(
        @Nonnull
        final Unrelated unrelated) {
        return new UnrelatedCreateFluentHelper(servicePath, unrelated, "A_TestEntityEndsWithCollection");
    }

    @Override
    @Nonnull
    public UnrelatedUpdateFluentHelper updateUnrelated(
        @Nonnull
        final Unrelated unrelated) {
        return new UnrelatedUpdateFluentHelper(servicePath, unrelated, "A_TestEntityEndsWithCollection");
    }

    @Override
    @Nonnull
    public UnrelatedDeleteFluentHelper deleteUnrelated(
        @Nonnull
        final Unrelated unrelated) {
        return new UnrelatedDeleteFluentHelper(servicePath, unrelated, "A_TestEntityEndsWithCollection");
    }

    @Override
    @Nonnull
    public TestFunctionImportNoReturnFluentHelper testFunctionImportNoReturnType() {
        return new TestFunctionImportNoReturnFluentHelper(servicePath);
    }

}
