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
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Bar
 */
// CHECKSTYLE:OFF
public class Bar implements OneOfWithEnumDiscriminator
// CHECKSTYLE:ON
{
    @JsonProperty( "bar" )
    private String bar;

    /**
     * Gets or Sets disc
     */
    public enum DiscEnum
    {
        /**
         * The DISC_BAR option of this Bar
         */
        DISC_BAR("disc_bar"),

        /**
         * The UNKNOWN_DEFAULT_OPEN_API option of this Bar
         */
        UNKNOWN_DEFAULT_OPEN_API("unknown_default_open_api");

        private String value;

        DiscEnum( String value )
        {
            this.value = value;
        }

        /**
         * Get the value of the enum
         *
         * @return The enum value
         */
        @JsonValue
        @Nonnull
        public String getValue()
        {
            return value;
        }

        /**
         * Get the String value of the enum value.
         *
         * @return The enum value as String
         */
        @Override
        @Nonnull
        public String toString()
        {
            return String.valueOf(value);
        }

        /**
         * Get the enum value from a String value
         *
         * @param value
         *            The String value
         * @return The enum value of type Bar
         */
        @JsonCreator
        @Nonnull
        public static DiscEnum fromValue( @Nonnull final String value )
        {
            for( DiscEnum b : DiscEnum.values() ) {
                if( b.value.equals(value) ) {
                    return b;
                }
            }
            return UNKNOWN_DEFAULT_OPEN_API;
        }
    }

    @JsonProperty( "disc" )
    private DiscEnum disc;

    @JsonAnySetter
    @JsonAnyGetter
    private final Map<String, Object> cloudSdkCustomFields = new LinkedHashMap<>();

    /**
     * Default constructor for Bar.
     */
    protected Bar()
    {
    }

    /**
     * Set the bar of this {@link Bar} instance and return the same instance.
     *
     * @param bar
     *            The bar of this {@link Bar}
     * @return The same instance of this {@link Bar} class
     */
    @Nonnull
    public Bar bar( @Nullable final String bar )
    {
        this.bar = bar;
        return this;
    }

    /**
     * Get bar
     *
     * @return bar The bar of this {@link Bar} instance.
     */
    @Nonnull
    public String getBar()
    {
        return bar;
    }

    /**
     * Set the bar of this {@link Bar} instance.
     *
     * @param bar
     *            The bar of this {@link Bar}
     */
    public void setBar( @Nullable final String bar )
    {
        this.bar = bar;
    }

    /**
     * Set the disc of this {@link Bar} instance and return the same instance.
     *
     * @param disc
     *            The disc of this {@link Bar}
     * @return The same instance of this {@link Bar} class
     */
    @Nonnull
    public Bar disc( @Nullable final DiscEnum disc )
    {
        this.disc = disc;
        return this;
    }

    /**
     * Get disc
     *
     * @return disc The disc of this {@link Bar} instance.
     */
    @Nonnull
    public DiscEnum getDisc()
    {
        return disc;
    }

    /**
     * Set the disc of this {@link Bar} instance.
     *
     * @param disc
     *            The disc of this {@link Bar}
     */
    public void setDisc( @Nullable final DiscEnum disc )
    {
        this.disc = disc;
    }

    /**
     * Get the names of the unrecognizable properties of the {@link Bar}.
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
     * Get the value of an unrecognizable property of this {@link Bar} instance.
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
            throw new NoSuchElementException("Bar has no field with name '" + name + "'.");
        }
        return cloudSdkCustomFields.get(name);
    }

    /**
     * Get the value of all properties of this {@link Bar} instance including unrecognized properties.
     *
     * @return The map of all properties
     */
    @JsonIgnore
    @Nonnull
    public Map<String, Object> toMap()
    {
        final Map<String, Object> declaredFields = new LinkedHashMap<>(cloudSdkCustomFields);
        if( bar != null )
            declaredFields.put("bar", bar);
        if( disc != null )
            declaredFields.put("disc", disc);
        return declaredFields;
    }

    /**
     * Set an unrecognizable property of this {@link Bar} instance. If the map previously contained a mapping for the
     * key, the old value is replaced by the specified value.
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
        final Bar bar = (Bar) o;
        return Objects.equals(this.cloudSdkCustomFields, bar.cloudSdkCustomFields)
            && Objects.equals(this.bar, bar.bar)
            && Objects.equals(this.disc, bar.disc);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(bar, disc, cloudSdkCustomFields);
    }

    @Override
    @Nonnull
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append("class Bar {\n");
        sb.append("    bar: ").append(toIndentedString(bar)).append("\n");
        sb.append("    disc: ").append(toIndentedString(disc)).append("\n");
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
     * Create a new {@link Bar} instance. No arguments are required.
     */
    public static Bar create()
    {
        return new Bar();
    }

}
