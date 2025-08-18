/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.sdkgrocerystore;

import java.time.LocalTime;
import java.time.OffsetDateTime;
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
import testcomparison.services.SdkGroceryStoreService;


/**
 * <p>Original entity name from the Odata EDM: <b>OpeningHours</b></p>
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
public class OpeningHours
    extends VdmEntity<OpeningHours>
    implements VdmEntitySet
{

    @Getter
    private final String odataType = "com.sap.cloud.sdk.store.grocery.OpeningHours";
    /**
     * Selector for all available fields of OpeningHours.
     * 
     */
    public final static SimpleProperty<OpeningHours> ALL_FIELDS = all();
    /**
     * (Key Field) Constraints: Not nullable<p>Original property name from the Odata EDM: <b>Id</b></p>
     * 
     * @return
     *     The id contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("Id")
    private Integer id;
    public final static SimpleProperty.NumericInteger<OpeningHours> ID = new SimpleProperty.NumericInteger<OpeningHours>(OpeningHours.class, "Id");
    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>DayOfWeek</b></p>
     * 
     * @return
     *     The dayOfWeek contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("DayOfWeek")
    private Integer dayOfWeek;
    public final static SimpleProperty.NumericInteger<OpeningHours> DAY_OF_WEEK = new SimpleProperty.NumericInteger<OpeningHours>(OpeningHours.class, "DayOfWeek");
    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>OpenTime</b></p>
     * 
     * @return
     *     The openTime contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("OpenTime")
    private LocalTime openTime;
    public final static SimpleProperty.Time<OpeningHours> OPEN_TIME = new SimpleProperty.Time<OpeningHours>(OpeningHours.class, "OpenTime");
    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>CloseTime</b></p>
     * 
     * @return
     *     The closeTime contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("CloseTime")
    private LocalTime closeTime;
    public final static SimpleProperty.Time<OpeningHours> CLOSE_TIME = new SimpleProperty.Time<OpeningHours>(OpeningHours.class, "CloseTime");

    @Nonnull
    @Override
    public Class<OpeningHours> getType() {
        return OpeningHours.class;
    }

    /**
     * (Key Field) Constraints: Not nullable<p>Original property name from the Odata EDM: <b>Id</b></p>
     * 
     * @param id
     *     The id to set.
     */
    public void setId(
        @Nullable
        final Integer id) {
        rememberChangedField("Id", this.id);
        this.id = id;
    }

    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>DayOfWeek</b></p>
     * 
     * @param dayOfWeek
     *     The dayOfWeek to set.
     */
    public void setDayOfWeek(
        @Nullable
        final Integer dayOfWeek) {
        rememberChangedField("DayOfWeek", this.dayOfWeek);
        this.dayOfWeek = dayOfWeek;
    }

    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>OpenTime</b></p>
     * 
     * @param openTime
     *     The openTime to set.
     */
    public void setOpenTime(
        @Nullable
        final LocalTime openTime) {
        rememberChangedField("OpenTime", this.openTime);
        this.openTime = openTime;
    }

    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>CloseTime</b></p>
     * 
     * @param closeTime
     *     The closeTime to set.
     */
    public void setCloseTime(
        @Nullable
        final LocalTime closeTime) {
        rememberChangedField("CloseTime", this.closeTime);
        this.closeTime = closeTime;
    }

    @Override
    protected String getEntityCollection() {
        return "OpeningHours";
    }

    @Nonnull
    @Override
    protected ODataEntityKey getKey() {
        final ODataEntityKey entityKey = super.getKey();
        entityKey.addKeyProperty("Id", getId());
        return entityKey;
    }

    @Nonnull
    @Override
    protected Map<String, Object> toMapOfFields() {
        final Map<String, Object> cloudSdkValues = super.toMapOfFields();
        cloudSdkValues.put("Id", getId());
        cloudSdkValues.put("DayOfWeek", getDayOfWeek());
        cloudSdkValues.put("OpenTime", getOpenTime());
        cloudSdkValues.put("CloseTime", getCloseTime());
        return cloudSdkValues;
    }

    @Override
    protected void fromMap(final Map<String, Object> inputValues) {
        final Map<String, Object> cloudSdkValues = Maps.newLinkedHashMap(inputValues);
        // simple properties
        {
            if (cloudSdkValues.containsKey("Id")) {
                final Object cloudSdkValue = cloudSdkValues.remove("Id");
                if ((cloudSdkValue == null)||(!cloudSdkValue.equals(getId()))) {
                    setId(((Integer) cloudSdkValue));
                }
            }
            if (cloudSdkValues.containsKey("DayOfWeek")) {
                final Object cloudSdkValue = cloudSdkValues.remove("DayOfWeek");
                if ((cloudSdkValue == null)||(!cloudSdkValue.equals(getDayOfWeek()))) {
                    setDayOfWeek(((Integer) cloudSdkValue));
                }
            }
            if (cloudSdkValues.containsKey("OpenTime")) {
                final Object cloudSdkValue = cloudSdkValues.remove("OpenTime");
                if ((cloudSdkValue == null)||(!cloudSdkValue.equals(getOpenTime()))) {
                    setOpenTime(((LocalTime) cloudSdkValue));
                }
            }
            if (cloudSdkValues.containsKey("CloseTime")) {
                final Object cloudSdkValue = cloudSdkValues.remove("CloseTime");
                if ((cloudSdkValue == null)||(!cloudSdkValue.equals(getCloseTime()))) {
                    setCloseTime(((LocalTime) cloudSdkValue));
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

    @Override
    protected String getDefaultServicePath() {
        return SdkGroceryStoreService.DEFAULT_SERVICE_PATH;
    }

    /**
     * Function that can be applied to any entity object of this class.</p>
     * 
     * @param dateTime
     *     Constraints: Not nullable<p>Original parameter name from the Odata EDM: <b>DateTime</b></p>
     * @return
     *     Function object prepared with the given parameters to be applied to any entity object of this class.</p> To execute it use the {@code service.forEntity(entity).applyFunction(thisFunction)} API.
     */
    @Nonnull
    public static com.sap.cloud.sdk.datamodel.odatav4.core.BoundFunction.SingleToSingle<OpeningHours, Boolean> isStoreOpen(
        @Nonnull
        final OffsetDateTime dateTime) {
        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("DateTime", dateTime);
        return new com.sap.cloud.sdk.datamodel.odatav4.core.BoundFunction.SingleToSingle<OpeningHours, Boolean>(OpeningHours.class, Boolean.class, "com.sap.cloud.sdk.store.grocery.IsStoreOpen", parameters);
    }

}
