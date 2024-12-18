/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.minimalmetadata;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmEntity;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataField;
import com.sap.cloud.sdk.s4hana.datamodel.odata.annotation.Key;
import com.sap.cloud.sdk.typeconverter.TypeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import testcomparison.namespaces.minimalmetadata.field.SimplePersonField;
import testcomparison.namespaces.minimalmetadata.selectable.SimplePersonSelectable;


/**
 * Simple Person Test Data<p></p><p>Original entity name from the Odata EDM: <b>A_SimplePerson</b></p>
 * 
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(doNotUseGetters = true, callSuper = true)
@EqualsAndHashCode(doNotUseGetters = true, callSuper = true)
@JsonAdapter(com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory.class)
public class SimplePerson
    extends VdmEntity<SimplePerson>
{

    /**
     * Selector for all available fields of SimplePerson.
     * 
     */
    public final static SimplePersonSelectable ALL_FIELDS = () -> "*";
    /**
     * (Key Field) Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>Person</b></p><p>Some long description for the Person property.</p>
     * 
     * @return
     *     Person Summary.
     */
    @Key
    @SerializedName("Person")
    @JsonProperty("Person")
    @Nullable
    @ODataField(odataName = "Person")
    private String person;
    /**
     * Use with available fluent helpers to apply the <b>Person</b> field to query operations.
     * 
     */
    public final static SimplePersonField<String> PERSON = new SimplePersonField<String>("Person");
    /**
     * Constraints: Not nullable, Maximum length: 241 <p>Original property name from the Odata EDM: <b>EmailAddress</b></p><p>Some long description for the EmailAddress property.</p>
     * 
     * @return
     *     Email Address Summary.
     */
    @SerializedName("EmailAddress")
    @JsonProperty("EmailAddress")
    @Nullable
    @ODataField(odataName = "EmailAddress")
    private String emailAddress;
    /**
     * Use with available fluent helpers to apply the <b>EmailAddress</b> field to query operations.
     * 
     */
    public final static SimplePersonField<String> EMAIL_ADDRESS = new SimplePersonField<String>("EmailAddress");

    @Nonnull
    @Override
    public Class<SimplePerson> getType() {
        return SimplePerson.class;
    }

    /**
     * (Key Field) Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>Person</b></p><p>Some long description for the Person property.</p>
     * 
     * @param person
     *     Person Summary.
     */
    public void setPerson(
        @Nullable
        final String person) {
        rememberChangedField("Person", this.person);
        this.person = person;
    }

    /**
     * Constraints: Not nullable, Maximum length: 241 <p>Original property name from the Odata EDM: <b>EmailAddress</b></p><p>Some long description for the EmailAddress property.</p>
     * 
     * @param emailAddress
     *     Email Address Summary.
     */
    public void setEmailAddress(
        @Nullable
        final String emailAddress) {
        rememberChangedField("EmailAddress", this.emailAddress);
        this.emailAddress = emailAddress;
    }

    @Override
    protected String getEntityCollection() {
        return "A_SimplePerson";
    }

    @Nonnull
    @Override
    protected Map<String, Object> getKey() {
        final Map<String, Object> result = Maps.newLinkedHashMap();
        result.put("Person", getPerson());
        return result;
    }

    @Nonnull
    @Override
    protected Map<String, Object> toMapOfFields() {
        final Map<String, Object> cloudSdkValues = super.toMapOfFields();
        cloudSdkValues.put("Person", getPerson());
        cloudSdkValues.put("EmailAddress", getEmailAddress());
        return cloudSdkValues;
    }

    @Override
    protected void fromMap(final Map<String, Object> inputValues) {
        final Map<String, Object> cloudSdkValues = Maps.newLinkedHashMap(inputValues);
        // simple properties
        {
            if (cloudSdkValues.containsKey("Person")) {
                final Object value = cloudSdkValues.remove("Person");
                if ((value == null)||(!value.equals(getPerson()))) {
                    setPerson(((String) value));
                }
            }
            if (cloudSdkValues.containsKey("EmailAddress")) {
                final Object value = cloudSdkValues.remove("EmailAddress");
                if ((value == null)||(!value.equals(getEmailAddress()))) {
                    setEmailAddress(((String) value));
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
    public static<T >SimplePersonField<T> field(
        @Nonnull
        final String fieldName,
        @Nonnull
        final Class<T> fieldType) {
        return new SimplePersonField<T>(fieldName);
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
    public static<T,DomainT >SimplePersonField<T> field(
        @Nonnull
        final String fieldName,
        @Nonnull
        final TypeConverter<T, DomainT> typeConverter) {
        return new SimplePersonField<T>(fieldName, typeConverter);
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
        return (testcomparison.services.MinimalMetadataService.DEFAULT_SERVICE_PATH);
    }

}
