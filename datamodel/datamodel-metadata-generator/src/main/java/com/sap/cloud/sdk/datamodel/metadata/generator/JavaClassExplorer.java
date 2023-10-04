/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.metadata.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.AllArgsConstructor;
import lombok.Value;

@FunctionalInterface
interface JavaClassExplorer
{
    @Value
    @AllArgsConstructor
    class ExploredType
    {
        @Nonnull
        String name;

        @Nonnull
        List<ExploredType> parameters;

        public ExploredType( @Nonnull final String name )
        {
            this(name, Collections.emptyList());
        }

        @Nonnull
        public String getFullName()
        {
            final Collection<ExploredType> params = getParameters();
            return params.isEmpty()
                ? getName()
                : getName()
                    + "<"
                    + params.stream().map(ExploredType::getFullName).collect(Collectors.joining(", "))
                    + ">";
        }
    }

    @Value
    class ExploredMethod
    {
        @Nonnull
        String name;

        @Nonnull
        ExploredType resultType;

        @Nonnull
        LinkedHashMap<String, ExploredType> arguments;
    }

    @Value
    class ExploredClass
    {
        @Nonnull
        ExploredType type;

        @Nonnull
        List<ExploredMethod> methods;
    }

    @Nonnull
    Optional<ExploredClass> getClassByName(
        @Nonnull final String name,
        @Nonnull final List<ExploredType> genericTypeValues,
        @Nonnull final JavaClassExplorer callback );

    @Nonnull
    default
        Optional<ExploredClass>
        getClassByName( @Nonnull final String name, @Nonnull final List<ExploredType> genericTypeValues )
    {
        return getClassByName(name, genericTypeValues, this);
    }

    @Nonnull
    static JavaClassExplorer of( @Nonnull final JavaClassExplorer... explorers )
    {
        return ( name, typeValues, callback ) -> Stream
            .of(explorers)
            .map(ex -> ex.getClassByName(name, typeValues, callback))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst();
    }

    @Nonnull
    static String ensureNamespace( @Nonnull final String packageName, @Nonnull final String className )
    {
        return className.contains(".") || packageName.isEmpty() ? className : packageName + "." + className;
    }

    @Nonnull
    static
        ExploredClass
        mergeMethods( @Nonnull final ExploredClass thisClass, @Nullable final ExploredClass otherClass )
    {
        if( otherClass == null ) {
            return thisClass;
        }
        final List<ExploredMethod> methods = new ArrayList<>(thisClass.getMethods());
        methods.addAll(otherClass.getMethods());
        return new ExploredClass(thisClass.getType(), methods);
    }
}
