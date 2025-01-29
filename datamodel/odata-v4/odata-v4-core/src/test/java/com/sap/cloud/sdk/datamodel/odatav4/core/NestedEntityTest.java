package com.sap.cloud.sdk.datamodel.odatav4.core;

import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.patch;
import static com.github.tomakehurst.wiremock.client.WireMock.patchRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatNoException;

import javax.annotation.Nonnull;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestCount;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestCreate;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestDelete;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestRead;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestReadByKey;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestUpdate;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.PlanItem;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Trip;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.services.DefaultTrippinService;

@WireMockTest
class NestedEntityTest
{
    private static final WireMockConfiguration WIREMOCK_CONFIGURATION = wireMockConfig().dynamicPort();

    private DefaultHttpDestination destination;
    private DefaultTrippinService service;

    @BeforeEach
    void setup( @Nonnull final WireMockRuntimeInfo wm )
    {
        destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
        service = new DefaultTrippinService().withServicePath("/TripPinServiceRW");
    }

    @Test
    void testGetSingleNestedFriend()
    {
        final Person personByKey = Person.builder().userName("russellwhyte").build();

        final ODataRequestReadByKey getAll =
            service.forEntity(personByKey).navigateTo(Person.TO_BEST_FRIEND).get().toRequest();

        assertThat(getAll.getRelativeUri()).hasToString("/TripPinServiceRW/People('russellwhyte')/BestFriend");
    }

    @Test
    void testGetSingleNestedNestedNestedFriend()
    {
        final Person personByKey = Person.builder().userName("russellwhyte").build();

        final ODataRequestReadByKey getAll =
            service
                .forEntity(personByKey)
                .navigateTo(Person.TO_BEST_FRIEND)
                .navigateTo(Person.TO_BEST_FRIEND)
                .navigateTo(Person.TO_BEST_FRIEND)
                .get()
                .toRequest();

        assertThat(getAll.getRelativeUri())
            .hasToString("/TripPinServiceRW/People('russellwhyte')/BestFriend/BestFriend/BestFriend");
    }

    @Test
    void testDeleteSingleNestedFriend()
    {
        final Person personByKey = Person.builder().userName("russellwhyte").build();

        final ODataRequestDelete delete =
            service.forEntity(personByKey).navigateTo(Person.TO_BEST_FRIEND).delete().toRequest();

        assertThat(delete.getRelativeUri()).hasToString("/TripPinServiceRW/People('russellwhyte')/BestFriend");
    }

    @Test
    void testUpdateSingleNestedFriend()
    {
        final Person friend = new Person();
        friend.setLastName("Jobs");

        final Person personByKey = Person.builder().userName("russellwhyte").build();

        final ODataRequestUpdate update =
            service.forEntity(personByKey).navigateTo(Person.TO_BEST_FRIEND).update(friend).toRequest();

        assertThat(update.getRelativeUri()).hasToString("/TripPinServiceRW/People('russellwhyte')/BestFriend");
    }

    @Test
    void testCreateNestedTrip()
    {
        final Trip customTrip = new Trip();
        final Person personByKey = Person.builder().userName("russellwhyte").build();

        final ODataRequestCreate create =
            service.forEntity(personByKey).navigateTo(Person.TO_TRIPS).create(customTrip).toRequest();

        assertThat(create.getRelativeUri()).hasToString("/TripPinServiceRW/People('russellwhyte')/Trips");
    }

    @Test
    void testGetAllNestedTrips()
    {
        final Person personByKey = Person.builder().userName("russellwhyte").build();

        final ODataRequestRead getAll = service.forEntity(personByKey).navigateTo(Person.TO_TRIPS).getAll().toRequest();

        assertThat(getAll.getRelativeUri()).hasToString("/TripPinServiceRW/People('russellwhyte')/Trips");
    }

    @Test
    void testDeleteBestFriend()
    {
        final Person personByKey = Person.builder().userName("russellwhyte").build();

        final ODataRequestDelete delete =
            service
                .forEntity(personByKey)
                .navigateTo(Person.TO_BEST_FRIEND)
                .delete()
                .matchAnyVersionIdentifier()
                .toRequest();

        assertThat(delete.getRelativeUri()).hasToString("/TripPinServiceRW/People('russellwhyte')/BestFriend");
    }

