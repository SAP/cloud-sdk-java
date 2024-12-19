/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.actionsandfunctions;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Maps;
import com.google.gson.annotations.JsonAdapter;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataEntityKey;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmComplex;
import com.sap.cloud.sdk.result.ElementName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * <p>Original complex type name from the Odata EDM: <b>Address</b></p>
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
    extends VdmComplex<Address>
{

    @Getter
    private final java.lang.String odataType = "API_ACTIONS_FUNCTIONS_TEST_CASE.Address";
    /**
     * Constraints: Not nullable, Maximum length: 40 <p>Original property name from the Odata EDM: <b>Street</b></p>
     * 
     * @return
     *     The street contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("Street")
    private java.lang.String street;
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.String<Address> STREET = new com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.String<Address>(Address.class, "Street");
    /**
     * Constraints: Not nullable, Maximum length: 30 <p>Original property name from the Odata EDM: <b>City</b></p>
     * 
     * @return
     *     The city contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("City")
    private java.lang.String city;
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.String<Address> CITY = new com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.String<Address>(Address.class, "City");

    @Nonnull
    @Override
    public Class<Address> getType() {
        return Address.class;
    }

    @Nonnull
    @Override
    protected Map<java.lang.String, Object> toMapOfFields() {
        final Map<java.lang.String, Object> cloudSdkValues = super.toMapOfFields();
        cloudSdkValues.put("Street", getStreet());
        cloudSdkValues.put("City", getCity());
        return cloudSdkValues;
    }

    @Override
    protected void fromMap(final Map<java.lang.String, Object> inputValues) {
        final Map<java.lang.String, Object> cloudSdkValues = Maps.newLinkedHashMap(inputValues);
        // simple properties
        {
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
    protected ODataEntityKey getKey() {
        final ODataEntityKey entityKey = super.getKey();
        return entityKey;
    }

    /**
     * Constraints: Not nullable, Maximum length: 40 <p>Original property name from the Odata EDM: <b>Street</b></p>
     * 
     * @param street
     *     The street to set.
     */
    public void setStreet(
        @Nullable
        final java.lang.String street) {
        rememberChangedField("Street", this.street);
        this.street = street;
    }

    /**
     * Constraints: Not nullable, Maximum length: 30 <p>Original property name from the Odata EDM: <b>City</b></p>
     * 
     * @param city
     *     The city to set.
     */
    public void setCity(
        @Nullable
        final java.lang.String city) {
        rememberChangedField("City", this.city);
        this.city = city;
    }

}
