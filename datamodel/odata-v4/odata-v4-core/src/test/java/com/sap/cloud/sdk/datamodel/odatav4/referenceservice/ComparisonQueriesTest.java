/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.datamodel.odatav4.referenceservice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.services.DefaultTrippinService;

class ComparisonQueriesTest
{
    @Test
    void testGetFilteredWithEquals()
    {
        final GetAllRequestBuilder<Person> query =
            new DefaultTrippinService().getAllPeople().filter(Person.TO_TRIPS.length().equalTo(5));
        final String expectedUnencodedFilter = "(length(Trips) eq 5)";

        assertThat(query.toRequest().getRelativeUri()).hasParameter("$filter", expectedUnencodedFilter);
    }

    @Test
    void testGetFilteredWithNotEquals()
    {
        final GetAllRequestBuilder<Person> query =
            new DefaultTrippinService().getAllPeople().filter(Person.TO_TRIPS.length().notEqualTo(10));
        final String expectedUnencodedFilter = "(length(Trips) ne 10)";

        assertThat(query.toRequest().getRelativeUri()).hasParameter("$filter", expectedUnencodedFilter);
    }

    @Test
    void testGetFilteredWithLessThan()
    {
        final GetAllRequestBuilder<Person> query =
            new DefaultTrippinService().getAllPeople().filter(Person.TO_TRIPS.length().lessThan(2));
        final String expectedUnencodedFilter = "(length(Trips) lt 2)";

        assertThat(query.toRequest().getRelativeUri()).hasParameter("$filter", expectedUnencodedFilter);
    }

    @Test
    void testGetFilteredWithGreaterThan()
    {
        final GetAllRequestBuilder<Person> query =
            new DefaultTrippinService().getAllPeople().filter(Person.TO_TRIPS.length().greaterThan(15));
        final String expectedUnencodedFilter = "(length(Trips) gt 15)";

        assertThat(query.toRequest().getRelativeUri()).hasParameter("$filter", expectedUnencodedFilter);
    }

    @Test
    void testGetFilteredWithLessThanOrEquals()
    {
        final GetAllRequestBuilder<Person> query =
            new DefaultTrippinService().getAllPeople().filter(Person.TO_TRIPS.length().lessThanEqual(1));
        final String expectedUnencodedFilter = "(length(Trips) le 1)";

        assertThat(query.toRequest().getRelativeUri()).hasParameter("$filter", expectedUnencodedFilter);
    }

    @Test
    void testGetFilteredWithGreaterThanOrEquals()
    {
        final GetAllRequestBuilder<Person> query =
            new DefaultTrippinService().getAllPeople().filter(Person.TO_TRIPS.length().greaterThanEqual(20));
        final String expectedUnencodedFilter = "(length(Trips) ge 20)";

        assertThat(query.toRequest().getRelativeUri()).hasParameter("$filter", expectedUnencodedFilter);
    }

    @Test
    void testGetFilteredWithInLiterals()
    {
        final GetAllRequestBuilder<Person> query =
            new DefaultTrippinService().getAllPeople().filter(Person.USER_NAME.in("scottketchum", "javieralfred"));
        final String expectedUnencodedFilter = "(UserName in ('scottketchum','javieralfred'))";

        assertThat(query.toRequest().getRelativeUri()).hasParameter("$filter", expectedUnencodedFilter);
    }

    @Test
    void testGetFilteredWithInSimpleCollection()
    {
        final GetAllRequestBuilder<Person> query =
            new DefaultTrippinService().getAllPeople().filter(Person.USER_NAME.in(Person.EMAILS));
        final String expectedUnencodedFilter = "(UserName in Emails)";

        assertThat(query.toRequest().getRelativeUri()).hasParameter("$filter", expectedUnencodedFilter);
    }

    @Test
    void testGetFilteredWithInNavigationPropertyCollection()
    {
        final GetAllRequestBuilder<Person> query =
            new DefaultTrippinService().getAllPeople().filter(Person.TO_BEST_FRIEND.in(Person.TO_FRIENDS));
        final String expectedUnencodedFilter = "(BestFriend in Friends)";

        assertThat(query.toRequest().getRelativeUri()).hasParameter("$filter", expectedUnencodedFilter);
    }

    @Test
    void testGetFilteredWithInEnumCollection()
    {
        final GetAllRequestBuilder<Person> query =
            new DefaultTrippinService().getAllPeople().filter(Person.FAVORITE_FEATURE.in(Person.FEATURES));
        final String expectedUnencodedFilter = "(FavoriteFeature in Features)";

        assertThat(query.toRequest().getRelativeUri()).hasParameter("$filter", expectedUnencodedFilter);
    }
}
