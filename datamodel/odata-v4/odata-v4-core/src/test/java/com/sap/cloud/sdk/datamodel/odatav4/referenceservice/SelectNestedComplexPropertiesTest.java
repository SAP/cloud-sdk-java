package com.sap.cloud.sdk.datamodel.odatav4.referenceservice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.expression.FilterableBoolean;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.City;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Location;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.PlanItem;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Trip;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.services.DefaultTrippinService;

public class SelectNestedComplexPropertiesTest
{
    @Test
    public void testSelectComplexProperty()
    {
        final GetAllRequestBuilder<Person> query =
            new DefaultTrippinService().getAllPeople().select(Person.FIRST_NAME, Person.LAST_NAME, Person.ADDRESS_INFO);

        final String expectedUnencodedQuery = "FirstName,LastName,AddressInfo";

        assertThat(query.toRequest().getRelativeUri()).hasParameter("$select", expectedUnencodedQuery);
    }

    @Test
    public void testSelectNestedComplexProperty()
    {
        final GetAllRequestBuilder<Person> query =
            new DefaultTrippinService()
                .getAllPeople()
                .select(
                    Person.FIRST_NAME,
                    Person.LAST_NAME,
                    Person.ADDRESS_INFO.select(Location.ADDRESS, Location.CITY));

        final String expectedUnencodedQuery = "FirstName,LastName,AddressInfo/Address,AddressInfo/City";

        assertThat(query.toRequest().getRelativeUri()).hasParameter("$select", expectedUnencodedQuery);
    }

    @Test
    public void testSelectSimplePropertyInNestedComplexProperty()
    {
        final GetAllRequestBuilder<Person> query =
            new DefaultTrippinService()
                .getAllPeople()
                .select(
                    Person.FIRST_NAME,
                    Person.LAST_NAME,
                    Person.ADDRESS_INFO.select(Location.ADDRESS, Location.CITY.select(City.NAME)));

        final String expectedUnencodedQuery = "FirstName,LastName,AddressInfo/Address,AddressInfo/City/Name";

        assertThat(query.toRequest().getRelativeUri()).hasParameter("$select", expectedUnencodedQuery);
    }

    @Test
    public void testSelectComplexPropertyInNavigationProperty()
    {
        final GetAllRequestBuilder<Person> query =
            new DefaultTrippinService()
                .getAllPeople()
                .select(
                    Person.TO_BEST_FRIEND
                        .select(Person.FIRST_NAME, Person.ADDRESS_INFO.select(Location.ADDRESS, Location.CITY)));

        assertThat(query.toRequest().getRelativeUri())
            .hasParameter("$expand", "BestFriend($select=FirstName,AddressInfo/Address,AddressInfo/City)");
    }

    @Test
    public void testAnyInNestedComplexPropertyCollection()
    {
        final GetAllRequestBuilder<Person> query =
            new DefaultTrippinService()
                .getAllPeople()
                .select(Person.FIRST_NAME, Person.LAST_NAME)
                .filter(Person.ADDRESS_INFO.any(Location.ADDRESS.startsWith("Diagon Alley")));

        assertThat(query.toRequest().getRelativeUri())
            .hasParameter("$select", "FirstName,LastName")
            .hasParameter("$filter", "AddressInfo/any(a:startswith(a/Address,'Diagon Alley'))");
    }

    @Test
    public void testAnyInAnyPropertyCollection()
    {
        final GetAllRequestBuilder<Person> query =
            new DefaultTrippinService()
                .getAllPeople()
                .filter(Person.TO_FRIENDS.any(Person.ADDRESS_INFO.any(Location.ADDRESS.startsWith("Diagon Alley"))));

        assertThat(query.toRequest().getRelativeUri())
            .hasParameter("$filter", "Friends/any(a:a/AddressInfo/any(b:startswith(b/Address,'Diagon Alley')))");
    }

