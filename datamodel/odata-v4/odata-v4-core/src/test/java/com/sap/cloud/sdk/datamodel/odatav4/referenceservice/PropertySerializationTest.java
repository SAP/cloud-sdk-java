package com.sap.cloud.sdk.datamodel.odatav4.referenceservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.City;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Location;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.PersonGender;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Trip;

class PropertySerializationTest
{
    @Test
    void testEnum()
    {
        final Person person = Person.builder().gender(PersonGender.FEMALE).firstName("Eve").build();
        final String json = new Gson().toJson(person);
        final JsonElement actual = new Gson().toJsonTree(person);
        assertThat(actual)
            .isEqualTo(
                JsonParser
                    .parseString(
                        "{\"FirstName\":\"Eve\",\"Gender\":\"Female\",\"@odata.type\":\"#Trippin.Person\",\"Friends\":[],\"Trips\":[]}"));
    }

    @Test
    void testComplexCollection()
    {
        final City city = new City();
        city.setName("Potsdam");
        city.setRegion("Nedlitz");
        city.setCountryRegion("Brandenburg");

        final Location location = new Location();
        location.setAddress("Konrad-Zuse-Ring 10, 14469");
        location.setCity(city);

        final Person person = Person.builder().addressInfo(Arrays.asList(location)).build();

        final JsonElement actual = new Gson().toJsonTree(person);
        assertThat(actual)
            .isEqualTo(
                JsonParser
                    .parseString(
                        "{\"AddressInfo\":["
                            + "{\"Address\":\"Konrad-Zuse-Ring 10, 14469\","
                            + "\"City\":{\"Name\":\"Potsdam\",\"CountryRegion\":\"Brandenburg\",\"Region\":\"Nedlitz\",\"@odata.type\":\"#Trippin.City\"},"
                            + "\"@odata.type\":\"#Trippin.Location\"}"
                            + "],\"@odata.type\":\"#Trippin.Person\""
                            + ",\"Friends\":[],\"Trips\":[]}"));
        // {"@odata.type":"#Trippin.Person","AddressInfo":[{"@odata.type":"#Trippin.Location","Address":"Konrad-Zuse-Ring 10, 14469","City":{"@odata.type":"#Trippin.City","Name":"Potsdam","CountryRegion":"Brandenburg","Region":"Nedlitz"}}],"Friends":[],"Trips":[]}
    }

    @Test
    void testPrimitiveCollection()
    {
        final String email = "eve@sap.com";
        final Person person = Person.builder().emails(Arrays.asList(email)).build();
        final JsonElement actual = new Gson().toJsonTree(person);
        assertThat(actual)
            .isEqualTo(
                JsonParser
                    .parseString(
                        "{\"Emails\":[\"eve@sap.com\"],\"@odata.type\":\"#Trippin.Person\",\"Friends\":[],\"Trips\":[]}"));
    }

    @Test
    void testDateTimeGson()
    {
        final OffsetDateTime date1 = LocalDate.of(2020, 2, 20).atStartOfDay().atOffset(ZoneOffset.UTC);
        final OffsetDateTime date2 = LocalDate.of(2020, 2, 20).atTime(3, 0).atOffset(ZoneOffset.ofHours(1));
        final Trip trip = Trip.builder().name("Trip1").startsAt(date1).endsAt(date2).build();

        final String tripJson = new Gson().toJson(trip);
        final JsonObject gsonObject = JsonParser.parseString(tripJson).getAsJsonObject();

        final String startsAt = gsonObject.getAsJsonPrimitive("StartsAt").getAsString();
        assertThat(startsAt).isEqualTo("2020-02-20T00:00:00Z");

        final String endsAt = gsonObject.getAsJsonPrimitive("EndsAt").getAsString();
        assertThat(endsAt).isEqualTo("2020-02-20T03:00:00+01:00");

        final Trip tripGson = new Gson().fromJson(tripJson, Trip.class);
        assertThat(tripGson).isEqualTo(trip);
        assertThat(tripGson.getStartsAt()).isEqualTo(date1);
        assertThat(tripGson.getEndsAt()).isEqualTo(date2);
    }

    @Test
    void testDateTimeJackson()
        throws JsonProcessingException
    {
        final OffsetDateTime date1 = LocalDate.of(2020, 2, 20).atStartOfDay().atOffset(ZoneOffset.UTC);
        final OffsetDateTime date2 = LocalDate.of(2020, 2, 20).atTime(3, 0).atOffset(ZoneOffset.ofHours(1));
        final Trip trip = Trip.builder().name("Trip1").startsAt(date1).endsAt(date2).build();

        final ObjectMapper mapper = new ObjectMapper();
        final String tripJson = mapper.writeValueAsString(trip);

        final String startsAt = mapper.readTree(tripJson).get("StartsAt").textValue();
        assertThat(startsAt).isEqualTo("2020-02-20T00:00:00Z");

        final String endsAt = mapper.readTree(tripJson).get("EndsAt").textValue();
        assertThat(endsAt).isEqualTo("2020-02-20T03:00:00+01:00");

        final Trip tripJackson = mapper.readValue(tripJson, Trip.class);
        assertThat(tripJackson).isEqualTo(trip);
        assertThat(tripJackson.getStartsAt()).isEqualTo(date1);
        assertThat(tripJackson.getEndsAt()).isEqualTo(date2);
    }
}
