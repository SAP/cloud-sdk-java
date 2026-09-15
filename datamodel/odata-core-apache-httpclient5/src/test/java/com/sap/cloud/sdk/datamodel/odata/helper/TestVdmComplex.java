package com.sap.cloud.sdk.datamodel.odata.helper;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString( doNotUseGetters = true, callSuper = true )
@EqualsAndHashCode( doNotUseGetters = true, callSuper = true )
@JsonAdapter( com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory.class )
public class TestVdmComplex extends VdmComplex<TestVdmComplex>
{
    @SerializedName( "SomeValue" )
    @JsonProperty( "SomeValue" )
    @Nullable
    @ODataField( odataName = "SomeValue" )
    private String someValue;

    @SerializedName( "OtherValue" )
    @JsonProperty( "OtherValue" )
    @Nullable
    @ODataField( odataName = "OtherValue" )
    private String otherValue;

    @SerializedName( "ComplexValue" )
    @JsonProperty( "ComplexValue" )
    @Nullable
    @ODataField( odataName = "ComplexValue" )
    private TestVdmComplex complexValue;

    @Getter
    private final Class<TestVdmComplex> type = TestVdmComplex.class;

    @Nonnull
    @Override
    protected Map<String, Object> toMapOfFields()
    {
        final Map<String, Object> values = super.toMapOfFields();
        values.put("SomeValue", getSomeValue());
        values.put("OtherValue", getOtherValue());
        values.put("ComplexValue", getComplexValue());
        return values;
    }

    public void setSomeValue( String value )
    {
        rememberChangedField("SomeValue", someValue);
        someValue = value;
    }

    public void setOtherValue( String value )
    {
        rememberChangedField("OtherValue", otherValue);
        otherValue = value;
    }

    public void setComplexValue( TestVdmComplex value )
    {
        rememberChangedField("ComplexValue", complexValue);
        complexValue = value;
    }
}
