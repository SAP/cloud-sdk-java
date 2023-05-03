package com.sap.cloud.sdk.testutil;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
class SerializedTestSystem
{
    @JsonProperty( "alias" )
    private String alias;

    @JsonProperty( "uri" )
    private URI uri;

    @JsonProperty( "proxy" )
    private URI proxyUri;
}
