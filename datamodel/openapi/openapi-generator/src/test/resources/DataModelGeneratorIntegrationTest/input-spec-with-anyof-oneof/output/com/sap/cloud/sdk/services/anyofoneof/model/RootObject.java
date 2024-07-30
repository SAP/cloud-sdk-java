/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

/*
 * Sample API
 * API for managing root and child objects
 *
 * The version of the OpenAPI document: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package com.sap.cloud.sdk.services.anyofoneof.model;

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
import com.sap.cloud.sdk.services.anyofoneof.model.RootObjectQuestionsInner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * RootObject
 */


// CHECKSTYLE:OFF
public class RootObject 
// CHECKSTYLE:ON
{
  @JsonProperty("questions")
  private List<RootObjectQuestionsInner> questions = new ArrayList<>();

  @JsonAnySetter
  @JsonAnyGetter
  private final Map<String, Object> cloudSdkCustomFields = new LinkedHashMap<>();

   /**
   * Set the questions of this {@link RootObject} instance and return the same instance.
   *
   * @param questions  The questions of this {@link RootObject}
   * @return The same instance of this {@link RootObject} class
   */
   @Nonnull public RootObject questions(@Nonnull final List<RootObjectQuestionsInner> questions) {
    this.questions = questions;
    return this;
  }
  /**
  * Add one questions instance to this {@link RootObject}.
  * @param questionsItem The questions that should be added
  * @return The same instance of type {@link RootObject}
  */
  @Nonnull public RootObject addQuestionsItem( @Nonnull final RootObjectQuestionsInner questionsItem) {
    if (this.questions == null) {
      this.questions = new ArrayList<>();
    }
    this.questions.add(questionsItem);
    return this;
  }

   /**
   * Get questions
   * @return questions  The questions of this {@link RootObject} instance.
  **/
  @Nonnull public List<RootObjectQuestionsInner> getQuestions() {
    return questions;
  }

  /**
  * Set the questions of this {@link RootObject} instance.
  *
  * @param questions  The questions of this {@link RootObject}
  */
  public void setQuestions( @Nonnull final List<RootObjectQuestionsInner> questions) {
    this.questions = questions;
  }

  /**
   * Get the names of the unrecognizable properties of the {@link RootObject}.
   * @return The set of properties names
   */
  @JsonIgnore
  @Nonnull
  public Set<String> getCustomFieldNames() {
    return cloudSdkCustomFields.keySet();
  }

  /**
   * Get the value of an unrecognizable property of this {@link RootObject} instance.
   * @param name  The name of the property
   * @return The value of the property
   * @throws NoSuchElementException  If no property with the given name could be found.
   */
  @Nullable
  public Object getCustomField(@Nonnull final String name) throws NoSuchElementException {
    if( !cloudSdkCustomFields.containsKey(name) ) {
        throw new NoSuchElementException("RootObject has no field with name '" + name + "'.");
    }
    return cloudSdkCustomFields.get(name);
  }

  /**
   * Set an unrecognizable property of this {@link RootObject} instance. If the map previously contained a mapping
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
    final RootObject rootObject = (RootObject) o;
    return Objects.equals(this.cloudSdkCustomFields, rootObject.cloudSdkCustomFields) &&
        Objects.equals(this.questions, rootObject.questions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(questions, cloudSdkCustomFields);
  }

  @Override
  @Nonnull public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("class RootObject {\n");
    sb.append("    questions: ").append(toIndentedString(questions)).append("\n");
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

