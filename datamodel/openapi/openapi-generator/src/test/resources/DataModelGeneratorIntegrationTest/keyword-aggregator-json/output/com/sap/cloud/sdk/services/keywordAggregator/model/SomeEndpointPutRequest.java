/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

/*
 * Sample Cloud SDK Test API
 * This is a sample API to test the Cloud SDK's OpenAPI generator.
 *
 * The version of the OpenAPI document: 0.0.1
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package com.sap.cloud.sdk.services.keywordAggregator.model;

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
import com.sap.cloud.sdk.services.keywordAggregator.model.Object1;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * SomeEndpointPutRequest
 */

// CHECKSTYLE:OFF
public class SomeEndpointPutRequest 
// CHECKSTYLE:ON
{
  @JsonProperty("Name")
  private String name;

  @JsonProperty("Age")
  private Integer age;

  @JsonAnySetter
  @JsonAnyGetter
  private final Map<String, Object> cloudSdkCustomFields = new LinkedHashMap<>();

   /**
   * Set the name of this {@link SomeEndpointPutRequest} instance and return the same instance.
   *
   * @param name  The name of this {@link SomeEndpointPutRequest}
   * @return The same instance of this {@link SomeEndpointPutRequest} class
   */
   @Nonnull public SomeEndpointPutRequest name(@Nonnull final String name) {
    this.name = name;
    return this;
  }

   /**
   * Get name
   * @return name  The name of this {@link SomeEndpointPutRequest} instance.
  **/
  @Nonnull public String getName() {
    return name;
  }

  /**
  * Set the name of this {@link SomeEndpointPutRequest} instance.
  *
  * @param name  The name of this {@link SomeEndpointPutRequest}
  */
  public void setName( @Nonnull final String name) {
    this.name = name;
  }

   /**
   * Set the age of this {@link SomeEndpointPutRequest} instance and return the same instance.
   *
   * @param age  The age of this {@link SomeEndpointPutRequest}
   * @return The same instance of this {@link SomeEndpointPutRequest} class
   */
   @Nonnull public SomeEndpointPutRequest age(@Nonnull final Integer age) {
    this.age = age;
    return this;
  }

   /**
   * Get age
   * @return age  The age of this {@link SomeEndpointPutRequest} instance.
  **/
  @Nonnull public Integer getAge() {
    return age;
  }

  /**
  * Set the age of this {@link SomeEndpointPutRequest} instance.
  *
  * @param age  The age of this {@link SomeEndpointPutRequest}
  */
  public void setAge( @Nonnull final Integer age) {
    this.age = age;
  }

  /**
   * Get the names of the unrecognizable properties of the {@link SomeEndpointPutRequest}.
   * @return The set of properties names
   */
  @JsonIgnore
  @Nonnull
  public Set<String> getCustomFieldNames() {
    return cloudSdkCustomFields.keySet();
  }

  /**
   * Get the value of an unrecognizable property of this {@link SomeEndpointPutRequest} instance.
   * @param name  The name of the property
   * @return The value of the property
   * @throws NoSuchElementException  If no property with the given name could be found.
   */
  @Nullable
  public Object getCustomField(@Nonnull final String name) throws NoSuchElementException {
    if( !cloudSdkCustomFields.containsKey(name) ) {
        throw new NoSuchElementException("SomeEndpointPutRequest has no field with name '" + name + "'.");
    }
    return cloudSdkCustomFields.get(name);
  }

  /**
   * Set an unrecognizable property of this {@link SomeEndpointPutRequest} instance. If the map previously contained a mapping
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
    final SomeEndpointPutRequest someEndpointPutRequest = (SomeEndpointPutRequest) o;
    return Objects.equals(this.cloudSdkCustomFields, someEndpointPutRequest.cloudSdkCustomFields) &&
        Objects.equals(this.name, someEndpointPutRequest.name) &&
        Objects.equals(this.age, someEndpointPutRequest.age);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, age, cloudSdkCustomFields);
  }

  @Override
  @Nonnull public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("class SomeEndpointPutRequest {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    age: ").append(toIndentedString(age)).append("\n");
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

