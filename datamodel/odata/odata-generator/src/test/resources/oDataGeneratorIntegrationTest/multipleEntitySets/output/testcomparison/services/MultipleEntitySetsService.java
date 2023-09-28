package testcomparison.services;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchService;
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
import testcomparison.namespaces.multipleentitysets.batch.MultipleEntitySetsServiceBatch;


/**
 * <h3>Details:</h3><table summary='Details'><tr><td align='right'>OData Service:</td><td>multiple_entity_sets</td></tr></table>
 *
 */
public interface MultipleEntitySetsService
    extends BatchService<MultipleEntitySetsServiceBatch>
{

    /**
     * If no other path was provided via the {@link #withServicePath(String)} method, this is the default service path used to access the endpoint.
     *
     */
    String DEFAULT_SERVICE_PATH = "/some/path/SOME_API";

    /**
     * Overrides the default service path and returns a new service instance with the specified service path. Also adjusts the respective entity URLs.
     *
     * @param servicePath
     *     Service path that will override the default.
     * @return
     *     A new service instance with the specified service path.
     */
    @Nonnull
    MultipleEntitySetsService withServicePath(
        @Nonnull
        final String servicePath);

    /**
     * Fetch multiple {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entities.
     *
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.multipleentitysets.SimplePersonFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    SimplePersonFluentHelper getAllFirstSimplePersons();

    /**
     * Fetch a single {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity using key fields.
     *
     * @param person
     *
     * @return
     *     A fluent helper to fetch a single {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.multipleentitysets.SimplePersonByKeyFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    SimplePersonByKeyFluentHelper getFirstSimplePersonsByKey(final String person);

    /**
     * Create a new {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity and save it to the S/4HANA system.
     *
     * @param simplePerson
     *     {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity object that will be created in the S/4HANA system.
     * @return
     *     A fluent helper to create a new {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity. To perform execution, call the {@link testcomparison.namespaces.multipleentitysets.SimplePersonCreateFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    SimplePersonCreateFluentHelper createFirstSimplePersons(
        @Nonnull
        final SimplePerson simplePerson);

    /**
     * Update an existing {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity and save it to the S/4HANA system.
     *
     * @param simplePerson
     *     {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity object that will be updated in the S/4HANA system.
     * @return
     *     A fluent helper to update an existing {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity. To perform execution, call the {@link testcomparison.namespaces.multipleentitysets.SimplePersonUpdateFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    SimplePersonUpdateFluentHelper updateFirstSimplePersons(
        @Nonnull
        final SimplePerson simplePerson);

    /**
     * Deletes an existing {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity in the S/4HANA system.
     *
     * @param simplePerson
     *     {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A fluent helper to delete an existing {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity. To perform execution, call the {@link testcomparison.namespaces.multipleentitysets.SimplePersonDeleteFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    SimplePersonDeleteFluentHelper deleteFirstSimplePersons(
        @Nonnull
        final SimplePerson simplePerson);

    /**
     * Fetch multiple {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entities.
     *
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.multipleentitysets.SimplePersonFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    SimplePersonFluentHelper getAllSecondSimplePersons();

    /**
     * Fetch a single {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity using key fields.
     *
     * @param person
     *
     * @return
     *     A fluent helper to fetch a single {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.multipleentitysets.SimplePersonByKeyFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    SimplePersonByKeyFluentHelper getSecondSimplePersonsByKey(final String person);

    /**
     * Create a new {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity and save it to the S/4HANA system.
     *
     * @param simplePerson
     *     {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity object that will be created in the S/4HANA system.
     * @return
     *     A fluent helper to create a new {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity. To perform execution, call the {@link testcomparison.namespaces.multipleentitysets.SimplePersonCreateFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    SimplePersonCreateFluentHelper createSecondSimplePersons(
        @Nonnull
        final SimplePerson simplePerson);

    /**
     * Update an existing {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity and save it to the S/4HANA system.
     *
     * @param simplePerson
     *     {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity object that will be updated in the S/4HANA system.
     * @return
     *     A fluent helper to update an existing {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity. To perform execution, call the {@link testcomparison.namespaces.multipleentitysets.SimplePersonUpdateFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    SimplePersonUpdateFluentHelper updateSecondSimplePersons(
        @Nonnull
        final SimplePerson simplePerson);

    /**
     * Deletes an existing {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity in the S/4HANA system.
     *
     * @param simplePerson
     *     {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A fluent helper to delete an existing {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity. To perform execution, call the {@link testcomparison.namespaces.multipleentitysets.SimplePersonDeleteFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    SimplePersonDeleteFluentHelper deleteSecondSimplePersons(
        @Nonnull
        final SimplePerson simplePerson);

    /**
     * Fetch multiple {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entities.
     *
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.multipleentitysets.FooTypeFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    FooTypeFluentHelper getAllFooType();

    /**
     * Fetch a single {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity using key fields.
     *
     * @param foo
     *
     * @return
     *     A fluent helper to fetch a single {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.multipleentitysets.FooTypeByKeyFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    FooTypeByKeyFluentHelper getFooTypeByKey(final String foo);

    /**
     * Create a new {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity and save it to the S/4HANA system.
     *
     * @param fooType
     *     {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity object that will be created in the S/4HANA system.
     * @return
     *     A fluent helper to create a new {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity. To perform execution, call the {@link testcomparison.namespaces.multipleentitysets.FooTypeCreateFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    FooTypeCreateFluentHelper createFooType(
        @Nonnull
        final FooType fooType);

    /**
     * Update an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity and save it to the S/4HANA system.
     *
     * @param fooType
     *     {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity object that will be updated in the S/4HANA system.
     * @return
     *     A fluent helper to update an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity. To perform execution, call the {@link testcomparison.namespaces.multipleentitysets.FooTypeUpdateFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    FooTypeUpdateFluentHelper updateFooType(
        @Nonnull
        final FooType fooType);

    /**
     * Deletes an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity in the S/4HANA system.
     *
     * @param fooType
     *     {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A fluent helper to delete an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity. To perform execution, call the {@link testcomparison.namespaces.multipleentitysets.FooTypeDeleteFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    FooTypeDeleteFluentHelper deleteFooType(
        @Nonnull
        final FooType fooType);

    /**
     * Fetch multiple {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entities.
     *
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.multipleentitysets.FooTypeFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    FooTypeFluentHelper getAllTypeWithoutKey();

    /**
     * Fetch a single {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity using key fields.
     *
     * @param foo
     *
     * @return
     *     A fluent helper to fetch a single {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.multipleentitysets.FooTypeByKeyFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    FooTypeByKeyFluentHelper getTypeWithoutKeyByKey(final String foo);

    /**
     * Create a new {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity and save it to the S/4HANA system.
     *
     * @param fooType
     *     {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity object that will be created in the S/4HANA system.
     * @return
     *     A fluent helper to create a new {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity. To perform execution, call the {@link testcomparison.namespaces.multipleentitysets.FooTypeCreateFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    FooTypeCreateFluentHelper createTypeWithoutKey(
        @Nonnull
        final FooType fooType);

    /**
     * Update an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity and save it to the S/4HANA system.
     *
     * @param fooType
     *     {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity object that will be updated in the S/4HANA system.
     * @return
     *     A fluent helper to update an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity. To perform execution, call the {@link testcomparison.namespaces.multipleentitysets.FooTypeUpdateFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    FooTypeUpdateFluentHelper updateTypeWithoutKey(
        @Nonnull
        final FooType fooType);

    /**
     * Deletes an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity in the S/4HANA system.
     *
     * @param fooType
     *     {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A fluent helper to delete an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity. To perform execution, call the {@link testcomparison.namespaces.multipleentitysets.FooTypeDeleteFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    FooTypeDeleteFluentHelper deleteTypeWithoutKey(
        @Nonnull
        final FooType fooType);

    /**
     * Fetch multiple {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entities.
     *
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.multipleentitysets.FooTypeFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    FooTypeFluentHelper getAllSecondFooType();

    /**
     * Fetch a single {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity using key fields.
     *
     * @param foo
     *
     * @return
     *     A fluent helper to fetch a single {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.multipleentitysets.FooTypeByKeyFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    FooTypeByKeyFluentHelper getSecondFooTypeByKey(final String foo);

    /**
     * Update an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity and save it to the S/4HANA system.
     *
     * @param fooType
     *     {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity object that will be updated in the S/4HANA system.
     * @return
     *     A fluent helper to update an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity. To perform execution, call the {@link testcomparison.namespaces.multipleentitysets.FooTypeUpdateFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    FooTypeUpdateFluentHelper updateSecondFooType(
        @Nonnull
        final FooType fooType);

    /**
     * Deletes an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity in the S/4HANA system.
     *
     * @param fooType
     *     {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A fluent helper to delete an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity. To perform execution, call the {@link testcomparison.namespaces.multipleentitysets.FooTypeDeleteFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    FooTypeDeleteFluentHelper deleteSecondFooType(
        @Nonnull
        final FooType fooType);

    /**
     * Fetch multiple {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entities.
     *
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.multipleentitysets.FooTypeFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    FooTypeFluentHelper getAllThirdFooType();

    /**
     * Fetch a single {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity using key fields.
     *
     * @param foo
     *
     * @return
     *     A fluent helper to fetch a single {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.multipleentitysets.FooTypeByKeyFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    FooTypeByKeyFluentHelper getThirdFooTypeByKey(final String foo);

    /**
     * Create a new {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity and save it to the S/4HANA system.
     *
     * @param fooType
     *     {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity object that will be created in the S/4HANA system.
     * @return
     *     A fluent helper to create a new {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity. To perform execution, call the {@link testcomparison.namespaces.multipleentitysets.FooTypeCreateFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    FooTypeCreateFluentHelper createThirdFooType(
        @Nonnull
        final FooType fooType);

    /**
     * Deletes an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity in the S/4HANA system.
     *
     * @param fooType
     *     {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A fluent helper to delete an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity. To perform execution, call the {@link testcomparison.namespaces.multipleentitysets.FooTypeDeleteFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    FooTypeDeleteFluentHelper deleteThirdFooType(
        @Nonnull
        final FooType fooType);

}
