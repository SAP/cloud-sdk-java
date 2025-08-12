/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.multipleentitysets;

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
import testcomparison.services.MultipleEntitySetsService;


/**
 * <p>Original entity name from the Odata EDM: <b>A_FooTypeType</b></p>
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
public class FooType
    extends VdmEntity<FooType>
    implements VdmEntitySet
{

    @Getter
    private final java.lang.String odataType = "Schema.A_FooTypeType";
    /**
     * Selector for all available fields of FooType.
     * 
     */
    public final static SimpleProperty<FooType> ALL_FIELDS = all();
    /**
     * (Key Field) Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>Foo</b></p>
     * 
     * @return
     *     The foo contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("Foo")
    private java.lang.String foo;
    public final static SimpleProperty.String<FooType> FOO = new SimpleProperty.String<FooType>(FooType.class, "Foo");
    /**
     * Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>Type</b></p>
     * 
     * @return
     *     The type_2 contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("Type")
    private java.lang.String type_2;
    public final static SimpleProperty.String<FooType> TYPE_2 = new SimpleProperty.String<FooType>(FooType.class, "Type");

    @Nonnull
    @Override
    public Class<FooType> getType() {
        return FooType.class;
    }

    /**
     * (Key Field) Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>Foo</b></p>
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
     * Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>Type</b></p>
     * 
     * @param type_2
     *     The type_2 to set.
     */
    public void setType_2(
        @Nullable
        final java.lang.String type_2) {
        rememberChangedField("Type", this.type_2);
        this.type_2 = type_2;
    }

    @Override
    protected java.lang.String getEntityCollection() {
        return "A_FooType";
    }

    @Nonnull
    @Override
    protected ODataEntityKey getKey() {
        final ODataEntityKey entityKey = super.getKey();
        entityKey.addKeyProperty("Foo", getFoo());
        return entityKey;
    }

    @Nonnull
    @Override
    protected Map<java.lang.String, Object> toMapOfFields() {
        final Map<java.lang.String, Object> cloudSdkValues = super.toMapOfFields();
        cloudSdkValues.put("Foo", getFoo());
        cloudSdkValues.put("Type", getType_2());
        return cloudSdkValues;
    }

    @Override
    protected void fromMap(final Map<java.lang.String, Object> inputValues) {
        final Map<java.lang.String, Object> cloudSdkValues = Maps.newLinkedHashMap(inputValues);
        // simple properties
        {
            if (cloudSdkValues.containsKey("Foo")) {
                final Object cloudSdkValue = cloudSdkValues.remove("Foo");
                if ((cloudSdkValue == null)||(!cloudSdkValue.equals(getFoo()))) {
                    setFoo(((java.lang.String) cloudSdkValue));
                }
            }
            if (cloudSdkValues.containsKey("Type")) {
                final Object cloudSdkValue = cloudSdkValues.remove("Type");
                if ((cloudSdkValue == null)||(!cloudSdkValue.equals(getType_2()))) {
                    setType_2(((java.lang.String) cloudSdkValue));
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
        return MultipleEntitySetsService.DEFAULT_SERVICE_PATH;
    }

}
