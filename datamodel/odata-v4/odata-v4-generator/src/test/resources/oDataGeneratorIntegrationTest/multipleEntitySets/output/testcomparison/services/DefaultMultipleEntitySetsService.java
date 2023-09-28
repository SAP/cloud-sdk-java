package testcomparison.services;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odatav4.core.BatchRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.ServiceWithNavigableEntities;
import com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder;
import lombok.Getter;
import testcomparison.namespaces.multipleentitysets.FooType;
import testcomparison.namespaces.multipleentitysets.SimplePerson;


/**
 * <h3>Details:</h3><table summary='Details'><tr><td align='right'>OData Service:</td><td>multiple_entity_sets</td></tr></table>
 *
 */
public class DefaultMultipleEntitySetsService
    implements ServiceWithNavigableEntities, MultipleEntitySetsService
{

    @Nonnull
    @Getter
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
    public BatchRequestBuilder batch() {
        return new BatchRequestBuilder(servicePath);
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<SimplePerson> getAllFirstSimplePersons() {
        return new GetAllRequestBuilder<SimplePerson>(servicePath, SimplePerson.class, "A_FirstSimplePersons");
    }

    @Override
    @Nonnull
    public CountRequestBuilder<SimplePerson> countFirstSimplePersons() {
        return new CountRequestBuilder<SimplePerson>(servicePath, SimplePerson.class, "A_FirstSimplePersons");
    }

    @Override
    @Nonnull
    public GetByKeyRequestBuilder<SimplePerson> getFirstSimplePersonsByKey(final String person) {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("Person", person);
        return new GetByKeyRequestBuilder<SimplePerson>(servicePath, SimplePerson.class, key, "A_FirstSimplePersons");
    }

    @Override
    @Nonnull
    public CreateRequestBuilder<SimplePerson> createFirstSimplePersons(
        @Nonnull
        final SimplePerson simplePerson) {
        return new CreateRequestBuilder<SimplePerson>(servicePath, simplePerson, "A_FirstSimplePersons");
    }

    @Override
    @Nonnull
    public UpdateRequestBuilder<SimplePerson> updateFirstSimplePersons(
        @Nonnull
        final SimplePerson simplePerson) {
        return new UpdateRequestBuilder<SimplePerson>(servicePath, simplePerson, "A_FirstSimplePersons");
    }

    @Override
    @Nonnull
    public DeleteRequestBuilder<SimplePerson> deleteFirstSimplePersons(
        @Nonnull
        final SimplePerson simplePerson) {
        return new DeleteRequestBuilder<SimplePerson>(servicePath, simplePerson, "A_FirstSimplePersons");
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<SimplePerson> getAllSecondSimplePersons() {
        return new GetAllRequestBuilder<SimplePerson>(servicePath, SimplePerson.class, "A_SecondSimplePersons");
    }

    @Override
    @Nonnull
    public CountRequestBuilder<SimplePerson> countSecondSimplePersons() {
        return new CountRequestBuilder<SimplePerson>(servicePath, SimplePerson.class, "A_SecondSimplePersons");
    }

    @Override
    @Nonnull
    public GetByKeyRequestBuilder<SimplePerson> getSecondSimplePersonsByKey(final String person) {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("Person", person);
        return new GetByKeyRequestBuilder<SimplePerson>(servicePath, SimplePerson.class, key, "A_SecondSimplePersons");
    }

    @Override
    @Nonnull
    public CreateRequestBuilder<SimplePerson> createSecondSimplePersons(
        @Nonnull
        final SimplePerson simplePerson) {
        return new CreateRequestBuilder<SimplePerson>(servicePath, simplePerson, "A_SecondSimplePersons");
    }

    @Override
    @Nonnull
    public UpdateRequestBuilder<SimplePerson> updateSecondSimplePersons(
        @Nonnull
        final SimplePerson simplePerson) {
        return new UpdateRequestBuilder<SimplePerson>(servicePath, simplePerson, "A_SecondSimplePersons");
    }

    @Override
    @Nonnull
    public DeleteRequestBuilder<SimplePerson> deleteSecondSimplePersons(
        @Nonnull
        final SimplePerson simplePerson) {
        return new DeleteRequestBuilder<SimplePerson>(servicePath, simplePerson, "A_SecondSimplePersons");
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<FooType> getAllFooType() {
        return new GetAllRequestBuilder<FooType>(servicePath, FooType.class, "A_FooType");
    }

    @Override
    @Nonnull
    public CountRequestBuilder<FooType> countFooType() {
        return new CountRequestBuilder<FooType>(servicePath, FooType.class, "A_FooType");
    }

    @Override
    @Nonnull
    public GetByKeyRequestBuilder<FooType> getFooTypeByKey(final String foo) {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("Foo", foo);
        return new GetByKeyRequestBuilder<FooType>(servicePath, FooType.class, key, "A_FooType");
    }

    @Override
    @Nonnull
    public CreateRequestBuilder<FooType> createFooType(
        @Nonnull
        final FooType fooType) {
        return new CreateRequestBuilder<FooType>(servicePath, fooType, "A_FooType");
    }

    @Override
    @Nonnull
    public UpdateRequestBuilder<FooType> updateFooType(
        @Nonnull
        final FooType fooType) {
        return new UpdateRequestBuilder<FooType>(servicePath, fooType, "A_FooType");
    }

    @Override
    @Nonnull
    public DeleteRequestBuilder<FooType> deleteFooType(
        @Nonnull
        final FooType fooType) {
        return new DeleteRequestBuilder<FooType>(servicePath, fooType, "A_FooType");
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<FooType> getAllSecondFooType() {
        return new GetAllRequestBuilder<FooType>(servicePath, FooType.class, "A_SecondFooType");
    }

    @Override
    @Nonnull
    public CountRequestBuilder<FooType> countSecondFooType() {
        return new CountRequestBuilder<FooType>(servicePath, FooType.class, "A_SecondFooType");
    }

    @Override
    @Nonnull
    public GetByKeyRequestBuilder<FooType> getSecondFooTypeByKey(final String foo) {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("Foo", foo);
        return new GetByKeyRequestBuilder<FooType>(servicePath, FooType.class, key, "A_SecondFooType");
    }

    @Override
    @Nonnull
    public UpdateRequestBuilder<FooType> updateSecondFooType(
        @Nonnull
        final FooType fooType) {
        return new UpdateRequestBuilder<FooType>(servicePath, fooType, "A_SecondFooType");
    }

    @Override
    @Nonnull
    public DeleteRequestBuilder<FooType> deleteSecondFooType(
        @Nonnull
        final FooType fooType) {
        return new DeleteRequestBuilder<FooType>(servicePath, fooType, "A_SecondFooType");
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<FooType> getAllThirdFooType() {
        return new GetAllRequestBuilder<FooType>(servicePath, FooType.class, "A_ThirdFooType");
    }

    @Override
    @Nonnull
    public CountRequestBuilder<FooType> countThirdFooType() {
        return new CountRequestBuilder<FooType>(servicePath, FooType.class, "A_ThirdFooType");
    }

    @Override
    @Nonnull
    public GetByKeyRequestBuilder<FooType> getThirdFooTypeByKey(final String foo) {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("Foo", foo);
        return new GetByKeyRequestBuilder<FooType>(servicePath, FooType.class, key, "A_ThirdFooType");
    }

    @Override
    @Nonnull
    public CreateRequestBuilder<FooType> createThirdFooType(
        @Nonnull
        final FooType fooType) {
        return new CreateRequestBuilder<FooType>(servicePath, fooType, "A_ThirdFooType");
    }

    @Override
    @Nonnull
    public DeleteRequestBuilder<FooType> deleteThirdFooType(
        @Nonnull
        final FooType fooType) {
        return new DeleteRequestBuilder<FooType>(servicePath, fooType, "A_ThirdFooType");
    }

}
