/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataException;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmEntity;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.field.CustomerField;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.link.CustomerOneToOneLink;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.selectable.CustomerSelectable;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataField;
import com.sap.cloud.sdk.s4hana.datamodel.odata.annotation.Key;
import com.sap.cloud.sdk.typeconverter.TypeConverter;

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
 * OData entity representation of Customer
 * <p>
 * </p>
 * <p>
 * Original entity name from the Odata EDM: <b>Customer</b>
 * </p>
 *
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString( doNotUseGetters = true, callSuper = true )
@EqualsAndHashCode( doNotUseGetters = true, callSuper = true )
@JsonAdapter( com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory.class )
public class Customer extends VdmEntity<Customer>
{

    /**
     * Selector for all available fields of Customer.
     *
     */
    public final static CustomerSelectable ALL_FIELDS = () -> "*";
    /**
     * (Key Field) Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>Id</b>
     * </p>
     * <p>
     * With this numeric identifier it's possible to resolve and manipulate customer information
     * </p>
     *
     * @return Customer identifier used as entity key value.
     */
    @Key
    @SerializedName( "Id" )
    @JsonProperty( "Id" )
    @Nullable
    @ODataField( odataName = "Id" )
    private Integer id;
    /**
     * Use with available fluent helpers to apply the <b>Id</b> field to query operations.
     *
     */
    public final static CustomerField<Integer> ID = new CustomerField<Integer>("Id");
    /**
     * Constraints: Not nullable, Maximum length: 100
     * <p>
     * Original property name from the Odata EDM: <b>Name</b>
     * </p>
     * <p>
     * The mandatory field may contain different combinations of first- and last-name.
     * </p>
     *
     * @return Customer name.
     */
    @SerializedName( "Name" )
    @JsonProperty( "Name" )
    @Nullable
    @ODataField( odataName = "Name" )
    private String name;
    /**
     * Use with available fluent helpers to apply the <b>Name</b> field to query operations.
     *
     */
    public final static CustomerField<String> NAME = new CustomerField<String>("Name");
    /**
     * Constraints: none
     * <p>
     * Original property name from the Odata EDM: <b>Email</b>
     * </p>
     * <p>
     * The optional field for customer email address is checked server-side for validity.
     * </p>
     *
     * @return Customer email address.
     */
    @SerializedName( "Email" )
    @JsonProperty( "Email" )
    @Nullable
    @ODataField( odataName = "Email" )
    private String email;
    /**
     * Use with available fluent helpers to apply the <b>Email</b> field to query operations.
     *
     */
    public final static CustomerField<String> EMAIL = new CustomerField<String>("Email");
    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>AddressId</b>
     * </p>
     * <p>
     * The optional field can be used to resolve the current customer address id.
     * </p>
     *
     * @return Customer address identifier.
     */
    @SerializedName( "AddressId" )
    @JsonProperty( "AddressId" )
    @Nullable
    @ODataField( odataName = "AddressId" )
    private Integer addressId;
    /**
     * Use with available fluent helpers to apply the <b>AddressId</b> field to query operations.
     *
     */
    public final static CustomerField<Integer> ADDRESS_ID = new CustomerField<Integer>("AddressId");
    /**
     * Navigation property <b>Address</b> for <b>Customer</b> to single <b>Address</b>.
     *
     */
    @SerializedName( "Address" )
    @JsonProperty( "Address" )
    @ODataField( odataName = "Address" )
    @Nullable
    @Getter( AccessLevel.NONE )
    @Setter( AccessLevel.NONE )
    private Address toAddress;
    /**
     * Use with available fluent helpers to apply the <b>Address</b> navigation property to query operations.
     *
     */
    public final static CustomerOneToOneLink<Address> TO_ADDRESS = new CustomerOneToOneLink<Address>("Address");

    @Nonnull
    @Override
    public Class<Customer> getType()
    {
        return Customer.class;
    }

    /**
     * (Key Field) Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>Id</b>
     * </p>
     * <p>
     * With this numeric identifier it's possible to resolve and manipulate customer information
     * </p>
     *
     * @param id
     *            Customer identifier used as entity key value.
     */
    public void setId( @Nullable final Integer id )
    {
        rememberChangedField("Id", this.id);
        this.id = id;
    }

