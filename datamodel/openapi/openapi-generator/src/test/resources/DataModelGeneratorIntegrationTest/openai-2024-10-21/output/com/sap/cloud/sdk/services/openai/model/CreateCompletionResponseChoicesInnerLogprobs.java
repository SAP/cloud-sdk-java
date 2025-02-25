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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * CreateCompletionResponseChoicesInnerLogprobs
 */
// CHECKSTYLE:OFF
public class CreateCompletionResponseChoicesInnerLogprobs 
// CHECKSTYLE:ON
{
  @JsonProperty("text_offset")
  private List<Integer> textOffset = new ArrayList<>();

  @JsonProperty("token_logprobs")
  private float[] tokenLogprobs;

  @JsonProperty("tokens")
  private List<String> tokens = new ArrayList<>();

  @JsonProperty("top_logprobs")
  private List<Map<String, BigDecimal>> topLogprobs = new ArrayList<>();

  @JsonAnySetter
  @JsonAnyGetter
  private final Map<String, Object> cloudSdkCustomFields = new LinkedHashMap<>();

  /**
   * Set the textOffset of this {@link CreateCompletionResponseChoicesInnerLogprobs} instance and return the same instance.
   *
   * @param textOffset  The textOffset of this {@link CreateCompletionResponseChoicesInnerLogprobs}
   * @return The same instance of this {@link CreateCompletionResponseChoicesInnerLogprobs} class
   */
  @Nonnull public CreateCompletionResponseChoicesInnerLogprobs textOffset( @Nullable final List<Integer> textOffset) {
    this.textOffset = textOffset;
    return this;
  }
  /**
   * Add one textOffset instance to this {@link CreateCompletionResponseChoicesInnerLogprobs}.
   * @param textOffsetItem The textOffset that should be added
   * @return The same instance of type {@link CreateCompletionResponseChoicesInnerLogprobs}
   */
  @Nonnull public CreateCompletionResponseChoicesInnerLogprobs addTextOffsetItem( @Nonnull final Integer textOffsetItem) {
    if (this.textOffset == null) {
      this.textOffset = new ArrayList<>();
    }
    this.textOffset.add(textOffsetItem);
    return this;
  }

  /**
   * Get textOffset
   * @return textOffset  The textOffset of this {@link CreateCompletionResponseChoicesInnerLogprobs} instance.
   */
  @Nonnull
  public List<Integer> getTextOffset() {
    return textOffset;
  }

  /**
   * Set the textOffset of this {@link CreateCompletionResponseChoicesInnerLogprobs} instance.
   *
   * @param textOffset  The textOffset of this {@link CreateCompletionResponseChoicesInnerLogprobs}
   */
  public void setTextOffset( @Nullable final List<Integer> textOffset) {
    this.textOffset = textOffset;
  }

  /**
   * Set the tokenLogprobs of this {@link CreateCompletionResponseChoicesInnerLogprobs} instance and return the same instance.
   *
   * @param tokenLogprobs  The tokenLogprobs of this {@link CreateCompletionResponseChoicesInnerLogprobs}
   * @return The same instance of this {@link CreateCompletionResponseChoicesInnerLogprobs} class
   */
  @Nonnull public CreateCompletionResponseChoicesInnerLogprobs tokenLogprobs( @Nullable final float[] tokenLogprobs) {
    this.tokenLogprobs = tokenLogprobs;
    return this;
  }

  /**
   * Get tokenLogprobs
   * @return tokenLogprobs  The tokenLogprobs of this {@link CreateCompletionResponseChoicesInnerLogprobs} instance.
   */
  @Nonnull
  public float[] getTokenLogprobs() {
    return tokenLogprobs;
  }

  /**
   * Set the tokenLogprobs of this {@link CreateCompletionResponseChoicesInnerLogprobs} instance.
   *
   * @param tokenLogprobs  The tokenLogprobs of this {@link CreateCompletionResponseChoicesInnerLogprobs}
   */
  public void setTokenLogprobs( @Nullable final float[] tokenLogprobs) {
    this.tokenLogprobs = tokenLogprobs;
  }

  /**
   * Set the tokens of this {@link CreateCompletionResponseChoicesInnerLogprobs} instance and return the same instance.
   *
   * @param tokens  The tokens of this {@link CreateCompletionResponseChoicesInnerLogprobs}
   * @return The same instance of this {@link CreateCompletionResponseChoicesInnerLogprobs} class
   */
  @Nonnull public CreateCompletionResponseChoicesInnerLogprobs tokens( @Nullable final List<String> tokens) {
    this.tokens = tokens;
    return this;
  }
  /**
   * Add one tokens instance to this {@link CreateCompletionResponseChoicesInnerLogprobs}.
   * @param tokensItem The tokens that should be added
   * @return The same instance of type {@link CreateCompletionResponseChoicesInnerLogprobs}
   */
  @Nonnull public CreateCompletionResponseChoicesInnerLogprobs addTokensItem( @Nonnull final String tokensItem) {
    if (this.tokens == null) {
      this.tokens = new ArrayList<>();
    }
    this.tokens.add(tokensItem);
    return this;
  }

  /**
   * Get tokens
   * @return tokens  The tokens of this {@link CreateCompletionResponseChoicesInnerLogprobs} instance.
   */
  @Nonnull
  public List<String> getTokens() {
    return tokens;
  }

  /**
   * Set the tokens of this {@link CreateCompletionResponseChoicesInnerLogprobs} instance.
   *
   * @param tokens  The tokens of this {@link CreateCompletionResponseChoicesInnerLogprobs}
   */
  public void setTokens( @Nullable final List<String> tokens) {
    this.tokens = tokens;
  }

  /**
   * Set the topLogprobs of this {@link CreateCompletionResponseChoicesInnerLogprobs} instance and return the same instance.
   *
   * @param topLogprobs  The topLogprobs of this {@link CreateCompletionResponseChoicesInnerLogprobs}
   * @return The same instance of this {@link CreateCompletionResponseChoicesInnerLogprobs} class
   */
  @Nonnull public CreateCompletionResponseChoicesInnerLogprobs topLogprobs( @Nullable final List<Map<String, BigDecimal>> topLogprobs) {
    this.topLogprobs = topLogprobs;
    return this;
  }
  /**
   * Add one topLogprobs instance to this {@link CreateCompletionResponseChoicesInnerLogprobs}.
   * @param topLogprobsItem The topLogprobs that should be added
   * @return The same instance of type {@link CreateCompletionResponseChoicesInnerLogprobs}
   */
  @Nonnull public CreateCompletionResponseChoicesInnerLogprobs addTopLogprobsItem( @Nonnull final Map<String, BigDecimal> topLogprobsItem) {
    if (this.topLogprobs == null) {
      this.topLogprobs = new ArrayList<>();
    }
    this.topLogprobs.add(topLogprobsItem);
    return this;
  }

  /**
   * Get topLogprobs
   * @return topLogprobs  The topLogprobs of this {@link CreateCompletionResponseChoicesInnerLogprobs} instance.
   */
  @Nonnull
  public List<Map<String, BigDecimal>> getTopLogprobs() {
    return topLogprobs;
  }

  /**
   * Set the topLogprobs of this {@link CreateCompletionResponseChoicesInnerLogprobs} instance.
   *
   * @param topLogprobs  The topLogprobs of this {@link CreateCompletionResponseChoicesInnerLogprobs}
   */
  public void setTopLogprobs( @Nullable final List<Map<String, BigDecimal>> topLogprobs) {
    this.topLogprobs = topLogprobs;
  }

  /**
   * Get the names of the unrecognizable properties of the {@link CreateCompletionResponseChoicesInnerLogprobs}.
   * @return The set of properties names
   */
  @JsonIgnore
  @Nonnull
  public Set<String> getCustomFieldNames() {
    return cloudSdkCustomFields.keySet();
  }

  /**
   * Get the value of an unrecognizable property of this {@link CreateCompletionResponseChoicesInnerLogprobs} instance.
   * @deprecated Use {@link #toMap()} instead.
   * @param name  The name of the property
   * @return The value of the property
   * @throws NoSuchElementException  If no property with the given name could be found.
   */
  @Nullable
  @Deprecated
  public Object getCustomField( @Nonnull final String name ) throws NoSuchElementException {
    if( !cloudSdkCustomFields.containsKey(name) ) {
        throw new NoSuchElementException("CreateCompletionResponseChoicesInnerLogprobs has no field with name '" + name + "'.");
    }
    return cloudSdkCustomFields.get(name);
  }

  /**
   * Get the value of all properties of this {@link CreateCompletionResponseChoicesInnerLogprobs} instance including unrecognized properties.
   *
   * @return The map of all properties
   */
  @JsonIgnore
  @Nonnull
  public Map<String, Object> toMap()
  {
    final Map<String, Object> declaredFields = new LinkedHashMap<>(cloudSdkCustomFields);
    if( textOffset != null ) declaredFields.put("textOffset", textOffset);
    if( tokenLogprobs != null ) declaredFields.put("tokenLogprobs", tokenLogprobs);
    if( tokens != null ) declaredFields.put("tokens", tokens);
    if( topLogprobs != null ) declaredFields.put("topLogprobs", topLogprobs);
    return declaredFields;
  }

  /**
   * Set an unrecognizable property of this {@link CreateCompletionResponseChoicesInnerLogprobs} instance. If the map previously contained a mapping
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
    final CreateCompletionResponseChoicesInnerLogprobs createCompletionResponseChoicesInnerLogprobs = (CreateCompletionResponseChoicesInnerLogprobs) o;
    return Objects.equals(this.cloudSdkCustomFields, createCompletionResponseChoicesInnerLogprobs.cloudSdkCustomFields) &&
        Objects.equals(this.textOffset, createCompletionResponseChoicesInnerLogprobs.textOffset) &&
        Objects.equals(this.tokenLogprobs, createCompletionResponseChoicesInnerLogprobs.tokenLogprobs) &&
        Objects.equals(this.tokens, createCompletionResponseChoicesInnerLogprobs.tokens) &&
        Objects.equals(this.topLogprobs, createCompletionResponseChoicesInnerLogprobs.topLogprobs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(textOffset, tokenLogprobs, tokens, topLogprobs, cloudSdkCustomFields);
  }

  @Override
  @Nonnull public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("class CreateCompletionResponseChoicesInnerLogprobs {\n");
    sb.append("    textOffset: ").append(toIndentedString(textOffset)).append("\n");
    sb.append("    tokenLogprobs: ").append(toIndentedString(tokenLogprobs)).append("\n");
    sb.append("    tokens: ").append(toIndentedString(tokens)).append("\n");
    sb.append("    topLogprobs: ").append(toIndentedString(topLogprobs)).append("\n");
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

