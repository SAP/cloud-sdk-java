/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.sdkgrocerystore;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import testcomparison.services.SdkGroceryStoreService;


/**
 * <p>Original entity name from the Odata EDM: <b>Address</b></p>
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
public class Address
    extends VdmEntity<Address>
    implements VdmEntitySet
{

    @Getter
    private final java.lang.String odataType = "com.sap.cloud.sdk.store.grocery.Address";
    /**
     * Selector for all available fields of Address.
     * 
     */
    public final static SimpleProperty<Address> ALL_FIELDS = all();
    /**
     * (Key Field) Constraints: Not nullable<p>Original property name from the Odata EDM: <b>Id</b></p>
     * 
     * @return
     *     ID of the address.
     */
    @Nullable
    @ElementName("Id")
    private Integer id;
    public final static SimpleProperty.NumericInteger<Address> ID = new SimpleProperty.NumericInteger<Address>(Address.class, "Id");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>Street</b></p>
     * 
     * @return
     *     Street of the address.
     */
    @Nullable
    @ElementName("Street")
    private java.lang.String street;
    public final static SimpleProperty.String<Address> STREET = new SimpleProperty.String<Address>(Address.class, "Street");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>City</b></p>
     * 
     * @return
     *     City of the address.
     */
    @Nullable
    @ElementName("City")
    private java.lang.String city;
    public final static SimpleProperty.String<Address> CITY = new SimpleProperty.String<Address>(Address.class, "City");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>State</b></p>
     * 
     * @return
     *     State of the address.
     */
    @Nullable
    @ElementName("State")
    private java.lang.String state;
    public final static SimpleProperty.String<Address> STATE = new SimpleProperty.String<Address>(Address.class, "State");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>Country</b></p>
     * 
     * @return
     *     The country contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("Country")
    private java.lang.String country;
    public final static SimpleProperty.String<Address> COUNTRY = new SimpleProperty.String<Address>(Address.class, "Country");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>PostalCode</b></p>
     * 
     * @return
     *     The postalCode contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("PostalCode")
    private java.lang.String postalCode;
    public final static SimpleProperty.String<Address> POSTAL_CODE = new SimpleProperty.String<Address>(Address.class, "PostalCode");
    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>Latitude</b></p>
     * 
     * @return
     *     The latitude contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("Latitude")
    private Double latitude;
    public final static SimpleProperty.NumericDecimal<Address> LATITUDE = new SimpleProperty.NumericDecimal<Address>(Address.class, "Latitude");
    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>Longitude</b></p>
     * 
     * @return
     *     The longitude contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("Longitude")
    private Double longitude;
    public final static SimpleProperty.NumericDecimal<Address> LONGITUDE = new SimpleProperty.NumericDecimal<Address>(Address.class, "Longitude");

    @Nonnull
    @Override
    public Class<Address> getType() {
        return Address.class;
    }

    /**
     * (Key Field) Constraints: Not nullable<p>Original property name from the Odata EDM: <b>Id</b></p>
     * 
     * @param id
     *     ID of the address.
     */
    public void setId(
        @Nullable
        final Integer id) {
        rememberChangedField("Id", this.id);
        this.id = id;
    }

    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>Street</b></p>
     * 
     * @param street
     *     Street of the address.
     */
    public void setStreet(
        @Nullable
        final java.lang.String street) {
        rememberChangedField("Street", this.street);
        this.street = street;
    }

    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>City</b></p>
     * 
     * @param city
     *     City of the address.
     */
    public void setCity(
        @Nullable
        final java.lang.String city) {
        rememberChangedField("City", this.city);
        this.city = city;
    }

    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>State</b></p>
     * 
     * @param state
     *     State of the address.
     */
    public void setState(
        @Nullable
        final java.lang.String state) {
        rememberChangedField("State", this.state);
        this.state = state;
    }

    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>Country</b></p>
     * 
     * @param country
     *     The country to set.
     */
    public void setCountry(
        @Nullable
        final java.lang.String country) {
        rememberChangedField("Country", this.country);
        this.country = country;
    }

    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>PostalCode</b></p>
     * 
     * @param postalCode
     *     The postalCode to set.
     */
    public void setPostalCode(
        @Nullable
        final java.lang.String postalCode) {
        rememberChangedField("PostalCode", this.postalCode);
        this.postalCode = postalCode;
    }

    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>Latitude</b></p>
     * 
     * @param latitude
     *     The latitude to set.
     */
    public void setLatitude(
        @Nullable
        final Double latitude) {
        rememberChangedField("Latitude", this.latitude);
        this.latitude = latitude;
    }

    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>Longitude</b></p>
     * 
     * @param longitude
     *     The longitude to set.
     */
    public void setLongitude(
        @Nullable
        final Double longitude) {
        rememberChangedField("Longitude", this.longitude);
        this.longitude = longitude;
    }

    @Override
    protected java.lang.String getEntityCollection() {
        return "Addresses";
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
        cloudSdkValues.put("Street", getStreet());
        cloudSdkValues.put("City", getCity());
        cloudSdkValues.put("State", getState());
        cloudSdkValues.put("Country", getCountry());
        cloudSdkValues.put("PostalCode", getPostalCode());
        cloudSdkValues.put("Latitude", getLatitude());
        cloudSdkValues.put("Longitude", getLongitude());
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
            if (cloudSdkValues.containsKey("Street")) {
                final Object value = cloudSdkValues.remove("Street");
                if ((value == null)||(!value.equals(getStreet()))) {
                    setStreet(((java.lang.String) value));
                }
            }
            if (cloudSdkValues.containsKey("City")) {
                final Object value = cloudSdkValues.remove("City");
                if ((value == null)||(!value.equals(getCity()))) {
                    setCity(((java.lang.String) value));
                }
            }
            if (cloudSdkValues.containsKey("State")) {
                final Object value = cloudSdkValues.remove("State");
                if ((value == null)||(!value.equals(getState()))) {
                    setState(((java.lang.String) value));
                }
            }
            if (cloudSdkValues.containsKey("Country")) {
                final Object value = cloudSdkValues.remove("Country");
                if ((value == null)||(!value.equals(getCountry()))) {
                    setCountry(((java.lang.String) value));
                }
            }
            if (cloudSdkValues.containsKey("PostalCode")) {
                final Object value = cloudSdkValues.remove("PostalCode");
                if ((value == null)||(!value.equals(getPostalCode()))) {
                    setPostalCode(((java.lang.String) value));
                }
            }
            if (cloudSdkValues.containsKey("Latitude")) {
                final Object value = cloudSdkValues.remove("Latitude");
                if ((value == null)||(!value.equals(getLatitude()))) {
                    setLatitude(((Double) value));
                }
            }
            if (cloudSdkValues.containsKey("Longitude")) {
                final Object value = cloudSdkValues.remove("Longitude");
                if ((value == null)||(!value.equals(getLongitude()))) {
                    setLongitude(((Double) value));
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

    @Override
    protected java.lang.String getDefaultServicePath() {
        return SdkGroceryStoreService.DEFAULT_SERVICE_PATH;
    }

}