    /**
     * Constraints: Not nullable, Maximum length: 100
     * <p>
     * Original property name from the Odata EDM: <b>Name</b>
     * </p>
     * <p>
     * The mandatory field may contain different combinations of first- and last-name.
     * </p>
     *
     * @param name
     *            Customer name.
     */
    public void setName( @Nullable final String name )
    {
        rememberChangedField("Name", this.name);
        this.name = name;
    }

    /**
     * Constraints: none
     * <p>
     * Original property name from the Odata EDM: <b>Email</b>
     * </p>
     * <p>
     * The optional field for customer email address is checked server-side for validity.
     * </p>
     *
     * @param email
     *            Customer email address.
     */
    public void setEmail( @Nullable final String email )
    {
        rememberChangedField("Email", this.email);
        this.email = email;
    }

    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>AddressId</b>
     * </p>
     * <p>
     * The optional field can be used to resolve the current customer address id.
     * </p>
     *
     * @param addressId
     *            Customer address identifier.
     */
    public void setAddressId( @Nullable final Integer addressId )
    {
        rememberChangedField("AddressId", this.addressId);
        this.addressId = addressId;
    }

    @Override
    protected String getEntityCollection()
    {
        return "Customers";
    }

    @Nonnull
    @Override
    protected Map<String, Object> getKey()
    {
        final Map<String, Object> result = Maps.newHashMap();
        result.put("Id", getId());
        return result;
    }

    @Nonnull
    @Override
    protected Map<String, Object> toMapOfFields()
    {
        final Map<String, Object> values = super.toMapOfFields();
        values.put("Id", getId());
        values.put("Name", getName());
        values.put("Email", getEmail());
        values.put("AddressId", getAddressId());
        return values;
    }

