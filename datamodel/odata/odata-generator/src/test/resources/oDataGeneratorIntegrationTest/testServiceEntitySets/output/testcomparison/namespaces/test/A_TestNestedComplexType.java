/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test;

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
 * <p>Original complex type name from the Odata EDM: <b>A_TestNestedComplexType</b></p>
 * 
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(doNotUseGetters = true, callSuper = true)
@EqualsAndHashCode(doNotUseGetters = true, callSuper = true)
@JsonAdapter(com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory.class)
public class A_TestNestedComplexType
    extends VdmComplex<A_TestNestedComplexType>
{

    /**
     * Constraints: Not nullable, Maximum length: 10 <p>Original property from the Odata EDM: <b>StringProperty</b></p>
     * 
     * @param stringProperty
     *     
     */
    @SerializedName("StringProperty")
    @JsonProperty("StringProperty")
    @Nullable
    @ODataField(odataName = "StringProperty")
    private String stringProperty;
    /**
     * Constraints: Nullable<p>Original property from the Odata EDM: <b>ComplexTypeProperty</b></p>
     * 
     * @param complexTypeProperty
     *     
     */
    @SerializedName("ComplexTypeProperty")
    @JsonProperty("ComplexTypeProperty")
    @Nullable
    @ODataField(odataName = "ComplexTypeProperty")
    private A_TestLvl2NestedComplexType complexTypeProperty;

    @Nonnull
    @Override
    public Class<A_TestNestedComplexType> getType() {
        return A_TestNestedComplexType.class;
    }

    @Nonnull
    @Override
    protected Map<String, Object> toMapOfFields() {
        final Map<String, Object> values = super.toMapOfFields();
        values.put("StringProperty", getStringProperty());
        values.put("ComplexTypeProperty", getComplexTypeProperty());
        return values;
    }

    @Override
    protected void fromMap(final Map<String, Object> inputValues) {
        final Map<String, Object> values = Maps.newHashMap(inputValues);
        // simple properties
        {
            if (values.containsKey("StringProperty")) {
                final Object value = values.remove("StringProperty");
                if ((value == null)||(!value.equals(getStringProperty()))) {
                    setStringProperty(((String) value));
                }
            }
        }
        // structured properties
        {
            if (values.containsKey("ComplexTypeProperty")) {
                final Object value = values.remove("ComplexTypeProperty");
                if (value instanceof Map) {
                    if (getComplexTypeProperty() == null) {
                        setComplexTypeProperty(new A_TestLvl2NestedComplexType());
                    }
                    @SuppressWarnings("unchecked")
                    final Map<String, Object> inputMap = ((Map<String, Object> ) value);
                    getComplexTypeProperty().fromMap(inputMap);
                }
                if ((value == null)&&(getComplexTypeProperty()!= null)) {
                    setComplexTypeProperty(null);
                }
            }
        }
        // navigation properties
        {
        }
        super.fromMap(values);
    }

    @Nonnull
    @Override
    protected Map<String, Object> getKey() {
        final Map<String, Object> result = Maps.newHashMap();
        return result;
    }

    /**
     * Constraints: Not nullable, Maximum length: 10 <p>Original property from the Odata EDM: <b>StringProperty</b></p>
     * 
     * @param stringProperty
     *     The stringProperty to set.
     */
    public void setStringProperty(
        @Nullable
        final String stringProperty) {
        rememberChangedField("StringProperty", this.stringProperty);
        this.stringProperty = stringProperty;
    }

    /**
     * Constraints: Nullable<p>Original property from the Odata EDM: <b>ComplexTypeProperty</b></p>
     * 
     * @param complexTypeProperty
     *     The complexTypeProperty to set.
     */
    public void setComplexTypeProperty(
        @Nullable
        final A_TestLvl2NestedComplexType complexTypeProperty) {
        rememberChangedField("ComplexTypeProperty", this.complexTypeProperty);
        this.complexTypeProperty = complexTypeProperty;
    }

}
