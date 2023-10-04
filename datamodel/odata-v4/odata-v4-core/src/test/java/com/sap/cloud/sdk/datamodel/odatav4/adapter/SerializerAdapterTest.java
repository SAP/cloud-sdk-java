package com.sap.cloud.sdk.datamodel.odatav4.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.UUID;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.City;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Feature;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Location;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Trip;

public class SerializerAdapterTest
{
    private static final City CITY =
        City.builder().name("Potsdam").countryRegion("Brandenburg").region("Deutschland").build();

    private static final Trip TRIP_A =
        Trip.builder().budget(100.0f).endsAt(OffsetDateTime.now()).shareId(UUID.randomUUID()).name("a").build();

    private static final Trip TRIP_B =
        Trip.builder().budget(20.0f).endsAt(OffsetDateTime.now()).shareId(UUID.randomUUID()).name("b").build();

    private static final Person PERSON =
        Person
            .builder()
            .lastName("Bar")
            .emails(Collections.singletonList("foo@bar.com"))
            .favoriteFeature(Feature.FEATURE1)
            .trips(TRIP_A, TRIP_B)
            .addressInfo(Collections.singletonList(new Location("Sesamstr.1", CITY)))
            .build();

    @Test
    public void testCompatibleNonNullGsonAndJackson()
        throws JsonProcessingException
    {
        // GSON
        final Gson gson = new Gson();
        final String jsonGson = gson.toJson(PERSON);
        final Person personGson = gson.fromJson(jsonGson, Person.class);

        // Jackson
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        final String jsonJackson = mapper.writeValueAsString(PERSON);
        final Person personJackson = mapper.readValue(jsonJackson, Person.class);

        // test
        assertThat(personJackson).isEqualTo(personGson);
    }

    @Test
    public void testCompatibleNullableGsonAndJackson()
        throws JsonProcessingException
    {
        // GSON
        final Gson gson = new GsonBuilder().serializeNulls().create();
        final String jsonGson = gson.toJson(PERSON);
        final Person personGson = gson.fromJson(jsonGson, Person.class);

        // Jackson
        final ObjectMapper mapper = new ObjectMapper();
        final String jsonJackson = mapper.writeValueAsString(PERSON);
        final Person personJackson = mapper.readValue(jsonJackson, Person.class);

        // test
        assertThat(personJackson).isEqualTo(personGson);
    }
}
