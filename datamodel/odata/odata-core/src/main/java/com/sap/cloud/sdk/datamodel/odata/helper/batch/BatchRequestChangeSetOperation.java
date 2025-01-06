package com.sap.cloud.sdk.datamodel.odata.helper.batch;

import java.util.function.Consumer;

import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestBatch;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperBasic;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter( AccessLevel.PACKAGE )
class BatchRequestChangeSetOperation
{
    private final Consumer<ODataRequestBatch.Changeset> changeSetConsumer;
    private final FluentHelperBasic<?, ?, ?> fluentHelper;
}
