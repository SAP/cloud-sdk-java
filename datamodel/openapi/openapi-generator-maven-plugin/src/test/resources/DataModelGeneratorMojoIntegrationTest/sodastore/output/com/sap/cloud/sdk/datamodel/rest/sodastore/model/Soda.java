/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

/*
 * Soda Store API
 * API for managing sodas in a soda store
 *
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package com.sap.cloud.sdk.datamodel.rest.sodastore.model;

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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Soda
 */
// CHECKSTYLE:OFF
public class Soda 
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

  @JsonAnySetter
  @JsonAnyGetter
  private final Map<String, Object> cloudSdkCustomFields = new LinkedHashMap<>();

  /**
   * Set the id of this {@link Soda} instance and return the same instance.
   *
   * @param id  The id of this {@link Soda}
   * @return The same instance of this {@link Soda} class
   */
  @Nonnull public Soda id( @Nullable final Long id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id  The id of this {@link Soda} instance.
   */
  @Nonnull
  public Long getId() {
    return id;
  }

  /**
   * Set the id of this {@link Soda} instance.
   *
   * @param id  The id of this {@link Soda}
   */
  public void setId( @Nullable final Long id) {
    this.id = id;
  }

  /**
   * Set the name of this {@link Soda} instance and return the same instance.
   *
   * @param name  The name of this {@link Soda}
   * @return The same instance of this {@link Soda} class
   */
  @Nonnull public Soda name( @Nonnull final String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name  The name of this {@link Soda} instance.
   */
  @Nonnull
  public String getName() {
    return name;
  }

  /**
   * Set the name of this {@link Soda} instance.
   *
   * @param name  The name of this {@link Soda}
   */
  public void setName( @Nonnull final String name) {
    this.name = name;
  }

  /**
   * Set the brand of this {@link Soda} instance and return the same instance.
   *
   * @param brand  The brand of this {@link Soda}
   * @return The same instance of this {@link Soda} class
   */
  @Nonnull public Soda brand( @Nonnull final String brand) {
    this.brand = brand;
    return this;
  }

  /**
   * Get brand
   * @return brand  The brand of this {@link Soda} instance.
   */
  @Nonnull
  public String getBrand() {
    return brand;
  }

  /**
   * Set the brand of this {@link Soda} instance.
   *
   * @param brand  The brand of this {@link Soda}
   */
  public void setBrand( @Nonnull final String brand) {
    this.brand = brand;
  }

  /**
   * Set the flavor of this {@link Soda} instance and return the same instance.
   *
   * @param flavor  The flavor of this {@link Soda}
   * @return The same instance of this {@link Soda} class
   */
  @Nonnull public Soda flavor( @Nonnull final String flavor) {
    this.flavor = flavor;
    return this;
  }

  /**
   * Get flavor
   * @return flavor  The flavor of this {@link Soda} instance.
   */
  @Nonnull
  public String getFlavor() {
    return flavor;
  }

  /**
   * Set the flavor of this {@link Soda} instance.
   *
   * @param flavor  The flavor of this {@link Soda}
   */
  public void setFlavor( @Nonnull final String flavor) {
    this.flavor = flavor;
  }

  /**
   * Set the price of this {@link Soda} instance and return the same instance.
   *
   * @param price  The price of this {@link Soda}
   * @return The same instance of this {@link Soda} class
   */
  @Nonnull public Soda price( @Nonnull final Float price) {
    this.price = price;
    return this;
  }

  /**
   * Get price
   * @return price  The price of this {@link Soda} instance.
   */
  @Nonnull
  public Float getPrice() {
    return price;
  }

  /**
   * Set the price of this {@link Soda} instance.
   *
   * @param price  The price of this {@link Soda}
   */
  public void setPrice( @Nonnull final Float price) {
    this.price = price;
  }

  /**
   * Get the names of the unrecognizable properties of the {@link Soda}.
   * @return The set of properties names
   */
  @JsonIgnore
  @Nonnull
  public Set<String> getCustomFieldNames() {
    return cloudSdkCustomFields.keySet();
  }

  /**
   * Get the value of an unrecognizable property of this {@link Soda} instance.
   * @deprecated Use {@link #toMap()} instead.
   * @param name  The name of the property
   * @return The value of the property
   * @throws NoSuchElementException  If no property with the given name could be found.
   */
  @Nullable
  @Deprecated
  public Object getCustomField( @Nonnull final String name ) throws NoSuchElementException {
    if( !cloudSdkCustomFields.containsKey(name) ) {
        throw new NoSuchElementException("Soda has no field with name '" + name + "'.");
    }
    return cloudSdkCustomFields.get(name);
  }

  /**
   * Get the value of all properties of this {@link Soda} instance including unrecognized properties.
   *
   * @return The map of all properties
   */
  @JsonIgnore
  @Nonnull
  public Map<String, Object> toMap()
  {
    final Map<String, Object> declaredFields = new LinkedHashMap<>(cloudSdkCustomFields);
    if( id != null ) declaredFields.put("id", id);
    if( name != null ) declaredFields.put("name", name);
    if( brand != null ) declaredFields.put("brand", brand);
    if( flavor != null ) declaredFields.put("flavor", flavor);
    if( price != null ) declaredFields.put("price", price);
    return declaredFields;
  }

  /**
   * Set an unrecognizable property of this {@link Soda} instance. If the map previously contained a mapping
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
    final Soda soda = (Soda) o;
    return Objects.equals(this.cloudSdkCustomFields, soda.cloudSdkCustomFields) &&
        Objects.equals(this.id, soda.id) &&
        Objects.equals(this.name, soda.name) &&
        Objects.equals(this.brand, soda.brand) &&
        Objects.equals(this.flavor, soda.flavor) &&
        Objects.equals(this.price, soda.price);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, brand, flavor, price, cloudSdkCustomFields);
  }

  @Override
  @Nonnull public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("class Soda {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    brand: ").append(toIndentedString(brand)).append("\n");
    sb.append("    flavor: ").append(toIndentedString(flavor)).append("\n");
    sb.append("    price: ").append(toIndentedString(price)).append("\n");
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

