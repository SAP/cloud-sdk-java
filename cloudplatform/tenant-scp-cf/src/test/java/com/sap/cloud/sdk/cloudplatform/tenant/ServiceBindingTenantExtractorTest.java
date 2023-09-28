package com.sap.cloud.sdk.cloudplatform.tenant;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.sap.cloud.sdk.cloudplatform.ScpCfCloudPlatform;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutor;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RunWith( Parameterized.class )
@RequiredArgsConstructor
public class ServiceBindingTenantExtractorTest
{
    private final ScpCfCloudPlatform platform = ScpCfCloudPlatform.getInstanceOrThrow();

    private final Parameter parameter;

    @Value
    static class Parameter
    {
        String givenVcapServices;
        ScpCfTenant assertedTenant;
    }

    @Parameterized.Parameters
    public static Parameter[] resolveParameters()
    {
        return new Parameter[] {
            new Parameter(
                "{\"xsuaa\":[{\"plan\":\"application\",\"credentials\":{\"identityzone\":\"sub1\",\"tenantid\":\"tenant1\"}}]}",
                new ScpCfTenant("tenant1", "sub1")),
            new Parameter(
                "{\"xsuaa\":[{\"plan\":\"broker\",\"credentials\":{\"identityzone\":\"sub2\",\"zoneid\":\"tenant2\"}}]}",
                new ScpCfTenant("tenant2", "sub2")),
            new Parameter(
                "{\"xsuaa\":[{\"plan\":\"application\",\"credentials\":{\"identityzone\":\"sub3\",\"identityzoneid\":\"tenant3\"}}]}",
                new ScpCfTenant("tenant3", "sub3")),
            new Parameter(
                "{\"identity\":[{\"plan\":\"application\",\"credentials\":{\"zone_uuid\":\"tenant4\",\"url\":\"https://sub4.some.domain.com\"}}]}",
                new ScpCfTenant("tenant4", "sub4")) };
    }

    @Before
    @After
    public void cleanCache()
    {
        platform.setEnvironmentVariableReader(System::getenv);
        ScpCfCloudPlatform.invalidateCaches();
    }

    @Test
    public void givenServiceBindingsThenFacadeShouldProvideATenant()
    {
        final Map<String, String> env = Collections.singletonMap("VCAP_SERVICES", parameter.getGivenVcapServices());
        platform.setEnvironmentVariableReader(env::get);

        final ThreadContextExecutor executor = ThreadContextExecutor.fromNewContext().withoutDefaultListeners();
        final Try<Tenant> maybeTenant = executor.execute(() -> new ScpCfTenantFacade().tryGetCurrentTenant());
        assertThat(maybeTenant).containsExactly(parameter.getAssertedTenant());
    }
}
