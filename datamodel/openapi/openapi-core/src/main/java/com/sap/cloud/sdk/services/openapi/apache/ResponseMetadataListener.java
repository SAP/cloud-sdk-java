package com.sap.cloud.sdk.services.openapi.apache;

@FunctionalInterface
public interface ResponseMetadataListener
{
    void onResponse( OpenApiResponse response );
}
