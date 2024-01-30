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
import com.sap.cloud.sdk.services.keywordAggregator.model.NestedChildObject;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * ChildObject2
 */

// CHECKSTYLE:OFF
public class ChildObject2 
// CHECKSTYLE:ON
{
  @JsonProperty("id")
  private String id;

  @JsonProperty("type")
  private String type;

  @JsonProperty("text")
  private String text;

  @JsonAnySetter
  @JsonAnyGetter
  private final Map<String, Object> cloudSdkCustomFields = new LinkedHashMap<>();

   /**
   * Set the id of this {@link ChildObject2} instance and return the same instance.
   *
   * @param id  The id of this {@link ChildObject2}
   * @return The same instance of this {@link ChildObject2} class
   */
   @Nonnull public ChildObject2 id(@Nonnull final String id) {
    this.id = id;
    return this;
  }

   /**
   * Get id
   * @return id  The id of this {@link ChildObject2} instance.
  **/
  @Nonnull public String getId() {
    return id;
  }

  /**
  * Set the id of this {@link ChildObject2} instance.
  *
  * @param id  The id of this {@link ChildObject2}
  */
  public void setId( @Nonnull final String id) {
    this.id = id;
  }

   /**
   * Set the type of this {@link ChildObject2} instance and return the same instance.
   *
   * @param type  The type of this {@link ChildObject2}
   * @return The same instance of this {@link ChildObject2} class
   */
   @Nonnull public ChildObject2 type(@Nonnull final String type) {
    this.type = type;
    return this;
  }

   /**
   * Get type
   * @return type  The type of this {@link ChildObject2} instance.
  **/
  @Nonnull public String getType() {
    return type;
  }

  /**
  * Set the type of this {@link ChildObject2} instance.
  *
  * @param type  The type of this {@link ChildObject2}
  */
  public void setType( @Nonnull final String type) {
    this.type = type;
  }

   /**
   * Set the text of this {@link ChildObject2} instance and return the same instance.
   *
   * @param text  The text of this {@link ChildObject2}
   * @return The same instance of this {@link ChildObject2} class
   */
   @Nonnull public ChildObject2 text(@Nonnull final String text) {
    this.text = text;
    return this;
  }

   /**
   * Get text
   * @return text  The text of this {@link ChildObject2} instance.
  **/
  @Nonnull public String getText() {
    return text;
  }

  /**
  * Set the text of this {@link ChildObject2} instance.
  *
  * @param text  The text of this {@link ChildObject2}
  */
  public void setText( @Nonnull final String text) {
    this.text = text;
  }

  /**
   * Get the names of the unrecognizable properties of the {@link ChildObject2}.
   * @return The set of properties names
   */
  @JsonIgnore
  @Nonnull
  public Set<String> getCustomFieldNames() {
    return cloudSdkCustomFields.keySet();
  }

  /**
   * Get the value of an unrecognizable property of this {@link ChildObject2} instance.
   * @param name  The name of the property
   * @return The value of the property
   * @throws NoSuchElementException  If no property with the given name could be found.
   */
  @Nullable
  public Object getCustomField(@Nonnull final String name) throws NoSuchElementException {
    if( !cloudSdkCustomFields.containsKey(name) ) {
        throw new NoSuchElementException("ChildObject2 has no field with name '" + name + "'.");
    }
    return cloudSdkCustomFields.get(name);
  }

  /**
   * Set an unrecognizable property of this {@link ChildObject2} instance. If the map previously contained a mapping
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
    final ChildObject2 childObject2 = (ChildObject2) o;
    return Objects.equals(this.cloudSdkCustomFields, childObject2.cloudSdkCustomFields) &&
        Objects.equals(this.id, childObject2.id) &&
        Objects.equals(this.type, childObject2.type) &&
        Objects.equals(this.text, childObject2.text);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, type, text, cloudSdkCustomFields);
  }

  @Override
  @Nonnull public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("class ChildObject2 {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
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
