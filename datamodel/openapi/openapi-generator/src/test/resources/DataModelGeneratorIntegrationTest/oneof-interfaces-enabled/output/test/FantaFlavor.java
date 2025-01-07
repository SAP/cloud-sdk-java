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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;
import test.Cola;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * FantaFlavor
 */
public interface FantaFlavor  {
    record InnerCola(@com.fasterxml.jackson.annotation.JsonValue Cola value) implements FantaFlavor {}

    @com.fasterxml.jackson.annotation.JsonCreator
    static InnerCola create( Cola val) { return new InnerCola(val); }

    record InnerString(@com.fasterxml.jackson.annotation.JsonValue String value) implements FantaFlavor {}

    @com.fasterxml.jackson.annotation.JsonCreator
    static InnerString create( String val) { return new InnerString(val); }

    record InnerStrings(@com.fasterxml.jackson.annotation.JsonValue List<String> values) implements FantaFlavor {}

    @com.fasterxml.jackson.annotation.JsonCreator
    static InnerStrings create( List<String> val) { return new InnerStrings(val); }

}

