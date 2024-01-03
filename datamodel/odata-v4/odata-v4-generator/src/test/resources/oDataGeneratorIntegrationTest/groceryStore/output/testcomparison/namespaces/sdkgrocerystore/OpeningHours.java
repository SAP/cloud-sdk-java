/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
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
        final Map<String, Object> values = super.toMapOfFields();
        values.put("Id", getId());
        values.put("DayOfWeek", getDayOfWeek());
        values.put("OpenTime", getOpenTime());
        values.put("CloseTime", getCloseTime());
        return values;
    }

    @Override
    protected void fromMap(final Map<String, Object> inputValues) {
        final Map<String, Object> values = Maps.newHashMap(inputValues);
        // simple properties
        {
            if (values.containsKey("Id")) {
                final Object value = values.remove("Id");
                if ((value == null)||(!value.equals(getId()))) {
                    setId(((Integer) value));
                }
            }
            if (values.containsKey("DayOfWeek")) {
                final Object value = values.remove("DayOfWeek");
                if ((value == null)||(!value.equals(getDayOfWeek()))) {
                    setDayOfWeek(((Integer) value));
                }
            }
            if (values.containsKey("OpenTime")) {
                final Object value = values.remove("OpenTime");
                if ((value == null)||(!value.equals(getOpenTime()))) {
                    setOpenTime(((LocalTime) value));
                }
            }
            if (values.containsKey("CloseTime")) {
                final Object value = values.remove("CloseTime");
                if ((value == null)||(!value.equals(getCloseTime()))) {
                    setCloseTime(((LocalTime) value));
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
