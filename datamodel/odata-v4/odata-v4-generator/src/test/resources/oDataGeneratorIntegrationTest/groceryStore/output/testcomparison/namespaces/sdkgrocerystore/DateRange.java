/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.sdkgrocerystore;

import java.time.OffsetDateTime;
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
 * <p>Original complex type name from the Odata EDM: <b>DateRange</b></p>
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
public class DateRange
    extends VdmComplex<DateRange>
{

    @Getter
    private final String odataType = "com.sap.cloud.sdk.store.grocery.DateRange";
    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>Start</b></p>
     * 
     * @return
     *     The start contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("Start")
    private OffsetDateTime start;
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.DateTime<DateRange> START = new com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.DateTime<DateRange>(DateRange.class, "Start");
    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>End</b></p>
     * 
     * @return
     *     The end contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("End")
    private OffsetDateTime end;
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.DateTime<DateRange> END = new com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.DateTime<DateRange>(DateRange.class, "End");

    @Nonnull
    @Override
    public Class<DateRange> getType() {
        return DateRange.class;
    }

    @Nonnull
    @Override
    protected Map<String, Object> toMapOfFields() {
        final Map<String, Object> cloudSdkValues = super.toMapOfFields();
        cloudSdkValues.put("Start", getStart());
        cloudSdkValues.put("End", getEnd());
        return cloudSdkValues;
    }

    @Override
    protected void fromMap(final Map<String, Object> inputValues) {
        final Map<String, Object> cloudSdkValues = Maps.newLinkedHashMap(inputValues);
        // simple properties
        {
            if (cloudSdkValues.containsKey("Start")) {
                final Object value = cloudSdkValues.remove("Start");
                if ((value == null)||(!value.equals(getStart()))) {
                    setStart(((OffsetDateTime) value));
                }
            }
            if (cloudSdkValues.containsKey("End")) {
                final Object value = cloudSdkValues.remove("End");
                if ((value == null)||(!value.equals(getEnd()))) {
                    setEnd(((OffsetDateTime) value));
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
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>Start</b></p>
     * 
     * @param start
     *     The start to set.
     */
    public void setStart(
        @Nullable
        final OffsetDateTime start) {
        rememberChangedField("Start", this.start);
        this.start = start;
    }

    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>End</b></p>
     * 
     * @param end
     *     The end to set.
     */
    public void setEnd(
        @Nullable
        final OffsetDateTime end) {
        rememberChangedField("End", this.end);
        this.end = end;
    }

}
