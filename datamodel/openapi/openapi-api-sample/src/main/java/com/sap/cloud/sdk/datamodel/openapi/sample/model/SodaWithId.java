/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

/*
 * SodaStore API
 * API for managing soda products and orders in SodaStore.
 *
 * The version of the OpenAPI document: 1.0.0
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
 * SodaWithId
 */
// CHECKSTYLE:OFF
public class SodaWithId
// CHECKSTYLE:ON
{
    @JsonProperty( "name" )
    private String name;

    @JsonProperty( "brand" )
    private String brand;

    @JsonProperty( "quantity" )
    private Integer quantity;

    @JsonProperty( "price" )
    private Float price;

    @JsonProperty( "id" )
    private Long id;

    @JsonAnySetter
    @JsonAnyGetter
    private final Map<String, Object> cloudSdkCustomFields = new LinkedHashMap<>();

    protected SodaWithId()
    {
    }

    /**
     * Set the name of this {@link SodaWithId} instance and return the same instance.
     *
     * @param name
     *            The name of this {@link SodaWithId}
     * @return The same instance of this {@link SodaWithId} class
     */
    @Nonnull
    public SodaWithId name( @Nonnull final String name )
    {
        this.name = name;
        return this;
    }

    /**
     * Get name
     *
     * @return name The name of this {@link SodaWithId} instance.
     */
    @Nonnull
    public String getName()
    {
        return name;
    }

    /**
     * Set the name of this {@link SodaWithId} instance.
     *
     * @param name
     *            The name of this {@link SodaWithId}
     */
    public void setName( @Nonnull final String name )
    {
        this.name = name;
    }

    /**
     * Set the brand of this {@link SodaWithId} instance and return the same instance.
     *
     * @param brand
     *            The brand of this {@link SodaWithId}
     * @return The same instance of this {@link SodaWithId} class
     */
    @Nonnull
    public SodaWithId brand( @Nonnull final String brand )
    {
        this.brand = brand;
        return this;
    }

    /**
     * Get brand
     *
     * @return brand The brand of this {@link SodaWithId} instance.
     */
    @Nonnull
    public String getBrand()
    {
        return brand;
    }

    /**
     * Set the brand of this {@link SodaWithId} instance.
     *
     * @param brand
     *            The brand of this {@link SodaWithId}
     */
    public void setBrand( @Nonnull final String brand )
    {
        this.brand = brand;
    }

    /**
     * Set the quantity of this {@link SodaWithId} instance and return the same instance.
     *
     * @param quantity
     *            The quantity of this {@link SodaWithId}
     * @return The same instance of this {@link SodaWithId} class
     */
    @Nonnull
    public SodaWithId quantity( @Nonnull final Integer quantity )
    {
        this.quantity = quantity;
        return this;
    }

    /**
     * Get quantity
     *
     * @return quantity The quantity of this {@link SodaWithId} instance.
     */
    @Nonnull
    public Integer getQuantity()
    {
        return quantity;
    }

    /**
     * Set the quantity of this {@link SodaWithId} instance.
     *
     * @param quantity
     *            The quantity of this {@link SodaWithId}
     */
    public void setQuantity( @Nonnull final Integer quantity )
    {
        this.quantity = quantity;
    }

    /**
     * Set the price of this {@link SodaWithId} instance and return the same instance.
     *
     * @param price
     *            The price of this {@link SodaWithId}
     * @return The same instance of this {@link SodaWithId} class
     */
    @Nonnull
    public SodaWithId price( @Nonnull final Float price )
    {
        this.price = price;
        return this;
    }

    /**
     * Get price
     *
     * @return price The price of this {@link SodaWithId} instance.
     */
    @Nonnull
    public Float getPrice()
    {
        return price;
    }

    /**
     * Set the price of this {@link SodaWithId} instance.
     *
     * @param price
     *            The price of this {@link SodaWithId}
     */
    public void setPrice( @Nonnull final Float price )
    {
        this.price = price;
    }

    /**
     * Set the id of this {@link SodaWithId} instance and return the same instance.
     *
     * @param id
     *            The id of this {@link SodaWithId}
     * @return The same instance of this {@link SodaWithId} class
     */
    @Nonnull
    public SodaWithId id( @Nonnull final Long id )
    {
        this.id = id;
        return this;
    }

    /**
     * Get id
     *
     * @return id The id of this {@link SodaWithId} instance.
     */
    @Nonnull
    public Long getId()
    {
        return id;
    }

    /**
     * Set the id of this {@link SodaWithId} instance.
     *
     * @param id
     *            The id of this {@link SodaWithId}
     */
    public void setId( @Nonnull final Long id )
    {
        this.id = id;
    }

    /**
     * Get the names of the unrecognizable properties of the {@link SodaWithId}.
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
     * Get the value of an unrecognizable property of this {@link SodaWithId} instance.
     *
     * @param name
     *            The name of the property
     * @return The value of the property
     * @throws NoSuchElementException
     *             If no property with the given name could be found.
     */
    @Nullable
    public Object getCustomField( @Nonnull final String name )
        throws NoSuchElementException
    {
        if( !cloudSdkCustomFields.containsKey(name) ) {
            throw new NoSuchElementException("SodaWithId has no field with name '" + name + "'.");
        }
        return cloudSdkCustomFields.get(name);
    }

    /**
     * Set an unrecognizable property of this {@link SodaWithId} instance. If the map previously contained a mapping for
     * the key, the old value is replaced by the specified value.
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
        final SodaWithId sodaWithId = (SodaWithId) o;
        return Objects.equals(this.cloudSdkCustomFields, sodaWithId.cloudSdkCustomFields)
            && Objects.equals(this.name, sodaWithId.name)
            && Objects.equals(this.brand, sodaWithId.brand)
            && Objects.equals(this.quantity, sodaWithId.quantity)
            && Objects.equals(this.price, sodaWithId.price)
            && Objects.equals(this.id, sodaWithId.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, brand, quantity, price, id, cloudSdkCustomFields);
    }

    @Override
    @Nonnull
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append("class SodaWithId {\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    brand: ").append(toIndentedString(brand)).append("\n");
        sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
        sb.append("    price: ").append(toIndentedString(price)).append("\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
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
     * Create a type-safe, fluent-api builder object to construct a new {@link SodaWithId} instance with all required
     * arguments.
     */
    public static Builder create()
    {
        return ( name ) -> ( brand ) -> (
            quantity ) -> ( price ) -> new SodaWithId().name(name).brand(brand).quantity(quantity).price(price);
    }

    /**
     * Builder helper class.
     */
    public interface Builder
    {
        /**
         * Set the name of this {@link SodaWithId} instance.
         *
         * @param name
         *            The name of this {@link SodaWithId}
         * @return The SodaWithId builder.
         */
        Builder1 name( @Nonnull final String name );
    }

    /**
     * Builder helper class.
     */
    public interface Builder1
    {
        /**
         * Set the brand of this {@link SodaWithId} instance.
         *
         * @param brand
         *            The brand of this {@link SodaWithId}
         * @return The SodaWithId builder.
         */
        Builder2 brand( @Nonnull final String brand );
    }

    /**
     * Builder helper class.
     */
    public interface Builder2
    {
        /**
         * Set the quantity of this {@link SodaWithId} instance.
         *
         * @param quantity
         *            The quantity of this {@link SodaWithId}
         * @return The SodaWithId builder.
         */
        Builder3 quantity( @Nonnull final Integer quantity );
    }

    /**
     * Builder helper class.
     */
    public interface Builder3
    {
        /**
         * Set the price of this {@link SodaWithId} instance.
         *
         * @param price
         *            The price of this {@link SodaWithId}
         * @return The SodaWithId instance.
         */
        SodaWithId price( @Nonnull final Float price );
    }

}
