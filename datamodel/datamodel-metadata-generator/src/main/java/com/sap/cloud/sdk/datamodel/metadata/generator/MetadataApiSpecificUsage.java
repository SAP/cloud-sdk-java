/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.metadata.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

class MetadataApiSpecificUsage
{
    private static final String TEMPLATE_IMPORT = "import %s;";
    private static final String TEMPLATE_VARIABLE = "final %s %s;";
    private static final String TEMPLATE_VARIABLE_SET = "final %s %s = %s;";
    private static final String TEMPLATE_SERVICE_CONSTRUCTOR = "new %s(%s)";
    private static final String SERVICE_VARIABLE = "service";
    private static final String RESULT_VARIABLE = "result";

    private final String usageImports;
    private final String usageUnassignedVars;
    private final String usageAssignedVars;
    private final String usageServiceDeclaration;
    private final String usageServiceCall;

    @Builder
    private MetadataApiSpecificUsage(
        @Nonnull final ApiUsageMetadata data,
        @Singular final List<String> enforceImports,
        @Singular final List<Declaration> initialValues )
    {
        usageImports = Joiner.on("\n").join(getImports(data, enforceImports, initialValues));

        usageUnassignedVars = Joiner.on("\n").join(getUnassignedDeclarations(data, initialValues));

        usageAssignedVars = Joiner.on("\n").join(initialValues);

        usageServiceDeclaration = getServiceDeclaration(data).toString();

        usageServiceCall = getServiceCall(data).toString();
    }

    public String getUsage()
    {
        return Stream
            .of(usageImports + "\n", usageAssignedVars, usageUnassignedVars, usageServiceDeclaration, usageServiceCall)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.joining("\n"));
    }

    private static Declaration getServiceCall( @Nonnull final ApiUsageMetadata data )
    {
        final String methodResultType = getUnqualified(data.getQualifiedServiceMethodResult());
        String methodCall = SERVICE_VARIABLE;
        if( !data.getServiceMethodInvocations().isEmpty() ) {
            methodCall += "." + Joiner.on(".").join(data.getServiceMethodInvocations());
        }
        return new Declaration(RESULT_VARIABLE, methodResultType, methodCall);
    }

    private static Declaration getServiceDeclaration( @Nonnull final ApiUsageMetadata data )
    {
        final String serviceInterface = getUnqualified(data.getQualifiedServiceInterfaceName());
        final String serviceClass = getUnqualified(data.getQualifiedServiceClassName());
        final String constructorArguments = Joiner.on(", ").join(data.getServiceConstructorArguments());
        final String serviceInit = String.format(TEMPLATE_SERVICE_CONSTRUCTOR, serviceClass, constructorArguments);
        return new Declaration(SERVICE_VARIABLE, serviceInterface, serviceInit);
    }

    @Nonnull
    private static
        List<Declaration>
        getUnassignedDeclarations( @Nonnull final ApiUsageMetadata data, final List<Declaration> initialValues )
    {
        final List<ApiUsageMetadata.MethodArgument> unassignedValues =
            new ArrayList<>(data.getServiceConstructorArguments());
        for( final ApiUsageMetadata.Invocation invocation : data.getServiceMethodInvocations() ) {
            unassignedValues.addAll(invocation.getArguments());
        }
        return unassignedValues
            .stream()
            .filter(ApiUsageMetadata.MethodArgumentDynamic.class::isInstance)
            .map(ApiUsageMetadata.MethodArgumentDynamic.class::cast)
            .filter(arg -> initialValues.stream().noneMatch(v -> arg.getCode().equals(v.getName())))
            .map(arg -> new Declaration(arg.getCode(), arg.getTypeName(), null))
            .collect(Collectors.toList());
    }

    private static List<String> getImports(
        final ApiUsageMetadata data,
        final List<String> enforceImports,
        final List<Declaration> initialValues )
    {
        // add types from enforced imports list
        final LinkedHashSet<String> imports = new LinkedHashSet<>(enforceImports);

        // add types from initial value declarations
        imports.addAll(Lists.transform(initialValues, Declaration::getType));

        // add types from service
        imports.add(data.getQualifiedServiceInterfaceName());
        imports.add(data.getQualifiedServiceClassName());

        // add types from constructor
        for( final ApiUsageMetadata.MethodArgument arg : data.getServiceConstructorArguments() ) {
            if( arg instanceof ApiUsageMetadata.MethodArgumentDynamic ) {
                imports.addAll(getImportableTypes(((ApiUsageMetadata.MethodArgumentDynamic) arg).getTypeName()));
            }
        }

        // add types from method chain
        for( final ApiUsageMetadata.Invocation invoke : data.getServiceMethodInvocations() ) {
            for( final ApiUsageMetadata.MethodArgument arg : invoke.getArguments() ) {
                if( arg instanceof ApiUsageMetadata.MethodArgumentDynamic ) {
                    imports.addAll(getImportableTypes(((ApiUsageMetadata.MethodArgumentDynamic) arg).getTypeName()));
                }
            }
        }

        // add return types
        imports.addAll(getImportableTypes(data.getQualifiedServiceMethodResult()));

        return imports.stream().map(imp -> String.format(TEMPLATE_IMPORT, imp)).collect(Collectors.toList());
    }

    private static String getUnqualified( final String qualifiedType )
    {
        return qualifiedType.replaceAll("\\w+\\.", "");
    }

    private static Collection<String> getImportableTypes( final String... types )
    {
        final Pattern typePattern = Pattern.compile("[\\w.]*");
        final Collection<String> result = new ArrayList<>();
        for( final String constructorArgType : types ) {
            final Matcher matcher = typePattern.matcher(constructorArgType);
            while( matcher.find() ) {
                final String qualifiedType = matcher.group(0);
                if( !qualifiedType.isEmpty() && !qualifiedType.startsWith("java.lang.") ) {
                    result.add(qualifiedType);
                }
            }
        }
        return result;
    }

    @Value
    static class Declaration
    {
        @Nonnull
        String name;

        @Nonnull
        String type;

        @Nullable
        String value;

        @Nonnull
        @Override
        public String toString()
        {
            final String typeUnqualified = getUnqualified(type);
            return value == null
                ? String.format(TEMPLATE_VARIABLE, typeUnqualified, name)
                : String.format(TEMPLATE_VARIABLE_SET, typeUnqualified, name, value);
        }
    }
}
