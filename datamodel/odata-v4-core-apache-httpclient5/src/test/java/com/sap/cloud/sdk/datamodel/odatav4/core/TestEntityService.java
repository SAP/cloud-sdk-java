package com.sap.cloud.sdk.datamodel.odatav4.core;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableMap;

interface TestEntityService
{
    String SERVICE_PATH = "/";

    @Nonnull
    default GetAllRequestBuilder<TestEntity> getTestEntities()
    {
        return new GetAllRequestBuilder<>(SERVICE_PATH, TestEntity.class, "EntityCollection");
    }

    @Nonnull
    default CountRequestBuilder<TestEntity> countTestEntities()
    {
        return new CountRequestBuilder<>(SERVICE_PATH, TestEntity.class, "EntityCollection");
    }

    @Nonnull
    default GetByKeyRequestBuilder<TestEntity> getTestEntitiesByKey( @Nonnull final String key )
    {
        return new GetByKeyRequestBuilder<>(
            SERVICE_PATH,
            TestEntity.class,
            ImmutableMap.of("key", key),
            "EntityCollection");
    }

    @Nonnull
    default CreateRequestBuilder<TestEntity> createTestEntity( @Nonnull final TestEntity testEntity )
    {
        return new CreateRequestBuilder<>(SERVICE_PATH, testEntity, "EntityCollection");
    }

    @Nonnull
    default DeleteRequestBuilder<TestEntity> deleteTestEntity( @Nonnull final TestEntity testEntity )
    {
        return new DeleteRequestBuilder<>(SERVICE_PATH, testEntity, "EntityCollection");
    }

    @Nonnull
    default UpdateRequestBuilder<TestEntity> updateTestEntity( @Nonnull final TestEntity testEntity )
    {
        return new UpdateRequestBuilder<>(SERVICE_PATH, testEntity, "EntityCollection");
    }

    @Nonnull
    default SingleValueActionRequestBuilder<Integer> actionSingleResult()
    {
        return new SingleValueActionRequestBuilder<>(
            SERVICE_PATH,
            "action-single",
            ImmutableMap.of("secret", "pass"),
            Integer.class);
    }

    @Nonnull
    default CollectionValueActionRequestBuilder<String> actionMultipleResult()
    {
        return new CollectionValueActionRequestBuilder<>(
            SERVICE_PATH,
            "action-multiple",
            ImmutableMap.of("secret", "pass"),
            String.class);
    }

    @Nonnull
    default SingleValueFunctionRequestBuilder<Integer> functionSingleResult()
    {
        return new SingleValueFunctionRequestBuilder<>(
            SERVICE_PATH,
            "function-single",
            ImmutableMap.of("secret", "pass"),
            Integer.class);
    }

    @Nonnull
    default CollectionValueFunctionRequestBuilder<String> functionMultipleResult()
    {
        return new CollectionValueFunctionRequestBuilder<>(
            SERVICE_PATH,
            "function-multiple",
            ImmutableMap.of("secret", "pass"),
            String.class);
    }

    @Nonnull
    default BatchRequestBuilder batch()
    {
        final AtomicInteger uuidCounter = new AtomicInteger();
        return new BatchRequestBuilder(SERVICE_PATH)
        {
            @Override
            protected Supplier<UUID> getUuidProvider()
            {
                return () -> new UUID(0, uuidCounter.incrementAndGet());
            }
        };
    }
}
