/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.nameclash;

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
import testcomparison.namespaces.nameclash.field.TestEntityV2Field;
import testcomparison.namespaces.nameclash.link.TestEntityV2Link;
import testcomparison.namespaces.nameclash.selectable.TestEntityV2Selectable;


/**
 * <p>Original entity name from the Odata EDM: <b>A_TestEntityV2</b></p>
 * 
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(doNotUseGetters = true, callSuper = true)
@EqualsAndHashCode(doNotUseGetters = true, callSuper = true)
@JsonAdapter(com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory.class)
public class TestEntityV2
    extends VdmEntity<TestEntityV2>
{

    /**
     * Selector for all available fields of TestEntityV2.
     * 
     */
    public final static TestEntityV2Selectable ALL_FIELDS = () -> "*";
    /**
     * (Key Field) Constraints: Not nullable<p>Original property name from the Odata EDM: <b>KeyPropertyGuid</b></p>
     * 
     * @return
     *     The keyPropertyGuid contained in this entity.
     */
    @Key
    @SerializedName("KeyPropertyGuid")
    @JsonProperty("KeyPropertyGuid")
    @Nullable
    @ODataField(odataName = "KeyPropertyGuid")
    private UUID keyPropertyGuid;
    /**
     * Use with available fluent helpers to apply the <b>KeyPropertyGuid</b> field to query operations.
     * 
     */
    public final static TestEntityV2Field<UUID> KEY_PROPERTY_GUID = new TestEntityV2Field<UUID>("KeyPropertyGuid");
    /**
     * Constraints: Not nullable, Maximum length: 80 <p>Original property name from the Odata EDM: <b>MultiLink</b></p>
     * 
     * @return
     *     The multiLink contained in this entity.
     */
    @SerializedName("MultiLink")
    @JsonProperty("MultiLink")
    @Nullable
    @ODataField(odataName = "MultiLink")
    private String multiLink;
    /**
     * Use with available fluent helpers to apply the <b>MultiLink</b> field to query operations.
     * 
     */
    public final static TestEntityV2Field<String> MULTI_LINK = new TestEntityV2Field<String>("MultiLink");
    /**
     * Constraints: Not nullable, Maximum length: 100 <p>Original property name from the Odata EDM: <b>toMultiLink</b></p>
     * 
     * @return
     *     The toMultiLink contained in this entity.
     */
    @SerializedName("toMultiLink")
    @JsonProperty("toMultiLink")
    @Nullable
    @ODataField(odataName = "toMultiLink")
    private String toMultiLink;
    /**
     * Use with available fluent helpers to apply the <b>toMultiLink</b> field to query operations.
     * 
     */
    public final static TestEntityV2Field<String> TO_MULTI_LINK = new TestEntityV2Field<String>("toMultiLink");
    /**
     * Navigation property <b>to_MultiLink</b> for <b>TestEntityV2</b> to multiple <b>TestEntityMultiLink</b>.
     * 
     */
    @SerializedName("to_MultiLink")
    @JsonProperty("to_MultiLink")
    @ODataField(odataName = "to_MultiLink")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<TestEntityMultiLink> toMultiLink_2;
    /**
     * Use with available fluent helpers to apply the <b>to_MultiLink</b> navigation property to query operations.
     * 
     */
    public final static TestEntityV2Link<TestEntityMultiLink> TO_MULTI_LINK_2 = new TestEntityV2Link<TestEntityMultiLink>("to_MultiLink");

    @Nonnull
    @Override
    public Class<TestEntityV2> getType() {
        return TestEntityV2 .class;
    }

    /**
     * (Key Field) Constraints: Not nullable<p>Original property name from the Odata EDM: <b>KeyPropertyGuid</b></p>
     * 
     * @param keyPropertyGuid
     *     The keyPropertyGuid to set.
     */
    public void setKeyPropertyGuid(
        @Nullable
        final UUID keyPropertyGuid) {
        rememberChangedField("KeyPropertyGuid", this.keyPropertyGuid);
        this.keyPropertyGuid = keyPropertyGuid;
    }

    /**
     * Constraints: Not nullable, Maximum length: 80 <p>Original property name from the Odata EDM: <b>MultiLink</b></p>
     * 
     * @param multiLink
     *     The multiLink to set.
     */
    public void setMultiLink(
        @Nullable
        final String multiLink) {
        rememberChangedField("MultiLink", this.multiLink);
        this.multiLink = multiLink;
    }

    /**
     * Constraints: Not nullable, Maximum length: 100 <p>Original property name from the Odata EDM: <b>toMultiLink</b></p>
     * 
     * @param toMultiLink
     *     The toMultiLink to set.
     */
    public void setToMultiLink(
        @Nullable
        final String toMultiLink) {
        rememberChangedField("toMultiLink", this.toMultiLink);
        this.toMultiLink = toMultiLink;
    }

    @Override
    protected String getEntityCollection() {
        return "A_TestEntity";
    }

    @Nonnull
    @Override
    protected Map<String, Object> getKey() {
        final Map<String, Object> result = Maps.newLinkedHashMap();
        result.put("KeyPropertyGuid", getKeyPropertyGuid());
        return result;
    }

    @Nonnull
    @Override
    protected Map<String, Object> toMapOfFields() {
        final Map<String, Object> cloudSdkValues = super.toMapOfFields();
        cloudSdkValues.put("KeyPropertyGuid", getKeyPropertyGuid());
        cloudSdkValues.put("MultiLink", getMultiLink());
        cloudSdkValues.put("toMultiLink", getToMultiLink());
        return cloudSdkValues;
    }

    @Override
    protected void fromMap(final Map<String, Object> inputValues) {
        final Map<String, Object> cloudSdkValues = Maps.newLinkedHashMap(inputValues);
        // simple properties
        {
            if (cloudSdkValues.containsKey("KeyPropertyGuid")) {
                final Object value = cloudSdkValues.remove("KeyPropertyGuid");
                if ((value == null)||(!value.equals(getKeyPropertyGuid()))) {
                    setKeyPropertyGuid(((UUID) value));
                }
            }
            if (cloudSdkValues.containsKey("MultiLink")) {
                final Object value = cloudSdkValues.remove("MultiLink");
                if ((value == null)||(!value.equals(getMultiLink()))) {
                    setMultiLink(((String) value));
                }
            }
            if (cloudSdkValues.containsKey("toMultiLink")) {
                final Object value = cloudSdkValues.remove("toMultiLink");
                if ((value == null)||(!value.equals(getToMultiLink()))) {
                    setToMultiLink(((String) value));
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
                    if (toMultiLink_2 == null) {
                        toMultiLink_2 = Lists.newArrayList();
                    } else {
                        toMultiLink_2 = Lists.newArrayList(toMultiLink_2);
                    }
                    int i = 0;
                    for (Object item: ((Iterable<?> ) cloudSdkValue)) {
                        if (!(item instanceof Map)) {
                            continue;
                        }
                        TestEntityMultiLink entity;
                        if (toMultiLink_2 .size()>i) {
                            entity = toMultiLink_2 .get(i);
                        } else {
                            entity = new TestEntityMultiLink();
                            toMultiLink_2 .add(entity);
                        }
                        i = (i + 1);
                        @SuppressWarnings("unchecked")
                        final Map<String, Object> inputMap = ((Map<String, Object> ) item);
                        entity.fromMap(inputMap);
                    }
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
    public static<T >TestEntityV2Field<T> field(
        @Nonnull
        final String fieldName,
        @Nonnull
        final Class<T> fieldType) {
        return new TestEntityV2Field<T>(fieldName);
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
    public static<T,DomainT >TestEntityV2Field<T> field(
        @Nonnull
        final String fieldName,
        @Nonnull
        final TypeConverter<T, DomainT> typeConverter) {
        return new TestEntityV2Field<T>(fieldName, typeConverter);
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
    protected String getDefaultServicePath() {
        return (testcomparison.services.NameClashService.DEFAULT_SERVICE_PATH);
    }

    @Nonnull
    @Override
    protected Map<String, Object> toMapOfNavigationProperties() {
        final Map<String, Object> cloudSdkValues = super.toMapOfNavigationProperties();
        if (toMultiLink_2 != null) {
            (cloudSdkValues).put("to_MultiLink", toMultiLink_2);
        }
        return cloudSdkValues;
    }

    /**
     * Fetches the <b>TestEntityMultiLink</b> entities (one to many) associated with this entity. This corresponds to the OData navigation property <b>to_MultiLink</b>.
     * <p>
     * Please note: This method will not cache or persist the query results.
     * 
     * @return
     *     List containing one or more associated <b>TestEntityMultiLink</b> entities. If no entities are associated then an empty list is returned. 
     * @throws ODataException
     *     If the entity is unmanaged, i.e. it has not been retrieved using the OData VDM's services and therefore has no ERP configuration context assigned. An entity is managed if it has been either retrieved using the VDM's services or returned from the VDM's services as the result of a CREATE or UPDATE call. 
     */
    @Nonnull
    public List<TestEntityMultiLink> fetchMultiLink() {
        return fetchFieldAsList("to_MultiLink", TestEntityMultiLink.class);
    }

    /**
     * Retrieval of associated <b>TestEntityMultiLink</b> entities (one to many). This corresponds to the OData navigation property <b>to_MultiLink</b>.
     * <p>
     * If the navigation property <b>to_MultiLink</b> of a queried <b>TestEntityV2</b> is operated lazily, an <b>ODataException</b> can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the first time and it has not yet been loaded, an OData query will be run in order to load the missing information and its result will get cached for future invocations.
     * 
     * @return
     *     List of associated <b>TestEntityMultiLink</b> entities.
     * @throws ODataException
     *     If the entity is unmanaged, i.e. it has not been retrieved using the OData VDM's services and therefore has no ERP configuration context assigned. An entity is managed if it has been either retrieved using the VDM's services or returned from the VDM's services as the result of a CREATE or UPDATE call. 
     */
    @Nonnull
    public List<TestEntityMultiLink> getMultiLinkOrFetch() {
        if (toMultiLink_2 == null) {
            toMultiLink_2 = fetchMultiLink();
        }
        return toMultiLink_2;
    }

    /**
     * Retrieval of associated <b>TestEntityMultiLink</b> entities (one to many). This corresponds to the OData navigation property <b>to_MultiLink</b>.
     * <p>
     * If the navigation property for an entity <b>TestEntityV2</b> has not been resolved yet, this method will <b>not query</b> further information. Instead its <code>Option</code> result state will be <code>empty</code>.
     * 
     * @return
     *     If the information for navigation property <b>to_MultiLink</b> is already loaded, the result will contain the <b>TestEntityMultiLink</b> entities. If not, an <code>Option</code> with result state <code>empty</code> is returned.
     */
    @Nonnull
    public Option<List<TestEntityMultiLink>> getMultiLinkIfPresent() {
        return Option.of(toMultiLink_2);
    }

    /**
     * Overwrites the list of associated <b>TestEntityMultiLink</b> entities for the loaded navigation property <b>to_MultiLink</b>.
     * <p>
     * If the navigation property <b>to_MultiLink</b> of a queried <b>TestEntityV2</b> is operated lazily, an <b>ODataException</b> can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the first time and it has not yet been loaded, an OData query will be run in order to load the missing information and its result will get cached for future invocations.
     * 
     * @param cloudSdkValue
     *     List of <b>TestEntityMultiLink</b> entities.
     */
    public void setMultiLink(
        @Nonnull
        final List<TestEntityMultiLink> cloudSdkValue) {
        if (toMultiLink_2 == null) {
            toMultiLink_2 = Lists.newArrayList();
        }
        toMultiLink_2 .clear();
        toMultiLink_2 .addAll(cloudSdkValue);
    }

    /**
     * Adds elements to the list of associated <b>TestEntityMultiLink</b> entities. This corresponds to the OData navigation property <b>to_MultiLink</b>.
     * <p>
     * If the navigation property <b>to_MultiLink</b> of a queried <b>TestEntityV2</b> is operated lazily, an <b>ODataException</b> can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the first time and it has not yet been loaded, an OData query will be run in order to load the missing information and its result will get cached for future invocations.
     * 
     * @param entity
     *     Array of <b>TestEntityMultiLink</b> entities.
     */
    public void addMultiLink(TestEntityMultiLink... entity) {
        if (toMultiLink_2 == null) {
            toMultiLink_2 = Lists.newArrayList();
        }
        toMultiLink_2 .addAll(Lists.newArrayList(entity));
    }


    /**
     * Helper class to allow for fluent creation of TestEntityV2 instances.
     * 
     */
    public final static class TestEntityV2Builder {

        private List<TestEntityMultiLink> toMultiLink_2 = Lists.newArrayList();
        private String multiLink = null;

        private TestEntityV2 .TestEntityV2Builder toMultiLink_2(final List<TestEntityMultiLink> cloudSdkValue) {
            toMultiLink_2 .addAll(cloudSdkValue);
            return this;
        }

        /**
         * Navigation property <b>to_MultiLink</b> for <b>TestEntityV2</b> to multiple <b>TestEntityMultiLink</b>.
         * 
         * @param cloudSdkValue
         *     The TestEntityMultiLinks to build this TestEntityV2 with.
         * @return
         *     This Builder to allow for a fluent interface.
         */
        @Nonnull
        public TestEntityV2 .TestEntityV2Builder multiLink(TestEntityMultiLink... cloudSdkValue) {
            return toMultiLink_2(Lists.newArrayList(cloudSdkValue));
        }

        /**
         * Constraints: Not nullable, Maximum length: 80 <p>Original property name from the Odata EDM: <b>MultiLink</b></p>
         * 
         * @param cloudSdkValue
         *     The multiLink to build this TestEntityV2 with.
         * @return
         *     This Builder to allow for a fluent interface.
         */
        @Nonnull
        public TestEntityV2 .TestEntityV2Builder multiLink(final String cloudSdkValue) {
            multiLink = cloudSdkValue;
            return this;
        }

    }

}
