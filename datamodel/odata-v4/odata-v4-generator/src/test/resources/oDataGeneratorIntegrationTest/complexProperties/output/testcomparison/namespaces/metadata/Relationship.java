/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.metadata;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Maps;
import com.google.gson.annotations.JsonAdapter;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataEntityKey;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmComplex;
import com.sap.cloud.sdk.result.ElementName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * <p>Original complex type name from the Odata EDM: <b>Relationship</b></p>
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
public class Relationship
    extends VdmComplex<Relationship>
{

    @Getter
    private final java.lang.String odataType = "API_MINIMAL_TEST_CASE.Relationship";
    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>Description</b></p>
     * 
     * @return
     *     The description contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("Description")
    private java.lang.String description;
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.String<Relationship> DESCRIPTION = new com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.String<Relationship>(Relationship.class, "Description");
    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>FirstName</b></p>
     * 
     * @return
     *     The firstName contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("FirstName")
    private java.lang.String firstName;
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.String<Relationship> FIRST_NAME = new com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.String<Relationship>(Relationship.class, "FirstName");
    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>LastName</b></p>
     * 
     * @return
     *     The lastName contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("LastName")
    private java.lang.String lastName;
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.String<Relationship> LAST_NAME = new com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.String<Relationship>(Relationship.class, "LastName");

    @Nonnull
    @Override
    public Class<Relationship> getType() {
        return Relationship.class;
    }

    @Nonnull
    @Override
    protected Map<java.lang.String, Object> toMapOfFields() {
        final Map<java.lang.String, Object> cloudSdkValues = super.toMapOfFields();
        cloudSdkValues.put("Description", getDescription());
        cloudSdkValues.put("FirstName", getFirstName());
        cloudSdkValues.put("LastName", getLastName());
        return cloudSdkValues;
    }

    @Override
    protected void fromMap(final Map<java.lang.String, Object> inputValues) {
        final Map<java.lang.String, Object> cloudSdkValues = Maps.newLinkedHashMap(inputValues);
        // simple properties
        {
            if (cloudSdkValues.containsKey("Description")) {
                final Object value = cloudSdkValues.remove("Description");
                if ((value == null)||(!value.equals(getDescription()))) {
                    setDescription(((java.lang.String) value));
                }
            }
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
        }
        // navigation properties
        {
        }
        super.fromMap(cloudSdkValues);
    }

    @Nonnull
    @Override
    protected ODataEntityKey getKey() {
        final ODataEntityKey entityKey = super.getKey();
        return entityKey;
    }

    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>Description</b></p>
     * 
     * @param description
     *     The description to set.
     */
    public void setDescription(
        @Nullable
        final java.lang.String description) {
        rememberChangedField("Description", this.description);
        this.description = description;
    }

    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>FirstName</b></p>
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
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>LastName</b></p>
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

}
