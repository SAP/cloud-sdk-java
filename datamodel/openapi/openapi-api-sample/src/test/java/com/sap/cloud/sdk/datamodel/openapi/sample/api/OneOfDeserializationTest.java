package com.sap.cloud.sdk.datamodel.openapi.sample.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.AllOf;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.AnyOf;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.Bar;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.Cola;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.Fanta;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.FantaFlavor;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.FlavorType;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.Foo;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.OneOf;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.OneOfWithDiscriminator;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.OneOfWithDiscriminatorAndMapping;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.OneOfWithEnumDiscriminator;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.OneOfWithMatrix;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.OneOfWithMatrixAndArray;

class OneOfDeserializationTest
{
    private static final ObjectMapper objectMapper = newDefaultObjectMapper();

    private static final Cola COLA_OBJECT = Cola.create().caffeine(true).sodaType("Cola");
    private static final Fanta FANTA_OBJECT =
        Fanta
            .create()
            .color("orange")
            .sodaType("Fanta")
            .flavor(new FantaFlavor.InnerFlavorType(FlavorType.create().intensity(3).nuance("wood")));
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
    private static final String FANTA_FLAVOR_ARRAY_JSON = """
        {
          "sodaType": "Fanta",
          "color": "orange",
          "flavor": [
            {"intensity":3,"nuance":"wood"},
            {"intensity":5,"nuance":"citrus"}
          ]
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

        assertThatThrownBy(() -> objectMapper.readValue(UNKNOWN_JSON, OneOfWithDiscriminator.class))
            .isInstanceOf(JsonProcessingException.class);
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

    static Stream<Class<?>> oneOfStrategiesProvider()
    {
        return Stream.of(OneOf.class, OneOfWithDiscriminator.class, OneOfWithDiscriminatorAndMapping.class);
    }

    @ParameterizedTest( name = "Deserialization with strategy: {0}" )
    @MethodSource( "oneOfStrategiesProvider" )
    void oneOfWithNestedArrayOfObjects( Class<?> strategy )
        throws JsonProcessingException
    {
        Object actual = objectMapper.readValue(FANTA_FLAVOR_ARRAY_JSON, strategy);

        assertThat(actual)
            .describedAs("Object should automatically be deserialized as Fanta with JSON subtype deduction")
            .isInstanceOf(Fanta.class);
        var fanta = (Fanta) actual;
        assertThat(fanta.getFlavor())
            .describedAs("Flavor should be deserialized as wrapper class for a list of FlavorType instances")
            .isInstanceOf(FantaFlavor.InnerFlavorTypes.class);
        var flavorTypes = (FantaFlavor.InnerFlavorTypes) fanta.getFlavor();
        assertThat(flavorTypes.values())
            .describedAs("Flavor should be deserialized as a list of FlavorType instances")
            .isNotEmpty()
            .allMatch(FlavorType.class::isInstance);

    }

    @Test
    void oneOfWIthMatrixOfObjects()
        throws JsonProcessingException
    {
        var json = """
            [
              [1, 2, 3 ],
              [4, 5, 6 ]
            ]
            """;
        var matrix = objectMapper.readValue(json, OneOfWithMatrix.class);
        assertThat(matrix)
            .describedAs("Object should be deserialized as InnerIntegers2D")
            .isInstanceOf(OneOfWithMatrix.InnerIntegers2D.class);
        var integers2D = (OneOfWithMatrix.InnerIntegers2D) matrix;
        assertThat(integers2D.values()).isEqualTo(List.of(List.of(1, 2, 3), List.of(4, 5, 6)));

        assertThatThrownBy(() -> objectMapper.readValue(json, OneOfWithMatrixAndArray.class))
            .hasMessageContaining("Conflicting array-delegate creators")
            .isInstanceOf(InvalidDefinitionException.class);
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

        AnyOf actual = objectMapper.readValue(FANTA_FLAVOR_ARRAY_JSON, AnyOf.class);

        assertThat(actual.getSodaType()).isEqualTo("Fanta");
        assertThat(actual.getColor()).isEqualTo("orange");
        assertThat(actual.getFlavor()).isInstanceOf(FantaFlavor.InnerFlavorTypes.class);
        assertThat(((FantaFlavor.InnerFlavorTypes) actual.getFlavor()).values()).allMatch(FlavorType.class::isInstance);
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

    @Test
    void testDeserializationWithEnumDiscriminator()
        throws JsonProcessingException
    {
        var json = """
            {
              "foo": "asdf",
              "disc": "disc_foo"
            }""";
        assertThat(objectMapper.readValue(json, OneOfWithEnumDiscriminator.class))
            .isInstanceOf(Foo.class)
            .isEqualTo(Foo.create().foo("asdf").disc(Foo.DiscEnum.DISC_FOO));
        json = """
            {
              "bar": "asdf",
              "disc": "disc_bar"
            }""";
        assertThat(objectMapper.readValue(json, OneOfWithEnumDiscriminator.class))
            .isInstanceOf(Bar.class)
            .isEqualTo(Bar.create().bar("asdf").disc(Bar.DiscEnum.DISC_BAR));

        assertThatThrownBy(() -> objectMapper.readValue("{ \"type\": \"unknown\" }", OneOfWithEnumDiscriminator.class))
            .isInstanceOf(JsonProcessingException.class);
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
