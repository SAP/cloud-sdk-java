/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.metadata.generator;

import static com.sap.cloud.sdk.datamodel.metadata.generator.JavaClassExplorer.ExploredClass;
import static com.sap.cloud.sdk.datamodel.metadata.generator.JavaClassExplorer.ExploredMethod;
import static com.sap.cloud.sdk.datamodel.metadata.generator.JavaClassExplorer.ExploredType;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import lombok.ToString;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

/**
 * Helper class to resolve service method and return type from existing Java code.
 */
@Slf4j
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
@ToString
public class JavaServiceMethodResolver
{
    @Nonnull
    @Getter
    private final List<ApiUsageMetadata.Invocation> invocations;

    @Nonnull
    @Getter
    private final String resultType;

    @SuppressWarnings( "unused" ) // used by Lombok builder
    @Builder
    @Nonnull
    private static Optional<JavaServiceMethodResolver> resolve(
        @Nonnull final Path sourceDirectory,
        @Nonnull final String qualifiedServiceName,
        @Nullable final String[] priorityByMethodNamePrefix,
        @Nullable final ApiUsageMetadata.Invocation finalMethod,
        @Nonnull @Singular final List<AdditionalInvocation> additionalInvocations,
        @Nonnull @Singular final Set<String> excludedMethodNames )
    {
        final JavaClassExplorer explorer =
            JavaClassExplorer.of(new JavaClassFromClasspath(), new JavaClassFromParser(sourceDirectory));

        final MethodPrioritySelector prioritySelector =
            new MethodPrioritySelector(priorityByMethodNamePrefix, excludedMethodNames);

        final ExploredMethod method = lookupMethod(explorer, qualifiedServiceName, prioritySelector, new ArrayList<>());
        if( method == null ) {
            log.debug("No suitable method found.");
            return Optional.empty();
        }

        final ExploredType methodResultType = method.getResultType();
        String resultType = methodResultType.getFullName();

        final List<ApiUsageMetadata.Invocation> invocations = new ArrayList<>();
        invocations.add(getInvocationFromMethod(method));
        invocations.addAll(getInvocationsByPrefix(prioritySelector.getMethodPrefix(method), additionalInvocations));

        if( finalMethod != null ) {
            final MethodBySignatureSelector signatureSelector = new MethodBySignatureSelector(finalMethod);
            final ExploredMethod finalMethodLookup =
                lookupMethod(explorer, methodResultType.getName(), signatureSelector, methodResultType.getParameters());

            if( finalMethodLookup != null ) {
                invocations.add(finalMethod);
                resultType = finalMethodLookup.getResultType().getFullName();
            }
        }

        return Optional.of(new JavaServiceMethodResolver(invocations, resultType));
    }

    private static ApiUsageMetadata.Invocation getInvocationFromMethod( final ExploredMethod method )
    {
        ApiUsageMetadata.Invocation invocation = ApiUsageMetadata.method(method.getName());
        for( final Map.Entry<String, ExploredType> argEntry : method.getArguments().entrySet() ) {
            invocation = invocation.arg(argEntry.getKey(), argEntry.getValue().getFullName());
        }
        return invocation;
    }

    @Nullable
    private static ExploredMethod lookupMethod(
        final JavaClassExplorer javaExplorer,
        final String qualifiedServiceName,
        final MethodSelector selector,
        final List<ExploredType> typeArguments )
    {
        final Optional<ExploredClass> cl = javaExplorer.getClassByName(qualifiedServiceName, typeArguments);
        final List<ExploredMethod> methods = cl.map(ExploredClass::getMethods).orElseGet(Collections::emptyList);
        return selector.select(methods);
    }

    interface MethodSelector
    {
        @Nullable
        ExploredMethod select( @Nonnull final Collection<ExploredMethod> methods );
    }

    @Nonnull
    private static List<ApiUsageMetadata.Invocation> getInvocationsByPrefix(
        @Nullable final String methodPrefix,
        @Nonnull final List<AdditionalInvocation> additionalInvocations )
    {
        return methodPrefix == null
            ? Collections.emptyList()
            : additionalInvocations
                .stream()
                .filter(inv -> Objects.equals(inv.prefix, methodPrefix))
                .flatMap(a -> a.invocations.stream())
                .collect(Collectors.toList());
    }

    /**
     * Convenience method to define an additional service method invocation, e.g.
     * {@code forPrefix("getAll").add(ApiUsageMetadata.method("top").arg("5"))}.
     *
     * @param prefix
     *            The service method name prefix to apply this additional method invocation to.
     * @return An instance to continuously add invocations.
     */
    @Nonnull
    public static AdditionalInvocation forPrefix( @Nonnull final String prefix )
    {
        return new AdditionalInvocation(prefix);
    }

