package testcomparison.namespaces.multipleentitysets.batch;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchFluentHelperBasic;


/**
 * Default implementation of the {@link MultipleEntitySetsServiceBatch} interface exposed in the {@link testcomparison.services.MultipleEntitySetsService MultipleEntitySetsService}, allowing you to create multiple changesets and finally execute the batch request.
 *
 */
public class DefaultMultipleEntitySetsServiceBatch
    extends BatchFluentHelperBasic<MultipleEntitySetsServiceBatch, MultipleEntitySetsServiceBatchChangeSet>
    implements MultipleEntitySetsServiceBatch
{

    @Nonnull
    private final testcomparison.services.MultipleEntitySetsService service;
    @Nonnull
    private final String servicePath;

    /**
     * Creates a new instance of this DefaultMultipleEntitySetsServiceBatch.
     *
     * @param service
     *     The service to execute all operations in this changeset on.
     */
    public DefaultMultipleEntitySetsServiceBatch(
        @Nonnull
        final testcomparison.services.MultipleEntitySetsService service) {
        this(service, testcomparison.services.MultipleEntitySetsService.DEFAULT_SERVICE_PATH);
    }

    /**
     * Creates a new instance of this DefaultMultipleEntitySetsServiceBatch.
     *
     * @param service
     *     The service to execute all operations in this changeset on.
     * @param servicePath
     *     The custom service path to operate on.
     */
    public DefaultMultipleEntitySetsServiceBatch(
        @Nonnull
        final testcomparison.services.MultipleEntitySetsService service,
        @Nonnull
        final String servicePath) {
        this.service = service;
        this.servicePath = servicePath;
    }

    @Nonnull
    @Override
    protected String getServicePathForBatchRequest() {
        return servicePath;
    }

    @Nonnull
    @Override
    protected DefaultMultipleEntitySetsServiceBatch getThis() {
        return this;
    }

    @Nonnull
    @Override
    public MultipleEntitySetsServiceBatchChangeSet beginChangeSet() {
        return new DefaultMultipleEntitySetsServiceBatchChangeSet(this, service);
    }

}
