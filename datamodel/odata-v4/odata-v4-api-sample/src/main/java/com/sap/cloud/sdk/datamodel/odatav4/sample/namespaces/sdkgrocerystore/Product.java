/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

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
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEnum;
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
 * Original entity name from the Odata EDM: <b>Product</b>
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
public class Product extends VdmEntity<Product> implements VdmEntitySet
{

    @Getter
    private final java.lang.String odataType = "com.sap.cloud.sdk.store.grocery.Product";
    /**
     * Selector for all available fields of Product.
     *
     */
    public final static SimpleProperty<Product> ALL_FIELDS = all();
    /**
     * (Key Field) Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>Id</b>
     * </p>
     *
     * @return ID of the product.
     */
    @Nullable
    @ElementName( "Id" )
    private Integer id;
    public final static SimpleProperty.NumericInteger<Product> ID =
        new SimpleProperty.NumericInteger<Product>(Product.class, "Id");
    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>Name</b>
     * </p>
     *
     * @return Name of the product.
     */
    @Nullable
    @ElementName( "Name" )
    private java.lang.String name;
    public final static SimpleProperty.String<Product> NAME = new SimpleProperty.String<Product>(Product.class, "Name");
    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>ShelfId</b>
     * </p>
     *
     * @return The shelfId contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName( "ShelfId" )
    private Integer shelfId;
    public final static SimpleProperty.NumericInteger<Product> SHELF_ID =
        new SimpleProperty.NumericInteger<Product>(Product.class, "ShelfId");
    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>VendorId</b>
     * </p>
     *
     * @return ID of the vendor.
     */
    @Nullable
    @ElementName( "VendorId" )
    private Integer vendorId;
    public final static SimpleProperty.NumericInteger<Product> VENDOR_ID =
        new SimpleProperty.NumericInteger<Product>(Product.class, "VendorId");
    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>Price</b>
     * </p>
     *
     * @return Price of the product.
     */
    @Nullable
    @ElementName( "Price" )
    private BigDecimal price;
    public final static SimpleProperty.NumericDecimal<Product> PRICE =
        new SimpleProperty.NumericDecimal<Product>(Product.class, "Price");
    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>Categories</b>
     * </p>
     *
     * @return The categories contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName( "Categories" )
    private java.util.Collection<ProductCategory> categories;
    public final static SimpleProperty.Collection<Product, ProductCategory> CATEGORIES =
        new SimpleProperty.Collection<Product, ProductCategory>(Product.class, "Categories", ProductCategory.class);
    /**
     * Navigation property <b>Vendor</b> for <b>Product</b> to single <b>Vendor</b>.
     *
     */
    @ElementName( "Vendor" )
    @Nullable
    @Getter( AccessLevel.NONE )
    @Setter( AccessLevel.NONE )
    private Vendor toVendor;
    /**
     * Navigation property <b>Shelf</b> for <b>Product</b> to single <b>Shelf</b>.
     *
     */
    @ElementName( "Shelf" )
    @Nullable
    @Getter( AccessLevel.NONE )
    @Setter( AccessLevel.NONE )
    private Shelf toShelf;
    /**
     * Use with available request builders to apply the <b>Vendor</b> navigation property to query operations.
     *
     */
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Single<Product, Vendor> TO_VENDOR =
        new com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Single<Product, Vendor>(
            Product.class,
            "Vendor",
            Vendor.class);
    /**
     * Use with available request builders to apply the <b>Shelf</b> navigation property to query operations.
     *
     */
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Single<Product, Shelf> TO_SHELF =
        new com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Single<Product, Shelf>(
            Product.class,
            "Shelf",
            Shelf.class);

    @Nonnull
    @Override
    public Class<Product> getType()
    {
        return Product.class;
    }

