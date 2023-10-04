/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.nameclash.batch;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.FluentHelperBatchChangeSet;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.FluentHelperBatchEndChangeSet;
import testcomparison.namespaces.nameclash.TestEntityMultiLink;
import testcomparison.namespaces.nameclash.TestEntityV2;


/**
 * This interface enables you to combine multiple operations into one change set. For further information have a look into the {@link testcomparison.services.NameClashService NameClashService}.
 * 
 */
public interface NameClashServiceBatchChangeSet
    extends FluentHelperBatchChangeSet<NameClashServiceBatchChangeSet> , FluentHelperBatchEndChangeSet<NameClashServiceBatch>
{


    /**
     * Create a new {@link testcomparison.namespaces.nameclash.TestEntityV2 TestEntityV2} entity and save it to the S/4HANA system.
     * 
     * @param testEntityV2
     *     {@link testcomparison.namespaces.nameclash.TestEntityV2 TestEntityV2} entity object that will be created in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    NameClashServiceBatchChangeSet createTestEntity(
        @Nonnull
        final TestEntityV2 testEntityV2);

    /**
     * Update an existing {@link testcomparison.namespaces.nameclash.TestEntityV2 TestEntityV2} entity and save it to the S/4HANA system.
     * 
     * @param testEntityV2
     *     {@link testcomparison.namespaces.nameclash.TestEntityV2 TestEntityV2} entity object that will be updated in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    NameClashServiceBatchChangeSet updateTestEntity(
        @Nonnull
        final TestEntityV2 testEntityV2);

    /**
     * Deletes an existing {@link testcomparison.namespaces.nameclash.TestEntityV2 TestEntityV2} entity in the S/4HANA system.
     * 
     * @param testEntityV2
     *     {@link testcomparison.namespaces.nameclash.TestEntityV2 TestEntityV2} entity object that will be deleted in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    NameClashServiceBatchChangeSet deleteTestEntity(
        @Nonnull
        final TestEntityV2 testEntityV2);

    /**
     * Create a new {@link testcomparison.namespaces.nameclash.TestEntityMultiLink TestEntityMultiLink} entity and save it to the S/4HANA system.
     * 
     * @param testEntityMultiLink
     *     {@link testcomparison.namespaces.nameclash.TestEntityMultiLink TestEntityMultiLink} entity object that will be created in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    NameClashServiceBatchChangeSet createTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink);

    /**
     * Update an existing {@link testcomparison.namespaces.nameclash.TestEntityMultiLink TestEntityMultiLink} entity and save it to the S/4HANA system.
     * 
     * @param testEntityMultiLink
     *     {@link testcomparison.namespaces.nameclash.TestEntityMultiLink TestEntityMultiLink} entity object that will be updated in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    NameClashServiceBatchChangeSet updateTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink);

    /**
     * Deletes an existing {@link testcomparison.namespaces.nameclash.TestEntityMultiLink TestEntityMultiLink} entity in the S/4HANA system.
     * 
     * @param testEntityMultiLink
     *     {@link testcomparison.namespaces.nameclash.TestEntityMultiLink TestEntityMultiLink} entity object that will be deleted in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    NameClashServiceBatchChangeSet deleteTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink);

}
