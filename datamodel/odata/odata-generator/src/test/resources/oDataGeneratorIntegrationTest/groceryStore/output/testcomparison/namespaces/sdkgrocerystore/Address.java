/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.sdkgrocerystore;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmEntity;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataField;
import com.sap.cloud.sdk.s4hana.datamodel.odata.annotation.Key;
import com.sap.cloud.sdk.typeconverter.TypeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import testcomparison.namespaces.sdkgrocerystore.field.AddressField;
import testcomparison.namespaces.sdkgrocerystore.selectable.AddressSelectable;


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
@JsonAdapter(com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory.class)
public class Address
    extends VdmEntity<Address>
{

    /**
     * Selector for all available fields of Address.
     * 
     */
    public final static AddressSelectable ALL_FIELDS = () -> "*";
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
    public final static AddressField<Integer> ID = new AddressField<Integer>("Id");
    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>Street</b></p>
     * 
     * @return
     *     The street contained in this entity.
     */
    @SerializedName("Street")
    @JsonProperty("Street")
    @Nullable
    @ODataField(odataName = "Street")
    private String street;
    /**
     * Use with available fluent helpers to apply the <b>Street</b> field to query operations.
     * 
     */
    public final static AddressField<String> STREET = new AddressField<String>("Street");
    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>City</b></p>
     * 
     * @return
     *     The city contained in this entity.
     */
    @SerializedName("City")
    @JsonProperty("City")
    @Nullable
    @ODataField(odataName = "City")
    private String city;
    /**
     * Use with available fluent helpers to apply the <b>City</b> field to query operations.
     * 
     */
    public final static AddressField<String> CITY = new AddressField<String>("City");
    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>State</b></p>
     * 
     * @return
     *     The state contained in this entity.
     */
    @SerializedName("State")
    @JsonProperty("State")
    @Nullable
    @ODataField(odataName = "State")
    private String state;
    /**
     * Use with available fluent helpers to apply the <b>State</b> field to query operations.
     * 
     */
    public final static AddressField<String> STATE = new AddressField<String>("State");
    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>Country</b></p>
     * 
     * @return
     *     The country contained in this entity.
     */
    @SerializedName("Country")
    @JsonProperty("Country")
    @Nullable
    @ODataField(odataName = "Country")
    private String country;
    /**
     * Use with available fluent helpers to apply the <b>Country</b> field to query operations.
     * 
     */
    public final static AddressField<String> COUNTRY = new AddressField<String>("Country");
    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>PostalCode</b></p>
     * 
     * @return
     *     The postalCode contained in this entity.
     */
    @SerializedName("PostalCode")
    @JsonProperty("PostalCode")
    @Nullable
    @ODataField(odataName = "PostalCode")
    private String postalCode;
    /**
     * Use with available fluent helpers to apply the <b>PostalCode</b> field to query operations.
     * 
     */
    public final static AddressField<String> POSTAL_CODE = new AddressField<String>("PostalCode");
    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>Latitude</b></p>
     * 
     * @return
     *     The latitude contained in this entity.
     */
    @SerializedName("Latitude")
    @JsonProperty("Latitude")
    @Nullable
    @ODataField(odataName = "Latitude")
    private Double latitude;
    /**
     * Use with available fluent helpers to apply the <b>Latitude</b> field to query operations.
     * 
     */
    public final static AddressField<Double> LATITUDE = new AddressField<Double>("Latitude");
    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>Longitude</b></p>
     * 
     * @return
     *     The longitude contained in this entity.
     */
    @SerializedName("Longitude")
    @JsonProperty("Longitude")
    @Nullable
    @ODataField(odataName = "Longitude")
    private Double longitude;
    /**
     * Use with available fluent helpers to apply the <b>Longitude</b> field to query operations.
     * 
     */
    public final static AddressField<Double> LONGITUDE = new AddressField<Double>("Longitude");

    @Nonnull
    @Override
    public Class<Address> getType() {
        return Address.class;
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
     * Constraints: none<p>Original property name from the Odata EDM: <b>Street</b></p>
     * 
     * @param street
     *     The street to set.
     */
    public void setStreet(
        @Nullable
        final String street) {
        rememberChangedField("Street", this.street);
        this.street = street;
    }

    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>City</b></p>
     * 
     * @param city
     *     The city to set.
     */
    public void setCity(
        @Nullable
        final String city) {
        rememberChangedField("City", this.city);
        this.city = city;
    }

    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>State</b></p>
     * 
     * @param state
     *     The state to set.
     */
    public void setState(
        @Nullable
        final String state) {
        rememberChangedField("State", this.state);
        this.state = state;
    }

    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>Country</b></p>
     * 
     * @param country
     *     The country to set.
     */
    public void setCountry(
        @Nullable
        final String country) {
        rememberChangedField("Country", this.country);
        this.country = country;
    }

    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>PostalCode</b></p>
     * 
     * @param postalCode
     *     The postalCode to set.
     */
    public void setPostalCode(
        @Nullable
        final String postalCode) {
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
    protected String getEntityCollection() {
        return "Addresses";
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
        values.put("Street", getStreet());
        values.put("City", getCity());
        values.put("State", getState());
        values.put("Country", getCountry());
        values.put("PostalCode", getPostalCode());
        values.put("Latitude", getLatitude());
        values.put("Longitude", getLongitude());
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
            if (values.containsKey("Street")) {
                final Object value = values.remove("Street");
                if ((value == null)||(!value.equals(getStreet()))) {
                    setStreet(((String) value));
                }
            }
            if (values.containsKey("City")) {
                final Object value = values.remove("City");
                if ((value == null)||(!value.equals(getCity()))) {
                    setCity(((String) value));
                }
            }
            if (values.containsKey("State")) {
                final Object value = values.remove("State");
                if ((value == null)||(!value.equals(getState()))) {
                    setState(((String) value));
                }
            }
            if (values.containsKey("Country")) {
                final Object value = values.remove("Country");
                if ((value == null)||(!value.equals(getCountry()))) {
                    setCountry(((String) value));
                }
            }
            if (values.containsKey("PostalCode")) {
                final Object value = values.remove("PostalCode");
                if ((value == null)||(!value.equals(getPostalCode()))) {
                    setPostalCode(((String) value));
                }
            }
            if (values.containsKey("Latitude")) {
                final Object value = values.remove("Latitude");
                if ((value == null)||(!value.equals(getLatitude()))) {
                    setLatitude(((Double) value));
                }
            }
            if (values.containsKey("Longitude")) {
                final Object value = values.remove("Longitude");
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
    public static<T >AddressField<T> field(
        @Nonnull
        final String fieldName,
        @Nonnull
        final Class<T> fieldType) {
        return new AddressField<T>(fieldName);
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
    public static<T,DomainT >AddressField<T> field(
        @Nonnull
        final String fieldName,
        @Nonnull
        final TypeConverter<T, DomainT> typeConverter) {
        return new AddressField<T>(fieldName, typeConverter);
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

}
