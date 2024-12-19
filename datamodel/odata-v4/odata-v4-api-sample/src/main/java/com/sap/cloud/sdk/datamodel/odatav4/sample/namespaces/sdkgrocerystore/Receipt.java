/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore;

import java.math.BigDecimal;
import java.util.LinkedList;
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
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEntitySet;
import com.sap.cloud.sdk.datamodel.odatav4.sample.services.SdkGroceryStoreService;
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
@JsonAdapter( com.sap.cloud.sdk.datamodel.odatav4.adapter.GsonVdmAdapterFactory.class )
@JsonSerialize( using = com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectSerializer.class )
@JsonDeserialize( using = com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectDeserializer.class )
public class Receipt extends VdmEntity<Receipt> implements VdmEntitySet
{

    @Getter
    private final String odataType = "com.sap.cloud.sdk.store.grocery.Receipt";
    /**
     * Selector for all available fields of Receipt.
     *
     */
    public final static SimpleProperty<Receipt> ALL_FIELDS = all();
    /**
     * (Key Field) Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>Id</b>
     * </p>
     *
     * @return ID of the receipt.
     */
    @Nullable
    @ElementName( "Id" )
    private Integer id;
    public final static SimpleProperty.NumericInteger<Receipt> ID =
        new SimpleProperty.NumericInteger<Receipt>(Receipt.class, "Id");
    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>CustomerId</b>
     * </p>
     *
     * @return ID of the customer.
     */
    @Nullable
    @ElementName( "CustomerId" )
    private Integer customerId;
    public final static SimpleProperty.NumericInteger<Receipt> CUSTOMER_ID =
        new SimpleProperty.NumericInteger<Receipt>(Receipt.class, "CustomerId");
    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>TotalAmount</b>
     * </p>
     *
     * @return Total amount of the receipt.
     */
    @Nullable
    @ElementName( "TotalAmount" )
    private BigDecimal totalAmount;
    public final static SimpleProperty.NumericDecimal<Receipt> TOTAL_AMOUNT =
        new SimpleProperty.NumericDecimal<Receipt>(Receipt.class, "TotalAmount");
    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>ProductCounts</b>
     * </p>
     *
     * @return List of products and quantities associated with the receipt.
     */
    @Nullable
    @ElementName( "ProductCounts" )
    private java.util.Collection<ProductCount> productCounts;
    /**
     * Use with available request builders to apply the <b>ProductCounts</b> complex property to query operations.
     *
     */
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.ComplexProperty.Collection<Receipt, ProductCount> PRODUCT_COUNTS =
        new com.sap.cloud.sdk.datamodel.odatav4.core.ComplexProperty.Collection<Receipt, ProductCount>(
            Receipt.class,
            "ProductCounts",
            ProductCount.class);
    /**
     * Navigation property <b>Customer</b> for <b>Receipt</b> to single <b>Customer</b>.
     *
     */
    @ElementName( "Customer" )
    @Nullable
    @Getter( AccessLevel.NONE )
    @Setter( AccessLevel.NONE )
    private Customer toCustomer;
    /**
     * Use with available request builders to apply the <b>Customer</b> navigation property to query operations.
     *
     */
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Single<Receipt, Customer> TO_CUSTOMER =
        new com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Single<Receipt, Customer>(
            Receipt.class,
            "Customer",
            Customer.class);

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
     *            ID of the receipt.
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
     *            ID of the customer.
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
     *            Total amount of the receipt.
     */
    public void setTotalAmount( @Nullable final BigDecimal totalAmount )
    {
        rememberChangedField("TotalAmount", this.totalAmount);
        this.totalAmount = totalAmount;
    }

    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>ProductCounts</b>
     * </p>
     *
     * @param productCounts
     *            List of products and quantities associated with the receipt.
     */
    public void setProductCounts( @Nullable final java.util.Collection<ProductCount> productCounts )
    {
        rememberChangedField("ProductCounts", this.productCounts);
        this.productCounts = productCounts;
    }

    @Override
    protected String getEntityCollection()
    {
        return "Receipts";
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
    protected Map<String, Object> toMapOfFields()
    {
        final Map<String, Object> cloudSdkValues = super.toMapOfFields();
        cloudSdkValues.put("Id", getId());
        cloudSdkValues.put("CustomerId", getCustomerId());
        cloudSdkValues.put("TotalAmount", getTotalAmount());
        cloudSdkValues.put("ProductCounts", getProductCounts());
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
            if( cloudSdkValues.containsKey("ProductCounts") ) {
                final Object value = cloudSdkValues.remove("ProductCounts");
                if( value instanceof Iterable ) {
                    final LinkedList<ProductCount> productCounts = new LinkedList<ProductCount>();
                    for( Object properties : ((Iterable<?>) value) ) {
                        if( properties instanceof Map ) {
                            final ProductCount item = new ProductCount();
                            @SuppressWarnings( "unchecked" )
                            final Map<String, Object> inputMap = ((Map<String, Object>) value);
                            item.fromMap(inputMap);
                            productCounts.add(item);
                        }
                    }
                    setProductCounts(productCounts);
                }
                if( (value == null) && (getProductCounts() != null) ) {
                    setProductCounts(null);
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

    @Override
    protected String getDefaultServicePath()
    {
        return SdkGroceryStoreService.DEFAULT_SERVICE_PATH;
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
