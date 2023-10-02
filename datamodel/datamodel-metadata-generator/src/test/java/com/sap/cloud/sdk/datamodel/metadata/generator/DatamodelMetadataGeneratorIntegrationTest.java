/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.metadata.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

public class DatamodelMetadataGeneratorIntegrationTest
{
    private static final URI GENERATOR_REPOSITORY_LINK = URI.create("https://www.example.com");
    private static final MavenCoordinate GENERATOR_MAVEN_COORDINATE =
        MavenCoordinate.builder().artifactId("nice-generator").groupId("nice-generator-group").build();

    private static final String QUALIFIED_ENTITY_CLASS_NAME =
        "com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.entity.AwesomeEntity";
    private static final String QUALIFIED_SERVICE_INTERFACE_NAME =
        "com.sap.cloud.sdk.s4hana.datamodel.odata.services.AwesomeService";
    private static final String QUALIFIED_SERVICE_CLASS_NAME =
        "com.sap.cloud.sdk.s4hana.datamodel.odata.services.DefaultAwesomeService";
    private static final String ENTITY_COLLECTION = "AwesomeEntity";

    @RequiredArgsConstructor
    private enum Testcase
    {
        ODATA_V2_CLOUD(
            "odata-v2-cloud",
            true,
            Paths.get("odata-v2-cloud.edmx"),
            ServiceType.ODATA_V2,
            ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.of("Europe/Berlin")),
            "firstDescription",
            GENERATOR_MAVEN_COORDINATE,
            GENERATOR_REPOSITORY_LINK,
            ProtocolSpecificMetadata
                .ofODataV2(
                    ODataApiUsageMetadata
                        .builder()
                        .qualifiedServiceClassName(QUALIFIED_SERVICE_CLASS_NAME)
                        .qualifiedServiceInterfaceName(QUALIFIED_SERVICE_INTERFACE_NAME)
                        .serviceMethodInvocation(ApiUsageMetadata.method("getAll" + ENTITY_COLLECTION))
                        .serviceMethodInvocation(
                            ApiUsageMetadata.method("executeRequest").arg("destination", Destination.class))
                        .qualifiedServiceMethodResult("java.util.List<" + QUALIFIED_ENTITY_CLASS_NAME + ">")
                        .build())),

