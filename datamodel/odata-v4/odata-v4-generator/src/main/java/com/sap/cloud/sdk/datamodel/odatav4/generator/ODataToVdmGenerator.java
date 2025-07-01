package com.sap.cloud.sdk.datamodel.odatav4.generator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.edm.provider.CsdlTerm;
import org.apache.olingo.commons.api.format.ContentType;
import org.slf4j.Logger;
import org.springframework.util.AntPathMatcher;

import com.google.common.collect.Multimap;
import com.sap.cloud.sdk.datamodel.odata.utility.EdmxValidator;
import com.sap.cloud.sdk.datamodel.odata.utility.ServiceNameMappings;

import io.vavr.control.Try;

class ODataToVdmGenerator
{
    private static final Logger logger = MessageCollector.getLogger(ODataToVdmGenerator.class);

    private static final String SWAGGER_FILE_EXTENSION_PATTERN = "\\.json$";
    private static final String METADATA_FILE_EXTENSION_PATTERN = "\\.(edmx|xml)$";

    private static final String NAMESPACE_PARENT_PACKAGE_SPACE = "namespaces";
    private static final String SERVICE_PACKAGE_SPACE = "services";

    private static final Charset FILE_ENCODING = StandardCharsets.UTF_8;
    private static final String EDMX_VERSION_ODATA_V4 = "4.0";

    void generate( @Nonnull final DataModelGeneratorConfig config )
    {
        if( !config.getInputDirectory().exists() ) {
            throw new ODataGeneratorReadException(
                "The given input directory does not exist: " + getCanonicalPath(config.getInputDirectory()));
        }

        if( config.isDeleteTargetDirectory() && config.getOutputDirectory().exists() ) {
            cleanDirectory(config.getOutputDirectory());
        }

        final Collection<File> inputFiles = getInputFiles(config.getInputDirectory());
        final Collection<EdmxFile> allEdmxFiles = loadServicesFromInput(config, inputFiles);

        if( allEdmxFiles.isEmpty() ) {
            logger
                .warn(
                    String
                        .format(
                            "No OData service definitions found in the input directory '%s' - exiting.",
                            getCanonicalPath(config.getInputDirectory())));
            return;
        }

        final Collection<Service> allODataServices =
            allEdmxFiles
                .stream()
                .filter(EdmxFile::isSuccessfullyParsed)
                .map(edmxFile -> edmxFile.getService().get())
                .collect(Collectors.toCollection(ArrayList::new));

        storeConfiguration(config.getServiceNameMappings(), allODataServices);

        final int numServices = allODataServices.size();
        logger.info("Processing " + numServices + " OData service" + (numServices == 1 ? "" : "s") + "...");

        final CodeModelClassGenerator classGenerator = createCodeModelForServices(config, allODataServices);

        for( final EdmxFile edmxFile : allEdmxFiles ) {
            if( edmxFile.isSuccessfullyParsed()
                && classGenerator.wasServiceGenerated(edmxFile.getServiceName().get()) ) {
                edmxFile.setSuccessfullyGenerated();
            }
        }

        logger.info("Generating Java classes to " + getCanonicalPath(config.getOutputDirectory()));
        classGenerator.writeClasses(config, FILE_ENCODING);

        new DatamodelMetadataGeneratorAdapter(logger)
            .generateMetadataIfApplicable(
                Paths.get(config.getInputDirectory().getAbsolutePath()),
                Paths.get(config.getOutputDirectory().getAbsolutePath()),
                allEdmxFiles,
                classGenerator.getServiceClassGenerator());
    }