    /**
     * Helper class to store additional service method invocations, grouped by method name prefix.
     */
    @RequiredArgsConstructor( access = AccessLevel.PRIVATE )
    public static class AdditionalInvocation
    {
        private final String prefix;
        private final List<ApiUsageMetadata.Invocation> invocations = new ArrayList<>();

        /**
         * Add an invocation.
         *
         * @param invocation
         *            A service method invocation.
         * @return The same helper instance.
         */
        @Nonnull
        public AdditionalInvocation add( @Nonnull final ApiUsageMetadata.Invocation invocation )
        {
            this.invocations.add(invocation);
            return this;
        }
    }

    @Value
    private static class MethodPrioritySelector implements MethodSelector
    {
        private static ToIntFunction<ExploredMethod> methodToArgumentSize = m -> m.getArguments().size();
        private static ToIntFunction<ExploredMethod> methodToNameLength = m -> m.getName().length();

        @Nullable
        String[] priorityByMethodNamePrefix;

        @Nonnull
        Set<String> excludedMethodNames;

        @Override
        @Nullable
        public ExploredMethod select( @Nonnull final Collection<ExploredMethod> methods )
        {
            final Comparator<ExploredMethod> methodComparator =
                Comparator
                    .comparingInt(this::getMethodNamePriorityIndex)
                    .thenComparingInt(methodToArgumentSize)
                    .thenComparingInt(methodToNameLength);

            final List<ExploredMethod> filteredMethods = new ArrayList<>(methods);
            filteredMethods.removeIf(m -> excludedMethodNames.contains(m.getName()));
            return filteredMethods.isEmpty() ? null : Collections.min(filteredMethods, methodComparator);
        }

        private int getMethodNamePriorityIndex( final ExploredMethod method )
        {
            if( priorityByMethodNamePrefix == null ) {
                return 0;
            }

            return IntStream
                .range(0, priorityByMethodNamePrefix.length)
                .filter(i -> method.getName().startsWith(priorityByMethodNamePrefix[i]))
                .findFirst()
                .orElse(Integer.MAX_VALUE);
        }

        private String getMethodPrefix( final ExploredMethod method )
        {
            if( priorityByMethodNamePrefix == null ) {
                return null;
            }
            final int index = getMethodNamePriorityIndex(method);
            return index < priorityByMethodNamePrefix.length ? priorityByMethodNamePrefix[index] : null;
        }
    }

    @Value
    private static class MethodBySignatureSelector implements MethodSelector
    {
        @Nullable
        ApiUsageMetadata.Invocation target;

        @Override
        @Nullable
        public ExploredMethod select( @Nonnull final Collection<ExploredMethod> candidates )
        {
            if( target == null ) {
                return null;
            }

            final int argumentSize = target.getArguments().size();
            return candidates
                .stream()
                .filter(m -> target.getMethod().equals(m.getName())) // filter by name
                .filter(m -> m.getArguments().size() == argumentSize) // filter by argument count
                .filter(m -> IntStream.range(0, argumentSize).allMatch(i -> isAssignableArgument(m, i))) // filter by type
                .min(this::compareResultTypes)
                .orElse(null);
        }

        private int compareResultTypes( final ExploredMethod a, final ExploredMethod b )
        {
            if( Objects.equals(a.getResultType(), b.getResultType()) ) {
                return 0;
            }
            final Try<Class<?>> aTry = Try.of(() -> Class.forName(a.getResultType().getName()));
            if( aTry.contains(Object.class) ) {
                return 1;
            }
            final Try<Class<?>> bTry = Try.of(() -> Class.forName(b.getResultType().getName()));
            if( bTry.contains(Object.class) ) {
                return -1;
            }
            if( aTry.isSuccess() && bTry.isSuccess() ) {
                return aTry.get().isAssignableFrom(bTry.get()) ? 1 : -1;
            }
            return 0;
        }

        private boolean isAssignableArgument( final ExploredMethod m, final int i )
        {
            if( target == null ) {
                return false;
            }
            final ApiUsageMetadata.MethodArgumentDynamic finalMethodArg =
                (ApiUsageMetadata.MethodArgumentDynamic) target.getArguments().get(i);

            final String searchMethodArgType = new ArrayList<>(m.getArguments().values()).get(i).getName();
            final Try<Class<?>> aTry = Try.of(() -> Class.forName(searchMethodArgType));
            final Try<Class<?>> bTry = Try.of(() -> Class.forName(finalMethodArg.getTypeName()));
            return aTry.isSuccess() && bTry.isSuccess() && aTry.get().isAssignableFrom(bTry.get());
        }
    }
}
