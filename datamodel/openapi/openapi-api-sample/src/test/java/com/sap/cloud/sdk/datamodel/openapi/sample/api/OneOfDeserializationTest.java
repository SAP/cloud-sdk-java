package com.sap.cloud.sdk.datamodel.openapi.sample.api;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.AllOf;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.AnyOf;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.Cola;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.Fanta;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.FantaFlavor;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.FantaFlavorOneOf;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.OneOf;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.OneOfWithDiscriminator;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.OneOfWithDiscriminatorAndMapping;

class OneOfDeserializationTest
{
    private static final ObjectMapper objectMapper = newDefaultObjectMapper();

    private static final Cola COLA_OBJECT = Cola.create().caffeine(true).sodaType("Cola");
    private static final Fanta FANTA_OBJECT =
        Fanta
            .create()
            .color("orange")
            .sodaType("Fanta")
            .flavor(new FantaFlavor.InnerFantaFlavorOneOf(FantaFlavorOneOf.create().intensity(3).nuance("wood")));
    private static final String COLA_JSON = """
        {
          "sodaType": "Cola",
          "caffeine": true
        }""";
    private static final String FANTA_JSON = """
        {
          "sodaType": "Fanta",
          "color": "orange",
          "flavor": {"intensity":3,"nuance":"wood"}
        }""";
    private static final String UNKNOWN_JSON = """
        {
          "sodaType": "Sprite",
          "someProperty": "someValue"
        }""";

    @Test
    void oneOf()
        throws JsonProcessingException
    {
        var actual = objectMapper.readValue(COLA_JSON, OneOf.class);
        assertThat(actual)
            .describedAs("Object should automatically be deserialized as Cola with JSON subtype deduction")
            .isInstanceOf(Cola.class)
            .isEqualTo(COLA_OBJECT);

        actual = objectMapper.readValue(FANTA_JSON, OneOf.class);
        assertThat(actual)
            .describedAs("Object should automatically be deserialized as Fanta with JSON subtype deduction")
            .isInstanceOf(Fanta.class)
            .isEqualTo(FANTA_OBJECT);

        assertThatThrownBy(() -> objectMapper.readValue(UNKNOWN_JSON, OneOf.class))
            .isInstanceOf(JsonProcessingException.class);
    }

    @Test
    void oneOfWithDiscriminator()
        throws JsonProcessingException
    {
        var actual = objectMapper.readValue(COLA_JSON, OneOfWithDiscriminator.class);
        assertThat(actual)
            .describedAs(
                "Object should automatically be deserialized as Cola using the class names as discriminator mapping values")
            .isInstanceOf(Cola.class)
            .isEqualTo(COLA_OBJECT);

        actual = objectMapper.readValue(FANTA_JSON, OneOfWithDiscriminator.class);
        assertThat(actual)
            .describedAs(
                "Object should automatically be deserialized as Fanta using the class names as discriminator mapping values")
            .isInstanceOf(Fanta.class)
            .isEqualTo(FANTA_OBJECT);

        assertThatThrownBy(() -> objectMapper.readValue(UNKNOWN_JSON, OneOfWithDiscriminator.class));
    }

    @Test
    void oneOfWithDiscriminatorAndMapping()
        throws JsonProcessingException
    {
        var jsonWithCustomMapping = """
            {
              "sodaType": "cool_cola",
              "caffeine": true
            }""";
        var actual = objectMapper.readValue(jsonWithCustomMapping, OneOfWithDiscriminatorAndMapping.class);
        assertThat(actual)
            .describedAs(
                "Object should automatically be deserialized as Cola using the explicit discriminator mapping values")
            .isInstanceOf(Cola.class)
            .isEqualTo(Cola.create().caffeine(true).sodaType("cool_cola"));

        jsonWithCustomMapping = """
            {
              "sodaType": "fancy_fanta",
              "color": "orange"
            }""";
        actual = objectMapper.readValue(jsonWithCustomMapping, OneOfWithDiscriminatorAndMapping.class);
        assertThat(actual)
            .describedAs(
                "Object should automatically be deserialized as Fanta using the explicit discriminator mapping values")
            .isInstanceOf(Fanta.class)
            .isEqualTo(Fanta.create().color("orange").sodaType("fancy_fanta"));

        assertThatThrownBy(() -> objectMapper.readValue(UNKNOWN_JSON, OneOfWithDiscriminatorAndMapping.class))
            .isInstanceOf(JsonProcessingException.class);

    }

    @Test
    void anyOf()
        throws JsonProcessingException
    {
        AnyOf anyOfCola = objectMapper.readValue(COLA_JSON, AnyOf.class);
        assertThat(anyOfCola.getSodaType()).isEqualTo("Cola");
        assertThat(anyOfCola.isCaffeine()).isTrue();
        assertThat(anyOfCola.getColor()).isNull();

        AnyOf anyOfFanta = objectMapper.readValue(FANTA_JSON, AnyOf.class);
        assertThat(anyOfFanta.getSodaType()).isEqualTo("Fanta");
        assertThat(anyOfFanta.getColor()).isEqualTo("orange");
        assertThat(anyOfFanta.isCaffeine()).isNull();
    }

    @Test
    void allOf()
        throws JsonProcessingException
    {
        AllOf allOfCola = objectMapper.readValue(COLA_JSON, AllOf.class);
        assertThat(allOfCola.getSodaType()).isEqualTo("Cola");
        assertThat(allOfCola.isCaffeine()).isTrue();
        assertThat(allOfCola.getColor()).isNull();

        AllOf allOfFanta = objectMapper.readValue(FANTA_JSON, AllOf.class);
        assertThat(allOfFanta.getSodaType()).isEqualTo("Fanta");
        assertThat(allOfFanta.getColor()).isEqualTo("orange");
        assertThat(allOfFanta.isCaffeine()).isNull();
    }

    @Test
    void testColaSerialization()
        throws JsonProcessingException
    {
        var expected = objectMapper.readValue(COLA_JSON, JsonNode.class);
        var actual = objectMapper.valueToTree(expected);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testFantaSerialization()
        throws JsonProcessingException
    {
        var expected = objectMapper.readValue(FANTA_JSON, JsonNode.class);
        var actual = objectMapper.valueToTree(expected);

        assertThat(actual).isEqualTo(expected);
    }

    /**
     * Taken from {@link com.sap.cloud.sdk.services.openapi.apiclient.ApiClient}
     */
    @Nonnull
    private static ObjectMapper newDefaultObjectMapper()
    {
        return new Jackson2ObjectMapperBuilder()
            .modules(new JavaTimeModule())
            .visibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
            .visibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE)
            .build();
    }
}
