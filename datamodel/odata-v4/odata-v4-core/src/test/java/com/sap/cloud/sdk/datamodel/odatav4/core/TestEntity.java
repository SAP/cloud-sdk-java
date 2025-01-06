/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.JsonAdapter;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataEntityKey;
import com.sap.cloud.sdk.result.ElementName;

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
@JsonAdapter( com.sap.cloud.sdk.datamodel.odatav4.adapter.GsonVdmAdapterFactory.class )
@JsonSerialize( using = com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectSerializer.class )
@JsonDeserialize( using = com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectDeserializer.class )
public class TestEntity extends VdmEntity<TestEntity>
{
    static final String SERVICE_PATH = "/odata/default";

    @Getter
    private final String odataType = "TestEntity";

    @Nonnull
    @Override
    protected String getEntityCollection()
    {
        return "EntityCollection";
    }

    @Nonnull
    @Override
    public Class<TestEntity> getType()
    {
        return TestEntity.class;
    }

    @Override
    protected String getDefaultServicePath()
    {
        return SERVICE_PATH;
    }

    @Nullable
    @ElementName( "id" )
    private String id;

    public final static SimpleProperty.String<TestEntity> ID = new SimpleProperty.String<>(TestEntity.class, "id");

    @Nonnull
    @Override
    protected ODataEntityKey getKey()
    {
        final ODataEntityKey key = super.getKey();
        key.addKeyProperty("id", getId());
        return key;
    }

    public void setId( @Nullable final String id )
    {
        rememberChangedField("id", this.id);
        this.id = id;
    }

    @Nonnull
    @Override
    protected Map<String, Object> toMapOfFields()
    {
        return ImmutableMap.<String, Object> builder().putAll(super.toMapOfFields()).put("id", getId()).build();
    }
}
