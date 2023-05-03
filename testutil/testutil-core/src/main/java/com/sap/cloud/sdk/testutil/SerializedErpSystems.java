package com.sap.cloud.sdk.testutil;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
class SerializedErpSystems
{
    @JsonProperty( "default" )
    private String defaultAlias;

    @JsonProperty( "systems" )
    private List<SerializedErpSystem> systems;
}
