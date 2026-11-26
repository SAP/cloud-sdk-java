package com.sap.cloud.sdk.datamodel.openapi.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.ParameterizedType;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import javax.tools.ToolProvider;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.ApiMaturity;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;

class FreeFormObjectUnionIntegrationTest
{
    @ParameterizedTest
    @CsvSource( { "oneOf,3.0.3", "anyOf,3.0.3", "oneOf,3.1.0", "anyOf,3.1.0" } )
    void generatedMapCompilesAndRoundTripsArbitraryObjects(
        final String keyword,
        final String version,
        @TempDir final Path directory )
        throws Exception
    {
        final Path spec = directory.resolve("spec.yaml");
        Files.writeString(spec, """
            openapi: %s
            info:
              title: Free-form union
              version: '1'
            paths: {}
            components:
              schemas:
                FreeForm:
                  type: object
                  additionalProperties: true
                Specific:
                  type: object
                  properties:
                    enabled:
                      type: boolean
                Container:
                  type: object
                  properties:
                    modelConfig:
                      %s:
                        - $ref: '#/components/schemas/FreeForm'
                        - $ref: '#/components/schemas/Specific'
            """.formatted(version, keyword));
        final Path output = directory.resolve("generated");
        final var config =
            GenerationConfiguration
                .builder()
                .inputSpec(spec.toString())
                .outputDirectory(output.toString())
                .apiPackage("uniontest")
                .modelPackage("uniontest")
                .generateApis(false)
                .generateModels(true)
                .apiMaturity(ApiMaturity.RELEASED)
                .oneOfAnyOfGenerationEnabled(true)
                .additionalProperty("library", "apache-httpclient")
                .additionalProperty("useOneOfInterfaces", "true")
                .build();
        final var generated = new DataModelGenerator().generateDataModel(config).get();
        final Path classes = Files.createDirectory(directory.resolve("classes"));
        final var compiler = ToolProvider.getSystemJavaCompiler();
        try( final var manager = compiler.getStandardFileManager(null, null, null) ) {
            final var sources = manager.getJavaFileObjectsFromFiles(generated.getGeneratedFiles());
            assertThat(
                compiler
                    .getTask(
                        null,
                        manager,
                        null,
                        List
                            .of(
                                "-proc:none",
                                "-classpath",
                                System.getProperty("java.class.path"),
                                "-d",
                                classes.toString()),
                        null,
                        sources)
                    .call())
                .isTrue();
        }
        try(
            final var loader =
                new URLClassLoader(new java.net.URL[] { classes.toUri().toURL() }, getClass().getClassLoader()) ) {
            final Class<?> container = loader.loadClass("uniontest.Container");
            final var getter = container.getMethod("getModelConfig");
            final var returnType = (ParameterizedType) getter.getGenericReturnType();
            assertThat(returnType.getRawType()).isEqualTo(Map.class);
            assertThat(returnType.getActualTypeArguments()).containsExactly(String.class, Object.class);
            final ObjectMapper mapper = new ObjectMapper();
            // OpenAPI 3.1 currently generates map fields without map-item helpers.
            if( version.startsWith("3.0") ) {
                final Object empty = mapper.readValue("{}", container);
                final var putItem = container.getMethod("putModelConfigItem", String.class, Object.class);
                assertThat(putItem.invoke(empty, "enabled", true)).isSameAs(empty);
                assertThat(getter.invoke(empty)).isEqualTo(Map.of("enabled", true));
            }
            for( final String json : List.of("""
                {"modelConfig":{}}
                """, """
                {"modelConfig":{"enabled":true,"custom":{"values":[1,"x",null]}}}
                """) ) {
                final Object value = mapper.readValue(json, container);
                assertThat(getter.invoke(value)).isInstanceOf(Map.class);
                assertThat(mapper.valueToTree(value).get("modelConfig"))
                    .isEqualTo(mapper.readTree(json).get("modelConfig"));
            }
        }
    }
}
