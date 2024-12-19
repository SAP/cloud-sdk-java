/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test;

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
import testcomparison.services.TestService;


/**
 * <p>Original entity name from the Odata EDM: <b>A_TestEntityCircularLinkParentType</b></p>
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
public class TestEntityCircularLinkParent
    extends VdmEntity<TestEntityCircularLinkParent>
    implements VdmEntitySet
{

    @Getter
    private final java.lang.String odataType = "API_TEST_SRV.A_TestEntityCircularLinkParentType";
    /**
     * Selector for all available fields of TestEntityCircularLinkParent.
     * 
     */
    public final static SimpleProperty<TestEntityCircularLinkParent> ALL_FIELDS = all();
    /**
     * (Key Field) Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>KeyProperty</b></p>
     * 
     * @return
     *     The keyProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("KeyProperty")
    private java.lang.String keyProperty;
    public final static SimpleProperty.String<TestEntityCircularLinkParent> KEY_PROPERTY = new SimpleProperty.String<TestEntityCircularLinkParent>(TestEntityCircularLinkParent.class, "KeyProperty");
    /**
     * Navigation property <b>to_Child</b> for <b>TestEntityCircularLinkParent</b> to single <b>TestEntityCircularLinkChild</b>.
     * 
     */
    @ElementName("to_Child")
    @Nullable
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private TestEntityCircularLinkChild toChild;
    /**
     * Use with available request builders to apply the <b>to_Child</b> navigation property to query operations.
     * 
     */
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Single<TestEntityCircularLinkParent, TestEntityCircularLinkChild> TO_CHILD = new com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Single<TestEntityCircularLinkParent, TestEntityCircularLinkChild>(TestEntityCircularLinkParent.class, "to_Child", TestEntityCircularLinkChild.class);

    @Nonnull
    @Override
    public Class<TestEntityCircularLinkParent> getType() {
        return TestEntityCircularLinkParent.class;
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

    @Override
    protected java.lang.String getEntityCollection() {
        return "A_TestEntityCircularLinkParent";
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
        return cloudSdkValues;
    }

    @Override
    protected void fromMap(final Map<java.lang.String, Object> inputValues) {
        final Map<java.lang.String, Object> cloudSdkValues = Maps.newLinkedHashMap(inputValues);
        // simple properties
        {
            if (cloudSdkValues.containsKey("KeyProperty")) {
                final Object value = cloudSdkValues.remove("KeyProperty");
                if ((value == null)||(!value.equals(getKeyProperty()))) {
                    setKeyProperty(((java.lang.String) value));
                }
            }
        }
        // structured properties
        {
        }
        // navigation properties
        {
            if ((cloudSdkValues).containsKey("to_Child")) {
                final Object cloudSdkValue = (cloudSdkValues).remove("to_Child");
                if (cloudSdkValue instanceof Map) {
                    if (toChild == null) {
                        toChild = new TestEntityCircularLinkChild();
                    }
                    @SuppressWarnings("unchecked")
                    final Map<java.lang.String, Object> inputMap = ((Map<java.lang.String, Object> ) cloudSdkValue);
                    toChild.fromMap(inputMap);
                }
            }
        }
        super.fromMap(cloudSdkValues);
    }

    @Override
    protected java.lang.String getDefaultServicePath() {
        return TestService.DEFAULT_SERVICE_PATH;
    }

    @Nonnull
    @Override
    protected Map<java.lang.String, Object> toMapOfNavigationProperties() {
        final Map<java.lang.String, Object> cloudSdkValues = super.toMapOfNavigationProperties();
        if (toChild!= null) {
            (cloudSdkValues).put("to_Child", toChild);
        }
        return cloudSdkValues;
    }

    /**
     * Retrieval of associated <b>TestEntityCircularLinkChild</b> entity (one to one). This corresponds to the OData navigation property <b>to_Child</b>.
     * <p>
     * If the navigation property for an entity <b>TestEntityCircularLinkParent</b> has not been resolved yet, this method will <b>not query</b> further information. Instead its <code>Option</code> result state will be <code>empty</code>.
     * 
     * @return
     *     If the information for navigation property <b>to_Child</b> is already loaded, the result will contain the <b>TestEntityCircularLinkChild</b> entity. If not, an <code>Option</code> with result state <code>empty</code> is returned.
     */
    @Nonnull
    public Option<TestEntityCircularLinkChild> getChildIfPresent() {
        return Option.of(toChild);
    }

    /**
     * Overwrites the associated <b>TestEntityCircularLinkChild</b> entity for the loaded navigation property <b>to_Child</b>.
     * 
     * @param cloudSdkValue
     *     New <b>TestEntityCircularLinkChild</b> entity.
     */
    public void setChild(final TestEntityCircularLinkChild cloudSdkValue) {
        toChild = cloudSdkValue;
    }


    /**
     * Helper class to allow for fluent creation of TestEntityCircularLinkParent instances.
     * 
     */
    public final static class TestEntityCircularLinkParentBuilder {

        private TestEntityCircularLinkChild toChild;

        private TestEntityCircularLinkParent.TestEntityCircularLinkParentBuilder toChild(final TestEntityCircularLinkChild cloudSdkValue) {
            toChild = cloudSdkValue;
            return this;
        }

        /**
         * Navigation property <b>to_Child</b> for <b>TestEntityCircularLinkParent</b> to single <b>TestEntityCircularLinkChild</b>.
         * 
         * @param cloudSdkValue
         *     The TestEntityCircularLinkChild to build this TestEntityCircularLinkParent with.
         * @return
         *     This Builder to allow for a fluent interface.
         */
        @Nonnull
        public TestEntityCircularLinkParent.TestEntityCircularLinkParentBuilder child(final TestEntityCircularLinkChild cloudSdkValue) {
            return toChild(cloudSdkValue);
        }

    }

}
