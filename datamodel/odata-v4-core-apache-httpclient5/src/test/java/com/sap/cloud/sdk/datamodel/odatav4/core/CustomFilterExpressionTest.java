package com.sap.cloud.sdk.datamodel.odatav4.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FieldReference;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueBoolean;
import com.sap.cloud.sdk.datamodel.odatav4.expression.FilterableBoolean;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.services.DefaultTrippinService;

class CustomFilterExpressionTest
{
    @Test
    void requestBuilderHasExpectedParameters()
    {
        final ValueBoolean untypedExpression = FieldReference.of("MiddleName").equalTo("SomeName");
        final FilterableBoolean<Person> customFilterExpression =
            FilterableBoolean.fromCustomFilter(untypedExpression, Person.class);

        GetAllRequestBuilder<Person> requestBuilder =
            new DefaultTrippinService().getAllPeople().filter(customFilterExpression);
        assertThat(requestBuilder.toRequest().getRelativeUri()).hasParameter("$filter", "(MiddleName eq 'SomeName')");
    }

    @Test
    void requestBuilderHasExpectedComplexParameters()
    {
        final ValueBoolean untypedMultiExpression =
            FieldReference
                .of("MiddleName")
                .equalTo("SomeName")
                .and(FieldReference.of("YearsOfExperience").greaterThan(5));
        final FilterableBoolean<Person> customFilterExpression =
            FilterableBoolean.fromCustomFilter(untypedMultiExpression, Person.class);

        GetAllRequestBuilder<Person> requestBuilder =
            new DefaultTrippinService().getAllPeople().filter(customFilterExpression);
        assertThat(requestBuilder.toRequest().getRelativeUri())
            .hasParameter("$filter", "((MiddleName eq 'SomeName') and (YearsOfExperience gt 5))");

    }

    @Test
    void entityTypeIsCorrect()
    {
        final ValueBoolean untypedExpression = FieldReference.of("MiddleName").equalTo("SomeName");
        final FilterableBoolean<Person> customFilterExpression =
            FilterableBoolean.fromCustomFilter(untypedExpression, Person.class);

        assertThat(customFilterExpression.getEntityType()).isEqualTo(Person.class);
    }

    @Test
    void expressionDelegatedSuccessfully()
    {
        final ValueBoolean untypedExpression = FieldReference.of("MiddleName").equalTo("SomeName");
        final FilterableBoolean<Person> customFilterExpression =
            FilterableBoolean.fromCustomFilter(untypedExpression, Person.class);

        assertThat(customFilterExpression.getExpression(ODataProtocol.V4))
            .contains(untypedExpression.getExpression(ODataProtocol.V4));
    }

}
