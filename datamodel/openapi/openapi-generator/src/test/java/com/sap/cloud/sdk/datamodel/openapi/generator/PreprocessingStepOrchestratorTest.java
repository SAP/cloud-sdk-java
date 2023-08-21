package com.sap.cloud.sdk.datamodel.openapi.generator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sap.cloud.sdk.datamodel.openapi.generator.exception.OpenApiGeneratorException;

class PreprocessingStepOrchestratorTest
{
    private static final String TEST_FIELD_VALUE = "foo";
    private static final String TEST_FIELD_KEY = "bar";

    @Test
    void testNoManipulationOfApiSpecInJson()
    {
        final String inputSpecName = "sodastore.json";
        final Path inputSpecPath = getInputSpecPath(inputSpecName);

        final PreprocessingStepOrchestrator extensionProcessor = new PreprocessingStepOrchestrator(inputSpecPath);
        extensionProcessor.setSteps(Collections.singletonList(NoOpPreprocessingStep::new));

        final OpenApiSpec outputSpec = extensionProcessor.performPreprocessingSteps();
        assertThat(outputSpec.getFilePath()).isEqualTo(inputSpecPath);

        extensionProcessor.cleanUp();
    }

    @Test
    void testNoManipulationOfApiSpecInYaml()
    {
        final String inputSpecName = "sodastore.json";
        final Path inputSpecPath = getInputSpecPath(inputSpecName);

        final PreprocessingStepOrchestrator extensionProcessor = new PreprocessingStepOrchestrator(inputSpecPath);
        extensionProcessor.setSteps(Collections.singletonList(NoOpPreprocessingStep::new));

        final OpenApiSpec outputSpec = extensionProcessor.performPreprocessingSteps();
        assertThat(outputSpec.getFilePath()).isEqualTo(inputSpecPath);

        extensionProcessor.cleanUp();
    }

    @Test
    void testManipulationOfApiSpecInJson()
    {
        final String inputSpecName = "sodastore.json";
        final Path inputSpecPath = getInputSpecPath(inputSpecName);

        final PreprocessingStepOrchestrator extensionProcessor = new PreprocessingStepOrchestrator(inputSpecPath);
        extensionProcessor.setSteps(Collections.singletonList(ModelManipulatingPreprocessingStep::new));

        final OpenApiSpec outputSpec = extensionProcessor.performPreprocessingSteps();
        assertThat(outputSpec.getFilePath()).isNotEqualTo(inputSpecPath);
        assertThat(outputSpec.getFilePath().getFileName().toString()).contains(inputSpecName);
        assertThatTestFieldExists(outputSpec.getJsonNode());

        extensionProcessor.cleanUp();
    }

    @Test
    void testManipulationOfApiSpecInYaml()
    {
        final String inputSpecName = "sodastore.json";
        final Path inputSpecPath = getInputSpecPath(inputSpecName);

        final PreprocessingStepOrchestrator extensionProcessor = new PreprocessingStepOrchestrator(inputSpecPath);
        extensionProcessor.setSteps(Collections.singletonList(ModelManipulatingPreprocessingStep::new));

        final OpenApiSpec outputSpec = extensionProcessor.performPreprocessingSteps();
        assertThat(outputSpec.getFilePath()).isNotEqualTo(inputSpecPath);
        assertThat(outputSpec.getFilePath().getFileName().toString()).contains(inputSpecName);
        assertThatTestFieldExists(outputSpec.getJsonNode());

        extensionProcessor.cleanUp();
    }

    @Test
    void testParsingOfBrokenApiSpec()
    {
        final String inputSpecName = "sodastore-broken-file.yaml";
        final Path inputSpecPath = getInputSpecPath(inputSpecName);

        final PreprocessingStepOrchestrator extensionProcessor = new PreprocessingStepOrchestrator(inputSpecPath);

        assertThatExceptionOfType(OpenApiGeneratorException.class)
            .isThrownBy(extensionProcessor::performPreprocessingSteps);
    }

    @Test
    void testParsingUnsupportedFileExtension()
    {
        final String inputSpecName = "sodastore.txt";
        final Path inputSpecPath = getInputSpecPath(inputSpecName);

        assertThatExceptionOfType(OpenApiGeneratorException.class)
            .isThrownBy(() -> new PreprocessingStepOrchestrator(inputSpecPath));
    }

    @Test
    void testParsingFileNotFound()
    {
        final String inputSpecName = "doesntexist.yaml";
        final Path inputSpecPath = getInputSpecPath(inputSpecName);

        final PreprocessingStepOrchestrator extensionProcessor = new PreprocessingStepOrchestrator(inputSpecPath);

        assertThatThrownBy(extensionProcessor::performPreprocessingSteps)
            .isExactlyInstanceOf(OpenApiGeneratorException.class)
            .hasCauseExactlyInstanceOf(IOException.class);
    }

    private Path getInputSpecPath( final String fileName )
    {
        return Paths
            .get("src/test/resources/" + PreprocessingStepOrchestratorTest.class.getSimpleName() + "/" + fileName);
    }

    private void assertThatTestFieldExists( final JsonNode outputSpecNode )
    {
        assertThat(outputSpecNode.get(TEST_FIELD_KEY).asText()).isEqualTo(TEST_FIELD_VALUE);
    }

    private static class NoOpPreprocessingStep implements PreprocessingStep
    {
        @Nonnull
        @Override
        public
            PreprocessingStepResult
            execute( @Nonnull final JsonNode input, @Nonnull final ObjectMapper objectMapper )
        {
            return new PreprocessingStepResult()
            {
                @Override
                public JsonNode getJsonNode()
                {
                    return input;
                }

                @Override
                public boolean changesApplied()
                {
                    return false;
                }
            };
        }
    }

    private static class ModelManipulatingPreprocessingStep implements PreprocessingStep
    {

        @Nonnull
        @Override
        public
            PreprocessingStepResult
            execute( @Nonnull final JsonNode input, @Nonnull final ObjectMapper objectMapper )
        {
            return new PreprocessingStepResult()
            {
                @Override
                public JsonNode getJsonNode()
                {
                    return ((ObjectNode) input).put(TEST_FIELD_KEY, TEST_FIELD_VALUE);
                }

                @Override
                public boolean changesApplied()
                {
                    return true;
                }
            };
        }
    }
}
