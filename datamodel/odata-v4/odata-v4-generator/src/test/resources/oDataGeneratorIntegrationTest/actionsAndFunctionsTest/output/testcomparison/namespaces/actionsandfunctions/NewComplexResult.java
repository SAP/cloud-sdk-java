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
 * <p>Original complex type name from the Odata EDM: <b>NewComplexResult</b></p>
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
public class NewComplexResult
    extends VdmComplex<NewComplexResult>
{

    @Getter
    private final java.lang.String odataType = "API_ACTIONS_FUNCTIONS_TEST_CASE.NewComplexResult";
    /**
     * Constraints: Not nullable, Maximum length: 40 <p>Original property name from the Odata EDM: <b>Foo</b></p>
     * 
     * @return
     *     The foo contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("Foo")
    private java.lang.String foo;
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.String<NewComplexResult> FOO = new com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.String<NewComplexResult>(NewComplexResult.class, "Foo");
    /**
     * Constraints: Not nullable, Maximum length: 30 <p>Original property name from the Odata EDM: <b>Bar</b></p>
     * 
     * @return
     *     The bar contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("Bar")
    private java.lang.String bar;
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.String<NewComplexResult> BAR = new com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.String<NewComplexResult>(NewComplexResult.class, "Bar");

    @Nonnull
    @Override
    public Class<NewComplexResult> getType() {
        return NewComplexResult.class;
    }

    @Nonnull
    @Override
    protected Map<java.lang.String, Object> toMapOfFields() {
        final Map<java.lang.String, Object> cloudSdkValues = super.toMapOfFields();
        cloudSdkValues.put("Foo", getFoo());
        cloudSdkValues.put("Bar", getBar());
        return cloudSdkValues;
    }

    @Override
    protected void fromMap(final Map<java.lang.String, Object> inputValues) {
        final Map<java.lang.String, Object> cloudSdkValues = Maps.newHashMap(inputValues);
        // simple properties
        {
            if (cloudSdkValues.containsKey("Foo")) {
                final Object value = cloudSdkValues.remove("Foo");
                if ((value == null)||(!value.equals(getFoo()))) {
                    setFoo(((java.lang.String) value));
                }
            }
            if (cloudSdkValues.containsKey("Bar")) {
                final Object value = cloudSdkValues.remove("Bar");
                if ((value == null)||(!value.equals(getBar()))) {
                    setBar(((java.lang.String) value));
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
     * Constraints: Not nullable, Maximum length: 40 <p>Original property name from the Odata EDM: <b>Foo</b></p>
     * 
     * @param foo
     *     The foo to set.
     */
    public void setFoo(
        @Nullable
        final java.lang.String foo) {
        rememberChangedField("Foo", this.foo);
        this.foo = foo;
    }

    /**
     * Constraints: Not nullable, Maximum length: 30 <p>Original property name from the Odata EDM: <b>Bar</b></p>
     * 
     * @param bar
     *     The bar to set.
     */
    public void setBar(
        @Nullable
        final java.lang.String bar) {
        rememberChangedField("Bar", this.bar);
        this.bar = bar;
    }

}