    @Test
    public void testAnyBesidesAllPropertyCollection()
    {
        final GetAllRequestBuilder<Person> query =
            new DefaultTrippinService()
                .getAllPeople()
                .filter(
                    Person.TO_FRIENDS
                        .any(Person.FIRST_NAME.equalTo("Adam"))
                        .or(Person.TO_FRIENDS.all(Person.FIRST_NAME.equalTo("Eve"))));

        assertThat(query.toRequest().getRelativeUri())
            .hasParameter(
                "$filter",
                "(Friends/any(a:(a/FirstName eq 'Adam')) or Friends/all(a:(a/FirstName eq 'Eve')))");
    }

    @Test
    public void testLambdaParameterLevelsForSameEntity()
    {
        final FilterableBoolean<Person> lvl3 = Person.USER_NAME.equalTo("c1").or(Person.USER_NAME.equalTo("c2"));

        final FilterableBoolean<Person> lvl2 =
            Person.USER_NAME.equalTo("b1").and(Person.TO_FRIENDS.any(lvl3)).and(Person.USER_NAME.equalTo("b2"));

        final FilterableBoolean<Person> lvl1 =
            Person.USER_NAME.equalTo("a1").or(Person.TO_FRIENDS.all(lvl2)).or(Person.USER_NAME.equalTo("a2"));

        final FilterableBoolean<Person> lvl0 =
            Person.USER_NAME.equalTo("1").and(Person.TO_FRIENDS.any(lvl1)).or(Person.USER_NAME.equalTo("2"));

        final GetAllRequestBuilder<Person> query = new DefaultTrippinService().getAllPeople().filter(lvl0);

        // Expectation for prefix usage:
        // -> 1
        //   a -> a1
        //     b -> b1
        //       c -> c1
        //       c -> c2
        //     b -> b1
        //   a -> b1
        // -> 2
        assertThat(query.toRequest().getRelativeUri())
            .hasParameter(
                "$filter",
                "(((UserName eq '1') and Friends/any(a:(((a/UserName eq 'a1') or a/Friends/all(b:(((b/UserName eq 'b1') and b/Friends/any(c:((c/UserName eq 'c1') or (c/UserName eq 'c2')))) and (b/UserName eq 'b2')))) or (a/UserName eq 'a2')))) or (UserName eq '2'))");
    }

    @Test
    public void testLambdaParameterLevelsForDifferentEntities()
    {
        final FilterableBoolean<PlanItem> lvl3 =
            PlanItem.CONFIRMATION_CODE.equalTo("c1").or(PlanItem.CONFIRMATION_CODE.equalTo("c2"));

        final FilterableBoolean<Trip> lvl2 =
            Trip.NAME.equalTo("b1").and(Trip.TO_PLAN_ITEMS.any(lvl3)).and(Trip.NAME.equalTo("b2"));

        final FilterableBoolean<Person> lvl1 =
            Person.USER_NAME.equalTo("a1").or(Person.TO_TRIPS.all(lvl2)).or(Person.USER_NAME.equalTo("a2"));

        final FilterableBoolean<Person> lvl0 =
            Person.USER_NAME.equalTo("1").and(Person.TO_FRIENDS.any(lvl1)).and(Person.USER_NAME.equalTo("2"));

        final GetAllRequestBuilder<Person> query = new DefaultTrippinService().getAllPeople().filter(lvl0);

        // Expectation for prefix usage:
        // -> 1
        //   a -> a1
        //     b -> b1
        //       c -> c1
        //       c -> c2
        //     b -> b1
        //   a -> b1
        // -> 2
        assertThat(query.toRequest().getRelativeUri())
            .hasParameter(
                "$filter",
                "(((UserName eq '1') and Friends/any(a:(((a/UserName eq 'a1') or a/Trips/all(b:(((b/Name eq 'b1') and b/PlanItems/any(c:((c/ConfirmationCode eq 'c1') or (c/ConfirmationCode eq 'c2')))) and (b/Name eq 'b2')))) or (a/UserName eq 'a2')))) and (UserName eq '2'))");
    }
}
