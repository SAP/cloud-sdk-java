package com.sap.cloud.sdk.datamodel.openapi.sample.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.EmbeddingInputText;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.EmbeddingsInputTextById1D;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.EmbeddingsInputTextById2D;

class OneOfMultipleArrayDeserializationTest
{
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final String JSON_ARRAY_INTEGERS = """
        {
           "vector": [1, 2, 3, 4]
        }
        """;
    private static final String JSON_ARRAY_OF_INTEGERS_2D = """
        {
            "matrix": [[1, 2], [3, 4]]
        }
        """;
    private static final String JSON_STRING = "\"test\"";
    private static final String JSON_ARRAY_OF_STRINGS = """
        ["test1", "test2"]
        """;

    @Test
    public void testDeserializeArrayOfIntegers()
        throws Exception
    {

        final EmbeddingInputText result = objectMapper.readValue(JSON_ARRAY_INTEGERS, EmbeddingInputText.class);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(EmbeddingInputText.InnerEmbeddingsInputTextById.class);
        final var inner = (EmbeddingInputText.InnerEmbeddingsInputTextById) result;
        assertThat(inner.value()).isInstanceOf(EmbeddingsInputTextById1D.class);
    }

    @Test
    public void testDeserializeArrayOfIntegers2D()
        throws Exception
    {

        final EmbeddingInputText result = objectMapper.readValue(JSON_ARRAY_OF_INTEGERS_2D, EmbeddingInputText.class);
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(EmbeddingInputText.InnerEmbeddingsInputTextById.class);
        final var inner = (EmbeddingInputText.InnerEmbeddingsInputTextById) result;
        assertThat(inner.value()).isInstanceOf(EmbeddingsInputTextById2D.class);
    }

    @Test
    public void testDeserializeString()
        throws Exception
    {

        final EmbeddingInputText result = objectMapper.readValue(JSON_STRING, EmbeddingInputText.class);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(EmbeddingInputText.InnerString.class);
        final var inner = (EmbeddingInputText.InnerString) result;
        assertThat(inner.value()).isInstanceOf(String.class);
    }

    @Test
    public void testDeserializeArrayOfStrings()
        throws Exception
    {

        final EmbeddingInputText result = objectMapper.readValue(JSON_ARRAY_OF_STRINGS, EmbeddingInputText.class);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(EmbeddingInputText.InnerStrings.class);
        final var inner = (EmbeddingInputText.InnerStrings) result;
        assertThat(inner.values()).isInstanceOf(java.util.List.class);
        assertThat(inner.values()).hasSize(2);
        assertThat(inner.values().get(0)).isEqualTo("test1");
        assertThat(inner.values().get(1)).isEqualTo("test2");
    }
}
