package com.sap.cloud.sdk.testutil;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
class SerializedCredentials
{
    @JsonProperty( "alias" )
    private String alias;

    @JsonProperty( "username" )
    private String username;

    @JsonProperty( "password" )
    private String password;
}
