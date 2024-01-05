/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.services;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odatav4.core.BatchRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder;
import testcomparison.namespaces.multipleentitysets.FooType;
import testcomparison.namespaces.multipleentitysets.SimplePerson;


/**
 * <h3>Details:</h3><table summary='Details'><tr><td align='right'>OData Service:</td><td>multiple_entity_sets</td></tr></table>
 * 
 */
public interface MultipleEntitySetsService {

    /**
     * If no other path was provided via the {@link #withServicePath(String)} method, this is the default service path used to access the endpoint.
     * 
     */
    String DEFAULT_SERVICE_PATH = "/tests/multipleEntitySets/Schema";

    /**
     * Overrides the default service path and returns a new service instance with the specified service path. Also adjusts the respective entity URLs.
     * 
     * @param servicePath
     *     Service path that will override the default.
     * @return
     *     A new service instance with the specified service path.
     */
    @Nonnull
    MultipleEntitySetsService withServicePath(
        @Nonnull
        final String servicePath);

    /**
     * Creates a batch request builder object.
     * 
     * @return
     *     A request builder to handle batch operation on this service. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.BatchRequestBuilder#execute(Destination) execute} method on the request builder object.
     */
    @Nonnull
    BatchRequestBuilder batch();

    /**
     * Fetch multiple {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entities.
     * 
     * @return
     *     A request builder to fetch multiple {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<testcomparison.namespaces.multipleentitysets.SimplePerson>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetAllRequestBuilder<SimplePerson> getAllFirstSimplePersons();

    /**
     * Fetch the number of entries from the {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity collection matching the filter and search expressions.
     * 
     * @return
     *     A request builder to fetch the count of {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<testcomparison.namespaces.multipleentitysets.SimplePerson>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CountRequestBuilder<SimplePerson> countFirstSimplePersons();

    /**
     * Fetch a single {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity using key fields.
     * 
     * @param person
     *     <p>Constraints: Not nullable, Maximum length: 10</p>
     * @return
     *     A request builder to fetch a single {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity using key fields. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<testcomparison.namespaces.multipleentitysets.SimplePerson>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetByKeyRequestBuilder<SimplePerson> getFirstSimplePersonsByKey(final String person);

    /**
     * Create a new {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity and save it to the S/4HANA system.
     * 
     * @param simplePerson
     *     {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity object that will be created in the S/4HANA system.
     * @return
     *     A request builder to create a new {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<testcomparison.namespaces.multipleentitysets.SimplePerson>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CreateRequestBuilder<SimplePerson> createFirstSimplePersons(
        @Nonnull
        final SimplePerson simplePerson);

    /**
     * Update an existing {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity and save it to the S/4HANA system.
     * 
     * @param simplePerson
     *     {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity object that will be updated in the S/4HANA system.
     * @return
     *     A request builder to update an existing {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<testcomparison.namespaces.multipleentitysets.SimplePerson>#execute execute} method on the request builder object. 
     */
    @Nonnull
    UpdateRequestBuilder<SimplePerson> updateFirstSimplePersons(
        @Nonnull
        final SimplePerson simplePerson);

    /**
     * Deletes an existing {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity in the S/4HANA system.
     * 
     * @param simplePerson
     *     {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A request builder to delete an existing {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<testcomparison.namespaces.multipleentitysets.SimplePerson>#execute execute} method on the request builder object. 
     */
    @Nonnull
    DeleteRequestBuilder<SimplePerson> deleteFirstSimplePersons(
        @Nonnull
        final SimplePerson simplePerson);

    /**
     * Fetch multiple {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entities.
     * 
     * @return
     *     A request builder to fetch multiple {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<testcomparison.namespaces.multipleentitysets.SimplePerson>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetAllRequestBuilder<SimplePerson> getAllSecondSimplePersons();

    /**
     * Fetch the number of entries from the {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity collection matching the filter and search expressions.
     * 
     * @return
     *     A request builder to fetch the count of {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<testcomparison.namespaces.multipleentitysets.SimplePerson>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CountRequestBuilder<SimplePerson> countSecondSimplePersons();

    /**
     * Fetch a single {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity using key fields.
     * 
     * @param person
     *     <p>Constraints: Not nullable, Maximum length: 10</p>
     * @return
     *     A request builder to fetch a single {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity using key fields. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<testcomparison.namespaces.multipleentitysets.SimplePerson>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetByKeyRequestBuilder<SimplePerson> getSecondSimplePersonsByKey(final String person);

    /**
     * Create a new {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity and save it to the S/4HANA system.
     * 
     * @param simplePerson
     *     {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity object that will be created in the S/4HANA system.
     * @return
     *     A request builder to create a new {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<testcomparison.namespaces.multipleentitysets.SimplePerson>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CreateRequestBuilder<SimplePerson> createSecondSimplePersons(
        @Nonnull
        final SimplePerson simplePerson);

    /**
     * Update an existing {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity and save it to the S/4HANA system.
     * 
     * @param simplePerson
     *     {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity object that will be updated in the S/4HANA system.
     * @return
     *     A request builder to update an existing {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<testcomparison.namespaces.multipleentitysets.SimplePerson>#execute execute} method on the request builder object. 
     */
    @Nonnull
    UpdateRequestBuilder<SimplePerson> updateSecondSimplePersons(
        @Nonnull
        final SimplePerson simplePerson);

