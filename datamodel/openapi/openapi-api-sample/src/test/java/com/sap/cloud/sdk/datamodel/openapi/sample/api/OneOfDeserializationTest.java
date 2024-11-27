package com.sap.cloud.sdk.datamodel.openapi.sample.api;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.AllOf;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.AnyOf;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.Cola;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.Fanta;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.OneOf;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.OneOfWithDiscriminator;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.OneOfWithDiscriminatorAndMapping;

public class OneOfDeserializationTest
{
    String cola = """
        {
          "sodaType": "Cola",
          "caffeine": true
        }""";
    String fanta = """
        {
          "sodaType": "Fanta",
          "color": "orange"
        }""";

    @Test
    void oneOf()
    {
        // useOneOfInterfaces is enabled and no discriminator is present, the deserialization will fail
        // The fix is to use set a mixIn in the ObjectMapper
        assertThatThrownBy(() -> newDefaultObjectMapper().readValue(cola, OneOf.class))
            .isInstanceOf(InvalidTypeIdException.class)
            .hasMessageContaining("Could not resolve subtype");
        assertThatThrownBy(() -> newDefaultObjectMapper().readValue(fanta, OneOf.class))
            .isInstanceOf(InvalidTypeIdException.class)
            .hasMessageContaining("Could not resolve subtype");
    }

    @Test
    void oneOfWithDiscriminator()
        throws JsonProcessingException
    {
        Cola oneOfCola = (Cola) newDefaultObjectMapper().readValue(cola, OneOfWithDiscriminator.class);
        assertThat(oneOfCola.getSodaType()).isEqualTo("Cola");
        assertThat(oneOfCola.isCaffeine()).isTrue();

        Fanta oneOfFanta = (Fanta) newDefaultObjectMapper().readValue(fanta, OneOfWithDiscriminator.class);
        assertThat(oneOfFanta.getSodaType()).isEqualTo("Fanta");
        assertThat(oneOfFanta.getColor()).isEqualTo("orange");
    }

    @Test
    void oneOfWithDiscriminatorAndMapping()
        throws JsonProcessingException
    {
        Cola oneOfCola = (Cola) newDefaultObjectMapper().readValue(cola, OneOfWithDiscriminatorAndMapping.class);
        assertThat(oneOfCola.getSodaType()).isEqualTo("Cola");
        assertThat(oneOfCola.isCaffeine()).isTrue();

        Fanta oneOfFanta = (Fanta) newDefaultObjectMapper().readValue(fanta, OneOfWithDiscriminatorAndMapping.class);
        assertThat(oneOfFanta.getSodaType()).isEqualTo("Fanta");
        assertThat(oneOfFanta.getColor()).isEqualTo("orange");
    }

    @Test
    void anyOf()
        throws JsonProcessingException
    {
        AnyOf anyOfCola = newDefaultObjectMapper().readValue(cola, AnyOf.class);
        assertThat(anyOfCola.getSodaType()).isEqualTo("Cola");
        assertThat(anyOfCola.isCaffeine()).isTrue();

        AnyOf anyOfFanta = newDefaultObjectMapper().readValue(fanta, AnyOf.class);
        assertThat(anyOfFanta.getSodaType()).isEqualTo("Fanta");
        assertThat(anyOfFanta.getColor()).isEqualTo("orange");
    }

    @Test
    void allOf()
        throws JsonProcessingException
    {
        AllOf allOfCola = newDefaultObjectMapper().readValue(cola, AllOf.class);
        assertThat(allOfCola.getSodaType()).isEqualTo("Cola");
        assertThat(allOfCola.isCaffeine()).isTrue();
        assertThat(allOfCola.getColor()).isNull();


        AllOf allOfFanta = newDefaultObjectMapper().readValue(fanta, AllOf.class);
        assertThat(allOfFanta.getSodaType()).isEqualTo("Fanta");
        assertThat(allOfFanta.getColor()).isEqualTo("orange");
        assertThat(allOfFanta.isCaffeine()).isNull();
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
