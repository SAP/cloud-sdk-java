/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.metadata.generator;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.vavr.control.Try;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Value
@Slf4j
class JavaClassFromClasspath implements JavaClassExplorer
{
    @Nonnull
    @Override
    public Optional<ExploredClass> getClassByName(
        @Nonnull final String name,
        @Nonnull final List<ExploredType> genericTypeValues,
        @Nonnull final JavaClassExplorer callback )
    {
        final String binaryName = name.replaceAll("([A-Z].*?)\\.", "$1\\$"); // a.b.X.Y -> a.b.X$Y
        final Try<Class<?>> loadedClass = Try.of(() -> Class.forName(binaryName));
        if( loadedClass.isFailure() ) {
            return Optional.empty();
        }

        final Map<String, ExploredType> genericMapping = getGenericMapping(loadedClass.get(), genericTypeValues);
        if( genericMapping == null ) {
            return Optional.empty();
        }

        final ExploredClass exploredClass = getClassFromReference(loadedClass.get(), genericTypeValues, genericMapping);
        final ExploredClass exploredSuperClass = exploreSuperClass(loadedClass.get(), genericMapping, callback);
        final ExploredClass mergedClass = JavaClassExplorer.mergeMethods(exploredClass, exploredSuperClass);
        return Optional.of(mergedClass);
    }

    @Nonnull
    private static ExploredClass getClassFromReference(
        @Nonnull final Class<?> type,
        @Nonnull final List<ExploredType> genericTypeValues,
        @Nonnull final Map<String, ExploredType> genericMapping )
    {
        final String name = type.getCanonicalName();
        final List<ExploredMethod> methods =
            Stream
                .of(type.getDeclaredMethods())
                .filter(m -> Modifier.isPublic(m.getModifiers()))
                .map(m -> createMethod(m, genericMapping))
                .collect(Collectors.toList());

        return new ExploredClass(new ExploredType(name, genericTypeValues), methods);
    }

    @Nullable
    private static ExploredClass exploreSuperClass(
        @Nonnull final Class<?> child,
        @Nonnull final Map<String, ExploredType> genericMapping,
        @Nonnull final JavaClassExplorer callback )
    {
        final Type classReference = child.getGenericSuperclass();
        if( classReference == null || classReference == Object.class ) {
            return null;
        }

        final ExploredType type = getActualTypeArguments(classReference, genericMapping);
        final Optional<ExploredClass> exploredClass = callback.getClassByName(type.getName(), type.getParameters());
        return exploredClass.orElseGet(() -> {
            log.warn("Parent class {} could not be loaded.", type.getName());
            return null;
        });
    }

    @Nonnull
    private static
        ExploredMethod
        createMethod( @Nonnull final Method method, @Nonnull final Map<String, ExploredType> genericMapping )
    {
        final ExploredType returnType = createType(method.getGenericReturnType(), genericMapping);

        final Function<Parameter, ExploredType> parameterToType = p -> createType(p.getType(), genericMapping);

        final LinkedHashMap<String, ExploredType> arguments =
            Stream
                .of(method.getParameters())
                .collect(Collectors.toMap(Parameter::getName, parameterToType, ( o1, o2 ) -> o1, LinkedHashMap::new));

        return new ExploredMethod(method.getName(), returnType, arguments);
    }

    @Nonnull
    private static
        ExploredType
        createType( @Nonnull final Type returnType, @Nonnull final Map<String, ExploredType> genericMapping )
    {
        if( returnType instanceof ParameterizedType ) {
            return getActualTypeArguments(returnType, genericMapping);
        }
        if( returnType instanceof TypeVariable ) {
            final String variableName = ((TypeVariable<?>) returnType).getName();
            final ExploredType returnCandidate = genericMapping.get(variableName);
            return returnCandidate != null ? returnCandidate : new ExploredType(Object.class.getName());
        }
        if( returnType instanceof WildcardType ) {
            return new ExploredType("?");
        }
        return new ExploredType(returnType.getTypeName().replace('$', '.'));
    }

    @Nonnull
    private static
        ExploredType
        getActualTypeArguments( @Nonnull final Type type, @Nonnull final Map<String, ExploredType> genericMapping )
    {
        if( !(type instanceof ParameterizedType) ) {
            return new ExploredType(type.getTypeName());
        }

        final List<ExploredType> params =
            Stream
                .of(((ParameterizedType) type).getActualTypeArguments())
                .map(
                    t -> t instanceof TypeVariable
                        ? genericMapping.get(((TypeVariable<?>) t).getName())
                        : new ExploredType(t.getTypeName()))
                .collect(Collectors.toList());

        return new ExploredType(((ParameterizedType) type).getRawType().getTypeName().replace('$', '.'), params);
    }

    @Nullable
    private static
        Map<String, ExploredType>
        getGenericMapping( @Nonnull final Class<?> cl, @Nonnull final List<ExploredType> genericValues )
    {
        final TypeVariable<?>[] typeParameters = cl.getTypeParameters();
        if( typeParameters.length != genericValues.size() ) {
            log
                .debug(
                    "Number of generic type parameters {} does not fit the caller signature: {}",
                    typeParameters.length,
                    genericValues);
            return null;
        }

        final Map<String, ExploredType> genericMapping = new HashMap<>();
        for( int i = 0; i < typeParameters.length; i++ ) {
            genericMapping.put(typeParameters[i].getName(), genericValues.get(i));
        }
        return genericMapping;
    }
}
