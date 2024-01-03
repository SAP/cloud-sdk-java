/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.metadata.generator;

import static com.sap.cloud.sdk.datamodel.metadata.generator.ApiUsageMetadata.method;
import static com.sap.cloud.sdk.datamodel.metadata.generator.JavaServiceMethodResolver.forPrefix;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class JavaServiceMethodResolverTest
{
    private static final Path path =
        Paths.get("src/test/resources/" + JavaServiceMethodResolverTest.class.getSimpleName());

    public static class ThingService
    {
        public JavaServiceMethodResolverTest play()
        {
            return null;
        }
    }

    public interface ResultKnown
    {

    }

    public ResultKnown execute( URI origin )
    {
        return null;
    }

    @Test
    void testMethodGreet()
    {
        final Optional<JavaServiceMethodResolver> methodResolution = getMethodResolver("greet", "pet", "feed");

        assertThat(methodResolution).isNotEmpty();
        assertThat(methodResolution.get().getResultType()).isEqualTo("java.util.Optional<com.test.ResultGreet>");
        assertThat(methodResolution.get().getInvocations())
            .containsExactly(
                method("greetFriend").arg("other", "com.test.Person"),
                method("execute").arg("uri", URI.class));
    }

    @Test
    void testMethodPet()
    {
        final Optional<JavaServiceMethodResolver> methodResolution = getMethodResolver("pet", "greet", "feed");

        assertThat(methodResolution).isNotEmpty();
        assertThat(methodResolution.get().getResultType()).isEqualTo("io.vavr.control.Try<com.test.Animal>");
        assertThat(methodResolution.get().getInvocations())
            .containsExactly(
                method("pet").arg("animal", "com.test.Animal"),
                method("withDuration").arg("duration", Duration.class),
                method("execute").arg("uri", URI.class));
    }

    @Test
    void testMethodFeed()
    {
        final Optional<JavaServiceMethodResolver> methodResolution = getMethodResolver("feed", "pet", "greet");

        assertThat(methodResolution).isNotEmpty();
        assertThat(methodResolution.get().getResultType())
            .isEqualTo("io.vavr.Tuple3<java.lang.String, com.test.Person, java.lang.Integer>");
        assertThat(methodResolution.get().getInvocations())
            .containsExactly(
                method("feed").arg("animals", "java.util.List<com.test.Animal>"),
                method("perDay").arg("3"),
                method("execute").arg("uri", URI.class));
    }

    @Test
    void testMethodPlay()
    {
        final Optional<JavaServiceMethodResolver> methodResolution = getMethodResolver("play");

        assertThat(methodResolution).isNotEmpty();
        assertThat(methodResolution.get().getResultType())
            .isEqualTo("com.sap.cloud.sdk.datamodel.metadata.generator.JavaServiceMethodResolverTest.ResultKnown");
        assertThat(methodResolution.get().getInvocations())
            .containsExactly(method("play"), method("execute").arg("uri", URI.class));
    }

    @Test
    void testMethodUnknown()
    {
        final Optional<JavaServiceMethodResolver> methodResolution = getMethodResolver("unknown");

        assertThat(methodResolution).isNotEmpty();
        assertThat(methodResolution.get().getResultType())
            .isEqualTo("com.sap.cloud.sdk.datamodel.metadata.generator.JavaServiceMethodResolverTest.ResultKnown");
        assertThat(methodResolution.get().getInvocations())
            .containsExactly(method("play"), method("execute").arg("uri", URI.class));
    }

    private Optional<JavaServiceMethodResolver> getMethodResolver( final String... priorityByMethodNamePrefix )
    {
        return JavaServiceMethodResolver
            .builder()
            .sourceDirectory(path)
            .qualifiedServiceName("com.test.ServicePerson")
            .priorityByMethodNamePrefix(priorityByMethodNamePrefix)
            .finalMethod(method("execute").arg("uri", URI.class))
            .additionalInvocation(forPrefix("pet").add(method("withDuration").arg("duration", Duration.class)))
            .additionalInvocation(forPrefix("feed").add(method("perDay").arg("3")))
            .excludedMethodName("ignoreAction")
            .build();
    }
}
