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
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Transcription or translation segment.
 */
// CHECKSTYLE:OFF
public class AudioSegment 
// CHECKSTYLE:ON
{
  @JsonProperty("id")
  private Integer id;

  @JsonProperty("seek")
  private BigDecimal seek;

  @JsonProperty("start")
  private BigDecimal start;

  @JsonProperty("end")
  private BigDecimal end;

  @JsonProperty("text")
  private String text;

  @JsonProperty("tokens")
  private float[] tokens;

  @JsonProperty("temperature")
  private BigDecimal temperature;

  @JsonProperty("avg_logprob")
  private BigDecimal avgLogprob;

  @JsonProperty("compression_ratio")
  private BigDecimal compressionRatio;

  @JsonProperty("no_speech_prob")
  private BigDecimal noSpeechProb;

  @JsonAnySetter
  @JsonAnyGetter
  private final Map<String, Object> cloudSdkCustomFields = new LinkedHashMap<>();

  /**
   * Set the id of this {@link AudioSegment} instance and return the same instance.
   *
   * @param id  Segment identifier.
   * @return The same instance of this {@link AudioSegment} class
   */
  @Nonnull public AudioSegment id( @Nullable final Integer id) {
    this.id = id;
    return this;
  }

  /**
   * Segment identifier.
   * @return id  The id of this {@link AudioSegment} instance.
   */
  @Nonnull
  public Integer getId() {
    return id;
  }

  /**
   * Set the id of this {@link AudioSegment} instance.
   *
   * @param id  Segment identifier.
   */
  public void setId( @Nullable final Integer id) {
    this.id = id;
  }

  /**
   * Set the seek of this {@link AudioSegment} instance and return the same instance.
   *
   * @param seek  Offset of the segment.
   * @return The same instance of this {@link AudioSegment} class
   */
  @Nonnull public AudioSegment seek( @Nullable final BigDecimal seek) {
    this.seek = seek;
    return this;
  }

  /**
   * Offset of the segment.
   * @return seek  The seek of this {@link AudioSegment} instance.
   */
  @Nonnull
  public BigDecimal getSeek() {
    return seek;
  }

  /**
   * Set the seek of this {@link AudioSegment} instance.
   *
   * @param seek  Offset of the segment.
   */
  public void setSeek( @Nullable final BigDecimal seek) {
    this.seek = seek;
  }

  /**
   * Set the start of this {@link AudioSegment} instance and return the same instance.
   *
   * @param start  Segment start offset.
   * @return The same instance of this {@link AudioSegment} class
   */
  @Nonnull public AudioSegment start( @Nullable final BigDecimal start) {
    this.start = start;
    return this;
  }

  /**
   * Segment start offset.
   * @return start  The start of this {@link AudioSegment} instance.
   */
  @Nonnull
  public BigDecimal getStart() {
    return start;
  }

  /**
   * Set the start of this {@link AudioSegment} instance.
   *
   * @param start  Segment start offset.
   */
  public void setStart( @Nullable final BigDecimal start) {
    this.start = start;
  }

  /**
   * Set the end of this {@link AudioSegment} instance and return the same instance.
   *
   * @param end  Segment end offset.
   * @return The same instance of this {@link AudioSegment} class
   */
  @Nonnull public AudioSegment end( @Nullable final BigDecimal end) {
    this.end = end;
    return this;
  }

  /**
   * Segment end offset.
   * @return end  The end of this {@link AudioSegment} instance.
   */
  @Nonnull
  public BigDecimal getEnd() {
    return end;
  }

  /**
   * Set the end of this {@link AudioSegment} instance.
   *
   * @param end  Segment end offset.
   */
  public void setEnd( @Nullable final BigDecimal end) {
    this.end = end;
  }

  /**
   * Set the text of this {@link AudioSegment} instance and return the same instance.
   *
   * @param text  Segment text.
   * @return The same instance of this {@link AudioSegment} class
   */
  @Nonnull public AudioSegment text( @Nullable final String text) {
    this.text = text;
    return this;
  }

  /**
   * Segment text.
   * @return text  The text of this {@link AudioSegment} instance.
   */
  @Nonnull
  public String getText() {
    return text;
  }

  /**
   * Set the text of this {@link AudioSegment} instance.
   *
   * @param text  Segment text.
   */
  public void setText( @Nullable final String text) {
    this.text = text;
  }

  /**
   * Set the tokens of this {@link AudioSegment} instance and return the same instance.
   *
   * @param tokens  Tokens of the text.
   * @return The same instance of this {@link AudioSegment} class
   */
  @Nonnull public AudioSegment tokens( @Nullable final float[] tokens) {
    this.tokens = tokens;
    return this;
  }

  /**
   * Tokens of the text.
   * @return tokens  The tokens of this {@link AudioSegment} instance.
   */
  @Nonnull
  public float[] getTokens() {
    return tokens;
  }

  /**
   * Set the tokens of this {@link AudioSegment} instance.
   *
   * @param tokens  Tokens of the text.
   */
  public void setTokens( @Nullable final float[] tokens) {
    this.tokens = tokens;
  }

  /**
   * Set the temperature of this {@link AudioSegment} instance and return the same instance.
   *
   * @param temperature  Temperature.
   * @return The same instance of this {@link AudioSegment} class
   */
  @Nonnull public AudioSegment temperature( @Nullable final BigDecimal temperature) {
    this.temperature = temperature;
    return this;
  }

  /**
   * Temperature.
   * @return temperature  The temperature of this {@link AudioSegment} instance.
   */
  @Nonnull
  public BigDecimal getTemperature() {
    return temperature;
  }

  /**
   * Set the temperature of this {@link AudioSegment} instance.
   *
   * @param temperature  Temperature.
   */
  public void setTemperature( @Nullable final BigDecimal temperature) {
    this.temperature = temperature;
  }

  /**
   * Set the avgLogprob of this {@link AudioSegment} instance and return the same instance.
   *
   * @param avgLogprob  Average log probability.
   * @return The same instance of this {@link AudioSegment} class
   */
  @Nonnull public AudioSegment avgLogprob( @Nullable final BigDecimal avgLogprob) {
    this.avgLogprob = avgLogprob;
    return this;
  }

  /**
   * Average log probability.
   * @return avgLogprob  The avgLogprob of this {@link AudioSegment} instance.
   */
  @Nonnull
  public BigDecimal getAvgLogprob() {
    return avgLogprob;
  }

  /**
   * Set the avgLogprob of this {@link AudioSegment} instance.
   *
   * @param avgLogprob  Average log probability.
   */
  public void setAvgLogprob( @Nullable final BigDecimal avgLogprob) {
    this.avgLogprob = avgLogprob;
  }

  /**
   * Set the compressionRatio of this {@link AudioSegment} instance and return the same instance.
   *
   * @param compressionRatio  Compression ratio.
   * @return The same instance of this {@link AudioSegment} class
   */
  @Nonnull public AudioSegment compressionRatio( @Nullable final BigDecimal compressionRatio) {
    this.compressionRatio = compressionRatio;
    return this;
  }

  /**
   * Compression ratio.
   * @return compressionRatio  The compressionRatio of this {@link AudioSegment} instance.
   */
  @Nonnull
  public BigDecimal getCompressionRatio() {
    return compressionRatio;
  }

  /**
   * Set the compressionRatio of this {@link AudioSegment} instance.
   *
   * @param compressionRatio  Compression ratio.
   */
  public void setCompressionRatio( @Nullable final BigDecimal compressionRatio) {
    this.compressionRatio = compressionRatio;
  }

  /**
   * Set the noSpeechProb of this {@link AudioSegment} instance and return the same instance.
   *
   * @param noSpeechProb  Probability of &#39;no speech&#39;.
   * @return The same instance of this {@link AudioSegment} class
   */
  @Nonnull public AudioSegment noSpeechProb( @Nullable final BigDecimal noSpeechProb) {
    this.noSpeechProb = noSpeechProb;
    return this;
  }

  /**
   * Probability of &#39;no speech&#39;.
   * @return noSpeechProb  The noSpeechProb of this {@link AudioSegment} instance.
   */
  @Nonnull
  public BigDecimal getNoSpeechProb() {
    return noSpeechProb;
  }

  /**
   * Set the noSpeechProb of this {@link AudioSegment} instance.
   *
   * @param noSpeechProb  Probability of &#39;no speech&#39;.
   */
  public void setNoSpeechProb( @Nullable final BigDecimal noSpeechProb) {
    this.noSpeechProb = noSpeechProb;
  }

  /**
   * Get the names of the unrecognizable properties of the {@link AudioSegment}.
   * @return The set of properties names
   */
  @JsonIgnore
  @Nonnull
  public Set<String> getCustomFieldNames() {
    return cloudSdkCustomFields.keySet();
  }

  /**
   * Get the value of an unrecognizable property of this {@link AudioSegment} instance.
   * @deprecated Use {@link #toMap()} instead.
   * @param name  The name of the property
   * @return The value of the property
   * @throws NoSuchElementException  If no property with the given name could be found.
   */
  @Nullable
  @Deprecated
  public Object getCustomField( @Nonnull final String name ) throws NoSuchElementException {
    if( !cloudSdkCustomFields.containsKey(name) ) {
        throw new NoSuchElementException("AudioSegment has no field with name '" + name + "'.");
    }
    return cloudSdkCustomFields.get(name);
  }

  /**
   * Get the value of all properties of this {@link AudioSegment} instance including unrecognized properties.
   *
   * @return The map of all properties
   */
  @JsonIgnore
  @Nonnull
  public Map<String, Object> toMap()
  {
    final Map<String, Object> declaredFields = new LinkedHashMap<>(cloudSdkCustomFields);
    if( id != null ) declaredFields.put("id", id);
    if( seek != null ) declaredFields.put("seek", seek);
    if( start != null ) declaredFields.put("start", start);
    if( end != null ) declaredFields.put("end", end);
    if( text != null ) declaredFields.put("text", text);
    if( tokens != null ) declaredFields.put("tokens", tokens);
    if( temperature != null ) declaredFields.put("temperature", temperature);
    if( avgLogprob != null ) declaredFields.put("avgLogprob", avgLogprob);
    if( compressionRatio != null ) declaredFields.put("compressionRatio", compressionRatio);
    if( noSpeechProb != null ) declaredFields.put("noSpeechProb", noSpeechProb);
    return declaredFields;
  }

  /**
   * Set an unrecognizable property of this {@link AudioSegment} instance. If the map previously contained a mapping
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
    final AudioSegment audioSegment = (AudioSegment) o;
    return Objects.equals(this.cloudSdkCustomFields, audioSegment.cloudSdkCustomFields) &&
        Objects.equals(this.id, audioSegment.id) &&
        Objects.equals(this.seek, audioSegment.seek) &&
        Objects.equals(this.start, audioSegment.start) &&
        Objects.equals(this.end, audioSegment.end) &&
        Objects.equals(this.text, audioSegment.text) &&
        Objects.equals(this.tokens, audioSegment.tokens) &&
        Objects.equals(this.temperature, audioSegment.temperature) &&
        Objects.equals(this.avgLogprob, audioSegment.avgLogprob) &&
        Objects.equals(this.compressionRatio, audioSegment.compressionRatio) &&
        Objects.equals(this.noSpeechProb, audioSegment.noSpeechProb);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, seek, start, end, text, tokens, temperature, avgLogprob, compressionRatio, noSpeechProb, cloudSdkCustomFields);
  }

  @Override
  @Nonnull public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("class AudioSegment {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    seek: ").append(toIndentedString(seek)).append("\n");
    sb.append("    start: ").append(toIndentedString(start)).append("\n");
    sb.append("    end: ").append(toIndentedString(end)).append("\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
    sb.append("    tokens: ").append(toIndentedString(tokens)).append("\n");
    sb.append("    temperature: ").append(toIndentedString(temperature)).append("\n");
    sb.append("    avgLogprob: ").append(toIndentedString(avgLogprob)).append("\n");
    sb.append("    compressionRatio: ").append(toIndentedString(compressionRatio)).append("\n");
    sb.append("    noSpeechProb: ").append(toIndentedString(noSpeechProb)).append("\n");
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

