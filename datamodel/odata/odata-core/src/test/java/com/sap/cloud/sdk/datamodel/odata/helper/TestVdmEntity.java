/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataField;
import com.sap.cloud.sdk.s4hana.datamodel.odata.annotation.Key;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString( doNotUseGetters = true, callSuper = true )
@EqualsAndHashCode( doNotUseGetters = true, callSuper = true )
@JsonAdapter( com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory.class )
public class TestVdmEntity extends VdmEntity<TestVdmEntity>
{
    @Getter
    final String entityCollection = "Entities";

    @Getter
    private final String defaultServicePath = "/service";

    @Getter
    private final Class<TestVdmEntity> type = TestVdmEntity.class;

    @Key
    @SerializedName( "IntegerValue" )
    @JsonProperty( "IntegerValue" )
    @Nullable
    @ODataField( odataName = "IntegerValue" )
    private Integer integerValue;

    @SerializedName( "GuidValue" )
    @JsonProperty( "GuidValue" )
    @Nullable
    @ODataField( odataName = "GuidValue" )
    private UUID guidValue;

    @SerializedName( "StringValue" )
    @JsonProperty( "StringValue" )
    @Nullable
    @ODataField( odataName = "StringValue" )
    private String stringValue;

    @SerializedName( "OffsetDateTimeValue" )
    @JsonProperty( "OffsetDateTimeValue" )
    @Nullable
    @JsonSerialize( using = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.JacksonZonedDateTimeSerializer.class )
    @JsonDeserialize( using = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.JacksonZonedDateTimeDeserializer.class )
    @JsonAdapter( com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ZonedDateTimeAdapter.class )
    @ODataField(
        odataName = "OffsetDateTimeValue",
        converter = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ZonedDateTimeCalendarConverter.class )
    private ZonedDateTime offsetDateTimeValue;

    @SerializedName( "to_Parent" )
    @JsonProperty( "to_Parent" )
    @ODataField( odataName = "to_Parent" )
    @Nullable
    @Getter( AccessLevel.NONE )
    @Setter( AccessLevel.NONE )
    private TestVdmEntity toParent;

    @SerializedName( "to_Children" )
    @JsonProperty( "to_Children" )
    @ODataField( odataName = "to_Children" )
    @Getter( AccessLevel.NONE )
    @Setter( AccessLevel.NONE )
    private List<TestVdmEntity> toChildren;

    @SerializedName( "DecimalValue" )
    @JsonProperty( "DecimalValue" )
    @Nullable
    @ODataField( odataName = "DecimalValue" )
    private BigDecimal decimalValue;

    @SerializedName( "DoubleValue" )
    @JsonProperty( "DoubleValue" )
    @Nullable
    @ODataField( odataName = "DoubleValue" )
    private Double doubleValue;

    @SerializedName( "LocalTimeValue" )
    @JsonProperty( "LocalTimeValue" )
    @Nullable
    @JsonSerialize( using = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.JacksonLocalTimeSerializer.class )
    @JsonDeserialize( using = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.JacksonLocalTimeDeserializer.class )
    @JsonAdapter( com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.LocalTimeAdapter.class )
    @ODataField(
        odataName = "LocalTimeValue",
        converter = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.LocalTimeCalendarConverter.class )
    private LocalTime localTimeValue;

    @SerializedName( "LocalDateTimeValue" )
    @JsonProperty( "LocalDateTimeValue" )
    @Nullable
    @JsonSerialize( using = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.JacksonLocalDateTimeSerializer.class )
    @JsonDeserialize( using = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.JacksonLocalDateTimeDeserializer.class )
    @JsonAdapter( com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.LocalDateTimeAdapter.class )
    @ODataField(
        odataName = "LocalDateTimeValue",
        converter = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.LocalDateTimeCalendarConverter.class )
    private LocalDateTime localDateTimeValue;

    @SerializedName( "BooleanValue" )
    @JsonProperty( "BooleanValue" )
    @Nullable
    @JsonAdapter( com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataBooleanAdapter.class )
    @ODataField( odataName = "BooleanValue" )
    private Boolean booleanValue;

    @SerializedName( "ComplexValue" )
    @JsonProperty( "ComplexValue" )
    @Nullable
    @ODataField( odataName = "ComplexValue" )
    private TestVdmComplex complexValue;

