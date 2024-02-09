package com.sap.cloud.sdk.testutil;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.sdk.cloudplatform.thread.DefaultThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextAccessor;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutors;

class TestContextTest
{
    static void sanityCheck()
    {
        assertThat(ThreadContextAccessor.tryGetCurrentContext()).isEmpty();
    }

    @Nested
    class TestEmptyContext
    {
        @RegisterExtension
        static TestContext sut = TestContext.withThreadContext();

        @BeforeEach
        @AfterEach
        void beforeAfterEachSanityCheck()
        {
            sanityCheck();
        }

        @Test
        @DisplayName( "The context should exist but be empty" )
        void testEmptyThreadContext()
        {
            assertThat(ThreadContextAccessor.tryGetCurrentContext()).isNotEmpty();

            assertThat(TenantAccessor.tryGetCurrentTenant()).isEmpty();
            assertThat(PrincipalAccessor.tryGetCurrentPrincipal()).isEmpty();
            assertThat(AuthTokenAccessor.tryGetCurrentToken()).isEmpty();
        }

        @Test
        @DisplayName( "The context should be modifiable" )
        void testContextCanBeModified()
        {
            assertThat(TenantAccessor.tryGetCurrentTenant()).isEmpty();
            final Tenant tenant = sut.setTenant();
            assertThat(TenantAccessor.tryGetCurrentTenant()).contains(tenant);
            ThreadContextExecutors
                .execute(
                    () -> assertThat(TenantAccessor.tryGetCurrentTenant())
                        .describedAs("Context should be passed onto new Threads via ThreadContextExecutors.")
                        .contains(tenant));
            ThreadContextExecutors
                .execute(
                    () -> assertThat(TenantAccessor.tryGetCurrentTenant())
                        .describedAs("Context should not leak onto other threads when not explicitly propagating it.")
                        .isEmpty(),
                    new DefaultThreadContext());

            sut.clearTenant();
            assertThat(TenantAccessor.tryGetCurrentTenant()).isEmpty();
        }
    }

    @Nested
    class TestPrefilledContext
    {
        @RegisterExtension
        static TestContext sut = TestContext.withThreadContext();
        static Tenant tenant;
        static Principal principal;
        static AuthToken authToken;

        // region TEST_CODE
        // this block contains actual usage, the result is asserted on in the test methods below
        // this verifies that the context can be correctly prefilled and that the prefilled values can be overridden
        static {
            authToken = sut.setAuthToken();
        }

        @BeforeAll
        static void setTenant()
        {
            tenant = sut.setTenant();
        }

        @BeforeEach
        void setPrincipal()
        {
            principal = sut.setPrincipal();
        }

        TestPrefilledContext()
        {
            sut.setProperty("foo", "bar");
        }
        // endregion

        @BeforeEach
        @AfterEach
        void beforeAfterEachSanityCheck()
        {
            sanityCheck();
        }

        @Test
        @DisplayName( "The context should contain the prefilled values, regardless of how exactly they were filled." )
        void testPrefilledThreadContext()
        {
            assertThat(ThreadContextAccessor.tryGetCurrentContext()).isNotEmpty();

            assertThat(TenantAccessor.tryGetCurrentTenant()).contains(tenant);
            assertThat(PrincipalAccessor.tryGetCurrentPrincipal()).contains(principal);
            assertThat(AuthTokenAccessor.tryGetCurrentToken()).contains(authToken);
            assertThat(ThreadContextAccessor.tryGetCurrentContext().get().getPropertyValue("foo")).contains("bar");
        }

        @Test
        @DisplayName( "Overriding prefilled values should be possible." )
        void testPrefilledPropertiesCanBeOverriden()
        {
            assertThat(TenantAccessor.tryGetCurrentTenant()).contains(tenant);
            sut.setTenant("newTenant");
            assertThat(TenantAccessor.tryGetCurrentTenant())
                .doesNotContain(tenant)
                .contains(new DefaultTenant("newTenant"));
            sut.clearTenant();
            assertThat(TenantAccessor.tryGetCurrentTenant()).isEmpty();
        }
    }
}
