package com.sap.cloud.sdk.datamodel.odatav4.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Location;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.services.DefaultTrippinService;

public class BoundActionTest
{
    private static final DefaultTrippinService service = new DefaultTrippinService().withServicePath("service-root");
    private static final Person person = Person.builder().userName("Fridolin").build();

    @Test
    void testActionOnEntityWithEtag()
    {
        final String targetUrl = "/service-root/People('Fridolin')/Trippin.MakeHappy";

        person.setVersionIdentifier("some-etag");
        final SingleValueActionRequestBuilder<Void> action = service.forEntity(person).applyAction(Person.makeHappy());

        assertThat(action.toRequest().getRelativeUri()).hasToString(targetUrl);
        assertThat(action.toRequest().getActionParameters()).hasToString("{}");
        assertThat(action.toRequest().getHeaders()).containsEntry("ETag", Collections.singletonList("some-etag"));
    }

    @Test
    void testActionOnEntityNoEtag()
    {
        final String targetUrl = "/service-root/People('Fridolin')/Trippin.MakeHappy";

        person.setVersionIdentifier(null);
        final SingleValueActionRequestBuilder<Void> action = service.forEntity(person).applyAction(Person.makeHappy());

        assertThat(action.toRequest().getRelativeUri()).hasToString(targetUrl);
        assertThat(action.toRequest().getActionParameters()).hasToString("{}");
        assertThat(action.toRequest().getHeaders()).doesNotContainEntry("ETag", Collections.singletonList("some-etag"));
    }

    @Test
    void testActionWithParameters()
    {
        final String targetUrl = "/service-root/People('Fridolin')/Trippin.MakeUnhappy";

        final SingleValueActionRequestBuilder<Person> action =
            service.forEntity(person).applyAction(Person.makeUnhappy(true));

        assertThat(action.toRequest().getRelativeUri()).hasToString(targetUrl);
        assertThat(action.toRequest().getActionParameters()).hasToString("{\"very\":true}");
    }

    @Test
    void testActionSingleToCollection()
    {
        final String targetUrl = "/service-root/People/Trippin.MakeAllHappy";

        final SingleValueActionRequestBuilder<Void> action = service.applyAction(Person.makeAllHappy());

        assertThat(action.toRequest().getRelativeUri()).hasToString(targetUrl);
    }

    @Test
    void testActionCollectionToCollection()
    {
        final String targetUrl = "/service-root/People('Fridolin')/Trippin.sendMail";

        final CollectionValueActionRequestBuilder<Person> action =
            service.forEntity(person).applyAction(Person.sendMail("foo"));

        assertThat(action.toRequest().getActionParameters()).isEqualTo("{\"subject\":\"foo\"}");
        assertThat(action.toRequest().getRelativeUri()).hasToString(targetUrl);
    }

    @Test
    void testActionOnNestedCollection()
    {
        final String targetUrl = "/service-root/People('Fridolin')/Friends/Trippin.MakeAllHappy";

        final SingleValueActionRequestBuilder<Void> action =
            service.forEntity(person).navigateTo(Person.TO_FRIENDS).applyAction(Person.makeAllHappy());

        assertThat(action.toRequest().getRelativeUri()).hasToString(targetUrl);
    }

    @Test
    void testSuccessfulCompilation()
    {
        // test to ensure that all the combinations at least compile

        // 1 - 1
        final SingleValueActionRequestBuilder<?> action1 =
            service
                .forEntity(person)
                .applyAction(
                    new BoundAction.SingleToSingle<>(Person.class, Location.class, "Stuff", Collections.emptyMap()));

        // 1 - n
        final CollectionValueActionRequestBuilder<?> action2 =
            service
                .forEntity(person)
                .applyAction(
                    new BoundAction.SingleToCollection<>(Person.class, String.class, "Stuff", Collections.emptyMap()));

        // n - 1
        final SingleValueActionRequestBuilder<?> action3 =
            service
                .applyAction(
                    new BoundAction.CollectionToSingle<>(Person.class, String.class, "Stuff", Collections.emptyMap()));

        // n - n
        final CollectionValueActionRequestBuilder<?> action4 =
            service
                .applyAction(
                    new BoundAction.CollectionToCollection<>(
                        Person.class,
                        String.class,
                        "Stuff",
                        Collections.emptyMap()));
    }
}
