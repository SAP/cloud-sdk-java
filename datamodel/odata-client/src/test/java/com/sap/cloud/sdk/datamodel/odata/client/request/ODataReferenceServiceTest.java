package com.sap.cloud.sdk.datamodel.odata.client.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.apache.http.client.HttpClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.result.ElementName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Disabled( "Test runs against a v4 reference service on odata.org. Use it only to manually verify behaviour." )
class ODataReferenceServiceTest
{
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Person
    {
        @ElementName( "UserName" )
        @SerializedName( "UserName" )
        @JsonProperty( "UserName" )
        String userName;

        @ElementName( "UserName" )
        @SerializedName( "LastName" )
        @JsonProperty( "LastName" )
        String lastName;

        @ElementName( "UserName" )
        @SerializedName( "FirstName" )
        @JsonProperty( "FirstName" )
        String firstName;
    }

    @Test
    void testGetFiltered()
    {
        final Destination httpDestination = DefaultHttpDestination.builder("https://services.odata.org").build();
        final HttpClient httpClient = HttpClientAccessor.getHttpClient(httpDestination);

        final String queryString = "$top=1&$filter=(UserName%20eq%20'angelhuffman')";
        final ODataRequestRead request =
            new ODataRequestRead("TripPinRESTierService", "People", queryString, ODataProtocol.V4);
        final ODataRequestResultGeneric result = request.execute(httpClient);
        final List<Person> persons = result.asList(Person.class);
        assertThat(persons).hasSize(1);
        assertThat(persons.get(0)).matches(p -> "Angel".equals(p.getFirstName()) && "Huffman".equals(p.getLastName()));
    }
}
