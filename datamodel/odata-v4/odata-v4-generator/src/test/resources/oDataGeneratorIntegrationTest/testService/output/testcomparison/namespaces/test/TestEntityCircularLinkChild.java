/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
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
 * <p>Original entity name from the Odata EDM: <b>A_TestEntityCircularLinkChildType</b></p>
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
public class TestEntityCircularLinkChild
    extends VdmEntity<TestEntityCircularLinkChild>
    implements VdmEntitySet
{

    @Getter
    private final java.lang.String odataType = "API_TEST_SRV.A_TestEntityCircularLinkChildType";
    /**
     * Selector for all available fields of TestEntityCircularLinkChild.
     * 
     */
    public final static SimpleProperty<TestEntityCircularLinkChild> ALL_FIELDS = all();
    /**
     * (Key Field) Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>KeyProperty</b></p>
     * 
     * @return
     *     The keyProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("KeyProperty")
    private java.lang.String keyProperty;
    public final static SimpleProperty.String<TestEntityCircularLinkChild> KEY_PROPERTY = new SimpleProperty.String<TestEntityCircularLinkChild>(TestEntityCircularLinkChild.class, "KeyProperty");
    /**
     * Navigation property <b>to_Parent</b> for <b>TestEntityCircularLinkChild</b> to single <b>TestEntityCircularLinkParent</b>.
     * 
     */
    @ElementName("to_Parent")
    @Nullable
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private TestEntityCircularLinkParent toParent;
    /**
     * Use with available request builders to apply the <b>to_Parent</b> navigation property to query operations.
     * 
     */
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Single<TestEntityCircularLinkChild, TestEntityCircularLinkParent> TO_PARENT = new com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Single<TestEntityCircularLinkChild, TestEntityCircularLinkParent>(TestEntityCircularLinkChild.class, "to_Parent", TestEntityCircularLinkParent.class);

    @Nonnull
    @Override
    public Class<TestEntityCircularLinkChild> getType() {
        return TestEntityCircularLinkChild.class;
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
        return "A_TestEntityCircularLinkChild";
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
            if ((cloudSdkValues).containsKey("to_Parent")) {
                final Object cloudSdkValue = (cloudSdkValues).remove("to_Parent");
                if (cloudSdkValue instanceof Map) {
                    if (toParent == null) {
                        toParent = new TestEntityCircularLinkParent();
                    }
                    @SuppressWarnings("unchecked")
                    final Map<java.lang.String, Object> inputMap = ((Map<java.lang.String, Object> ) cloudSdkValue);
                    toParent.fromMap(inputMap);
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
        if (toParent!= null) {
            (cloudSdkValues).put("to_Parent", toParent);
        }
        return cloudSdkValues;
    }

    /**
     * Retrieval of associated <b>TestEntityCircularLinkParent</b> entity (one to one). This corresponds to the OData navigation property <b>to_Parent</b>.
     * <p>
     * If the navigation property for an entity <b>TestEntityCircularLinkChild</b> has not been resolved yet, this method will <b>not query</b> further information. Instead its <code>Option</code> result state will be <code>empty</code>.
     * 
     * @return
     *     If the information for navigation property <b>to_Parent</b> is already loaded, the result will contain the <b>TestEntityCircularLinkParent</b> entity. If not, an <code>Option</code> with result state <code>empty</code> is returned.
     */
    @Nonnull
    public Option<TestEntityCircularLinkParent> getParentIfPresent() {
        return Option.of(toParent);
    }

    /**
     * Overwrites the associated <b>TestEntityCircularLinkParent</b> entity for the loaded navigation property <b>to_Parent</b>.
     * 
     * @param cloudSdkValue
     *     New <b>TestEntityCircularLinkParent</b> entity.
     */
    public void setParent(final TestEntityCircularLinkParent cloudSdkValue) {
        toParent = cloudSdkValue;
    }


    /**
     * Helper class to allow for fluent creation of TestEntityCircularLinkChild instances.
     * 
     */
    public final static class TestEntityCircularLinkChildBuilder {

        private TestEntityCircularLinkParent toParent;

        private TestEntityCircularLinkChild.TestEntityCircularLinkChildBuilder toParent(final TestEntityCircularLinkParent cloudSdkValue) {
            toParent = cloudSdkValue;
            return this;
        }

        /**
         * Navigation property <b>to_Parent</b> for <b>TestEntityCircularLinkChild</b> to single <b>TestEntityCircularLinkParent</b>.
         * 
         * @param cloudSdkValue
         *     The TestEntityCircularLinkParent to build this TestEntityCircularLinkChild with.
         * @return
         *     This Builder to allow for a fluent interface.
         */
        @Nonnull
        public TestEntityCircularLinkChild.TestEntityCircularLinkChildBuilder parent(final TestEntityCircularLinkParent cloudSdkValue) {
            return toParent(cloudSdkValue);
        }

    }

}
