package com.sap.cloud.sdk.datamodel.odata.helper.batch;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperCreate;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmEntity;

import lombok.Value;

@Value
class DefaultBatchResponseChangeSet implements BatchResponseChangeSet
{
    List<BatchRequestChangeSetOperation> changesetOperations;
    Function<BatchRequestChangeSetOperation, VdmEntity<?>> changesetEntityExtractor;

    @Nonnull
    @Override
    public List<VdmEntity<?>> getCreatedEntities()
    {
        return changesetOperations
            .stream()
            .filter(req -> req.getFluentHelper() instanceof FluentHelperCreate)
            .map(changesetEntityExtractor)
            .collect(Collectors.toList());
    }
}
