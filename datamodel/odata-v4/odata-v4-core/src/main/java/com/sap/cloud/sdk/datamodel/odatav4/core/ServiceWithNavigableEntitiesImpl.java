package com.sap.cloud.sdk.datamodel.odatav4.core;

import javax.annotation.Nonnull;

import org.apache.http.HttpHeaders;

import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;

import io.vavr.control.Option;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

class ServiceWithNavigableEntitiesImpl
{
    @RequiredArgsConstructor
    static class EntitySingle<EntityT extends VdmEntity<EntityT>> implements NavigableEntitySingle<EntityT>
    {
        /**
         * The service path of the entity.
         */
        @Nonnull
        protected final String servicePath;

        /**
         * The path to the entity.
         */
        @Nonnull
        protected final ODataResourcePath entityPath;

        /**
         * The entity to be navigated.
         */
        @Nonnull
        protected final Option<EntityT> maybeEntity;

        @Nonnull
        private final Class<EntityT> entityType;

        @Nonnull
        @Override
        public GetByKeyRequestBuilder<EntityT> get()
        {
            return new GetByKeyRequestBuilder<>(servicePath, entityPath, entityType);
        }

        @Nonnull
        @Override
        public UpdateRequestBuilder<EntityT> update( @Nonnull final EntityT item )
        {
            return new UpdateRequestBuilder<>(servicePath, entityPath, item);
        }

        @Nonnull
        @Override
        public DeleteRequestBuilder<EntityT> delete()
        {
            return new DeleteRequestBuilder<>(
                servicePath,
                entityPath,
                maybeEntity.getOrElse(() -> new VdmEntityUtil<>(entityType).newInstance()));
        }

        @Nonnull
        @Override
        public <NavigationT extends VdmEntity<NavigationT>> NavigableEntityCollection<NavigationT> navigateTo(
            @Nonnull final NavigationProperty.Collection<EntityT, NavigationT> property )
        {
            entityPath.addSegment(property.getFieldName());
            return new EntityCollection<>(servicePath, entityPath, property.getItemType());
        }

        @Nonnull
        @Override
        public <NavigationT extends VdmEntity<NavigationT>> NavigableEntitySingle<NavigationT> navigateTo(
            @Nonnull final NavigationProperty.Single<EntityT, NavigationT> property )
        {
            entityPath.addSegment(property.getFieldName());
            return new EntitySingle<>(servicePath, entityPath, Option.none(), property.getItemType());
        }

        @Nonnull
        @Override
        public <ResultT> SingleValueFunctionRequestBuilder<ResultT> applyFunction(
            @Nonnull final BoundFunction.SingleToSingle<EntityT, ResultT> function )
        {
            return newSingleValueFunctionRequestBuilder(servicePath, entityPath, function);
        }

        @Nonnull
        @Override
        public <ResultT> CollectionValueFunctionRequestBuilder<ResultT> applyFunction(
            @Nonnull final BoundFunction.SingleToCollection<EntityT, ResultT> function )
        {
            return newCollectionValueFunctionRequestBuilder(servicePath, entityPath, function);
        }

        @Override
        @Nonnull
        public <ResultT extends VdmEntity<ResultT>> NavigableEntitySingle<ResultT> withFunction(
            @Nonnull final BoundFunction.SingleToSingleEntity.Composable<EntityT, ResultT> function )
        {
            entityPath.addSegment(function.getQualifiedName(), function.getParameters());
            return new EntitySingle<>(servicePath, entityPath, Option.none(), function.getReturnType());
        }

        @Override
        @Nonnull
        public <ResultT extends VdmEntity<ResultT>> NavigableEntityCollection<ResultT> withFunction(
            @Nonnull final BoundFunction.SingleToCollectionEntity.Composable<EntityT, ResultT> function )
        {
            entityPath.addSegment(function.getQualifiedName(), function.getParameters());
            return new EntityCollection<>(servicePath, entityPath, function.getReturnType());
        }

        @Override
        @Nonnull
        public <ResultT> SingleValueActionRequestBuilder<ResultT> applyAction(
            @Nonnull final BoundAction.SingleToSingle<EntityT, ResultT> action )
        {
            entityPath.addSegment(action.getQualifiedName());
            final SingleValueActionRequestBuilder<ResultT> requestBuilder =
                new SingleValueActionRequestBuilder<>(
                    servicePath,
                    entityPath,
                    action.getParameters(),
                    action.getReturnType());
            maybeEntity
                .filter(e -> e.getVersionIdentifier().isDefined())
                .map(VdmEntity::getVersionIdentifier)
                .filter(Option::isDefined)
                .map(Option::get)
                .forEach(eTag -> requestBuilder.withHeader(HttpHeaders.IF_MATCH, eTag));
            return requestBuilder;
        }

        @Override
        @Nonnull
        public <ResultT> CollectionValueActionRequestBuilder<ResultT> applyAction(
            @Nonnull final BoundAction.SingleToCollection<EntityT, ResultT> action )
        {
            entityPath.addSegment(action.getQualifiedName());
            final CollectionValueActionRequestBuilder<ResultT> requestBuilder =
                new CollectionValueActionRequestBuilder<>(
                    servicePath,
                    entityPath,
                    action.getParameters(),
                    action.getReturnType());
            maybeEntity
                .filter(e -> e.getVersionIdentifier().isDefined())
                .map(VdmEntity::getVersionIdentifier)
                .filter(Option::isDefined)
                .map(Option::get)
                .forEach(eTag -> requestBuilder.withHeader(HttpHeaders.IF_MATCH, eTag));
            return requestBuilder;
        }
    }

