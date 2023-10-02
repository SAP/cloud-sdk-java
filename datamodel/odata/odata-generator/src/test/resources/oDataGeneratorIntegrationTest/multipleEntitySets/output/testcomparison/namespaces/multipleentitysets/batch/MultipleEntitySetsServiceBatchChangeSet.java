/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.multipleentitysets.batch;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.FluentHelperBatchChangeSet;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.FluentHelperBatchEndChangeSet;
import testcomparison.namespaces.multipleentitysets.FooType;
import testcomparison.namespaces.multipleentitysets.SimplePerson;


/**
 * This interface enables you to combine multiple operations into one change set. For further information have a look into the {@link testcomparison.services.MultipleEntitySetsService MultipleEntitySetsService}.
 * 
 */
public interface MultipleEntitySetsServiceBatchChangeSet
    extends FluentHelperBatchChangeSet<MultipleEntitySetsServiceBatchChangeSet> , FluentHelperBatchEndChangeSet<MultipleEntitySetsServiceBatch>
{


    /**
     * Create a new {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity and save it to the S/4HANA system.
     * 
     * @param simplePerson
     *     {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity object that will be created in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    MultipleEntitySetsServiceBatchChangeSet createFirstSimplePersons(
        @Nonnull
        final SimplePerson simplePerson);

    /**
     * Update an existing {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity and save it to the S/4HANA system.
     * 
     * @param simplePerson
     *     {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity object that will be updated in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    MultipleEntitySetsServiceBatchChangeSet updateFirstSimplePersons(
        @Nonnull
        final SimplePerson simplePerson);

    /**
     * Deletes an existing {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity in the S/4HANA system.
     * 
     * @param simplePerson
     *     {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity object that will be deleted in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    MultipleEntitySetsServiceBatchChangeSet deleteFirstSimplePersons(
        @Nonnull
        final SimplePerson simplePerson);

    /**
     * Create a new {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity and save it to the S/4HANA system.
     * 
     * @param simplePerson
     *     {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity object that will be created in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    MultipleEntitySetsServiceBatchChangeSet createSecondSimplePersons(
        @Nonnull
        final SimplePerson simplePerson);

    /**
     * Update an existing {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity and save it to the S/4HANA system.
     * 
     * @param simplePerson
     *     {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity object that will be updated in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    MultipleEntitySetsServiceBatchChangeSet updateSecondSimplePersons(
        @Nonnull
        final SimplePerson simplePerson);

    /**
     * Deletes an existing {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity in the S/4HANA system.
     * 
     * @param simplePerson
     *     {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity object that will be deleted in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    MultipleEntitySetsServiceBatchChangeSet deleteSecondSimplePersons(
        @Nonnull
        final SimplePerson simplePerson);

    /**
     * Create a new {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity and save it to the S/4HANA system.
     * 
     * @param fooType
     *     {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity object that will be created in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    MultipleEntitySetsServiceBatchChangeSet createFooType(
        @Nonnull
        final FooType fooType);

    /**
     * Update an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity and save it to the S/4HANA system.
     * 
     * @param fooType
     *     {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity object that will be updated in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    MultipleEntitySetsServiceBatchChangeSet updateFooType(
        @Nonnull
        final FooType fooType);

    /**
     * Deletes an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity in the S/4HANA system.
     * 
     * @param fooType
     *     {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity object that will be deleted in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    MultipleEntitySetsServiceBatchChangeSet deleteFooType(
        @Nonnull
        final FooType fooType);

    /**
     * Create a new {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity and save it to the S/4HANA system.
     * 
     * @param fooType
     *     {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity object that will be created in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    MultipleEntitySetsServiceBatchChangeSet createTypeWithoutKey(
        @Nonnull
        final FooType fooType);

    /**
     * Update an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity and save it to the S/4HANA system.
     * 
     * @param fooType
     *     {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity object that will be updated in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    MultipleEntitySetsServiceBatchChangeSet updateTypeWithoutKey(
        @Nonnull
        final FooType fooType);

    /**
     * Deletes an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity in the S/4HANA system.
     * 
     * @param fooType
     *     {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity object that will be deleted in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    MultipleEntitySetsServiceBatchChangeSet deleteTypeWithoutKey(
        @Nonnull
        final FooType fooType);

    /**
     * Update an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity and save it to the S/4HANA system.
     * 
     * @param fooType
     *     {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity object that will be updated in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    MultipleEntitySetsServiceBatchChangeSet updateSecondFooType(
        @Nonnull
        final FooType fooType);

    /**
     * Deletes an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity in the S/4HANA system.
     * 
     * @param fooType
     *     {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity object that will be deleted in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    MultipleEntitySetsServiceBatchChangeSet deleteSecondFooType(
        @Nonnull
        final FooType fooType);

    /**
     * Create a new {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity and save it to the S/4HANA system.
     * 
     * @param fooType
     *     {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity object that will be created in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    MultipleEntitySetsServiceBatchChangeSet createThirdFooType(
        @Nonnull
        final FooType fooType);

    /**
     * Deletes an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity in the S/4HANA system.
     * 
     * @param fooType
     *     {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity object that will be deleted in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    MultipleEntitySetsServiceBatchChangeSet deleteThirdFooType(
        @Nonnull
        final FooType fooType);

}
