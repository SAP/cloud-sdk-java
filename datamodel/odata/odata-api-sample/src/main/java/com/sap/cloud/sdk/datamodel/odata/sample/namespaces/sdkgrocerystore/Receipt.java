/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore;

import java.math.BigDecimal;
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
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.field.ReceiptField;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.link.ReceiptOneToOneLink;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.selectable.ReceiptSelectable;
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
 * <p>
 * Original entity name from the Odata EDM: <b>Receipt</b>
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
public class Receipt extends VdmEntity<Receipt>
{

    /**
     * Selector for all available fields of Receipt.
     *
     */
    public final static ReceiptSelectable ALL_FIELDS = () -> "*";
    /**
     * (Key Field) Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>Id</b>
     * </p>
     *
     * @return The id contained in this entity.
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
    public final static ReceiptField<Integer> ID = new ReceiptField<Integer>("Id");
    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>CustomerId</b>
     * </p>
     *
     * @return The customerId contained in this entity.
     */
    @SerializedName( "CustomerId" )
    @JsonProperty( "CustomerId" )
    @Nullable
    @ODataField( odataName = "CustomerId" )
    private Integer customerId;
    /**
     * Use with available fluent helpers to apply the <b>CustomerId</b> field to query operations.
     *
     */
    public final static ReceiptField<Integer> CUSTOMER_ID = new ReceiptField<Integer>("CustomerId");
    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>TotalAmount</b>
     * </p>
     *
     * @return The totalAmount contained in this entity.
     */
    @SerializedName( "TotalAmount" )
    @JsonProperty( "TotalAmount" )
    @Nullable
    @ODataField( odataName = "TotalAmount" )
    private BigDecimal totalAmount;
    /**
     * Use with available fluent helpers to apply the <b>TotalAmount</b> field to query operations.
     *
     */
    public final static ReceiptField<BigDecimal> TOTAL_AMOUNT = new ReceiptField<BigDecimal>("TotalAmount");
    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>ProductCount1</b>
     * </p>
     *
     * @return The productCount1 contained in this entity.
     */
    @SerializedName( "ProductCount1" )
    @JsonProperty( "ProductCount1" )
    @Nullable
    @ODataField( odataName = "ProductCount1" )
    private ProductCount productCount1;
    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>ProductCount2</b>
     * </p>
     *
     * @return The productCount2 contained in this entity.
     */
    @SerializedName( "ProductCount2" )
    @JsonProperty( "ProductCount2" )
    @Nullable
    @ODataField( odataName = "ProductCount2" )
    private ProductCount productCount2;
    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>ProductCount3</b>
     * </p>
     *
     * @return The productCount3 contained in this entity.
     */
    @SerializedName( "ProductCount3" )
    @JsonProperty( "ProductCount3" )
    @Nullable
    @ODataField( odataName = "ProductCount3" )
    private ProductCount productCount3;
    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>ProductCount4</b>
     * </p>
     *
     * @return The productCount4 contained in this entity.
     */
    @SerializedName( "ProductCount4" )
    @JsonProperty( "ProductCount4" )
    @Nullable
    @ODataField( odataName = "ProductCount4" )
    private ProductCount productCount4;
    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>ProductCount5</b>
     * </p>
     *
     * @return The productCount5 contained in this entity.
     */
    @SerializedName( "ProductCount5" )
    @JsonProperty( "ProductCount5" )
    @Nullable
    @ODataField( odataName = "ProductCount5" )
    private ProductCount productCount5;
    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>ProductCount6</b>
     * </p>
     *
     * @return The productCount6 contained in this entity.
     */
    @SerializedName( "ProductCount6" )
    @JsonProperty( "ProductCount6" )
    @Nullable
    @ODataField( odataName = "ProductCount6" )
    private ProductCount productCount6;
    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>ProductCount7</b>
     * </p>
     *
     * @return The productCount7 contained in this entity.
     */
    @SerializedName( "ProductCount7" )
    @JsonProperty( "ProductCount7" )
    @Nullable
    @ODataField( odataName = "ProductCount7" )
    private ProductCount productCount7;
    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>ProductCount8</b>
     * </p>
     *
     * @return The productCount8 contained in this entity.
     */
    @SerializedName( "ProductCount8" )
    @JsonProperty( "ProductCount8" )
    @Nullable
    @ODataField( odataName = "ProductCount8" )
    private ProductCount productCount8;
    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>ProductCount9</b>
     * </p>
     *
     * @return The productCount9 contained in this entity.
     */
    @SerializedName( "ProductCount9" )
    @JsonProperty( "ProductCount9" )
    @Nullable
    @ODataField( odataName = "ProductCount9" )
    private ProductCount productCount9;
    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>ProductCount10</b>
     * </p>
     *
     * @return The productCount10 contained in this entity.
     */
    @SerializedName( "ProductCount10" )
    @JsonProperty( "ProductCount10" )
    @Nullable
    @ODataField( odataName = "ProductCount10" )
    private ProductCount productCount10;
    /**
     * Navigation property <b>Customer</b> for <b>Receipt</b> to single <b>Customer</b>.
     *
     */
    @SerializedName( "Customer" )
    @JsonProperty( "Customer" )
    @ODataField( odataName = "Customer" )
    @Nullable
    @Getter( AccessLevel.NONE )
    @Setter( AccessLevel.NONE )
    private Customer toCustomer;
    /**
     * Use with available fluent helpers to apply the <b>Customer</b> navigation property to query operations.
     *
     */
    public final static ReceiptOneToOneLink<Customer> TO_CUSTOMER = new ReceiptOneToOneLink<Customer>("Customer");

