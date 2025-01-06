/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

/*
 * Generated by OData VDM code generator of SAP Cloud SDK in version 4.21.0
 */

package com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin;

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
 * <p>
 * Original complex type name from the Odata EDM: <b>Beverage</b>
 * </p>
 *
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString( doNotUseGetters = true, callSuper = true )
@EqualsAndHashCode( doNotUseGetters = true, callSuper = true )
@JsonAdapter( com.sap.cloud.sdk.datamodel.odatav4.adapter.GsonVdmAdapterFactory.class )
@JsonSerialize( using = com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectSerializer.class )
@JsonDeserialize( using = com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectDeserializer.class )
public class Beverage extends VdmComplex<Beverage>
{

    @Getter
    private final java.lang.String odataType = "Trippin.Beverage";
    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>Name</b>
     * </p>
     *
     * @return The name contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName( "Name" )
    private java.lang.String name;
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.String<Beverage> NAME =
        new com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.String<Beverage>(Beverage.class, "Name");
    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>IsAlcoholic</b>
     * </p>
     *
     * @return The isAlcoholic contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName( "IsAlcoholic" )
    private java.lang.Boolean isAlcoholic;
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.Boolean<Beverage> IS_ALCOHOLIC =
        new com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.Boolean<Beverage>(Beverage.class, "IsAlcoholic");

    @Nonnull
    @Override
    public Class<Beverage> getType()
    {
        return Beverage.class;
    }

    @Nonnull
    @Override
    protected Map<java.lang.String, Object> toMapOfFields()
    {
        final Map<java.lang.String, Object> values = super.toMapOfFields();
        values.put("Name", getName());
        values.put("IsAlcoholic", getIsAlcoholic());
        return values;
    }

    @Override
    protected void fromMap( final Map<java.lang.String, Object> inputValues )
    {
        final Map<java.lang.String, Object> values = Maps.newHashMap(inputValues);
        // simple properties
        {
            if( values.containsKey("Name") ) {
                final Object value = values.remove("Name");
                if( (value == null) || (!value.equals(getName())) ) {
                    setName(((java.lang.String) value));
                }
            }
            if( values.containsKey("IsAlcoholic") ) {
                final Object value = values.remove("IsAlcoholic");
                if( (value == null) || (!value.equals(getIsAlcoholic())) ) {
                    setIsAlcoholic(((java.lang.Boolean) value));
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
    protected ODataEntityKey getKey()
    {
        final ODataEntityKey entityKey = super.getKey();
        return entityKey;
    }

    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>Name</b>
     * </p>
     *
     * @param name
     *            The name to set.
     */
    public void setName( @Nullable final java.lang.String name )
    {
        rememberChangedField("Name", this.name);
        this.name = name;
    }

    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>IsAlcoholic</b>
     * </p>
     *
     * @param isAlcoholic
     *            The isAlcoholic to set.
     */
    public void setIsAlcoholic( @Nullable final java.lang.Boolean isAlcoholic )
    {
        rememberChangedField("IsAlcoholic", this.isAlcoholic);
        this.isAlcoholic = isAlcoholic;
    }

}
