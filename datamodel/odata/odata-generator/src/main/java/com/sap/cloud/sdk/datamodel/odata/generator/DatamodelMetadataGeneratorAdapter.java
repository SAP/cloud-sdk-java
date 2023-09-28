package com.sap.cloud.sdk.datamodel.odata.generator;

import static com.sap.cloud.sdk.datamodel.metadata.generator.ApiUsageMetadata.method;
import static com.sap.cloud.sdk.datamodel.metadata.generator.JavaServiceMethodResolver.forPrefix;

import java.net.URI;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.slf4j.Logger;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.datamodel.metadata.generator.DatamodelMetadataGenerator;
import com.sap.cloud.sdk.datamodel.metadata.generator.DatamodelMetadataInput;
import com.sap.cloud.sdk.datamodel.metadata.generator.JavaServiceMethodResolver;
import com.sap.cloud.sdk.datamodel.metadata.generator.MavenCoordinate;
import com.sap.cloud.sdk.datamodel.metadata.generator.ODataApiUsageMetadata;
import com.sap.cloud.sdk.datamodel.metadata.generator.ProtocolSpecificMetadata;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class DatamodelMetadataGeneratorAdapter
{
    private final Logger logger;

    private static final URI MAVEN_MODULE_REPOSITORY_LINK =
        URI.create("https://mvnrepository.com/artifact/com.sap.cloud.sdk.datamodel/odata-generator");
    private static final MavenCoordinate GENERATOR_MAVEN_COORDINATE =
        MavenCoordinate.builder().artifactId("odata-generator").groupId("com.sap.cloud.sdk.datamodel").build();

    void generateMetadataIfApplicable(
        @Nonnull final Path inputDir,
        @Nonnull final Path outputDir,
        @Nonnull final Collection<EdmxFile> edmxFiles,
        @Nonnull final ServiceClassGenerator serviceClassGenerator )
    {
        final DatamodelMetadataGenerator datamodelMetadataGenerator = new DatamodelMetadataGenerator(inputDir);

        if( !datamodelMetadataGenerator.isMetadataGenerationEnabled() ) {
            return;
        }
        final List<EdmxFile> edmxFilesForMetadataGeneration =
            edmxFiles
                .stream()
                //OData V4 specs also fail parsing, hence we implicitly ensure hereby that only OData V2 specs are considered
                .filter(EdmxFile::isSuccessfullyParsed)
                .collect(Collectors.toList());

        final List<DatamodelMetadataInput> datamodelMetadataInputList =
            getDatamodelMetadata(edmxFilesForMetadataGeneration, serviceClassGenerator, outputDir);
        try {
            logger.info("Calling metadata generation using input directory: " + inputDir.toAbsolutePath());
            datamodelMetadataGenerator.generate(datamodelMetadataInputList, outputDir);
        }
        catch( final Exception e ) {
            throw new ODataGeneratorException("Failed during datamodel metadata generation", e);
        }
    }

    private List<DatamodelMetadataInput> getDatamodelMetadata(
        @Nonnull final Collection<EdmxFile> edmxFiles,
        @Nonnull final ServiceClassGenerator serviceClassGenerator,
        @Nonnull final Path outputDir )
    {
        final String description = "The Virtual Data Model for OData V2 Services";
        final ZonedDateTime currentTime = ZonedDateTime.now();

        return edmxFiles
            .stream()
            .map(
                edmxFile -> DatamodelMetadataInput
                    .builder()
                    .codeGenerationSuccessful(edmxFile.isSuccessfullyGenerated())
                    .apiSpecFilePath(edmxFile.getFilePath())
                    .description(description)
                    .generatorMavenCoordinate(GENERATOR_MAVEN_COORDINATE)
                    .generatorRepositoryLink(MAVEN_MODULE_REPOSITORY_LINK)
                    .generationTime(currentTime)
                    .protocolSpecificMetadata(
                        ProtocolSpecificMetadata
                            .ofODataV2(getODataV2ApiUsageMetadata(serviceClassGenerator, edmxFile, outputDir)))
                    .build())
            .collect(Collectors.toList());
    }

    private ODataApiUsageMetadata getODataV2ApiUsageMetadata(
        final ServiceClassGenerator serviceClassGenerator,
        final EdmxFile edmxFile,
        @Nonnull final Path outputDir )
    {
        return edmxFile.isSuccessfullyGenerated()
            ? edmxFile
                .getService()
                .flatMap(service -> getApiUsageMetadata(service, serviceClassGenerator, outputDir))
                .getOrNull()
            : null;
    }

    private Option<ODataApiUsageMetadata> getApiUsageMetadata(
        @Nonnull final Service service,
        @Nonnull final ServiceClassGenerator serviceClassGenerator,
        @Nonnull final Path outputDir )
    {
        final Option<String> serviceClass =
            serviceClassGenerator.getQualifiedServiceImplementationClassName(service.getName());
        if( serviceClass.isEmpty() ) {
            logger.info("No service implementation class name found for OData V2 service {}.", service.getName());
            return Option.none();
        }

        final Option<String> serviceInterface =
            serviceClassGenerator.getQualifiedServiceInterfaceName(service.getName());
        if( serviceInterface.isEmpty() ) {
            logger.info("No service interface name found for OData V2 service {}.", service.getName());
            return Option.none();
        }

        final Optional<JavaServiceMethodResolver> javaMethod =
            JavaServiceMethodResolver
                .builder()
                .finalMethod(method("executeRequest").arg("destination", Destination.class))
                .sourceDirectory(outputDir)
                .excludedMethodName("withServicePath")
                .qualifiedServiceName(serviceInterface.get())
                .priorityByMethodNamePrefix(new String[] { "getAll", "get", "create", "update", "delete" })
                .additionalInvocation(forPrefix("getAll").add(method("top").arg("5")))
                .build();

        if( !javaMethod.isPresent() ) {
            logger.info("No suitable service method found for OData V2 service {}.", service.getName());
            return Option.none();
        }

        return Option
            .of(
                ODataApiUsageMetadata
                    .builder()
                    .qualifiedServiceClassName(serviceClass.get())
                    .qualifiedServiceInterfaceName(serviceInterface.get())
                    .qualifiedServiceMethodResult(javaMethod.get().getResultType())
                    .serviceMethodInvocations(javaMethod.get().getInvocations())
                    .build());
    }
}
