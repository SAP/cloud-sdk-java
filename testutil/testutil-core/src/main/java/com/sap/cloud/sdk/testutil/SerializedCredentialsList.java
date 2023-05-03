package com.sap.cloud.sdk.testutil;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
class SerializedCredentialsList
{
    @JsonProperty( "credentials" )
    private List<SerializedCredentials> credentials;
}
