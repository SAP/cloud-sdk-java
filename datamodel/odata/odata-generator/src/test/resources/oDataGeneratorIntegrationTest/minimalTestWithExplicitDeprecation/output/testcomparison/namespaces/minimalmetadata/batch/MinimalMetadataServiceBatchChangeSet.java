/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.minimalmetadata.batch;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.FluentHelperBatchChangeSet;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.FluentHelperBatchEndChangeSet;
import testcomparison.namespaces.minimalmetadata.SimplePerson;


/**
 * This interface enables you to combine multiple operations into one change set. For further information have a look into the {@link testcomparison.services.MinimalMetadataService MinimalMetadataService}.
 * 
 */
public interface MinimalMetadataServiceBatchChangeSet
    extends FluentHelperBatchChangeSet<MinimalMetadataServiceBatchChangeSet> , FluentHelperBatchEndChangeSet<MinimalMetadataServiceBatch>
{


    /**
     * Create a new {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity and save it to the S/4HANA system.
     * 
     * @param simplePerson
     *     {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity object that will be created in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    MinimalMetadataServiceBatchChangeSet createSimplePerson(
        @Nonnull
        final SimplePerson simplePerson);

    /**
     * Update an existing {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity and save it to the S/4HANA system.
     * 
     * @param simplePerson
     *     {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity object that will be updated in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    MinimalMetadataServiceBatchChangeSet updateSimplePerson(
        @Nonnull
        final SimplePerson simplePerson);

    /**
     * Deletes an existing {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity in the S/4HANA system.
     * 
     * @param simplePerson
     *     {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity object that will be deleted in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    MinimalMetadataServiceBatchChangeSet deleteSimplePerson(
        @Nonnull
        final SimplePerson simplePerson);

}
