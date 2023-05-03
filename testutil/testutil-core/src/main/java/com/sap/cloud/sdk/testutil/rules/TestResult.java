package com.sap.cloud.sdk.testutil.rules;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.junit.runner.Description;

import lombok.Data;

@Data
class TestResult
{
    private static final String SYSTEM_CHAIN_DELIMITER = "-------------";
    private final List<SystemChainResult> resultsPerSystemChain = new ArrayList<>();
    @Nullable
    private final Description description;

    void addResult( final SystemChainResult result )
    {
        resultsPerSystemChain.add(result);
    }

    boolean isSuccess()
    {
        return resultsPerSystemChain.stream().allMatch(SystemChainResult::isSuccess);
    }

    String getResultString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("\n");

        if( !resultsPerSystemChain.isEmpty() ) {
            for( final SystemChainResult systemChainResult : resultsPerSystemChain ) {
                builder.append(SYSTEM_CHAIN_DELIMITER);
                if( description != null ) {
                    builder
                        .append(" ")
                        .append(description.getClassName())
                        .append(" -> ")
                        .append(description.getMethodName());
                }
                builder.append("\n");
                builder.append(systemChainResult.getResultString());
            }
            builder.append(SYSTEM_CHAIN_DELIMITER);
        } else {
            builder.append("No tests were run with this ");
        }

        return builder.toString();
    }
}
