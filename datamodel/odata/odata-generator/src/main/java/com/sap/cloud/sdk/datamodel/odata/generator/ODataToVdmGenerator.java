package com.sap.cloud.sdk.datamodel.odata.generator;

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
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.provider.DataServices;
import org.apache.olingo.odata2.core.edm.provider.EdmImplProv;
import org.apache.olingo.odata2.core.edm.provider.EdmxProvider;
import org.slf4j.Logger;
import org.springframework.util.AntPathMatcher;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.sap.cloud.sdk.datamodel.odata.utility.EdmxValidator;
import com.sap.cloud.sdk.datamodel.odata.utility.NamingUtils;

import io.vavr.control.Try;

class ODataToVdmGenerator
{
    private static final Logger logger = MessageCollector.getLogger(ODataToVdmGenerator.class);

    private static final String SWAGGER_FILE_EXTENSION_PATTERN = "\\.json$";
    private static final String METADATA_FILE_EXTENSION_PATTERN = "\\.(edmx|xml)$";

    private static final String NAMESPACE_PARENT_PACKAGE_SPACE = "namespaces";
    private static final String SERVICE_PACKAGE_SPACE = "services";
    private static final List<String> EDMX_VERSIONS = Lists.newArrayList("1.0", "2.0");

    private static final Charset FILE_ENCODING = StandardCharsets.UTF_8;

    void generate( @Nonnull final DataModelGeneratorConfig config )
    {
        if( !config.getInputDirectory().exists() ) {
            throw new ODataGeneratorReadException(
                "The given input directory does not exist: " + getCanonicalPath(config.getInputDirectory()));
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

        if( config.isDeleteTargetDirectory() && config.getOutputDirectory().exists() ) {
            cleanDirectory(config.getOutputDirectory());
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
                    .processService(service, config.getIncludedEntitySets(), config.getIncludedFunctionImports());
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
        final PropertiesConfiguration serviceNameMappings =
            loadPropertiesConfiguration(config.getServiceNameMappings());

        for( final File edmxFile : inputFiles ) {

            if( excludePatternMatch(config.getExcludeFilePattern(), edmxFile.getName()) ) {
                logger.info("Skipping EDMX file due to exclusion rule: {}", edmxFile);
                continue;
            }

            if( !EdmxValidator.isQualified(edmxFile, EdmxValidator.Version.V2) ) {
                logger.info("Skipping EDMX file due to incompatible OData version: {}", edmxFile);
                continue;
            }

            final String canonicalPath = getCanonicalPath(edmxFile);
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
                        config.getDefaultBasePath(),
                        edmxFile,
                        serviceSwaggerFile,
                        config.isGenerateLinksToApiBusinessHub());

                allEdmxFiles.add(new EdmxFile(edmxFile.toPath(), service));
            }
            catch( final ODataGeneratorReadException e ) {
                logger
                    .warn(
                        String
                            .format(
                                "Error in file %s; unable to generate VDM classes. The file will be skipped and generation continues for the next file.",
                                edmxFile.getName()),
                        e);

                allEdmxFiles.add(new EdmxFile(edmxFile.toPath(), null));
            }
        }

        return allEdmxFiles;
    }

