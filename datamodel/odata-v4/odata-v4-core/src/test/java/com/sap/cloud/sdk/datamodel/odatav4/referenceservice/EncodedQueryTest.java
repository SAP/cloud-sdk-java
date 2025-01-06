/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.referenceservice;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestRead;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Trip;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.services.DefaultTrippinService;

class EncodedQueryTest
{
    @Test
    void testEncodingInFilter()
    {
        final ODataRequestRead query =
            new DefaultTrippinService()
                .getAllPeople()
                .filter(
                    Person.USER_NAME
                        .length()
                        .greaterThanEqual(8)
                        .not()
                        .and(Person.FIRST_NAME.length().greaterThanEqual(10))
                        .or(Person.LAST_NAME.length().lessThanEqual(7)))
                .toRequest();

        final String expectedEncodedQuery =
            "$filter=(((not%20(length(UserName)%20ge%208))%20and%20(length(FirstName)%20ge%2010))%20or%20(length(LastName)%20le%207))";

        assertThat(query.getRequestQuery()).isEqualTo(expectedEncodedQuery);
    }

    @Test
    void testSafeCharsInFilter()
    {
        final ODataRequestRead query =
            new DefaultTrippinService().getAllPeople().filter(Person.USER_NAME.contains("_*-:,/'().")).toRequest();

        final String expectedEncodedQuery = "$filter=contains(UserName,'_*-:,/''().')";

        assertThat(query.getRequestQuery()).isEqualTo(expectedEncodedQuery);
    }

    @Test
    void testSpecialCharsInFilter()
    {
        final ODataRequestRead query =
            new DefaultTrippinService().getAllPeople().filter(Person.USER_NAME.contains("!@#$%^&=+|\\\"")).toRequest();

        final String expectedEncodedQuery = "$filter=contains(UserName,'%21%40%23%24%25%5E%26%3D%2B%7C%5C%22')";

        assertThat(query.getRequestQuery()).isEqualTo(expectedEncodedQuery);
    }

    @Test
    void testEncodingForeignCharactersInFilter()
    {
        final GetAllRequestBuilder<Person> query =
            new DefaultTrippinService()
                .getAllPeople()
                .filter(
                    Person.USER_NAME
                        .equalTo("François")
                        .and(Person.LAST_NAME.contains("Sørina").or(Person.FIRST_NAME.matches("维基百科"))));

        final String expectedEncodedQuery =
            "$filter=((UserName%20eq%20'Fran%C3%A7ois')%20and%20(contains(LastName,'S%C3%B8rina')%20or%20matchesPattern(FirstName,'%E7%BB%B4%E5%9F%BA%E7%99%BE%E7%A7%91')))";

        assertThat(query.toRequest().getRequestQuery()).isEqualTo(expectedEncodedQuery);
    }

    @Test
    void testEncodingInNestedFiltersWithAllSpecialCharacters()
    {
        final ODataRequestRead request =
            new DefaultTrippinService()
                .getAllPeople()
                .filter(Person.FIRST_NAME.contains("% $&#?\"\\+'"))
                .select(Person.TO_BEST_FRIEND.select(Person.TO_TRIPS.filter(Trip.NAME.contains("% $&#?\"\\+'"))))
                .toRequest();
        final String expected =
            "$expand=BestFriend($expand=Trips($filter=contains(Name,'%25%20%24%26%23%3F%22%5C%2B''')))&$filter=contains(FirstName,'%25%20%24%26%23%3F%22%5C%2B''')";

        assertThat(request.getRequestQuery()).isEqualTo(expected);
    }

    @Test
    void testEncodingInSearchQuery()
    {
        final ODataRequestRead search =
            new DefaultTrippinService().getAllPeople().search("Hash # Quoted \"string\" Escaped \\").toRequest();
        final String query = search.getRequestQuery();

        final String expected = "$search=%22Hash%20%23%20Quoted%20%5C%22string%5C%22%20Escaped%20%5C%5C%22";

        assertThat(query).isEqualTo(expected);
    }

    @Test
    void testEncodingInOrderBy()
    {
        final ODataRequestRead request =
            new DefaultTrippinService()
                .getAllPeople()
                .orderBy(Person.FIRST_NAME.asc(), Person.LAST_NAME.desc())
                .toRequest();

        final String expected = "$orderby=FirstName%20asc,LastName%20desc";

        assertThat(request.getRequestQuery()).isEqualTo(expected);
    }

    @Test
    void testEncodingInCustomQueryParametersWithAllSpecialCharacters()
    {
        final ODataRequestRead request =
            new DefaultTrippinService()
                .getAllPeople()
                .withQueryParameter("foo", "hash#tag")
                .withQueryParameter("param", "% $&#?\"\\+'bar")
                .toRequest();
        final String expected = "foo=hash%23tag&param=%25%20%24%26%23%3F%22%5C%2B'bar";

        assertThat(request.getRequestQuery()).isEqualTo(expected);
    }

    @Test
    void testSafeCharsInCustomQueryParameters()
    {
        final ODataRequestRead request =
            new DefaultTrippinService().getAllPeople().withQueryParameter("foo", "_*-:,/'().").toRequest();
        final String expected = "foo=_*-:,/'().";

        assertThat(request.getRequestQuery()).isEqualTo(expected);
    }
}
