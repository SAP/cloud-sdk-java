/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Location;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.services.DefaultTrippinService;

class BoundFunctionTest
{
    private static final DefaultTrippinService service = new DefaultTrippinService().withServicePath("service-root");
    private static final Person person = Person.builder().userName("Fridolin").build();

    /* From OData V4 ABNF

    ; boundOperation segments can only be composed if the type of the previous segment
    ; matches the type of the first parameter of the action or function being called.
    ; Note that the rule name reflects the return type of the function.
    boundOperation = "/" ( boundActionCall
                     / boundEntityColFunctionCall    [ collectionNavigation ]
                     / boundEntityFunctionCall       [ singleNavigation ]
                     / boundComplexColFunctionCall   [ complexColPath ]
                     / boundComplexFunctionCall      [ complexPath ]
                     / boundPrimitiveColFunctionCall [ primitiveColPath ]
                     / boundPrimitiveFunctionCall    [ primitivePath ]
                     / boundFunctionCallNoParens
                     )
     */

    // Problem: We don't have a good abstraction for entity sets
    // Consequence: forEntity(..) doesn't work if there is more than 1 entity set for the type
    // the same problem has this approach

    @Test
    void testEntityToPrimitive()
    {
        final String targetUrl = "/service-root/People('Fridolin')/Trippin.IsHappy()";

        final SingleValueFunctionRequestBuilder<Boolean> function =
            service.forEntity(person).applyFunction(Person.isHappy());

        assertThat(function.toRequest().getRelativeUri()).hasToString(targetUrl);
    }

    @Test
    void testNavigationToPrimitive()
    {
        final String targetUrl = "/service-root/People('Fridolin')/BestFriend/Trippin.IsHappy()";

        final SingleValueFunctionRequestBuilder<Boolean> function =
            service.forEntity(person).navigateTo(Person.TO_BEST_FRIEND).applyFunction(Person.isHappy());

        assertThat(function.toRequest().getRelativeUri()).hasToString(targetUrl);
    }

    @Test
    void testCollectionToPrimitive()
    {
        final String targetUrl = "/service-root/People('Fridolin')/Friends/Trippin.AreAllFriends()";

        final SingleValueFunctionRequestBuilder<Boolean> function =
            service.forEntity(person).navigateTo(Person.TO_FRIENDS).applyFunction(Person.areAllFriends());

        assertThat(function.toRequest().getRelativeUri()).hasToString(targetUrl);
    }

    @Test
    void testCollectionToCollection()
    {
        final String targetUrl = "/service-root/People/Trippin.MostPopularPersons()";

        final CollectionValueFunctionRequestBuilder<Person> function =
            service.applyFunction(Person.mostPopularPersons());

        assertThat(function.toRequest().getRelativeUri()).hasToString(targetUrl);
    }

    @Test
    void testCollectionOnRootEntity()
    {
        final String targetUrl = "/service-root/People/Trippin.AreAllFriends()";

        final SingleValueFunctionRequestBuilder<Boolean> function = service.applyFunction(Person.areAllFriends());

        assertThat(function.toRequest().getRelativeUri()).hasToString(targetUrl);
    }

    @Test
    void testNavigationCollectionToPrimitive()
    {
        final String targetUrl = "/service-root/People('Fridolin')/Friends/Trippin.AreAllFriends()";

        final SingleValueFunctionRequestBuilder<Boolean> function =
            service.forEntity(person).navigateTo(Person.TO_FRIENDS).applyFunction(Person.areAllFriends());

        assertThat(function.toRequest().getRelativeUri()).hasToString(targetUrl);
    }

    @Test
    void testEntityToEntity()
    {
        final String targetUrl = "/service-root/People('Fridolin')/Trippin.WorstFriend()";

        final SingleValueFunctionRequestBuilder<Person> function =
            service.forEntity(person).applyFunction(Person.worstFriend());

        assertThat(function.toRequest().getRelativeUri()).hasToString(targetUrl);
    }

    @Test
    void testCompositionOnEntity()
    {
        final String targetUrl = "/service-root/People('Fridolin')/Trippin.WorstFriend()/Friends?$top=5";

        final GetAllRequestBuilder<Person> request =
            service.forEntity(person).withFunction(Person.worstFriend()).navigateTo(Person.TO_FRIENDS).getAll().top(5);

        assertThat(request.toRequest().getRelativeUri()).hasToString(targetUrl);
    }

    @Test
    void testCompositionOnRootEntity()
    {
        final String targetUrl = "/service-root/People/Trippin.MostPopularPerson()/Friends?$top=5";

        final GetAllRequestBuilder<Person> request =
            service.withFunction(Person.mostPopularPerson()).navigateTo(Person.TO_FRIENDS).getAll().top(5);

        assertThat(request.toRequest().getRelativeUri()).hasToString(targetUrl);
    }

    @Test
    void testGetAllEntitiesAfterFunctionResult()
    {
        final String targetUrl = "/service-root/People/Trippin.MostPopularPersons()";

        final GetAllRequestBuilder<Person> request = service.withFunction(Person.mostPopularPersons()).getAll();

        assertThat(request.toRequest().getRelativeUri()).hasToString(targetUrl);
    }

