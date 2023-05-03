package com.sap.cloud.sdk.testutil;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
class SerializedErpSystem
{
    @JsonProperty( "alias" )
    private String alias;

    @JsonProperty( "systemId" )
    private String systemId;

    @JsonProperty( "sapClient" )
    private String sapClient;

    @JsonProperty( "locale" )
    private String locale;

    @JsonProperty( "erpEdition" )
    private String erpEdition;

    @JsonProperty( "uri" )
    private URI uri;

    @JsonProperty( "proxy" )
    private URI proxyUri;

    @JsonProperty( "applicationServer" )
    private String applicationServer;

    @JsonProperty( "instanceNumber" )
    private String instanceNumber;
}
