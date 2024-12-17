/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test;

import java.util.Map;
import java.util.UUID;
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
import testcomparison.services.TestService;


/**
 * <p>Original entity name from the Odata EDM: <b>A_TestEntityLvl2SingleLinkType</b></p>
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
public class TestEntityLvl2SingleLink
    extends VdmEntity<TestEntityLvl2SingleLink>
    implements VdmEntitySet
{

    @Getter
    private final java.lang.String odataType = "API_TEST_SRV.A_TestEntityLvl2SingleLinkType";
    /**
     * Selector for all available fields of TestEntityLvl2SingleLink.
     * 
     */
    public final static SimpleProperty<TestEntityLvl2SingleLink> ALL_FIELDS = all();
    /**
     * (Key Field) Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>KeyProperty</b></p>
     * 
     * @return
     *     The keyProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("KeyProperty")
    private java.lang.String keyProperty;
    public final static SimpleProperty.String<TestEntityLvl2SingleLink> KEY_PROPERTY = new SimpleProperty.String<TestEntityLvl2SingleLink>(TestEntityLvl2SingleLink.class, "KeyProperty");
    /**
     * Constraints: Nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>StringProperty</b></p>
     * 
     * @return
     *     The stringProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("StringProperty")
    private java.lang.String stringProperty;
    public final static SimpleProperty.String<TestEntityLvl2SingleLink> STRING_PROPERTY = new SimpleProperty.String<TestEntityLvl2SingleLink>(TestEntityLvl2SingleLink.class, "StringProperty");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>BooleanProperty</b></p>
     * 
     * @return
     *     The booleanProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("BooleanProperty")
    private java.lang.Boolean booleanProperty;
    public final static SimpleProperty.Boolean<TestEntityLvl2SingleLink> BOOLEAN_PROPERTY = new SimpleProperty.Boolean<TestEntityLvl2SingleLink>(TestEntityLvl2SingleLink.class, "BooleanProperty");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>GuidProperty</b></p>
     * 
     * @return
     *     The guidProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("GuidProperty")
    private UUID guidProperty;
    public final static SimpleProperty.Guid<TestEntityLvl2SingleLink> GUID_PROPERTY = new SimpleProperty.Guid<TestEntityLvl2SingleLink>(TestEntityLvl2SingleLink.class, "GuidProperty");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>Int16Property</b></p>
     * 
     * @return
     *     The int16Property contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("Int16Property")
    private Short int16Property;
    public final static SimpleProperty.NumericInteger<TestEntityLvl2SingleLink> INT16_PROPERTY = new SimpleProperty.NumericInteger<TestEntityLvl2SingleLink>(TestEntityLvl2SingleLink.class, "Int16Property");

    @Nonnull
    @Override
    public Class<TestEntityLvl2SingleLink> getType() {
        return TestEntityLvl2SingleLink.class;
    }

    /**
     * (Key Field) Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>KeyProperty</b></p>
     * 
     * @param keyProperty
     *     The keyProperty to set.
     */
    public void setKeyProperty(
        @Nullable
        final java.lang.String keyProperty) {
        rememberChangedField("KeyProperty", this.keyProperty);
        this.keyProperty = keyProperty;
    }

    /**
     * Constraints: Nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>StringProperty</b></p>
     * 
     * @param stringProperty
     *     The stringProperty to set.
     */
    public void setStringProperty(
        @Nullable
        final java.lang.String stringProperty) {
        rememberChangedField("StringProperty", this.stringProperty);
        this.stringProperty = stringProperty;
    }

    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>BooleanProperty</b></p>
     * 
     * @param booleanProperty
     *     The booleanProperty to set.
     */
    public void setBooleanProperty(
        @Nullable
        final java.lang.Boolean booleanProperty) {
        rememberChangedField("BooleanProperty", this.booleanProperty);
        this.booleanProperty = booleanProperty;
    }

    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>GuidProperty</b></p>
     * 
     * @param guidProperty
     *     The guidProperty to set.
     */
    public void setGuidProperty(
        @Nullable
        final UUID guidProperty) {
        rememberChangedField("GuidProperty", this.guidProperty);
        this.guidProperty = guidProperty;
    }

    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>Int16Property</b></p>
     * 
     * @param int16Property
     *     The int16Property to set.
     */
    public void setInt16Property(
        @Nullable
        final Short int16Property) {
        rememberChangedField("Int16Property", this.int16Property);
        this.int16Property = int16Property;
    }

    @Override
    protected java.lang.String getEntityCollection() {
        return "A_TestEntityLvl2SingleLink";
    }

    @Nonnull
    @Override
    protected ODataEntityKey getKey() {
        final ODataEntityKey entityKey = super.getKey();
        entityKey.addKeyProperty("KeyProperty", getKeyProperty());
        return entityKey;
    }

    @Nonnull
    @Override
    protected Map<java.lang.String, Object> toMapOfFields() {
        final Map<java.lang.String, Object> cloudSdkValues = super.toMapOfFields();
        cloudSdkValues.put("KeyProperty", getKeyProperty());
        cloudSdkValues.put("StringProperty", getStringProperty());
        cloudSdkValues.put("BooleanProperty", getBooleanProperty());
        cloudSdkValues.put("GuidProperty", getGuidProperty());
        cloudSdkValues.put("Int16Property", getInt16Property());
        return cloudSdkValues;
    }

    @Override
    protected void fromMap(final Map<java.lang.String, Object> inputValues) {
        final Map<java.lang.String, Object> cloudSdkValues = Maps.newHashMap(inputValues);
        // simple properties
        {
            if (cloudSdkValues.containsKey("KeyProperty")) {
                final Object value = cloudSdkValues.remove("KeyProperty");
                if ((value == null)||(!value.equals(getKeyProperty()))) {
                    setKeyProperty(((java.lang.String) value));
                }
            }
            if (cloudSdkValues.containsKey("StringProperty")) {
                final Object value = cloudSdkValues.remove("StringProperty");
                if ((value == null)||(!value.equals(getStringProperty()))) {
                    setStringProperty(((java.lang.String) value));
                }
            }
            if (cloudSdkValues.containsKey("BooleanProperty")) {
                final Object value = cloudSdkValues.remove("BooleanProperty");
                if ((value == null)||(!value.equals(getBooleanProperty()))) {
                    setBooleanProperty(((java.lang.Boolean) value));
                }
            }
            if (cloudSdkValues.containsKey("GuidProperty")) {
                final Object value = cloudSdkValues.remove("GuidProperty");
                if ((value == null)||(!value.equals(getGuidProperty()))) {
                    setGuidProperty(((UUID) value));
                }
            }
            if (cloudSdkValues.containsKey("Int16Property")) {
                final Object value = cloudSdkValues.remove("Int16Property");
                if ((value == null)||(!value.equals(getInt16Property()))) {
                    setInt16Property(((Short) value));
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
        return TestService.DEFAULT_SERVICE_PATH;
    }

}
