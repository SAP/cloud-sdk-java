package com.sap.cloud.sdk.cloudplatform.tenant;

import static com.sap.cloud.sdk.cloudplatform.DwcHeaderUtils.DWC_SUBDOMAIN_HEADER;
import static com.sap.cloud.sdk.cloudplatform.DwcHeaderUtils.DWC_TENANT_HEADER;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.exception.TenantAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextAccessor;

import io.vavr.control.Try;

class DwcTenantFacadeTest
{
    @Test
    void testFacadeIsPickedUpAutomatically()
    {
        assertThat(TenantAccessor.getTenantFacade()).isInstanceOf(DwcTenantFacade.class);
    }

    @Test
    void testSuccessfulTenantRetrieval()
    {
        final Map<String, String> headers =
            ImmutableMap.of(DWC_TENANT_HEADER, "tenant-value", DWC_SUBDOMAIN_HEADER, "subdomain-value");

        final DefaultTenant expectedTenant = new DefaultTenant("tenant-value", "subdomain-value");

        RequestHeaderAccessor.executeWithHeaderContainer(headers, () -> {
            final ThreadContext currentContext = ThreadContextAccessor.getCurrentContext();
            final DefaultTenant currentTenant = (DefaultTenant) TenantAccessor.getCurrentTenant();
            final Try<Tenant> shouldBeSuccess =
                currentContext.getPropertyValue(TenantThreadContextListener.PROPERTY_TENANT);

            assertThat(currentTenant).isEqualTo(expectedTenant);
            assertThat(shouldBeSuccess.isSuccess()).isTrue();
            assertThat(shouldBeSuccess.get()).isEqualTo(expectedTenant);
        });
    }

    @Test
    void testUnsuccessfulTenantRetrieval()
    {
        RequestHeaderAccessor.executeWithHeaderContainer(Collections.emptyMap(), () -> {
            final ThreadContext currentContext = ThreadContextAccessor.getCurrentContext();
            final Try<Tenant> tenantFailure = TenantAccessor.tryGetCurrentTenant();
            final Try<Tenant> shouldBeFailure =
                currentContext.getPropertyValue(TenantThreadContextListener.PROPERTY_TENANT);

            assertThat(tenantFailure.isFailure()).isTrue();
            assertThat(tenantFailure.getCause()).isInstanceOf(TenantAccessException.class);
            assertThat(shouldBeFailure.isFailure()).isTrue();
            assertThat(tenantFailure).isSameAs(shouldBeFailure);
        });
    }
}
