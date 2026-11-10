package com.sap.cloud.sdk.datamodel.odatav4.referenceservice;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.services.DefaultTrippinService;

class EncodedParameterTest
{
    @Test
    void testGetByKeyWithSpecialCharacters()
    {
        final String fullQueryWithSpecialCharacters =
            DefaultTrippinService.DEFAULT_SERVICE_PATH + "/People('" + "test%2F%3F%20%23&user''%25$" + "')";
        final GetByKeyRequestBuilder<Person> request = new DefaultTrippinService().getPeopleByKey("test/? #&user'%$");
        assertThat(request.toRequest().getRelativeUri()).hasToString(fullQueryWithSpecialCharacters);
    }
}
