package com.sap.cloud.sdk.testutil;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
class SerializedTestSystems
{
    @JsonProperty( "systems" )
    private List<SerializedTestSystem> systems;

    @JsonProperty( "erp" )
    private SerializedErpSystems erpSystems;
}
