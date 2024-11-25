/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

/*
 * SodaStore API
 * API for managing soda products and orders in SodaStore.
 *
 * The version of the OpenAPI document: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package com.sap.cloud.sdk.services.builder.model;

import java.util.Objects;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * SodaWithId
 */
// CHECKSTYLE:OFF
public class SodaWithId {
// CHECKSTYLE:ON
  @JsonProperty("name")
  private String name;

  @JsonProperty("brand")
  private String brand;

  @JsonProperty("quantity")
  private Integer quantity;

  /**
   * Gets or Sets packaging
   */
  public enum PackagingEnum {
    /**
    * The GLASS option of this SodaWithId
    */
    GLASS("glass"),
    
    /**
    * The CARTON option of this SodaWithId
    */
    CARTON("carton"),
    
    /**
    * The CAN option of this SodaWithId
    */
    CAN("can");

    private String value;

    PackagingEnum(String value) {
      this.value = value;
    }

    /**
    * Get the value of the enum
    * @return The enum value
    */
    @JsonValue
    @Nonnull public String getValue() {
      return value;
    }

    /**
    * Get the String value of the enum value.
    * @return The enum value as String
    */
    @Override
    @Nonnull public String toString() {
      return String.valueOf(value);
    }

    /**
    * Get the enum value from a String value
    * @param value The String value
    * @return The enum value of type SodaWithId
    */
    @JsonCreator
    @Nonnull public static PackagingEnum fromValue(@Nonnull final String value) {
      for (PackagingEnum b : PackagingEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("packaging")
  private PackagingEnum packaging;

  @JsonProperty("price")
  private Float price;

  @JsonProperty("id")
  private Long id;

  @JsonAnySetter
  @JsonAnyGetter
  private final Map<String, Object> cloudSdkCustomFields = new LinkedHashMap<>();

  /**
   * Set the name of this {@link SodaWithId} instance and return the same instance.
   *
   * @param name  The name of this {@link SodaWithId}
   * @return The same instance of this {@link SodaWithId} class
   */
  @Nonnull public SodaWithId name( @Nonnull final String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name  The name of this {@link SodaWithId} instance.
   */
  @Nonnull
  public String getName() {
    return name;
  }

  /**
   * Set the name of this {@link SodaWithId} instance.
   *
   * @param name  The name of this {@link SodaWithId}
   */
  public void setName( @Nonnull final String name) {
    this.name = name;
  }

  /**
   * Set the brand of this {@link SodaWithId} instance and return the same instance.
   *
   * @param brand  The brand of this {@link SodaWithId}
   * @return The same instance of this {@link SodaWithId} class
   */
  @Nonnull public SodaWithId brand( @Nonnull final String brand) {
    this.brand = brand;
    return this;
  }

  /**
   * Get brand
   * @return brand  The brand of this {@link SodaWithId} instance.
   */
  @Nonnull
  public String getBrand() {
    return brand;
  }

  /**
   * Set the brand of this {@link SodaWithId} instance.
   *
   * @param brand  The brand of this {@link SodaWithId}
   */
  public void setBrand( @Nonnull final String brand) {
    this.brand = brand;
  }

  /**
   * Set the quantity of this {@link SodaWithId} instance and return the same instance.
   *
   * @param quantity  The quantity of this {@link SodaWithId}
   * @return The same instance of this {@link SodaWithId} class
   */
  @Nonnull public SodaWithId quantity( @Nonnull final Integer quantity) {
    this.quantity = quantity;
    return this;
  }

  /**
   * Get quantity
   * @return quantity  The quantity of this {@link SodaWithId} instance.
   */
  @Nonnull
  public Integer getQuantity() {
    return quantity;
  }

  /**
   * Set the quantity of this {@link SodaWithId} instance.
   *
   * @param quantity  The quantity of this {@link SodaWithId}
   */
  public void setQuantity( @Nonnull final Integer quantity) {
    this.quantity = quantity;
  }

  /**
   * Set the packaging of this {@link SodaWithId} instance and return the same instance.
   *
   * @param packaging  The packaging of this {@link SodaWithId}
   * @return The same instance of this {@link SodaWithId} class
   */
  @Nonnull public SodaWithId packaging( @Nullable final PackagingEnum packaging) {
    this.packaging = packaging;
    return this;
  }

  /**
   * Get packaging
   * @return packaging  The packaging of this {@link SodaWithId} instance.
   */
  @Nonnull
  public PackagingEnum getPackaging() {
    return packaging;
  }

  /**
   * Set the packaging of this {@link SodaWithId} instance.
   *
   * @param packaging  The packaging of this {@link SodaWithId}
   */
  public void setPackaging( @Nullable final PackagingEnum packaging) {
    this.packaging = packaging;
  }

  /**
   * Set the price of this {@link SodaWithId} instance and return the same instance.
   *
   * @param price  The price of this {@link SodaWithId}
   * @return The same instance of this {@link SodaWithId} class
   */
  @Nonnull public SodaWithId price( @Nonnull final Float price) {
    this.price = price;
    return this;
  }

  /**
   * Get price
   * @return price  The price of this {@link SodaWithId} instance.
   */
  @Nonnull
  public Float getPrice() {
    return price;
  }

  /**
   * Set the price of this {@link SodaWithId} instance.
   *
   * @param price  The price of this {@link SodaWithId}
   */
  public void setPrice( @Nonnull final Float price) {
    this.price = price;
  }

  /**
   * Set the id of this {@link SodaWithId} instance and return the same instance.
   *
   * @param id  The id of this {@link SodaWithId}
   * @return The same instance of this {@link SodaWithId} class
   */
  @Nonnull public SodaWithId id( @Nullable final Long id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id  The id of this {@link SodaWithId} instance.
   */
  @Nonnull
  public Long getId() {
    return id;
  }

  /**
   * Set the id of this {@link SodaWithId} instance.
   *
   * @param id  The id of this {@link SodaWithId}
   */
  public void setId( @Nullable final Long id) {
    this.id = id;
  }

  /**
   * Get the names of the unrecognizable properties of the {@link SodaWithId}.
   * @return The set of properties names
   */
  @JsonIgnore
  @Nonnull
  public Set<String> getCustomFieldNames() {
    return cloudSdkCustomFields.keySet();
  }

  /**
   * Get the value of an unrecognizable property of this {@link SodaWithId} instance.
   * @param name  The name of the property
   * @return The value of the property
   * @throws NoSuchElementException  If no property with the given name could be found.
   */
  @Nullable
  public Object getCustomField( @Nonnull final String name ) throws NoSuchElementException {
    if( !cloudSdkCustomFields.containsKey(name) ) {
        throw new NoSuchElementException("SodaWithId has no field with name '" + name + "'.");
    }
    return cloudSdkCustomFields.get(name);
  }

  /**
   * Set an unrecognizable property of this {@link SodaWithId} instance. If the map previously contained a mapping
   * for the key, the old value is replaced by the specified value.
   * @param customFieldName The name of the property
   * @param customFieldValue The value of the property
   */
  @JsonIgnore
  public void setCustomField( @Nonnull String customFieldName, @Nullable Object customFieldValue )
  {
      cloudSdkCustomFields.put(customFieldName, customFieldValue);
  }


  @Override
  public boolean equals(@Nullable final java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final SodaWithId sodaWithId = (SodaWithId) o;
    return Objects.equals(this.cloudSdkCustomFields, sodaWithId.cloudSdkCustomFields) &&
        Objects.equals(this.name, sodaWithId.name) &&
        Objects.equals(this.brand, sodaWithId.brand) &&
        Objects.equals(this.quantity, sodaWithId.quantity) &&
        Objects.equals(this.packaging, sodaWithId.packaging) &&
        Objects.equals(this.price, sodaWithId.price) &&
        Objects.equals(this.id, sodaWithId.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, brand, quantity, packaging, price, id, cloudSdkCustomFields);
  }

  @Override
  @Nonnull public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("class SodaWithId {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    brand: ").append(toIndentedString(brand)).append("\n");
    sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
    sb.append("    packaging: ").append(toIndentedString(packaging)).append("\n");
    sb.append("    price: ").append(toIndentedString(price)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    cloudSdkCustomFields.forEach((k,v) -> sb.append("    ").append(k).append(": ").append(toIndentedString(v)).append("\n"));
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(final java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

