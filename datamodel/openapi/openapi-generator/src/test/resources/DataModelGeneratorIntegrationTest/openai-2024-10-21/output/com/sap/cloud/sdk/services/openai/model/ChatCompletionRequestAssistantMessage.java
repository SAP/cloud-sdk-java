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
import com.sap.cloud.sdk.services.openai.model.ChatCompletionMessageToolCall;
import com.sap.cloud.sdk.services.openai.model.ChatCompletionRequestAssistantMessageContent;
import com.sap.cloud.sdk.services.openai.model.ChatCompletionRequestAssistantMessageFunctionCall;
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
 * ChatCompletionRequestAssistantMessage
 */
// CHECKSTYLE:OFF
public class ChatCompletionRequestAssistantMessage 
// CHECKSTYLE:ON
{
  @JsonProperty("content")
  private ChatCompletionRequestAssistantMessageContent content;

  @JsonProperty("refusal")
  private String refusal;

  /**
   * The role of the messages author, in this case &#x60;assistant&#x60;.
   */
  public enum RoleEnum {
    /**
    * The ASSISTANT option of this ChatCompletionRequestAssistantMessage
    */
    ASSISTANT("assistant");

    private String value;

    RoleEnum(String value) {
      this.value = value;
    }

    /**
    * Get the value of the enum
    * @return The enum value
    */
    @JsonValue
    @Nonnull public String getValue() {
      return value;
    }

    /**
    * Get the String value of the enum value.
    * @return The enum value as String
    */
    @Override
    @Nonnull public String toString() {
      return String.valueOf(value);
    }

    /**
    * Get the enum value from a String value
    * @param value The String value
    * @return The enum value of type ChatCompletionRequestAssistantMessage
    */
    @JsonCreator
    @Nonnull public static RoleEnum fromValue(@Nonnull final String value) {
      for (RoleEnum b : RoleEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("role")
  private RoleEnum role;

  @JsonProperty("name")
  private String name;

  @JsonProperty("tool_calls")
  private List<ChatCompletionMessageToolCall> toolCalls = new ArrayList<>();

  @JsonProperty("function_call")
  private ChatCompletionRequestAssistantMessageFunctionCall functionCall;

  @JsonAnySetter
  @JsonAnyGetter
  private final Map<String, Object> cloudSdkCustomFields = new LinkedHashMap<>();

  /**
   * Set the content of this {@link ChatCompletionRequestAssistantMessage} instance and return the same instance.
   *
   * @param content  The content of this {@link ChatCompletionRequestAssistantMessage}
   * @return The same instance of this {@link ChatCompletionRequestAssistantMessage} class
   */
  @Nonnull public ChatCompletionRequestAssistantMessage content( @Nullable final ChatCompletionRequestAssistantMessageContent content) {
    this.content = content;
    return this;
  }

  /**
   * Get content
   * @return content  The content of this {@link ChatCompletionRequestAssistantMessage} instance.
   */
  @Nullable
  public ChatCompletionRequestAssistantMessageContent getContent() {
    return content;
  }

  /**
   * Set the content of this {@link ChatCompletionRequestAssistantMessage} instance.
   *
   * @param content  The content of this {@link ChatCompletionRequestAssistantMessage}
   */
  public void setContent( @Nullable final ChatCompletionRequestAssistantMessageContent content) {
    this.content = content;
  }

  /**
   * Set the refusal of this {@link ChatCompletionRequestAssistantMessage} instance and return the same instance.
   *
   * @param refusal  The refusal message by the assistant.
   * @return The same instance of this {@link ChatCompletionRequestAssistantMessage} class
   */
  @Nonnull public ChatCompletionRequestAssistantMessage refusal( @Nullable final String refusal) {
    this.refusal = refusal;
    return this;
  }

  /**
   * The refusal message by the assistant.
   * @return refusal  The refusal of this {@link ChatCompletionRequestAssistantMessage} instance.
   */
  @Nullable
  public String getRefusal() {
    return refusal;
  }

  /**
   * Set the refusal of this {@link ChatCompletionRequestAssistantMessage} instance.
   *
   * @param refusal  The refusal message by the assistant.
   */
  public void setRefusal( @Nullable final String refusal) {
    this.refusal = refusal;
  }

  /**
   * Set the role of this {@link ChatCompletionRequestAssistantMessage} instance and return the same instance.
   *
   * @param role  The role of the messages author, in this case &#x60;assistant&#x60;.
   * @return The same instance of this {@link ChatCompletionRequestAssistantMessage} class
   */
  @Nonnull public ChatCompletionRequestAssistantMessage role( @Nonnull final RoleEnum role) {
    this.role = role;
    return this;
  }

  /**
   * The role of the messages author, in this case &#x60;assistant&#x60;.
   * @return role  The role of this {@link ChatCompletionRequestAssistantMessage} instance.
   */
  @Nonnull
  public RoleEnum getRole() {
    return role;
  }

  /**
   * Set the role of this {@link ChatCompletionRequestAssistantMessage} instance.
   *
   * @param role  The role of the messages author, in this case &#x60;assistant&#x60;.
   */
  public void setRole( @Nonnull final RoleEnum role) {
    this.role = role;
  }

  /**
   * Set the name of this {@link ChatCompletionRequestAssistantMessage} instance and return the same instance.
   *
   * @param name  An optional name for the participant. Provides the model information to differentiate between participants of the same role.
   * @return The same instance of this {@link ChatCompletionRequestAssistantMessage} class
   */
  @Nonnull public ChatCompletionRequestAssistantMessage name( @Nullable final String name) {
    this.name = name;
    return this;
  }

  /**
   * An optional name for the participant. Provides the model information to differentiate between participants of the same role.
   * @return name  The name of this {@link ChatCompletionRequestAssistantMessage} instance.
   */
  @Nonnull
  public String getName() {
    return name;
  }

  /**
   * Set the name of this {@link ChatCompletionRequestAssistantMessage} instance.
   *
   * @param name  An optional name for the participant. Provides the model information to differentiate between participants of the same role.
   */
  public void setName( @Nullable final String name) {
    this.name = name;
  }

  /**
   * Set the toolCalls of this {@link ChatCompletionRequestAssistantMessage} instance and return the same instance.
   *
   * @param toolCalls  The tool calls generated by the model, such as function calls.
   * @return The same instance of this {@link ChatCompletionRequestAssistantMessage} class
   */
  @Nonnull public ChatCompletionRequestAssistantMessage toolCalls( @Nullable final List<ChatCompletionMessageToolCall> toolCalls) {
    this.toolCalls = toolCalls;
    return this;
  }
  /**
   * Add one toolCalls instance to this {@link ChatCompletionRequestAssistantMessage}.
   * @param toolCallsItem The toolCalls that should be added
   * @return The same instance of type {@link ChatCompletionRequestAssistantMessage}
   */
  @Nonnull public ChatCompletionRequestAssistantMessage addToolCallsItem( @Nonnull final ChatCompletionMessageToolCall toolCallsItem) {
    if (this.toolCalls == null) {
      this.toolCalls = new ArrayList<>();
    }
    this.toolCalls.add(toolCallsItem);
    return this;
  }

  /**
   * The tool calls generated by the model, such as function calls.
   * @return toolCalls  The toolCalls of this {@link ChatCompletionRequestAssistantMessage} instance.
   */
  @Nonnull
  public List<ChatCompletionMessageToolCall> getToolCalls() {
    return toolCalls;
  }

  /**
   * Set the toolCalls of this {@link ChatCompletionRequestAssistantMessage} instance.
   *
   * @param toolCalls  The tool calls generated by the model, such as function calls.
   */
  public void setToolCalls( @Nullable final List<ChatCompletionMessageToolCall> toolCalls) {
    this.toolCalls = toolCalls;
  }

  /**
   * Set the functionCall of this {@link ChatCompletionRequestAssistantMessage} instance and return the same instance.
   *
   * @param functionCall  The functionCall of this {@link ChatCompletionRequestAssistantMessage}
   * @return The same instance of this {@link ChatCompletionRequestAssistantMessage} class
   */
  @Nonnull public ChatCompletionRequestAssistantMessage functionCall( @Nullable final ChatCompletionRequestAssistantMessageFunctionCall functionCall) {
    this.functionCall = functionCall;
    return this;
  }

  /**
   * Get functionCall
   * @return functionCall  The functionCall of this {@link ChatCompletionRequestAssistantMessage} instance.
   * @deprecated
   */
  @Deprecated
  @Nullable
  public ChatCompletionRequestAssistantMessageFunctionCall getFunctionCall() {
    return functionCall;
  }

  /**
   * Set the functionCall of this {@link ChatCompletionRequestAssistantMessage} instance.
   *
   * @param functionCall  The functionCall of this {@link ChatCompletionRequestAssistantMessage}
   */
  public void setFunctionCall( @Nullable final ChatCompletionRequestAssistantMessageFunctionCall functionCall) {
    this.functionCall = functionCall;
  }

  /**
   * Get the names of the unrecognizable properties of the {@link ChatCompletionRequestAssistantMessage}.
   * @return The set of properties names
   */
  @JsonIgnore
  @Nonnull
  public Set<String> getCustomFieldNames() {
    return cloudSdkCustomFields.keySet();
  }

  /**
   * Get the value of an unrecognizable property of this {@link ChatCompletionRequestAssistantMessage} instance.
   * @deprecated Use {@link #toMap()} instead.
   * @param name  The name of the property
   * @return The value of the property
   * @throws NoSuchElementException  If no property with the given name could be found.
   */
  @Nullable
  @Deprecated
  public Object getCustomField( @Nonnull final String name ) throws NoSuchElementException {
    if( !cloudSdkCustomFields.containsKey(name) ) {
        throw new NoSuchElementException("ChatCompletionRequestAssistantMessage has no field with name '" + name + "'.");
    }
    return cloudSdkCustomFields.get(name);
  }

  /**
   * Get the value of all properties of this {@link ChatCompletionRequestAssistantMessage} instance including unrecognized properties.
   *
   * @return The map of all properties
   */
  @JsonIgnore
  @Nonnull
  public Map<String, Object> toMap()
  {
    final Map<String, Object> declaredFields = new LinkedHashMap<>(cloudSdkCustomFields);
    if( content != null ) declaredFields.put("content", content);
    if( refusal != null ) declaredFields.put("refusal", refusal);
    if( role != null ) declaredFields.put("role", role);
    if( name != null ) declaredFields.put("name", name);
    if( toolCalls != null ) declaredFields.put("toolCalls", toolCalls);
    if( functionCall != null ) declaredFields.put("functionCall", functionCall);
    return declaredFields;
  }

  /**
   * Set an unrecognizable property of this {@link ChatCompletionRequestAssistantMessage} instance. If the map previously contained a mapping
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
    final ChatCompletionRequestAssistantMessage chatCompletionRequestAssistantMessage = (ChatCompletionRequestAssistantMessage) o;
    return Objects.equals(this.cloudSdkCustomFields, chatCompletionRequestAssistantMessage.cloudSdkCustomFields) &&
        Objects.equals(this.content, chatCompletionRequestAssistantMessage.content) &&
        Objects.equals(this.refusal, chatCompletionRequestAssistantMessage.refusal) &&
        Objects.equals(this.role, chatCompletionRequestAssistantMessage.role) &&
        Objects.equals(this.name, chatCompletionRequestAssistantMessage.name) &&
        Objects.equals(this.toolCalls, chatCompletionRequestAssistantMessage.toolCalls) &&
        Objects.equals(this.functionCall, chatCompletionRequestAssistantMessage.functionCall);
  }

  @Override
  public int hashCode() {
    return Objects.hash(content, refusal, role, name, toolCalls, functionCall, cloudSdkCustomFields);
  }

  @Override
  @Nonnull public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("class ChatCompletionRequestAssistantMessage {\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    refusal: ").append(toIndentedString(refusal)).append("\n");
    sb.append("    role: ").append(toIndentedString(role)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    toolCalls: ").append(toIndentedString(toolCalls)).append("\n");
    sb.append("    functionCall: ").append(toIndentedString(functionCall)).append("\n");
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