    @Nonnull
    @Override
    public Class<Receipt> getType()
    {
        return Receipt.class;
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
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>CustomerId</b>
     * </p>
     *
     * @param customerId
     *            The customerId to set.
     */
    public void setCustomerId( @Nullable final Integer customerId )
    {
        rememberChangedField("CustomerId", this.customerId);
        this.customerId = customerId;
    }

    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>TotalAmount</b>
     * </p>
     *
     * @param totalAmount
     *            The totalAmount to set.
     */
    public void setTotalAmount( @Nullable final BigDecimal totalAmount )
    {
        rememberChangedField("TotalAmount", this.totalAmount);
        this.totalAmount = totalAmount;
    }

    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>ProductCount1</b>
     * </p>
     *
     * @param productCount1
     *            The productCount1 to set.
     */
    public void setProductCount1( @Nullable final ProductCount productCount1 )
    {
        rememberChangedField("ProductCount1", this.productCount1);
        this.productCount1 = productCount1;
    }

    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>ProductCount2</b>
     * </p>
     *
     * @param productCount2
     *            The productCount2 to set.
     */
    public void setProductCount2( @Nullable final ProductCount productCount2 )
    {
        rememberChangedField("ProductCount2", this.productCount2);
        this.productCount2 = productCount2;
    }

    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>ProductCount3</b>
     * </p>
     *
     * @param productCount3
     *            The productCount3 to set.
     */
    public void setProductCount3( @Nullable final ProductCount productCount3 )
    {
        rememberChangedField("ProductCount3", this.productCount3);
        this.productCount3 = productCount3;
    }

    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>ProductCount4</b>
     * </p>
     *
     * @param productCount4
     *            The productCount4 to set.
     */
    public void setProductCount4( @Nullable final ProductCount productCount4 )
    {
        rememberChangedField("ProductCount4", this.productCount4);
        this.productCount4 = productCount4;
    }

    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>ProductCount5</b>
     * </p>
     *
     * @param productCount5
     *            The productCount5 to set.
     */
    public void setProductCount5( @Nullable final ProductCount productCount5 )
    {
        rememberChangedField("ProductCount5", this.productCount5);
        this.productCount5 = productCount5;
    }

    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>ProductCount6</b>
     * </p>
     *
     * @param productCount6
     *            The productCount6 to set.
     */
    public void setProductCount6( @Nullable final ProductCount productCount6 )
    {
        rememberChangedField("ProductCount6", this.productCount6);
        this.productCount6 = productCount6;
    }

    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>ProductCount7</b>
     * </p>
     *
     * @param productCount7
     *            The productCount7 to set.
     */
    public void setProductCount7( @Nullable final ProductCount productCount7 )
    {
        rememberChangedField("ProductCount7", this.productCount7);
        this.productCount7 = productCount7;
    }

    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>ProductCount8</b>
     * </p>
     *
     * @param productCount8
     *            The productCount8 to set.
     */
    public void setProductCount8( @Nullable final ProductCount productCount8 )
    {
        rememberChangedField("ProductCount8", this.productCount8);
        this.productCount8 = productCount8;
    }

    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>ProductCount9</b>
     * </p>
     *
     * @param productCount9
     *            The productCount9 to set.
     */
    public void setProductCount9( @Nullable final ProductCount productCount9 )
    {
        rememberChangedField("ProductCount9", this.productCount9);
        this.productCount9 = productCount9;
    }

    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>ProductCount10</b>
     * </p>
     *
     * @param productCount10
     *            The productCount10 to set.
     */
    public void setProductCount10( @Nullable final ProductCount productCount10 )
    {
        rememberChangedField("ProductCount10", this.productCount10);
        this.productCount10 = productCount10;
    }

    @Override
    protected String getEntityCollection()
    {
        return "Receipts";
    }

    @Nonnull
    @Override
    protected Map<String, Object> getKey()
    {
        final Map<String, Object> result = Maps.newLinkedHashMap();
        result.put("Id", getId());
        return result;
    }

    @Nonnull
    @Override
    protected Map<String, Object> toMapOfFields()
    {
        final Map<String, Object> cloudSdkValues = super.toMapOfFields();
        cloudSdkValues.put("Id", getId());
        cloudSdkValues.put("CustomerId", getCustomerId());
        cloudSdkValues.put("TotalAmount", getTotalAmount());
        cloudSdkValues.put("ProductCount1", getProductCount1());
        cloudSdkValues.put("ProductCount2", getProductCount2());
        cloudSdkValues.put("ProductCount3", getProductCount3());
        cloudSdkValues.put("ProductCount4", getProductCount4());
        cloudSdkValues.put("ProductCount5", getProductCount5());
        cloudSdkValues.put("ProductCount6", getProductCount6());
        cloudSdkValues.put("ProductCount7", getProductCount7());
        cloudSdkValues.put("ProductCount8", getProductCount8());
        cloudSdkValues.put("ProductCount9", getProductCount9());
        cloudSdkValues.put("ProductCount10", getProductCount10());
        return cloudSdkValues;
    }

    @Override
    protected void fromMap( final Map<String, Object> inputValues )
    {
        final Map<String, Object> cloudSdkValues = Maps.newLinkedHashMap(inputValues);
        // simple properties
        {
            if( cloudSdkValues.containsKey("Id") ) {
                final Object value = cloudSdkValues.remove("Id");
                if( (value == null) || (!value.equals(getId())) ) {
                    setId(((Integer) value));
                }
            }
            if( cloudSdkValues.containsKey("CustomerId") ) {
                final Object value = cloudSdkValues.remove("CustomerId");
                if( (value == null) || (!value.equals(getCustomerId())) ) {
                    setCustomerId(((Integer) value));
                }
            }
            if( cloudSdkValues.containsKey("TotalAmount") ) {
                final Object value = cloudSdkValues.remove("TotalAmount");
                if( (value == null) || (!value.equals(getTotalAmount())) ) {
                    setTotalAmount(((BigDecimal) value));
                }
            }
        }
        // structured properties
        {
            if( cloudSdkValues.containsKey("ProductCount1") ) {
                final Object value = cloudSdkValues.remove("ProductCount1");
                if( value instanceof Map ) {
                    if( getProductCount1() == null ) {
                        setProductCount1(new ProductCount());
                    }
                    @SuppressWarnings( "unchecked" )
                    final Map<String, Object> inputMap = ((Map<String, Object>) value);
                    getProductCount1().fromMap(inputMap);
                }
                if( (value == null) && (getProductCount1() != null) ) {
                    setProductCount1(null);
                }
            }
            if( cloudSdkValues.containsKey("ProductCount2") ) {
                final Object value = cloudSdkValues.remove("ProductCount2");
                if( value instanceof Map ) {
                    if( getProductCount2() == null ) {
                        setProductCount2(new ProductCount());
                    }
                    @SuppressWarnings( "unchecked" )
                    final Map<String, Object> inputMap = ((Map<String, Object>) value);
                    getProductCount2().fromMap(inputMap);
                }
                if( (value == null) && (getProductCount2() != null) ) {
                    setProductCount2(null);
                }
            }
            if( cloudSdkValues.containsKey("ProductCount3") ) {
                final Object value = cloudSdkValues.remove("ProductCount3");
                if( value instanceof Map ) {
                    if( getProductCount3() == null ) {
                        setProductCount3(new ProductCount());
                    }
                    @SuppressWarnings( "unchecked" )
                    final Map<String, Object> inputMap = ((Map<String, Object>) value);
                    getProductCount3().fromMap(inputMap);
                }
                if( (value == null) && (getProductCount3() != null) ) {
                    setProductCount3(null);
                }
            }
            if( cloudSdkValues.containsKey("ProductCount4") ) {
                final Object value = cloudSdkValues.remove("ProductCount4");
                if( value instanceof Map ) {
                    if( getProductCount4() == null ) {
                        setProductCount4(new ProductCount());
                    }
                    @SuppressWarnings( "unchecked" )
                    final Map<String, Object> inputMap = ((Map<String, Object>) value);
                    getProductCount4().fromMap(inputMap);
                }
                if( (value == null) && (getProductCount4() != null) ) {
                    setProductCount4(null);
                }
            }
            if( cloudSdkValues.containsKey("ProductCount5") ) {
                final Object value = cloudSdkValues.remove("ProductCount5");
                if( value instanceof Map ) {
                    if( getProductCount5() == null ) {
                        setProductCount5(new ProductCount());
                    }
                    @SuppressWarnings( "unchecked" )
                    final Map<String, Object> inputMap = ((Map<String, Object>) value);
                    getProductCount5().fromMap(inputMap);
                }
                if( (value == null) && (getProductCount5() != null) ) {
                    setProductCount5(null);
                }
            }
            if( cloudSdkValues.containsKey("ProductCount6") ) {
                final Object value = cloudSdkValues.remove("ProductCount6");
                if( value instanceof Map ) {
                    if( getProductCount6() == null ) {
                        setProductCount6(new ProductCount());
                    }
                    @SuppressWarnings( "unchecked" )
                    final Map<String, Object> inputMap = ((Map<String, Object>) value);
                    getProductCount6().fromMap(inputMap);
                }
                if( (value == null) && (getProductCount6() != null) ) {
                    setProductCount6(null);
                }
            }
            if( cloudSdkValues.containsKey("ProductCount7") ) {
                final Object value = cloudSdkValues.remove("ProductCount7");
                if( value instanceof Map ) {
                    if( getProductCount7() == null ) {
                        setProductCount7(new ProductCount());
                    }
                    @SuppressWarnings( "unchecked" )
                    final Map<String, Object> inputMap = ((Map<String, Object>) value);
                    getProductCount7().fromMap(inputMap);
                }
                if( (value == null) && (getProductCount7() != null) ) {
                    setProductCount7(null);
                }
            }
            if( cloudSdkValues.containsKey("ProductCount8") ) {
                final Object value = cloudSdkValues.remove("ProductCount8");
                if( value instanceof Map ) {
                    if( getProductCount8() == null ) {
                        setProductCount8(new ProductCount());
                    }
                    @SuppressWarnings( "unchecked" )
                    final Map<String, Object> inputMap = ((Map<String, Object>) value);
                    getProductCount8().fromMap(inputMap);
                }
                if( (value == null) && (getProductCount8() != null) ) {
                    setProductCount8(null);
                }
            }
            if( cloudSdkValues.containsKey("ProductCount9") ) {
                final Object value = cloudSdkValues.remove("ProductCount9");
                if( value instanceof Map ) {
                    if( getProductCount9() == null ) {
                        setProductCount9(new ProductCount());
                    }
                    @SuppressWarnings( "unchecked" )
                    final Map<String, Object> inputMap = ((Map<String, Object>) value);
                    getProductCount9().fromMap(inputMap);
                }
                if( (value == null) && (getProductCount9() != null) ) {
                    setProductCount9(null);
                }
            }
            if( cloudSdkValues.containsKey("ProductCount10") ) {
                final Object value = cloudSdkValues.remove("ProductCount10");
                if( value instanceof Map ) {
                    if( getProductCount10() == null ) {
                        setProductCount10(new ProductCount());
                    }
                    @SuppressWarnings( "unchecked" )
                    final Map<String, Object> inputMap = ((Map<String, Object>) value);
                    getProductCount10().fromMap(inputMap);
                }
                if( (value == null) && (getProductCount10() != null) ) {
                    setProductCount10(null);
                }
            }
        }
        // navigation properties
        {
            if( (cloudSdkValues).containsKey("Customer") ) {
                final Object cloudSdkValue = (cloudSdkValues).remove("Customer");
                if( cloudSdkValue instanceof Map ) {
                    if( toCustomer == null ) {
                        toCustomer = new Customer();
                    }
                    @SuppressWarnings( "unchecked" )
                    final Map<String, Object> inputMap = ((Map<String, Object>) cloudSdkValue);
                    toCustomer.fromMap(inputMap);
                }
            }
        }
        super.fromMap(cloudSdkValues);
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
    public static <T> ReceiptField<T> field( @Nonnull final String fieldName, @Nonnull final Class<T> fieldType )
    {
        return new ReceiptField<T>(fieldName);
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
    public static <T, DomainT> ReceiptField<T> field(
        @Nonnull final String fieldName,
        @Nonnull final TypeConverter<T, DomainT> typeConverter )
    {
        return new ReceiptField<T>(fieldName, typeConverter);
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
        final Map<String, Object> cloudSdkValues = super.toMapOfNavigationProperties();
        if( toCustomer != null ) {
            (cloudSdkValues).put("Customer", toCustomer);
        }
        return cloudSdkValues;
    }

    /**
     * Fetches the <b>Customer</b> entity (one to one) associated with this entity. This corresponds to the OData
     * navigation property <b>Customer</b>.
     * <p>
     * Please note: This method will not cache or persist the query results.
     *
     * @return The single associated <b>Customer</b> entity, or {@code null} if an entity is not associated.
     * @throws ODataException
     *             If the entity is unmanaged, i.e. it has not been retrieved using the OData VDM's services and
     *             therefore has no ERP configuration context assigned. An entity is managed if it has been either
     *             retrieved using the VDM's services or returned from the VDM's services as the result of a CREATE or
     *             UPDATE call.
     */
    @Nullable
    public Customer fetchCustomer()
    {
        return fetchFieldAsSingle("Customer", Customer.class);
    }

    /**
     * Retrieval of associated <b>Customer</b> entity (one to one). This corresponds to the OData navigation property
     * <b>Customer</b>.
     * <p>
     * If the navigation property <b>Customer</b> of a queried <b>Receipt</b> is operated lazily, an
     * <b>ODataException</b> can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and
     * persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the
     * first time and it has not yet been loaded, an OData query will be run in order to load the missing information
     * and its result will get cached for future invocations.
     *
     * @return List of associated <b>Customer</b> entity.
     * @throws ODataException
     *             If the entity is unmanaged, i.e. it has not been retrieved using the OData VDM's services and
     *             therefore has no ERP configuration context assigned. An entity is managed if it has been either
     *             retrieved using the VDM's services or returned from the VDM's services as the result of a CREATE or
     *             UPDATE call.
     */
    @Nullable
    public Customer getCustomerOrFetch()
    {
        if( toCustomer == null ) {
            toCustomer = fetchCustomer();
        }
        return toCustomer;
    }

    /**
     * Retrieval of associated <b>Customer</b> entity (one to one). This corresponds to the OData navigation property
     * <b>Customer</b>.
     * <p>
     * If the navigation property for an entity <b>Receipt</b> has not been resolved yet, this method will <b>not
     * query</b> further information. Instead its <code>Option</code> result state will be <code>empty</code>.
     *
     * @return If the information for navigation property <b>Customer</b> is already loaded, the result will contain the
     *         <b>Customer</b> entity. If not, an <code>Option</code> with result state <code>empty</code> is returned.
     */
    @Nonnull
    public Option<Customer> getCustomerIfPresent()
    {
        return Option.of(toCustomer);
    }

    /**
     * Overwrites the associated <b>Customer</b> entity for the loaded navigation property <b>Customer</b>.
     *
     * @param cloudSdkValue
     *            New <b>Customer</b> entity.
     */
    public void setCustomer( final Customer cloudSdkValue )
    {
        toCustomer = cloudSdkValue;
    }

    /**
     * Helper class to allow for fluent creation of Receipt instances.
     *
     */
    public final static class ReceiptBuilder
    {

        private Customer toCustomer;

        private Receipt.ReceiptBuilder toCustomer( final Customer cloudSdkValue )
        {
            toCustomer = cloudSdkValue;
            return this;
        }

        /**
         * Navigation property <b>Customer</b> for <b>Receipt</b> to single <b>Customer</b>.
         *
         * @param cloudSdkValue
         *            The Customer to build this Receipt with.
         * @return This Builder to allow for a fluent interface.
         */
        @Nonnull
        public Receipt.ReceiptBuilder customer( final Customer cloudSdkValue )
        {
            return toCustomer(cloudSdkValue);
        }

    }

}
