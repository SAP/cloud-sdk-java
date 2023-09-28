package testcomparison.namespaces.sdkgrocerystore.batch;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchFluentHelperBasic;


/**
 * Default implementation of the {@link SdkGroceryStoreServiceBatch} interface exposed in the {@link testcomparison.services.SdkGroceryStoreService SdkGroceryStoreService}, allowing you to create multiple changesets and finally execute the batch request.
 *
 */
public class DefaultSdkGroceryStoreServiceBatch
    extends BatchFluentHelperBasic<SdkGroceryStoreServiceBatch, SdkGroceryStoreServiceBatchChangeSet>
    implements SdkGroceryStoreServiceBatch
{

    @Nonnull
    private final testcomparison.services.SdkGroceryStoreService service;
    @Nonnull
    private final String servicePath;

    /**
     * Creates a new instance of this DefaultSdkGroceryStoreServiceBatch.
     *
     * @param service
     *     The service to execute all operations in this changeset on.
     */
    public DefaultSdkGroceryStoreServiceBatch(
        @Nonnull
        final testcomparison.services.SdkGroceryStoreService service) {
        this(service, testcomparison.services.SdkGroceryStoreService.DEFAULT_SERVICE_PATH);
    }

    /**
     * Creates a new instance of this DefaultSdkGroceryStoreServiceBatch.
     *
     * @param service
     *     The service to execute all operations in this changeset on.
     * @param servicePath
     *     The custom service path to operate on.
     */
    public DefaultSdkGroceryStoreServiceBatch(
        @Nonnull
        final testcomparison.services.SdkGroceryStoreService service,
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
    protected DefaultSdkGroceryStoreServiceBatch getThis() {
        return this;
    }

    @Nonnull
    @Override
    public SdkGroceryStoreServiceBatchChangeSet beginChangeSet() {
        return new DefaultSdkGroceryStoreServiceBatchChangeSet(this, service);
    }

}
