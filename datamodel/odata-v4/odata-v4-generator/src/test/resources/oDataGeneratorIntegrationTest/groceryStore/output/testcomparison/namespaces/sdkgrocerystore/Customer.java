/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.sdkgrocerystore;

import java.util.Collection;
import java.util.HashMap;
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
 * <p>Original entity name from the Odata EDM: <b>Customer</b></p>
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
public class Customer
    extends VdmEntity<Customer>
    implements VdmEntitySet
{

    @Getter
    private final java.lang.String odataType = "com.sap.cloud.sdk.store.grocery.Customer";
    /**
     * Selector for all available fields of Customer.
     * 
     */
    public final static SimpleProperty<Customer> ALL_FIELDS = all();
    /**
     * (Key Field) Constraints: Not nullable<p>Original property name from the Odata EDM: <b>Id</b></p>
     * 
     * @return
     *     ID of the customer.
     */
    @Nullable
    @ElementName("Id")
    private Integer id;
    public final static SimpleProperty.NumericInteger<Customer> ID = new SimpleProperty.NumericInteger<Customer>(Customer.class, "Id");
    /**
     * Constraints: Not nullable, Maximum length: 100 <p>Original property name from the Odata EDM: <b>Name</b></p>
     * 
     * @return
     *     Name of the customer.
     */
    @Nullable
    @ElementName("Name")
    private java.lang.String name;
    public final static SimpleProperty.String<Customer> NAME = new SimpleProperty.String<Customer>(Customer.class, "Name");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>Email</b></p>
     * 
     * @return
     *     Email address of the customer.
     */
    @Nullable
    @ElementName("Email")
    private java.lang.String email;
    public final static SimpleProperty.String<Customer> EMAIL = new SimpleProperty.String<Customer>(Customer.class, "Email");
    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>AddressId</b></p>
     * 
     * @return
     *     ID of the customer's address.
     */
    @Nullable
    @ElementName("AddressId")
    private Integer addressId;
    public final static SimpleProperty.NumericInteger<Customer> ADDRESS_ID = new SimpleProperty.NumericInteger<Customer>(Customer.class, "AddressId");
    /**
     * Navigation property <b>Address</b> for <b>Customer</b> to single <b>Address</b>.
     * 
     */
    @ElementName("Address")
    @Nullable
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Address toAddress;
    /**
     * Use with available request builders to apply the <b>Address</b> navigation property to query operations.
     * 
     */
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Single<Customer, Address> TO_ADDRESS = new com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Single<Customer, Address>(Customer.class, "Address", Address.class);

    @Nonnull
    @Override
    public Class<Customer> getType() {
        return Customer.class;
    }

    /**
     * (Key Field) Constraints: Not nullable<p>Original property name from the Odata EDM: <b>Id</b></p>
     * 
     * @param id
     *     ID of the customer.
     */
    public void setId(
        @Nullable
        final Integer id) {
        rememberChangedField("Id", this.id);
        this.id = id;
    }

    /**
     * Constraints: Not nullable, Maximum length: 100 <p>Original property name from the Odata EDM: <b>Name</b></p>
     * 
     * @param name
     *     Name of the customer.
     */
    public void setName(
        @Nullable
        final java.lang.String name) {
        rememberChangedField("Name", this.name);
        this.name = name;
    }

    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>Email</b></p>
     * 
     * @param email
     *     Email address of the customer.
     */
    public void setEmail(
        @Nullable
        final java.lang.String email) {
        rememberChangedField("Email", this.email);
        this.email = email;
    }

    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>AddressId</b></p>
     * 
     * @param addressId
     *     ID of the customer's address.
     */
    public void setAddressId(
        @Nullable
        final Integer addressId) {
        rememberChangedField("AddressId", this.addressId);
        this.addressId = addressId;
    }

    @Override
    protected java.lang.String getEntityCollection() {
        return "Customers";
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
    protected Map<java.lang.String, Object> toMapOfFields() {
        final Map<java.lang.String, Object> cloudSdkValues = super.toMapOfFields();
        cloudSdkValues.put("Id", getId());
        cloudSdkValues.put("Name", getName());
        cloudSdkValues.put("Email", getEmail());
        cloudSdkValues.put("AddressId", getAddressId());
        return cloudSdkValues;
    }

    @Override
    protected void fromMap(final Map<java.lang.String, Object> inputValues) {
        final Map<java.lang.String, Object> cloudSdkValues = Maps.newLinkedHashMap(inputValues);
        // simple properties
        {
            if (cloudSdkValues.containsKey("Id")) {
                final Object value = cloudSdkValues.remove("Id");
                if ((value == null)||(!value.equals(getId()))) {
                    setId(((Integer) value));
                }
            }
            if (cloudSdkValues.containsKey("Name")) {
                final Object value = cloudSdkValues.remove("Name");
                if ((value == null)||(!value.equals(getName()))) {
                    setName(((java.lang.String) value));
                }
            }
            if (cloudSdkValues.containsKey("Email")) {
                final Object value = cloudSdkValues.remove("Email");
                if ((value == null)||(!value.equals(getEmail()))) {
                    setEmail(((java.lang.String) value));
                }
            }
            if (cloudSdkValues.containsKey("AddressId")) {
                final Object value = cloudSdkValues.remove("AddressId");
                if ((value == null)||(!value.equals(getAddressId()))) {
                    setAddressId(((Integer) value));
                }
            }
        }
        // structured properties
        {
        }
        // navigation properties
        {
            if ((cloudSdkValues).containsKey("Address")) {
                final Object cloudSdkValue = (cloudSdkValues).remove("Address");
                if (cloudSdkValue instanceof Map) {
                    if (toAddress == null) {
                        toAddress = new Address();
                    }
                    @SuppressWarnings("unchecked")
                    final Map<java.lang.String, Object> inputMap = ((Map<java.lang.String, Object> ) cloudSdkValue);
                    toAddress.fromMap(inputMap);
                }
            }
        }
        super.fromMap(cloudSdkValues);
    }

    @Override
    protected java.lang.String getDefaultServicePath() {
        return SdkGroceryStoreService.DEFAULT_SERVICE_PATH;
    }

    @Nonnull
    @Override
    protected Map<java.lang.String, Object> toMapOfNavigationProperties() {
        final Map<java.lang.String, Object> cloudSdkValues = super.toMapOfNavigationProperties();
        if (toAddress!= null) {
            (cloudSdkValues).put("Address", toAddress);
        }
        return cloudSdkValues;
    }

    /**
     * Retrieval of associated <b>Address</b> entity (one to one). This corresponds to the OData navigation property <b>Address</b>.
     * <p>
     * If the navigation property for an entity <b>Customer</b> has not been resolved yet, this method will <b>not query</b> further information. Instead its <code>Option</code> result state will be <code>empty</code>.
     * 
     * @return
     *     If the information for navigation property <b>Address</b> is already loaded, the result will contain the <b>Address</b> entity. If not, an <code>Option</code> with result state <code>empty</code> is returned.
     */
    @Nonnull
    public Option<Address> getAddressIfPresent() {
        return Option.of(toAddress);
    }

    /**
     * Overwrites the associated <b>Address</b> entity for the loaded navigation property <b>Address</b>.
     * 
     * @param cloudSdkValue
     *     New <b>Address</b> entity.
     */
    public void setAddress(final Address cloudSdkValue) {
        toAddress = cloudSdkValue;
    }

    /**
     * Action that can be applied to any entity object of this class.</p>
     * 
     * @param quantity
     *     Constraints: Not nullable<p>Original parameter name from the Odata EDM: <b>Quantity</b></p>
     * @param productId
     *     Constraints: Not nullable<p>Original parameter name from the Odata EDM: <b>ProductId</b></p>
     * @return
     *     Action object prepared with the given parameters to be applied to any entity object of this class.</p> To execute it use the {@code service.forEntity(entity).applyAction(thisAction)} API.
     */
    @Nonnull
    public static com.sap.cloud.sdk.datamodel.odatav4.core.BoundAction.SingleToSingle<Customer, Void> orderProduct(
        @Nonnull
        final Integer productId,
        @Nonnull
        final Integer quantity) {
        final Map<java.lang.String, Object> parameters = new HashMap<java.lang.String, Object>();
        parameters.put("ProductId", productId);
        parameters.put("Quantity", quantity);
        return new com.sap.cloud.sdk.datamodel.odatav4.core.BoundAction.SingleToSingle<Customer, Void>(Customer.class, Void.class, "com.sap.cloud.sdk.store.grocery.OrderProduct", parameters);
    }

    /**
     * Action that can be applied to any entity object of this class.</p>
     * 
     * @param receipts
     *     Constraints: Nullable<p>Original parameter name from the Odata EDM: <b>Receipts</b></p>
     * @param productCategories
     *     Constraints: Nullable<p>Original parameter name from the Odata EDM: <b>ProductCategories</b></p>
     * @param dateRange
     *     Constraints: Nullable<p>Original parameter name from the Odata EDM: <b>DateRange</b></p>
     * @param productNames
     *     Constraints: Nullable<p>Original parameter name from the Odata EDM: <b>ProductNames</b></p>
     * @return
     *     Action object prepared with the given parameters to be applied to any entity object of this class.</p> To execute it use the {@code service.forEntity(entity).applyAction(thisAction)} API.
     */
    @Nonnull
    public static com.sap.cloud.sdk.datamodel.odatav4.core.BoundAction.SingleToCollection<Customer, PurchaseHistoryItem> filterPurchaseHistory(
        @Nullable
        final Collection<Receipt> receipts,
        @Nullable
        final Collection<java.lang.String> productNames,
        @Nullable
        final Collection<ProductCategory> productCategories,
        @Nullable
        final DateRange dateRange) {
        final Map<java.lang.String, Object> parameters = new HashMap<java.lang.String, Object>();
        parameters.put("Receipts", receipts);
        parameters.put("ProductNames", productNames);
        parameters.put("ProductCategories", productCategories);
        parameters.put("DateRange", dateRange);
        return new com.sap.cloud.sdk.datamodel.odatav4.core.BoundAction.SingleToCollection<Customer, PurchaseHistoryItem>(Customer.class, PurchaseHistoryItem.class, "com.sap.cloud.sdk.store.grocery.FilterPurchaseHistory", parameters);
    }


    /**
     * Helper class to allow for fluent creation of Customer instances.
     * 
     */
    public final static class CustomerBuilder {

        private Address toAddress;

        private Customer.CustomerBuilder toAddress(final Address cloudSdkValue) {
            toAddress = cloudSdkValue;
            return this;
        }

        /**
         * Navigation property <b>Address</b> for <b>Customer</b> to single <b>Address</b>.
         * 
         * @param cloudSdkValue
         *     The Address to build this Customer with.
         * @return
         *     This Builder to allow for a fluent interface.
         */
        @Nonnull
        public Customer.CustomerBuilder address(final Address cloudSdkValue) {
            return toAddress(cloudSdkValue);
        }

    }

}