    public void setIntegerValue( @Nullable final Integer integerValue )
    {
        rememberChangedField("IntegerValue", this.integerValue);
        this.integerValue = integerValue;
    }

    public void setBooleanValue( @Nullable final Boolean booleanValue )
    {
        rememberChangedField("BooleanValue", this.booleanValue);
        this.booleanValue = booleanValue;
    }

    public void setGuidValue( @Nullable final UUID guidValue )
    {
        rememberChangedField("GuidValue", this.guidValue);
        this.guidValue = guidValue;
    }

    public void setStringValue( @Nullable final String stringValue )
    {
        rememberChangedField("StringValue", this.stringValue);
        this.stringValue = stringValue;
    }

    public void setDecimalValue( @Nullable final BigDecimal decimalValue )
    {
        rememberChangedField("DecimalValue", this.decimalValue);
        this.decimalValue = decimalValue;
    }

    public void setDoubleValue( @Nullable final Double doubleValue )
    {
        rememberChangedField("DoubleValue", this.doubleValue);
        this.doubleValue = doubleValue;
    }

    public void setOffsetDateTimeValue( @Nullable final ZonedDateTime offsetDateTimeValue )
    {
        rememberChangedField("OffsetDateTimeValue", this.offsetDateTimeValue);
        this.offsetDateTimeValue = offsetDateTimeValue;
    }

    public void setLocalTimeValue( @Nullable final LocalTime localTimeValue )
    {
        rememberChangedField("LocalTimeValue", this.localTimeValue);
        this.localTimeValue = localTimeValue;
    }

    public void setLocalDateTimeValue( @Nullable final LocalDateTime localDateTimeValue )
    {
        rememberChangedField("LocalDateTimeValue", this.localDateTimeValue);
        this.localDateTimeValue = localDateTimeValue;
    }

    public void setComplexValue( @Nullable final TestVdmComplex complexValue )
    {
        rememberChangedField("ComplexValue", this.complexValue);
        this.complexValue = complexValue;
    }

    @Nonnull
    @Override
    protected Map<String, Object> toMapOfFields()
    {
        final Map<String, Object> values = super.toMapOfFields();
        values.put("IntegerValue", getIntegerValue());
        values.put("BooleanValue", getBooleanValue());
        values.put("StringValue", getStringValue());
        values.put("DecimalValue", getDecimalValue());
        values.put("DoubleValue", getDoubleValue());
        values.put("GuidValue", getGuidValue());
        values.put("OffsetDateTimeValue", getOffsetDateTimeValue());
        values.put("LocalDateTimeValue", getLocalDateTimeValue());
        values.put("LocalTimeValue", getLocalTimeValue());
        values.put("ComplexValue", getComplexValue());
        return values;
    }

    @Nullable
    public List<TestVdmEntity> fetchToChildren()
    {
        // not implemented here
        return null;
    }

    @Nonnull
    public List<TestVdmEntity> getToChildrenOrFetch()
    {
        if( toChildren == null ) {
            toChildren = fetchToChildren();
        }
        return toChildren;
    }

    @Nonnull
    public Option<List<TestVdmEntity>> getToChildrenIfPresent()
    {
        return Option.of(toChildren);
    }

    public void setToChildren( @Nonnull final List<TestVdmEntity> value )
    {
        // rememberChangedField("to_Children", toChildren);
        if( toChildren == null ) {
            toChildren = Lists.newArrayList();
        }
        toChildren.clear();
        toChildren.addAll(value);
    }

    public void addToChildren( TestVdmEntity... entity )
    {
        // rememberChangedField("to_Children", toChildren);
        if( toChildren == null ) {
            toChildren = Lists.newArrayList();
        }
        toChildren.addAll(Lists.newArrayList(entity));
    }

    @Nullable
    public TestVdmEntity fetchToParent()
    {
        // not implemented here
        return null;
    }

    @Nonnull
    public TestVdmEntity getToParentOrFetch()
    {
        if( toParent == null ) {
            toParent = fetchToParent();
        }
        return toParent;
    }

    @Nonnull
    public Option<TestVdmEntity> getToParentIfPresent()
    {
        return Option.of(toParent);
    }

    public void setToParent( @Nonnull final TestVdmEntity value )
    {
        // rememberChangedField("to_Children", toChildren);
        toParent = value;
    }

    @Nonnull
    @Override
    protected Map<String, Object> getKey()
    {
        return Collections.singletonMap("IntegerValue", integerValue);
    }
}
