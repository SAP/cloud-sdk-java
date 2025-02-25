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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.sap.cloud.sdk.services.openai.model.OnYourDataApiKeyAuthenticationOptions;
import com.sap.cloud.sdk.services.openai.model.OnYourDataDeploymentNameVectorizationSource;
import com.sap.cloud.sdk.services.openai.model.OnYourDataEndpointVectorizationSource;
import com.sap.cloud.sdk.services.openai.model.OnYourDataVectorizationSourceType;
import java.net.URI;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * AzureSearchChatExtensionParametersEmbeddingDependency
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = OnYourDataDeploymentNameVectorizationSource.class, name = "deployment_name"),
  @JsonSubTypes.Type(value = OnYourDataEndpointVectorizationSource.class, name = "endpoint"),
  @JsonSubTypes.Type(value = OnYourDataDeploymentNameVectorizationSource.class, name = "onYourDataDeploymentNameVectorizationSource"),
  @JsonSubTypes.Type(value = OnYourDataEndpointVectorizationSource.class, name = "onYourDataEndpointVectorizationSource"),
})
// CHECKSTYLE:OFF
public class AzureSearchChatExtensionParametersEmbeddingDependency 
// CHECKSTYLE:ON
{
  @JsonProperty("type")
  private OnYourDataVectorizationSourceType type;

  @JsonProperty("deployment_name")
  private String deploymentName;

  @JsonAnySetter
  @JsonAnyGetter
  private final Map<String, Object> cloudSdkCustomFields = new LinkedHashMap<>();

  /**
   * Set the type of this {@link AzureSearchChatExtensionParametersEmbeddingDependency} instance and return the same instance.
   *
   * @param type  The type of this {@link AzureSearchChatExtensionParametersEmbeddingDependency}
   * @return The same instance of this {@link AzureSearchChatExtensionParametersEmbeddingDependency} class
   */
  @Nonnull public AzureSearchChatExtensionParametersEmbeddingDependency type( @Nonnull final OnYourDataVectorizationSourceType type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type  The type of this {@link AzureSearchChatExtensionParametersEmbeddingDependency} instance.
   */
  @Nonnull
  public OnYourDataVectorizationSourceType getType() {
    return type;
  }

  /**
   * Set the type of this {@link AzureSearchChatExtensionParametersEmbeddingDependency} instance.
   *
   * @param type  The type of this {@link AzureSearchChatExtensionParametersEmbeddingDependency}
   */
  public void setType( @Nonnull final OnYourDataVectorizationSourceType type) {
    this.type = type;
  }

  /**
   * Set the deploymentName of this {@link AzureSearchChatExtensionParametersEmbeddingDependency} instance and return the same instance.
   *
   * @param deploymentName  Specifies the name of the model deployment to use for vectorization. This model deployment must be in the same Azure OpenAI resource, but On Your Data will use this model deployment via an internal call rather than a public one, which enables vector search even in private networks.
   * @return The same instance of this {@link AzureSearchChatExtensionParametersEmbeddingDependency} class
   */
  @Nonnull public AzureSearchChatExtensionParametersEmbeddingDependency deploymentName( @Nonnull final String deploymentName) {
    this.deploymentName = deploymentName;
    return this;
  }

  /**
   * Specifies the name of the model deployment to use for vectorization. This model deployment must be in the same Azure OpenAI resource, but On Your Data will use this model deployment via an internal call rather than a public one, which enables vector search even in private networks.
   * @return deploymentName  The deploymentName of this {@link AzureSearchChatExtensionParametersEmbeddingDependency} instance.
   */
  @Nonnull
  public String getDeploymentName() {
    return deploymentName;
  }

  /**
   * Set the deploymentName of this {@link AzureSearchChatExtensionParametersEmbeddingDependency} instance.
   *
   * @param deploymentName  Specifies the name of the model deployment to use for vectorization. This model deployment must be in the same Azure OpenAI resource, but On Your Data will use this model deployment via an internal call rather than a public one, which enables vector search even in private networks.
   */
  public void setDeploymentName( @Nonnull final String deploymentName) {
    this.deploymentName = deploymentName;
  }

  /**
   * Get the names of the unrecognizable properties of the {@link AzureSearchChatExtensionParametersEmbeddingDependency}.
   * @return The set of properties names
   */
  @JsonIgnore
  @Nonnull
  public Set<String> getCustomFieldNames() {
    return cloudSdkCustomFields.keySet();
  }

  /**
   * Get the value of an unrecognizable property of this {@link AzureSearchChatExtensionParametersEmbeddingDependency} instance.
   * @deprecated Use {@link #toMap()} instead.
   * @param name  The name of the property
   * @return The value of the property
   * @throws NoSuchElementException  If no property with the given name could be found.
   */
  @Nullable
  @Deprecated
  public Object getCustomField( @Nonnull final String name ) throws NoSuchElementException {
    if( !cloudSdkCustomFields.containsKey(name) ) {
        throw new NoSuchElementException("AzureSearchChatExtensionParametersEmbeddingDependency has no field with name '" + name + "'.");
    }
    return cloudSdkCustomFields.get(name);
  }

  /**
   * Get the value of all properties of this {@link AzureSearchChatExtensionParametersEmbeddingDependency} instance including unrecognized properties.
   *
   * @return The map of all properties
   */
  @JsonIgnore
  @Nonnull
  public Map<String, Object> toMap()
  {
    final Map<String, Object> declaredFields = new LinkedHashMap<>(cloudSdkCustomFields);
    if( type != null ) declaredFields.put("type", type);
    if( deploymentName != null ) declaredFields.put("deploymentName", deploymentName);
    return declaredFields;
  }

  /**
   * Set an unrecognizable property of this {@link AzureSearchChatExtensionParametersEmbeddingDependency} instance. If the map previously contained a mapping
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
    final AzureSearchChatExtensionParametersEmbeddingDependency azureSearchChatExtensionParametersEmbeddingDependency = (AzureSearchChatExtensionParametersEmbeddingDependency) o;
    return Objects.equals(this.cloudSdkCustomFields, azureSearchChatExtensionParametersEmbeddingDependency.cloudSdkCustomFields) &&
        Objects.equals(this.type, azureSearchChatExtensionParametersEmbeddingDependency.type) &&
        Objects.equals(this.deploymentName, azureSearchChatExtensionParametersEmbeddingDependency.deploymentName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, deploymentName, cloudSdkCustomFields);
  }

  @Override
  @Nonnull public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("class AzureSearchChatExtensionParametersEmbeddingDependency {\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    deploymentName: ").append(toIndentedString(deploymentName)).append("\n");
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

