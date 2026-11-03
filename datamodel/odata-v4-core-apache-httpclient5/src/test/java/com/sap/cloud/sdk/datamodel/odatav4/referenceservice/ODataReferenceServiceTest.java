package com.sap.cloud.sdk.datamodel.odatav4.referenceservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Trip;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.services.DefaultTrippinService;

class ODataReferenceServiceTest
{
    @Test
    void testSelectAndExpand()
    {
        final GetAllRequestBuilder<Person> request =
            new DefaultTrippinService()
                .getAllPeople()
                .select(Person.FIRST_NAME, Person.LAST_NAME)
                .select(Person.TO_TRIPS);

        assertThat(request.toRequest().getRelativeUri())
            .hasParameter("$select", "FirstName,LastName")
            .hasParameter("$expand", "Trips");
    }

    @Test
    void testMultipleExpandsWithFilter()
    {
        final GetAllRequestBuilder<Person> request =
            new DefaultTrippinService()
                .getAllPeople()
                .select(Person.TO_TRIPS.select(Trip.NAME))
                .select(Person.TO_BEST_FRIEND.select(Person.LAST_NAME));

        assertThat(request.toRequest().getRelativeUri())
            .hasParameter("$expand", "Trips($select=Name),BestFriend($select=LastName)");
    }

    @Test
    void testAllSelectionVariants()
    {
        final GetAllRequestBuilder<Person> request =
            new DefaultTrippinService()
                .getAllPeople()
                .select(Person.GENDER, Person.USER_NAME)
                .select(Person.TO_BEST_FRIEND, Person.TO_TRIPS, Person.TO_FRIENDS.top(2).skip(1))
                .filter(Person.EMAILS.contains(Collections.singletonList("ASD")));

        assertThat(request.toRequest().getRelativeUri())
            .hasParameter("$select", "Gender,UserName")
            .hasParameter("$expand", "BestFriend,Trips,Friends($top=2;$skip=1)")
            .hasParameter("$filter", "contains(Emails,['ASD'])");
    }

    @Test
    void testFiltersWithSpecialCharacters()
    {
        final GetAllRequestBuilder<Person> request =
            new DefaultTrippinService().getAllPeople().filter(Person.FIRST_NAME.contains("' +&#\\"));

        assertThat(request.toRequest().getRelativeUri()).hasParameter("$filter", "contains(FirstName,''' +&#\\')");
    }

    @Test
    void testNestedFiltersWithAllSpecialCharacters()
    {
        final GetAllRequestBuilder<Person> request =
            new DefaultTrippinService()
                .getAllPeople()
                .filter(Person.FIRST_NAME.contains("% $&#?\"\\+'"))
                .select(Person.TO_BEST_FRIEND.select(Person.TO_TRIPS.filter(Trip.NAME.contains("% $&#?\"\\+'"))));

        assertThat(request.toRequest().getRelativeUri())
            .hasParameter("$expand", "BestFriend($expand=Trips($filter=contains(Name,'% $&#?\"\\+''')))")
            .hasParameter("$filter", "contains(FirstName,'% $&#?\"\\+''')");
    }

    @Test
    void testNestedFiltersWithSpecialCharacters()
    {
        final GetAllRequestBuilder<Person> request =
            new DefaultTrippinService()
                .getAllPeople()
                .filter(Person.FIRST_NAME.contains("' +&#\\"))
                .select(Person.TO_TRIPS.filter(Trip.NAME.equalTo("Trip in '&USA'#")));

        assertThat(request.toRequest().getRelativeUri())
            .hasParameter("$expand", "Trips($filter=(Name eq 'Trip in ''&USA''#'))")
            .hasParameter("$filter", "contains(FirstName,''' +&#\\')");
    }

    @Test
    void testAllDuplicateSelections()
    {
        final GetAllRequestBuilder<Person> request =
            new DefaultTrippinService()
                .getAllPeople()
                .select(Person.GENDER, Person.USER_NAME)
                .select(Person.GENDER, Person.USER_NAME)
                .select(Person.TO_BEST_FRIEND, Person.TO_TRIPS)
                .select(Person.TO_BEST_FRIEND, Person.TO_BEST_FRIEND.select(Person.TO_FRIENDS))
                .select(Person.TO_FRIENDS, Person.TO_FRIENDS.top(2).skip(1));

        assertThat(request.toRequest().getRelativeUri())
            .hasParameter("$select", "Gender,UserName")
            .hasParameter("$expand", "Trips,BestFriend($expand=Friends),Friends($top=2;$skip=1)");
    }

    @Test
    void testOrderBy()
    {
        final GetAllRequestBuilder<Person> request =
            new DefaultTrippinService().getAllPeople().orderBy(Person.FIRST_NAME.asc(), Person.LAST_NAME.desc());

        assertThat(request.toRequest().getRelativeUri()).hasParameter("$orderby", "FirstName asc,LastName desc");
    }

    @Test
    void testBadOrderByUsage()
    {
        final GetAllRequestBuilder<Person> request =
            new DefaultTrippinService()
                .getAllPeople()
                .orderBy(Person.FIRST_NAME.desc(), Person.LAST_NAME.asc(), Person.FIRST_NAME.desc());

        assertThat(request.toRequest().getRelativeUri()).hasParameter("$orderby", "FirstName desc,LastName asc");
    }
}
