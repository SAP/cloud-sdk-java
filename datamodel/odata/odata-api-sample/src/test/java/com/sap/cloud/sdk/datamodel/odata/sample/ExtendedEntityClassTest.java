/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.sample;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

class ExtendedEntityClassTest
{
    private static final String PRODUCT_INPUT =
        """
        {
          "versionIdentifier": "testVersion",
          "Id": "1005",
          "Name": "Cloud SDK",
          "Image": "Rk9PIEJBUg==",
          "Weight": 42,
          "Shoesize": 8
        }
        """;
    private static final String EXPECTED_PRODUCT_OUTPUT =
        """
        {
        "versionIdentifier":"testVersion",
        "Id":1005,
        "Name":"Cloud SDK",
        "ShelfId":null,
        "VendorId":null,
        "Price":null,
        "Image":"Rk9PIEJBUg==",
        "Vendor":null,
        "Shelf":null,
        "Weight":42,
        "Shoesize":8
        }
        """;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Image
    {
        private byte[] binary;
    }

    public static class CustomImageAdapter
    {
        public static class Serializer extends JsonSerializer<Image>
        {
            @Override
            @SneakyThrows
            public void serialize( final Image value, final JsonGenerator gen, final SerializerProvider provider )
            {
                gen.writeString(new String(Base64.getEncoder().encode(value.binary), StandardCharsets.UTF_8));
            }
        }

        public static class Deserializer extends JsonDeserializer<Image>
        {
            @Override
            @SneakyThrows
            public Image deserialize( final JsonParser parser, final DeserializationContext context )
            {
                final byte[] bytes = parser.readValueAs(String.class).getBytes(StandardCharsets.UTF_8);
                return new Image(Base64.getDecoder().decode(bytes));
            }
        }

        public static class TypeAdapter extends com.google.gson.TypeAdapter<Image>
        {
            @SneakyThrows
            @Override
            public void write( final JsonWriter out, final Image image )
            {
                out.value(new String(Base64.getEncoder().encode(image.binary), StandardCharsets.UTF_8));
            }

            @SneakyThrows
            @Override
            public Image read( final JsonReader in )
            {
                if( in.peek() == JsonToken.NULL ) {
                    in.nextNull();
                    return null;
                }
                return new Image(Base64.getDecoder().decode(in.nextString().getBytes(StandardCharsets.UTF_8)));
            }
        }
    }

    @Data
    @EqualsAndHashCode( callSuper = true )
    @JsonAdapter( ODataVdmEntityAdapterFactory.class )
    public static class CustomProduct extends Product
    {
        /* New custom field as property */
        @SerializedName( "Weight" )
        @JsonProperty( "Weight" )
        @Nullable
        private Integer weight;

        /* Overriding existing OData property */
        @SerializedName( "Image" )
        @JsonProperty( "Image" )
        @JsonSerialize( using = CustomImageAdapter.Serializer.class )
        @JsonDeserialize( using = CustomImageAdapter.Deserializer.class )
        @JsonAdapter( CustomImageAdapter.TypeAdapter.class )
        @Nullable
        private Image CustomImage;

        @Override
        public void fromMap( final Map<String, Object> inputValues )
        {
            super.fromMap(inputValues);
        }
    }

    @Test
    void testGsonDeserialization()
    {
        final CustomProduct product = new Gson().fromJson(PRODUCT_INPUT, CustomProduct.class);

        assertThat(product).isNotNull();

        assertThat(product.getVersionIdentifier()).isNotEmpty().contains("testVersion");
        assertThat(product.getCustomImage()).isEqualTo(new Image("FOO BAR".getBytes(StandardCharsets.UTF_8)));
        assertThat(product.getWeight()).isEqualTo(42);
        assertThat(product.getName()).isEqualTo("Cloud SDK");
        assertThat(product.getId()).isEqualTo(1005);
        assertThat(product.<Integer> getCustomField("Shoesize")).isEqualTo(8);
    }

    @Test
    void testGsonSerialization()
    {
        final CustomProduct customProduct = new CustomProduct();
        customProduct.setVersionIdentifier("testVersion");
        customProduct.setId(1005);
        customProduct.setName("Cloud SDK");
        customProduct.setWeight(42);
        customProduct.setCustomImage(new Image("FOO BAR".getBytes(StandardCharsets.UTF_8)));
        customProduct.setCustomField("Shoesize", 8);

        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls().disableHtmlEscaping();

        final String actualSerializedProduct = gsonBuilder.create().toJson(customProduct);

        assertThat(actualSerializedProduct).isNotNull();
        assertThat(actualSerializedProduct).isEqualTo(EXPECTED_PRODUCT_OUTPUT);
    }

    @Test
    void testJacksonDeserialization()
        throws Exception
    {
        final ObjectMapper mapper = new ObjectMapper();
        final CustomProduct product = mapper.readValue(PRODUCT_INPUT, CustomProduct.class);

        assertThat(product).isNotNull();

        assertThat(product.getVersionIdentifier()).isNotEmpty().contains("testVersion");
        assertThat(product.getCustomImage()).isEqualTo(new Image("FOO BAR".getBytes(StandardCharsets.UTF_8)));
        assertThat(product.getWeight()).isEqualTo(42);
        assertThat(product.getName()).isEqualTo("Cloud SDK");
        assertThat(product.getId()).isEqualTo(1005);
        assertThat(product.<Integer> getCustomField("Shoesize")).isEqualTo(8);
    }

    @Test
    void testJacksonSerialization()
        throws Exception
    {
        final CustomProduct customProduct = new CustomProduct();
        customProduct.setVersionIdentifier("testVersion");
        customProduct.setId(1005);
        customProduct.setName("Cloud SDK");
        customProduct.setWeight(42);
        customProduct.setCustomImage(new Image("FOO BAR".getBytes(StandardCharsets.UTF_8)));
        customProduct.setCustomField("Shoesize", 8);

        final ObjectMapper mapper = new ObjectMapper();
        final String actualSerializedProduct = mapper.writeValueAsString(customProduct);

        assertThat(actualSerializedProduct).isNotNull().isEqualTo(EXPECTED_PRODUCT_OUTPUT);
    }

    @Test
    void testFromMapWithNavigationProperties()
    {
        final Map<String, Object> productMap = new HashMap<>();
        final Collection<Map<String, Object>> listOfMaps = new ArrayList<>();
        final Map<String, Object> someMapRepresentationOfProductText = new HashMap<>();
        listOfMaps.add(someMapRepresentationOfProductText);
        listOfMaps.add(someMapRepresentationOfProductText);
        listOfMaps.add(someMapRepresentationOfProductText);
        productMap.put("Shelf", listOfMaps);

        final CustomProduct sut = new CustomProduct();
        sut.fromMap(productMap);

        assertThat(sut.getShelfIfPresent()).isNotEmpty();
        assertThat(sut.getShelfIfPresent().get()).hasSize(3);
    }
}