    @Test
    void testUpdateBestFriend()
    {
        final Person oldPerson = Person.builder().userName("oldMan").build();
        final Person newPerson = Person.builder().userName("youngMan").build();

        final ODataRequestUpdate update =
            service
                .forEntity(oldPerson)
                .navigateTo(Person.TO_BEST_FRIEND)
                .update(newPerson)
                .matchAnyVersionIdentifier()
                .toRequest();

        assertThat(update.getRelativeUri()).hasToString("/TripPinServiceRW/People('oldMan')/BestFriend");
    }

    @Test
    void testCountNestedTrip()
    {
        final Person personByKey = Person.builder().userName("russellwhyte").build();

        final ODataRequestCount count = service.forEntity(personByKey).navigateTo(Person.TO_TRIPS).count().toRequest();

        assertThat(count.getRelativeUri()).hasToString("/TripPinServiceRW/People('russellwhyte')/Trips/$count");
    }

    @Test
    void testGetByKeyNestedPlanItem()
    {
        final Person personByKey = Person.builder().userName("russellwhyte").build();
        final Trip tripByKey = Trip.builder().tripId(1003).build();

        final GetAllRequestBuilder<PlanItem> builder =
            service
                .forEntity(personByKey)
                .navigateTo(Person.TO_TRIPS)
                .forEntity(tripByKey)
                .navigateTo(Trip.TO_PLAN_ITEMS)
                .getAll();

        final ODataRequestRead getAll = builder.toRequest();

        assertThat(getAll.getRelativeUri())
            .hasToString("/TripPinServiceRW/People('russellwhyte')/Trips(1003)/PlanItems");
    }

    @Test
    void testCreateNestedPlanItem()
    {
        final PlanItem customPlanItem = new PlanItem();
        final Person personByKey = Person.builder().userName("russellwhyte").build();
        final Trip tripByKey = Trip.builder().tripId(1003).build();

        final CreateRequestBuilder<PlanItem> createdPlanItem =
            service
                .forEntity(personByKey)
                .navigateTo(Person.TO_TRIPS)
                .forEntity(tripByKey)
                .navigateTo(Trip.TO_PLAN_ITEMS)
                .create(customPlanItem)
                .withHeader("header", "value")
                .withQueryParameter("query", "parameter");

        // check client
        final ODataRequestCreate requestCreate = createdPlanItem.toRequest();
        assertThat(requestCreate.getRelativeUri()).hasQuery("query=parameter");
        assertThat(requestCreate.getRelativeUri())
            .hasPath("/TripPinServiceRW/People('russellwhyte')/Trips(1003)/PlanItems");

        // check VDM execution
        final String headPath = "/TripPinServiceRW";
        final String postPath = "/TripPinServiceRW/People('russellwhyte')/Trips(1003)/PlanItems";

        stubFor(head(urlPathEqualTo(headPath)).willReturn(ok().withHeader("x-csrf-token", "foo")));
        stubFor(
            post(urlPathEqualTo(postPath))
                .withQueryParam("query", equalTo("parameter"))
                .withHeader("header", equalTo("value"))
                .willReturn(okJson("{}")));

        final ModificationResponse<PlanItem> createResponse = createdPlanItem.execute(destination);
        assertThat(createResponse).isNotNull();
        assertThat(createResponse.getResponseStatusCode()).isEqualTo(200);
        verify(1, postRequestedFor(urlPathEqualTo(postPath)));
    }

    @Test
    void testDeleteEntityWithEtag()
    {
        final Person personByKey = Person.builder().userName("russellwhyte").build();

        personByKey.setVersionIdentifier("foobar");

        final String headPath = service.getServicePath();
        final String deletePath = headPath + "/People('russellwhyte')";

        stubFor(head(urlPathEqualTo(headPath)).willReturn(ok().withHeader("x-csrf-token", "foo")));
        stubFor(delete(urlPathEqualTo(deletePath)).willReturn(okJson("{}")));

        final DeleteRequestBuilder<Person> delete = service.forEntity(personByKey).delete();

        assertThat(delete.toRequest().getRelativeUri()).hasToString(deletePath);

        delete.execute(destination);

        verify(deleteRequestedFor(urlEqualTo(deletePath)).withHeader(HttpHeaders.IF_MATCH, equalTo("foobar")));
    }

