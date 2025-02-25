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
import com.sap.cloud.sdk.services.openai.model.DalleFilterResults;
import com.sap.cloud.sdk.services.openai.model.InnerErrorCode;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Inner error with additional details.
 */
// CHECKSTYLE:OFF
public class DalleInnerError 
// CHECKSTYLE:ON
{
  @JsonProperty("code")
  private InnerErrorCode code;

  @JsonProperty("content_filter_results")
  private DalleFilterResults contentFilterResults;

  @JsonProperty("revised_prompt")
  private String revisedPrompt;

  @JsonAnySetter
  @JsonAnyGetter
  private final Map<String, Object> cloudSdkCustomFields = new LinkedHashMap<>();

  /**
   * Set the code of this {@link DalleInnerError} instance and return the same instance.
   *
   * @param code  The code of this {@link DalleInnerError}
   * @return The same instance of this {@link DalleInnerError} class
   */
  @Nonnull public DalleInnerError code( @Nullable final InnerErrorCode code) {
    this.code = code;
    return this;
  }

  /**
   * Get code
   * @return code  The code of this {@link DalleInnerError} instance.
   */
  @Nonnull
  public InnerErrorCode getCode() {
    return code;
  }

  /**
   * Set the code of this {@link DalleInnerError} instance.
   *
   * @param code  The code of this {@link DalleInnerError}
   */
  public void setCode( @Nullable final InnerErrorCode code) {
    this.code = code;
  }

  /**
   * Set the contentFilterResults of this {@link DalleInnerError} instance and return the same instance.
   *
   * @param contentFilterResults  The contentFilterResults of this {@link DalleInnerError}
   * @return The same instance of this {@link DalleInnerError} class
   */
  @Nonnull public DalleInnerError contentFilterResults( @Nullable final DalleFilterResults contentFilterResults) {
    this.contentFilterResults = contentFilterResults;
    return this;
  }

  /**
   * Get contentFilterResults
   * @return contentFilterResults  The contentFilterResults of this {@link DalleInnerError} instance.
   */
  @Nonnull
  public DalleFilterResults getContentFilterResults() {
    return contentFilterResults;
  }

  /**
   * Set the contentFilterResults of this {@link DalleInnerError} instance.
   *
   * @param contentFilterResults  The contentFilterResults of this {@link DalleInnerError}
   */
  public void setContentFilterResults( @Nullable final DalleFilterResults contentFilterResults) {
    this.contentFilterResults = contentFilterResults;
  }

  /**
   * Set the revisedPrompt of this {@link DalleInnerError} instance and return the same instance.
   *
   * @param revisedPrompt  The prompt that was used to generate the image, if there was any revision to the prompt.
   * @return The same instance of this {@link DalleInnerError} class
   */
  @Nonnull public DalleInnerError revisedPrompt( @Nullable final String revisedPrompt) {
    this.revisedPrompt = revisedPrompt;
    return this;
  }

  /**
   * The prompt that was used to generate the image, if there was any revision to the prompt.
   * @return revisedPrompt  The revisedPrompt of this {@link DalleInnerError} instance.
   */
  @Nonnull
  public String getRevisedPrompt() {
    return revisedPrompt;
  }

  /**
   * Set the revisedPrompt of this {@link DalleInnerError} instance.
   *
   * @param revisedPrompt  The prompt that was used to generate the image, if there was any revision to the prompt.
   */
  public void setRevisedPrompt( @Nullable final String revisedPrompt) {
    this.revisedPrompt = revisedPrompt;
  }

  /**
   * Get the names of the unrecognizable properties of the {@link DalleInnerError}.
   * @return The set of properties names
   */
  @JsonIgnore
  @Nonnull
  public Set<String> getCustomFieldNames() {
    return cloudSdkCustomFields.keySet();
  }

  /**
   * Get the value of an unrecognizable property of this {@link DalleInnerError} instance.
   * @deprecated Use {@link #toMap()} instead.
   * @param name  The name of the property
   * @return The value of the property
   * @throws NoSuchElementException  If no property with the given name could be found.
   */
  @Nullable
  @Deprecated
  public Object getCustomField( @Nonnull final String name ) throws NoSuchElementException {
    if( !cloudSdkCustomFields.containsKey(name) ) {
        throw new NoSuchElementException("DalleInnerError has no field with name '" + name + "'.");
    }
    return cloudSdkCustomFields.get(name);
  }

  /**
   * Get the value of all properties of this {@link DalleInnerError} instance including unrecognized properties.
   *
   * @return The map of all properties
   */
  @JsonIgnore
  @Nonnull
  public Map<String, Object> toMap()
  {
    final Map<String, Object> declaredFields = new LinkedHashMap<>(cloudSdkCustomFields);
    if( code != null ) declaredFields.put("code", code);
    if( contentFilterResults != null ) declaredFields.put("contentFilterResults", contentFilterResults);
    if( revisedPrompt != null ) declaredFields.put("revisedPrompt", revisedPrompt);
    return declaredFields;
  }

  /**
   * Set an unrecognizable property of this {@link DalleInnerError} instance. If the map previously contained a mapping
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
    final DalleInnerError dalleInnerError = (DalleInnerError) o;
    return Objects.equals(this.cloudSdkCustomFields, dalleInnerError.cloudSdkCustomFields) &&
        Objects.equals(this.code, dalleInnerError.code) &&
        Objects.equals(this.contentFilterResults, dalleInnerError.contentFilterResults) &&
        Objects.equals(this.revisedPrompt, dalleInnerError.revisedPrompt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, contentFilterResults, revisedPrompt, cloudSdkCustomFields);
  }

  @Override
  @Nonnull public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("class DalleInnerError {\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    contentFilterResults: ").append(toIndentedString(contentFilterResults)).append("\n");
    sb.append("    revisedPrompt: ").append(toIndentedString(revisedPrompt)).append("\n");
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

