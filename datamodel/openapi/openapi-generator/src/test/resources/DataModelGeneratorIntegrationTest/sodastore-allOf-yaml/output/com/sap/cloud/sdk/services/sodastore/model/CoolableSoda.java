/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

/*
 * Soda Store API
 * API for managing sodas in a soda store
 *
 * The version of the OpenAPI document: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package com.sap.cloud.sdk.services.sodastore.model;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * CoolableSoda
 */

// CHECKSTYLE:OFF
public class CoolableSoda 
// CHECKSTYLE:ON
{
  @JsonProperty("id")
  private Long id;

  @JsonProperty("name")
  private String name;

  @JsonProperty("brand")
  private String brand;

  @JsonProperty("flavor")
  private String flavor;

  @JsonProperty("price")
  private Float price;

  /**
   * Gets or Sets coolness
   */
  public enum CoolnessEnum {
    /**
    * The COOL option of this CoolableSoda
    */
    COOL("cool"),
    
    /**
    * The UNCOOL option of this CoolableSoda
    */
    UNCOOL("uncool");

    private String value;

    CoolnessEnum(String value) {
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
    * @return The enum value of type CoolableSoda
    */
    @JsonCreator
    @Nonnull public static CoolnessEnum fromValue(@Nonnull final String value) {
      for (CoolnessEnum b : CoolnessEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("coolness")
  private CoolnessEnum coolness;

  @JsonAnySetter
  @JsonAnyGetter
  private final Map<String, Object> cloudSdkCustomFields = new LinkedHashMap<>();

   /**
   * Set the id of this {@link CoolableSoda} instance and return the same instance.
   *
   * @param id  The id of this {@link CoolableSoda}
   * @return The same instance of this {@link CoolableSoda} class
   */
   @Nonnull public CoolableSoda id(@Nonnull final Long id) {
    this.id = id;
    return this;
  }

   /**
   * Get id
   * @return id  The id of this {@link CoolableSoda} instance.
  **/
  @Nonnull public Long getId() {
    return id;
  }

  /**
  * Set the id of this {@link CoolableSoda} instance.
  *
  * @param id  The id of this {@link CoolableSoda}
  */
  public void setId( @Nonnull final Long id) {
    this.id = id;
  }

   /**
   * Set the name of this {@link CoolableSoda} instance and return the same instance.
   *
   * @param name  The name of this {@link CoolableSoda}
   * @return The same instance of this {@link CoolableSoda} class
   */
   @Nonnull public CoolableSoda name(@Nonnull final String name) {
    this.name = name;
    return this;
  }

   /**
   * Get name
   * @return name  The name of this {@link CoolableSoda} instance.
  **/
  @Nonnull public String getName() {
    return name;
  }

  /**
  * Set the name of this {@link CoolableSoda} instance.
  *
  * @param name  The name of this {@link CoolableSoda}
  */
  public void setName( @Nonnull final String name) {
    this.name = name;
  }

   /**
   * Set the brand of this {@link CoolableSoda} instance and return the same instance.
   *
   * @param brand  The brand of this {@link CoolableSoda}
   * @return The same instance of this {@link CoolableSoda} class
   */
   @Nonnull public CoolableSoda brand(@Nonnull final String brand) {
    this.brand = brand;
    return this;
  }

   /**
   * Get brand
   * @return brand  The brand of this {@link CoolableSoda} instance.
  **/
  @Nonnull public String getBrand() {
    return brand;
  }

  /**
  * Set the brand of this {@link CoolableSoda} instance.
  *
  * @param brand  The brand of this {@link CoolableSoda}
  */
  public void setBrand( @Nonnull final String brand) {
    this.brand = brand;
  }

   /**
   * Set the flavor of this {@link CoolableSoda} instance and return the same instance.
   *
   * @param flavor  The flavor of this {@link CoolableSoda}
   * @return The same instance of this {@link CoolableSoda} class
   */
   @Nonnull public CoolableSoda flavor(@Nonnull final String flavor) {
    this.flavor = flavor;
    return this;
  }

   /**
   * Get flavor
   * @return flavor  The flavor of this {@link CoolableSoda} instance.
  **/
  @Nonnull public String getFlavor() {
    return flavor;
  }

  /**
  * Set the flavor of this {@link CoolableSoda} instance.
  *
  * @param flavor  The flavor of this {@link CoolableSoda}
  */
  public void setFlavor( @Nonnull final String flavor) {
    this.flavor = flavor;
  }

   /**
   * Set the price of this {@link CoolableSoda} instance and return the same instance.
   *
   * @param price  The price of this {@link CoolableSoda}
   * @return The same instance of this {@link CoolableSoda} class
   */
   @Nonnull public CoolableSoda price(@Nonnull final Float price) {
    this.price = price;
    return this;
  }

   /**
   * Get price
   * @return price  The price of this {@link CoolableSoda} instance.
  **/
  @Nonnull public Float getPrice() {
    return price;
  }

  /**
  * Set the price of this {@link CoolableSoda} instance.
  *
  * @param price  The price of this {@link CoolableSoda}
  */
  public void setPrice( @Nonnull final Float price) {
    this.price = price;
  }

   /**
   * Set the coolness of this {@link CoolableSoda} instance and return the same instance.
   *
   * @param coolness  The coolness of this {@link CoolableSoda}
   * @return The same instance of this {@link CoolableSoda} class
   */
   @Nonnull public CoolableSoda coolness(@Nonnull final CoolnessEnum coolness) {
    this.coolness = coolness;
    return this;
  }

   /**
   * Get coolness
   * @return coolness  The coolness of this {@link CoolableSoda} instance.
  **/
  @Nonnull public CoolnessEnum getCoolness() {
    return coolness;
  }

  /**
  * Set the coolness of this {@link CoolableSoda} instance.
  *
  * @param coolness  The coolness of this {@link CoolableSoda}
  */
  public void setCoolness( @Nonnull final CoolnessEnum coolness) {
    this.coolness = coolness;
  }

  /**
   * Get the names of the unrecognizable properties of the {@link CoolableSoda}.
   * @return The set of properties names
   */
  @JsonIgnore
  @Nonnull
  public Set<String> getCustomFieldNames() {
    return cloudSdkCustomFields.keySet();
  }

  /**
   * Get the value of an unrecognizable property of this {@link CoolableSoda} instance.
   * @param name  The name of the property
   * @return The value of the property
   * @throws NoSuchElementException  If no property with the given name could be found.
   */
  @Nullable
  public Object getCustomField(@Nonnull final String name) throws NoSuchElementException {
    if( !cloudSdkCustomFields.containsKey(name) ) {
        throw new NoSuchElementException("CoolableSoda has no field with name '" + name + "'.");
    }
    return cloudSdkCustomFields.get(name);
  }

  /**
   * Set an unrecognizable property of this {@link CoolableSoda} instance. If the map previously contained a mapping
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
    final CoolableSoda coolableSoda = (CoolableSoda) o;
    return Objects.equals(this.cloudSdkCustomFields, coolableSoda.cloudSdkCustomFields) &&
        Objects.equals(this.id, coolableSoda.id) &&
        Objects.equals(this.name, coolableSoda.name) &&
        Objects.equals(this.brand, coolableSoda.brand) &&
        Objects.equals(this.flavor, coolableSoda.flavor) &&
        Objects.equals(this.price, coolableSoda.price) &&
        Objects.equals(this.coolness, coolableSoda.coolness);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, brand, flavor, price, coolness, cloudSdkCustomFields);
  }

  @Override
  @Nonnull public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("class CoolableSoda {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    brand: ").append(toIndentedString(brand)).append("\n");
    sb.append("    flavor: ").append(toIndentedString(flavor)).append("\n");
    sb.append("    price: ").append(toIndentedString(price)).append("\n");
    sb.append("    coolness: ").append(toIndentedString(coolness)).append("\n");
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