    private CodeModelClassGenerator createCodeModelForServices(
        @Nonnull final DataModelGeneratorConfig config,
        @Nonnull final Iterable<Service> allODataServices )
    {
        final String packageName = config.getPackageName();
        final String namespaceParentPackageName = buildPackageName(packageName, NAMESPACE_PARENT_PACKAGE_SPACE);
        final String servicePackageName = buildPackageName(packageName, SERVICE_PACKAGE_SPACE);
        final CodeModelClassGenerator classGenerator =
            new CodeModelClassGenerator(config, namespaceParentPackageName, servicePackageName);

        for( final Service service : allODataServices ) {
            try {
                classGenerator
                    .processService(
                        service,
                        config.getIncludedEntitySets(),
                        config.getIncludedFunctionImports(),
                        config.getIncludedActionImports());
            }
            catch( final ODataGeneratorReadException e ) {
                logger
                    .warn(String.format("Error in file %s; unable to generate all VDM classes.", service.getName()), e);
            }
        }

        return classGenerator;
    }

    private Collection<EdmxFile> loadServicesFromInput(
        @Nonnull final DataModelGeneratorConfig config,
        @Nonnull final Collection<File> inputFiles )
    {
        final Collection<EdmxFile> allEdmxFiles = new LinkedList<>();
        final ServiceNameMappings serviceNameMappings = loadPropertiesConfiguration(config.getServiceNameMappings());
        final List<CsdlSchema> edmxTerms = loadEdmxSchemas();

        for( final File inputFile : inputFiles ) {

            if( excludePatternMatch(config.getExcludeFilePattern(), inputFile.getName()) ) {
                logger.info("Skipping EDMX file due to exclusion rule: {}", inputFile);
                continue;
            }

            if( !EdmxValidator.isQualified(inputFile, EdmxValidator.Version.V4) ) {
                logger.info("Skipping EDMX file due to incompatible OData version: {}", inputFile);
                continue;
            }

            final String canonicalPath = getCanonicalPath(inputFile);
            final String servicePath = FilenameUtils.getFullPath(canonicalPath);
            final String serviceName = FilenameUtils.getBaseName(canonicalPath);
            logger.info(String.format("Loading OData service %s", serviceName));

            final File serviceSwaggerFile = getSwaggerFile(servicePath, serviceName);
            logger.info(String.format("Reading metadata file: %s", canonicalPath));
            logger.info(String.format("Reading swagger file:  %s", getCanonicalPath(serviceSwaggerFile)));

            try {
                final Service service =
                    buildService(
                        serviceName,
                        serviceNameMappings,
                        edmxTerms,
                        config.getDefaultBasePath(),
                        inputFile,
                        serviceSwaggerFile,
                        config.isGenerateLinksToApiBusinessHub());

                allEdmxFiles.add(new EdmxFile(inputFile.toPath(), service));
            }
            catch( final ODataGeneratorReadException e ) {
                logger
                    .warn(
                        String
                            .format(
                                "Error in file %s; unable to generate VDM classes. The file will be skipped and generation continues for the next file.",
                                inputFile.getName()),
                        e);

                allEdmxFiles.add(new EdmxFile(inputFile.toPath(), null));
            }
        }
        return allEdmxFiles;
    }

    @Nullable
    private File getSwaggerFile( @Nonnull final String servicePath, @Nonnull final String serviceName )
    {
        final String patternText = serviceName + SWAGGER_FILE_EXTENSION_PATTERN;
        final Pattern pattern = Pattern.compile(patternText, Pattern.CASE_INSENSITIVE);
        final File[] files = new File(servicePath).listFiles(( dir, fileName ) -> pattern.matcher(fileName).matches());
        return files == null || files.length == 0 ? null : files[0];
    }

    @Nonnull
    private Collection<File> getInputFiles( @Nonnull final File inputDir )
    {
        final Pattern pattern = Pattern.compile(".*" + METADATA_FILE_EXTENSION_PATTERN, Pattern.CASE_INSENSITIVE);
        final Predicate<Path> edmxPredicate = p -> pattern.matcher(p.getFileName().toString()).matches();
        return Try
            .of(() -> Files.walk(inputDir.toPath()))
            .getOrElse(Stream::empty)
            .filter(Files::isRegularFile)
            .filter(edmxPredicate)
            .map(Path::toFile)
            .collect(Collectors.toList());
    }

