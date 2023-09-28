package com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore;

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
 * <p>
 * Original entity name from the Odata EDM: <b>FloorPlan</b>
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
public class FloorPlan extends VdmEntity<FloorPlan>
{

    @Getter
    private final java.lang.String odataType = "com.sap.cloud.sdk.store.grocery.FloorPlan";
    /**
     * Selector for all available fields of FloorPlan.
     *
     */
    public final static SimpleProperty<FloorPlan> ALL_FIELDS = all();
    /**
     * (Key Field) Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>Id</b>
     * </p>
     *
     * @return The id contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName( "Id" )
    private Integer id;
    public final static SimpleProperty.NumericInteger<FloorPlan> ID =
        new SimpleProperty.NumericInteger<FloorPlan>(FloorPlan.class, "Id");
    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>ImageUri</b>
     * </p>
     *
     * @return The imageUri contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName( "ImageUri" )
    private java.lang.String imageUri;
    public final static SimpleProperty.String<FloorPlan> IMAGE_URI =
        new SimpleProperty.String<FloorPlan>(FloorPlan.class, "ImageUri");

    @Nonnull
    @Override
    public Class<FloorPlan> getType()
    {
        return FloorPlan.class;
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
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>ImageUri</b>
     * </p>
     *
     * @param imageUri
     *            The imageUri to set.
     */
    public void setImageUri( @Nullable final java.lang.String imageUri )
    {
        rememberChangedField("ImageUri", this.imageUri);
        this.imageUri = imageUri;
    }

    @Override
    protected java.lang.String getEntityCollection()
    {
        return "FloorPlan";
    }

    @Nonnull
    @Override
    protected ODataEntityKey getKey()
    {
        final ODataEntityKey entityKey = super.getKey();
        entityKey.addKeyProperty("Id", getId());
        return entityKey;
    }

    @Nonnull
    @Override
    protected Map<java.lang.String, Object> toMapOfFields()
    {
        final Map<java.lang.String, Object> values = super.toMapOfFields();
        values.put("Id", getId());
        values.put("ImageUri", getImageUri());
        return values;
    }

    @Override
    protected void fromMap( final Map<java.lang.String, Object> inputValues )
    {
        final Map<java.lang.String, Object> values = Maps.newHashMap(inputValues);
        // simple properties
        {
            if( values.containsKey("Id") ) {
                final Object value = values.remove("Id");
                if( (value == null) || (!value.equals(getId())) ) {
                    setId(((Integer) value));
                }
            }
            if( values.containsKey("ImageUri") ) {
                final Object value = values.remove("ImageUri");
                if( (value == null) || (!value.equals(getImageUri())) ) {
                    setImageUri(((java.lang.String) value));
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
    protected Map<java.lang.String, Object> toMapOfNavigationProperties()
    {
        final Map<java.lang.String, Object> values = super.toMapOfNavigationProperties();
        return values;
    }

}
