/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

/*
 * Generated by OData VDM code generator of SAP Cloud SDK in version 4.21.0
 */

package com.sap.cloud.sdk.datamodel.odatav4.referenceservice.services;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odatav4.core.BatchRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.SingleValueActionRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.SingleValueFunctionRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airline;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airport;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person;

/**
 * <h3>Details:</h3>
 * <table summary='Details'>
 * <tr>
 * <td align='right'>OData Service:</td>
 * <td>trippin</td>
 * </tr>
 * </table>
 *
 */
public interface TrippinService
{

    /**
     * If no other path was provided via the {@link #withServicePath(String)} method, this is the default service path
     * used to access the endpoint.
     *
     */
    String DEFAULT_SERVICE_PATH = "/TripPinRESTierServiceTrippin";

    /**
     * Overrides the default service path and returns a new service instance with the specified service path. Also
     * adjusts the respective entity URLs.
     *
     * @param servicePath
     *            Service path that will override the default.
     * @return A new service instance with the specified service path.
     */
    @Nonnull
    TrippinService withServicePath( @Nonnull final String servicePath );

    /**
     * Creates a batch request builder object.
     *
     * @return A request builder to handle batch operation on this service. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.BatchRequestBuilder#execute(Destination) execute} method
     *         on the request builder object.
     */
    @Nonnull
    BatchRequestBuilder batch();

