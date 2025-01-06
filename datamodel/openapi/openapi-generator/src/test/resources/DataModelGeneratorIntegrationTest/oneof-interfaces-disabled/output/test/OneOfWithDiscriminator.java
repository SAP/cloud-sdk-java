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
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * OneOfWithDiscriminator
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "sodaType", visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = Cola.class, name = "Cola"),
  @JsonSubTypes.Type(value = Fanta.class, name = "Fanta"),
})
// CHECKSTYLE:OFF
public class OneOfWithDiscriminator 
// CHECKSTYLE:ON
{
  @JsonProperty("sodaType")
  private String sodaType;

  @JsonAnySetter
  @JsonAnyGetter
  private final Map<String, Object> cloudSdkCustomFields = new LinkedHashMap<>();

  /**
   * Set the sodaType of this {@link OneOfWithDiscriminator} instance and return the same instance.
   *
   * @param sodaType  The sodaType of this {@link OneOfWithDiscriminator}
   * @return The same instance of this {@link OneOfWithDiscriminator} class
   */
  @Nonnull public OneOfWithDiscriminator sodaType( @Nullable final String sodaType) {
    this.sodaType = sodaType;
    return this;
  }

  /**
   * Get sodaType
   * @return sodaType  The sodaType of this {@link OneOfWithDiscriminator} instance.
   */
  @Nonnull
  public String getSodaType() {
    return sodaType;
  }

  /**
   * Set the sodaType of this {@link OneOfWithDiscriminator} instance.
   *
   * @param sodaType  The sodaType of this {@link OneOfWithDiscriminator}
   */
  public void setSodaType( @Nullable final String sodaType) {
    this.sodaType = sodaType;
  }

  /**
   * Get the names of the unrecognizable properties of the {@link OneOfWithDiscriminator}.
   * @return The set of properties names
   */
  @JsonIgnore
  @Nonnull
  public Set<String> getCustomFieldNames() {
    return cloudSdkCustomFields.keySet();
  }

  /**
   * Get the value of an unrecognizable property of this {@link OneOfWithDiscriminator} instance.
   * @param name  The name of the property
   * @return The value of the property
   * @throws NoSuchElementException  If no property with the given name could be found.
   */
  @Nullable
  public Object getCustomField( @Nonnull final String name ) throws NoSuchElementException {
    if( !cloudSdkCustomFields.containsKey(name) ) {
        throw new NoSuchElementException("OneOfWithDiscriminator has no field with name '" + name + "'.");
    }
    return cloudSdkCustomFields.get(name);
  }

  /**
   * Set an unrecognizable property of this {@link OneOfWithDiscriminator} instance. If the map previously contained a mapping
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
    final OneOfWithDiscriminator oneOfWithDiscriminator = (OneOfWithDiscriminator) o;
    return Objects.equals(this.cloudSdkCustomFields, oneOfWithDiscriminator.cloudSdkCustomFields) &&
        Objects.equals(this.sodaType, oneOfWithDiscriminator.sodaType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sodaType, cloudSdkCustomFields);
  }

  @Override
  @Nonnull public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("class OneOfWithDiscriminator {\n");
    sb.append("    sodaType: ").append(toIndentedString(sodaType)).append("\n");
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

