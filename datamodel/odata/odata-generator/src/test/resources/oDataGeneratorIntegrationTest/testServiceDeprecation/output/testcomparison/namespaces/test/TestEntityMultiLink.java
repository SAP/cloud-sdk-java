/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataException;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmEntity;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataField;
import com.sap.cloud.sdk.s4hana.datamodel.odata.annotation.Key;
import com.sap.cloud.sdk.typeconverter.TypeConverter;
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
import testcomparison.namespaces.test.field.TestEntityMultiLinkField;
import testcomparison.namespaces.test.link.TestEntityMultiLinkLink;
import testcomparison.namespaces.test.link.TestEntityMultiLinkOneToOneLink;
import testcomparison.namespaces.test.selectable.TestEntityMultiLinkSelectable;


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
@JsonAdapter(com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory.class)
public class TestEntityMultiLink
    extends VdmEntity<TestEntityMultiLink>
{

    /**
     * Selector for all available fields of TestEntityMultiLink.
     * 
     */
    public final static TestEntityMultiLinkSelectable ALL_FIELDS = () -> "*";
    /**
     * (Key Field) Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>KeyProperty</b></p>
     * 
     * @return
     *     The keyProperty contained in this entity.
     */
    @Key
    @SerializedName("KeyProperty")
    @JsonProperty("KeyProperty")
    @Nullable
    @ODataField(odataName = "KeyProperty")
    private String keyProperty;
    /**
     * Use with available fluent helpers to apply the <b>KeyProperty</b> field to query operations.
     * 
     */
    public final static TestEntityMultiLinkField<String> KEY_PROPERTY = new TestEntityMultiLinkField<String>("KeyProperty");
    /**
     * Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>StringProperty</b></p>
     * 
     * @return
     *     The stringProperty contained in this entity.
     */
    @SerializedName("StringProperty")
    @JsonProperty("StringProperty")
    @Nullable
    @ODataField(odataName = "StringProperty")
    private String stringProperty;
    /**
     * Use with available fluent helpers to apply the <b>StringProperty</b> field to query operations.
     * 
     */
    public final static TestEntityMultiLinkField<String> STRING_PROPERTY = new TestEntityMultiLinkField<String>("StringProperty");
    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>BooleanProperty</b></p>
     * 
     * @return
     *     The booleanProperty contained in this entity.
     */
    @SerializedName("BooleanProperty")
    @JsonProperty("BooleanProperty")
    @Nullable
    @JsonAdapter(com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataBooleanAdapter.class)
    @ODataField(odataName = "BooleanProperty")
    private Boolean booleanProperty;
    /**
     * Use with available fluent helpers to apply the <b>BooleanProperty</b> field to query operations.
     * 
     */
    public final static TestEntityMultiLinkField<Boolean> BOOLEAN_PROPERTY = new TestEntityMultiLinkField<Boolean>("BooleanProperty");
    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>GuidProperty</b></p>
     * 
     * @return
     *     The guidProperty contained in this entity.
     */
    @SerializedName("GuidProperty")
    @JsonProperty("GuidProperty")
    @Nullable
    @ODataField(odataName = "GuidProperty")
    private UUID guidProperty;
    /**
     * Use with available fluent helpers to apply the <b>GuidProperty</b> field to query operations.
     * 
     */
    public final static TestEntityMultiLinkField<UUID> GUID_PROPERTY = new TestEntityMultiLinkField<UUID>("GuidProperty");
    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>Int16Property</b></p>
     * 
     * @return
     *     The int16Property contained in this entity.
     */
    @SerializedName("Int16Property")
    @JsonProperty("Int16Property")
    @Nullable
    @ODataField(odataName = "Int16Property")
    private Short int16Property;
    /**
     * Use with available fluent helpers to apply the <b>Int16Property</b> field to query operations.
     * 
     */
    public final static TestEntityMultiLinkField<Short> INT16_PROPERTY = new TestEntityMultiLinkField<Short>("Int16Property");
    /**
     * Navigation property <b>to_MultiLink</b> for <b>TestEntityMultiLink</b> to multiple <b>TestEntityLvl2MultiLink</b>.
     * 
     */
    @SerializedName("to_MultiLink")
    @JsonProperty("to_MultiLink")
    @ODataField(odataName = "to_MultiLink")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<TestEntityLvl2MultiLink> toMultiLink;
    /**
     * Navigation property <b>to_SingleLink</b> for <b>TestEntityMultiLink</b> to single <b>TestEntityLvl2SingleLink</b>.
     * 
     */
    @SerializedName("to_SingleLink")
    @JsonProperty("to_SingleLink")
    @ODataField(odataName = "to_SingleLink")
    @Nullable
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private TestEntityLvl2SingleLink toSingleLink;
    /**
     * Use with available fluent helpers to apply the <b>to_MultiLink</b> navigation property to query operations.
     * 
     */
    public final static TestEntityMultiLinkLink<TestEntityLvl2MultiLink> TO_MULTI_LINK = new TestEntityMultiLinkLink<TestEntityLvl2MultiLink>("to_MultiLink");
    /**
     * Use with available fluent helpers to apply the <b>to_SingleLink</b> navigation property to query operations.
     * 
     */
    public final static TestEntityMultiLinkOneToOneLink<TestEntityLvl2SingleLink> TO_SINGLE_LINK = new TestEntityMultiLinkOneToOneLink<TestEntityLvl2SingleLink>("to_SingleLink");

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
        final String keyProperty) {
        rememberChangedField("KeyProperty", this.keyProperty);
        this.keyProperty = keyProperty;
    }

    /**
     * Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>StringProperty</b></p>
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
     * Constraints: none<p>Original property name from the Odata EDM: <b>BooleanProperty</b></p>
     * 
     * @param booleanProperty
     *     The booleanProperty to set.
     */
    public void setBooleanProperty(
        @Nullable
        final Boolean booleanProperty) {
        rememberChangedField("BooleanProperty", this.booleanProperty);
        this.booleanProperty = booleanProperty;
    }

    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>GuidProperty</b></p>
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
     * Constraints: none<p>Original property name from the Odata EDM: <b>Int16Property</b></p>
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
    protected String getEntityCollection() {
        return "A_TestEntityMultiLink";
    }

    @Nonnull
    @Override
    protected Map<String, Object> getKey() {
        final Map<String, Object> result = Maps.newLinkedHashMap();
        result.put("KeyProperty", getKeyProperty());
        return result;
    }

    @Nonnull
    @Override
    protected Map<String, Object> toMapOfFields() {
        final Map<String, Object> cloudSdkValues = super.toMapOfFields();
        cloudSdkValues.put("KeyProperty", getKeyProperty());
        cloudSdkValues.put("StringProperty", getStringProperty());
        cloudSdkValues.put("BooleanProperty", getBooleanProperty());
        cloudSdkValues.put("GuidProperty", getGuidProperty());
        cloudSdkValues.put("Int16Property", getInt16Property());
        return cloudSdkValues;
    }

    @Override
    protected void fromMap(final Map<String, Object> inputValues) {
        final Map<String, Object> cloudSdkValues = Maps.newLinkedHashMap(inputValues);
        // simple properties
        {
            if (cloudSdkValues.containsKey("KeyProperty")) {
                final Object value = cloudSdkValues.remove("KeyProperty");
                if ((value == null)||(!value.equals(getKeyProperty()))) {
                    setKeyProperty(((String) value));
                }
            }
            if (cloudSdkValues.containsKey("StringProperty")) {
                final Object value = cloudSdkValues.remove("StringProperty");
                if ((value == null)||(!value.equals(getStringProperty()))) {
                    setStringProperty(((String) value));
                }
            }
            if (cloudSdkValues.containsKey("BooleanProperty")) {
                final Object value = cloudSdkValues.remove("BooleanProperty");
                if ((value == null)||(!value.equals(getBooleanProperty()))) {
                    setBooleanProperty(((Boolean) value));
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
                        final Map<String, Object> inputMap = ((Map<String, Object> ) item);
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
                    final Map<String, Object> inputMap = ((Map<String, Object> ) cloudSdkValue);
                    toSingleLink.fromMap(inputMap);
                }
            }
        }
        super.fromMap(cloudSdkValues);
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
    public static<T >TestEntityMultiLinkField<T> field(
        @Nonnull
        final String fieldName,
        @Nonnull
        final Class<T> fieldType) {
        return new TestEntityMultiLinkField<T>(fieldName);
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
    public static<T,DomainT >TestEntityMultiLinkField<T> field(
        @Nonnull
        final String fieldName,
        @Nonnull
        final TypeConverter<T, DomainT> typeConverter) {
        return new TestEntityMultiLinkField<T>(fieldName, typeConverter);
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
    @SuppressWarnings("deprecation")
    protected String getDefaultServicePath() {
        return (testcomparison.services.TestService.DEFAULT_SERVICE_PATH);
    }

    @Nonnull
    @Override
    protected Map<String, Object> toMapOfNavigationProperties() {
        final Map<String, Object> cloudSdkValues = super.toMapOfNavigationProperties();
        if (toMultiLink!= null) {
            (cloudSdkValues).put("to_MultiLink", toMultiLink);
        }
        if (toSingleLink!= null) {
            (cloudSdkValues).put("to_SingleLink", toSingleLink);
        }
        return cloudSdkValues;
    }

    /**
     * Fetches the <b>TestEntityLvl2MultiLink</b> entities (one to many) associated with this entity. This corresponds to the OData navigation property <b>to_MultiLink</b>.
     * <p>
     * Please note: This method will not cache or persist the query results.
     * 
     * @return
     *     List containing one or more associated <b>TestEntityLvl2MultiLink</b> entities. If no entities are associated then an empty list is returned. 
     * @throws ODataException
     *     If the entity is unmanaged, i.e. it has not been retrieved using the OData VDM's services and therefore has no ERP configuration context assigned. An entity is managed if it has been either retrieved using the VDM's services or returned from the VDM's services as the result of a CREATE or UPDATE call. 
     */
    @Nonnull
    public List<TestEntityLvl2MultiLink> fetchMultiLink() {
        return fetchFieldAsList("to_MultiLink", TestEntityLvl2MultiLink.class);
    }

    /**
     * Retrieval of associated <b>TestEntityLvl2MultiLink</b> entities (one to many). This corresponds to the OData navigation property <b>to_MultiLink</b>.
     * <p>
     * If the navigation property <b>to_MultiLink</b> of a queried <b>TestEntityMultiLink</b> is operated lazily, an <b>ODataException</b> can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the first time and it has not yet been loaded, an OData query will be run in order to load the missing information and its result will get cached for future invocations.
     * 
     * @return
     *     List of associated <b>TestEntityLvl2MultiLink</b> entities.
     * @throws ODataException
     *     If the entity is unmanaged, i.e. it has not been retrieved using the OData VDM's services and therefore has no ERP configuration context assigned. An entity is managed if it has been either retrieved using the VDM's services or returned from the VDM's services as the result of a CREATE or UPDATE call. 
     */
    @Nonnull
    public List<TestEntityLvl2MultiLink> getMultiLinkOrFetch() {
        if (toMultiLink == null) {
            toMultiLink = fetchMultiLink();
        }
        return toMultiLink;
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
     * Fetches the <b>TestEntityLvl2SingleLink</b> entity (one to one) associated with this entity. This corresponds to the OData navigation property <b>to_SingleLink</b>.
     * <p>
     * Please note: This method will not cache or persist the query results.
     * 
     * @return
     *     The single associated <b>TestEntityLvl2SingleLink</b> entity, or {@code null} if an entity is not associated. 
     * @throws ODataException
     *     If the entity is unmanaged, i.e. it has not been retrieved using the OData VDM's services and therefore has no ERP configuration context assigned. An entity is managed if it has been either retrieved using the VDM's services or returned from the VDM's services as the result of a CREATE or UPDATE call. 
     */
    @Nullable
    public TestEntityLvl2SingleLink fetchSingleLink() {
        return fetchFieldAsSingle("to_SingleLink", TestEntityLvl2SingleLink.class);
    }

    /**
     * Retrieval of associated <b>TestEntityLvl2SingleLink</b> entity (one to one). This corresponds to the OData navigation property <b>to_SingleLink</b>.
     * <p>
     * If the navigation property <b>to_SingleLink</b> of a queried <b>TestEntityMultiLink</b> is operated lazily, an <b>ODataException</b> can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the first time and it has not yet been loaded, an OData query will be run in order to load the missing information and its result will get cached for future invocations.
     * 
     * @return
     *     List of associated <b>TestEntityLvl2SingleLink</b> entity.
     * @throws ODataException
     *     If the entity is unmanaged, i.e. it has not been retrieved using the OData VDM's services and therefore has no ERP configuration context assigned. An entity is managed if it has been either retrieved using the VDM's services or returned from the VDM's services as the result of a CREATE or UPDATE call. 
     */
    @Nullable
    public TestEntityLvl2SingleLink getSingleLinkOrFetch() {
        if (toSingleLink == null) {
            toSingleLink = fetchSingleLink();
        }
        return toSingleLink;
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
     * @param cloudSdkValue
     *     New <b>TestEntityLvl2SingleLink</b> entity.
     */
    public void setSingleLink(final TestEntityLvl2SingleLink cloudSdkValue) {
        toSingleLink = cloudSdkValue;
    }


    /**
     * Helper class to allow for fluent creation of TestEntityMultiLink instances.
     * 
     */
    public final static class TestEntityMultiLinkBuilder {

        private List<TestEntityLvl2MultiLink> toMultiLink = Lists.newArrayList();
        private TestEntityLvl2SingleLink toSingleLink;

        private TestEntityMultiLink.TestEntityMultiLinkBuilder toMultiLink(final List<TestEntityLvl2MultiLink> cloudSdkValue) {
            toMultiLink.addAll(cloudSdkValue);
            return this;
        }

        /**
         * Navigation property <b>to_MultiLink</b> for <b>TestEntityMultiLink</b> to multiple <b>TestEntityLvl2MultiLink</b>.
         * 
         * @param cloudSdkValue
         *     The TestEntityLvl2MultiLinks to build this TestEntityMultiLink with.
         * @return
         *     This Builder to allow for a fluent interface.
         */
        @Nonnull
        public TestEntityMultiLink.TestEntityMultiLinkBuilder multiLink(TestEntityLvl2MultiLink... cloudSdkValue) {
            return toMultiLink(Lists.newArrayList(cloudSdkValue));
        }

        private TestEntityMultiLink.TestEntityMultiLinkBuilder toSingleLink(final TestEntityLvl2SingleLink cloudSdkValue) {
            toSingleLink = cloudSdkValue;
            return this;
        }

        /**
         * Navigation property <b>to_SingleLink</b> for <b>TestEntityMultiLink</b> to single <b>TestEntityLvl2SingleLink</b>.
         * 
         * @param cloudSdkValue
         *     The TestEntityLvl2SingleLink to build this TestEntityMultiLink with.
         * @return
         *     This Builder to allow for a fluent interface.
         */
        @Nonnull
        public TestEntityMultiLink.TestEntityMultiLinkBuilder singleLink(final TestEntityLvl2SingleLink cloudSdkValue) {
            return toSingleLink(cloudSdkValue);
        }

    }

}
