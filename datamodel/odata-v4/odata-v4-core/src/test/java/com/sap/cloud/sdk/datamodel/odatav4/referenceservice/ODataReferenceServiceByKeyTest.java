package com.sap.cloud.sdk.datamodel.odatav4.referenceservice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestReadByKey;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Trip;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.services.DefaultTrippinService;

public class ODataReferenceServiceByKeyTest
{
    private static final String testUserName = "testuser";
    private static final String keyWithSpecialCharacters = "test''user";
    private static final String fullQuery =
        DefaultTrippinService.DEFAULT_SERVICE_PATH + "/People('" + testUserName + "')?%s";
    private static final String fullQueryWithSpecialCharacters =
        DefaultTrippinService.DEFAULT_SERVICE_PATH + "/People('" + keyWithSpecialCharacters + "')";

    @Test
    public void testSelectAndExpand()
    {
        final ODataRequestReadByKey request =
            new DefaultTrippinService()
                .getPeopleByKey(testUserName)
                .select(Person.FIRST_NAME, Person.LAST_NAME)
                .select(Person.TO_TRIPS)
                .toRequest();

        final String expected = "$select=FirstName,LastName&$expand=Trips";

        assertThat(request.getQueryString()).isEqualTo(expected);
        assertThat(request.getRelativeUri()).hasToString(String.format(fullQuery, expected));
    }

    @Test
    public void testMultipleExpandsWithFilter()
    {
        final ODataRequestReadByKey request =
            new DefaultTrippinService()
                .getPeopleByKey(testUserName)
                .select(Person.TO_TRIPS.select(Trip.NAME))
                .select(Person.TO_BEST_FRIEND.select(Person.LAST_NAME))
                .toRequest();

        final String expected = "$expand=Trips($select=Name),BestFriend($select=LastName)";

        assertThat(request.getQueryString()).isEqualTo(expected);
        assertThat(request.getRelativeUri()).hasToString(String.format(fullQuery, expected));
    }

    @Test
    public void testAllSelectionVariants()
    {
        final ODataRequestReadByKey request =
            new DefaultTrippinService()
                .getPeopleByKey(testUserName)
                .select(Person.GENDER, Person.USER_NAME)
                .select(Person.EMAILS)
                .select(Person.TO_BEST_FRIEND, Person.TO_TRIPS, Person.TO_FRIENDS.top(2).skip(1))
                .toRequest();

        final String expected = "$select=Gender,UserName,Emails&$expand=BestFriend,Trips,Friends($top=2;$skip=1)";

        assertThat(request.getQueryString()).isEqualTo(expected);
        assertThat(request.getRelativeUri()).hasToString(String.format(fullQuery, expected));
    }

    @Test
    public void testGetByKeyWithSpecialCharacters()
    {
        final ODataRequestReadByKey request = new DefaultTrippinService().getPeopleByKey("test'user").toRequest();
        assertThat(request.getRelativeUri()).hasToString(fullQueryWithSpecialCharacters);
    }

    @Test
    public void testAllDuplicateSelections()
    {
        final ODataRequestReadByKey request =
            new DefaultTrippinService()
                .getPeopleByKey(testUserName)
                .select(Person.GENDER, Person.USER_NAME)
                .select(Person.GENDER, Person.USER_NAME)
                .select(Person.TO_BEST_FRIEND, Person.TO_TRIPS)
                .select(Person.TO_BEST_FRIEND, Person.TO_BEST_FRIEND.select(Person.TO_FRIENDS))
                .select(Person.TO_FRIENDS, Person.TO_FRIENDS.top(2).skip(1))
                .toRequest();

        final String expected =
            "$select=Gender,UserName&$expand=Trips,BestFriend($expand=Friends),Friends($top=2;$skip=1)";

        assertThat(request.getQueryString()).isEqualTo(expected);
        assertThat(request.getRelativeUri()).hasToString(String.format(fullQuery, expected));
    }
}