        ODATA_V4_ON_PREMISE(
            "odata-v4-on-premise",
            true,
            Paths.get("odata-v4-on-premise.edmx"),
            ServiceType.ODATA_V4,
            ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.of("Europe/Berlin")),
            "secondDescription",
            GENERATOR_MAVEN_COORDINATE,
            GENERATOR_REPOSITORY_LINK,
            ProtocolSpecificMetadata
                .ofODataV4(
                    ODataApiUsageMetadata
                        .builder()
                        .qualifiedServiceClassName(QUALIFIED_SERVICE_CLASS_NAME)
                        .qualifiedServiceInterfaceName(QUALIFIED_SERVICE_INTERFACE_NAME)
                        .serviceMethodInvocation(ApiUsageMetadata.method("getAll" + ENTITY_COLLECTION))
                        .serviceMethodInvocation(ApiUsageMetadata.method("top").arg("5"))
                        .serviceMethodInvocation(
                            ApiUsageMetadata.method("execute").arg("destination", Destination.class))
                        .qualifiedServiceMethodResult("java.util.List<" + QUALIFIED_ENTITY_CLASS_NAME + ">")
                        .build())),

        ODATA_V4_CLOUD(
            "odata-v4-cloud",
            true,
            Paths.get("odata-v4-cloud.edmx"),
            ServiceType.ODATA_V4,
            ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.of("Europe/Berlin")),
            "thirdDescription",
            GENERATOR_MAVEN_COORDINATE,
            GENERATOR_REPOSITORY_LINK,
            ProtocolSpecificMetadata
                .ofODataV4(
                    ODataApiUsageMetadata
                        .builder()
                        .qualifiedServiceClassName(QUALIFIED_SERVICE_CLASS_NAME)
                        .qualifiedServiceInterfaceName(QUALIFIED_SERVICE_INTERFACE_NAME)
                        .serviceMethodInvocation(ApiUsageMetadata.method("customMethod").arg("foobar", String.class))
                        .serviceMethodInvocation(
                            ApiUsageMetadata.method("execute").arg("destination", Destination.class))
                        .qualifiedServiceMethodResult(
                            "com.sap.cloud.sdk.datamodel.odatav4.core.ModificationResponse<"
                                + QUALIFIED_ENTITY_CLASS_NAME
                                + ">")
                        .build())),

        ODATA_V4_NO_PREGENERATED_LIB(
            "odata-v4-no-pregenerated-lib",
            true,
            Paths.get("odata-v4-no-pregenerated-lib"),
            ServiceType.ODATA_V4,
            ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.of("Europe/Berlin")),
            "thirdDescription",
            GENERATOR_MAVEN_COORDINATE,
            GENERATOR_REPOSITORY_LINK,
            ProtocolSpecificMetadata
                .ofODataV4(
                    ODataApiUsageMetadata
                        .builder()
                        .qualifiedServiceClassName(QUALIFIED_SERVICE_CLASS_NAME)
                        .qualifiedServiceInterfaceName(QUALIFIED_SERVICE_INTERFACE_NAME)
                        .serviceMethodInvocation(ApiUsageMetadata.method("getAll" + ENTITY_COLLECTION))
                        .serviceMethodInvocation(
                            ApiUsageMetadata.method("execute").arg("destination", Destination.class))
                        .qualifiedServiceMethodResult("java.util.List<" + QUALIFIED_ENTITY_CLASS_NAME + ">")
                        .build())),

        REST_RETURNING_LIST(
            "rest-returning-list",
            true,
            Paths.get("rest-returning-list.yaml"),
            ServiceType.REST,
            ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.of("Europe/Berlin")),
            "rest-library",
            GENERATOR_MAVEN_COORDINATE,
            GENERATOR_REPOSITORY_LINK,
            ProtocolSpecificMetadata
                .ofRest(
                    RestApiUsageMetadata
                        .builder()
                        .qualifiedServiceClassName("com.sap.foo.api.FooApi")
                        .serviceMethodInvocation(ApiUsageMetadata.method("invokeFoo"))
                        .qualifiedServiceMethodResult("java.util.List<com.sap.foo.model.BarModel>")
                        .serviceConstructorArgument(ApiUsageMetadata.arg("destination"))
                        .build())),

        REST_RETURNING_SINGLE_OBJECT(
            "rest-returning-single-object",
            true,
            Paths.get("rest-returning-single-object.json"),
            ServiceType.REST,
            ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.of("Europe/Berlin")),
            "rest-library",
            GENERATOR_MAVEN_COORDINATE,
            GENERATOR_REPOSITORY_LINK,
            ProtocolSpecificMetadata
                .ofRest(
                    RestApiUsageMetadata
                        .builder()
                        .qualifiedServiceClassName("com.sap.foo.api.FooApi")
                        .serviceMethodInvocation(ApiUsageMetadata.method("invokeFoo"))
                        .qualifiedServiceMethodResult("com.sap.foo.model.BarModel")
                        .serviceConstructorArgument(ApiUsageMetadata.arg("destination"))
                        .build())),

        REST_WITHOUT_MODULE_REFERENCE(
            "rest-without-module-reference",
            true,
            Paths.get("rest-without-module-reference.json"),
            ServiceType.REST,
            ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.of("Europe/Berlin")),
            "rest-library",
            GENERATOR_MAVEN_COORDINATE,
            GENERATOR_REPOSITORY_LINK,
            ProtocolSpecificMetadata
                .ofRest(
                    RestApiUsageMetadata
                        .builder()
                        .qualifiedServiceClassName("com.sap.foo.api.FooApi")
                        .serviceMethodInvocation(ApiUsageMetadata.method("invokeFoo"))
                        .qualifiedServiceMethodResult("com.sap.foo.model.BarModel")
                        .serviceConstructorArgument(ApiUsageMetadata.arg("destination"))
                        .build())),

        SERVICE_WITHOUT_API_USAGE_METADATA(
            "ServiceWithoutApiUsageMetadata",
            true,
            Paths.get("ServiceWithoutApiUsageMetadata.edmx"),

            ServiceType.ODATA_V2,
            ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.of("Europe/Berlin")),
            "awesomeDescription",
            GENERATOR_MAVEN_COORDINATE,
            GENERATOR_REPOSITORY_LINK,
            ProtocolSpecificMetadata.ofODataV2(null)),

        FAILING_CLIENT_GENERATION(
            "failing-client-generation",
            false,
            Paths.get("failing-client-generation.edmx"),
            ServiceType.ODATA_V2,
            ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.of("Europe/Berlin")),
            "awesomeDescription",
            GENERATOR_MAVEN_COORDINATE,
            GENERATOR_REPOSITORY_LINK,
            ProtocolSpecificMetadata.ofODataV2(null)),

        VERSIONS_GIVEN_AS_INPUT(
            "versions-given-as-input-properties",
            true,
            Paths.get("versions-given-as-input-properties.edmx"),
            ServiceType.ODATA_V2,
            ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.of("Europe/Berlin")),
            "awesomeDescription",
            GENERATOR_MAVEN_COORDINATE,
            GENERATOR_REPOSITORY_LINK,
            ProtocolSpecificMetadata
                .ofODataV2(
                    ODataApiUsageMetadata
                        .builder()
                        .qualifiedServiceClassName(QUALIFIED_SERVICE_CLASS_NAME)
                        .qualifiedServiceInterfaceName(QUALIFIED_SERVICE_INTERFACE_NAME)
                        .serviceMethodInvocation(ApiUsageMetadata.method("getAll" + ENTITY_COLLECTION))
                        .serviceMethodInvocation(
                            ApiUsageMetadata.method("executeRequest").arg("destination", Destination.class))
                        .qualifiedServiceMethodResult("java.util.List<" + QUALIFIED_ENTITY_CLASS_NAME + ">")
                        .build()));

        final String testCaseName;
        final boolean generationSuccessful;
        final Path apiSpecFilePath;
        final ServiceType serviceType;
        final ZonedDateTime generationTime;
        final String description;
        final MavenCoordinate generatorMavenCoordinate;
        final URI generatorRepositoryLink;
        final ProtocolSpecificMetadata protocolSpecificMetadata;
    }

    @ParameterizedTest
    @EnumSource( Testcase.class )
    void testDatamodelMetadataGeneration( final Testcase testCase, @TempDir final Path outputDirectory )
        throws IOException
    {
        final Path inputDirectory = getInputDirectory(testCase);
        final List<DatamodelMetadataInput> testData = getTestData(testCase);

        generateMetadata(inputDirectory, testData, outputDirectory);

        assertThatDirectoriesHaveSameContent(getComparisonDirectory(testCase), outputDirectory);
    }

    private
        void
        generateMetadata( final Path inputDir, final List<DatamodelMetadataInput> testData, final Path outputDir )
    {
        final DatamodelMetadataGenerator datamodelMetadataGenerator =
            new DatamodelMetadataGenerator(inputDir, new DummyMavenRepositoryAccessor());
        datamodelMetadataGenerator.generate(testData, outputDir);
    }

    //Run this to regenerate the test output
    //@ParameterizedTest
    //@EnumSource( Testcase.class )
    public void generateTestComparisonOutput( final Testcase testCase )
    {
        final Path inputDir = getInputDirectory(testCase);
        final Path outputDir = getComparisonDirectory(testCase);
        final List<DatamodelMetadataInput> testData = getTestData(testCase);
        generateMetadata(inputDir, testData, outputDir);
    }

    private List<DatamodelMetadataInput> getTestData( final Testcase testcase )
    {
        final DatamodelMetadataInput service =
            DatamodelMetadataInput
                .builder()
                .codeGenerationSuccessful(testcase.generationSuccessful)
                .apiSpecFilePath(testcase.apiSpecFilePath)
                .description(testcase.description)
                .generationTime(testcase.generationTime)
                .generatorMavenCoordinate(testcase.generatorMavenCoordinate)
                .generatorRepositoryLink(testcase.generatorRepositoryLink)
                .protocolSpecificMetadata(testcase.protocolSpecificMetadata)
                .build();

        return Collections.singletonList(service);
    }

    private Path getComparisonDirectory( final Testcase testCase )
    {
        final Path testCaseDirectory = getTestCaseDirectory();
        final Path outputDirectory = testCaseDirectory.resolve(testCase.testCaseName).resolve("output");

        assertThat(outputDirectory).exists().isDirectory().isReadable();

        return outputDirectory;
    }

    private static Path getInputDirectory( final Testcase testCase )
    {
        final Path testCaseDirectory = getTestCaseDirectory();
        final Path inputDirectory = testCaseDirectory.resolve(testCase.testCaseName).resolve("input");

        assertThat(inputDirectory).exists().isDirectory().isReadable();

        return inputDirectory;
    }

    private static Path getTestCaseDirectory()
    {
        final Path testCaseDirectory = Paths.get("src/test/resources/DatamodelMetadataGeneratorIntegrationTest");

        assertThat(testCaseDirectory).exists().isDirectory().isReadable();

        return testCaseDirectory;
    }

    private static class DummyMavenRepositoryAccessor implements MavenRepositoryAccessor
    {
        @Nonnull
        @Override
        public Try<String> getLatestModuleVersion( @Nonnull final MavenCoordinate mavenCoordinate )
        {
            return Try.success("1.1.1");
        }
    }

    @SuppressWarnings( "resource" )
    @SneakyThrows
    private static void assertThatDirectoriesHaveSameContent( final Path a, final Path b )
    {
        final Predicate<Path> isFile = p -> p.toFile().isFile();
        Files.walk(a).filter(isFile).forEach(p -> assertThat(p).hasSameTextualContentAs(b.resolve(a.relativize(p))));
        Files.walk(b).filter(isFile).forEach(p -> assertThat(p).hasSameTextualContentAs(a.resolve(b.relativize(p))));
    }
}
