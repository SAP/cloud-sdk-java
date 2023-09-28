package testcomparison.namespaces.test.batch;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.FluentHelperBatchChangeSet;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.FluentHelperBatchEndChangeSet;
import testcomparison.namespaces.test.TestEntityLvl2MultiLink;
import testcomparison.namespaces.test.TestEntityMultiLink;
import testcomparison.namespaces.test.TestEntityOtherMultiLink;
import testcomparison.namespaces.test.TestEntitySingleLink;
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
    TestServiceBatchChangeSet createTestEntityV2(
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

}
