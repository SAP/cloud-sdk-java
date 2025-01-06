/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
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
 * <p>Original entity name from the Odata EDM: <b>A_TestEntitySingleLinkType</b></p>
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
public class TestEntitySingleLink
    extends VdmEntity<TestEntitySingleLink>
    implements VdmEntitySet
{

    @Getter
    private final java.lang.String odataType = "API_TEST_SRV.A_TestEntitySingleLinkType";
    /**
     * Selector for all available fields of TestEntitySingleLink.
     * 
     */
    public final static SimpleProperty<TestEntitySingleLink> ALL_FIELDS = all();
    /**
     * (Key Field) Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>KeyProperty</b></p>
     * 
     * @return
     *     The keyProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("KeyProperty")
    private java.lang.String keyProperty;
    public final static SimpleProperty.String<TestEntitySingleLink> KEY_PROPERTY = new SimpleProperty.String<TestEntitySingleLink>(TestEntitySingleLink.class, "KeyProperty");
    /**
     * Constraints: Nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>StringProperty</b></p>
     * 
     * @return
     *     The stringProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("StringProperty")
    private java.lang.String stringProperty;
    public final static SimpleProperty.String<TestEntitySingleLink> STRING_PROPERTY = new SimpleProperty.String<TestEntitySingleLink>(TestEntitySingleLink.class, "StringProperty");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>BooleanProperty</b></p>
     * 
     * @return
     *     The booleanProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("BooleanProperty")
    private java.lang.Boolean booleanProperty;
    public final static SimpleProperty.Boolean<TestEntitySingleLink> BOOLEAN_PROPERTY = new SimpleProperty.Boolean<TestEntitySingleLink>(TestEntitySingleLink.class, "BooleanProperty");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>GuidProperty</b></p>
     * 
     * @return
     *     The guidProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("GuidProperty")
    private UUID guidProperty;
    public final static SimpleProperty.Guid<TestEntitySingleLink> GUID_PROPERTY = new SimpleProperty.Guid<TestEntitySingleLink>(TestEntitySingleLink.class, "GuidProperty");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>Int16Property</b></p>
     * 
     * @return
     *     The int16Property contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("Int16Property")
    private Short int16Property;
    public final static SimpleProperty.NumericInteger<TestEntitySingleLink> INT16_PROPERTY = new SimpleProperty.NumericInteger<TestEntitySingleLink>(TestEntitySingleLink.class, "Int16Property");
    /**
     * Navigation property <b>to_MultiLink</b> for <b>TestEntitySingleLink</b> to multiple <b>TestEntityLvl2MultiLink</b>.
     * 
     */
    @ElementName("to_MultiLink")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<TestEntityLvl2MultiLink> toMultiLink;
    /**
     * Navigation property <b>to_SingleLink</b> for <b>TestEntitySingleLink</b> to single <b>TestEntityLvl2SingleLink</b>.
     * 
     */
    @ElementName("to_SingleLink")
    @Nullable
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private TestEntityLvl2SingleLink toSingleLink;
    /**
     * Use with available request builders to apply the <b>to_MultiLink</b> navigation property to query operations.
     * 
     */
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Collection<TestEntitySingleLink, TestEntityLvl2MultiLink> TO_MULTI_LINK = new com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Collection<TestEntitySingleLink, TestEntityLvl2MultiLink>(TestEntitySingleLink.class, "to_MultiLink", TestEntityLvl2MultiLink.class);
    /**
     * Use with available request builders to apply the <b>to_SingleLink</b> navigation property to query operations.
     * 
     */
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Single<TestEntitySingleLink, TestEntityLvl2SingleLink> TO_SINGLE_LINK = new com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Single<TestEntitySingleLink, TestEntityLvl2SingleLink>(TestEntitySingleLink.class, "to_SingleLink", TestEntityLvl2SingleLink.class);

    @Nonnull
    @Override
    public Class<TestEntitySingleLink> getType() {
        return TestEntitySingleLink.class;
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
        return "A_TestEntitySingleLink";
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
        final Map<java.lang.String, Object> cloudSdkValues = Maps.newLinkedHashMap(inputValues);
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
            if ((cloudSdkValues).containsKey("to_MultiLink")) {
                final Object cloudSdkValue = (cloudSdkValues).remove("to_MultiLink");
                if (cloudSdkValue instanceof Iterable) {
                    if (toMultiLink == null) {
                        toMultiLink = Lists.newArrayList();
                    } else {
                        toMultiLink = Lists.newArrayList(toMultiLink);
                    }
                    int i = 0;
                    for (Object item: ((Iterable<?> ) cloudSdkValue)) {
                        if (!(item instanceof Map)) {
                            continue;
                        }
                        TestEntityLvl2MultiLink entity;
                        if (toMultiLink.size()>i) {
                            entity = toMultiLink.get(i);
                        } else {
                            entity = new TestEntityLvl2MultiLink();
                            toMultiLink.add(entity);
                        }
                        i = (i + 1);
                        @SuppressWarnings("unchecked")
                        final Map<java.lang.String, Object> inputMap = ((Map<java.lang.String, Object> ) item);
                        entity.fromMap(inputMap);
                    }
                }
            }
            if ((cloudSdkValues).containsKey("to_SingleLink")) {
                final Object cloudSdkValue = (cloudSdkValues).remove("to_SingleLink");
                if (cloudSdkValue instanceof Map) {
                    if (toSingleLink == null) {
                        toSingleLink = new TestEntityLvl2SingleLink();
                    }
                    @SuppressWarnings("unchecked")
                    final Map<java.lang.String, Object> inputMap = ((Map<java.lang.String, Object> ) cloudSdkValue);
                    toSingleLink.fromMap(inputMap);
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
        if (toMultiLink!= null) {
            (cloudSdkValues).put("to_MultiLink", toMultiLink);
        }
        if (toSingleLink!= null) {
            (cloudSdkValues).put("to_SingleLink", toSingleLink);
        }
        return cloudSdkValues;
    }

    /**
     * Retrieval of associated <b>TestEntityLvl2MultiLink</b> entities (one to many). This corresponds to the OData navigation property <b>to_MultiLink</b>.
     * <p>
     * If the navigation property for an entity <b>TestEntitySingleLink</b> has not been resolved yet, this method will <b>not query</b> further information. Instead its <code>Option</code> result state will be <code>empty</code>.
     * 
     * @return
     *     If the information for navigation property <b>to_MultiLink</b> is already loaded, the result will contain the <b>TestEntityLvl2MultiLink</b> entities. If not, an <code>Option</code> with result state <code>empty</code> is returned.
     */
    @Nonnull
    public Option<List<TestEntityLvl2MultiLink>> getMultiLinkIfPresent() {
        return Option.of(toMultiLink);
    }

    /**
     * Overwrites the list of associated <b>TestEntityLvl2MultiLink</b> entities for the loaded navigation property <b>to_MultiLink</b>.
     * <p>
     * If the navigation property <b>to_MultiLink</b> of a queried <b>TestEntitySingleLink</b> is operated lazily, an <b>ODataException</b> can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the first time and it has not yet been loaded, an OData query will be run in order to load the missing information and its result will get cached for future invocations.
     * 
     * @param cloudSdkValue
     *     List of <b>TestEntityLvl2MultiLink</b> entities.
     */
    public void setMultiLink(
        @Nonnull
        final List<TestEntityLvl2MultiLink> cloudSdkValue) {
        if (toMultiLink == null) {
            toMultiLink = Lists.newArrayList();
        }
        toMultiLink.clear();
        toMultiLink.addAll(cloudSdkValue);
    }

    /**
     * Adds elements to the list of associated <b>TestEntityLvl2MultiLink</b> entities. This corresponds to the OData navigation property <b>to_MultiLink</b>.
     * <p>
     * If the navigation property <b>to_MultiLink</b> of a queried <b>TestEntitySingleLink</b> is operated lazily, an <b>ODataException</b> can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the first time and it has not yet been loaded, an OData query will be run in order to load the missing information and its result will get cached for future invocations.
     * 
     * @param entity
     *     Array of <b>TestEntityLvl2MultiLink</b> entities.
     */
    public void addMultiLink(TestEntityLvl2MultiLink... entity) {
        if (toMultiLink == null) {
            toMultiLink = Lists.newArrayList();
        }
        toMultiLink.addAll(Lists.newArrayList(entity));
    }

    /**
     * Retrieval of associated <b>TestEntityLvl2SingleLink</b> entity (one to one). This corresponds to the OData navigation property <b>to_SingleLink</b>.
     * <p>
     * If the navigation property for an entity <b>TestEntitySingleLink</b> has not been resolved yet, this method will <b>not query</b> further information. Instead its <code>Option</code> result state will be <code>empty</code>.
     * 
     * @return
     *     If the information for navigation property <b>to_SingleLink</b> is already loaded, the result will contain the <b>TestEntityLvl2SingleLink</b> entity. If not, an <code>Option</code> with result state <code>empty</code> is returned.
     */
    @Nonnull
    public Option<TestEntityLvl2SingleLink> getSingleLinkIfPresent() {
        return Option.of(toSingleLink);
    }

    /**
     * Overwrites the associated <b>TestEntityLvl2SingleLink</b> entity for the loaded navigation property <b>to_SingleLink</b>.
     * 
     * @param cloudSdkValue
     *     New <b>TestEntityLvl2SingleLink</b> entity.
     */
    public void setSingleLink(final TestEntityLvl2SingleLink cloudSdkValue) {
        toSingleLink = cloudSdkValue;
    }


    /**
     * Helper class to allow for fluent creation of TestEntitySingleLink instances.
     * 
     */
    public final static class TestEntitySingleLinkBuilder {

        private List<TestEntityLvl2MultiLink> toMultiLink = Lists.newArrayList();
        private TestEntityLvl2SingleLink toSingleLink;

        private TestEntitySingleLink.TestEntitySingleLinkBuilder toMultiLink(final List<TestEntityLvl2MultiLink> cloudSdkValue) {
            toMultiLink.addAll(cloudSdkValue);
            return this;
        }

        /**
         * Navigation property <b>to_MultiLink</b> for <b>TestEntitySingleLink</b> to multiple <b>TestEntityLvl2MultiLink</b>.
         * 
         * @param cloudSdkValue
         *     The TestEntityLvl2MultiLinks to build this TestEntitySingleLink with.
         * @return
         *     This Builder to allow for a fluent interface.
         */
        @Nonnull
        public TestEntitySingleLink.TestEntitySingleLinkBuilder multiLink(TestEntityLvl2MultiLink... cloudSdkValue) {
            return toMultiLink(Lists.newArrayList(cloudSdkValue));
        }

        private TestEntitySingleLink.TestEntitySingleLinkBuilder toSingleLink(final TestEntityLvl2SingleLink cloudSdkValue) {
            toSingleLink = cloudSdkValue;
            return this;
        }

        /**
         * Navigation property <b>to_SingleLink</b> for <b>TestEntitySingleLink</b> to single <b>TestEntityLvl2SingleLink</b>.
         * 
         * @param cloudSdkValue
         *     The TestEntityLvl2SingleLink to build this TestEntitySingleLink with.
         * @return
         *     This Builder to allow for a fluent interface.
         */
        @Nonnull
        public TestEntitySingleLink.TestEntitySingleLinkBuilder singleLink(final TestEntityLvl2SingleLink cloudSdkValue) {
            return toSingleLink(cloudSdkValue);
        }

    }

}
