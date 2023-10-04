/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.services;

import javax.annotation.Nonnull;
import testcomparison.namespaces.multipleentitysets.FooType;
import testcomparison.namespaces.multipleentitysets.FooTypeByKeyFluentHelper;
import testcomparison.namespaces.multipleentitysets.FooTypeCreateFluentHelper;
import testcomparison.namespaces.multipleentitysets.FooTypeDeleteFluentHelper;
import testcomparison.namespaces.multipleentitysets.FooTypeFluentHelper;
import testcomparison.namespaces.multipleentitysets.FooTypeUpdateFluentHelper;
import testcomparison.namespaces.multipleentitysets.SimplePerson;
import testcomparison.namespaces.multipleentitysets.SimplePersonByKeyFluentHelper;
import testcomparison.namespaces.multipleentitysets.SimplePersonCreateFluentHelper;
import testcomparison.namespaces.multipleentitysets.SimplePersonDeleteFluentHelper;
import testcomparison.namespaces.multipleentitysets.SimplePersonFluentHelper;
import testcomparison.namespaces.multipleentitysets.SimplePersonUpdateFluentHelper;
import testcomparison.namespaces.multipleentitysets.batch.DefaultMultipleEntitySetsServiceBatch;


/**
 * <h3>Details:</h3><table summary='Details'><tr><td align='right'>OData Service:</td><td>multiple_entity_sets</td></tr></table>
 * 
 */