    @Test
    void testUpdateEntityWithEtag()
    {
        final Person personByKey = Person.builder().userName("russellwhyte").build();

        personByKey.setVersionIdentifier("foobar");

        final String headPath = service.getServicePath();
        final String updatePath = headPath + "/People('russellwhyte')";

        stubFor(head(urlPathEqualTo(headPath)).willReturn(ok().withHeader("x-csrf-token", "foo")));
        stubFor(patch(urlPathEqualTo(updatePath)).willReturn(okJson("{}")));

        final UpdateRequestBuilder<Person> update = service.forEntity(personByKey).update(personByKey);

        assertThat(update.toRequest().getRelativeUri()).hasToString(updatePath);

        update.execute(destination);

        verify(patchRequestedFor(urlEqualTo(updatePath)).withHeader(HttpHeaders.IF_MATCH, equalTo("foobar")));
    }

    @Test
    void testTypeRestrictionForVdmEntitySet()
    {
        final EntityWithoutEntitySet entityWithoutEntitySet = new EntityWithoutEntitySet();
        final EntityWithEntitySet entityWithEntitySet = new EntityWithEntitySet();

        assertThatIllegalStateException().isThrownBy(() -> service.forEntity(entityWithoutEntitySet));
        assertThatNoException().isThrownBy(() -> service.forEntity(entityWithEntitySet));

        assertThatNoException().isThrownBy(() -> {
            service
                .forEntity(entityWithEntitySet)
                .navigateTo(EntityWithEntitySet.NAVIGATION_PROPERTY)
                .forEntity(entityWithoutEntitySet);
        });
    }

    private static class EntityWithoutEntitySet extends VdmEntity<EntityWithoutEntitySet>
    {
        @Nonnull
        @Override
        protected String getEntityCollection()
        {
            return "outside-of-entity-set";
        }

        @Nonnull
        @Override
        public String getOdataType()
        {
            return getEntityCollection();
        }

        @Nonnull
        @Override
        public Class<EntityWithoutEntitySet> getType()
        {
            return EntityWithoutEntitySet.class;
        }
    }

    private static class EntityWithEntitySet extends VdmEntity<EntityWithEntitySet> implements VdmEntitySet
    {
        public static final NavigationProperty.Collection<EntityWithEntitySet, EntityWithoutEntitySet> NAVIGATION_PROPERTY =
            new NavigationProperty.Collection<>(EntityWithEntitySet.class, "Navigations", EntityWithoutEntitySet.class);

        @Nonnull
        @Override
        protected String getEntityCollection()
        {
            return "outside-of-entity-set";
        }

        @Nonnull
        @Override
        public String getOdataType()
        {
            return getEntityCollection();
        }

        @Nonnull
        @Override
        public Class<EntityWithEntitySet> getType()
        {
            return EntityWithEntitySet.class;
        }
    }

    /* Commented out since otherwise this leads to a traceability mapping failure
    @Disabled( "Use this to run and check against the reference OData service." )
    @Test
    void integrationTestCreateNestedEntity()
        throws IOException
    {
        final HttpDestination destination = TripPinUtility.getDestinationRW();

        final String RANDOM_CONFIRMATION_CODE = "" + new Random().nextLong();

        final PlanItem somePlanItem =
            PlanItem
                .builder()
                .confirmationCode(RANDOM_CONFIRMATION_CODE)
                .duration(Duration.ofHours(3))
                .endsAt(LocalDate.of(2014, Month.JUNE, 1).atStartOfDay().atOffset(ZoneOffset.UTC))
                .startsAt(LocalDate.of(2014, Month.MAY, 1).atStartOfDay().atOffset(ZoneOffset.UTC))
                .build();

        final Person personByKey = Person.builder().userName("russellwhyte").build();
        final Trip tripByKey = Trip.builder().tripId(1003L).build();

        // Create new item
        final ModificationResponse<PlanItem> createResult =
            service
                .forEntity(personByKey)
                .navigateTo(Person.TO_TRIPS)
                .forEntity(tripByKey)
                .navigateTo(Trip.PLAN_ITEMS)
                .create(somePlanItem)
                .execute(destination);

        // Assert on positive feedback
        assertThat(createResult).isNotNull();

        // Query newly created item
        final Person personFound =
            service
                .getPersonsByKey("russellwhyte")
                .select(
                    Person.TO_TRIPS.filter(Trip.TRIP_ID.equalTo(1003L)).select(
                        Trip.PLAN_ITEMS.select(PlanItem.CONFIRMATION_CODE)))
                .execute(destination);

        // Find item in response
        assertThat(personFound).isNotNull();
        assertThat(personFound.getTrips()).isNotEmpty().anySatisfy(
            trip -> assertThat(trip.getPlanItems())
                .anySatisfy(item -> assertThat(item.getConfirmationCode()).isEqualTo(RANDOM_CONFIRMATION_CODE)));
    }
    */
}
