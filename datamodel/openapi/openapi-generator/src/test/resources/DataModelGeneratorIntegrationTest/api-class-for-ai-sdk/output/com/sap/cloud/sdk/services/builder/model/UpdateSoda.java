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
 * UpdateSoda
 */
// CHECKSTYLE:OFF
public class UpdateSoda 
// CHECKSTYLE:ON
{
  @JsonProperty("name")
  private String name;

  @JsonProperty("zero")
  private Boolean zero;

  @JsonProperty("since")
  private LocalDate since;

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
   * Set the name of this {@link UpdateSoda} instance and return the same instance.
   *
   * @param name  The name of this {@link UpdateSoda}
   * @return The same instance of this {@link UpdateSoda} class
   */
  @Nonnull public UpdateSoda name( @Nullable final String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name  The name of this {@link UpdateSoda} instance.
   */
  @Nonnull
  public String getName() {
    return name;
  }

  /**
   * Set the name of this {@link UpdateSoda} instance.
   *
   * @param name  The name of this {@link UpdateSoda}
   */
  public void setName( @Nullable final String name) {
    this.name = name;
  }

  /**
   * Set the zero of this {@link UpdateSoda} instance and return the same instance.
   *
   * @param zero  The zero of this {@link UpdateSoda}
   * @return The same instance of this {@link UpdateSoda} class
   */
  @Nonnull public UpdateSoda zero( @Nullable final Boolean zero) {
    this.zero = zero;
    return this;
  }

  /**
   * Get zero
   * @return zero  The zero of this {@link UpdateSoda} instance.
   */
  @Nonnull
  public Boolean isZero() {
    return zero;
  }

  /**
   * Set the zero of this {@link UpdateSoda} instance.
   *
   * @param zero  The zero of this {@link UpdateSoda}
   */
  public void setZero( @Nullable final Boolean zero) {
    this.zero = zero;
  }

  /**
   * Set the since of this {@link UpdateSoda} instance and return the same instance.
   *
   * @param since  The since of this {@link UpdateSoda}
   * @return The same instance of this {@link UpdateSoda} class
   */
  @Nonnull public UpdateSoda since( @Nullable final LocalDate since) {
    this.since = since;
    return this;
  }

  /**
   * Get since
   * @return since  The since of this {@link UpdateSoda} instance.
   */
  @Nonnull
  public LocalDate getSince() {
    return since;
  }

  /**
   * Set the since of this {@link UpdateSoda} instance.
   *
   * @param since  The since of this {@link UpdateSoda}
   */
  public void setSince( @Nullable final LocalDate since) {
    this.since = since;
  }

  /**
   * Set the brand of this {@link UpdateSoda} instance and return the same instance.
   *
   * @param brand  The brand of this {@link UpdateSoda}
   * @return The same instance of this {@link UpdateSoda} class
   */
  @Nonnull public UpdateSoda brand( @Nullable final String brand) {
    this.brand = brand;
    return this;
  }

  /**
   * Get brand
   * @return brand  The brand of this {@link UpdateSoda} instance.
   */
  @Nonnull
  public String getBrand() {
    return brand;
  }

  /**
   * Set the brand of this {@link UpdateSoda} instance.
   *
   * @param brand  The brand of this {@link UpdateSoda}
   */
  public void setBrand( @Nullable final String brand) {
    this.brand = brand;
  }

  /**
   * Set the flavor of this {@link UpdateSoda} instance and return the same instance.
   *
   * @param flavor  The flavor of this {@link UpdateSoda}
   * @return The same instance of this {@link UpdateSoda} class
   */
  @Nonnull public UpdateSoda flavor( @Nullable final String flavor) {
    this.flavor = flavor;
    return this;
  }

  /**
   * Get flavor
   * @return flavor  The flavor of this {@link UpdateSoda} instance.
   */
  @Nonnull
  public String getFlavor() {
    return flavor;
  }

  /**
   * Set the flavor of this {@link UpdateSoda} instance.
   *
   * @param flavor  The flavor of this {@link UpdateSoda}
   */
  public void setFlavor( @Nullable final String flavor) {
    this.flavor = flavor;
  }

  /**
   * Set the price of this {@link UpdateSoda} instance and return the same instance.
   *
   * @param price  The price of this {@link UpdateSoda}
   * @return The same instance of this {@link UpdateSoda} class
   */
  @Nonnull public UpdateSoda price( @Nullable final Float price) {
    this.price = price;
    return this;
  }

  /**
   * Get price
   * @return price  The price of this {@link UpdateSoda} instance.
   */
  @Nonnull
  public Float getPrice() {
    return price;
  }

  /**
   * Set the price of this {@link UpdateSoda} instance.
   *
   * @param price  The price of this {@link UpdateSoda}
   */
  public void setPrice( @Nullable final Float price) {
    this.price = price;
  }

  /**
   * Get the names of the unrecognizable properties of the {@link UpdateSoda}.
   * @return The set of properties names
   */
  @JsonIgnore
  @Nonnull
  public Set<String> getCustomFieldNames() {
    return cloudSdkCustomFields.keySet();
  }

  /**
   * Get the value of an unrecognizable property of this {@link UpdateSoda} instance.
   * @deprecated Use {@link #toMap()} instead.
   * @param name  The name of the property
   * @return The value of the property
   * @throws NoSuchElementException  If no property with the given name could be found.
   */
  @Nullable
  @Deprecated
  public Object getCustomField( @Nonnull final String name ) throws NoSuchElementException {
    if( !cloudSdkCustomFields.containsKey(name) ) {
        throw new NoSuchElementException("UpdateSoda has no field with name '" + name + "'.");
    }
    return cloudSdkCustomFields.get(name);
  }

  /**
   * Get the value of all properties of this {@link UpdateSoda} instance including unrecognized properties.
   *
   * @return The map of all properties
   */
  @JsonIgnore
  @Nonnull
  public Map<String, Object> toMap()
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
   * Set an unrecognizable property of this {@link UpdateSoda} instance. If the map previously contained a mapping
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
    final UpdateSoda updateSoda = (UpdateSoda) o;
    return Objects.equals(this.cloudSdkCustomFields, updateSoda.cloudSdkCustomFields) &&
        Objects.equals(this.name, updateSoda.name) &&
        Objects.equals(this.zero, updateSoda.zero) &&
        Objects.equals(this.since, updateSoda.since) &&
        Objects.equals(this.brand, updateSoda.brand) &&
        Objects.equals(this.flavor, updateSoda.flavor) &&
        Objects.equals(this.price, updateSoda.price);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, zero, since, brand, flavor, price, cloudSdkCustomFields);
  }

  @Override
  @Nonnull public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("class UpdateSoda {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    zero: ").append(toIndentedString(zero)).append("\n");
    sb.append("    since: ").append(toIndentedString(since)).append("\n");
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

