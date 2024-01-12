/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.message.BasicHeader;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.result.ElementName;
import com.sap.cloud.sdk.result.ResultElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

class ODataResponseComplexDataParsingTest
{
    @Data
    @NoArgsConstructor
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class Person
    {
        @ElementName( "UserName" )
        @SerializedName( "UserName" )
        @JsonProperty( "UserName" )
        @Nonnull
        String userName;

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

        private static final String PAYLOAD_SAMPLE_SET = """
            {
            "@odata.context": "serviceRoot/$metadata#People",
            "@odata.nextLink": "serviceRoot/People?%24skiptoken=8",\
            "value": [
            {
              "@odata.id": "serviceRoot/People('jackblack')",
              "@odata.etag": "W/\\"08D1694BD49A0F11\\"",
              "@odata.editLink": "serviceRoot/People('jackblack')",
              "UserName": "jackblack",
              "FirstName": "Jack",
              "LastName": "Black",
              "AddressInfo": [
                  {
                      "Address": "187 Suffolk Ln.",
                      "City": {
                          "Name": "Boise",
                          "CountryRegion": "United States",
                          "Region": "ID"
                      }
                  }
              ]
            },
            {
              "@odata.id": "serviceRoot/People('kylegass')",
              "@odata.etag": "W/\\"08D1694BD49A0F11\\"",
              "@odata.editLink": "serviceRoot/People('kylegass')",
              "UserName": "kylegass",
              "FirstName": "Kyle",
              "LastName": "Gass",
              "AddressInfo": [
                  {
                      "Address": "187 Suffolk Ln.",
                      "City": {
                          "Name": "Boise",
                          "CountryRegion": "United States",
                          "Region": "ID"
                      }
                  }
              ]
            }
            ]}\
            """;

        private static final String PAYLOAD_SAMPLE_ENTITY =
            """
                {
                  "@odata.context": "https://services.odata.org/TripPinRESTierService/(S(3mslpb2bc0k5ufk24olpghzx))/$metadata#People/$entity",
                  "UserName": "jackblack",
                  "FirstName": "Jack",
                  "LastName": "Black",
                  "AddressInfo": [
                      {
                          "Address": "187 Suffolk Ln.",
                          "City": {
                              "Name": "Boise",
                              "CountryRegion": "United States",
                              "Region": "ID"
                          }
                      }
                  ],
                  "HomeAddress": null
                }\
                """;

        private static final String PAYLOAD_SAMPLE_ENTITY_WITH_EXPANDED_NAVIGATION_PROPERTY =
            """
                {
                    "@odata.context": "https://services.odata.org/TripPinRESTierService/(S(3ubj4rnppjfo1oflxryca1e2))/$metadata#People",
                            "UserName": "russellwhyte",
                            "FirstName": "Russell",
                            "LastName": "Whyte",
                            "AddressInfo": [
                                {
                                    "Address": "187 Suffolk Ln.",
                                    "City": {
                                        "Name": "Boise",
                                        "CountryRegion": "United States",
                                        "Region": "ID"
                                    }
                                }
                            ],
                            "HomeAddress": null,
                            "Friends": [
                                {
                                    "UserName": "scottketchum",
                                    "FirstName": "Scott",
                                    "LastName": "Ketchum",
                                    "AddressInfo": [
                                        {
                                            "Address": "2817 Milton Dr.",
                                            "City": {
                                                "Name": "Albuquerque",
                                                "CountryRegion": "United States",
                                                "Region": "NM"
                                            }
                                        }
                                    ],
                                    "HomeAddress": null
                                },
                                {
                                    "UserName": "ronaldmundy",
                                    "FirstName": "Ronald",
                                    "LastName": "Mundy",
                                    "AddressInfo": [
                                        {
                                            "Address": "187 Suffolk Ln.",
                                            "City": {
                                                "Name": "Boise",
                                                "CountryRegion": "United States",
                                                "Region": "ID"
                                            }
                                        }
                                    ],
                                    "HomeAddress": null
                                }
                            ]
                }\
                """;

        private static final String PAYLOAD_ENTITY_WITH_BINARY_NAVIGATION_PROPERTY = """
            {
                        "UserName": "russellwhyte",
                        "FirstName": "Russell",
                        "LastName": "Whyte",
                        "AddressInfo": [
                            {
                                "Address": "187 Suffolk Ln.",
                                "City": {
                                    "Name": "Boise",
                                    "CountryRegion": "United States",
                                    "Region": "ID"
                                }
                            }
                        ],
                        "HomeAddress": null,
                        "BestFriend": {
                            "UserName": "scottketchum",
                            "FirstName": "Scott",
                            "LastName": "Ketchum",
                            "AddressInfo": [
                                {
                                    "Address": "2817 Milton Dr.",
                                    "City": {
                                        "Name": "Albuquerque",
                                        "CountryRegion": "United States",
                                        "Region": "NM"
                                    }
                                }
                            ],
                            "HomeAddress": null
                        }
                    }\
            """;

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

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
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

    private final Person jackBlack =
        new Person(
            "jackblack",
            "Black",
            "Jack",
            Arrays.asList(new AddressInfo("187 Suffolk Ln.", new AddressInfo.City("Boise", "United States", "ID"))));

    private final Person kyleGass =
        new Person(
            "kylegass",
            "Gass",
            "Kyle",
            Arrays.asList(new AddressInfo("187 Suffolk Ln.", new AddressInfo.City("Boise", "United States", "ID"))));

    private final Person russellWhyte =
        new Person(
            "russellwhyte",
            "Whyte",
            "Russell",
            Arrays.asList(new AddressInfo("187 Suffolk Ln.", new AddressInfo.City("Boise", "United States", "ID"))));

    private final Person scottKetchum =
        new Person(
            "scottketchum",
            "Ketchum",
            "Scott",
            Arrays
                .asList(
                    new AddressInfo("2817 Milton Dr.", new AddressInfo.City("Albuquerque", "United States", "NM"))));

    private final Person ronaldMundy =
        new Person(
            "ronaldmundy",
            "Mundy",
            "Ronald",
            Arrays.asList(new AddressInfo("187 Suffolk Ln.", new AddressInfo.City("Boise", "United States", "ID"))));

    @Test
    void testGetResultElementPerson()
    {
        final ODataRequestResultGeneric result = mockRequestResult(Person.PAYLOAD_SAMPLE_ENTITY);
        final Person person = result.as(Person.class);
        assertThat(person).isEqualTo(jackBlack);
    }

    @Test
    void testGetResultPersonWithExpandedFriends()
    {
        final ODataRequestResultGeneric result =
            mockRequestResult(Person.PAYLOAD_SAMPLE_ENTITY_WITH_EXPANDED_NAVIGATION_PROPERTY);
        final Person person = result.as(Person.class);
        russellWhyte.setFriends(Arrays.asList(scottKetchum, ronaldMundy));
        assertThat(person).isEqualTo(russellWhyte);
    }

    @Test
    void testGetResultPersonWithExpandedBestFriend()
    {
        final ODataRequestResultGeneric result =
            mockRequestResult(Person.PAYLOAD_ENTITY_WITH_BINARY_NAVIGATION_PROPERTY);
        final Person person = result.as(Person.class);
        russellWhyte.setBestFriend(scottKetchum);
        assertThat(person).isEqualTo(russellWhyte);
    }

    @Test
    void testGetResultElements()
    {
        final ODataRequestResultGeneric result = mockRequestResult(Person.PAYLOAD_SAMPLE_SET);
        final Iterable<ResultElement> elements = result.getResultElements();
        assertThat(elements).isNotNull();
    }

    @Test
    void testGetResultAsList()
    {
        final ODataRequestResultGeneric result = mockRequestResult(Person.PAYLOAD_SAMPLE_SET);
        final List<Person> persons = result.asList(Person.class);
        assertThat(persons).containsOnly(jackBlack, kyleGass);
    }

    @Test
    void testGetResultAsMap()
    {
        final ODataRequestResultGeneric result = mockRequestResult(Person.PAYLOAD_SAMPLE_SET);
        final List<Map<String, Object>> maps = result.asListOfMaps();
        assertThat(maps)
            .containsOnly(
                ImmutableMap
                    .<String, Object> builder()
                    .put("@odata.id", "serviceRoot/People('jackblack')")
                    .put("@odata.etag", "W/\"08D1694BD49A0F11\"")
                    .put("@odata.editLink", "serviceRoot/People('jackblack')")
                    .put("UserName", "jackblack")
                    .put("FirstName", "Jack")
                    .put("LastName", "Black")
                    .put(
                        "AddressInfo",
                        Arrays
                            .asList(
                                ImmutableMap
                                    .of(
                                        "Address",
                                        "187 Suffolk Ln.",
                                        "City",
                                        ImmutableMap
                                            .of("Name", "Boise", "CountryRegion", "United States", "Region", "ID"))))
                    .build(),
                ImmutableMap
                    .<String, Object> builder()
                    .put("@odata.id", "serviceRoot/People('kylegass')")
                    .put("@odata.etag", "W/\"08D1694BD49A0F11\"")
                    .put("@odata.editLink", "serviceRoot/People('kylegass')")
                    .put("UserName", "kylegass")
                    .put("FirstName", "Kyle")
                    .put("LastName", "Gass")
                    .put(
                        "AddressInfo",
                        Arrays
                            .asList(
                                ImmutableMap
                                    .of(
                                        "Address",
                                        "187 Suffolk Ln.",
                                        "City",
                                        ImmutableMap
                                            .of("Name", "Boise", "CountryRegion", "United States", "Region", "ID"))))
                    .build());
    }

    @Test
    void testGetResultAsStream()
    {
        final ODataRequestResultGeneric result = mockRequestResult(Person.PAYLOAD_SAMPLE_SET);
        final List<String> userNames = Lists.newArrayList();
        result.streamElements(element -> userNames.add(element.getAsObject().as(Person.class).getUserName()));
        assertThat(userNames).containsOnly("jackblack", "kylegass");
    }

    private static ODataRequestResultGeneric mockRequestResult( final String payload )
    {
        try {
            final ODataRequestGeneric request = mock(ODataRequestGeneric.class);
            when(request.getProtocol()).thenReturn(ODataProtocol.V4);

            final HttpEntity httpEntity = spy(HttpEntity.class);
            when(httpEntity.getContentLength()).thenReturn(0L);
            when(httpEntity.isRepeatable()).thenReturn(true);
            when(httpEntity.getContentType()).thenReturn(new BasicHeader("Content-Type", "application/json"));
            when(httpEntity.getContentEncoding()).thenReturn(new BasicHeader("Content-Encoding", "identity"));
            when(httpEntity.getContent()).thenReturn(new ByteArrayInputStream(payload.getBytes()));
            final HttpEntity bufferedHttpEntity = new BufferedHttpEntity(httpEntity);

            final StatusLine statusLine = mock(StatusLine.class);
            when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);

            final HttpResponse httpResponse = mock(HttpResponse.class);
            when(httpResponse.getAllHeaders()).thenReturn(new Header[0]);
            when(httpResponse.getEntity()).thenReturn(bufferedHttpEntity);
            when(httpResponse.getStatusLine()).thenReturn(statusLine);

            return new ODataRequestResultGeneric(request, httpResponse);
        }
        catch( final Exception e ) {
            throw new ShouldNotHappenException("Failed to run tests");
        }
    }
}