    @RequiredArgsConstructor
    static class EntityCollection<NavigationT extends VdmEntity<NavigationT>>
        implements
        NavigableEntityCollection<NavigationT>
    {
        @Getter
        private final String servicePath;
        private final ODataResourcePath entityPath;
        private final Class<NavigationT> navigationType;

        @Nonnull
        @Override
        public <EntityT extends VdmEntity<EntityT>> NavigableEntitySingle<EntityT> forEntity(
            @Nonnull final EntityT entity )
        {
            entityPath.addParameterToLastSegment(entity.getKey());
            return new EntitySingle<>(servicePath, entityPath, Option.of(entity), entity.getType());
        }

        @Override
        @Nonnull
        public GetAllRequestBuilder<NavigationT> getAll()
        {
            return new GetAllRequestBuilder<>(servicePath, entityPath, navigationType);
        }

        @Override
        @Nonnull
        public CreateRequestBuilder<NavigationT> create( @Nonnull final NavigationT item )
        {
            return new CreateRequestBuilder<>(servicePath, entityPath, item);
        }

        @Override
        @Nonnull
        public CountRequestBuilder<NavigationT> count()
        {
            return new CountRequestBuilder<>(servicePath, entityPath, navigationType);
        }

        @Nonnull
        @Override
        public <
            EntityT extends VdmEntity<EntityT>, ResultT extends VdmEntity<ResultT>>
            NavigableEntitySingle<ResultT>
            withFunction( @Nonnull final BoundFunction.CollectionToSingleEntity.Composable<EntityT, ResultT> function )
        {
            entityPath.addSegment(function.getQualifiedName(), function.getParameters());
            return new ServiceWithNavigableEntitiesImpl.EntitySingle<>(
                servicePath,
                entityPath,
                Option.none(),
                function.getReturnType());
        }

        @Nonnull
        @Override
        public <
            EntityT extends VdmEntity<EntityT>, ResultT extends VdmEntity<ResultT>>
            NavigableEntityCollection<ResultT>
            withFunction(
                @Nonnull final BoundFunction.CollectionToCollectionEntity.Composable<EntityT, ResultT> function )
        {
            entityPath.addSegment(function.getQualifiedName(), function.getParameters());
            return new ServiceWithNavigableEntitiesImpl.EntityCollection<>(
                servicePath,
                entityPath,
                function.getReturnType());
        }

        @Nonnull
        @Override
        public <EntityT extends VdmEntity<EntityT>, ResultT> CollectionValueActionRequestBuilder<ResultT> applyAction(
            @Nonnull final BoundAction.CollectionToCollection<EntityT, ResultT> action )
        {
            return new CollectionValueActionRequestBuilder<>(
                servicePath,
                entityPath.addSegment(action.getQualifiedName()),
                action.getParameters(),
                action.getReturnType());
        }

        @Nonnull
        @Override
        public <EntityT extends VdmEntity<EntityT>, ResultT> SingleValueActionRequestBuilder<ResultT> applyAction(
            @Nonnull final BoundAction.CollectionToSingle<EntityT, ResultT> action )
        {
            return new SingleValueActionRequestBuilder<>(
                servicePath,
                entityPath.addSegment(action.getQualifiedName()),
                action.getParameters(),
                action.getReturnType());
        }

        @Nonnull
        @Override
        public <EntityT extends VdmEntity<EntityT>, ResultT> SingleValueFunctionRequestBuilder<ResultT> applyFunction(
            @Nonnull final BoundFunction.CollectionToSingle<EntityT, ResultT> function )
        {
            return newSingleValueFunctionRequestBuilder(servicePath, entityPath, function);
        }

        @Nonnull
        @Override
        public <
            EntityT extends VdmEntity<EntityT>, ResultT> CollectionValueFunctionRequestBuilder<ResultT> applyFunction(
                @Nonnull final BoundFunction.CollectionToCollection<EntityT, ResultT> function )
        {
            return newCollectionValueFunctionRequestBuilder(servicePath, entityPath, function);
        }
    }

    private static <
        EntityT extends VdmEntity<EntityT>, ResultT>
        SingleValueFunctionRequestBuilder<ResultT>
        newSingleValueFunctionRequestBuilder(
            final String servicePath,
            final ODataResourcePath entityPath,
            final BoundFunction<EntityT, ResultT> function )
    {
        return new SingleValueFunctionRequestBuilder<>(
            servicePath,
            entityPath.addSegment(function.getQualifiedName(), function.getParameters()),
            function.getReturnType());
    }

    private static <
        EntityT extends VdmEntity<EntityT>, ResultT>
        CollectionValueFunctionRequestBuilder<ResultT>
        newCollectionValueFunctionRequestBuilder(
            final String servicePath,
            final ODataResourcePath entityPath,
            final BoundFunction<EntityT, ResultT> function )
    {
        return new CollectionValueFunctionRequestBuilder<>(
            servicePath,
            entityPath.addSegment(function.getQualifiedName(), function.getParameters()),
            function.getReturnType());
    }
}
