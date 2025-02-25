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
import com.sap.cloud.sdk.services.openai.model.ImageQuality;
import com.sap.cloud.sdk.services.openai.model.ImageSize;
import com.sap.cloud.sdk.services.openai.model.ImageStyle;
import com.sap.cloud.sdk.services.openai.model.ImagesResponseFormat;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * ImageGenerationsRequest
 */
// CHECKSTYLE:OFF
public class ImageGenerationsRequest 
// CHECKSTYLE:ON
{
  @JsonProperty("prompt")
  private String prompt;

  @JsonProperty("n")
  private Integer n = 1;

  @JsonProperty("size")
  private ImageSize size = ImageSize._1024X1024;

  @JsonProperty("response_format")
  private ImagesResponseFormat responseFormat = ImagesResponseFormat.URL;

  @JsonProperty("user")
  private String user;

  @JsonProperty("quality")
  private ImageQuality quality = ImageQuality.STANDARD;

  @JsonProperty("style")
  private ImageStyle style = ImageStyle.VIVID;

  @JsonAnySetter
  @JsonAnyGetter
  private final Map<String, Object> cloudSdkCustomFields = new LinkedHashMap<>();

  /**
   * Set the prompt of this {@link ImageGenerationsRequest} instance and return the same instance.
   *
   * @param prompt  A text description of the desired image(s). The maximum length is 4000 characters.
   * @return The same instance of this {@link ImageGenerationsRequest} class
   */
  @Nonnull public ImageGenerationsRequest prompt( @Nonnull final String prompt) {
    this.prompt = prompt;
    return this;
  }

  /**
   * A text description of the desired image(s). The maximum length is 4000 characters.
   * @return prompt  The prompt of this {@link ImageGenerationsRequest} instance.
   */
  @Nonnull
  public String getPrompt() {
    return prompt;
  }

  /**
   * Set the prompt of this {@link ImageGenerationsRequest} instance.
   *
   * @param prompt  A text description of the desired image(s). The maximum length is 4000 characters.
   */
  public void setPrompt( @Nonnull final String prompt) {
    this.prompt = prompt;
  }

  /**
   * Set the n of this {@link ImageGenerationsRequest} instance and return the same instance.
   *
   * @param n  The number of images to generate.
   * Minimum: 1
   * Maximum: 1
   * @return The same instance of this {@link ImageGenerationsRequest} class
   */
  @Nonnull public ImageGenerationsRequest n( @Nullable final Integer n) {
    this.n = n;
    return this;
  }

  /**
   * The number of images to generate.
   * minimum: 1
   * maximum: 1
   * @return n  The n of this {@link ImageGenerationsRequest} instance.
   */
  @Nonnull
  public Integer getN() {
    return n;
  }

  /**
   * Set the n of this {@link ImageGenerationsRequest} instance.
   *
   * @param n  The number of images to generate.
   * Minimum: 1
   * Maximum: 1
   */
  public void setN( @Nullable final Integer n) {
    this.n = n;
  }

  /**
   * Set the size of this {@link ImageGenerationsRequest} instance and return the same instance.
   *
   * @param size  The size of this {@link ImageGenerationsRequest}
   * @return The same instance of this {@link ImageGenerationsRequest} class
   */
  @Nonnull public ImageGenerationsRequest size( @Nullable final ImageSize size) {
    this.size = size;
    return this;
  }

  /**
   * Get size
   * @return size  The size of this {@link ImageGenerationsRequest} instance.
   */
  @Nonnull
  public ImageSize getSize() {
    return size;
  }

  /**
   * Set the size of this {@link ImageGenerationsRequest} instance.
   *
   * @param size  The size of this {@link ImageGenerationsRequest}
   */
  public void setSize( @Nullable final ImageSize size) {
    this.size = size;
  }

  /**
   * Set the responseFormat of this {@link ImageGenerationsRequest} instance and return the same instance.
   *
   * @param responseFormat  The responseFormat of this {@link ImageGenerationsRequest}
   * @return The same instance of this {@link ImageGenerationsRequest} class
   */
  @Nonnull public ImageGenerationsRequest responseFormat( @Nullable final ImagesResponseFormat responseFormat) {
    this.responseFormat = responseFormat;
    return this;
  }

  /**
   * Get responseFormat
   * @return responseFormat  The responseFormat of this {@link ImageGenerationsRequest} instance.
   */
  @Nonnull
  public ImagesResponseFormat getResponseFormat() {
    return responseFormat;
  }

  /**
   * Set the responseFormat of this {@link ImageGenerationsRequest} instance.
   *
   * @param responseFormat  The responseFormat of this {@link ImageGenerationsRequest}
   */
  public void setResponseFormat( @Nullable final ImagesResponseFormat responseFormat) {
    this.responseFormat = responseFormat;
  }

  /**
   * Set the user of this {@link ImageGenerationsRequest} instance and return the same instance.
   *
   * @param user  A unique identifier representing your end-user, which can help to monitor and detect abuse.
   * @return The same instance of this {@link ImageGenerationsRequest} class
   */
  @Nonnull public ImageGenerationsRequest user( @Nullable final String user) {
    this.user = user;
    return this;
  }

  /**
   * A unique identifier representing your end-user, which can help to monitor and detect abuse.
   * @return user  The user of this {@link ImageGenerationsRequest} instance.
   */
  @Nonnull
  public String getUser() {
    return user;
  }

  /**
   * Set the user of this {@link ImageGenerationsRequest} instance.
   *
   * @param user  A unique identifier representing your end-user, which can help to monitor and detect abuse.
   */
  public void setUser( @Nullable final String user) {
    this.user = user;
  }

  /**
   * Set the quality of this {@link ImageGenerationsRequest} instance and return the same instance.
   *
   * @param quality  The quality of this {@link ImageGenerationsRequest}
   * @return The same instance of this {@link ImageGenerationsRequest} class
   */
  @Nonnull public ImageGenerationsRequest quality( @Nullable final ImageQuality quality) {
    this.quality = quality;
    return this;
  }

  /**
   * Get quality
   * @return quality  The quality of this {@link ImageGenerationsRequest} instance.
   */
  @Nonnull
  public ImageQuality getQuality() {
    return quality;
  }

  /**
   * Set the quality of this {@link ImageGenerationsRequest} instance.
   *
   * @param quality  The quality of this {@link ImageGenerationsRequest}
   */
  public void setQuality( @Nullable final ImageQuality quality) {
    this.quality = quality;
  }

  /**
   * Set the style of this {@link ImageGenerationsRequest} instance and return the same instance.
   *
   * @param style  The style of this {@link ImageGenerationsRequest}
   * @return The same instance of this {@link ImageGenerationsRequest} class
   */
  @Nonnull public ImageGenerationsRequest style( @Nullable final ImageStyle style) {
    this.style = style;
    return this;
  }

  /**
   * Get style
   * @return style  The style of this {@link ImageGenerationsRequest} instance.
   */
  @Nonnull
  public ImageStyle getStyle() {
    return style;
  }

  /**
   * Set the style of this {@link ImageGenerationsRequest} instance.
   *
   * @param style  The style of this {@link ImageGenerationsRequest}
   */
  public void setStyle( @Nullable final ImageStyle style) {
    this.style = style;
  }

  /**
   * Get the names of the unrecognizable properties of the {@link ImageGenerationsRequest}.
   * @return The set of properties names
   */
  @JsonIgnore
  @Nonnull
  public Set<String> getCustomFieldNames() {
    return cloudSdkCustomFields.keySet();
  }

  /**
   * Get the value of an unrecognizable property of this {@link ImageGenerationsRequest} instance.
   * @deprecated Use {@link #toMap()} instead.
   * @param name  The name of the property
   * @return The value of the property
   * @throws NoSuchElementException  If no property with the given name could be found.
   */
  @Nullable
  @Deprecated
  public Object getCustomField( @Nonnull final String name ) throws NoSuchElementException {
    if( !cloudSdkCustomFields.containsKey(name) ) {
        throw new NoSuchElementException("ImageGenerationsRequest has no field with name '" + name + "'.");
    }
    return cloudSdkCustomFields.get(name);
  }

  /**
   * Get the value of all properties of this {@link ImageGenerationsRequest} instance including unrecognized properties.
   *
   * @return The map of all properties
   */
  @JsonIgnore
  @Nonnull
  public Map<String, Object> toMap()
  {
    final Map<String, Object> declaredFields = new LinkedHashMap<>(cloudSdkCustomFields);
    if( prompt != null ) declaredFields.put("prompt", prompt);
    if( n != null ) declaredFields.put("n", n);
    if( size != null ) declaredFields.put("size", size);
    if( responseFormat != null ) declaredFields.put("responseFormat", responseFormat);
    if( user != null ) declaredFields.put("user", user);
    if( quality != null ) declaredFields.put("quality", quality);
    if( style != null ) declaredFields.put("style", style);
    return declaredFields;
  }

  /**
   * Set an unrecognizable property of this {@link ImageGenerationsRequest} instance. If the map previously contained a mapping
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
    final ImageGenerationsRequest imageGenerationsRequest = (ImageGenerationsRequest) o;
    return Objects.equals(this.cloudSdkCustomFields, imageGenerationsRequest.cloudSdkCustomFields) &&
        Objects.equals(this.prompt, imageGenerationsRequest.prompt) &&
        Objects.equals(this.n, imageGenerationsRequest.n) &&
        Objects.equals(this.size, imageGenerationsRequest.size) &&
        Objects.equals(this.responseFormat, imageGenerationsRequest.responseFormat) &&
        Objects.equals(this.user, imageGenerationsRequest.user) &&
        Objects.equals(this.quality, imageGenerationsRequest.quality) &&
        Objects.equals(this.style, imageGenerationsRequest.style);
  }

  @Override
  public int hashCode() {
    return Objects.hash(prompt, n, size, responseFormat, user, quality, style, cloudSdkCustomFields);
  }

  @Override
  @Nonnull public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("class ImageGenerationsRequest {\n");
    sb.append("    prompt: ").append(toIndentedString(prompt)).append("\n");
    sb.append("    n: ").append(toIndentedString(n)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    responseFormat: ").append(toIndentedString(responseFormat)).append("\n");
    sb.append("    user: ").append(toIndentedString(user)).append("\n");
    sb.append("    quality: ").append(toIndentedString(quality)).append("\n");
    sb.append("    style: ").append(toIndentedString(style)).append("\n");
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

