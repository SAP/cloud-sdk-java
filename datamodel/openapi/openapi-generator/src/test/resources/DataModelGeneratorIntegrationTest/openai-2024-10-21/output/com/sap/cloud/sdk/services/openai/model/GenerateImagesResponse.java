/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

/*
 * Azure OpenAI Service API
 * Azure OpenAI APIs for completions and search
 *
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package com.sap.cloud.sdk.services.openai.model;

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
import com.sap.cloud.sdk.services.openai.model.ImageResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * GenerateImagesResponse
 */
// CHECKSTYLE:OFF
public class GenerateImagesResponse 
// CHECKSTYLE:ON
{
  @JsonProperty("created")
  private Integer created;

  @JsonProperty("data")
  private List<ImageResult> data = new ArrayList<>();

  @JsonAnySetter
  @JsonAnyGetter
  private final Map<String, Object> cloudSdkCustomFields = new LinkedHashMap<>();

  /**
   * Set the created of this {@link GenerateImagesResponse} instance and return the same instance.
   *
   * @param created  The unix timestamp when the operation was created.
   * @return The same instance of this {@link GenerateImagesResponse} class
   */
  @Nonnull public GenerateImagesResponse created( @Nonnull final Integer created) {
    this.created = created;
    return this;
  }

  /**
   * The unix timestamp when the operation was created.
   * @return created  The created of this {@link GenerateImagesResponse} instance.
   */
  @Nonnull
  public Integer getCreated() {
    return created;
  }

  /**
   * Set the created of this {@link GenerateImagesResponse} instance.
   *
   * @param created  The unix timestamp when the operation was created.
   */
  public void setCreated( @Nonnull final Integer created) {
    this.created = created;
  }

  /**
   * Set the data of this {@link GenerateImagesResponse} instance and return the same instance.
   *
   * @param data  The result data of the operation, if successful
   * @return The same instance of this {@link GenerateImagesResponse} class
   */
  @Nonnull public GenerateImagesResponse data( @Nonnull final List<ImageResult> data) {
    this.data = data;
    return this;
  }
  /**
   * Add one data instance to this {@link GenerateImagesResponse}.
   * @param dataItem The data that should be added
   * @return The same instance of type {@link GenerateImagesResponse}
   */
  @Nonnull public GenerateImagesResponse addDataItem( @Nonnull final ImageResult dataItem) {
    if (this.data == null) {
      this.data = new ArrayList<>();
    }
    this.data.add(dataItem);
    return this;
  }

  /**
   * The result data of the operation, if successful
   * @return data  The data of this {@link GenerateImagesResponse} instance.
   */
  @Nonnull
  public List<ImageResult> getData() {
    return data;
  }

  /**
   * Set the data of this {@link GenerateImagesResponse} instance.
   *
   * @param data  The result data of the operation, if successful
   */
  public void setData( @Nonnull final List<ImageResult> data) {
    this.data = data;
  }

  /**
   * Get the names of the unrecognizable properties of the {@link GenerateImagesResponse}.
   * @return The set of properties names
   */
  @JsonIgnore
  @Nonnull
  public Set<String> getCustomFieldNames() {
    return cloudSdkCustomFields.keySet();
  }

  /**
   * Get the value of an unrecognizable property of this {@link GenerateImagesResponse} instance.
   * @deprecated Use {@link #toMap()} instead.
   * @param name  The name of the property
   * @return The value of the property
   * @throws NoSuchElementException  If no property with the given name could be found.
   */
  @Nullable
  @Deprecated
  public Object getCustomField( @Nonnull final String name ) throws NoSuchElementException {
    if( !cloudSdkCustomFields.containsKey(name) ) {
        throw new NoSuchElementException("GenerateImagesResponse has no field with name '" + name + "'.");
    }
    return cloudSdkCustomFields.get(name);
  }

  /**
   * Get the value of all properties of this {@link GenerateImagesResponse} instance including unrecognized properties.
   *
   * @return The map of all properties
   */
  @JsonIgnore
  @Nonnull
  public Map<String, Object> toMap()
  {
    final Map<String, Object> declaredFields = new LinkedHashMap<>(cloudSdkCustomFields);
    if( created != null ) declaredFields.put("created", created);
    if( data != null ) declaredFields.put("data", data);
    return declaredFields;
  }

  /**
   * Set an unrecognizable property of this {@link GenerateImagesResponse} instance. If the map previously contained a mapping
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
    final GenerateImagesResponse generateImagesResponse = (GenerateImagesResponse) o;
    return Objects.equals(this.cloudSdkCustomFields, generateImagesResponse.cloudSdkCustomFields) &&
        Objects.equals(this.created, generateImagesResponse.created) &&
        Objects.equals(this.data, generateImagesResponse.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(created, data, cloudSdkCustomFields);
  }

  @Override
  @Nonnull public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("class GenerateImagesResponse {\n");
    sb.append("    created: ").append(toIndentedString(created)).append("\n");
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
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

