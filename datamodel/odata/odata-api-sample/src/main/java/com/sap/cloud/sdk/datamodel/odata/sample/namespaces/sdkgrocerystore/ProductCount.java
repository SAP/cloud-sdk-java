/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmComplex;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <p>
 * Original complex type name from the Odata EDM: <b>ProductCount</b>
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
public class ProductCount extends VdmComplex<ProductCount>
{

    /**
     * Constraints: Not nullable
     * <p>
     * Original property from the Odata EDM: <b>ProductId</b>
     * </p>
     *
     * @param productId
     *
     */
    @SerializedName( "ProductId" )
    @JsonProperty( "ProductId" )
    @Nullable
    @ODataField( odataName = "ProductId" )
    private Integer productId;
    /**
     * Constraints: Not nullable
     * <p>
     * Original property from the Odata EDM: <b>Quantity</b>
     * </p>
     *
     * @param quantity
     *
     */
    @SerializedName( "Quantity" )
    @JsonProperty( "Quantity" )
    @Nullable
    @ODataField( odataName = "Quantity" )
    private Integer quantity;

    @Nonnull
    @Override
    public Class<ProductCount> getType()
    {
        return ProductCount.class;
    }

    @Nonnull
    @Override
    protected Map<String, Object> toMapOfFields()
    {
        final Map<String, Object> cloudSdkValues = super.toMapOfFields();
        cloudSdkValues.put("ProductId", getProductId());
        cloudSdkValues.put("Quantity", getQuantity());
        return cloudSdkValues;
    }

    @Override
    protected void fromMap( final Map<String, Object> inputValues )
    {
        final Map<String, Object> cloudSdkValues = Maps.newLinkedHashMap(inputValues);
        // simple properties
        {
            if( cloudSdkValues.containsKey("ProductId") ) {
                final Object value = cloudSdkValues.remove("ProductId");
                if( (value == null) || (!value.equals(getProductId())) ) {
                    setProductId(((Integer) value));
                }
            }
            if( cloudSdkValues.containsKey("Quantity") ) {
                final Object value = cloudSdkValues.remove("Quantity");
                if( (value == null) || (!value.equals(getQuantity())) ) {
                    setQuantity(((Integer) value));
                }
            }
        }
        // structured properties
        {
        }
        // navigation properties
        {
        }
        super.fromMap(cloudSdkValues);
    }

    @Nonnull
    @Override
    protected Map<String, Object> getKey()
    {
        final Map<String, Object> result = Maps.newLinkedHashMap();
        return result;
    }

    /**
     * Constraints: Not nullable
     * <p>
     * Original property from the Odata EDM: <b>ProductId</b>
     * </p>
     *
     * @param productId
     *            The productId to set.
     */
    public void setProductId( @Nullable final Integer productId )
    {
        rememberChangedField("ProductId", this.productId);
        this.productId = productId;
    }

    /**
     * Constraints: Not nullable
     * <p>
     * Original property from the Odata EDM: <b>Quantity</b>
     * </p>
     *
     * @param quantity
     *            The quantity to set.
     */
    public void setQuantity( @Nullable final Integer quantity )
    {
        rememberChangedField("Quantity", this.quantity);
        this.quantity = quantity;
    }

}
