package com.sap.cloud.sdk.datamodel.odata.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.sap.cloud.sdk.datamodel.odata.utility.NameSource;

/**
 * Test to reproduce and verify the fix for the bug where service name mappings change between the first and second
 * generation runs.
 *
 * Bug description: When generating VDM classes from an EDMX file with a service name like "API_MATERIAL_DOCUMENT_SRV",
 * the first generation creates a mapping with packageName=apimaterialdocumentsrv, but the second generation changes it
 * to packageName=materialdocumentsrv (the "api" prefix is removed).
 */
class ServiceNameMappingConsistencyTest
{
    @Test
    void testServiceNameMappingRemainsConsistentAcrossMultipleGenerations( @TempDir final Path tempDir )
        throws IOException
    {
        // Setup: Create a minimal test EDMX file with API_MATERIAL_DOCUMENT_SRV as service name
        final Path inputDir = tempDir.resolve("input");
        final Path outputDir = tempDir.resolve("output");
        final Path serviceMappingFile = inputDir.resolve("service-mappings.properties");

        Files.createDirectories(inputDir);
        Files.createDirectories(outputDir);

        // Create a minimal EDMX file
        final String edmxContent = createMinimalEdmx("API_MATERIAL_DOCUMENT_SRV");
        Files.writeString(inputDir.resolve("API_MATERIAL_DOCUMENT_SRV.edmx"), edmxContent);

        // First generation - no service mapping file exists
        final DataModelGenerator generator1 =
            new DataModelGenerator()
                .withInputDirectory(inputDir.toFile())
                .withOutputDirectory(outputDir.toFile())
                .withServiceNameMapping(serviceMappingFile.toFile())
                .withNameSource(NameSource.NAME)
                .withPackageName("test.package")
                .withDefaultBasePath("/sap/opu/odata/sap/")
                .deleteOutputDirectory();

        System.out.println("=== First Generation ===");
        System.out.println("Input dir: " + inputDir);
        System.out.println("Output dir: " + outputDir);
        System.out.println("Service mapping file: " + serviceMappingFile);
        System.out.println("Service mapping file exists before: " + Files.exists(serviceMappingFile));

        generator1.execute();

        System.out.println("Service mapping file exists after: " + Files.exists(serviceMappingFile));
        if( Files.exists(serviceMappingFile) ) {
            System.out.println("File content:");
            System.out.println(Files.readString(serviceMappingFile));
        }

        // Read the generated service mapping file after first run
        assertThat(serviceMappingFile).exists();
        final Properties firstRunMappings = new Properties();
        firstRunMappings.load(Files.newInputStream(serviceMappingFile));

        final String firstRunClassName = firstRunMappings.getProperty("API_MATERIAL_DOCUMENT_SRV.className");
        final String firstRunPackageName = firstRunMappings.getProperty("API_MATERIAL_DOCUMENT_SRV.packageName");

        System.out.println("First run - className: " + firstRunClassName);
        System.out.println("First run - packageName: " + firstRunPackageName);

        // Verify first run generated expected values
        assertThat(firstRunClassName).as("First run should generate className").isNotNull().isNotEmpty();

        assertThat(firstRunPackageName).as("First run should generate packageName").isNotNull().isNotEmpty();

        // Second generation - service mapping file now exists
        new DataModelGenerator()
            .withInputDirectory(inputDir.toFile())
            .withOutputDirectory(outputDir.toFile())
            .withServiceNameMapping(serviceMappingFile.toFile())
            .withNameSource(NameSource.NAME)
            .withPackageName("test.package")
            .withDefaultBasePath("/sap/opu/odata/sap/")
            .deleteOutputDirectory()
            .execute();

        // Read the service mapping file after second run
        final Properties secondRunMappings = new Properties();
        secondRunMappings.load(Files.newInputStream(serviceMappingFile));

        final String secondRunClassName = secondRunMappings.getProperty("API_MATERIAL_DOCUMENT_SRV.className");
        final String secondRunPackageName = secondRunMappings.getProperty("API_MATERIAL_DOCUMENT_SRV.packageName");

        System.out.println("Second run - className: " + secondRunClassName);
        System.out.println("Second run - packageName: " + secondRunPackageName);

        // THE BUG: The packageName changes between runs
        // This assertion should FAIL before the fix, demonstrating the bug
        assertThat(secondRunClassName)
            .as("Service mapping className should remain consistent across multiple generation runs")
            .isEqualTo(firstRunClassName);

        assertThat(secondRunPackageName)
            .as(
                "Service mapping packageName should remain consistent across multiple generation runs - BUG: 'api' prefix gets removed on second run")
            .isEqualTo(firstRunPackageName);
    }

    /**
     * Creates a minimal valid OData V2 EDMX file for testing
     */
    private String createMinimalEdmx( final String namespace )
    {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
            + "<edmx:Edmx Version=\"1.0\" xmlns:edmx=\"http://schemas.microsoft.com/ado/2007/06/edmx\" "
            + "xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\" "
            + "xmlns:sap=\"http://www.sap.com/Protocols/SAPData\">\n"
            + "  <edmx:DataServices m:DataServiceVersion=\"2.0\">\n"
            + "    <Schema Namespace=\""
            + namespace
            + "\" xml:lang=\"en\" sap:schema-version=\"1\" xmlns=\"http://schemas.microsoft.com/ado/2008/09/edm\">\n"
            + "      <EntityType Name=\"TestEntity\" sap:content-version=\"1\">\n"
            + "        <Key>\n"
            + "          <PropertyRef Name=\"ID\"/>\n"
            + "        </Key>\n"
            + "        <Property Name=\"ID\" Type=\"Edm.String\" Nullable=\"false\"/>\n"
            + "        <Property Name=\"Name\" Type=\"Edm.String\"/>\n"
            + "      </EntityType>\n"
            + "      <EntityContainer Name=\""
            + namespace
            + "_Entities\" m:IsDefaultEntityContainer=\"true\" sap:supported-formats=\"atom json xlsx\">\n"
            + "        <EntitySet Name=\"TestEntitySet\" EntityType=\""
            + namespace
            + ".TestEntity\" sap:content-version=\"1\"/>\n"
            + "      </EntityContainer>\n"
            + "    </Schema>\n"
            + "  </edmx:DataServices>\n"
            + "</edmx:Edmx>";
    }
}
