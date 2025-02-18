/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

/*
 * Soda Store API
 * API for managing sodas in a soda store
 *
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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import test.FantaFlavor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.annotations.Beta;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Fanta
 */

@Beta// CHECKSTYLE:OFF
public class Fanta implements OneOf, OneOfWithDiscriminator, OneOfWithDiscriminatorAndMapping 
// CHECKSTYLE:ON
{
  @JsonProperty("sodaType")
  private String sodaType;

  @JsonProperty("flavor")
  private FantaFlavor flavor;

  @JsonAnySetter
  @JsonAnyGetter
  private final Map<String, Object> cloudSdkCustomFields = new LinkedHashMap<>();

  /**
   * Set the sodaType of this {@link Fanta} instance and return the same instance.
   *
   * @param sodaType  The sodaType of this {@link Fanta}
   * @return The same instance of this {@link Fanta} class
   */
  @Nonnull public Fanta sodaType( @Nullable final String sodaType) {
    this.sodaType = sodaType;
    return this;
  }

  /**
   * Get sodaType
   * @return sodaType  The sodaType of this {@link Fanta} instance.
   */
  @Nonnull
  public String getSodaType() {
    return sodaType;
  }

  /**
   * Set the sodaType of this {@link Fanta} instance.
   *
   * @param sodaType  The sodaType of this {@link Fanta}
   */
  public void setSodaType( @Nullable final String sodaType) {
    this.sodaType = sodaType;
  }

  /**
   * Set the flavor of this {@link Fanta} instance and return the same instance.
   *
   * @param flavor  The flavor of this {@link Fanta}
   * @return The same instance of this {@link Fanta} class
   */
  @Nonnull public Fanta flavor( @Nullable final FantaFlavor flavor) {
    this.flavor = flavor;
    return this;
  }

  /**
   * Get flavor
   * @return flavor  The flavor of this {@link Fanta} instance.
   */
  @Nonnull
  public FantaFlavor getFlavor() {
    return flavor;
  }

  /**
   * Set the flavor of this {@link Fanta} instance.
   *
   * @param flavor  The flavor of this {@link Fanta}
   */
  public void setFlavor( @Nullable final FantaFlavor flavor) {
    this.flavor = flavor;
  }

  /**
   * Get the names of the unrecognizable properties of the {@link Fanta}.
   * @return The set of properties names
   */
  @JsonIgnore
  @Nonnull
  public Set<String> getCustomFieldNames() {
    return cloudSdkCustomFields.keySet();
  }

  /**
   * Get the value of an unrecognizable property of this {@link Fanta} instance.
   * @deprecated Use {@link #toMap()} instead.
   * @param name  The name of the property
   * @return The value of the property
   * @throws NoSuchElementException  If no property with the given name could be found.
   */
  @Nullable
  @Deprecated
  public Object getCustomField( @Nonnull final String name ) throws NoSuchElementException {
    if( !cloudSdkCustomFields.containsKey(name) ) {
        throw new NoSuchElementException("Fanta has no field with name '" + name + "'.");
    }
    return cloudSdkCustomFields.get(name);
  }

  /**
   * Get the value of all properties of this {@link Fanta} instance including unrecognized properties.
   *
   * @return The map of all properties
   */
  @JsonIgnore
  @Nonnull
  public Map<String, Object> toMap()
  {
    final Map<String, Object> declaredFields = Arrays.stream(getClass().getDeclaredFields())
        .collect(LinkedHashMap::new, ( map, field ) -> {
          Object value = null;
          try {
            value = field.get(this);
          } catch (IllegalAccessException e) {
            // do nothing, value will not be added
          }
          final String name = field.getName();
          if (value != null && !name.equals("cloudSdkCustomFields")) {
            map.put(name, value);
          }
        }, Map::putAll);
    declaredFields.putAll(cloudSdkCustomFields);
    return declaredFields;
  }

  /**
   * Set an unrecognizable property of this {@link Fanta} instance. If the map previously contained a mapping
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
    final Fanta fanta = (Fanta) o;
    return Objects.equals(this.cloudSdkCustomFields, fanta.cloudSdkCustomFields) &&
        Objects.equals(this.sodaType, fanta.sodaType) &&
        Objects.equals(this.flavor, fanta.flavor);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sodaType, flavor, cloudSdkCustomFields);
  }

  @Override
  @Nonnull public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("class Fanta {\n");
    sb.append("    sodaType: ").append(toIndentedString(sodaType)).append("\n");
    sb.append("    flavor: ").append(toIndentedString(flavor)).append("\n");
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