    @Override
    protected void fromMap( final Map<String, Object> inputValues )
    {
        final Map<String, Object> values = Maps.newHashMap(inputValues);
        // simple properties
        {
            if( values.containsKey("Id") ) {
                final Object value = values.remove("Id");
                if( (value == null) || (!value.equals(getId())) ) {
                    setId(((Integer) value));
                }
            }
            if( values.containsKey("Name") ) {
                final Object value = values.remove("Name");
                if( (value == null) || (!value.equals(getName())) ) {
                    setName(((String) value));
                }
            }
            if( values.containsKey("Email") ) {
                final Object value = values.remove("Email");
                if( (value == null) || (!value.equals(getEmail())) ) {
                    setEmail(((String) value));
                }
            }
            if( values.containsKey("AddressId") ) {
                final Object value = values.remove("AddressId");
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
            if( (values).containsKey("Address") ) {
                final Object value = (values).remove("Address");
                if( value instanceof Map ) {
                    if( toAddress == null ) {
                        toAddress = new Address();
                    }
                    @SuppressWarnings( "unchecked" )
                    final Map<String, Object> inputMap = ((Map<String, Object>) value);
                    toAddress.fromMap(inputMap);
                }
            }
        }
        super.fromMap(values);
    }

    /**
     * Use with available fluent helpers to apply an extension field to query operations.
     *
     * @param fieldName
     *            The name of the extension field as returned by the OData service.
     * @param <T>
     *            The type of the extension field when performing value comparisons.
     * @param fieldType
     *            The Java type to use for the extension field when performing value comparisons.
     * @return A representation of an extension field from this entity.
     */
    @Nonnull
    public static <T> CustomerField<T> field( @Nonnull final String fieldName, @Nonnull final Class<T> fieldType )
    {
        return new CustomerField<T>(fieldName);
    }

    /**
     * Use with available fluent helpers to apply an extension field to query operations.
     *
     * @param typeConverter
     *            A TypeConverter<T, DomainT> instance whose first generic type matches the Java type of the field
     * @param fieldName
     *            The name of the extension field as returned by the OData service.
     * @param <T>
     *            The type of the extension field when performing value comparisons.
     * @param <DomainT>
     *            The type of the extension field as returned by the OData service.
     * @return A representation of an extension field from this entity, holding a reference to the given TypeConverter.
     */
    @Nonnull
    public static <T, DomainT> CustomerField<T> field(
        @Nonnull final String fieldName,
        @Nonnull final TypeConverter<T, DomainT> typeConverter )
    {
        return new CustomerField<T>(fieldName, typeConverter);
    }

    @Override
    @Nullable
    public Destination getDestinationForFetch()
    {
        return super.getDestinationForFetch();
    }

    @Override
    protected void setServicePathForFetch( @Nullable final String servicePathForFetch )
    {
        super.setServicePathForFetch(servicePathForFetch);
    }

    @Override
    public void attachToService( @Nullable final String servicePath, @Nonnull final Destination destination )
    {
        super.attachToService(servicePath, destination);
    }

    @Override
    protected String getDefaultServicePath()
    {
        return (com.sap.cloud.sdk.datamodel.odata.sample.services.SdkGroceryStoreService.DEFAULT_SERVICE_PATH);
    }

    @Nonnull
    @Override
    protected Map<String, Object> toMapOfNavigationProperties()
    {
        final Map<String, Object> values = super.toMapOfNavigationProperties();
        if( toAddress != null ) {
            (values).put("Address", toAddress);
        }
        return values;
    }

    /**
     * Fetches the <b>Address</b> entity (one to one) associated with this entity. This corresponds to the OData
     * navigation property <b>Address</b>.
     * <p>
     * Please note: This method will not cache or persist the query results.
     *
     * @return The single associated <b>Address</b> entity, or {@code null} if an entity is not associated.
     * @throws ODataException
     *             If the entity is unmanaged, i.e. it has not been retrieved using the OData VDM's services and
     *             therefore has no ERP configuration context assigned. An entity is managed if it has been either
     *             retrieved using the VDM's services or returned from the VDM's services as the result of a CREATE or
     *             UPDATE call.
     */
    @Nullable
    public Address fetchAddress()
    {
        return fetchFieldAsSingle("Address", Address.class);
    }

    /**
     * Retrieval of associated <b>Address</b> entity (one to one). This corresponds to the OData navigation property
     * <b>Address</b>.
     * <p>
     * If the navigation property <b>Address</b> of a queried <b>Customer</b> is operated lazily, an
     * <b>ODataException</b> can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and
     * persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the
     * first time and it has not yet been loaded, an OData query will be run in order to load the missing information
     * and its result will get cached for future invocations.
     *
     * @return List of associated <b>Address</b> entity.
     * @throws ODataException
     *             If the entity is unmanaged, i.e. it has not been retrieved using the OData VDM's services and
     *             therefore has no ERP configuration context assigned. An entity is managed if it has been either
     *             retrieved using the VDM's services or returned from the VDM's services as the result of a CREATE or
     *             UPDATE call.
     */
    @Nullable
    public Address getAddressOrFetch()
    {
        if( toAddress == null ) {
            toAddress = fetchAddress();
        }
        return toAddress;
    }

    /**
     * Retrieval of associated <b>Address</b> entity (one to one). This corresponds to the OData navigation property
     * <b>Address</b>.
     * <p>
     * If the navigation property for an entity <b>Customer</b> has not been resolved yet, this method will <b>not
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
     * @param value
     *            New <b>Address</b> entity.
     */
    public void setAddress( final Address value )
    {
        toAddress = value;
    }

    /**
     * Helper class to allow for fluent creation of Customer instances.
     *
     */
    public final static class CustomerBuilder
    {

        private Address toAddress;

        private Customer.CustomerBuilder toAddress( final Address value )
        {
            toAddress = value;
            return this;
        }

        /**
         * Navigation property <b>Address</b> for <b>Customer</b> to single <b>Address</b>.
         *
         * @param value
         *            The Address to build this Customer with.
         * @return This Builder to allow for a fluent interface.
         */
        @Nonnull
        public Customer.CustomerBuilder address( final Address value )
        {
            return toAddress(value);
        }

    }

}
