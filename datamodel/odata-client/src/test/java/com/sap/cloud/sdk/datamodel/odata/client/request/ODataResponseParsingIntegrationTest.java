/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.client.HttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataDeserializationException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceErrorException;
import com.sap.cloud.sdk.result.ElementName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Disabled( "Test runs against a reference service on odata.org. Use it only to manually verify behaviour." )
public class ODataResponseParsingIntegrationTest
{
    HttpClient httpClient;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class Person
    {

        @ElementName( "UserName" )
        @SerializedName( "UserName" )
        @JsonProperty( "UserName" )
        @Nonnull
        String username;

        @ElementName( "LastName" )
        @SerializedName( "LastName" )
        @JsonProperty( "LastName" )
        @Nonnull
        String lastName;

        @ElementName( "FirstName" )
        @SerializedName( "FirstName" )
        @JsonProperty( "FirstName" )
        @Nonnull
        String firstName;

        @ElementName( "Emails" )
        @SerializedName( "Emails" )
        @JsonProperty( "Emails" )
        @Nonnull
        List<String> emails;

        @ElementName( "AddressInfo" )
        @SerializedName( "AddressInfo" )
        @JsonProperty( "AddressInfo" )
        @Nonnull
        List<AddressInfo> addressInfo;

        @ElementName( "Friends" )
        @SerializedName( "Friends" )
        @JsonProperty( "Friends" )
        @Nullable
        List<Person> friends;

        @ElementName( "BestFriend" )
        @SerializedName( "BestFriend" )
        @JsonProperty( "BestFriend" )
        @Nullable
        Person bestFriend;

        @ElementName( "Trips" )
        @SerializedName( "Trips" )
        @JsonProperty( "Trips" )
        @Nullable
        List<Trip> trips;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Trip
    {

        @ElementName( "TripId" )
        @SerializedName( "TripId" )
        @JsonProperty( "TripId" )
        int tripId;

        @ElementName( "ShareId" )
        @SerializedName( "ShareId" )
        @JsonProperty( "ShareId" )
        UUID shareId;

        @ElementName( "Name" )
        @SerializedName( "Name" )
        @JsonProperty( "Name" )
        String name;

        @ElementName( "Budget" )
        @SerializedName( "Budget" )
        @JsonProperty( "Budget" )
        int budget;

        @ElementName( "Description" )
        @SerializedName( "Description" )
        @JsonProperty( "Description" )
        String description;

        @ElementName( "Tags" )
        @SerializedName( "Tags" )
        @JsonProperty( "Tags" )
        List<String> tags;

        @ElementName( "StartsAt" )
        @SerializedName( "StartsAt" )
        @JsonProperty( "StartsAt" )
        OffsetDateTime startsAt;

        @ElementName( "EndsAt" )
        @SerializedName( "EndsAt" )
        @JsonProperty( "EndsAt" )
        OffsetDateTime endsAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class AddressInfo
    {

        @ElementName( "Address" )
        @SerializedName( "Address" )
        @JsonProperty( "Address" )
        String address;

        @ElementName( "City" )
        @SerializedName( "City" )
        @JsonProperty( "City" )
        City city;

        @ToString
        public static class City
        {

            @ElementName( "Name" )
            @SerializedName( "Name" )
            @JsonProperty( "Name" )
            String name;

            @ElementName( "CountryRegion" )
            @SerializedName( "CountryRegion" )
            @JsonProperty( "CountryRegion" )
            String countryRegion;

            @ElementName( "Region" )
            @SerializedName( "Region" )
            @JsonProperty( "Region" )
            String region;
        }
    }

    @BeforeEach
    public void configure()
    {
        final Destination dest = DefaultHttpDestination.builder("https://services.odata.org").build();
        httpClient = HttpClientAccessor.getHttpClient(dest);
    }

    //Validating response for primitive collections
    @Test
    public void testEmailList()
    {
        final ODataRequestRead request =
            new ODataRequestRead(
                "TripPinRESTierService",
                "People",
                "$filter=UserName%20eq%20'russellwhyte'",
                ODataProtocol.V4);
        final ODataRequestResultGeneric result = request.execute(httpClient);
        final List<Person> persons = result.asList(Person.class);
        assertThat(persons).hasSize(1);
        assertThat(persons.get(0).getEmails()).isNotEmpty();
        assertThat(persons.get(0).getEmails()).isInstanceOf(List.class);
        assertThat(persons.get(0).getEmails().get(0)).isInstanceOf(String.class);
    }

    //Validating response for complex types
    @Test
    public void testAddressInfo()
    {
        final ODataRequestRead request =
            new ODataRequestRead(
                "TripPinRESTierService",
                "People",
                "$filter=UserName%20eq%20'russellwhyte'",
                ODataProtocol.V4);
        final ODataRequestResultGeneric result = request.execute(httpClient);
        final List<Person> persons = result.asList(Person.class);
        assertThat(persons).hasSize(1);
        final List<AddressInfo> addressInfo = persons.get(0).getAddressInfo();
        assertThat(addressInfo).isNotEmpty();
        assertThat(addressInfo.get(0)).isInstanceOf(AddressInfo.class);
        assertThat(addressInfo.get(0).getCity()).isInstanceOf(AddressInfo.City.class);
    }

    //The below test tests an 1:1 navigation property
    @Test
    public void testExpandedNavigationBestFriend()
    {
        final ODataRequestRead request =
            new ODataRequestRead(
                "TripPinRESTierService",
                "People",
                "$filter=UserName%20eq%20'russellwhyte'&$expand=BestFriend",
                ODataProtocol.V4);
        final ODataRequestResultGeneric result = request.execute(httpClient);
        final List<Person> persons = result.asList(Person.class);
        assertThat(persons).hasSize(1);
        final Person bestFriend = persons.get(0).getBestFriend();
        assertThat(bestFriend).isInstanceOf(Person.class);
    }

    //The below tests test an 1:n navigation property
    @Test
    public void testExpandedNavigationFriends()
    {
        final ODataRequestRead request =
            new ODataRequestRead(
                "TripPinRESTierService",
                "People",
                "$filter=UserName%20eq%20'russellwhyte'&$expand=Friends",
                ODataProtocol.V4);
        final ODataRequestResultGeneric result = request.execute(httpClient);
        final List<Person> persons = result.asList(Person.class);
        assertThat(persons).hasSize(1);
        final List<Person> friends = persons.get(0).getFriends();
        assertThat(friends).isNotEmpty();
        assertThat(friends.get(0)).isInstanceOf(Person.class);
    }

    @Test
    public void testExpandedNavigationTrips()
    {
        final ODataRequestRead request =
            new ODataRequestRead(
                "TripPinRESTierService",
                "People",
                "$filter=UserName%20eq%20'russellwhyte'&$expand=Trips",
                ODataProtocol.V4);
        final ODataRequestResultGeneric result = request.execute(httpClient);
        final List<Person> persons = result.asList(Person.class);
        assertThat(persons).hasSize(1);
        final List<Trip> trips = persons.get(0).getTrips();
        assertThat(trips).isNotEmpty();
        assertThat(trips.get(0)).isInstanceOf(Trip.class);
    }

    @Test
    public void testExpandedNavigationTripsWithNestedRequest()
    {
        final ODataRequestRead request =
            new ODataRequestRead(
                "TripPinRESTierService",
                "People",
                "$filter=UserName%20eq%20'russellwhyte'&$expand=Trips($top=1;$select=TripId)",
                ODataProtocol.V4);
        final ODataRequestResultGeneric result = request.execute(httpClient);
        final List<Person> persons = result.asList(Person.class);
        assertThat(persons).hasSize(1);
        final List<Trip> trips = persons.get(0).getTrips();
        assertThat(trips).isNotEmpty();
        assertThat(trips.get(0)).isInstanceOf(Trip.class);
    }

    //Validating response for error message
    @Test
    public void testServiceError()
    {
        final ODataRequestRead request =
            new ODataRequestRead(
                "TripPinRESTierService",
                "People",
                "$filter=contains(Emails,'Russell@example.com')",
                ODataProtocol.V4);
        assertThatCode(() -> request.execute(httpClient)).isInstanceOf(ODataServiceErrorException.class);
    }

    @Test
    public void testExceptionWhenUnbufferedHttpEntityIsAccessedMultipleTimes()
    {
        final ODataRequestRead request =
            new ODataRequestRead("TripPinRESTierService", "People", "$count=true&$format=json", ODataProtocol.V4);

        final ODataRequestResultGeneric result = request.execute(httpClient);
        result.disableBufferingHttpResponse();
        assertThat(result.asMap()).containsKeys("value", "@odata.count");
        assertThatExceptionOfType(ODataDeserializationException.class).isThrownBy(() -> result.getInlineCount());
    }

    @Test
    public void testWhenUnbufferedHttpEntityIsAccessedAfterAccessingBufferedHttpEntity()
    {
        final ODataRequestRead request =
            new ODataRequestRead("TripPinRESTierService", "People", "$count=true&$format=json", ODataProtocol.V4);

        final ODataRequestResultGeneric result = request.execute(httpClient);
        assertThat(result.asMap()).containsKeys("value", "@odata.count");
        result.disableBufferingHttpResponse();
        assertThat(result.getInlineCount()).isEqualTo(20);
    }
}