    @Test
    void testSubsequentFunctionInvocation()
    {
        final String targetUrl = "/service-root/People/Trippin.MostPopularPersons()/Trippin.AreAllFriends()";

        final SingleValueFunctionRequestBuilder<Boolean> request =
            service.withFunction(Person.mostPopularPersons()).applyFunction(Person.areAllFriends());

        assertThat(request.toRequest().getRelativeUri()).hasToString(targetUrl);
    }

    @Test
    void testAdditionalParameters()
    {
        final String targetUrl = "/service-root/People('Fridolin')/Trippin.IsHappy(really=true)";

        final SingleValueFunctionRequestBuilder<Boolean> function =
            service.forEntity(person).applyFunction(Person.isHappy(true));

        assertThat(function.toRequest().getRelativeUri()).hasToString(targetUrl);
    }

    @Test
    void testSuccessfulCompilation()
    {
        // test to ensure that all the combinations at least compile

        // 1 - 1
        SingleValueFunctionRequestBuilder<?> fun1;
        fun1 =
            service
                .forEntity(person)
                .applyFunction(
                    new BoundFunction.SingleToSingleComplex<>(
                        Person.class,
                        Location.class,
                        "Stuff",
                        Collections.emptyMap()));
        fun1 =
            service
                .forEntity(person)
                .applyFunction(
                    new BoundFunction.SingleToSingleEntity<>(
                        Person.class,
                        Person.class,
                        "Stuff",
                        Collections.emptyMap()));
        fun1 =
            service
                .forEntity(person)
                .applyFunction(
                    new BoundFunction.SingleToSinglePrimitive<>(
                        Person.class,
                        String.class,
                        "Stuff",
                        Collections.emptyMap()));

        // 1 - n
        CollectionValueFunctionRequestBuilder<?> fun2;
        fun2 =
            service
                .forEntity(person)
                .applyFunction(
                    new BoundFunction.SingleToCollectionPrimitive<>(
                        Person.class,
                        String.class,
                        "Stuff",
                        Collections.emptyMap()));
        fun2 =
            service
                .forEntity(person)
                .applyFunction(
                    new BoundFunction.SingleToCollectionComplex<>(
                        Person.class,
                        Location.class,
                        "Stuff",
                        Collections.emptyMap()));
        fun2 =
            service
                .forEntity(person)
                .applyFunction(
                    new BoundFunction.SingleToCollectionEntity<>(
                        Person.class,
                        Person.class,
                        "Stuff",
                        Collections.emptyMap()));

        // n - 1
        SingleValueFunctionRequestBuilder<?> fun3;
        fun3 =
            service
                .applyFunction(
                    new BoundFunction.CollectionToSinglePrimitive<>(
                        Person.class,
                        String.class,
                        "Stuff",
                        Collections.emptyMap()));
        fun3 =
            service
                .applyFunction(
                    new BoundFunction.CollectionToSingleComplex<>(
                        Person.class,
                        Location.class,
                        "Stuff",
                        Collections.emptyMap()));
        fun3 =
            service
                .applyFunction(
                    new BoundFunction.CollectionToSingleEntity<>(
                        Person.class,
                        Person.class,
                        "Stuff",
                        Collections.emptyMap()));

        // n - n
        CollectionValueFunctionRequestBuilder<?> fun4;
        fun2 =
            service
                .applyFunction(
                    new BoundFunction.CollectionToCollectionPrimitive<>(
                        Person.class,
                        String.class,
                        "Stuff",
                        Collections.emptyMap()));
        fun2 =
            service
                .applyFunction(
                    new BoundFunction.CollectionToCollectionComplex<>(
                        Person.class,
                        Location.class,
                        "Stuff",
                        Collections.emptyMap()));
        fun2 =
            service
                .applyFunction(
                    new BoundFunction.CollectionToCollectionEntity<>(
                        Person.class,
                        Person.class,
                        "Stuff",
                        Collections.emptyMap()));

    }

    /*
    @Test
    void testBoundFunctionOnPrimitiveType()
    {
        // Target URI: /service-root/People('Fridolin')/FirstName/Model.String.ToUpperCase()

        SingleValueFunctionRequestBuilder<String> function =
            service
                .forEntity(person)
                // overload this to support both primitive and complex types
                .onProperty(Person.FIRST_NAME)
                // Function is of type: String -> String
                .applyFunction(TrippinService.PrimitiveFunctions.toUpperCase());
    }*/

    /*
    @Test
    void testBoundFunctionOnComplexType()
    {
        // Target URI: /service-root/People('Fridolin')/AddressInfo/Model.Address.GetClosestLocation()

        SingleValueFunctionRequestBuilder<String> function =
            service
                .forEntity(person)
                .onProperty(Person.ADDRESS_INFO)
                // Function is of type: Collection<Location> -> Location
                .applyFunction(Location.getClosestLocation());
    }*/

    /*
    @Test
    void testBoundFunctionOnEachEntityInCollection()
    {
        // Target URI: /service-root/People/$each/Trippin.isHappy()

        SingleValueFunctionRequestBuilder<Boolean> function =
                service
                        // overload this to support both functions on single entities and collections
                        // automatically apply $each if the function is of type: EntityT -> Any
                        .applyFunction(Person.isHappy());

    }*/
}
