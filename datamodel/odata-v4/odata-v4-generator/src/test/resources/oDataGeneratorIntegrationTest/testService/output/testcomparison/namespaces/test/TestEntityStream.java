/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test;

import java.net.URI;
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
import com.sap.cloud.sdk.result.ElementName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * <p>Original entity name from the Odata EDM: <b>A_TestEntityStreamType</b></p>
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
public class TestEntityStream
    extends VdmEntity<TestEntityStream>
{

    @Getter
    private final String odataType = "API_TEST_SRV.A_TestEntityStreamType";
    /**
     * Selector for all available fields of TestEntityStream.
     * 
     */
    public final static SimpleProperty<TestEntityStream> ALL_FIELDS = all();
    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>StreamProperty</b></p>
     * 
     * @return
     *     The streamProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("StreamProperty")
    private URI streamProperty;

    @Nonnull
    @Override
    public Class<TestEntityStream> getType() {
        return TestEntityStream.class;
    }

    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>StreamProperty</b></p>
     * 
     * @param streamProperty
     *     The streamProperty to set.
     */
    public void setStreamProperty(
        @Nullable
        final URI streamProperty) {
        rememberChangedField("StreamProperty", this.streamProperty);
        this.streamProperty = streamProperty;
    }

    @Override
    protected String getEntityCollection() {
        return "to_StreamLink";
    }

    @Nonnull
    @Override
    protected ODataEntityKey getKey() {
        final ODataEntityKey entityKey = super.getKey();
        return entityKey;
    }

    @Nonnull
    @Override
    protected Map<String, Object> toMapOfFields() {
        final Map<String, Object> cloudSdkValues = super.toMapOfFields();
        cloudSdkValues.put("StreamProperty", getStreamProperty());
        return cloudSdkValues;
    }

    @Override
    protected void fromMap(final Map<String, Object> inputValues) {
        final Map<String, Object> cloudSdkValues = Maps.newLinkedHashMap(inputValues);
        // simple properties
        {
            if (cloudSdkValues.containsKey("StreamProperty")) {
                final Object value = cloudSdkValues.remove("StreamProperty");
                if ((value == null)||(!value.equals(getStreamProperty()))) {
                    setStreamProperty(((URI) value));
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
    protected Map<String, Object> toMapOfNavigationProperties() {
        final Map<String, Object> cloudSdkValues = super.toMapOfNavigationProperties();
        return cloudSdkValues;
    }

}
