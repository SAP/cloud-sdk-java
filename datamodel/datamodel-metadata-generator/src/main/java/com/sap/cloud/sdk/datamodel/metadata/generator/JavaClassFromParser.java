package com.sap.cloud.sdk.datamodel.metadata.generator;

import static com.sap.cloud.sdk.datamodel.metadata.generator.JavaClassExplorer.ensureNamespace;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaGenericDeclaration;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaParameterizedType;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.JavaTypeVariable;
import com.thoughtworks.qdox.model.JavaWildcardType;

import io.vavr.control.Try;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Value
@Slf4j
class JavaClassFromParser implements JavaClassExplorer
{
    @Nonnull
    Path sourcePath;

    @Nonnull
    @Override
    public Optional<ExploredClass> getClassByName(
        @Nonnull final String name,
        @Nonnull final List<ExploredType> genericTypeValues,
        @Nonnull final JavaClassExplorer callback )
    {
        final Try<JavaClass> javaClass = Try.of(() -> parseClass(sourcePath, name));
        if( javaClass.isFailure() ) {
            return Optional.empty();
        }

        final Map<String, ExploredType> genericMapping = getGenericMapping(javaClass.get(), genericTypeValues);
        if( genericMapping == null ) {
            return Optional.empty();
        }

        final ExploredClass exploredClass = getClassFromReference(javaClass.get(), genericTypeValues, genericMapping);
        final ExploredClass exploredSuperClass = exploreSuperClass(javaClass.get(), genericMapping, callback);
        final ExploredClass mergedClass = JavaClassExplorer.mergeMethods(exploredClass, exploredSuperClass);
        return Optional.of(mergedClass);
    }

    @Nullable
    private static ExploredClass exploreSuperClass(
        @Nonnull final JavaClass child,
        @Nonnull final Map<String, ExploredType> genericMapping,
        @Nonnull final JavaClassExplorer callback )
    {
        final JavaType classReference = child.getSuperClass();
        if( classReference == null ) {
            return null;
        }

        final ExploredType type = getActualTypeArguments(child.getPackageName(), classReference, genericMapping);
        final Optional<ExploredClass> exploredClass = callback.getClassByName(type.getName(), type.getParameters());
        return exploredClass.orElseGet(() -> {
            log.warn("Parent class {} could not be loaded.", type.getName());
            return null;
        });
    }

    @Nonnull
    private static ExploredClass getClassFromReference(
        @Nonnull final JavaClass javaClass,
        @Nonnull final List<ExploredType> genericTypeValues,
        @Nonnull final Map<String, ExploredType> genericMapping )
    {
        final String name = javaClass.getFullyQualifiedName();
        final List<ExploredMethod> methods =
            javaClass
                .getMethods()
                .stream()
                .filter(m -> javaClass.isInterface() || m.getModifiers().contains("public"))
                .map(m -> createMethod(javaClass, genericMapping, m))
                .collect(Collectors.toList());

        return new ExploredClass(new ExploredType(name, genericTypeValues), methods);
    }

    @Nonnull
    private static
        ExploredMethod
        createMethod( final JavaClass javaClass, final Map<String, ExploredType> genericMapping, final JavaMethod m )
    {
        final String packageName = javaClass.getPackageName();
        final ExploredType returnType = createType(m.getReturnType(), packageName, genericMapping);

        final Function<JavaParameter, ExploredType> parseParameterType =
            p -> createType(p.getType(), packageName, genericMapping);

        final LinkedHashMap<String, ExploredType> arguments =
            m
                .getParameters()
                .stream()
                .collect(
                    Collectors.toMap(JavaParameter::getName, parseParameterType, ( o1, o2 ) -> o1, LinkedHashMap::new));

        return new ExploredMethod(m.getName(), returnType, arguments);
    }

    @Nonnull
    private JavaClass parseClass( final Path path, final String fullName )
        throws IOException
    {
        final String relativeLegacyClassPath = fullName.replace(".", File.separator) + ".java";
        final File legacyClassFile = path.resolve(relativeLegacyClassPath).toFile();
        final JavaProjectBuilder javaProjectBuilder = new JavaProjectBuilder();
        return javaProjectBuilder.addSource(legacyClassFile).getClasses().get(0);
    }

    @Nonnull
    private static ExploredType createType(
        final JavaType returnType,
        final String packageName,
        final Map<String, ExploredType> genericMapping )
    {
        if( returnType instanceof JavaParameterizedType ) {
            return getActualTypeArguments(packageName, returnType, genericMapping);
        }
        if( returnType instanceof JavaTypeVariable ) {
            final ExploredType returnCandidate = genericMapping.get(((JavaTypeVariable<?>) returnType).getName());
            return returnCandidate != null ? returnCandidate : new ExploredType(Object.class.getName());
        }
        if( returnType instanceof JavaWildcardType ) {
            return new ExploredType("?");
        }
        return new ExploredType(returnType.getGenericFullyQualifiedName());
    }

    private static
        ExploredType
        getActualTypeArguments( final String packageName, final JavaType type, final Map<String, ExploredType> map )
    {
        final String name = ensureNamespace(packageName, type.getFullyQualifiedName());
        if( !(type instanceof JavaParameterizedType) ) {
            return new ExploredType(name);
        }

        final List<ExploredType> params =
            ((JavaParameterizedType) type)
                .getActualTypeArguments()
                .stream()
                .map(
                    t -> map.containsKey(t.getValue())
                        ? map.get(t.getValue())
                        : new ExploredType(ensureNamespace(packageName, t.getGenericFullyQualifiedName())))
                .collect(Collectors.toList());

        return new ExploredType(name, params);
    }

    @Nullable
    private static
        Map<String, ExploredType>
        getGenericMapping( @Nonnull final JavaClass cl, @Nonnull final List<ExploredType> genericValues )
    {
        final List<JavaTypeVariable<JavaGenericDeclaration>> typeParameters = cl.getTypeParameters();
        if( typeParameters.size() != genericValues.size() ) {
            log
                .debug(
                    "Number of generic type parameters {} does not fit the caller signature: {}",
                    typeParameters.size(),
                    genericValues);
            return null;
        }

        final Map<String, ExploredType> genericMapping = new HashMap<>();
        for( int i = 0; i < typeParameters.size(); i++ ) {
            genericMapping.put(typeParameters.get(i).getName(), genericValues.get(i));
        }
        return genericMapping;
    }
}
