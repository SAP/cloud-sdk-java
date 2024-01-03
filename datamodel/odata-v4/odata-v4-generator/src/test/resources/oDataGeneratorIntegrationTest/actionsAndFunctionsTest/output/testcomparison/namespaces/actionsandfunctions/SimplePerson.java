/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.actionsandfunctions;

import java.util.Collection;
import java.util.HashMap;
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
import testcomparison.services.ActionsAndFunctionsService;


/**
 * <p>Original entity name from the Odata EDM: <b>SimplePerson</b></p>
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
    private final java.lang.String odataType = "API_ACTIONS_FUNCTIONS_TEST_CASE.SimplePerson";
    /**
     * Selector for all available fields of SimplePerson.
     * 
     */
    public final static SimpleProperty<SimplePerson> ALL_FIELDS = all();
    /**
     * (Key Field) Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>Person</b></p>
     * 
     * @return
     *     The person contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("Person")
    private java.lang.String person;
    public final static SimpleProperty.String<SimplePerson> PERSON = new SimpleProperty.String<SimplePerson>(SimplePerson.class, "Person");
    /**
     * Constraints: Nullable, Maximum length: 241 <p>Original property name from the Odata EDM: <b>EmailAddress</b></p>
     * 
     * @return
     *     The emailAddress contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("EmailAddress")
    private java.lang.String emailAddress;
    public final static SimpleProperty.String<SimplePerson> EMAIL_ADDRESS = new SimpleProperty.String<SimplePerson>(SimplePerson.class, "EmailAddress");

    @Nonnull
    @Override
    public Class<SimplePerson> getType() {
        return SimplePerson.class;
    }

    /**
     * (Key Field) Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>Person</b></p>
     * 
     * @param person
     *     The person to set.
     */
    public void setPerson(
        @Nullable
        final java.lang.String person) {
        rememberChangedField("Person", this.person);
        this.person = person;
    }

    /**
     * Constraints: Nullable, Maximum length: 241 <p>Original property name from the Odata EDM: <b>EmailAddress</b></p>
     * 
     * @param emailAddress
     *     The emailAddress to set.
     */
    public void setEmailAddress(
        @Nullable
        final java.lang.String emailAddress) {
        rememberChangedField("EmailAddress", this.emailAddress);
        this.emailAddress = emailAddress;
    }

    @Override
    protected java.lang.String getEntityCollection() {
        return "SimplePersons";
    }

    @Nonnull
    @Override
    protected ODataEntityKey getKey() {
        final ODataEntityKey entityKey = super.getKey();
        entityKey.addKeyProperty("Person", getPerson());
        return entityKey;
    }

    @Nonnull
    @Override
    protected Map<java.lang.String, Object> toMapOfFields() {
        final Map<java.lang.String, Object> values = super.toMapOfFields();
        values.put("Person", getPerson());
        values.put("EmailAddress", getEmailAddress());
        return values;
    }

    @Override
    protected void fromMap(final Map<java.lang.String, Object> inputValues) {
        final Map<java.lang.String, Object> values = Maps.newHashMap(inputValues);
        // simple properties
        {
            if (values.containsKey("Person")) {
                final Object value = values.remove("Person");
                if ((value == null)||(!value.equals(getPerson()))) {
                    setPerson(((java.lang.String) value));
                }
            }
            if (values.containsKey("EmailAddress")) {
                final Object value = values.remove("EmailAddress");
                if ((value == null)||(!value.equals(getEmailAddress()))) {
                    setEmailAddress(((java.lang.String) value));
                }
            }
        }
        // structured properties
        {
        }
        // navigation properties
        {
        }
        super.fromMap(values);
    }

    @Override
    protected java.lang.String getDefaultServicePath() {
        return ActionsAndFunctionsService.DEFAULT_SERVICE_PATH;
    }

    /**
     * Action that can be applied to any entity object of this class.</p>
     * 
     * @param addresses
     *     Constraints: Not nullable<p>Original parameter name from the Odata EDM: <b>Addresses</b></p>
     * @return
     *     Action object prepared with the given parameters to be applied to any entity object of this class.</p> To execute it use the {@code service.forEntity(entity).applyAction(thisAction)} API.
     */
    @Nonnull
    public static com.sap.cloud.sdk.datamodel.odatav4.core.BoundAction.SingleToSingle<SimplePerson, Void> insertAddresses(
        @Nonnull
        final Collection<Address> addresses) {
        final Map<java.lang.String, Object> parameters = new HashMap<java.lang.String, Object>();
        parameters.put("Addresses", addresses);
        return new com.sap.cloud.sdk.datamodel.odatav4.core.BoundAction.SingleToSingle<SimplePerson, Void>(SimplePerson.class, Void.class, "API_ACTIONS_FUNCTIONS_TEST_CASE.InsertAddresses", parameters);
    }

}