    /**
     * (Key Field) Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>Id</b>
     * </p>
     *
     * @param id
     *            ID of the product.
     */
    public void setId( @Nullable final Integer id )
    {
        rememberChangedField("Id", this.id);
        this.id = id;
    }

    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>Name</b>
     * </p>
     *
     * @param name
     *            Name of the product.
     */
    public void setName( @Nullable final java.lang.String name )
    {
        rememberChangedField("Name", this.name);
        this.name = name;
    }

    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>ShelfId</b>
     * </p>
     *
     * @param shelfId
     *            The shelfId to set.
     */
    public void setShelfId( @Nullable final Integer shelfId )
    {
        rememberChangedField("ShelfId", this.shelfId);
        this.shelfId = shelfId;
    }

    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>VendorId</b>
     * </p>
     *
     * @param vendorId
     *            ID of the vendor.
     */
    public void setVendorId( @Nullable final Integer vendorId )
    {
        rememberChangedField("VendorId", this.vendorId);
        this.vendorId = vendorId;
    }

    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>Price</b>
     * </p>
     *
     * @param price
     *            Price of the product.
     */
    public void setPrice( @Nullable final BigDecimal price )
    {
        rememberChangedField("Price", this.price);
        this.price = price;
    }

    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>Categories</b>
     * </p>
     *
     * @param categories
     *            The categories to set.
     */
    public void setCategories( @Nullable final java.util.Collection<ProductCategory> categories )
    {
        rememberChangedField("Categories", this.categories);
        this.categories = categories;
    }

