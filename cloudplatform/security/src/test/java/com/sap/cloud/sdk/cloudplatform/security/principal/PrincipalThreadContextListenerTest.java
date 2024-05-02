package com.sap.cloud.sdk.cloudplatform.security.principal;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextListener;
import com.sap.cloud.sdk.cloudplatform.util.FacadeLocator;

class PrincipalThreadContextListenerTest
{
    @Test
    void testImplementationIsRegisteredInServiceLocatorPattern()
    {
        final Collection<ThreadContextListener> facades = FacadeLocator.getFacades(ThreadContextListener.class);

        assertThat(facades).anyMatch(f -> f instanceof PrincipalThreadContextListener);
    }

}
