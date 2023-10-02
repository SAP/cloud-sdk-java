/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper.batch;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestBatch;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestFunction;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestRead;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestReadByKey;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperByKey;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperFunction;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperRead;

import lombok.EqualsAndHashCode;
import lombok.Value;

abstract class BatchRequestRead implements BatchRequestOperation
{
    @Value
    @EqualsAndHashCode( callSuper = true )
    static class GetAll extends BatchRequestRead
    {
        FluentHelperRead<?, ?, ?> fluentHelper;
        ODataRequestRead request;

        @Override
        public void addToRequestBuilder( @Nonnull final ODataRequestBatch builder )
        {
            builder.addRead(request);
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    static class GetByKey extends BatchRequestRead
    {
        FluentHelperByKey<?, ?, ?> fluentHelper;
        ODataRequestReadByKey request;

        @Override
        public void addToRequestBuilder( @Nonnull final ODataRequestBatch builder )
        {
            builder.addReadByKey(request);
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    static class GetFunctionRequest extends BatchRequestRead
    {
        FluentHelperFunction<?, ?, ?> fluentHelper;
        ODataRequestFunction request;

        @Override
        public void addToRequestBuilder( @Nonnull final ODataRequestBatch builder )
        {
            builder.addFunction(request);
        }
    }
}