    /**
     * Deletes an existing {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity in the S/4HANA system.
     * 
     * @param simplePerson
     *     {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A request builder to delete an existing {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<testcomparison.namespaces.multipleentitysets.SimplePerson>#execute execute} method on the request builder object. 
     */
    @Nonnull
    DeleteRequestBuilder<SimplePerson> deleteSecondSimplePersons(
        @Nonnull
        final SimplePerson simplePerson);

    /**
     * Fetch multiple {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entities.
     * 
     * @return
     *     A request builder to fetch multiple {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<testcomparison.namespaces.multipleentitysets.FooType>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetAllRequestBuilder<FooType> getAllFooType();

    /**
     * Fetch the number of entries from the {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity collection matching the filter and search expressions.
     * 
     * @return
     *     A request builder to fetch the count of {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<testcomparison.namespaces.multipleentitysets.FooType>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CountRequestBuilder<FooType> countFooType();

    /**
     * Fetch a single {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity using key fields.
     * 
     * @param foo
     *     <p>Constraints: Not nullable, Maximum length: 10</p>
     * @return
     *     A request builder to fetch a single {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity using key fields. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<testcomparison.namespaces.multipleentitysets.FooType>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetByKeyRequestBuilder<FooType> getFooTypeByKey(final String foo);

    /**
     * Create a new {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity and save it to the S/4HANA system.
     * 
     * @param fooType
     *     {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity object that will be created in the S/4HANA system.
     * @return
     *     A request builder to create a new {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<testcomparison.namespaces.multipleentitysets.FooType>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CreateRequestBuilder<FooType> createFooType(
        @Nonnull
        final FooType fooType);

    /**
     * Update an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity and save it to the S/4HANA system.
     * 
     * @param fooType
     *     {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity object that will be updated in the S/4HANA system.
     * @return
     *     A request builder to update an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<testcomparison.namespaces.multipleentitysets.FooType>#execute execute} method on the request builder object. 
     */
    @Nonnull
    UpdateRequestBuilder<FooType> updateFooType(
        @Nonnull
        final FooType fooType);

    /**
     * Deletes an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity in the S/4HANA system.
     * 
     * @param fooType
     *     {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A request builder to delete an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<testcomparison.namespaces.multipleentitysets.FooType>#execute execute} method on the request builder object. 
     */
    @Nonnull
    DeleteRequestBuilder<FooType> deleteFooType(
        @Nonnull
        final FooType fooType);

    /**
     * Fetch multiple {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entities.
     * 
     * @return
     *     A request builder to fetch multiple {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<testcomparison.namespaces.multipleentitysets.FooType>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetAllRequestBuilder<FooType> getAllSecondFooType();

    /**
     * Fetch the number of entries from the {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity collection matching the filter and search expressions.
     * 
     * @return
     *     A request builder to fetch the count of {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<testcomparison.namespaces.multipleentitysets.FooType>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CountRequestBuilder<FooType> countSecondFooType();

    /**
     * Fetch a single {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity using key fields.
     * 
     * @param foo
     *     <p>Constraints: Not nullable, Maximum length: 10</p>
     * @return
     *     A request builder to fetch a single {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity using key fields. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<testcomparison.namespaces.multipleentitysets.FooType>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetByKeyRequestBuilder<FooType> getSecondFooTypeByKey(final String foo);

    /**
     * Update an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity and save it to the S/4HANA system.
     * 
     * @param fooType
     *     {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity object that will be updated in the S/4HANA system.
     * @return
     *     A request builder to update an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<testcomparison.namespaces.multipleentitysets.FooType>#execute execute} method on the request builder object. 
     */
    @Nonnull
    UpdateRequestBuilder<FooType> updateSecondFooType(
        @Nonnull
        final FooType fooType);

    /**
     * Deletes an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity in the S/4HANA system.
     * 
     * @param fooType
     *     {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A request builder to delete an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<testcomparison.namespaces.multipleentitysets.FooType>#execute execute} method on the request builder object. 
     */
    @Nonnull
    DeleteRequestBuilder<FooType> deleteSecondFooType(
        @Nonnull
        final FooType fooType);

    /**
     * Fetch multiple {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entities.
     * 
     * @return
     *     A request builder to fetch multiple {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<testcomparison.namespaces.multipleentitysets.FooType>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetAllRequestBuilder<FooType> getAllThirdFooType();

    /**
     * Fetch the number of entries from the {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity collection matching the filter and search expressions.
     * 
     * @return
     *     A request builder to fetch the count of {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<testcomparison.namespaces.multipleentitysets.FooType>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CountRequestBuilder<FooType> countThirdFooType();

    /**
     * Fetch a single {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity using key fields.
     * 
     * @param foo
     *     <p>Constraints: Not nullable, Maximum length: 10</p>
     * @return
     *     A request builder to fetch a single {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity using key fields. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<testcomparison.namespaces.multipleentitysets.FooType>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetByKeyRequestBuilder<FooType> getThirdFooTypeByKey(final String foo);

    /**
     * Create a new {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity and save it to the S/4HANA system.
     * 
     * @param fooType
     *     {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity object that will be created in the S/4HANA system.
     * @return
     *     A request builder to create a new {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<testcomparison.namespaces.multipleentitysets.FooType>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CreateRequestBuilder<FooType> createThirdFooType(
        @Nonnull
        final FooType fooType);

    /**
     * Deletes an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity in the S/4HANA system.
     * 
     * @param fooType
     *     {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A request builder to delete an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<testcomparison.namespaces.multipleentitysets.FooType>#execute execute} method on the request builder object. 
     */
    @Nonnull
    DeleteRequestBuilder<FooType> deleteThirdFooType(
        @Nonnull
        final FooType fooType);

}
