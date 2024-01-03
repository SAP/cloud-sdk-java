/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.utility;

import static java.util.Comparator.comparingInt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaExecutable;
import com.thoughtworks.qdox.model.JavaParameter;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Helper class that enables finding the correct order of parameters for get-by-key and function/actions. Used only
 * INTERNALLY by the SDK and IS NOT intended for public usage.
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor( access = AccessLevel.PRIVATE )
@Beta
public class LegacyClassScanner
{
    @Nullable
    private File outputDir;

    /**
     * Enables a LegacyClassScanner where parameter reordering is skipped
     */
    public static final LegacyClassScanner DISABLED = new LegacyClassScanner();

    /**
     * Determine a list of argument-sets for a named method. It reads the existing argument sets for this method from
     * the class file present in the output directory and combines the new, incoming argument set.<br>
     * <p>
     * <strong>Example:</strong><br>
     * Let's assume legacy method "foo" exists in the old class. It is defined twice with arguments "a" and "a,b,c"
     * respectively. Now the generator reads a service specification where a method "foo" is referenced with arguments
     * "a,x,c,b". The result of this method will be: "a", "a,b,c", "a,b,c,x".
     * </p>
     *
     * @param className
     *            The fully qualified class name (including package), used to parse the legacy class file from output
     *            directory.
     * @param methodName
     *            The method name to determine multiple argument-sets for.
     * @param currentArguments
     *            A single set of arguments coming from generator input.
     * @param argumentNameLookup
     *            A function to get the parameter name.
     * @param <ParameterT>
     *            Currently either EntityPropertyModel or FunctionImportParameterModel or UnboundOperationParameterModel
     * @return The ordered combination of argument-sets for a specific method.
     */
    @Nonnull
    public <ParameterT> List<List<ParameterT>> determineArgumentsForMethod(
        @Nonnull final String className,
        @Nonnull final String methodName,
        @Nonnull final Iterable<ParameterT> currentArguments,
        @Nonnull final Function<ParameterT, String> argumentNameLookup )
    {
        final Function<JavaClass, List<List<JavaParameter>>> javaClassListFunction =
            javaClass -> javaClass
                .getMethods()
                .stream()
                .filter(method -> Objects.equals(method.getName(), methodName))
                .map(JavaExecutable::getParameters)
                .collect(Collectors.toList());

        return determineArguments(className, javaClassListFunction, currentArguments, argumentNameLookup);
    }

    /**
     * Determine a list of argument-sets for a constructor. It reads the existing argument sets for constructors of the
     * class present in the output directory and combines the new, incoming argument set.<br>
     * <p>
     * <strong>Example:</strong><br>
     * Let's assume there's a legacy class "Bar". It has two constructors with arguments "a" and "a,b,c" respectively.
     * Now the generator reads a service specification where a resulting class "Bar" is required with constructor
     * arguments "a,x,c,b". The result of this method will be: "a", "a,b,c", "a,b,c,x".
     * </p>
     *
     * @param className
     *            The fully qualified class name (including package), used to parse the legacy class file from output
     *            directory.
     * @param currentArguments
     *            A single set of arguments coming from generator input.
     * @param argumentNameLookup
     *            A function to get the parameter name.
     * @param <ParameterT>
     *            Currently EntityPropertyModel
     * @param numSkipArguments
     *            The number of arguments to skip in the parameters passed
     * @return The ordered combination of argument-sets for a specific constructor.
     */
    @Nonnull
    public <ParameterT> List<List<ParameterT>> determineArgumentsForConstructor(
        @Nonnull final String className,
        @Nonnull final Iterable<ParameterT> currentArguments,
        @Nonnull final Function<ParameterT, String> argumentNameLookup,
        final int numSkipArguments )
    {
        final Function<JavaClass, List<List<JavaParameter>>> javaClassListFunction =
            javaClass -> javaClass
                .getConstructors()
                .stream()
                .map(constructor -> Lists.newArrayList(Iterables.skip(constructor.getParameters(), numSkipArguments)))
                .collect(Collectors.toList());

        return determineArguments(className, javaClassListFunction, currentArguments, argumentNameLookup);
    }

