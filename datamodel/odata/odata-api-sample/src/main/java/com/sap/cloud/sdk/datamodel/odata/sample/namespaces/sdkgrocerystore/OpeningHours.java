/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore;

import java.time.LocalTime;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Maps;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmEntity;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.field.OpeningHoursField;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.selectable.OpeningHoursSelectable;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataField;
import com.sap.cloud.sdk.s4hana.datamodel.odata.annotation.Key;
import com.sap.cloud.sdk.typeconverter.TypeConverter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <p>
 * Original entity name from the Odata EDM: <b>OpeningHours</b>
 * </p>
 *
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString( doNotUseGetters = true, callSuper = true )
@EqualsAndHashCode( doNotUseGetters = true, callSuper = true )
@JsonAdapter( com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory.class )
public class OpeningHours extends VdmEntity<OpeningHours>
{

    /**
     * Selector for all available fields of OpeningHours.
     *
     */
    public final static OpeningHoursSelectable ALL_FIELDS = () -> "*";
    /**
     * (Key Field) Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>Id</b>
     * </p>
     *
     * @return The id contained in this entity.
     */
    @Key
    @SerializedName( "Id" )
    @JsonProperty( "Id" )
    @Nullable
    @ODataField( odataName = "Id" )
    private Integer id;
    /**
     * Use with available fluent helpers to apply the <b>Id</b> field to query operations.
     *
     */
    public final static OpeningHoursField<Integer> ID = new OpeningHoursField<Integer>("Id");
    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>DayOfWeek</b>
     * </p>
     *
     * @return The dayOfWeek contained in this entity.
     */
    @SerializedName( "DayOfWeek" )
    @JsonProperty( "DayOfWeek" )
    @Nullable
    @ODataField( odataName = "DayOfWeek" )
    private Integer dayOfWeek;
    /**
     * Use with available fluent helpers to apply the <b>DayOfWeek</b> field to query operations.
     *
     */
    public final static OpeningHoursField<Integer> DAY_OF_WEEK = new OpeningHoursField<Integer>("DayOfWeek");
    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>OpenTime</b>
     * </p>
     *
     * @return The openTime contained in this entity.
     */
    @SerializedName( "OpenTime" )
    @JsonProperty( "OpenTime" )
    @Nullable
    @JsonSerialize( using = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.JacksonLocalTimeSerializer.class )
    @JsonDeserialize( using = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.JacksonLocalTimeDeserializer.class )
    @JsonAdapter( com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.LocalTimeAdapter.class )
    @ODataField(
        odataName = "OpenTime",
        converter = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.LocalTimeCalendarConverter.class )
    private LocalTime openTime;
    /**
     * Use with available fluent helpers to apply the <b>OpenTime</b> field to query operations.
     *
     */
    public final static OpeningHoursField<LocalTime> OPEN_TIME = new OpeningHoursField<LocalTime>("OpenTime");
    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>CloseTime</b>
     * </p>
     *
     * @return The closeTime contained in this entity.
     */
    @SerializedName( "CloseTime" )
    @JsonProperty( "CloseTime" )
    @Nullable
    @JsonSerialize( using = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.JacksonLocalTimeSerializer.class )
    @JsonDeserialize( using = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.JacksonLocalTimeDeserializer.class )
    @JsonAdapter( com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.LocalTimeAdapter.class )
    @ODataField(
        odataName = "CloseTime",
        converter = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.LocalTimeCalendarConverter.class )
    private LocalTime closeTime;
    /**
     * Use with available fluent helpers to apply the <b>CloseTime</b> field to query operations.
     *
     */
    public final static OpeningHoursField<LocalTime> CLOSE_TIME = new OpeningHoursField<LocalTime>("CloseTime");

    @Nonnull
    @Override
    public Class<OpeningHours> getType()
    {
        return OpeningHours.class;
    }

    /**
     * (Key Field) Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>Id</b>
     * </p>
     *
     * @param id
     *            The id to set.
     */
    public void setId( @Nullable final Integer id )
    {
        rememberChangedField("Id", this.id);
        this.id = id;
    }

    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>DayOfWeek</b>
     * </p>
     *
     * @param dayOfWeek
     *            The dayOfWeek to set.
     */
    public void setDayOfWeek( @Nullable final Integer dayOfWeek )
    {
        rememberChangedField("DayOfWeek", this.dayOfWeek);
        this.dayOfWeek = dayOfWeek;
    }

    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>OpenTime</b>
     * </p>
     *
     * @param openTime
     *            The openTime to set.
     */
    public void setOpenTime( @Nullable final LocalTime openTime )
    {
        rememberChangedField("OpenTime", this.openTime);
        this.openTime = openTime;
    }

    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>CloseTime</b>
     * </p>
     *
     * @param closeTime
     *            The closeTime to set.
     */
    public void setCloseTime( @Nullable final LocalTime closeTime )
    {
        rememberChangedField("CloseTime", this.closeTime);
        this.closeTime = closeTime;
    }