    @Override
    protected java.lang.String getEntityCollection()
    {
        return "Products";
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
        cloudSdkValues.put("ShelfId", getShelfId());
        cloudSdkValues.put("VendorId", getVendorId());
        cloudSdkValues.put("Price", getPrice());
        cloudSdkValues.put("Categories", getCategories());
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
            if( cloudSdkValues.containsKey("ShelfId") ) {
                final Object value = cloudSdkValues.remove("ShelfId");
                if( (value == null) || (!value.equals(getShelfId())) ) {
                    setShelfId(((Integer) value));
                }
            }
            if( cloudSdkValues.containsKey("VendorId") ) {
                final Object value = cloudSdkValues.remove("VendorId");
                if( (value == null) || (!value.equals(getVendorId())) ) {
                    setVendorId(((Integer) value));
                }
            }
            if( cloudSdkValues.containsKey("Price") ) {
                final Object value = cloudSdkValues.remove("Price");
                if( (value == null) || (!value.equals(getPrice())) ) {
                    setPrice(((BigDecimal) value));
                }
            }
            if( cloudSdkValues.containsKey("Categories") ) {
                final Object value = cloudSdkValues.remove("Categories");
                if( (value == null) && (getCategories() != null) ) {
                    setCategories(null);
                }
                if( value instanceof Iterable ) {
                    final LinkedList<ProductCategory> categories = new LinkedList<ProductCategory>();
                    for( Object item : ((Iterable<?>) value) ) {
                        if( item instanceof java.lang.String ) {
                            final ProductCategory enumConstant =
                                VdmEnum.getConstant(ProductCategory.class, ((java.lang.String) item));
                            categories.add(enumConstant);
                        }
                    }
                    if( !Objects.equals(categories, getCategories()) ) {
                        setCategories(categories);
                    }
                }
            }
        }
        // structured properties
        {
        }
        // navigation properties
        {
            if( (cloudSdkValues).containsKey("Vendor") ) {
                final Object cloudSdkValue = (cloudSdkValues).remove("Vendor");
                if( cloudSdkValue instanceof Map ) {
                    if( toVendor == null ) {
                        toVendor = new Vendor();
                    }
                    @SuppressWarnings( "unchecked" )
                    final Map<java.lang.String, Object> inputMap = ((Map<java.lang.String, Object>) cloudSdkValue);
                    toVendor.fromMap(inputMap);
                }
            }
            if( (cloudSdkValues).containsKey("Shelf") ) {
                final Object cloudSdkValue = (cloudSdkValues).remove("Shelf");
                if( cloudSdkValue instanceof Map ) {
                    if( toShelf == null ) {
                        toShelf = new Shelf();
                    }
                    @SuppressWarnings( "unchecked" )
                    final Map<java.lang.String, Object> inputMap = ((Map<java.lang.String, Object>) cloudSdkValue);
                    toShelf.fromMap(inputMap);
                }
            }
        }
        super.fromMap(cloudSdkValues);
    }

    @Override
    protected java.lang.String getDefaultServicePath()
    {
        return SdkGroceryStoreService.DEFAULT_SERVICE_PATH;
    }

    @Nonnull
    @Override
    protected Map<java.lang.String, Object> toMapOfNavigationProperties()
    {
        final Map<java.lang.String, Object> cloudSdkValues = super.toMapOfNavigationProperties();
        if( toVendor != null ) {
            (cloudSdkValues).put("Vendor", toVendor);
        }
        if( toShelf != null ) {
            (cloudSdkValues).put("Shelf", toShelf);
        }
        return cloudSdkValues;
    }

    /**
     * Retrieval of associated <b>Vendor</b> entity (one to one). This corresponds to the OData navigation property
     * <b>Vendor</b>.
     * <p>
     * If the navigation property for an entity <b>Product</b> has not been resolved yet, this method will <b>not
     * query</b> further information. Instead its <code>Option</code> result state will be <code>empty</code>.
     *
     * @return If the information for navigation property <b>Vendor</b> is already loaded, the result will contain the
     *         <b>Vendor</b> entity. If not, an <code>Option</code> with result state <code>empty</code> is returned.
     */
    @Nonnull
    public Option<Vendor> getVendorIfPresent()
    {
        return Option.of(toVendor);
    }

    /**
     * Overwrites the associated <b>Vendor</b> entity for the loaded navigation property <b>Vendor</b>.
     *
     * @param cloudSdkValue
     *            New <b>Vendor</b> entity.
     */
    public void setVendor( final Vendor cloudSdkValue )
    {
        toVendor = cloudSdkValue;
    }

    /**
     * Retrieval of associated <b>Shelf</b> entity (one to one). This corresponds to the OData navigation property
     * <b>Shelf</b>.
     * <p>
     * If the navigation property for an entity <b>Product</b> has not been resolved yet, this method will <b>not
     * query</b> further information. Instead its <code>Option</code> result state will be <code>empty</code>.
     *
     * @return If the information for navigation property <b>Shelf</b> is already loaded, the result will contain the
     *         <b>Shelf</b> entity. If not, an <code>Option</code> with result state <code>empty</code> is returned.
     */
    @Nonnull
    public Option<Shelf> getShelfIfPresent()
    {
        return Option.of(toShelf);
    }

    /**
     * Overwrites the associated <b>Shelf</b> entity for the loaded navigation property <b>Shelf</b>.
     *
     * @param cloudSdkValue
     *            New <b>Shelf</b> entity.
     */
    public void setShelf( final Shelf cloudSdkValue )
    {
        toShelf = cloudSdkValue;
    }

    /**
     * Helper class to allow for fluent creation of Product instances.
     *
     */
    public final static class ProductBuilder
    {

        private Vendor toVendor;
        private Shelf toShelf;

        private Product.ProductBuilder toVendor( final Vendor cloudSdkValue )
        {
            toVendor = cloudSdkValue;
            return this;
        }

        /**
         * Navigation property <b>Vendor</b> for <b>Product</b> to single <b>Vendor</b>.
         *
         * @param cloudSdkValue
         *            The Vendor to build this Product with.
         * @return This Builder to allow for a fluent interface.
         */
        @Nonnull
        public Product.ProductBuilder vendor( final Vendor cloudSdkValue )
        {
            return toVendor(cloudSdkValue);
        }

        private Product.ProductBuilder toShelf( final Shelf cloudSdkValue )
        {
            toShelf = cloudSdkValue;
            return this;
        }

        /**
         * Navigation property <b>Shelf</b> for <b>Product</b> to single <b>Shelf</b>.
         *
         * @param cloudSdkValue
         *            The Shelf to build this Product with.
         * @return This Builder to allow for a fluent interface.
         */
        @Nonnull
        public Product.ProductBuilder shelf( final Shelf cloudSdkValue )
        {
            return toShelf(cloudSdkValue);
        }

    }

}
