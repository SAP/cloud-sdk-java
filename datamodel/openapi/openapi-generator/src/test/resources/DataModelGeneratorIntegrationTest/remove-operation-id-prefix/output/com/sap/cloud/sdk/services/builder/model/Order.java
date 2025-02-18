/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

/*
 * SodaStore API
 * API for managing soda products and orders in SodaStore.
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
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Order
 */
// CHECKSTYLE:OFF
public class Order 
// CHECKSTYLE:ON
{
  @JsonProperty("productId")
  private Long productId;

  @JsonProperty("quantity")
  private Integer quantity;

  @JsonProperty("totalPrice")
  private Float totalPrice;

  @JsonProperty("typelessProperty")
  private Object typelessProperty = null;

  @JsonProperty("nullableProperty")
  private String nullableProperty;

  @JsonAnySetter
  @JsonAnyGetter
  private final Map<String, Object> cloudSdkCustomFields = new LinkedHashMap<>();

  /**
   * Set the productId of this {@link Order} instance and return the same instance.
   *
   * @param productId  The productId of this {@link Order}
   * @return The same instance of this {@link Order} class
   */
  @Nonnull public Order productId( @Nonnull final Long productId) {
    this.productId = productId;
    return this;
  }

  /**
   * Get productId
   * @return productId  The productId of this {@link Order} instance.
   */
  @Nonnull
  public Long getProductId() {
    return productId;
  }

  /**
   * Set the productId of this {@link Order} instance.
   *
   * @param productId  The productId of this {@link Order}
   */
  public void setProductId( @Nonnull final Long productId) {
    this.productId = productId;
  }

  /**
   * Set the quantity of this {@link Order} instance and return the same instance.
   *
   * @param quantity  The quantity of this {@link Order}
   * @return The same instance of this {@link Order} class
   */
  @Nonnull public Order quantity( @Nonnull final Integer quantity) {
    this.quantity = quantity;
    return this;
  }

  /**
   * Get quantity
   * @return quantity  The quantity of this {@link Order} instance.
   */
  @Nonnull
  public Integer getQuantity() {
    return quantity;
  }

  /**
   * Set the quantity of this {@link Order} instance.
   *
   * @param quantity  The quantity of this {@link Order}
   */
  public void setQuantity( @Nonnull final Integer quantity) {
    this.quantity = quantity;
  }

  /**
   * Set the totalPrice of this {@link Order} instance and return the same instance.
   *
   * @param totalPrice  The totalPrice of this {@link Order}
   * @return The same instance of this {@link Order} class
   */
  @Nonnull public Order totalPrice( @Nullable final Float totalPrice) {
    this.totalPrice = totalPrice;
    return this;
  }

  /**
   * Get totalPrice
   * @return totalPrice  The totalPrice of this {@link Order} instance.
   */
  @Nonnull
  public Float getTotalPrice() {
    return totalPrice;
  }

  /**
   * Set the totalPrice of this {@link Order} instance.
   *
   * @param totalPrice  The totalPrice of this {@link Order}
   */
  public void setTotalPrice( @Nullable final Float totalPrice) {
    this.totalPrice = totalPrice;
  }

  /**
   * Set the typelessProperty of this {@link Order} instance and return the same instance.
   *
   * @param typelessProperty  Some typeless property, interpreted by the generator as nullable by default (because typeless)
   * @return The same instance of this {@link Order} class
   */
  @Nonnull public Order typelessProperty( @Nullable final Object typelessProperty) {
    this.typelessProperty = typelessProperty;
    return this;
  }

  /**
   * Some typeless property, interpreted by the generator as nullable by default (because typeless)
   * @return typelessProperty  The typelessProperty of this {@link Order} instance.
   */
  @Nullable
  public Object getTypelessProperty() {
    return typelessProperty;
  }

  /**
   * Set the typelessProperty of this {@link Order} instance.
   *
   * @param typelessProperty  Some typeless property, interpreted by the generator as nullable by default (because typeless)
   */
  public void setTypelessProperty( @Nullable final Object typelessProperty) {
    this.typelessProperty = typelessProperty;
  }

  /**
   * Set the nullableProperty of this {@link Order} instance and return the same instance.
   *
   * @param nullableProperty  Some typed property that is deliberately made nullable
   * @return The same instance of this {@link Order} class
   */
  @Nonnull public Order nullableProperty( @Nullable final String nullableProperty) {
    this.nullableProperty = nullableProperty;
    return this;
  }

  /**
   * Some typed property that is deliberately made nullable
   * @return nullableProperty  The nullableProperty of this {@link Order} instance.
   */
  @Nullable
  public String getNullableProperty() {
    return nullableProperty;
  }

  /**
   * Set the nullableProperty of this {@link Order} instance.
   *
   * @param nullableProperty  Some typed property that is deliberately made nullable
   */
  public void setNullableProperty( @Nullable final String nullableProperty) {
    this.nullableProperty = nullableProperty;
  }

  /**
   * Get the names of the unrecognizable properties of the {@link Order}.
   * @return The set of properties names
   */
  @JsonIgnore
  @Nonnull
  public Set<String> getCustomFieldNames() {
    return cloudSdkCustomFields.keySet();
  }

  /**
   * Get the value of an unrecognizable property of this {@link Order} instance.
   * @deprecated Use {@link #toMap()} instead.
   * @param name  The name of the property
   * @return The value of the property
   * @throws NoSuchElementException  If no property with the given name could be found.
   */
  @Nullable
  @Deprecated
  public Object getCustomField( @Nonnull final String name ) throws NoSuchElementException {
    if( !cloudSdkCustomFields.containsKey(name) ) {
        throw new NoSuchElementException("Order has no field with name '" + name + "'.");
    }
    return cloudSdkCustomFields.get(name);
  }

  /**
   * Get the value of all properties of this {@link Order} instance including unrecognized properties.
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
   * Set an unrecognizable property of this {@link Order} instance. If the map previously contained a mapping
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
    final Order order = (Order) o;
    return Objects.equals(this.cloudSdkCustomFields, order.cloudSdkCustomFields) &&
        Objects.equals(this.productId, order.productId) &&
        Objects.equals(this.quantity, order.quantity) &&
        Objects.equals(this.totalPrice, order.totalPrice) &&
        Objects.equals(this.typelessProperty, order.typelessProperty) &&
        Objects.equals(this.nullableProperty, order.nullableProperty);
  }

  @Override
  public int hashCode() {
    return Objects.hash(productId, quantity, totalPrice, typelessProperty, nullableProperty, cloudSdkCustomFields);
  }

  @Override
  @Nonnull public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("class Order {\n");
    sb.append("    productId: ").append(toIndentedString(productId)).append("\n");
    sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
    sb.append("    totalPrice: ").append(toIndentedString(totalPrice)).append("\n");
    sb.append("    typelessProperty: ").append(toIndentedString(typelessProperty)).append("\n");
    sb.append("    nullableProperty: ").append(toIndentedString(nullableProperty)).append("\n");
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

