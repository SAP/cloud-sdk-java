/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test.batch;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.FluentHelperBatchChangeSet;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.FluentHelperBatchEndChangeSet;
import testcomparison.namespaces.test.MediaEntity;
import testcomparison.namespaces.test.TestEntityLvl2MultiLink;
import testcomparison.namespaces.test.TestEntityLvl2SingleLink;
import testcomparison.namespaces.test.TestEntityMultiLink;
import testcomparison.namespaces.test.TestEntityOtherMultiLink;
import testcomparison.namespaces.test.TestEntitySingleLink;
import testcomparison.namespaces.test.TestEntityV2;
import testcomparison.namespaces.test.Unrelated;


/**
 * This interface enables you to combine multiple operations into one change set. For further information have a look into the {@link testcomparison.services.TestService TestService}.
 * 
 */
public interface TestServiceBatchChangeSet
    extends FluentHelperBatchChangeSet<TestServiceBatchChangeSet> , FluentHelperBatchEndChangeSet<TestServiceBatch>
{


    /**
     * Create a new {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity and save it to the S/4HANA system.
     * 
     * @param testEntityV2
     *     {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity object that will be created in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    TestServiceBatchChangeSet createTestEntityV2(
        @Nonnull
        final TestEntityV2 testEntityV2);

    /**
     * Update an existing {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity and save it to the S/4HANA system.
     * 
     * @param testEntityV2
     *     {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity object that will be updated in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    TestServiceBatchChangeSet updateTestEntityV2(
        @Nonnull
        final TestEntityV2 testEntityV2);

    /**
     * Deletes an existing {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity in the S/4HANA system.
     * 
     * @param testEntityV2
     *     {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity object that will be deleted in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    TestServiceBatchChangeSet deleteTestEntityV2(
        @Nonnull
        final TestEntityV2 testEntityV2);

    /**
     * Create a new {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entity and save it to the S/4HANA system.
     * 
     * @param testEntityMultiLink
     *     {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entity object that will be created in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    TestServiceBatchChangeSet createTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink);

    /**
     * Update an existing {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entity and save it to the S/4HANA system.
     * 
     * @param testEntityMultiLink
     *     {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entity object that will be updated in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    TestServiceBatchChangeSet updateTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink);

    /**
     * Deletes an existing {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entity in the S/4HANA system.
     * 
     * @param testEntityMultiLink
     *     {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} entity object that will be deleted in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    TestServiceBatchChangeSet deleteTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink);

    /**
     * Create a new {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity and save it to the S/4HANA system.
     * 
     * @param testEntityOtherMultiLink
     *     {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity object that will be created in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    TestServiceBatchChangeSet createTestEntityOtherMultiLink(
        @Nonnull
        final TestEntityOtherMultiLink testEntityOtherMultiLink);

    /**
     * Update an existing {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity and save it to the S/4HANA system.
     * 
     * @param testEntityOtherMultiLink
     *     {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity object that will be updated in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    TestServiceBatchChangeSet updateTestEntityOtherMultiLink(
        @Nonnull
        final TestEntityOtherMultiLink testEntityOtherMultiLink);

    /**
     * Deletes an existing {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity in the S/4HANA system.
     * 
     * @param testEntityOtherMultiLink
     *     {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity object that will be deleted in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    TestServiceBatchChangeSet deleteTestEntityOtherMultiLink(
        @Nonnull
        final TestEntityOtherMultiLink testEntityOtherMultiLink);

    /**
     * Create a new {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity and save it to the S/4HANA system.
     * 
     * @param testEntityLvl2MultiLink
     *     {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity object that will be created in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    TestServiceBatchChangeSet createTestEntityLvl2MultiLink(
        @Nonnull
        final TestEntityLvl2MultiLink testEntityLvl2MultiLink);

    /**
     * Update an existing {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity and save it to the S/4HANA system.
     * 
     * @param testEntityLvl2MultiLink
     *     {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity object that will be updated in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    TestServiceBatchChangeSet updateTestEntityLvl2MultiLink(
        @Nonnull
        final TestEntityLvl2MultiLink testEntityLvl2MultiLink);

    /**
     * Deletes an existing {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity in the S/4HANA system.
     * 
     * @param testEntityLvl2MultiLink
     *     {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity object that will be deleted in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    TestServiceBatchChangeSet deleteTestEntityLvl2MultiLink(
        @Nonnull
        final TestEntityLvl2MultiLink testEntityLvl2MultiLink);

    /**
     * Create a new {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entity and save it to the S/4HANA system.
     * 
     * @param testEntitySingleLink
     *     {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entity object that will be created in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    TestServiceBatchChangeSet createTestEntitySingleLink(
        @Nonnull
        final TestEntitySingleLink testEntitySingleLink);

    /**
     * Update an existing {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entity and save it to the S/4HANA system.
     * 
     * @param testEntitySingleLink
     *     {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entity object that will be updated in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    TestServiceBatchChangeSet updateTestEntitySingleLink(
        @Nonnull
        final TestEntitySingleLink testEntitySingleLink);

    /**
     * Deletes an existing {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entity in the S/4HANA system.
     * 
     * @param testEntitySingleLink
     *     {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink} entity object that will be deleted in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    TestServiceBatchChangeSet deleteTestEntitySingleLink(
        @Nonnull
        final TestEntitySingleLink testEntitySingleLink);

    /**
     * Create a new {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity and save it to the S/4HANA system.
     * 
     * @param testEntityLvl2SingleLink
     *     {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity object that will be created in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    TestServiceBatchChangeSet createTestEntityLvl2SingleLink(
        @Nonnull
        final TestEntityLvl2SingleLink testEntityLvl2SingleLink);

    /**
     * Update an existing {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity and save it to the S/4HANA system.
     * 
     * @param testEntityLvl2SingleLink
     *     {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity object that will be updated in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    TestServiceBatchChangeSet updateTestEntityLvl2SingleLink(
        @Nonnull
        final TestEntityLvl2SingleLink testEntityLvl2SingleLink);

    /**
     * Deletes an existing {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity in the S/4HANA system.
     * 
     * @param testEntityLvl2SingleLink
     *     {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity object that will be deleted in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    TestServiceBatchChangeSet deleteTestEntityLvl2SingleLink(
        @Nonnull
        final TestEntityLvl2SingleLink testEntityLvl2SingleLink);

    /**
     * Create a new {@link testcomparison.namespaces.test.MediaEntity MediaEntity} entity and save it to the S/4HANA system.
     * 
     * @param mediaEntity
     *     {@link testcomparison.namespaces.test.MediaEntity MediaEntity} entity object that will be created in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    TestServiceBatchChangeSet createMediaEntity(
        @Nonnull
        final MediaEntity mediaEntity);

    /**
     * Update an existing {@link testcomparison.namespaces.test.MediaEntity MediaEntity} entity and save it to the S/4HANA system.
     * 
     * @param mediaEntity
     *     {@link testcomparison.namespaces.test.MediaEntity MediaEntity} entity object that will be updated in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    TestServiceBatchChangeSet updateMediaEntity(
        @Nonnull
        final MediaEntity mediaEntity);

    /**
     * Deletes an existing {@link testcomparison.namespaces.test.MediaEntity MediaEntity} entity in the S/4HANA system.
     * 
     * @param mediaEntity
     *     {@link testcomparison.namespaces.test.MediaEntity MediaEntity} entity object that will be deleted in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    TestServiceBatchChangeSet deleteMediaEntity(
        @Nonnull
        final MediaEntity mediaEntity);

    /**
     * Create a new {@link testcomparison.namespaces.test.Unrelated Unrelated} entity and save it to the S/4HANA system.
     * 
     * @param unrelated
     *     {@link testcomparison.namespaces.test.Unrelated Unrelated} entity object that will be created in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    TestServiceBatchChangeSet createUnrelated(
        @Nonnull
        final Unrelated unrelated);

    /**
     * Update an existing {@link testcomparison.namespaces.test.Unrelated Unrelated} entity and save it to the S/4HANA system.
     * 
     * @param unrelated
     *     {@link testcomparison.namespaces.test.Unrelated Unrelated} entity object that will be updated in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    TestServiceBatchChangeSet updateUnrelated(
        @Nonnull
        final Unrelated unrelated);

    /**
     * Deletes an existing {@link testcomparison.namespaces.test.Unrelated Unrelated} entity in the S/4HANA system.
     * 
     * @param unrelated
     *     {@link testcomparison.namespaces.test.Unrelated Unrelated} entity object that will be deleted in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    TestServiceBatchChangeSet deleteUnrelated(
        @Nonnull
        final Unrelated unrelated);

}
