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
 * Soda
 */
// CHECKSTYLE:OFF
public class Soda
// CHECKSTYLE:ON
{
    @JsonProperty( "name" )
    private String name;

    @JsonProperty( "brand" )
    private String brand;

    @JsonProperty( "quantity" )
    private Integer quantity;

    /**
     * Gets or Sets packaging
     */
    public enum PackagingEnum
    {
        /**
         * The GLASS option of this Soda
         */
        GLASS("glass"),

        /**
         * The CARTON option of this Soda
         */
        CARTON("carton"),

        /**
         * The CAN option of this Soda
         */
        CAN("can"),

        /**
         * The UNKNOWN_DEFAULT_OPEN_API option of this Soda
         */
        UNKNOWN_DEFAULT_OPEN_API("unknown_default_open_api");

        private String value;

        PackagingEnum( String value )
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
         * @return The enum value of type Soda
         */
        @JsonCreator
        @Nonnull
        public static PackagingEnum fromValue( @Nonnull final String value )
        {
            for( PackagingEnum b : PackagingEnum.values() ) {
                if( b.value.equals(value) ) {
                    return b;
                }
            }
            return UNKNOWN_DEFAULT_OPEN_API;
        }
    }

    @JsonProperty( "packaging" )
    private PackagingEnum packaging;

    @JsonProperty( "price" )
    private Float price;

    @JsonAnySetter
    @JsonAnyGetter
    private final Map<String, Object> cloudSdkCustomFields = new LinkedHashMap<>();

    /**
     * Default constructor for Soda.
     */
    protected Soda()
    {
    }

    /**
     * Set the name of this {@link Soda} instance and return the same instance.
     *
     * @param name
     *            The name of this {@link Soda}
     * @return The same instance of this {@link Soda} class
     */
    @Nonnull
    public Soda name( @Nonnull final String name )
    {
        this.name = name;
        return this;
    }

    /**
     * Get name
     *
     * @return name The name of this {@link Soda} instance.
     */
    @Nonnull
    public String getName()
    {
        return name;
    }

    /**
     * Set the name of this {@link Soda} instance.
     *
     * @param name
     *            The name of this {@link Soda}
     */
    public void setName( @Nonnull final String name )
    {
        this.name = name;
    }

    /**
     * Set the brand of this {@link Soda} instance and return the same instance.
     *
     * @param brand
     *            The brand of this {@link Soda}
     * @return The same instance of this {@link Soda} class
     */
    @Nonnull
    public Soda brand( @Nonnull final String brand )
    {
        this.brand = brand;
        return this;
    }

    /**
     * Get brand
     *
     * @return brand The brand of this {@link Soda} instance.
     */
    @Nonnull
    public String getBrand()
    {
        return brand;
    }

    /**
     * Set the brand of this {@link Soda} instance.
     *
     * @param brand
     *            The brand of this {@link Soda}
     */
    public void setBrand( @Nonnull final String brand )
    {
        this.brand = brand;
    }

    /**
     * Set the quantity of this {@link Soda} instance and return the same instance.
     *
     * @param quantity
     *            The quantity of this {@link Soda}
     * @return The same instance of this {@link Soda} class
     */
    @Nonnull
    public Soda quantity( @Nonnull final Integer quantity )
    {
        this.quantity = quantity;
        return this;
    }

    /**
     * Get quantity
     *
     * @return quantity The quantity of this {@link Soda} instance.
     */
    @Nonnull
    public Integer getQuantity()
    {
        return quantity;
    }

    /**
     * Set the quantity of this {@link Soda} instance.
     *
     * @param quantity
     *            The quantity of this {@link Soda}
     */
    public void setQuantity( @Nonnull final Integer quantity )
    {
        this.quantity = quantity;
    }

    /**
     * Set the packaging of this {@link Soda} instance and return the same instance.
     *
     * @param packaging
     *            The packaging of this {@link Soda}
     * @return The same instance of this {@link Soda} class
     */
    @Nonnull
    public Soda packaging( @Nullable final PackagingEnum packaging )
    {
        this.packaging = packaging;
        return this;
    }

    /**
     * Get packaging
     *
     * @return packaging The packaging of this {@link Soda} instance.
     */
    @Nonnull
    public PackagingEnum getPackaging()
    {
        return packaging;
    }

    /**
     * Set the packaging of this {@link Soda} instance.
     *
     * @param packaging
     *            The packaging of this {@link Soda}
     */
    public void setPackaging( @Nullable final PackagingEnum packaging )
    {
        this.packaging = packaging;
    }

    /**
     * Set the price of this {@link Soda} instance and return the same instance.
     *
     * @param price
     *            The price of this {@link Soda}
     * @return The same instance of this {@link Soda} class
     */
    @Nonnull
    public Soda price( @Nonnull final Float price )
    {
        this.price = price;
        return this;
    }

    /**
     * Get price
     *
     * @return price The price of this {@link Soda} instance.
     */
    @Nonnull
    public Float getPrice()
    {
        return price;
    }

    /**
     * Set the price of this {@link Soda} instance.
     *
     * @param price
     *            The price of this {@link Soda}
     */
    public void setPrice( @Nonnull final Float price )
    {
        this.price = price;
    }

    /**
     * Get the names of the unrecognizable properties of the {@link Soda}.
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
     * Get the value of an unrecognizable property of this {@link Soda} instance.
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
            throw new NoSuchElementException("Soda has no field with name '" + name + "'.");
        }
        return cloudSdkCustomFields.get(name);
    }

    /**
     * Get the value of all properties of this {@link Soda} instance including unrecognized properties.
     *
     * @return The map of all properties
     */
    @JsonIgnore
    @Nonnull
    public Map<String, Object> toMap()
    {
        final Map<String, Object> declaredFields = new LinkedHashMap<>(cloudSdkCustomFields);
        if( name != null )
            declaredFields.put("name", name);
        if( brand != null )
            declaredFields.put("brand", brand);
        if( quantity != null )
            declaredFields.put("quantity", quantity);
        if( packaging != null )
            declaredFields.put("packaging", packaging);
        if( price != null )
            declaredFields.put("price", price);
        return declaredFields;
    }

    /**
     * Set an unrecognizable property of this {@link Soda} instance. If the map previously contained a mapping for the
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
        final Soda soda = (Soda) o;
        return Objects.equals(this.cloudSdkCustomFields, soda.cloudSdkCustomFields)
            && Objects.equals(this.name, soda.name)
            && Objects.equals(this.brand, soda.brand)
            && Objects.equals(this.quantity, soda.quantity)
            && Objects.equals(this.packaging, soda.packaging)
            && Objects.equals(this.price, soda.price);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, brand, quantity, packaging, price, cloudSdkCustomFields);
    }

    @Override
    @Nonnull
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append("class Soda {\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    brand: ").append(toIndentedString(brand)).append("\n");
        sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
        sb.append("    packaging: ").append(toIndentedString(packaging)).append("\n");
        sb.append("    price: ").append(toIndentedString(price)).append("\n");
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
     * Create a type-safe, fluent-api builder object to construct a new {@link Soda} instance with all required
     * arguments.
     */
    public static Builder create()
    {
        return ( name ) -> (
            brand ) -> ( quantity ) -> ( price ) -> new Soda().name(name).brand(brand).quantity(quantity).price(price);
    }

    /**
     * Builder helper class.
     */
    public interface Builder
    {
        /**
         * Set the name of this {@link Soda} instance.
         *
         * @param name
         *            The name of this {@link Soda}
         * @return The Soda builder.
         */
        Builder1 name( @Nonnull final String name );
    }

    /**
     * Builder helper class.
     */
    public interface Builder1
    {
        /**
         * Set the brand of this {@link Soda} instance.
         *
         * @param brand
         *            The brand of this {@link Soda}
         * @return The Soda builder.
         */
        Builder2 brand( @Nonnull final String brand );
    }

    /**
     * Builder helper class.
     */
    public interface Builder2
    {
        /**
         * Set the quantity of this {@link Soda} instance.
         *
         * @param quantity
         *            The quantity of this {@link Soda}
         * @return The Soda builder.
         */
        Builder3 quantity( @Nonnull final Integer quantity );
    }

    /**
     * Builder helper class.
     */
    public interface Builder3
    {
        /**
         * Set the price of this {@link Soda} instance.
         *
         * @param price
         *            The price of this {@link Soda}
         * @return The Soda instance.
         */
        Soda price( @Nonnull final Float price );
    }

}
