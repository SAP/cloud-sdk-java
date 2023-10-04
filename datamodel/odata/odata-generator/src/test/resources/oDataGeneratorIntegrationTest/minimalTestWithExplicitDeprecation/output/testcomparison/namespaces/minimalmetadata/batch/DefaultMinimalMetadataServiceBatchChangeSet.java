/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.minimalmetadata.batch;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchChangeSetFluentHelperBasic;
import testcomparison.namespaces.minimalmetadata.SimplePerson;


/**
 * Implementation of the {@link MinimalMetadataServiceBatchChangeSet} interface, enabling you to combine multiple operations into one changeset. For further information have a look into the {@link testcomparison.services.MinimalMetadataService MinimalMetadataService}.
 * 
 */
public class DefaultMinimalMetadataServiceBatchChangeSet
    extends BatchChangeSetFluentHelperBasic<MinimalMetadataServiceBatch, MinimalMetadataServiceBatchChangeSet>
    implements MinimalMetadataServiceBatchChangeSet
{

    @Nonnull
    @SuppressWarnings("deprecation")
    private final testcomparison.services.MinimalMetadataService service;

    @SuppressWarnings("deprecation")
    DefaultMinimalMetadataServiceBatchChangeSet(
        @Nonnull
        final DefaultMinimalMetadataServiceBatch batchFluentHelper,
        @Nonnull
        final testcomparison.services.MinimalMetadataService service) {
        super(batchFluentHelper, batchFluentHelper);
        this.service = service;
    }

    @Nonnull
    @Override
    protected DefaultMinimalMetadataServiceBatchChangeSet getThis() {
        return this;
    }

    @Nonnull
    @Override
    public MinimalMetadataServiceBatchChangeSet createSimplePerson(
        @Nonnull
        final SimplePerson simplePerson) {
        return addRequestCreate(service::createSimplePerson, simplePerson);
    }

    @Nonnull
    @Override
    public MinimalMetadataServiceBatchChangeSet updateSimplePerson(
        @Nonnull
        final SimplePerson simplePerson) {
        return addRequestUpdate(service::updateSimplePerson, simplePerson);
    }

    @Nonnull
    @Override
    public MinimalMetadataServiceBatchChangeSet deleteSimplePerson(
        @Nonnull
        final SimplePerson simplePerson) {
        return addRequestDelete(service::deleteSimplePerson, simplePerson);
    }

}