    @Nullable
    private File getSwaggerFile( @Nonnull final String servicePath, @Nonnull final String serviceName )
    {
        final String patternText = "^" + serviceName + SWAGGER_FILE_EXTENSION_PATTERN;
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
        @Nonnull final String serviceName,
        @Nonnull final PropertiesConfiguration serviceNameMappings,
        @Nullable final String defaultBasePath,
        @Nonnull final File serviceMetadataFile,
        @Nullable final File serviceSwaggerFile,
        final boolean linkToApiBusinessHub )
    {
        final Edm metadata = getMetadata(serviceMetadataFile);

        final ServiceDetails serviceDetails =
            new ServiceDetailsResolver(defaultBasePath, FILE_ENCODING)
                .createServiceDetails(serviceMetadataFile, serviceSwaggerFile);

        final Multimap<String, ApiFunction> allowedFunctions =
            new AllowedFunctionsResolver(FILE_ENCODING)
                .findAllowedFunctions(metadata, serviceSwaggerFile, serviceMetadataFile);

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

    private Edm getMetadata( final File serviceMetadataFile )
    {
        try( InputStream stream = Files.newInputStream(serviceMetadataFile.toPath()) ) {
            final EdmxProvider provider = new EdmxProvider().parse(stream, false);
            final String oDataVersion = getODataVersion(provider);
            if( !EDMX_VERSIONS.contains(oDataVersion) ) {
                throw new ODataGeneratorReadException(
                    "Metadata file being read is not an OData V2 service " + serviceMetadataFile.getPath());
            }
            return new EdmImplProv(provider);
        }
        catch( final IOException e ) {
            throw new ODataGeneratorReadException(e);
        }
        catch( final Exception e ) {
            throw new ODataGeneratorReadException(
                "Parsing of the metadata file failed. Please check that the metadata file is valid and belongs to an OData Version 2.0 service.",
                e);
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

    private PropertiesConfiguration loadPropertiesConfiguration( final File serviceMappingsFile )
    {
        final FileBasedConfigurationBuilder<PropertiesConfiguration> configurationBuilder =
            loadPropertiesConfigurationBuilder(serviceMappingsFile);
        final PropertiesConfiguration serviceNameMappings;
        try {
            serviceNameMappings = configurationBuilder.getConfiguration();
        }
        catch( final ConfigurationException e ) {
            throw new ODataGeneratorReadException(e);
        }
        return serviceNameMappings;
    }

    private FileBasedConfigurationBuilder<PropertiesConfiguration> loadPropertiesConfigurationBuilder(
        final File serviceMappingsFile )
    {
        final PropertiesConfiguration serviceNameMappings;
        final FileBasedConfigurationBuilder<PropertiesConfiguration> configurationBuilder;
        try {
            if( serviceMappingsFile.exists() ) {
                configurationBuilder =
                    new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                        .configure(new Parameters().fileBased().setFile(serviceMappingsFile));
            } else {
                configurationBuilder = new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class);
            }
            serviceNameMappings = configurationBuilder.getConfiguration();

        }
        catch( final ConfigurationException e ) {
            throw new ODataGeneratorReadException(e);
        }

        sanitizeConfiguration(serviceNameMappings);

        return configurationBuilder;
    }

    private void sanitizeConfiguration( final Configuration configuration )
    {
        final var keys = new ArrayList<String>();
        configuration.getKeys().forEachRemaining(keys::add);

        for( final String key : keys ) {
            if( key.endsWith(Service.SERVICE_MAPPINGS_CLASS_SUFFIX) ) {
                final String javaClassName = configuration.getString(key);
                final String sanitizedJavaClassName = NamingUtils.serviceNameToBaseJavaClassName(javaClassName);
                configuration.setProperty(key, sanitizedJavaClassName);
            }
            if( key.endsWith(Service.SERVICE_MAPPINGS_PACKAGE_SUFFIX) ) {
                final String javaPackageName = configuration.getString(key);
                final String sanitizedJavaPackageName = NamingUtils.serviceNameToJavaPackageName(javaPackageName);
                configuration.setProperty(key, sanitizedJavaPackageName);
            }
        }
    }

    private void storeConfiguration( final File serviceMappingsFile, final Iterable<Service> allODataServices )
    {
        ensureFileExists(serviceMappingsFile);
        final var configurationBuilder = loadPropertiesConfigurationBuilder(serviceMappingsFile);
        final PropertiesConfiguration serviceNameMappings;
        try {
            serviceNameMappings = configurationBuilder.getConfiguration();
        }
        catch( final ConfigurationException e ) {
            throw new ODataGeneratorReadException(e);
        }

        for( final Service oDataService : allODataServices ) {
            final String javaClassNameKey = oDataService.getName() + Service.SERVICE_MAPPINGS_CLASS_SUFFIX;
            serviceNameMappings.setProperty(javaClassNameKey, oDataService.getJavaClassName());
            serviceNameMappings.getLayout().setComment(javaClassNameKey, oDataService.getTitle());
            serviceNameMappings.getLayout().setBlankLinesBefore(javaClassNameKey, 1);

            final String javaPackageNameKey = oDataService.getName() + Service.SERVICE_MAPPINGS_PACKAGE_SUFFIX;
            serviceNameMappings.setProperty(javaPackageNameKey, oDataService.getJavaPackageName());
        }

        try {
            configurationBuilder.save();
        }
        catch( final ConfigurationException e ) {
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

    private String getODataVersion( final EdmxProvider edmxProvider )
        throws IllegalAccessException
    {
        final DataServices dataServices = (DataServices) FieldUtils.readField(edmxProvider, "dataServices", true);
        return dataServices.getDataServiceVersion();
    }
}
