/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
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
 * <p>Original entity name from the Odata EDM: <b>A_TestEntityMultiLinkType</b></p>
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
public class TestEntityMultiLink
    extends VdmEntity<TestEntityMultiLink>
    implements VdmEntitySet
{

    @Getter
    private final java.lang.String odataType = "API_TEST_SRV.A_TestEntityMultiLinkType";
    /**
     * Selector for all available fields of TestEntityMultiLink.
     * 
     */
    public final static SimpleProperty<TestEntityMultiLink> ALL_FIELDS = all();
    /**
     * (Key Field) Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>KeyProperty</b></p>
     * 
     * @return
     *     The keyProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("KeyProperty")
    private java.lang.String keyProperty;
    public final static SimpleProperty.String<TestEntityMultiLink> KEY_PROPERTY = new SimpleProperty.String<TestEntityMultiLink>(TestEntityMultiLink.class, "KeyProperty");
    /**
     * Constraints: Nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>StringProperty</b></p>
     * 
     * @return
     *     The stringProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("StringProperty")
    private java.lang.String stringProperty;
    public final static SimpleProperty.String<TestEntityMultiLink> STRING_PROPERTY = new SimpleProperty.String<TestEntityMultiLink>(TestEntityMultiLink.class, "StringProperty");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>BooleanProperty</b></p>
     * 
     * @return
     *     The booleanProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("BooleanProperty")
    private java.lang.Boolean booleanProperty;
    public final static SimpleProperty.Boolean<TestEntityMultiLink> BOOLEAN_PROPERTY = new SimpleProperty.Boolean<TestEntityMultiLink>(TestEntityMultiLink.class, "BooleanProperty");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>GuidProperty</b></p>
     * 
     * @return
     *     The guidProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("GuidProperty")
    private UUID guidProperty;
    public final static SimpleProperty.Guid<TestEntityMultiLink> GUID_PROPERTY = new SimpleProperty.Guid<TestEntityMultiLink>(TestEntityMultiLink.class, "GuidProperty");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>Int16Property</b></p>
     * 
     * @return
     *     The int16Property contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("Int16Property")
    private Short int16Property;
    public final static SimpleProperty.NumericInteger<TestEntityMultiLink> INT16_PROPERTY = new SimpleProperty.NumericInteger<TestEntityMultiLink>(TestEntityMultiLink.class, "Int16Property");
    /**
     * Navigation property <b>to_MultiLink</b> for <b>TestEntityMultiLink</b> to multiple <b>TestEntityLvl2MultiLink</b>.
     * 
     */
    @ElementName("to_MultiLink")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<TestEntityLvl2MultiLink> toMultiLink;
    /**
     * Navigation property <b>to_SingleLink</b> for <b>TestEntityMultiLink</b> to single <b>TestEntityLvl2SingleLink</b>.
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
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Collection<TestEntityMultiLink, TestEntityLvl2MultiLink> TO_MULTI_LINK = new com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Collection<TestEntityMultiLink, TestEntityLvl2MultiLink>(TestEntityMultiLink.class, "to_MultiLink", TestEntityLvl2MultiLink.class);
    /**
     * Use with available request builders to apply the <b>to_SingleLink</b> navigation property to query operations.
     * 
     */
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Single<TestEntityMultiLink, TestEntityLvl2SingleLink> TO_SINGLE_LINK = new com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Single<TestEntityMultiLink, TestEntityLvl2SingleLink>(TestEntityMultiLink.class, "to_SingleLink", TestEntityLvl2SingleLink.class);

    @Nonnull
    @Override
    public Class<TestEntityMultiLink> getType() {
        return TestEntityMultiLink.class;
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
        return "A_TestEntityMultiLink";
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
        final Map<java.lang.String, Object> values = super.toMapOfFields();
        values.put("KeyProperty", getKeyProperty());
        values.put("StringProperty", getStringProperty());
        values.put("BooleanProperty", getBooleanProperty());
        values.put("GuidProperty", getGuidProperty());
        values.put("Int16Property", getInt16Property());
        return values;
    }

    @Override
    protected void fromMap(final Map<java.lang.String, Object> inputValues) {
        final Map<java.lang.String, Object> values = Maps.newHashMap(inputValues);
        // simple properties
        {
            if (values.containsKey("KeyProperty")) {
                final Object value = values.remove("KeyProperty");
                if ((value == null)||(!value.equals(getKeyProperty()))) {
                    setKeyProperty(((java.lang.String) value));
                }
            }
            if (values.containsKey("StringProperty")) {
                final Object value = values.remove("StringProperty");
                if ((value == null)||(!value.equals(getStringProperty()))) {
                    setStringProperty(((java.lang.String) value));
                }
            }
            if (values.containsKey("BooleanProperty")) {
                final Object value = values.remove("BooleanProperty");
                if ((value == null)||(!value.equals(getBooleanProperty()))) {
                    setBooleanProperty(((java.lang.Boolean) value));
                }
            }
            if (values.containsKey("GuidProperty")) {
                final Object value = values.remove("GuidProperty");
                if ((value == null)||(!value.equals(getGuidProperty()))) {
                    setGuidProperty(((UUID) value));
                }
            }
            if (values.containsKey("Int16Property")) {
                final Object value = values.remove("Int16Property");
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
            if ((values).containsKey("to_MultiLink")) {
                final Object value = (values).remove("to_MultiLink");
                if (value instanceof Iterable) {
                    if (toMultiLink == null) {
                        toMultiLink = Lists.newArrayList();
                    } else {
                        toMultiLink = Lists.newArrayList(toMultiLink);
                    }
                    int i = 0;
                    for (Object item: ((Iterable<?> ) value)) {
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
            if ((values).containsKey("to_SingleLink")) {
                final Object value = (values).remove("to_SingleLink");
                if (value instanceof Map) {
                    if (toSingleLink == null) {
                        toSingleLink = new TestEntityLvl2SingleLink();
                    }
                    @SuppressWarnings("unchecked")
                    final Map<java.lang.String, Object> inputMap = ((Map<java.lang.String, Object> ) value);
                    toSingleLink.fromMap(inputMap);
                }
            }
        }
        super.fromMap(values);
    }

    @Override
    protected java.lang.String getDefaultServicePath() {
        return TestService.DEFAULT_SERVICE_PATH;
    }

    @Nonnull
    @Override
    protected Map<java.lang.String, Object> toMapOfNavigationProperties() {
        final Map<java.lang.String, Object> values = super.toMapOfNavigationProperties();
        if (toMultiLink!= null) {
            (values).put("to_MultiLink", toMultiLink);
        }
        if (toSingleLink!= null) {
            (values).put("to_SingleLink", toSingleLink);
        }
        return values;
    }

    /**
     * Retrieval of associated <b>TestEntityLvl2MultiLink</b> entities (one to many). This corresponds to the OData navigation property <b>to_MultiLink</b>.
     * <p>
     * If the navigation property for an entity <b>TestEntityMultiLink</b> has not been resolved yet, this method will <b>not query</b> further information. Instead its <code>Option</code> result state will be <code>empty</code>.
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
     * If the navigation property <b>to_MultiLink</b> of a queried <b>TestEntityMultiLink</b> is operated lazily, an <b>ODataException</b> can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the first time and it has not yet been loaded, an OData query will be run in order to load the missing information and its result will get cached for future invocations.
     * 
     * @param value
     *     List of <b>TestEntityLvl2MultiLink</b> entities.
     */
    public void setMultiLink(
        @Nonnull
        final List<TestEntityLvl2MultiLink> value) {
        if (toMultiLink == null) {
            toMultiLink = Lists.newArrayList();
        }
        toMultiLink.clear();
        toMultiLink.addAll(value);
    }

    /**
     * Adds elements to the list of associated <b>TestEntityLvl2MultiLink</b> entities. This corresponds to the OData navigation property <b>to_MultiLink</b>.
     * <p>
     * If the navigation property <b>to_MultiLink</b> of a queried <b>TestEntityMultiLink</b> is operated lazily, an <b>ODataException</b> can be thrown in case of an OData query error.
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
     * If the navigation property for an entity <b>TestEntityMultiLink</b> has not been resolved yet, this method will <b>not query</b> further information. Instead its <code>Option</code> result state will be <code>empty</code>.
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
     * @param value
     *     New <b>TestEntityLvl2SingleLink</b> entity.
     */
    public void setSingleLink(final TestEntityLvl2SingleLink value) {
        toSingleLink = value;
    }


    /**
     * Helper class to allow for fluent creation of TestEntityMultiLink instances.
     * 
     */
    public final static class TestEntityMultiLinkBuilder {

        private List<TestEntityLvl2MultiLink> toMultiLink = Lists.newArrayList();
        private TestEntityLvl2SingleLink toSingleLink;

        private TestEntityMultiLink.TestEntityMultiLinkBuilder toMultiLink(final List<TestEntityLvl2MultiLink> value) {
            toMultiLink.addAll(value);
            return this;
        }

        /**
         * Navigation property <b>to_MultiLink</b> for <b>TestEntityMultiLink</b> to multiple <b>TestEntityLvl2MultiLink</b>.
         * 
         * @param value
         *     The TestEntityLvl2MultiLinks to build this TestEntityMultiLink with.
         * @return
         *     This Builder to allow for a fluent interface.
         */
        @Nonnull
        public TestEntityMultiLink.TestEntityMultiLinkBuilder multiLink(TestEntityLvl2MultiLink... value) {
            return toMultiLink(Lists.newArrayList(value));
        }

        private TestEntityMultiLink.TestEntityMultiLinkBuilder toSingleLink(final TestEntityLvl2SingleLink value) {
            toSingleLink = value;
            return this;
        }

        /**
         * Navigation property <b>to_SingleLink</b> for <b>TestEntityMultiLink</b> to single <b>TestEntityLvl2SingleLink</b>.
         * 
         * @param value
         *     The TestEntityLvl2SingleLink to build this TestEntityMultiLink with.
         * @return
         *     This Builder to allow for a fluent interface.
         */
        @Nonnull
        public TestEntityMultiLink.TestEntityMultiLinkBuilder singleLink(final TestEntityLvl2SingleLink value) {
            return toSingleLink(value);
        }

    }

}
