/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.actionsandfunctions;

import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Maps;
import com.google.gson.annotations.JsonAdapter;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataEntityKey;
import com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEntity;
import com.sap.cloud.sdk.result.ElementName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * <p>Original entity name from the Odata EDM: <b>FunctionResult</b></p>
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
public class FunctionResult
    extends VdmEntity<FunctionResult>
{

    @Getter
    private final java.lang.String odataType = "API_ACTIONS_FUNCTIONS_TEST_CASE.FunctionResult";
    /**
     * Selector for all available fields of FunctionResult.
     * 
     */
    public final static SimpleProperty<FunctionResult> ALL_FIELDS = all();
    /**
     * (Key Field) Constraints: Not nullable<p>Original property name from the Odata EDM: <b>RequestId</b></p>
     * 
     * @return
     *     The requestId contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("RequestId")
    private UUID requestId;
    public final static SimpleProperty.Guid<FunctionResult> REQUEST_ID = new SimpleProperty.Guid<FunctionResult>(FunctionResult.class, "RequestId");
    /**
     * Constraints: Nullable, Maximum length: 4096 <p>Original property name from the Odata EDM: <b>Message</b></p>
     * 
     * @return
     *     The message contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("Message")
    private java.lang.String message;
    public final static SimpleProperty.String<FunctionResult> MESSAGE = new SimpleProperty.String<FunctionResult>(FunctionResult.class, "Message");

    @Nonnull
    @Override
    public Class<FunctionResult> getType() {
        return FunctionResult.class;
    }

    /**
     * (Key Field) Constraints: Not nullable<p>Original property name from the Odata EDM: <b>RequestId</b></p>
     * 
     * @param requestId
     *     The requestId to set.
     */
    public void setRequestId(
        @Nullable
        final UUID requestId) {
        rememberChangedField("RequestId", this.requestId);
        this.requestId = requestId;
    }

    /**
     * Constraints: Nullable, Maximum length: 4096 <p>Original property name from the Odata EDM: <b>Message</b></p>
     * 
     * @param message
     *     The message to set.
     */
    public void setMessage(
        @Nullable
        final java.lang.String message) {
        rememberChangedField("Message", this.message);
        this.message = message;
    }

    @Override
    protected java.lang.String getEntityCollection() {
        return "GetPersonWithMostFriends";
    }

    @Nonnull
    @Override
    protected ODataEntityKey getKey() {
        final ODataEntityKey entityKey = super.getKey();
        entityKey.addKeyProperty("RequestId", getRequestId());
        return entityKey;
    }

    @Nonnull
    @Override
    protected Map<java.lang.String, Object> toMapOfFields() {
        final Map<java.lang.String, Object> values = super.toMapOfFields();
        values.put("RequestId", getRequestId());
        values.put("Message", getMessage());
        return values;
    }

    @Override
    protected void fromMap(final Map<java.lang.String, Object> inputValues) {
        final Map<java.lang.String, Object> values = Maps.newHashMap(inputValues);
        // simple properties
        {
            if (values.containsKey("RequestId")) {
                final Object value = values.remove("RequestId");
                if ((value == null)||(!value.equals(getRequestId()))) {
                    setRequestId(((UUID) value));
                }
            }
            if (values.containsKey("Message")) {
                final Object value = values.remove("Message");
                if ((value == null)||(!value.equals(getMessage()))) {
                    setMessage(((java.lang.String) value));
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

    @Nonnull
    @Override
    protected Map<java.lang.String, Object> toMapOfNavigationProperties() {
        final Map<java.lang.String, Object> values = super.toMapOfNavigationProperties();
        return values;
    }

}