    private <ParameterT> List<List<ParameterT>> determineArguments(
        @Nonnull final String className,
        @Nonnull final Function<JavaClass, List<List<JavaParameter>>> legacyArgumentSetsExtractor,
        @Nonnull final Iterable<ParameterT> currentParameters,
        @Nonnull final Function<ParameterT, String> parameterNameLookup )
    {
        final List<List<ParameterT>> fallbackResult = Collections.singletonList(Lists.newArrayList(currentParameters));

        // if no output directory is selected, skip the feature
        if( outputDir == null ) {
            log.trace("No output directory selected. Parameter reordering is skipped.");
            return fallbackResult;
        }

        // load collection of functions (argument sets) from legacy class
        final Try<JavaClass> legacyClass = Try.of(() -> getLegacyClass(className));
        if( legacyClass.isFailure() ) {
            log.debug("No legacy class found for {}. Parameter reordering is skipped.", className);
            return fallbackResult;
        }

        final Try<List<List<JavaParameter>>> legacyArgumentSetsRaw = legacyClass.map(legacyArgumentSetsExtractor);
        if( legacyArgumentSetsRaw.isFailure() ) {
            log.debug("Failed to lookup argument sets for legacy method in {}.", className);
            return fallbackResult;
        }
        if( legacyArgumentSetsRaw.get().isEmpty() ) {
            log.debug("No argument sets found for legacy method in {}. Parameter reordering is skipped.", className);
            return fallbackResult;
        }

        final List<List<ParameterT>> resultingArgumentSets =
            legacyArgumentSetsRaw
                .map(setsRaw -> getArgumentSets(setsRaw, currentParameters, parameterNameLookup))
                .onFailure(e -> log.error("Detected a breaking change due to missing at least one parameter."))
                .getOrElseThrow(e -> new IllegalStateException("Unable to find parameter from previous build.", e));

        // sort methods according to argument set size
        resultingArgumentSets.sort(comparingInt(List::size));

        // determine whether a new function needs to be generated, with additional arguments
        final Option<List<ParameterT>> newFunction = getNewArgumentSet(resultingArgumentSets, currentParameters);
        if( newFunction.isDefined() ) {
            log.debug("Add new function to existing class {} with parameters {}.", className, newFunction);
            resultingArgumentSets.add(newFunction.get());
        }
        return resultingArgumentSets;
    }

    @Nonnull
    private <ParameterT> Option<List<ParameterT>> getNewArgumentSet(
        @Nonnull final List<List<ParameterT>> legacyArgumentSets,
        @Nonnull final Iterable<ParameterT> currentArguments )
    {
        final List<ParameterT> biggestLegacyArgumentSet = Collections.max(legacyArgumentSets, comparingInt(List::size));
        final List<ParameterT> additionalArguments = Lists.newArrayList(currentArguments);
        additionalArguments.removeAll(biggestLegacyArgumentSet);

        // in case no differences were found between legacy argument set and current argument set
        if( additionalArguments.isEmpty() ) {
            return Option.none();
        }

        final List<ParameterT> additionalFunction = new ArrayList<>(biggestLegacyArgumentSet);
        additionalFunction.addAll(additionalArguments);
        return Option.some(additionalFunction);
    }

    @Nonnull
    private <ParameterT> List<List<ParameterT>> getArgumentSets(
        @Nonnull final List<List<JavaParameter>> legacyArgumentSets,
        @Nonnull final Iterable<ParameterT> currentArguments,
        @Nonnull final Function<ParameterT, String> argumentNameLookup )
    {
        final Map<String, ParameterT> paramLookup = Maps.uniqueIndex(currentArguments, argumentNameLookup::apply);

        final Function<JavaParameter, ParameterT> legacyParameterToCurrentParameter =
            p -> Objects.requireNonNull(paramLookup.get(p.getName()), p.getName());

        return legacyArgumentSets
            .stream()
            .map(ps -> ps.stream().map(legacyParameterToCurrentParameter).collect(Collectors.toList()))
            .collect(Collectors.toList());
    }

    @Nonnull
    private JavaClass getLegacyClass( @Nonnull final String className )
        throws IOException
    {
        final String relativeLegacyClassPath = className.replace(".", File.separator) + ".java";
        final File legacyClassFile = new File(outputDir, relativeLegacyClassPath);
        return new JavaProjectBuilder().addSource(legacyClassFile).getClasses().get(0);
    }
}
