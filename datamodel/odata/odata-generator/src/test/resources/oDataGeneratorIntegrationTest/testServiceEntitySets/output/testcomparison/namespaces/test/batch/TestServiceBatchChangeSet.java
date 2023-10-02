/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test.batch;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.FluentHelperBatchChangeSet;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.FluentHelperBatchEndChangeSet;
import testcomparison.namespaces.test.TestEntityV2;


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
    TestServiceBatchChangeSet createTestEntity(
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
    TestServiceBatchChangeSet updateTestEntity(
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
    TestServiceBatchChangeSet deleteTestEntity(
        @Nonnull
        final TestEntityV2 testEntityV2);

    /**
     * Create a new {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity and save it to the S/4HANA system.
     * 
     * @param testEntityV2
     *     {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity object that will be created in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    TestServiceBatchChangeSet createOtherTestEntity(
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
    TestServiceBatchChangeSet updateOtherTestEntity(
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
    TestServiceBatchChangeSet deleteOtherTestEntity(
        @Nonnull
        final TestEntityV2 testEntityV2);

}
