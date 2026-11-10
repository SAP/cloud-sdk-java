package com.sap.cloud.sdk.datamodel.odatav4.core;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FieldReference;

class UpdateRequestHelperPut
{
    String toJson( @Nonnull final VdmEntity<?> entity, @Nullable final Collection<FieldReference> excludedFields )
    {
        final Gson gson = new GsonBuilder().create();
        final JsonObject jsonObject = gson.toJsonTree(entity).getAsJsonObject();

        // find field names to be removed from PUT request
        if( excludedFields != null ) {
            excludedFields.stream().map(FieldReference::getFieldName).forEach(jsonObject::remove);
        }

        return gson.toJson(jsonObject);
    }
}
