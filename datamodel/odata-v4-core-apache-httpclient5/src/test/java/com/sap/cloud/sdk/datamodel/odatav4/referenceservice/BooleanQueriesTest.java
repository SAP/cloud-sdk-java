package com.sap.cloud.sdk.datamodel.odatav4.referenceservice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.services.DefaultTrippinService;

class BooleanQueriesTest
{
    @Test
    void testGetFilteredWithNot()
    {
        final GetAllRequestBuilder<Person> query =
            new DefaultTrippinService().getAllPeople().filter(Person.USER_NAME.length().greaterThanEqual(8).not());
        final String expectedUnencodedFilter = "(not (length(UserName) ge 8))";

        assertThat(query.toRequest().getRelativeUri()).hasParameter("$filter", expectedUnencodedFilter);
    }

    @Test
    void testGetFilteredWithAnd()
    {
        final GetAllRequestBuilder<Person> query =
            new DefaultTrippinService()
                .getAllPeople()
                .filter(Person.FIRST_NAME.length().lessThanEqual(8).and(Person.LAST_NAME.length().lessThanEqual(8)));
        final String expectedUnencodedFilter = "((length(FirstName) le 8) and (length(LastName) le 8))";

        assertThat(query.toRequest().getRelativeUri()).hasParameter("$filter", expectedUnencodedFilter);
    }

    @Test
    void testGetFilteredWithOr()
    {
        final GetAllRequestBuilder<Person> query =
            new DefaultTrippinService()
                .getAllPeople()
                .filter(Person.FIRST_NAME.length().lessThanEqual(8).or(Person.LAST_NAME.length().lessThanEqual(8)));
        final String expectedUnencodedFilter = "((length(FirstName) le 8) or (length(LastName) le 8))";

        assertThat(query.toRequest().getRelativeUri()).hasParameter("$filter", expectedUnencodedFilter);
    }
}
