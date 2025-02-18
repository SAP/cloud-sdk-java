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
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * NewSoda
 */
// CHECKSTYLE:OFF
public class NewSoda 
// CHECKSTYLE:ON
{
  @JsonProperty("name")
  private String name;

  @JsonProperty("brand")
  private String brand;

  @JsonProperty("zero")
  private Boolean zero;

  @JsonProperty("since")
  private LocalDate since;

  @JsonProperty("flavor")
  private String flavor;

  @JsonProperty("price")
  private Float price;

  @JsonAnySetter
  @JsonAnyGetter
  private final Map<String, Object> cloudSdkCustomFields = new LinkedHashMap<>();

  /**
   * Set the name of this {@link NewSoda} instance and return the same instance.
   *
   * @param name  The name of this {@link NewSoda}
   * @return The same instance of this {@link NewSoda} class
   */
  @Nonnull public NewSoda name( @Nonnull final String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name  The name of this {@link NewSoda} instance.
   */
  @Nonnull
  public String getName() {
    return name;
  }

  /**
   * Set the name of this {@link NewSoda} instance.
   *
   * @param name  The name of this {@link NewSoda}
   */
  public void setName( @Nonnull final String name) {
    this.name = name;
  }

  /**
   * Set the brand of this {@link NewSoda} instance and return the same instance.
   *
   * @param brand  The brand of this {@link NewSoda}
   * @return The same instance of this {@link NewSoda} class
   */
  @Nonnull public NewSoda brand( @Nonnull final String brand) {
    this.brand = brand;
    return this;
  }

  /**
   * Get brand
   * @return brand  The brand of this {@link NewSoda} instance.
   */
  @Nonnull
  public String getBrand() {
    return brand;
  }

  /**
   * Set the brand of this {@link NewSoda} instance.
   *
   * @param brand  The brand of this {@link NewSoda}
   */
  public void setBrand( @Nonnull final String brand) {
    this.brand = brand;
  }

  /**
   * Set the zero of this {@link NewSoda} instance and return the same instance.
   *
   * @param zero  The zero of this {@link NewSoda}
   * @return The same instance of this {@link NewSoda} class
   */
  @Nonnull public NewSoda zero( @Nullable final Boolean zero) {
    this.zero = zero;
    return this;
  }

  /**
   * Get zero
   * @return zero  The zero of this {@link NewSoda} instance.
   */
  @Nonnull
  public Boolean isZero() {
    return zero;
  }

  /**
   * Set the zero of this {@link NewSoda} instance.
   *
   * @param zero  The zero of this {@link NewSoda}
   */
  public void setZero( @Nullable final Boolean zero) {
    this.zero = zero;
  }

  /**
   * Set the since of this {@link NewSoda} instance and return the same instance.
   *
   * @param since  The since of this {@link NewSoda}
   * @return The same instance of this {@link NewSoda} class
   */
  @Nonnull public NewSoda since( @Nullable final LocalDate since) {
    this.since = since;
    return this;
  }

  /**
   * Get since
   * @return since  The since of this {@link NewSoda} instance.
   */
  @Nonnull
  public LocalDate getSince() {
    return since;
  }

  /**
   * Set the since of this {@link NewSoda} instance.
   *
   * @param since  The since of this {@link NewSoda}
   */
  public void setSince( @Nullable final LocalDate since) {
    this.since = since;
  }

  /**
   * Set the flavor of this {@link NewSoda} instance and return the same instance.
   *
   * @param flavor  The flavor of this {@link NewSoda}
   * @return The same instance of this {@link NewSoda} class
   */
  @Nonnull public NewSoda flavor( @Nonnull final String flavor) {
    this.flavor = flavor;
    return this;
  }

  /**
   * Get flavor
   * @return flavor  The flavor of this {@link NewSoda} instance.
   */
  @Nonnull
  public String getFlavor() {
    return flavor;
  }

  /**
   * Set the flavor of this {@link NewSoda} instance.
   *
   * @param flavor  The flavor of this {@link NewSoda}
   */
  public void setFlavor( @Nonnull final String flavor) {
    this.flavor = flavor;
  }

  /**
   * Set the price of this {@link NewSoda} instance and return the same instance.
   *
   * @param price  The price of this {@link NewSoda}
   * @return The same instance of this {@link NewSoda} class
   */
  @Nonnull public NewSoda price( @Nonnull final Float price) {
    this.price = price;
    return this;
  }

  /**
   * Get price
   * @return price  The price of this {@link NewSoda} instance.
   */
  @Nonnull
  public Float getPrice() {
    return price;
  }

  /**
   * Set the price of this {@link NewSoda} instance.
   *
   * @param price  The price of this {@link NewSoda}
   */
  public void setPrice( @Nonnull final Float price) {
    this.price = price;
  }

  /**
   * Get the names of the unrecognizable properties of the {@link NewSoda}.
   * @return The set of properties names
   */
  @JsonIgnore
  @Nonnull
  public Set<String> getCustomFieldNames() {
    return cloudSdkCustomFields.keySet();
  }

  /**
   * Get the value of an unrecognizable property of this {@link NewSoda} instance.
   * @deprecated Use {@link #getAllFields()} instead.
   * @param name  The name of the property
   * @return The value of the property
   * @throws NoSuchElementException  If no property with the given name could be found.
   */
  @Nullable
  @Deprecated
  public Object getCustomField( @Nonnull final String name ) throws NoSuchElementException {
    if( !cloudSdkCustomFields.containsKey(name) ) {
        throw new NoSuchElementException("NewSoda has no field with name '" + name + "'.");
    }
    return cloudSdkCustomFields.get(name);
  }

  /**
   * Get the value of all properties of this {@link NewSoda} instance including unrecognized properties.
   *
   * @return The map of all properties
   */
  @JsonIgnore
  @Nonnull
  public Map<String, Object> getAllFields()
  {
    final Map<String, Object> declaredFields = Arrays.stream(getClass().getDeclaredFields())
        .collect(LinkedHashMap::new, ( map, field ) -> {
          Object value = null;
          try {
            value = field.get(this);
          } catch (IllegalAccessException e) {
            // do nothing, value will not be added
          }
          final String name = field.getName();
          if (value != null && !name.equals("cloudSdkCustomFields")) {
            map.put(name, value);
          }
        }, Map::putAll);
    declaredFields.putAll(cloudSdkCustomFields);
    return declaredFields;
  }

  /**
   * Set an unrecognizable property of this {@link NewSoda} instance. If the map previously contained a mapping
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
    final NewSoda newSoda = (NewSoda) o;
    return Objects.equals(this.cloudSdkCustomFields, newSoda.cloudSdkCustomFields) &&
        Objects.equals(this.name, newSoda.name) &&
        Objects.equals(this.brand, newSoda.brand) &&
        Objects.equals(this.zero, newSoda.zero) &&
        Objects.equals(this.since, newSoda.since) &&
        Objects.equals(this.flavor, newSoda.flavor) &&
        Objects.equals(this.price, newSoda.price);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, brand, zero, since, flavor, price, cloudSdkCustomFields);
  }

  @Override
  @Nonnull public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("class NewSoda {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    brand: ").append(toIndentedString(brand)).append("\n");
    sb.append("    zero: ").append(toIndentedString(zero)).append("\n");
    sb.append("    since: ").append(toIndentedString(since)).append("\n");
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