    private Service buildService(
        final String serviceName,
        final ServiceNameMappings serviceNameMappings,
        final List<CsdlSchema> edmxTerms,
        @Nullable final String defaultBasePath,
        final File serviceMetadataFile,
        final File serviceSwaggerFile,
        final boolean linkToApiBusinessHub )
    {
        final Edm metadata = getMetadata(serviceMetadataFile, edmxTerms);

        final ServiceDetails serviceDetails =
            new ServiceDetailsResolver(defaultBasePath, FILE_ENCODING)
                .createServiceDetails(serviceMetadataFile, serviceSwaggerFile);

        final Multimap<String, ApiFunction> allowedFunctions =
            new AllowedFunctionsResolver(FILE_ENCODING).findAllowedFunctions(metadata, serviceSwaggerFile);

        final Service newService =
            new EdmService(
                serviceName,
                serviceNameMappings,
                metadata,
                serviceDetails,
                allowedFunctions,
                linkToApiBusinessHub);

        logger.info(String.format("  Title: %s", newService.getTitle()));
        logger.info(String.format("  Raw URL: %s", newService.getServiceUrl()));
        logger.info(String.format("  Java Package Name: %s", newService.getJavaPackageName()));
        logger.info(String.format("  Java Class Name: %s", newService.getJavaClassName()));

        return newService;
    }

