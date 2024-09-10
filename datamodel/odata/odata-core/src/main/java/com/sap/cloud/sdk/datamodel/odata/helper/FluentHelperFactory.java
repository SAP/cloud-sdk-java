/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * Utility class to conveniently create fluent helper instances.
 */
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
class FluentHelperFactory
{
    @Nonnull
    private final String servicePath;

    static FluentHelperFactory withServicePath( @Nonnull final String servicePath )
    {
        return new FluentHelperFactory(servicePath);
    }

    <
        FluentHelperT extends FluentHelperByKey<FluentHelperT, EntityT, SelectableT>, EntityT extends VdmEntity<?>, SelectableT>
        FluentHelperByKey<FluentHelperT, EntityT, SelectableT>
        readByKey(
            @Nonnull final Class<EntityT> entityClass,
            @Nonnull final String entityCollection,
            @Nonnull final Map<String, Object> key )
    {
        return new FluentHelperByKey<>(servicePath, entityCollection)
        {
            @Nonnull
            @Override
            protected Map<String, Object> getKey()
            {
                return key;
            }

            @Nonnull
            @Override
            protected Class<EntityT> getEntityClass()
            {
                return entityClass;
            }
        };
    }

    <
        FluentHelperT extends FluentHelperRead<FluentHelperT, EntityT, SelectableT>, EntityT extends VdmEntity<?>, SelectableT>
        FluentHelperRead<FluentHelperT, EntityT, SelectableT>
        read( @Nonnull final Class<EntityT> entityClass, @Nonnull final String entityCollection )
    {
        return new FluentHelperRead<>(servicePath, entityCollection)
        {
            @Nonnull
            @Override
            protected Class<EntityT> getEntityClass()
            {
                return entityClass;
            }
        };
    }

    <
        FluentHelperT extends FluentHelperCreate<FluentHelperT, EntityT>, EntityT extends VdmEntity<?>>
        FluentHelperCreate<FluentHelperT, EntityT>
        create( @Nonnull final String entityCollection, @Nonnull final EntityT entity )
    {
        return new FluentHelperCreate<>(servicePath, entityCollection)
        {
            @Nonnull
            @Override
            protected EntityT getEntity()
            {
                return entity;
            }

            @SuppressWarnings( "unchecked" )
            @Nonnull
            @Override
            protected Class<EntityT> getEntityClass()
            {
                return (Class<EntityT>) entity.getClass();
            }
        };
    }

    <
        FluentHelperT extends FluentHelperCreate<FluentHelperT, EntityT>, EntityT extends VdmEntity<?>>
        FluentHelperCreate<FluentHelperT, EntityT>
        create( @Nonnull final EntityT entity )
    {
        return create(entity.getEntityCollection(), entity);
    }

    <
        FluentHelperT extends FluentHelperDelete<FluentHelperT, EntityT>, EntityT extends VdmEntity<?>>
        FluentHelperDelete<FluentHelperT, EntityT>
        delete( @Nonnull final String entityCollection, @Nonnull final EntityT entity )
    {
        return new FluentHelperDelete<>(servicePath, entityCollection)
        {
            @Nonnull
            @Override
            protected EntityT getEntity()
            {
                return entity;
            }

            @SuppressWarnings( "unchecked" )
            @Nonnull
            @Override
            protected Class<EntityT> getEntityClass()
            {
                return (Class<EntityT>) entity.getClass();
            }
        };
    }

    <
        FluentHelperT extends FluentHelperDelete<FluentHelperT, EntityT>, EntityT extends VdmEntity<?>>
        FluentHelperDelete<FluentHelperT, EntityT>
        delete( @Nonnull final EntityT entity )
    {
        return delete(entity.getEntityCollection(), entity);
    }

    <
        FluentHelperT extends FluentHelperUpdate<FluentHelperT, EntityT>, EntityT extends VdmEntity<?>>
        FluentHelperUpdate<FluentHelperT, EntityT>
        update( @Nonnull final String entityCollection, @Nonnull final EntityT entity )
    {
        return new FluentHelperUpdate<>(servicePath, entityCollection)
        {
            @Nonnull
            @Override
            protected EntityT getEntity()
            {
                return entity;
            }

            @SuppressWarnings( "unchecked" )
            @Nonnull
            @Override
            protected Class<EntityT> getEntityClass()
            {
                return (Class<EntityT>) entity.getClass();
            }
        };
    }

