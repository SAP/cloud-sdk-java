package com.sap.cloud.sdk.datamodel.odatav4.referenceservice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.SearchExpression;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.services.DefaultTrippinService;

public class SearchQueriesTest
{
    @Test
    public void testGetSearched()
    {
        final GetAllRequestBuilder<Person> search = new DefaultTrippinService().getAllPeople().search("Portland");
        assertThat(search.toRequest().getRelativeUri()).hasParameter("$search", "\"Portland\"");
    }

    @Test
    public void testSpecialCharacters()
    {
        final GetAllRequestBuilder<Person> search = new DefaultTrippinService().getAllPeople().search("Hash #");
        assertThat(search.toRequest().getRelativeUri()).hasParameter("$search", "\"Hash #\"");
    }

    @Test
    public void testGetSearchedPhrase()
    {
        final GetAllRequestBuilder<Person> search = new DefaultTrippinService().getAllPeople().search("United States");
        assertThat(search.toRequest().getRelativeUri()).hasParameter("$search", "\"United States\"");
    }

    @Test
    public void testGetSearchedWithQuotesInString()
    {
        final GetAllRequestBuilder<Person> search =
            new DefaultTrippinService().getAllPeople().search("Quoted \"string\"");

        // double quotes are escaped with simple backslash: Quote "string" -> Quoted \"string\"
        assertThat(search.toRequest().getRelativeUri()).hasParameter("$search", "\"Quoted \\\"string\\\"\"");
    }

    @Test
    public void testGetSearchedWithBackslashInString()
    {
        final GetAllRequestBuilder<Person> search = new DefaultTrippinService().getAllPeople().search("Escaped \\");

        // backslashes (=escape character) are escaped with backslashes: Escaped \ -> Escaped \\
        assertThat(search.toRequest().getRelativeUri()).hasParameter("$search", "\"Escaped \\\\\"");
    }

    @Test
    public void testSearchBooleanExpression()
    {

        final GetAllRequestBuilder<Person> search =
            new DefaultTrippinService().getAllPeople().search(SearchExpression.of("termA").or("termB").and("termC"));

        // double quotes are escaped with simple backslash: Quote "string" -> Quoted \"string\"
        // (("termA" OR "termB") AND "termC")
        assertThat(search.toRequest().getRelativeUri())
            .hasParameter("$search", "((\"termA\" OR \"termB\") AND \"termC\")");
    }

    @Test
    public void testSearchBooleanNot()
    {

        final GetAllRequestBuilder<Person> search =
            new DefaultTrippinService()
                .getAllPeople()
                .search(SearchExpression.of("termA").or("termB").and(SearchExpression.of("termC").not()));

        // double quotes are escaped with simple backslash: Quote "string" -> Quoted \"string\"
        // (("termA" OR "termB") AND NOT "termC")
        assertThat(search.toRequest().getRelativeUri())
            .hasParameter("$search", "((\"termA\" OR \"termB\") AND NOT \"termC\")");
    }
}