    /**
     * Fetch multiple {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person Person}
     * entities.
     *
     * @return A request builder to fetch multiple
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person Person} entities.
     *         This request builder allows methods which modify the underlying query to be called before executing the
     *         query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    GetAllRequestBuilder<Person> getAllPeople();

    /**
     * Fetch the number of entries from the
     * {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person Person} entity collection
     * matching the filter and search expressions.
     *
     * @return A request builder to fetch the count of
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person Person} entities.
     *         This request builder allows methods which modify the underlying query to be called before executing the
     *         query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    CountRequestBuilder<Person> countPeople();

    /**
     * Fetch a single {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person Person}
     * entity using key fields.
     *
     * @param userName
     *            <p>
     *            Constraints: Not nullable
     *            </p>
     * @return A request builder to fetch a single
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person Person} entity
     *         using key fields. This request builder allows methods which modify the underlying query to be called
     *         before executing the query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    GetByKeyRequestBuilder<Person> getPeopleByKey( final String userName );

    /**
     * Create a new {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person Person} entity
     * and save it to the S/4HANA system.
     *
     * @param person
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person Person} entity
     *            object that will be created in the S/4HANA system.
     * @return A request builder to create a new
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person Person} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    CreateRequestBuilder<Person> createPeople( @Nonnull final Person person );

    /**
     * Update an existing {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person Person}
     * entity and save it to the S/4HANA system.
     *
     * @param person
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person Person} entity
     *            object that will be updated in the S/4HANA system.
     * @return A request builder to update an existing
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person Person} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    UpdateRequestBuilder<Person> updatePeople( @Nonnull final Person person );

    /**
     * Deletes an existing {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person Person}
     * entity in the S/4HANA system.
     *
     * @param person
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person Person} entity
     *            object that will be deleted in the S/4HANA system.
     * @return A request builder to delete an existing
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person Person} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    DeleteRequestBuilder<Person> deletePeople( @Nonnull final Person person );

    /**
     * Fetch multiple {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airline Airline}
     * entities.
     *
     * @return A request builder to fetch multiple
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airline Airline} entities.
     *         This request builder allows methods which modify the underlying query to be called before executing the
     *         query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airline>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    GetAllRequestBuilder<Airline> getAllAirlines();

    /**
     * Fetch the number of entries from the
     * {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airline Airline} entity collection
     * matching the filter and search expressions.
     *
     * @return A request builder to fetch the count of
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airline Airline} entities.
     *         This request builder allows methods which modify the underlying query to be called before executing the
     *         query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airline>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    CountRequestBuilder<Airline> countAirlines();

    /**
     * Fetch a single {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airline Airline}
     * entity using key fields.
     *
     * @param airlineCode
     *            <p>
     *            Constraints: Not nullable
     *            </p>
     * @return A request builder to fetch a single
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airline Airline} entity
     *         using key fields. This request builder allows methods which modify the underlying query to be called
     *         before executing the query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airline>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    GetByKeyRequestBuilder<Airline> getAirlinesByKey( final String airlineCode );

    /**
     * Create a new {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airline Airline}
     * entity and save it to the S/4HANA system.
     *
     * @param airline
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airline Airline} entity
     *            object that will be created in the S/4HANA system.
     * @return A request builder to create a new
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airline Airline} entity.
     *         To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airline>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    CreateRequestBuilder<Airline> createAirlines( @Nonnull final Airline airline );

    /**
     * Update an existing {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airline
     * Airline} entity and save it to the S/4HANA system.
     *
     * @param airline
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airline Airline} entity
     *            object that will be updated in the S/4HANA system.
     * @return A request builder to update an existing
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airline Airline} entity.
     *         To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airline>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    UpdateRequestBuilder<Airline> updateAirlines( @Nonnull final Airline airline );

    /**
     * Deletes an existing {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airline
     * Airline} entity in the S/4HANA system.
     *
     * @param airline
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airline Airline} entity
     *            object that will be deleted in the S/4HANA system.
     * @return A request builder to delete an existing
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airline Airline} entity.
     *         To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airline>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    DeleteRequestBuilder<Airline> deleteAirlines( @Nonnull final Airline airline );

    /**
     * Fetch multiple {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airport Airport}
     * entities.
     *
     * @return A request builder to fetch multiple
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airport Airport} entities.
     *         This request builder allows methods which modify the underlying query to be called before executing the
     *         query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airport>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    GetAllRequestBuilder<Airport> getAllAirports();

    /**
     * Fetch the number of entries from the
     * {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airport Airport} entity collection
     * matching the filter and search expressions.
     *
     * @return A request builder to fetch the count of
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airport Airport} entities.
     *         This request builder allows methods which modify the underlying query to be called before executing the
     *         query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airport>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    CountRequestBuilder<Airport> countAirports();

    /**
     * Fetch a single {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airport Airport}
     * entity using key fields.
     *
     * @param icaoCode
     *            <p>
     *            Constraints: Not nullable
     *            </p>
     * @return A request builder to fetch a single
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airport Airport} entity
     *         using key fields. This request builder allows methods which modify the underlying query to be called
     *         before executing the query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airport>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    GetByKeyRequestBuilder<Airport> getAirportsByKey( final String icaoCode );

    /**
     * Create a new {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airport Airport}
     * entity and save it to the S/4HANA system.
     *
     * @param airport
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airport Airport} entity
     *            object that will be created in the S/4HANA system.
     * @return A request builder to create a new
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airport Airport} entity.
     *         To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airport>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    CreateRequestBuilder<Airport> createAirports( @Nonnull final Airport airport );

    /**
     * Update an existing {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airport
     * Airport} entity and save it to the S/4HANA system.
     *
     * @param airport
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airport Airport} entity
     *            object that will be updated in the S/4HANA system.
     * @return A request builder to update an existing
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airport Airport} entity.
     *         To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airport>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    UpdateRequestBuilder<Airport> updateAirports( @Nonnull final Airport airport );

    /**
     * Deletes an existing {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airport
     * Airport} entity in the S/4HANA system.
     *
     * @param airport
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airport Airport} entity
     *            object that will be deleted in the S/4HANA system.
     * @return A request builder to delete an existing
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airport Airport} entity.
     *         To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Airport>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    DeleteRequestBuilder<Airport> deleteAirports( @Nonnull final Airport airport );

    /**
     * <p>
     * Creates a request builder for the <b>GetPersonWithMostFriends</b> OData function.
     * </p>
     *
     * @return A request builder object that will execute the <b>GetPersonWithMostFriends</b> OData function with the
     *         provided parameters. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.SingleValueFunctionRequestBuilder#execute execute} method
     *         on the request builder object.
     */
    @Nonnull
    SingleValueFunctionRequestBuilder<Person> getPersonWithMostFriends();

    /**
     * <p>
     * Creates a request builder for the <b>GetNearestAirport</b> OData function.
     * </p>
     *
     * @param lon
     *            Constraints: Not nullable
     *            <p>
     *            Original parameter name from the Odata EDM: <b>lon</b>
     *            </p>
     * @param lat
     *            Constraints: Not nullable
     *            <p>
     *            Original parameter name from the Odata EDM: <b>lat</b>
     *            </p>
     * @return A request builder object that will execute the <b>GetNearestAirport</b> OData function with the provided
     *         parameters. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.SingleValueFunctionRequestBuilder#execute execute} method
     *         on the request builder object.
     */
    @Nonnull
    SingleValueFunctionRequestBuilder<Airport>
        getNearestAirport( @Nonnull final Double lat, @Nonnull final Double lon );

    /**
     * <p>
     * Creates a request builder for the <b>ResetDataSource</b> OData action.
     * </p>
     *
     * @return A request builder object that will execute the <b>ResetDataSource</b> OData action with the provided
     *         parameters. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.SingleValueActionRequestBuilder#execute execute} method
     *         on the request builder object.
     */
    @Nonnull
    SingleValueActionRequestBuilder<Void> resetDataSource();

}
