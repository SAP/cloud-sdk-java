/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
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
 * Original entity name from the Odata EDM: <b>Shelf</b>
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
public class Shelf extends VdmEntity<Shelf> implements VdmEntitySet
{

    @Getter
    private final String odataType = "com.sap.cloud.sdk.store.grocery.Shelf";
    /**
     * Selector for all available fields of Shelf.
     *
     */
    public final static SimpleProperty<Shelf> ALL_FIELDS = all();
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
    public final static SimpleProperty.NumericInteger<Shelf> ID =
        new SimpleProperty.NumericInteger<Shelf>(Shelf.class, "Id");
    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>FloorPlanId</b>
     * </p>
     *
     * @return The floorPlanId contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName( "FloorPlanId" )
    private Integer floorPlanId;
    public final static SimpleProperty.NumericInteger<Shelf> FLOOR_PLAN_ID =
        new SimpleProperty.NumericInteger<Shelf>(Shelf.class, "FloorPlanId");
    /**
     * Navigation property <b>FloorPlan</b> for <b>Shelf</b> to single <b>FloorPlan</b>.
     *
     */
    @ElementName( "FloorPlan" )
    @Nullable
    @Getter( AccessLevel.NONE )
    @Setter( AccessLevel.NONE )
    private FloorPlan toFloorPlan;
    /**
     * Navigation property <b>Products</b> for <b>Shelf</b> to multiple <b>Product</b>.
     *
     */
    @ElementName( "Products" )
    @Getter( AccessLevel.NONE )
    @Setter( AccessLevel.NONE )
    private List<Product> toProducts;
    /**
     * Use with available request builders to apply the <b>FloorPlan</b> navigation property to query operations.
     *
     */
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Single<Shelf, FloorPlan> TO_FLOOR_PLAN =
        new com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Single<Shelf, FloorPlan>(
            Shelf.class,
            "FloorPlan",
            FloorPlan.class);
    /**
     * Use with available request builders to apply the <b>Products</b> navigation property to query operations.
     *
     */
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Collection<Shelf, Product> TO_PRODUCTS =
        new com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Collection<Shelf, Product>(
            Shelf.class,
            "Products",
            Product.class);