    @Override
    protected String getEntityCollection()
    {
        return "OpeningHours";
    }

    @Nonnull
    @Override
    protected Map<String, Object> getKey()
    {
        final Map<String, Object> result = Maps.newLinkedHashMap();
        result.put("Id", getId());
        return result;
    }

    @Nonnull
    @Override
    protected Map<String, Object> toMapOfFields()
    {
        final Map<String, Object> cloudSdkValues = super.toMapOfFields();
        cloudSdkValues.put("Id", getId());
        cloudSdkValues.put("DayOfWeek", getDayOfWeek());
        cloudSdkValues.put("OpenTime", getOpenTime());
        cloudSdkValues.put("CloseTime", getCloseTime());
        return cloudSdkValues;
    }

    @Override
    protected void fromMap( final Map<String, Object> inputValues )
    {
        final Map<String, Object> cloudSdkValues = Maps.newLinkedHashMap(inputValues);
        // simple properties
        {
            if( cloudSdkValues.containsKey("Id") ) {
                final Object value = cloudSdkValues.remove("Id");
                if( (value == null) || (!value.equals(getId())) ) {
                    setId(((Integer) value));
                }
            }
            if( cloudSdkValues.containsKey("DayOfWeek") ) {
                final Object value = cloudSdkValues.remove("DayOfWeek");
                if( (value == null) || (!value.equals(getDayOfWeek())) ) {
                    setDayOfWeek(((Integer) value));
                }
            }
            if( cloudSdkValues.containsKey("OpenTime") ) {
                final Object value = cloudSdkValues.remove("OpenTime");
                if( (value == null) || (!value.equals(getOpenTime())) ) {
                    setOpenTime(((LocalTime) value));
                }
            }
            if( cloudSdkValues.containsKey("CloseTime") ) {
                final Object value = cloudSdkValues.remove("CloseTime");
                if( (value == null) || (!value.equals(getCloseTime())) ) {
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
        super.fromMap(cloudSdkValues);
    }

    /**
     * Use with available fluent helpers to apply an extension field to query operations.
     *
     * @param fieldName
     *            The name of the extension field as returned by the OData service.
     * @param <T>
     *            The type of the extension field when performing value comparisons.
     * @param fieldType
     *            The Java type to use for the extension field when performing value comparisons.
     * @return A representation of an extension field from this entity.
     */
    @Nonnull
    public static <T> OpeningHoursField<T> field( @Nonnull final String fieldName, @Nonnull final Class<T> fieldType )
    {
        return new OpeningHoursField<T>(fieldName);
    }

    /**
     * Use with available fluent helpers to apply an extension field to query operations.
     *
     * @param typeConverter
     *            A TypeConverter<T, DomainT> instance whose first generic type matches the Java type of the field
     * @param fieldName
     *            The name of the extension field as returned by the OData service.
     * @param <T>
     *            The type of the extension field when performing value comparisons.
     * @param <DomainT>
     *            The type of the extension field as returned by the OData service.
     * @return A representation of an extension field from this entity, holding a reference to the given TypeConverter.
     */
    @Nonnull
    public static <T, DomainT> OpeningHoursField<T> field(
        @Nonnull final String fieldName,
        @Nonnull final TypeConverter<T, DomainT> typeConverter )
    {
        return new OpeningHoursField<T>(fieldName, typeConverter);
    }

    @Override
    @Nullable
    public Destination getDestinationForFetch()
    {
        return super.getDestinationForFetch();
    }

    @Override
    protected void setServicePathForFetch( @Nullable final String servicePathForFetch )
    {
        super.setServicePathForFetch(servicePathForFetch);
    }

    @Override
    public void attachToService( @Nullable final String servicePath, @Nonnull final Destination destination )
    {
        super.attachToService(servicePath, destination);
    }

    @Override
    protected String getDefaultServicePath()
    {
        return (com.sap.cloud.sdk.datamodel.odata.sample.services.SdkGroceryStoreService.DEFAULT_SERVICE_PATH);
    }

}
