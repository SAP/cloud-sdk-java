/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.openapi.generator;

import static com.sap.cloud.sdk.datamodel.metadata.generator.ApiUsageMetadata.arg;

import java.io.File;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.datamodel.metadata.generator.DatamodelMetadataGenerator;
import com.sap.cloud.sdk.datamodel.metadata.generator.DatamodelMetadataInput;
import com.sap.cloud.sdk.datamodel.metadata.generator.JavaServiceMethodResolver;
import com.sap.cloud.sdk.datamodel.metadata.generator.MavenCoordinate;
import com.sap.cloud.sdk.datamodel.metadata.generator.ProtocolSpecificMetadata;
import com.sap.cloud.sdk.datamodel.metadata.generator.RestApiUsageMetadata;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationResult;

import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class DatamodelMetadataGeneratorAdapter
{
    private static final URI MAVEN_MODULE_REPOSITORY_LINK =
        URI.create("https://mvnrepository.com/artifact/com.sap.cloud.sdk.datamodel/openapi-generator");
    private static final MavenCoordinate GENERATOR_MAVEN_COORDINATE =
        MavenCoordinate.builder().artifactId("openapi-generator").groupId("com.sap.cloud.sdk.datamodel").build();

    void generateDatamodelMetadataIfApplicable(
        @Nonnull final GenerationConfiguration generationConfiguration,
        @Nullable final GenerationResult generationResult )
    {
        if( StringUtils.isBlank(generationConfiguration.getInputSpec()) ) {
            throw new IllegalArgumentException("Failure generating metadata because input spec was not provided.");
        }

        final Path inputSpecPath = Paths.get(generationConfiguration.getInputSpec());

        final DatamodelMetadataGenerator datamodelMetadataGenerator =
            new DatamodelMetadataGenerator(inputSpecPath.toAbsolutePath().getParent());

        if( !datamodelMetadataGenerator.isMetadataGenerationEnabled() ) {
            return;
        }

        final String serviceName =
            Option
                .of(generationResult)
                .flatMap(GenerationResult::getServiceName)
                .getOrElse(() -> FilenameUtils.removeExtension(inputSpecPath.getFileName().toString()));
        final String libraryDescription = "OpenAPI client for the service " + serviceName;

        final RestApiUsageMetadata restApiUsageMetadata =
            Option
                .of(generationResult)
                .map(result -> getApiUsageMetadata(generationConfiguration, result.getGeneratedFiles()))
                .getOrNull();

        final DatamodelMetadataInput datamodelMetadata =
            DatamodelMetadataInput
                .builder()
                .codeGenerationSuccessful(generationResult != null)
                .apiSpecFilePath(inputSpecPath)
                .generationTime(ZonedDateTime.now())
                .description(libraryDescription)
                .generatorMavenCoordinate(GENERATOR_MAVEN_COORDINATE)
                .generatorRepositoryLink(MAVEN_MODULE_REPOSITORY_LINK)
                .protocolSpecificMetadata(ProtocolSpecificMetadata.ofRest(restApiUsageMetadata))
                .build();

        datamodelMetadataGenerator
            .generate(
                Collections.singletonList(datamodelMetadata),
                Paths.get(generationConfiguration.getOutputDirectory()));
    }

    @Nullable
    private
        RestApiUsageMetadata
        getApiUsageMetadata( final GenerationConfiguration generationConfiguration, final List<File> generatedFiles )
    {
        final Path sourcePath = FileSystems.getDefault().getPath(generationConfiguration.getOutputDirectory());
        final String packagePath = generationConfiguration.getApiPackage().replace(".", File.separator);

        if( generatedFiles.isEmpty() ) {
            throw new IllegalStateException("No generated files found.");
        }
        for( final File file : generatedFiles ) {
            if( !file.getPath().contains(packagePath) ) {
                continue;
            }

            final String className = StringUtils.removeEnd(file.getName(), ".java");
            final String qualifiedServiceName = generationConfiguration.getApiPackage() + "." + className;

            final Optional<JavaServiceMethodResolver> methodResolver =
                JavaServiceMethodResolver
                    .builder()
                    .sourceDirectory(sourcePath)
                    .qualifiedServiceName(qualifiedServiceName)
                    .build();

            if( methodResolver.isPresent() ) {
                return RestApiUsageMetadata
                    .builder()
                    .qualifiedServiceClassName(qualifiedServiceName)
                    .qualifiedServiceMethodResult(methodResolver.get().getResultType())
                    .serviceMethodInvocations(methodResolver.get().getInvocations())
                    .serviceConstructorArgument(arg("destination", Destination.class))
                    .build();
            }
        }
        log.info("No API class with a suitable method found.");
        return null;
    }
}