    @Nonnull
    @Override
    public Class<Shelf> getType()
    {
        return Shelf.class;
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
     * Original property name from the Odata EDM: <b>FloorPlanId</b>
     * </p>
     *
     * @param floorPlanId
     *            The floorPlanId to set.
     */
    public void setFloorPlanId( @Nullable final Integer floorPlanId )
    {
        rememberChangedField("FloorPlanId", this.floorPlanId);
        this.floorPlanId = floorPlanId;
    }

    @Override
    protected String getEntityCollection()
    {
        return "Shelves";
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
        cloudSdkValues.put("FloorPlanId", getFloorPlanId());
        return cloudSdkValues;
    }

    @Override
    protected void fromMap( final Map<String, Object> inputValues )
    {
        final Map<String, Object> cloudSdkValues = Maps.newHashMap(inputValues);
        // simple properties
        {
            if( cloudSdkValues.containsKey("Id") ) {
                final Object value = cloudSdkValues.remove("Id");
                if( (value == null) || (!value.equals(getId())) ) {
                    setId(((Integer) value));
                }
            }
            if( cloudSdkValues.containsKey("FloorPlanId") ) {
                final Object value = cloudSdkValues.remove("FloorPlanId");
                if( (value == null) || (!value.equals(getFloorPlanId())) ) {
                    setFloorPlanId(((Integer) value));
                }
            }
        }
        // structured properties
        {
        }
        // navigation properties
        {
            if( (cloudSdkValues).containsKey("FloorPlan") ) {
                final Object cloudSdkValue = (cloudSdkValues).remove("FloorPlan");
                if( cloudSdkValue instanceof Map ) {
                    if( toFloorPlan == null ) {
                        toFloorPlan = new FloorPlan();
                    }
                    @SuppressWarnings( "unchecked" )
                    final Map<String, Object> inputMap = ((Map<String, Object>) cloudSdkValue);
                    toFloorPlan.fromMap(inputMap);
                }
            }
            if( (cloudSdkValues).containsKey("Products") ) {
                final Object cloudSdkValue = (cloudSdkValues).remove("Products");
                if( cloudSdkValue instanceof Iterable ) {
                    if( toProducts == null ) {
                        toProducts = Lists.newArrayList();
                    } else {
                        toProducts = Lists.newArrayList(toProducts);
                    }
                    int i = 0;
                    for( Object item : ((Iterable<?>) cloudSdkValue) ) {
                        if( !(item instanceof Map) ) {
                            continue;
                        }
                        Product entity;
                        if( toProducts.size() > i ) {
                            entity = toProducts.get(i);
                        } else {
                            entity = new Product();
                            toProducts.add(entity);
                        }
                        i = (i + 1);
                        @SuppressWarnings( "unchecked" )
                        final Map<String, Object> inputMap = ((Map<String, Object>) item);
                        entity.fromMap(inputMap);
                    }
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
        if( toFloorPlan != null ) {
            (cloudSdkValues).put("FloorPlan", toFloorPlan);
        }
        if( toProducts != null ) {
            (cloudSdkValues).put("Products", toProducts);
        }
        return cloudSdkValues;
    }

    /**
     * Retrieval of associated <b>FloorPlan</b> entity (one to one). This corresponds to the OData navigation property
     * <b>FloorPlan</b>.
     * <p>
     * If the navigation property for an entity <b>Shelf</b> has not been resolved yet, this method will <b>not
     * query</b> further information. Instead its <code>Option</code> result state will be <code>empty</code>.
     *
     * @return If the information for navigation property <b>FloorPlan</b> is already loaded, the result will contain
     *         the <b>FloorPlan</b> entity. If not, an <code>Option</code> with result state <code>empty</code> is
     *         returned.
     */
    @Nonnull
    public Option<FloorPlan> getFloorPlanIfPresent()
    {
        return Option.of(toFloorPlan);
    }

    /**
     * Overwrites the associated <b>FloorPlan</b> entity for the loaded navigation property <b>FloorPlan</b>.
     *
     * @param cloudSdkValue
     *            New <b>FloorPlan</b> entity.
     */
    public void setFloorPlan( final FloorPlan cloudSdkValue )
    {
        toFloorPlan = cloudSdkValue;
    }

    /**
     * Retrieval of associated <b>Product</b> entities (one to many). This corresponds to the OData navigation property
     * <b>Products</b>.
     * <p>
     * If the navigation property for an entity <b>Shelf</b> has not been resolved yet, this method will <b>not
     * query</b> further information. Instead its <code>Option</code> result state will be <code>empty</code>.
     *
     * @return If the information for navigation property <b>Products</b> is already loaded, the result will contain the
     *         <b>Product</b> entities. If not, an <code>Option</code> with result state <code>empty</code> is returned.
     */
    @Nonnull
    public Option<List<Product>> getProductsIfPresent()
    {
        return Option.of(toProducts);
    }

    /**
     * Overwrites the list of associated <b>Product</b> entities for the loaded navigation property <b>Products</b>.
     * <p>
     * If the navigation property <b>Products</b> of a queried <b>Shelf</b> is operated lazily, an <b>ODataException</b>
     * can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and
     * persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the
     * first time and it has not yet been loaded, an OData query will be run in order to load the missing information
     * and its result will get cached for future invocations.
     *
     * @param cloudSdkValue
     *            List of <b>Product</b> entities.
     */
    public void setProducts( @Nonnull final List<Product> cloudSdkValue )
    {
        if( toProducts == null ) {
            toProducts = Lists.newArrayList();
        }
        toProducts.clear();
        toProducts.addAll(cloudSdkValue);
    }

    /**
     * Adds elements to the list of associated <b>Product</b> entities. This corresponds to the OData navigation
     * property <b>Products</b>.
     * <p>
     * If the navigation property <b>Products</b> of a queried <b>Shelf</b> is operated lazily, an <b>ODataException</b>
     * can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and
     * persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the
     * first time and it has not yet been loaded, an OData query will be run in order to load the missing information
     * and its result will get cached for future invocations.
     *
     * @param entity
     *            Array of <b>Product</b> entities.
     */
    public void addProducts( Product... entity )
    {
        if( toProducts == null ) {
            toProducts = Lists.newArrayList();
        }
        toProducts.addAll(Lists.newArrayList(entity));
    }

    /**
     * Function that can be applied to any entity object of this class.
     * </p>
     *
     * @return Function object prepared with the given parameters to be applied to any entity object of this class.
     *         </p>
     *         To execute it use the {@code service.forEntity(entity).applyFunction(thisFunction)} API.
     */
    @Nonnull
    public static
        com.sap.cloud.sdk.datamodel.odatav4.core.BoundFunction.SingleToCollection<Shelf, ProductCount>
        getProductQuantities()
    {
        final Map<String, Object> parameters = Collections.emptyMap();
        return new com.sap.cloud.sdk.datamodel.odatav4.core.BoundFunction.SingleToCollection<Shelf, ProductCount>(
            Shelf.class,
            ProductCount.class,
            "com.sap.cloud.sdk.store.grocery.GetProductQuantities",
            parameters);
    }

    /**
     * Helper class to allow for fluent creation of Shelf instances.
     *
     */
    public final static class ShelfBuilder
    {

        private FloorPlan toFloorPlan;
        private List<Product> toProducts = Lists.newArrayList();

        private Shelf.ShelfBuilder toFloorPlan( final FloorPlan cloudSdkValue )
        {
            toFloorPlan = cloudSdkValue;
            return this;
        }

        /**
         * Navigation property <b>FloorPlan</b> for <b>Shelf</b> to single <b>FloorPlan</b>.
         *
         * @param cloudSdkValue
         *            The FloorPlan to build this Shelf with.
         * @return This Builder to allow for a fluent interface.
         */
        @Nonnull
        public Shelf.ShelfBuilder floorPlan( final FloorPlan cloudSdkValue )
        {
            return toFloorPlan(cloudSdkValue);
        }

        private Shelf.ShelfBuilder toProducts( final List<Product> cloudSdkValue )
        {
            toProducts.addAll(cloudSdkValue);
            return this;
        }

        /**
         * Navigation property <b>Products</b> for <b>Shelf</b> to multiple <b>Product</b>.
         *
         * @param cloudSdkValue
         *            The Products to build this Shelf with.
         * @return This Builder to allow for a fluent interface.
         */
        @Nonnull
        public Shelf.ShelfBuilder products( Product... cloudSdkValue )
        {
            return toProducts(Lists.newArrayList(cloudSdkValue));
        }

    }

}
