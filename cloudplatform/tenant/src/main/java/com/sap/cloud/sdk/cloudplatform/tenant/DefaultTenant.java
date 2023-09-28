package com.sap.cloud.sdk.cloudplatform.tenant;

import javax.annotation.Nonnull;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class DefaultTenant implements Tenant
{
    @Getter
    @Nonnull
    private final String tenantId;
}
