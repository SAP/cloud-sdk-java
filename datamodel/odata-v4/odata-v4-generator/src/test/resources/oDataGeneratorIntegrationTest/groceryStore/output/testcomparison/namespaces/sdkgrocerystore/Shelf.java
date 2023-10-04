/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.sdkgrocerystore;

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
import testcomparison.services.SdkGroceryStoreService;


/**
 * <p>Original entity name from the Odata EDM: <b>Shelf</b></p>
 * 
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(doNotUseGetters = true, callSuper = true)
@EqualsAndHashCode(doNotUseGetters = true, callSuper = true)
@JsonAdapter(com.sap.cloud.sdk.datamodel.odatav4.adapter.GsonVdmAdapterFactory.class)
@JsonSerialize(using = com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectSerializer.class)
@JsonDeserialize(using = com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectDeserializer.class)
public class Shelf
    extends VdmEntity<Shelf>
    implements VdmEntitySet
{

    @Getter
    private final String odataType = "com.sap.cloud.sdk.store.grocery.Shelf";
    /**
     * Selector for all available fields of Shelf.
     * 
     */
    public final static SimpleProperty<Shelf> ALL_FIELDS = all();
    /**
     * (Key Field) Constraints: Not nullable<p>Original property name from the Odata EDM: <b>Id</b></p>
     * 
     * @return
     *     The id contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("Id")
    private Integer id;
    public final static SimpleProperty.NumericInteger<Shelf> ID = new SimpleProperty.NumericInteger<Shelf>(Shelf.class, "Id");
    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>FloorPlanId</b></p>
     * 
     * @return
     *     The floorPlanId contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("FloorPlanId")
    private Integer floorPlanId;
    public final static SimpleProperty.NumericInteger<Shelf> FLOOR_PLAN_ID = new SimpleProperty.NumericInteger<Shelf>(Shelf.class, "FloorPlanId");
    /**
     * Navigation property <b>FloorPlan</b> for <b>Shelf</b> to single <b>FloorPlan</b>.
     * 
     */
    @ElementName("FloorPlan")
    @Nullable
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private FloorPlan toFloorPlan;
    /**
     * Navigation property <b>Products</b> for <b>Shelf</b> to multiple <b>Product</b>.
     * 
     */
    @ElementName("Products")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<Product> toProducts;
    /**
     * Use with available request builders to apply the <b>FloorPlan</b> navigation property to query operations.
     * 
     */
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Single<Shelf, FloorPlan> TO_FLOOR_PLAN = new com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Single<Shelf, FloorPlan>(Shelf.class, "FloorPlan", FloorPlan.class);
    /**
     * Use with available request builders to apply the <b>Products</b> navigation property to query operations.
     * 
     */
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Collection<Shelf, Product> TO_PRODUCTS = new com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Collection<Shelf, Product>(Shelf.class, "Products", Product.class);

    @Nonnull
    @Override
    public Class<Shelf> getType() {
        return Shelf.class;
    }

    /**
     * (Key Field) Constraints: Not nullable<p>Original property name from the Odata EDM: <b>Id</b></p>
     * 
     * @param id
     *     The id to set.
     */
    public void setId(
        @Nullable
        final Integer id) {
        rememberChangedField("Id", this.id);
        this.id = id;
    }

    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>FloorPlanId</b></p>
     * 
     * @param floorPlanId
     *     The floorPlanId to set.
     */
    public void setFloorPlanId(
        @Nullable
        final Integer floorPlanId) {
        rememberChangedField("FloorPlanId", this.floorPlanId);
        this.floorPlanId = floorPlanId;
    }

    @Override
    protected String getEntityCollection() {
        return "Shelves";
    }

    @Nonnull
    @Override
    protected ODataEntityKey getKey() {
        final ODataEntityKey entityKey = super.getKey();
        entityKey.addKeyProperty("Id", getId());
        return entityKey;
    }

    @Nonnull
    @Override
    protected Map<String, Object> toMapOfFields() {
        final Map<String, Object> values = super.toMapOfFields();
        values.put("Id", getId());
        values.put("FloorPlanId", getFloorPlanId());
        return values;
    }

    @Override
    protected void fromMap(final Map<String, Object> inputValues) {
        final Map<String, Object> values = Maps.newHashMap(inputValues);
        // simple properties
        {
            if (values.containsKey("Id")) {
                final Object value = values.remove("Id");
                if ((value == null)||(!value.equals(getId()))) {
                    setId(((Integer) value));
                }
            }
            if (values.containsKey("FloorPlanId")) {
                final Object value = values.remove("FloorPlanId");
                if ((value == null)||(!value.equals(getFloorPlanId()))) {
                    setFloorPlanId(((Integer) value));
                }
            }
        }
        // structured properties
        {
        }
        // navigation properties
        {
            if ((values).containsKey("FloorPlan")) {
                final Object value = (values).remove("FloorPlan");
                if (value instanceof Map) {
                    if (toFloorPlan == null) {
                        toFloorPlan = new FloorPlan();
                    }
                    @SuppressWarnings("unchecked")
                    final Map<String, Object> inputMap = ((Map<String, Object> ) value);
                    toFloorPlan.fromMap(inputMap);
                }
            }
            if ((values).containsKey("Products")) {
                final Object value = (values).remove("Products");
                if (value instanceof Iterable) {
                    if (toProducts == null) {
                        toProducts = Lists.newArrayList();
                    } else {
                        toProducts = Lists.newArrayList(toProducts);
                    }
                    int i = 0;
                    for (Object item: ((Iterable<?> ) value)) {
                        if (!(item instanceof Map)) {
                            continue;
                        }
                        Product entity;
                        if (toProducts.size()>i) {
                            entity = toProducts.get(i);
                        } else {
                            entity = new Product();
                            toProducts.add(entity);
                        }
                        i = (i + 1);
                        @SuppressWarnings("unchecked")
                        final Map<String, Object> inputMap = ((Map<String, Object> ) item);
                        entity.fromMap(inputMap);
                    }
                }
            }
        }
        super.fromMap(values);
    }

    @Override
    protected String getDefaultServicePath() {
        return SdkGroceryStoreService.DEFAULT_SERVICE_PATH;
    }

    @Nonnull
    @Override
    protected Map<String, Object> toMapOfNavigationProperties() {
        final Map<String, Object> values = super.toMapOfNavigationProperties();
        if (toFloorPlan!= null) {
            (values).put("FloorPlan", toFloorPlan);
        }
        if (toProducts!= null) {
            (values).put("Products", toProducts);
        }
        return values;
    }

    /**
     * Retrieval of associated <b>FloorPlan</b> entity (one to one). This corresponds to the OData navigation property <b>FloorPlan</b>.
     * <p>
     * If the navigation property for an entity <b>Shelf</b> has not been resolved yet, this method will <b>not query</b> further information. Instead its <code>Option</code> result state will be <code>empty</code>.
     * 
     * @return
     *     If the information for navigation property <b>FloorPlan</b> is already loaded, the result will contain the <b>FloorPlan</b> entity. If not, an <code>Option</code> with result state <code>empty</code> is returned.
     */
    @Nonnull
    public Option<FloorPlan> getFloorPlanIfPresent() {
        return Option.of(toFloorPlan);
    }

    /**
     * Overwrites the associated <b>FloorPlan</b> entity for the loaded navigation property <b>FloorPlan</b>.
     * 
     * @param value
     *     New <b>FloorPlan</b> entity.
     */
    public void setFloorPlan(final FloorPlan value) {
        toFloorPlan = value;
    }

    /**
     * Retrieval of associated <b>Product</b> entities (one to many). This corresponds to the OData navigation property <b>Products</b>.
     * <p>
     * If the navigation property for an entity <b>Shelf</b> has not been resolved yet, this method will <b>not query</b> further information. Instead its <code>Option</code> result state will be <code>empty</code>.
     * 
     * @return
     *     If the information for navigation property <b>Products</b> is already loaded, the result will contain the <b>Product</b> entities. If not, an <code>Option</code> with result state <code>empty</code> is returned.
     */
    @Nonnull
    public Option<List<Product>> getProductsIfPresent() {
        return Option.of(toProducts);
    }

    /**
     * Overwrites the list of associated <b>Product</b> entities for the loaded navigation property <b>Products</b>.
     * <p>
     * If the navigation property <b>Products</b> of a queried <b>Shelf</b> is operated lazily, an <b>ODataException</b> can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the first time and it has not yet been loaded, an OData query will be run in order to load the missing information and its result will get cached for future invocations.
     * 
     * @param value
     *     List of <b>Product</b> entities.
     */
    public void setProducts(
        @Nonnull
        final List<Product> value) {
        if (toProducts == null) {
            toProducts = Lists.newArrayList();
        }
        toProducts.clear();
        toProducts.addAll(value);
    }

    /**
     * Adds elements to the list of associated <b>Product</b> entities. This corresponds to the OData navigation property <b>Products</b>.
     * <p>
     * If the navigation property <b>Products</b> of a queried <b>Shelf</b> is operated lazily, an <b>ODataException</b> can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the first time and it has not yet been loaded, an OData query will be run in order to load the missing information and its result will get cached for future invocations.
     * 
     * @param entity
     *     Array of <b>Product</b> entities.
     */
    public void addProducts(Product... entity) {
        if (toProducts == null) {
            toProducts = Lists.newArrayList();
        }
        toProducts.addAll(Lists.newArrayList(entity));
    }

    /**
     * Function that can be applied to any entity object of this class.</p>
     * 
     * @return
     *     Function object prepared with the given parameters to be applied to any entity object of this class.</p> To execute it use the {@code service.forEntity(entity).applyFunction(thisFunction)} API.
     */
    @Nonnull
    public static com.sap.cloud.sdk.datamodel.odatav4.core.BoundFunction.SingleToCollection<Shelf, ProductCount> getProductQuantities() {
        final Map<String, Object> parameters = Collections.emptyMap();
        return new com.sap.cloud.sdk.datamodel.odatav4.core.BoundFunction.SingleToCollection<Shelf, ProductCount>(Shelf.class, ProductCount.class, "com.sap.cloud.sdk.store.grocery.GetProductQuantities", parameters);
    }


    /**
     * Helper class to allow for fluent creation of Shelf instances.
     * 
     */
    public final static class ShelfBuilder {

        private FloorPlan toFloorPlan;
        private List<Product> toProducts = Lists.newArrayList();

        private Shelf.ShelfBuilder toFloorPlan(final FloorPlan value) {
            toFloorPlan = value;
            return this;
        }

        /**
         * Navigation property <b>FloorPlan</b> for <b>Shelf</b> to single <b>FloorPlan</b>.
         * 
         * @param value
         *     The FloorPlan to build this Shelf with.
         * @return
         *     This Builder to allow for a fluent interface.
         */
        @Nonnull
        public Shelf.ShelfBuilder floorPlan(final FloorPlan value) {
            return toFloorPlan(value);
        }

        private Shelf.ShelfBuilder toProducts(final List<Product> value) {
            toProducts.addAll(value);
            return this;
        }

        /**
         * Navigation property <b>Products</b> for <b>Shelf</b> to multiple <b>Product</b>.
         * 
         * @param value
         *     The Products to build this Shelf with.
         * @return
         *     This Builder to allow for a fluent interface.
         */
        @Nonnull
        public Shelf.ShelfBuilder products(Product... value) {
            return toProducts(Lists.newArrayList(value));
        }

    }

}
