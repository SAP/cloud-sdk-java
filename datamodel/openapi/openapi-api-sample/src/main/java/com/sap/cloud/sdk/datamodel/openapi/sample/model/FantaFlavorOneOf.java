/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

/*
 * SodaStore API
 * API for managing soda products and orders in SodaStore.
 *
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package com.sap.cloud.sdk.datamodel.openapi.sample.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FantaFlavorOneOf
 */
// CHECKSTYLE:OFF
public class FantaFlavorOneOf
// CHECKSTYLE:ON
{
    @JsonProperty( "intensity" )
    private Integer intensity;

    @JsonProperty( "nuance" )
    private String nuance;

    @JsonAnySetter
    @JsonAnyGetter
    private final Map<String, Object> cloudSdkCustomFields = new LinkedHashMap<>();

    /**
     * Default constructor for FantaFlavorOneOf.
     */
    protected FantaFlavorOneOf()
    {
    }

    /**
     * Set the intensity of this {@link FantaFlavorOneOf} instance and return the same instance.
     *
     * @param intensity
     *            The intensity of this {@link FantaFlavorOneOf}
     * @return The same instance of this {@link FantaFlavorOneOf} class
     */
    @Nonnull
    public FantaFlavorOneOf intensity( @Nullable final Integer intensity )
    {
        this.intensity = intensity;
        return this;
    }

    /**
     * Get intensity
     *
     * @return intensity The intensity of this {@link FantaFlavorOneOf} instance.
     */
    @Nonnull
    public Integer getIntensity()
    {
        return intensity;
    }

    /**
     * Set the intensity of this {@link FantaFlavorOneOf} instance.
     *
     * @param intensity
     *            The intensity of this {@link FantaFlavorOneOf}
     */
    public void setIntensity( @Nullable final Integer intensity )
    {
        this.intensity = intensity;
    }

    /**
     * Set the nuance of this {@link FantaFlavorOneOf} instance and return the same instance.
     *
     * @param nuance
     *            The nuance of this {@link FantaFlavorOneOf}
     * @return The same instance of this {@link FantaFlavorOneOf} class
     */
    @Nonnull
    public FantaFlavorOneOf nuance( @Nullable final String nuance )
    {
        this.nuance = nuance;
        return this;
    }

    /**
     * Get nuance
     *
     * @return nuance The nuance of this {@link FantaFlavorOneOf} instance.
     */
    @Nonnull
    public String getNuance()
    {
        return nuance;
    }

    /**
     * Set the nuance of this {@link FantaFlavorOneOf} instance.
     *
     * @param nuance
     *            The nuance of this {@link FantaFlavorOneOf}
     */
    public void setNuance( @Nullable final String nuance )
    {
        this.nuance = nuance;
    }

    /**
     * Get the names of the unrecognizable properties of the {@link FantaFlavorOneOf}.
     *
     * @return The set of properties names
     */
    @JsonIgnore
    @Nonnull
    public Set<String> getCustomFieldNames()
    {
        return cloudSdkCustomFields.keySet();
    }

    /**
     * Get the value of an unrecognizable property of this {@link FantaFlavorOneOf} instance.
     *
     * @deprecated Use {@link #toMap()} instead.
     * @param name
     *            The name of the property
     * @return The value of the property
     * @throws NoSuchElementException
     *             If no property with the given name could be found.
     */
    @Nullable
    @Deprecated
    public Object getCustomField( @Nonnull final String name )
        throws NoSuchElementException
    {
        if( !cloudSdkCustomFields.containsKey(name) ) {
            throw new NoSuchElementException("FantaFlavorOneOf has no field with name '" + name + "'.");
        }
        return cloudSdkCustomFields.get(name);
    }

    /**
     * Get the value of all properties of this {@link FantaFlavorOneOf} instance including unrecognized properties.
     *
     * @return The map of all properties
     */
    @JsonIgnore
    @Nonnull
    public Map<String, Object> toMap()
    {
        final Map<String, Object> declaredFields = new LinkedHashMap<>(cloudSdkCustomFields);
        if( intensity != null )
            declaredFields.put("intensity", intensity);
        if( nuance != null )
            declaredFields.put("nuance", nuance);
        return declaredFields;
    }

    /**
     * Set an unrecognizable property of this {@link FantaFlavorOneOf} instance. If the map previously contained a
     * mapping for the key, the old value is replaced by the specified value.
     *
     * @param customFieldName
     *            The name of the property
     * @param customFieldValue
     *            The value of the property
     */
    @JsonIgnore
    public void setCustomField( @Nonnull String customFieldName, @Nullable Object customFieldValue )
    {
        cloudSdkCustomFields.put(customFieldName, customFieldValue);
    }

    @Override
    public boolean equals( @Nullable final java.lang.Object o )
    {
        if( this == o ) {
            return true;
        }
        if( o == null || getClass() != o.getClass() ) {
            return false;
        }
        final FantaFlavorOneOf fantaFlavorOneOf = (FantaFlavorOneOf) o;
        return Objects.equals(this.cloudSdkCustomFields, fantaFlavorOneOf.cloudSdkCustomFields)
            && Objects.equals(this.intensity, fantaFlavorOneOf.intensity)
            && Objects.equals(this.nuance, fantaFlavorOneOf.nuance);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(intensity, nuance, cloudSdkCustomFields);
    }

    @Override
    @Nonnull
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append("class FantaFlavorOneOf {\n");
        sb.append("    intensity: ").append(toIndentedString(intensity)).append("\n");
        sb.append("    nuance: ").append(toIndentedString(nuance)).append("\n");
        cloudSdkCustomFields
            .forEach(( k, v ) -> sb.append("    ").append(k).append(": ").append(toIndentedString(v)).append("\n"));
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces (except the first line).
     */
    private String toIndentedString( final java.lang.Object o )
    {
        if( o == null ) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

    /**
     * Create a new {@link FantaFlavorOneOf} instance. No arguments are required.
     */
    public static FantaFlavorOneOf create()
    {
        return new FantaFlavorOneOf();
    }

}
