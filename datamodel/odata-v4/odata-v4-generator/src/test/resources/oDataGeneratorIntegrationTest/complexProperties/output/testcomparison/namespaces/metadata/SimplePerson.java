/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.metadata;

import java.util.LinkedList;
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
import testcomparison.services.MetadataService;


/**
 * <p>Original entity name from the Odata EDM: <b>A_SimplePerson</b></p>
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
public class SimplePerson
    extends VdmEntity<SimplePerson>
    implements VdmEntitySet
{

    @Getter
    private final java.lang.String odataType = "API_MINIMAL_TEST_CASE.A_SimplePerson";
    /**
     * Selector for all available fields of SimplePerson.
     * 
     */
    public final static SimpleProperty<SimplePerson> ALL_FIELDS = all();
    /**
     * (Key Field) Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>FirstName</b></p>
     * 
     * @return
     *     The firstName contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("FirstName")
    private java.lang.String firstName;
    public final static SimpleProperty.String<SimplePerson> FIRST_NAME = new SimpleProperty.String<SimplePerson>(SimplePerson.class, "FirstName");
    /**
     * (Key Field) Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>LastName</b></p>
     * 
     * @return
     *     The lastName contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("LastName")
    private java.lang.String lastName;
    public final static SimpleProperty.String<SimplePerson> LAST_NAME = new SimpleProperty.String<SimplePerson>(SimplePerson.class, "LastName");
    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>Relationships</b></p>
     * 
     * @return
     *     The relationships contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("Relationships")
    private java.util.Collection<Relationship> relationships;
    /**
     * Use with available request builders to apply the <b>Relationships</b> complex property to query operations.
     * 
     */
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.ComplexProperty.Collection<SimplePerson, Relationship> RELATIONSHIPS = new com.sap.cloud.sdk.datamodel.odatav4.core.ComplexProperty.Collection<SimplePerson, Relationship>(SimplePerson.class, "Relationships", Relationship.class);
    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>Favorite</b></p>
     * 
     * @return
     *     The favorite contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("Favorite")
    private Relationship favorite;
    /**
     * Use with available request builders to apply the <b>Favorite</b> complex property to query operations.
     * 
     */
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.ComplexProperty.Single<SimplePerson, Relationship> FAVORITE = new com.sap.cloud.sdk.datamodel.odatav4.core.ComplexProperty.Single<SimplePerson, Relationship>(SimplePerson.class, "Favorite", Relationship.class);

    @Nonnull
    @Override
    public Class<SimplePerson> getType() {
        return SimplePerson.class;
    }

    /**
     * (Key Field) Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>FirstName</b></p>
     * 
     * @param firstName
     *     The firstName to set.
     */
    public void setFirstName(
        @Nullable
        final java.lang.String firstName) {
        rememberChangedField("FirstName", this.firstName);
        this.firstName = firstName;
    }

    /**
     * (Key Field) Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>LastName</b></p>
     * 
     * @param lastName
     *     The lastName to set.
     */
    public void setLastName(
        @Nullable
        final java.lang.String lastName) {
        rememberChangedField("LastName", this.lastName);
        this.lastName = lastName;
    }

    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>Relationships</b></p>
     * 
     * @param relationships
     *     The relationships to set.
     */
    public void setRelationships(
        @Nullable
        final java.util.Collection<Relationship> relationships) {
        rememberChangedField("Relationships", this.relationships);
        this.relationships = relationships;
    }

    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>Favorite</b></p>
     * 
     * @param favorite
     *     The favorite to set.
     */
    public void setFavorite(
        @Nullable
        final Relationship favorite) {
        rememberChangedField("Favorite", this.favorite);
        this.favorite = favorite;
    }

    @Override
    protected java.lang.String getEntityCollection() {
        return "A_SimplePersons";
    }

    @Nonnull
    @Override
    protected ODataEntityKey getKey() {
        final ODataEntityKey entityKey = super.getKey();
        entityKey.addKeyProperty("FirstName", getFirstName());
        entityKey.addKeyProperty("LastName", getLastName());
        return entityKey;
    }

    @Nonnull
    @Override
    protected Map<java.lang.String, Object> toMapOfFields() {
        final Map<java.lang.String, Object> cloudSdkValues = super.toMapOfFields();
        cloudSdkValues.put("FirstName", getFirstName());
        cloudSdkValues.put("LastName", getLastName());
        cloudSdkValues.put("Relationships", getRelationships());
        cloudSdkValues.put("Favorite", getFavorite());
        return cloudSdkValues;
    }

    @Override
    protected void fromMap(final Map<java.lang.String, Object> inputValues) {
        final Map<java.lang.String, Object> cloudSdkValues = Maps.newLinkedHashMap(inputValues);
        // simple properties
        {
            if (cloudSdkValues.containsKey("FirstName")) {
                final Object value = cloudSdkValues.remove("FirstName");
                if ((value == null)||(!value.equals(getFirstName()))) {
                    setFirstName(((java.lang.String) value));
                }
            }
            if (cloudSdkValues.containsKey("LastName")) {
                final Object value = cloudSdkValues.remove("LastName");
                if ((value == null)||(!value.equals(getLastName()))) {
                    setLastName(((java.lang.String) value));
                }
            }
        }
        // structured properties
        {
            if (cloudSdkValues.containsKey("Relationships")) {
                final Object value = cloudSdkValues.remove("Relationships");
                if (value instanceof Iterable) {
                    final LinkedList<Relationship> relationships = new LinkedList<Relationship>();
                    for (Object properties: ((Iterable<?> ) value)) {
                        if (properties instanceof Map) {
                            final Relationship item = new Relationship();
                            @SuppressWarnings("unchecked")
                            final Map<java.lang.String, Object> inputMap = ((Map<java.lang.String, Object> ) value);
                            item.fromMap(inputMap);
                            relationships.add(item);
                        }
                    }
                    setRelationships(relationships);
                }
                if ((value == null)&&(getRelationships()!= null)) {
                    setRelationships(null);
                }
            }
            if (cloudSdkValues.containsKey("Favorite")) {
                final Object value = cloudSdkValues.remove("Favorite");
                if (value instanceof Map) {
                    if (getFavorite() == null) {
                        setFavorite(new Relationship());
                    }
                    @SuppressWarnings("unchecked")
                    final Map<java.lang.String, Object> inputMap = ((Map<java.lang.String, Object> ) value);
                    getFavorite().fromMap(inputMap);
                }
                if ((value == null)&&(getFavorite()!= null)) {
                    setFavorite(null);
                }
            }
        }
        // navigation properties
        {
        }
        super.fromMap(cloudSdkValues);
    }

    @Override
    protected java.lang.String getDefaultServicePath() {
        return MetadataService.DEFAULT_SERVICE_PATH;
    }

}
