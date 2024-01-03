/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.metadata.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.common.annotations.Beta;

import lombok.Value;

/**
 * Metadata about API usage.
 */
@Beta
public interface ApiUsageMetadata
{
    /**
     * Get constructor arguments.
     *
     * @return A sorted list of constructor arguments.
     */
    @Nonnull
    List<MethodArgument> getServiceConstructorArguments();

    /**
     * Get method invocations.
     *
     * @return A sorted list of method invocations.
     */
    @Nonnull
    List<Invocation> getServiceMethodInvocations();

    /**
     * Get fully-qualified service interface name.
     *
     * @return The service interface name.
     */
    @Nonnull
    String getQualifiedServiceInterfaceName();

    /**
     * Get fully-qualified service class name.
     *
     * @return The service class name.
     */
    @Nonnull
    String getQualifiedServiceClassName();

    /**
     * Get fully-qualified method result type.
     *
     * @return The method result type.
     */
    @Nonnull
    String getQualifiedServiceMethodResult();

    /**
     * A method argument.
     */
    interface MethodArgument
    {
        /**
         * Get argument code, e.g. variable name or static code.
         *
         * @return The argument code.
         */
        @Nonnull
        String getCode();
    }

    /**
     * Method invocation type.
     */
    @Value
    class Invocation
    {
        @Nonnull
        String method; // e.g. "getAllBusinessPartners"

        @Nonnull
        List<MethodArgument> arguments; // e.g. "sap-client" -> "java.util.String"

        /**
         * Convenient method to add an argument reference.
         *
         * @param name
         *            argument name
         * @param type
         *            argument type, fully qualified type name
         * @return A new instance with an additional argument
         */
        @Nonnull
        public Invocation arg( @Nonnull final String name, @Nonnull final Class<?> type )
        {
            final List<MethodArgument> args = new ArrayList<>(getArguments());
            args.add(new MethodArgumentDynamic(name, type.getName()));
            return new Invocation(getMethod(), args);
        }

        /**
         * Convenient method to add an argument reference.
         *
         * @param name
         *            argument name
         * @param typeName
         *            argument type, fully qualified type name
         * @return A new instance with an additional argument
         */
        @Nonnull
        public Invocation arg( @Nonnull final String name, @Nonnull final String typeName )
        {
            final List<MethodArgument> args = new ArrayList<>(getArguments());
            args.add(new MethodArgumentDynamic(name, typeName));
            return new Invocation(getMethod(), args);
        }

        /**
         * Convenient method to add static argument code.
         *
         * @param code
         *            argument code, e.g. "5" or "\"String\""
         * @return A new instance with an additional argument
         */
        @Nonnull
        public Invocation arg( @Nonnull final String code )
        {
            final List<MethodArgument> args = new ArrayList<>(getArguments());
            args.add(new MethodArgumentStatic(code));
            return new Invocation(getMethod(), args);
        }

        @Nonnull
        @Override
        public String toString()
        {
            final String args = arguments.stream().map(MethodArgument::getCode).collect(Collectors.joining(", "));
            return getMethod() + "(" + args + ")";
        }
    }

    /**
     * A method argument as static code.
     */
    @Value
    class MethodArgumentStatic implements MethodArgument
    {
        @Nonnull
        String code;

        @Nonnull
        @Override
        public String toString()
        {
            return getCode();
        }
    }

    /**
     * A method argument as variable reference.
     */
    @Value
    class MethodArgumentDynamic implements MethodArgument
    {
        @Nonnull
        String code;

        @Nonnull
        String typeName;

        @Nonnull
        @Override
        public String toString()
        {
            return getCode();
        }
    }

    /**
     * Convenience method to create a new method invocation.
     *
     * @param name
     *            method name
     * @return A new instance of a method invocation.
     */
    @Nonnull
    static Invocation method( @Nonnull final String name )
    {
        return new Invocation(name, Collections.emptyList());
    }

    /**
     * Convenient method to add static argument code.
     *
     * @param code
     *            argument code, e.g. "5" or "\"String\""
     * @return A new instance with an additional argument
     */
    @Nonnull
    static MethodArgument arg( @Nonnull final String code )
    {
        return new MethodArgumentStatic(code);
    }

    /**
     * Convenient method to add static argument code.
     *
     * @param code
     *            argument code, e.g. "5" or "\"String\""
     * @param type
     *            argument type, e.g. java.lang.String
     * @return A new instance with an additional argument
     */
    @Nonnull
    static MethodArgument arg( @Nonnull final String code, @Nonnull final Class<?> type )
    {
        return new MethodArgumentDynamic(code, type.getName());
    }
}
