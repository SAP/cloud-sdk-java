/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.multipleentitysets.batch;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchChangeSetFluentHelperBasic;
import testcomparison.namespaces.multipleentitysets.FooType;
import testcomparison.namespaces.multipleentitysets.SimplePerson;


/**
 * Implementation of the {@link MultipleEntitySetsServiceBatchChangeSet} interface, enabling you to combine multiple operations into one changeset. For further information have a look into the {@link testcomparison.services.MultipleEntitySetsService MultipleEntitySetsService}.
 * 
 */
public class DefaultMultipleEntitySetsServiceBatchChangeSet
    extends BatchChangeSetFluentHelperBasic<MultipleEntitySetsServiceBatch, MultipleEntitySetsServiceBatchChangeSet>
    implements MultipleEntitySetsServiceBatchChangeSet
{

    @Nonnull
    private final testcomparison.services.MultipleEntitySetsService service;

    DefaultMultipleEntitySetsServiceBatchChangeSet(
        @Nonnull
        final DefaultMultipleEntitySetsServiceBatch batchFluentHelper,
        @Nonnull
        final testcomparison.services.MultipleEntitySetsService service) {
        super(batchFluentHelper, batchFluentHelper);
        this.service = service;
    }

    @Nonnull
    @Override
    protected DefaultMultipleEntitySetsServiceBatchChangeSet getThis() {
        return this;
    }

    @Nonnull
    @Override
    public MultipleEntitySetsServiceBatchChangeSet createFirstSimplePersons(
        @Nonnull
        final SimplePerson simplePerson) {
        return addRequestCreate(service::createFirstSimplePersons, simplePerson);
    }

    @Nonnull
    @Override
    public MultipleEntitySetsServiceBatchChangeSet updateFirstSimplePersons(
        @Nonnull
        final SimplePerson simplePerson) {
        return addRequestUpdate(service::updateFirstSimplePersons, simplePerson);
    }

    @Nonnull
    @Override
    public MultipleEntitySetsServiceBatchChangeSet deleteFirstSimplePersons(
        @Nonnull
        final SimplePerson simplePerson) {
        return addRequestDelete(service::deleteFirstSimplePersons, simplePerson);
    }

    @Nonnull
    @Override
    public MultipleEntitySetsServiceBatchChangeSet createSecondSimplePersons(
        @Nonnull
        final SimplePerson simplePerson) {
        return addRequestCreate(service::createSecondSimplePersons, simplePerson);
    }

    @Nonnull
    @Override
    public MultipleEntitySetsServiceBatchChangeSet updateSecondSimplePersons(
        @Nonnull
        final SimplePerson simplePerson) {
        return addRequestUpdate(service::updateSecondSimplePersons, simplePerson);
    }

    @Nonnull
    @Override
    public MultipleEntitySetsServiceBatchChangeSet deleteSecondSimplePersons(
        @Nonnull
        final SimplePerson simplePerson) {
        return addRequestDelete(service::deleteSecondSimplePersons, simplePerson);
    }

    @Nonnull
    @Override
    public MultipleEntitySetsServiceBatchChangeSet createFooType(
        @Nonnull
        final FooType fooType) {
        return addRequestCreate(service::createFooType, fooType);
    }

    @Nonnull
    @Override
    public MultipleEntitySetsServiceBatchChangeSet updateFooType(
        @Nonnull
        final FooType fooType) {
        return addRequestUpdate(service::updateFooType, fooType);
    }

    @Nonnull
    @Override
    public MultipleEntitySetsServiceBatchChangeSet deleteFooType(
        @Nonnull
        final FooType fooType) {
        return addRequestDelete(service::deleteFooType, fooType);
    }

    @Nonnull
    @Override
    public MultipleEntitySetsServiceBatchChangeSet createTypeWithoutKey(
        @Nonnull
        final FooType fooType) {
        return addRequestCreate(service::createTypeWithoutKey, fooType);
    }

    @Nonnull
    @Override
    public MultipleEntitySetsServiceBatchChangeSet updateTypeWithoutKey(
        @Nonnull
        final FooType fooType) {
        return addRequestUpdate(service::updateTypeWithoutKey, fooType);
    }

    @Nonnull
    @Override
    public MultipleEntitySetsServiceBatchChangeSet deleteTypeWithoutKey(
        @Nonnull
        final FooType fooType) {
        return addRequestDelete(service::deleteTypeWithoutKey, fooType);
    }

    @Nonnull
    @Override
    public MultipleEntitySetsServiceBatchChangeSet updateSecondFooType(
        @Nonnull
        final FooType fooType) {
        return addRequestUpdate(service::updateSecondFooType, fooType);
    }

    @Nonnull
    @Override
    public MultipleEntitySetsServiceBatchChangeSet deleteSecondFooType(
        @Nonnull
        final FooType fooType) {
        return addRequestDelete(service::deleteSecondFooType, fooType);
    }

    @Nonnull
    @Override
    public MultipleEntitySetsServiceBatchChangeSet createThirdFooType(
        @Nonnull
        final FooType fooType) {
        return addRequestCreate(service::createThirdFooType, fooType);
    }

    @Nonnull
    @Override
    public MultipleEntitySetsServiceBatchChangeSet deleteThirdFooType(
        @Nonnull
        final FooType fooType) {
        return addRequestDelete(service::deleteThirdFooType, fooType);
    }

}