    <
        FluentHelperT extends FluentHelperUpdate<FluentHelperT, EntityT>, EntityT extends VdmEntity<?>>
        FluentHelperUpdate<FluentHelperT, EntityT>
        update( @Nonnull final EntityT entity )
    {
        return update(entity.getEntityCollection(), entity);
    }

    <
        FluentHelperT extends FluentHelperFunction<FluentHelperT, ObjectT, ResultT>, ObjectT, ResultT>
        FluentHelperFunction<FluentHelperT, ObjectT, ResultT>
        function(
            @Nonnull final Map<String, Object> parameters,
            @Nonnull final String functionName,
            @Nonnull final Class<ObjectT> objectClass,
            @Nonnull final Function<URI, HttpUriRequest> requestHandler,
            @Nonnull final BiFunction<FluentHelperFunction<FluentHelperT, ObjectT, ResultT>, Destination, ResultT> executeHandler )
    {
        return new FluentHelperFunction<>(servicePath)
        {
            @Nonnull
            @Override
            protected Map<String, Object> getParameters()
            {
                return parameters;
            }

            @Nonnull
            @Override
            protected String getFunctionName()
            {
                return functionName;
            }

            @Nonnull
            @Override
            protected HttpUriRequest createRequest( @Nonnull final URI uri )
            {
                return requestHandler.apply(uri);
            }

            @Nonnull
            @Override
            protected Class<? extends ObjectT> getEntityClass()
            {
                return objectClass;
            }

            @Nullable
            @Override
            public ResultT executeRequest( @Nonnull final Destination destination )
            {
                return executeHandler.apply(this, destination);
            }

            /**
             * {@inheritDoc} The special treatment for SAP S/4 HANA OData v2 responses is considered.
             */
            @Override
            @Nullable
            protected JsonElement refineJsonResponse( @Nullable JsonElement jsonElement )
            {
                if( jsonElement instanceof JsonObject && ((JsonObject) jsonElement).has(getFunctionName()) ) {
                    jsonElement = ((JsonObject) jsonElement).get(getFunctionName());
                }
                return super.refineJsonResponse(jsonElement);
            }
        };
    }

    <
        FluentHelperT extends FluentHelperFunction<FluentHelperT, ObjectT, ObjectT>, ObjectT>
        FluentHelperFunction<FluentHelperT, ObjectT, ObjectT>
        functionSinglePost(
            @Nonnull final Map<String, Object> parameters,
            @Nonnull final String functionName,
            @Nonnull final Class<ObjectT> objectClass )
    {
        return function(parameters, functionName, objectClass, HttpPost::new, FluentHelperFunction::executeSingle);
    }

    <
        FluentHelperT extends FluentHelperFunction<FluentHelperT, ObjectT, ObjectT>, ObjectT>
        FluentHelperFunction<FluentHelperT, ObjectT, ObjectT>
        functionSingleGet(
            @Nonnull final Map<String, Object> parameters,
            @Nonnull final String functionName,
            @Nonnull final Class<ObjectT> objectClass )
    {
        return function(parameters, functionName, objectClass, HttpGet::new, FluentHelperFunction::executeSingle);
    }

    <
        FluentHelperT extends FluentHelperFunction<FluentHelperT, ObjectT, List<ObjectT>>, ObjectT>
        FluentHelperFunction<FluentHelperT, ObjectT, List<ObjectT>>
        functionMultiplePost(
            @Nonnull final Map<String, Object> parameters,
            @Nonnull final String functionName,
            @Nonnull final Class<ObjectT> objectClass )
    {
        return function(parameters, functionName, objectClass, HttpPost::new, FluentHelperFunction::executeMultiple);
    }

    <
        FluentHelperT extends FluentHelperFunction<FluentHelperT, ObjectT, List<ObjectT>>, ObjectT>
        FluentHelperFunction<FluentHelperT, ObjectT, List<ObjectT>>
        functionMultipleGet(
            @Nonnull final Map<String, Object> parameters,
            @Nonnull final String functionName,
            @Nonnull final Class<ObjectT> objectClass )
    {
        return function(parameters, functionName, objectClass, HttpGet::new, FluentHelperFunction::executeMultiple);
    }
}