    private Edm getMetadata( final File serviceMetadataFile, final List<CsdlSchema> edmxTerms )
    {
        try( InputStream stream = Files.newInputStream(serviceMetadataFile.toPath()) ) {
            final ODataClient client = ODataClientFactory.getClient();
            final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).toMetadata(stream);
            final String edmVersion = metadata.getEdmVersion();
            if( !EDMX_VERSION_ODATA_V4.equals(edmVersion) ) {
                throw new ODataGeneratorReadException(
                    "Metadata file being read is not an OData V4 service " + serviceMetadataFile.getPath());
            }
            final Map<String, CsdlSchema> edmxSchemas = metadata.getSchemaByNsOrAlias();
            return client.getReader().readMetadata(edmxSchemas, edmxTerms);
        }
        catch( final Exception e ) {
            throw new ODataGeneratorReadException("Problem reading metadata file " + serviceMetadataFile.getPath(), e);
        }
    }

    private String buildPackageName( final String basePackageName, final String subPackageName )
    {
        if( StringUtils.isBlank(basePackageName) ) {
            return subPackageName;
        } else {
            return String.format("%s.%s", basePackageName, subPackageName);
        }
    }

    private void cleanDirectory( final File outputDir )
    {
        try {
            FileUtils.cleanDirectory(outputDir);
        }
        catch( final IOException e ) {
            throw new ODataGeneratorWriteException(e);
        }
    }

    private ServiceNameMappings loadPropertiesConfiguration( final File serviceMappingsFile )
    {
        return ServiceNameMappings.load(serviceMappingsFile.toPath());
    }

    // Schema definitions are necessary to make the EDMX properties explorable through Olingo API at runtime, example:
    //
    // <edmx:Edmx xmlns:edmx="http://docs.oasis-open.org/odata/ns/edmx" Version="4.0">
    //   <edmx:DataServices>
    //     <Schema xmlns="http://docs.oasis-open.org/odata/ns/edm" Namespace="[NAMESPACE]" Alias="[ALIAS]">
    //       <Term Name="[NAME]" Type="Edm.String"></Term>
    //     </Schema>
    //   </edmx:DataServices>
    // </edmx>
    static List<CsdlSchema> loadEdmxSchemas()
    {
        final List<CsdlSchema> termSchemas = new LinkedList<>();
        final Function<String, CsdlTerm> term = name -> new CsdlTerm().setName(name).setType("Edm.String");

        // schemas for labels (common)
        final List<CsdlTerm> commonTerms = Arrays.asList(term.apply("Label"), term.apply("QuickInfo"));
        final String commonNamespace = "com.sap.vocabularies.Common.v1";
        termSchemas.add(new CsdlSchema().setNamespace(commonNamespace).setAlias("SAP__common").setTerms(commonTerms));
        termSchemas.add(new CsdlSchema().setNamespace(commonNamespace).setAlias("Common").setTerms(commonTerms));

        // schema for description (core)
        final List<CsdlTerm> coreTerms = Arrays.asList(term.apply("LongDescription"), term.apply("Description"));
        termSchemas.add(new CsdlSchema().setNamespace("Org.OData.Core.V1").setAlias("Core").setTerms(coreTerms));

        // schemas for entity allowed operations
        final String[] aliases = { "Capabilities", "SAP__Capabilities" };
        final String[] terms = { "InsertRestrictions", "UpdateRestrictions", "DeleteRestrictions", "ReadRestrictions" };
        for( final String alias : aliases ) {
            final CsdlSchema schema = new CsdlSchema().setNamespace("Org.OData.Capabilities.V1").setAlias(alias);
            final Function<String, CsdlTerm> termer = n -> new CsdlTerm().setName(n).setType(alias + "." + n + "Type");
            termSchemas.add(schema.setTerms(Arrays.stream(terms).map(termer).collect(Collectors.toList())));
        }
        return termSchemas;
    }

    private void storeConfiguration( final File serviceMappingsFile, final Iterable<Service> allODataServices )
    {
        ensureFileExists(serviceMappingsFile);
        final ServiceNameMappings mappings = ServiceNameMappings.load(serviceMappingsFile.toPath());

        for( final Service oDataService : allODataServices ) {
            final String javaClassNameKey = oDataService.getName() + Service.SERVICE_MAPPINGS_CLASS_SUFFIX;
            mappings.putString(javaClassNameKey, oDataService.getJavaClassName(), oDataService.getTitle());

            final String javaPackageNameKey = oDataService.getName() + Service.SERVICE_MAPPINGS_PACKAGE_SUFFIX;
            mappings.putString(javaPackageNameKey, oDataService.getJavaPackageName());
        }

        try {
            mappings.save();
        }
        catch( final IOException e ) {
            throw new ODataGeneratorWriteException(e);
        }
    }

    private void ensureFileExists( final File serviceMappingsFile )
    {
        if( !serviceMappingsFile.exists() ) {
            if( logger.isInfoEnabled() ) {
                logger
                    .info(
                        "The service mappings file at '"
                            + getCanonicalPath(serviceMappingsFile)
                            + "' does not exist. Creating an empty one.");
            }
            final boolean success;
            try {
                success = serviceMappingsFile.createNewFile();
            }
            catch( final IOException e ) {
                throw new ODataGeneratorWriteException(e);
            }
            if( !success ) {
                throw new ODataGeneratorWriteException(
                    "Could not create service mappings file at '" + getCanonicalPath(serviceMappingsFile) + "'");
            }
        }
    }

    private String getCanonicalPath( @Nullable final File outputDir )
    {
        if( outputDir == null ) {
            return null;
        }
        try {
            return outputDir.getCanonicalPath();
        }
        catch( final IOException e ) {
            throw new ODataGeneratorReadException(e);
        }
    }

    private boolean excludePatternMatch( final String excludeFilePattern, final String serviceMetadataFilename )
    {
        final List<String> excludeFilePatternEach = new ArrayList<>(Arrays.asList(excludeFilePattern.split(",")));
        final AntPathMatcher antPathMatcher = new AntPathMatcher();

        for( final String filePattern : excludeFilePatternEach ) {
            if( antPathMatcher.match(filePattern, serviceMetadataFilename) ) {
                logger
                    .info(
                        String
                            .format(
                                "Excluding metadata file %s, as it matches with the excludes pattern.",
                                serviceMetadataFilename));
                return true;
            }
        }
        return false;
    }
}
