/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Maps;
import com.google.gson.annotations.JsonAdapter;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataEntityKey;
import com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEntity;
import com.sap.cloud.sdk.result.ElementName;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * Original entity name from the Odata EDM: <b>Vendor</b>
 * </p>
 *
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString( doNotUseGetters = true, callSuper = true )
@EqualsAndHashCode( doNotUseGetters = true, callSuper = true )
@JsonAdapter( com.sap.cloud.sdk.datamodel.odatav4.adapter.GsonVdmAdapterFactory.class )
@JsonSerialize( using = com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectSerializer.class )
@JsonDeserialize( using = com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectDeserializer.class )
public class Vendor extends VdmEntity<Vendor>
{

    @Getter
    private final java.lang.String odataType = "com.sap.cloud.sdk.store.grocery.Vendor";
    /**
     * Selector for all available fields of Vendor.
     *
     */
    public final static SimpleProperty<Vendor> ALL_FIELDS = all();
    /**
     * (Key Field) Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>Id</b>
     * </p>
     *
     * @return The id contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName( "Id" )
    private Integer id;
    public final static SimpleProperty.NumericInteger<Vendor> ID =
        new SimpleProperty.NumericInteger<Vendor>(Vendor.class, "Id");
    /**
     * Constraints: Nullable, Maximum length: 100
     * <p>
     * Original property name from the Odata EDM: <b>Name</b>
     * </p>
     *
     * @return The name contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName( "Name" )
    private java.lang.String name;
    public final static SimpleProperty.String<Vendor> NAME = new SimpleProperty.String<Vendor>(Vendor.class, "Name");
    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>AddressId</b>
     * </p>
     *
     * @return The addressId contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName( "AddressId" )
    private Integer addressId;
    public final static SimpleProperty.NumericInteger<Vendor> ADDRESS_ID =
        new SimpleProperty.NumericInteger<Vendor>(Vendor.class, "AddressId");
    /**
     * Navigation property <b>Address</b> for <b>Vendor</b> to single <b>Address</b>.
     *
     */
    @ElementName( "Address" )
    @Nullable
    @Getter( AccessLevel.NONE )
    @Setter( AccessLevel.NONE )
    private Address toAddress;
    /**
     * Use with available request builders to apply the <b>Address</b> navigation property to query operations.
     *
     */
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Single<Vendor, Address> TO_ADDRESS =
        new com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Single<Vendor, Address>(
            Vendor.class,
            "Address",
            Address.class);

    @Nonnull
    @Override
    public Class<Vendor> getType()
    {
        return Vendor.class;
    }

    /**
     * (Key Field) Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>Id</b>
     * </p>
     *
     * @param id
     *            The id to set.
     */
    public void setId( @Nullable final Integer id )
    {
        rememberChangedField("Id", this.id);
        this.id = id;
    }

    /**
     * Constraints: Nullable, Maximum length: 100
     * <p>
     * Original property name from the Odata EDM: <b>Name</b>
     * </p>
     *
     * @param name
     *            The name to set.
     */
    public void setName( @Nullable final java.lang.String name )
    {
        rememberChangedField("Name", this.name);
        this.name = name;
    }

    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>AddressId</b>
     * </p>
     *
     * @param addressId
     *            The addressId to set.
     */
    public void setAddressId( @Nullable final Integer addressId )
    {
        rememberChangedField("AddressId", this.addressId);
        this.addressId = addressId;
    }

    @Override
    protected java.lang.String getEntityCollection()
    {
        return "Vendor";
    }

    @Nonnull
    @Override
    protected ODataEntityKey getKey()
    {
        final ODataEntityKey entityKey = super.getKey();
        entityKey.addKeyProperty("Id", getId());
        return entityKey;
    }

    @Nonnull
    @Override
    protected Map<java.lang.String, Object> toMapOfFields()
    {
        final Map<java.lang.String, Object> cloudSdkValues = super.toMapOfFields();
        cloudSdkValues.put("Id", getId());
        cloudSdkValues.put("Name", getName());
        cloudSdkValues.put("AddressId", getAddressId());
        return cloudSdkValues;
    }

    @Override
    protected void fromMap( final Map<java.lang.String, Object> inputValues )
    {
        final Map<java.lang.String, Object> cloudSdkValues = Maps.newLinkedHashMap(inputValues);
        // simple properties
        {
            if( cloudSdkValues.containsKey("Id") ) {
                final Object value = cloudSdkValues.remove("Id");
                if( (value == null) || (!value.equals(getId())) ) {
                    setId(((Integer) value));
                }
            }
            if( cloudSdkValues.containsKey("Name") ) {
                final Object value = cloudSdkValues.remove("Name");
                if( (value == null) || (!value.equals(getName())) ) {
                    setName(((java.lang.String) value));
                }
            }
            if( cloudSdkValues.containsKey("AddressId") ) {
                final Object value = cloudSdkValues.remove("AddressId");
                if( (value == null) || (!value.equals(getAddressId())) ) {
                    setAddressId(((Integer) value));
                }
            }
        }
        // structured properties
        {
        }
        // navigation properties
        {
            if( (cloudSdkValues).containsKey("Address") ) {
                final Object cloudSdkValue = (cloudSdkValues).remove("Address");
                if( cloudSdkValue instanceof Map ) {
                    if( toAddress == null ) {
                        toAddress = new Address();
                    }
                    @SuppressWarnings( "unchecked" )
                    final Map<java.lang.String, Object> inputMap = ((Map<java.lang.String, Object>) cloudSdkValue);
                    toAddress.fromMap(inputMap);
                }
            }
        }
        super.fromMap(cloudSdkValues);
    }

    @Nonnull
    @Override
    protected Map<java.lang.String, Object> toMapOfNavigationProperties()
    {
        final Map<java.lang.String, Object> cloudSdkValues = super.toMapOfNavigationProperties();
        if( toAddress != null ) {
            (cloudSdkValues).put("Address", toAddress);
        }
        return cloudSdkValues;
    }

    /**
     * Retrieval of associated <b>Address</b> entity (one to one). This corresponds to the OData navigation property
     * <b>Address</b>.
     * <p>
     * If the navigation property for an entity <b>Vendor</b> has not been resolved yet, this method will <b>not
     * query</b> further information. Instead its <code>Option</code> result state will be <code>empty</code>.
     *
     * @return If the information for navigation property <b>Address</b> is already loaded, the result will contain the
     *         <b>Address</b> entity. If not, an <code>Option</code> with result state <code>empty</code> is returned.
     */
    @Nonnull
    public Option<Address> getAddressIfPresent()
    {
        return Option.of(toAddress);
    }

    /**
     * Overwrites the associated <b>Address</b> entity for the loaded navigation property <b>Address</b>.
     *
     * @param cloudSdkValue
     *            New <b>Address</b> entity.
     */
    public void setAddress( final Address cloudSdkValue )
    {
        toAddress = cloudSdkValue;
    }

    /**
     * Helper class to allow for fluent creation of Vendor instances.
     *
     */
    public final static class VendorBuilder
    {

        private Address toAddress;

        private Vendor.VendorBuilder toAddress( final Address cloudSdkValue )
        {
            toAddress = cloudSdkValue;
            return this;
        }

        /**
         * Navigation property <b>Address</b> for <b>Vendor</b> to single <b>Address</b>.
         *
         * @param cloudSdkValue
         *            The Address to build this Vendor with.
         * @return This Builder to allow for a fluent interface.
         */
        @Nonnull
        public Vendor.VendorBuilder address( final Address cloudSdkValue )
        {
            return toAddress(cloudSdkValue);
        }

    }

}