public class DefaultMultipleEntitySetsService
    implements MultipleEntitySetsService
{

    @Nonnull
    private final String servicePath;

    /**
     * Creates a service using {@link MultipleEntitySetsService#DEFAULT_SERVICE_PATH} to send the requests.
     * 
     */
    public DefaultMultipleEntitySetsService() {
        servicePath = MultipleEntitySetsService.DEFAULT_SERVICE_PATH;
    }

    /**
     * Creates a service using the provided service path to send the requests.
     * <p>
     * Used by the fluent {@link #withServicePath(String)} method.
     * 
     */
    private DefaultMultipleEntitySetsService(
        @Nonnull
        final String servicePath) {
        this.servicePath = servicePath;
    }

    @Override
    @Nonnull
    public DefaultMultipleEntitySetsService withServicePath(
        @Nonnull
        final String servicePath) {
        return new DefaultMultipleEntitySetsService(servicePath);
    }

    @Override
    @Nonnull
    public DefaultMultipleEntitySetsServiceBatch batch() {
        return new DefaultMultipleEntitySetsServiceBatch(this, servicePath);
    }

    @Override
    @Nonnull
    public SimplePersonFluentHelper getAllFirstSimplePersons() {
        return new SimplePersonFluentHelper(servicePath, "A_FirstSimplePersons");
    }

    @Override
    @Nonnull
    public SimplePersonByKeyFluentHelper getFirstSimplePersonsByKey(final String person) {
        return new SimplePersonByKeyFluentHelper(servicePath, "A_FirstSimplePersons", person);
    }

    @Override
    @Nonnull
    public SimplePersonCreateFluentHelper createFirstSimplePersons(
        @Nonnull
        final SimplePerson simplePerson) {
        return new SimplePersonCreateFluentHelper(servicePath, simplePerson, "A_FirstSimplePersons");
    }

    @Override
    @Nonnull
    public SimplePersonUpdateFluentHelper updateFirstSimplePersons(
        @Nonnull
        final SimplePerson simplePerson) {
        return new SimplePersonUpdateFluentHelper(servicePath, simplePerson, "A_FirstSimplePersons");
    }

    @Override
    @Nonnull
    public SimplePersonDeleteFluentHelper deleteFirstSimplePersons(
        @Nonnull
        final SimplePerson simplePerson) {
        return new SimplePersonDeleteFluentHelper(servicePath, simplePerson, "A_FirstSimplePersons");
    }

    @Override
    @Nonnull
    public SimplePersonFluentHelper getAllSecondSimplePersons() {
        return new SimplePersonFluentHelper(servicePath, "A_SecondSimplePersons");
    }

    @Override
    @Nonnull
    public SimplePersonByKeyFluentHelper getSecondSimplePersonsByKey(final String person) {
        return new SimplePersonByKeyFluentHelper(servicePath, "A_SecondSimplePersons", person);
    }

    @Override
    @Nonnull
    public SimplePersonCreateFluentHelper createSecondSimplePersons(
        @Nonnull
        final SimplePerson simplePerson) {
        return new SimplePersonCreateFluentHelper(servicePath, simplePerson, "A_SecondSimplePersons");
    }

    @Override
    @Nonnull
    public SimplePersonUpdateFluentHelper updateSecondSimplePersons(
        @Nonnull
        final SimplePerson simplePerson) {
        return new SimplePersonUpdateFluentHelper(servicePath, simplePerson, "A_SecondSimplePersons");
    }

    @Override
    @Nonnull
    public SimplePersonDeleteFluentHelper deleteSecondSimplePersons(
        @Nonnull
        final SimplePerson simplePerson) {
        return new SimplePersonDeleteFluentHelper(servicePath, simplePerson, "A_SecondSimplePersons");
    }

    @Override
    @Nonnull
    public FooTypeFluentHelper getAllFooType() {
        return new FooTypeFluentHelper(servicePath, "A_FooType");
    }

    @Override
    @Nonnull
    public FooTypeByKeyFluentHelper getFooTypeByKey(final String foo) {
        return new FooTypeByKeyFluentHelper(servicePath, "A_FooType", foo);
    }

    @Override
    @Nonnull
    public FooTypeCreateFluentHelper createFooType(
        @Nonnull
        final FooType fooType) {
        return new FooTypeCreateFluentHelper(servicePath, fooType, "A_FooType");
    }

    @Override
    @Nonnull
    public FooTypeUpdateFluentHelper updateFooType(
        @Nonnull
        final FooType fooType) {
        return new FooTypeUpdateFluentHelper(servicePath, fooType, "A_FooType");
    }

    @Override
    @Nonnull
    public FooTypeDeleteFluentHelper deleteFooType(
        @Nonnull
        final FooType fooType) {
        return new FooTypeDeleteFluentHelper(servicePath, fooType, "A_FooType");
    }

    @Override
    @Nonnull
    public FooTypeFluentHelper getAllTypeWithoutKey() {
        return new FooTypeFluentHelper(servicePath, "A_TypeWithoutKey");
    }

    @Override
    @Nonnull
    public FooTypeByKeyFluentHelper getTypeWithoutKeyByKey(final String foo) {
        return new FooTypeByKeyFluentHelper(servicePath, "A_TypeWithoutKey", foo);
    }

    @Override
    @Nonnull
    public FooTypeCreateFluentHelper createTypeWithoutKey(
        @Nonnull
        final FooType fooType) {
        return new FooTypeCreateFluentHelper(servicePath, fooType, "A_TypeWithoutKey");
    }

    @Override
    @Nonnull
    public FooTypeUpdateFluentHelper updateTypeWithoutKey(
        @Nonnull
        final FooType fooType) {
        return new FooTypeUpdateFluentHelper(servicePath, fooType, "A_TypeWithoutKey");
    }

    @Override
    @Nonnull
    public FooTypeDeleteFluentHelper deleteTypeWithoutKey(
        @Nonnull
        final FooType fooType) {
        return new FooTypeDeleteFluentHelper(servicePath, fooType, "A_TypeWithoutKey");
    }

    @Override
    @Nonnull
    public FooTypeFluentHelper getAllSecondFooType() {
        return new FooTypeFluentHelper(servicePath, "A_SecondFooType");
    }

    @Override
    @Nonnull
    public FooTypeByKeyFluentHelper getSecondFooTypeByKey(final String foo) {
        return new FooTypeByKeyFluentHelper(servicePath, "A_SecondFooType", foo);
    }

    @Override
    @Nonnull
    public FooTypeUpdateFluentHelper updateSecondFooType(
        @Nonnull
        final FooType fooType) {
        return new FooTypeUpdateFluentHelper(servicePath, fooType, "A_SecondFooType");
    }

    @Override
    @Nonnull
    public FooTypeDeleteFluentHelper deleteSecondFooType(
        @Nonnull
        final FooType fooType) {
        return new FooTypeDeleteFluentHelper(servicePath, fooType, "A_SecondFooType");
    }

    @Override
    @Nonnull
    public FooTypeFluentHelper getAllThirdFooType() {
        return new FooTypeFluentHelper(servicePath, "A_ThirdFooType");
    }

    @Override
    @Nonnull
    public FooTypeByKeyFluentHelper getThirdFooTypeByKey(final String foo) {
        return new FooTypeByKeyFluentHelper(servicePath, "A_ThirdFooType", foo);
    }

    @Override
    @Nonnull
    public FooTypeCreateFluentHelper createThirdFooType(
        @Nonnull
        final FooType fooType) {
        return new FooTypeCreateFluentHelper(servicePath, fooType, "A_ThirdFooType");
    }

    @Override
    @Nonnull
    public FooTypeDeleteFluentHelper deleteThirdFooType(
        @Nonnull
        final FooType fooType) {
        return new FooTypeDeleteFluentHelper(servicePath, fooType, "A_ThirdFooType");
    }

}
