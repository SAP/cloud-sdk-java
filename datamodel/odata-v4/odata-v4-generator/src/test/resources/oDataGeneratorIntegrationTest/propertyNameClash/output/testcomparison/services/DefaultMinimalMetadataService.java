/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.services;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odatav4.core.BatchRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.ServiceWithNavigableEntities;
import com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder;
import lombok.Getter;
import testcomparison.namespaces.minimalmetadata.SimplePerson;


/**
 * <p>You can use this inbound synchronous service to create, read and update.</p><p><a href='https://sap.com'>Business Documentation</a></p><h3>Details:</h3><table summary='Details'><tr><td align='right'>OData Service:</td><td>name-clash</td></tr><tr><td align='right'>API Version:</td><td>1 </td></tr><tr><td align='right'>Communication Scenario:</td><td>minimal metadata</td></tr><tr><td align='right'>Scope Items:</td><td><a href='[API_BUSINESS_PARTNER_Entities](https://sap.com)'>[API_BUSINESS_PARTNER_Entities](https://sap.com)</a></td></tr><tr><td align='right'>Authentication Methods:</td><td>Basic, x509</td></tr><tr><td align='right'>Service Group Name:</td><td>API_BUSINESS_PARTNER</td></tr><tr><td align='right'>Business Object:</td><td>SimplePerson</td></tr></table>
 * 
 */
public class DefaultMinimalMetadataService
    implements ServiceWithNavigableEntities, MinimalMetadataService
{

    @Nonnull
    @Getter
    private final String servicePath;

    /**
     * Creates a service using {@link MinimalMetadataService#DEFAULT_SERVICE_PATH} to send the requests.
     * 
     */
    public DefaultMinimalMetadataService() {
        servicePath = MinimalMetadataService.DEFAULT_SERVICE_PATH;
    }

    /**
     * Creates a service using the provided service path to send the requests.
     * <p>
     * Used by the fluent {@link #withServicePath(String)} method.
     * 
     */
    private DefaultMinimalMetadataService(
        @Nonnull
        final String servicePath) {
        this.servicePath = servicePath;
    }

    @Override
    @Nonnull
    public DefaultMinimalMetadataService withServicePath(
        @Nonnull
        final String servicePath) {
        return new DefaultMinimalMetadataService(servicePath);
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
    public GetByKeyRequestBuilder<SimplePerson> getSimplePersonsByKey(final String person) {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("Person", person);
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

}
