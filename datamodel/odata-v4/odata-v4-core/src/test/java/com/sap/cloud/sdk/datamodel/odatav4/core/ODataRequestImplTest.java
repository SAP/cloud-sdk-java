/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Trip;

class ODataRequestImplTest
{
    @Test
    void testRequestWithoutNestedRequests()
    {
        final NavigationPropertyCollectionQuery<Person, Person> request =
            NavigationPropertyCollectionQuery.ofSubQuery("ASDF");

        request
            .select(Person.FIRST_NAME, Person.LAST_NAME)
            .filter(Person.FIRST_NAME.contains("foo"))
            .top(5)
            .skip(3)
            .orderBy(Person.USER_NAME.desc());

        final String expected =
            "$select=FirstName,LastName;$filter=contains(FirstName,'foo');$top=5;$skip=3;$orderby=UserName%20desc";
        final String unencodedExpected =
            "$select=FirstName,LastName;$filter=contains(FirstName,'foo');$top=5;$skip=3;$orderby=UserName desc";

        assertThat(request.getEncodedQueryString()).isEqualTo(expected);
        assertThat(request.getQueryString()).isEqualTo(unencodedExpected);
    }

    @Test
    void testRequestWithNestedRequests()
    {
        final NavigationPropertyCollectionQuery<Person, Person> request =
            NavigationPropertyCollectionQuery.ofSubQuery("ASDF");

        request
            .select(Person.FIRST_NAME, Person.LAST_NAME)
            .select(Person.TO_BEST_FRIEND.select(Person.TO_TRIPS.select(Trip.DESCRIPTION).top(10)))
            .filter(Person.FIRST_NAME.contains("foo"));

        final String expected =
            "$select=FirstName,LastName;$expand=BestFriend($expand=Trips($select=Description;$top=10));$filter=contains(FirstName,'foo')";
        final String unencodedExpected =
            "$select=FirstName,LastName;$expand=BestFriend($expand=Trips($select=Description;$top=10));$filter=contains(FirstName,'foo')";

        assertThat(request.getEncodedQueryString()).isEqualTo(expected);
        assertThat(request.getQueryString()).isEqualTo(unencodedExpected);
    }

}
