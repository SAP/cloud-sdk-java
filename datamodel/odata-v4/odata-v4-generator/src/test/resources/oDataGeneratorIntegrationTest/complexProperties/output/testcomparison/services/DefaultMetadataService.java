/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.services;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odatav4.core.BatchRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.ServiceWithNavigableEntities;
import com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder;
import lombok.Getter;
import testcomparison.namespaces.metadata.SimplePerson;


/**
 * <h3>Details:</h3><table summary='Details'><tr><td align='right'>OData Service:</td><td>metadata</td></tr></table>
 * 
 */
public class DefaultMetadataService
    implements ServiceWithNavigableEntities, MetadataService
{

    @Nonnull
    @Getter
    private final String servicePath;

    /**
     * Creates a service using {@link MetadataService#DEFAULT_SERVICE_PATH} to send the requests.
     * 
     */
    public DefaultMetadataService() {
        servicePath = MetadataService.DEFAULT_SERVICE_PATH;
    }

    /**
     * Creates a service using the provided service path to send the requests.
     * <p>
     * Used by the fluent {@link #withServicePath(String)} method.
     * 
     */
    private DefaultMetadataService(
        @Nonnull
        final String servicePath) {
        this.servicePath = servicePath;
    }

    @Override
    @Nonnull
    public DefaultMetadataService withServicePath(
        @Nonnull
        final String servicePath) {
        return new DefaultMetadataService(servicePath);
    }

    @Override
    @Nonnull
    public BatchRequestBuilder batch() {
        return new BatchRequestBuilder(servicePath);
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<SimplePerson> getAllSimplePersons() {
        return new GetAllRequestBuilder<SimplePerson>(servicePath, SimplePerson.class, "A_SimplePersons");
    }

    @Override
    @Nonnull
    public CountRequestBuilder<SimplePerson> countSimplePersons() {
        return new CountRequestBuilder<SimplePerson>(servicePath, SimplePerson.class, "A_SimplePersons");
    }

    @Override
    @Nonnull
    public GetByKeyRequestBuilder<SimplePerson> getSimplePersonsByKey(final String firstName, final String lastName) {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("FirstName", firstName);
        key.put("LastName", lastName);
        return new GetByKeyRequestBuilder<SimplePerson>(servicePath, SimplePerson.class, key, "A_SimplePersons");
    }

    @Override
    @Nonnull
    public CreateRequestBuilder<SimplePerson> createSimplePersons(
        @Nonnull
        final SimplePerson simplePerson) {
        return new CreateRequestBuilder<SimplePerson>(servicePath, simplePerson, "A_SimplePersons");
    }

    @Override
    @Nonnull
    public UpdateRequestBuilder<SimplePerson> updateSimplePersons(
        @Nonnull
        final SimplePerson simplePerson) {
        return new UpdateRequestBuilder<SimplePerson>(servicePath, simplePerson, "A_SimplePersons");
    }

    @Override
    @Nonnull
    public DeleteRequestBuilder<SimplePerson> deleteSimplePersons(
        @Nonnull
        final SimplePerson simplePerson) {
        return new DeleteRequestBuilder<SimplePerson>(servicePath, simplePerson, "A_SimplePersons");
    }

}
