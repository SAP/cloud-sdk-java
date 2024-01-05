/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.sdkgrocerystore;

import java.math.BigDecimal;
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
import com.sap.cloud.sdk.datamodel.odata.helper.VdmMediaEntity;
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
import testcomparison.namespaces.sdkgrocerystore.field.ProductField;
import testcomparison.namespaces.sdkgrocerystore.link.ProductLink;
import testcomparison.namespaces.sdkgrocerystore.link.ProductOneToOneLink;
import testcomparison.namespaces.sdkgrocerystore.selectable.ProductSelectable;


/**
 * <p>Original entity name from the Odata EDM: <b>Product</b></p>
 * 
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(doNotUseGetters = true, callSuper = true)
@EqualsAndHashCode(doNotUseGetters = true, callSuper = true)
@JsonAdapter(com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory.class)
public class Product
    extends VdmMediaEntity<Product>
{

    /**
     * Selector for all available fields of Product.
     * 
     */
    public final static ProductSelectable ALL_FIELDS = () -> "*";
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
    public final static ProductField<Integer> ID = new ProductField<Integer>("Id");
    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>Name</b></p>
     * 
     * @return
     *     The name contained in this entity.
     */
    @SerializedName("Name")
    @JsonProperty("Name")
    @Nullable
    @ODataField(odataName = "Name")
    private String name;
    /**
     * Use with available fluent helpers to apply the <b>Name</b> field to query operations.
     * 
     */
    public final static ProductField<String> NAME = new ProductField<String>("Name");
    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>ShelfId</b></p>
     * 
     * @return
     *     The shelfId contained in this entity.
     */
    @SerializedName("ShelfId")
    @JsonProperty("ShelfId")
    @Nullable
    @ODataField(odataName = "ShelfId")
    private Integer shelfId;
    /**
     * Use with available fluent helpers to apply the <b>ShelfId</b> field to query operations.
     * 
     */
    public final static ProductField<Integer> SHELF_ID = new ProductField<Integer>("ShelfId");
    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>VendorId</b></p>
     * 
     * @return
     *     The vendorId contained in this entity.
     */
    @SerializedName("VendorId")
    @JsonProperty("VendorId")
    @Nullable
    @ODataField(odataName = "VendorId")
    private Integer vendorId;
    /**
     * Use with available fluent helpers to apply the <b>VendorId</b> field to query operations.
     * 
     */
    public final static ProductField<Integer> VENDOR_ID = new ProductField<Integer>("VendorId");
    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>Price</b></p>
     * 
     * @return
     *     The price contained in this entity.
     */
    @SerializedName("Price")
    @JsonProperty("Price")
    @Nullable
    @ODataField(odataName = "Price")
    private BigDecimal price;
    /**
     * Use with available fluent helpers to apply the <b>Price</b> field to query operations.
     * 
     */
    public final static ProductField<BigDecimal> PRICE = new ProductField<BigDecimal>("Price");
    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>Image</b></p>
     * 
     * @return
     *     The image contained in this entity.
     */
    @SerializedName("Image")
    @JsonProperty("Image")
    @Nullable
    @JsonAdapter(com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataBinaryAdapter.class)
    @ODataField(odataName = "Image")
    private byte[] image;
    /**
     * Use with available fluent helpers to apply the <b>Image</b> field to query operations.
     * 
     */
    public final static ProductField<byte[]> IMAGE = new ProductField<byte[]>("Image");
    /**
     * Navigation property <b>Vendor</b> for <b>Product</b> to single <b>Vendor</b>.
     * 
     */
    @SerializedName("Vendor")
    @JsonProperty("Vendor")
    @ODataField(odataName = "Vendor")
    @Nullable
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Vendor toVendor;
    /**
     * Navigation property <b>Shelf</b> for <b>Product</b> to multiple <b>Shelf</b>.
     * 
     */
    @SerializedName("Shelf")
    @JsonProperty("Shelf")
    @ODataField(odataName = "Shelf")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<Shelf> toShelf;
    /**
     * Use with available fluent helpers to apply the <b>Vendor</b> navigation property to query operations.
     * 
     */
    public final static ProductOneToOneLink<Vendor> TO_VENDOR = new ProductOneToOneLink<Vendor>("Vendor");
    /**
     * Use with available fluent helpers to apply the <b>Shelf</b> navigation property to query operations.
     * 
     */
    public final static ProductLink<Shelf> TO_SHELF = new ProductLink<Shelf>("Shelf");

    @Nonnull
    @Override
    public Class<Product> getType() {
        return Product.class;
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
     * Constraints: none<p>Original property name from the Odata EDM: <b>Name</b></p>
     * 
     * @param name
     *     The name to set.
     */
    public void setName(
        @Nullable
        final String name) {
        rememberChangedField("Name", this.name);
        this.name = name;
    }

    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>ShelfId</b></p>
     * 
     * @param shelfId
     *     The shelfId to set.
     */
    public void setShelfId(
        @Nullable
        final Integer shelfId) {
        rememberChangedField("ShelfId", this.shelfId);
        this.shelfId = shelfId;
    }

    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>VendorId</b></p>
     * 
     * @param vendorId
     *     The vendorId to set.
     */
    public void setVendorId(
        @Nullable
        final Integer vendorId) {
        rememberChangedField("VendorId", this.vendorId);
        this.vendorId = vendorId;
    }

    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>Price</b></p>
     * 
     * @param price
     *     The price to set.
     */
    public void setPrice(
        @Nullable
        final BigDecimal price) {
        rememberChangedField("Price", this.price);
        this.price = price;
    }

    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>Image</b></p>
     * 
     * @param image
     *     The image to set.
     */
    public void setImage(
        @Nullable
        final byte[] image) {
        rememberChangedField("Image", this.image);
        this.image = image;
    }

    @Override
    protected String getEntityCollection() {
        return "Products";
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
        values.put("Name", getName());
        values.put("ShelfId", getShelfId());
        values.put("VendorId", getVendorId());
        values.put("Price", getPrice());
        values.put("Image", getImage());
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
            if (values.containsKey("Name")) {
                final Object value = values.remove("Name");
                if ((value == null)||(!value.equals(getName()))) {
                    setName(((String) value));
                }
            }
            if (values.containsKey("ShelfId")) {
                final Object value = values.remove("ShelfId");
                if ((value == null)||(!value.equals(getShelfId()))) {
                    setShelfId(((Integer) value));
                }
            }
            if (values.containsKey("VendorId")) {
                final Object value = values.remove("VendorId");
                if ((value == null)||(!value.equals(getVendorId()))) {
                    setVendorId(((Integer) value));
                }
            }
            if (values.containsKey("Price")) {
                final Object value = values.remove("Price");
                if ((value == null)||(!value.equals(getPrice()))) {
                    setPrice(((BigDecimal) value));
                }
            }
            if (values.containsKey("Image")) {
                final Object value = values.remove("Image");
                if ((value == null)||(!value.equals(getImage()))) {
                    setImage(((byte[]) value));
                }
            }
        }
        // structured properties
        {
        }
        // navigation properties
        {
            if ((values).containsKey("Vendor")) {
                final Object value = (values).remove("Vendor");
                if (value instanceof Map) {
                    if (toVendor == null) {
                        toVendor = new Vendor();
                    }
                    @SuppressWarnings("unchecked")
                    final Map<String, Object> inputMap = ((Map<String, Object> ) value);
                    toVendor.fromMap(inputMap);
                }
            }
            if ((values).containsKey("Shelf")) {
                final Object value = (values).remove("Shelf");
                if (value instanceof Iterable) {
                    if (toShelf == null) {
                        toShelf = Lists.newArrayList();
                    } else {
                        toShelf = Lists.newArrayList(toShelf);
                    }
                    int i = 0;
                    for (Object item: ((Iterable<?> ) value)) {
                        if (!(item instanceof Map)) {
                            continue;
                        }
                        Shelf entity;
                        if (toShelf.size()>i) {
                            entity = toShelf.get(i);
                        } else {
                            entity = new Shelf();
                            toShelf.add(entity);
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
    public static<T >ProductField<T> field(
        @Nonnull
        final String fieldName,
        @Nonnull
        final Class<T> fieldType) {
        return new ProductField<T>(fieldName);
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
    public static<T,DomainT >ProductField<T> field(
        @Nonnull
        final String fieldName,
        @Nonnull
        final TypeConverter<T, DomainT> typeConverter) {
        return new ProductField<T>(fieldName, typeConverter);
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
        if (toVendor!= null) {
            (values).put("Vendor", toVendor);
        }
        if (toShelf!= null) {
            (values).put("Shelf", toShelf);
        }
        return values;
    }

    /**
     * Fetches the <b>Vendor</b> entity (one to one) associated with this entity. This corresponds to the OData navigation property <b>Vendor</b>.
     * <p>
     * Please note: This method will not cache or persist the query results.
     * 
     * @return
     *     The single associated <b>Vendor</b> entity, or {@code null} if an entity is not associated. 
     * @throws ODataException
     *     If the entity is unmanaged, i.e. it has not been retrieved using the OData VDM's services and therefore has no ERP configuration context assigned. An entity is managed if it has been either retrieved using the VDM's services or returned from the VDM's services as the result of a CREATE or UPDATE call. 
     */
    @Nullable
    public Vendor fetchVendor() {
        return fetchFieldAsSingle("Vendor", Vendor.class);
    }

    /**
     * Retrieval of associated <b>Vendor</b> entity (one to one). This corresponds to the OData navigation property <b>Vendor</b>.
     * <p>
     * If the navigation property <b>Vendor</b> of a queried <b>Product</b> is operated lazily, an <b>ODataException</b> can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the first time and it has not yet been loaded, an OData query will be run in order to load the missing information and its result will get cached for future invocations.
     * 
     * @return
     *     List of associated <b>Vendor</b> entity.
     * @throws ODataException
     *     If the entity is unmanaged, i.e. it has not been retrieved using the OData VDM's services and therefore has no ERP configuration context assigned. An entity is managed if it has been either retrieved using the VDM's services or returned from the VDM's services as the result of a CREATE or UPDATE call. 
     */
    @Nullable
    public Vendor getVendorOrFetch() {
        if (toVendor == null) {
            toVendor = fetchVendor();
        }
        return toVendor;
    }

    /**
     * Retrieval of associated <b>Vendor</b> entity (one to one). This corresponds to the OData navigation property <b>Vendor</b>.
     * <p>
     * If the navigation property for an entity <b>Product</b> has not been resolved yet, this method will <b>not query</b> further information. Instead its <code>Option</code> result state will be <code>empty</code>.
     * 
     * @return
     *     If the information for navigation property <b>Vendor</b> is already loaded, the result will contain the <b>Vendor</b> entity. If not, an <code>Option</code> with result state <code>empty</code> is returned.
     */
    @Nonnull
    public Option<Vendor> getVendorIfPresent() {
        return Option.of(toVendor);
    }

    /**
     * Overwrites the associated <b>Vendor</b> entity for the loaded navigation property <b>Vendor</b>.
     * 
     * @param value
     *     New <b>Vendor</b> entity.
     */
    public void setVendor(final Vendor value) {
        toVendor = value;
    }

    /**
     * Fetches the <b>Shelf</b> entities (one to many) associated with this entity. This corresponds to the OData navigation property <b>Shelf</b>.
     * <p>
     * Please note: This method will not cache or persist the query results.
     * 
     * @return
     *     List containing one or more associated <b>Shelf</b> entities. If no entities are associated then an empty list is returned. 
     * @throws ODataException
     *     If the entity is unmanaged, i.e. it has not been retrieved using the OData VDM's services and therefore has no ERP configuration context assigned. An entity is managed if it has been either retrieved using the VDM's services or returned from the VDM's services as the result of a CREATE or UPDATE call. 
     */
    @Nonnull
    public List<Shelf> fetchShelf() {
        return fetchFieldAsList("Shelf", Shelf.class);
    }

    /**
     * Retrieval of associated <b>Shelf</b> entities (one to many). This corresponds to the OData navigation property <b>Shelf</b>.
     * <p>
     * If the navigation property <b>Shelf</b> of a queried <b>Product</b> is operated lazily, an <b>ODataException</b> can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the first time and it has not yet been loaded, an OData query will be run in order to load the missing information and its result will get cached for future invocations.
     * 
     * @return
     *     List of associated <b>Shelf</b> entities.
     * @throws ODataException
     *     If the entity is unmanaged, i.e. it has not been retrieved using the OData VDM's services and therefore has no ERP configuration context assigned. An entity is managed if it has been either retrieved using the VDM's services or returned from the VDM's services as the result of a CREATE or UPDATE call. 
     */
    @Nonnull
    public List<Shelf> getShelfOrFetch() {
        if (toShelf == null) {
            toShelf = fetchShelf();
        }
        return toShelf;
    }

    /**
     * Retrieval of associated <b>Shelf</b> entities (one to many). This corresponds to the OData navigation property <b>Shelf</b>.
     * <p>
     * If the navigation property for an entity <b>Product</b> has not been resolved yet, this method will <b>not query</b> further information. Instead its <code>Option</code> result state will be <code>empty</code>.
     * 
     * @return
     *     If the information for navigation property <b>Shelf</b> is already loaded, the result will contain the <b>Shelf</b> entities. If not, an <code>Option</code> with result state <code>empty</code> is returned.
     */
    @Nonnull
    public Option<List<Shelf>> getShelfIfPresent() {
        return Option.of(toShelf);
    }

    /**
     * Overwrites the list of associated <b>Shelf</b> entities for the loaded navigation property <b>Shelf</b>.
     * <p>
     * If the navigation property <b>Shelf</b> of a queried <b>Product</b> is operated lazily, an <b>ODataException</b> can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the first time and it has not yet been loaded, an OData query will be run in order to load the missing information and its result will get cached for future invocations.
     * 
     * @param value
     *     List of <b>Shelf</b> entities.
     */
    public void setShelf(
        @Nonnull
        final List<Shelf> value) {
        if (toShelf == null) {
            toShelf = Lists.newArrayList();
        }
        toShelf.clear();
        toShelf.addAll(value);
    }

    /**
     * Adds elements to the list of associated <b>Shelf</b> entities. This corresponds to the OData navigation property <b>Shelf</b>.
     * <p>
     * If the navigation property <b>Shelf</b> of a queried <b>Product</b> is operated lazily, an <b>ODataException</b> can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the first time and it has not yet been loaded, an OData query will be run in order to load the missing information and its result will get cached for future invocations.
     * 
     * @param entity
     *     Array of <b>Shelf</b> entities.
     */
    public void addShelf(Shelf... entity) {
        if (toShelf == null) {
            toShelf = Lists.newArrayList();
        }
        toShelf.addAll(Lists.newArrayList(entity));
    }


    /**
     * Helper class to allow for fluent creation of Product instances.
     * 
     */
    public final static class ProductBuilder {

        private Vendor toVendor;
        private List<Shelf> toShelf = Lists.newArrayList();

        private Product.ProductBuilder toVendor(final Vendor value) {
            toVendor = value;
            return this;
        }

        /**
         * Navigation property <b>Vendor</b> for <b>Product</b> to single <b>Vendor</b>.
         * 
         * @param value
         *     The Vendor to build this Product with.
         * @return
         *     This Builder to allow for a fluent interface.
         */
        @Nonnull
        public Product.ProductBuilder vendor(final Vendor value) {
            return toVendor(value);
        }

        private Product.ProductBuilder toShelf(final List<Shelf> value) {
            toShelf.addAll(value);
            return this;
        }

        /**
         * Navigation property <b>Shelf</b> for <b>Product</b> to multiple <b>Shelf</b>.
         * 
         * @param value
         *     The Shelfs to build this Product with.
         * @return
         *     This Builder to allow for a fluent interface.
         */
        @Nonnull
        public Product.ProductBuilder shelf(Shelf... value) {
            return toShelf(Lists.newArrayList(value));
        }

    }

}
