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
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Error codes for the inner error object.
 */
public enum InnerErrorCode {
  
  RESPONSIBLE_AI_POLICY_VIOLATION("ResponsibleAIPolicyViolation");

  private final String value;

  InnerErrorCode(String value) {
    this.value = value;
  }

  /**
   * @return The enum value.
   */
  @JsonValue
  public String getValue() {
    return value;
  }

  /**
   * @return The String representation of the enum value.
   */
  @Override
  @Nonnull public String toString() {
    return String.valueOf(value);
  }

  /**
   * Converts the given value to its enum representation.
   *
   * @param value The input value.
   *
   * @return The enum representation of the given value.
   */
  @JsonCreator
  public static InnerErrorCode fromValue(@Nonnull final String value) {
    for (final InnerErrorCode b : InnerErrorCode.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

