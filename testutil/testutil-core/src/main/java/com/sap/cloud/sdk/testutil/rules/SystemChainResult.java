package com.sap.cloud.sdk.testutil.rules;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
class SystemChainResult
{
    private final List<SystemResult> results = new ArrayList<>();

    void addResult( final SystemResult result )
    {
        results.add(result);
    }

    boolean isSuccess()
    {
        return results.stream().anyMatch(SystemResult::isSuccess);
    }

    String getResultString()
    {
        final StringBuilder builder = new StringBuilder();
        results.forEach(result -> builder.append(result.getResultString()).append("\n"));
        return builder.toString();
    }
}
