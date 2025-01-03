/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

/*
 * Soda Store API
 * API for managing sodas in a soda store
 *
 * The version of the OpenAPI document: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package test;

import java.util.Objects;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import test.Cola;
import test.Fanta;
import test.FantaFlavor;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * OneOfWithDiscriminatorAndMapping
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "sodaType", visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = Cola.class, name = "cool_cola"),
  @JsonSubTypes.Type(value = Fanta.class, name = "fancy_fanta"),
  @JsonSubTypes.Type(value = Cola.class, name = "Cola"),
  @JsonSubTypes.Type(value = Fanta.class, name = "Fanta"),
})

public interface OneOfWithDiscriminatorAndMapping  {
    String getSodaType();
}

