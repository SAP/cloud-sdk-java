/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.sdkgrocerystore;

import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataException;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmEntity;
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
import testcomparison.namespaces.sdkgrocerystore.field.ShelfField;
import testcomparison.namespaces.sdkgrocerystore.link.ShelfLink;
import testcomparison.namespaces.sdkgrocerystore.link.ShelfOneToOneLink;
import testcomparison.namespaces.sdkgrocerystore.selectable.ShelfSelectable;


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
@JsonAdapter(com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory.class)
public class Shelf
    extends VdmEntity<Shelf>
{

    /**
     * Selector for all available fields of Shelf.
     * 
     */
    public final static ShelfSelectable ALL_FIELDS = () -> "*";
    /**
     * (Key Field) Constraints: Not nullable<p>Original property name from the Odata EDM: <b>Id</b></p>
     * 
     * @return
     *     The id contained in this entity.
     */
    @Key
    @SerializedName("Id")
    @JsonProperty("Id")
    @Nullable
    @ODataField(odataName = "Id")
    private Integer id;
    /**
     * Use with available fluent helpers to apply the <b>Id</b> field to query operations.
     * 
     */
    public final static ShelfField<Integer> ID = new ShelfField<Integer>("Id");
    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>FloorPlanId</b></p>
     * 
     * @return
     *     The floorPlanId contained in this entity.
     */
    @SerializedName("FloorPlanId")
    @JsonProperty("FloorPlanId")
    @Nullable
    @ODataField(odataName = "FloorPlanId")
    private Integer floorPlanId;
    /**
     * Use with available fluent helpers to apply the <b>FloorPlanId</b> field to query operations.
     * 
     */
    public final static ShelfField<Integer> FLOOR_PLAN_ID = new ShelfField<Integer>("FloorPlanId");
    /**
     * Navigation property <b>FloorPlan</b> for <b>Shelf</b> to single <b>FloorPlan</b>.
     * 
     */
    @SerializedName("FloorPlan")
    @JsonProperty("FloorPlan")
    @ODataField(odataName = "FloorPlan")
    @Nullable
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private FloorPlan toFloorPlan;
    /**
     * Navigation property <b>Products</b> for <b>Shelf</b> to multiple <b>Product</b>.
     * 
     */
    @SerializedName("Products")
    @JsonProperty("Products")
    @ODataField(odataName = "Products")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<Product> toProducts;
    /**
     * Use with available fluent helpers to apply the <b>FloorPlan</b> navigation property to query operations.
     * 
     */
    public final static ShelfOneToOneLink<FloorPlan> TO_FLOOR_PLAN = new ShelfOneToOneLink<FloorPlan>("FloorPlan");
    /**
     * Use with available fluent helpers to apply the <b>Products</b> navigation property to query operations.
     * 
     */
    public final static ShelfLink<Product> TO_PRODUCTS = new ShelfLink<Product>("Products");

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
    protected Map<String, Object> getKey() {
        final Map<String, Object> result = Maps.newHashMap();
        result.put("Id", getId());
        return result;
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

    /**
     * Use with available fluent helpers to apply an extension field to query operations.
     * 
     * @param fieldName
     *     The name of the extension field as returned by the OData service.
     * @param <T>
     *     The type of the extension field when performing value comparisons.
     * @param fieldType
     *     The Java type to use for the extension field when performing value comparisons.
     * @return
     *     A representation of an extension field from this entity.
     */
    @Nonnull
    public static<T >ShelfField<T> field(
        @Nonnull
        final String fieldName,
        @Nonnull
        final Class<T> fieldType) {
        return new ShelfField<T>(fieldName);
    }

    /**
     * Use with available fluent helpers to apply an extension field to query operations.
     * 
     * @param typeConverter
     *     A TypeConverter<T, DomainT> instance whose first generic type matches the Java type of the field
     * @param fieldName
     *     The name of the extension field as returned by the OData service.
     * @param <T>
     *     The type of the extension field when performing value comparisons.
     * @param <DomainT>
     *     The type of the extension field as returned by the OData service.
     * @return
     *     A representation of an extension field from this entity, holding a reference to the given TypeConverter.
     */
    @Nonnull
    public static<T,DomainT >ShelfField<T> field(
        @Nonnull
        final String fieldName,
        @Nonnull
        final TypeConverter<T, DomainT> typeConverter) {
        return new ShelfField<T>(fieldName, typeConverter);
    }

    @Override
    @Nullable
    public Destination getDestinationForFetch() {
        return super.getDestinationForFetch();
    }

    @Override
    protected void setServicePathForFetch(
        @Nullable
        final String servicePathForFetch) {
        super.setServicePathForFetch(servicePathForFetch);
    }

    @Override
    public void attachToService(
        @Nullable
        final String servicePath,
        @Nonnull
        final Destination destination) {
        super.attachToService(servicePath, destination);
    }

    @Override
    protected String getDefaultServicePath() {
        return (testcomparison.services.SdkGroceryStoreService.DEFAULT_SERVICE_PATH);
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
     * Fetches the <b>FloorPlan</b> entity (one to one) associated with this entity. This corresponds to the OData navigation property <b>FloorPlan</b>.
     * <p>
     * Please note: This method will not cache or persist the query results.
     * 
     * @return
     *     The single associated <b>FloorPlan</b> entity, or {@code null} if an entity is not associated. 
     * @throws ODataException
     *     If the entity is unmanaged, i.e. it has not been retrieved using the OData VDM's services and therefore has no ERP configuration context assigned. An entity is managed if it has been either retrieved using the VDM's services or returned from the VDM's services as the result of a CREATE or UPDATE call. 
     */
    @Nullable
    public FloorPlan fetchFloorPlan() {
        return fetchFieldAsSingle("FloorPlan", FloorPlan.class);
    }

    /**
     * Retrieval of associated <b>FloorPlan</b> entity (one to one). This corresponds to the OData navigation property <b>FloorPlan</b>.
     * <p>
     * If the navigation property <b>FloorPlan</b> of a queried <b>Shelf</b> is operated lazily, an <b>ODataException</b> can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the first time and it has not yet been loaded, an OData query will be run in order to load the missing information and its result will get cached for future invocations.
     * 
     * @return
     *     List of associated <b>FloorPlan</b> entity.
     * @throws ODataException
     *     If the entity is unmanaged, i.e. it has not been retrieved using the OData VDM's services and therefore has no ERP configuration context assigned. An entity is managed if it has been either retrieved using the VDM's services or returned from the VDM's services as the result of a CREATE or UPDATE call. 
     */
    @Nullable
    public FloorPlan getFloorPlanOrFetch() {
        if (toFloorPlan == null) {
            toFloorPlan = fetchFloorPlan();
        }
        return toFloorPlan;
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
     * Fetches the <b>Product</b> entities (one to many) associated with this entity. This corresponds to the OData navigation property <b>Products</b>.
     * <p>
     * Please note: This method will not cache or persist the query results.
     * 
     * @return
     *     List containing one or more associated <b>Product</b> entities. If no entities are associated then an empty list is returned. 
     * @throws ODataException
     *     If the entity is unmanaged, i.e. it has not been retrieved using the OData VDM's services and therefore has no ERP configuration context assigned. An entity is managed if it has been either retrieved using the VDM's services or returned from the VDM's services as the result of a CREATE or UPDATE call. 
     */
    @Nonnull
    public List<Product> fetchProducts() {
        return fetchFieldAsList("Products", Product.class);
    }

    /**
     * Retrieval of associated <b>Product</b> entities (one to many). This corresponds to the OData navigation property <b>Products</b>.
     * <p>
     * If the navigation property <b>Products</b> of a queried <b>Shelf</b> is operated lazily, an <b>ODataException</b> can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the first time and it has not yet been loaded, an OData query will be run in order to load the missing information and its result will get cached for future invocations.
     * 
     * @return
     *     List of associated <b>Product</b> entities.
     * @throws ODataException
     *     If the entity is unmanaged, i.e. it has not been retrieved using the OData VDM's services and therefore has no ERP configuration context assigned. An entity is managed if it has been either retrieved using the VDM's services or returned from the VDM's services as the result of a CREATE or UPDATE call. 
     */
    @Nonnull
    public List<Product> getProductsOrFetch() {
        if (toProducts == null) {
            toProducts = fetchProducts();
        }
        return toProducts;
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
